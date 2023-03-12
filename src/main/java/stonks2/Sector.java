package stonks2;

import java.util.Map;

public enum Sector {
	BASIC_MATERIALS,
	CONSUMER_CYCLICAL,
	FINANCIAL_SERVICES,
	REAL_ESTATE,
	CONSUMER_DEFENSIVE,
	HEALTHCARE,
	UTILITIES,
	COMMUNICATION_SERVICES,
	ENERGY,
	INDUSTRIALS,
	TECHNOLOGY;
	
	private static final Map<String, Sector> SECTOR_MAP = Map.ofEntries(
			Map.entry("Basic Materials", BASIC_MATERIALS),
			Map.entry("Consumer Cyclical", CONSUMER_CYCLICAL),
			Map.entry("Financial Services", FINANCIAL_SERVICES),
			Map.entry("Real Estate", REAL_ESTATE),
			Map.entry("Consumer Defensive", CONSUMER_DEFENSIVE),
			Map.entry("Healthcare", HEALTHCARE),
			Map.entry("Utilities", UTILITIES),
			Map.entry("Communication Services", COMMUNICATION_SERVICES),
			Map.entry("Energy", ENERGY),
			Map.entry("Industrials", INDUSTRIALS),
			Map.entry("Technology", TECHNOLOGY)
		);
	
	public static Sector getSector(String yahooCode) {
		return SECTOR_MAP.get(yahooCode);
	}
}
