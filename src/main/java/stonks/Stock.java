package stonks;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Response;

public class Stock extends Security {
	private static final Connector conn = new Connector();
	private static final Gson gson = new Gson();
	
	/**
	 * Get information about a stock
	 * @param ticker the stock's ticker
	 * @return the stock
	 * @throws IOException
	 */
	public static Stock getStock(String ticker) throws IOException {
		Response<JsonObject> r = conn.conn.getQuotes(conn.nextHeader(), ticker, "false").execute();
		
		JsonObject quote = null;
		
		try {
			quote = r.body().get("quotes").getAsJsonObject().get("quote").getAsJsonObject();
		}
		catch(IllegalStateException e) {
			return null;
		}
		
		conn.ok.connectionPool().evictAll();
		
		return gson.fromJson(quote, Stock.class);
	}
	
	/**
	 * Gets this stock as a quote
	 * @return This stock as a quote
	 */
	public Quote getQuote() {
		Quote q = gson.fromJson(gson.toJson(this, Stock.class), Quote.class);
		q.description = description;
		return q;
	}
}
