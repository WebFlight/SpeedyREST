package speedyrest.services;

import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.ServletOutputStream;

import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;

import speedyrest.entities.SpeedyCacheEntity;

public class SpeedyServletOutputStream extends ServletOutputStream {

	private ServletOutputStream servletOutputStream;
	private SpeedyCacheEntity speedyCacheObject;
	private Cache cacheDriver;
	private IMxRuntimeRequest request;
	private IMxRuntimeResponse response;
	
	public SpeedyServletOutputStream(ServletOutputStream servletOutputStream, SpeedyCacheEntity speedyCacheObject, Cache cacheDriver, IMxRuntimeRequest request, IMxRuntimeResponse response) {

		this.servletOutputStream = servletOutputStream;
		this.speedyCacheObject = speedyCacheObject;
		this.cacheDriver = cacheDriver;
		this.request = request;
		this.response = response;
	}

	@Override
	public void write(int b) throws IOException {
		if (isGetRequest() && isHttpStatusSuccess()) {
			cacheDriver.append(speedyCacheObject, String.valueOf(b));
		}
		servletOutputStream.write(BigInteger.valueOf(b).toByteArray());
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (isGetRequest() && isHttpStatusSuccess()) {
			cacheDriver.appendFilePart(speedyCacheObject, b, len);
			if (len < 4096) {
				cacheDriver.setHashMap(this.speedyCacheObject.getCacheKey(), this.speedyCacheObject.getCacheObject());
			}
		}
		this.servletOutputStream.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		if (isGetRequest() && isHttpStatusSuccess()) {
			cacheDriver.append(speedyCacheObject, new String(b));
		}
		servletOutputStream.write(b);
	}
	
	@Override
	public void close() throws IOException {
		if (this.speedyCacheObject.getContent() != null) {
				cacheDriver.setHashMap(this.speedyCacheObject.getCacheKey(), this.speedyCacheObject.getCacheObject());
		}
		this.servletOutputStream.close();
	}
	
	@Override
	public void flush() throws IOException {
		
	}
	
	private boolean isGetRequest () {
		return request.getHttpServletRequest().getMethod().equals("GET");
	}
	
	private boolean isHttpStatusSuccess () {
		return (response.getHttpServletResponse().getStatus() == 200);
	}
}
