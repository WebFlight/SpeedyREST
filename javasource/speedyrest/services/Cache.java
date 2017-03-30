package speedyrest.services;

import java.util.HashMap;

public interface Cache {
	
	void set(String key, String value);
	
	void setHashMap(String key, HashMap<String, String> treeMap);
	
	HashMap<String, String> getHashMap(String key);
	
	String get(String key);
	
	boolean deleteByPattern(String pattern);
}
