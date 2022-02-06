package stonks;

public enum OptionType {
	CALL, PUT, ALL;
	
	public static OptionType fromString(String str) {
	
		if(str.equals("call")) return CALL;
		if(str.equals("put")) return PUT;
		if(str.equals("all")) return ALL;
		return null;

	}
	
	/**
	 * String representation of option
	 */
	@Override
	public String toString() {
		if(this == CALL) return "call";
		if(this == PUT) return "put";
		else return null;
	}
}
