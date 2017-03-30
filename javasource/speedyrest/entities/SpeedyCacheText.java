package speedyrest.entities;

import java.util.HashMap;
import java.util.Map;

public class SpeedyCacheText extends SpeedyCacheEntity{
	
	private StringBuilder content;
	
	public SpeedyCacheText (String cacheKey) {
		super(cacheKey);
	}

	@Override
	public void append(String value) {
		content.append(value);
	}
	
	public String getContent() {
		return content.toString();
	}
	
	@Override
	public void setContent(String content) {
		this.content = new StringBuilder();
		this.content.append(content);
	}

	@Override
	public Map<String, byte[]> getFileParts() {
		return new HashMap<String, byte[]>();
	}

	@Override
	public Map<String, String> getFilePartLengths() {
		return new HashMap<String, String>();
	}
	
	public void append(byte[] b, int len) {}

	@Override
	public void setFileParts(Map<String, byte[]> filePartMap) {}

	@Override
	public void setFilePartLengths(Map<String, String> filePartLengthMap) {}
}
