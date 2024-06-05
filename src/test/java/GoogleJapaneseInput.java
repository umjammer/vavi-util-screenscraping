/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * GoogleJapaneseInput.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/03 umjammer initial version <br>
 * @see "https://www.google.co.jp/ime/cgiapi.html"
 */
public class GoogleJapaneseInput {

    /** */
    Gson gson = new GsonBuilder().create();

    /** */
    void test1(String[] args) throws Exception {
        String text = "こんにちは、きょうもげんきです。";
        String url = String.format("https://www.google.com/transliterate?langpair=%s&text=%s", URLEncoder.encode("ja-Hira|ja", "utf-8"), URLEncoder.encode(text, "utf-8"));
System.err.println(url);
        List<?> list = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(URI.create(url).toURL().openStream()))) {
            list = gson.fromJson(reader, List.class);
        }
        list.forEach(System.err::println);
    }

    /** not working */
    void test2(String[] args) throws Exception {
        String text = "薄茶色のシミがあちこちについた掛け布団。座ったら、五分でお尻が序くなってきそうだ。";
        String url = String.format("https://www.google.com/transliterate?langpair=%s&text=%s", URLEncoder.encode("ja|ja-Hira", "utf-8"), URLEncoder.encode(text, "utf-8"));
System.err.println(url);
        List<?> list = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(URI.create(url).toURL().openStream()))) {
            list = gson.fromJson(reader, List.class);
        }
        list.forEach(System.err::println);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        GoogleJapaneseInput app = new GoogleJapaneseInput();
        app.test2(args);
    }
}
