# vavi.util.screenscrap

Screen Scraping 関連のクラスを提供します．

## Screen Scraping 決定版か！？

### POJO クラス作ってクラスに URL、フィールドに XPath を指定するだけでスクレイピングできるよ！

```java
    @WebScraper(url = "http://foo.com/bar.html")
    public class Buz {
        <span class="strong">@Target(value = "//TABLE//TR/TD[2]/DIV/text()")</span>
        String artist;
        <span class="strong">@Target(value = "//TABLE//TR/TD[4]/A/text()")</span>
        String title;
        <span class="strong">@Target(value = "//TABLE//TR/TD[4]/A/@href")</span>
        String url;
    }

    :

    List&lt;Buz&gt; buzs = <span class="strong">WebScraper.Util.scrape(Buz.class);</span>
```

### 大きなファイルでも平気だよ！

 * `SaxonXPathParser`

### 特殊な入力でも大丈夫だよ！

 * `InputHandler`

### メモリを節約できるよ！

 * `Parser#foreach()`

## TODO

 * 入力が htmlunit とか面倒なので、やっぱりスクリプト？
