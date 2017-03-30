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
import speedyrest.services.Cache;
import speedyrest.services.SpeedyHeaders;

public class CacheRepository {

	private Cache cache;

	public CacheRepository(Cache cache) {
		this.cache = cache;
	}

	public ResponseCache find(String cacheKey) {
		Map<String, String> cachedMap = cache.getHashMap(cacheKey);
		SpeedyHeaders speedyHeaders = getHeaders(cachedMap.get("headers"));
		ResponseCache cachedResponse = new ResponseCache(cacheKey);

		if (speedyHeaders.getHeader("Content-Type").equals("application/octet-stream")) {
			cachedResponse.setFileParts(getFileParts(cachedMap));
			cachedResponse.setFilePartLengths(getFilePartLengths(cachedMap));
		}

		if (!speedyHeaders.getHeader("Content-Type").equals("application/octet-stream")) {
			cachedResponse.setTextualContent(cachedMap.get("content"));
		}

		cachedResponse.setCookies(getCookies(cachedMap.get("cookies")));
		cachedResponse.setHeaders(speedyHeaders);

		return cachedResponse;
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

	private HashMap<String, byte[]> getFileParts(Map<String, String> cachedMap) {
		HashMap<String, byte[]> fileMap = new HashMap<>();
		for (Entry<String, String> entry : cachedMap.entrySet()) {
			if (entry.getKey().startsWith("filepartcontent")) {
				fileMap.put(entry.getKey(), Base64.getDecoder().decode(entry.getValue()));
			}
		}
		return fileMap;
	}

	private HashMap<String, String> getFilePartLengths(Map<String, String> cachedMap) {
		HashMap<String, String> filePartLengthMap = new HashMap<>();
		for (Entry<String, String> entry : cachedMap.entrySet()) {
			if (entry.getKey().startsWith("filepartlength")) {
				filePartLengthMap.put(entry.getKey(), entry.getValue());
			}
		}
		return filePartLengthMap;
	}
}
