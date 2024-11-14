/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.xml.jaxp.html.jericho;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * SAXParserFactoryImpl.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060926 nsano initial version <br>
 */
public class SAXParserFactoryImpl extends SAXParserFactory {

    /** */
    public SAXParserFactoryImpl() {
    }

    @Override
    public SAXParser newSAXParser()
        throws SAXException, ParserConfigurationException {

        return new SAXParserImpl();
    }

    @Override
    public void setFeature(String name, boolean value) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean getFeature(String name) {
        throw new UnsupportedOperationException("not implemented");
    }
}
