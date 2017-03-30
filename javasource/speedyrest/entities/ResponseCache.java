package speedyrest.entities;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import speedyrest.services.SpeedyHeaders;

public class ResponseCache {

	private final String cacheKey;
	private List<Cookie> cookies;
	private SpeedyHeaders headers;
	private StringBuilder textualContent = new StringBuilder();
	private int filePartCounter = 0;
	private Map<String, byte[]> filePartMap;
	private Map<String, String> filePartLengthMap;

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
	
	public Map<String, byte[]> getFileParts() {
		return filePartMap;
	}
	
	public void setFileParts(Map<String, byte[]> filePartMap) {
		this.filePartMap = filePartMap;
	}
	
	public Map<String, String> getFilePartLengths() {
		return filePartLengthMap;
	}
	
	public void setFilePartLengths(Map<String, String> filePartLengthMap) {
		this.filePartLengthMap = filePartLengthMap;
	}
	
	public void addFilePart(byte[] b, int len) {
		filePartMap.put("filepartcontent" + this.filePartCounter, b);
		filePartLengthMap.put("filepartlength" + this.filePartCounter, String.valueOf(len));
		filePartCounter++;
	}
}
