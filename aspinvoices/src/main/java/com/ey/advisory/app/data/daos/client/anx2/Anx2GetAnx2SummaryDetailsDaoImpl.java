package com.ey.advisory.app.data.daos.client.anx2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.anx2.Anx2GetAnx2SummaryDetailsReqDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetAnx2SummaryDetailsResDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetAnx2SummaryItemsResDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetFinalSummaryDetailsResDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetGstinSummaryDetailsResData;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Anand3.M
 *
 */

@Component("Anx2GetAnx2SummaryDetailsDao")
public class Anx2GetAnx2SummaryDetailsDaoImpl implements Anx2GetAnx2SummaryDetailsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public Anx2GetFinalSummaryDetailsResDto loadSummaryDetails(Anx2GetAnx2SummaryDetailsReqDto criteria) {

		String taxperiod = criteria.getTaxPeriod();
		List<String> docType = criteria.getDocType();
		List<String> recordType = criteria.getRecordType();

		Map<String, List<String>> dataSecAttrs = criteria.getDataSecAttrs();

		String gstn = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstn = key;
					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& !dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}

		StringBuilder buildQuery = new StringBuilder();

		if (gstn != null && !gstn.isEmpty() && gstinList != null && !gstinList.isEmpty()) {
			buildQuery.append(" AND CGSTIN IN :gstinList");
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			buildQuery.append(" AND DERIVED_RET_PERIOD = :taxPeriod");
		}

		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND INV_TYPE IN :docType");
		}
		if (recordType != null && !recordType.isEmpty()) {
			buildQuery.append(" AND TABLE_SECTION IN :recordType");
		}

		String queryStr = createQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (gstn != null && !gstn.isEmpty() && gstinList != null && !gstinList.isEmpty()) {
			q.setParameter("gstinList", gstinList);
		}
		if (taxperiod != null && !taxperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil.convertTaxPeriodToInt(taxperiod);
			q.setParameter("taxPeriod", derivedRetPeriod);
		}

		if (docType != null && !docType.isEmpty()) {
			q.setParameter("docType", docType);
		}
		if (recordType != null && !recordType.isEmpty()) {
			q.setParameter("recordType", recordType);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		Anx2GetFinalSummaryDetailsResDto finalSummaryDetailsResDto = new Anx2GetFinalSummaryDetailsResDto();
		List<Anx2GetAnx2SummaryDetailsResDto> finalRetSummaryList = new LinkedList<Anx2GetAnx2SummaryDetailsResDto>();

		if (list.isEmpty() || list.size() == 0) {
			createEmptySummaryList(finalRetSummaryList);
			finalRetSummaryList.add(calculateAndAddTotalDataObject(new ArrayList()));
			finalSummaryDetailsResDto.setTable(finalRetSummaryList);
			finalSummaryDetailsResDto.setGSTIN(createGstinObject());
			return finalSummaryDetailsResDto;
		} else {
			List<Anx2GetAnx2SummaryDetailsResDto> retList = list.parallelStream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			Map<String, String> tableMap = createTableMap();
			Set<String> existingDataSet = retList.stream().map(dto -> dto.getTable()).collect(Collectors.toSet());
			Set<String> emptyAdditionalDataSet = new HashSet<String>();
			tableMap.keySet().forEach(key -> {
				if (!existingDataSet.contains(key)) {
					emptyAdditionalDataSet.add(key);
				}
			});
			enrichWithAdditionalData(retList, emptyAdditionalDataSet);
			enrichWithSubItemAdditionalData(retList);
			Map<String, List<Anx2GetAnx2SummaryDetailsResDto>> finalMap = convertToMap(retList);
			Map<String, Anx2GetAnx2SummaryDetailsResDto> dtoSets = new HashMap<String, Anx2GetAnx2SummaryDetailsResDto>();
			retList.forEach(dto -> {
				dtoSets.put(dto.getTable(), dto);
			});

			List<Anx2GetAnx2SummaryDetailsResDto> finalRetList = new ArrayList<Anx2GetAnx2SummaryDetailsResDto>();

			dtoSets.values().forEach(dto -> {
				List<Anx2GetAnx2SummaryDetailsResDto> items = finalMap.get(dto.getTable());
				List<Anx2GetAnx2SummaryItemsResDto> itemsList = new ArrayList<>();
				Anx2GetAnx2SummaryDetailsResDto finalDto = new Anx2GetAnx2SummaryDetailsResDto();

				int recordsCount = 0;
				BigDecimal invoiceValue = BigDecimal.ZERO;
				BigDecimal taxableValue = BigDecimal.ZERO;
				BigDecimal totalTax = BigDecimal.ZERO;
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cgst = BigDecimal.ZERO;
				BigDecimal sgst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;

				for (Anx2GetAnx2SummaryDetailsResDto mainDto : items) {
					Anx2GetAnx2SummaryItemsResDto itemsResDto = new Anx2GetAnx2SummaryItemsResDto();
					itemsResDto.setTable((mainDto.getDocType().equals("INV") || mainDto.getDocType().equals("I"))
							? "Invoice"
							: (mainDto.getDocType().equals("CR") || mainDto.getDocType().equals("C")) ? "Credit Note"
									: (mainDto.getDocType().equals("DR") || mainDto.getDocType().equals("D"))
											? "Debit Note"
											: mainDto.getDocType().equals("RNV") ? "Invoice-Amendment"
													: mainDto.getDocType().equals("RCR") ? "Credit Note-Amendment"
															: mainDto.getDocType().equals("RDR")
																	? "Debit Note-Amendment" : "");
					itemsResDto.setRecords(mainDto.getRecords());
					itemsResDto.setInvoiceValue(mainDto.getInvoiceValue());
					itemsResDto.setTaxableValue(mainDto.getTaxableValue());
					itemsResDto.setTotalTax(mainDto.getTotalTax());
					itemsResDto.setIgst(mainDto.getIgst());
					itemsResDto.setCgst(mainDto.getCgst());
					itemsResDto.setSgst(mainDto.getSgst());
					itemsResDto.setCess(mainDto.getCess());

					if (mainDto.getDocType().equals("CR") || mainDto.getDocType().equals("C") ) {
						recordsCount = recordsCount - mainDto.getRecords();
						invoiceValue = invoiceValue.subtract(mainDto.getInvoiceValue());
						taxableValue = taxableValue.subtract(mainDto.getTaxableValue());
						totalTax = totalTax.subtract(mainDto.getTotalTax());
						igst = igst.subtract(mainDto.getIgst());
						cgst = cgst.subtract(mainDto.getCgst());
						sgst = sgst.subtract(mainDto.getSgst());
						cess = cess.subtract(mainDto.getCess());
					} else if (mainDto.getDocType().equals("RCR")) {
						recordsCount = recordsCount - mainDto.getRecords();
						invoiceValue = invoiceValue.subtract(mainDto.getInvoiceValue());
						taxableValue = taxableValue.subtract(mainDto.getTaxableValue());
						totalTax = totalTax.subtract(mainDto.getTotalTax());
						igst = igst.subtract(mainDto.getIgst());
						cgst = cgst.subtract(mainDto.getCgst());
						sgst = sgst.subtract(mainDto.getSgst());
						cess = cess.subtract(mainDto.getCess());
					} else {
						recordsCount = recordsCount + mainDto.getRecords();
						invoiceValue = invoiceValue.add(mainDto.getInvoiceValue());
						taxableValue = taxableValue.add(mainDto.getTaxableValue());
						totalTax = totalTax.add(mainDto.getTotalTax());
						igst = igst.add(mainDto.getIgst());
						cgst = cgst.add(mainDto.getCgst());
						sgst = sgst.add(mainDto.getSgst());
						cess = cess.add(mainDto.getCess());
					}

					itemsList.add(itemsResDto);
				}

				finalDto.setTable(tableMap.get(dto.getTable()));
				finalDto.setRecords(recordsCount);
				finalDto.setInvoiceValue(invoiceValue);
				finalDto.setTaxableValue(taxableValue);
				finalDto.setTotalTax(totalTax);
				finalDto.setIgst(igst);
				finalDto.setCgst(cgst);
				finalDto.setSgst(sgst);
				finalDto.setCess(cess);
				finalDto.setItems(itemsList);
				finalRetList.add(finalDto);
			});

			List<Anx2GetAnx2SummaryDetailsResDto> totalList = new LinkedList<Anx2GetAnx2SummaryDetailsResDto>();
			totalList.addAll(finalRetList);
			totalList.add(calculateAndAddTotalDataObject(finalRetList));
			finalSummaryDetailsResDto.setTable(totalList);
			finalSummaryDetailsResDto.setGSTIN(createGstinObject());
			return finalSummaryDetailsResDto;
		}
	}

	private Anx2GetGstinSummaryDetailsResData createGstinObject() {
		Anx2GetGstinSummaryDetailsResData data = new Anx2GetGstinSummaryDetailsResData();
		data.setTaxableValue(BigDecimal.ZERO);
		data.setIgst(BigDecimal.ZERO);
		data.setCgst(BigDecimal.ZERO);
		data.setSgst(BigDecimal.ZERO);
		data.setCess(BigDecimal.ZERO);
		data.setIsdTaxableValue(BigDecimal.ZERO);
		data.setIsdIgst(BigDecimal.ZERO);
		data.setIsdCgst(BigDecimal.ZERO);
		data.setIsdSgst(BigDecimal.ZERO);
		data.setIsdCess(BigDecimal.ZERO);
		return data;
	}

	private Anx2GetAnx2SummaryDetailsResDto calculateAndAddTotalDataObject(
			List<Anx2GetAnx2SummaryDetailsResDto> finalRetList) {
		Anx2GetAnx2SummaryDetailsResDto itemsResDto = new Anx2GetAnx2SummaryDetailsResDto();
		int recordsCount = 0;
		BigDecimal invoiceValue = BigDecimal.ZERO;
		BigDecimal taxableValue = BigDecimal.ZERO;
		BigDecimal totalTax = BigDecimal.ZERO;
		BigDecimal igst = BigDecimal.ZERO;
		BigDecimal cgst = BigDecimal.ZERO;
		BigDecimal sgst = BigDecimal.ZERO;
		BigDecimal cess = BigDecimal.ZERO;

		if (!finalRetList.isEmpty() && finalRetList.size() > 0) {
			for (Anx2GetAnx2SummaryDetailsResDto mainDto : finalRetList) {
				recordsCount = recordsCount + mainDto.getRecords();
				invoiceValue = invoiceValue.add(mainDto.getInvoiceValue());
				taxableValue = taxableValue.add(mainDto.getTaxableValue());
				totalTax = totalTax.add(mainDto.getTotalTax());
				igst = igst.add(mainDto.getIgst());
				cgst = cgst.add(mainDto.getCgst());
				sgst = sgst.add(mainDto.getSgst());
				cess = cess.add(mainDto.getCess());
			}
		}
		itemsResDto.setTable("Total");
		itemsResDto.setRecords(recordsCount);
		itemsResDto.setInvoiceValue(invoiceValue);
		itemsResDto.setTaxableValue(taxableValue);
		itemsResDto.setTotalTax(totalTax);
		itemsResDto.setIgst(igst);
		itemsResDto.setCgst(cgst);
		itemsResDto.setSgst(sgst);
		itemsResDto.setCess(cess);

		return itemsResDto;
	}

	private void enrichWithSubItemAdditionalData(List<Anx2GetAnx2SummaryDetailsResDto> retList) {
		Map<String, List<String>> dataMap = new HashMap<String, List<String>>();
		retList.forEach(dto -> {
			List<String> existList = null;
			if (dataMap.containsKey(dto.getTable())) {
				existList = dataMap.get(dto.getTable());
				existList.add(dto.getDocType());
			} else {
				existList = new ArrayList<String>();
				existList.add(dto.getDocType());
			}
			dataMap.put(dto.getTable(), existList);
		});

		if (dataMap != null && dataMap.size() > 0) {
			dataMap.forEach((tabName, items) -> {
				checkDataItemsAndAddAdditionalItems(tabName, items, retList);
			});
		}

	}

	private void checkDataItemsAndAddAdditionalItems(String tabName, List<String> items,
			List<Anx2GetAnx2SummaryDetailsResDto> retList) {
		List<String> emptyAdditionalDataSet = new ArrayList<String>();

		if (tabName.equals("5-ISD") || tabName.equals("ISDC")) {
			Arrays.asList("INV", "CR", "RNV", "RCR").forEach(key -> {
				if (!items.contains(key)) {
					emptyAdditionalDataSet.add(key);
				}
			});
		} else {
			createTableItemsList().forEach(key -> {
				if (!items.contains(key)) {
					emptyAdditionalDataSet.add(key);
				}
			});
		}

		if (emptyAdditionalDataSet != null && emptyAdditionalDataSet.size() > 0) {
			emptyAdditionalDataSet.forEach(subItem -> {
				Anx2GetAnx2SummaryDetailsResDto dto = new Anx2GetAnx2SummaryDetailsResDto();
				dto.setTable(tabName);
				dto.setDocType(subItem);
				dto.setRecords(0);
				dto.setInvoiceValue(BigDecimal.ZERO);
				dto.setTaxableValue(BigDecimal.ZERO);
				dto.setTotalTax(BigDecimal.ZERO);
				dto.setIgst(BigDecimal.ZERO);
				dto.setCgst(BigDecimal.ZERO);
				dto.setSgst(BigDecimal.ZERO);
				dto.setCess(BigDecimal.ZERO);
				retList.add(dto);
			});
		}

	}

	private void enrichWithAdditionalData(List<Anx2GetAnx2SummaryDetailsResDto> retList,
			Set<String> emptyAdditionalDataSet) {
		if (emptyAdditionalDataSet != null && emptyAdditionalDataSet.size() > 0) {
			emptyAdditionalDataSet.forEach(item -> {

				if (item.equals("ISDC") || item.equals("5-ISD")) {
					Arrays.asList("INV", "CR", "RNV", "RCR").forEach(key -> {
						Anx2GetAnx2SummaryDetailsResDto dto = new Anx2GetAnx2SummaryDetailsResDto();
						dto.setTable(item);
						dto.setDocType(key);
						dto.setRecords(0);
						dto.setInvoiceValue(BigDecimal.ZERO);
						dto.setTaxableValue(BigDecimal.ZERO);
						dto.setTotalTax(BigDecimal.ZERO);
						dto.setIgst(BigDecimal.ZERO);
						dto.setCgst(BigDecimal.ZERO);
						dto.setSgst(BigDecimal.ZERO);
						dto.setCess(BigDecimal.ZERO);
						retList.add(dto);
					});
				} else {
					createTableItemsList().forEach(subItem -> {
						Anx2GetAnx2SummaryDetailsResDto dto = new Anx2GetAnx2SummaryDetailsResDto();
						dto.setTable(item);
						dto.setDocType(subItem);
						dto.setRecords(0);
						dto.setInvoiceValue(BigDecimal.ZERO);
						dto.setTaxableValue(BigDecimal.ZERO);
						dto.setTotalTax(BigDecimal.ZERO);
						dto.setIgst(BigDecimal.ZERO);
						dto.setCgst(BigDecimal.ZERO);
						dto.setSgst(BigDecimal.ZERO);
						dto.setCess(BigDecimal.ZERO);
						retList.add(dto);
					});
				}
			});
		}

	}

	private Map<String, String> createTableMap() {
		Map<String, String> tableMap = new HashMap<String, String>();
		tableMap.put("B2B", "3B-B2B");
		tableMap.put("SEZWP", "3E-SEZWP");
		tableMap.put("SEZWOP", "3F-SEZWOP");
		tableMap.put("DE", "3G-Deemed Exports");
		tableMap.put("ISDC", "5-ISD");
		return tableMap;
	}

	private List<String> createTableItemsList() {
		List<String> tableItems = Arrays.asList("INV", "DR", "CR", "RNV", "RDR", "RCR");
		return tableItems;
	}

	private void createEmptySummaryList(List<Anx2GetAnx2SummaryDetailsResDto> finalRetList) {

		Map<String, String> tableMap = createTableMap();
		List<String> itemsList = createTableItemsList();

		tableMap.values().forEach(tableValue -> {
			Anx2GetAnx2SummaryDetailsResDto dto = new Anx2GetAnx2SummaryDetailsResDto();
			dto.setTable(tableValue);
			dto.setRecords(0);
			dto.setInvoiceValue(BigDecimal.ZERO);
			dto.setTaxableValue(BigDecimal.ZERO);
			dto.setTotalTax(BigDecimal.ZERO);
			dto.setIgst(BigDecimal.ZERO);
			dto.setCgst(BigDecimal.ZERO);
			dto.setSgst(BigDecimal.ZERO);
			dto.setCess(BigDecimal.ZERO);

			if (tableValue.equals("5-ISD")) {
				addTheItemsBasedOnTableHeader(Arrays.asList("INV", "CR", "RNV", "RCR"), dto);
			} else {
				addTheItemsBasedOnTableHeader(itemsList, dto);
			}
			finalRetList.add(dto);
		});
	}

	private void addTheItemsBasedOnTableHeader(List<String> itemsList, Anx2GetAnx2SummaryDetailsResDto dto) {
		List<Anx2GetAnx2SummaryItemsResDto> tableItemsList = new ArrayList<>();
		itemsList.forEach(tableItemValue -> {
			Anx2GetAnx2SummaryItemsResDto itemDto = new Anx2GetAnx2SummaryItemsResDto();
			itemDto.setTable(tableItemValue.equals("INV") ? "Invoice"
					: tableItemValue.equals("CR") ? "Credit Note"
							: tableItemValue.equals("DR") ? "Debit Note"
									: tableItemValue.equals("RNV") ? "Invoice-Amendment"
											: tableItemValue.equals("RCR") ? "Credit Note-Amendment"
													: tableItemValue.equals("RDR") ? "Debit Note-Amendment" : "");
			itemDto.setRecords(0);
			itemDto.setInvoiceValue(BigDecimal.ZERO);
			itemDto.setTaxableValue(BigDecimal.ZERO);
			itemDto.setTotalTax(BigDecimal.ZERO);
			itemDto.setIgst(BigDecimal.ZERO);
			itemDto.setCgst(BigDecimal.ZERO);
			itemDto.setSgst(BigDecimal.ZERO);
			itemDto.setCess(BigDecimal.ZERO);
			tableItemsList.add(itemDto);
		});
		dto.setItems(tableItemsList);
	}

	private Map<String, List<Anx2GetAnx2SummaryDetailsResDto>> convertToMap(
			List<Anx2GetAnx2SummaryDetailsResDto> retList) {
		Map<String, List<Anx2GetAnx2SummaryDetailsResDto>> finalHashMap = new HashMap<String, List<Anx2GetAnx2SummaryDetailsResDto>>();
		for (Anx2GetAnx2SummaryDetailsResDto resDto : retList) {
			List<Anx2GetAnx2SummaryDetailsResDto> items = retList.stream()
					.filter(dto -> dto.getTable().equals(resDto.getTable()))
					.collect(Collectors.toCollection(ArrayList::new));
			finalHashMap.put(resDto.getTable(), items);
		}
		return finalHashMap;
	}

	private Anx2GetAnx2SummaryDetailsResDto convert(Object[] arr) {
		Anx2GetAnx2SummaryDetailsResDto obj = new Anx2GetAnx2SummaryDetailsResDto();
		BigInteger bb = GenUtil.getBigInteger(arr[0]);
		obj.setRecords(bb.intValue());
		BigDecimal bigDecimal = (BigDecimal) arr[1];
		if (bigDecimal != null) {
			BigDecimal inv = new BigDecimal(bigDecimal.longValue());
			obj.setInvoiceValue(inv);
		} else {
			obj.setInvoiceValue(BigDecimal.ZERO);
		}

		String invType = (String) arr[2];
		if (invType != null) {
			obj.setDocType(invType.toUpperCase());
		} else {
			obj.setDocType("");
		}
		BigDecimal bigDecimalT = (BigDecimal) arr[3];
		if (bigDecimalT != null) {
			BigDecimal tax = new BigDecimal(bigDecimalT.longValue());
			obj.setTaxableValue(tax);
		} else {
			obj.setTaxableValue(BigDecimal.ZERO);
		}

		BigDecimal bigDecimalTv = (BigDecimal) arr[4];
		if (bigDecimalTv != null) {
			BigDecimal taxv = new BigDecimal(bigDecimalTv.longValue());
			obj.setTotalTax(taxv);
		} else {
			obj.setTotalTax(BigDecimal.ZERO);
		}

		BigDecimal bigDecimalIg = (BigDecimal) arr[5];
		if (bigDecimalIg != null) {
			BigDecimal igst = new BigDecimal(bigDecimalIg.longValue());
			obj.setIgst(igst);
		} else {
			obj.setIgst(BigDecimal.ZERO);
		}

		BigDecimal bigDecimalCg = (BigDecimal) arr[6];
		if (bigDecimalCg != null) {
			BigDecimal cgst = new BigDecimal(bigDecimalCg.longValue());
			obj.setCgst(cgst);
		} else {
			obj.setCgst(BigDecimal.ZERO);
		}

		BigDecimal bigDecimalSg = (BigDecimal) arr[7];
		if (bigDecimalSg != null) {
			BigDecimal sgst = new BigDecimal(bigDecimalSg.longValue());
			obj.setSgst(sgst);
		} else {
			obj.setSgst(BigDecimal.ZERO);
		}

		BigDecimal bigDecimalCess = (BigDecimal) arr[8];
		if (bigDecimalCess != null) {
			BigDecimal cess = new BigDecimal(bigDecimalCess.longValue());
			obj.setCess(cess);
		} else {
			obj.setCess(BigDecimal.ZERO);
		}

		String table = (String) arr[9];
		if (table != null) {
			obj.setTable(table);
		} else {
			obj.setTable("");
		}

		return obj;

	}

	private String createQueryString(String buildQuery) {

		String queryStr = "SELECT * FROM " + "( SELECT COUNT(ID) AS COUNT, IFNULL(SUM( SUPPLIER_INV_VAL ),0) "
				+ " AS INVOICE_VALUE,INV_TYPE," + " IFNULL(SUM( TAXABLE_VALUE ),0) AS TAXABLE_VALUE, "
				+ " SUM ((IFNULL( IGST_AMT ,0))+(IFNULL( CGST_AMT ,0)) "
				+ " +(IFNULL( SGST_AMT ,0))+(IFNULL( CESS_AMT ,0)) ) AS TOTAL_TAX,"
				+ " IFNULL(SUM( IGST_AMT ),0) AS IGST_AMT,IFNULL(SUM( CGST_AMT ),0) AS CGST_AMT,"
				+ " IFNULL(SUM( SGST_AMT ),0) AS SGST_AMT,IFNULL(SUM( CESS_AMT ),0) AS CESS_AMT," + " TABLE_SECTION "
				+ "FROM  GETANX2_B2B_HEADER  " + "WHERE IS_DELETE = FALSE " + buildQuery.toString()
				+ " GROUP BY  INV_TYPE , TABLE_SECTION "

				+ "UNION ALL SELECT COUNT(ID) AS COUNT, IFNULL(SUM( SUPPLIER_INV_VAL ),0) "
				+ " AS INVOICE_VALUE,INV_TYPE," + " IFNULL(SUM( TAXABLE_VALUE ),0) AS TAXABLE_VALUE, "
				+ " SUM ((IFNULL( IGST_AMT ,0))+(IFNULL( CGST_AMT ,0)) "
				+ " +(IFNULL( SGST_AMT ,0))+(IFNULL( CESS_AMT ,0)) ) AS TOTAL_TAX,"
				+ " IFNULL(SUM( IGST_AMT ),0) AS IGST_AMT,IFNULL(SUM( CGST_AMT ),0) AS CGST_AMT,"
				+ " IFNULL(SUM( SGST_AMT ),0) AS SGST_AMT,IFNULL(SUM( CESS_AMT ),0) AS CESS_AMT," + " TABLE_SECTION "
				+ " FROM  GETANX2_DE_HEADER  " + "WHERE IS_DELETE = FALSE " + buildQuery.toString()
				+ " GROUP BY  INV_TYPE , TABLE_SECTION "

				+ "UNION ALL  SELECT COUNT(ID) AS COUNT, "
				+ " IFNULL(SUM( SUPPLIER_INV_VAL ),0) AS INVOICE_VALUE,INV_TYPE,"
				+ " IFNULL(SUM( TAXABLE_VALUE ),0) AS TAXABLE_VALUE, "
				+ " SUM ((IFNULL( IGST_AMT ,0))+(IFNULL( CGST_AMT ,0)) "
				+ " +(IFNULL( SGST_AMT ,0))+(IFNULL( CESS_AMT ,0)) ) AS TOTAL_TAX,"
				+ " IFNULL(SUM( IGST_AMT ),0) AS IGST_AMT,IFNULL(SUM( CGST_AMT ),0) AS CGST_AMT,"
				+ " IFNULL(SUM( SGST_AMT ),0) AS SGST_AMT,IFNULL(SUM( CESS_AMT ),0) AS CESS_AMT," + " TABLE_SECTION "
				+ " FROM  GETANX2_SEZWP_HEADER  " + "WHERE IS_DELETE = FALSE " + buildQuery.toString()
				+ " GROUP BY  INV_TYPE , TABLE_SECTION "

				+ "UNION ALL  SELECT COUNT(ID) AS COUNT, "
				+ " IFNULL(SUM( SUPPLIER_INV_VAL ),0) AS INVOICE_VALUE,INV_TYPE,"
				+ " IFNULL(SUM( TAXABLE_VALUE ),0) AS TAXABLE_VALUE, '0' AS TOTAL_TAX,"
				+ " '0' AS  IGST_AMT , '0' AS  CGST_AMT , '0' AS  SGST_AMT , '0' AS  CESS_AMT ," + " TABLE_SECTION "
				+ " FROM  GETANX2_SEZWOP_HEADER  " + "WHERE IS_DELETE = FALSE " + buildQuery.toString()
				+ " GROUP BY  INV_TYPE , TABLE_SECTION "

				+ "UNION ALL  SELECT COUNT(ID) AS COUNT, '0' AS INVOICE_VALUE,INV_TYPE,"
				+ " '0' AS TAXABLE_VALUE, SUM ((IFNULL( IGST_AMT ,0))"
				+ " +(IFNULL( CGST_AMT ,0)) +(IFNULL( SGST_AMT ,0))+(IFNULL( CESS_AMT ,0)) ) AS TOTAL_TAX,"
				+ " IFNULL(SUM( IGST_AMT ),0) AS IGST_AMT,IFNULL(SUM( CGST_AMT ),0) AS CGST_AMT,"
				+ " IFNULL(SUM( SGST_AMT ),0) AS SGST_AMT,IFNULL(SUM( CESS_AMT ),0) AS CESS_AMT," + " TABLE_SECTION "
				+ " FROM  GETANX2_ISDC_HEADER  " + "WHERE IS_DELETE = FALSE " + buildQuery.toString()
				+ " GROUP BY  INV_TYPE , TABLE_SECTION )";

		return queryStr;
	}

}