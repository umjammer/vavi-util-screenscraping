/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

    @WebScraper(url = "https://api.a3rt.recruit-tech.co.jp/proofreading/v2/typo?apikey={0}&sentence={1}&sensitivity={2}",
                parser = JsonPathParser.class,
                input = PlainInputHandler.class,
                isDebug = true,
                isCollection = false)
    public static class Result {
        @Target
        String resultID;
        @Target
        int status;
        @Target
        String message;
        @Target
        String inputSentence;
        @Target
        String normalizedSentence;
        @Target
        String checkedSentence;
        class Alert {
            int pos;
            String word;
            float score;
            List<String> suggestions;
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("Alert [pos=");
                sb.append(pos);
                sb.append(", word=");
                sb.append(word);
                sb.append(", score=");
                sb.append(score);
                sb.append(", suggestions=");
                sb.append(suggestions);
                sb.append("]");
                return sb.toString();
            }
        }
        // TODO should be eliminated
        public static class MyTypeToken extends com.google.gson.reflect.TypeToken<ArrayList<Alert>> { public MyTypeToken() { super(); }};
        @Target(option = MyTypeToken.class) /* should be "option = Alert.class" */
        List<Alert> alerts;
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
            sb.append(",");
            sb.append(alerts);
            return sb.toString();
        }
    }

    public void test1(String[] args) throws Exception {
        String text = "天秤棒を振り回しながら村への帰路に就いた。時には雑木林の中に足を踏み入れ、戯れに周囲の木立を厳に見立て、突く、打つ、払う。";
        Result result = WebScraper.Util.scrape(Result.class, apiKey, text, "low").get(0);
        System.err.println(result);
    }

    int line = 1;
    int count = 0;
    Pattern pattern = Pattern.compile("[。、「」]"); // TODO check

    void test2(String[] args) throws Exception {
        Path file = Paths.get(args[0]);
        Files.lines(file).map(l -> l.replaceAll("(｜|［＃.+?］|《.+?》)", "")).forEach(l -> {
            try {
                if (pattern.matcher(l).find()) {
                    System.out.printf("B: %4d: %s\n", line, l);
                    Result result = WebScraper.Util.scrape(Result.class, apiKey, l, "low").get(0);
                    System.out.printf("A: %4d: %s\n", line, result.checkedSentence);

                    count++;
                    if (count > 3) {
                        System.exit(0);
                    }

                    Thread.sleep(3000);
                }

                line++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        RecruitProofreadingV2 app = new RecruitProofreadingV2();
        PropsEntity.Util.bind(app);
        app.test1(args);
    }
}
