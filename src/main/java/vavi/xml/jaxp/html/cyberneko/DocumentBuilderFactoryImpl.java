/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.xml.jaxp.html.cyberneko;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * DocumentBuilderFactoryImpl.
 *
 * @author <a href=mailto:umjammer@gmail.com>nsano</a>
 * @version 0.00 031103 nsano initial version <br>
 *          0.01 040312 nsano i18n <br>
 */
public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {

    /** */
    private String encoding;

    /**
     *
     */
    @Override
    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        return new DocumentBuilderImpl(this);
    }

    /**
     * Allows the user to set specific attributes on the underlying implementation.
     */
    @Override
    public void setAttribute(String name, Object value) throws IllegalArgumentException {
        if ("encoding".equals(name)) {
            this.encoding = (String) value;
        } else {
            throw new IllegalArgumentException("unknown attribute: " + name);
        }
    }

    /**
     * Allows the user to retrieve specific attributes on the underlying implementation.
     */
    @Override
    public Object getAttribute(String name) throws IllegalArgumentException {
        if ("encoding".equals(name)) {
            return encoding;
        } else {
            throw new IllegalArgumentException("unknown attribute: " + name);
        }
    }

    /** @see javax.xml.parsers.DocumentBuilderFactory#setFeature(java.lang.String, boolean) */
    @Override
    public void setFeature(String feature, boolean enabled) throws ParserConfigurationException {
        throw new IllegalArgumentException("No feature are implemented");
    }

    /** @see javax.xml.parsers.DocumentBuilderFactory#getFeature(java.lang.String) */
    @Override
    public boolean getFeature(String feature) throws ParserConfigurationException {
        throw new IllegalArgumentException("No feature are implemented");
    }
}
