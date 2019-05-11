/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.List;

import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import vavix.util.screenscrape.annotation.JsonPathParser;
import vavix.util.screenscrape.annotation.PlainInputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * RecruitProofreadingV2 Version 2.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2018/02/22 umjammer initial version <br>
 * @see "https://a3rt.recruit-tech.co.jp/product/proofreadingAPI/"
 */
@PropsEntity(url = "file://${user.dir}/local.properties")
public class RecruitProofreadingV2 {

    @Property(name = "java.test.recruit.proofreading.apiKey")
    String apiKey;

    @WebScraper(url = "https://api.a3rt.recruit-tech.co.jp/proofreading/v2/typo?apikey={0}&sentence={1}",
                parser = JsonPathParser.class,
                input = PlainInputHandler.class,
                isCollection = false)
    public static class Result {
        @Target
        String resultID;
        @Target
        String status;
        @Target
        String message;
        @Target
        String inputSentence;
        @Target
        String normalizedSentence;
        @Target
        String checkedSentence;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(resultID);
            sb.append(",");
            sb.append(status);
            sb.append(",");
            sb.append(message);
            sb.append(",");
            sb.append(inputSentence);
            sb.append(",");
            sb.append(normalizedSentence);
            sb.append(",");
            sb.append(checkedSentence);
            return sb.toString();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        RecruitProofreadingV2 app = new RecruitProofreadingV2();
        PropsEntity.Util.bind(app);
        String text = "薄茶色のシミがあちこちについた掛け布団。座ったら、五分でお尻が序くなってきそうだ。";
        List<Result> results = WebScraper.Util.scrape(Result.class, app.apiKey, text);
        for (Result result : results) {
            System.err.println(result);
        }
    }
}

/* */
