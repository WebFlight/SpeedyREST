package speedyrest.entities;

import com.google.common.primitives.Bytes;

public class BinaryContentCache {
	
	private byte[] binaryContentCache = new byte[0];
	private int length = 0;
	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public void addLength(int length) {
		this.length += length;
	}

	public byte[] getBinaryContentCache() {
		return binaryContentCache;
	}

	public void setBinaryContentCache(byte[] binaryContent) {
		this.binaryContentCache = binaryContent;
	}
	
	public void addBinaryContentCache(byte[] binaryContent, int length) {
		this.binaryContentCache = Bytes.concat(this.binaryContentCache, binaryContent);
		addLength(length);
	}
}
