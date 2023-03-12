package stonks2;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;

public class Main {
	private static final int nthreads = 10;
	
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, RateLimitException {
		scanAllTickers();

//		List<ScreenEntry> ls = readTickers("./tickers.txt");
//	
//		Collections.sort(ls, (e1, e2) -> {
//			if (e1.getFcfYield() == null) return -1;
//			else if (e2.getFcfYield() == null) return 1;
//			
//			return e1.getFcfYield() > e2.getFcfYield() ? -1 : 1;
//		});
//		
//		System.out.println("Ticker,FCF Yield,FCF Margin,Price to Sales,Market Cap,Sector,Industry,Free Cash Flow,Revenue,Net Income");
//		
//		
//		
//		for (var e : ls) {
//			if (e.getSector() == Sector.TECHNOLOGY) {
//				final Company c = e.getCompany();
//				
//				System.out.println(e.getTicker() + ","
//						+ e.getFcfYield() + "," 
//						+ e.getFcfMargin() + ","
//						+ e.getPriceToSales() + ","
//						+ e.getCompany().getMarketCap().getAmt() + ","
//						+ e.getSector() + ","
//						+ e.getIndustry() + ","
//						+ c.getCashFlow().getFreeCF().getAmt() + ","
//						+ c.getIncome().getRevenue().getAmt() + ","
//						+ c.getIncome().getNetIncome().getAmt());
//			}
//		}
	}
	
	public static void scanAllTickers() throws IOException, InterruptedException {
		Filter[] filters = {
			Filters.checkFCFYield, Filters.desiredSector
		};
		
		// List of lists containing all the tickers in the database
		List<List<String>> allTickers = separateTickers(nthreads);
		
		// Empty list where all found tickers are placed
		List<ScreenEntry> foundTickers = Collections.synchronizedList(
				new ArrayList<ScreenEntry>());
		
		List<Thread> threads = new ArrayList<Thread>(nthreads);
		
		for (List<String> search : allTickers) {
			Thread t = newScreeningThread(foundTickers, search, filters);
			t.start();
			threads.add(t);
		}
		
		for (Thread t : threads) t.join();
		
		System.out.println("Screen complete, writing found tickers to file...");
		writeTickers(foundTickers);
	}
	
	/**
	 * Creates a new thread for screening tickers with {@link #screenTickers(List, Filter[])}
	 * @param foundTickers The SYNCHRONIZED list to put tickers that were found into
	 * @param search The list to search through
	 * @param filters
	 * @return
	 */
	private static Thread newScreeningThread(List<ScreenEntry> foundTickers, List<String> search, Filter[] filters) {
		return new Thread() {
			public void run() {
				try {
					foundTickers.addAll(Screen.screenTickers(search, filters));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	/**
	 * Retrieve all tickers and separate them into n lists
	 * @param n The number of sublists to separate into
	 * @return The list of sublists of tickers
	 * @throws IOException
	 */
	private static List<List<String>> separateTickers(int n) throws IOException {
		final YahooConnector conn = new YahooConnector();
		final List<String> allTickers = conn.getAllTickers();
		
		conn.close();
		
		final int size = allTickers.size();
		final int elemPer = size / n;
		
		List<List<String>> list = new ArrayList<List<String>>(n);
		
		for(int i = 0; i != n; i++) {
			list.add(
				allTickers.subList(i * elemPer, (i * elemPer + elemPer) + (i == n - 1 ? size % n : 0))
			);
		}
		
		return list;
	}
	
	
	
	static void writeTickers(List<ScreenEntry> tickers) throws FileNotFoundException, IOException {
		final FileWriter fw = new FileWriter("./tickers.txt");
		
		for (ScreenEntry e : tickers) {
			new Gson().toJson(e, fw);
			fw.append(",\n");
		}
		
		fw.flush();
		fw.close();
	}
	
	@SuppressWarnings("unchecked")
	static List<ScreenEntry> readTickers(String fileName) throws ClassNotFoundException, IOException {
		ScreenEntry[] arr = new Gson().fromJson(new FileReader(fileName), ScreenEntry[].class);
	    return Arrays.asList(arr);
	}
}