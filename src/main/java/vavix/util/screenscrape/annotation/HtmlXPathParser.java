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
import java.util.function.Consumer;
import java.util.logging.Level;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

import vavi.beans.BeanUtil;
import vavi.util.Debug;
import vavi.xml.util.PrettyPrinter;


/**
 * HtmlXPathParser.
 * <p>
 * parse HTML using cyberneko.
 * </p>
 * <p>
 * CAUTION: xpath should be upper cased.
 * </p>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/01 nsano initial version <br>
 */
public class HtmlXPathParser<T> implements Parser<Reader, T> {

    /** */
    protected XPath xPath;

    {
//System.err.println(XPathFactory.DEFAULT_PROPERTY_NAME + ":" + XPathFactory.DEFAULT_OBJECT_MODEL_URI);
        System.setProperty(XPathFactory.DEFAULT_PROPERTY_NAME + ":" + XPathFactory.DEFAULT_OBJECT_MODEL_URI, "org.apache.xpath.jaxp.XPathFactoryImpl");
        xPath = XPathFactory.newInstance().newXPath();
//System.err.println(XPathFactory.newInstance().getClass());
    }

    /**
     * TODO Reader は、 InputSource の引数ならどれでもとか
     * <li> TODO now 1 step XPath only
     */
    public List<T> parse(Class<T> type, InputHandler<Reader> handler, String ... args) {
        try {
            String encoding = WebScraper.Util.getEncoding(type);
//System.err.println("encoding: " + encoding);

            List<T> results = new ArrayList<>();

            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "vavi.xml.jaxp.html.cyberneko.DocumentBuilderFactoryImpl");

            Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
            for (Field field : targetFields) {
                Reader reader = handler.getInput(args);

                InputSource in = new InputSource(reader);
                in.setEncoding(encoding);

                String xpath = Target.Util.getValue(field);
//PrettyPrinter pp = new PrettyPrinter(System.err);
//pp.print(in);
//System.err.println("xpath: " + xpath);

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
        } catch (IllegalAccessException | InstantiationException | IOException e) {
            throw new IllegalStateException(e);
        } finally {
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        }
    }

    /**
     * <h4>2 step XPath</h4>
     * <p>
     *  {@link WebScraper#value()} で指定した XPath で取得できる部分 XML から
     *  {@link Target#value()} で指定した XPath で取得する方法。
     * </p>
     * <p>
     * you need to specify at the first element of the {@link Target#value()} as same as the last element in {@link WebScraper#value()}.
     * </p>
     * <li> TODO now 2 step XPath only
     * <li> TODO {@link WebScraper#value()} が存在すれば 2 step とか
     */
    public void foreach(Class<T> type, Consumer<T> eachHandler, InputHandler<Reader> inputHandler, String... args) {
        try {
            String encoding = WebScraper.Util.getEncoding(type);
Debug.println(Level.FINE, "encoding: " + encoding);

            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "vavi.xml.jaxp.html.cyberneko.DocumentBuilderFactoryImpl");

            InputSource in = new InputSource(inputHandler.getInput(args));
            in.setEncoding(encoding);

            String xpath = WebScraper.Util.getValue(type);

            Object nodeSet = xPath.evaluate(xpath, in, XPathConstants.NODESET);

            // TODO vavi.xml.jaxp.html.cyberneko has bug! <- what's ???
//            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

            NodeList nodeList = NodeList.class.cast(nodeSet);
//System.err.println("nodeList: " + nodeList.getLength());
if (nodeList.getLength() == 0) {
 Debug.println("no node list: " + xpath);
}

            for (int i = 0; i < nodeList.getLength(); i++) {
                T bean = type.newInstance();

                Node node = nodeList.item(i);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                new PrettyPrinter(new PrintWriter(baos)).print(node); // TODO use constructor w/ encoding
//System.err.println("-------------------------------------------------------------");
//System.err.println(baos.toString()); // TODO use encoding

                Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
                for (Field field : targetFields) {
                    String subXpath = Target.Util.getValue(field);
                    InputSource is = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
                    in.setEncoding(System.getProperty("file.encoding")); // TODO use encoding
                    String text = (String) xPath.evaluate(subXpath, is, XPathConstants.STRING);
                    BeanUtil.setFieldValue(field, bean, text.trim());
                }

                eachHandler.accept(bean);
            }

        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException | InstantiationException | IOException e) {
            throw new IllegalStateException(e);
        } finally {
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        }
    }
}

/* */
