/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

import org.junit.jupiter.api.Test;

import vavi.util.CharNormalizerJa;
import vavi.xml.util.PrettyPrinter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


/**
 * WebScraperTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/27 umjammer initial version <br>
 */
public class WebScraperTest {

    protected XPath xPath;

    {
        System.setProperty(XPathFactory.DEFAULT_PROPERTY_NAME + ":" + XPathFactory.DEFAULT_OBJECT_MODEL_URI, "org.apache.xpath.jaxp.XPathFactoryImpl");
        xPath = XPathFactory.newInstance().newXPath();
    }

    @Test
    public void test() throws Exception {
//        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "vavi.xml.jaxp.html.cyberneko.DocumentBuilderFactoryImpl");

        InputSource in = new InputSource(new InputStreamReader((WebScraperTest.class.getResourceAsStream("/amazon.xml"))));
        in.setEncoding("utf-8");

//        String xpath = "/DIV/DIV/DIV/DIV/DIV/DIV/DIV/SPAN/text()";
//        String xpath = "//DIV[@class='a-row a-size-base']";
        String xpath = "//DIV[1]/DIV/DIV/DIV/DIV[1]/DIV/DIV[1]/DIV[2]/SPAN/text()";

        NodeList nodeList = (NodeList) xPath.evaluate(xpath, in, XPathConstants.NODESET);
        assertNotEquals(0, nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            new PrettyPrinter(System.err).print(node);
System.err.println("-------------------------------------------------------------");
        }
    }

    @WebScraper(url = "classpath:amazon.html",
            parser = HtmlXPathParser.class,
            isDebug = true,
            value = "//DIV[@id='ordersContainer']/DIV[@class='a-box-group a-spacing-base order']")
    public static class Result {
        @Target(value = "//DIV[1]/DIV/DIV/DIV/DIV[1]/DIV/DIV[1]/DIV[2]/SPAN/text()")
        String date;
        @Target(value = "//DIV[1]/DIV/DIV/DIV/DIV[1]/DIV/DIV[2]/DIV[2]/SPAN/text()")
        String price;
        @Target(value = "//DIV[2]/DIV/DIV/DIV/DIV[1]/DIV/DIV/DIV/DIV[2]/DIV[1]/A/text()")
        String title;
        @Target(value = "//DIV[2]/DIV/DIV/DIV/DIV[1]/DIV/DIV/DIV/DIV[2]/DIV[2]/SPAN/text()")
        String author;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(date.replaceAll("[年月]", "/").replace("日", ""));
            sb.append(",\"");
            sb.append(CharNormalizerJa.ToHalf.normalize(price).replace('￥', '¥'));
            sb.append("\",\"");
            sb.append(title);
            sb.append("\",\"");
            sb.append(author.replaceAll("\\s+", " "));
            sb.append("\"");
            return sb.toString();
        }
    }

//    @Test
    public void test1() throws Exception {
        List<Result> results = new ArrayList<>();
        WebScraper.Util.foreach(Result.class, e -> results.add(e));
        assertEquals(10, results.size());
    }
}

/* */
