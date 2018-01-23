/**
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.observers.TestSubscriber;

import com.elasticpath.commons.exception.EpValidationException;
import com.elasticpath.commons.exception.InvalidBusinessStateException;
import com.elasticpath.commons.exception.UserIdExistException;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;

@RunWith(MockitoJUnitRunner.class)
public class ReactiveAdapterImplTest {

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;

	@Mock
	private ExceptionTransformer exceptionTransformer;

	TestSubscriber<Object> testSubscriber;

	@Before
	public void initialize() {
		testSubscriber = new TestSubscriber<>();
	}

	@Test
	public void fromServiceShouldDeferExecution() {
		reactiveAdapter.fromService(() -> "Hello World!")
				.subscribe(testSubscriber);

		testSubscriber.assertNoErrors();
		testSubscriber.assertValue("Hello World!");
	}

	@Test
	public void fromServiceShouldDeferExecutionAndHandleNull() {
		reactiveAdapter.fromService(() -> null)
				.subscribe(testSubscriber);

		testSubscriber.assertError(ResourceOperationFailure.notFound());
	}

	@Test
	public void fromServiceShouldDeferExecutionAndHandleExceptions() {
		reactiveAdapter.fromService(() -> {
			throw new IllegalArgumentException();
		}).subscribe(testSubscriber);

		testSubscriber.assertError(IllegalArgumentException.class);
	}

	@Test
	public void fromServiceShouldDeferExecutionAndHandleValidationExceptions() {
		when(exceptionTransformer.getResourceOperationFailure(any(EpValidationException.class)))
				.thenReturn(ResourceOperationFailure.badRequestBody());
		reactiveAdapter.fromService(() -> {
			throw new EpValidationException("Exception Message", Collections.emptyList());
		}).subscribe(testSubscriber);

		testSubscriber.assertError(ResourceOperationFailure.badRequestBody());
	}

	@Test
	public void fromServiceShouldDeferExecutionAndHandleInvalidBusinessStateExceptions() {
		when(exceptionTransformer.getResourceOperationFailure(any(InvalidBusinessStateException.class)))
				.thenReturn(ResourceOperationFailure.stateFailure());

		reactiveAdapter.fromService(() -> {
			throw new UserIdExistException("Exception Message", Collections.emptyList());
		}).subscribe(testSubscriber);

		testSubscriber.assertError(ResourceOperationFailure.stateFailure());
	}

	@Test
	public void fromServiceAsSingleShouldDeferExecutionAndHandleNull() {
		reactiveAdapter.fromServiceAsSingle(() -> null)
				.subscribe(testSubscriber);

		testSubscriber.assertError(ResourceOperationFailure.notFound());
	}
	
	@Test
	public void fromRepositoryShouldDeferExecution() throws Exception {
		reactiveAdapter.fromRepository(() -> ExecutionResultFactory.createReadOK(Arrays.asList("test1", "test2")))
				.subscribe(testSubscriber);

		testSubscriber.assertValueCount(2);
		testSubscriber.assertValues("test1", "test2");
	}

	@Test
	public void fromRepositoryAsCompletableShouldDeferExecution() throws Exception {
		reactiveAdapter.<String>fromRepositoryAsCompletable(() -> ExecutionResultFactory.createReadOK(null))
				.subscribe(testSubscriber);
		testSubscriber.assertNoErrors();
		testSubscriber.assertCompleted();
	}

	@Test
	public void fromRepositoryAsSingleShouldDeferExecution() throws Exception {
		reactiveAdapter.fromRepositoryAsSingle(() -> ExecutionResultFactory.createReadOK("test"))
				.subscribe(testSubscriber);
		testSubscriber.assertValue("test");
	}

}