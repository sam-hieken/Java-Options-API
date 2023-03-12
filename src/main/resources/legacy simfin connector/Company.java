package stonks.fundamental;

import java.io.Serializable;
import java.time.LocalDate;

public class Company implements Serializable {	
	private static final long serialVersionUID = 1L;
	
	private final String	ticker;
	private String  		name;
	
	private Integer			industryID;
	
	private Integer			employeeCount;
	
	private Long			sharesOutstanding;
	
	private byte 			monthFYEnd;

	public Company(String ticker) {
		this.ticker = ticker;
	}
	
	public int getCurrentQuarter() {
		final int currentMonth = LocalDate.now().getMonthValue(),
				  monthEnd = getMonthFYEnd();
		
		final int monthsUntilEnd = monthsUntil(currentMonth, monthEnd);
		
		if (monthsUntilEnd < 3) return 4;
		if (monthsUntilEnd >= 3 && monthsUntilEnd < 6) return 3;
		if (monthsUntilEnd >= 6 && monthsUntilEnd < 9) return 2;
		
		return 1;
	}
	
	public int getCurrentFiscalYear() {
		final int currentMonth = LocalDate.now().getMonthValue(),
				  currentYear = LocalDate.now().getYear(),
				  currentQuarter = getCurrentQuarter();
		
		return currentQuarter * 3 < currentMonth ? currentYear + 1 : currentYear;
	}
	
	private int monthsUntil(int currentMonth, int laterMonth) {
		if (currentMonth == laterMonth) return 0;
		
		final int nextMonth = currentMonth + 1;
		
		return monthsUntil(nextMonth > 12 ? 1 : nextMonth, laterMonth) + 1;
	}
	
//	---------------------------------------------------------------------------------	
	
	public String getTicker() {
		return ticker;
	}

	public String getName() {
		return name;
	}
	
	public Long getSharesOutstanding() {
		return sharesOutstanding;
	}

	public Integer getIndustryID() {
		return industryID;
	}

	public Integer getEmployeeCount() {
		return employeeCount;
	}
	
	public byte getSectorID() {
		return  (byte) (industryID / 1000);
	}

	public byte getMonthFYEnd() {
		return monthFYEnd;
	}

	void setName(String name) {
		this.name = name;
	}

	void setIndustryID(Integer industryID) {
		this.industryID = industryID;
	}

	void setEmployeeCount(Integer employeeCount) {
		this.employeeCount = employeeCount;
	}

	void setMonthFYEnd(byte monthFYEnd) {
		this.monthFYEnd = monthFYEnd;
	}
	
	void setSharesOutstanding(Long sharesOutstanding) {
		this.sharesOutstanding = sharesOutstanding;
	}
}
