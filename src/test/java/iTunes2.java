/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import vavi.util.CharNormalizerJa;
import vavi.util.LevenshteinDistance;

import vavix.util.screenscrape.annotation.HtmlXPathParser;
import vavix.util.screenscrape.annotation.InputHandler;
import vavix.util.screenscrape.annotation.SaxonXPathParser;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * iTunes (Selenium version).
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2018/02/27 nsano initial version <br>
 */
public class iTunes2 {

    /** iTunes ライブラリ一曲 */
    @WebScraper(url = "file:///Users/nsano/Music/iTunes/iTunes%20Music%20Library.xml",
                parser = SaxonXPathParser.class,
                value = "/plist/dict/dict/dict")
    public static class Title {
        @Target("/dict/key[text()='Artist']/following-sibling::string[1]/text()")
        String artist;
        @Target("/dict/key[text()='Name']/following-sibling::string[1]/text()")
        String name;
        @Target("/dict/key[text()='Composer']/following-sibling::string[1]/text()")
        String composer;
        @Target("/dict/key[text()='Album']/following-sibling::string[1]/text()")
        String album;
        @Target("/dict/key[text()='Album Artist']/following-sibling::string[1]/text()")
        String albumArtist;
        @Target("/dict/key[text()='Location']/following-sibling::string[1]/text()")
        String location;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(artist);
            sb.append("\t");
            sb.append(name);
            sb.append("\t");
            sb.append(composer);
            return sb.toString();
        }
    }

    WebDriver driver;

    {
        String pwd = System.getProperty("user.dir");
        System.setProperty("webdriver.chrome.driver", pwd + "/bin/chromedriver");

        ChromeOptions chromeOptions = new ChromeOptions();
        String app = System.getProperty("com.google.chrome.app");
        chromeOptions.setBinary(app);
        chromeOptions.addArguments("--headless"/*, "--disable-gpu"*/);

        driver = new ChromeDriver(chromeOptions);
    }

    static class SeleniumUtil {
        static void waitFor(WebDriver driver) {
            new WebDriverWait(driver, 10).until(
                d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        }

        static void setAttribute(WebDriver driver, WebElement element, String name, String value) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", element, name, value);
        }
    }

    /** アーティスト、作品名検索 */
    public static class MyInput implements InputHandler<Reader> {
        /**
         * @param args 0: artist, 1: title
         */
        public Reader getInput(String ... args) throws IOException {
try {
            String artist = args[0].toUpperCase();
            String title = args[1].toUpperCase();
//System.err.println("ARGS: " + artist + ", " + title);
            app.driver.navigate().to("http://www2.jasrac.or.jp/eJwid/");
            WebElement button0 = app.driver.findElements(By.tagName("form")).get(1).findElement(By.name("input"));

            button0.click();
            SeleniumUtil.waitFor(app.driver);

            app.driver.switchTo().frame("frame2");
            SeleniumUtil.waitFor(app.driver);

            WebElement inputT = app.driver.findElement(By.name("IN_WORKS_TITLE_NAME1"));
            SeleniumUtil.setAttribute(app.driver, inputT, "value", title);
            // 0:前方一致, 1:後方一致, 2:中間一致 3:完全一致
//            Select selectT = new Select(app.driver.getSelectByName("IN_WORKS_TITLE_OPTION1"));
//            selectT.selectByValue("3");
            WebElement inputA = app.driver.findElement(By.name("IN_ARTIST_NAME1"));
            SeleniumUtil.setAttribute(app.driver, inputA, "value", artist);
            // 0:前方一致, 1:後方一致, 2:中間一致 3:完全一致
            Select selectA = new Select(app.driver.findElement(By.name("IN_ARTIST_NAME_OPTION1")));
            selectA.selectByValue("3");
            WebElement button1 = app.driver.findElement(By.name("CMD_SEARCH"));

            button1.click();
            SeleniumUtil.waitFor(app.driver);

//System.err.println("@@@: " + app.driver.getPageSource());
            return new StringReader(app.driver.getPageSource());
} catch (Exception e) {
System.err.println("@@@: " + app.driver.getPageSource());
    throw e;
}
        }
    }

    @WebScraper(input = MyInput.class,
                parser = HtmlXPathParser.class,
                encoding = "MS932")
    public static class TitleUrl {
        @Target(value = "//TABLE//TR/TD[2]/DIV/text()")
        String artist;
        @Target(value = "//TABLE//TR/TD[4]/A/text()")
        String title;
        @Target(value = "//TABLE//TR/TD[4]/A/@href")
        String url;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(CharNormalizerJa.ToHalfAns2.normalize(artist));
            sb.append(", ");
            sb.append(CharNormalizerJa.ToHalfAns2.normalize(title));
            return sb.toString();
        }
    }

    /** 作詞、作曲詳細 (単品) */
    public static class MyInput2 implements InputHandler<Reader> {
        /**
         * @param args 0: url
         */
        public Reader getInput(String ... args) throws IOException {
            String url = args[0];

            app.driver.navigate().to("http://www2.jasrac.or.jp/eJwid/" + url);
            SeleniumUtil.waitFor(app.driver);

            return new StringReader(app.driver.getPageSource());
        }
    }

    /** 作詞、作曲詳細 (一行) */
    @WebScraper(input = MyInput2.class,
                parser = HtmlXPathParser.class,
                encoding = "MS932")
    public static class Composer {
        @Target(value = "//TABLE[4]//TR/TD[2]/SPAN/text()")
        String name;
        @Target(value = "//TABLE[4]//TR/TD[3]/DIV/text()")
        String type;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(type);
            sb.append(", ");
            sb.append(CharNormalizerJa.ToHalfAns2.normalize(name));
            return sb.toString();
        }
    }

    /** 作品名で検索 */
    public static class MyInput3 implements InputHandler<Reader> {
        /**
         * @param args 0: title
         */
        public Reader getInput(String ... args) throws IOException {
            String title = args[0];
//System.err.println("ARGS: " + artist + ", " + title);
            app.driver.navigate().to("http://www2.jasrac.or.jp/eJwid/");
            WebElement button0 = app.driver.findElements(By.tagName("form")).get(1).findElement(By.name("input"));

            button0.click();
            SeleniumUtil.waitFor(app.driver);

            app.driver.switchTo().frame("frame2");
            SeleniumUtil.waitFor(app.driver);

            WebElement inputT = app.driver.findElement(By.name("IN_WORKS_TITLE_NAME1"));
            SeleniumUtil.setAttribute(app.driver, inputT, "value", title);
            Select selectT = new Select(app.driver.findElement(By.name("IN_WORKS_TITLE_OPTION1")));
            selectT.selectByValue("3");
            WebElement button1 = app.driver.findElement(By.name("CMD_SEARCH"));

            button1.click();
            SeleniumUtil.waitFor(app.driver);

            StringBuffer sb = new StringBuffer(app.driver.getPageSource());

            try {
                while (true) {
                    WebElement nextAnchor = nextAnchor(app.driver.findElements(By.tagName("a")));
System.err.println("nextAnchor: " + nextAnchor);
                    nextAnchor.click();
                    sb.append(app.driver.getPageSource());
                }
            } catch (NoSuchElementException e) {
            }

//System.err.println(sb);
            return new StringReader(sb.toString());
        }

        /** */
        WebElement nextAnchor(List<WebElement> anchors) {
            for (WebElement anchor : anchors) {
                if (anchor.getAttribute("title").equals("次ページの結果を表示します")) {
                    return anchor;
                }
            }
            throw new NoSuchElementException();
        }
    }

    /** 作品名指定の作品 (複数) */
    @WebScraper(input = MyInput3.class,
                parser = HtmlXPathParser.class,
                encoding = "MS932")
    public static class TitleUrl3 {
        @Target(value = "//TABLE//TR/TD[5]/text()")
        String artist;
        @Target(value = "//TABLE//TR/TD[3]/A/text()")
        String title;
        @Target(value = "//TABLE//TR/TD[3]/A/@href")
        String url;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(CharNormalizerJa.ToHalfAns2.normalize(artist));
            sb.append(", ");
            sb.append(CharNormalizerJa.ToHalfAns2.normalize(title));
            return sb.toString();
        }
    }

    /** アーティストで検索 */
    public static class MyInput4 implements InputHandler<Reader> {
        /**
         * @param args 0: artist
         */
        public Reader getInput(String ... args) throws IOException {
            String artist = args[0];

            app.driver.navigate().to("http://www2.jasrac.or.jp/eJwid/");
            WebElement button0 = app.driver.findElements(By.tagName("form")).get(1).findElement(By.name("input"));

            button0.click();
            SeleniumUtil.waitFor(app.driver);

            app.driver.switchTo().frame("frame2");
            SeleniumUtil.waitFor(app.driver);

            WebElement inputA = app.driver.findElement(By.name("IN_ARTIST_NAME1"));
            SeleniumUtil.setAttribute(app.driver, inputA, "value", artist);
            Select selectA = new Select(app.driver.findElement(By.name("IN_ARTIST_NAME_OPTION1")));
            selectA.selectByValue("3");
            WebElement button1 = app.driver.findElement(By.name("CMD_SEARCH"));

            button1.click();
            SeleniumUtil.waitFor(app.driver);

            StringBuffer sb = new StringBuffer(app.driver.getPageSource());

            try {
                while (true) {
                    WebElement nextAnchor = nextAnchor(app.driver.findElements(By.tagName("a")));
                    nextAnchor.click();
                    sb.append(app.driver.getPageSource());
                }
            } catch (NoSuchElementException e) {
            }

            return new StringReader(sb.toString());
        }

        /** */
        WebElement nextAnchor(List<WebElement> anchors) {
            for (WebElement anchor : anchors) {
                if (anchor.getAttribute("title").equals("次ページの結果を表示します")) {
                    return anchor;
                }
            }
            throw new NoSuchElementException();
        }
    }

    /** アーティスト指定の作品 (複数) */
    @WebScraper(input = MyInput4.class,
                parser = HtmlXPathParser.class,
                encoding = "MS932")
    public static class TitleUrl4 {
        @Target(value = "//TABLE//TR/TD[1]/DIV/text()")
        String artist;
        @Target(value = "//TABLE//TR/TD[4]/A/text()")
        String title;
        @Target(value = "//TABLE//TR/TD[4]/A/@href")
        String url;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(CharNormalizerJa.ToHalfAns2.normalize(artist));
            sb.append(", ");
            sb.append(CharNormalizerJa.ToHalfAns2.normalize(title));
            return sb.toString();
        }
    }

    public static String capitalize(String s) {
        if (s.length() == 0) {
            return s;
        } else {
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }
    }

    static final Pattern normalizeComposerPattern = Pattern.compile("[\\p{Upper}\\d' _ー\\.\\(\\)-]+");

    /** TODO Mc-, O-, Dr, St, Van, De-, La-, III, II, Jr, Sr, DJ ... and (US1), (GB) ... */
    static String normalizeComposer(String name) {
        Matcher matcher = normalizeComposerPattern.matcher(name);
        if (!matcher.matches()) {
            return name; // 国内
        }
        name = name.replace("ー", "-");
        StringBuilder result = new StringBuilder();
        String[] ns = name.split("\\s");
        if (ns.length > 1) {
            for (int i = 1; i < ns.length; i++) {
                result.append(capitalize(ns[i]));
                result.append(" ");
            }
        }
        result.append(capitalize(ns[0]));
        return result.toString();
    }

    static String getComposer(String url) throws IOException {
        List<Composer> cs = WebScraper.Util.scrape(Composer.class, url);
        StringBuilder lyrics_ = new StringBuilder();
        StringBuilder music_ = new StringBuilder();
        for (Composer composer : cs) {
//System.err.println(composer);
//System.err.println(composer.type + ", " + composer.type.indexOf("作詞") + ", " + composer.type.indexOf("作曲"));
            if ((composer.type.indexOf("作詞") != -1 || composer.type.indexOf("訳詞") != -1) && composer.name.indexOf("権利者") == -1) {
                lyrics_.append(normalizeComposer(CharNormalizerJa.ToHalfAns2.normalize(composer.name)));
                lyrics_.append(", ");
            }
            if ((composer.type.indexOf("作曲") != -1 || composer.type.indexOf("不明") != -1) && composer.name.indexOf("権利者") == -1) {
                music_.append(normalizeComposer(CharNormalizerJa.ToHalfAns2.normalize(composer.name)));
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

    /** アーティスト名で近い順 */
    static class MyComparator3 implements Comparator<TitleUrl3> {
        String artist;
        MyComparator3(String artist) {
            this.artist = artist.toUpperCase();
        }
        public int compare(TitleUrl3 o1, TitleUrl3 o2) {
            int d1 = LevenshteinDistance.calculate(artist, CharNormalizerJa.ToHalfAns2.normalize(o1.artist)) - LevenshteinDistance.calculate(artist, CharNormalizerJa.ToHalfAns2.normalize(o2.artist));
            return d1;
        }
    }

    /** 作品名で近い順 */
    static class MyComparator4 implements Comparator<TitleUrl4> {
        String name;
        MyComparator4(String name) {
            this.name = name.toUpperCase();
        }
        public int compare(TitleUrl4 o1, TitleUrl4 o2) {
            int d1 = LevenshteinDistance.calculate(name, CharNormalizerJa.ToHalfAns2.normalize(o1.title)) - LevenshteinDistance.calculate(name, CharNormalizerJa.ToHalfAns2.normalize(o2.title));
            return d1;
        }
    }

    static iTunes2 app;

    /**
     * @param args 0: artist, 1: title
     */
    public static void main(String[] args) throws Exception {
        app = new iTunes2();
        app.processByWebScraper(args);
    }

    int errorCount = 0;

    static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    void processByWebScraper(String[] args) throws Exception {
        WebScraper.Util.foreach(Title.class, each -> {
            try {
                boolean r = false;
                if (each.location.startsWith("file:///Users/nsano/Music/iTunes/iTunes%20Music/") &&
                    !each.location.startsWith("file:///Users/nsano/Music/iTunes/iTunes%20Music/Podcasts/") &&
                    !each.location.endsWith(".pdf")
                    ) {
                    r = doEach(each);
                } else {
                    System.err.println("not music: " + each.location);
                }
                if (r) {
                    sleep();
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.err.println("error: " + each);
                errorCount++;
                if (errorCount > 2) {
System.err.println("too many errors: " + errorCount);
                    System.exit(1);
                }
            }
        }, args);
    }

    // TODO la, un, los
    static final Pattern normalizeArticlePattern = Pattern.compile("(An|A|The) (.*)");

    // TODO "...
    static final Pattern normalizeNamePattern = Pattern.compile("(.*)(feat.*|[~〜].+|[-ー].+|[\\/／].+|[\\(（].+)");

    /**
     * main
     */
    boolean doEach(Title each) throws IOException {
        // SPECIAL, exclude speed leaning
        if ("Speed Learning".equals(each.artist)) {
            return false;
        }

        if (!each.composer.isEmpty()) {
            return false;
        }
System.err.println(each);

        // 1. plain artist, name
        List<TitleUrl> urls = WebScraper.Util.scrape(TitleUrl.class, each.artist, each.name);
        if (urls.size() > 0) {
            System.out.println("RESULT\t" + each + getComposer(urls.get(0).url));
            return true;
        }

        // 2. re-scrape by album artist, name
        String normalizedArtist = each.artist;
        if (each.albumArtist != null && !each.albumArtist.isEmpty()) {
            normalizedArtist = each.albumArtist;
            sleep();
            List<TitleUrl> urls2 = WebScraper.Util.scrape(TitleUrl.class, normalizedArtist, each.name);
            if (urls2.size() > 0) {
                System.out.println("RESULTa\t" + each + getComposer(urls2.get(0).url));
                return true;
            }
        }

        // 3. re-scrape by album artist, normalized name (cut ~XXX, -XXX, feat. XXX)
        // TODO (...), & -> and, II -> 2
        String normalizedName = each.name;
        Matcher matcher = normalizeArticlePattern.matcher(each.name);
        if (matcher.matches()) {
            normalizedName = matcher.group(2);
        }
        matcher = normalizeNamePattern.matcher(normalizedName);
        if (matcher.matches()) {
            normalizedName = matcher.group(1);
        }
        sleep();
        List<TitleUrl> urls3 = WebScraper.Util.scrape(TitleUrl.class, normalizedArtist, normalizedName);
        if (urls3.size() > 0) {
            System.out.println("RESULTn\t" + each + getComposer(urls3.get(0).url));
            return true;
        }

        // 4. by artist only
        int ca = 0;
        sleep();
        List<TitleUrl4> url4s = WebScraper.Util.scrape(TitleUrl4.class, normalizedArtist);
        if (url4s.size() > 0) {
            Collections.sort(url4s, new MyComparator4(normalizedName));
            for (TitleUrl4 url4 : url4s) {
                if (ca == 0 && normalizedName.equalsIgnoreCase(CharNormalizerJa.ToHalfAns2.normalize(url4.title))) {
                    System.out.println("RESULTp\t" + each + getComposer(url4.url));
                    return true;
                }
                System.out.println("MAYBEa" + ca + "\t" + each + "(" + getComposer(url4.url) + ")" + "\t[" + CharNormalizerJa.ToHalfAns2.normalize(url4.artist) + ", " + CharNormalizerJa.ToHalfAns2.normalize(url4.title) + "]");
                ca++;
                if (ca > 2) {
                    break;
                }
            }
        }

        // 5. by name only
        sleep();
        List<TitleUrl3> url3s = WebScraper.Util.scrape(TitleUrl3.class, normalizedName);
        int cn = 0;
        if (url3s.size() > 0) {
            matcher = normalizeArticlePattern.matcher(normalizedArtist);
            if (matcher.matches()) {
                normalizedArtist = matcher.group(2);
            }
            Collections.sort(url3s, new MyComparator3(normalizedArtist));
            for (TitleUrl3 url3 : url3s) {
                System.out.println("MAYBEn" + cn + "\t" + each + "(" + getComposer(url3.url) + ")" + "\t[" + CharNormalizerJa.ToHalfAns2.normalize(url3.artist) + ", " + CharNormalizerJa.ToHalfAns2.normalize(url3.title) + "]");
                cn++;
                if (cn > 2) {
                    break;
                }
            }
            return true;
        }

        // at last
        if (ca == 0) {
            System.out.println("NONE\t" + each);
        }

        return true;
    }
}

/* */
