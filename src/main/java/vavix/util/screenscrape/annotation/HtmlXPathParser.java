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
import java.io.StringReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

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
import vavi.xml.util.XPathDebugger;

import static java.lang.System.getLogger;


/**
 * HtmlXPathParser.
 * <p>
 * parse HTML using cyberneko.
 * </p>
 * <p>
 * CAUTION: xpath at {@link Target#value()} should be upper cased.
 * </p>
 * TODO binder
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/01 nsano initial version <br>
 */
public class HtmlXPathParser<T> implements Parser<Reader, T> {

    private static final Logger logger = getLogger(HtmlXPathParser.class.getName());

    /** */
    protected final XPath xPath;

    {
logger.log(Level.TRACE, XPathFactory.DEFAULT_PROPERTY_NAME + ":" + XPathFactory.DEFAULT_OBJECT_MODEL_URI);
        System.setProperty(XPathFactory.DEFAULT_PROPERTY_NAME + ":" + XPathFactory.DEFAULT_OBJECT_MODEL_URI, "org.apache.xpath.jaxp.XPathFactoryImpl");
        xPath = XPathFactory.newInstance().newXPath();
logger.log(Level.TRACE, XPathFactory.newInstance().getClass());
    }

    static final String JAXP_KEY_DBF = "javax.xml.parsers.DocumentBuilderFactory";
    static final String JAXP_VALUE_DBF_CYBERNEKO = "vavi.xml.jaxp.html.cyberneko.DocumentBuilderFactoryImpl";

    String backup;

    /** backups current property */
    private void push() {
        backup = System.getProperty(JAXP_KEY_DBF);
    }

    /** restores backup property */
    private void pop() {
        if (backup != null)
            System.setProperty(JAXP_KEY_DBF, backup);
    }

    /**
     * TODO A Reader can be any of the arguments of an InputSource.
     * <li> TODO now 1 step XPath only
     */
    @Override
    public List<T> parse(Class<T> type, InputHandler<Reader> handler, String ... args) {
        try {
            String encoding = WebScraper.Util.getEncoding(type);
if (WebScraper.Util.isDebug(type)) {
 logger.log(Level.TRACE, "encoding: " + encoding);
}
            List<T> results = new ArrayList<>();

            push();
            System.setProperty(JAXP_KEY_DBF, JAXP_VALUE_DBF_CYBERNEKO);

            Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
            for (Field field : targetFields) {
                Reader reader = handler.getInput(args);

                InputSource in = new InputSource(reader);
                in.setEncoding(encoding);

                String xpath = Target.Util.getValue(field);
if (WebScraper.Util.isDebug(type)) {
 logger.log(Level.DEBUG, "xpath: " + xpath);
}
                if (WebScraper.Util.isCollection(type)) {

                    NodeList nodeList = (NodeList) xPath.evaluate(xpath, in, XPathConstants.NODESET);
if (WebScraper.Util.isDebug(type)) {
 if (nodeList.getLength() == 0) {
  logger.log(Level.DEBUG, "nodeList: " + nodeList.getLength());
 }
}
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
if (WebScraper.Util.isDebug(type)) {
 logger.log(Level.DEBUG, field.getName() + ": " + text);
}
                        BeanUtil.setFieldValue(field, bean, text);
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
if (WebScraper.Util.isDebug(type)) {
 logger.log(Level.DEBUG, field.getName() + ": " + text);
}
                    BeanUtil.setFieldValue(field, bean, text);
                }
            }

            return results;

        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException | InstantiationException | IOException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new IllegalStateException(e);
        } finally {
            pop();
        }
    }

    /**
     * <h4>2 step XPath</h4>
     * <p>
     *  A method to obtain a part of XML that can be obtained using the XPath
     *  specified in {@link WebScraper#value()} using the XPath specified in {@link Target#value()}.
     * </p>
     * <p>
     * you need to specify at the first element of the {@link Target#value()}
     * as same as the last element in {@link WebScraper#value()}.
     * </p>
     * <li> TODO now 2 step XPath only
     * <li> TODO {@link WebScraper#value()} が存在すれば 2 step とか
     */
    @Override
    public void foreach(Class<T> type, Consumer<T> eachHandler, InputHandler<Reader> inputHandler, String... args) {
        try {
            String encoding = WebScraper.Util.getEncoding(type);
if (WebScraper.Util.isDebug(type)) {
 logger.log(Level.DEBUG, "encoding: " + encoding);
}
            push();
            System.setProperty(JAXP_KEY_DBF, JAXP_VALUE_DBF_CYBERNEKO);

            InputSource in = new InputSource(inputHandler.getInput(args));
            in.setEncoding(encoding);

            String xpath = WebScraper.Util.getValue(type);

            Object nodeSet = xPath.evaluate(xpath, in, XPathConstants.NODESET);

            NodeList nodeList = (NodeList) nodeSet;
if (WebScraper.Util.isDebug(type)) {
 if (nodeList.getLength() == 0) {
  logger.log(Level.DEBUG, "no node list: " + xpath);
  XPathDebugger.getEntryList(new InputSource(inputHandler.getInput(args))).forEach(System.err::println);
 }
}
            // below is needed for sub xpath query
            // !!!CAUTION!!! when adding debugging code or etc. check the code needs cyberneko or not,
            //               and position against this line. <- what r u saying?
            pop();

            for (int i = 0; i < nodeList.getLength(); i++) {
                T bean = type.getDeclaredConstructor().newInstance();

                Node node = nodeList.item(i);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                new PrettyPrinter(new PrintWriter(baos)).print(node); // TODO use constructor w/ encoding
if (WebScraper.Util.isDebug(type)) {
 if (Debug.isLoggable(java.util.logging.Level.FINE)) {
  System.err.println("-------------------------------------------------------------");
  System.err.println(baos); // TODO use encoding
 }
}
                Set<Field> targetFields = WebScraper.Util.getTargetFields(type);
                for (Field field : targetFields) {
                    String subXpath = Target.Util.getValue(field);
                    InputSource is = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
                    in.setEncoding(System.getProperty("file.encoding")); // TODO use encoding
                    String text = (String) xPath.evaluate(subXpath, is, XPathConstants.STRING);
if (WebScraper.Util.isDebug(type)) {
 if (text == null) {
  XPathDebugger.getEntryList(new InputSource(new StringReader(baos.toString()))).forEach(System.err::println);
 } else {
  logger.log(Level.DEBUG, "subXpath: " + subXpath + ": " + text);
 }
}
                    BeanUtil.setFieldValue(field, bean, text.trim());
                }

                eachHandler.accept(bean);
            }

        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException | InstantiationException | IOException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new IllegalStateException(e);
        } finally {
            pop();
        }
    }
}
