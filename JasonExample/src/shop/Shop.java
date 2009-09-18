package shop;

import jason.annotation.Authentic;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import shared.Choice;
import shared.TransferDetails;

@WebService
public class Shop {
	@WebMethod
	public Choice choose() {
		return new Choice("Compaq 6710b", 799, 12345678);
	}

	@WebMethod
	public void order(
	  @Authentic(signedBy="Client") Choice choice, 
	  @Authentic(signedBy="Bank") TransferDetails transfer) {
		//Ship the order
	}
	
	public static void main(String[] args) {
		Endpoint endpoint = Endpoint.create(new Shop());
		endpoint.publish("http://localhost:8081/Shop");
	}
}
