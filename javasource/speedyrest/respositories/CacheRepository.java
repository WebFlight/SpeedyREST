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

	private IContext context;

	public CacheRepository(IContext context) {
		this.context = context;
	}
	
	public ResponseCache find(String cacheKey) throws CoreException {
		List<IMendixObject> objectList = Core.retrieveXPathQuery(context, "//SpeedyREST.ResponseCache[Key='" + cacheKey + "']");
		if (objectList.size() == 0) {
			return new ResponseCache(context);
		}
		return ResponseCache.initialize(context, objectList.get(0));
	}

	public void persist(ResponseCache responseCache) throws CoreException {
		responseCache.commit(context);
	}
	
	public ResponseCache createResponseCache(String key) {
		ResponseCache responseCache = new ResponseCache(context);
		responseCache.setKey(key);
		return responseCache;
	}
	
	public String getContent(ResponseCache responseCache) {
		return responseCache.getContent(context);
	}
	
	public String getKey(ResponseCache responseCache) {
		return responseCache.getKey(context);
	}
	
	public Date getDateTimeCreated(ResponseCache responseCache) throws CoreException {
		return responseCache.getMendixObject().getCreatedDate(context);
	}
	
	public void addContent(ResponseCache responseCache, String content) {
		String oldContent = getContent(responseCache);
		if (oldContent == null) {
			oldContent = new String("");
		}
		StringBuilder stringBuilder = new StringBuilder(oldContent);
		stringBuilder.append(content);
		responseCache.setContent(context, stringBuilder.toString());
	}
	
	public SpeedyHeaders getHeaders(ResponseCache responseCache) {
		return deserializeHeaders(responseCache.getHeaders(context));
	}
	
	public void addHeader(ResponseCache responseCache, String key, String value) {
		SpeedyHeaders speedyHeaders = getHeaders(responseCache);
		if (speedyHeaders == null) {
			speedyHeaders = new SpeedyHeaders();			
		}
		
		speedyHeaders.addHeader(key, value);
		responseCache.setHeaders(context, serializeHeaders(speedyHeaders));
	}

	public List<Cookie> getCookies(ResponseCache responseCache) {
		return deserializeCookies(responseCache.getCookies(context));
	}
	
	public void addCookie(ResponseCache responseCache, Cookie cookie) {
		List<Cookie> cookies = getCookies(responseCache);
		cookies.add(cookie);
		responseCache.setCookies(context, serializeCookies(cookies));
	}
	
	public long cacheTTL() {
		return  Constants.getCACHE_TTL();
	}
	
	public void clearCacheEntry(ResponseCache responseCache) {
		Core.delete(context, responseCache.getMendixObject());
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
