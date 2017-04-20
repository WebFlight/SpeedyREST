package speedyrest.respositories;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import com.google.common.primitives.Bytes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

import speedyrest.entities.SpeedyHeaders;
import speedyrest.proxies.ResponseCache;

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

	public void getBinaryContent(ResponseCache responseCache, OutputStream outputStream) {
		responseCache.getBinaryContent(context, outputStream);
	}
	
	public void addBinaryContent(ResponseCache responseCache, byte[] byteArray, int length) throws IOException {
		ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
		byte[] bOld = null;
		byte[] bNew  = null;
		int oldLength = 0;
		
		try {
			getBinaryContent(responseCache, bOutput);
			bOutput.close();
			bOld = bOutput.toByteArray();
		}
		catch (Exception e) {
			
		}
		if (bOld != null) {
			bNew = Bytes.concat(bOld, byteArray);
			oldLength = bOld.length;
		}
		else{
			bNew = byteArray;
		}
		
		responseCache.setBinaryContent(context, new ByteArrayInputStream(bNew), oldLength + length);
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
