/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.commons.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.UrlResource;

@RunWith(MockitoJUnitRunner.class)
public class EhcacheConfigurationLoaderTest {

	private EhcacheConfigurationLoader fixture;

	@Test
	public void shouldReturnNullWhenResourceIsNull() {
		fixture = new EhcacheConfigurationLoader();
		fixture.setResource(null);

		assertNull("Returned resource must be null", fixture.getResource());
	}

	@Test
	public void shouldReturnNullWhenResourceDoesNotExist() throws Exception {
		fixture = new EhcacheConfigurationLoader();
		fixture.setResource(new UrlResource("file:///somewhere/ep.properties"));

		assertNull("Returned resource must be null", fixture.getResource());
	}

	@Test
	public void shouldReturnResourceWhenResourceExists() throws Exception {
		final File expectedResource = File.createTempFile("tmp", "properties");

		fixture = new EhcacheConfigurationLoader();
		fixture.setResource(new UrlResource("file://" + expectedResource.getAbsolutePath()));

		assertEquals("Returned resource must be the same as original", fixture.getResource().getFile(), expectedResource);
		expectedResource.delete();

	}

}
