/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;


/**
 * Apache Commons HttpClient Base.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 071004 nsano initial version <br>
 */
@Deprecated
abstract class AbstractApacheHttpScraper<I, O> extends AbstractHttpScraper<I, O> implements Scraper<I, O> {
    /** */
    protected String realm;
    /** */
    protected String host;
    /** */
    protected Credentials credentials;

    /** */
    protected AbstractApacheHttpScraper(Scraper<InputStream, O> scraper) {
        super(scraper);
        responseHeaders = new HashMap<>();
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
    public AbstractApacheHttpScraper(Scraper<InputStream, O> scraper, Properties props) {
        this(scraper);
        // auth
        String account = props.getProperty("auth.account");
        String password = props.getProperty("auth.password");
        if (account != null && password != null) {
            credentials = new UsernamePasswordCredentials(account, password);
            realm = props.getProperty("auth.realm");
            host = props.getProperty("auth.host");
        }
        injectRequestHeaders(props);
        injectProxy(props);
    }

    /** */
    protected final ErrorHandler<Integer> errorHandler = status -> {
        if (status != 200) {
            throw new IllegalStateException("unexpected result: " + status);
        }
    };

    /** */
    protected void applyAuthentication(HttpClientBuilder clientBuilder) {
        if (credentials != null) {
            BasicCredentialsProvider provider = new BasicCredentialsProvider();
            AuthScope authScope = new AuthScope(new HttpHost(host));
            provider.setCredentials(authScope, credentials);
            clientBuilder.setDefaultCredentialsProvider(provider);
        }
    }

    /** */
    protected void applyRequestHeaders(HttpRequestBase method) {
        for (String name : requestHeaders.keySet()) {
            String value = requestHeaders.get(name);
            method.addHeader(name, value);
        }
    }

    /** */
    protected void retrieveResponseHeaders(HttpRequestBase method) {
        for (Header header : method.getAllHeaders()) {
            String name = header.getName();
            String value = header.getValue();
            if (responseHeaders.containsKey(name)) {
                List<String> values = responseHeaders.get(name);
                values.add(value);
            } else {
                List<String> values = new ArrayList<>();
                values.add(value);
                responseHeaders.put(name, values);
            }
        }
    }

    /** */
    protected void applyProxy(HttpClientBuilder httpClientBuilder) {
        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        httpClientBuilder.setRoutePlanner(routePlanner);
    }

    /** */
    protected CookieStore cookieStore;

    /** to application */
    public CookieStore getCookieStore() {
        return cookieStore;
    }

    /** from application */
    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    /** to HttpClient */
    protected void applyCookies(HttpClientBuilder httpClientBuilder) {
        httpClientBuilder.setDefaultCookieStore(cookieStore);
    }
}
