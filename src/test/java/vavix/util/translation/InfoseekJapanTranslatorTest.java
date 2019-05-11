/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.translation;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;


/**
 * InfoseekJapanTranslatorTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/24 umjammer initial version <br>
 */
@Disabled
public class InfoseekJapanTranslatorTest {

    @Test
    public void test() {
        fail("Not yet implemented");
    }

    //----

    /**
     * @param args [0] Japanese sentence, [1] English sentence
     */
    public static void main(String[] args) throws IOException {

        Translator translator = new InfoseekJapanTranslator();

        System.out.println("---- E to J ----");
        System.out.println("I: " + args[0]);
long t1 = System.currentTimeMillis();
        System.out.println("O: " + translator.toLocal(args[0]));
System.out.println("This translation costs " + (System.currentTimeMillis() - t1) + " ms");

        System.out.println("---- J to E ----");
        System.out.println("I: " + args[1]);
long t2 = System.currentTimeMillis();
        System.out.println("O: " + translator.toGlobal(args[1]));
System.out.println("This translation costs " + (System.currentTimeMillis() - t2) + " ms");
    }
}

/* */
