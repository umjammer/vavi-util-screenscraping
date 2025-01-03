/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.NoSuchElementException;

import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.FrameWindow;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;

import vavi.util.CharNormalizerJa;
import vavix.util.screenscrape.annotation.InputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;
import vavix.util.screenscrape.annotation.XPathParser;


/**
 * Jasrac.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/02 nsano initial version <br>
 */
public class Jasrac {

    static final WebClient client = new WebClient(BrowserVersion.FIREFOX_ESR);

    static {
        client.setJavaScriptEngine(null); // TODO
    }

    /** */
    public static class MyInput implements InputHandler<Reader> {
        private String cache;
        /**
         * @param args 0: artist, 1: title
         */
        @Override
        public Reader getInput(String ... args) throws IOException {
            if (cache != null) {
                return new StringReader(cache);
            }

            String artist = args[0];
//            String title = args[1];

            HtmlPage page0 = client.getPage("http://www2.jasrac.or.jp/eJwid/");
            HtmlInput button0 = page0.getForms().get(1).getInputByName("input");

            HtmlPage page1 = button0.click();

            FrameWindow frame2 = page1.getFrameByName("frame2");
            HtmlPage page2 = (HtmlPage) frame2.getEnclosedPage();

//System.err.println(page2.asXml());

            HtmlForm form1 = page2.getFormByName("Form");
//            HtmlInput inputT = form1.getInputByName("IN_WORKS_TITLE_NAME1");
//            inputT.setTextContent(title);
            HtmlInput inputA = form1.getInputByName("IN_ARTIST_NAME1");
            inputA.setValueAttribute(artist);
            HtmlSelect selectA = form1.getSelectByName("IN_ARTIST_NAME_OPTION1");
            selectA.setSelectedAttribute("3", true);
            HtmlInput button1 = form1.getInputByName("CMD_SEARCH");

            HtmlPage page3 = button1.click();
            StringBuilder sb = new StringBuilder(page3.asXml());

int p = 0;
            try {
                HtmlPage nextPage = page3;
                while (true) {
                    HtmlAnchor nextAnchor = nextAnchor(nextPage.getAnchors());
                    nextPage = nextAnchor.click();
                    sb.append(nextPage.asXml());
System.err.println("page: " + ++p);
                }
            } catch (NoSuchElementException e) {
System.err.println("last page: " + sb.length());
            }

            cache = sb.toString();

            return new StringReader(cache);
        }

        /** */
        static HtmlAnchor nextAnchor(List<HtmlAnchor> anchors) {
            for (HtmlAnchor anchor : anchors) {
                if (anchor.getAttribute("title").equals("次ページの結果を表示します")) {
                    return anchor;
                }
            }
            throw new NoSuchElementException();
        }
    }

    @WebScraper(input = MyInput.class,
                encoding = "MS932")
    public static class TitleUrl {
        @Target(value = "//TABLE//TR/TD[1]/DIV/text()")
        String artist;
        @Target(value = "//TABLE//TR/TD[4]/A/text()")
        String title;
        @Target(value = "//TABLE//TR/TD[4]/A/@href")
        String url;
        public String toString() {
            String sb = CharNormalizerJa.ToHalfAns2.normalize(artist) +
                    ", " +
                    CharNormalizerJa.ToHalfAns2.normalize(title);
//            sb.append(", ");
//            sb.append(url);
            return sb;
        }
    }

    /** */
    public static class MyInput2 implements InputHandler<Reader> {
        private String cache;
        /**
         * @param args 0: url
         */
        @Override
        public Reader getInput(String ... args) throws IOException {
            if (cache != null) {
                return new StringReader(cache);
            }

            String url = args[0];

            HtmlPage page = client.getPage("http://www2.jasrac.or.jp/eJwid/" + url);
//System.err.println(page.asXml());

            cache = page.asXml();

            return new StringReader(cache);
        }
    }

    @WebScraper(input = MyInput2.class,
                parser = XPathParser.class,
                encoding = "MS932")
    public static class Composer {
        @Target(value = "//TABLE[4]//TR/TD[2]/SPAN/text()")
        String name;
        @Target(value = "//TABLE[4]//TR/TD[3]/DIV/text()")
        String type;
        public String toString() {
            String sb = type +
                    ", " +
                    CharNormalizerJa.ToHalfAns2.normalize(name);
            return sb;
        }
    }

    static String getComposer(String url) throws IOException {
        List<Composer> cs = WebScraper.Util.scrape(Composer.class, url);
        StringBuilder lyrics_ = new StringBuilder();
        StringBuilder music_ = new StringBuilder();
        for (Composer composer : cs) {
//System.err.println(composer);
//System.err.println(composer.type + ", " + composer.type.indexOf("作詞") + ", " + composer.type.indexOf("作曲"));
            if ((composer.type.contains("作詞") || composer.type.contains("訳詞")) && !composer.name.contains("権利者")) {
                lyrics_.append(iTunes.normalizeComposer(CharNormalizerJa.ToHalfAns2.normalize(composer.name)));
                lyrics_.append(", ");
            }
            if ((composer.type.contains("作曲") || composer.type.contains("不明")) && !composer.name.contains("権利者")) {
                music_.append(iTunes.normalizeComposer(CharNormalizerJa.ToHalfAns2.normalize(composer.name)));
                music_.append(", ");
            }
        }
        if (lyrics_.length() > 1) {
            lyrics_.setLength(lyrics_.length() - 2);
        }
        if (music_.length() > 1) {
            music_.setLength(music_.length() - 2);
        }
        String lyrics = lyrics_.toString();
        String music = music_.toString();
        return lyrics.equals(music) || lyrics.isEmpty() ? music : music + " / " + lyrics;
    }

    /**
     * @param args 0: artist, 1: title
     */
    public static void main(String[] args) throws Exception {
        List<TitleUrl> urls = WebScraper.Util.scrape(TitleUrl.class, args);
        for (TitleUrl url : urls) {
            System.out.println(CharNormalizerJa.ToHalfAns2.normalize(url.artist) + "\t" + CharNormalizerJa.ToHalfAns2.normalize(url.title) + "\t" + getComposer(url.url));
        }
    }
}
