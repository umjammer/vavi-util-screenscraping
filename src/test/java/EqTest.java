/*
 * Copyright (c) 2015 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.Disabled;

import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;

import vavi.math.TriDecimal;

import vavix.util.screenscrape.annotation.InputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * EqTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2015/11/06 umjammer initial version <br>
 */
@Disabled
public class EqTest {

    static final WebClient client = new WebClient(BrowserVersion.FIREFOX_ESR);

    static {
        client.setJavaScriptEngine(null);
    }

    /** */
    public static class MyInput implements InputHandler<Reader> {
        private String cache;
        /**
         * @param args 0: number of answer pattern, 1: sex, 2: age
         */
        @Override
        public Reader getInput(String ... args) throws IOException {
            if (cache != null) {
                return new StringReader(cache);
            }

            StringBuilder sb = new StringBuilder(new TriDecimal(Integer.parseInt(args[0])).toString());
            sb.insert(0, "0000000000000000000");
            String no = sb.reverse().toString();
System.out.println("  \"ans\": " + no.substring(0, 20) + ",");

            String url = "http://eqtest.biz/cate/index.html";
            HtmlPage page0 = client.getPage(url);

            // Q: 0, 1, 2
            for (int i = 0; i < 20; i++) {

                HtmlButton[] submitButtons = new HtmlButton[3];
                HtmlForm form1 = page0.getFormByName("form1");
                submitButtons[0] = (HtmlButton) page0.createElement("button");
                submitButtons[0].setAttribute("type", "submit");
                form1.appendChild(submitButtons[0]);
                HtmlForm form2 = page0.getFormByName("form2");
                submitButtons[1] = (HtmlButton) page0.createElement("button");
                submitButtons[1].setAttribute("type", "submit");
                form2.appendChild(submitButtons[1]);
                HtmlForm form3 = page0.getFormByName("form3");
                submitButtons[2] = (HtmlButton) page0.createElement("button");
                submitButtons[2].setAttribute("type", "submit");
                form3.appendChild(submitButtons[2]);

                int selection = Integer.parseInt(String.valueOf(no.charAt(i)));
                page0 = submitButtons[selection].click();

                sleep();
            }

            // sex: 0, 1
            HtmlButton[] submitButtons = new HtmlButton[3];
            HtmlForm form1 = page0.getFormByName("form1");
            submitButtons[0] = (HtmlButton) page0.createElement("button");
            submitButtons[0].setAttribute("type", "submit");
            form1.appendChild(submitButtons[0]);
            HtmlForm form2 = page0.getFormByName("form2");
            submitButtons[1] = (HtmlButton) page0.createElement("button");
            submitButtons[1].setAttribute("type", "submit");
            form2.appendChild(submitButtons[1]);

            int sex = Integer.parseInt(String.valueOf(args[1]));
System.out.println("  \"sex\": " + sex + ",");
            page0 = submitButtons[sex].click();
//System.err.println(page0.asXml());

            sleep();

            // age: 0 ~ 5
            submitButtons = new HtmlButton[6];
            for (int i = 0; i < 6; i++) {
                HtmlForm form = page0.getFormByName("form" + (i + 1));
                submitButtons[i] = (HtmlButton) page0.createElement("button");
                submitButtons[i].setAttribute("type", "submit");
                form.appendChild(submitButtons[i]);
            }

            int age = Integer.parseInt(String.valueOf(args[2]));
System.out.println("  \"age\": " + age + ",");
            page0 = submitButtons[sex].click();
//System.err.println(page0.asXml());

            sleep();

            // result
            List<HtmlAnchor> anchors = page0.getAnchors();
            HtmlAnchor resultAnchor = null;
            for (HtmlAnchor anchor : anchors) {
                if (anchor.getHrefAttribute().indexOf("ans") > 0) {
                    resultAnchor = anchor;
                    break;
                }
            }
System.out.println("  \"url\": \"" + resultAnchor.getHrefAttribute() + "\", ");

            page0 = resultAnchor.click();

            //
            cache = page0.asXml();
//System.err.println(cache);

            return new StringReader(cache);
        }

        static void sleep() {
            try { Thread.sleep(333); } catch (InterruptedException ignored) {}
        }
    }

    @WebScraper(input = MyInput.class,
                isCollection = false,
                encoding = "UTF-8")
    public static class Result {
        @Target(value = "//div[@class='ten']/span/text()")
        String score;
        @Target(value = "//div[@class='quin']")
        String desc;
        public String toString() {
            String sb = "\"result\": {" +
                    "\"score\": " + score +
                    ", " +
                    "\"desc\": \"" + desc + "\"" +
                    "}";
            return sb;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        for (int i = 663; i < Math.pow(3, 20); i++) {
            System.out.println("{");
            Result result = WebScraper.Util.scrape(Result.class, String.valueOf(i), String.valueOf(0), String.valueOf(1)).get(0);
            System.out.println(result);
            System.out.println("},");
        }
    }
}
