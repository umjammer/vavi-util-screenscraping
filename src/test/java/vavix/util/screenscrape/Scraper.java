/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape;


/**
 * Scraper.
 *
 * @param <I> 入力するデータの型
 * @param <O> 切り出されるデータの型
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051014 nsano initial version <br>
 */
@Deprecated
public interface Scraper<I, O> {
    /** */
    O scrape(I source);
}
