/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.registrations.relationships;

import javax.inject.Inject;

import rx.Observable;

import com.elasticpath.rest.definition.registrations.NewAccountRegistrationFormIdentifier;
import com.elasticpath.rest.definition.registrations.RegistrationFromRootRelationship;
import com.elasticpath.rest.helix.data.annotation.UserScopes;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Implementation for registration-from-root relationship.
 */
public class RegistrationFromRootRelationshipImpl implements RegistrationFromRootRelationship.LinkTo {

	@Inject
	@UserScopes
	private Iterable<String> scopes;

	@Override
	public Observable<NewAccountRegistrationFormIdentifier> onLinkTo() {
		return Observable.from(scopes)
				.map(StringIdentifier::of)
				.map(scopeId -> NewAccountRegistrationFormIdentifier.builder()
						.withScope(scopeId)
						.build())
				.first();
	}
}
