package stonks2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Company {
	private String 				ticker;
	
	private Long				sharesOutstanding;

	private Double				priceToSalesRatio;
	
	private Sector 				sector;
	private Industry 			industry;
	
	private CashFlowStatement 	cashFlow;
	private IncomeStatement 	income;

	private Money				marketCap;
	
	public static Company initCompany(String ticker, JsonObject companyInfoResponse, JsonObject customFundamentals, JsonObject stats) throws RateLimitException {
		final Company ret = new Company(ticker);
		
		final CashFlowStatement cashFlow = new CashFlowStatement(ticker);
		final IncomeStatement income = new IncomeStatement(ticker);
		
		final JsonObject profile = companyInfoResponse
									.get("quoteSummary").getAsJsonObject()
									.get("result").getAsJsonArray()
									.get(0).getAsJsonObject()
									.get("assetProfile").getAsJsonObject();
		
		final JsonArray query = customFundamentals
									.get("timeseries").getAsJsonObject()
									.get("result").getAsJsonArray();
		
//		if (stats.get("revenue") == null) {//|| stats.get("ebitda") == null)
//			return null;
//		}
		
		String overviewCurrCode = null;
		try {
			overviewCurrCode = stats.get("currency").getAsString();
		} catch (NullPointerException e1) { overviewCurrCode = "USD"; }
		
		ret.marketCap = new Money(stats.get("marketCap").getAsJsonObject()
				.get("raw").getAsLong(), overviewCurrCode);
		
		ret.sharesOutstanding = stats.get("sharesOutstanding").getAsJsonObject()
									.get("raw").getAsLong();

		ret.sector = Sector.getSector(profile.get("sector").getAsString());
		ret.industry = Industry.getIndustry(profile.get("industry").getAsString());
		
		ret.income = income;
		ret.cashFlow = cashFlow;
		
		int nset = 0;
		boolean noRateLimit = false;
		
		for (JsonElement e : query) {
			final JsonObject obj = e.getAsJsonObject();
			final String type = obj.get("meta").getAsJsonObject()
									.get("type").getAsString();
			
			if (!obj.has(type))
				continue;
			
			final JsonObject statistic = obj.get(type).getAsJsonArray()
							.get(0).getAsJsonObject();
			
			final JsonElement value = statistic.get("reportedValue").getAsJsonObject()
												.get("raw");
			
			final String statCurrCode = statistic.get("currencyCode") != null ? 
							statistic.get("currencyCode").getAsString() : null;
			
			switch (type) {
			
			case "trailingFreeCashFlow":
				nset++;
				cashFlow.setFreeCF(new Money(value.getAsLong(), statCurrCode));
				continue;
				
			case "trailingResearchAndDevelopment":
				nset++;
				income.setRnd(new Money(value.getAsLong(), statCurrCode));
				continue;
				
			case "trailingOperatingCashFlow":
				nset++;
				cashFlow.setOperatingCF(new Money(value.getAsLong(), statCurrCode));
				continue;
				
			case "trailingOperatingIncome":
				nset++;
				income.setOperatingIncome(new Money(value.getAsLong(), statCurrCode));
				continue;
				
			case "trailingNetIncomeFromContinuingAndDiscontinuedOperation":
				nset++;
				income.setNetIncome(new Money(value.getAsLong(), statCurrCode));
				continue;
				
			case "trailingSellingGeneralAndAdministration":
				nset++;
				income.setSga(new Money(value.getAsLong(), statCurrCode));
				continue;
				
			case "trailingTotalRevenue":
				nset++;
				income.setRevenue(new Money(value.getAsLong(), statCurrCode));
				continue;
				
			case "trailingCapitalExpenditure":
				nset++;
				cashFlow.setCapEx(new Money(value.getAsLong(), statCurrCode));
				continue;
				
			case "trailingPsRatio":
				nset++;
				ret.priceToSalesRatio = value.getAsDouble();
				continue;
				
			case "annualTotalRevenue":
				noRateLimit = true;
				continue;
				
			case "annualTotalAssets":
				noRateLimit = true;
				continue;
			}
			
		}
		
		// No TTM data
		if (nset == 0 && noRateLimit) 
			return null;
		
		else if (nset == 0)
			throw new RateLimitException();
	
		return ret;
	}
	
	private Company(String ticker) {
		this.ticker = ticker;
	}
}
