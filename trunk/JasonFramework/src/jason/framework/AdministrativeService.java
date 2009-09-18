package jason.framework;

import jason.annotation.AccessibleTo;
import jason.annotation.Roles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 * WebService to deploy and undeploy other Jason WebServices. When required each
 * WebService may run in its own ClassLoader separating it from the other 
 * WebServices. As of now the KeyStores are assumed to be deployed already. 
 * However this behaviour may change in future versions.
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
@WebService
@Roles({Constants.SERVICE_PROVIDER_ROLE})
public final class AdministrativeService {
	private static final Logger logger = Logger.getLogger(AdministrativeService.class.getName());
	private static final AdministrativeService instance = new AdministrativeService();
	private final Set<ServiceEntry> serviceEntries;
	
	private AdministrativeService() {
		serviceEntries = new HashSet<ServiceEntry>();
	}
	
	public static AdministrativeService getInstance() {
		return instance;
	}
    
	/**
	 * Publishes a web service. The WebService will run in its own compartment, 
     * when required, by loading it with a new ClassLoader. The WebService will
     * be published and linked to a new instance of the {@link JasonHandler}.
	 * @param serviceEntry
	 */
	@WebMethod
	@AccessibleTo(Constants.SERVICE_PROVIDER_ROLE)
	public void add(final ServiceEntry serviceEntry) {
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
				@Override
				public Void run() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
					Object service;
					ClassLoader standardClassLoader = Thread.currentThread().getContextClassLoader();
					if (serviceEntry.isUsingClassloader()) {
						JasonClassLoader jasonClassLoader = new JasonClassLoader(serviceEntry.getClassPath());
						Thread.currentThread().setContextClassLoader(jasonClassLoader);
						service = jasonClassLoader.loadClass(serviceEntry.getClassName()).newInstance();
					} else //not using classloader
						service = serviceEntry.getService();
					Endpoint endpoint = Endpoint.create(service);
					endpoint.getBinding().setHandlerChain(
						new JasonHandlerResolver(serviceEntry).getHandlerChain(null)
					);
					endpoint.publish(serviceEntry.getEndpointAddress());
					Thread.currentThread().setContextClassLoader(standardClassLoader);
					serviceEntry.setEndpoint(endpoint);
					serviceEntries.add(serviceEntry);
					return null;
				}
			});

			logger.log(Level.INFO, "Service endpoint published on " + serviceEntry.getEndpointAddress());
		} catch (PrivilegedActionException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
	}
	
	/**
	 * Unpublishes a service.
	 * @param serviceEntry
	 */
	@WebMethod
	@AccessibleTo(Constants.SERVICE_PROVIDER_ROLE)
	public void remove(final ServiceEntry serviceEntry) {
		serviceEntries.remove(serviceEntry);
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			@Override
			public Void run() {
				serviceEntry.getEndpoint().stop();
				return null;
			}
		});
		logger.log(Level.INFO, serviceEntry.getEndpointAddress() + " is unpublished");
	}
	
	/**
	 * Closes all the registered serviceEntries.
	 */
	public void close() {
		for (ServiceEntry entry : serviceEntries) 
			remove(entry);
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
}
