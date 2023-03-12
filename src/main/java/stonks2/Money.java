package stonks2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
public class Money {
	private final long 		amt;
	private final String	currCode;
	
	@Override
	public String toString() {
		return "$" + amt + ", " + currCode;
	}
}
