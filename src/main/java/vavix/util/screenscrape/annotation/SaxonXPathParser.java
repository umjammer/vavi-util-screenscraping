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
import java.lang.reflect.InvocationTargetException;
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

import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.om.NodeInfo;


/**
 * XPathParser.
 *
 * TODO binder
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/01 nsano initial version <br>
 */
public class SaxonXPathParser<T> implements Parser<Reader, T> {

    static final String JAXP_KEY_XPF = XPathFactory.DEFAULT_PROPERTY_NAME + ":" + XPathFactory.DEFAULT_OBJECT_MODEL_URI;
    static final String JAXP_VALUE_XPF_SAXON = "net.sf.saxon.xpath.XPathFactoryImpl";

    /** */
    protected XPath xPath;

    {
        String backup = System.setProperty(JAXP_KEY_XPF, JAXP_VALUE_XPF_SAXON);
        System.setProperty(JAXP_KEY_XPF, JAXP_VALUE_XPF_SAXON);
        XPathFactory factory = XPathFactory.newInstance();
        assert factory.getClass().getName().equals(JAXP_VALUE_XPF_SAXON) : "not saxon factory: " + factory.getClass().getName();
Debug.println(Level.FINE, "XPathFactory: " + factory.getClass().getName());
        xPath = factory.newXPath();
        if (backup != null)
            System.setProperty(JAXP_KEY_XPF, backup);
    }

    /**
     * <li> TODO WebScraper#value()
     * <li> TODO now 1 step XPath only
     */
    @Override
    public List<T> parse(Class<T> type, InputHandler<Reader> inputHandler, String ... args) {
        try {
            String encoding = WebScraper.Util.getEncoding(type);
Debug.println(Level.FINE, "encoding: " + encoding);

            List<T> results = new ArrayList<>();

            Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
            for (Field field : targetFields) {

                InputSource in = new InputSource(inputHandler.getInput(args));
                in.setEncoding(encoding);

                String xpath = Target.Util.getValue(field);
Debug.println(Level.FINE, "xpath: " + xpath);

                if (WebScraper.Util.isCollection(type)) {

                    Object nodeSet = xPath.evaluate(xpath, in, XPathConstants.NODESET);

                    if (nodeSet instanceof List) {

                        @SuppressWarnings("unchecked")
                        List<NodeInfo> nodeList = (List<NodeInfo>) nodeSet;
if (nodeList.size() == 0) {
 Debug.println(Level.WARNING, "no node list: " + xpath);
}
                        for (int i = 0; i < nodeList.size(); i++) {
                            // because loops for each fields, instantiation should be done once
                            T bean = null;
                            try {
                                bean = results.get(i);
                            } catch (IndexOutOfBoundsException e) {
                                bean = type.getDeclaredConstructor().newInstance();
                                results.add(bean);
                            }

                            String text = nodeList.get(i).getStringValue().trim();
Debug.println(Level.FINE, field.getName() + ": " + text);
                            BeanUtil.setFieldValue(field, bean, text);
                        }
                    } else if (nodeSet instanceof NodeList) {

                        NodeList nodeList = (NodeList) nodeSet;
Debug.println(Level.FINE, "nodeList: " + nodeList.getLength());
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            // because loops for each fields, instantiation should be done once
                            T bean = null;
                            try {
                                bean = results.get(i);
                            } catch (IndexOutOfBoundsException e) {
                                bean = type.getDeclaredConstructor().newInstance();
                                results.add(bean);
                            }

                            String text = nodeList.item(i).getTextContent().trim();
Debug.println(Level.FINE, field.getName() + ": " + text);
                            BeanUtil.setFieldValue(field, bean, text);
                        }
                    } else {
                        throw new IllegalStateException("unsupported type returns: " + nodeSet.getClass().getName());
                    }
                } else {

                    // because loops for each fields, instantiation should be done once
                    T bean = null;
                    try {
                        bean = results.get(0);
                    } catch (IndexOutOfBoundsException e) {
                        bean = type.getDeclaredConstructor().newInstance();
                        results.add(bean);
                    }

                    String text = ((String) xPath.evaluate(xpath, in, XPathConstants.STRING)).trim();
                    BeanUtil.setFieldValue(field, bean, text);
                }
            }

            return results;

        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException | InstantiationException | IOException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * <h4>2 step XPath</h4>
     * <p>
     *  the method to retrieve values by `JsonPath` specified at {@link Target#value()} from
     *  part of XML that is retrieved by `JsonPath` specified at {@link WebScraper#value()}
     * </p>
     * <p>
     * you need to specify at the first element of the {@link Target#value()} as same as the last element in {@link WebScraper#value()}.
     * </p>
     * TODO now 2 step XPath only
     * TODO how about: if {@link WebScraper#value()} exists then 2 step
     */
    @Override
    public void foreach(Class<T> type, Consumer<T> eachHandler, InputHandler<Reader> inputHandler, String ... args) {
        try {
            String encoding = WebScraper.Util.getEncoding(type);
//System.err.println("encoding: " + encoding);

            InputSource in = new InputSource(inputHandler.getInput(args));
            in.setEncoding(encoding);

            String xpath = WebScraper.Util.getValue(type);

            Object nodeSet = xPath.evaluate(xpath, in, XPathConstants.NODESET);

            if (nodeSet instanceof List) {

                @SuppressWarnings("unchecked")
                List<NodeInfo> nodeList = (List<NodeInfo>) nodeSet;
//System.err.println("nodeList: " + nodeList.size());
if (nodeList.size() == 0) {
 Debug.println(Level.WARNING, "no node list: " + xpath);
}

                for (NodeInfo nodeInfo : nodeList) {
                    T bean = type.getDeclaredConstructor().newInstance();

                    NodeInfo node = nodeInfo;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    new PrettyPrinter(new PrintWriter(baos)).print(NodeOverNodeInfo.wrap(node));

                    Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
                    for (Field field : targetFields) {
                        String subXpath = Target.Util.getValue(field);
//System.err.println("----------------------------------------------------------------------------------------------------");
//System.err.println(baos.toString());
//System.err.println("----------------------------------------------------------------------------------------------------");
                        InputSource is = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
                        in.setEncoding(encoding);
                        String text = (String) xPath.evaluate(subXpath, is, XPathConstants.STRING);
                        BeanUtil.setFieldValue(field, bean, text);
                    }

                    eachHandler.accept(bean);
                }
            } else if (nodeSet instanceof NodeList) {

                NodeList nodeList = (NodeList) nodeSet;
//System.err.println("nodeList: " + nodeList.getLength());
if (nodeList.getLength() == 0) {
 Debug.println(Level.WARNING, "no node list: " + xpath);
}

                for (int i = 0; i < nodeList.getLength(); i++) {
                    T bean = type.getDeclaredConstructor().newInstance();

                    Node node = nodeList.item(i);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    new PrettyPrinter(new PrintWriter(baos)).print(node);

                    Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
                    for (Field field : targetFields) {
                        String subXpath = Target.Util.getValue(field);
                        InputSource is = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
                        in.setEncoding(encoding);
                        String text = (String) xPath.evaluate(subXpath, is, XPathConstants.STRING);
                        BeanUtil.setFieldValue(field, bean, text);
                    }

                    eachHandler.accept(bean);
                }
            } else {
                throw new IllegalStateException("unsupported type returns: " + nodeSet.getClass().getName());
            }

        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException | InstantiationException | IOException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }
}
