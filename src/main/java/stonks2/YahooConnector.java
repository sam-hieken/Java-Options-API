package stonks2;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("unused")
public class YahooConnector implements Closeable {
	private static final String allTickersQuery = "https://www.nyse.com/api/quotes/filter";
	private static final String allTickersBody = "{\"instrumentType\":\"EQUITY\",\"pageNumber\":1,\"sortColumn\":\"NORMALIZED_TICKER\",\"sortOrder\":\"ASC\",\"maxResultsPerPage\":1000000,\"filterToken\":\"\"}";
	
	private static final String customQuery = "https://query2.finance.yahoo.com/ws/fundamentals-timeseries/v1/finance/timeseries/<<TICKER>>?lang=en-US&region=US&padTimeSeries=true&type=<<TYPE>>&merge=false&period1=493590046&period2=<<TIMESTAMP>>";
	private static final String screenQuery = "https://query2.finance.yahoo.com/ws/fundamentals-timeseries/v1/finance/timeseries/<<TICKER>>?lang=en-US&region=US&padTimeSeries=true&type=annualTotalAssets,annualTotalRevenue,trailingResearchAndDevelopment,trailingSellingGeneralAndAdministration,trailingNetIncomeFromContinuingAndDiscontinuedOperation,trailingOperatingIncome,trailingTotalRevenue,trailingFreeCashFlow,trailingOperatingCashFlow,trailingCapitalExpenditure,trailingPsRatio&merge=false&period1=493590046&period2=<<TIMESTAMP>>&corsDomain=finance.yahoo.com";
	private static final String incomeQuery = "https://query2.finance.yahoo.com/ws/fundamentals-timeseries/v1/finance/timeseries/<<TICKER>>?lang=en-US&region=US&padTimeSeries=true&type=annualTaxEffectOfUnusualItems,trailingTaxEffectOfUnusualItems,annualTaxRateForCalcs,trailingTaxRateForCalcs,annualNormalizedEBITDA,trailingNormalizedEBITDA,annualNormalizedDilutedEPS,trailingNormalizedDilutedEPS,annualNormalizedBasicEPS,trailingNormalizedBasicEPS,annualTotalUnusualItems,trailingTotalUnusualItems,annualTotalUnusualItemsExcludingGoodwill,trailingTotalUnusualItemsExcludingGoodwill,annualNetIncomeFromContinuingOperationNetMinorityInterest,trailingNetIncomeFromContinuingOperationNetMinorityInterest,annualReconciledDepreciation,trailingReconciledDepreciation,annualReconciledCostOfRevenue,trailingReconciledCostOfRevenue,annualEBITDA,trailingEBITDA,annualEBIT,trailingEBIT,annualNetInterestIncome,trailingNetInterestIncome,annualInterestExpense,trailingInterestExpense,annualInterestIncome,trailingInterestIncome,annualContinuingAndDiscontinuedDilutedEPS,trailingContinuingAndDiscontinuedDilutedEPS,annualContinuingAndDiscontinuedBasicEPS,trailingContinuingAndDiscontinuedBasicEPS,annualNormalizedIncome,trailingNormalizedIncome,annualNetIncomeFromContinuingAndDiscontinuedOperation,trailingNetIncomeFromContinuingAndDiscontinuedOperation,annualTotalExpenses,trailingTotalExpenses,annualRentExpenseSupplemental,trailingRentExpenseSupplemental,annualReportedNormalizedDilutedEPS,trailingReportedNormalizedDilutedEPS,annualReportedNormalizedBasicEPS,trailingReportedNormalizedBasicEPS,annualTotalOperatingIncomeAsReported,trailingTotalOperatingIncomeAsReported,annualDividendPerShare,trailingDividendPerShare,annualDilutedAverageShares,trailingDilutedAverageShares,annualBasicAverageShares,trailingBasicAverageShares,annualDilutedEPS,trailingDilutedEPS,annualDilutedEPSOtherGainsLosses,trailingDilutedEPSOtherGainsLosses,annualTaxLossCarryforwardDilutedEPS,trailingTaxLossCarryforwardDilutedEPS,annualDilutedAccountingChange,trailingDilutedAccountingChange,annualDilutedExtraordinary,trailingDilutedExtraordinary,annualDilutedDiscontinuousOperations,trailingDilutedDiscontinuousOperations,annualDilutedContinuousOperations,trailingDilutedContinuousOperations,annualBasicEPS,trailingBasicEPS,annualBasicEPSOtherGainsLosses,trailingBasicEPSOtherGainsLosses,annualTaxLossCarryforwardBasicEPS,trailingTaxLossCarryforwardBasicEPS,annualBasicAccountingChange,trailingBasicAccountingChange,annualBasicExtraordinary,trailingBasicExtraordinary,annualBasicDiscontinuousOperations,trailingBasicDiscontinuousOperations,annualBasicContinuousOperations,trailingBasicContinuousOperations,annualDilutedNIAvailtoComStockholders,trailingDilutedNIAvailtoComStockholders,annualAverageDilutionEarnings,trailingAverageDilutionEarnings,annualNetIncomeCommonStockholders,trailingNetIncomeCommonStockholders,annualOtherunderPreferredStockDividend,trailingOtherunderPreferredStockDividend,annualPreferredStockDividends,trailingPreferredStockDividends,annualNetIncome,trailingNetIncome,annualMinorityInterests,trailingMinorityInterests,annualNetIncomeIncludingNoncontrollingInterests,trailingNetIncomeIncludingNoncontrollingInterests,annualNetIncomeFromTaxLossCarryforward,trailingNetIncomeFromTaxLossCarryforward,annualNetIncomeExtraordinary,trailingNetIncomeExtraordinary,annualNetIncomeDiscontinuousOperations,trailingNetIncomeDiscontinuousOperations,annualNetIncomeContinuousOperations,trailingNetIncomeContinuousOperations,annualEarningsFromEquityInterestNetOfTax,trailingEarningsFromEquityInterestNetOfTax,annualTaxProvision,trailingTaxProvision,annualPretaxIncome,trailingPretaxIncome,annualOtherIncomeExpense,trailingOtherIncomeExpense,annualOtherNonOperatingIncomeExpenses,trailingOtherNonOperatingIncomeExpenses,annualSpecialIncomeCharges,trailingSpecialIncomeCharges,annualGainOnSaleOfPPE,trailingGainOnSaleOfPPE,annualGainOnSaleOfBusiness,trailingGainOnSaleOfBusiness,annualOtherSpecialCharges,trailingOtherSpecialCharges,annualWriteOff,trailingWriteOff,annualImpairmentOfCapitalAssets,trailingImpairmentOfCapitalAssets,annualRestructuringAndMergernAcquisition,trailingRestructuringAndMergernAcquisition,annualSecuritiesAmortization,trailingSecuritiesAmortization,annualEarningsFromEquityInterest,trailingEarningsFromEquityInterest,annualGainOnSaleOfSecurity,trailingGainOnSaleOfSecurity,annualNetNonOperatingInterestIncomeExpense,trailingNetNonOperatingInterestIncomeExpense,annualTotalOtherFinanceCost,trailingTotalOtherFinanceCost,annualInterestExpenseNonOperating,trailingInterestExpenseNonOperating,annualInterestIncomeNonOperating,trailingInterestIncomeNonOperating,annualOperatingIncome,trailingOperatingIncome,annualOperatingExpense,trailingOperatingExpense,annualOtherOperatingExpenses,trailingOtherOperatingExpenses,annualOtherTaxes,trailingOtherTaxes,annualProvisionForDoubtfulAccounts,trailingProvisionForDoubtfulAccounts,annualDepreciationAmortizationDepletionIncomeStatement,trailingDepreciationAmortizationDepletionIncomeStatement,annualDepletionIncomeStatement,trailingDepletionIncomeStatement,annualDepreciationAndAmortizationInIncomeStatement,trailingDepreciationAndAmortizationInIncomeStatement,annualAmortization,trailingAmortization,annualAmortizationOfIntangiblesIncomeStatement,trailingAmortizationOfIntangiblesIncomeStatement,annualDepreciationIncomeStatement,trailingDepreciationIncomeStatement,annualResearchAndDevelopment,trailingResearchAndDevelopment,annualSellingGeneralAndAdministration,trailingSellingGeneralAndAdministration,annualSellingAndMarketingExpense,trailingSellingAndMarketingExpense,annualGeneralAndAdministrativeExpense,trailingGeneralAndAdministrativeExpense,annualOtherGandA,trailingOtherGandA,annualInsuranceAndClaims,trailingInsuranceAndClaims,annualRentAndLandingFees,trailingRentAndLandingFees,annualSalariesAndWages,trailingSalariesAndWages,annualGrossProfit,trailingGrossProfit,annualCostOfRevenue,trailingCostOfRevenue,annualTotalRevenue,trailingTotalRevenue,annualExciseTaxes,trailingExciseTaxes,annualOperatingRevenue,trailingOperatingRevenue&merge=false&period1=493590046&period2=<<TIMESTAMP>>&corsDomain=finance.yahoo.com";
	private static final String cashFlowQuery = "https://query2.finance.yahoo.com/ws/fundamentals-timeseries/v1/finance/timeseries/<<TICKER>>?lang=en-US&region=US&padTimeSeries=true&type=annualForeignSales,trailingForeignSales,annualDomesticSales,trailingDomesticSales,annualAdjustedGeographySegmentData,trailingAdjustedGeographySegmentData,annualFreeCashFlow,trailingFreeCashFlow,annualRepurchaseOfCapitalStock,trailingRepurchaseOfCapitalStock,annualRepaymentOfDebt,trailingRepaymentOfDebt,annualIssuanceOfDebt,trailingIssuanceOfDebt,annualIssuanceOfCapitalStock,trailingIssuanceOfCapitalStock,annualCapitalExpenditure,trailingCapitalExpenditure,annualInterestPaidSupplementalData,trailingInterestPaidSupplementalData,annualIncomeTaxPaidSupplementalData,trailingIncomeTaxPaidSupplementalData,annualEndCashPosition,trailingEndCashPosition,annualOtherCashAdjustmentOutsideChangeinCash,trailingOtherCashAdjustmentOutsideChangeinCash,annualBeginningCashPosition,trailingBeginningCashPosition,annualEffectOfExchangeRateChanges,trailingEffectOfExchangeRateChanges,annualChangesInCash,trailingChangesInCash,annualOtherCashAdjustmentInsideChangeinCash,trailingOtherCashAdjustmentInsideChangeinCash,annualCashFlowFromDiscontinuedOperation,trailingCashFlowFromDiscontinuedOperation,annualFinancingCashFlow,trailingFinancingCashFlow,annualCashFromDiscontinuedFinancingActivities,trailingCashFromDiscontinuedFinancingActivities,annualCashFlowFromContinuingFinancingActivities,trailingCashFlowFromContinuingFinancingActivities,annualNetOtherFinancingCharges,trailingNetOtherFinancingCharges,annualInterestPaidCFF,trailingInterestPaidCFF,annualProceedsFromStockOptionExercised,trailingProceedsFromStockOptionExercised,annualCashDividendsPaid,trailingCashDividendsPaid,annualPreferredStockDividendPaid,trailingPreferredStockDividendPaid,annualCommonStockDividendPaid,trailingCommonStockDividendPaid,annualNetPreferredStockIssuance,trailingNetPreferredStockIssuance,annualPreferredStockPayments,trailingPreferredStockPayments,annualPreferredStockIssuance,trailingPreferredStockIssuance,annualNetCommonStockIssuance,trailingNetCommonStockIssuance,annualCommonStockPayments,trailingCommonStockPayments,annualCommonStockIssuance,trailingCommonStockIssuance,annualNetIssuancePaymentsOfDebt,trailingNetIssuancePaymentsOfDebt,annualNetShortTermDebtIssuance,trailingNetShortTermDebtIssuance,annualShortTermDebtPayments,trailingShortTermDebtPayments,annualShortTermDebtIssuance,trailingShortTermDebtIssuance,annualNetLongTermDebtIssuance,trailingNetLongTermDebtIssuance,annualLongTermDebtPayments,trailingLongTermDebtPayments,annualLongTermDebtIssuance,trailingLongTermDebtIssuance,annualInvestingCashFlow,trailingInvestingCashFlow,annualCashFromDiscontinuedInvestingActivities,trailingCashFromDiscontinuedInvestingActivities,annualCashFlowFromContinuingInvestingActivities,trailingCashFlowFromContinuingInvestingActivities,annualNetOtherInvestingChanges,trailingNetOtherInvestingChanges,annualInterestReceivedCFI,trailingInterestReceivedCFI,annualDividendsReceivedCFI,trailingDividendsReceivedCFI,annualNetInvestmentPurchaseAndSale,trailingNetInvestmentPurchaseAndSale,annualSaleOfInvestment,trailingSaleOfInvestment,annualPurchaseOfInvestment,trailingPurchaseOfInvestment,annualNetInvestmentPropertiesPurchaseAndSale,trailingNetInvestmentPropertiesPurchaseAndSale,annualSaleOfInvestmentProperties,trailingSaleOfInvestmentProperties,annualPurchaseOfInvestmentProperties,trailingPurchaseOfInvestmentProperties,annualNetBusinessPurchaseAndSale,trailingNetBusinessPurchaseAndSale,annualSaleOfBusiness,trailingSaleOfBusiness,annualPurchaseOfBusiness,trailingPurchaseOfBusiness,annualNetIntangiblesPurchaseAndSale,trailingNetIntangiblesPurchaseAndSale,annualSaleOfIntangibles,trailingSaleOfIntangibles,annualPurchaseOfIntangibles,trailingPurchaseOfIntangibles,annualNetPPEPurchaseAndSale,trailingNetPPEPurchaseAndSale,annualSaleOfPPE,trailingSaleOfPPE,annualPurchaseOfPPE,trailingPurchaseOfPPE,annualCapitalExpenditureReported,trailingCapitalExpenditureReported,annualOperatingCashFlow,trailingOperatingCashFlow,annualCashFromDiscontinuedOperatingActivities,trailingCashFromDiscontinuedOperatingActivities,annualCashFlowFromContinuingOperatingActivities,trailingCashFlowFromContinuingOperatingActivities,annualTaxesRefundPaid,trailingTaxesRefundPaid,annualInterestReceivedCFO,trailingInterestReceivedCFO,annualInterestPaidCFO,trailingInterestPaidCFO,annualDividendReceivedCFO,trailingDividendReceivedCFO,annualDividendPaidCFO,trailingDividendPaidCFO,annualChangeInWorkingCapital,trailingChangeInWorkingCapital,annualChangeInOtherWorkingCapital,trailingChangeInOtherWorkingCapital,annualChangeInOtherCurrentLiabilities,trailingChangeInOtherCurrentLiabilities,annualChangeInOtherCurrentAssets,trailingChangeInOtherCurrentAssets,annualChangeInPayablesAndAccruedExpense,trailingChangeInPayablesAndAccruedExpense,annualChangeInAccruedExpense,trailingChangeInAccruedExpense,annualChangeInInterestPayable,trailingChangeInInterestPayable,annualChangeInPayable,trailingChangeInPayable,annualChangeInDividendPayable,trailingChangeInDividendPayable,annualChangeInAccountPayable,trailingChangeInAccountPayable,annualChangeInTaxPayable,trailingChangeInTaxPayable,annualChangeInIncomeTaxPayable,trailingChangeInIncomeTaxPayable,annualChangeInPrepaidAssets,trailingChangeInPrepaidAssets,annualChangeInInventory,trailingChangeInInventory,annualChangeInReceivables,trailingChangeInReceivables,annualChangesInAccountReceivables,trailingChangesInAccountReceivables,annualOtherNonCashItems,trailingOtherNonCashItems,annualExcessTaxBenefitFromStockBasedCompensation,trailingExcessTaxBenefitFromStockBasedCompensation,annualStockBasedCompensation,trailingStockBasedCompensation,annualUnrealizedGainLossOnInvestmentSecurities,trailingUnrealizedGainLossOnInvestmentSecurities,annualProvisionandWriteOffofAssets,trailingProvisionandWriteOffofAssets,annualAssetImpairmentCharge,trailingAssetImpairmentCharge,annualAmortizationOfSecurities,trailingAmortizationOfSecurities,annualDeferredTax,trailingDeferredTax,annualDeferredIncomeTax,trailingDeferredIncomeTax,annualDepreciationAmortizationDepletion,trailingDepreciationAmortizationDepletion,annualDepletion,trailingDepletion,annualDepreciationAndAmortization,trailingDepreciationAndAmortization,annualAmortizationCashFlow,trailingAmortizationCashFlow,annualAmortizationOfIntangibles,trailingAmortizationOfIntangibles,annualDepreciation,trailingDepreciation,annualOperatingGainsLosses,trailingOperatingGainsLosses,annualPensionAndEmployeeBenefitExpense,trailingPensionAndEmployeeBenefitExpense,annualEarningsLossesFromEquityInvestments,trailingEarningsLossesFromEquityInvestments,annualGainLossOnInvestmentSecurities,trailingGainLossOnInvestmentSecurities,annualNetForeignCurrencyExchangeGainLoss,trailingNetForeignCurrencyExchangeGainLoss,annualGainLossOnSaleOfPPE,trailingGainLossOnSaleOfPPE,annualGainLossOnSaleOfBusiness,trailingGainLossOnSaleOfBusiness,annualNetIncomeFromContinuingOperations,trailingNetIncomeFromContinuingOperations,annualCashFlowsfromusedinOperatingActivitiesDirect,trailingCashFlowsfromusedinOperatingActivitiesDirect,annualTaxesRefundPaidDirect,trailingTaxesRefundPaidDirect,annualInterestReceivedDirect,trailingInterestReceivedDirect,annualInterestPaidDirect,trailingInterestPaidDirect,annualDividendsReceivedDirect,trailingDividendsReceivedDirect,annualDividendsPaidDirect,trailingDividendsPaidDirect,annualClassesofCashPayments,trailingClassesofCashPayments,annualOtherCashPaymentsfromOperatingActivities,trailingOtherCashPaymentsfromOperatingActivities,annualPaymentsonBehalfofEmployees,trailingPaymentsonBehalfofEmployees,annualPaymentstoSuppliersforGoodsandServices,trailingPaymentstoSuppliersforGoodsandServices,annualClassesofCashReceiptsfromOperatingActivities,trailingClassesofCashReceiptsfromOperatingActivities,annualOtherCashReceiptsfromOperatingActivities,trailingOtherCashReceiptsfromOperatingActivities,annualReceiptsfromGovernmentGrants,trailingReceiptsfromGovernmentGrants,annualReceiptsfromCustomers,trailingReceiptsfromCustomers&merge=false&period1=493590046&period2=<<TIMESTAMP>>&corsDomain=finance.yahoo.com";
	
	private static final String	industryQuery = "https://query1.finance.yahoo.com/v10/finance/quoteSummary/<<TICKER>>?formatted=true&lang=en-US&region=US&modules=assetProfile&corsDomain=finance.yahoo.com";
	private static final String statQuery = "https://query2.finance.yahoo.com/v7/finance/quote?formatted=true&lang=en-US&region=US&symbols=<<TICKER>>&fields=ebitda,revenue,messageBoardId,longName,shortName,marketCap,underlyingSymbol,underlyingExchangeSymbol,headSymbolAsString,regularMarketPrice,regularMarketChange,regularMarketChangePercent,regularMarketVolume,uuid,regularMarketOpen,fiftyTwoWeekLow,fiftyTwoWeekHigh,toCurrency,fromCurrency,toExchange,fromExchange&corsDomain=finance.yahoo.com";
	
	public static final int 	ESTIMATED_DB_SIZE = 8100;
	
	private final OkHttpClient 			client;
	private final YahooConnectorDirect 	conn;
	
	public YahooConnector() {
		this.client = new OkHttpClient.Builder()
			      .connectTimeout(12, TimeUnit.SECONDS)
			      .build();

		final Retrofit rfit = new Retrofit.Builder().baseUrl("https://query2.finance.yahoo.com/")
				.client(client)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		
		this.conn = rfit.create(YahooConnectorDirect.class);
	}
	
	/**
	 * @param ticker The public ticker
	 * @return The company represented by this ticker, null if ticker invalid.
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public Company getCompany(String ticker, JsonObject stats) throws IOException, InterruptedException {
		try {
			for (int i = 10 ;; i += 10) {
				
				try {
//					TimeUnit.MILLISECONDS.sleep(50);
					final JsonObject 	info = getCompanyInfo(ticker);
//					TimeUnit.MILLISECONDS.sleep(50);
					final JsonObject	fundamentals = getScreenFundamentals(ticker);
		
					clearIdle();
					
					return Company.initCompany(ticker, info, fundamentals, stats);
				} catch (RateLimitException e) {
					int iterations = (i / 10 - 1);
					
					// At this point it's not a rate limit issue
					// We couldn't fix it so just say fuck it, move along
					if (iterations > 6)
						return null;
					
					System.err.println(ticker + " Rate limit hit, pausing... (" + iterations + " iterations)");
					TimeUnit.SECONDS.sleep(i);
				}
			}
		} catch (NullPointerException | IllegalStateException e) {
			return null;
		}
	}
	
	public JsonObject getCustomData(String ticker, String...queryTypes) throws IOException {
		final StringBuilder type = new StringBuilder();
		for (String str : queryTypes)
			type.append(str + ",");
		
		type.setLength(type.length() - 1);
		
		final String url = customQuery
				.replace("<<TICKER>>", ticker)
				.replace("<<TIMESTAMP>>", "" + (System.currentTimeMillis() / 1000 - 15))
				.replace("<<TYPE>>", type.toString());
		
		final var resp = conn.get(url).execute();
		final var body = resp.body();
		final int code = resp.code();
		
		if (code != 200 && code != 404) {
			System.err.println(body);
		}
		
		return body;
	}
	
	public JsonObject getScreenFundamentals(String ticker) throws IOException {
		final String url = screenQuery.replace("<<TICKER>>", ticker).replace("<<TIMESTAMP>>", "" + (System.currentTimeMillis() / 1000 - 15));
		
		final var resp = conn.get(url).execute();
		final var body = resp.body();
		final int code = resp.code();
		
		if (code != 200 && code != 404)
			System.err.println(body);
		
		return body;
	}
	
	public JsonObject getStatistics(List<String> tickers) throws IOException {
		final StringBuilder paramBuilder = new StringBuilder();
		for (String ticker : tickers) {
			paramBuilder.append(ticker);
			paramBuilder.append(",");
		}
		
		final String param = paramBuilder.substring(0, paramBuilder.length() - 1);
		
		return getStatistics(param);
	}
	
	private JsonObject getCompanyInfo(String ticker) throws IOException, RateLimitException {
		final String url = industryQuery.replace("<<TICKER>>", ticker);
		final var resp = conn.get(url).execute();
		final var body = resp.body();
		final int code = resp.code();
		
		if (resp.code() != 200 && code != 404) {
			System.err.println("ERROR: " + code + ", " + url);
//			System.err.println("ERROR: " + code);
			System.err.println(body);
		}
		
		else if (resp.code() >= 500)
			throw new RateLimitException();
		
		return body;
	}
	
	public JsonObject getStatistics(String ticker) throws IOException {
		final String url = statQuery.replace("<<TICKER>>", ticker);
		
		System.out.println(url);
		final var resp = conn.get(url).execute();
		final var body = resp.body();
		final int code = resp.code();
		
		if (code != 200 && code != 404) {
			System.err.println("ERROR: " + code + " for ticker " + ticker);
			System.err.println(body);
		}
		
		return body;
	}
		
	public List<String> getAllTickers() throws IOException {
		final var json = JsonParser.parseString(allTickersBody).getAsJsonObject();
		final var resp = conn.post(allTickersQuery, json).execute().body();

		final List<String> tickers = new ArrayList<String>(ESTIMATED_DB_SIZE);
		
		clearIdle();
		
		for (JsonElement e : resp)
			tickers.add(e.getAsJsonObject().get("symbolTicker").getAsString().replace("\\.", "-"));
		
		return tickers;
	}
	
	private void clearIdle() {
		client.connectionPool().evictAll();
	}

	@Override
	public void close() throws IOException {
		client.connectionPool().evictAll();
		client.dispatcher().executorService().shutdown();
	}
	
	public JsonObject getIncome(String ticker) throws IOException {
		final String url = incomeQuery.replace("<<TICKER>>", ticker).replace("<<TIMESTAMP>>", "" + (System.currentTimeMillis() / 1000));
		
		return conn.get(url).execute().body();
	}

//	private JsonObject getCashFlow(String ticker) throws IOException {
//		final String url = cashFlowQuery.replace("<<TICKER>>", ticker).replace("<<TIMESTAMP>>", "" + (System.currentTimeMillis() / 1000));
//		
//		return conn.get(url).execute().body();
//	}
}