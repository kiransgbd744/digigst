package com.ey.advisory.app.services.docs;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.Gstr2XVerticalErrorEntity;
import com.ey.advisory.app.caches.UomCache;
import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnAsEnteredEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnProcessEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9AsEnteredHsnRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9HsnProcessedRepository;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.Lists;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr9HsnExcelConvertion")
public class Gstr9HsnExcelConvertion {

	@Autowired
	@Qualifier("Gstr9HsnExcelErrorConvertion")
	private Gstr9HsnExcelErrorConvertion gstr9ExcelErrorConvertion;

	@Autowired
	@Qualifier("Gstr9AsEnteredHsnRepository")
	private Gstr9AsEnteredHsnRepository gstr9AsEnteredTdsRepository;

	@Autowired
	@Qualifier("Gstr9HsnProcessedRepository")
	private Gstr9HsnProcessedRepository gstr9ProcessedRepository;

	@Autowired
	@Qualifier("DefaultUomCache")
	private UomCache uomCache;

	private String getValue(Object obj) {
		return obj != null && !obj.toString().trim().isEmpty()
				? String.valueOf(obj).trim() : null;
	}

	public List<Gstr9HsnAsEnteredEntity> convertHsn(List<Object[]> hsnList,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Gstr9HsnAsEnteredEntity> hsn = new ArrayList<>();

		Gstr9HsnAsEnteredEntity hsnExcel = null;
		for (Object[] obj : hsnList) {
			hsnExcel = new Gstr9HsnAsEnteredEntity();

			String gstin = (obj[0] != null
					&& !obj[0].toString().trim().isEmpty())
							? String.valueOf(obj[0]).trim() : null;

			String fy = getValue(obj[1]);
			LocalDate date = DateFormatForStructuralValidatons
					.parseObjToDate(fy);
			if (date != null) {
				fy = date.toString();
			}

			String tableNumber = (obj[2] != null
					&& !obj[2].toString().trim().isEmpty())
							? String.valueOf(obj[2]).trim() : null;

			if (tableNumber != null && !tableNumber.isEmpty()) {
				if (tableNumber.length() > 5) {
					tableNumber = tableNumber.substring(0, 5);
				}
			}

			String hsnL = (obj[3] != null
					&& !obj[3].toString().trim().isEmpty())
							? String.valueOf(obj[3]).trim() : null;

			String desc = (obj[4] != null
					&& !obj[4].toString().trim().isEmpty())
							? String.valueOf(obj[4]).trim() : null;
			if (desc != null && !desc.isEmpty()) {
				if (desc.length() > 1000) {
					desc = desc.substring(0, 1000);
				}
			}

			String rate = (obj[5] != null
					&& !obj[5].toString().trim().isEmpty())
							? String.valueOf(obj[5]).trim() : null;

			String uqc = (obj[6] != null && !obj[6].toString().trim().isEmpty())
					? String.valueOf(obj[6]).trim() : null;

			String total = getValue(obj[7]);

			String taxValue = getValue(obj[8]);

			String concessional = (obj[9] != null
					&& !obj[9].toString().trim().isEmpty())
							? String.valueOf(obj[9]).trim() : null;

			if (concessional != null && !concessional.isEmpty()) {
				if (concessional.length() > 5) {
					concessional = concessional.substring(0, 5);
				}
			}

			String igst = getValue(obj[10]);

			String cgst = getValue(obj[11]);

			String sgst = getValue(obj[12]);

			String cess = getValue(obj[13]);

			String processKey = getFileProcessedKey(obj);
			// prod.setEntityId(entityId);
			if (updateFileStatus != null) {
				hsnExcel.setFileId(updateFileStatus.getId());

				// distrbtnExcel.setFileName(updateFileStatus.getFileName());
			}

			hsnExcel.setGstin(gstin);
			hsnExcel.setFy(fy);
			hsnExcel.setTableNumber(tableNumber);
			hsnExcel.setHsn(hsnL);
			hsnExcel.setDesc(desc);
			hsnExcel.setRateOfTax(rate);
			hsnExcel.setUqc(uqc);
			hsnExcel.setTotalQnt(total);
			hsnExcel.setTaxableVal(taxValue);
			hsnExcel.setConRateFlag(concessional);
			if (concessional == null || concessional.isEmpty()) {
				hsnExcel.setConRateFlag("N");
			}
			hsnExcel.setIgst(igst);
			hsnExcel.setCgst(cgst);
			hsnExcel.setSgst(sgst);
			hsnExcel.setCess(cess);

			hsnExcel.setGst9DocKey(processKey);
			hsnExcel.setError(false);

			hsnExcel.setCreatedBy(APIConstants.SYSTEM);

			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			hsnExcel.setCreatedOn(convertNow);

			hsn.add(hsnExcel);

		}
		return hsn;
	}

	public String getFileProcessedKey(Object[] obj) {

		String gstin = (obj[0] != null && !obj[0].toString().trim().isEmpty())
				? String.valueOf(obj[0]).trim() : null;

		String fy = (obj[1] != null && !obj[1].toString().trim().isEmpty())
				? String.valueOf(obj[1]).trim() : null;

		String tableNumber = (obj[2] != null
				&& !obj[2].toString().trim().isEmpty())
						? String.valueOf(obj[2]).trim() : null;

		String hsnL = (obj[3] != null && !obj[3].toString().trim().isEmpty())
				? String.valueOf(obj[3]).trim() : null;

		String rate = (obj[5] != null && !obj[5].toString().trim().isEmpty())
				? String.valueOf(obj[5]).trim() : null;

		String uqc = (obj[6] != null && !obj[6].toString().trim().isEmpty())
				? String.valueOf(obj[6]).trim() : null;

		/*
		 * if (uqc != null && !uqc.isEmpty()) { uomCache =
		 * StaticContextHolder.getBean("DefaultUomCache", UomCache.class); int m
		 * = uomCache.finduom(trimAndConvToUpperCase(uqc)); int n =
		 * uomCache.finduomDesc(trimAndConvToUpperCase(uqc)); int o =
		 * uomCache.finduomMergeDesc(trimAndConvToUpperCase(uqc));
		 * 
		 * if (m == 1 || n == 1 || o == 1) { if (n == 1) { uqc =
		 * uomCache.uQcDescAndCodemap() .get(trimAndConvToUpperCase(uqc)); } if
		 * (o == 1) {
		 * 
		 * uqc = uomCache.uQcDesc().get(trimAndConvToUpperCase(uqc)); } } else {
		 * 
		 * uqc = GSTConstants.OTH; } }
		 */

		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(gstin).add(fy)
				.add(tableNumber).add(hsnL).add(rate).add(uqc).toString()
				.trim();
	}

	public List<Gstr2XVerticalErrorEntity> convertErrorsIntoDocErrorEntity(
			List<Object[]> struErrRecords,
			Map<String, List<ProcessingResult>> structuralErrors,
			Gstr1FileStatusEntity updateFileStatus, Map<Long, String> hsnMap) {
		List<Gstr2XVerticalErrorEntity> docErrorEntities = Lists.newArrayList();
		List<Long> asEnterIds = Lists.newArrayList();

		Map<String, List<Object[]>> filteredMap = new HashMap<>();
		struErrRecords.forEach(obj -> {
			String prodKey = getFileProcessedKey(obj);

			if (filteredMap.containsKey(prodKey)) {
				List<Object[]> objArray = filteredMap.get(prodKey);
				objArray.add(obj);
				filteredMap.put(prodKey, objArray);
			} else {
				List<Object[]> objArray = new ArrayList<>();
				objArray.add(obj);
				filteredMap.put(prodKey, objArray);
			}
		});

		int i = 0;
		for (String productKey : filteredMap.keySet()) {
			asEnterIds.addAll(filterAndGetEnterIdsByProdKeyWithEnteredId(
					productKey, hsnMap));
			List<Object[]> objArray = filteredMap.get(productKey);
			List<Gstr2XVerticalErrorEntity> errorList = gstr9ExcelErrorConvertion
					.appendErroRecordsData(objArray, structuralErrors,
							updateFileStatus, hsnMap, asEnterIds, i++);
			docErrorEntities.addAll(errorList);
		}

		gstr9AsEnteredTdsRepository.updateAsEnteredbyErrorAndInfo(asEnterIds);
		return docErrorEntities;
	}

	public List<Long> filterAndGetEnterIdsByProdKeyWithEnteredId(String prodKey,
			Map<Long, String> hsnMap) {
		return hsnMap.entrySet().stream()
				.filter(e -> e.getValue().equals(prodKey))
				.map(Map.Entry::getKey).collect(Collectors.toList());
	}

	public List<Gstr9HsnProcessEntity> convertRecordsIntoProcessedRecords(
			List<Object[]> businessProRecords,
			Gstr1FileStatusEntity updateFileStatus, Map<Long, String> tdsMap,
			List<Long> asEnterIdsList) {
		List<Gstr9HsnProcessEntity> processEntities = Lists.newArrayList();
		List<Long> asEnterIds = Lists.newArrayList();
		int asEnterId = 0;
		for (Object obj[] : businessProRecords) {
			Gstr9HsnProcessEntity processEntity = new Gstr9HsnProcessEntity();

			String gstin = (obj[0] != null
					&& !obj[0].toString().trim().isEmpty())
							? String.valueOf(obj[0]).trim() : null;

			String fy = (obj[1] != null && !obj[1].toString().trim().isEmpty())
					? String.valueOf(obj[1]).trim() : null;

			String retPeriod = "03" + fy.trim().substring(5);
			processEntity.setRetPeriod(retPeriod);
			processEntity.setSource("E");

			String tableNumber = (obj[2] != null
					&& !obj[2].toString().trim().isEmpty())
							? String.valueOf(obj[2]).trim() : null;

			if (tableNumber != null && !tableNumber.isEmpty()) {
				if (tableNumber.length() > 5) {
					tableNumber = tableNumber.substring(0, 5);
				}
			}

			String hsnL = (obj[3] != null
					&& !obj[3].toString().trim().isEmpty())
							? String.valueOf(obj[3]).trim() : null;

			String desc = (obj[4] != null
					&& !obj[4].toString().trim().isEmpty())
							? String.valueOf(obj[4]).trim() : null;
			if (desc != null && !desc.isEmpty()) {
				if (desc.length() > 1000) {
					desc = desc.substring(0, 1000);
				}
			}

			processEntity.setRateOfTax(getAppropriateValueFromObject(obj[5]));

			String uqc = (obj[6] != null && !obj[6].toString().trim().isEmpty())
					? String.valueOf(obj[6]).trim() : null;

			if (uqc != null && !uqc.isEmpty()) {

				uomCache = StaticContextHolder.getBean("DefaultUomCache",
						UomCache.class);
				int m = uomCache.finduom(trimAndConvToUpperCase(uqc));
				int n = uomCache.finduomDesc(trimAndConvToUpperCase(uqc));
				int o = uomCache.finduomMergeDesc(trimAndConvToUpperCase(uqc));

				if (m == 1 || n == 1 || o == 1) {
					if (n == 1) {
						uqc = uomCache.uQcDescAndCodemap()
								.get(trimAndConvToUpperCase(uqc));
					}
					if (o == 1) {

						uqc = uomCache.uQcDesc()
								.get(trimAndConvToUpperCase(uqc));
					}
				} else {
					uqc = GSTConstants.OTH;
				}
			}

			BigDecimal total = BigDecimal.ZERO;

			if (obj[7] != null && !obj[7].toString().trim().isEmpty()) {
				total = NumberFomatUtil
						.getBigDecimal((String.valueOf(obj[7])).trim());
				processEntity.setTotalQnt(
						total.setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {
				processEntity.setTotalQnt(BigDecimal.ZERO);

			}

			BigDecimal taxVal = BigDecimal.ZERO;

			if (obj[8] != null && !obj[8].toString().trim().isEmpty()) {
				taxVal = NumberFomatUtil
						.getBigDecimal((String.valueOf(obj[8])).trim());
				processEntity.setTaxableVal(
						taxVal.setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {
				processEntity.setTaxableVal(BigDecimal.ZERO);

			}

			String concessional = (obj[9] != null
					&& !obj[9].toString().trim().isEmpty())
							? String.valueOf(obj[9]).trim() : null;

			if (concessional != null && !concessional.isEmpty()) {
				if (concessional.length() > 5) {
					concessional = concessional.substring(0, 5);
				}
			}

			BigDecimal igst = BigDecimal.ZERO;

			if (obj[10] != null && !obj[10].toString().trim().isEmpty()) {
				igst = NumberFomatUtil
						.getBigDecimal((String.valueOf(obj[10])).trim());
				processEntity
						.setIgst(igst.setScale(2, BigDecimal.ROUND_HALF_UP));

			} else {

				processEntity.setIgst(BigDecimal.ZERO);

			}

			BigDecimal cgst = BigDecimal.ZERO;

			if (obj[11] != null && !obj[11].toString().trim().isEmpty()) {
				cgst = NumberFomatUtil
						.getBigDecimal((String.valueOf(obj[11])).trim());
				processEntity
						.setCgst(cgst.setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {

				processEntity.setCgst(BigDecimal.ZERO);

			}

			BigDecimal sgst = BigDecimal.ZERO;

			if (obj[12] != null && !obj[12].toString().trim().isEmpty()) {
				sgst = NumberFomatUtil
						.getBigDecimal((String.valueOf(obj[12])).trim());
				processEntity
						.setSgst(sgst.setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {

				processEntity.setSgst(BigDecimal.ZERO);

			}

			BigDecimal cess = BigDecimal.ZERO;
			if (obj[13] != null && !obj[13].toString().trim().isEmpty()) {
				cess = NumberFomatUtil
						.getBigDecimal((String.valueOf(obj[13])).trim());
				processEntity
						.setCess(cess.setScale(2, BigDecimal.ROUND_HALF_UP));
			} else {

				processEntity.setCess(BigDecimal.ZERO);

			}

			String processKey = getFileProcessedKey(obj);

			Long asEntererdId = asEnterIdsList.get(asEnterId);

			processEntity.setAsEnterId(asEntererdId);

			if (updateFileStatus != null) {
				processEntity.setFileId(updateFileStatus.getId());

			}

			processEntity.setGstin(gstin);
			processEntity.setFy(fy);
			processEntity.setTableNumber(tableNumber);
			processEntity.setHsn(hsnL);
			processEntity.setDesc(desc);
			processEntity.setUqc(uqc);
			processEntity.setConRateFlag(concessional);
			if (concessional == null || concessional.isEmpty()) {
				processEntity.setConRateFlag("N");
			}

			processEntity.setGst9HsnDocKey(processKey);

			processEntity.setCreatedBy(APIConstants.SYSTEM);
			// processEntity.setGstnError(false);

			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			processEntity.setCreatedOn(convertNow);

			processEntities.add(processEntity);
			asEnterIds.add(asEntererdId);
			asEnterId++;
		}
		gstr9AsEnteredTdsRepository.updateAsEnteredbyProcessAndInfo(asEnterIds);
		return processEntities;
	}

	private BigDecimal getAppropriateValueFromObject(Object obj) {
		if (obj != null && !obj.toString().trim().isEmpty()) {
			String str = String.valueOf(obj);
			if (str.contains(".")) {
				Double value = Double.parseDouble(str);
				return BigDecimal.valueOf(value);
			} else if (str.contains("-") || str.contains("_")) {
				return BigDecimal.ZERO;
			} else {
				Integer value = Integer.parseInt(str);
				return new BigDecimal(value);
			}
		}
		return null;
	}

	public static void main(String[] args) {
		BigDecimal total = null;

		if (total == null) {
			System.out.println(new BigDecimal("0.0"));
		}

		// total = NumberFomatUtil.getBigDecimal(1234567890397.8699);

		// System.out.println(total.setScale(2, BigDecimal.ROUND_HALF_EVEN));
	}

}
