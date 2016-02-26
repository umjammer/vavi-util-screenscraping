/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

import vavi.net.rest.Parameter;
import vavi.net.rest.Rest;

import vavix.util.screenscrape.annotation.InputHandler;
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

    @Rest(url = "http://www.google.com/complete/search", method = "GET")
    public static class Query {
        /** query word */
        @Parameter(required = true)
        String q;
        /** language */
        @Parameter(required = true)
        String hl = "ja";
        /** result as xml */
        @Parameter(required = true)
        String output = "toolbar";
        /** encoding for input */
        @Parameter
        String ie = "utf-8";
        /** encoding for output */
        @Parameter
        String oe = "utf-8";
    }

    /** */
    public static class MyInput implements InputHandler<Reader> {
        String cacheKey; // word cache
        byte[] cache; // result cache
        /** @param args 0: key */
        public Reader getInput(String ... args) throws IOException {
            String key = args[0];
            if (!key.equals(cacheKey)) {
                Query queryBean = new Query();
                queryBean.q = key;
                cache = Rest.Util.getContent(queryBean);
                cacheKey = key;
            }
            return new StringReader(new String(cache, Charset.forName("UTF8").name()));
        }
    }

    /** query result as xml */
    @WebScraper(input = MyInput.class)
    public static class Result {
        @Target("//toplevel/CompleteSuggestion/suggestion/@data")
        String data;
        public String toString() {
            return "data: " + data;
        }
    }

    /** @args 0: word */
    public static void main(String[] args) throws Exception {
        List<Result> data = WebScraper.Util.scrape(Result.class, args[0]);
        data.forEach(System.err::println);
    }
}

/* */
