/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.trader;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import vavix.util.screenscrape.ApacheURLScraper;


/**
 * ApacheURLHtmlData.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051014 nsano initial version <br>
 */
@SuppressWarnings("deprecation")
public class MonexURLScraper extends ApacheURLScraper<String> {

    /** */
    private HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

    /**
     * @param props use followings
     * <pre>
     *  "account" BASIC 認証アカウント名
     *  "password" BASIC 認証パスワード
     *  "realm" BASIC 認証レルム
     *  "host"
     * </pre>
     */
    public MonexURLScraper(Properties props) {
        super(null);
        String account = props.getProperty("account");
        String password = props.getProperty("password");
        String loginUrl = props.getProperty("login.url");

        // https://www.monex.co.jp/Login/00000000/login/ipan_web/exec
        // SJIS encoding
        try {
            BasicCookieStore cookieStore = new BasicCookieStore();
            httpClientBuilder.setDefaultCookieStore(cookieStore);

            HttpPost postMethod = new HttpPost(loginUrl);
            postMethod.addHeader("uid", "NULLGWDOCOMO");
            postMethod.addHeader("loginid", account);
            postMethod.addHeader("koza1", "");
            postMethod.addHeader("koza2", "");
            postMethod.addHeader("passwd", password);
            postMethod.addHeader("syokiGamen", "0");
            postMethod.addHeader("frameMode.x", "フレーム");
            postMethod.addHeader("submit", " ログイン ");
            HttpResponse status = httpClientBuilder.build().execute(postMethod);
            if (status.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IllegalStateException("unexpected result: " + status);
            }
            // Cookie:
            // MenuColor=0,80;
            // custom_num=11+22+0+49+31,clr;
            // loginmode=frm;
            // Apache=221.186.108.105.183131132897275897;
            // JSESSIONID=0000ocMrG07m8PhTlF2BMCqSsqj:-1;
            // nbsid=04019dbc4f3129054653;
            // s_cc=true;
            // s_sq=monexcojp%3D%2526pid%253Dhttps%25253A//www.monex.co.jp/Login/00000000/login/ipan_web/hyoji%2526oid%253D%252520%2525u30ED%2525u30B0%2525u30A4%2525u30F3%252520%2526oidt%253D3%2526ot%253DSUBMIT
            this.cookieStore = cookieStore;

            // /Etc/0000JK8D/member/M901/menu/frame/topmenu2.htm
            // HTML/BODY/TABLE[1]/TR[1]/TD[3]/TABLE/TR[2]/FROM/@onsubmit
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** */
    public String scrape(URL url) {
        try {
            HttpGet get = new HttpGet(url.toString());
            httpClientBuilder.setDefaultCookieStore(cookieStore);
            HttpResponse response = httpClientBuilder.build().execute(get);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IllegalStateException("unexpected result: " + response);
            }

            String value = scraper.scrape(response.getEntity().getContent());

            get.releaseConnection();

            return value;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
