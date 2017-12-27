/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.translation;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.fail;


/**
 * GoogleTranslatorTest. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/24 umjammer initial version <br>
 */
@Ignore
public class GoogleTranslatorTest {

    @Test
    public void test() {
        fail("Not yet implemented");
    }

    //----

    /**
     * @param args [0] japanese sentence, [1] english sentence 
     */
    public static void main(String[] args) throws IOException {

        Translator translator = new GoogleTranslator();

        System.out.println("---- E to J ----");
        System.out.println("I: " + args[0]);
long t1 = System.currentTimeMillis();
        System.out.println("O: " + translator.toLocal(args[0]));
System.out.println("This translation costs " + (System.currentTimeMillis() - t1) + " ms");

//        System.out.println("---- J to E ----");
//        System.out.println("I: " + args[1]);
//long t2 = System.currentTimeMillis();
//        System.out.println("O: " + translator.toGlobal(args[1]));
//System.out.println("This translation costs " + (System.currentTimeMillis() - t2) + " ms");
        System.exit(0);
    }
}

/* */
