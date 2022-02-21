/*
 * Copyright (c) 2015 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import vavix.util.screenscrape.annotation.InputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * EqTest2.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2015/11/06 umjammer initial version <br>
 */
public class EqTest2 {

    static WebClient client = new WebClient(BrowserVersion.FIREFOX_68);

    static {
        client.setJavaScriptEngine(null);
    }

    /** */
    public static class MyInput implements InputHandler<Reader> {
        private String cache;
        /**
         * @param args 0: number of answer pattern, 1: sex, 2: age
         */
        public Reader getInput(String ... args) throws IOException {
            if (cache != null) {
                return new StringReader(cache);
            }

            String no = args[0];
System.out.println("  \"ans\": " + no + ",");

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

                int selection = Integer.valueOf(String.valueOf(no.charAt(i)));
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

            int sex = Integer.valueOf(String.valueOf(args[1]));
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

            int age = Integer.valueOf(String.valueOf(args[2]));
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

        void sleep() {
            try {
                Thread.sleep(333);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
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
            StringBuilder sb = new StringBuilder();
            sb.append("\"result\": {");
            sb.append("\"score\": " + score);
            sb.append(", ");
            sb.append("\"desc\": \"" + desc + "\"");
            sb.append("}");
            return sb.toString();
        }
    }

    static String[] data = {
//        "10000000000000000000",
//        "20000000000000000000",
        "01000000000000000000",
//        "02000000000000000000",
//        "00100000000000000000",
//        "00200000000000000000",
//        "00010000000000000000",
//        "00020000000000000000",
//        "00001000000000000000",
//        "00002000000000000000",
//        "00000100000000000000",
//        "00000200000000000000",
//        "00000010000000000000",
//        "00000020000000000000",
//        "00000001000000000000",
//        "00000002000000000000",
//        "00000000100000000000",
//        "00000000200000000000",
//        "00000000010000000000",
//        "00000000020000000000",
//        "00000000001000000000",
//        "00000000002000000000",
//        "00000000000100000000",
//        "00000000000200000000",
//        "00000000000010000000",
//        "00000000000020000000",
//        "00000000000001000000",
//        "00000000000002000000",
//        "00000000000000100000",
//        "00000000000000200000",
//        "00000000000000010000",
//        "00000000000000020000",
//        "00000000000000001000",
//        "00000000000000002000",
//        "00000000000000000100",
//        "00000000000000000200",
//        "00000000000000000010",
//        "00000000000000000020",
//        "00000000000000000001",
//        "00000000000000000002",
    };

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        for (String datum : data) {
            System.out.println("{");
            Result result = WebScraper.Util.scrape(Result.class, datum, String.valueOf(0), String.valueOf(1)).get(0);
            System.out.println(result);
            System.out.println("},");
        }
    }
}

/* */
