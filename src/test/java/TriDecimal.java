/*
 * Copyright (c) 2015 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */


/**
 * TriDecimal. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2015/11/06 umjammer initial version <br>
 */
public class TriDecimal extends NDecimal {

    TriDecimal(int value) {
        super(value, 3);
    }

    public static int toDecimal(String nDecimal) {
        return toDecimal(nDecimal, 3);
    }
}

/* */
