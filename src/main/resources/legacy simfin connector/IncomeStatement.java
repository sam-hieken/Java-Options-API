package stonks.fundamental;

public class IncomeStatement {
	private final String 	ticker;
	
	private Long 			revenue;
	private Long 			operatingIncome;
	private Long			netIncome;
	private Long			sga;
	private Long			rnd;
	
	IncomeStatement(String ticker) {
		this.ticker = ticker;
	}

	public String getTicker() {
		return ticker;
	}

	public Long getRevenue() {
		return revenue;
	}

	public Long getOperatingIncome() {
		return operatingIncome;
	}

	public Long getNetIncome() {
		return netIncome;
	}
	
	public Long getSGA() {
		return sga;
	}
	
	public Long getRND() {
		return rnd;
	}

	void setRevenue(Long revenue) {
		this.revenue = revenue;
	}

	void setOperatingIncome(Long operatingIncome) {
		this.operatingIncome = operatingIncome;
	}

	void setNetIncome(Long netIncome) {
		this.netIncome = netIncome;
	}
	
	void setSGA(Long sga) {
		this.sga = sga;
	}
	
	void setRND(Long rnd) {
		this.rnd = rnd;
	}
}
