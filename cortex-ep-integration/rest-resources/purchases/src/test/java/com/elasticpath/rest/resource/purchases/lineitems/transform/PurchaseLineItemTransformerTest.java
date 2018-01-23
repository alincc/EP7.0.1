/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.transform;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Test;

import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Components;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.resource.purchases.lineitems.LineItems;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.test.AssertResourceInfo;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for PurchaseLineItemTransformer.
 */
public final class PurchaseLineItemTransformerTest {

	private static final String PARENT_URI = "/parent/uri";
	private static final String PURCHASES_RESOURCE = "purchases";
	private static final String SCOPE = "scope";
	private static final String PURCHASE_ID = "purchase_id";
	private static final String PURCHASE_LINE_ITEM_ID = "purchase_line_item_id";
	private static final String AMOUNT_DISPLAY = "2.00";
	private static final String TAX_DISPLAY = "0.5";
	private static final String TOTAL_DISPLAY = "2.50";
	private static final BigDecimal AMOUNT = new BigDecimal(AMOUNT_DISPLAY);
	private static final BigDecimal TAX_AMOUNT = new BigDecimal(TAX_DISPLAY);
	private static final BigDecimal TOTAL_AMOUNT = new BigDecimal(TOTAL_DISPLAY);
	private static final String CURRENCY = "CAD";
	private static final Integer QUANTITY = 1;
	private static final String NAME = "TestItem";


	/**
	 * Test purchase line item transformer.
	 */
	@Test
	public void testPurchaseLineItemTransformer() {
		PurchaseLineItemTransformer purchaseLineItemTransformer = new PurchaseLineItemTransformer(PURCHASES_RESOURCE);

		CostEntity amount = buildCostEntity(AMOUNT, CURRENCY, AMOUNT_DISPLAY);
		CostEntity tax = buildCostEntity(TAX_AMOUNT, CURRENCY, TAX_DISPLAY);
		CostEntity total = buildCostEntity(TOTAL_AMOUNT, CURRENCY, TOTAL_DISPLAY);

		PurchaseLineItemEntity purchaseLineItemEntity = createPurchaseLineItemDto(amount, tax, total);

		ResourceState<PurchaseLineItemEntity> representation =
				purchaseLineItemTransformer.transformToRepresentation(SCOPE, PURCHASE_ID, PURCHASE_LINE_ITEM_ID, purchaseLineItemEntity, "");

		PurchaseLineItemEntity resultEntity = representation.getEntity();
		assertEquals(PURCHASE_LINE_ITEM_ID, resultEntity.getLineItemId());
		assertEquals(PURCHASE_ID, resultEntity.getPurchaseId());
		assertEquals(NAME, resultEntity.getName());
		assertEquals(QUANTITY, resultEntity.getQuantity());
		assertTrue(resultEntity.getLineExtensionAmount().contains(amount));
		assertTrue(resultEntity.getLineExtensionTax().contains(tax));
		assertTrue(resultEntity.getLineExtensionTotal().contains(total));

		String purchaseUri = URIUtil.format(PURCHASES_RESOURCE, SCOPE, PURCHASE_ID);
		String listUri = URIUtil.format(purchaseUri, LineItems.URI_PART);
		String expectedSelfUri = URIUtil.format(listUri, PURCHASE_LINE_ITEM_ID);

		Self expectedSelf = SelfFactory.createSelf(expectedSelfUri);

		ResourceLink expectedListLink = ElementListFactory.createListWithoutElement(listUri, CollectionsMediaTypes.LINKS.id());
		ResourceLink expectedPurchaseLink = ResourceLinkFactory.createNoRev(purchaseUri,
				PurchasesMediaTypes.PURCHASE.id(), PurchaseResourceRels.PURCHASE_REL);

		assertResourceState(representation)
				.self(expectedSelf)
				.resourceInfoMaxAge(PurchaseResourceConstants.MAX_AGE)
				.containsLink(expectedPurchaseLink)
				.containsLink(expectedListLink);
	}

	/**
	 * Test purchase line item transformer with parent URI.
	 */
	@Test
	public void testPurchaseLineItemTransformerWithParentUri() {
		PurchaseLineItemTransformer purchaseLineItemTransformer = new PurchaseLineItemTransformer(PURCHASES_RESOURCE);

		PurchaseLineItemEntity purchaseLineItemEntity = createPurchaseLineItemDto(null, null, null);

		ResourceState<PurchaseLineItemEntity> representation =
				purchaseLineItemTransformer.transformToRepresentation(SCOPE, PURCHASE_ID, PURCHASE_LINE_ITEM_ID, purchaseLineItemEntity, PARENT_URI);

		String listUri = URIUtil.format(PARENT_URI, Components.URI_PART);
		String expectedSelfUri = URIUtil.format(listUri, PURCHASE_LINE_ITEM_ID);

		Self expectedSelf = SelfFactory.createSelf(expectedSelfUri);

		assertEquals(expectedSelf, representation.getSelf());
		AssertResourceInfo.assertResourceInfo(representation.getResourceInfo())
			.maxAge(PurchaseResourceConstants.MAX_AGE);
	}

	private PurchaseLineItemEntity createPurchaseLineItemDto(final CostEntity amount, final CostEntity tax, final CostEntity total) {

		PurchaseLineItemEntity.Builder builder = PurchaseLineItemEntity.builder()
				.withName(NAME)
				.withQuantity(QUANTITY);
		if (amount != null) {
			builder.withLineExtensionAmount(Collections.singleton(amount));
		}
		if (tax != null) {
			builder.withLineExtensionTax(Collections.singleton(tax));
		}
		if (total != null) {
			builder.withLineExtensionTotal(Collections.singleton(total));
		}
		return builder.build();
	}

	private CostEntity buildCostEntity(final BigDecimal amount, final String currency, final String displayValue) {
		return CostEntity.builder()
				.withAmount(amount)
				.withCurrency(currency)
				.withDisplay(displayValue)
				.build();
	}
}
