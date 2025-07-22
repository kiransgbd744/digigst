package com.ey.advisory.app.services.docs;

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
import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
import com.ey.advisory.app.data.entities.client.Gstr7DocErrorEntity;
import com.ey.advisory.app.data.entities.client.Gstr7ProcessedTdsEntity;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7AsEnteredTdsRepository;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7ProcessedRepository;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.collect.Lists;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr7ExcelConvertion")
public class Gstr7ExcelConvertion {

	@Autowired
	@Qualifier("Gstr7ExcelErrorConvertion")
	private Gstr7ExcelErrorConvertion gstr7ExcelErrorConvertion;

	@Autowired
	@Qualifier("Gstr7AsEnteredTdsRepository")
	private Gstr7AsEnteredTdsRepository gstr7AsEnteredTdsRepository;

	@Autowired
	@Qualifier("Gstr7ProcessedRepository")
	private Gstr7ProcessedRepository gstr7ProcessedRepository;

	public List<Gstr7AsEnteredTdsEntity> convertTds(List<Object[]> tdsList,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Gstr7AsEnteredTdsEntity> tds = new ArrayList<>();

		Gstr7AsEnteredTdsEntity tdsExcel = null;
		for (Object[] obj : tdsList) {
			tdsExcel = new Gstr7AsEnteredTdsEntity();

			String returnPeriod = (obj[0] != null
					&& !obj[0].toString().trim().isEmpty())
							? String.valueOf(obj[0]) : null;

			String derivedRePeroid = null;
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int month = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((month < 12 && month > 01)
							&& (year < 9999 && year > 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(returnPeriod);
						derivedRePeroid = String.valueOf(derivedRetPeriod);
					}
				}
			}

			String actType = (obj[1] != null
					&& !obj[1].toString().trim().isEmpty())
							? String.valueOf(obj[1]).trim() : null;

			String tdsGstin = (obj[2] != null
					&& !obj[2].toString().trim().isEmpty())
							? String.valueOf(obj[2]).trim() : null;

			String orgTdsGstin = (obj[3] != null
					&& !obj[3].toString().trim().isEmpty())
							? String.valueOf(obj[3]).trim() : null;

			String origRetPeriod = (obj[4] != null
					&& !obj[4].toString().trim().isEmpty())
							? String.valueOf(obj[4]).trim() : null;

			String orgGrossAmt = (obj[5] != null
					&& !obj[5].toString().trim().isEmpty())
							? String.valueOf(obj[5]).trim() : null;

			String tdsDedGstin = (obj[6] != null
					&& !obj[6].toString().trim().isEmpty())
							? String.valueOf(obj[6]).trim() : null;

			String grossAmt = (obj[7] != null
					&& !obj[7].toString().trim().isEmpty())
							? String.valueOf(obj[7]).trim() : null;

			String tdsIgst = (obj[8] != null
					&& !obj[8].toString().trim().isEmpty())
							? String.valueOf(obj[8]).trim() : null;

			String tdsCgst = (obj[9] != null
					&& !obj[9].toString().trim().isEmpty())
							? String.valueOf(obj[9]).trim() : null;

			String tdsSgst = (obj[10] != null
					&& !obj[10].toString().trim().isEmpty())
							? String.valueOf(obj[10]).trim() : null;

			String conNum = (obj[11] != null
					&& !obj[11].toString().trim().isEmpty())
							? String.valueOf(obj[11]).trim() : null;
			if (conNum != null && !conNum.isEmpty()) {
				if (conNum.length() > 20) {
					conNum = conNum.substring(0, 20);
				}
			}

			String conDate = (obj[12] != null
					&& !obj[12].toString().trim().isEmpty())
							? String.valueOf(obj[12]).trim() : null;

			LocalDate localConDate = null;
			if (conDate != null) {
				localConDate = EYDateUtil.toUTCDateTimeFromLocal(
						DateUtil.parseObjToDate(conDate));
			}

			String conValue = (obj[13] != null
					&& !obj[13].toString().trim().isEmpty())
							? String.valueOf(obj[13]).trim() : null;

			String payAdvNum = (obj[14] != null
					&& !obj[14].toString().trim().isEmpty())
							? String.valueOf(obj[14]).trim() : null;
			if (payAdvNum != null && !payAdvNum.isEmpty()) {
				if (payAdvNum.length() > 20) {
					payAdvNum = payAdvNum.substring(0, 20);
				}
			}

			String payAdvDate = (obj[15] != null
					&& !obj[15].toString().trim().isEmpty())
							? String.valueOf(obj[15]).trim() : null;

			LocalDate localPayDate = null;
			if (payAdvDate != null) {
				localPayDate = EYDateUtil.toUTCDateTimeFromLocal(
						DateUtil.parseObjToDate(payAdvDate));
			}

			String docNum = (obj[16] != null
					&& !obj[16].toString().trim().isEmpty())
							? String.valueOf(obj[16]).trim() : null;

			String docDate = (obj[17] != null
					&& !obj[17].toString().trim().isEmpty())
							? String.valueOf(obj[17]).trim() : null;

			LocalDate localDocDate = null;
			if (docDate != null) {
				localDocDate = EYDateUtil.toUTCDateTimeFromLocal(
						DateUtil.parseObjToDate(docDate));
			}

			String invValue = (obj[18] != null
					&& !obj[18].toString().trim().isEmpty())
							? String.valueOf(obj[18]).trim() : null;

			String plantCode = (obj[19] != null
					&& !obj[19].toString().trim().isEmpty())
							? String.valueOf(obj[19]).trim() : null;
			if (plantCode != null && plantCode.length() > 100) {
				plantCode = plantCode.substring(0, 100);
			}

			String division = (obj[20] != null
					&& !obj[20].toString().trim().isEmpty())
							? String.valueOf(obj[20]).trim() : null;
			if (division != null && division.length() > 100) {
				division = division.substring(0, 100);
			}

			String purOrg = (obj[21] != null
					&& !obj[21].toString().trim().isEmpty())
							? String.valueOf(obj[21]).trim() : null;
			if (purOrg != null && purOrg.length() > 100) {
				purOrg = purOrg.substring(0, 100);
			}

			String profitCentre1 = (obj[22] != null
					&& !obj[22].toString().trim().isEmpty())
							? String.valueOf(obj[22]).trim() : null;
			if (profitCentre1 != null && profitCentre1.length() > 100) {
				profitCentre1 = profitCentre1.substring(0, 100);
			}

			String profitCentre2 = (obj[23] != null
					&& !obj[23].toString().trim().isEmpty())
							? String.valueOf(obj[23]).trim() : null;
			if (profitCentre2 != null && profitCentre2.length() > 100) {
				profitCentre2 = profitCentre2.substring(0, 100);
			}
			String usrDef1 = (obj[24] != null
					&& !obj[24].toString().trim().isEmpty())
							? String.valueOf(obj[24]).trim() : null;
			if (usrDef1 != null && usrDef1.length() > 500) {
				usrDef1 = usrDef1.substring(0, 500);
			}

			String usrDef2 = (obj[25] != null
					&& !obj[25].toString().trim().isEmpty())
							? String.valueOf(obj[25]).trim() : null;
			if (usrDef2 != null && usrDef2.length() > 500) {
				usrDef2 = usrDef2.substring(0, 500);
			}
			String usrDef3 = (obj[26] != null
					&& !obj[26].toString().trim().isEmpty())
							? String.valueOf(obj[26]).trim() : null;
			if (usrDef3 != null && usrDef3.length() > 500) {
				usrDef3 = usrDef3.substring(0, 500);
			}

			String processKey = getFileProcessedKey(obj);
			// prod.setEntityId(entityId);
			if (updateFileStatus != null) {
				tdsExcel.setFileId(updateFileStatus.getId());

				// distrbtnExcel.setFileName(updateFileStatus.getFileName());
			}

			if (updateFileStatus != null) {
				tdsExcel.setCreatedBy(updateFileStatus.getUpdatedBy());
				if (updateFileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
					tdsExcel.setDataOriginType("E");
				} else if (updateFileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.ERP)) {
					tdsExcel.setDataOriginType("A");
				}

			}

			tdsExcel.setReturnPeriod(returnPeriod);
			tdsExcel.setActType(actType);
			tdsExcel.setTdsGstin((tdsGstin));
			tdsExcel.setOrgTdsGstin(orgTdsGstin);
			tdsExcel.setOrgRetPeriod(origRetPeriod);
			tdsExcel.setOrgGrossAmt(orgGrossAmt);
			tdsExcel.setNewGstin(tdsDedGstin);
			tdsExcel.setNewGrossAmt(grossAmt);
			tdsExcel.setIgstAmt(tdsIgst);
			tdsExcel.setCgstAmt(tdsCgst);
			tdsExcel.setSgstAmt(tdsSgst);
			tdsExcel.setConNumber(conNum);
			tdsExcel.setConDate(localConDate);
			tdsExcel.setConValue(conValue);
			tdsExcel.setPayNum(payAdvNum);
			tdsExcel.setPayDate(localPayDate);
			tdsExcel.setDocNum(docNum);
			tdsExcel.setDocDate(localDocDate);
			tdsExcel.setInvValue(invValue);
			tdsExcel.setPlantCode(plantCode);
			tdsExcel.setDivision(division);
			tdsExcel.setPurOrg(purOrg);
			tdsExcel.setProCen1(profitCentre1);
			tdsExcel.setProCen2(profitCentre2);
			tdsExcel.setUsrDefField1(usrDef1);
			tdsExcel.setUsrDefField2(usrDef2);
			tdsExcel.setUsrDefField3(usrDef3);

			tdsExcel.setDerReturnPeriod(derivedRePeroid);
			tdsExcel.setTdsKey(processKey);
			tdsExcel.setError(false);
			tdsExcel.setInformation(false);

			tdsExcel.setCreatedBy(APIConstants.SYSTEM);

			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			tdsExcel.setCreatedOn(convertNow);

			tds.add(tdsExcel);

		}
		return tds;
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	public static  BigDecimal getAppropriateValueFromObject(Object obj) {
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

	public String getFileProcessedKey(Object[] obj) {

		String tdsGstin = (obj[2] != null
				&& !obj[2].toString().trim().isEmpty())
						? String.valueOf(obj[2]).trim() : null;

		String returnPeriod = (obj[0] != null
				&& !obj[0].toString().trim().isEmpty())
						? String.valueOf(obj[0]).trim() : null;

		String orgTdsGstin = (obj[3] != null
				&& !obj[3].toString().trim().isEmpty())
						? String.valueOf(obj[3]).trim() : null;

		String origRetPeriod = (obj[4] != null
				&& !obj[4].toString().trim().isEmpty())
						? String.valueOf(obj[4]).trim() : null;

		String tdsDedGstin = (obj[6] != null
				&& !obj[6].toString().trim().isEmpty())
						? String.valueOf(obj[6]).trim() : null;

		String conNum = (obj[11] != null
				&& !obj[11].toString().trim().isEmpty())
						? String.valueOf(obj[11]).trim() : null;

		String conDate = (obj[12] != null
				&& !obj[12].toString().trim().isEmpty())
						? String.valueOf(obj[12]).trim() : null;

		String payAdvNum = (obj[14] != null
				&& !obj[14].toString().trim().isEmpty())
						? String.valueOf(obj[14]).trim() : null;

		String payAdvDate = (obj[15] != null
				&& !obj[15].toString().trim().isEmpty())
						? String.valueOf(obj[15]).trim() : null;

		String docNum = (obj[16] != null
				&& !obj[16].toString().trim().isEmpty())
						? String.valueOf(obj[16]).trim() : null;

		String docDate = (obj[17] != null
				&& !obj[17].toString().trim().isEmpty())
						? String.valueOf(obj[17]).trim() : null;

		/*String profitCentre1 = (obj[22] != null
				&& !obj[22].toString().trim().isEmpty())
						? String.valueOf(obj[22]).trim() : null;

		String profitCentre2 = (obj[23] != null
				&& !obj[23].toString().trim().isEmpty())
						? String.valueOf(obj[23]).trim() : null;

		String usrDef1 = (obj[24] != null
				&& !obj[24].toString().trim().isEmpty())
						? String.valueOf(obj[24]).trim() : null;

		String usrDef2 = (obj[25] != null
				&& !obj[25].toString().trim().isEmpty())
						? String.valueOf(obj[25]).trim() : null;

		String usrDef3 = (obj[26] != null
				&& !obj[26].toString().trim().isEmpty())
						? String.valueOf(obj[26]).trim() : null;
						*/

						
		if (conDate != null && conDate.contains("T")) {
			String conArray[] = conDate.split("T");
			conDate = conArray[0];
		}

		if (payAdvDate != null && payAdvDate.contains("T")) {
			String conArray[] = payAdvDate.split("T");
			payAdvDate = conArray[0];
		}

		if (docDate != null && docDate.contains("T")) {
			String conArray[] = docDate.split("T");
			docDate = conArray[0];
		}

		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(tdsGstin)
				.add(returnPeriod).add(orgTdsGstin).add(origRetPeriod)
				.add(tdsDedGstin).add(conNum).add(conDate).add(payAdvNum)
				.add(payAdvDate).add(docNum).add(docDate)/*.add(profitCentre1)
				.add(profitCentre2).add(usrDef1).add(usrDef2).add(usrDef3)*/
				.toString().trim();
	}

	public List<Gstr7DocErrorEntity> convertErrorsIntoDocErrorEntity(
			List<Object[]> struErrRecords,
			Map<String, List<ProcessingResult>> structuralErrors,
			Gstr1FileStatusEntity updateFileStatus, Map<Long, String> tdsMap) {
		List<Gstr7DocErrorEntity> docErrorEntities = Lists.newArrayList();
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
					productKey, tdsMap));
			List<Object[]> objArray = filteredMap.get(productKey);
			List<Gstr7DocErrorEntity> errorList = gstr7ExcelErrorConvertion
					.appendErroRecordsData(objArray, structuralErrors,
							updateFileStatus, tdsMap, asEnterIds, i++);
			docErrorEntities.addAll(errorList);
		}

		gstr7AsEnteredTdsRepository.updateAsEnteredbyErrorAndInfo(asEnterIds);
		return docErrorEntities;
	}

	public List<Long> filterAndGetEnterIdsByProdKeyWithEnteredId(String prodKey,
			Map<Long, String> tdsMap) {
		return tdsMap.entrySet().stream()
				.filter(e -> e.getValue().equals(prodKey))
				.map(Map.Entry::getKey).collect(Collectors.toList());
	}

	public List<Gstr7ProcessedTdsEntity> convertRecordsIntoProcessedRecords(
			List<Object[]> businessProRecords,
			Gstr1FileStatusEntity updateFileStatus, Map<Long, String> tdsMap,
			List<Long> asEnterIdsList) {
		List<Gstr7ProcessedTdsEntity> processEntities = Lists.newArrayList();
		List<Long> asEnterIds = Lists.newArrayList();
		int asEnterId = 0;
		for (Object obj[] : businessProRecords) {
			Gstr7ProcessedTdsEntity processEntity = new Gstr7ProcessedTdsEntity();

			String returnPeriod = (obj[0] != null
					&& !obj[0].toString().trim().isEmpty())
							? String.valueOf(obj[0]).trim() : null;

			String derivedRePeroid = null;
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int month = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((month < 12 && month > 01)
							&& (year < 9999 && year > 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(returnPeriod);
						derivedRePeroid = String.valueOf(derivedRetPeriod);
					}
				}
			}

			String actType = (obj[1] != null
					&& !obj[1].toString().trim().isEmpty())
							? String.valueOf(obj[1]).trim() : null;

			String tdsGstin = (obj[2] != null
					&& !obj[2].toString().trim().isEmpty())
							? String.valueOf(obj[2]).trim() : null;

			String orgTdsGstin = (obj[3] != null
					&& !obj[3].toString().trim().isEmpty())
							? String.valueOf(obj[3]).trim() : null;

			String origRetPeriod = (obj[4] != null
					&& !obj[4].toString().trim().isEmpty())
							? String.valueOf(obj[4]).trim() : null;

			processEntity.setOrgGrossAmt(getAppropriateValueFromObject(obj[5]));

			String tdsDedGstin = (obj[6] != null
					&& !obj[6].toString().trim().isEmpty())
							? String.valueOf(obj[6]).trim() : null;

			processEntity.setNewGrossAmt(getAppropriateValueFromObject(obj[7]));

			processEntity.setIgstAmt(getAppropriateValueFromObject(obj[8]));

			processEntity.setCgstAmt(getAppropriateValueFromObject(obj[9]));

			processEntity.setSgstAmt(getAppropriateValueFromObject(obj[10]));

			String conNum = String
					.valueOf(CommonUtility.exponentialAndZeroCheck(obj[11]));
			if (conNum != null && !conNum.isEmpty()) {
				if (conNum.length() > 20) {
					conNum = conNum.substring(0, 20);
				}
			}

			String conDate = (obj[12] != null
					&& !obj[12].toString().trim().isEmpty())
							? String.valueOf(obj[12]).trim() : null;

			LocalDate localConDate = null;
			if (conDate != null) {
				localConDate = EYDateUtil.toUTCDateTimeFromLocal(
						DateUtil.parseObjToDate(conDate));
			}

			processEntity.setConValue(getAppropriateValueFromObject(obj[13]));

			String payAdvNum = (obj[14] != null
					&& !obj[14].toString().trim().isEmpty())
							? String.valueOf(obj[14]).trim() : null;
			if (payAdvNum != null && !payAdvNum.isEmpty()) {
				if (payAdvNum.length() > 20) {
					payAdvNum = payAdvNum.substring(0, 20);
				}
			}

			String payAdvDate = (obj[15] != null
					&& !obj[15].toString().trim().isEmpty())
							? String.valueOf(obj[15]).trim() : null;

			LocalDate localPayDate = null;
			if (payAdvDate != null) {
				localPayDate = EYDateUtil.toUTCDateTimeFromLocal(
						DateUtil.parseObjToDate(payAdvDate));
			}

			String docNum = (obj[16] != null
					&& !obj[16].toString().trim().isEmpty())
							? String.valueOf(obj[16]).trim() : null;

			String docDate = (obj[17] != null
					&& !obj[17].toString().trim().isEmpty())
							? String.valueOf(obj[17]).trim() : null;

			LocalDate localDocDate = null;
			if (docDate != null) {
				localDocDate = EYDateUtil.toUTCDateTimeFromLocal(
						DateUtil.parseObjToDate(docDate));
			}

			processEntity.setInvValue(getAppropriateValueFromObject(obj[18]));

			String plantCode = (obj[19] != null
					&& !obj[19].toString().trim().isEmpty())
							? String.valueOf(obj[19]).trim() : null;
			if (plantCode != null && plantCode.length() > 100) {
				plantCode = plantCode.substring(0, 100);
			}

			String division = (obj[20] != null
					&& !obj[20].toString().trim().isEmpty())
							? String.valueOf(obj[20]).trim() : null;
			if (division != null && division.length() > 100) {
				division = division.substring(0, 100);
			}

			String purOrg = (obj[21] != null
					&& !obj[21].toString().trim().isEmpty())
							? String.valueOf(obj[21]).trim() : null;
			if (purOrg != null && purOrg.length() > 100) {
				purOrg = purOrg.substring(0, 100);
			}

			String profitCentre1 = (obj[22] != null
					&& !obj[22].toString().trim().isEmpty())
							? String.valueOf(obj[22]).trim() : null;
			if (profitCentre1 != null && profitCentre1.length() > 100) {
				profitCentre1 = profitCentre1.substring(0, 100);
			}

			String profitCentre2 = (obj[23] != null
					&& !obj[23].toString().trim().isEmpty())
							? String.valueOf(obj[23]).trim() : null;
			if (profitCentre2 != null && profitCentre2.length() > 100) {
				profitCentre2 = profitCentre2.substring(0, 100);
			}

			String usrDef1 = (obj[24] != null
					&& !obj[24].toString().trim().isEmpty())
							? String.valueOf(obj[24]).trim() : null;
			if (usrDef1 != null && usrDef1.length() > 500) {
				usrDef1 = usrDef1.substring(0, 500);
			}

			String usrDef2 = (obj[25] != null
					&& !obj[25].toString().trim().isEmpty())
							? String.valueOf(obj[25]).trim() : null;
			if (usrDef2 != null && usrDef2.length() > 500) {
				usrDef2 = usrDef2.substring(0, 500);
			}

			String usrDef3 = (obj[26] != null
					&& !obj[26].toString().trim().isEmpty())
							? String.valueOf(obj[26]).trim() : null;
			if (usrDef3 != null && usrDef3.length() > 500) {
				usrDef3 = usrDef3.substring(0, 500);
			}

			String processKey = getFileProcessedKey(obj);

			Long asEntererdId = asEnterIdsList.get(asEnterId);
			processEntity.setAsEnteredId(asEntererdId);
			if (updateFileStatus != null) {
				processEntity.setFileId(updateFileStatus.getId());

			}
			/*
			 * if (asEnterTdsList != null) {
			 * processEntity.setAsEnteredId(((Gstr7AsEnteredTdsEntity)
			 * asEnterTdsList).getId());
			 * 
			 * }
			 */

			if (updateFileStatus != null) {
				processEntity.setCreatedBy(updateFileStatus.getUpdatedBy());
				if (updateFileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
					processEntity.setDataOriginType("E");
				} else if (updateFileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.ERP)) {
					processEntity.setDataOriginType("A");
				}

			}

			processEntity.setReturnPeriod(returnPeriod);
			processEntity.setActType(actType);
			processEntity.setTdsGstin((tdsGstin));
			processEntity.setOrgTdsGstin(orgTdsGstin);
			processEntity.setOrgRetPeriod(origRetPeriod);
			processEntity.setNewGstin(tdsDedGstin);

			processEntity.setConNumber(conNum);
			processEntity.setConDate(localConDate);
			processEntity.setPayNum(payAdvNum);
			processEntity.setPayDate(localPayDate);
			processEntity.setDocNum(docNum);
			processEntity.setDocDate(localDocDate);
			processEntity.setPlantCode(plantCode);
			processEntity.setDivision(division);
			processEntity.setPurOrg(purOrg);
			processEntity.setProCen1(profitCentre1);
			processEntity.setProCen2(profitCentre2);
			processEntity.setUsrDefField1(usrDef1);
			processEntity.setUsrDefField2(usrDef2);
			processEntity.setUsrDefField3(usrDef3);

			processEntity.setReturnPeriod(returnPeriod);
			processEntity.setDerReturnPeriod(
					GenUtil.convertTaxPeriodToInt(returnPeriod));

			processEntity.setTdsInvKey(processKey);

			processEntity.setCreatedBy(APIConstants.SYSTEM);
			processEntity.setGstnError(false);

			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			processEntity.setCreatedOn(convertNow);

			processEntities.add(processEntity);
			asEnterIds.add(asEntererdId);
			asEnterId++;
		}
		gstr7AsEnteredTdsRepository.updateAsEnteredbyProcessAndInfo(asEnterIds);
		return processEntities;
	}
}
