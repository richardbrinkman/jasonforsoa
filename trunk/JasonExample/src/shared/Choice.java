package shared;

import java.io.Serializable;

public class Choice implements Serializable {
	private String description;
	private int amount;
	private int accountTo;
	
	public Choice() {
		
	}
	
	public Choice(String description, int amount, int accountTo) {
		this.description = description;
		this.amount = amount;
		this.accountTo = accountTo;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public int getAccountTo() {
		return accountTo;
	}
}
