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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import vavi.net.auth.oauth2.amazon.AmazonLocalAuthenticator;
import vavi.util.CharNormalizerJa;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import vavix.util.screenscrape.annotation.DefaultInputHandler;
import vavix.util.screenscrape.annotation.HtmlXPathParser;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * AmazonPurchaseHistory. 2020 version.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/12 umjammer initial version <br>
 */
@PropsEntity(url = "file://${user.dir}/local.properties")
public class AmazonPurchaseHistory {

    @Property(name = "java.test.amazon.email")
    String email;

    static WebDriver driver;

    /** */
    public static class MyInput extends DefaultInputHandler {
        private String cache;
        /**
         * @param args 0: url, 1: ignore, 2: start
         */
        public Reader getInput(String ... args) throws IOException {
            if (cache != null) {
                return new StringReader(cache);
            }

            String url = args[0];
            int start = Integer.parseInt(args[2]);

//System.err.println("goto: " + url);
            driver.navigate().to(url);

            WebDriverWait wait = new WebDriverWait(driver, 10);
            try { Thread.sleep(300); } catch (InterruptedException e) {}
            wait.until(d -> {
                if (d == null) {
                    throw new IllegalStateException("browser maight be closed");
                }
                String r = ((JavascriptExecutor) d).executeScript("return document.readyState;").toString();
//Debug.println(r);
                return "complete".equals(r);
            });

//System.err.println("location: " + driver.getCurrentUrl());
            //
            cache = driver.getPageSource();
//System.err.println(cache);
//try {
// SAXParserFactory spf = SAXParserFactory.newInstance();
// SAXParser sp = spf.newSAXParser();
// XMLReader xr = sp.getXMLReader();
//
// xr.setContentHandler(new FragmentContentHandler(xr));
// xr.parse(new InputSource(new StringReader(cache)));
//} catch (ParserConfigurationException | SAXException e) {
// e.printStackTrace();
//}

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
        AmazonPurchaseHistory app = new AmazonPurchaseHistory();
        PropsEntity.Util.bind(app);

        String url = "https://www.amazon.co.jp/ap/signin?openid.return_to=https%3A%2F%2Fwww.amazon.co.jp%2Fref%3Dgw_sgn_ib%2F358-4710901-2880702&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.assoc_handle=jpflex&openid.mode=checkid_setup&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0";
        driver = new AmazonLocalAuthenticator(url).authorize(app.email);
//System.err.println("auth done");

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

        driver.quit();
    }
}

/* */
