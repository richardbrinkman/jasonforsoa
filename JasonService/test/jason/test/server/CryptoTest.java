/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jason.test.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSEncryptionPart;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.apache.ws.security.util.WSSecurityUtil;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author brinkman
 */
public class CryptoTest {
	public static final String NAMESPACE = "http://cs.ru.nl/jason/ns";
	private static final String soapMessage = 
	  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
	  "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
//	  "  <S:Header>" +
//	  "    <wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">" +
//	  "      <wsu:Timestamp xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" wsu:Id=\"Timestamp-673105290\">" +
//	  "        <wsu:Created>2008-08-08T09:15:36.482Z</wsu:Created>" +
//	  "        <wsu:Expires>2008-08-08T09:20:36.482Z</wsu:Expires>" +
//	  "      </wsu:Timestamp>" +
//	  "    </wsse:Security>" +
//	  "  </S:Header>" +
	  "  <S:Body>" +
	  "    <ns2:sayHello xmlns:ns2=\"http://cs.ru.nl/jason/ns\">" +
	  "      <name>Richard</name>" +
	  "    </ns2:sayHello>" +
	  "  </S:Body>" +
	  "</S:Envelope>";

	private Crypto crypto;
	
    public CryptoTest() {
		testInitializeCrypto();
    }

	@Test
	public void testInitializeCrypto() {
		Properties cryptoProperties = new Properties();
		cryptoProperties.setProperty("org.apache.ws.security.crypto.provider", "org.apache.ws.security.components.crypto.Merlin");
		cryptoProperties.setProperty("org.apache.ws.security.crypto.merlin.file", "keys/keystore.jks");
		cryptoProperties.setProperty("org.apache.ws.security.crypto.merlin.keystore.type", KeyStore.getDefaultType());
		cryptoProperties.setProperty("org.apache.ws.security.crypto.merlin.keystore.password", "storepass");
		crypto = CryptoFactory.getInstance(cryptoProperties);
		assertNotNull("crypto cannot be initialized", crypto);
	}
	
	@Test
	public void testGetCertificates() {
		try {
			X509Certificate[] certs = crypto.getCertificates("service");
			assertNotNull("No certificates found", certs);
			assertNotSame("Found 0 certificates", certs.length, 0);
		} catch (WSSecurityException ex) {
			Logger.getLogger(CryptoTest.class.getName()).log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		}
	}
	
	@Test
	public void testFindElement() {
		try {
			Element rootElement = getDocument().getDocumentElement();
			assertNotNull("root element does not have a local name", rootElement.getLocalName());
			Node node = WSSecurityUtil.findElement(rootElement, "sayHello", NAMESPACE);
			assertNotNull("Could not find sayHello element", node);
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(CryptoTest.class.getName()).log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		} catch (IOException ex) {
			Logger.getLogger(CryptoTest.class.getName()).log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		} catch (SAXException ex) {
			Logger.getLogger(CryptoTest.class.getName()).log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		}
	}
	
	@Test
	public void testSignature() {
		try {
			Document document = getDocument();
			WSSecSignature signature = new WSSecSignature();
			signature.setUserInfo("service", "keypass");
			signature.setKeyIdentifierType(WSConstants.ISSUER_SERIAL);
			WSSecHeader header = new WSSecHeader();
			Element envelope = header.insertSecurityHeader(document);
			signature.prepare(document, crypto, header);
			Vector parts = new Vector();
			WSEncryptionPart encP = new WSEncryptionPart("sayHello", NAMESPACE, "Element");
			assertNotNull("encryptionPart should not be null", encP);
			parts.add(encP);
			signature.addReferencesToSign(parts, header);
			signature.prependToHeader(header);
			signature.prependBSTElementToHeader(header);
			signature.computeSignature();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Properties transformerProperties = new Properties();
			transformerProperties.setProperty(OutputKeys.MEDIA_TYPE, "xml");
			transformerProperties.setProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperties(transformerProperties);
			transformer.transform(new DOMSource(document), new StreamResult(System.out));
		} catch (TransformerException ex) {
			Logger.getLogger(CryptoTest.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SAXException ex) {
			Logger.getLogger(CryptoTest.class.getName()).log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		} catch (IOException ex) {
			Logger.getLogger(CryptoTest.class.getName()).log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(CryptoTest.class.getName()).log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		}
	}

	private Document getDocument() throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		return factory.newDocumentBuilder().parse(new ByteArrayInputStream(soapMessage.getBytes()));
	}
}