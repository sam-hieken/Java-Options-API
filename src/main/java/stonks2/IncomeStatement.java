package stonks2;

import lombok.Data;

@Data
public class IncomeStatement {
	private final String 	ticker;
	
	private Money 			revenue;
	private Money 			operatingIncome;
	private Money			netIncome;
	private Money			sga;
	private Money			rnd;
	
	IncomeStatement(String ticker) {
		this.ticker = ticker;
	}
}
