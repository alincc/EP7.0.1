package com.elasticpath.rest.ws.fixture.alias

import com.elasticpath.rest.ws.client.FluentRelosClient

class OAuth2Authentication extends Authentication {

//    TODO Make test scope as a cucumber step so customer project can change scope.
    private static final String DEFAULT_TEST_SCOPE = "mobee";
    private static final String DEFAULT_TEST_USER = "itest.default.user@elasticpath.com";
    private static final String DEFAULT_TEST_USER_PASSWORD = "password";

    public OAuth2Authentication(final FluentRelosClient client) {
        super(client);
    }

    def authenticate(def userName, def password, def scope, def role) {
        oauthAuthentication(userName, password, scope, role)
    }

    def authAsAPublicUser() {
        oauthPublicAuthentication(DEFAULT_TEST_SCOPE)
	}

    def authAsAPublicUserOnScope(def scope) {
        oauthPublicAuthentication(scope)
    }

    def authAsRegisteredUser() {
        authRegisteredUserByName(DEFAULT_TEST_SCOPE, DEFAULT_TEST_USER)
    }

    def authRegisteredUserByName(def scope, def userId) {
        oauthRegisteredAuthentication(userId, DEFAULT_TEST_USER_PASSWORD, scope)
    }

    def roleTransitionToRegisteredUser() {
        roleTransitionToRegisteredUserByName(DEFAULT_TEST_SCOPE, DEFAULT_TEST_USER)
    }

    def roleTransitionToRegisteredUserByName(def scope, def userName) {
        oauthRoleTransitionAuthentication(userName, DEFAULT_TEST_USER_PASSWORD, scope)
    }

    def invalidateAuthentication() {
        oauthInvalidateAuthentication()
    }
}
