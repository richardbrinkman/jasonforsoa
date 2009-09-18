/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import com.sun.xml.wss.impl.callback.EncryptionKeyCallback;
import com.sun.xml.wss.impl.callback.EncryptionKeyCallback.AliasX509CertificateRequest;
import com.sun.xml.wss.impl.callback.PasswordValidationCallback;
import com.sun.xml.wss.impl.callback.PasswordValidationCallback.PasswordValidationException;
import com.sun.xml.wss.impl.callback.PasswordValidationCallback.PlainTextPasswordRequest;
import com.sun.xml.wss.impl.callback.PasswordValidationCallback.Request;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;


/**
 *
 * @author brinkman
 */
public class PolicyHandler implements CallbackHandler {

    private static final Logger logger = Logger.getLogger(PolicyHandler.class.getName());
    
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (Callback callback : callbacks) {
            logger.info("Got callback of type: " + callback.getClass().getName());
			if (callback instanceof PasswordValidationCallback) {
				PasswordValidationCallback validationCallback = (PasswordValidationCallback) callback;
				if (validationCallback.getRequest() instanceof PlainTextPasswordRequest)
					validationCallback.setValidator(new PlainTextPasswordValidator());
			} else if (callback instanceof EncryptionKeyCallback) {
                EncryptionKeyCallback.Request request = ((EncryptionKeyCallback) callback).getRequest();
                logger.info("EncryptionKeyRequest has type: " + request.getClass().getName());
                if (request instanceof EncryptionKeyCallback.AliasX509CertificateRequest) {
                    EncryptionKeyCallback.AliasX509CertificateRequest aliasRequest = (AliasX509CertificateRequest) request;
                    String alias = aliasRequest.getAlias();
                    InputStream keystoreInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/public.jks");
                    try {
                        KeyStore keyStore;
                        keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                        keyStore.load(keystoreInput, "storepass".toCharArray());
                        X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
                        aliasRequest.setX509Certificate(x509Certificate);
                    } catch (NoSuchAlgorithmException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    } catch (CertificateException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    } catch (KeyStoreException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    } finally {
                        keystoreInput.close();
                    }                    
                }
            }
		}
	}

	private static class PlainTextPasswordValidator implements PasswordValidationCallback.PasswordValidator {

		public boolean validate(Request request) throws PasswordValidationException {
			PlainTextPasswordRequest passwordRequest = (PlainTextPasswordRequest) request;
			return passwordRequest.getUsername().equals("myusername") &&
			       passwordRequest.getPassword().equals("mypassword");
		}
		
	}
}
