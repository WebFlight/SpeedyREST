package speedyrest.usecases;

import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;

import com.mendix.externalinterface.connector.RequestHandler;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;

import restservices.publish.RestServiceHandler;
import speedyrest.entities.SpeedyCacheEntity;
import speedyrest.entities.SpeedyCacheFile;
import speedyrest.entities.SpeedyCacheText;
import speedyrest.respositories.CacheRepository;
import speedyrest.services.Cache;
import speedyrest.services.SpeedyResponse;
import speedyrest.services.SpeedyResponseCacheInjector;

public class ServeRequestFromCache extends RequestHandler {

	private SpeedyResponse speedyResponse;
	private CacheRepository cacheRepository;

	public ServeRequestFromCache(CacheRepository cacheRepository) {
		this.cacheRepository = cacheRepository;
	}

	@Override
	protected void processRequest(IMxRuntimeRequest request, IMxRuntimeResponse response, String path) throws Exception {

		String cacheKey = path + request.getHttpServletRequest().getParameterMap().toString();
		SpeedyCacheEntity speedyCacheEntity = cacheRepository.find(cacheKey);
		
		if (!speedyCacheEntity.getCacheKey().isEmpty()) {
			
			setHeaders(response, speedyCacheEntity);
			setCookies(response, speedyCacheEntity);
			
			if (speedyCacheEntity instanceof SpeedyCacheFile) {
				Map<String, byte[]> fileMap = speedyCacheEntity.getFileParts();
				Map<String, String> fileLengthMap = speedyCacheEntity.getFilePartLengths();
				
				for (int i = 0; i < fileMap.size(); i++) {
					byte[] b = fileMap.get("filepartcontent" + i);
					int len = Integer.valueOf(fileLengthMap.get("filepartlength" + i));
					response.getOutputStream().write(b, 0, len);
				}
			}
			
			if (speedyCacheEntity instanceof SpeedyCacheText) {
				response.getOutputStream().write(((SpeedyCacheText) speedyCacheEntity).getContent().getBytes());
			}
			return;
		}

		RestServiceHandler handler = new RestServiceHandler();
		SpeedyCacheText speedyCacheTextObject = new SpeedyCacheText(cacheKey);
		speedyResponse = new SpeedyResponse(request, response, speedyCacheTextObject, this.cacheDriver);
		handler.processRequest(request, speedyResponse, path);
		return;
	}

	private void setCookies(IMxRuntimeResponse response, SpeedyCacheEntity speedyCacheEntity) {
		Iterator<Cookie> cookies = speedyCacheEntity.getCookies().iterator();
		while(cookies.hasNext()) {
			Cookie cookie = cookies.next();
			response.getHttpServletResponse().addCookie(cookie);
		}
	}

	private void setHeaders(IMxRuntimeResponse response, SpeedyCacheEntity speedyCacheEntity) {
		Map<String, String> headerMap = speedyCacheEntity.getHeaders().getHeaders();
		java.util.Iterator<Entry<String, String>> headerIterator = headerMap.entrySet().iterator();
		while (headerIterator.hasNext()) {
			Map.Entry<String, String> header = headerIterator.next();
			response.getHttpServletResponse().addHeader(header.getKey(), header.getValue());
		}
	}
}
