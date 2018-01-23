/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.geographies.CountriesIdentifier;
import com.elasticpath.rest.definition.geographies.CountryEntity;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.definition.geographies.RegionEntity;
import com.elasticpath.rest.definition.geographies.RegionIdentifier;
import com.elasticpath.rest.definition.geographies.RegionsIdentifier;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.geographies.Countries;
import com.elasticpath.rest.resource.geographies.GeographiesLookup;
import com.elasticpath.rest.resource.geographies.RegionId;
import com.elasticpath.rest.resource.geographies.Regions;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Processes the resource operation on Geographies.
 */
@Singleton
@Named("geographiesResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART, Countries.PATH_PART})
public final class GeographiesResourceOperatorImpl implements ResourceOperator {

	private final IdentifierTransformerProvider transformerProvider;
	private final GeographiesLookup geographiesLookup;

	/**
	 * Constructor.
	 *
	 * @param transformerProvider identifier transformer provider
	 * @param geographiesLookup the geographies lookup.
	 */
	@Inject
	GeographiesResourceOperatorImpl(
			@Named("identifierTransformerProvider")
			final IdentifierTransformerProvider transformerProvider,
			@Named("geographiesLookup")
			final GeographiesLookup geographiesLookup) {

		this.transformerProvider = transformerProvider;
		this.geographiesLookup = geographiesLookup;
	}

	/**
	 * Handles the READ operations for all Countries.
	 *
	 * @param operation The resource Operation you are responding to.
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadCountries(final ResourceOperation operation) {

		CountriesIdentifier countriesIdentifier = transformerProvider
				.forClass(CountriesIdentifier.class)
				.uriToIdentifier(operation.getUri());
		ExecutionResult<ResourceState<LinksEntity>> result = geographiesLookup.getAllCountries(countriesIdentifier);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles the READ operation for a Country by ID.
	 *
	 * @param operation The resource Operation you are responding to.
	 * @return the operation result
	 */
	@Path(ResourceId.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadCountry(final ResourceOperation operation) {

		CountryIdentifier countryIdentifier = transformerProvider
				.forClass(CountryIdentifier.class)
				.uriToIdentifier(operation.getUri());
		ExecutionResult<ResourceState<CountryEntity>> result = geographiesLookup.getCountry(countryIdentifier);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles the READ operations for all Regions.
	 *
	 * @param operation The resource Operation you are responding to.
	 * @return the operation result
	 */
	@Path({ ResourceId.PATH_PART, Regions.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processReadRegions(final ResourceOperation operation) {

		RegionsIdentifier regionsIdentifier = transformerProvider
				.forClass(RegionsIdentifier.class)
				.uriToIdentifier(operation.getUri());
		ExecutionResult<ResourceState<LinksEntity>> result = geographiesLookup.getAllRegions(regionsIdentifier);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles the READ operation for a Region by ID.
	 *
	 * @param operation The resource Operation you are responding to.
	 * @return the operation result
	 */
	@Path({ ResourceId.PATH_PART, Regions.PATH_PART, RegionId.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processReadRegion(final ResourceOperation operation) {

		RegionIdentifier regionIdentifier = transformerProvider
				.forClass(RegionIdentifier.class)
				.uriToIdentifier(operation.getUri());
		ExecutionResult<ResourceState<RegionEntity>> result = geographiesLookup.getRegion(regionIdentifier);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}
