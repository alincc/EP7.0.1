/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.command.impl;

import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.resource.searches.command.CreateSearchUriCommand;
import com.elasticpath.rest.resource.searches.keywords.Keywords;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * The Class CreateSearchUriCommandImpl.
 */
@Named
public final class CreateSearchUriCommandImpl implements CreateSearchUriCommand {

	private final String resourceServerName;

	private String scope;
	private String searchPath;
	private SearchKeywordsEntity searchForm;


	/**
	 * Constructor.
	 * @param resourceServerName resource server name.
	 */
	@Inject
	CreateSearchUriCommandImpl(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public ExecutionResult<ResourceState<ResourceEntity>> execute() {
		SearchKeywordsEntity searchKeywordsForm = searchForm;
		String keywords = searchKeywordsForm.getKeywords();
		Integer pageSize = searchKeywordsForm.getPageSize();

		Map<String, String> searchFieldValuePairs = new TreeMap<>();
		searchFieldValuePairs.put(SearchKeywordsEntity.KEYWORDS_PROPERTY, keywords);

		if (pageSize != null) {
			searchFieldValuePairs.put(SearchKeywordsEntity.PAGE_SIZE_PROPERTY, String.valueOf(pageSize));
		}

		String encodedKeywords = CompositeIdUtil.encodeCompositeId(searchFieldValuePairs);
		String searchUri = URIUtil.format(resourceServerName, scope, Keywords.URI_PART, searchPath, encodedKeywords);
		return ExecutionResultFactory.createCreateOK(searchUri, false);
	}

	/**
	 * Constructs a CreateSearchUriCommand.
	 */
	@Named("createSearchUriCommandBuilder")
	static class BuilderImpl implements Builder {

		private final CreateSearchUriCommandImpl command;

		/**
		 * Constructor.
		 *
		 * @param command Command instance.
		 */
		@Inject
		BuilderImpl(final CreateSearchUriCommandImpl command) {
			this.command = command;
		}

		@Override
		public CreateSearchUriCommand build() {
			assert command.scope != null : "scope is required.";
			assert command.searchPath != null : "searchPath is required.";
			return command;
		}

		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public Builder setSearchPath(final String searchPath) {
			command.searchPath = searchPath;
			return this;
		}

		@Override
		public Builder setSearchForm(final SearchKeywordsEntity searchForm) {
			command.searchForm = searchForm;
			return this;
		}
	}
}
