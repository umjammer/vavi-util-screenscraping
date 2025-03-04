/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import vavi.net.rest.Rest;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import vavix.util.screenscrape.annotation.InputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * YahooJapanParse3.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 080319 nsano initial version <br>
 */
@SuppressWarnings("deprecation")
@PropsEntity(url = "file://${user.dir}/local.properties")
public class YahooJapanParse3 {

    @Property(name = "java.test.yahooJapan.apiKey.v1")
    String appid;

    /** */
    public static class MyInput implements InputHandler<Reader> {
        String cacheKey;
        byte[] cache;
        /**
         * @param args 1: sentence
         */
        @Override
        public Reader getInput(String ... args) throws IOException {

            String appid = args[0];
            String sentence = args[1];

            if (!sentence.equals(cacheKey)) {

System.err.println("appid: " + appid);
System.err.println("sentence: " + sentence);
                YahooJapanParse queryBean = new YahooJapanParse();
                queryBean.appid = appid;
                queryBean.sentence = sentence;

                cache = Rest.Util.getContent(queryBean);
                cacheKey = sentence;
            }

            return new StringReader(new String(cache, StandardCharsets.UTF_8));
        }
    }

    /** */
    @WebScraper(input = MyInput.class)
    public static class Data {
        @Target("//ResultSet/ma_result/word_list/word/surface/text()")
        String surface;
        @Target("//ResultSet/ma_result/word_list/word/reading/text()")
        String reading;
        @Target("//ResultSet/ma_result/word_list/word/pos/text()")
        String pos;
        public String toString() {
            String sb = "surface: " +
                    surface +
                    ", " +
                    "pos: " +
                    pos +
                    ", " +
                    "reading: " +
                    reading;
            return sb;
        }
    }

    /** */
    public static void main(String[] args) throws Exception {
        YahooJapanParse3 app = new YahooJapanParse3();
        PropsEntity.Util.bind(app);
        Scanner scanner = new Scanner(new File(args[0]));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            List<Data> data = WebScraper.Util.scrape(Data.class, app.appid, line);
            for (Data datum : data) {
                System.err.println(line + ": " + datum);
            }
            Thread.sleep(5000);
        }
        scanner.close();
    }
}
