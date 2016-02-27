/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.translation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

import vavi.util.Debug;

import vavix.net.proxy.CyberSyndromeProxyServerDao;
import vavix.net.proxy.PropertiesUserAgentDao;
import vavix.net.proxy.ProxyChanger;
import vavix.net.proxy.ProxyChanger.InternetAddress;
import vavix.net.proxy.ProxyServerDao;
import vavix.net.proxy.UserAgentSwitcher;
import vavix.util.screenscrape.Scraper;
import vavix.util.screenscrape.SimpleURLScraper;
import vavix.util.screenscrape.StringI18nSimpleXPathScraper;


/**
 * Google の機械翻訳を利用する翻訳機です。 
 * 
 * @author <a href=mailto:vavivavi@yahoo.co.jp>nsano</a>
 * @version 0.00 071002 nsano initial version <br>
 */
public class GoogleTranslator implements Translator {

    /** url host */
    private static String HOST;
    /** url port */
    private static int PORT;
    /** url path, specify one {0} */
    private static String TO_LOCAL;
    /** url path, specify one {0} */
    private static String TO_GLOBAL;

    /** url encoding */
    private static String encoding;

    /** */
    private static class MyScraper extends SimpleURLScraper<String> {
        /** */
        static UserAgentSwitcher userAgentSwitcher;
        /** */
        static ProxyChanger proxyChanger;
        /* */
        static {
            userAgentSwitcher = new UserAgentSwitcher();
            userAgentSwitcher.setUserAgentDao(new PropertiesUserAgentDao());

            proxyChanger = new ProxyChanger();
            ProxyServerDao proxyServerDao = new CyberSyndromeProxyServerDao();
            proxyChanger.setProxyServerDao(proxyServerDao);
            while (proxyServerDao.getProxyInetSocketAddresses().size() < 5) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }
        }
        /** */
        public MyScraper() {
            super(new Scraper<InputStream, String>() {
                public String scrape(InputStream source) {
                    return null;
                }
            });
        }
        /** */
        public MyScraper(String xpath, final String cookie, final String referer) {
            super(new StringI18nSimpleXPathScraper(xpath, encoding),
                  new Properties() {
                    {
                        InternetAddress inetSocketAddress = proxyChanger.getInetSocketAddress();
                        String host = inetSocketAddress.getHostName();
                        int port = inetSocketAddress.getPort();
Debug.println("proxy: " + host + ":" + port);
//                        System.setProperty("http.proxyHost", host);
//                        System.setProperty("http.proxyPort", String.valueOf(port));
                        setProperty("proxy.host", host);
                        setProperty("proxy.port", String.valueOf(port));

                        String userAgent = userAgentSwitcher.getUserAgent();
Debug.println("userAgent: " + userAgent);

                        setProperty("header.User-Agent", userAgent); // 必須
                        setProperty("header.Cookie", cookie); // 無くてもよい
                        setProperty("header.Accept-Charset", "utf-8;q=0.7,*;q=0.7"); // 無くてもよい
                        setProperty("header.Referer", referer);
                    }
                  });
        }
    }

    /**
     * @param word use {@link #encoding} when url encoding 
     */
    public String toLocal(String word) throws IOException {
        return translate(word, TO_LOCAL);
    }

    /** */
    private String translate(String word, String base) throws IOException {
        MyScraper scraper1 = new MyScraper();
        scraper1.scrape(new URL(url1));
        String cookie = scraper1.getCookie();

        word = URLEncoder.encode(word, encoding);
        String file = MessageFormat.format(base, word);
        URL url = new URL("http", HOST, PORT, file);
Debug.println("url: " + url);

        MyScraper scraper2 = new MyScraper(xpath2, cookie, url1);
        String converted = scraper2.scrape(url);
        return converted;
    }

    /**
     * @param word use {@link #encoding} when url encoding 
     */
    public String toGlobal(String word) throws IOException {
        return translate(word, TO_GLOBAL);
    }

    /** */
    private static String url1;
    /** */
    private static String xpath2;

    /** */
    static {
        final Class<?> clazz = GoogleTranslator.class;
        final String path = "GoogleTranslator.properties";
        try {
            Properties props = new Properties();
            props.load(clazz.getResourceAsStream(path));
            
            HOST = props.getProperty("host");
            PORT = Integer.parseInt(props.getProperty("port"));
            TO_LOCAL = props.getProperty("file.toLocal");
            TO_GLOBAL = props.getProperty("file.toGlobal");
            
            encoding = props.getProperty("encoding");

            url1 = props.getProperty("url1");
            xpath2   = props.getProperty("xpath2");
        } catch (Exception e) {
Debug.printStackTrace(e);
            throw new IllegalStateException(e);
        }
    }
    
    /** */
    public Locale getLocalLocale() {
        return Locale.JAPANESE;
    }

    /** */
    public Locale getGlobalLocal() {
        return Locale.ENGLISH;
    }
}

/* */
