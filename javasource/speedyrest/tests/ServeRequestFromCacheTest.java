package speedyrest.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.mendix.logging.ILogNode;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;

import restservices.publish.RestServiceHandler;
import speedyrest.entities.SpeedyHeaders;
import speedyrest.entities.SpeedyResponse;
import speedyrest.helpers.CacheValidator;
import speedyrest.proxies.ResponseCache;
import speedyrest.respositories.CacheRepository;
import speedyrest.usecases.ServeRequestFromCache;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServeRequestFromCacheTest {
	
	private ResponseCache responseCache;
	private CacheRepository cacheRepository;
	private CacheValidator cacheValidator;
	private ILogNode logger;
	private IMxRuntimeRequest request;
	private IMxRuntimeResponse response;
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	private Map<String, String[]> parameterMap;
	private HashMap<String, String> headerMap;
	private Set<Entry<String, String>> headerMapEntrySet;
	private Map.Entry<String, String> headerMapEntry;
	private Iterator<Entry<String, String>> headerIterator;
	private RestServiceHandler restServiceHandler;
	private SpeedyResponse speedyResponse;
	private SpeedyHeaders speedyHeaders;
	private List<Cookie> cookieList;
	private Iterator<Cookie> cookieIterator;
	private Cookie cookie;
	private OutputStream outputStream;
	
	
	@Before
	public void setUp() throws Exception {
		this.responseCache = mock(ResponseCache.class);
		this.cacheRepository = mock(CacheRepository.class);
		this.cacheValidator = mock(CacheValidator.class);
		this.logger = mock(ILogNode.class);
		this.request = mock(IMxRuntimeRequest.class);
		this.response = mock(IMxRuntimeResponse.class);
		this.httpServletRequest = mock(HttpServletRequest.class);
		this.httpServletResponse = mock(HttpServletResponse.class);
		this.parameterMap = mock(Map.class);
		this.headerMap = mock(HashMap.class);
		this.speedyResponse = mock(SpeedyResponse.class);
		this.speedyHeaders = mock(SpeedyHeaders.class);
		this.headerMapEntrySet = mock(Set.class);
		this.headerMapEntry = mock(Map.Entry.class);
		this.headerIterator = mock(Iterator.class);
		this.cookieList = mock(List.class);
		this.cookieIterator = mock(Iterator.class);
		this.cookie = mock(Cookie.class);
		this.outputStream = mock(OutputStream.class);
	}
	
	@Test
	public void testProcessRequestCacheNotValid() throws Exception {
		String path = "testpath";
		String cacheKey = "testpath{}";
		
		when(request.getHttpServletRequest()).thenReturn(httpServletRequest);
		when(httpServletRequest.getParameterMap()).thenReturn(parameterMap);
		when(parameterMap.toString()).thenReturn("{}");
		when(cacheRepository.find(cacheKey)).thenReturn(responseCache);
		when(cacheRepository.createResponseCache(null)).thenReturn(responseCache);
		when(cacheValidator.isNotValid(responseCache, cacheRepository, logger)).thenReturn(true);
		when(cacheValidator.isNotFound(responseCache, cacheRepository)).thenReturn(false);
		
		ServeRequestFromCache serveRequestFromCache = new ServeRequestFromCache(cacheRepository, cacheValidator, logger);
		
		serveRequestFromCache.serveRequest(request, response, path, cacheKey, restServiceHandler, responseCache, speedyResponse);
		
		verify(cacheRepository, times(1)).clearCacheEntry(responseCache);
		verify(cacheRepository, times(1)).createResponseCache(null);
	}
	
	@Test
	public void testProcessRequestCacheValidServeFromCacheText() throws Exception {
		String path = "testpath";
		String cacheKey = "testpath{}";
		
		when(request.getHttpServletRequest()).thenReturn(httpServletRequest);
		when(httpServletRequest.getParameterMap()).thenReturn(parameterMap);
		when(parameterMap.toString()).thenReturn("{}");
		when(cacheRepository.find(cacheKey)).thenReturn(responseCache);
		when(cacheRepository.createResponseCache(null)).thenReturn(responseCache);
		when(cacheRepository.getHeaders(responseCache)).thenReturn(speedyHeaders);
		when(cacheRepository.getCookies(responseCache)).thenReturn(cookieList);
		when(cacheRepository.getContent(responseCache)).thenReturn("TestContent");
		when(speedyHeaders.getHeaders()).thenReturn(headerMap);
		when(cacheValidator.isNotValid(responseCache, cacheRepository, logger)).thenReturn(false);
		when(cacheValidator.isNotFound(responseCache, cacheRepository)).thenReturn(false);
		when(cacheValidator.isFound(responseCache, cacheRepository)).thenReturn(true);
		when(headerMap.entrySet()).thenReturn(headerMapEntrySet);
		when(headerMapEntrySet.iterator()).thenReturn(headerIterator);
		when(headerIterator.hasNext()).thenReturn(true, false);
		when(headerIterator.next()).thenReturn(headerMapEntry);
		when(headerMapEntry.getKey()).thenReturn("Testkey");
		when(headerMapEntry.getValue()).thenReturn("Testvalue");
		when(response.getHttpServletResponse()).thenReturn(httpServletResponse);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(cookieList.iterator()).thenReturn(cookieIterator);
		when(cookieIterator.hasNext()).thenReturn(true, false);
		when(cookieIterator.next()).thenReturn(cookie);
		
		ServeRequestFromCache serveRequestFromCache = new ServeRequestFromCache(cacheRepository, cacheValidator, logger);
		
		serveRequestFromCache.serveRequest(request, response, path, cacheKey, restServiceHandler, responseCache, speedyResponse);
		
		verify(cacheRepository, times(1)).getCookies(responseCache);
		verify(cacheRepository, times(1)).getHeaders(responseCache);
		verify(httpServletResponse, times(1)).addCookie(cookie);
		verify(httpServletResponse, times(1)).addHeader(headerMapEntry.getKey(), headerMapEntry.getValue());
		verify(cacheRepository, times(1)).getContent(responseCache);
	}
	
	@Test
	public void testProcessRequestCacheValidServeFromCacheBinary() throws Exception {
		String path = "testpath";
		String cacheKey = "testpath{}";
		
		when(request.getHttpServletRequest()).thenReturn(httpServletRequest);
		when(httpServletRequest.getParameterMap()).thenReturn(parameterMap);
		when(parameterMap.toString()).thenReturn("{}");
		when(cacheRepository.find(cacheKey)).thenReturn(responseCache);
		when(cacheRepository.createResponseCache(null)).thenReturn(responseCache);
		when(cacheRepository.getHeaders(responseCache)).thenReturn(speedyHeaders);
		when(cacheRepository.getCookies(responseCache)).thenReturn(cookieList);
		when(cacheRepository.getContent(responseCache)).thenReturn(null);
		when(speedyHeaders.getHeaders()).thenReturn(headerMap);
		when(cacheValidator.isNotValid(responseCache, cacheRepository, logger)).thenReturn(false);
		when(cacheValidator.isNotFound(responseCache, cacheRepository)).thenReturn(false);
		when(cacheValidator.isFound(responseCache, cacheRepository)).thenReturn(true);
		when(headerMap.entrySet()).thenReturn(headerMapEntrySet);
		when(headerMapEntrySet.iterator()).thenReturn(headerIterator);
		when(headerIterator.hasNext()).thenReturn(true, false);
		when(headerIterator.next()).thenReturn(headerMapEntry);
		when(headerMapEntry.getKey()).thenReturn("Testkey");
		when(headerMapEntry.getValue()).thenReturn("Testvalue");
		when(response.getHttpServletResponse()).thenReturn(httpServletResponse);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(cookieList.iterator()).thenReturn(cookieIterator);
		when(cookieIterator.hasNext()).thenReturn(true, false);
		when(cookieIterator.next()).thenReturn(cookie);
		
		ServeRequestFromCache serveRequestFromCache = new ServeRequestFromCache(cacheRepository, cacheValidator, logger);
		
		serveRequestFromCache.serveRequest(request, response, path, cacheKey, restServiceHandler, responseCache, speedyResponse);
		
		verify(cacheRepository, times(1)).getCookies(responseCache);
		verify(cacheRepository, times(1)).getHeaders(responseCache);
		verify(httpServletResponse, times(1)).addCookie(cookie);
		verify(httpServletResponse, times(1)).addHeader(headerMapEntry.getKey(), headerMapEntry.getValue());
		verify(cacheRepository, times(1)).getContent(responseCache);
		verify(cacheRepository, times(1)).getBinaryContent(responseCache, outputStream);
	}
	
	@Test(expected = NullPointerException.class)
	public void testProcessRequestCacheValidServeFromRest() throws Exception {
		String path = "testpath";
		String cacheKey = "testpath{}";
		
		when(request.getHttpServletRequest()).thenReturn(httpServletRequest);
		when(httpServletRequest.getParameterMap()).thenReturn(parameterMap);
		when(parameterMap.toString()).thenReturn("{}");
		when(cacheRepository.find(cacheKey)).thenReturn(responseCache);
		when(cacheRepository.createResponseCache(null)).thenReturn(responseCache);
		when(cacheRepository.getHeaders(responseCache)).thenReturn(speedyHeaders);
		when(cacheRepository.getCookies(responseCache)).thenReturn(cookieList);
		when(cacheRepository.getContent(responseCache)).thenReturn(null);
		when(speedyHeaders.getHeaders()).thenReturn(headerMap);
		when(cacheValidator.isNotValid(responseCache, cacheRepository, logger)).thenReturn(false);
		when(cacheValidator.isNotFound(responseCache, cacheRepository)).thenReturn(true);
		when(cacheValidator.isFound(responseCache, cacheRepository)).thenReturn(false);
		when(headerMap.entrySet()).thenReturn(headerMapEntrySet);
		when(headerMapEntrySet.iterator()).thenReturn(headerIterator);
		when(headerIterator.hasNext()).thenReturn(true, false);
		when(headerIterator.next()).thenReturn(headerMapEntry);
		when(headerMapEntry.getKey()).thenReturn("Testkey");
		when(headerMapEntry.getValue()).thenReturn("Testvalue");
		when(response.getHttpServletResponse()).thenReturn(httpServletResponse);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(cookieList.iterator()).thenReturn(cookieIterator);
		when(cookieIterator.hasNext()).thenReturn(true, false);
		when(cookieIterator.next()).thenReturn(cookie);
		
		ServeRequestFromCache serveRequestFromCache = new ServeRequestFromCache(cacheRepository, cacheValidator, logger);
		
		serveRequestFromCache.serveRequest(request, response, path, cacheKey, restServiceHandler, responseCache, speedyResponse);
	}

}
