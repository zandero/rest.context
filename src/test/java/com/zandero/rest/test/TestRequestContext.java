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

			/**
			 * @return user making the request or null if not known
			 */
		@Override
		public Principal getUserPrincipal() {

			return () -> session;
		}

		/**
		 * Checks if user is in role
		 * @param role
		 * @return true if user is in role, false if not
		 */
		@Override
		public boolean isUserInRole(String role) {

			return session != null;
		}

		/**
		 * @return true if call is secure, false if not
		 */
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
