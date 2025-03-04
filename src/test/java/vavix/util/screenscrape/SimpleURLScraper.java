/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;
import java.util.Properties;


/**
 * Java SE HttpUrlConnection GET method.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051014 nsano initial version <br>
 */
@Deprecated
public class SimpleURLScraper<O> extends AbstractHttpScraper<URL, O> {

    /** */
    public SimpleURLScraper(Scraper<InputStream, O> scraper) {
        super(scraper);
    }

    /** */
    protected final ErrorHandler<HttpURLConnection> errorHandler = connection -> {
        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            throw new IllegalStateException("unexpected result: " + status);
        }
    };

    /**
     * @param props use followings
     * <pre>
     *  "auth.account"
     *  "auth.password"
     *  "header.${header.name}"
     * </pre>
     */
    public SimpleURLScraper(Scraper<InputStream, O> scraper, Properties props) {
        this(scraper);
        String account = props.getProperty("auth.account");
        String password = props.getProperty("auth.password");
        if (account != null && password != null) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication(account, password.toCharArray());
                }
            });
        }
        injectRequestHeaders(props);
        injectProxy(props);
    }

    /**
     * @throws IllegalStateException when an error occurs
     */
    @Override
    public O scrape(URL url) {
        try {
            applyProxy();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            applyRequestHeaders(connection);

            errorHandler.handle(connection);

            applyResponseHeaders(connection);

            O value = scraper.scrape(connection.getInputStream());

            connection.disconnect();

            return value;
        } catch (IOException e) {
e.printStackTrace(System.err);
            throw new IllegalStateException(e);
        }
    }

    /** */
    private void applyRequestHeaders(HttpURLConnection connection) {
        for (String name : requestHeaders.keySet()) {
            String value = requestHeaders.get(name);
//Debug.println("header: " + name + " = " + value);
            connection.setRequestProperty(name, value);
        }
    }

    /** */
    private void applyResponseHeaders(HttpURLConnection connection) {
        responseHeaders = connection.getHeaderFields();
    }

    /** */
    protected void applyProxy() {
        if (proxyHost != null) {
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", String.valueOf(proxyPort));
        }
    }

    /** */
    public String getCookie() {
        List<String> values = responseHeaders.get("Set-Cookie");

        StringBuilder cookieValue = null;
        for (String value : values) {
             if (cookieValue == null) {
                 cookieValue = new StringBuilder(value);
             } else {
                 cookieValue.append(";").append(value);
             }
        }
//Debug.println("cookie: " + cookieValue);
        return cookieValue.toString();
    }
}
