package com.zandero.rest;

import com.zandero.rest.test.BaseRestTest;
import com.zandero.rest.test.TestRequestContext;
import com.zandero.rest.test.UserJSON;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Simple authorization context test
 */
public class TestAuthorizationContext extends BaseRestTest {

	//
	@Test
	public void invalidLogTest() {

		Response response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/login")
			.request()
			.post(null);

		assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatus());
	}

	@Test
	public void loginTest() {

		Response response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/login?user=user&password=password")
			.request()
			.post(null);

		assertEquals(HttpStatus.SC_OK, response.getStatus());

		UserJSON user = response.readEntity(UserJSON.class);
		assertEquals("user", user.user);
		assertNotNull(user.session);


		// ok make call to private URL with session Id
		response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/private")
			.request()
			.header(TestRequestContext.SESSION_HEADER, user.session)
			.get();

		assertEquals(HttpStatus.SC_OK, response.getStatus());
		assertEquals("user", user.user);


		// make call with invalid session id
		response = new ResteasyClientBuilder()
			.build()
			.target(ROOT_URL + "/rest/test/private")
			.request()
			.header(TestRequestContext.SESSION_HEADER, "dummy")
			.get();

		assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
		String output = response.readEntity(String.class);
		assertEquals("{\"code\":404,\"message\":\"Session id unknown!\"}", output);
	}
}
