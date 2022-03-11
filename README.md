[![Maven Package](https://github.com/umjammer/vavi-util-screenscraping/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-util-screenscraping/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/umjammer/vavi-util-screenscraping/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-util-screenscraping/actions/workflows/maven-publish.yml)
[![CodeQL](https://github.com/umjammer/vavi-util-screenscraping/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-util-screenscraping/actions/workflows/codeql-analysis.yml)

# Screen Scraping Library for Java

🌏 Scrape the world!

## Introduction

This library screen-scrapes data from html and injects data into POJO using annotation.

```java
    @WebScraper(url = "http://foo.com/bar.html")
    public class Buz {
        @Target(value = "//TABLE//TR/TD[2]/DIV/text()")
        String artist;
        @Target(value = "//TABLE//TR/TD[4]/A/text()")
        String title;
        @Target(value = "//TABLE//TR/TD[4]/A/@href")
        String url;
    }
    
    :
    
    List<Buz> buzs = WebScraper.Util.scrape(Buz.class);
```

## Details

 * `InputHandler` ... apply any processing before parsing

 * `Parser`

    * `XPathParser` ... default
    * `HtmlXPathParser` ... for original purpose
    * `SaxonXPathParser` ... for huge xml file
    * `JsonPathParser` ... for json return

 * `Parser#foreach()` ... like java collection stream

## Sample

 * [Scraping composers from JASRAC database for iTuens](https://github.com/umjammer/vavi-util-screenscraping/wiki)
 * [Scraping json for deep learning proof reading](https://github.com/umjammer/umjammer/blob/wiki/DeepLearningProofReading.md)
 * [Amazon purchase history](https://github.com/umjammer/vavi-util-screenscraping/blob/master/src/test/java/AmazonPurchaseHistory.java)
 * [Amazon yourstore collection](https://github.com/umjammer/vavi-util-screenscraping/blob/master/src/test/java/Amazon.java)
 * [Google suggest](https://github.com/umjammer/vavi-util-screenscraping/blob/master/src/test/java/GoogleSuggest.java)
 * [Yahoo! Japan proof reading](https://github.com/umjammer/vavi-util-screenscraping/blob/master/src/test/java/YahooJapanKouseiV1.java)

## TODO

 * ~~Tidy version~~
 * ~~deleted garbled text~~
 * InputHandler w/o cache
 * ~~argument injection into WebScraper#url~~
    ```
        @WebScraper(url = "http://foo.com?bar={bar}")
        public static class Result {
            :

        List<Result> data = WebScraper.Util.scrape(Result.class, @UrlParam(bar) args[0]);
    ```
 * ~~json parser~~
 * css selector
 * https://github.com/jhy/jsoup
