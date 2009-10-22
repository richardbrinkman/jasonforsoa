package jason.framework;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * <code>JasonClassLoader</code> enables to run strictly separate services on 
 * the same virtual machine. Services loaded by different ClassLoader instances
 * cannot access each other. <code>JasonClassLoader</code> can be used as 
 * follows:
 * <pre><code>
 * JasonClassLoader loaderServiceA = new JasonClassLoader(new URL[] {new URL("file:///home/to/ServiceA")});
 * JasonClassLoader loaderServiceB = new JasonClassLoader(new URL[] {new URL("file:///home/to/ServiceB")});
 * Object serviceA = loaderServiceA.loadClass("package.of.serviceA.MyServiceA");
 * Object serviceB = loaderServiceB.loadClass("package.or.serviceB.MyServiceB");
 * Endpoint endpointA = Endpoint.create(serviceA);
 * Endpoint endpointB = Endpoint.create(serviceB);
 * ...
 * </code></pre>
 * Please ensure that the services cannot be found on the classpath, since only
 * classes that cannot be loaded by the parent ClassLoader, will be loaded by 
 * the <code>JasonClassLoader</code>.
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
public class JasonClassLoader extends URLClassLoader {
	public JasonClassLoader(URL[] serviceClassPath) {
		super(serviceClassPath);
	}
}
