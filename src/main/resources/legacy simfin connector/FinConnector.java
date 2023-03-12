package stonks.fundamental;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class FinConnector implements Closeable {
	
	// The number of companies in SimFin's database
	// does NOT have to be exact (used as initial size of ArrayList in getAllTickersInDB())
	public static final int ESTIMATED_SIZE_OF_DB = 3500;
	
	private static final Random r = new Random();
	
	private final PrintWriter fout;
	private final OkHttpClient ok;
	private final Retrofit rfit;
	private final FinConnect conn;
	
	private final String[] tokens;
	
	public byte nextToken;
	
	public FinConnector() {
		this(	
				"WiO5yKXIcC9gpigLZgmDBI2d8x5ieiFX",
				"gKaVtFjDV3SLPmaQsoX4pFY3Jnhangq3",
				"CznxIFJWv5x3Dtf7AgH3qpknCHjpLALJ",
				"5R4CP708hzdZZRyDL4hpJIpt484leZVV",
				"WgKdCgkojnOqJ5XSNeZcPjACXCKGh7nN",
				"7jbAh36Bldt8V6Nu7lRKRBCK0FiK5gaB",
				"cdhx9Yw5IVvLQlNvm0WoUIJkKR87scCn"
			);
	}
	
	public FinConnector(String...tokens) {
		this.nextToken = tokens.length == 0 ? 0 : (byte) r.nextInt(tokens.length);
		
		this.fout = new PrintWriter(System.err);
		this.ok = new OkHttpClient();
		this.rfit = new Retrofit.Builder().baseUrl("https://simfin.com/data/api/v2/")
				.client(ok)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		this.conn = rfit.create(FinConnect.class);
		
		this.tokens = tokens;
	}
	
	public FinConnector(File log, String...tokens) throws FileNotFoundException {
		this.fout = new PrintWriter(new FileOutputStream(log, true));

		this.nextToken = tokens.length == 0 ? 0 : (byte) r.nextInt(tokens.length);
		Interceptor interceptor = (chain) -> {
			Response r = chain.proceed(chain.request());
			//if((r.code() / 100) != 2) {
			fout.println(chain.request());
			fout.println(r);
			//}
			return r;
		};
		
		this.ok = new OkHttpClient.Builder().addInterceptor(interceptor).build();
		
		this.rfit = new Retrofit.Builder().baseUrl("https://simfin.com/data/api/v2/")
			.client(ok)
			.addConverterFactory(GsonConverterFactory.create())
			.build();
		this.conn = rfit.create(FinConnect.class);
		
		this.tokens = tokens;
	}
	
	private String nextToken() {
		nextToken++;
		if(nextToken > tokens.length - 1) nextToken = 0;
		return tokens[nextToken];
	}
	
	public IncomeStatement getProfitLoss(String ticker, Period period) throws IOException {
		return getProfitLoss(ticker, period.getYear(), period.getQuarter());
	}
	
	public IncomeStatement getProfitLoss(String ticker, int year, Quarter quarter) throws IOException {
		String token =  nextToken();
		JsonObject resp = conn.getProfitLoss(ticker, Integer.toString(year), quarter.toString(), token)
							.execute().body().get(0).getAsJsonObject();
		
		if (!resp.get("found").getAsBoolean()) return null;
		
		JsonArray keys = resp.get("columns").getAsJsonArray(),
				  values = resp.get("data").getAsJsonArray().get(0).getAsJsonArray();
		
		IncomeStatement base = new IncomeStatement(ticker);
		
		for (int i = 0; i < keys.size(); i++) {
			final JsonElement value = values.get(i);
			
			switch (keys.get(i).getAsString()) {
			
			case "Revenue": 
				base.setRevenue(getAsLong(value));
				break;
			
			case "Net Income": 
				base.setNetIncome(getAsLong(value));
				break;
				
			case "Operating Income (Loss)": 
				base.setOperatingIncome(getAsLong(value));
				break;
				
			case "Selling, General & Administrative": 
				base.setSGA(getAsLong(value));
				break;
				
			case "Research & Development": 
				base.setRND(getAsLong(value));
				break;
			
			}
		}
		
		return base;
	}
	
	public CashFlowStatement getCashFlowTTM(String ticker, Period period) throws IOException {
		CashFlowStatement[] cf = {
				getCashFlow(ticker, period), 	
				getCashFlow(ticker, period.lastPeriod()),
				getCashFlow(ticker, period.lastPeriod().lastPeriod()),
				getCashFlow(ticker, period.lastPeriod().lastPeriod().lastPeriod())
							};
		
		CashFlowStatement newCf = new CashFlowStatement(ticker);
		
		for (CashFlowStatement stmt : cf) {
			if (stmt == null) return null;
			
			newCf.operatingCF += stmt.getOperatingCF();
			newCf.capEx += stmt.getCapEx();
		}
		
		return newCf;
	}
	
	public CashFlowStatement getCashFlow(String ticker, Period period) throws IOException {
		return getCashFlow(ticker, period.getYear(), period.getQuarter());
	}
	
	public CashFlowStatement getCashFlow(String ticker, int year, Quarter quarter) throws IOException {
		JsonObject resp = conn.getCashFlow(ticker, Integer.toString(year), quarter.toString(), nextToken())
							.execute().body().get(0).getAsJsonObject();
		
		if (!resp.get("found").getAsBoolean()) return null;
		
		JsonArray keys = resp.get("columns").getAsJsonArray(),
				  values = resp.get("data").getAsJsonArray().get(0).getAsJsonArray();
		
		CashFlowStatement base = new CashFlowStatement(ticker);
		
		for (int i = 0; i < keys.size(); i++) {
			final JsonElement value = values.get(i);
			
			try {
				switch (keys.get(i).getAsString()) {
				
				case "Net Cash from Operating Activities": 
					base.setOperatingCF(getAsLong(value));
					break;
				
				case "Change in Fixed Assets & Intangibles": 
					base.setCapEx(getAsLong(value));
					break;
				
				}
			} catch (NullPointerException e) {
				return null;
			}
		}
		
		return base;
	}
	
	public BalanceSheet getBalanceSheet(String ticker, Period period) throws IOException {
		return getBalanceSheet(ticker, period.getYear(), period.getQuarter());
	}
	
	public BalanceSheet getBalanceSheet(String ticker, int year, Quarter quarter) throws IOException {
		JsonObject resp = conn.getBalanceSheet(ticker, Integer.toString(year), quarter.toString(), nextToken())
							.execute().body().get(0).getAsJsonObject();
		
		if (!resp.get("found").getAsBoolean()) return null;
		
		JsonArray keys = resp.get("columns").getAsJsonArray(),
				  values = resp.get("data").getAsJsonArray().get(0).getAsJsonArray();
		
		BalanceSheet base = new BalanceSheet(ticker);
		
		for (int i = 0; i < keys.size(); i++) {
			final JsonElement value = values.get(i);
			
			switch (keys.get(i).getAsString()) {
			
			case "Common Stock": 
				base.setCommonStock(value.getAsLong());
				break;
			
			}
		}
		
		return base;
	}
	
	public Company getCompanyInfo(String ticker) throws IOException {
		JsonObject resp = conn.getCompanyInfo(ticker, nextToken())
				.execute().body().get(0).getAsJsonObject();
		
		if (!resp.get("found").getAsBoolean()) return null;

		JsonArray keys = resp.get("columns").getAsJsonArray(),
				values = resp.get("data").getAsJsonArray();

		Company base = new Company(ticker);
		
		for (int i = 0; i < keys.size(); i++) {
			final JsonElement value = values.get(i);
			
			switch (keys.get(i).getAsString()) {
			
			case "Company Name": 
				base.setName(value.getAsString());
				break;
			
			case "IndustryId": 
				base.setIndustryID(value.isJsonNull() ? null : value.getAsInt());
				break;
			
			case "Month FY End":
				if (value.isJsonNull()) return null;
				base.setMonthFYEnd(value.getAsByte());
				break;
				
			case "Number Employees":
				base.setEmployeeCount(value.isJsonNull() ? null : value.getAsInt());
				break;
			}
		}
		
		JsonArray shareCountBase = conn.getCommonShares(ticker, nextToken()).execute().body()
									.get(0).getAsJsonObject().get("data").getAsJsonArray();
		
		if (shareCountBase.isEmpty())
			base.setSharesOutstanding(null);
		else {
			JsonArray shareCountInf = shareCountBase.get(shareCountBase.size() - 1).getAsJsonArray();
			base.setSharesOutstanding(shareCountInf.get(3).getAsLong());
		}
		
		return base;
		
	}
	
	public ArrayList<String> getAllTickersInDB() throws IOException {
		JsonArray values = conn.getCompaniesInDB(nextToken())
				.execute().body().get("data").getAsJsonArray();
		
		ArrayList<String> base = new ArrayList<String>(ESTIMATED_SIZE_OF_DB);
		
		for (int i = 0; i < values.size(); i++) {
			final String ticker = values.get(i).getAsJsonArray().get(1).getAsString();
			
			if (!ticker.endsWith("_delist") && !ticker.endsWith("_delis")) 
				base.add(ticker);
		}
		
		return base;
	}
	
	private Long getAsLong(JsonElement value) {
		return value.isJsonNull() ? null : value.getAsLong();
	}

	public void close() throws IOException {
		ok.connectionPool().evictAll();
		ok.dispatcher().executorService().shutdown();
		fout.close();
	}
}

interface FinConnect {
	@GET("/api/v2/companies/statements?statement=cf")
	Call<JsonArray> getCashFlow(@Query("ticker") String ticker,  @Query("fyear") String fullYear, @Query("period") String period, @Query("api-key") String apiKey);
	
	@GET("/api/v2/companies/statements?statement=pl")
	Call<JsonArray> getProfitLoss(@Query("ticker") String ticker,  @Query("fyear") String fullYear, @Query("period") String period, @Query("api-key") String apiKey);
	
	@GET("/api/v2/companies/statements?statement=bs")
	Call<JsonArray> getBalanceSheet(@Query("ticker") String ticker,  @Query("fyear") String fullYear, @Query("period") String period, @Query("api-key") String apiKey);
	
	@GET("/api/v2/companies/general")
	Call<JsonArray> getCompanyInfo(@Query("ticker") String ticker, @Query("api-key") String apiKey);
	
	@GET("/api/v2/companies/shares?type=common")
	Call<JsonArray> getCommonShares(@Query("ticker") String ticker, @Query("api-key") String apiKey);
	
	@GET("/api/v2/companies/list")
	Call<JsonObject> getCompaniesInDB(@Query("api-key") String apiKey);
}