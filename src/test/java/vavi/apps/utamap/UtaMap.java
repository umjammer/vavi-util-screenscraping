/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.utamap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import vavi.util.Debug;
import vavix.util.screenscrape.Scraper;


/**
 * UtaMap Downloader.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060922 nsano initial version <br>
 */
@SuppressWarnings("deprecation")
class UtaMap {

    static String urlIdsRegex;
    static String utamapUrlFormat;
    static String userAgent;

    /** */
    static {
        try {
            Properties props = new Properties();
            props.load(UtaMap.class.getResourceAsStream("/vavi/apps/utamap/UtaMap.properties"));

            urlIdsRegex = props.getProperty("url.ids.regex");
            utamapUrlFormat = props.getProperty("utamap.url.format");
            userAgent = props.getProperty("useragent");
        } catch (Exception e) {
Debug.printStackTrace(e);
            System.exit(1);
        }
    }

    /** */
    static class UtaMapURLScraper implements Scraper<URL, File> {
        /** */
        private final HttpClient httpClient = HttpClients.createDefault();

        /**
         * <pre>
         * Yahoo!
         * "http://music.yahoo.co.jp/shop/p/53/27083/Y034871"
         * UtaMap
         * "http://music.yimg.jp/bin/sendlyricstext?ArtistId=248066&Id=Y013506"
         * </pre>
         * @param url UtaMap Yahoo! URL
         */
        @Override public File scrape(URL url) {
            try {
                String artistId;
                String lyricsId;

                // videoId
                Pattern pattern = Pattern.compile(urlIdsRegex);
                Matcher matcher = pattern.matcher(url.toString());
                if (matcher.find()) {
                    artistId = matcher.group(1);
                    lyricsId = matcher.group(2);
                } else {
                    throw new IllegalArgumentException("no suitable url");
                }

                // tag
                String utamapUrl = String.format(utamapUrlFormat, artistId, lyricsId);

System.err.println("utamap: " + utamapUrl);
                HttpGet get = new HttpGet(utamapUrl);
                get.addHeader("User-Agent", userAgent);
                HttpResponse status = httpClient.execute(get);
                if (status.getStatusLine().getStatusCode() != 200) {
                    throw new IllegalStateException("unexpected result getting 'utamap': " + status);
                }

                //
                String result = EntityUtils.toString(status.getEntity(), "UTF-8").substring(7);
System.err.println("downloading... size: " + result);

                get.releaseConnection();

                File file = File.createTempFile("utamap", ".html");
                FileOutputStream out = new FileOutputStream(file);
                out.write(result.getBytes());
                out.flush();
                out.close();

                return file;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * @param args 0: Yahoo! url for lyrics
     */
    public static void main(String[] args) throws Exception {
        String url = args[0];

        Scraper<URL, File> scraper = new UtaMapURLScraper();
        File file = scraper.scrape(new URL(url));
System.err.println("file:\n" + file);
    }
}
