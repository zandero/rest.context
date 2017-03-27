package com.zandero.rest.test;

import com.zandero.rest.annotations.NotNullAndIgnoreUnknowns;

/**
 * Simple user JSON object
 */
@NotNullAndIgnoreUnknowns
public class UserJSON {

	public String user;

	public String session;

	private UserJSON() {

	}

	public UserJSON(String username, String sessionId) {

		user = username;
		session = sessionId;
	}
}
