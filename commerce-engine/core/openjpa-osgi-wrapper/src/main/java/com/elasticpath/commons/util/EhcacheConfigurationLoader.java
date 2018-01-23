/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.commons.util;

import org.springframework.core.io.Resource;

/**
 * The class {@link org.springframework.cache.ehcache.EhCacheManagerFactoryBean} doesn't check whether given resource,
 * provided via "configLocation" property, exists or not and throws NullPointerExcepion in case when doesn't and stream is requested.
 *
 * EhcacheConfigurationLoader class is a workaround for this issue, using {@link #getResource} method that returns null in case when resource
 * doesn't exist.
 *
 */
public class EhcacheConfigurationLoader  {

	private Resource resource;

	/**
	 * Returns {@link org.springframework.core.io.Resource}, if exists.
	 *
	 * @return resource
	 */
	public Resource getResource() {
		if (resource != null && resource.exists()) {
			return resource;
		}
		return null;
	}

	public void setResource(final Resource resource) {
		this.resource = resource;
	}
}
