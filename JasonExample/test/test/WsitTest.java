package test;

import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.jaxws.PolicyConfigParser;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.SecurityAnnotator;
import com.sun.xml.wss.impl.SecurityRecipient;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy.UsernameTokenBinding;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy.X509CertificateBinding;
import com.sun.xml.wss.impl.policy.mls.PrivateKeyBinding;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.WSSPolicyGenerator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests to get a feeling how to work with WSIT and XWSS.
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
public class WsitTest {
	private static final String SOAPMESSAGE = 
	  "<?xml version=\"1.0\"?>" +
	  "<soap:Envelope xmlns:soap=\""+ MessageConstants.SOAP_1_1_NS + "\">" +
	    "<soap:Body>" +
		  "<tns:add xmlns:tns=\"http://jason.cs.ru.nl\">" +
		    "<tns:x>3</tns:x>" +
			"<tns:y>4</tns:y>" +
	      "</tns:add>" +
		"</soap:Body>" +
	  "</soap:Envelope>";
    private static final String TESTPRIVATEKEY = "testprivatekey";
    private static final String TESTCERTIFICATE = "testcertificate";
	private static final Logger logger = Logger.getLogger(WsitTest.class.getName());
	
	@Test
	public void testAuthenticatedSoap() {
		try {
			ProcessingContext processingContext = new ProcessingContext();
			assertNotNull("Could not create ProcessingContext", processingContext);

			//Set the callbackhandler
			processingContext.setHandler(new PolicyHandler());

			//Set the SecurityPolicy
			WSSPolicyGenerator wssPolicyGenerator = new WSSPolicyGenerator();
			AuthenticationTokenPolicy authenticationTokenPolicy = wssPolicyGenerator.newAuthenticationTokenPolicy();
			UsernameTokenBinding usernameTokenBinding = (UsernameTokenBinding) authenticationTokenPolicy.newUsernameTokenFeatureBinding();
			usernameTokenBinding.setUsername("myusername");
			usernameTokenBinding.setPassword("mypassword");
			processingContext.setSecurityPolicy(authenticationTokenPolicy);

			//Set the message
			MessageFactory messageFactory = MessageFactory.newInstance();
			InputStream soapInput = new ByteArrayInputStream(SOAPMESSAGE.getBytes());
			SOAPMessage soapMessage = messageFactory.createMessage(null, soapInput);
			processingContext.setSOAPMessage(soapMessage);

			SecurityAnnotator.secureMessage(processingContext);

			SOAPMessage securedSoapMessage = processingContext.getSOAPMessage();
			securedSoapMessage.writeTo(System.out);
			
			testVerifySoap(securedSoapMessage);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		} catch (SOAPException ex) {
			logger.log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		} catch (XWSSecurityException ex) {
			logger.log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		}
	}
	
	//@Test
	public void testSignedSoap() {
		try {
			ProcessingContext processingContext = new ProcessingContext();
			assertNotNull("Could not create ProcessingContext", processingContext);

			//Set the callbackhandler
			processingContext.setHandler(new PolicyHandler());

			//Set the SecurityPolicy
			WSSPolicyGenerator wssPolicyGenerator = new WSSPolicyGenerator();
			SignaturePolicy signaturePolicy = wssPolicyGenerator.newSignaturePolicy();
			X509CertificateBinding x509CertificateBinding = (X509CertificateBinding) signaturePolicy.newX509CertificateKeyBinding();
            PrivateKeyBinding privateKeyBinding = (PrivateKeyBinding) x509CertificateBinding.newPrivateKeyBinding();
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream keystoreInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/keystore.jks");
			keyStore.load(keystoreInput, "storepass".toCharArray());
			keystoreInput.close();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(TESTPRIVATEKEY, "keypass".toCharArray());
            privateKeyBinding.setPrivateKey(privateKey);
            keystoreInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/public.jks");
            keyStore.load(keystoreInput, "storepass".toCharArray());
			X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(TESTCERTIFICATE);
			assertEquals("CN=Test,OU=Digital Security,O=Radboud Universiteit,L=Nijmegen,ST=Gelderland,C=NL", x509Certificate.getSubjectX500Principal().getName());
			x509CertificateBinding.setX509Certificate(x509Certificate);
            x509CertificateBinding.setCertificateIdentifier(TESTCERTIFICATE);
            x509CertificateBinding.setKeyAlgorithm(MessageConstants.HMAC_SHA1_SIGMETHOD);
            x509CertificateBinding.setReferenceType("KeyIdentifier");
			processingContext.setSecurityPolicy(signaturePolicy);

			//Set the message
			MessageFactory messageFactory = MessageFactory.newInstance();
			InputStream soapInput = new ByteArrayInputStream(SOAPMESSAGE.getBytes());
			SOAPMessage soapMessage = messageFactory.createMessage(null, soapInput);
			processingContext.setSOAPMessage(soapMessage);

			SecurityAnnotator.secureMessage(processingContext);

			SOAPMessage securedSoapMessage = processingContext.getSOAPMessage();
			securedSoapMessage.writeTo(System.out);
			
			testVerifySoap(securedSoapMessage);
		} catch (KeyStoreException ex) {
            logger.log(Level.SEVERE, null, ex);
            fail(ex.getLocalizedMessage());
        } catch (NoSuchAlgorithmException ex) {
            logger.log(Level.SEVERE, null, ex);
            fail(ex.getLocalizedMessage());
        } catch (UnrecoverableKeyException ex) {
            logger.log(Level.SEVERE, null, ex);
            fail(ex.getLocalizedMessage());
        } catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		} catch (CertificateException ex) {
			logger.log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		} catch (SOAPException ex) {
			logger.log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		} catch (XWSSecurityException ex) {
			logger.log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		}
	}
	
	public void testVerifySoap(SOAPMessage securedSoapMessage) {
		try {
			assertNotNull("testSignSoap should be run first", securedSoapMessage);
			
			ProcessingContext processingContext = new ProcessingContext();
			assertNotNull("Could not create ProcessingContext", processingContext);
			
			//Set the callbackhandler
			processingContext.setHandler(new PolicyHandler());

			//Set the SecurityPolicy
			WSSPolicyGenerator wssPolicyGenerator = new WSSPolicyGenerator();
			AuthenticationTokenPolicy authenticationTokenPolicy = (AuthenticationTokenPolicy) wssPolicyGenerator.newAuthenticationTokenPolicy();
			UsernameTokenBinding usernameTokenBinding = (UsernameTokenBinding) authenticationTokenPolicy.newUsernameTokenFeatureBinding();
			usernameTokenBinding.setUsername("myusername");
			usernameTokenBinding.setPassword("mypassword");
			processingContext.setSecurityPolicy(authenticationTokenPolicy);

			processingContext.setSOAPMessage(securedSoapMessage);
			
			SecurityRecipient.validateMessage(processingContext);
		} catch (XWSSecurityException ex) {
			logger.log(Level.SEVERE, null, ex);
			fail(ex.getLocalizedMessage());
		}
	}
    
    @Test
    public void testParseWSSecurityPolicy() {
        try {
            URL wsdl = Thread.currentThread().getContextClassLoader().getResource("META-INF/interop-1.wsdl");
            PolicyMap policyMap = PolicyConfigParser.parse(wsdl, true);
            for (Policy policy : policyMap) {
                logger.info(policy.toString());
            }
        } catch (PolicyException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}