package speedyrest.entities;

import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.ServletOutputStream;

import com.mendix.core.CoreException;
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
			cacheRepository.addContent(responseCache, String.valueOf(b));
		}
		servletOutputStream.write(BigInteger.valueOf(b).toByteArray());
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (isGetRequest() && isHttpStatusSuccess() && cacheRepository.cacheFileContent()) {
			cacheRepository.addBinaryContent(responseCache, b, len);
		}
		this.servletOutputStream.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		if (isGetRequest() && isHttpStatusSuccess()) {
			cacheRepository.addContent(responseCache, new String(b));
		}
		servletOutputStream.write(b);
	}
	
	@Override
	public void close() throws IOException {
		if (this.responseCache.getContent() != null) {
			try {
				cacheRepository.persist(responseCache);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		this.servletOutputStream.close();
	}
	
	private boolean isGetRequest () {
		return request.getHttpServletRequest().getMethod().equals("GET");
	}
	
	private boolean isHttpStatusSuccess () {
		return (response.getHttpServletResponse().getStatus() == 200);
	}
}
