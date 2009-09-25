package jason.framework;

import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.Node;

/**
 * When put in a HandlerChain a <code>LogHandler</code> will log the SOAP 
 * messages that are passing by. Depending on the order of the 
 * <code>LogHandler</code> and the {@link JasonHandler} it will log plain or
 * encrypted/signed messages. You can even add the sequence of 
 * <code>LogHandler</code> - <code>JasonHandler</code> - <code>LogHandler</code>
 * to see both the plain and the encrypted/signed messages.
 * @author brinkman
 */
public class LogHandler implements SOAPHandler<SOAPMessageContext> {
	private final static Logger logger = Logger.getLogger(LogHandler.class.getName());
	
	@Override 
	public Set<QName> getHeaders() {
		return null;
	}

	@Override 
	public void close(MessageContext context) {
		//There is nothing to close
	}

	/**
	 * Logs a fault to the standard Logger.
	 * @param context The SOAPMessageContext
	 * @return false
	 */
	@Override 
	public boolean handleFault(SOAPMessageContext context) {
		try {
			SOAPMessage message = context.getMessage();
			logger.log(Level.SEVERE, "handleFault:\n" + direction(context) + prettyPrintXML(message.getSOAPPart()));
		} catch (TransformerConfigurationException ex) {
			logger.log(Level.SEVERE, null, ex);
		} catch (TransformerException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		return false;
	}

	/**
	 * Logs the message to the standard Logger.
	 * @param context The SOAPMessageContext
	 * @return true
	 */
	@Override 
	public boolean handleMessage(SOAPMessageContext context) {
		SOAPMessage message = context.getMessage();
		try {
			logger.info(direction(context) + prettyPrintXML(message.getSOAPPart()));
		} catch (TransformerConfigurationException ex) {
			logger.log(Level.SEVERE, null, ex);
		} catch (TransformerException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		return true;
	}

	protected String direction(SOAPMessageContext context) {
		return (((Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue() ? "out" : "in") + "bound message:\n";
	}

	/**
	 * Prints a node and its contents as an XML stream to the standard Logger. 
	 * Extra whitespace is added to improve readability.
	 * @param node Node to be printed (for example the <code>SOAPPart</code>)
	 * @throws javax.xml.transform.TransformerConfigurationException
	 * @throws javax.xml.transform.TransformerException
	 */
	protected String prettyPrintXML(Node node) throws TransformerConfigurationException, TransformerException {
		DOMSource source = new DOMSource(node);
		StringWriter output = new StringWriter();
		StreamResult target = new StreamResult(output);
		TransformerFactory factory = TransformerFactory.newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);
		factory.setAttribute("indent-number", Integer.valueOf(2));
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.transform(source, target);
		return output.toString();
	}
}
