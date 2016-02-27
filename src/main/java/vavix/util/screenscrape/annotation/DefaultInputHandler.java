/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;


/**
 * DefaultInputHandler. 
 * 
 * WebScraper で指定された url 中の文字 {args_index} は args の順に置き換えられます。
 * @see {@link DefaultInputHandler#dealUrlAndArgs(String, String...)}
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/01 nsano initial version <br>
 */
public class DefaultInputHandler implements InputHandler<Reader> {

    /**
     * CAUTION!!! Reader が -Dfile.encoding に依存しているので注意
     * 
     * @param args 0: url 
     */
    public Reader getInput(String ... args) throws IOException {
        String url = args[0];
//System.err.println("url: " + url);
        URLConnection connection = new URL(url).openConnection();
        InputStream is = connection.getInputStream();
//System.err.println(StringUtil.getDump(baos.toByteArray()));
        // CAUTION!!! InputStreamReader が -Dfile.encoding に依存しているので注意
        return new BufferedReader(new InputStreamReader(is));
    }

    /**
     * replaces "{args_index}" in url.
     * 
     * <pre>
     *  ex. url: "http://foo.com?bar={0}&buz={1}"
     *      args: { "VAVI", "UMJAMMER" }
     *      
     *      result: "http://foo.com?bar=VAVI&buz=UMJAMMER"
     * </pre>
     */
    public String[] dealUrlAndArgs(String url, String... args) {
        String[] newArgs = new String[1];
        if (url != null && !url.isEmpty()) {
            int c = 0;
            for (String arg : args) {
                url = url.replace("{" + c++ + "}", arg);
                //System.err.println(url + ", " + arg);
            }
            newArgs[0] = url;
            return newArgs;
        } else {
            throw new IllegalArgumentException("url should not be null or empty");
        }
    }
}

/* */
