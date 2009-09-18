package bank;

import jason.annotation.Authentic;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import shared.TransferDetails;

@WebService
public class Bank {
	@WebMethod
	@Authentic(signedBy="Bank")
	public TransferDetails transfer(
	  @Authentic(signedBy="Client") int amount,
	  @Authentic(signedBy="Client") int accountFrom,
	  @Authentic(signedBy="Client") int accountTo) {
		return new TransferDetails(amount, accountFrom, accountTo);
	}
	
	public static void main(String[] args) {
		Endpoint endpoint = Endpoint.create(new Bank());
		endpoint.publish("http://localhost:8080/Bank");
	}
}
