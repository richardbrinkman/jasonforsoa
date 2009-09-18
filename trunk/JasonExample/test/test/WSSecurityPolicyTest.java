package test;

import com.sun.xml.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.model.wsdl.WSDLService;
import com.sun.xml.ws.policy.jaxws.PolicyResourceLoader;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

public class WSSecurityPolicyTest {
	private static final Logger logger = Logger.getLogger(WSSecurityPolicyTest.class.getName());

	@Test
	public void readWSSPolicy() {
		try {
			WSDLModel model = PolicyResourceLoader.getWsdlModel(
				Thread.currentThread().getContextClassLoader().getResource("META-INF/sts.wsdl"),
				true
			);
			assertNotNull("Model could not be read", model);
			assertFalse(
				"WSDL should not have extensions", 
				model.getExtensions().iterator().hasNext()
			);
			Map<QName,? extends WSDLService> services = model.getServices();
			assertNotNull("Could not get services", services);
			assertEquals(
				"Servicename is wrong",
				"{http://tempuri.org/}SecurityTokenService",
				services.get(
					services.keySet().iterator().next()
				).getName().toString()
			);
			for (Entry<QName,WSDLBoundPortType> entry : model.getBindings().entrySet()) {
				logger.info(
					entry.getKey().toString() + 
					" = " + 
					entry.getValue().getName().toString()
				);
			}
			assertNotNull(
				"Could not get the binding", 
				model.getBinding(
					new QName("http://tempuri.org/", "CustomBinding_ISecurityTokenService")
				)
			);
		} catch (IOException ex) {
			fail(ex.getLocalizedMessage());
			logger.log(Level.SEVERE, null, ex);
		} catch (XMLStreamException ex) {
			fail(ex.getLocalizedMessage());
			logger.log(Level.SEVERE, null, ex);
		} catch (SAXException ex) {
			fail(ex.getLocalizedMessage());
			logger.log(Level.SEVERE, null, ex);
		}
	}
}
