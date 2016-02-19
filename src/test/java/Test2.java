/*
 * Copyright (c) 2014 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.List;
import java.util.Scanner;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import vavix.util.screenscrape.annotation.HtmlXPathParser;
import vavix.util.screenscrape.annotation.InputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * Test2. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2014/04/30 umjammer initial version <br>
 */
public class Test2 {

    static WebClient client = new WebClient(BrowserVersion.FIREFOX_10);

    static {
        client.setJavaScriptEnabled(false);
    }

    /** */
    public static class MyInput implements InputHandler<Reader> {
        String cacheKey;
        String cache;
        /**
         * @param args 0: artist
         */
        public Reader getInput(String ... args) throws IOException {

            String name = args[0];

            if (!name.equals(cacheKey)) {
                HtmlPage page = client.getPage("https://www.google.com/search?q=" + URLEncoder.encode(name, "UTF-8") + "#lr=lang_ja");
                cache = page.asXml();
                cacheKey = name;
            }

            return new StringReader(cache);
        }
    }

    /** */
    @WebScraper(input = MyInput.class,
                parser = HtmlXPathParser.class)
    public static class Data {
        @Target("//SPAN[@class='st']/text()")
        String name;
        @Target("//SPAN[@class='st']/text()")
        String pronunciation;
        public String toString() {
            StringBuilder sb = new StringBuilder();
//            sb.append("name: ");
//            sb.append(name);
//            sb.append(", ");
            sb.append("pronunciation: ");
            sb.append(pronunciation);
            return sb.toString();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(new File(args[0]));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            List<Data> data = WebScraper.Util.scrape(Data.class, line);
            for (Data datum : data) {
                System.err.println(line + ": " + datum);
            }
            Thread.sleep(5000);
        }
        scanner.close();
    }
}

/* */
