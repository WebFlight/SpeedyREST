package speedyrest.helpers;

import java.util.Date;

import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;

import speedyrest.proxies.ResponseCache;
import speedyrest.respositories.CacheRepository;

public class CacheValidator {
	
	public static boolean isValid(ResponseCache responseCache, CacheRepository cacheRepository, ILogNode logger) throws CoreException {
		long cacheTTL = cacheRepository.cacheTTL();
		if (cacheTTL == 0) {
			return true;
		}
		Date dateTimeCreated = cacheRepository.getDateTimeCreated(responseCache);
		long duration = (System.currentTimeMillis() - dateTimeCreated.getTime())/1000;
		boolean isValid = duration <= cacheTTL;
		logger.debug("Duration: " + duration + " seconds. Valid: " + (isValid));
		return isValid;
	}
	
	public static boolean isNotValid(ResponseCache responseCache, CacheRepository cacheRepository, ILogNode logger) throws CoreException {
		return !CacheValidator.isValid(responseCache, cacheRepository, logger);
	}
	
	public static boolean isFound(ResponseCache responseCache, CacheRepository cacheRepository) {
		if (cacheRepository.getKey(responseCache) != null) {
			return true;
		}
		return false;
	}
	
	public static boolean isNotFound(ResponseCache responseCache, CacheRepository cacheRepository) {
		return !CacheValidator.isFound(responseCache, cacheRepository);
	}
}
