/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.NoSuchElementException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

import org.openqa.selenium.WebDriver;

import vavi.util.CharNormalizerJa;

import vavix.util.screenscrape.annotation.HtmlXPathParser;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;
import vavix.util.selenium.SeleniumUtil;


/**
 * AmazonPurchaseHistory. 2020 version.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/12 umjammer initial version <br>
 */
public class AmazonPurchaseHistory {

    /** */
    public static class MyInput extends Amazon.MyInput {
        /**
         * @param args 0: url, 1: ignore, 2: start
         */
        public Reader getInput(String ... args) throws IOException {

            String url = args[0];
            int start = Integer.parseInt(args[2]);

            WebDriver driver = getDriver();
//System.err.println("goto: " + url);
            driver.navigate().to(url);

            SeleniumUtil.waitFor(driver);

//System.err.println("location: " + driver.getCurrentUrl());
            //
            cache = driver.getPageSource();

            try {
                System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "vavi.xml.jaxp.html.cyberneko.DocumentBuilderFactoryImpl");
                XPath xPath = XPathFactory.newInstance().newXPath();
                InputSource in = new InputSource(new StringReader(cache));
                String xpath = "//SPAN[@class='num-orders']/text()";
                String text = (String) xPath.evaluate(xpath, in, XPathConstants.STRING);
//System.err.println("num-orders: " + text);
                text = text.replace("件", "").trim();
                int max = text.isEmpty() ? 0 : Integer.parseInt(text);
                if (start > max) {
                    throw new NoSuchElementException(start + " > " + max);
                }
            } catch (XPathExpressionException e) {
                throw new IllegalStateException(e);
            }

            return new StringReader(cache);
        }
    }

    @WebScraper(url = "https://www.amazon.co.jp/gp/css/order-history?digitalOrders=1&unifiedOrders=1&orderFilter=year-{0}&startIndex={1}",
                input = MyInput.class,
                parser = HtmlXPathParser.class,
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

    /**
     */
    public static void main(String[] args) throws Exception {
        for (int year = 2000; year <= 2020; year++) {
            for (int i = 0; ; i++) {
                try {
                    int start = i * 10;
                    WebScraper.Util.foreach(Result.class, System.out::println, String.valueOf(year), String.valueOf(start));
                } catch (NoSuchElementException e) {
//                    System.err.println(year + ": " + e.getMessage());
                    break;
                }
            }
        }
    }
}

/* */
