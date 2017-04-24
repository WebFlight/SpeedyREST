package speedyrest.usecases;

import com.mendix.core.Core;
import com.mendix.externalinterface.connector.RequestHandler;
import com.mendix.logging.ILogNode;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;

import speedyrest.helpers.ServeRequestFromCache;
import speedyrest.respositories.CacheRepository;

public class ServeRequestFromCacheHandler extends RequestHandler {

	private CacheRepository cacheRepository;
	private ILogNode logger;

	public ServeRequestFromCacheHandler(CacheRepository cacheRepository) {
		this.cacheRepository = cacheRepository;
	}

	@Override
	protected void processRequest(IMxRuntimeRequest request, IMxRuntimeResponse response, String path) throws Exception {
		logger = Core.getLogger("SpeedyREST");
		ServeRequestFromCache serveRequestFromCache = new ServeRequestFromCache(cacheRepository, logger);
		serveRequestFromCache.serveRequest(request, response, path);
	}
}
