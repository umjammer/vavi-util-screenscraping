/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.checkerframework.common.reflection.qual.GetMethod;


/**
 * Apache Commons HttpClient GET method.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051014 nsano initial version <br>
 */
@Deprecated
public class ApacheURLScraper<O> extends AbstractApacheHttpScraper<URL, O> {

    /** */
    protected ApacheURLScraper(Scraper<InputStream, O> scraper) {
        super(scraper);
    }

    /**
     * @param props use followings
     * <pre>
     *  "auth.account" BASIC 認証アカウント名
     *  "auth.password" BASIC 認証パスワード
     *  "auth.realm" BASIC 認証レルム
     *  "auth.host"
     *  "header.${header.name}"
     * </pre>
     */
    public ApacheURLScraper(Scraper<InputStream, O> scraper, Properties props) {
        super(scraper, props);
    }

    /**
     * @throws IllegalStateException when an error occurs
     */
    @Override public O scrape(URL url) {
        try {
            HttpClientBuilder client = HttpClientBuilder.create();

            HttpGet get = new HttpGet(url.toString());
            applyAuthentication(client);
            applyCookies(client);
            applyRequestHeaders(get);
            HttpResponse status = client.build().execute(get);

            errorHandler.handle(status.getStatusLine().getStatusCode());

            retrieveResponseHeaders(get);

            O value = scraper.scrape(status.getEntity().getContent());

            get.releaseConnection();

            return value;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
