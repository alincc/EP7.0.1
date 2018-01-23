/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.sellingchannel.director.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elasticpath.common.dto.OrderItemDto;
import com.elasticpath.commons.tree.Functor;
import com.elasticpath.commons.tree.TreeNode;
import com.elasticpath.commons.tree.impl.PreOrderTreeTraverser;
import com.elasticpath.commons.tree.impl.TreeNodeMemento;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.sellingchannel.director.OrderItemAssembler;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.store.StoreService;

/**
 * Default implementation of {@link OrderItemAssembler}.
 */
public class OrderItemAssemblerImpl implements OrderItemAssembler {

	private BundleIdentifier bundleIdentifier;
	
	private final PreOrderTreeTraverser<OrderSkuImplTreeNodeAdapter, TreeNodeMemento<OrderItemDto>> traverser 
		= new PreOrderTreeTraverser<>();

	private ProductInventoryManagementService productInventoryManagementService;
	private StoreService storeService;
	private ProductSkuLookup productSkuLookup;
	private PricingSnapshotService pricingSnapshotService;

	@Override
	public OrderItemDto createOrderItemDto(final OrderSku orderSku, final OrderShipment shipment) {
		Store store = getStoreService().findStoreWithCode(shipment.getOrder().getStoreCode());

		final OrderSkuImplTreeNodeAdapter sourceNode = new OrderSkuImplTreeNodeAdapter(orderSku, shipment, store, bundleIdentifier,
				productInventoryManagementService, getProductSkuLookup(), pricingSnapshotService);
		TreeNodeMemento<OrderItemDto> rootMemento = traverser
				.traverseTree(sourceNode, null, null, new CopyFunctor(), 0);
		return rootMemento.getTreeNode();
	}
	
	/**
	 * 
	 * @param bundleIdentifier The BundleIdentifier bean.
	 */
	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}
	
	/**
	 * 
	 * @param productInventoryManagementService to set.
	 */
	public void setProductInventoryManagementService(
			final ProductInventoryManagementService productInventoryManagementService) {
		this.productInventoryManagementService = productInventoryManagementService;
	}

	protected StoreService getStoreService() {
		return storeService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	protected PricingSnapshotService getPricingSnapshotService() {
		return pricingSnapshotService;
	}

	public void setPricingSnapshotService(final PricingSnapshotService pricingSnapshotService) {
		this.pricingSnapshotService = pricingSnapshotService;
	}

	/**
	 * Functor for use with {@code PreOrderTreeTraverser}. Copies the {@code OrderSku} tree to an {@code OrderItemDto} tree.
	 */
	static class CopyFunctor implements Functor<OrderSkuImplTreeNodeAdapter, TreeNodeMemento<OrderItemDto>> {
		@Override
		public TreeNodeMemento<OrderItemDto> processNode(final OrderSkuImplTreeNodeAdapter sourceNode, final OrderSkuImplTreeNodeAdapter parentNode,
				final TreeNodeMemento<OrderItemDto> parentStackMemento, final int level) {
			OrderItemDto destDto = new OrderItemDto();
			
			if (parentStackMemento == null) {
				sourceNode.copyToParent(destDto);
			} else {
				sourceNode.copyToChild(destDto);
				parentStackMemento.getTreeNode().addChild(destDto);
			}
			return new TreeNodeMemento<>(destDto);
		}
	}
	
	/**
	 * Adapter that allows the getChildren/addChild methods to be used on OrderSku even though they exist,
	 * with different returns types, in the base class ShoppingItem.
	 */
	static class OrderSkuImplTreeNodeAdapter implements TreeNode<OrderSkuImplTreeNodeAdapter> {

		private final BundleIdentifier bundleIdentifier;
		private final OrderSku orderSku;
		private final OrderShipment shipment;
		private final Store store;
		private final ProductInventoryManagementService productInventoryManagementService;
		private final ProductSkuLookup productSkuLookup;
		private final PricingSnapshotService pricingSnapshotService;

		/**
		 * Normal parameter constructor.
		 *
		 * @param orderSku The sku to wrap.
		 * @param shipment The order shipment to filter by
		 * @param store the order's store
		 * @param bundleIdentifier The BundleIdentifier bean.
		 * @param productInventoryManagementService The Inventory Service.
		 * @param productSkuLookup a product sku lookup
		 * @param pricingSnapshotService the pricing snapshot service
		 */
		@SuppressWarnings("checkstyle:redundantmodifier")
		public OrderSkuImplTreeNodeAdapter(final OrderSku orderSku, final OrderShipment shipment, final Store store,
											final BundleIdentifier bundleIdentifier,
											final ProductInventoryManagementService productInventoryManagementService,
											final ProductSkuLookup productSkuLookup, final PricingSnapshotService pricingSnapshotService) {
			this.bundleIdentifier = bundleIdentifier;
			this.orderSku = orderSku;
			this.store = store;
			this.shipment = shipment;
			this.productInventoryManagementService = productInventoryManagementService;
			this.productSkuLookup = productSkuLookup;
			this.pricingSnapshotService = pricingSnapshotService;
		}
		
		/**
		 * Copies the fields of the {@code OrderSku} to the {@code destDto}.
		 * @param destDto The dto to copy to.
		 */
		public void copyToParent(final OrderItemDto destDto) {
			copyToChild(destDto);	
		}
		
		/**
		 * Copies the fields of the {@code OrderSku} to the {@code destDto}.
		 * @param destDto The dto to copy to.
		 */
		public void copyToChild(final OrderItemDto destDto) {
			ProductSku productSku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());

			destDto.setDigitalAsset(orderSku.getDigitalAsset());
			destDto.setDisplayName(orderSku.getDisplayName());
			destDto.setEncryptedUidPk(orderSku.getEncryptedUidPk());
			destDto.setImage(orderSku.getImage());
			destDto.setDisplaySkuOptions(orderSku.getDisplaySkuOptions());
			destDto.setAllocated(orderSku.isAllocated());
			destDto.setProductSku(productSku);
			destDto.setSkuCode(orderSku.getSkuCode());
			destDto.setQuantity(orderSku.getQuantity());
			destDto.setIsBundle(orderSku.isBundle(getProductSkuLookup()));
			destDto.setCalculatedBundle(bundleIdentifier.isCalculatedBundle(productSku));
			if (orderSku.getParent() == null) {
				destDto.setCalculatedBundleItem(false);
			} else {
				final ProductSku parentSku = getProductSkuLookup().findByGuid(orderSku.getParent().getSkuGuid());
				destDto.setCalculatedBundleItem(bundleIdentifier.isCalculatedBundle(parentSku));
			}
			
			((OrderSkuImpl) orderSku).enableRecalculation();

			final ShoppingItemPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForOrderSku(orderSku);
			destDto.setListPrice(pricingSnapshot.getListUnitPrice());
			destDto.setUnitPrice(orderSku.getUnitPriceMoney());
			destDto.setPrice(pricingSnapshot.getPrice());
			destDto.setDollarSavings(orderSku.getDollarSavingsMoney());
			destDto.setTotal(pricingSnapshot.getTotal());

			destDto.setInventory(getInventory(productSku, getWarehouseUidPk()));
		}

		private InventoryDto getInventory(final ProductSku productSku, final Long warehouseUid) {
			return productInventoryManagementService.getInventory(productSku, warehouseUid);
		}

		/**
		 * Gets the warehouse uidpk.
		 * @return warehouse uidpk.
		 */
		protected long getWarehouseUidPk() {
			return store.getWarehouse().getUidPk();
		}

		@Override
		public void addChild(final OrderSkuImplTreeNodeAdapter child) {
			orderSku.addChildItem(child.orderSku);
		}

		@Override
		public List<OrderSkuImplTreeNodeAdapter> getChildren() {
			if (orderSku.isBundle(getProductSkuLookup())) {
				List<OrderSkuImplTreeNodeAdapter> orderSkuList = new ArrayList<>(
						orderSku.getBundleItems(getProductSkuLookup()).size());
				for (ShoppingItem shoppingItem : orderSku.getBundleItems(getProductSkuLookup())) {
					OrderSku candidateSku = (OrderSku) shoppingItem;
					if (hasChildrenInShipment(candidateSku, this.shipment) 
							|| isInShipment(candidateSku, this.shipment)) {
						orderSkuList.add(new OrderSkuImplTreeNodeAdapter(candidateSku, shipment, store, bundleIdentifier,
								productInventoryManagementService, productSkuLookup, pricingSnapshotService));
					} 
				}
				return orderSkuList;
			}
			
			return Collections.emptyList();
		}

		private boolean hasChildrenInShipment(final ShoppingItem item, final OrderShipment shipment) {
			boolean result = false;
			if (item.isBundle(getProductSkuLookup())) {
				for (ShoppingItem shoppingItem : item.getBundleItems(getProductSkuLookup())) {
					OrderSku candidateSku = (OrderSku) shoppingItem;
					if (candidateSku.isBundle(getProductSkuLookup())) {
						result = result || hasChildrenInShipment(candidateSku, shipment);
					} else {
						result = result || isInShipment(candidateSku, shipment);
					}
					if (result) {
						break;
					}
				}
			}
			return result;
		}

		private boolean isInShipment(final OrderSku candidateSku, final OrderShipment shipment) {
			return candidateSku.getShipment() != null && candidateSku.getShipment().getUidPk() == shipment.getUidPk();
		}

		protected ProductSkuLookup getProductSkuLookup() {
			return productSkuLookup;
		}
	}
}
