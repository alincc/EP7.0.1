/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.common.dto;

import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import com.google.common.testing.EqualsTester;
import org.junit.Test;

/**
 * Tests for {@link StructuredErrorMessage}.
 */
public class StructuredErrorMessageTest {

	@Test
	public void testObjects() {
		new EqualsTester()
				.addEqualityGroup(createCommerceMessageObject("messageId", "debugMessage"), createCommerceMessageObject("messageId", "debugMessage"))
				.addEqualityGroup(createCommerceMessageObject("messageId", "debugMessage1"))
				.addEqualityGroup(createCommerceMessageObject("messageId1", "debugMessage"))
				.testEquals();
	}

	@Test
	public void testObjectDataIneEquality() {
		Map<String, String> data = new HashMap<>();
		data.put("key4", "value4");
		data.put("key5", "value5");
		data.put("key6", "value6");
		data.put("key7", "value7");

		StructuredErrorMessage structuredErrorMessage1 = new StructuredErrorMessage("messageId4", "debugMessage4", data);
		StructuredErrorMessage structuredErrorMessage2 = createCommerceMessageObject("messageId4", "debugMessage4");
		assertFalse(structuredErrorMessage1.equals(structuredErrorMessage2));
	}

	private StructuredErrorMessage createCommerceMessageObject(final String messageId, final String debugMessage) {
		Map<String, String> data = new HashMap<>();
		data.put("key1", "value1");
		data.put("key2", "value2");
		data.put("key3", "value3");

		return new StructuredErrorMessage(messageId, debugMessage, data);
	}
}
