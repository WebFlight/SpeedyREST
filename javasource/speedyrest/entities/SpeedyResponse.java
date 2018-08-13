package speedyrest.entities;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;

import speedyrest.proxies.ResponseCache;
import speedyrest.respositories.CacheRepository;

public class SpeedyResponse implements IMxRuntimeResponse{
	
	private IMxRuntimeResponse response;
	private SpeedyHttpServletResponse speedyHttpServletResponse;
	
	public SpeedyResponse(IMxRuntimeRequest request, IMxRuntimeResponse response, ResponseCache responseCache, CacheRepository cacheRepository) throws IOException {
		this.response = response;
		speedyHttpServletResponse = new SpeedyHttpServletResponse(responseCache, this.response.getHttpServletResponse(), cacheRepository, request, response);
	}

	@Override
	public void addCookie(String arg0, String arg1) 
	{
		this.response.addCookie(arg0, arg1);
	}

	@Override
	public void addCookie(String arg0, String arg1, boolean arg2) {
		this.response.addCookie(arg0, arg1, arg2);
	}

	@Override
	public void addCookie(String arg0, String arg1, String arg2, String arg3, int arg4) {
		this.response.addCookie(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public void addCookie(String arg0, String arg1, String arg2, String arg3, int arg4, boolean arg5) {
		this.response.addCookie(arg0, arg1, arg2, arg3, arg4, arg5);		
	}

	@Override
	public void addHeader(String arg0, String arg1) {
		this.response.addHeader(arg0, arg1);
	}

	@Override
	public String getCharacterEncoding() {
		return this.response.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return this.response.getContentType();
	}

	@Override
	public HttpServletResponse getHttpServletResponse() {
		return speedyHttpServletResponse;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return this.response.getOutputStream();
	}

	@Override
	public Writer getWriter() throws IOException {
		return this.response.getWriter();
	}

	@Override
	public void sendError(String arg0) {
		this.response.sendError(arg0);
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		this.response.setCharacterEncoding(arg0);
	}

	@Override
	public void setContentType(String arg0) {
		this.response.setContentType(arg0);
	}

	@Override
	public void setStatus(int arg0) {
		this.response.setStatus(arg0);	
	}

	@Override
	public void setStatus(int arg0, String arg1) {
		this.response.setStatus(arg0, arg1);
	}

}
