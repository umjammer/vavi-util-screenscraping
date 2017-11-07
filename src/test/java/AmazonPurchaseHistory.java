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

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import vavi.util.CharNormalizerJa;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import vavix.util.screenscrape.annotation.DefaultInputHandler;
import vavix.util.screenscrape.annotation.HtmlXPathParser;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * AmazonPurchaseHistory.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/12 umjammer initial version <br>
 */
@PropsEntity(url = "file://${user.dir}/local.properties")
public class AmazonPurchaseHistory {

    @Property(name = "java.test.amazon.email")
    String email;
    @Property(name = "java.test.amazon.password")
    String password;

    static WebClient client = new WebClient(BrowserVersion.FIREFOX_10);

    static {
        client.setJavaScriptEnabled(false);
    }

    /** */
    public static class MyInput extends DefaultInputHandler {
        private String cache;
        /**
         * @param args 0: url, 1: ignore, 2: start, 3: email, 4: password
         */
        public Reader getInput(String ... args) throws IOException {
            if (cache != null) {
                return new StringReader(cache);
            }

            String url = args[0];
            int start = Integer.parseInt(args[2]);
            String email = args[3];
            String password = args[4];
            HtmlPage page0 = client.getPage(url);

//System.exit(1);
            if (page0.getUrl().toString().startsWith("https://www.amazon.co.jp/ap/signin/")) {
System.err.println(page0.getUrl());
//System.err.println(page0.asXml());

                HtmlInput input1 = (HtmlInput) page0.getHtmlElementById("ap_email");
                input1.setValueAttribute(email);
                HtmlInput input2 = (HtmlInput) page0.getHtmlElementById("ap_password");
                input2.setValueAttribute(password);
                HtmlInput input3 = (HtmlInput) page0.getHtmlElementById("signInSubmit");

                page0 = input3.click();
System.err.println("-----------------------------------------------------------------------------------");
            }
//System.err.println(page0.getUrl());
//System.err.println(page0.asXml());

            //
            cache = page0.asXml();
//System.err.println(cache);
            try {
                System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "vavi.xml.jaxp.html.cyberneko.DocumentBuilderFactoryImpl");
                XPath xPath = XPathFactory.newInstance().newXPath();
                InputSource in = new InputSource(new StringReader(cache));
                String xpath = "//*[@id='controlsContainer']/DIV[2]/SPAN[2]/SPAN/text()";
                String text = (String) xPath.evaluate(xpath, in, XPathConstants.STRING);
//System.err.println("text: " + text);
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
                encoding = "Windows-31J",
                value = "//DIV[@id='ordersContainer']/DIV[@class='a-box-group a-spacing-base order']")
    public static class Result {
        @Target(value = "/DIV/DIV[1]/DIV/DIV/DIV/DIV[1]/DIV/DIV[1]/DIV[2]/SPAN/text()")
        String date;
        @Target(value = "/DIV/DIV[1]/DIV/DIV/DIV/DIV[1]/DIV/DIV[2]/DIV[2]/SPAN/text()")
        String price;
        @Target(value = "/DIV/DIV[2]/DIV/DIV/DIV/DIV[1]/DIV/DIV/DIV/DIV[2]/DIV[1]/A/text()")
        String title;
        @Target(value = "/DIV/DIV[2]/DIV/DIV/DIV/DIV[1]/DIV/DIV/DIV/DIV[2]/DIV[2]/SPAN/text()")
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
        for (int year = 2000; year < 2017; year++) {
            for (int i = 0; ; i++) {
                try {
                    int start = i * 10;
                    WebScraper.Util.foreach(Result.class, System.out::println, String.valueOf(year), String.valueOf(start), app.email, app.password);
                } catch (NoSuchElementException e) {
//                    System.err.println(year + ": " + e.getMessage());
                    break;
                }
            }
        }
    }
}

/* */
