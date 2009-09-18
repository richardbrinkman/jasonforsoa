/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jason.test.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.junit.Test;
import org.w3c.dom.Document;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
public class WsdlParseTest {
	private static final String wsdlLocation = "src/ServiceSource/ServiceService.wsdl";
	private Document wsdl;
	private static final Logger logger = Logger.getLogger(WsdlParseTest.class.getName());

    public WsdlParseTest() {
		parseWsdl();
    }

	@Test
	public void parseWsdl() {
		try {
			wsdl = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(wsdlLocation);
		} catch (ParserConfigurationException ex) {
			logger.log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		} catch (SAXException ex) {
			logger.log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		}
	}
	
	@Test
	public void testAlias() {
		assertNotNull("WSDL is not parsed", wsdl);
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			String query = "substring-after(/definitions/binding[concat(\"tns:\",@name)=/definitions/service/port[address/@location=\"http://localhost:8080/Service/ServiceService\"]/@binding]/@type,\"tns:\")";
			String alias = xpath.evaluate(query, wsdl.getDocumentElement()).toLowerCase();
			assertEquals("Invalid alias", "service", alias);
		} catch (XPathExpressionException ex) {
			logger.log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		}
	}
}