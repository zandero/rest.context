package com.zandero.rest.test;

import org.jboss.resteasy.plugins.guice.RequestScoped;
import com.zandero.http.RequestUtils;
import com.zandero.rest.context.BaseRequestContext;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Simple context ... relying on the fact that a session id is present or not
 */
@RequestScoped
public class TestRequestContext extends BaseRequestContext {

	public static final String SESSION_HEADER = "X-SessionId";

	private final String session;

	@Inject
	public TestRequestContext(HttpServletRequest servletRequest) {

		super(servletRequest);

		session = RequestUtils.getHeader(servletRequest, SESSION_HEADER);
	}

	@Override
	public Principal getUserPrincipal() {

		return () -> session;
	}

	@Override
	public boolean isUserInRole(String s) {

		return session != null;
	}

	@Override
	public boolean isSecure() {

		return session != null;
	}

	@Override
	public String getAuthenticationScheme() {

		return null;
	}

	public String getSession() {

		return session;
	}
}
