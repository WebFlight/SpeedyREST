package speedyrest.usecases;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;

import com.mendix.externalinterface.connector.RequestHandler;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;

import restservices.publish.RestServiceHandler;
import speedyrest.entities.SpeedyHeaders;
import speedyrest.entities.SpeedyResponse;
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
			ResponseCache cachedResponse = cacheRepository.find(cacheKey);
			serveFromCache(cachedResponse, response);
		} catch(Exception e) {
			serveFromRest(cacheKey, request, response, path);
		}
	}

	private void serveFromCache(ResponseCache cachedResponse, IMxRuntimeResponse response) {			
		setHeaders(response, cachedResponse);
		setCookies(response, cachedResponse);
		
		if (cachedResponse.getTextualContent() != null) {
			Map<String, Map<String, Object>> fileParts = cachedResponse.getFileParts();
			
			for (Entry<String, Map<String, Object>> filePart : fileParts.entrySet()) {
				Map<String, Object> fileComponents = filePart.getValue();
				for (Entry<String, Object> fileComponent : fileComponents.entrySet()) {
					byte[] b = new byte[0];
					int len = 0;
					if (fileComponent.getKey().startsWith("filepartcontent")) {
						b = (byte[]) fileComponent.getValue();
					}
					if (fileComponent.getKey().startsWith("filepartlength")) {
						len = (int) fileComponent.getValue();
					}
					response.getOutputStream().write(b, 0, len);
				}
			}
		}
		
		if (cachedResponse.getTextualContent().length() > 0) {
			response.getOutputStream().write(cachedResponse.getTextualContent().getBytes());
		}

		SpeedyHeaders cachedHeaders = cachedResponse.getHeaders();
		cachedHeaders.getHeader(key)
	}
	
	private void serveFromRest(String cacheKey, IMxRuntimeRequest request, IMxRuntimeResponse response, String path) throws Exception {
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
