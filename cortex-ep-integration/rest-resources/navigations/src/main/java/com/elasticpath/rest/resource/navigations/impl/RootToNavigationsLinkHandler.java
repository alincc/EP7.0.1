/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.navigations.NavigationsFromRootRelationship;
import com.elasticpath.rest.definition.navigations.NavigationsIdentifier;
import com.elasticpath.rest.id.ResourceIdentifier;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.dispatch.linker.AbstractRelationshipLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Root to Navigations link handler.
 */
@Singleton
@Named("rootToNavigationsLinkHandler")
public class RootToNavigationsLinkHandler extends AbstractRelationshipLinkHandler<LinksEntity> {

	/**
	 * Constructor.
	 *
	 * @param transformerProvider identifier transformer provider
	 * @param resourceContext the resource operation context
	 */
	@Inject
	public RootToNavigationsLinkHandler(
			@Named("identifierTransformerProvider")
			final IdentifierTransformerProvider transformerProvider,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceContext) {
		super(transformerProvider, resourceContext, NavigationsFromRootRelationship.class);
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<LinksEntity> resourceState) {
		return reallyGetLinks();
	}

	@Override
	protected Iterable<ResourceIdentifier> toIdentifiers(final ResourceIdentifier fromIdentifier) {
		return Collections.<ResourceIdentifier>singleton(NavigationsIdentifier.builder()
			.withScope(getScopeIdentifier())
			.build());
	}
}
