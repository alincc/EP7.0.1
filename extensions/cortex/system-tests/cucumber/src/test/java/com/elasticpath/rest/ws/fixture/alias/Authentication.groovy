package com.elasticpath.rest.ws.fixture.alias

import com.elasticpath.rest.ws.client.FluentRelosClient
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient

abstract class Authentication {

    private FluentRelosClient client;

    def Authentication(final FluentRelosClient client) {
        this.client = client

        this.client.alias(this.&authenticate)
        this.client.alias(this.&roleTransitionToRegisteredUser)
        this.client.alias(this.&roleTransitionToRegisteredUserByName)
        this.client.alias(this.&invalidateAuthentication)
        this.client.alias(this.&authAsRegisteredUser)
        this.client.alias(this.&authRegisteredUserByName)
        this.client.alias(this.&authAsAPublicUser)
        this.client.alias(this.&authAsAPublicUserOnScope)
    }

    def oauthAuthentication(def username, def password, def scope, def role) {
        client.login(
                username: username,
                password: password,
                grant_type: "password",
                scope: scope,
                role: role)
    }

    def oauthPublicAuthentication(def scope) {
        client.login(
                grant_type: "password",
                scope: scope,
                role: "PUBLIC")
    }

    def oauthRegisteredAuthentication(def userName, def password, def scope) {
        client.login(
                username: userName,
                password: password,
                grant_type: "password",
                scope: scope,
                role: "REGISTERED")
    }

    def oauthRoleTransitionAuthentication(def userName, def password, def scope) {
        client.roleTransitionLogin(
                username: userName,
                password: password,
                grant_type: "password",
                scope: scope,
                role: "REGISTERED")
    }

    def oauthInvalidateAuthentication() {
        client.DELETE("/oauth2/tokens")
    }

    def headerRegisteredAuthentication(def userName, def scope) {
        client.withAuthenticationHeaders(
                userGuid: userName,
                scopes: scope,
                roles: "REGISTERED")
    }

    def headerAuthentication(def userName, def password, def scope, def role) {
        client.withAuthenticationHeaders(
                userGuid: userName,
                scopes: scope,
                roles: role)
    }

    def headerRoleTransitionAuthentication(def publicUser, def registeredUser, def scope) {
        client.authRegisteredUserByName(scope, registeredUser)
        client.POST("events/$scope", [
                type       : "events/roleTransition",
                oldUserGuid: publicUser,
                newUserGuid: registeredUser,
                oldRole    : "PUBLIC",
                newRole    : "REGISTERED"
        ])
        assert client.response.status == 200
    }

    def headerInvalidateAuthentication() {

    }

    abstract def authenticate(def userName, def password, def scope, def role);

    abstract def authAsAPublicUser();

    abstract def authAsAPublicUserOnScope(def scope);

    abstract def authAsRegisteredUser();

    abstract def authRegisteredUserByName(def scope, def userId);

    abstract def roleTransitionToRegisteredUser();

    abstract def roleTransitionToRegisteredUserByName(def scope, def userName);

    abstract def invalidateAuthentication();
}
