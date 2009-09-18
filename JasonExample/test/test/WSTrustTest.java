package test;

import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.junit.Assert;
import org.junit.Test;

public class WSTrustTest {
	private static final Logger logger = Logger.getLogger(WSTrustTest.class.getName());
	
	@Test
	public void createRequestSecurityToken() {
		try {
			final WSTrustElementFactory factory = WSTrustElementFactory.newInstance(WSTrustVersion.WS_TRUST_13);
			final Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			final StreamResult result = new StreamResult(System.out);
			transformer.transform(
				factory.toSource(factory.createRST()),
				result
			);
			transformer.transform(
				factory.toSource(
					factory.createRSTForIssue(
						new URI("http://cs.ru.nl/jason/token/contract"), 
						new URI(WSTrustVersion.WS_TRUST_13.getIssueRequestTypeURI()), 
						null, 
						null, 
						null, 
						null, 
						null					
					)				
				),
				result			
			);
		} catch (URISyntaxException ex) {
			Assert.fail(ex.getMessage());
			logger.log(Level.SEVERE, null, ex);
		} catch (WSTrustException ex) {
			Assert.fail(ex.getMessage());
			logger.log(Level.SEVERE, null, ex);
		} catch (TransformerException ex) {
			Assert.fail(ex.getMessage());
			logger.log(Level.SEVERE, null, ex);
		}
	}
}