package jason.test.server;

import jason.annotation.Authentic;
import jason.annotation.Confidential;
import jason.annotation.Roles;
import javax.jws.WebParam;
import javax.jws.WebMethod;
import javax.jws.WebService;

//@AvailablePolicies({"Version1", "Version2", "Version3"})
@Roles({"service", "caller"})
@WebService(targetNamespace = "http://cs.ru.nl/jason/ns")
public class Service {
	/** 
	 * Greets a user. In Version1 the <code>name</code> parameter should not be
	 * encrypted. Policy Version2 adds the encryption requirement of the
	 * parameter. Both versions specify that the output is encrypted.  
	 * @param name 
	 * @return "Hello" + name
	 */
	@WebMethod
	@Authentic(signedBy="service")
	public String sayHello(@WebParam(name="name") @Confidential @Authentic String name) {
		return "Hello " + name;
	}
	
}

