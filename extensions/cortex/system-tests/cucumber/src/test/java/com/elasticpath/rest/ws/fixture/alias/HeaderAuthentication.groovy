package com.elasticpath.rest.ws.fixture.alias

import com.elasticpath.rest.ws.client.FluentRelosClient

class HeaderAuthentication extends Authentication {

    private static final String DEFAULT_TEST_SCOPE = "mobee";
    private static final String DEFAULT_TEST_USER_GUID = "95be8397-5262-49e7-bb7b-485a756abfe5";

    def authenticate(def userName, def password, def scope, def role) {
        headerAuthentication(userName, password, scope, role)
    }

    def HeaderAuthentication(final FluentRelosClient client) {
        super(client);
    }

	def authAsAPublicUser() {
        throw new UnsupportedOperationException();
	}

    def authAsAPublicUserOnScope(def scope) {
        throw new UnsupportedOperationException();
    }

    def authAsRegisteredUser() {
        headerRegisteredAuthentication(DEFAULT_TEST_USER_GUID, DEFAULT_TEST_SCOPE)
    }

    def authRegisteredUserByName(def scope, def userId) {
        headerRegisteredAuthentication(userId, scope)
    }

    def invalidateAuthentication() {
        headerInvalidateAuthentication()
    }

    def roleTransitionToRegisteredUser() {
        throw new UnsupportedOperationException();
    }

    def roleTransitionToRegisteredUserByName(def scope, def userName) {
        throw new UnsupportedOperationException();
    }
}
