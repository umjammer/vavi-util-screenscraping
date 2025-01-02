/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.youtube;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import vavi.util.Debug;
import vavix.util.screenscrape.Scraper;
import vavix.util.screenscrape.StringSimpleXPathScraper;


/**
 * YouTube Downloader.
 *
 * @author Takashi Ohida (pichon@gmail.com)
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060705 nsano initial version <br>
 */
@SuppressWarnings("deprecation")
class YouTube {

    static final String videoXpath;
    static final String videoTagRregex;
    static final String urlVideoIdRegex;
    static final String watchUrlFormat;
    static final String getUrlFormat;

    /* */
    static {
        try {
            Properties props = new Properties();
            props.load(YouTube.class.getResourceAsStream("/vavi/apps/youtube/YouTube.properties"));

            videoXpath = props.getProperty("video.xpath");
            videoTagRregex = props.getProperty("video.tag.regex");
            urlVideoIdRegex = props.getProperty("url.videoId.regex");
            watchUrlFormat = props.getProperty("watch.url.format");
            getUrlFormat = props.getProperty("get.url.format");
        } catch (Exception e) {
Debug.printStackTrace(e);
            throw new IllegalStateException(e);
        }
    }

    /** TODO  */
    static class YouTubeURLScraper implements Scraper<URL, File> {
        /** */
        private final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        /**
         * xpath <code>"//DIV[@ID='interactDiv']/SCRIPT"</code>
         * tag
         */
        final StringSimpleXPathScraper myStreamXPathScraper = new StringSimpleXPathScraper(videoXpath) {
            final Pattern pattern = Pattern.compile(videoTagRregex);
            @Override public String scrape(InputStream source) {
                String tag;
                String script = super.scrape(source);
                Matcher matcher = pattern.matcher(script);
                if (matcher.find()) {
                    tag = matcher.group(1);
                } else {
                    throw new IllegalArgumentException("no tag found");
                }
                return tag;
            }
        };

        /**
         * @param url YouTube  URL
         */
        @Override public File scrape(URL url) {
            try {
                return pattern2(url);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        /** */
        File pattern1(URL url) throws IOException {
            String videoId;

            // videoId
            Pattern pattern = Pattern.compile(urlVideoIdRegex);
            Matcher matcher = pattern.matcher(url.toString());
            if (matcher.find()) {
                videoId = matcher.group(1);
            } else {
                throw new IllegalArgumentException("no video id found");
            }

            // tag
            String watchUrl = String.format(watchUrlFormat, videoId);
System.err.println("watch: " + watchUrl);
            HttpGet get = new HttpGet(watchUrl);
            BasicCookieStore cookieStore = new BasicCookieStore();
            BasicClientCookie cookie = new BasicClientCookie("VISITOR_INFO1_LIVE", "H5LU-y_SA9w");
            cookie.setDomain(".youtube.com");
            cookie.setAttribute("LOGIN_INFO", "9bb3bfa8da28d9518e35b22193026217e3QgAAAAbV91c2VyX2lkX0FORF9zZXNzaW9uX251bWJlcl9tZDVzIAAAADRmNDJmZmU3MTYyNzg0N2UzZTRkZjQxNzcyOWQ4Yjc1dAkAAABtX3VzZXJfaWRsAgAAAKEK9QIw");
            cookie.setAttribute("is_adult", "8d3a778dcb047f7c9a6ab4917e55b74adAEAAAAx");
            cookie.setAttribute("watched_video_id_list_vavivavi", "17bc3ed9329253d96fa33e53eea4c750WwEAAABzCwAAAGxHenVtMU5zdDJj");
            cookie.setPath("/");
            cookieStore.addCookie(cookie);
            httpClientBuilder.setDefaultCookieStore(cookieStore);
            HttpResponse response = httpClientBuilder.build().execute(get);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IllegalStateException("unexpected result when 'watch': " + response);
            }

            String tag = myStreamXPathScraper.scrape(response.getEntity().getContent());

            get.releaseConnection();

            // flv
            String getUrl = String.format(getUrlFormat, videoId, tag);
System.err.println("getUrl: " + getUrl);
            get = new HttpGet(getUrl);
            response = httpClientBuilder.build().execute(get);
            if (response.getStatusLine().getStatusCode() == 303) {
                //
                getUrl = response.getHeaders("Location")[0].getValue();
System.err.println("redirectUrl: " + getUrl);
                get = new HttpGet(getUrl);
                response = httpClientBuilder.build().execute(get);
            }
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IllegalStateException("unexpected result when 'get': " + response);
            } else {
                for (Header header : response.getAllHeaders()) {
System.err.println(header.getName() + "=" + header.getValue());
                }
            }

            // flv
            InputStream is = response.getEntity().getContent();
            int length = Integer.parseInt(response.getHeaders("Content-Length")[0].getValue());
            ReadableByteChannel inputChannel = Channels.newChannel(is);

            File file = File.createTempFile("youtube", ".flv");
            FileOutputStream out = new FileOutputStream(file);
            FileChannel outputChannel = out.getChannel();
System.err.println("downloading... size: " + length);
            outputChannel.transferFrom(inputChannel, 0, length);
            out.close();

            get.releaseConnection();

            return file;
        }

        /** 2007/8/17 ~ */
        File pattern2(URL url) throws IOException {
            String videoId;

            // videoId
            Pattern pattern = Pattern.compile(urlVideoIdRegex);
            Matcher matcher = pattern.matcher(url.toString());
            if (matcher.find()) {
                videoId = matcher.group(1);
            } else {
                throw new IllegalArgumentException("no video id found");
            }

            // flv
            String getUrl = String.format(getUrlFormat, videoId);
System.err.println("getUrl: " + getUrl);
            HttpGet get = new HttpGet(getUrl);
            HttpResponse response = httpClientBuilder.build().execute(get);
            while (response.getStatusLine().getStatusCode() == 302) {
System.err.println("status2: " + response);
                //
                getUrl = response.getHeaders("Location")[0].getValue();
System.err.println("redirectUrl: " + getUrl);
                get = new HttpGet(getUrl);
                response = httpClientBuilder.build().execute(get);
            }
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IllegalStateException("unexpected result when 'get': " + response);
            } else {
                for (Header header : response.getAllHeaders()) {
System.err.println(header.getName() + "=" + header.getValue());
                }
            }

            // flv
            InputStream is = response.getEntity().getContent();
            int length = Integer.parseInt(response.getHeaders("Content-Length")[0].getValue());
            ReadableByteChannel inputChannel = Channels.newChannel(is);

            File file = File.createTempFile("youtube", ".flv");
            FileOutputStream out = new FileOutputStream(file);
            FileChannel outputChannel = out.getChannel();
System.err.println("downloading... size: " + length);
            outputChannel.transferFrom(inputChannel, 0, length);
            out.close();

            get.releaseConnection();

            return file;
        }
    }

    /** */
    public static void main(String[] args) throws Exception {
        String url = args[0];

        Scraper<URL, File> scraper = new YouTubeURLScraper();
        File file = scraper.scrape(new URL(url));
System.err.println("file:\n" + file);
    }
}
