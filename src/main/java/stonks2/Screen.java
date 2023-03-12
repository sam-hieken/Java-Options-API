package stonks2;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Used to create a new screen for stocks.
 * 
 * Use {@link #screenTickers(List, Filter[])} to create a new Screen
 * @author Sam
 *
 */
public class Screen {

	private static final Random r = new Random();
	/**
	 * Screen tickers (see {@link #screenTickers(List, Filter[], FinConnector)}) 
	 * with a newly generated, default SimFin connector.
	 */
	public static List<ScreenEntry> screenTickers(List<String> tickers, Filter...filters) throws IOException {
		return screenTickers(tickers, filters, new YahooConnector());
	}
	
	/**
	 * Screen the tickers in a list.
	 * 
	 * @param tickers The list to screen
	 * @param filters Filters for screening
	 * @param conn The connector to SimFin
	 * @return The list of screened tickers
	 * @throws IOException
	 */
	public static List<ScreenEntry> screenTickers(List<String> tickers, Filter[] filters, YahooConnector conn) 
			throws IOException {
		
		try {
			TimeUnit.MILLISECONDS.sleep(r.nextInt(4000));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		final List<ScreenEntry> foundTickers = new LinkedList<>();
		
		final var tickerMap = new HashMap<String, JsonObject>();
		JsonArray stats = null;
		
		for (;;) {
			try {
				stats = conn.getStatistics(tickers)
						.get("quoteResponse").getAsJsonObject()
						.get("result").getAsJsonArray();
				
				break;
			} catch (SocketTimeoutException e) {
				System.err.println("----------------------\nBAD SOCKET\n----------------------");
			}
		}
		
		for (JsonElement e : stats) {
			final JsonObject obj = e.getAsJsonObject();
			tickerMap.put(obj.get("symbol").getAsString(), obj);
		}
		
		// Spread out each thread's execution a little bit so we're not making big globs of requests
		try {
			TimeUnit.MILLISECONDS.sleep(r.nextInt(5000));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < tickers.size(); i++) {
			String ticker = tickers.get(i);
			Company c = null;
			try {
				c = conn.getCompany(ticker, tickerMap.get(ticker));
			} catch (SocketTimeoutException e) {
				i--;
				System.err.println("-----------\nSOCKET ERROR\n-----------");
				continue;
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			
			applyFilters(foundTickers, filters, c, conn);
		}
		
		conn.close();
		
		return foundTickers;
	}
	
	/**
	 * Apply the filters passed to {@link #screenTickers(List, Filter[])}
	 * @param foundTickers The list to add tickers that were found into
	 * @param filters The filters to use in the screen
	 * @param c The company to apply the filters to
	 * @param p The period to get the company's data from
	 * @param conn The connector to SimFin
	 * @throws IOException
	 */
	private static void applyFilters(List<ScreenEntry> foundTickers, Filter[] filters, 
			Company c, YahooConnector conn) throws IOException {
		
		if (c == null) return;
		
		ScreenEntry entry;
		
		try {
			entry = new ScreenEntry(c);
		} catch (NullPointerException e) {
			return;
		}
		
		for (Filter f : filters) {
			try {
				// If the stock doesn't survive a filter, immediately exit.
				if (!f.filter(entry, conn)) {
					return;
				}
				
			// Missing data for the stock, do not include them	
			} catch (NullPointerException e) {
				return;
			}
		}
		
		// Stock survived all filters, and will now be added
		System.out.println("Survived Filter: " + c.getTicker() + "\t" + c);
		foundTickers.add(entry);
	}
}
