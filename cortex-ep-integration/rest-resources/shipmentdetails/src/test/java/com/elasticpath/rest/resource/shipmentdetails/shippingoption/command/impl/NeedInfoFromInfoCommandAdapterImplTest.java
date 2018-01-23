/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.impl;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.command.needinfo.NeedInfoFromInfoCommand;
import com.elasticpath.rest.schema.ResourceLink;

@RunWith(MockitoJUnitRunner.class)
public class NeedInfoFromInfoCommandAdapterImplTest {
	private NeedInfoFromInfoCommand needInfoFromInfoCommand;

	private NeedInfoFromInfoCommandAdapterImpl needInfoFromInfoCommandAdapter;

	@Before
	public void setupTestComponents() {
		Provider<NeedInfoFromInfoCommand.Builder> needInfoProvider = mock(NeedInfoProvider.class);
		NeedInfoFromInfoCommand.Builder needInfoFromInfoCommandBuilder = mock(NeedInfoFromInfoCommand.Builder.class, new SelfReturningAnswer());
		needInfoFromInfoCommand = mock(NeedInfoFromInfoCommand.class);

		needInfoFromInfoCommandAdapter = new NeedInfoFromInfoCommandAdapterImpl(needInfoProvider);

		when(needInfoProvider.get()).thenReturn(needInfoFromInfoCommandBuilder);
		when((Object) needInfoFromInfoCommandBuilder.build()).thenReturn(needInfoFromInfoCommand);
	}

	@Test
	public void ensureFailedExecutionReturnsEmptyCollection() {
		when(needInfoFromInfoCommand.execute()).thenReturn(ExecutionResultFactory.<Collection<ResourceLink>>createNotFound());
		Collection<ResourceLink> links = needInfoFromInfoCommandAdapter.getNeedInfoLinksForInfo("/infoUri", "rel");
		assertThat(links, empty());
	}

	@Test
	public void ensureSuccessfulButEmptyExecutionReturnsEmptyCollection() {
		when(needInfoFromInfoCommand.execute()).thenReturn(ExecutionResultFactory.<Collection<ResourceLink>>createReadOK(
				Collections.<ResourceLink>emptyList()));
		Collection<ResourceLink> links = needInfoFromInfoCommandAdapter.getNeedInfoLinksForInfo("/infoUri", "rel");
		assertThat(links, empty());
	}

	@Test
	public void ensureSuccessfulNonEmptyExecutionReturnsLinks() {
		ResourceLink resourceLink = mock(ResourceLink.class);
		when(needInfoFromInfoCommand.execute()).thenReturn(ExecutionResultFactory.<Collection<ResourceLink>>createReadOK(
				Collections.singleton(resourceLink)));
		Collection<ResourceLink> links = needInfoFromInfoCommandAdapter.getNeedInfoLinksForInfo("/infoUri", "rel");
		assertThat(links, contains(resourceLink));
	}

	private static class SelfReturningAnswer implements Answer<Object> {
		@Override
		public Object answer(final InvocationOnMock invocation) throws Throwable {
			Object self = invocation.getMock();
			if (invocation.getMethod().getReturnType().isAssignableFrom(self.getClass())) {
				return self;
			}
			return RETURNS_DEFAULTS.answer(invocation);
		}
	}

	private interface NeedInfoProvider extends Provider<NeedInfoFromInfoCommand.Builder> {
	}
}