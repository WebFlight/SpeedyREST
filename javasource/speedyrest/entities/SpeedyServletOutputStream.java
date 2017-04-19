package speedyrest.entities;

import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.ServletOutputStream;

import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;

import speedyrest.proxies.ResponseCache;
import speedyrest.respositories.CacheRepository;

public class SpeedyServletOutputStream extends ServletOutputStream {

	private ServletOutputStream servletOutputStream;
	private ResponseCache responseCache;
	private CacheRepository cacheRepository;
	private IMxRuntimeRequest request;
	private IMxRuntimeResponse response;
	
	public SpeedyServletOutputStream(ServletOutputStream servletOutputStream, ResponseCache responseCache, CacheRepository cacheRepository, IMxRuntimeRequest request, IMxRuntimeResponse response) {

		this.servletOutputStream = servletOutputStream;
		this.responseCache = responseCache;
		this.cacheRepository = cacheRepository;
		this.request = request;
		this.response = response;
	}

	@Override
	public void write(int b) throws IOException {
		if (isGetRequest() && isHttpStatusSuccess()) {
			responseCache.addTextualContent(String.valueOf(b));
		}
		servletOutputStream.write(BigInteger.valueOf(b).toByteArray());
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		// TODO: Use new FileParts method
		if (isGetRequest() && isHttpStatusSuccess()) {
			responseCache.addFilePart(b, len);
			if (len < 4096) {
				cacheRepository.persist(responseCache);
			}
		}
		this.servletOutputStream.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		if (isGetRequest() && isHttpStatusSuccess()) {
			responseCache.addTextualContent(String.valueOf(b));
		}
		servletOutputStream.write(b);
	}
	
	@Override
	public void close() throws IOException {
		if (this.responseCache.getTextualContent() != null) {
			cacheRepository.persist(responseCache);
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
