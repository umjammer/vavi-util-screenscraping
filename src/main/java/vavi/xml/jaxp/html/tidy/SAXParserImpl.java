/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.xml.jaxp.html.tidy;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;


/**
 * This implements JAXP parser interface for Tidy HTML parser.
 *
 * @author <a href=mailto:umjammer@gmail.com>nsano</a>
 * @version 0.00 031103 nsano initial version <br>
 */
@SuppressWarnings(value="deprecation")
public class SAXParserImpl extends SAXParser {

    /** */
    private org.xml.sax.Parser parser;

    /** */
    SAXParserImpl() throws SAXException, ParserConfigurationException {
        parser = new org.cyberneko.html.parsers.SAXParser(); // TODO use Tidy
    }

    @Override
    public org.xml.sax.Parser getParser() throws SAXException {
        return parser;
    }

    @Override
    public XMLReader getXMLReader() throws SAXException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setProperty(String name, Object value) {
System.err.println("not implemented");
    }

    @Override
    public Object getProperty(String name) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isNamespaceAware() {
        return false;
    }

    @Override
    public boolean isValidating() {
        return false;
    }
}

/* */
