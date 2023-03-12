package stonks2;

public class Filters {
	public static final Filter 
	
	/**
	 * Checks if company is in technology sector
	 */
	desiredSector = (c, conn) -> {
		if (c.getSector() == Sector.TECHNOLOGY
			||	c.getSector() == Sector.CONSUMER_CYCLICAL
			||	c.getSector() == Sector.CONSUMER_DEFENSIVE)
			return true;
		
		return false;
	},
	
	/**
	 * Check if FCF yield > n%
	 */
	checkFCFYield = (c, conn) -> {
		if (c == null) return false;
		
		final Double fcfYield = c.getFcfYield();
		final Double fcfMargin = c.getFcfMargin();
		
		final boolean cashFlowCheck = // If the yield is > 0.08 and the margin is > 0.1 this is true
									  (fcfYield != null && fcfYield > 0.075 && fcfMargin != null && fcfMargin > 0.1)
									  
									  // But if the yield was invalid, we'll still accept it if the margin is > 0.15
								||	  (fcfYield == null && fcfMargin != null && fcfMargin > 0.15);
		
		if (	cashFlowCheck
				&& notBankOrInsurer(c))
//			System.out.printf("Low Value Stock Found: %s - %.2f%% yield\t\t(%d / %.0f)"
//					+ "\t\t(OCF: %d, CapEX: %d, P: %s)\n",
//					stock.getSymbol(), fcfYield * 100, cfTTM.getFreeCF(), mktCap, 
//					cfTTM.getOperatingCF(), cfTTM.getCapEx(), p.toString());
			
			return true;
		
		else if (c.getPriceToSales() < 9 && c.getSector() == Sector.TECHNOLOGY)
			return true;
		
		return false;
	};
	
	
	

	
	
	
	
	private static boolean notBankOrInsurer(ScreenEntry c) {
		if (c.getSector() == Sector.FINANCIAL_SERVICES) {
			final Industry industry = c.getIndustry();
	
			switch (industry) {
			
				case BANKS_DIVERSIFIED:
				case BANKS_REGIONAL:
				case INSURANCE_LIFE:
				case INSURANCE_PROPERTY_AND_CASUALTY:
				case INSURANCE_REINSURANCE:
				case INSURANCE_SPECIALTY:
				case INSURANCE_BROKERS:
				case INSURANCE_DIVERSIFIED:
					return false;
					
			}
		}
		
		return true;
	}
}
