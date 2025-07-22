package com.ey.advisory.app.services.jobs.gstr6;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomaInvoicesItemEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2bHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2bItemEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2baHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2baItemEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnItemEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnaHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnaItemEntity;
import com.google.common.collect.Lists;

public class Gstr6aTaxRateDiffUtilStaging {

	public static List<GetGstr6aStagingB2bHeaderEntity> filterInvoiceKeyAndMergeRateonB2bHeaders(
			List<GetGstr6aStagingB2bHeaderEntity> b2bList) {
		List<GetGstr6aStagingB2bHeaderEntity> b2bFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(b2bList)) {
			Map<String, List<GetGstr6aStagingB2bHeaderEntity>> invoiceKeyMapList = b2bList
					.stream().collect(Collectors.groupingBy(
							GetGstr6aStagingB2bHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr6aStagingB2bHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr6aStagingB2bHeaderEntity hEntity = mergeRatesIntoB2bHeader(
						headerList);
				if (hEntity != null) {
					b2bFilterList.add(hEntity);
				}
			});
		}

		return b2bFilterList;
	}

	private static GetGstr6aStagingB2bHeaderEntity mergeRatesIntoB2bHeader(
			List<GetGstr6aStagingB2bHeaderEntity> headerList) {
		AtomicReference<GetGstr6aStagingB2bHeaderEntity> headerEntity = new AtomicReference<GetGstr6aStagingB2bHeaderEntity>();
		List<GetGstr6aStagingB2bItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr6aStagingB2bHeaderEntity hEntity = headerList
						.get(i);
				List<GetGstr6aStagingB2bItemEntity> hLineItems = hEntity
						.getLineItems();
				if (checkIfRateIsDifferent(lineItems, hLineItems)) {
					headerEntity.get().setTaxableValue(headerEntity.get()
							.getTaxableValue().add(hEntity.getTaxableValue()));
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
			List<GetGstr6aStagingB2bItemEntity> lineItems,
			List<GetGstr6aStagingB2bItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr6aStagingB2bItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr6aStagingB2bItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr6aStagingB2baHeaderEntity> filterInvoiceKeyAndMergeRateonB2baHeaders(
			List<GetGstr6aStagingB2baHeaderEntity> b2baList) {
		List<GetGstr6aStagingB2baHeaderEntity> b2baFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(b2baList)) {
			Map<String, List<GetGstr6aStagingB2baHeaderEntity>> invoiceKeyMapList = b2baList
					.stream().collect(Collectors.groupingBy(
							GetGstr6aStagingB2baHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr6aStagingB2baHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr6aStagingB2baHeaderEntity hEntity = mergeRatesIntoB2baHeader(
						headerList);
				if (hEntity != null) {
					b2baFilterList.add(hEntity);
				}
			});
		}

		return b2baFilterList;
	}

	private static GetGstr6aStagingB2baHeaderEntity mergeRatesIntoB2baHeader(
			List<GetGstr6aStagingB2baHeaderEntity> headerList) {
		AtomicReference<GetGstr6aStagingB2baHeaderEntity> headerEntity = new AtomicReference<GetGstr6aStagingB2baHeaderEntity>();
		List<GetGstr6aStagingB2baItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr6aStagingB2baHeaderEntity hEntity = headerList
						.get(i);
				List<GetGstr6aStagingB2baItemEntity> hLineItems = hEntity
						.getLineItems();
				if (checkIfRateB2baIsDifferent(lineItems, hLineItems)) {
					headerEntity.get().setTaxableValue(headerEntity.get()
							.getTaxableValue().add(hEntity.getTaxableValue()));
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
			List<GetGstr6aStagingB2baItemEntity> lineItems,
			List<GetGstr6aStagingB2baItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr6aStagingB2baItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr6aStagingB2baItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr6aStagingCdnaHeaderEntity> filterInvoiceKeyAndMergeRateonCdnaHeaders(
			List<GetGstr6aStagingCdnaHeaderEntity> cdnraList) {
		List<GetGstr6aStagingCdnaHeaderEntity> cdnaFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(cdnraList)) {
			Map<String, List<GetGstr6aStagingCdnaHeaderEntity>> invoiceKeyMapList = cdnraList
					.stream().collect(Collectors.groupingBy(
							GetGstr6aStagingCdnaHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr6aStagingCdnaHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr6aStagingCdnaHeaderEntity hEntity = mergeRatesIntoCdnaHeader(
						headerList);
				if (hEntity != null) {
					cdnaFilterList.add(hEntity);
				}
			});
		}

		return cdnaFilterList;
	}

	private static GetGstr6aStagingCdnaHeaderEntity mergeRatesIntoCdnaHeader(
			List<GetGstr6aStagingCdnaHeaderEntity> headerList) {
		AtomicReference<GetGstr6aStagingCdnaHeaderEntity> headerEntity = new AtomicReference<GetGstr6aStagingCdnaHeaderEntity>();
		List<GetGstr6aStagingCdnaItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr6aStagingCdnaHeaderEntity hEntity = headerList
						.get(i);
				List<GetGstr6aStagingCdnaItemEntity> hLineItems = hEntity
						.getLineItems();
				if (checkIfRateCdnaIsDifferent(lineItems, hLineItems)) {
					headerEntity.get().setTaxableValue(headerEntity.get().getTaxableValue()
							.add(hEntity.getTaxableValue()));
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

	private static boolean checkIfRateCdnaIsDifferent(
			List<GetGstr6aStagingCdnaItemEntity> lineItems,
			List<GetGstr6aStagingCdnaItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr6aStagingCdnaItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr6aStagingCdnaItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
					.collect(Collectors.toList());
			lineItems.add(lineItem);
			if (CollectionUtils.isNotEmpty(itemEntities)) {
				return true;
			}
		}

		return false;
	}

	public static List<GetGstr6aStagingCdnHeaderEntity> filterInvoiceKeyAndMergeRateonCdnHeaders(
			List<GetGstr6aStagingCdnHeaderEntity> cdnraList) {
		List<GetGstr6aStagingCdnHeaderEntity> cdnaFilterList = Lists
				.newArrayList();
		if (!org.springframework.util.CollectionUtils.isEmpty(cdnraList)) {
			Map<String, List<GetGstr6aStagingCdnHeaderEntity>> invoiceKeyMapList = cdnraList
					.stream().collect(Collectors.groupingBy(
							GetGstr6aStagingCdnHeaderEntity::getInvKey));

			invoiceKeyMapList.keySet().forEach(invoiceKey -> {
				List<GetGstr6aStagingCdnHeaderEntity> headerList = invoiceKeyMapList
						.get(invoiceKey);
				GetGstr6aStagingCdnHeaderEntity hEntity = mergeRatesIntoCdnHeader(
						headerList);
				if (hEntity != null) {
					cdnaFilterList.add(hEntity);
				}
			});
		}

		return cdnaFilterList;
	}

	private static GetGstr6aStagingCdnHeaderEntity mergeRatesIntoCdnHeader(
			List<GetGstr6aStagingCdnHeaderEntity> headerList) {
		AtomicReference<GetGstr6aStagingCdnHeaderEntity> headerEntity = new AtomicReference<GetGstr6aStagingCdnHeaderEntity>();
		List<GetGstr6aStagingCdnItemEntity> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(headerList)) {
			headerEntity.set(headerList.stream().findFirst().get());
			lineItems.addAll(headerEntity.get().getLineItems());
			headerEntity.get().setLineItems(null);
			for (int i = 1; i < headerList.size(); i++) {
				GetGstr6aStagingCdnHeaderEntity hEntity = headerList
						.get(i);
				List<GetGstr6aStagingCdnItemEntity> hLineItems = hEntity
						.getLineItems();
				if (checkIfRateCdnIsDifferent(lineItems, hLineItems)) {
					headerEntity.get().setTaxableValue(headerEntity.get().getTaxableValue()
							.add(hEntity.getTaxableValue()));
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

	private static boolean checkIfRateCdnIsDifferent(
			List<GetGstr6aStagingCdnItemEntity> lineItems,
			List<GetGstr6aStagingCdnItemEntity> hLineItems) {
		if (CollectionUtils.isNotEmpty(hLineItems)) {
			GetGstr6aStagingCdnItemEntity lineItem = hLineItems
					.size() == 1 ? hLineItems.get(0)
							: hLineItems.stream().findFirst().get();
			BigDecimal taxRate = lineItem.getTaxRate();
			List<GetGstr6aStagingCdnItemEntity> itemEntities = lineItems
					.stream().filter(item -> !item.getTaxRate().equals(taxRate))
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
