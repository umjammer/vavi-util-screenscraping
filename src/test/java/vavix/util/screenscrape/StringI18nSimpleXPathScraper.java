/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.InputSource;


/**
 * Java SE XPath で切り出す Scraper です。
 *
 * @author <a href=mailto:umjammer@gmail.com>nsano</a>
 * @version 0.00 050909 nsano initial version <br>
 */
@Deprecated
public class StringI18nSimpleXPathScraper extends SimpleXPathScraper<String> {

    /** encoding for html */
    private final String encoding;

    /** */
    public StringI18nSimpleXPathScraper(String xpath, String encoding) {
        super(xpath);
        this.encoding = encoding;
    }

    /** 。 */
    @Override
    public String scrape(InputStream is) {

        try {
//try {
// PrettyPrinter pp = new PrettyPrinter(System.out);
// pp.print(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is));
//} catch (Exception e) {
// Debug.println(e);
//}
            InputSource in = new InputSource(new InputStreamReader(is, encoding));
            String value = xPath.evaluate(xpath, in);
            return value;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (XPathExpressionException e) {
            throw (RuntimeException) new IllegalArgumentException("wrong input").initCause(e);
        }
    }

    // ----

    /**
     * @param args 0: url, 1: xpath
     */
    public static void main(String[] args) throws Exception {
        InputStream is = StringI18nSimpleXPathScraper.class.getResourceAsStream(args[0]);
        for (int i = 1; i < args.length; i++) {
            StringI18nSimpleXPathScraper scraper = new StringI18nSimpleXPathScraper(args[1], "UTF-8");
            System.out.println(scraper.scrape(is));
        }
    }
}
