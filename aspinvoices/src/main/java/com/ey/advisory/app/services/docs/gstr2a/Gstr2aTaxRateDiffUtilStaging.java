package com.ey.advisory.app.services.docs.gstr2a;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2bInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2baInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnaInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomaInvoicesItemEntity;
import com.google.common.collect.Lists;

public class Gstr2aTaxRateDiffUtilStaging {

	public static List<GetGstr2aStagingB2bInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonB2bHeaders(
			List<GetGstr2aStagingB2bInvoicesHeaderEntity> b2bList) {
		List<GetGstr2aStagingB2bInvoicesHeaderEntity> b2bFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(b2bList)) {
			Map<String, List<GetGstr2aStagingB2bInvoicesHeaderEntity>> invoiceKeyMapList = b2bList
					.stream().collect(Collectors.groupingBy(
							GetGstr2aStagingB2bInvoicesHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2aStagingB2bInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2aStagingB2bInvoicesHeaderEntity hEntity = mergeRatesIntoB2bHeader(
						headerList);
				if (hEntity != null) {
					b2bFilterList.add(hEntity);
				}
			});
		}

		return b2bFilterList;
	}

	private static GetGstr2aStagingB2bInvoicesHeaderEntity mergeRatesIntoB2bHeader(
			List<GetGstr2aStagingB2bInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2aStagingB2bInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2aStagingB2bInvoicesHeaderEntity>();
		List<GetGstr2aStagingB2bInvoicesItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr2aStagingB2bInvoicesHeaderEntity hEntity = headerList
						.get(i);
				List<GetGstr2aStagingB2bInvoicesItemEntity> hLineItems = hEntity
						.getLineItems();
				if (checkIfRateIsDifferent(lineItems, hLineItems)) {
					headerEntity.get().setTaxable(headerEntity.get()
							.getTaxable().add(hEntity.getTaxable()));
					headerEntity.get().setIgstAmt(headerEntity.get()
							.getIgstAmt().add(hEntity.getIgstAmt()));
					headerEntity.get().setCgstAmt(headerEntity.get()
							.getCgstAmt().add(hEntity.getCgstAmt()));
					headerEntity.get().setSgstAmt(headerEntity.get()
							.getSgstAmt().add(hEntity.getSgstAmt()));
					headerEntity.get().setCessAmt(headerEntity.get()
							.getCessAmt().add(hEntity.getCessAmt()));
				}
			}
			headerEntity.get().setLineItems(lineItems);
			lineItems.stream().filter(hlItem -> hlItem != null).forEach(
					lineItem -> lineItem.setHeader(headerEntity.get()));
		}

		return headerEntity.get() != null ? headerEntity.get() : null;
	}

	private static boolean checkIfRateIsDifferent(
			List<GetGstr2aStagingB2bInvoicesItemEntity> lineItems,
			List<GetGstr2aStagingB2bInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2aStagingB2bInvoicesItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2aStagingB2bInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr2aStagingB2baInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonB2baHeaders(
			List<GetGstr2aStagingB2baInvoicesHeaderEntity> b2baList) {
		List<GetGstr2aStagingB2baInvoicesHeaderEntity> b2baFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(b2baList)) {
			Map<String, List<GetGstr2aStagingB2baInvoicesHeaderEntity>> invoiceKeyMapList = b2baList
					.stream().collect(Collectors.groupingBy(
							GetGstr2aStagingB2baInvoicesHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2aStagingB2baInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2aStagingB2baInvoicesHeaderEntity hEntity = mergeRatesIntoB2baHeader(
						headerList);
				if (hEntity != null) {
					b2baFilterList.add(hEntity);
				}
			});
		}

		return b2baFilterList;
	}

	private static GetGstr2aStagingB2baInvoicesHeaderEntity mergeRatesIntoB2baHeader(
			List<GetGstr2aStagingB2baInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2aStagingB2baInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2aStagingB2baInvoicesHeaderEntity>();
		List<GetGstr2aStagingB2baInvoicesItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr2aStagingB2baInvoicesHeaderEntity hEntity = headerList
						.get(i);
				List<GetGstr2aStagingB2baInvoicesItemEntity> hLineItems = hEntity
						.getLineItems();
				if (checkIfRateB2baIsDifferent(lineItems, hLineItems)) {
					headerEntity.get().setTaxable(headerEntity.get()
							.getTaxable().add(hEntity.getTaxable()));
					headerEntity.get().setIgstAmt(headerEntity.get()
							.getIgstAmt().add(hEntity.getIgstAmt()));
					headerEntity.get().setCgstAmt(headerEntity.get()
							.getCgstAmt().add(hEntity.getCgstAmt()));
					headerEntity.get().setSgstAmt(headerEntity.get()
							.getSgstAmt().add(hEntity.getSgstAmt()));
					headerEntity.get().setCessAmt(headerEntity.get()
							.getCessAmt().add(hEntity.getCessAmt()));
				}
			}
			headerEntity.get().setLineItems(lineItems);
			lineItems.stream().filter(hlItem -> hlItem != null).forEach(
					lineItem -> lineItem.setHeader(headerEntity.get()));
		}

		return headerEntity.get() != null ? headerEntity.get() : null;
	}

	private static boolean checkIfRateB2baIsDifferent(
			List<GetGstr2aStagingB2baInvoicesItemEntity> lineItems,
			List<GetGstr2aStagingB2baInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2aStagingB2baInvoicesItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2aStagingB2baInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr2aStagingCdnaInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonCdnaHeaders(
			List<GetGstr2aStagingCdnaInvoicesHeaderEntity> cdnraList) {
		List<GetGstr2aStagingCdnaInvoicesHeaderEntity> cdnaFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(cdnraList)) {
			Map<String, List<GetGstr2aStagingCdnaInvoicesHeaderEntity>> invoiceKeyMapList = cdnraList
					.stream().collect(Collectors.groupingBy(
							GetGstr2aStagingCdnaInvoicesHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2aStagingCdnaInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2aStagingCdnaInvoicesHeaderEntity hEntity = mergeRatesIntoCdnaHeader(
						headerList);
				if (hEntity != null) {
					cdnaFilterList.add(hEntity);
				}
			});
		}

		return cdnaFilterList;
	}

	private static GetGstr2aStagingCdnaInvoicesHeaderEntity mergeRatesIntoCdnaHeader(
			List<GetGstr2aStagingCdnaInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2aStagingCdnaInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2aStagingCdnaInvoicesHeaderEntity>();
		List<GetGstr2aStagingCdnaInvoicesItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr2aStagingCdnaInvoicesHeaderEntity hEntity = headerList
						.get(i);
				List<GetGstr2aStagingCdnaInvoicesItemEntity> hLineItems = hEntity
						.getLineItems();
				if (checkIfRateCdnaIsDifferent(lineItems, hLineItems)) {
					headerEntity.get().setTaxVal(headerEntity.get().getTaxVal()
							.add(hEntity.getTaxVal()));
					headerEntity.get().setIgstamt(headerEntity.get()
							.getIgstamt().add(hEntity.getIgstamt()));
					headerEntity.get().setCgstamt(headerEntity.get()
							.getCgstamt().add(hEntity.getCgstamt()));
					headerEntity.get().setSgstamt(headerEntity.get()
							.getSgstamt().add(hEntity.getSgstamt()));
					headerEntity.get().setCessamt(headerEntity.get()
							.getCessamt().add(hEntity.getCessamt()));
				}
			}
			headerEntity.get().setLineItems(lineItems);
			lineItems.stream().filter(hlItem -> hlItem != null).forEach(
					lineItem -> lineItem.setHeader(headerEntity.get()));
		}

		return headerEntity.get() != null ? headerEntity.get() : null;
	}

	private static boolean checkIfRateCdnaIsDifferent(
			List<GetGstr2aStagingCdnaInvoicesItemEntity> lineItems,
			List<GetGstr2aStagingCdnaInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2aStagingCdnaInvoicesItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxrate();
			List<GetGstr2aStagingCdnaInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxrate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr2aStagingCdnInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonCdnHeaders(
			List<GetGstr2aStagingCdnInvoicesHeaderEntity> cdnraList) {
		List<GetGstr2aStagingCdnInvoicesHeaderEntity> cdnaFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(cdnraList)) {
			Map<String, List<GetGstr2aStagingCdnInvoicesHeaderEntity>> invoiceKeyMapList = cdnraList
					.stream().collect(Collectors.groupingBy(
							GetGstr2aStagingCdnInvoicesHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2aStagingCdnInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2aStagingCdnInvoicesHeaderEntity hEntity = mergeRatesIntoCdnHeader(
						headerList);
				if (hEntity != null) {
					cdnaFilterList.add(hEntity);
				}
			});
		}

		return cdnaFilterList;
	}

	private static GetGstr2aStagingCdnInvoicesHeaderEntity mergeRatesIntoCdnHeader(
			List<GetGstr2aStagingCdnInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2aStagingCdnInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2aStagingCdnInvoicesHeaderEntity>();
		List<GetGstr2aStagingCdnInvoicesItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr2aStagingCdnInvoicesHeaderEntity hEntity = headerList
						.get(i);
				List<GetGstr2aStagingCdnInvoicesItemEntity> hLineItems = hEntity
						.getLineItems();
				if (checkIfRateCdnIsDifferent(lineItems, hLineItems)) {
					headerEntity.get().setTaxVal(headerEntity.get().getTaxVal()
							.add(hEntity.getTaxVal()));
					headerEntity.get().setIgstamt(headerEntity.get()
							.getIgstamt().add(hEntity.getIgstamt()));
					headerEntity.get().setCgstamt(headerEntity.get()
							.getCgstamt().add(hEntity.getCgstamt()));
					headerEntity.get().setSgstamt(headerEntity.get()
							.getSgstamt().add(hEntity.getSgstamt()));
					headerEntity.get().setCessamt(headerEntity.get()
							.getCessamt().add(hEntity.getCessamt()));
				}
			}
			headerEntity.get().setLineItems(lineItems);
			lineItems.stream().filter(hlItem -> hlItem != null).forEach(
					lineItem -> lineItem.setHeader(headerEntity.get()));
		}

		return headerEntity.get() != null ? headerEntity.get() : null;
	}

	private static boolean checkIfRateCdnIsDifferent(
			List<GetGstr2aStagingCdnInvoicesItemEntity> lineItems,
			List<GetGstr2aStagingCdnInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2aStagingCdnInvoicesItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxrate();
			List<GetGstr2aStagingCdnInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxrate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}
	
	public static List<GetGstr2aStagingEcomInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonEcomHeaders(
			List<GetGstr2aStagingEcomInvoicesHeaderEntity> ecomList) {
		List<GetGstr2aStagingEcomInvoicesHeaderEntity> ecomFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(ecomList)) {
			Map<String, List<GetGstr2aStagingEcomInvoicesHeaderEntity>> invoiceKeyMapList = ecomList
					.stream().collect(Collectors.groupingBy(
							GetGstr2aStagingEcomInvoicesHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2aStagingEcomInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2aStagingEcomInvoicesHeaderEntity hEntity = mergeRatesIntoEcomHeader(
						headerList);
				if (hEntity != null) {
					ecomFilterList.add(hEntity);
				}
			});
		}

		return ecomFilterList;
	}

	private static GetGstr2aStagingEcomInvoicesHeaderEntity mergeRatesIntoEcomHeader(
			List<GetGstr2aStagingEcomInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2aStagingEcomInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2aStagingEcomInvoicesHeaderEntity>();
		List<GetGstr2aStagingEcomInvoicesItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr2aStagingEcomInvoicesHeaderEntity hEntity = headerList
						.get(i);
				List<GetGstr2aStagingEcomInvoicesItemEntity> hLineItems = hEntity
						.getLineItems();
				if (checkIfRateIsDifferentEcom(lineItems, hLineItems)) {
					headerEntity.get().setTaxable(headerEntity.get()
							.getTaxable().add(hEntity.getTaxable()));
					headerEntity.get().setIgstAmt(headerEntity.get()
							.getIgstAmt().add(hEntity.getIgstAmt()));
					headerEntity.get().setCgstAmt(headerEntity.get()
							.getCgstAmt().add(hEntity.getCgstAmt()));
					headerEntity.get().setSgstAmt(headerEntity.get()
							.getSgstAmt().add(hEntity.getSgstAmt()));
					headerEntity.get().setCessAmt(headerEntity.get()
							.getCessAmt().add(hEntity.getCessAmt()));
				}
			}
			headerEntity.get().setLineItems(lineItems);
			lineItems.stream().filter(hlItem -> hlItem != null).forEach(
					lineItem -> lineItem.setHeader(headerEntity.get()));
		}

		return headerEntity.get() != null ? headerEntity.get() : null;
	}

	private static boolean checkIfRateIsDifferentEcom(
			List<GetGstr2aStagingEcomInvoicesItemEntity> lineItems,
			List<GetGstr2aStagingEcomInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2aStagingEcomInvoicesItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2aStagingEcomInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr2aStagingEcomaInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonEcomaHeaders(
			List<GetGstr2aStagingEcomaInvoicesHeaderEntity> ecomaList) {
		List<GetGstr2aStagingEcomaInvoicesHeaderEntity> ecomaFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(ecomaList)) {
			Map<String, List<GetGstr2aStagingEcomaInvoicesHeaderEntity>> invoiceKeyMapList = ecomaList
					.stream().collect(Collectors.groupingBy(
							GetGstr2aStagingEcomaInvoicesHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2aStagingEcomaInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2aStagingEcomaInvoicesHeaderEntity hEntity = mergeRatesIntoEcomaHeader(
						headerList);
				if (hEntity != null) {
					ecomaFilterList.add(hEntity);
				}
			});
		}

		return ecomaFilterList;
	}

	private static GetGstr2aStagingEcomaInvoicesHeaderEntity mergeRatesIntoEcomaHeader(
			List<GetGstr2aStagingEcomaInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2aStagingEcomaInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2aStagingEcomaInvoicesHeaderEntity>();
		List<GetGstr2aStagingEcomaInvoicesItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr2aStagingEcomaInvoicesHeaderEntity hEntity = headerList
						.get(i);
				List<GetGstr2aStagingEcomaInvoicesItemEntity> hLineItems = hEntity
						.getLineItems();
				if (checkIfRateEcomaIsDifferent(lineItems, hLineItems)) {
					headerEntity.get().setTaxable(headerEntity.get()
							.getTaxable().add(hEntity.getTaxable()));
					headerEntity.get().setIgstAmt(headerEntity.get()
							.getIgstAmt().add(hEntity.getIgstAmt()));
					headerEntity.get().setCgstAmt(headerEntity.get()
							.getCgstAmt().add(hEntity.getCgstAmt()));
					headerEntity.get().setSgstAmt(headerEntity.get()
							.getSgstAmt().add(hEntity.getSgstAmt()));
					headerEntity.get().setCessAmt(headerEntity.get()
							.getCessAmt().add(hEntity.getCessAmt()));
				}
			}
			headerEntity.get().setLineItems(lineItems);
			lineItems.stream().filter(hlItem -> hlItem != null).forEach(
					lineItem -> lineItem.setHeader(headerEntity.get()));
		}

		return headerEntity.get() != null ? headerEntity.get() : null;
	}

	private static boolean checkIfRateEcomaIsDifferent(
			List<GetGstr2aStagingEcomaInvoicesItemEntity> lineItems,
			List<GetGstr2aStagingEcomaInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2aStagingEcomaInvoicesItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2aStagingEcomaInvoicesItemEntity> itemEntities = lineItems
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
