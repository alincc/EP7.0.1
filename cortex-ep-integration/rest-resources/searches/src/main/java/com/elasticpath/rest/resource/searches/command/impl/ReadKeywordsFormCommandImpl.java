/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.searches.command.ReadKeywordsFormCommand;
import com.elasticpath.rest.resource.searches.keywords.Items;
import com.elasticpath.rest.resource.searches.keywords.Keywords;
import com.elasticpath.rest.resource.searches.keywords.lookup.KeywordsSearchesLookup;
import com.elasticpath.rest.resource.searches.rel.SearchesResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * The Class ReadKeywordsFormCommandImpl.
 */
@Named
public final class ReadKeywordsFormCommandImpl implements ReadKeywordsFormCommand {

	private final String resourceServerName;
	private final KeywordsSearchesLookup keywordsSearchesLookup;

	private String scope;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName     resource server name
	 * @param keywordsSearchesLookup keywords searches lookup
	 */
	@Inject
	ReadKeywordsFormCommandImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("keywordsSearchesLookup")
			final KeywordsSearchesLookup keywordsSearchesLookup) {

		this.resourceServerName = resourceServerName;
		this.keywordsSearchesLookup = keywordsSearchesLookup;
	}


	@Override
	public ExecutionResult<ResourceState<SearchKeywordsEntity>> execute() {

		int defaultPageSize = Assign.ifSuccessful(keywordsSearchesLookup.getDefaultPageSize(scope));
		String baseSearchUri = URIUtil.format(resourceServerName, scope, Keywords.URI_PART);
		Self self = SelfFactory.createSelf(URIUtil.format(baseSearchUri, Form.URI_PART));

		String itemKeywordSearchActionUri = URIUtil.format(baseSearchUri, Items.URI_PART);
		ResourceLink itemKeywordSearchActionLink = ResourceLinkFactory.createUriRel(itemKeywordSearchActionUri,
				SearchesResourceRels.ITEM_KEYWORDS_SEARCH_ACTION_REL);

		ResourceState<SearchKeywordsEntity> searchForm = ResourceState.Builder
				.create(SearchKeywordsEntity.builder()
						.withKeywords(StringUtils.EMPTY)
						.withPageSize(defaultPageSize)
						.build())
				.withSelf(self)
				.addingLinks(itemKeywordSearchActionLink)
				.build();

		return ExecutionResultFactory.createReadOK(searchForm);
	}

	/**
	 * Constructs a ReadKeywordsFormCommand.
	 */
	@Named("readKeywordsFormCommandBuilder")
	static class BuilderImpl implements Builder {

		private final ReadKeywordsFormCommandImpl command;

		/**
		 * Constructor.
		 *
		 * @param command Command instance.
		 */
		@Inject
		BuilderImpl(final ReadKeywordsFormCommandImpl command) {
			this.command = command;
		}

		@Override
		public ReadKeywordsFormCommand build() {
			assert command.scope != null : "scope required.";
			return command;
		}

		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}
	}
}
