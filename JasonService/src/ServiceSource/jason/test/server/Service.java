package jason.test.server;

import jason.annotation.Authentic;
import jason.annotation.AvailablePolicies;
import jason.annotation.Confidential;
import jason.annotation.Fresh;
import jason.annotation.Policies;
import jason.annotation.Policy;
import jason.annotation.Roles;
import jason.annotation.ServiceRoles;
import javax.jws.WebParam;
import javax.jws.WebMethod;
import javax.jws.WebService;

@AvailablePolicies({"OldVersion", "NewVersion"})
@Roles("caller")
@ServiceRoles("service")
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
	@Authentic(signedBy="service") //for all policies
	@Fresh
	public String sayHello(
		@WebParam(name="name")
		@Policies({
			@Policy(name="OldVersion", authentic=@Authentic),
			@Policy(name="NewVersion", confidential=@Confidential)
		})
		String name) {
//		throw new RuntimeException("Test exception");
		return "Hello " + name;
	}
	
}

