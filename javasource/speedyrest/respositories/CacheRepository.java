package speedyrest.respositories;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import speedyrest.entities.ResponseCache;
import speedyrest.entities.SpeedyHeaders;
import speedyrest.services.Cache;

public class CacheRepository {

	private Cache cache;

	public CacheRepository(Cache cache) {
		this.cache = cache;
	}

	public ResponseCache find(String cacheKey) {
		Map<String, String> cachedMap = cache.getHashMap(cacheKey);
		ResponseCache cachedResponse = new ResponseCache(cacheKey);
		System.out.println(cachedMap.size());
		
		if (cachedMap.size() > 0) {
			SpeedyHeaders speedyHeaders = getHeaders(cachedMap.get("headers"));
	
			if (speedyHeaders.getHeader("Content-Type").equals("application/octet-stream")) {
				cachedResponse.setFileParts(getFileParts(cachedMap));
			}
	
			if (!speedyHeaders.getHeader("Content-Type").equals("application/octet-stream")) {
				cachedResponse.setTextualContent(cachedMap.get("content"));
			}
	
			cachedResponse.setCookies(getCookies(cachedMap.get("cookies")));
			cachedResponse.setHeaders(speedyHeaders);
		}

		return cachedResponse;
	}

	public void persist(ResponseCache responseCache) {
		cache.persistObject(responseCache);
	}
	
	private SpeedyHeaders getHeaders(String headerString) {
		Gson gson = new Gson();
		return gson.fromJson(headerString, SpeedyHeaders.class);
	}

	private List<Cookie> getCookies(String cookieString) {
		Gson gson = new Gson();

		if (cookieString != null) {
			Type collectionType = new TypeToken<ArrayList<Cookie>>() {
			}.getType();
			return gson.fromJson(cookieString, collectionType);
		}

		return new ArrayList<Cookie>();
	}

	private Map<String, Map<String, Object>> getFileParts(Map<String, String> cachedMap) {
		Map<String, Map<String, Object>> fileParts = new HashMap<>();
		int filePartCounter = 0;
		
		for (Entry<String, String> fileComponent : cachedMap.entrySet()) {
			Map<String, Object> fileComponents = new HashMap<>();
			if (fileComponent.getKey().startsWith("filepartcontent")) {
				fileComponents.put(fileComponent.getKey(), Base64.getDecoder().decode(fileComponent.getValue()));
			}
			if (fileComponent.getKey().startsWith("filepartlength")) {
				fileComponents.put(fileComponent.getKey(), fileComponent.getValue());
			}
			fileParts.put("filepart" + filePartCounter, fileComponents);
			filePartCounter ++;
		}
		return fileParts;
	}
}
