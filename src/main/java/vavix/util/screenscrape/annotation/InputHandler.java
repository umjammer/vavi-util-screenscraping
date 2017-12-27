/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.io.IOException;


/**
 * InputHandler.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/01 nsano initial version <br>
 */
public interface InputHandler<T> {

    /**
     * TODO こういう所だよなぁ、Java のメソッドが First Class Object だったらなぁと思う場面
     * @param args {@link WebScraper.Util#scrape(Class, String...)},
     *             {@link WebScraper.Util#foreach(Class, EachHandler, String...)}
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
}

/* */
