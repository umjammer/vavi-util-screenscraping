/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webquery;

import java.util.Map;


/**
 * UrlHandler.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 080219 nsano initial version <br>
 */
@Deprecated
public interface QueryHandler {

    /** */
    Query getQuery(String url, Map<String, String> parameters, boolean doInput, boolean doOutput);
}
