package stonks2;

import java.io.Serializable;

import lombok.Data;

@Data
public class ScreenEntry implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Double fcfYield;
	private Double fcfMargin;
	
	private Company company;
	
	ScreenEntry(Company c) {
		this.company = c;
		
		final String fcfCurrCode = c.getCashFlow().getFreeCF().getCurrCode();
		final String revCurrCode = c.getCashFlow().getFreeCF().getCurrCode();
		final String mktCapCurrCode = c.getMarketCap().getCurrCode();
		
		if (fcfCurrCode.equals(mktCapCurrCode))
			this.fcfYield = c.getCashFlow().getFreeCF().getAmt() / (double) c.getMarketCap().getAmt();
		
		if (fcfCurrCode.equals(revCurrCode))
			this.fcfMargin = c.getCashFlow().getFreeCF().getAmt() / (double) c.getIncome().getRevenue().getAmt();
	}
	
	public String getTicker() {
		return this.company.getTicker();
	}
	
	public Double getPriceToSales() {
		return this.company.getPriceToSalesRatio();
	}
	
	public Industry getIndustry() {
		return this.company.getIndustry();
	}
	
	public Sector getSector() {
		return this.company.getSector();
	}
}
