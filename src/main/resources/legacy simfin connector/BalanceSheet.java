package stonks.fundamental;

public class BalanceSheet {
	private final String 	ticker;
	
	long 			commonStock;
	
	BalanceSheet(String ticker) {
		this.ticker = ticker;
	}

	public long getCommonStock() {
		return commonStock;
	}

	public String getTicker() {
		return ticker;
	}
	
	void setCommonStock(long commonStock) {
		this.commonStock = commonStock;
	}
}
