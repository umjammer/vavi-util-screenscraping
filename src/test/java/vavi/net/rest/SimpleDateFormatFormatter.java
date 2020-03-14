/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.rest;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * SimpleDateFormatFormatter.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 080212 nsano initial version <br>
 */
@Deprecated
public class SimpleDateFormatFormatter implements Formatter {

    @Override
    public String format(String format, Object value) {
        return new SimpleDateFormat(format).format(Date.class.cast(value));
    }
}

/* */
