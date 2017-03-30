package speedyrest.services;

import java.util.Map;

public interface Cache {
	
	void set(String key, String value);
	
	void setHashMap(String key, Map<String, String> hashMap);
	
	Map<String, String> getHashMap(String key);
	
	String get(String key);
	
	boolean deleteByPattern(String pattern);
}
