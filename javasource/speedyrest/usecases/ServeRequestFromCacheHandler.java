package speedyrest.usecases;

import com.mendix.core.Core;
import com.mendix.externalinterface.connector.RequestHandler;
import com.mendix.logging.ILogNode;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;

import restservices.publish.RestServiceHandler;
import speedyrest.helpers.CacheValidator;
import speedyrest.respositories.CacheRepository;

public class ServeRequestFromCacheHandler extends RequestHandler {

	private CacheRepository cacheRepository;
	private CacheValidator cacheValidator;
	private RestServiceHandler restServiceHandler;
	
	private ILogNode logger;

	public ServeRequestFromCacheHandler(CacheRepository cacheRepository, CacheValidator cacheValidator, RestServiceHandler restServiceHandler) {
		this.cacheRepository = cacheRepository;
		this.cacheValidator = cacheValidator;
		this.restServiceHandler = restServiceHandler;
	}

	@Override
	protected void processRequest(IMxRuntimeRequest request, IMxRuntimeResponse response, String path) throws Exception {
		logger = Core.getLogger("SpeedyREST");
		ServeRequestFromCache serveRequestFromCache = new ServeRequestFromCache(cacheRepository, cacheValidator, logger);
		serveRequestFromCache.serveRequest(request, response, path, restServiceHandler);
	}
	
}
