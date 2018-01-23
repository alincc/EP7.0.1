/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.promotion;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;

/**
 * Holds mapping between EqQL fields and field descriptors for ContentSpace.
 */
public class ContentSpaceConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT cs.guid FROM ContentSpaceImpl cs ");
		setQueryPostfix(" ORDER BY cs.guid ASC ");
	}
}
