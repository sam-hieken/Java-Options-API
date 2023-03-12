package stonks2;

import java.io.IOException;

public interface Filter {
	/**
	 * Filters a stock.
	 * @param c Company object for stock to check.
	 * @param p The period to check.
	 * @param conn The connector to SimFin
	 * @return False if the stock didn't survive the filter, true if it did.
	 * @throws IOException
	 */
	public boolean filter(ScreenEntry c, YahooConnector conn) throws IOException;
}
