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
	
	public void serveRequest(IMxRuntimeRequest request, IMxRuntimeResponse response, String path, String cacheKey, RestServiceHandler restServiceHandler) throws Exception {
		if (isGetRequest(request)) {
			try{
				ResponseCache responseCache = cacheRepository.find(cacheKey);
				
				if (cacheValidator.isNotValid(responseCache, cacheRepository, logger)) {
					cacheRepository.clearCacheEntry(responseCache);
					responseCache = cacheRepository.createResponseCache(null);
				}
				
				if (cacheValidator.isNotFound(responseCache, cacheRepository)) {
					logger.debug("Served request from REST module: " + cacheKey);
					ResponseCache newResponseCache = cacheRepository.createResponseCache(cacheKey);
					SpeedyResponse speedyResponse = new SpeedyResponse(request, response, newResponseCache, cacheRepository);
					serveFromRest(request, path, restServiceHandler, speedyResponse, newResponseCache);
				}
				if (cacheValidator.isFound(responseCache, cacheRepository)) {
					logger.debug("Served request from SpeedyREST cache: " + cacheKey);
					serveFromCache(responseCache, request, response, path, restServiceHandler);
				}
			} catch(Exception e) {
				logger.error("Error encountered during serving from SpeedyREST cache: " + e);
				restServiceHandler.processRequest(request, response, path);
			}	
		}
		
		if (isNoGetRequest(request)) {
			logger.debug("Served request from REST module: POST request");
			restServiceHandler.processRequest(request, response, path);
		}
	}
	
	private void serveFromCache(ResponseCache responseCache, IMxRuntimeRequest request, IMxRuntimeResponse response, String path, RestServiceHandler restServiceHandler) throws IOException {
		String content = cacheRepository.getContent(responseCache);
		
		if (content == null) {
			logger.debug("Served request from REST module: binary content");
			restServiceHandler.processRequest(request, response, path);
		}
		
		if (content != null) {
			setHeaders(response, responseCache);
			setCookies(response, responseCache);
			response.getOutputStream().write(content.getBytes());
			response.getOutputStream().close();
		}
	}
	
	private void serveFromRest(IMxRuntimeRequest request, String path, RestServiceHandler restServiceHandler, SpeedyResponse speedyResponse, ResponseCache responseCache) throws Exception {
		restServiceHandler.processRequest(request, speedyResponse, path);
		if (isHttpStatusSuccess(speedyResponse)) {
			logger.debug("HTTP status success: cache is stored");
			cacheRepository.persist(responseCache);
		}
		if (isNoHttpStatusSuccess(speedyResponse)) {
			logger.debug("HTTP status no success: cache not stored");
			cacheRepository.clearCacheEntry(responseCache);
		}
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
	
	private boolean isGetRequest (IMxRuntimeRequest request) {
		return request.getHttpServletRequest().getMethod().equals("GET");
	}
	
	private boolean isNoGetRequest (IMxRuntimeRequest request) {
		return !isGetRequest(request);
	}
	
	private boolean isHttpStatusSuccess (IMxRuntimeResponse response) {
		return (response.getHttpServletResponse().getStatus() == 200);
	}
	
	private boolean isNoHttpStatusSuccess (IMxRuntimeResponse response) {
		return !isHttpStatusSuccess(response);
	}
}
