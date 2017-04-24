package speedyrest.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;

import speedyrest.helpers.CacheValidator;
import speedyrest.helpers.ServeRequestFromCache;
import speedyrest.proxies.ResponseCache;
import speedyrest.respositories.CacheRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServeRequestFromCacheTest {
	
	private ResponseCache responseCache;
	private CacheRepository cacheRepository;
	private ILogNode logger;
	private IMxRuntimeRequest request;
	private IMxRuntimeResponse response;
	
	@Before
	public void setUp() throws Exception {
		this.responseCache = mock(ResponseCache.class);
		this.cacheRepository = mock(CacheRepository.class);
		this.logger = mock(ILogNode.class);
		this.request = mock(IMxRuntimeRequest.class);
		this.response = mock(IMxRuntimeResponse.class);
	}
	
	@Test
	public void testProcessRequest() throws Exception {
		ServeRequestFromCache serveRequestFromCache = new ServeRequestFromCache(cacheRepository, logger);
		String path = "testpath";
		serveRequestFromCache.serveRequest(request, response, path);
	}

}
