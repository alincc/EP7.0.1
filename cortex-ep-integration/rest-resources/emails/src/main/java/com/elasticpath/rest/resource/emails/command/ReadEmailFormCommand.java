/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.command;

import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.definition.emails.EmailEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The read email form command interface.
 */
public interface ReadEmailFormCommand extends Command<ResourceState<EmailEntity>> {

	/**
	 * ReadEmailFormCommand builder.
	 */
	interface Builder extends Command.Builder<ReadEmailFormCommand> {

		/**
		 * Set the scope.
		 *
		 * @param scope the scope
		 * @return this builder instance
		 */
		Builder setScope(String scope);
	}
}
