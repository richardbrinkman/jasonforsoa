package shared;

public class TransferDetails {
	private int amount;
	private int accountFrom;
	private int accountTo;
	
	public TransferDetails() {
		
	}
	
	public TransferDetails(int amount, int accountFrom, int accountTo) {
		this.amount = amount;
		this.accountFrom = accountFrom;
		this.accountTo = accountTo;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public int getAccountFrom() {
		return accountFrom;
	}
	
	public int getAccountTo() {
		return accountTo;
	}
}
