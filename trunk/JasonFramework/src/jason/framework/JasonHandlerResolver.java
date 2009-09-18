package jason.framework;

import java.util.LinkedList;
import java.util.List;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

/**
 * <code>JasonHandlerResolver</code> provides a handler chain for both the 
 * server and the client.
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
public class JasonHandlerResolver implements HandlerResolver {
	private final List<Handler> handlerChain;
	
	/**
	 * Creates a handler chain a handler chain consisting of the
	 * {@link JasonHandler} and an optional {@link LogHandler}.
	 * @param serviceEntry
	 */
	public JasonHandlerResolver(ServiceEntry serviceEntry) {
		handlerChain = new LinkedList<Handler>();
		handlerChain.add(new JasonHandler(serviceEntry));
		if (serviceEntry.isLogging())
			handlerChain.add(new LogHandler());
	}
	  
	/**
	 * Returns a list of the <code>JasonHandler</code> and optionally
	 * <code>LogHandler</code>
	 * @param portInfo this parameter is ignored
	 * @return handler chain
	 */
	@Override
	public List<Handler> getHandlerChain(PortInfo portInfo) {
		return handlerChain;
	}

}
