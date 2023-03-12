package stonks2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CSVUtil {
	private static final List<String> queriesList = List.of(
			// INCOME
			"annualTaxEffectOfUnusualItems","trailingTaxEffectOfUnusualItems","annualTaxRateForCalcs","trailingTaxRateForCalcs","annualNormalizedEBITDA","trailingNormalizedEBITDA","annualNormalizedDilutedEPS","trailingNormalizedDilutedEPS","annualNormalizedBasicEPS","trailingNormalizedBasicEPS","annualTotalUnusualItems","trailingTotalUnusualItems","annualTotalUnusualItemsExcludingGoodwill","trailingTotalUnusualItemsExcludingGoodwill","annualNetIncomeFromContinuingOperationNetMinorityInterest","trailingNetIncomeFromContinuingOperationNetMinorityInterest","annualReconciledDepreciation","trailingReconciledDepreciation","annualReconciledCostOfRevenue","trailingReconciledCostOfRevenue","annualEBITDA","trailingEBITDA","annualEBIT","trailingEBIT","annualNetInterestIncome","trailingNetInterestIncome","annualInterestExpense","trailingInterestExpense","annualInterestIncome","trailingInterestIncome","annualContinuingAndDiscontinuedDilutedEPS","trailingContinuingAndDiscontinuedDilutedEPS","annualContinuingAndDiscontinuedBasicEPS","trailingContinuingAndDiscontinuedBasicEPS","annualNormalizedIncome","trailingNormalizedIncome","annualNetIncomeFromContinuingAndDiscontinuedOperation","trailingNetIncomeFromContinuingAndDiscontinuedOperation","annualTotalExpenses","trailingTotalExpenses","annualRentExpenseSupplemental","trailingRentExpenseSupplemental","annualReportedNormalizedDilutedEPS","trailingReportedNormalizedDilutedEPS","annualReportedNormalizedBasicEPS","trailingReportedNormalizedBasicEPS","annualTotalOperatingIncomeAsReported","trailingTotalOperatingIncomeAsReported","annualDividendPerShare","trailingDividendPerShare","annualDilutedAverageShares","trailingDilutedAverageShares","annualBasicAverageShares","trailingBasicAverageShares","annualDilutedEPS","trailingDilutedEPS","annualDilutedEPSOtherGainsLosses","trailingDilutedEPSOtherGainsLosses","annualTaxLossCarryforwardDilutedEPS","trailingTaxLossCarryforwardDilutedEPS","annualDilutedAccountingChange","trailingDilutedAccountingChange","annualDilutedExtraordinary","trailingDilutedExtraordinary","annualDilutedDiscontinuousOperations","trailingDilutedDiscontinuousOperations","annualDilutedContinuousOperations","trailingDilutedContinuousOperations","annualBasicEPS","trailingBasicEPS","annualBasicEPSOtherGainsLosses","trailingBasicEPSOtherGainsLosses","annualTaxLossCarryforwardBasicEPS","trailingTaxLossCarryforwardBasicEPS","annualBasicAccountingChange","trailingBasicAccountingChange","annualBasicExtraordinary","trailingBasicExtraordinary","annualBasicDiscontinuousOperations","trailingBasicDiscontinuousOperations","annualBasicContinuousOperations","trailingBasicContinuousOperations","annualDilutedNIAvailtoComStockholders","trailingDilutedNIAvailtoComStockholders","annualAverageDilutionEarnings","trailingAverageDilutionEarnings","annualNetIncomeCommonStockholders","trailingNetIncomeCommonStockholders","annualOtherunderPreferredStockDividend","trailingOtherunderPreferredStockDividend","annualPreferredStockDividends","trailingPreferredStockDividends","annualNetIncome","trailingNetIncome","annualMinorityInterests","trailingMinorityInterests","annualNetIncomeIncludingNoncontrollingInterests","trailingNetIncomeIncludingNoncontrollingInterests","annualNetIncomeFromTaxLossCarryforward","trailingNetIncomeFromTaxLossCarryforward","annualNetIncomeExtraordinary","trailingNetIncomeExtraordinary","annualNetIncomeDiscontinuousOperations","trailingNetIncomeDiscontinuousOperations","annualNetIncomeContinuousOperations","trailingNetIncomeContinuousOperations","annualEarningsFromEquityInterestNetOfTax","trailingEarningsFromEquityInterestNetOfTax","annualTaxProvision","trailingTaxProvision","annualPretaxIncome","trailingPretaxIncome","annualOtherIncomeExpense","trailingOtherIncomeExpense","annualOtherNonOperatingIncomeExpenses","trailingOtherNonOperatingIncomeExpenses","annualSpecialIncomeCharges","trailingSpecialIncomeCharges","annualGainOnSaleOfPPE","trailingGainOnSaleOfPPE","annualGainOnSaleOfBusiness","trailingGainOnSaleOfBusiness","annualOtherSpecialCharges","trailingOtherSpecialCharges","annualWriteOff","trailingWriteOff","annualImpairmentOfCapitalAssets","trailingImpairmentOfCapitalAssets","annualRestructuringAndMergernAcquisition","trailingRestructuringAndMergernAcquisition","annualSecuritiesAmortization","trailingSecuritiesAmortization","annualEarningsFromEquityInterest","trailingEarningsFromEquityInterest","annualGainOnSaleOfSecurity","trailingGainOnSaleOfSecurity","annualNetNonOperatingInterestIncomeExpense","trailingNetNonOperatingInterestIncomeExpense","annualTotalOtherFinanceCost","trailingTotalOtherFinanceCost","annualInterestExpenseNonOperating","trailingInterestExpenseNonOperating","annualInterestIncomeNonOperating","trailingInterestIncomeNonOperating","annualOperatingIncome","trailingOperatingIncome","annualOperatingExpense","trailingOperatingExpense","annualOtherOperatingExpenses","trailingOtherOperatingExpenses","annualOtherTaxes","trailingOtherTaxes","annualProvisionForDoubtfulAccounts","trailingProvisionForDoubtfulAccounts","annualDepreciationAmortizationDepletionIncomeStatement","trailingDepreciationAmortizationDepletionIncomeStatement","annualDepletionIncomeStatement","trailingDepletionIncomeStatement","annualDepreciationAndAmortizationInIncomeStatement","trailingDepreciationAndAmortizationInIncomeStatement","annualAmortization","trailingAmortization","annualAmortizationOfIntangiblesIncomeStatement","trailingAmortizationOfIntangiblesIncomeStatement","annualDepreciationIncomeStatement","trailingDepreciationIncomeStatement","annualResearchAndDevelopment","trailingResearchAndDevelopment","annualSellingGeneralAndAdministration","trailingSellingGeneralAndAdministration","annualSellingAndMarketingExpense","trailingSellingAndMarketingExpense","annualGeneralAndAdministrativeExpense","trailingGeneralAndAdministrativeExpense","annualOtherGandA","trailingOtherGandA","annualInsuranceAndClaims","trailingInsuranceAndClaims","annualRentAndLandingFees","trailingRentAndLandingFees","annualSalariesAndWages","trailingSalariesAndWages","annualGrossProfit","trailingGrossProfit","annualCostOfRevenue","trailingCostOfRevenue","annualTotalRevenue","trailingTotalRevenue","annualExciseTaxes","trailingExciseTaxes","annualOperatingRevenue","trailingOperatingRevenue",
			
			// CASH FLOW
			"annualForeignSales","trailingForeignSales","annualDomesticSales","trailingDomesticSales","annualAdjustedGeographySegmentData","trailingAdjustedGeographySegmentData","annualFreeCashFlow","trailingFreeCashFlow","annualRepurchaseOfCapitalStock","trailingRepurchaseOfCapitalStock","annualRepaymentOfDebt","trailingRepaymentOfDebt","annualIssuanceOfDebt","trailingIssuanceOfDebt","annualIssuanceOfCapitalStock","trailingIssuanceOfCapitalStock","annualCapitalExpenditure","trailingCapitalExpenditure","annualInterestPaidSupplementalData","trailingInterestPaidSupplementalData","annualIncomeTaxPaidSupplementalData","trailingIncomeTaxPaidSupplementalData","annualEndCashPosition","trailingEndCashPosition","annualOtherCashAdjustmentOutsideChangeinCash","trailingOtherCashAdjustmentOutsideChangeinCash","annualBeginningCashPosition","trailingBeginningCashPosition","annualEffectOfExchangeRateChanges","trailingEffectOfExchangeRateChanges","annualChangesInCash","trailingChangesInCash","annualOtherCashAdjustmentInsideChangeinCash","trailingOtherCashAdjustmentInsideChangeinCash","annualCashFlowFromDiscontinuedOperation","trailingCashFlowFromDiscontinuedOperation","annualFinancingCashFlow","trailingFinancingCashFlow","annualCashFromDiscontinuedFinancingActivities","trailingCashFromDiscontinuedFinancingActivities","annualCashFlowFromContinuingFinancingActivities","trailingCashFlowFromContinuingFinancingActivities","annualNetOtherFinancingCharges","trailingNetOtherFinancingCharges","annualInterestPaidCFF","trailingInterestPaidCFF","annualProceedsFromStockOptionExercised","trailingProceedsFromStockOptionExercised","annualCashDividendsPaid","trailingCashDividendsPaid","annualPreferredStockDividendPaid","trailingPreferredStockDividendPaid","annualCommonStockDividendPaid","trailingCommonStockDividendPaid","annualNetPreferredStockIssuance","trailingNetPreferredStockIssuance","annualPreferredStockPayments","trailingPreferredStockPayments","annualPreferredStockIssuance","trailingPreferredStockIssuance","annualNetCommonStockIssuance","trailingNetCommonStockIssuance","annualCommonStockPayments","trailingCommonStockPayments","annualCommonStockIssuance","trailingCommonStockIssuance","annualNetIssuancePaymentsOfDebt","trailingNetIssuancePaymentsOfDebt","annualNetShortTermDebtIssuance","trailingNetShortTermDebtIssuance","annualShortTermDebtPayments","trailingShortTermDebtPayments","annualShortTermDebtIssuance","trailingShortTermDebtIssuance","annualNetLongTermDebtIssuance","trailingNetLongTermDebtIssuance","annualLongTermDebtPayments","trailingLongTermDebtPayments","annualLongTermDebtIssuance","trailingLongTermDebtIssuance","annualInvestingCashFlow","trailingInvestingCashFlow","annualCashFromDiscontinuedInvestingActivities","trailingCashFromDiscontinuedInvestingActivities","annualCashFlowFromContinuingInvestingActivities","trailingCashFlowFromContinuingInvestingActivities","annualNetOtherInvestingChanges","trailingNetOtherInvestingChanges","annualInterestReceivedCFI","trailingInterestReceivedCFI","annualDividendsReceivedCFI","trailingDividendsReceivedCFI","annualNetInvestmentPurchaseAndSale","trailingNetInvestmentPurchaseAndSale","annualSaleOfInvestment","trailingSaleOfInvestment","annualPurchaseOfInvestment","trailingPurchaseOfInvestment","annualNetInvestmentPropertiesPurchaseAndSale","trailingNetInvestmentPropertiesPurchaseAndSale","annualSaleOfInvestmentProperties","trailingSaleOfInvestmentProperties","annualPurchaseOfInvestmentProperties","trailingPurchaseOfInvestmentProperties","annualNetBusinessPurchaseAndSale","trailingNetBusinessPurchaseAndSale","annualSaleOfBusiness","trailingSaleOfBusiness","annualPurchaseOfBusiness","trailingPurchaseOfBusiness","annualNetIntangiblesPurchaseAndSale","trailingNetIntangiblesPurchaseAndSale","annualSaleOfIntangibles","trailingSaleOfIntangibles","annualPurchaseOfIntangibles","trailingPurchaseOfIntangibles","annualNetPPEPurchaseAndSale","trailingNetPPEPurchaseAndSale","annualSaleOfPPE","trailingSaleOfPPE","annualPurchaseOfPPE","trailingPurchaseOfPPE","annualCapitalExpenditureReported","trailingCapitalExpenditureReported","annualOperatingCashFlow","trailingOperatingCashFlow","annualCashFromDiscontinuedOperatingActivities","trailingCashFromDiscontinuedOperatingActivities","annualCashFlowFromContinuingOperatingActivities","trailingCashFlowFromContinuingOperatingActivities","annualTaxesRefundPaid","trailingTaxesRefundPaid","annualInterestReceivedCFO","trailingInterestReceivedCFO","annualInterestPaidCFO","trailingInterestPaidCFO","annualDividendReceivedCFO","trailingDividendReceivedCFO","annualDividendPaidCFO","trailingDividendPaidCFO","annualChangeInWorkingCapital","trailingChangeInWorkingCapital","annualChangeInOtherWorkingCapital","trailingChangeInOtherWorkingCapital","annualChangeInOtherCurrentLiabilities","trailingChangeInOtherCurrentLiabilities","annualChangeInOtherCurrentAssets","trailingChangeInOtherCurrentAssets","annualChangeInPayablesAndAccruedExpense","trailingChangeInPayablesAndAccruedExpense","annualChangeInAccruedExpense","trailingChangeInAccruedExpense","annualChangeInInterestPayable","trailingChangeInInterestPayable","annualChangeInPayable","trailingChangeInPayable","annualChangeInDividendPayable","trailingChangeInDividendPayable","annualChangeInAccountPayable","trailingChangeInAccountPayable","annualChangeInTaxPayable","trailingChangeInTaxPayable","annualChangeInIncomeTaxPayable","trailingChangeInIncomeTaxPayable","annualChangeInPrepaidAssets","trailingChangeInPrepaidAssets","annualChangeInInventory","trailingChangeInInventory","annualChangeInReceivables","trailingChangeInReceivables","annualChangesInAccountReceivables","trailingChangesInAccountReceivables","annualOtherNonCashItems","trailingOtherNonCashItems","annualExcessTaxBenefitFromStockBasedCompensation","trailingExcessTaxBenefitFromStockBasedCompensation","annualStockBasedCompensation","trailingStockBasedCompensation","annualUnrealizedGainLossOnInvestmentSecurities","trailingUnrealizedGainLossOnInvestmentSecurities","annualProvisionandWriteOffofAssets","trailingProvisionandWriteOffofAssets","annualAssetImpairmentCharge","trailingAssetImpairmentCharge","annualAmortizationOfSecurities","trailingAmortizationOfSecurities","annualDeferredTax","trailingDeferredTax","annualDeferredIncomeTax","trailingDeferredIncomeTax","annualDepreciationAmortizationDepletion","trailingDepreciationAmortizationDepletion","annualDepletion","trailingDepletion","annualDepreciationAndAmortization","trailingDepreciationAndAmortization","annualAmortizationCashFlow","trailingAmortizationCashFlow","annualAmortizationOfIntangibles","trailingAmortizationOfIntangibles","annualDepreciation","trailingDepreciation","annualOperatingGainsLosses","trailingOperatingGainsLosses","annualPensionAndEmployeeBenefitExpense","trailingPensionAndEmployeeBenefitExpense","annualEarningsLossesFromEquityInvestments","trailingEarningsLossesFromEquityInvestments","annualGainLossOnInvestmentSecurities","trailingGainLossOnInvestmentSecurities","annualNetForeignCurrencyExchangeGainLoss","trailingNetForeignCurrencyExchangeGainLoss","annualGainLossOnSaleOfPPE","trailingGainLossOnSaleOfPPE","annualGainLossOnSaleOfBusiness","trailingGainLossOnSaleOfBusiness","annualNetIncomeFromContinuingOperations","trailingNetIncomeFromContinuingOperations","annualCashFlowsfromusedinOperatingActivitiesDirect","trailingCashFlowsfromusedinOperatingActivitiesDirect","annualTaxesRefundPaidDirect","trailingTaxesRefundPaidDirect","annualInterestReceivedDirect","trailingInterestReceivedDirect","annualInterestPaidDirect","trailingInterestPaidDirect","annualDividendsReceivedDirect","trailingDividendsReceivedDirect","annualDividendsPaidDirect","trailingDividendsPaidDirect","annualClassesofCashPayments","trailingClassesofCashPayments","annualOtherCashPaymentsfromOperatingActivities","trailingOtherCashPaymentsfromOperatingActivities","annualPaymentsonBehalfofEmployees","trailingPaymentsonBehalfofEmployees","annualPaymentstoSuppliersforGoodsandServices","trailingPaymentstoSuppliersforGoodsandServices","annualClassesofCashReceiptsfromOperatingActivities","trailingClassesofCashReceiptsfromOperatingActivities","annualOtherCashReceiptsfromOperatingActivities","trailingOtherCashReceiptsfromOperatingActivities","annualReceiptsfromGovernmentGrants","trailingReceiptsfromGovernmentGrants","annualReceiptsfromCustomers","trailingReceiptsfromCustomers"
			
		);
	
	public static void main(String[] args) throws IOException {
		
		String ticker = null;
		String[] params = null;
		boolean ttm = true;
		
		for (String arg : args) {
			if (arg.startsWith("-ttm="))
				ttm = Boolean.parseBoolean(arg.replace("-ttm=", ""));
			
			else if (arg.startsWith("-type="))
				params = arg.replace("-type=", "").split("\\,");
			
			else if (arg.startsWith("-tix="))
				ticker = arg.replace("-tix=", "");
		}
		
		JsonObject obj = getData(ticker, ttm, params);
		csvIfy(obj);
	}
	
	private static String[] csvIfy(JsonObject data) throws IOException {
		final FileWriter fw = new FileWriter(System.getProperty("user.home") + "/Desktop/output.csv");
		final var entrySet = data.entrySet();
		
		final String[] file = new String[entrySet.size() + 1];
		
		file[0] = "Date,";
		
		int line = 1;
		for (Entry<String, JsonElement> entry : entrySet) {
			file[line] = entry.getKey() + ",";
			
			final JsonArray fundamentals = entry.getValue().getAsJsonArray();
			
			for (int j = 0; j < file.length + 1; j++) {
				final JsonObject fundamental = fundamentals.get(j).getAsJsonObject();
				
				file[line] += fundamental.get("reportedValue").getAsJsonObject().get("raw").getAsLong() + ",";
				if (line == 1) {
					file[0] += fundamental.get("asOfDate").getAsString() + ",";
				}
			}
			
			line++;
		}
		
		for (String ln : file) 
			fw.write(ln + "\n");
		
		fw.close();
		
		return file;
	}
	
	private static JsonObject getData(String ticker, boolean ttm, String...types) throws IOException {
		final List<String> params = new ArrayList<String>(types.length * (ttm ? 2 : 1));
		final YahooConnector yoohoo = new YahooConnector();
		
		for (String type : types) {
			for (String query : queriesList) {
				if (!type.toLowerCase().startsWith(query.replaceAll("annual", "").toLowerCase()))
					continue;
				
				params.add(query);
				if (ttm) params.add(query.replace("annual", "trailing"));
			}
		}
		final String[] array = new String[params.size()];
		
		params.toArray(array);
		
		final var ret = yoohoo.getCustomData(ticker, array);
		
		yoohoo.close();
		return condense(ret, ttm);
	}
	
	private static JsonObject condense(JsonObject obj, boolean ttm) {
		final JsonObject ret = new JsonObject();
		
		final JsonArray result = obj.get("timeseries").getAsJsonObject()
									.get("result").getAsJsonArray();
		
		for (JsonElement e : result) {
			final var temp = e.getAsJsonObject();
			if (!temp.has("timestamp"))
				continue;
			
propLoop:	for (Entry<String, JsonElement> set : temp.entrySet()) {
				if (set.getKey().equals("timestamp") || set.getKey().equals("meta"))
					continue propLoop;
				
				final String prop = set.getKey();
				if (prop.startsWith("trailing"))
					ret.add(prop, temp.get(prop).getAsJsonArray().get(0));
				
				else ret.add(prop, temp.get(prop));
			}
		}
		
		return blurTrailing(ret, ttm);
	}
	
	private static JsonObject blurTrailing(JsonObject body, boolean ttm) {
		final JsonObject ret = new JsonObject();
		
		for (Entry<String, JsonElement> set : body.entrySet()) {
			final String propName = set.getKey();
			
			if (propName.startsWith("annual")) {
				final JsonArray annual = set.getValue().getAsJsonArray();
				
				if (ttm) {
					final var ttmDat = body.get(propName.replace("annual", "trailing")).getAsJsonObject();
					
					ttmDat.addProperty("asOfDate", "TTM");
					annual.add(ttmDat);
				}
				
				ret.add(propName.replace("annual", ""), annual);
			}
		}
		
		return ret;
	}
}
