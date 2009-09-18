package test;

import jason.annotation.Authentic;
import jason.annotation.Confidential;
import jason.annotation.Roles;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
@Roles({"bank", "customer"})
public class Payment {
	@WebMethod
	@Authentic(signedBy="bank")
	public boolean transfer(
	  @Authentic(signedBy="customer") @WebParam(name="fromAccount") String fromAccount, 
	  @Authentic(signedBy="customer") @WebParam(name="toAccount") String toAccount, 
	  @Confidential(encryptedFor="bank") @WebParam(name="amount") float amount) {
		//Do the magic
		return true;
	}
}
