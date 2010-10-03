/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape;


/**
 * DOM ���\�� Stream ���� {@link #xpath} ��p���ăf�[�^��؂�o���C���^�[�t�F�[�X�ł��B
 *
 * {@link StringApacheXPathScraper} �� {@link StringSimpleXPathScraper} �̃T���v�����Q�Ƃ��Ă��������B
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051014 nsano initial version <br>
 */
public abstract class XPathScraper<I, O> implements Scraper<I, O> {

    /** �؂�o���ɗp���� XPath */
    protected String xpath;

    /** @param xpath �؂�o���ɗp���� XPath ������ */
    protected XPathScraper(String xpath) {
        this.xpath = xpath;
    }
}

/* */
