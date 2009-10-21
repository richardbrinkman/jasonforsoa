package jason.framework;

import javax.xml.namespace.QName;
//import org.apache.ws.security.WSConstants;

/**
 * This interface contains some constants, like namespaces, which are used in other
 * classes, such as {@link jason.compiler.JasonProcessor} and 
 * {@link jason.framework.JasonHandler}.
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
public final class Constants {
	public static final String JASON_NAMESPACE = "http://cs.ru.nl/jason/ns";
	public static final String XML_SIGNATURE_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";
	public static final String XML_ENCRYPTION_NAMESPACE = "http://www.w3.org/2001/04/xmlenc#";
	public static final String WS_POLICY_NAMESPACE = "http://www.w3.org/ns/ws-policy";
	public static final String WS_SECURITY_POLICY_NAMESPACE = "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702";
	public static final String WS_SECURITY_UTILITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wsssecurity-utility-1.0.xsd";
	public static final String WS_SECURECONVERSATION_NAMESPACE = "http://docs.oasis-open.org/ws-sx/ws-secureconverstation/200512";
	public static final String WS_TRUST_NAMESPACE = "http://docs.oasis-open.org/ws-sx/ws-trust/200512";
	public static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
	public static final String JASON_PREFIX = "jason";
	public static final String XML_SIGNATURE_PREFIX = "ds";
	public static final String XML_ENCRYPTION_PREFIX = "enc";
	public static final String WS_POLICY_PREFIX = "wsp";
	public static final String WS_SECURITY_POLICY_PREFIX = "sp";
	public static final String WS_SECURITY_UTILITY_PREFIX = "wsu";
	public static final String WS_SECURECONVERSATION_PREFIX = "wsc";
	public static final String WS_TRUST_POLICY = "wst";
	public static final String SIGNATURE_ELEMENT_NAME = "Signature";

	public static final QName SIGNATURE_ELEMENT_QNAME = new QName(XML_SIGNATURE_NAMESPACE, SIGNATURE_ELEMENT_NAME, XML_SIGNATURE_PREFIX);
//	public static final QName WSSE_SECURITY_ELEMENT_QNAME = new
//	QName(WSConstants.WSSE_PREFIX, WSConstants.WSSE_NS);
    
  public static final String SERVICE_PROVIDER_ROLE = "ServiceProvider";
    
	private Constants() {
			//You are not supposed to create instances of this class
	}
}
