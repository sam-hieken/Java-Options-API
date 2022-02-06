package stonks;

import java.util.List;

public class Util {
	public static double min(List<Double> d) {
		double min = d.get(0);
		
		for(int i = 1; i < d.size(); i++) {
			if(d.get(i) < min) min = d.get(i);
		}
		
		return Math.floor(min);
	}
	
	public static double max(List<Double> d) {
		double max = d.get(0);
		
		for(int i = 1; i < d.size(); i++) {
			if(d.get(i) > max) max = d.get(i);
		}
		
		return Math.ceil(max);
	}
	
	/** Stolen */
	public static double roundAvoid(double value, int places) {
	    double scale = Math.pow(10, places);
	    return Math.round(value * scale) / scale;
	}
}
