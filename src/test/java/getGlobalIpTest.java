/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.net.URL;
import java.util.Properties;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

import vavix.util.screenscrape.ApacheURLScraper;
import vavix.util.screenscrape.Scraper;
import vavix.util.screenscrape.SimpleURLScraper;
import vavix.util.screenscrape.StringApacheXPathScraper;
import vavix.util.screenscrape.StringSimpleXPathScraper;


/**
 * getGlobalIpTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051014 nsano initial version <br>
 */
@Disabled
public class getGlobalIpTest {

    String url = "http://x68000.q-e-d.net/~68user/net/sample/http-auth/secret.html";
    String realm = "Secret File";
    String host = "x68000.q-e-d.net";
    String account = "hoge";
    String password = "fuga";

    /** */
    @Test
    public void test01() {
        try {
            Properties props = new Properties();
            props.setProperty("realm", realm);
            props.setProperty("host", host);
            props.setProperty("account", account);
            props.setProperty("password", password);

            Scraper<URL, String> scraper = new ApacheURLScraper<>(new StringApacheXPathScraper("/HTML/BODY/text()"), props);

            System.out.println("ApacheXPathURLScraper: " + scraper.scrape(new URL(url)));
        } catch (Exception e) {
e.printStackTrace();
            fail();
        }
    }

    /** */
    @Test
    public void test02() {
        try {
            Properties props = new Properties();
            props.setProperty("account", account);
            props.setProperty("password", password);

            Scraper<URL, String> scraper = new SimpleURLScraper<>(new StringSimpleXPathScraper("/HTML/BODY/text()"), props);

            System.out.println("SimpleXPathURLScraper: " + scraper.scrape(new URL(url)).trim());
        } catch (Exception e) {
e.printStackTrace();
            fail();
        }
    }
}

/* */
