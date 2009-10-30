package jason.framework;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSEncryptionPart;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.apache.ws.security.message.WSSecTimestamp;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * The <code>JasonHandler</code> is still under construction. It is invoked both
 * for incoming messages and for outgoing messages. The sequence of outgoing 
 * messages is
 * <ol>
 *  <li>Read the plain SOAP message</li>
 *  <li>Search for an appropriate policy</li>
 *  <li>
 *      If such a policy exists the SOAP message is encrypted/signed according
 *      to that policy.
 *  </li>
 *  <li>Sends the encrypted/signed SOAP message to the other side</li>
 * </ol>
 * Incoming messages are handled as follows:
 * <ol>
 *  <li>The encrypted/signed SOAP message is read from the network</li>
 *  <li>Search for an appropriate policy</li>
 *  <li>
 *      If such a policy exists the signatures in the SOAP message are verified
 *      and the cipher texts are decrypted.
 *  </li>
 *  <li>The plain SOAP message is given to the <code>Endpoint</code></li>
 * </ol>
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
public class JasonHandler implements SOAPHandler<SOAPMessageContext> {
	private static final Logger logger = Logger.getLogger(JasonHandler.class.getName());

	private final ServiceEntry serviceEntry;
	private final WSSecurityEngine wsSecurityEngine;
	private Crypto crypto;
	private Document wsdl;
	private Node policy;
	private final XPath xpath;
	private KeyStore keyStore;
	
	public JasonHandler(final ServiceEntry serviceEntry) {
		this.serviceEntry = serviceEntry;
		xpath = XPathFactory.newInstance().newXPath();
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			//TODO: make namespace aware
//			documentFactory.setNamespaceAware(true);
			wsdl = documentFactory.newDocumentBuilder().parse(serviceEntry.getWsdl());
			//When nothing else specified use the default policy (the one without @Policy)
			policy = (Node) xpath.evaluate("/definitions/binding/Policy/ExactlyOne/All", wsdl, XPathConstants.NODE);
		} catch (XPathExpressionException ex) {
			logger.log(Level.SEVERE, null, ex);
		} catch (SAXException ex) {
			logger.log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		} catch (ParserConfigurationException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		final Properties cryptoProperties = new Properties();
		cryptoProperties.setProperty("org.apache.ws.security.crypto.provider", "org.apache.ws.security.components.crypto.Merlin");
		cryptoProperties.setProperty("org.apache.ws.security.crypto.merlin.file", serviceEntry.getKeystorePath());
		cryptoProperties.setProperty("org.apache.ws.security.crypto.merlin.keystore.type", KeyStore.getDefaultType());
		cryptoProperties.setProperty("org.apache.ws.security.crypto.merlin.keystore.password", serviceEntry.getKeystorePassword());
		wsSecurityEngine = AccessController.doPrivileged(new PrivilegedAction<WSSecurityEngine>() {
			@Override
			public WSSecurityEngine run() {
				WSSecurityEngine internalWSSecurityEngine = WSSecurityEngine.getInstance();
				crypto = CryptoFactory.getInstance(cryptoProperties);
				FileInputStream input = null;
				try {
					keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
					input = new FileInputStream(serviceEntry.getKeystorePath());
					keyStore.load(input, serviceEntry.getKeystorePassword().toCharArray());
				} catch (IOException ex) {
					logger.log(Level.SEVERE, null, ex);
				} catch (NoSuchAlgorithmException ex) {
					logger.log(Level.SEVERE, null, ex);
				} catch (CertificateException ex) {
					logger.log(Level.SEVERE, null, ex);
				} catch (KeyStoreException ex) {
					logger.log(Level.SEVERE, null, ex);
				} finally {
					if (input != null)
						try {
							input.close();
						} catch (IOException ex) {
							logger.log(Level.SEVERE, null, ex);
						}
				}
				return internalWSSecurityEngine;
			}
		});
                
	}
	
	/**
	 * Gets the header blocks that can be processed by this Handler instance.
	 * The JasonHandler is able to handle the SOAP headers 
	 * <code>&lt;jason:policy&gt;</code> and <code>&lt;ds:Signature&gt;</code>.
	 * @return the set of header names
	 */
	@Override 
	public Set<QName> getHeaders() {
		HashSet<QName> set = new HashSet<QName>();
		set.add(new QName(WSConstants.WSSE_PREFIX, WSConstants.WSSE_NS));
		return set;
	}

	@Override 
	public void close(MessageContext context) {
		//There is nothing to close
	}

	@Override 
	public boolean handleFault(SOAPMessageContext context) {
		return true; //Let the LogHandler log the fault
	}

	/**
	 * <code>handleMessage</code> will do the actual work of transforming a plain
	 * SOAP message to an encrypted/signed SOAP message or the otherway around.
	 * This function is still work in progress. Currently it just passes through
	 * the message unaltered.
	 * @param context
	 * @return true
	 */
	@Override 
	public boolean handleMessage(SOAPMessageContext context) {
		try {
			if ((Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)) { //outbound
				WSSecHeader secHeader = new WSSecHeader();
				secHeader.setMustUnderstand(false); //TODO: try mustUnderstand="true"
				SOAPPart soapEnvelope = context.getMessage().getSOAPPart();
				secHeader.insertSecurityHeader(soapEnvelope);
				
				//Find out what to do
//				Node securityPolicy = (Node) xpath.evaluate("", wsdl.getDocumentElement(), XPathConstants.NODE);
				
				//timestamp
				if (timestampRequested()) {
					WSSecTimestamp secTimestamp = new WSSecTimestamp();
					secTimestamp.prepare(soapEnvelope);
					secTimestamp.prependToHeader(secHeader);
				}

				//signature
				if (signatureRequested()) {
					WSSecSignature secSignature = new WSSecSignature();
					//TODO: Make "service" more generic
					//String alias = xpath.evaluate(expression, wsdl);
					String query = "substring-after(/definitions/binding[concat(\"tns:\",@name)=/definitions/service/port[address/@location=\"http://localhost:8080/Service/ServiceService\"]/@binding]/@type,\"tns:\")";
					String alias = xpath.evaluate(query, wsdl.getDocumentElement()).toLowerCase();
					secSignature.setUserInfo(alias, serviceEntry.getKeyPassword());
					secSignature.setKeyIdentifierType(WSConstants.ISSUER_SERIAL);
					secSignature.prepare(soapEnvelope, crypto, secHeader);
					Vector<WSEncryptionPart> parts = new Vector<WSEncryptionPart>();
					WSEncryptionPart encP = new WSEncryptionPart("Body", WSConstants.URI_SOAP11_ENV, "Element");
					parts.add(encP);
					secSignature.addReferencesToSign(parts, secHeader);
					secSignature.prependToHeader(secHeader);
					secSignature.prependBSTElementToHeader(secHeader);
					secSignature.computeSignature();
				}
			} else { //inbound
				List wsSecurityEngineResults = wsSecurityEngine.processSecurityHeader(context.getMessage().getSOAPPart(), "MyActor", null, crypto);
				if (wsSecurityEngineResults == null)
					logger.log(Level.INFO, "wsSecurityEngine does not have a validation result");
//				for (WSSecurityEngineResult result : wsSecurityEngineResults) 
//					for (Object entry : result.entrySet())
//						System.out.println("Key: " + ((Map.Entry) entry).getKey() + ", value: " + ((Map.Entry) entry).getValue());
				
			} 
		} catch (XPathExpressionException ex) {
			logger.log(Level.SEVERE, null, ex);
		} catch (WSSecurityException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		return true;
	}

	protected boolean signatureRequested() {
		logger.info("Signing is not yet implemented");
		return false;
	}

	protected boolean timestampRequested() {
		boolean result;
		try {
			if (policy == null)
				logger.log(Level.SEVERE, "policy == null");
			result = (Boolean) xpath.evaluate("IncludeTimestamp", policy, XPathConstants.BOOLEAN);
		} catch (XPathExpressionException ex) {
			logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			result = false;
		}
		return result;
	}
}
