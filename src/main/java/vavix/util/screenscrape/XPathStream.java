/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape;

import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import static java.lang.System.getLogger;


/**
 * XPathStream.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-11-15 nsano initial version <br>
 */
public final class XPathStream {

    private static final Logger logger = getLogger(XPathStream.class.getName());

    private XPathStream() {}

    /**
     *
     * @param xml
     * @param xpathExpression
     * @return
     */
    public static Stream<String> source(InputStream xml, String xpathExpression) {

        try {
            // Parse XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xml);

            // Create XPath
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xpath.compile(xpathExpression);

            // Get matching nodes
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
logger.log(Level.INFO, "nodes: " + nodes.getLength());

            // Create iterator
            Iterator<String> iterator = new Iterator<>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return index < nodes.getLength();
                }

                @Override
                public String next() {
                    try {
                        return nodes.item(index++).getTextContent();
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Error converting node at index " + (index - 1), e);
                    }
                }
            };

            // Create stream
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                    false);

        } catch (Exception e) {
            throw new IllegalStateException("Error creating XPath stream", e);
        }
    }
}
