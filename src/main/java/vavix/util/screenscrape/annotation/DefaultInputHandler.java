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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


/**
 * DefaultInputHandler.
 *
 * {@link WebScraper} で指定された {@link WebScraper#url()} 中の文字 {args_index} は args の順に置き換えられます。
 * @see {@link DefaultInputHandler#dealUrlAndArgs(String, String...)}
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/01 nsano initial version <br>
 */
public class DefaultInputHandler implements InputHandler<Reader> {

    /**
     * CAUTION!!! Reader が <code>-Dfile.encoding</code> に依存しているので注意
     *
     * @param args 0: url, 1, 2, 3...: {@link WebScraper.Util#scrape(Class, String...)}'s args or
     *             {@link WebScraper.Util#foreach(Class, EachHandler, String...)}'s args.
     */
    public Reader getInput(String ... args) throws IOException {
        String url = args[0];
System.err.println("url: " + url);
        URLConnection connection = new URL(url).openConnection();
if (HttpURLConnection.class.isInstance(connection)) {
 System.err.println("responseCode: " + HttpURLConnection.class.cast(connection).getResponseCode());
}
        InputStream is = connection.getInputStream();
//System.err.println(StringUtil.getDump(baos.toByteArray()));
        // CAUTION!!! InputStreamReader が -Dfile.encoding に依存しているので注意
        return new BufferedReader(new InputStreamReader(is));
    }

    /**
     * args will be embedded in url.
     */
    public String[] dealUrlAndArgs(String url, String ... args) {
        return InputHandler._dealUrlAndArgs(url, args);
    }
}

/* */
