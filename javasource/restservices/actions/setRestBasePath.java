// This file was generated by Mendix Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package restservices.actions;

import restservices.RestServices;
import restservices.publish.RestServiceHandler;
import restservices.util.Utils;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;

public class setRestBasePath extends CustomJavaAction<java.lang.Boolean>
{
	private java.lang.String basePath;

	public setRestBasePath(IContext context, java.lang.String basePath)
	{
		super(context);
		this.basePath = basePath;
	}

	@Override
	public java.lang.Boolean executeAction() throws Exception
	{
		// BEGIN USER CODE
		if (RestServiceHandler.isStarted())
			throw new IllegalStateException("RestService module has already started. Please call 'setRestBasePath' before starting the module");
		if (basePath == null || basePath.trim().isEmpty())
			throw new IllegalArgumentException("Basepath cannot be empty");
		
		basePath = Utils.appendSlashToUrl(basePath);
		if (!basePath.matches("^[A-Za-z0-9_-]+/$"))
			throw new IllegalArgumentException("Not a valid basepath: '" + basePath + "': should be a single identifier");
		
		RestServices.PATH_REST = basePath;
		return true;		
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public java.lang.String toString()
	{
		return "setRestBasePath";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
