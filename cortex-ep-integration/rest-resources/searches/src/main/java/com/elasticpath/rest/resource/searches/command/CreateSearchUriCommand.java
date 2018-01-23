/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The Interface CreateSearchUriCommand.
 */
public interface CreateSearchUriCommand extends Command<ResourceState<ResourceEntity>> {
	/**
	 * CreateSearchUri Command Builder.
	 */
	interface Builder extends Command.Builder<CreateSearchUriCommand> {

		/**
		 * Set the scope.
		 *
		 * @param scope the scope
		 * @return this builder instance
		 */
		Builder setScope(String scope);

		/**
		 * Sets the resource operation.
		 *
		 * @param searchForm the search form
		 * @return this builder instance
		 */
		Builder setSearchForm(SearchKeywordsEntity searchForm);

		/**
		 * Sets the search path.
		 *
		 * @param searchPath the search path
		 * @return this builder instance
		 */
		Builder setSearchPath(String searchPath);
	}
}
