package speedyrest.services;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mendix.m2ee.api.IMxRuntimeResponse;

public class SpeedyResponseCacheInjector {

	private IMxRuntimeResponse response;
	private Map<String, String> cachedObject;
	
	public SpeedyResponseCacheInjector(IMxRuntimeResponse response, Map<String, String> cacheObject) {
		this.response = response;
		this.cachedObject = cacheObject;
	}
	
	public void inject () throws IOException {
		setHeaders();
		setCookies();
		setContent();
	}
	
	private void setHeaders () {
		Map<String, String> headerMap = this.getHeaders().getHeaders();
		java.util.Iterator<Entry<String, String>> headerIterator = headerMap.entrySet().iterator();
		
		while (headerIterator.hasNext()) {
			Map.Entry<String, String> header = headerIterator.next();
			response.getHttpServletResponse().addHeader(header.getKey(), header.getValue());
		}
	}
	
	private void setCookies () {
		Iterator<Cookie> cookies = getCookies().iterator();
		while(cookies.hasNext()) {
			Cookie cookie = cookies.next();
			response.getHttpServletResponse().addCookie(cookie);
		}
	}
	
	private void setContent () throws IOException {
		
		SpeedyHeaders speedyHeaders = getHeaders();
		if(speedyHeaders.getHeader("Content-Type").equals("application/octet-stream")){
			HashMap<String, String> fileHashMap = getFileParts();
			for (int i = 0; i < (fileHashMap.size()/2 - 1) - 1; i++) {
				byte[] b = Base64.getDecoder().decode(fileHashMap.get("filepart" + i));
				int len = Integer.valueOf(fileHashMap.get("filepart" + i + "length"));
				response.getOutputStream().write(b, 0, len);
			}
			return;
		}
		response.getOutputStream().write(getContent().getBytes());
		response.getOutputStream().close();
	}
	
	private SpeedyHeaders getHeaders () {
		String headerString = this.cachedObject.get("headers");
		Gson gson = new Gson();
		return gson.fromJson(headerString, SpeedyHeaders.class);
	}
	
	private List<Cookie> getCookies () {
		String cookies = this.cachedObject.get("cookies");
		Gson gson = new Gson();
		
		if (cookies !=  null) {
			Type collectionType = new TypeToken<ArrayList<Cookie>>(){}.getType();
			return gson.fromJson(cookies, collectionType);
		}
		
		return new ArrayList<Cookie>();
	}
	
	private String getContent () {
		return this.cachedObject.get("content");
	}
	
	private HashMap<String, String> getFileParts () {
		HashMap<String, String> fileHashMap = new HashMap<>();
		for (Entry<String, String> entry : cachedObject.entrySet()) {
			if (entry.getKey().startsWith("filepart")) {
				fileHashMap.put(entry.getKey(), entry.getValue());
			}
		}
		return fileHashMap;
	}
}
