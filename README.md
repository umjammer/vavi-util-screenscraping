[![Release](https://jitpack.io/v/umjammer/vavi-util-screenscraping.svg)](https://jitpack.io/#umjammer/vavi-util-screenscraping)

# Screen Scraping Library for Java

## Introduction

アノテーションを使って Screen Scraping を行い POJO に設定できます。

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

 * InputHandler
 * Parser
  * SaxonParser
 * EachHandler
