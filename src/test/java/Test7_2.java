/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.File;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Disabled;

import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * YahooJapanParse.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 080319 nsano initial version <br>
 */
@Disabled
@PropsEntity(url = "file://${user.dir}/local.properties")
public class Test7_2 {

    @Property(name = "java.test.yahooJapan.apiKey")
    String appid;

    /** */
    @WebScraper(url = "https://jlp.yahooapis.jp/MAService/V1/parse?appid={0}&results=ma&sentence={1}")
    public static class Data {
        @Target("//ResultSet/ma_result/word_list/word/surface/text()")
        String surface;
        @Target("//ResultSet/ma_result/word_list/word/reading/text()")
        String reading;
        @Target("//ResultSet/ma_result/word_list/word/pos/text()")
        String pos;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("surface: ");
            sb.append(surface);
            sb.append(", ");
            sb.append("pos: ");
            sb.append(pos);
            sb.append(", ");
            sb.append("reading: ");
            sb.append(reading);
            return sb.toString();
        }
    }

    /** */
    public static void main(String[] args) throws Exception {
        Test7_2 app = new Test7_2();
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

/* */
