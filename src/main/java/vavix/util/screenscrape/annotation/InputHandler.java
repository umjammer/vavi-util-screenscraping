/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * InputHandler.
 *
 * @param <T> TODO necessary?
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/01 nsano initial version <br>
 */
public interface InputHandler<T> {

    /**
     * @param args {@link WebScraper.Util#scrape(Class, String...)},
     *             {@link WebScraper.Util#foreach(Class, java.util.function.Consumer, String...)}
     *             の args
     */
    T getInput(String ... args) throws IOException;

    /**
     * Converts args.
     * This default method does nothing.
     * Returns args just as it is.
     */
    default String[] dealUrlAndArgs(String url, String ... args) {
        return args;
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
     * @param args will be url-encoded.
     * @return [ url, args... ]
     * @throws IllegalArgumentException when url is null.
     */
    static String[] _dealUrlAndArgs(String url, String... args) {
        try {
            String[] newArgs = new String[args.length + 1];
            if (url != null && !url.isEmpty()) {
                int c = 0;
                for (String arg : args) {
                    url = url.replace("{" + c + "}", URLEncoder.encode(arg, "utf-8"));
//System.err.println(url + ", " + arg);
                    newArgs[c + 1] = arg;
                    c++;
                }
                newArgs[0] = url;
                return newArgs;
            } else {
                throw new IllegalArgumentException("url should not be null or empty");
            }
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
