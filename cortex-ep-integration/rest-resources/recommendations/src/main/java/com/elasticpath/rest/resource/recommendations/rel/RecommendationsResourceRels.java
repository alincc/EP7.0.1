/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.rel;

/**
 * Constants for Recommendations Resource.
 */
public final class RecommendationsResourceRels {

	/**
	 * The Constant MAX_AGE.
	 */
	public static final Integer MAX_AGE = 600;

	/**
	 * Recommendations rel identifier.
	 */
	public static final String RECOMMENDATIONS_REL = "recommendations";

	/**
	 * Recommendations rev identifier.
	 */
	public static final String RECOMMENDATIONS_REV = RECOMMENDATIONS_REL;

	/**
	 * Items rel identifier.
	 */
	public static final String ITEMS_REL = "items";

	/**
	 * Forcing non instantiation with private default constructor.
	 */
	private RecommendationsResourceRels() { }

}
