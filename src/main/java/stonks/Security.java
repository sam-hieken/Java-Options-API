package stonks;

public abstract class Security {
	//	TODO Add your token(s) here
	public static final String[] DEFAULT_KEYS = {};
	//	TODO Add your token(s) here
			
	protected String symbol;
	protected String description;
	
	protected double open;
	protected double close;
	protected double high;
	protected double low;
	protected double bid;
	protected double ask;
	protected double last;
	
	protected int bidsize;
	protected int asksize;
	
	protected long volume;

	public String getSymbol() {
		return symbol;
	}

	public String getDescription() {
		return description;
	}

	public double getOpen() {
		return open;
	}

	public double getClose() {
		return close;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getBid() {
		return bid;
	}

	public double getAsk() {
		return ask;
	}

	public double getLast() {
		return last;
	}

	public int getBidSize() {
		return bidsize;
	}

	public int getAskSize() {
		return asksize;
	}

	public long getVolume() {
		return volume;
	}
	
}
