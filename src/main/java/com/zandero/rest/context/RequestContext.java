package com.zandero.rest.context;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;
import java.util.Map;

/**
 *
 */
public interface RequestContext extends SecurityContext {

	Map<String, String> getHeaders(HttpServletRequest servletRequest);

	String getScheme();

	String getDomainName();

	int getPort();

	String getClientIP();

	String getHeader(String name);

	Map<String, String> getHeaders();

	String getQuery();

	String getBaseUrl();

	String getPath();
}
