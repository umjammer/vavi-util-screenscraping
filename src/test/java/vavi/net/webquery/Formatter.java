/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webquery;

import vavi.net.rest.Formatted;


/**
 * Formatter.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070224 nsano initial version <br>
 */
@Deprecated
public interface Formatter {

    /**
     * @param format {@link Formatted#value()}
     * @param value field value
     */
    String format(String format, Object value);
}

/* */
