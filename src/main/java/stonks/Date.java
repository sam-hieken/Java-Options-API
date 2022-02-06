package stonks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Date {
	private String date;
	
	private short year;
	
	private byte month;
	private byte day;
	
	public static Date toDate(String str) throws ParseException {
		String[] date = str.split("\\-");
		
		return new Date(Short.parseShort(date[0]), Byte.parseByte(date[1]), Byte.parseByte(date[2]));
	}
	
	public static Date today() throws ParseException {
		Calendar c = Calendar.getInstance();
		int year = c.get(1);
		int month = 1 + c.get(2);
		int day = c.get(5);
		return new Date(year, month, day);
	}
	
	/**
	 * @throws ParseException If the date is invalid
	 */
	public Date(int year, int month, int day) throws ParseException {
		if(month < 1 || day < 1 || year < 1970) throw new ParseException("Invalid Date.", -1);
		
		this.year = (short) year;
		
		this.month = (byte) month;
		this.day = (byte) day;
		
		this.date = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
	}

	public short getYear() {
		return year;
	}

	public byte getMonth() {
		return month;
	}

	public byte getDay() {
		return day;
	}
	
	@Override
	public String toString() {
		return date;
	}
	
	public long getUnixTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date d = null;
		try {
			d = format.parse(date);
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		return d.getTime() / 1000;
	}
}
