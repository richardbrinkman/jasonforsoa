package jason.framework;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Endpoint;

/**
 * A <code>ServiceEntry</code> links a service with its WSDL file, Keystore and
 * Classpath location. It is used by {@link AdministrativeService} to register
 * all the Services.  
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
public class ServiceEntry {
	private final Object service;
	private final String keystorePath;
	private final String keystorePassword;
	private final String keyPassword;
	private final String wsdl;
	private final String className;
	private final URL[] classPath;
	private final String endpointAddress;
	private Endpoint endpoint;
	private final boolean logging;
	private final boolean usingClassloader;
	
    /**
     * This constructor is used by the client.
     * @param keystorePath
     * @param keystorePassword
     * @param keyPassword
     * @param wsdl
     * @param logging
     */
	public ServiceEntry(String keystorePath, String keystorePassword, String keyPassword, String wsdl, boolean logging) {
		this.service = null;
		this.keystorePath = keystorePath;
		this.keystorePassword = keystorePassword;
		this.keyPassword = keyPassword;
		this.wsdl = wsdl;
		this.className = null;
		this.classPath = null;
		this.endpointAddress = null;
		this.endpoint = null;
		this.logging = logging;
		this.usingClassloader = false;
	}

 	public ServiceEntry(Object service, String keystorePath, String keystorePassword, String keyPassword, String wsdl, String endpointAddress, boolean logging) {
		this.usingClassloader = false;
		this.service = service;
		this.keystorePath = keystorePath;
		this.keystorePassword = keystorePassword;
		this.keyPassword = keyPassword;
		this.wsdl = wsdl;
		this.className = service.getClass().getName();
		this.classPath = null;
		this.endpointAddress = endpointAddress;
		this.endpoint = null;
		this.logging = logging;
	}

    /**
     * This constructor is used by the server.
     * @param className
     * @param jarFile
     * @param keystorePath
     * @param keystorePassword
     * @param keyPassword
     * @param wsdl
     * @param endpointAddress
     * @param logging
     * @throws java.net.MalformedURLException
     */
	public ServiceEntry(String className, String jarFile, String keystorePath, String keystorePassword, String keyPassword, String wsdl, String endpointAddress, boolean logging) throws MalformedURLException {
		this.service = null;
		this.usingClassloader = true;
		final File jar = new File(jarFile);
        String absolutePath = AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return jar.getAbsolutePath();
            }
        });
		this.className = className;
		this.classPath = new URL[] {new URL("file://" + absolutePath)};
		this.keystorePath = keystorePath;
		this.keystorePassword = keystorePassword;
		this.keyPassword = keyPassword;
		this.wsdl = wsdl;
		this.endpointAddress = endpointAddress;
		this.endpoint = null;
		this.logging = logging;
	}
	
	public ServiceEntry(String className, URL[] classPath, String keystorePath, String keystorePassword, String keyPassword, String wsdl, String endpointAddress, boolean logging) {
    this.service = null;
		this.usingClassloader = true;
		this.keystorePath = keystorePath;
		this.keystorePassword = keystorePassword;
		this.keyPassword = keyPassword;
		this.wsdl = wsdl;
		this.className = className;
		this.classPath = classPath.clone();
		this.endpointAddress = endpointAddress;
		this.logging = logging;
	}

	public URL[] getClassPath() {
		return classPath.clone();
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}
	
	public String getKeyPassword() {
		return keyPassword;
	}

	public String getKeystorePath() {
		return keystorePath;
	}

	public String getWsdl() {
		return wsdl;
	}

	public String getEndpointAddress() {
		return endpointAddress;
	}
	
	public Endpoint getEndpoint() {
		return endpoint;
	}
	
	public void setEndpoint(Endpoint endpoint) {
		if (this.endpoint == null) 
			this.endpoint = endpoint;
		else
			Logger.getLogger(ServiceEntry.class.getName()).log(Level.WARNING, "Cannot set endpoint when it is already set before");
	}

	public boolean isLogging() {
		return logging;
	}

	public String getClassName() {
		return className;
	}

	public Object getService() {
		return service;
	}

	public boolean isUsingClassloader() {
		return usingClassloader;
	}
}
