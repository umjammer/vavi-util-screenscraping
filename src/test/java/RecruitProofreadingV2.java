/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.io.Files;

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

    void test1(String[] args) throws Exception {
        String text = "薄茶色のシミがあちこちについた掛け布団。座ったら、五分でお尻が序くなってきそうだ。";
        List<Result> results = WebScraper.Util.scrape(Result.class, app.apiKey, text);
        for (Result result : results) {
            System.err.println(result);
        }
    }

    int line = 1;
    int count = 0;
    Pattern pattern = Pattern.compile("[。、「」]");

    void test2(String[] args) throws Exception {
        Path file = Paths.get(args[0]);
        Files.asCharSource(file.toFile(), Charset.forName("utf8")).forEachLine(l -> {
            try {
                if (pattern.matcher(l).find()) {
                    System.out.printf("B: %4d: %s\n", line, l);
                    Result result = WebScraper.Util.scrape(Result.class, app.apiKey, l).get(0);
                    System.out.printf("A: %4d: %s\n", line, result.checkedSentence);

                    count++;
//                    if (count > 3) {
//                        System.exit(0);
//                    }

                    Thread.sleep(3000);
                }

                line++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    static RecruitProofreadingV2 app;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        app = new RecruitProofreadingV2();
        PropsEntity.Util.bind(app);
        app.test1(args);
    }
}

/* */
