package jason.test.server;

import jason.framework.ServiceEntry;
import jason.framework.AdministrativeService;
import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Startup {
	private static final Logger logger = Logger.getLogger(Startup.class.getName());
	
	private Startup() {
		//This class should not be instantiated.
	}

	public static void main(final String[] args) {
		try {
			if (args.length==1 && args[0].equals("remote")) {
				jason.test.server.ServiceEntry serviceEntry = new jason.test.server.ServiceEntry();
				serviceEntry.setClassName("jason.test.server.Service");
				serviceEntry.getClassPath().add(
					"file://" +
					new File("dist/JasonService.jar").getAbsolutePath()
				);
				serviceEntry.setKeystorePath("keys/keystore.jks");
				serviceEntry.setKeystorePassword("storepass");
				serviceEntry.setKeyPassword("keypass");
				serviceEntry.setWsdl("build/classes/Service/Service.wsdl");
				serviceEntry.setEndpointAddress("http://localhost:8080/Service/ServiceService");
				serviceEntry.setLogging(true);
				serviceEntry.setUsingClassloader(true);
				jason.test.server.AdministrativeService administrativeService = new AdministrativeServiceService().getAdministrativeServicePort();
				administrativeService.add(serviceEntry);
			} else {
				ServiceEntry serviceEntry = new ServiceEntry(
					"jason.test.server.Service",
					"dist/JasonService.jar",
					"keys/keystore.jks",
					"storepass",
					"keypass",
					"build/classes/Service/Service.wsdl",
					"http://localhost:8080/Service/ServiceService",
					true
				);
				AdministrativeService administrativeService = AdministrativeService.getInstance();
				administrativeService.add(serviceEntry);
				Thread.sleep(1000 * 60 * 15);
				administrativeService.close();
				logger.log(Level.INFO, "Stopped the service after 15 minutes");
			}
		} catch (InterruptedException ex) {
			logger.log(Level.SEVERE, null, ex);
		} catch (MalformedURLException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
	}
}
