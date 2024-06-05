/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.net.URL;
import java.util.Properties;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import vavix.util.screenscrape.ApacheURLScraper;
import vavix.util.screenscrape.Scraper;
import vavix.util.screenscrape.SimpleURLScraper;
import vavix.util.screenscrape.StringSimpleXPathScraper;


/**
 * getGlobalIpTest.
 *
 * TODO 2022-02-25 not work
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051014 nsano initial version <br>
 */
@Disabled
@SuppressWarnings("deprecation")
class getGlobalIpTest {

    static {
        System.setProperty("vavi.util.logging.VaviFormatter.extraClassMethod",
                           "org\\.apache\\.commons\\.logging\\.impl\\.Jdk14Logger#\\w+");
    }

    static final String url = "http://x68000.q-e-d.net/~68user/net/sample/http-auth/secret.html";
    static final String realm = "Secret File";
    static final String host = "x68000.q-e-d.net";
    static final String account = "hoge";
    static final String password = "fuga";

    @Test
    void test02() throws Exception {
        Properties props = new Properties();
        props.setProperty("account", account);
        props.setProperty("password", password);

        Scraper<URL, String> scraper = new SimpleURLScraper<>(new StringSimpleXPathScraper("/HTML/BODY/text()"), props);

        System.out.println("SimpleXPathURLScraper: " + scraper.scrape(new URL(url)).trim());
    }
}
