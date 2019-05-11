/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import vavix.util.screenscrape.annotation.DefaultInputHandler;
import vavix.util.screenscrape.annotation.HtmlXPathParser;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * Amazon.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/12 umjammer initial version <br>
 */
@PropsEntity(url = "file://${user.dir}/local.properties")
public class Amazon {

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
         * @param args 0: url, 1: ignore, 2: ignore, 3: email, 4: password
         */
        public Reader getInput(String ... args) throws IOException {
            if (cache != null) {
                return new StringReader(cache);
            }

            String url = args[0];
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

            return new StringReader(cache);
        }
    }

    @WebScraper(url = "https://www.amazon.co.jp/gp/yourstore/iyr/ref=pd_ys_iyr_next?ie=UTF8&collection=owned&iyrGroup=&maxItem={0}&minItem={1}",
                input = MyInput.class,
                parser = HtmlXPathParser.class,
                encoding = "Windows-31J",
                value = "/HTML/BODY/TABLE/TBODY/TR/TD/DIV[@id='iyrCenter']/TABLE/TBODY/TR[@valign='middle']")
    public static class Result {
        @Target(value = "/TR/TD/FONT/B/A/text()")
        String title;
        @Target(value = "/TR/TD/FONT/SPAN/text()")
        String author;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(title);
            sb.append(",");
            sb.append(author);
            return sb.toString();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Amazon app = new Amazon();
        PropsEntity.Util.bind(app);
        for (int i = 0; i < 193; i++) {
            int min = i * 15 + 1;
            int max = (i + 1) * 15;
            WebScraper.Util.foreach(Result.class, System.out::println, String.valueOf(max), String.valueOf(min), app.email, app.password);
        }
    }
}

/* */
