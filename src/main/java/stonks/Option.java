package stonks;

import java.io.IOException;
import java.text.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Response;

public class Option extends Security {
	private static final Gson gson = new Gson();
	private static final Connector conn = new Connector(Security.DEFAULT_KEYS);
	
	private JsonElement greeks;
	
	private String root_symbol;
	private String option_type;
	private String expiration_date;
	
	private double strike;
	
	public int open_interest;

	private Option() {}
	
	/**
	 * Get information about option, 15m delay
	 * @param ticker Ticker
	 * @param oType Type of option
	 * @param expr Expiration date
	 * @param strike Strike
	 * @param conn Custom Connector
	 * @return The option
	 * @throws IOException
	 * @throws StonkException If the option couldn't be found or parsed properly
	 */
	public static Option getOption(String ticker, OptionType oType, Date expr, double strike, Connector conn) throws IOException, StonkException {
		return getOption(ticker, oType, expr.toString(), strike, conn);
	}
	
	/**
	 * Get information about option, 15m delay
	 * @param ticker Ticker
	 * @param oType Type of option
	 * @param expr Expiration date
	 * @param strike Strike
	 * @return The option
	 * @throws IOException If bad connection occurs
	 * @throws StonkException If the option couldn't be found or parsed properly
	 */
	public static Option getOption(String ticker, OptionType oType, Date expr, double strike) throws StonkException, IOException {
		return getOption(ticker, oType, expr.toString(), strike, conn);
	}

	private static Option getOption(String ticker, OptionType oType, String expr, double strike, Connector conn) throws StonkException, IOException {
		Response<JsonObject> r = conn.conn.getOptions(conn.nextHeader(), oType.toString(), ticker, expr).execute();
		
		JsonArray oList = null;
		
		try {
			oList = r.body().get("options").getAsJsonObject().get("option").getAsJsonArray();
		}
		catch(IllegalStateException e) {
			throw new StonkException("Option could not be located.");
		}
		
		JsonObject option = null;
		
		for(JsonElement e : oList) {
			JsonObject jo = e.getAsJsonObject();
			if(jo.get("strike").getAsDouble() == strike) option = jo;
		}
		
		conn.ok.connectionPool().evictAll();
		
		return gson.fromJson(option, Option.class);
	}
	
	/**
	 * Gets all of the options of a particular expiration date at each strike price
	 * @param ticker
	 * @param oType
	 * @param expr
	 * @throws IOException 
	 */
	public static Option[] getOptions(String ticker, OptionType oType, Date expr) throws IOException {
		return getOptions(ticker, oType, expr, conn);
	}
	
	/**
	 * Gets all of the options of a particular expiration date at each strike price
	 * @param ticker
	 * @param oType
	 * @param expr
	 * @throws IOException 
	 */
	public static Option[] getOptions(String ticker, OptionType oType, Date expr, Connector conn) throws IOException {
		Response<JsonObject> r = conn.conn.getOptions(conn.nextHeader(), oType.toString(), ticker, expr.toString()).execute();
		
		JsonArray oList = null;
		
		try {
			oList = r.body().get("options").getAsJsonObject().get("option").getAsJsonArray();
		}
		catch(IllegalStateException e) {
			return null;
		}
		catch(Exception e) {
			System.err.println(r.code());
			return null;
		}
		
		Option[] options = new Option[oList.size()];
		
		for(int i = 0; i != options.length; i++) {
			options[i] = gson.fromJson(oList.get(i), Option.class);
		}

		conn.ok.connectionPool().evictAll();
		
		return options;
	}
	
	/**
	 * Get list of strike prices
	 * @param symbol Ticker
	 * @param expiration Expr date
	 * @return double array of strike prices
	 * @throws IOException
	 */
	public static double[] getStrikes(String symbol, Date expiration) throws IOException {		
		return getStrikes(symbol, expiration, conn);
	}
	
	/**
	 * Get list of strike prices
	 * @param symbol Ticker
	 * @param expiration Expr date
	 * @param conn Custom connector
	 * @return double array of strike prices
	 * @throws IOException
	 */
	public static double[] getStrikes(String symbol, Date expiration, Connector conn) throws IOException {		
		JsonArray boobs = conn.conn.getStrikes(conn.nextHeader(), symbol, expiration.toString())
							.execute().body().getAsJsonObject().get("strikes").getAsJsonObject()
							.get("strike").getAsJsonArray();
		
		double[] strikes = new double[boobs.size()];
		
		for(int i = 0; i != strikes.length; i++) {
			strikes[i] = boobs.get(i).getAsDouble();
		}

		conn.ok.connectionPool().evictAll();
		
		return strikes;
	}
	
	/**
	 * Every current expiration date for ticker
	 * @param symbol Ticker
	 * @return {@link Date} array of expirations
	 * @throws IOException
	 */
	public static Date[] getExpirations(String symbol) throws IOException {		
		return getExpirations(symbol, conn);
	}
	
	/**
	 * Every current expiration date for ticker
	 * @param symbol Ticker
	 * @return {@link Date} array of expirations
	 * @param conn Custom connector
	 * @throws IOException
	 */
	public static Date[] getExpirations(String symbol, Connector conn) throws IOException {		
		JsonElement tits = null;
		
		try {
			tits = conn.conn.getExprs(conn.nextHeader(), symbol)
			  .execute().body().getAsJsonObject().get("expirations").getAsJsonObject()
			  .get("date").getAsJsonArray();
		}
		catch(IllegalStateException e) {
			try {
				return new Date[] {
						Date.toDate(conn.conn.getExprs(conn.nextHeader(), symbol)
					  .execute().body().getAsJsonObject().get("expirations").getAsJsonObject()
					  .get("date").getAsString())
				};
			}
			catch(IllegalStateException _e) {
				_e.printStackTrace();
				return null;
			} 
			catch (ParseException e1) {
				e1.printStackTrace();
				return null;
			}
		}
		
		JsonArray boobs = tits.getAsJsonArray();
		
		Date[] strikes = new Date[boobs.size()];
		
		for(int i = 0; i != strikes.length; i++) {
			try {
				strikes[i] = Date.toDate(boobs.get(i).getAsString());
			} 
			catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		conn.ok.connectionPool().evictAll();
		
		return strikes;
	}

	public String getStock() {
		return root_symbol;
	}

	public OptionType getOptionType() {
		return OptionType.fromString(option_type);
	}

	public String getExpiration() {
		return expiration_date;
	}

	public double getStrike() {
		return strike;
	}
	
	public double getPremium() {
		return last == last && last != 0 ? last : Util.roundAvoid(((ask - bid) / 2) + bid, 2);
	}

	public double getGreek(Greek greek) {
		return greeks.isJsonObject() ? greeks.getAsJsonObject().get(greek.toString()).getAsDouble() : Math.sqrt(-1);
	}
	
	public double getIV() {
		if(!greeks.isJsonObject()) return Double.NaN;
		
		double bidIV = greeks.getAsJsonObject().get("bid_iv").getAsDouble();
		double askIV = greeks.getAsJsonObject().get("ask_iv").getAsDouble();
		
		if(bidIV == 0) return askIV;
		if(askIV == 0) return bidIV;
		
		return greeks.getAsJsonObject().get("mid_iv").getAsDouble();
	}
	
	public int getOpenInterest() {
		return open_interest;
	}
	
	/**
	 * Gets a {@link Quote} array with every price logged of this option between the start & end dates
	 * @param start Start date
	 * @param end End date
	 * @return Array of prices
	 * @throws IOException
	 */
	public Quote[] getHistoricalQuotes(Date start, Date end) throws IOException {
		return getHistoricalQuotes(start.toString(), end.toString(), conn);
	}
	
	/**
	 * Gets a {@link Quote} array with every price logged of this option between the start & end dates
	 * @param start Start date
	 * @param end End date
	 * @param conn Custom connector
	 * @return Array of prices
	 * @throws IOException
	 */
	public Quote[] getHistoricalQuotes(Date start, Date end, Connector conn) throws IOException {
		return getHistoricalQuotes(start.toString(), end.toString(), conn);
	}
	
	private Quote[] getHistoricalQuotes(String start, String end, Connector conn) throws IOException {		
		JsonElement base;
		JsonArray jray = null;
		
		try{
			base = conn.conn.getHistory(conn.nextHeader(), "daily", symbol, start, end).execute().body().get("history").getAsJsonObject().get("day"); 
		}
		catch(IllegalStateException e){
			return null;
		}
		
		try {
			jray = base.getAsJsonArray();
		}
		catch(IllegalStateException e) {
			return new Quote[] {gson.fromJson(base.getAsJsonObject(), Quote.class)};
		}
		
		Quote[] prices = new Quote[jray.size()];
		
		for(int i = 0; i < prices.length; i++) {
			prices[i] = gson.fromJson(jray.get(i).getAsJsonObject(), Quote.class);
			prices[i].description = description;
		}

		conn.ok.connectionPool().evictAll();
		
		return prices;
	}
	
	/**
	 * Gets this option as a quote
	 * @return This option as a quote
	 */
	public Quote getQuote() {
		Quote q = gson.fromJson(gson.toJson(this, Option.class), Quote.class);
		q.description = description;
		return q;
	}
}
