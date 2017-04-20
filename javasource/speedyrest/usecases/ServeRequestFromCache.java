package speedyrest.usecases;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;

import com.mendix.externalinterface.connector.RequestHandler;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;

import restservices.publish.RestServiceHandler;
import speedyrest.entities.SpeedyResponse;
import speedyrest.proxies.BinaryContent;
import speedyrest.proxies.ResponseCache;
import speedyrest.respositories.CacheRepository;

public class ServeRequestFromCache extends RequestHandler {

	private CacheRepository cacheRepository;

	public ServeRequestFromCache(CacheRepository cacheRepository) {
		this.cacheRepository = cacheRepository;
	}

	@Override
	protected void processRequest(IMxRuntimeRequest request, IMxRuntimeResponse response, String path) throws Exception {

		String cacheKey = path + request.getHttpServletRequest().getParameterMap().toString();
		try {
			System.out.println("Cachekey: " + cacheKey);
			ResponseCache cachedResponse = cacheRepository.find(cacheKey);
			serveFromCache(cachedResponse, response);
		} catch(Exception e) {
			serveFromRest(cacheKey, request, response, path);
		}
	}

	private void serveFromCache(ResponseCache responseCache, IMxRuntimeResponse response) throws IOException {
		System.out.println("Cache: YES");
		setHeaders(response, responseCache);
		setCookies(response, responseCache);
		
		if (responseCache.getContent() == null) {
			List<BinaryContent> fileParts = cacheRepository.getFileParts(responseCache);
			Iterator<BinaryContent> filePart = fileParts.iterator();
			
			while (filePart.hasNext()) {
				cacheRepository.getBinaryContentContent(filePart.next(), response.getOutputStream());
			}
		}
		
		if (cacheRepository.getContent(responseCache).length() > 0) {
			response.getOutputStream().write(cacheRepository.getContent(responseCache).getBytes());
		}
	}
	
	private void serveFromRest(String cacheKey, IMxRuntimeRequest request, IMxRuntimeResponse response, String path) throws Exception {
		System.out.println("Cache: NO");
		RestServiceHandler handler = new RestServiceHandler();
		ResponseCache responseCache = this.cacheRepository.createResponseCache(cacheKey);
		SpeedyResponse speedyResponse = new SpeedyResponse(request, response, responseCache, cacheRepository);
		handler.processRequest(request, speedyResponse, path);
	}
	
	private void setCookies(IMxRuntimeResponse response, ResponseCache cachedResponse) {
		Iterator<Cookie> cookies = cacheRepository.getCookies(cachedResponse).iterator();
		while(cookies.hasNext()) {
			Cookie cookie = cookies.next();
			response.getHttpServletResponse().addCookie(cookie);
		}
	}

	private void setHeaders(IMxRuntimeResponse response, ResponseCache cachedResponse) {
		Map<String, String> headerMap = cacheRepository.getHeaders(cachedResponse).getHeaders();
		java.util.Iterator<Entry<String, String>> headerIterator = headerMap.entrySet().iterator();
		while (headerIterator.hasNext()) {
			Map.Entry<String, String> header = headerIterator.next();
			response.getHttpServletResponse().addHeader(header.getKey(), header.getValue());
		}
	}
}
