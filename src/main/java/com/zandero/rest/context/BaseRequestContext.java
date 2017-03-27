package com.zandero.rest.context;

import com.google.inject.servlet.RequestScoped;
import com.zandero.http.RequestUtils;
import com.zandero.utils.UrlUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Base request context to be expanded
 */
@RequestScoped
public abstract class BaseRequestContext implements RequestContext {

	private final int port;

	private final String scheme;

	private final String domainName;

	private final String clientIpAddress;

	private final Map<String, String> headers;

	private final String query;

	private final String path;

	@Inject
	public BaseRequestContext(HttpServletRequest servletRequest) {

		scheme = RequestUtils.getScheme(servletRequest);
		port = servletRequest.getServerPort();

		domainName = UrlUtils.resolveDomain(servletRequest.getServerName());
		path = servletRequest.getContextPath() + servletRequest.getPathInfo();

		clientIpAddress = RequestUtils.getClientIpAddress(servletRequest);
		headers = getHeaders(servletRequest);
		query = servletRequest.getQueryString();
	}

	@Override
	public Map<String, String> getHeaders(HttpServletRequest servletRequest) {

		Map<String, String> map = new HashMap<>();
		Enumeration e = servletRequest.getHeaderNames();

		if (e != null) {
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				String value = servletRequest.getHeader(name);

				map.put(name, value);
			}
		}

		return map;
	}

	@Override
	public String getScheme() {

		return scheme;
	}

	@Override
	public String getDomainName() {

		return domainName;
	}

	@Override
	public int getPort() {

		return port;
	}

	@Override
	public String getClientIP() {

		return clientIpAddress;
	}

	@Override
	public String getHeader(String name) {

		if (headers == null) {
			return null;
		}

		return headers.get(name);
	}

	@Override
	public Map<String, String> getHeaders() {

		return headers;
	}

	@Override
	public String getQuery() {

		return query;
	}

	@Override
	public String getBaseUrl() {

		return UrlUtils.composeUrl(scheme, domainName, port);
	}

	@Override
	public String getPath() {

		return path;
	}
}
