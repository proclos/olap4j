/*
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.olap4j.driver.xmla;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.olap4j.metadata.XmlaConstant;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Utility methods for the olap4j driver for XML/A.
 *
 * <p>Many of the methods are related to XML parsing. For general-purpose
 * methods useful for implementing any olap4j driver, see the org.olap4j.impl
 * package and in particular {@link org.olap4j.impl.Olap4jUtil}.
 *
 * @author jhyde
 * @since Dec 2, 2007
 */
abstract class XmlaOlap4jUtil {
    static final String LINE_SEP =
        System.getProperty("line.separator", "\n");
    static final String SOAP_PREFIX = "SOAP-ENV";
    static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
    static final String XMLA_PREFIX = "xmla";
    static final String XMLA_NS = "urn:schemas-microsoft-com:xml-analysis";
    static final String MDDATASET_NS =
        "urn:schemas-microsoft-com:xml-analysis:mddataset";
    static final String ROWSET_NS =
        "urn:schemas-microsoft-com:xml-analysis:rowset";
    static final String SQL_NS =
        "urn:schemas-microsoft-com:xml-sql";

    static final String XSD_PREFIX = "xsd";
    static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";
    static final String XMLNS = "xmlns";

    static final String NAMESPACES_FEATURE_ID =
        "http://xml.org/sax/features/namespaces";
    static final String VALIDATION_FEATURE_ID =
        "http://xml.org/sax/features/validation";
    static final String SCHEMA_VALIDATION_FEATURE_ID =
        "http://apache.org/xml/features/validation/schema";
    static final String FULL_SCHEMA_VALIDATION_FEATURE_ID =
        "http://apache.org/xml/features/validation/schema-full-checking";
    static final String DEFER_NODE_EXPANSION =
        "http://apache.org/xml/features/dom/defer-node-expansion";
  
    
    static DocumentBuilderFactory factory = null;
    
    static DocumentBuilderFactory getDOMFactory() throws ParserConfigurationException {
    	if (factory == null) {
    		factory = DocumentBuilderFactory.newInstance();
    		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        	factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        	factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
            factory.setNamespaceAware(true);
    	}
    	return factory;
    }
    
    /**
     * Parse a stream into a Document (no validation).
     *
     */
    static Document parse(byte[] in)
        throws SAXException, IOException
    {
        InputSource source = new InputSource(new ByteArrayInputStream(in));
        try {
	        DocumentBuilder builder = getDOMFactory().newDocumentBuilder();
	        builder.setErrorHandler(new ErrorHandler() {
	
	        	@Override
	    		public void warning(SAXParseException exception) throws SAXException {
	    			//nothing to do 
	    		}
	
	    		@Override
	    		public void error(SAXParseException exception) throws SAXException {
	    			throw new SAXException(exception);
	    		}
	
	    		@Override
	    		public void fatalError(SAXParseException exception) throws SAXException {
	    			error(exception);
	    		}
	        	
	        });
	        return builder.parse(source);
        }
        catch (ParserConfigurationException e) {
        	throw new SAXException(e);
        }
    }

    
    static List<Node> listOf(final NodeList nodeList) {
        return new AbstractList<Node>() {
            public Node get(int index) {
                return nodeList.item(index);
            }

            public int size() {
                return nodeList.getLength();
            }
        };
    }

    static String gatherText(Element element) {
        StringBuilder buf = new StringBuilder();
        final NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            buf.append(childNodes.item(i).getTextContent());
        }
        return buf.toString();
    }

    static String prettyPrint(Element element) {
        StringBuilder string = new StringBuilder();
        prettyPrintLoop(element, string, "");
        return string.toString();
    }

    private static void prettyPrintLoop(
        NodeList nodes,
        StringBuilder string,
        String indentation)
    {
        for (int index = 0; index < nodes.getLength(); index++) {
            prettyPrintLoop(nodes.item(index), string, indentation);
        }
    }

    private static void prettyPrintLoop(
        Node node,
        StringBuilder string,
        String indentation)
    {
        if (node == null) {
            return;
        }

        int type = node.getNodeType();
        switch (type) {
        case Node.DOCUMENT_NODE:
            string.append("\n");
            prettyPrintLoop(node.getChildNodes(), string, indentation + "\t");
            break;

        case Node.ELEMENT_NODE:
            string.append(indentation);
            string.append("<");
            string.append(node.getNodeName());

            Attr[] attributes;
            if (node.getAttributes() != null) {
                int length = node.getAttributes().getLength();
                attributes = new Attr[length];
                for (int loopIndex = 0; loopIndex < length; loopIndex++) {
                    attributes[loopIndex] =
                        (Attr)node.getAttributes().item(loopIndex);
                }
            } else {
                attributes = new Attr[0];
            }

            for (Attr attribute : attributes) {
                string.append(" ");
                string.append(attribute.getNodeName());
                string.append("=\"");
                string.append(attribute.getNodeValue());
                string.append("\"");
            }

            string.append(">\n");

            prettyPrintLoop(node.getChildNodes(), string, indentation + "\t");

            string.append(indentation);
            string.append("</");
            string.append(node.getNodeName());
            string.append(">\n");

            break;

        case Node.TEXT_NODE:
            string.append(indentation);
            string.append(node.getNodeValue().trim());
            string.append("\n");
            break;

        case Node.PROCESSING_INSTRUCTION_NODE:
            string.append(indentation);
            string.append("<?");
            string.append(node.getNodeName());
            String text = node.getNodeValue();
            if (text != null && text.length() > 0) {
                string.append(text);
            }
            string.append("?>\n");
            break;

        case Node.CDATA_SECTION_NODE:
            string.append(indentation);
            string.append("<![CDATA[");
            string.append(node.getNodeValue());
            string.append("]]>");
            break;
        }
    }

    static Element findChild(Element element, String ns, String tag) {
        final NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                Element child = (Element) childNodes.item(i);
                if (child.getLocalName().equals(tag)
                    && (ns == null || child.getNamespaceURI().equals(ns)))
                {
                    return child;
                }
            }
        }
        return null;
    }

    static String stringElement(Element row, String name) {
        final NodeList childNodes = row.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node node = childNodes.item(i);
            if (name.equals(node.getLocalName())) {
                String result = node.getTextContent();
                // If content is not plain text then returns name of
                // first child tag
                if (result == null && node.hasChildNodes()) {
                    result = node.getFirstChild().getLocalName();
                }
                return result;
            }
        }
        return null;
    }

    static Integer integerElement(Element row, String name) {
        final String s = stringElement(row, name);
        if (s == null || s.equals("")) {
            return null;
        } else {
            return Integer.valueOf(s);
        }
    }

    static int integerElement(Element row, String name, int defaultValue) {
        final String s = stringElement(row, name);
        if (s == null || s.equals("")) {
            return defaultValue;
        } else {
            return Integer.valueOf(s);
        }
    }

    static byte byteElement(Element row, String name) {
        return Byte.valueOf(stringElement(row, name)).byteValue();
    }

    static short shortElement(Element row, String name) {
        return Short.valueOf(stringElement(row, name));
    }


    static int intElement(Element row, String name) {
        return integerElement(row, name).intValue();
    }

    static Double doubleElement(Element row, String name) {
        return Double.valueOf(stringElement(row, name));
    }

    static BigDecimal bigDecimalElement(Element row, String name) {
        return new BigDecimal(stringElement(row, name));
    }

    static boolean booleanElement(Element row, String name) {
        return booleanElement(row, name, false);
    }

    static boolean booleanElement(Element row, String name, boolean dflt) {
        final String s = stringElement(row, name);
        if (s == null) {
            return dflt;
        }
        return "true".equals(s);
    }

    static Float floatElement(Element row, String name) {
        return Float.valueOf(stringElement(row, name));
    }

    static long longElement(Element row, String name) {
        return Long.parseLong(stringElement(row, name));
    }

    static Object bigIntegerElement(Element row, String name) {
        return new BigInteger(stringElement(row, name));
    }

    static <E extends Enum<E> & XmlaConstant> E enumElement(
        Element row, XmlaConstant.Dictionary<E> dictionary, String name)
    {
        return enumElement(row, dictionary, name, null);
    }

    static <E extends Enum<E> & XmlaConstant> E enumElement(
        Element row, XmlaConstant.Dictionary<E> dictionary, String name,
        E defaultValue)
    {
        final String s = stringElement(row, name);
        if (s != null && !s.equals("")) {
            int result = Integer.valueOf(s);
            final E e = dictionary.forOrdinal(result);
            if (e != null) {
                return e;
            }
        }
        return defaultValue;
    }

    static <E extends Enum<E> & XmlaConstant> Set<E> enumSetElement(
        Element row, XmlaConstant.Dictionary<E> dictionary, String name,
        Set<E> defaultValue)
    {
        final Integer code = integerElement(row, name);
        if (code != null) {
            return dictionary.forMask(code);
        } else {
            return defaultValue;
        }
    }

    static List<Element> childElements(Element memberNode) {
        final List<Element> list = new ArrayList<Element>();
        final NodeList childNodes = memberNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node childNode = childNodes.item(i);
            if (childNode instanceof Element) {
                list.add((Element) childNode);
            }
        }
        return list;
    }

    static List<Element> findChildren(Element element, String ns, String tag) {
        final List<Element> list = new ArrayList<Element>();
        for (Node node : listOf(element.getChildNodes())) {
            if (tag.equals(node.getLocalName())
                && ((ns == null) || node.getNamespaceURI().equals(ns)))
            {
                list.add((Element) node);
            }
        }
        return list;
    }

    /**
     * Converts a Node to a String.
     *
     * @param node XML node
     * @param prettyPrint Whether to print with nice indentation
     * @return String representation of XML
     */
    public static String toString(Node node, boolean prettyPrint) {
        if (node == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, prettyPrint ? "yes" : "no");
            DOMSource source = new DOMSource(node);
            StreamResult result = new StreamResult(writer);
            transformer.transform(new DOMSource(node), new StreamResult(writer));          
            if (node instanceof Document) {
                transformer.transform(source, result);
            } else if (node instanceof Element) {
            	transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            	transformer.transform(source, result);
            } else if (node instanceof DocumentFragment) {
            	transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            	transformer.transform(source, result);
            } else if (node instanceof Text) {
                Text text = (Text) node;
                return text.getData();
            } else if (node instanceof Attr) {
                Attr attr = (Attr) node;
                String name = attr.getName();
                String value = attr.getValue();
                writer.write(name);
                writer.write("=\"");
                writer.write(value);
                writer.write("\"");
                if (prettyPrint) {
                    writer.write(LINE_SEP);
                }
            } else {
                writer.write("node class = " + node.getClass().getName());
                if (prettyPrint) {
                    writer.write(LINE_SEP);
                } else {
                    writer.write(' ');
                }
                writer.write("XmlUtil.toString: fix me: ");
                writer.write(node.toString());
                if (prettyPrint) {
                    writer.write(LINE_SEP);
                }
            }
            return writer.toString();
        } catch (Exception ex) {
            // ignore
            return null;
        }
    }



    /**
     * Error handler plus helper methods.
     */
    static class ErrorHandlerImpl implements ErrorHandler {
        public static final String WARNING_STRING        = "WARNING";
        public static final String ERROR_STRING          = "ERROR";
        public static final String FATAL_ERROR_STRING    = "FATAL";

        // DOMError values
        public static final short SEVERITY_WARNING      = 1;
        public static final short SEVERITY_ERROR        = 2;
        public static final short SEVERITY_FATAL_ERROR  = 3;

        public void printErrorInfos(PrintStream out) {
            if (errors != null) {
                for (ErrorInfo error : errors) {
                    out.println(formatErrorInfo(error));
                }
            }
        }

        public static String formatErrorInfos(ErrorHandlerImpl saxEH) {
            if (! saxEH.hasErrors()) {
                return "";
            }
            StringBuilder buf = new StringBuilder(512);
            for (ErrorInfo error : saxEH.getErrors()) {
                buf.append(formatErrorInfo(error));
                buf.append(LINE_SEP);
            }
            return buf.toString();
        }

        public static String formatErrorInfo(ErrorInfo ei) {
            StringBuilder buf = new StringBuilder(128);
            buf.append("[");
            switch (ei.severity) {
            case SEVERITY_WARNING:
                buf.append(WARNING_STRING);
                break;
            case SEVERITY_ERROR:
                buf.append(ERROR_STRING);
                break;
            case SEVERITY_FATAL_ERROR:
                buf.append(FATAL_ERROR_STRING);
                break;
            }
            buf.append(']');
            String systemId = ei.exception.getSystemId();
            if (systemId != null) {
                int index = systemId.lastIndexOf('/');
                if (index != -1) {
                    systemId = systemId.substring(index + 1);
                }
                buf.append(systemId);
            }
            buf.append(':');
            buf.append(ei.exception.getLineNumber());
            buf.append(':');
            buf.append(ei.exception.getColumnNumber());
            buf.append(": ");
            buf.append(ei.exception.getMessage());
            return buf.toString();
        }

        private List<ErrorInfo> errors;

        public ErrorHandlerImpl() {
        }

        public List<ErrorInfo> getErrors() {
            return this.errors;
        }

        public boolean hasErrors() {
            return (this.errors != null);
        }

        public void warning(SAXParseException exception) throws SAXException {
            addError(new ErrorInfo(SEVERITY_WARNING, exception));
        }

        public void error(SAXParseException exception) throws SAXException {
            addError(new ErrorInfo(SEVERITY_ERROR, exception));
        }

        public void fatalError(SAXParseException exception)
            throws SAXException
        {
            addError(new ErrorInfo(SEVERITY_FATAL_ERROR, exception));
        }

        protected void addError(ErrorInfo ei) {
            if (this.errors == null) {
                this.errors = new ArrayList<ErrorInfo>();
            }
            this.errors.add(ei);
        }

        public String getFirstError() {
            return (hasErrors())
                ? formatErrorInfo(errors.get(0))
                : "";
        }
    }

    static class ErrorInfo {
        final SAXParseException exception;
        final short severity;

        ErrorInfo(short severity, SAXParseException exception) {
            this.severity = severity;
            this.exception = exception;
        }
    }
}

// End XmlaOlap4jUtil.java
