package stonks;

public enum Greek {
	DELTA, GAMMA, THETA, VEGA, RHO, PHI;
	
	@Override
	public String toString() {
		switch(this) {
		
			case DELTA: return "delta";
			case GAMMA: return "gamma";
			case THETA: return "theta";
			case VEGA: return "vega";
			case RHO: return "rho";
			case PHI: return "phi";
			default: return null;
		
		}
	}
}
