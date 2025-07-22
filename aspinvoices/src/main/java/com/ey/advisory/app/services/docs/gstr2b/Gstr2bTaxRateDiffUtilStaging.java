package com.ey.advisory.app.services.docs.gstr2b;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.ey.advisory.app.data.entities.client.GetGstr2bB2bEcomHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bB2bEcomaHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bEcomItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bEcomaItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingB2bInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingB2baInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingCdnrInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingCdnrInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingCdnraInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingCdnraInvoicesItemEntity;
import com.google.common.collect.Lists;

public class Gstr2bTaxRateDiffUtilStaging {

	public static List<GetGstr2bStagingB2bInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonB2bHeaders(
			List<GetGstr2bStagingB2bInvoicesHeaderEntity> b2bList) {
		List<GetGstr2bStagingB2bInvoicesHeaderEntity> b2bFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(b2bList)) {
			Map<String, List<GetGstr2bStagingB2bInvoicesHeaderEntity>> invoiceKeyMapList = b2bList
					.stream().collect(Collectors.groupingBy(
							GetGstr2bStagingB2bInvoicesHeaderEntity::getDocKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2bStagingB2bInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2bStagingB2bInvoicesHeaderEntity hEntity = mergeRatesIntoB2bHeader(
						headerList);
				if (hEntity != null) {
					b2bFilterList.add(hEntity);
				}
			});
		}

		return b2bFilterList;
	}
	
	public static List<GetGstr2bB2bEcomHeaderEntity> filterInvoiceKeyAndMergeRateonEcomHeader(
			List<GetGstr2bB2bEcomHeaderEntity> ecomList) {
		List<GetGstr2bB2bEcomHeaderEntity> b2bEcomFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(ecomList)) {
			Map<String, List<GetGstr2bB2bEcomHeaderEntity>> invoiceKeyMapList = ecomList
					.stream().collect(Collectors.groupingBy(
							GetGstr2bB2bEcomHeaderEntity::getDocKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2bB2bEcomHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2bB2bEcomHeaderEntity hEntity = mergeRatesIntoEcomHeader(
						headerList);
				if (hEntity != null) {
					b2bEcomFilterList.add(hEntity);
				}
			});
		}

		return b2bEcomFilterList;
	}

	private static GetGstr2bStagingB2bInvoicesHeaderEntity mergeRatesIntoB2bHeader(
			List<GetGstr2bStagingB2bInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2bStagingB2bInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2bStagingB2bInvoicesHeaderEntity>();
		List<GetGstr2bStagingB2bInvoicesItemEntity> lineItems = new ArrayList<>();
		List<GetGstr2bStagingB2bInvoicesItemEntity> lineItemsforMultipleHeaders = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			
			if(headerList.size() > 1){
				GetGstr2bStagingB2bInvoicesItemEntity finalEntitySum = new GetGstr2bStagingB2bInvoicesItemEntity();
				Long id = lineItems.get(0).getId();
				String checksum =  lineItems.get(0).getChecksum();
				int itemNumber =  lineItems.get(0).getItemNumber() ;
				BigDecimal taxRate  =   lineItems.get(0).getTaxRate() ;
				BigDecimal taxableValue =  lineItems.get(0).getTaxableValue();
				BigDecimal igstAmt = lineItems.get(0).getIgstAmt();
				BigDecimal cgstAmt =  lineItems.get(0).getCgstAmt();
				BigDecimal sgstAmt =  lineItems.get(0).getSgstAmt();
				BigDecimal cessAmt =  lineItems.get(0).getCessAmt();
				
				for (int i = 1; i < headerList.size(); i++) {
					GetGstr2bStagingB2bInvoicesHeaderEntity hEntity = headerList
							.get(i);
					List<GetGstr2bStagingB2bInvoicesItemEntity> hLineItems = hEntity
							.getLineItems();				
					
					if (checkIfRateIsDifferent(lineItems, hLineItems)) {
						taxableValue = taxableValue.add(hLineItems.get(0).getTaxableValue());
						igstAmt = igstAmt.add(hLineItems.get(0).getIgstAmt());
						cgstAmt = cgstAmt.add(hLineItems.get(0).getCgstAmt());
						sgstAmt = sgstAmt.add(hLineItems.get(0).getSgstAmt());
						cessAmt = cessAmt.add(hLineItems.get(0).getCessAmt());
						
					}
					
				}
				finalEntitySum.setId(id);
				finalEntitySum.setChecksum(checksum);
				finalEntitySum.setItemNumber(itemNumber);
				finalEntitySum.setTaxRate(taxRate);
				finalEntitySum.setTaxableValue(taxableValue);
				finalEntitySum.setIgstAmt(igstAmt);
				finalEntitySum.setCgstAmt(cgstAmt);
				finalEntitySum.setSgstAmt(sgstAmt);
				finalEntitySum.setCessAmt(cessAmt);
				lineItemsforMultipleHeaders.add(finalEntitySum);
				headerEntity.get().setLineItems(lineItemsforMultipleHeaders);
				lineItemsforMultipleHeaders.stream().filter(hlItem -> hlItem != null).forEach(
							lineItem -> lineItem.setHeader(headerEntity.get()));
			} else{
				
				for (int i = 1; i < headerList.size(); i++) {
					GetGstr2bStagingB2bInvoicesHeaderEntity hEntity = headerList
							.get(i);
					List<GetGstr2bStagingB2bInvoicesItemEntity> hLineItems = hEntity
							.getLineItems();			
					
					if (checkIfRateIsDifferent(lineItems, hLineItems)) {
						
					}
					
				}
				headerEntity.get().setLineItems(lineItems);
				lineItems.stream().filter(hlItem -> hlItem != null).forEach(
							lineItem -> lineItem.setHeader(headerEntity.get()));
			}
			
		}

		return headerEntity.get() != null ? headerEntity.get() : null;
	}

	private static boolean checkIfRateIsDifferent(
			List<GetGstr2bStagingB2bInvoicesItemEntity> lineItems,
			List<GetGstr2bStagingB2bInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2bStagingB2bInvoicesItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2bStagingB2bInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}
	
	private static boolean checkIfRateIsDifferentForEcom(
			List<GetGstr2bEcomItemEntity> lineItems,
			List<GetGstr2bEcomItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2bEcomItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2bEcomItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr2bStagingB2baInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonB2baHeaders(
			List<GetGstr2bStagingB2baInvoicesHeaderEntity> b2baList) {
		List<GetGstr2bStagingB2baInvoicesHeaderEntity> b2baFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(b2baList)) {
			Map<String, List<GetGstr2bStagingB2baInvoicesHeaderEntity>> invoiceKeyMapList = b2baList
					.stream().collect(Collectors.groupingBy(
							GetGstr2bStagingB2baInvoicesHeaderEntity::getDocKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2bStagingB2baInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2bStagingB2baInvoicesHeaderEntity hEntity = mergeRatesIntoB2baHeader(
						headerList);
				if (hEntity != null) {
					b2baFilterList.add(hEntity);
				}
			});
		}

		return b2baFilterList;
	}

	private static GetGstr2bStagingB2baInvoicesHeaderEntity mergeRatesIntoB2baHeader(
			List<GetGstr2bStagingB2baInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2bStagingB2baInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2bStagingB2baInvoicesHeaderEntity>();
		List<GetGstr2bStagingB2baInvoicesItemEntity> lineItems = new ArrayList<>();
		List<GetGstr2bStagingB2baInvoicesItemEntity> lineItemsforMultipleHeaders = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItemss());
			headerEntity.get().setLineItemss(null);
			
			if(headerList.size() > 1){
				GetGstr2bStagingB2baInvoicesItemEntity finalEntitySum = new GetGstr2bStagingB2baInvoicesItemEntity();
				Long id = lineItems.get(0).getId();
				String checksum =  lineItems.get(0).getChecksum();
				int itemNumber =  lineItems.get(0).getItemNumber() ;
				BigDecimal taxRate  =   lineItems.get(0).getTaxRate() ;
				BigDecimal taxableValue =  lineItems.get(0).getTaxableValue();
				BigDecimal igstAmt = lineItems.get(0).getIgstAmt();
				BigDecimal cgstAmt =  lineItems.get(0).getCgstAmt();
				BigDecimal sgstAmt =  lineItems.get(0).getSgstAmt();
				BigDecimal cessAmt =  lineItems.get(0).getCessAmt();
				
				for (int i = 1; i < headerList.size(); i++) {
					GetGstr2bStagingB2baInvoicesHeaderEntity hEntity = headerList
							.get(i);
					List<GetGstr2bStagingB2baInvoicesItemEntity> hLineItems = hEntity
							.getLineItemss();				
					
					if (checkIfRateB2baIsDifferent(lineItems, hLineItems)) {
						taxableValue = taxableValue.add(hLineItems.get(0).getTaxableValue());
						igstAmt = igstAmt.add(hLineItems.get(0).getIgstAmt());
						cgstAmt = cgstAmt.add(hLineItems.get(0).getCgstAmt());
						sgstAmt = sgstAmt.add(hLineItems.get(0).getSgstAmt());
						cessAmt = cessAmt.add(hLineItems.get(0).getCessAmt());
						
					}
					
				}
				finalEntitySum.setId(id);
				finalEntitySum.setChecksum(checksum);
				finalEntitySum.setItemNumber(itemNumber);
				finalEntitySum.setTaxRate(taxRate);
				finalEntitySum.setTaxableValue(taxableValue);
				finalEntitySum.setIgstAmt(igstAmt);
				finalEntitySum.setCgstAmt(cgstAmt);
				finalEntitySum.setSgstAmt(sgstAmt);
				finalEntitySum.setCessAmt(cessAmt);
				lineItemsforMultipleHeaders.add(finalEntitySum);
				headerEntity.get().setLineItemss(lineItemsforMultipleHeaders);
				lineItemsforMultipleHeaders.stream().filter(hlItem -> hlItem != null).forEach(
							lineItem -> lineItem.setHeader(headerEntity.get()));
			} else{
				
				for (int i = 1; i < headerList.size(); i++) {
					GetGstr2bStagingB2baInvoicesHeaderEntity hEntity = headerList
							.get(i);
					List<GetGstr2bStagingB2baInvoicesItemEntity> hLineItems = hEntity
							.getLineItemss();			
					
					if (checkIfRateB2baIsDifferent(lineItems, hLineItems)) {
						
					}
					
				}
				headerEntity.get().setLineItemss(lineItems);
				lineItems.stream().filter(hlItem -> hlItem != null).forEach(
							lineItem -> lineItem.setHeader(headerEntity.get()));
			}
			
		}

		return headerEntity.get() != null ? headerEntity.get() : null;
	}

	private static boolean checkIfRateB2baIsDifferent(
			List<GetGstr2bStagingB2baInvoicesItemEntity> lineItems,
			List<GetGstr2bStagingB2baInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2bStagingB2baInvoicesItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2bStagingB2baInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr2bStagingCdnrInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonCdnrHeaders(
			List<GetGstr2bStagingCdnrInvoicesHeaderEntity> cdnraList) {
		List<GetGstr2bStagingCdnrInvoicesHeaderEntity> cdnaFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(cdnraList)) {
			Map<String, List<GetGstr2bStagingCdnrInvoicesHeaderEntity>> invoiceKeyMapList = cdnraList
					.stream().collect(Collectors.groupingBy(
							GetGstr2bStagingCdnrInvoicesHeaderEntity::getDocKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2bStagingCdnrInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2bStagingCdnrInvoicesHeaderEntity hEntity = mergeRatesIntoCdnaHeader(
						headerList);
				if (hEntity != null) {
					cdnaFilterList.add(hEntity);
				}
			});
		}

		return cdnaFilterList;
	}

	private static GetGstr2bStagingCdnrInvoicesHeaderEntity mergeRatesIntoCdnaHeader(
			List<GetGstr2bStagingCdnrInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2bStagingCdnrInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2bStagingCdnrInvoicesHeaderEntity>();
		List<GetGstr2bStagingCdnrInvoicesItemEntity> lineItems = new ArrayList<>();
		List<GetGstr2bStagingCdnrInvoicesItemEntity> lineItemsforMultipleHeaders = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItemss());
			headerEntity.get().setLineItemss(null);
			
			if(headerList.size() > 1){
				GetGstr2bStagingCdnrInvoicesItemEntity finalEntitySum = new GetGstr2bStagingCdnrInvoicesItemEntity();
				Long id = lineItems.get(0).getId();
				String checksum =  lineItems.get(0).getChecksum();
				int itemNumber =  lineItems.get(0).getItemNumber() ;
				BigDecimal taxRate  =   lineItems.get(0).getTaxRate() ;
				BigDecimal taxableValue =  lineItems.get(0).getTaxableValue();
				BigDecimal igstAmt = lineItems.get(0).getIgstAmt();
				BigDecimal cgstAmt =  lineItems.get(0).getCgstAmt();
				BigDecimal sgstAmt =  lineItems.get(0).getSgstAmt();
				BigDecimal cessAmt =  lineItems.get(0).getCessAmt();
				
				for (int i = 1; i < headerList.size(); i++) {
					GetGstr2bStagingCdnrInvoicesHeaderEntity hEntity = headerList
							.get(i);
					List<GetGstr2bStagingCdnrInvoicesItemEntity> hLineItems = hEntity
							.getLineItemss();				
					
					if (checkIfRateCdnaIsDifferent(lineItems, hLineItems)) {
						taxableValue = taxableValue.add(hLineItems.get(0).getTaxableValue());
						igstAmt = igstAmt.add(hLineItems.get(0).getIgstAmt());
						cgstAmt = cgstAmt.add(hLineItems.get(0).getCgstAmt());
						sgstAmt = sgstAmt.add(hLineItems.get(0).getSgstAmt());
						cessAmt = cessAmt.add(hLineItems.get(0).getCessAmt());
						
					}
					
				}
				finalEntitySum.setId(id);
				finalEntitySum.setChecksum(checksum);
				finalEntitySum.setItemNumber(itemNumber);
				finalEntitySum.setTaxRate(taxRate);
				finalEntitySum.setTaxableValue(taxableValue);
				finalEntitySum.setIgstAmt(igstAmt);
				finalEntitySum.setCgstAmt(cgstAmt);
				finalEntitySum.setSgstAmt(sgstAmt);
				finalEntitySum.setCessAmt(cessAmt);
				lineItemsforMultipleHeaders.add(finalEntitySum);
				headerEntity.get().setLineItemss(lineItemsforMultipleHeaders);
				lineItemsforMultipleHeaders.stream().filter(hlItem -> hlItem != null).forEach(
							lineItem -> lineItem.setHeader(headerEntity.get()));
			} else{
				
				for (int i = 1; i < headerList.size(); i++) {
					GetGstr2bStagingCdnrInvoicesHeaderEntity hEntity = headerList
							.get(i);
					List<GetGstr2bStagingCdnrInvoicesItemEntity> hLineItems = hEntity
							.getLineItemss();			
					
					if (checkIfRateCdnaIsDifferent(lineItems, hLineItems)) {
						
					}
					
				}
				headerEntity.get().setLineItemss(lineItems);
				lineItems.stream().filter(hlItem -> hlItem != null).forEach(
							lineItem -> lineItem.setHeader(headerEntity.get()));
			}
			
		}

		return headerEntity.get() != null ? headerEntity.get() : null;
	}

	private static boolean checkIfRateCdnaIsDifferent(
			List<GetGstr2bStagingCdnrInvoicesItemEntity> lineItems,
			List<GetGstr2bStagingCdnrInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2bStagingCdnrInvoicesItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2bStagingCdnrInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr2bStagingCdnraInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonCdrnaHeaders(
			List<GetGstr2bStagingCdnraInvoicesHeaderEntity> cdnraList) {
		List<GetGstr2bStagingCdnraInvoicesHeaderEntity> cdnaFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(cdnraList)) {
			Map<String, List<GetGstr2bStagingCdnraInvoicesHeaderEntity>> invoiceKeyMapList = cdnraList
					.stream().collect(Collectors.groupingBy(
							GetGstr2bStagingCdnraInvoicesHeaderEntity::getDocKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2bStagingCdnraInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2bStagingCdnraInvoicesHeaderEntity hEntity = mergeRatesIntoCdnHeader(
						headerList);
				if (hEntity != null) {
					cdnaFilterList.add(hEntity);
				}
			});
		}

		return cdnaFilterList;
	}

	private static GetGstr2bStagingCdnraInvoicesHeaderEntity mergeRatesIntoCdnHeader(
			List<GetGstr2bStagingCdnraInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2bStagingCdnraInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2bStagingCdnraInvoicesHeaderEntity>();
		List<GetGstr2bStagingCdnraInvoicesItemEntity> lineItems = new ArrayList<>();
		List<GetGstr2bStagingCdnraInvoicesItemEntity> lineItemsforMultipleHeaders = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItemss());
			headerEntity.get().setLineItemss(null);
			
			if(headerList.size() > 1){
				GetGstr2bStagingCdnraInvoicesItemEntity finalEntitySum = new GetGstr2bStagingCdnraInvoicesItemEntity();
				Long id = lineItems.get(0).getId();
				String checksum =  lineItems.get(0).getChecksum();
				int itemNumber =  lineItems.get(0).getItemNumber() ;
				BigDecimal taxRate  =   lineItems.get(0).getTaxRate() ;
				BigDecimal taxableValue =  lineItems.get(0).getTaxableValue();
				BigDecimal igstAmt = lineItems.get(0).getIgstAmt();
				BigDecimal cgstAmt =  lineItems.get(0).getCgstAmt();
				BigDecimal sgstAmt =  lineItems.get(0).getSgstAmt();
				BigDecimal cessAmt =  lineItems.get(0).getCessAmt();
				
				for (int i = 1; i < headerList.size(); i++) {
					GetGstr2bStagingCdnraInvoicesHeaderEntity hEntity = headerList
							.get(i);
					List<GetGstr2bStagingCdnraInvoicesItemEntity> hLineItems = hEntity
							.getLineItemss();				
					
					if (checkIfRateCdnIsDifferent(lineItems, hLineItems)) {
						taxableValue = taxableValue.add(hLineItems.get(0).getTaxableValue());
						igstAmt = igstAmt.add(hLineItems.get(0).getIgstAmt());
						cgstAmt = cgstAmt.add(hLineItems.get(0).getCgstAmt());
						sgstAmt = sgstAmt.add(hLineItems.get(0).getSgstAmt());
						cessAmt = cessAmt.add(hLineItems.get(0).getCessAmt());
						
					}
					
				}
				finalEntitySum.setId(id);
				finalEntitySum.setChecksum(checksum);
				finalEntitySum.setItemNumber(itemNumber);
				finalEntitySum.setTaxRate(taxRate);
				finalEntitySum.setTaxableValue(taxableValue);
				finalEntitySum.setIgstAmt(igstAmt);
				finalEntitySum.setCgstAmt(cgstAmt);
				finalEntitySum.setSgstAmt(sgstAmt);
				finalEntitySum.setCessAmt(cessAmt);
				lineItemsforMultipleHeaders.add(finalEntitySum);
				headerEntity.get().setLineItemss(lineItemsforMultipleHeaders);
				lineItemsforMultipleHeaders.stream().filter(hlItem -> hlItem != null).forEach(
							lineItem -> lineItem.setHeader(headerEntity.get()));
			} else{
				
				for (int i = 1; i < headerList.size(); i++) {
					GetGstr2bStagingCdnraInvoicesHeaderEntity hEntity = headerList
							.get(i);
					List<GetGstr2bStagingCdnraInvoicesItemEntity> hLineItems = hEntity
							.getLineItemss();			
					
					if (checkIfRateCdnIsDifferent(lineItems, hLineItems)) {
						
					}
					
				}
				headerEntity.get().setLineItemss(lineItems);
				lineItems.stream().filter(hlItem -> hlItem != null).forEach(
							lineItem -> lineItem.setHeader(headerEntity.get()));
			}
			
		}

		return headerEntity.get() != null ? headerEntity.get() : null;
	}

	private static boolean checkIfRateCdnIsDifferent(
			List<GetGstr2bStagingCdnraInvoicesItemEntity> lineItems,
			List<GetGstr2bStagingCdnraInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2bStagingCdnraInvoicesItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2bStagingCdnraInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}
	
	private static GetGstr2bB2bEcomHeaderEntity mergeRatesIntoEcomHeader(
			List<GetGstr2bB2bEcomHeaderEntity> headerList) {
		AtomicReference<GetGstr2bB2bEcomHeaderEntity> headerEntity = new AtomicReference<GetGstr2bB2bEcomHeaderEntity>();
		List<GetGstr2bEcomItemEntity> lineItems = new ArrayList<>();
		List<GetGstr2bEcomItemEntity> lineItemsforMultipleHeaders = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			
			if(headerList.size() > 1){
				GetGstr2bEcomItemEntity finalEntitySum = new GetGstr2bEcomItemEntity();
				Long id = lineItems.get(0).getId();
				String checksum =  lineItems.get(0).getChecksum();
				int itemNumber =  lineItems.get(0).getItemNumber() ;
				BigDecimal taxRate  =   lineItems.get(0).getTaxRate() ;
				BigDecimal taxableValue =  lineItems.get(0).getTaxableValue();
				BigDecimal igstAmt = lineItems.get(0).getIgstAmt();
				BigDecimal cgstAmt =  lineItems.get(0).getCgstAmt();
				BigDecimal sgstAmt =  lineItems.get(0).getSgstAmt();
				BigDecimal cessAmt =  lineItems.get(0).getCessAmt();
				
				for (int i = 1; i < headerList.size(); i++) {
					GetGstr2bB2bEcomHeaderEntity hEntity = headerList
							.get(i);
					List<GetGstr2bEcomItemEntity> hLineItems = hEntity
							.getLineItems();				
					
					if (checkIfRateIsDifferentForEcom(lineItems, hLineItems)) {
						taxableValue = taxableValue.add(hLineItems.get(0).getTaxableValue());
						igstAmt = igstAmt.add(hLineItems.get(0).getIgstAmt());
						cgstAmt = cgstAmt.add(hLineItems.get(0).getCgstAmt());
						sgstAmt = sgstAmt.add(hLineItems.get(0).getSgstAmt());
						cessAmt = cessAmt.add(hLineItems.get(0).getCessAmt());
						
					}
					
				}
				finalEntitySum.setId(id);
				finalEntitySum.setChecksum(checksum);
				finalEntitySum.setItemNumber(itemNumber);
				finalEntitySum.setTaxRate(taxRate);
				finalEntitySum.setTaxableValue(taxableValue);
				finalEntitySum.setIgstAmt(igstAmt);
				finalEntitySum.setCgstAmt(cgstAmt);
				finalEntitySum.setSgstAmt(sgstAmt);
				finalEntitySum.setCessAmt(cessAmt);
				lineItemsforMultipleHeaders.add(finalEntitySum);
				headerEntity.get().setLineItems(lineItemsforMultipleHeaders);
				lineItemsforMultipleHeaders.stream().filter(hlItem -> hlItem != null).forEach(
							lineItem -> lineItem.setHeader(headerEntity.get()));
			} else{
				
				for (int i = 1; i < headerList.size(); i++) {
					GetGstr2bB2bEcomHeaderEntity hEntity = headerList
							.get(i);
					List<GetGstr2bEcomItemEntity> hLineItems = hEntity
							.getLineItems();		
					
					if (checkIfRateIsDifferentForEcom(lineItems, hLineItems)) {
						
					}
					
				}
				headerEntity.get().setLineItems(lineItems);
				lineItems.stream().filter(hlItem -> hlItem != null).forEach(
							lineItem -> lineItem.setHeader(headerEntity.get()));
			}
			
		}

		return headerEntity.get() != null ? headerEntity.get() : null;
	}

	public static List<GetGstr2bB2bEcomaHeaderEntity> filterInvoiceKeyAndMergeRateonEcomaHeaders(
			List<GetGstr2bB2bEcomaHeaderEntity> ecomaList) {
		List<GetGstr2bB2bEcomaHeaderEntity> ecomaFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(ecomaList)) {
			Map<String, List<GetGstr2bB2bEcomaHeaderEntity>> invoiceKeyMapList = ecomaList
					.stream().collect(Collectors.groupingBy(
							GetGstr2bB2bEcomaHeaderEntity::getDocKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2bB2bEcomaHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2bB2bEcomaHeaderEntity hEntity = mergeRatesIntoEcomaHeader(
						headerList);
				if (hEntity != null) {
					ecomaFilterList.add(hEntity);
				}
			});
		}

		return ecomaFilterList;
	}
	
	private static GetGstr2bB2bEcomaHeaderEntity mergeRatesIntoEcomaHeader(
			List<GetGstr2bB2bEcomaHeaderEntity> headerList) {
		AtomicReference<GetGstr2bB2bEcomaHeaderEntity> headerEntity = new AtomicReference<GetGstr2bB2bEcomaHeaderEntity>();
		List<GetGstr2bEcomaItemEntity> lineItems = new ArrayList<>();
		List<GetGstr2bEcomaItemEntity> lineItemsforMultipleHeaders = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItemss());
			headerEntity.get().setLineItemss(null);
			
			if(headerList.size() > 1){
				GetGstr2bEcomaItemEntity finalEntitySum = new GetGstr2bEcomaItemEntity();
				Long id = lineItems.get(0).getId();
				String checksum =  lineItems.get(0).getChecksum();
				int itemNumber =  lineItems.get(0).getItemNumber() ;
				BigDecimal taxRate  =   lineItems.get(0).getTaxRate() ;
				BigDecimal taxableValue =  lineItems.get(0).getTaxableValue();
				BigDecimal igstAmt = lineItems.get(0).getIgstAmt();
				BigDecimal cgstAmt =  lineItems.get(0).getCgstAmt();
				BigDecimal sgstAmt =  lineItems.get(0).getSgstAmt();
				BigDecimal cessAmt =  lineItems.get(0).getCessAmt();
				
				for (int i = 1; i < headerList.size(); i++) {
					GetGstr2bB2bEcomaHeaderEntity hEntity = headerList
							.get(i);
					List<GetGstr2bEcomaItemEntity> hLineItems = hEntity
							.getLineItemss();				
					
					if (checkIfRateEcomaIsDifferent(lineItems, hLineItems)) {
						taxableValue = taxableValue.add(hLineItems.get(0).getTaxableValue());
						igstAmt = igstAmt.add(hLineItems.get(0).getIgstAmt());
						cgstAmt = cgstAmt.add(hLineItems.get(0).getCgstAmt());
						sgstAmt = sgstAmt.add(hLineItems.get(0).getSgstAmt());
						cessAmt = cessAmt.add(hLineItems.get(0).getCessAmt());
						
					}
					
				}
				finalEntitySum.setId(id);
				finalEntitySum.setChecksum(checksum);
				finalEntitySum.setItemNumber(itemNumber);
				finalEntitySum.setTaxRate(taxRate);
				finalEntitySum.setTaxableValue(taxableValue);
				finalEntitySum.setIgstAmt(igstAmt);
				finalEntitySum.setCgstAmt(cgstAmt);
				finalEntitySum.setSgstAmt(sgstAmt);
				finalEntitySum.setCessAmt(cessAmt);
				lineItemsforMultipleHeaders.add(finalEntitySum);
				headerEntity.get().setLineItemss(lineItemsforMultipleHeaders);
				lineItemsforMultipleHeaders.stream().filter(hlItem -> hlItem != null).forEach(
							lineItem -> lineItem.setHeader(headerEntity.get()));
			} else{
				
				for (int i = 1; i < headerList.size(); i++) {
					GetGstr2bB2bEcomaHeaderEntity hEntity = headerList
							.get(i);
					List<GetGstr2bEcomaItemEntity> hLineItems = hEntity
							.getLineItemss();			
					
					if (checkIfRateEcomaIsDifferent(lineItems, hLineItems)) {
						
					}
					
				}
				headerEntity.get().setLineItemss(lineItems);
				lineItems.stream().filter(hlItem -> hlItem != null).forEach(
							lineItem -> lineItem.setHeader(headerEntity.get()));
			}
			
		}

		return headerEntity.get() != null ? headerEntity.get() : null;
	}
	
	private static boolean checkIfRateEcomaIsDifferent(
			List<GetGstr2bEcomaItemEntity> lineItems,
			List<GetGstr2bEcomaItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2bEcomaItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2bEcomaItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}
	
}
