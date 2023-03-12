package stonks2;

import java.io.Serializable;

import lombok.Data;

@Data
public class CashFlowStatement implements Serializable {
	private final String	ticker;
	
	Money 					operatingCF;
	Money 					freeCF;
	Money					capEx;
	
	CashFlowStatement(String ticker) {
		this.ticker = ticker;
	}
}
