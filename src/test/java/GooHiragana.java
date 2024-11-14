/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.List;

import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import vavix.util.screenscrape.annotation.JsonPathParser;
import vavix.util.screenscrape.annotation.PostInputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * GooHiragana.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/03 umjammer initial version <br>
 * @see "https://dev.smt.docomo.ne.jp/?p=docs.api.page&api_name=language_analysis&p_name=api_4#tag01"
 */
@PropsEntity(url = "file://${user.dir}/local.properties")
public class GooHiragana {

    @Property(name = "docomo.goo.hiragana.apiKey")
    String apiKey;

    @WebScraper(
        url = "https://labs.goo.ne.jp/api/hiragana",
        isDebug = true,
        input = PostInputHandler.class,
        parser = JsonPathParser.class)
    public static class Result {
        @Target
        String request_id;
        @Target
        String output_type;
        @Target
        String converted;
        public String toString() {
            String sb = request_id +
                    "," +
                    output_type +
                    "," +
                    converted;
            return sb;
        }
        static final String BODY = "{\"app_id\":\"{0}\",\"request_id\":\"001\", \"sentence\":\"{1}\",\"output_type\":\"katakana\"}";
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        GooHiragana app = new GooHiragana();
        PropsEntity.Util.bind(app);
        String text = "薄茶色のシミがあちこちについた掛け布団。座ったら、五分でお尻が痒くなってきそうだ。";
        List<Result> results = WebScraper.Util.scrape(Result.class, Result.BODY, "application/json", app.apiKey, text);
        for (Result result : results) {
            System.err.println(result);
        }
    }
}
