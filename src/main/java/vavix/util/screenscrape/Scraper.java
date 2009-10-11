/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape;


/**
 * Scraper.
 *
 * @param <I> ���͂���f�[�^�̌^
 * @param <O> �؂�o�����f�[�^�̌^
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051014 nsano initial version <br>
 */
public interface Scraper<I, O> {
    /** */
    O scrape(I source);
}

/* */
