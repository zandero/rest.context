package com.zandero.rest.test;

import com.zandero.utils.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

/**
 * Simple REST API to test RestFiltering ...
 * each REST call is for a different test
 */
@Path("/test")
@Singleton
public class TestRestApi {

	private final static Logger log = LoggerFactory.getLogger(TestRestApi.class);

	// static session holder for testing purposes only
	private final static HashMap<String, UserJSON> sessions = new HashMap<>();

	private final Provider<TestRequestContext> ctxProvider;

	@Inject
	public TestRestApi(Provider<TestRequestContext> contextProvider) {

		sessions.clear();
		ctxProvider = contextProvider;
	}

	/**
	 * Simple rest to test interface is up and running
	 *
	 * @return 200 ping
	 */
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public UserJSON login(@QueryParam("user") String username, @QueryParam("password") String password) {

		// TODO: simulate users in different roles

		// return user with session or throws exception otherwise
		if ("user".equals(username) && "password".equals(password)) {
			// create new session
			String sessionId = KeyGenerator.generateString(7);
			UserJSON user = new UserJSON(username, sessionId);

			// add user into sessions
			sessions.put(sessionId, user);

			// return user with session ...
			return user;
		}

		throw new NotAuthorizedException("Invalid username or password!");
	}

	/**
	 * Only accessible if user has a valid session
	 * Expecting to find session is HTTP header
	 *
	 * @return user info
	 *
	 * @throws Exception access not allowed
	 */
	@GET
	@RolesAllowed("User")
	@Path("/private")
	@Produces(MediaType.APPLICATION_JSON)
	public UserJSON getUserInfo() {

		// only
		String sessionId = ctxProvider.get().getSession();

		UserJSON found = sessions.get(sessionId);
		if (found == null) {
			throw new NotFoundException("Session id unknown!");
		}

		return found;
	}
}
