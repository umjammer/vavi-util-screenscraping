/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;

import vavix.util.screenscrape.annotation.HtmlXPathParser;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * list blog by category and page no.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/12 umjammer initial version <br>
 */
public class Hatena {

    static WebClient client = new WebClient(BrowserVersion.FIREFOX_ESR);

    static {
        client.setJavaScriptEngine(null);
    }

    @WebScraper(url = "http://umjammer.hatenablog.com/archive/category/{0}?page={1}",
                value = "//*[@id='main-inner']/DIV[2]/SECTION",
                parser = HtmlXPathParser.class)
    public static class Result {
        @Target(value = "/SECTION/DIV[1]/H1/A/text()")
        String title;
        @Target(value = "/SECTION/DIV[1]/DIV/A/TIME[@datetime]")
        String date;
        @Target(value = "/SECTION/DIV[3]/P/text()")
        String desc;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(title);
            sb.append(",");
            sb.append(date.replaceAll("\\s", ""));
            sb.append(",");
            sb.append(desc);
            return sb.toString();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        for (int i = 1; i < 10; i++) {
            WebScraper.Util.foreach(Result.class, System.out::println, "映画", String.valueOf(i));
        }
    }
}

/* */
