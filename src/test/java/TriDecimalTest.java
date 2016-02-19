/*
 * Copyright (c) 2015 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * TestTriDecimal. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2015/11/06 umjammer initial version <br>
 */
public class TriDecimalTest {

    @Test
    public void test() {
        assertEquals("0", new TriDecimal(0).toString());
        assertEquals("1", new TriDecimal(1).toString());
        assertEquals("2", new TriDecimal(2).toString());
        assertEquals("10", new TriDecimal(3).toString());
        assertEquals("11", new TriDecimal(4).toString());
        assertEquals("12", new TriDecimal(5).toString());
        assertEquals("20", new TriDecimal(6).toString());
        assertEquals("21", new TriDecimal(7).toString());
        assertEquals("22", new TriDecimal(8).toString());
        assertEquals("100", new TriDecimal(9).toString());
    }

    @Test
    public void test2() {
        assertEquals("00000000000000000000", fill(0).substring(0, 20));
        assertEquals("10000000000000000000", fill(1).substring(0, 20));
        assertEquals("20000000000000000000", fill(2).substring(0, 20));
        assertEquals("01000000000000000000", fill(3).substring(0, 20));
        assertEquals("11000000000000000000", fill(4).substring(0, 20));
        assertEquals("21000000000000000000", fill(5).substring(0, 20));
        assertEquals("02000000000000000000", fill(6).substring(0, 20));
        assertEquals("12000000000000000000", fill(7).substring(0, 20));
        assertEquals("22000000000000000000", fill(8).substring(0, 20));
        assertEquals("00100000000000000000", fill(9).substring(0, 20));
        assertEquals("10100000000000000000", fill(10).substring(0, 20));
    }

    @Test
    public void test3() {
        assertEquals(0, TriDecimal.toDecimal("0"));
        assertEquals(1, TriDecimal.toDecimal("1"));
        assertEquals(2, TriDecimal.toDecimal("2"));
        assertEquals(3, TriDecimal.toDecimal("10"));
        assertEquals(4, TriDecimal.toDecimal("11"));
        assertEquals(5, TriDecimal.toDecimal("12"));
        assertEquals(6, TriDecimal.toDecimal("20"));
        assertEquals(7, TriDecimal.toDecimal("21"));
        assertEquals(8, TriDecimal.toDecimal("22"));
        assertEquals(9, TriDecimal.toDecimal("100"));
        assertEquals(10, TriDecimal.toDecimal("101"));
    }

    static String fill(int i) {
        StringBuilder sb = new StringBuilder(new TriDecimal(Integer.valueOf(i)).toString());
        sb.insert(0, "0000000000000000000");
        return sb.reverse().toString();
    }
    
    public static void main(String[] args) throws Exception {
        System.err.println(TriDecimal.toDecimal("220120"));
//        for (int i = 0; i < Math.pow(3, 20); i++) {
//            System.err.println(fill(i));
//        }
    }
}

/* */
