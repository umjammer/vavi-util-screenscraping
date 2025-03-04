/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.xml.jaxp.html.jericho;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

import static java.lang.System.getLogger;


/**
 * This implements JAXP parser interface for Tidy HTML parser.
 *
 * @author <a href=mailto:umjammer@gmail.com>nsano</a>
 * @version 0.00 060926 nsano initial version <br>
 */
@SuppressWarnings(value="deprecation")
public class SAXParserImpl extends SAXParser {

    private static final Logger logger = getLogger(SAXParserImpl.class.getName());

    /** */
    private final org.xml.sax.Parser parser;

    /** */
    SAXParserImpl() throws SAXException, ParserConfigurationException {
        parser = new org.codelibs.nekohtml.parsers.SAXParser(); // TODO use Tidy
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
logger.log(Level.TRACE, "not implemented");
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
