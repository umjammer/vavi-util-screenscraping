/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import vavi.net.http.HttpContext;
import vavi.util.Debug;


/**
 * Apache Commons HttpClient POST method.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 071004 nsano initial version <br>
 */
@Deprecated
public class ApacheHttpScraper<O> extends AbstractApacheHttpScraper<HttpContext, O> {

    /** */
    protected ApacheHttpScraper(Scraper<InputStream, O> scraper) {
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
    public ApacheHttpScraper(Scraper<InputStream, O> scraper, Properties props) {
        super(scraper, props);
    }

    /**
     * @throws IllegalStateException when an error occurs
     */
    @Override public O scrape(HttpContext request) {
        try {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

            String url = "http://" + request.getRemoteHost() + ":" + request.getRemotePort() + request.getRequestURI();
Debug.println("post url: " + url);
            HttpPost post;
            if (proxyHost != null) {
                post = new HttpPost();
                applyProxy(httpClientBuilder);
            } else {
                post = new HttpPost(url);
            }
            applyAuthentication(httpClientBuilder);
            applyCookies(httpClientBuilder);
            applyRequestHeaders(post);
            applyRequestParameters(post, request);
            post.setProtocolVersion(new ProtocolVersion("HTTP", 1, 1));
            HttpResponse status = httpClientBuilder.build().execute(post);

            errorHandler.handle(status.getStatusLine().getStatusCode());

            retrieveResponseHeaders(post);

            O value = scraper.scrape(status.getEntity().getContent());

            post.releaseConnection();

            return value;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** */
    private void applyRequestParameters(HttpPost method, HttpContext request) {
        for (String name : request.getParameters().keySet()) {
            for (String value : request.getParameters().get(name)) {;
Debug.println("post param: " + name + ": " + value);
                method.addHeader(name, value);
            }
        }
    }
}
