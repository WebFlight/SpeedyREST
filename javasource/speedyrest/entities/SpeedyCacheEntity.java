package speedyrest.entities;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import speedyrest.services.SpeedyHeaders;

public abstract class SpeedyCacheEntity {

	private final String cacheKey;
	private List<Cookie> cookies;
	private SpeedyHeaders headers;
	
	public SpeedyCacheEntity(String cacheKey) {
		this.cacheKey = cacheKey;
	}
	
	public abstract void append (String value);
	
	public abstract void setContent (String content);
	
	public abstract void append (byte[] b, int len);
	
	public abstract Map<String, byte[]> getFileParts();
	
	public abstract void setFileParts (Map<String, byte[]> filePartMap);
	
	public abstract Map<String, String> getFilePartLengths();
	
	public abstract void setFilePartLengths (Map<String, String> filePartLengthMap);
	
	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	public SpeedyHeaders getHeaders() {
		return headers;
	}

	public void setHeaders(SpeedyHeaders headers) {
		this.headers = headers;
	}
	
	public String getCacheKey () {
		return this.cacheKey;
	}
	
	public void addHeader (String key, String value) {
		headers.addHeader(key, value);
	}
	
	public void addCookie (Cookie cookie) {
		cookies.add(cookie);
//		String currentCookies = this.cacheObject.get("cookies");
//		Gson gson = new Gson();
//		ArrayList<Cookie> cookieList = new ArrayList<>();
//		
//		if (currentCookies !=  null) {
//			Type collectionType = new TypeToken<ArrayList<Cookie>>(){}.getType();
//			cookieList = gson.fromJson(currentCookies, collectionType);
//		}
//		
//		cookieList.add(cookie);
//		String newCookies = gson.toJson(cookieList);
//		this.cacheObject.put("cookies", newCookies);
	}
}
