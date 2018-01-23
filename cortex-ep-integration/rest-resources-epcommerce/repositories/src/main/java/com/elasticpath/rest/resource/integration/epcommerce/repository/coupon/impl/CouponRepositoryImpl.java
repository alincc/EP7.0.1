/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.coupon.specifications.PotentialCouponUse;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.specifications.Specification;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;
import com.elasticpath.service.rules.CouponService;

/**
 * The facade for Coupon related operations.
 */
@Singleton
@Named("couponRepository")
public class CouponRepositoryImpl implements CouponRepository {
	
	private final Specification<PotentialCouponUse> validCouponUseSpecification;
	
	private final CouponService couponService;
	
	/**
	 * Constructor.
	 * @param validCouponUseSpecification coupon validity spec.
	 * @param couponService coupon service.
	 */
	@Inject
	public CouponRepositoryImpl(
			@Named("validCouponUseSpecification")
			final Specification<PotentialCouponUse> validCouponUseSpecification,
			@Named("couponService")
			final CouponService couponService) {
		this.validCouponUseSpecification = validCouponUseSpecification;
		this.couponService = couponService;
	}

	@Override
	@CacheResult
	public ExecutionResult<Boolean> isCouponValidInStore(final String couponCode, final String storeCode, final String customerEmail) {
		return new ExecutionResultChain() {

			@Override
			protected ExecutionResult<?> build() {
				Coupon coupon = couponService.findByCouponCode(couponCode);
				PotentialCouponUse potentialCouponUse = new PotentialCouponUse(coupon, storeCode, customerEmail);

				return ExecutionResultFactory.createReadOK(validCouponUseSpecification.isSatisfiedBy(potentialCouponUse));
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<Coupon> findByCouponCode(final String couponCode) {
		return new ExecutionResultChain() {

			@Override
			protected ExecutionResult<?> build() {
				Coupon coupon = Assign.ifNotNull(couponService.findByCouponCode(couponCode), OnFailure.returnNotFound());
				return ExecutionResultFactory.createReadOK(coupon);
			}
		}.execute();
	}
}
