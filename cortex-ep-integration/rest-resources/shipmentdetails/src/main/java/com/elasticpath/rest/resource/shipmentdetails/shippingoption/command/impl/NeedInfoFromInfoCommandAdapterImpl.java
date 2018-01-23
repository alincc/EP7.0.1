/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.needinfo.NeedInfoFromInfoCommand;
import com.elasticpath.rest.schema.ResourceLink;

/**
 * Determines if there are any matching needinfos for the given URI and needed rel. Adapts to the somewhat more complicated API of
 * {@link NeedInfoFromInfoCommand}, but hides the complexity from callers.
 */
@Named
@Singleton
public class NeedInfoFromInfoCommandAdapterImpl implements NeedInfoHandler {
	private final Provider<NeedInfoFromInfoCommand.Builder> needInfoProvider;

	/**
	 * Constructor.
	 * @param needInfoProvider the {@link Provider} of {@link NeedInfoFromInfoCommand.Builder}s.
	 */
	@Inject
	public NeedInfoFromInfoCommandAdapterImpl(
			@Named("needInfoFromInfoCommandBuilder")
			final Provider<NeedInfoFromInfoCommand.Builder> needInfoProvider) {
		this.needInfoProvider = needInfoProvider;
	}

	@Override
	public Collection<ResourceLink> getNeedInfoLinksForInfo(final String infoUri, final String rel) {
		Preconditions.checkNotNull(infoUri, "info uri must not be null");
		Preconditions.checkNotNull(rel, "rel must not be null");

		ExecutionResult<Collection<ResourceLink>> needInfoResult = needInfoProvider.get()
				.setInfoUri(infoUri)
				.setNeededRel(rel)
				.build()
				.execute();

		Collection<ResourceLink> result;
		if (needInfoResult.isSuccessful()) {
			result = needInfoResult.getData();
		} else {
			result = Collections.emptyList();
		}
		return result;
	}
}