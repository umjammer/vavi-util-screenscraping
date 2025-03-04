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
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import vavi.beans.BeanUtil;
import vavi.util.Debug;
import vavi.xml.util.PrettyPrinter;

import static java.lang.System.getLogger;


/**
 * XPathParser.
 *
 * TODO binder
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/01 nsano initial version <br>
 */
public class XPathParser<T> implements Parser<Reader, T> {

    private static final Logger logger = getLogger(XPathParser.class.getName());

    /** */
    protected final XPath xPath;

    /** */
    protected final DocumentBuilder db;

    {
logger.log(Level.TRACE, XPathFactory.DEFAULT_PROPERTY_NAME + ":" + XPathFactory.DEFAULT_OBJECT_MODEL_URI);
//        System.setProperty(XPathFactory.DEFAULT_PROPERTY_NAME + ":" + XPathFactory.DEFAULT_OBJECT_MODEL_URI, "org.apache.xpath.jaxp.XPathFactoryImpl");
        xPath = XPathFactory.newInstance().newXPath();
logger.log(Level.TRACE, XPathFactory.newInstance().getClass());
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            db = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * TODO how about: Reader accepts same as the InputSource arguments
     * TODO InputHandler needs cache because Reader is inside of fields loop
     * <li> TODO now 1 step XPath only
     */
    @Override
    public List<T> parse(Class<T> type, InputHandler<Reader> handler, String ... args) {
        try {
            String encoding = WebScraper.Util.getEncoding(type);
logger.log(Level.TRACE, "encoding: " + encoding);
            Reader reader = handler.getInput(args);

            InputSource in = new InputSource(reader);
            in.setEncoding(encoding);

            Document document = db.parse(in);
if (WebScraper.Util.isDebug(type)) {
 if (Debug.isLoggable(java.util.logging.Level.FINE)) {
  PrettyPrinter pp = new PrettyPrinter(System.err);
  pp.print(document);
 }
}
            List<T> results = new ArrayList<>();

            Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
            for (Field field : targetFields) {

                String xpath = Target.Util.getValue(field);
logger.log(Level.TRACE, "xpath: " + xpath);

                if (WebScraper.Util.isCollection(type)) {

                    NodeList nodeList = (NodeList) xPath.evaluate(xpath, document, XPathConstants.NODESET);
logger.log(Level.TRACE, "nodeList: " + nodeList.getLength());
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        // because loops for each field, instantiation should be done once
                        T bean = null;
                        try {
                            bean = results.get(i);
                        } catch (IndexOutOfBoundsException e) {
                            bean = type.getDeclaredConstructor().newInstance();
                            results.add(bean);
                        }

                        String text = nodeList.item(i).getTextContent().trim();
logger.log(Level.TRACE, field.getName() + ": " + text);
                        BeanUtil.setFieldValue(field, bean, text);
                    }
                } else {

                    // because loops for each field, instantiation should be done once
                    T bean = null;
                    try {
                        bean = results.get(0);
                    } catch (IndexOutOfBoundsException e) {
                        bean = type.getDeclaredConstructor().newInstance();
                        results.add(bean);
                    }

                    String text = ((String) xPath.evaluate(xpath, document, XPathConstants.STRING)).trim();
                    BeanUtil.setFieldValue(field, bean, text);
                }
            }

            return results;

        } catch (XPathExpressionException | SAXException e) {
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
logger.log(Level.TRACE, "encoding: " + encoding);

            InputSource in = new InputSource(inputHandler.getInput(args));
            in.setEncoding(encoding);

            String xpath = WebScraper.Util.getValue(type);

            Object nodeSet = xPath.evaluate(xpath, in, XPathConstants.NODESET);

            NodeList nodeList = (NodeList) nodeSet;
//logger.log(Level.TRACE, "nodeList: " + nodeList.getLength());
if (WebScraper.Util.isDebug(type)) {
 if (nodeList.getLength() == 0) {
//logger.log(Level.TRACE, "xpath: " + xpath);
  logger.log(Level.DEBUG, "no node list: " + xpath);
  new PrettyPrinter(System.err).print(new InputSource(inputHandler.getInput(args)));
 }
}

            for (int i = 0; i < nodeList.getLength(); i++) {
                T bean = type.getDeclaredConstructor().newInstance();

                Node node = nodeList.item(i);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                new PrettyPrinter(new PrintWriter(baos)).print(node);
if (WebScraper.Util.isDebug(type)) {
 if (Debug.isLoggable(java.util.logging.Level.FINE)) {
  System.err.println("-------------------------------------------------------------");
  System.err.println(baos); // TODO use encoding
 }
}
                InputSource is = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
                is.setEncoding(encoding);

                Document document = db.parse(is);

                Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
                for (Field field : targetFields) {
                    String subXpath = Target.Util.getValue(field);
                    String text = (String) xPath.evaluate(subXpath, document, XPathConstants.STRING);
                    BeanUtil.setFieldValue(field, bean, text);
                }

                eachHandler.accept(bean);
            }

        } catch (XPathExpressionException | SAXException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException | InstantiationException | IOException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
