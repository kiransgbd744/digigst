package com.ey.advisory.app.services.jobs.gstr6;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.ey.advisory.app.data.entities.client.GetGstr2aB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aB2bInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aB2baInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnaInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aEcomInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aEcomInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aEcomaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aEcomaInvoicesItemEntity;
import com.google.common.collect.Lists;

public class Gstr6aTaxRateDiffUtil {

	public static List<GetGstr2aB2bInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonB2bHeader(
			List<GetGstr2aB2bInvoicesHeaderEntity> b2bList) {
		List<GetGstr2aB2bInvoicesHeaderEntity> b2bFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(b2bList)) {
			Map<String, List<GetGstr2aB2bInvoicesHeaderEntity>> invoiceKeyMapList = b2bList
					.stream().collect(Collectors.groupingBy(
							GetGstr2aB2bInvoicesHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2aB2bInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2aB2bInvoicesHeaderEntity hEntity = mergeRatesIntoB2bHeader(
						headerList);
				if (hEntity != null) {
					b2bFilterList.add(hEntity);
				}
			});
		}

		return b2bFilterList;
	}

	private static GetGstr2aB2bInvoicesHeaderEntity mergeRatesIntoB2bHeader(
			List<GetGstr2aB2bInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2aB2bInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2aB2bInvoicesHeaderEntity>();
		List<GetGstr2aB2bInvoicesItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr2aB2bInvoicesHeaderEntity hEntity = headerList.get(i);
				List<GetGstr2aB2bInvoicesItemEntity> hLineItems = hEntity
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
			List<GetGstr2aB2bInvoicesItemEntity> lineItems,
			List<GetGstr2aB2bInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2aB2bInvoicesItemEntity lineItem = hLineItems.size() == 1
					? hLineItems.get(0) : hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2aB2bInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr2aB2baInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonB2baHeader(
			List<GetGstr2aB2baInvoicesHeaderEntity> b2baList) {
		List<GetGstr2aB2baInvoicesHeaderEntity> b2baFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(b2baList)) {
			Map<String, List<GetGstr2aB2baInvoicesHeaderEntity>> invoiceKeyMapList = b2baList
					.stream().collect(Collectors.groupingBy(
							GetGstr2aB2baInvoicesHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2aB2baInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2aB2baInvoicesHeaderEntity hEntity = mergeRatesIntoB2baHeader(
						headerList);
				if (hEntity != null) {
					b2baFilterList.add(hEntity);
				}
			});
		}

		return b2baFilterList;
	}

	private static GetGstr2aB2baInvoicesHeaderEntity mergeRatesIntoB2baHeader(
			List<GetGstr2aB2baInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2aB2baInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2aB2baInvoicesHeaderEntity>();
		List<GetGstr2aB2baInvoicesItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr2aB2baInvoicesHeaderEntity hEntity = headerList.get(i);
				List<GetGstr2aB2baInvoicesItemEntity> hLineItems = hEntity
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
			List<GetGstr2aB2baInvoicesItemEntity> lineItems,
			List<GetGstr2aB2baInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2aB2baInvoicesItemEntity lineItem = hLineItems.size() == 1
					? hLineItems.get(0) : hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2aB2baInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr2aCdnaInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonCdnaHeader(
			List<GetGstr2aCdnaInvoicesHeaderEntity> cdnraList) {
		List<GetGstr2aCdnaInvoicesHeaderEntity> cdnaFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(cdnraList)) {
			Map<String, List<GetGstr2aCdnaInvoicesHeaderEntity>> invoiceKeyMapList = cdnraList
					.stream().collect(Collectors.groupingBy(
							GetGstr2aCdnaInvoicesHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2aCdnaInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2aCdnaInvoicesHeaderEntity hEntity = mergeRatesIntoCdnaHeader(
						headerList);
				if (hEntity != null) {
					cdnaFilterList.add(hEntity);
				}
			});
		}

		return cdnaFilterList;
	}

	private static GetGstr2aCdnaInvoicesHeaderEntity mergeRatesIntoCdnaHeader(
			List<GetGstr2aCdnaInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2aCdnaInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2aCdnaInvoicesHeaderEntity>();
		List<GetGstr2aCdnaInvoicesItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr2aCdnaInvoicesHeaderEntity hEntity = headerList.get(i);
				List<GetGstr2aCdnaInvoicesItemEntity> hLineItems = hEntity
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
			List<GetGstr2aCdnaInvoicesItemEntity> lineItems,
			List<GetGstr2aCdnaInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2aCdnaInvoicesItemEntity lineItem = hLineItems.size() == 1
					? hLineItems.get(0) : hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxrate();
			List<GetGstr2aCdnaInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxrate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr2aCdnInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonCdnHeader(
			List<GetGstr2aCdnInvoicesHeaderEntity> cdnraList) {
		List<GetGstr2aCdnInvoicesHeaderEntity> cdnFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(cdnraList)) {
			Map<String, List<GetGstr2aCdnInvoicesHeaderEntity>> invoiceKeyMapList = cdnraList
					.stream().collect(Collectors.groupingBy(
							GetGstr2aCdnInvoicesHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2aCdnInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2aCdnInvoicesHeaderEntity hEntity = mergeRatesIntoCdnHeader(
						headerList);
				if (hEntity != null) {
					cdnFilterList.add(hEntity);
				}
			});
		}

		return cdnFilterList;
	}

	private static GetGstr2aCdnInvoicesHeaderEntity mergeRatesIntoCdnHeader(
			List<GetGstr2aCdnInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2aCdnInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2aCdnInvoicesHeaderEntity>();
		List<GetGstr2aCdnInvoicesItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr2aCdnInvoicesHeaderEntity hEntity = headerList.get(i);
				List<GetGstr2aCdnInvoicesItemEntity> hLineItems = hEntity
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
			List<GetGstr2aCdnInvoicesItemEntity> lineItems,
			List<GetGstr2aCdnInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2aCdnInvoicesItemEntity lineItem = hLineItems.size() == 1
					? hLineItems.get(0) : hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxrate();
			List<GetGstr2aCdnInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxrate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}
	
	public static List<GetGstr2aEcomInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonEcomHeader(
			List<GetGstr2aEcomInvoicesHeaderEntity> ecomList) {
		List<GetGstr2aEcomInvoicesHeaderEntity> ecomFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(ecomList)) {
			Map<String, List<GetGstr2aEcomInvoicesHeaderEntity>> invoiceKeyMapList = ecomList
					.stream().collect(Collectors.groupingBy(
							GetGstr2aEcomInvoicesHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2aEcomInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2aEcomInvoicesHeaderEntity hEntity = mergeRatesIntoEcomHeader(
						headerList);
				if (hEntity != null) {
					ecomFilterList.add(hEntity);
				}
			});
		}

		return ecomFilterList;
	}

	private static GetGstr2aEcomInvoicesHeaderEntity mergeRatesIntoEcomHeader(
			List<GetGstr2aEcomInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2aEcomInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2aEcomInvoicesHeaderEntity>();
		List<GetGstr2aEcomInvoicesItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr2aEcomInvoicesHeaderEntity hEntity = headerList.get(i);
				List<GetGstr2aEcomInvoicesItemEntity> hLineItems = hEntity
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
			List<GetGstr2aEcomInvoicesItemEntity> lineItems,
			List<GetGstr2aEcomInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2aEcomInvoicesItemEntity lineItem = hLineItems.size() == 1
					? hLineItems.get(0) : hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2aEcomInvoicesItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr2aEcomaInvoicesHeaderEntity> filterInvoiceKeyAndMergeRateonEcomaHeader(
			List<GetGstr2aEcomaInvoicesHeaderEntity> ecomaList) {
		List<GetGstr2aEcomaInvoicesHeaderEntity> ecomaFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(ecomaList)) {
			Map<String, List<GetGstr2aEcomaInvoicesHeaderEntity>> invoiceKeyMapList = ecomaList
					.stream().collect(Collectors.groupingBy(
							GetGstr2aEcomaInvoicesHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr2aEcomaInvoicesHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr2aEcomaInvoicesHeaderEntity hEntity = mergeRatesIntoEcomaHeader(
						headerList);
				if (hEntity != null) {
					ecomaFilterList.add(hEntity);
				}
			});
		}

		return ecomaFilterList;
	}

	private static GetGstr2aEcomaInvoicesHeaderEntity mergeRatesIntoEcomaHeader(
			List<GetGstr2aEcomaInvoicesHeaderEntity> headerList) {
		AtomicReference<GetGstr2aEcomaInvoicesHeaderEntity> headerEntity = new AtomicReference<GetGstr2aEcomaInvoicesHeaderEntity>();
		List<GetGstr2aEcomaInvoicesItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr2aEcomaInvoicesHeaderEntity hEntity = headerList.get(i);
				List<GetGstr2aEcomaInvoicesItemEntity> hLineItems = hEntity
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
			List<GetGstr2aEcomaInvoicesItemEntity> lineItems,
			List<GetGstr2aEcomaInvoicesItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr2aEcomaInvoicesItemEntity lineItem = hLineItems.size() == 1
					? hLineItems.get(0) : hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr2aEcomaInvoicesItemEntity> itemEntities = lineItems
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
