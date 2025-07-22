package com.ey.advisory.app.services.gstr1fileupload;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Gstr9OutwardInwardAsEnteredEntity;
import com.ey.advisory.app.data.entities.client.VerticalWebErrorTable;
import com.ey.advisory.app.data.repositories.client.Gstr9InOutwardExcelRepository;
import com.ey.advisory.app.services.annexure1fileupload.VerticalWebUploadErrorService2;
import com.ey.advisory.app.services.docs.SRFileToGstr9InOutwardConvertion;
import com.ey.advisory.app.services.strcutvalidation.gstr9InOutward.Gstr9InOutStructValidationChain;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Gstr9InOutwardStructuralProcess")
@Slf4j
public class Gstr9InOutwardStructuralProcess {

	@Autowired
	@Qualifier("Gstr9InOutwardBusinessProcess")
	private Gstr9InOutwardBusinessProcess gstr9InOutwardBusinessProcess;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService2")
	private VerticalWebUploadErrorService2 verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr9InOutwardExcelRepository")
	private Gstr9InOutwardExcelRepository gstr9InOutwardExcelRepository;

	@Autowired
	@Qualifier("SRFileToGstr9InOutwardConvertion")
	private SRFileToGstr9InOutwardConvertion sRFileToGstr9InOutwardConvertion;

	@Autowired
	@Qualifier("Gstr9InOutStructValidationChain")
	private Gstr9InOutStructValidationChain gstr9InOutStructValidationChain;

	@Transactional(value = "clientTransactionManager")
	public void gstr9InOutwardStructureProcessData(
			List<Object[]> listOfGstr9InOutward,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr9OutwardInwardAsEnteredEntity> strErrRecords = new ArrayList<>();
		List<Gstr9OutwardInwardAsEnteredEntity> strProcessRecords = new ArrayList<>();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Enter into Structural process data method");
		}

		listOfGstr9InOutward = removeExponenti(listOfGstr9InOutward);
		List<Gstr9OutwardInwardAsEnteredEntity> excelData = sRFileToGstr9InOutwardConvertion
				.convertSRFileToGstr9InOutwardExcel(listOfGstr9InOutward,
						updateFileStatus);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Starting Excel data Dumping process in to Table");
		}

		List<Gstr9OutwardInwardAsEnteredEntity> excelDataSave = gstr9InOutwardExcelRepository
				.saveAll(excelData);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Starting Excel data Dumping process in to Table");
		}

		Map<String, List<ProcessingResult>> processingCrdrResults = gstr9InOutStructValidationChain
				.validation(listOfGstr9InOutward, excelDataSave);

		Map<String, List<ProcessingResult>> processingResults1 = duplicateRecords(
				excelDataSave);

		HashMap<String, List<ProcessingResult>> processingResults = new HashMap<>(
				processingCrdrResults);

		processingResults1.forEach((key, value) -> processingResults.merge(key,
				value, (v1, v2) -> Stream.of(v1, v2).flatMap(x -> x.stream())
						.collect(Collectors.toList())));

		List<String> strErrorKeys = new ArrayList<>();
		for (String keys : processingResults.keySet()) {
			String errkey = keys.substring(0, keys.lastIndexOf('-'));
			strErrorKeys.add(errkey);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Structural Validations Keys" + strErrorKeys);
		}

		for (Gstr9OutwardInwardAsEnteredEntity gstr9InOutward : excelDataSave) {
			String tcsTdsKey = gstr9InOutward.getGst9DocKey();
			if (!strErrorKeys.contains(tcsTdsKey)) {
				strProcessRecords.add(gstr9InOutward);
			} else {
				gstr9InOutward.setError(true);
				gstr9InOutward.setDelete(false);
				strErrRecords.add(gstr9InOutward);
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.error("strucErrorRecords ", strErrRecords.size());
			LOGGER.error("strucProcessRecords ", strProcessRecords.size());
		}

		if (!strErrRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<VerticalWebErrorTable>> errorMap = verticalWebUploadErrorService
					.convertErrors(processingResults,
							GSTConstants.STRUCTURAL_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedErrorGstr9InOutwardRecords(strErrRecords, errorMap);

		}

		if (!strProcessRecords.isEmpty()) {
			gstr9InOutwardBusinessProcess.processGstr9InOutwardBusinessPData(
					strProcessRecords, strErrRecords, updateFileStatus);
		} else {
			totalRecords = (!excelData.isEmpty()) ? excelData.size() : 0;
			errorRecords = (!strErrRecords.isEmpty()) ? strErrRecords.size()
					: 0;
			processedRecords = totalRecords - errorRecords;
			information = 0;

			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			gstr1FileStatusRepository.save(updateFileStatus);
		}
	}

	private List<Object[]> removeExponenti(
			List<Object[]> listOfGstr9InOutward) {

		List<Object[]> list = new ArrayList<>();
		for (Object[] obj : listOfGstr9InOutward) {
			Object[] obj1 = new Object[13];
			obj1[0] = obj[0];
			obj1[1] = obj[1];
			obj1[2] = obj[2];
			obj1[3] = obj[3];
			obj1[4] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[4]);
			obj1[4] = decimalCheck(obj1[4]);
			obj1[5] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[5]);
			obj1[5] = decimalCheck(obj1[5]);
			obj1[6] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[6]);
			obj1[6] = decimalCheck(obj1[6]);
			obj1[7] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[7]);
			obj1[7] = decimalCheck(obj1[7]);
			obj1[8] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[8]);
			obj1[8] = decimalCheck(obj1[8]);
			obj1[9] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[9]);
			obj1[9] = decimalCheck(obj1[9]);
			obj1[10] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[10]);
			obj1[10] = decimalCheck(obj1[10]);
			obj1[11] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[11]);
			obj1[11] = decimalCheck(obj1[11]);
			obj1[12] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[12]);
			obj1[12] = decimalCheck(obj1[12]);
			list.add(obj1);
		}
		return list;

	}

	private static Object decimalCheck(Object obj) {

		if (!isPresent(obj) || !isDecimal(obj.toString().trim())) {
			return obj;
		} else {
			BigDecimal qty = null;
			qty = new BigDecimal(String.valueOf(obj).trim());
			obj = qty.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
			return obj;
		}

	}

	private static Map<String, List<ProcessingResult>> duplicateRecords(
			List<Gstr9OutwardInwardAsEnteredEntity> docs) {
		Map<String, List<Gstr9OutwardInwardAsEnteredEntity>> allDocsMap = docs
				.stream()
				.collect(Collectors.groupingBy(doc -> doc.getGst9DocKey()));
		// Filter out the documents that have more than one element in the value
		// list.
		Map<String, List<Gstr9OutwardInwardAsEnteredEntity>> duplicatesMap = allDocsMap
				.entrySet().stream().filter(e -> e.getValue().size() > 1)
				.collect(Collectors.toMap(Map.Entry::getKey,
						Map.Entry::getValue));
		Set<String> keySet = duplicatesMap.keySet();
		Map<String, List<ProcessingResult>> map = new HashMap<>();
		for (String str : keySet) {
			List<Gstr9OutwardInwardAsEnteredEntity> list = duplicatesMap
					.get(str);
			Set<String> lineItemmSet = new HashSet<>();
			for (Gstr9OutwardInwardAsEnteredEntity dup : list) {
				lineItemmSet.add(getAllLineItems(dup));
			}
			if (lineItemmSet.size() > 1) {
				for (Gstr9OutwardInwardAsEnteredEntity dup : list) {
					List<ProcessingResult> errors = new ArrayList<>();
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.DUP_RECORD);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER6165",
							"Values should be same across all line items of a document.",
							location));
					String key = dup.getGst9DocKey().concat(GSTConstants.SLASH)
							.concat(dup.getId().toString());
					map.put(key, errors);
				}
			}

		}
		return map;

	}

	private static String getAllLineItems(
			Gstr9OutwardInwardAsEnteredEntity docs) {
		String natureOfSup = (docs.getNatureOfSupp() != null)
				? (String.valueOf(docs.getNatureOfSupp())).trim() : "";
		String taxbleValue = (docs.getTaxableVal() != null)
				? (String.valueOf(docs.getTaxableVal())).trim() : "";
		String igst = (docs.getIgst() != null)
				? (String.valueOf(docs.getIgst())).trim() : "";
		String cgst = (docs.getCgst() != null)
				? (String.valueOf(docs.getCgst())).trim() : "";
		String sgst = (docs.getSgst() != null)
				? (String.valueOf(docs.getSgst())).trim() : "";
		String cess = (docs.getCess() != null)
				? (String.valueOf(docs.getCess())).trim() : "";
		String interst = (docs.getInterest() != null)
				? (String.valueOf(docs.getInterest())).trim() : "";
		String penalty = (docs.getPenalty() != null)
				? (String.valueOf(docs.getPenalty())).trim() : "";
		String lateFee = (docs.getLateFee() != null)
				? (String.valueOf(docs.getLateFee())).trim() : "";
		String others = (docs.getOther() != null)
				? (String.valueOf(docs.getOther())).trim() : "";
		return new StringJoiner("|").add(natureOfSup).add(taxbleValue).add(igst)
				.add(cgst).add(sgst).add(cess).add(interst).add(lateFee)
				.add(penalty).add(others).toString();
	}
}
