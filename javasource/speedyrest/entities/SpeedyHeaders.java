package speedyrest.entities;

import java.util.HashMap;

public class SpeedyHeaders {
	private HashMap<String, String> speedyHeaders;

	public SpeedyHeaders () {
		this.speedyHeaders = new HashMap<>();
	}
	
	public void addHeader (String key, String value) {
		this.speedyHeaders.put(key, value);
	}
	
	public String getHeader (String key) {
		return this.speedyHeaders.get(key);
	}
	
	public HashMap<String, String> getHeaders () {
		return this.speedyHeaders;
	}
}
