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

import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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

    static final String JAXP_KEY_DBF = "javax.xml.parsers.DocumentBuilderFactory";
    static final String JAXP_VALUE_DBF_DEFAULT = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";
    static final String JAXP_VALUE_DBF_CYBERNEKO = "vavi.xml.jaxp.html.cyberneko.DocumentBuilderFactoryImpl";

    String backup;

    private void push() {
        backup = System.getProperty(JAXP_KEY_DBF);
    }

    private void pop() {
        if (backup != null)
            System.setProperty(JAXP_KEY_DBF, backup);
    }

    protected XPath xPath;

    {
        System.setProperty(XPathFactory.DEFAULT_PROPERTY_NAME + ":" + XPathFactory.DEFAULT_OBJECT_MODEL_URI, "org.apache.xpath.jaxp.XPathFactoryImpl");
        xPath = XPathFactory.newInstance().newXPath();
    }

    @Test
    public void test() throws Exception {
        push();
        System.setProperty(JAXP_KEY_DBF, JAXP_VALUE_DBF_CYBERNEKO);

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

        pop();
    }

    @WebScraper(url = "classpath:amazon.html",
            parser = HtmlXPathParser.class,
            isDebug = true,
            value = "/HTML/BODY/TABLE/TBODY/TR")
    public static class Result {
        @Target(value = "//TR/TD[@class='line-number']/@value")
        String td1;
        @Target(value = "//TR/TD[@class='line-content']/text()")
        String t2;
    }

    @Test
    public void test1() throws Exception {
        List<Result> results = new ArrayList<>();
        WebScraper.Util.foreach(Result.class, e -> results.add(e));
        assertEquals(7987, results.size());
    }

    @WebScraper(url = "classpath:amazon.xml",
            parser = SaxonXPathParser.class,
            isDebug = true,
            value = "//DIV[@class='a-box-group a-spacing-base order']")
    public static class Result2 {
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

    @Test
    public void test2() throws Exception {
//InputStream is = WebScraperTest.class.getResourceAsStream("/amazon.xml");
//XPathDebugger.getEntryList(new InputSource(is)).forEach(System.err::println);

        List<Result2> results = new ArrayList<>();
        WebScraper.Util.foreach(Result2.class, e -> { results.add(e); System.err.println(e); });
        assertEquals(1, results.size());
    }
}

/* */
