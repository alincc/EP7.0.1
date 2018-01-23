/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.rel.ListElementRels;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Values;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.test.AssertResourceInfo;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test class for {@link PurchaseLineItemOptionTransformer}.
 */
public final class PurchaseLineItemOptionTransformerTest {

	private static final String LINEITEMURI = "/lineitemuri";
	private static final String SELECTED_VALUE_CODE = "selectedValueCode";
	private static final String ENCODED_SELECTED_VALUE_CODE = Base32Util.encode(SELECTED_VALUE_CODE);
	private static final String OPTION_CODE = "optionCode";
	private static final String ENCODED_OPTION_CODE = Base32Util.encode(OPTION_CODE);
	private static final String DISPLAY_NAME = "displayName";
	private static final String NAME = "name";

	/**
	 * Test transform to representation.
	 */
	@Test
	public void testTransformToRepresentation() {
		PurchaseLineItemOptionTransformer transformer = new PurchaseLineItemOptionTransformer();

		PurchaseLineItemOptionEntity purchaseLineItemOptionEntity = createDto();

		ResourceState<PurchaseLineItemOptionEntity> representation =
				transformer.transformToRepresentation(purchaseLineItemOptionEntity, LINEITEMURI);
		String optionsUri = URIUtil.format(LINEITEMURI, Options.URI_PART);

		Self expectedSelf = SelfFactory.createSelf(URIUtil.format(optionsUri, ENCODED_OPTION_CODE));

		assertEquals("Name field is not the same as expected value.", NAME, representation.getEntity().getName());
		assertEquals("Display Name field is not the same as expected value.", DISPLAY_NAME, representation.getEntity().getDisplayName());
		assertEquals("Self is different from expected self.", expectedSelf, representation.getSelf());
		AssertResourceInfo.assertResourceInfo(representation.getResourceInfo())
			.maxAge(PurchaseResourceConstants.MAX_AGE);

		ResourceLink expectedOptionsLink = ResourceLinkFactory.createNoRev(optionsUri, CollectionsMediaTypes.LINKS.id(), ListElementRels.LIST);
		String optionValueUri = URIUtil.format(optionsUri, ENCODED_OPTION_CODE, Values.URI_PART, ENCODED_SELECTED_VALUE_CODE);
		ResourceLink optionValueLink = ResourceLinkFactory.create(optionValueUri, PurchasesMediaTypes.PURCHASE_LINE_ITEM_OPTION_VALUE.id(),
				PurchaseLineItemsResourceRels.VALUE_REL, PurchaseLineItemsResourceRels.OPTION_REV);

		assertTrue("Not all resource links in representation match expected links.",
				CollectionUtil.containsOnly(Arrays.asList(optionValueLink, expectedOptionsLink), representation.getLinks()));
	}

	private PurchaseLineItemOptionEntity createDto() {
		return PurchaseLineItemOptionEntity.builder()
				.withName(NAME)
				.withDisplayName(DISPLAY_NAME)
				.withOptionId(OPTION_CODE)
				.withSelectedValueId(SELECTED_VALUE_CODE)
				.build();
	}
}
