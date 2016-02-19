/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

import vavi.beans.BeanUtil;
import vavi.xml.util.PrettyPrinter;


/**
 * XPathParser. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/01 nsano initial version <br>
 */
public class XPathParser<T> implements Parser<Reader, T> {
    
    /** */
    protected XPath xPath;
    
    {
//System.err.println(XPathFactory.DEFAULT_PROPERTY_NAME + ":" + XPathFactory.DEFAULT_OBJECT_MODEL_URI);    
//        System.setProperty(XPathFactory.DEFAULT_PROPERTY_NAME + ":" + XPathFactory.DEFAULT_OBJECT_MODEL_URI, "org.apache.xpath.jaxp.XPathFactoryImpl");    
        xPath = XPathFactory.newInstance().newXPath();
//System.err.println(XPathFactory.newInstance().getClass());
    }

    /**
     * TODO Reader は、 InputSource の引数ならどれでもとか
     * TODO Reader が fields のループに入ってるから InputHandler にキャッシュが必要になる 
     */
    public List<T> parse(Class<T> type, InputHandler<Reader> handler, String ... args) {
        try {
            String encoding = WebScraper.Util.getEncoding(type);
//System.err.println("encoding: " + encoding);

            List<T> results = new ArrayList<T>();

            Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
            for (Field field : targetFields) {
                Reader reader = handler.getInput(args);

                InputSource in = new InputSource(reader);
                in.setEncoding(encoding);

                String xpath = Target.Util.getValue(field);
//System.err.println("xpath: " + xpath);
//PrettyPrinter pp = new PrettyPrinter(System.err);
//pp.print(in);

                if (WebScraper.Util.isCollection(type)) {

                    NodeList nodeList = (NodeList) xPath.evaluate(xpath, in, XPathConstants.NODESET);
//System.err.println("nodeList: " + nodeList.getLength());
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        // because loops for each fields, instantiation should be done once
                        T bean = null;
                        try {
                            bean = results.get(i);
                        } catch (IndexOutOfBoundsException e) {
                            bean = type.newInstance();
                            results.add(bean);
                        }
    
                        String text = nodeList.item(i).getTextContent().trim();
//System.err.println(field.getName() + ": " + text);
                        BeanUtil.setFieldValue(field, bean, text);
                    }
                } else {

                    // because loops for each fields, instantiation should be done once
                    T bean = null;
                    try {
                        bean = results.get(0);
                    } catch (IndexOutOfBoundsException e) {
                        bean = type.newInstance();
                        results.add(bean);
                    }

                    String text = ((String) xPath.evaluate(xpath, in, XPathConstants.STRING)).trim();
                    BeanUtil.setFieldValue(field, bean, text);
                }
            }
            
            return results;

        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * <h4>2 step XPath</h4>
     * <p>
     *  {@link WebScraper#value()} で指定した XPath で取得できる部分 XML から
     *  {@link Target#value()} で指定した XPath で取得する方法。
     * </p>
     * <li> TODO now 2 step XPath only
     * <li> TODO {@link WebScraper#value()} が存在すれば 2 step とか
     */
    public void foreach(Class<T> type, EachHandler<T> eachHandler, InputHandler<Reader> inputHandler, String ... args) {
        try {
            String encoding = WebScraper.Util.getEncoding(type);
//System.err.println("encoding: " + encoding);
            
            InputSource in = new InputSource(inputHandler.getInput(args));
            in.setEncoding(encoding);
    
            String xpath = WebScraper.Util.getValue(type);
    
            Object nodeSet = xPath.evaluate(xpath, in, XPathConstants.NODESET);

            NodeList nodeList = NodeList.class.cast(nodeSet);
//System.err.println("nodeList: " + nodeList.getLength());

            for (int i = 0; i < nodeList.getLength(); i++) {
                T bean = type.newInstance();
                
                Node node = nodeList.item(i);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                new PrettyPrinter(new PrintWriter(baos)).print(node);
    
                Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
                for (Field field : targetFields) {
                    String subXpath = Target.Util.getValue(field);
                    InputSource is = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
                    String text = (String) xPath.evaluate(subXpath, is, XPathConstants.STRING);
                    BeanUtil.setFieldValue(field, bean, text);
                }
                
                eachHandler.exec(bean);
            }

        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
