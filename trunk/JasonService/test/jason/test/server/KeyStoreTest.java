/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jason.test.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPrivateKey;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests whether the keys/keystore.jks is in good shape, i.e., has a private
 * DSAPrivateKey called "service" and two valid certificates: servicecertificate
 * and cacertificate.
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
public class KeyStoreTest {
	private KeyStore keystore;

    public KeyStoreTest() {
		FileInputStream input = null;
		try {
			keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			input = new FileInputStream("keys/keystore.jks");
			keystore.load(input, "storepass".toCharArray());
		} catch (IOException ex) {
			fail("Keystore cannot be read: " + ex.getLocalizedMessage());
		} catch (NoSuchAlgorithmException ex) {
			fail("Algorithm not found: " + ex.getLocalizedMessage());
		} catch (CertificateException ex) {
			fail("Something wrong with a certificate: " + ex.getLocalizedMessage());
		} catch (KeyStoreException ex) {
			fail("Keystore failure: " + ex.getLocalizedMessage());
		} finally {
			try {
				input.close();
			} catch (IOException ex) {
				fail("Failed to close the keystore inputstream: " + ex.getLocalizedMessage());
			}
		}
    }

	@Test
	public void testLoadKey() {
		try {
			Key key = keystore.getKey("service", "keypass".toCharArray());
			assertNotNull("Key is not read", key);
			assertTrue("Key should be a private key", key instanceof PrivateKey);
			assertTrue("Key should be a DSAPrivateKey", key instanceof DSAPrivateKey);
		} catch (KeyStoreException ex) {
			fail(ex.getLocalizedMessage());
		} catch (NoSuchAlgorithmException ex) {
			fail(ex.getLocalizedMessage());
		} catch (UnrecoverableKeyException ex) {
			fail(ex.getLocalizedMessage());
		}
	}
	
	@Test
	public void testLoadCertificate() {
		try {
			Certificate[] certificates = new Certificate[] {
				keystore.getCertificate("servicecertificate"),
				keystore.getCertificate("cacertificate")
			};
			Date today = new Date();
			for (Certificate certificate : certificates) {
				assertNotNull("Certificate should not be null", certificate);
				assertTrue("Service certificate should be a X509Certificate", certificate instanceof X509Certificate);
				X509Certificate x509Certificate = (X509Certificate) certificate;
				Date notAfter = x509Certificate.getNotAfter();
				Date notBefore = x509Certificate.getNotBefore();
				assertTrue("Service certificate is not yet valid", notBefore.before(today));
				assertTrue("Service certificate has expired", notAfter.after(today));
				X500Principal issuer = x509Certificate.getIssuerX500Principal();
				assertNotNull("Service certificate should have an issuer", issuer);
				String issuerName = issuer.getName();
				assertNotNull("Issuer name should not be null", issuerName);
				x509Certificate.checkValidity(today);
			}
			assertEquals(
				"Issuer is different from the one loaded from the keystore",
				((X509Certificate) certificates[0]).getIssuerX500Principal().getName(),
				((X509Certificate) certificates[1]).getSubjectX500Principal().getName()
			);
		} catch (CertificateExpiredException ex) {
			fail("Certificate has expired: " + ex.getLocalizedMessage());
		} catch (CertificateNotYetValidException ex) {
			fail("Certificate is not yet valid: " + ex.getLocalizedMessage());
		} catch (KeyStoreException ex) {
			fail(ex.getLocalizedMessage());
		}
	}
}