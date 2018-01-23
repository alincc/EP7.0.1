/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Reads a list of emails for a user.
 */
public interface ReadEmailListCommand extends Command<ResourceState<LinksEntity>> {

	/**
	 * Builder for {@link ReadEmailListCommand}.
	 */
	interface Builder extends Command.Builder<ReadEmailListCommand> {

		/**
		 * Set the scope.
		 *
		 * @param scope the scope
		 * @return this builder instance
		 */
		Builder setScope(String scope);
	}
}
