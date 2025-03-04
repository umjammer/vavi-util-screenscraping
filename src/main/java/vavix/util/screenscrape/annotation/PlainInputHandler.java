/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * PlainInputHandler.
 *
 * for ascii only documents (json etc.).
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/01 nsano initial version <br>
 */
public class PlainInputHandler implements InputHandler<InputStream> {

    /**
     * @param args 0: url
     */
    @Override
    public InputStream getInput(String ... args) throws IOException {
        String url = args[0];
        URLConnection connection = new URL(url).openConnection();
        InputStream is = connection.getInputStream();
        return new BufferedInputStream(is);
    }

    /**
     * args will be embedded in url.
     */
    @Override
    public String[] dealUrlAndArgs(String url, String ... args) {
        return InputHandler._dealUrlAndArgs(url, args);
    }
}
