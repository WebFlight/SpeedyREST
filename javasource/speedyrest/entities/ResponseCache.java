package speedyrest.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

public class ResponseCache {

	private final String cacheKey;
	private List<Cookie> cookies;
	private SpeedyHeaders headers;
	private StringBuilder textualContent = new StringBuilder();
	private int filePartCounter = 0;
	private Map<String, Map<String, Object>> fileParts;

	public ResponseCache(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public String getCacheKey() {
		return this.cacheKey;
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	public SpeedyHeaders getHeaders() {
		return headers;
	}

	public void setHeaders(SpeedyHeaders headers) {
		this.headers = headers;
	}

	public void addHeader(String key, String value) {
		headers.addHeader(key, value);
	}

	public String getTextualContent() {
		return textualContent.toString();
	}

	// TODO: Both these methods do the same. Not happy with that.
	public void setTextualContent(String content) {
		textualContent.append(content);
	}

	public void addTextualContent(String value) {
		textualContent.append(value);
	}
	
	public Map<String, Map<String, Object>> getFileParts() {
		return fileParts;
	}

	public void setFileParts(Map<String, Map<String, Object>> fileParts) {
		this.fileParts = fileParts;
	}
	
	public void addFilePart(byte[] b, int len) {
		Map<String, Object> filePart = new HashMap<>();
		filePart.put("filepartcontent" + this.filePartCounter, b);
		filePart.put("filepartlength" + this.filePartCounter, String.valueOf(len));
		fileParts.put("filepart" + this.filePartCounter, filePart);
		
		filePartCounter++;
	}
}
