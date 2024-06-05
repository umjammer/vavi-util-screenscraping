/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.openqa.selenium.WebDriver;

import vavix.util.selenium.SeleniumUtil;


/**
 * SeleniumInputHandler.
 *
 * strings "{args_index}" specified in {@link WebScraper#url()} at {@link WebScraper} will be replaced by `args` by its order.
 * @see SeleniumInputHandler#dealUrlAndArgs(String, String...)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2020/03/14 nsano initial version <br>
 */
public abstract class SeleniumInputHandler implements InputHandler<Reader> {

    /** */
    protected String cache;

    /**
     * @after {@link #cache} set
     * @param args 0: url ({#} is embedded), 1, 2, 3...: {@link WebScraper.Util#scrape(Class, String...)}'s args or
     *             {@link WebScraper.Util#foreach(Class, java.util.function.Consumer, String...)}'s args.
     */
    public Reader getInput(String ... args) throws IOException {
        if (cache != null) {
            return new StringReader(cache);
        }

        WebDriver driver = getDriver();

        String url = args[0];
//System.err.println("goto: " + url);
        driver.navigate().to(url);
        SeleniumUtil.waitFor(driver);
        cache = driver.getPageSource();

        return new StringReader(cache);
    }

    /**
     * args will be embedded in url.
     * @param args will be url-encoded.
     */
    public String[] dealUrlAndArgs(String url, String ... args) {
        return InputHandler._dealUrlAndArgs(url, args);
    }

    /** */
    protected abstract WebDriver getDriver();
}
