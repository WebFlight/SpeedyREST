package speedyrest.entities;
import java.util.Map;

public class SpeedyCacheFile extends SpeedyCacheEntity {
	
	private int filePartCounter = 0;
	private Map<String, byte[]> filePartMap;
	private Map<String, String> filePartLengthMap;
	
	public SpeedyCacheFile(String cacheKey) {
		super(cacheKey);
	}

	@Override
	public void append(byte[] b, int len) {
		filePartMap.put("filepartcontent" + this.filePartCounter, b);
		filePartLengthMap.put("filepartlength" + this.filePartCounter, String.valueOf(len));
		filePartCounter++;
	}
	
	public Map<String, byte[]> getFileParts() {
		return filePartMap;
	}
	
	public Map<String, String> getFilePartLengths() {
		return filePartLengthMap;
	}

	@Override
	public void setFileParts(Map<String, byte[]> filePartMap) {
		this.filePartMap = filePartMap;
	}

	@Override
	public void setFilePartLengths(Map<String, String> filePartLengthMap) {
		this.filePartLengthMap = filePartLengthMap;
	}
	
	@Override
	public void setContent(String content) {}
	
	@Override
	public void append(String value) {}

}
