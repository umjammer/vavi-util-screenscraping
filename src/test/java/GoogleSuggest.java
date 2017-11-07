/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.List;

import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * GoogleSuggest.
 *
 * @see "http://so-zou.jp/web-app/tech/web-api/google/suggest/"
 * @see "http://blog.shuffleee.com/1352/"
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/24 umjammer initial version <br>
 */
public class GoogleSuggest {

    /** query result as xml */
    @WebScraper(url = "http://www.google.com/complete/search?q={0}&hl={1}&output={2}&ie={3}&oe={3}")
    public static class Result {
        @Target("//toplevel/CompleteSuggestion/suggestion/@data")
        String data;
        public String toString() {
            return "data: " + data;
        }
    }

    /** @args 0: word */
    public static void main(String[] args) throws Exception {
        List<Result> data = WebScraper.Util.scrape(Result.class, args[0], "ja", "toolbar", "utf-8");
        data.forEach(System.err::println);
    }
}

/* */
