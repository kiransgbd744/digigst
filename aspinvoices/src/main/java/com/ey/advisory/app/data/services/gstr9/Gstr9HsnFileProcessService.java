package com.ey.advisory.app.data.services.gstr9;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.Gstr2XVerticalErrorEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnAsEnteredEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnProcessEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2xVerticalErrorRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9AsEnteredHsnRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9HsnProcessedRepository;
import com.ey.advisory.app.services.common.GstnKeyGenerator;
import com.ey.advisory.app.services.docs.Gstr9HsnExcelConvertion;
import com.ey.advisory.app.services.structuralvalidation.gstr9.Gstr9HsnBusinessValidationChain;
import com.ey.advisory.app.services.structuralvalidation.gstr9.Gstr9HsnStructuralValidatorChain;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr9HsnFileProcessService")
@Slf4j
public class Gstr9HsnFileProcessService {

	@Autowired
	@Qualifier("Gstr9AsEnteredHsnRepository")
	private Gstr9AsEnteredHsnRepository gstr9AsEnteredHsnRepository;

	@Autowired
	@Qualifier("Gstr2xVerticalErrorRepository")
	private Gstr2xVerticalErrorRepository gstr9DocErrorRepository;

	@Autowired
	@Qualifier("Gstr9HsnProcessedRepository")
	private Gstr9HsnProcessedRepository gstr9ProcessedRepository;

	@Autowired
	@Qualifier("Gstr9HsnExcelConvertion")
	private Gstr9HsnExcelConvertion gstr9ExcelConvertion;

	@Autowired
	@Qualifier("Gstr9HsnStructuralValidatorChain")
	private Gstr9HsnStructuralValidatorChain gstr9StructuralValidatorChain;

	@Autowired
	@Qualifier("Gstr9HsnBusinessValidationChain")
	private Gstr9HsnBusinessValidationChain gstr9BusinessValidationChain;

	@Autowired
	@Qualifier("GstnKeyGenerator")
	private GstnKeyGenerator docKeyGenerator;

	public void validateAndProcessGstr9HsnFileData(List<Object[]> gstr9HsnList,
			Gstr1FileStatusEntity updateFileStatus,
			Gstr1FileStatusRepository gstr1FileStatusRepository) {

		List<String> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(gstr9HsnList)) {
			gstr9HsnList = removeExponenti(gstr9HsnList);
			List<Gstr9HsnAsEnteredEntity> asEnterHsnList = gstr9ExcelConvertion
					.convertHsn(gstr9HsnList, updateFileStatus);

			totalIds.addAll(asEnterHsnList.stream()
					.map(Gstr9HsnAsEnteredEntity::getGst9DocKey)
					.collect(Collectors.toList()));
			List<Gstr9HsnAsEnteredEntity> fetchIdsList = Lists.newArrayList();
			if (CollectionUtils.isNotEmpty(totalIds)) {
				List<List<String>> partitions = Lists.partition(totalIds, 999);
				partitions.forEach(partition -> gstr9AsEnteredHsnRepository
						.inactiveExistingData(partition));

			}

			if (CollectionUtils.isNotEmpty(asEnterHsnList)) {
				List<List<Gstr9HsnAsEnteredEntity>> partitions = Lists
						.partition(asEnterHsnList, 10000);
				partitions.forEach(partition -> gstr9AsEnteredHsnRepository
						.saveAll(partition));
			}
			if (CollectionUtils.isNotEmpty(totalIds)) {
				List<List<String>> partitions = Lists.partition(totalIds, 999);
				partitions.forEach(partition -> fetchIdsList
						.addAll(gstr9AsEnteredHsnRepository
								.fetchEnteredEntiryByTdsKeys(partition)));
			}

			Map<Long, String> tdsMap = Maps.newHashMap();
			fetchIdsList.forEach(
					dto -> tdsMap.put(dto.getId(), dto.getGst9DocKey()));

			validateStructuralBusinessValidations(gstr9HsnList, asEnterHsnList,
					updateFileStatus, gstr1FileStatusRepository, tdsMap);
		}
	}

	private List<Object[]> removeExponenti(List<Object[]> gstr9HsnList) {

		List<Object[]> list = new ArrayList<>();
		for (Object[] obj : gstr9HsnList) {
			Object[] obj1 = new Object[14];
			obj1[0] = obj[0];
			obj1[1] = obj[1];
			obj1[2] = obj[2];
			obj1[3] = obj[3];
			obj1[4] = obj[4];
			obj1[5] = obj[5];
			obj1[6] = obj[6];
			obj1[7] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[7]);
			obj1[7] = decimalCheck(obj1[7]);
			obj1[8] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[8]);
			obj1[8] = decimalCheck(obj1[8]);
			obj1[9] = obj[9];
			obj1[10] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[10]);
			obj1[10] = decimalCheck(obj1[10]);
			obj1[11] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[11]);
			obj1[11] = decimalCheck(obj1[11]);
			obj1[12] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[12]);
			obj1[12] = decimalCheck(obj1[12]);
			obj1[13] = CommonUtility
					.exponentialAndZeroCheckForBigDecimal(obj[13]);
			obj1[13] = decimalCheck(obj1[13]);

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

	private void validateStructuralBusinessValidations(
			List<Object[]> gstr9HsnList,
			List<Gstr9HsnAsEnteredEntity> asEnterHsnList,
			Gstr1FileStatusEntity updateFileStatus,
			Gstr1FileStatusRepository gstr1FileStatusRepository,
			Map<Long, String> hsnMap) {

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		List<Object[]> struErrRecords = new ArrayList<>();
		List<Object[]> struProRecords = new ArrayList<>();
		List<Object[]> businessErrRecords = new ArrayList<>();
		List<Object[]> businessProRecords = new ArrayList<>();
		Map<String, List<ProcessingResult>> processingStructResults = gstr9StructuralValidatorChain
				.validation(gstr9HsnList, asEnterHsnList);

		List<String> structuralValKeys = Lists
				.newArrayList(processingStructResults.keySet());

		for (Object[] obj : gstr9HsnList) {
			String hsnKey = gstr9ExcelConvertion.getFileProcessedKey(obj);
			if (!structuralValKeys.contains(hsnKey)) {
				struProRecords.add(obj);
			} else {
				struErrRecords.add(obj);
			}
		}

		// asEnterTdsList.

		if (CollectionUtils.isNotEmpty(struErrRecords)) {
			List<Gstr2XVerticalErrorEntity> errorEntities = gstr9ExcelConvertion
					.convertErrorsIntoDocErrorEntity(struErrRecords,
							processingStructResults, updateFileStatus, hsnMap);
			if (CollectionUtils.isNotEmpty(errorEntities)) {
				errorEntities.forEach(entity -> {
					entity.setValType("SV");
					entity.setErrorType("ERR");
				});
				errorRecords = errorRecords + errorEntities.size();

				List<List<Gstr2XVerticalErrorEntity>> partions = Lists
						.partition(errorEntities, 10000);

				partions.forEach(
						partion -> gstr9DocErrorRepository.saveAll(partion));
			}
		}

		if (CollectionUtils.isNotEmpty(struProRecords)) {
			Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
			List<Gstr9HsnAsEnteredEntity> processList = gstr9ExcelConvertion
					.convertHsn(struProRecords, updateFileStatus);
			for (Gstr9HsnAsEnteredEntity hsn : processList) {
				List<ProcessingResult> results = gstr9BusinessValidationChain
						.validate(hsn, null);
				if (results != null && results.size() > 0) {
					String hsnKey = hsn.getGst9DocKey();
					List<ProcessingResult> current = businessValErrors
							.get(hsnKey);
					if (current == null) {
						current = new ArrayList<>();
						businessValErrors.put(hsnKey, results);
					} else {
						businessValErrors.put(hsnKey, results);
						current.addAll(results);
					}
				}
			}

			List<String> errorKeys = new ArrayList<>();
			for (String keys : businessValErrors.keySet()) {
				errorKeys.add(keys);
			}
			List<Long> asEnterIdsList = Lists.newArrayList();
			for (Object[] productEntity : struProRecords) {
				String hsnKeys = gstr9ExcelConvertion
						.getFileProcessedKey(productEntity);
				if (!errorKeys.contains(hsnKeys)) {
					asEnterIdsList.addAll(gstr9ExcelConvertion
							.filterAndGetEnterIdsByProdKeyWithEnteredId(hsnKeys,
									hsnMap));
					businessProRecords.add(productEntity);
				} else {
					businessErrRecords.add(productEntity);
				}
			}

			if (CollectionUtils.isNotEmpty(businessErrRecords)) {
				List<Gstr2XVerticalErrorEntity> errorEntities = gstr9ExcelConvertion
						.convertErrorsIntoDocErrorEntity(businessErrRecords,
								businessValErrors, updateFileStatus, hsnMap);
				if (CollectionUtils.isNotEmpty(errorEntities)) {
					errorEntities.forEach(entity -> {
						entity.setValType("BV");
						entity.setErrorType("ERR");
					});
					errorRecords = errorRecords + errorEntities.size();

					List<List<Gstr2XVerticalErrorEntity>> partions = Lists
							.partition(errorEntities, 10000);

					partions.forEach(partion -> gstr9DocErrorRepository
							.saveAll(partion));
				}
			}
			List<String> totalIds = Lists.newArrayList();
			if (CollectionUtils.isNotEmpty(businessProRecords)) {
				List<Gstr9HsnProcessEntity> processedIdEntities = gstr9ExcelConvertion
						.convertRecordsIntoProcessedRecords(businessProRecords,
								updateFileStatus, hsnMap, asEnterIdsList);
				if (CollectionUtils.isNotEmpty(processedIdEntities)) {
					processedRecords = processedIdEntities.size();
					processedIdEntities.forEach(entity -> {

						String docKey = docKeyGenerator.generateGstr9HsnKey(
								entity.getGstin(), entity.getFy(),
								entity.getTableNumber(), entity.getHsn(),
								entity.getRateOfTax(), entity.getUqc());

						entity.setGst9HsnDocKey(docKey);

					});

				}
				if (CollectionUtils.isNotEmpty(processedIdEntities)) {
					totalIds.addAll(processedIdEntities.stream()
							.map(Gstr9HsnProcessEntity::getGst9HsnDocKey)
							.collect(Collectors.toList()));
				}
				if (CollectionUtils.isNotEmpty(totalIds)) {
					List<List<String>> partions = Lists.partition(totalIds,
							999);

					partions.forEach(partion -> gstr9ProcessedRepository
							.updateSameInvKey(partion));

				}

				List<List<Gstr9HsnProcessEntity>> partions = Lists
						.partition(processedIdEntities, 10000);

				partions.forEach(
						partion -> gstr9ProcessedRepository.saveAll(partion));
			}
		}
		totalRecords = (gstr9HsnList.size() != 0) ? gstr9HsnList.size() : 0;

		errorRecords = (businessErrRecords.size() != 0
				|| struErrRecords.size() != 0)
						? businessErrRecords.size() + struErrRecords.size() : 0;
		processedRecords = totalRecords - errorRecords;

		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		updateFileStatus.setFileStatus(JobStatusConstants.PROCESSED);
		gstr1FileStatusRepository.save(updateFileStatus);
	}

}
