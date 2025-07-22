package com.ey.advisory.app.services.annexure1fileupload;

import static com.ey.advisory.common.GSTConstants.BUSINESS_VALIDATIONS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Ann1VerticalWebError;
import com.ey.advisory.app.data.entities.client.RefundEntity;
import com.ey.advisory.app.data.entities.client.RefundsExcelEntity;
import com.ey.advisory.app.data.repositories.client.RefundExcelRepository;
import com.ey.advisory.app.data.repositories.client.RefundRepository;
import com.ey.advisory.app.services.businessvalidation.refund.RefundBusinessValidationChain;
import com.ey.advisory.app.services.docs.SRFileToRefundExcelConvertion;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("RefundBusinessErrorUploadService")
public class RefundBusinessErrorUploadService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RefundBusinessErrorUploadService.class);
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	/*@Autowired
	@Qualifier("RefundGstnTable4Repository")
	private RefundGstnTable4Repository refundGstnTable4Repository;*/

	@Autowired
	@Qualifier("SRFileToRefundExcelConvertion")
	private SRFileToRefundExcelConvertion sRFileToRefundExcelConvertion;
	
	@Autowired
	@Qualifier("RefundBusinessValidationChain")
	private RefundBusinessValidationChain refundBusinessValidationChain;

	@Autowired
	@Qualifier("RefundExcelRepository")
	private RefundExcelRepository refundExcelRepository;

	@Autowired
	@Qualifier("RefundRepository")
	private RefundRepository refundRepository;

	public void processBusinessData(List<Object[]> refundList,
			List<RefundsExcelEntity> strucProcessedRecords,
			List<RefundsExcelEntity> strucErrorRecords,
			Gstr1FileStatusEntity updateFileStatus, List<Long> ids) {

		List<RefundsExcelEntity> businessErrorRecords = new ArrayList<>();
		List<RefundsExcelEntity> infoProcessed = new ArrayList<>();
		List<RefundsExcelEntity> businessProcessedRecords = new ArrayList<>();
		//List<RefundGstnEntity> addFile = new ArrayList<>();

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
		Map<String, List<ProcessingResult>> infoWithProcessed = new HashMap<>();
		List<String> errorKeys = new ArrayList<>();
		List<String> infoKeys = new ArrayList<>();
		List<String> errorInfo = new ArrayList<>();
		

		List<ProcessingResult> current = null;

		for (RefundsExcelEntity refund : strucProcessedRecords) {
			List<ProcessingResult> results = refundBusinessValidationChain
					.validate(refund, null);

			if (results != null && results.size() > 0) {
				String invKey = refund.getRefundInvkey();
				Long id = refund.getId();
				String keys = invKey.concat(GSTConstants.SLASH)
						.concat(id.toString());
				List<ProcessingResultType> listTypes = new ArrayList<>();
				for (ProcessingResult types : results) {
					ProcessingResultType type2 = types.getType();
					listTypes.add(type2);
				}

				List<String> errorType = listTypes.stream()
						.map(object -> Objects.toString(object, null))
						.collect(Collectors.toList());

				current = businessValErrors.get(keys);
				if (current == null) {
					current = new ArrayList<>();
					if (errorType.size() > 0) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							/*refundExcelRepository.REFUNDErrorUpdateInfo(invKey,
									id);*/
							errorInfo.add(invKey);
							errorKeys.add(invKey);
							businessValErrors.put(keys, results);
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(invKey);
							businessValErrors.put(keys, results);
						} else {
							infoKeys.add(invKey);
							infoWithProcessed
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						}

					}
				} else {
					if (errorType.size() > 0) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							errorInfo.add(invKey);
							errorKeys.add(invKey);
							/*refundExcelRepository.REFUNDErrorUpdateInfo(invKey,
									id);*/
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(invKey);
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						} else {
							infoKeys.add(invKey);
							infoWithProcessed
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						}
					}
				}
			}
		}

		List<String> busiProcessedKeys = new ArrayList<>();

		for (RefundsExcelEntity process : strucProcessedRecords) {
			String key = process.getRefundInvkey();
			if (!errorKeys.contains(key)) {
				busiProcessedKeys.add(key);
			}
		}
		if (errorInfo.size() > 0 && !errorInfo.isEmpty()) {
			refundExcelRepository.REFUNDErrorUpdateInfo(errorInfo,ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			refundExcelRepository.REFUNDUpdateErrors(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			refundExcelRepository.REFUNDUpdateInfo(infoKeys, ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			businessErrorRecords = refundExcelRepository
					.getAllExcelData(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			infoProcessed = refundExcelRepository
					.getAllProcessedWithInfoData(infoKeys, ids);
		}
		if (busiProcessedKeys.size() > 0 && !busiProcessedKeys.isEmpty()) {
			businessProcessedRecords = refundExcelRepository
					.getAllExcelData(busiProcessedKeys, ids);
		}

		LOGGER.error("businessErrorRecords " + businessErrorRecords.size());
		LOGGER.error(
				"businessProcessedRecords " + businessProcessedRecords.size());
		if (businessValErrors.size() > 0 && !businessValErrors.isEmpty()) {
			Map<String, List<Ann1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(businessValErrors, BUSINESS_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedREFUNDErrorRecords(businessErrorRecords, errorMap);
		}

		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			Map<String, List<Ann1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedREFUNDErrorRecords(infoProcessed, errorMap);
		}

		if (businessProcessedRecords.size() > 0
				&& !businessProcessedRecords.isEmpty()) {
			List<RefundEntity> processedOfBusi = sRFileToRefundExcelConvertion
					.convertSRFileToRefundProcessed(businessProcessedRecords,
							updateFileStatus);

			for (RefundEntity refundUpdate : processedOfBusi) {
				String refundKey = refundUpdate.getRefundInvkey();
				refundRepository.UpdateSameInvKey(refundKey);
			}
			/*List<RefundEntity> saveAll = */refundRepository
					.saveAll(processedOfBusi);
			/*List<RefundGstnEntity> sameRecords = sameRecordsProcessed(saveAll,
					updateFileStatus);
			addFile = convertAddWithinSameFile(sameRecords);

			for (RefundGstnEntity refunds : addFile) {
				String refundGstnKey = refunds.getRefundGstnkey();
				refundGstnTable4Repository.UpdateSameGstnKey(refundGstnKey);
			}

			refundGstnTable4Repository.saveAll(addFile);
*/
		}

		totalRecords = (refundList.size() != 0) ? refundList.size() : 0;
		errorRecords = (businessErrorRecords.size() != 0
				|| strucErrorRecords.size() != 0)
						? businessErrorRecords.size() + strucErrorRecords.size()
						: 0;
		processedRecords = totalRecords - errorRecords;
		information = (infoProcessed.size() != 0) ? infoProcessed.size() : 0;

		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		gstr1FileStatusRepository.save(updateFileStatus);

	}

	/**
	 * @param processedOfBusi
	 * @return
	 */
	/*private List<RefundGstnEntity> convertAddWithinSameFile(
			List<RefundGstnEntity> processedOfBusi) {

		Map<String, RefundGstnEntity> map = new HashMap<>();
		for (RefundGstnEntity summEntity : processedOfBusi) {

			String key = summEntity.getRefundGstnkey();
			if (!map.containsKey(key)) {
				map.put(key, summEntity);
				continue;
			}

			RefundGstnEntity table4Key = map.get(key);
			table4Key.add(summEntity);

		}

		return new ArrayList<>(map.values());
	}

	private List<RefundGstnEntity> sameRecordsProcessed(
			List<RefundEntity> saveAll,
			Gstr1FileStatusEntity updateFileStatus) {
		List<RefundGstnEntity> list = new ArrayList<>();
		for (RefundEntity arr : saveAll) {
			RefundGstnEntity obj = new RefundGstnEntity();

			obj.setSNo(arr.getSNo());
			obj.setSgstin(arr.getSgstin());
			obj.setRetPeriod(arr.getRetPeriod());
			obj.setDerivedRetPeriod(arr.getDerivedRetPeriod());
			obj.setDesc(arr.getDesc());
			if (arr.getTax() != null) {
				obj.setTax(arr.getTax());
			}
			if (arr.getFee() != null) {
				obj.setFee(arr.getFee());
			}
			if (arr.getTotal() != null) {
				obj.setTotal(arr.getTotal());
			}
			if (arr.getInterest() != null) {
				obj.setInterest(arr.getInterest());
			}
			if (arr.getPenalty() != null) {
				obj.setPenalty(arr.getPenalty());
			}
			if (arr.getOther() != null) {
				obj.setOther(arr.getOther());
			}
			if (updateFileStatus != null) {
				obj.setFileId(updateFileStatus.getId());
			}
			obj.setCreatedBy(arr.getCreatedBy());
			obj.setModifiedBy(arr.getModifiedBy());
			obj.setModifiedOn(arr.getModifiedOn());
			obj.setRefundGstnkey(arr.getRefundGstnkey());
			obj.setInfo(arr.isInfo());
			list.add(obj);
		}
		return list;
	}
*/}
