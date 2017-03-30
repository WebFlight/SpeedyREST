package speedyrest.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import redis.clients.johm.Attribute;
import redis.clients.johm.CollectionList;
import redis.clients.johm.CollectionMap;
import redis.clients.johm.Id;
import redis.clients.johm.Model;
import redis.clients.johm.Reference;

@Model
public class ResponseCache {

    @Id
    private int id;

	@Attribute
	private final String cacheKey;
	
	@CollectionList(of = Cookie.class)
	private List<Cookie> cookies;
	
	@Reference
	private SpeedyHeaders headers = new SpeedyHeaders();
	
	@Reference
	private StringBuilder textualContent = new StringBuilder();
	
	@Attribute
	private int filePartCounter = 0;
	
	@CollectionMap(key = String.class, value = String.class)
	private Map<String, String> fileParts;

	public ResponseCache(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
	
	public Map<String, String> getFileParts() {
		return fileParts;
	}

	public void setFileParts(Map<String, String> fileParts) {
		this.fileParts = fileParts;
	}
	
	public void addFilePart(byte[] b, int len) {
//		Map<String, Object> filePart = new HashMap<>();
//		filePart.put("filepartcontent" + this.filePartCounter, b);
//		filePart.put("filepartlength" + this.filePartCounter, String.valueOf(len));
		fileParts.put("filepart" + this.filePartCounter, "Some string");
		
		filePartCounter++;
	}
}
