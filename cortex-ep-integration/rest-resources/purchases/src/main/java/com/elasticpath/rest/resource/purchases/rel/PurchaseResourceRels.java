/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.rel;


/**
 * Constants for purchases resource.
 */
public final class PurchaseResourceRels {

	/**
	 * Submit Order rel.
	 */
	public static final String SUBMIT_ORDER_ACTION_REL = "submitorderaction";

	/**
	 * Purchase rel identifier.
	 */
	public static final String PURCHASE_REL = "purchase";

	/**
	 * Purchase rev identifier.
	 */
	public static final String PURCHASE_REV = PURCHASE_REL;

	/**
	 * Purchase Form rel/rev identifier.
	 */
	public static final String PURCHASE_FORM_REL = "purchaseform";

	/**
	 * payment means sub-resource.
	 */
	public static final String PAYMENT_MEANS = "paymentmeans";

	/**
	 * payments rel.
	 */
	public static final String PAYMENT_MEANS_REL = "paymentmeans";

	/**
	 * payments rev.
	 */
	public static final String PAYMENT_MEANS_REV = PAYMENT_MEANS_REL;

	/**
	 * The paymentinfo link rel.
	 */
	public static final String PAYMENT_INFO_REL = "paymentmethodinfo";

	/**
	 * The purchases rel.
	 */
	public static final String PURCHASES_REL = "purchases";
}
