/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.List;

import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * YahooJapanKousei Version 2.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2018/02/24 umjammer initial version <br>
 * @see "https://developer.yahoo.co.jp/webapi/jlp/kousei/v1/kousei.html"
 */
@PropsEntity(url = "file://${user.dir}/local.properties")
public class YahooJapanKouseiV1 {

    @Property(name = "java.test.yahooJapan.apiKey")
    String apiKey;

    @WebScraper(url = "https://jlp.yahooapis.jp/KouseiService/V1/kousei?appid={0}&sentence={1}") // &no_filter={2}
    public static class Result {
        @Target(value = "/ResultSet/Result/StartPos")
        String startPos;
        @Target(value = "/ResultSet/Result/Length")
        String length;
        @Target(value = "/ResultSet/Result/Surface")
        String surface;
        @Target(value = "/ResultSet/Result/ShitekiWord")
        String shitekiWord;
        @Target(value = "/ResultSet/Result/ShitekiInfo")
        String shitekiInfo;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(startPos);
            sb.append(",");
            sb.append(length);
            sb.append(",");
            sb.append(surface);
            sb.append(",");
            sb.append(shitekiWord);
            sb.append(",");
            sb.append(shitekiInfo);
            return sb.toString();
        }
    }

    static final String noFilter = "2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17";

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        YahooJapanKouseiV1 app = new YahooJapanKouseiV1();
        PropsEntity.Util.bind(app);
        String text = "薄茶色のシミがあちこちについた掛け布団。座ったら、五分でお尻が序くなってきそうだ。";
        List<Result> results = WebScraper.Util.scrape(Result.class, app.apiKey, text, noFilter);
        for (Result result : results) {
            System.err.println(result);
        }
    }
}
