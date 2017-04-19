package speedyrest.services;

import java.util.List;
import java.util.Map;

public interface Cache {
		
	Map<String, String> getHashMap(String key);
	
	boolean deleteByPattern(String pattern);
	
	void persistObject(Object object);
	
	List<?> findObject(Class<?> classDefinition, String key, Object value);
}
