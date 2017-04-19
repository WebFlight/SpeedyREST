package speedyrest.respositories;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

import speedyrest.entities.ResponseCache;
import speedyrest.entities.SpeedyHeaders;

public class CacheRepository {

	private IContext context;
	private IMendixObject cacheObject;

	public CacheRepository(IContext context) {
		this.context = context;
	}
	
	public IMendixObject find(String cacheKey) {
		
		if (cacheObject == null || cacheObject.getValue(context, "Key") != cacheKey) {
			List<IMendixObject> objectList;
			try {
				objectList = Core.retrieveXPathQuery(context, "//SpeedyREST.CachedObject[Key='" + cacheKey + "']");
				this.cacheObject = objectList.get(0);
			} catch (CoreException e) {
				return cacheObject;
			}
		}
		return cacheObject;
	}

	public void persist(ResponseCache responseCache) {
		
	}
	
	public SpeedyHeaders getHeaders(String cacheKey) {
		IMendixObject object = find(cacheKey);
		return getHeadersFromString((String) object.getValue(context, "headers"));
	}
	
	private SpeedyHeaders getHeadersFromString(String headerString) {
		Gson gson = new Gson();
		return gson.fromJson(headerString, SpeedyHeaders.class);
	}

	public List<Cookie> getCookies(String cacheKey) {
		IMendixObject object = find(cacheKey);
		return getCookiesFromString((String) object.getValue(context, "cookies"));
	}
	
	private List<Cookie> getCookiesFromString(String cookieString) {
		Gson gson = new Gson();

		if (cookieString != null) {
			Type collectionType = new TypeToken<ArrayList<Cookie>>() {
			}.getType();
			return gson.fromJson(cookieString, collectionType);
		}

		return new ArrayList<Cookie>();
	}

	public List<IMendixObject> getFileParts(String cacheKey) {
		IMendixObject object = find(cacheKey);
		List<IMendixObject> fileParts = Core.retrieveByPath(context, object, "BinaryContent_CachedObject");
		return fileParts;
	}
}
