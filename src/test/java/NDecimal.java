import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2015 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */


/**
 * NDecimal. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2015/11/06 umjammer initial version <br>
 */
public class NDecimal {

    protected final int base;
    protected final int value;
    
    /**
     * 
     */
    public NDecimal(int value, int base) {
        this.value = value;
        this.base = base; 
    }

    public String toString() {
        if (base == 0 || value == 0) {
            return "0";
        }
        
        List<Integer> list = new ArrayList<Integer>();
        int v = value;
        while (v > 0) {
            list.add(v % base);
            v = (v / base);
        }

        StringBuilder sb = new StringBuilder();
        for (Integer number : list) {
            sb.append(number);
        }
        return sb.reverse().toString();
    }

    protected static int toDecimal(String nDecimal, int base) {
        int b = 1;
        int v = 0;
        for (char c : new StringBuilder(nDecimal).reverse().toString().toCharArray()) {
            v += Integer.parseInt(String.valueOf(c)) * b;
            b *= base;
        }
        return v;
    }
}

/* */
