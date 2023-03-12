package stonks.fundamental;

public class CashFlowStatement {
	private final String	ticker;
	
	long 					operatingCF;
	long					capEx;
	
	CashFlowStatement(String ticker) {
		this.ticker = ticker;
	}
	
	public String getTicker() {
		return ticker;
	}

	public long getOperatingCF() {
		return operatingCF;
	}

	public long getCapEx() {
		return capEx;
	}

	public long getFreeCF() {
		return operatingCF + -Math.abs(capEx);
	}

	public void setOperatingCF(long operatingCF) {
		this.operatingCF = operatingCF;
	}

	public void setCapEx(long capEx) {
		this.capEx = capEx;
	}
}
