package com.ey.advisory.app.services.gstr1fileupload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredHsnEntity;
import com.ey.advisory.app.data.entities.client.Gstr1UserInputHsnSacEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1HsnAsEnteredRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1UserInputHsnSacRepository;
import com.ey.advisory.app.services.annexure1fileupload.VerticalWebUploadErrorService;
import com.ey.advisory.app.services.docs.SRFileToHsnDetailsConvertion;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.services.strcutvalidation.HsnSacSummery.HsnSacSummeryStructValidationChain;
import com.ey.advisory.app.services.validation.HsnSacSummery.HsnValidationChain;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Gstr1HsnStructuralProcess")
@Slf4j
public class Gstr1HsnStructuralProcess {

	@Autowired
	@Qualifier("Gstr1HsnBusinessProcess")
	private Gstr1HsnBusinessProcess gstr1HsnBusinessProcess;

	@Autowired
	@Qualifier("SRFileToHsnDetailsConvertion")
	private SRFileToHsnDetailsConvertion sRFileToHsnDetailsConvertion;

	@Autowired
	@Qualifier("Gstr1HsnAsEnteredRepository")
	private Gstr1HsnAsEnteredRepository gstr1HsnAsEnteredRepository;

	@Autowired
	@Qualifier("Gstr1UserInputHsnSacRepository")
	private Gstr1UserInputHsnSacRepository gstr1HsnAsUserInputRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("HsnSacSummeryStructValidationChain")
	private HsnSacSummeryStructValidationChain hsnSacSummeryStructValidationChain;

	@Autowired
	@Qualifier("HsnValidationChain")
	HsnValidationChain hsnBusinessChain;

	@Autowired
	@Qualifier("DefaultClientGroupService")
	private ClientGroupService clientGroupService;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	private static final String EXCEPTION_APP = "Exception while saving the records";
	private static final String EXCEPTIONS = "Exception Occured: {}";
	private static final Map<String, String> ERROR_CODE_MAP = new ImmutableMap.Builder<String, String>()
			.put("ER5205", "Invalid Serial Number")
			.put("ER5206", "Invalid Serial Number")
			.put("ER5701", "Invalid Supplier GSTIN")
			.put("ER5703", "Invalid Return Period")
			.put("ER5705", "Invalid HSNorSAC")
			.put("ER5207", "Invalid ProductDescription")
			.put("ER5707", "Invalid UnitOfMeasurement")
			.put("ER5708", "Invalid Quantity").put("ER1622", "Invalid Rate")
			.put("ER5709", "Invalid Taxable Value")
			.put("ER5710", "Invalid IGST").put("ER5711", "Invalid CGST")
			.put("ER5712", "Invalid SGST").put("ER5713", "Invalid CESS")
			.put("ER5714", "Invalid Total Value")
			.put("ER5702", "Supplier GSTIN is not as per On-Boarding data.")
			.put("ER5704", "Return Period cannot be before 072017.")
			.put("ER5706", "Invalid UQC.")
			.put("ER1278",
					"Length of entered HSN code is not valid as per AATO")
			.put("ER1276", "GSTR1 for this tax period is already filed")
			.put("ER1277", "Invalid RecordType").build();

	@Transactional(value = "clientTransactionManager")
	public void processData(Map<String, List<Object[]>> hsnMap,
			Gstr1FileStatusEntity updateFileStatus, String userName) {

		Map<String, List<ProcessingResult>> strValidation = hsnSacSummeryStructValidationChain
				.validation(hsnMap);

		List<String> listKeys = new ArrayList<>();
		for (String keys : strValidation.keySet()) {
			listKeys.add(keys);
		}

		Map<String, List<Object[]>> processMapObj = new HashMap<>();
		Map<String, List<Object[]>> errDocMapObj = new HashMap<>();
		for (String keys : hsnMap.keySet()) {
			if (!listKeys.contains(keys)) {
				List<Object[]> list = hsnMap.get(keys);
				processMapObj.put(keys, list);
			} else {
				List<Object[]> list = hsnMap.get(keys);
				errDocMapObj.put(keys, list);
			}
		}

		saveErrDocAndDoc(hsnMap, updateFileStatus, strValidation, processMapObj,
				errDocMapObj, userName);
	}

	@Transactional(value = "clientTransactionManager")
	private void saveErrDocAndDoc(Map<String, List<Object[]>> documentMap,
			Gstr1FileStatusEntity updateFileStatus,
			Map<String, List<ProcessingResult>> processingResults,
			Map<String, List<Object[]>> processMapObj,
			Map<String, List<Object[]>> errDocMapObj, String userName) {
		Integer totalRecords;
		Integer processedRecords;
		Integer errorRecords;
		Integer businessErrorCount;
		try {
			if (!errDocMapObj.isEmpty()) {
				convertToHsn(errDocMapObj, processingResults, updateFileStatus,
						userName, true);
			}
			if (!processMapObj.isEmpty()) {
				convertToHsn(processMapObj, processingResults, updateFileStatus,
						userName, false);
			}

			processedRecords = gstr1HsnAsEnteredRepository
					.processedCount(updateFileStatus.getId());
			errorRecords = gstr1HsnAsEnteredRepository
					.businessValidationCount(updateFileStatus.getId());
			totalRecords = errorRecords + processedRecords;
			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			gstr1FileStatusRepository.save(updateFileStatus);
		} catch (Exception e) {
			LOGGER.error(EXCEPTIONS, e);
			throw new AppException(EXCEPTION_APP);
		}
	}

	public void convertToProcess(Map<String, List<Object[]>> processMapObj,
			Map<String, List<ProcessingResult>> processingResults,
			Gstr1FileStatusEntity fileStatus, String userName,
			Boolean isError) {
		List<Gstr1UserInputHsnSacEntity> hsnUserInputs = new ArrayList<>();
		try {
			Set<String> gstinTaxPeriodSet = new HashSet<>();
			processMapObj.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<Object[]> objs = entry.getValue();
				BigDecimal quantity, taxableValueAmount, igstAmount, cgstAmount,
						sgstAmount, cessAmount, totalValueAmount;
				quantity = taxableValueAmount = igstAmount = cgstAmount = sgstAmount = cessAmount = totalValueAmount = BigDecimal.ZERO;
				Gstr1UserInputHsnSacEntity userInputEntity = new Gstr1UserInputHsnSacEntity();
				for (Object[] obj : objs) {
					String gstin = getWithValue(obj[1]);
					String returnPeriod = getWithValue(obj[2]);
					// recordType added
					String recordType = getWithValue(obj[3]);
					String hsnOrSac = getWithValue(obj[4]);
					String description = getWithValue(obj[5]);
					
					String uqc = (obj[6] != null
							&& !obj[6].toString().trim().isEmpty())
									? String.valueOf(obj[6]).trim().toUpperCase() : null;
					
					//String uqc = getWithValue(obj[6]);
					String qty = getWithValue(obj[7]);
					String rate = getWithValue(obj[8]);
					String taxableValue = getWithValue(obj[9]);
					String igst = getWithValue(obj[10]);
					String cgst = getWithValue(obj[11]);
					String sgst = getWithValue(obj[12]);
					String cess = getWithValue(obj[13]);
					String invoiceValue = getWithValue(obj[14]);
					String generateHsnKey = generateHsnKey(obj);
					Integer deriRetPeriod = GenUtil
							.convertTaxPeriodToInt(returnPeriod);
					//gstinTaxPeriodSet.add(gstin + returnPeriod);
					userInputEntity.setSgstin(gstin);
					taxableValueAmount = taxableValueAmount
							.add(convertToBigDecimal(taxableValue));
					quantity = quantity.add(convertToBigDecimal(qty));
					igstAmount = igstAmount.add(convertToBigDecimal(igst));
					cgstAmount = cgstAmount.add(convertToBigDecimal(cgst));
					sgstAmount = sgstAmount.add(convertToBigDecimal(sgst));
					cessAmount = cessAmount.add(convertToBigDecimal(cess));
					totalValueAmount = totalValueAmount
							.add(convertToBigDecimal(invoiceValue));
					LocalDateTime convertNow = LocalDateTime.now();
					userInputEntity.setCreatedOn(convertNow);
					userInputEntity.setReturnPeriod(returnPeriod);
					userInputEntity.setRecordType(recordType);
					userInputEntity.setHsn(hsnOrSac);
					userInputEntity.setDesc(description);
					userInputEntity.setUqc(uqc);
					userInputEntity.setUsrQunty(quantity);
					userInputEntity.setUsrTaxableValue(taxableValueAmount);
					userInputEntity.setUsrIgst(igstAmount);
					userInputEntity.setUsrCgst(cgstAmount);
					userInputEntity.setUsrSgst(sgstAmount);
					userInputEntity.setUsrCess(cessAmount);
					userInputEntity.setUsrTotalValue(totalValueAmount);
					userInputEntity.setDocKey(generateHsnKey);
					gstinTaxPeriodSet.add(userInputEntity.getDocKey());
					userInputEntity.setUsrRate(convertToBigDecimal(rate));
					userInputEntity.setUsrQunty(quantity);
					userInputEntity.setDerivedRetPeriod(
							deriRetPeriod != null ? deriRetPeriod.toString()
									: null);
					if (hsnOrSac != null && returnPeriod != null) {
						String tax = "01" + returnPeriod.toString().trim();
						DateTimeFormatter formatter = DateTimeFormatter
								.ofPattern("ddMMyyyy");
						LocalDate returnPeriod1 = LocalDate.parse(tax,
								formatter);
						String pregst = "01052021";
						LocalDate validReturnDate = LocalDate.parse(pregst,
								formatter);
						if (validReturnDate != null
								&& (returnPeriod1
										.compareTo(validReturnDate) >= 0)
								&& (hsnOrSac.toString().startsWith("99"))) {
							userInputEntity.setUqc("NA");
							userInputEntity.setUsrQunty(BigDecimal.ZERO);
						}
					}
				}
				hsnUserInputs.add(userInputEntity);

			});
			List<String> gstinTaxPeriodList = new ArrayList<String>();
			gstinTaxPeriodList.addAll(gstinTaxPeriodSet);
			softDelete(gstinTaxPeriodList);
			gstr1HsnAsUserInputRepository.saveAll(hsnUserInputs);
		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
			throw new AppException(EXCEPTION_APP);
		}
	}

	public void convertToHsn(Map<String, List<Object[]>> docMapObj,
			Map<String, List<ProcessingResult>> processingResults,
			Gstr1FileStatusEntity updateFileStatus, String userName,
			Boolean isError) {
		List<Gstr1AsEnteredHsnEntity> hsns = new ArrayList<>();
		try {
			Map<String, List<Object[]>> updatedDocMapObj = new HashMap<>();
			List<Long> entityIds = clientGroupService
					.findEntityDetailsForGroupCode();
			Map<String, Long> gstinAndEntityMap = clientGroupService
					.getGstinAndEntityMapForGroupCode(entityIds);
			Map<Long, List<EntityConfigPrmtEntity>> map = onboardingConfigParamCheck
					.getEntityAndConfParamMap();

			docMapObj.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<ProcessingResult> errorList = processingResults.get(key);
				List<Object[]> objs = entry.getValue();
				for (Object[] obj : objs) {

					Gstr1AsEnteredHsnEntity hsnEntity = new Gstr1AsEnteredHsnEntity();
					String serialNo = getWithValue(obj[0]);
					String gstin = getWithValue(obj[1]);
					String returnPeriod = getWithValue(obj[2]);
					String recordType = getWithValue(obj[3]);
					String hsnOrSac = getWithValue(obj[4]);
					String description = getWithValue(obj[5]);
					
					String uqc = (obj[6] != null
							&& !obj[6].toString().trim().isEmpty())
									? String.valueOf(obj[6]).trim().toUpperCase() : null;
					
					//String uqc = getWithValue(obj[6]);
					String qty = getWithValue(obj[7]);
					String rate = getWithValue(obj[8]);
					String taxableValue = getWithValue(obj[9]);
					String igst = getWithValue(obj[10]);
					String cgst = getWithValue(obj[11]);
					String sgst = getWithValue(obj[12]);
					String cess = getWithValue(obj[13]);
					String invoiceValue = getWithValue(obj[14]);
					String generateKey = generateHsnKey(obj);
					String taxPeriod;
					if (returnPeriod != null
							&& !returnPeriod.toString().isEmpty()) {
						taxPeriod = returnPeriod.toString().trim();
						if (StringUtils.isNumeric(taxPeriod)
								&& taxPeriod.length() == 6) {
							Integer deriRetPeriod = GenUtil
									.convertTaxPeriodToInt(returnPeriod);
							hsnEntity.setDerivedRetPeriod(deriRetPeriod);
						}
					}
					hsnEntity.setSgstin(gstin);
					if (updateFileStatus != null) {
						hsnEntity.setFileId(updateFileStatus.getId());
						hsnEntity.setCreatedBy(updateFileStatus.getUpdatedBy());
					}
					LocalDateTime convertNow = LocalDateTime.now();
					hsnEntity.setCreatedOn(convertNow);
					hsnEntity.setModifiedOn(convertNow);
					hsnEntity.setSerialNo(serialNo);
					hsnEntity.setReturnPeriod(returnPeriod);
					hsnEntity.setRecordType(recordType);
					hsnEntity.setHsn(hsnOrSac);
					hsnEntity.setDescription(description);
					hsnEntity.setUqc(uqc);
					hsnEntity.setQuentity(qty);
					hsnEntity.setTaxableValue(taxableValue);
					hsnEntity.setIgst(igst);
					hsnEntity.setCgst(cgst);
					hsnEntity.setSgst(sgst);
					hsnEntity.setCess(cess);
					hsnEntity.setTotalValue(invoiceValue);
					hsnEntity.setInvHsnKey(generateKey);
					hsnEntity.setRate(rate);
					hsnEntity.setError(isError);
					hsnEntity.setDataOrgType("E");
					hsnEntity.setEntityId(gstinAndEntityMap.get(gstin));
					hsnEntity.setEntityConfigParamMap(map);
					if (isError) {
						Pair<String, String> errorCodeDescPair = populateErrorCodesAndDescription(
								errorList);
						hsnEntity.setErrorDescription(
								errorCodeDescPair.getValue1());
						hsnEntity.setErrorCode(errorCodeDescPair.getValue0());
					} else {
						List<ProcessingResult> businessValidate = new ArrayList<>();
						businessValidate = hsnBusinessChain.validate(hsnEntity,
								null);
						if (!businessValidate.isEmpty()) {
							Pair<String, String> errorCodeDescPair = populateErrorCodesAndDescription(
									businessValidate);
							hsnEntity.setErrorDescription(
									errorCodeDescPair.getValue1());
							hsnEntity.setErrorCode(
									errorCodeDescPair.getValue0());
							hsnEntity.setError(true);
						} else {
							updatedDocMapObj.put(entry.getKey(),
									entry.getValue());
						}
					}
					hsns.add(hsnEntity);
				}
			});
			gstr1HsnAsEnteredRepository.saveAll(hsns);
			if (!isError) {
				convertToProcess(updatedDocMapObj, processingResults,
						updateFileStatus, userName, false);
			}
		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
			throw new AppException(EXCEPTION_APP);
		}
	}

	private String getWithValue(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;
		return value;
	}

	public String generateHsnKey(Object[] arr) {

		String recordType = arr[3] != null ? String.valueOf(arr[3]) : "";
		String hsnOrSac = arr[4] != null ? String.valueOf(arr[4]) : "";
		String uqc = arr[6] != null ? String.valueOf(arr[6]).toUpperCase() : "";
		String gstin = arr[1] != null ? String.valueOf(arr[1]) : "";
		String taxRate = arr[8] != null ? String.valueOf(arr[8]) : "";
		String taxPeriod = "";
		String returnPeriod = "";
		if (arr[2] != null && !arr[2].toString().isEmpty()) {
			taxPeriod = arr[2].toString().trim();
			if (StringUtils.isNumeric(taxPeriod) && taxPeriod.length() == 6) {
				returnPeriod = arr[2] != null
						? GenUtil.getDerivedTaxPeriod(String.valueOf(arr[2]))
								.toString()
						: "";
			}
		}
		if (arr[2] != null && !arr[2].toString().isEmpty()) {
			taxPeriod = arr[2].toString().trim();
			if (StringUtils.isNumeric(taxPeriod) && taxPeriod.length() == 6) {
				if (hsnOrSac != null && taxPeriod != null) {
					String tax = "01" + taxPeriod.toString().trim();
					DateTimeFormatter formatter = DateTimeFormatter
							.ofPattern("ddMMyyyy");
					LocalDate returnPeriod1 = LocalDate.parse(tax, formatter);
					String pregst = "01052021";
					LocalDate validReturnDate = LocalDate.parse(pregst,
							formatter);
					if (validReturnDate != null
							&& (returnPeriod1.compareTo(validReturnDate) >= 0)
							&& (hsnOrSac.toString().startsWith("99"))) {
						uqc = "NA";
					}
				}
			}
		} else {
			taxPeriod = null;
		}

		/*
		 * return new
		 * StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(returnPeriod)
		 * .add(gstin).add(uqc).add(taxRate).add(hsnOrSac).toString();
		 */

		// new Invoice key : SupplierGSTIN + ReturnPeriod + RecordType +
		// HSNorSAC + UnitOfMeasurement + Rate

		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(gstin)
				.add(returnPeriod).add(recordType).add(hsnOrSac).add(uqc)
				.add(taxRate).toString();
	}

	private BigDecimal convertToBigDecimal(Object value) {
		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		} else if (value instanceof String) {
			return new BigDecimal(value.toString());
		} else {
			return BigDecimal.valueOf(0);
		}
	}

	private void softDelete(List<String> gstinTaxPeriodKey) {

		if (gstinTaxPeriodKey.isEmpty())
			return;

		int softDeletedCount = 0;

		List<List<String>> chunks = Lists.partition(gstinTaxPeriodKey, 2000);
		for (List<String> chunk : chunks) {
			int rowsEffected = 0;

			rowsEffected = gstr1HsnAsUserInputRepository
					.updateIsDeleteFlag(chunk);

			softDeletedCount = softDeletedCount + rowsEffected;
			String msg = String.format(
					" %d rows affected in GSTR1_USERINPUT_HSNSAC table",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	private Pair<String, String> populateErrorCodesAndDescription(
			List<ProcessingResult> processingResults) {
		List<String> descList = new ArrayList<>();
		List<String> errorCodeList = new ArrayList<>();
		for (ProcessingResult error : processingResults) {
			errorCodeList.add(error.getCode());
			String errorCodeDesc = error.getCode() + "-"
					+ ERROR_CODE_MAP.get(error.getCode());
			descList.add(errorCodeDesc);
		}
		String errorCode = errorCodeList.stream()
				.collect(Collectors.joining(","));
		String errorDesc = descList.stream().collect(Collectors.joining(","));
		return new Pair<>(errorCode, errorDesc);
	}

}