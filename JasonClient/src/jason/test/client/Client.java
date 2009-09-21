package jason.test.client;

import jason.framework.JasonHandlerResolver;
import jason.framework.ServiceEntry;

public class Client {
	public static void main(String[] args) {
		ServiceService serviceService = new ServiceService();
		ServiceEntry serviceEntry = new ServiceEntry(
			"keys/keystore.jks", 
			"storepass", 
			"keypass",
			"../JasonService/build/classes/Service/Service.wsdl", 
			true
		);
		serviceService.setHandlerResolver(new JasonHandlerResolver(serviceEntry));
		Service service = serviceService.getServicePort();
		System.out.println(service.sayHello("Richard"));
	}
}
