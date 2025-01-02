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
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static java.lang.System.getLogger;


/**
 * DefaultInputHandler.
 *
 * strings "{args_index}" specified in {@link WebScraper#url()} at {@link WebScraper} will be replaced by `args` by its order.
 * @see DefaultInputHandler#dealUrlAndArgs(String, String...)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/01 nsano initial version <br>
 */
public class DefaultInputHandler implements InputHandler<Reader> {

    private static final Logger logger = getLogger(DefaultInputHandler.class.getName());

    /**
     * CAUTION!!! Reader depends on <code>-Dfile.encoding</code>.
     *
     * @param args 0: url ({#} is embedded), 1, 2, 3...: {@link WebScraper.Util#scrape(Class, String...)}'s args or
     *             {@link WebScraper.Util#foreach(Class, java.util.function.Consumer, String...)}'s args.
     */
    @Override
    public Reader getInput(String ... args) throws IOException {
        String url = args[0];
logger.log(Level.DEBUG, "url: " + url);
        URLConnection connection = new URL(url).openConnection();
if (connection instanceof HttpURLConnection) {
 logger.log(Level.DEBUG, "responseCode: " + ((HttpURLConnection) connection).getResponseCode());
}
        InputStream is = connection.getInputStream();
//logger.log(Level.TRACE, StringUtil.getDump(baos.toByteArray()));
        // CAUTION!!! InputStreamReader depends on `-Dfile.encoding`.
        return new BufferedReader(new InputStreamReader(is));
    }

    /**
     * args will be embedded in url.
     * @param args will be url-encoded.
     */
    @Override
    public String[] dealUrlAndArgs(String url, String ... args) {
        return InputHandler._dealUrlAndArgs(url, args);
    }
}
