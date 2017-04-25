package speedyrest.usecases;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;

import com.mendix.logging.ILogNode;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;

import restservices.publish.RestServiceHandler;
import speedyrest.entities.SpeedyResponse;
import speedyrest.helpers.CacheValidator;
import speedyrest.proxies.ResponseCache;
import speedyrest.respositories.CacheRepository;

public class ServeRequestFromCache {
	
	private CacheRepository cacheRepository;
	private CacheValidator cacheValidator;
	private ILogNode logger;
	
	public ServeRequestFromCache (CacheRepository cacheRepository, CacheValidator cacheValidator, ILogNode logger) {
		this.cacheRepository = cacheRepository;
		this.cacheValidator = cacheValidator;
		this.logger = logger;
	}
	
	public void serveRequest(IMxRuntimeRequest request, IMxRuntimeResponse response, String path, String cacheKey, RestServiceHandler restServiceHandler, ResponseCache responseCache, SpeedyResponse speedyResponse) throws Exception {
		if (cacheValidator.isNotValid(responseCache, cacheRepository, logger)) {
			cacheRepository.clearCacheEntry(responseCache);
			responseCache = cacheRepository.createResponseCache(null);
		}
		
		if (cacheValidator.isNotFound(responseCache, cacheRepository)) {
			logger.debug("Served request from REST module: " + cacheKey);
			serveFromRest(request, path, restServiceHandler, speedyResponse);
		}
		if (cacheValidator.isFound(responseCache, cacheRepository)) {
			logger.debug("Served request from SpeedyREST cache: " + cacheKey);
			serveFromCache(responseCache, response);
		}
	}
	
	private void serveFromCache(ResponseCache responseCache, IMxRuntimeResponse response) throws IOException {
		setHeaders(response, responseCache);
		setCookies(response, responseCache);
		String content = cacheRepository.getContent(responseCache);
		
		if (content == null) {
			cacheRepository.getBinaryContent(responseCache, response.getOutputStream());
		}
		
		if (content != null) {
			response.getOutputStream().write(content.getBytes());
			response.getOutputStream().close();
		}
	}
	
	private void serveFromRest(IMxRuntimeRequest request, String path, RestServiceHandler restServiceHandler, SpeedyResponse speedyResponse) throws Exception {
		restServiceHandler.processRequest(request, speedyResponse, path);
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
