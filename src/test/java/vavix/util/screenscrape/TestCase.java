/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape;

import java.io.InputStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * TestCase.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-11-15 nsano initial version <br>
 */
public class TestCase {

    static class Test1 {
        String number;
    }

    @Test
    @Disabled
    void test1() throws Exception {
        InputStream xml = TestCase.class.getResourceAsStream("/amazon.xml");
        String xpath = "//DIV[@class='a-box-group a-spacing-base order']";
        XPathStream.source(xml, xpath)
                .map(s -> s.replaceAll("\\s+", " "))
                .forEach(System.err::println);
//        IntStream.range(0, 5).forEach(System.err::println);
    }
}
