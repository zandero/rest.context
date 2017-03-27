package com.zandero.rest.context;

import com.zandero.rest.RestException;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.core.interception.jaxrs.ContainerResponseContextImpl;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@javax.ws.rs.ext.Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthorizationFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private Provider<RequestContext> ctxProvider;

	@Inject
	public AuthorizationFilter(Provider<RequestContext> requestContextProvider) {
		ctxProvider = requestContextProvider;
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		requestContext.setSecurityContext(ctxProvider.get());
	}

	/**
	 * NOTE: this is a hack ... we are actively listening for the RoleBaseSecurityFilter output
	 * as no exception is thrown in order to create desired JSON output (exception wrapper)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

		// check if responseContext holds BuiltResponse with 403 ... to transform into JSON
		if (responseContext instanceof ContainerResponseContextImpl &&
			responseContext.getStatus() == HttpStatus.SC_FORBIDDEN) {

			ContainerResponseContextImpl ctx = (ContainerResponseContextImpl) responseContext;
			if (ctx.getEntity() instanceof String) {
				String entity = (String) ctx.getEntity();

				if (entity.equals("Access forbidden: role not allowed")) {  // see RoleBaseSecurityFilter output
					RestException wrapper = new RestException(HttpStatus.SC_FORBIDDEN, entity);
					ctx.setEntity(wrapper.toString(), null, MediaType.APPLICATION_JSON_TYPE);
				}
			}
		}
	}
}
