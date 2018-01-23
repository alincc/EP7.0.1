/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The Interface ReadKeywordsFormCommand.
 */
public interface ReadKeywordsFormCommand extends Command<ResourceState<SearchKeywordsEntity>> {

	/**
	 * ReadKeywordsFormCommand builder.
	 */
	interface Builder extends Command.Builder<ReadKeywordsFormCommand> {

		/**
		 * Set the scope.
		 *
		 * @param scope the scope
		 * @return this builder instance
		 */
		Builder setScope(String scope);
	}
}
