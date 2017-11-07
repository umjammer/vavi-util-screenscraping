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

## Annotation -> inject -> POJO

 一般化できそう

 * [javax.persistence](http://ja.wikipedia.org/wiki/Java_Persistence_API)
 * [CLI](https://github.com/umjammer/klab-commons-cli/)
 * [CSV](https://github.com/umjammer/klab-commons-csv/)
 * [Properties](http://code.google.com/p/vavi-commons/blob/master/src/src/main/java/vavi/util/properties/annotation)

 * [Rest]
 * [WebQuery]

 * [UPnP](https://github.com/umjammer/cyberlink4java2/)
 
## 紆余曲折

何かを簡単にしたいと思った場合ボトムアップな設計は良くないかもしれないと思った。

まず何も考えずにスクレイピングするプログラムを書いて、リファクタリングをし続けた結果

https://github.com/umjammer/vavi-util-screenscraping/blob/master/src/test/java/vavix/util/screenscrape/Scraper.java

```java
public interface Scraper<I, O> {
    O scrape(I source);
}
```

一番最後にできたインターフェースがこれ。できた時は「おぉ、やはり考えを突き詰めれば本当にシンプルなものに突き当たるんだ」と一瞬思ったんだが、よく見りゃ"入力を加工して出力する"という"プログラム"の定義を Java言語で書き直してるだけやんか！アホか...orz

恥ずかしいけど自戒のためにリポジトリに残しておく。

トップダウン的に

 * URLを指定して
 * その中身は XPath で指定して
 * POJO のリストでほしい

で、それ以外は何もしたくないと決め打ちすればおのずと上記の仕様が出てきた。
