package com.zandero.rest.test;

import com.google.inject.AbstractModule;
import com.zandero.rest.context.AuthorizationFilter;
import com.zandero.rest.context.RequestContext;

/**
 *
 */
public class RequestScopeModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(RequestContext.class).to(TestRequestContext.class);
		bind(AuthorizationFilter.class);
	}
}
