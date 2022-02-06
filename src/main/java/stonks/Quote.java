package stonks;

import java.text.ParseException;

public class Quote {
	private String date;
	
	private double open;
	private double high;
	private double low;
	private double close;
	
	private long volume;

	String description;

	public Date getDate() {
		String[] date = this.date.split("\\-");
		int year = Integer.parseInt(date[0]);
		int month = Integer.parseInt(date[1]);
		int day = Integer.parseInt(date[2]);
		
		try {
			return new Date(year, month, day);
		} 
		catch (ParseException e) {
			return null;
		}
	}
	
	public String getDescription() {
		return description;
	}

	public double getOpen() {
		return open;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getClose() {
		return close;
	}

	public long getVolume() {
		return volume;
	}
}
