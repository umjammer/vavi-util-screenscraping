/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * DefaultInputHandlerTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/27 umjammer initial version <br>
 */
public class DefaultInputHandlerTest {

    @Test
    public void test() {
        DefaultInputHandler dih = new DefaultInputHandler();
        String bar = "VAVI";
        String buz = "UMJAMMER";
        String[] result = dih.dealUrlAndArgs("http://foo.com?bar={0}&buz={1}", bar, buz);
        assertEquals(3, result.length);
        assertEquals("http://foo.com?bar=VAVI&buz=UMJAMMER", result[0]);
    }

}

/* */
