/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.query;

import java.util.Collection;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * A repository for accessing slots related services.
 */
public interface SlotsRepository {

	/**
	 * Finds all slot ids.
	 *
	 * @return a collection of slot ids.
	 */
	ExecutionResult<Collection<String>> findAllSlotIds();


	/**
	 * get ContentSpace by GUID.
	 *
	 * @param contentSpaceGuid content space GUID
	 * @return ContentSpace
	 */
	ExecutionResult<ContentSpace> getContentSpaceByGuid(String contentSpaceGuid);


	/**
	 * get DynamicContent by GUID.
	 *
	 * @param contentSpaceGuid content space GUID
	 * @return DynamicContent
	 */
	ExecutionResult<DynamicContent> getDynamicContent(String contentSpaceGuid);
}
