/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import static java.lang.System.getLogger;


/**
 * JsonPath Parser.
 * <p>
 * This class ignores {@link WebScraper#encoding()}.
 * </p>
 * <p>
 * <li> 1 pass
 * <li> 2 pass
 *  the method to retrieve values by `JsonPath` specified at {@link Target#value()} from
 *  part of Json that is retrieved by `JsonPath` specified at {@link WebScraper#value()}
 * </p>
 * <p>
 * TODO it doesn't have to be json path? we might as well use gson deserialize?
 * </p>
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2018/02/22 nsano initial version <br>
 * @see "https://github.com/cmunilla/JPath"
 */
public class JsonPathParser<T> extends BaseParser<InputStream, T, Object> {

    private static final Logger logger = getLogger(JsonPathParser.class.getName());

    @Override
    protected String fixSelector(Field field, String selector) {
        String jsonPath = selector;
        if (jsonPath.isEmpty()) {
            if (isTwoPass) {
                jsonPath = field.getName();
            } else {
                jsonPath = "$." + (isCollection ? "." : "") + field.getName();
            }
        }
        return jsonPath;
    }

    @Override
    protected Object getDocument(InputStream input) {
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(input, encoding);
if (isDebug) {
 logger.log(Level.DEBUG, document);
}
        return document;
    }

    @Override
    protected Iterable<Object> selectAll(String selector, Object document) {
        List<Object> nodes = JsonPath.read(document, selector);
if (isDebug) {
 logger.log(Level.DEBUG, "nodes: " + nodes);
}
        if (isTwoPass) {
            List<?> a = (List<?>) nodes.get(0); // assume json-path's JSONArray has List interface
            return () -> new Iterator<>() {
                int i = 0;

                @Override
                public boolean hasNext() {
                    return i < a.size();
                }

                @Override
                public Object next() {
                    try {
                        return a.get(i++);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            };
        } else {
            return nodes;
        }
    }

    @Override
    protected String asText(Object node) {
        return node.toString().trim();
    }

    @Override
    protected Object select(String selector, Object document) {
        Object value = JsonPath.read(document, selector);
        return value;
    }

    @Override
    protected Object getSubDocument(Object node) {
if (isDebug) {
 logger.log(Level.DEBUG, node);
}
        return node;
    }
}
