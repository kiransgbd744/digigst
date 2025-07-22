package com.ey.advisory.app.services.gstr7fileupload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
import com.ey.advisory.app.data.entities.client.Gstr7DocErrorEntity;
import com.ey.advisory.app.data.entities.client.Gstr7ProcessedTdsEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7AsEnteredTdsRepository;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7DocErrorRepository;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7ProcessedRepository;
import com.ey.advisory.app.services.common.GstnKeyGenerator;
import com.ey.advisory.app.services.docs.Gstr7ExcelConvertion;
import com.ey.advisory.app.services.structuralvalidation.gstr7.Gstr7BusinessValidationChain;
import com.ey.advisory.app.services.structuralvalidation.gstr7.Gstr7StructuralValidatorChain;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr7TdsFileProcessService")
public class Gstr7TdsFileProcessService {

	private static final String TABLE_4 = "Table-4";
	private static final String TABLE_3 = "Table-3";
	@Autowired
	@Qualifier("Gstr7AsEnteredTdsRepository")
	private Gstr7AsEnteredTdsRepository gstr7AsEnteredTdsRepository;

	@Autowired
	@Qualifier("Gstr7DocErrorRepository")
	private Gstr7DocErrorRepository gstr7DocErrorRepository;

	@Autowired
	@Qualifier("Gstr7ProcessedRepository")
	private Gstr7ProcessedRepository gstr7ProcessedRepository;

	@Autowired
	@Qualifier("Gstr7ExcelConvertion")
	private Gstr7ExcelConvertion gstr7ExcelConvertion;

	@Autowired
	@Qualifier("Gstr7StructuralValidatorChain")
	private Gstr7StructuralValidatorChain gstr7StructuralValidatorChain;

	@Autowired
	@Qualifier("Gstr7BusinessValidationChain")
	private Gstr7BusinessValidationChain gstr7BusinessValidationChain;

	@Autowired
	@Qualifier("gstr7CanServiceImpl")
	private Gstr7CanService canService;

	@Autowired
	@Qualifier("gstr7FilingLookUpServiceImpl")
	private Gstr7CanService filingService;

	@Autowired
	@Qualifier("GstnKeyGenerator")
	private GstnKeyGenerator docKeyGenerator;
	
	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;
	
	private static final String PIPE = "|";

	Map<String, List<ProcessingResult>> canLookUp = new HashMap<>();

	public void validateAndProcessGstr7TdsFileData(List<Object[]> gstr7TdsList,
			Gstr1FileStatusEntity updateFileStatus,
			Gstr1FileStatusRepository gstr1FileStatusRepository, ProcessingContext context) {

		if (CollectionUtils.isNotEmpty(gstr7TdsList)) {
			List<Gstr7AsEnteredTdsEntity> asEnterTdsList = gstr7ExcelConvertion
					.convertTds(gstr7TdsList, updateFileStatus);
			settingFiledGstins(context);
			canLookUp = canService.DistriButionCanLookUp(asEnterTdsList, context);

			List<String> tdsKeys = asEnterTdsList.stream()
					.map(Gstr7AsEnteredTdsEntity::getTdsKey)
					.collect(Collectors.toList());
			gstr7AsEnteredTdsRepository.inactiveExistingData(tdsKeys);
			asEnterTdsList = (List<Gstr7AsEnteredTdsEntity>) gstr7AsEnteredTdsRepository
					.saveAll(asEnterTdsList);
			validateStructuralBusinessValidations(gstr7TdsList, asEnterTdsList,
					updateFileStatus, gstr1FileStatusRepository);
		}
	}

	private void validateStructuralBusinessValidations(
			List<Object[]> gstr7TdsList,
			List<Gstr7AsEnteredTdsEntity> asEnterTdsList,
			Gstr1FileStatusEntity updateFileStatus,
			Gstr1FileStatusRepository gstr1FileStatusRepository) {

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		Map<String, List<ProcessingResult>> processingStructResults = gstr7StructuralValidatorChain
				.validation(gstr7TdsList, asEnterTdsList);

		/*Map<String, List<ProcessingResult>> distriButionLookUp = filingService
				.DistriButionCanLookUp(asEnterTdsList);*/

		/*Map<String, List<ProcessingResult>> filingError = new HashMap<>(
				processingStructResults);

		distriButionLookUp.forEach((key, value) -> filingError.merge(key, value,
				(v1, v2) -> Stream.of(v1, v2).flatMap(x -> x.stream())
						.collect(Collectors.toList())));*/

		Map<String, List<ProcessingResult>> structuralErrorMap = new HashMap<>(
				processingStructResults);

		canLookUp.forEach((key, value) -> structuralErrorMap.merge(key, value,
				(v1, v2) -> Stream.of(v1, v2).flatMap(x -> x.stream())
						.collect(Collectors.toList())));
		List<Gstr7AsEnteredTdsEntity> businessErrRecords = new ArrayList<>();
		List<Gstr7AsEnteredTdsEntity> struErrRecords = new ArrayList<>();
		List<Gstr7AsEnteredTdsEntity> struProRecords = new ArrayList<>();
		for (Gstr7AsEnteredTdsEntity obj : asEnterTdsList) {

			if (structuralErrorMap.containsKey(obj.getTdsKey())) {

				List<ProcessingResult> errorsList = structuralErrorMap
						.get(obj.getTdsKey());
				if (!errorsList.isEmpty()) {
					obj.setError(true);
					struErrRecords.add(obj);
					List<Gstr7DocErrorEntity> errorEntitiesList = new ArrayList<>();
					for (ProcessingResult result : errorsList) {
						Gstr7DocErrorEntity errorEntity = new Gstr7DocErrorEntity();
						errorEntity.setCreatedBy("System");
						errorEntity.setCreatedOn(LocalDate.now());
						errorEntity.setErrorSource("WEBUPLOAD");
						errorEntity.setErrorField("docNo");
						errorEntity.setErrorCode(result.getCode());
						errorEntity.setValType("SV");
						errorEntity.setErrorType("ERR");
						errorEntity
								.setErrorDescription(result.getDescription());
						errorEntity.setDocHeaderId(obj.getId());
						errorEntity.setFileId(obj.getFileId());
						errorEntity.setInvKey(obj.getTdsKey());
						errorEntitiesList.add(errorEntity);
					}
					gstr7AsEnteredTdsRepository.saveAll(struErrRecords);
					gstr7DocErrorRepository.saveAll(errorEntitiesList);
				}

			} else {
				struProRecords.add(obj);
			}
		}
		if (!struProRecords.isEmpty()) {
			Map<String, List<ProcessingResult>> businesserrorInfoMap = new HashMap<>();

			List<Gstr7AsEnteredTdsEntity> businessProRecords = new ArrayList<>();
			for (Gstr7AsEnteredTdsEntity tds : struProRecords) {
				List<ProcessingResult> results = gstr7BusinessValidationChain
						.validate(tds, null);

				if (results!=null && !results.isEmpty()) {
					Boolean error = results.stream().anyMatch(
							r -> r.getType() == ProcessingResultType.ERROR);
					Boolean info = results.stream().anyMatch(
							r -> r.getType() == ProcessingResultType.INFO);
					if (error != null && error) {
						businessErrRecords.add(tds);
						tds.setError(true);
					}
					if (info != null && info) {
						tds.setInformation(true);
					}
					businesserrorInfoMap.put(tds.getTdsKey(), results);
				}
			}
			businessProRecords = struProRecords.stream()
					.filter(x -> !x.isError()).collect(Collectors.toList());

			for (Gstr7AsEnteredTdsEntity obj : businessProRecords) {
				if (businesserrorInfoMap.containsKey(obj.getTdsKey())) {
					List<ProcessingResult> infoList = businesserrorInfoMap
							.get(obj.getTdsKey());
					if (infoList!=null && !infoList.isEmpty()) {
						List<Gstr7DocErrorEntity> errorEntitiesList = new ArrayList<>();
						for (ProcessingResult result : infoList) {
							Gstr7DocErrorEntity errorEntity = new Gstr7DocErrorEntity();
							errorEntity.setCreatedBy("System");
							errorEntity.setCreatedOn(LocalDate.now());
							errorEntity.setErrorSource("WEBUPLOAD");
							errorEntity.setErrorField("docNo");
							errorEntity.setErrorCode(result.getCode());
							errorEntity.setValType("BV");
							errorEntity.setErrorType("INFO");
							errorEntity.setErrorDescription(
									result.getDescription());
							errorEntity.setDocHeaderId(obj.getId());
							errorEntity.setFileId(obj.getFileId());
							errorEntity.setInvKey(obj.getTdsKey());
							errorEntitiesList.add(errorEntity);
						}
						if(!errorEntitiesList.isEmpty()){
						gstr7DocErrorRepository.saveAll(errorEntitiesList);
						}
					}
				}

			}
			if (!businessErrRecords.isEmpty()) {
				for (Gstr7AsEnteredTdsEntity obj : businessErrRecords) {
					if (businesserrorInfoMap.containsKey(obj.getTdsKey())) {

						List<ProcessingResult> errorsList = businesserrorInfoMap
								.get(obj.getTdsKey());
						if (!errorsList.isEmpty()) {
							obj.setError(true);
							List<Gstr7DocErrorEntity> errorEntitiesList = new ArrayList<>();
							for (ProcessingResult result : errorsList) {
								Gstr7DocErrorEntity errorEntity = new Gstr7DocErrorEntity();
								errorEntity.setCreatedBy("System");
								errorEntity.setCreatedOn(LocalDate.now());
								errorEntity.setErrorSource("WEBUPLOAD");
								errorEntity.setErrorField("docNo");
								errorEntity.setErrorCode(result.getCode());
								errorEntity.setValType("BV");
								String errorType = result.getType().name();
								if("ERROR".equalsIgnoreCase(errorType)){
									errorType="ERR";
								}
								errorEntity
										.setErrorType(errorType);
								errorEntity.setErrorDescription(
										result.getDescription());
								errorEntity.setDocHeaderId(obj.getId());
								errorEntity.setFileId(obj.getFileId());
								errorEntity.setInvKey(obj.getTdsKey());
								errorEntitiesList.add(errorEntity);
							}
							gstr7AsEnteredTdsRepository
									.saveAll(businessErrRecords);
							gstr7DocErrorRepository.saveAll(errorEntitiesList);
						}
					}
				}
			}

			if (!businessProRecords.isEmpty()) {
				List<Gstr7ProcessedTdsEntity> processedIdEntities = deriveProcessRecords(
						businessProRecords, gstr7TdsList);
				if (CollectionUtils.isNotEmpty(processedIdEntities)) {
					processedIdEntities.forEach(entity -> {
						boolean isDataPresent = StringUtils
								.isNotBlank(entity.getOrgTdsGstin())
								&& StringUtils
										.isNotBlank(entity.getOrgRetPeriod())
								&& entity.getOrgGrossAmt() != BigDecimal.ZERO;
						String tableName = isDataPresent ? TABLE_4 : TABLE_3;
						entity.setTabNum(tableName);

						if (tableName.equals(TABLE_3)) {

							String docKey = docKeyGenerator.generateGstr7TdsKey(
									entity.getTdsGstin(),
									entity.getReturnPeriod(),
									entity.getNewGstin());

							entity.setGstinkey(docKey);
						} else if (tableName.equals(TABLE_4)) {
							String docKey = docKeyGenerator
									.generateGstr7TdsaKey(entity.getTdsGstin(),
											entity.getReturnPeriod(),
											entity.getOrgRetPeriod(),
											entity.getNewGstin());

							entity.setGstinkey(docKey);
						}

					});

				}
				List<String> tdsInvKeys = processedIdEntities.stream()
						.map(Gstr7ProcessedTdsEntity::getTdsInvKey)
						.collect(Collectors.toList());
				gstr7ProcessedRepository.updateSameInvKey(tdsInvKeys);
				gstr7ProcessedRepository.saveAll(processedIdEntities);
			}
		}
		totalRecords = gstr7TdsList.size();
		errorRecords = businessErrRecords.size() + struErrRecords.size();
		processedRecords = totalRecords - errorRecords;
		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		updateFileStatus.setFileStatus(JobStatusConstants.PROCESSED);
		gstr1FileStatusRepository.save(updateFileStatus);
	}

	public List<Gstr7ProcessedTdsEntity> deriveProcessRecords(
			List<Gstr7AsEnteredTdsEntity> processRecords,
			List<Object[]> gstr7TdsList) {
		Long fileId = processRecords.get(0).getFileId();
		String dataOriginType = processRecords.get(0).getDataOriginType();
		Set<String> processedDocKeyList = processRecords.stream()
				.map(Gstr7AsEnteredTdsEntity::getTdsKey)
				.collect(Collectors.toSet());
		List<Gstr7ProcessedTdsEntity> processTdsList = new ArrayList<>();
		for (Object[] obj : gstr7TdsList) {
			String processKey = gstr7ExcelConvertion.getFileProcessedKey(obj);
			if (processedDocKeyList.contains(processKey)) {
				Gstr7ProcessedTdsEntity processEntity = new Gstr7ProcessedTdsEntity();

				String returnPeriod = (obj[0] != null
						&& !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]).trim() : null;

				String derivedRePeroid = null;
				if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
					if (returnPeriod.length() == 6) {
						int month = Integer
								.valueOf(returnPeriod.substring(0, 2));
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

				processEntity.setOrgGrossAmt(Gstr7ExcelConvertion
						.getAppropriateValueFromObject(obj[5]));

				String tdsDedGstin = (obj[6] != null
						&& !obj[6].toString().trim().isEmpty())
								? String.valueOf(obj[6]).trim() : null;

				processEntity.setNewGrossAmt(Gstr7ExcelConvertion
						.getAppropriateValueFromObject(obj[7]));

				processEntity.setIgstAmt(Gstr7ExcelConvertion
						.getAppropriateValueFromObject(obj[8]));

				processEntity.setCgstAmt(Gstr7ExcelConvertion
						.getAppropriateValueFromObject(obj[9]));

				processEntity.setSgstAmt(Gstr7ExcelConvertion
						.getAppropriateValueFromObject(obj[10]));

				String conNum = String.valueOf(
						CommonUtility.exponentialAndZeroCheck(obj[11]));
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

				processEntity.setConValue(Gstr7ExcelConvertion
						.getAppropriateValueFromObject(obj[13]));

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

				processEntity.setInvValue(Gstr7ExcelConvertion
						.getAppropriateValueFromObject(obj[18]));

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

				processEntity.setFileId(fileId);

				processEntity.setDataOriginType(dataOriginType);

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
				processTdsList.add(processEntity);

			}
		}
		return processTdsList;

	}
	
	private void settingFiledGstins(ProcessingContext context) {
		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.findByReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
						"GSTR7", "FILED");
		Set<String> filedSet = new HashSet<>();
		for (GstrReturnStatusEntity entity : filedRecords) {

			filedSet.add(entity.getGstin() + PIPE + entity.getTaxPeriod());
		}
		context.seAttribute("filedSet", filedSet);
	}
}
