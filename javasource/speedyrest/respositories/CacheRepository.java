package speedyrest.respositories;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

import speedyrest.entities.SpeedyHeaders;
import speedyrest.proxies.ResponseCache;
import speedyrest.proxies.constants.Constants;

public class CacheRepository {

	public CacheRepository() {
		
	}
	
	public ResponseCache find(String cacheKey) throws CoreException {
		List<IMendixObject> objectList = Core.retrieveXPathQuery(Core.createSystemContext(), "//SpeedyREST.ResponseCache[Key='" + cacheKey + "']");
		if (objectList.size() == 0) {
			return new ResponseCache(Core.createSystemContext());
		}
		return ResponseCache.initialize(Core.createSystemContext(), objectList.get(0));
	}

	public void persist(ResponseCache responseCache) throws CoreException {
		responseCache.commit(Core.createSystemContext());
	}
	
	public ResponseCache createResponseCache(String key) {
		ResponseCache responseCache = new ResponseCache(Core.createSystemContext());
		responseCache.setKey(key);
		return responseCache;
	}
	
	public String getContent(ResponseCache responseCache) {
		return responseCache.getContent(Core.createSystemContext());
	}
	
	public String getKey(ResponseCache responseCache) {
		return responseCache.getKey(Core.createSystemContext());
	}
	
	public Date getDateTimeCreated(ResponseCache responseCache) throws CoreException {
		return responseCache.getMendixObject().getCreatedDate(Core.createSystemContext());
	}
	
	public void addContent(ResponseCache responseCache, String content) {
		String oldContent = getContent(responseCache);
		if (oldContent == null) {
			oldContent = new String("");
		}
		StringBuilder stringBuilder = new StringBuilder(oldContent);
		stringBuilder.append(content);
		responseCache.setContent(Core.createSystemContext(), stringBuilder.toString());
	}
	
	public SpeedyHeaders getHeaders(ResponseCache responseCache) {
		return deserializeHeaders(responseCache.getHeaders(Core.createSystemContext()));
	}
	
	public void addHeader(ResponseCache responseCache, String key, String value) {
		SpeedyHeaders speedyHeaders = getHeaders(responseCache);
		if (speedyHeaders == null) {
			speedyHeaders = new SpeedyHeaders();			
		}
		
		speedyHeaders.addHeader(key, value);
		responseCache.setHeaders(Core.createSystemContext(), serializeHeaders(speedyHeaders));
	}

	public List<Cookie> getCookies(ResponseCache responseCache) {
		return deserializeCookies(responseCache.getCookies(Core.createSystemContext()));
	}
	
	public void addCookie(ResponseCache responseCache, Cookie cookie) {
		List<Cookie> cookies = getCookies(responseCache);
		cookies.add(cookie);
		responseCache.setCookies(Core.createSystemContext(), serializeCookies(cookies));
	}
	
	public long cacheTTL() {
		return  Constants.getCACHE_TTL();
	}
	
	public void clearCacheEntry(ResponseCache responseCache) {
		Core.delete(Core.createSystemContext(), responseCache.getMendixObject());
	}
	
	private SpeedyHeaders deserializeHeaders(String headerString) {
		Gson gson = new Gson();
		return gson.fromJson(headerString, SpeedyHeaders.class);
	}
	
	private String serializeHeaders(SpeedyHeaders speedyHeaders) {
		Gson gson = new Gson();
		return gson.toJson(speedyHeaders, SpeedyHeaders.class);
	}
	
	private List<Cookie> deserializeCookies(String cookieString) {
		Gson gson = new Gson();

		if (cookieString != null) {
			Type collectionType = new TypeToken<ArrayList<Cookie>>() {}.getType();
			return gson.fromJson(cookieString, collectionType);
		}

		return new ArrayList<Cookie>();
	}
	
	private String serializeCookies(List<Cookie> cookies) {
		if (!cookies.isEmpty()) {
			Gson gson = new Gson();
			Type collectionType = new TypeToken<ArrayList<Cookie>>() {}.getType();
			return gson.toJson(cookies, collectionType);
		}
		return new String();
	}
}
