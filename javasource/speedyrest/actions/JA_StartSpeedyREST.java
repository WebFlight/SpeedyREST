// This file was generated by Mendix Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package speedyrest.actions;

import com.mendix.core.Core;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import speedyrest.proxies.constants.Constants;
import speedyrest.respositories.CacheRepository;
import speedyrest.services.Cache;
import speedyrest.services.RedisCache;
import speedyrest.usecases.ServeRequestFromCache;

public class JA_StartSpeedyREST extends CustomJavaAction<java.lang.Boolean>
{
	public JA_StartSpeedyREST(IContext context)
	{
		super(context);
	}

	@Override
	public java.lang.Boolean executeAction() throws Exception
	{
		// BEGIN USER CODE
		
		Cache redisCache = new RedisCache(
				Constants.getREDIS_HOST(), 
				Constants.getREDIS_PASSWORD(), 
				Constants.getREDIS_PORT().intValue(), 
				Constants.getREDIS_DATABASE_INDEX().intValue());
		
		CacheRepository cacheRepository = new CacheRepository(redisCache);
		
		Core.addRequestHandler("srest/", new ServeRequestFromCache(cacheRepository));
		
		Core.getLogger("RequestHandlers").info("Registered SpeedyREST requesthandler for 'srest/'");
		return true;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public java.lang.String toString()
	{
		return "JA_StartSpeedyREST";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
