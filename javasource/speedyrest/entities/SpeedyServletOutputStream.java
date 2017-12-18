package speedyrest.entities;

import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;

import speedyrest.proxies.ResponseCache;
import speedyrest.respositories.CacheRepository;

public class SpeedyServletOutputStream extends ServletOutputStream {

	private ServletOutputStream servletOutputStream;
	private ResponseCache responseCache;
	private CacheRepository cacheRepository;
	
	public SpeedyServletOutputStream(ServletOutputStream servletOutputStream, ResponseCache responseCache, CacheRepository cacheRepository, IMxRuntimeRequest request, IMxRuntimeResponse response) {

		this.servletOutputStream = servletOutputStream;
		this.responseCache = responseCache;
		this.cacheRepository = cacheRepository;
	}

	@Override
	public void write(int b) throws IOException {
		cacheRepository.addContent(responseCache, String.valueOf(b));
		servletOutputStream.write(BigInteger.valueOf(b).toByteArray());
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		this.servletOutputStream.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		cacheRepository.addContent(responseCache, new String(b));
		this.servletOutputStream.write(b);
	}
	
	@Override
	public void close() throws IOException {
		this.servletOutputStream.close();
	}

	@Override
	public boolean isReady() {
		return this.servletOutputStream.isReady();
	}

	@Override
	public void setWriteListener(WriteListener arg0) {
		this.servletOutputStream.setWriteListener(arg0);
	}
}
