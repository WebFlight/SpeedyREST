package speedyrest.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;

import restservices.publish.RestServiceHandler;
import speedyrest.helpers.CacheValidator;
import speedyrest.proxies.ResponseCache;
import speedyrest.respositories.CacheRepository;
import speedyrest.usecases.ServeRequestFromCache;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ServeRequestFromCacheTest {
	
	private ResponseCache responseCache;
	private CacheRepository cacheRepository;
	private CacheValidator cacheValidator;
	private ILogNode logger;
	private IMxRuntimeRequest request;
	private IMxRuntimeResponse response;
	private HttpServletRequest httpServletRequest;
	private Map<String, String[]> parameterMap;
	private RestServiceHandler restServiceHandler;
	
	@Before
	public void setUp() throws Exception {
		this.responseCache = mock(ResponseCache.class);
		this.cacheRepository = mock(CacheRepository.class);
		this.cacheValidator = mock(CacheValidator.class);
		this.logger = mock(ILogNode.class);
		this.request = mock(IMxRuntimeRequest.class);
		this.response = mock(IMxRuntimeResponse.class);
		this.httpServletRequest = mock(HttpServletRequest.class);
		this.parameterMap = mock(Map.class);
		this.restServiceHandler = mock(RestServiceHandler.class);
	}
	
	@Test
	public void testProcessRequest() throws Exception {
		ServeRequestFromCache serveRequestFromCache = new ServeRequestFromCache(cacheRepository, cacheValidator, logger);
		String path = "testpath";
		
		when(request.getHttpServletRequest()).thenReturn(httpServletRequest);
		when(httpServletRequest.getParameterMap()).thenReturn(parameterMap);
		when(parameterMap.toString()).thenReturn("{}");
		
		serveRequestFromCache.serveRequest(request, response, path, restServiceHandler);
		
		String cacheKey = path + request.getHttpServletRequest().getParameterMap().toString();
		verify(cacheRepository.find(cacheKey), times(1));
	}

}
