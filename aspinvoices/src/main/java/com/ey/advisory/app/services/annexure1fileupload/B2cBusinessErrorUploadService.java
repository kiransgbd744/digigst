
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
import com.ey.advisory.app.data.entities.client.OutwardB2cEntity;
import com.ey.advisory.app.data.entities.client.OutwardB2cExcelEntity;
import com.ey.advisory.app.data.repositories.client.OutwardB2cExcelRepository;
import com.ey.advisory.app.data.repositories.client.OutwardB2cRepository;
import com.ey.advisory.app.services.businessvalidation.b2c.B2cBusinessValidationChain;
import com.ey.advisory.app.services.docs.SRFileToOutwardB2CDetailsConvertion;
import com.ey.advisory.app.services.docs.SRFileToOutwardB2CExcelConvertion;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("B2cBusinessErrorUploadService")
@Slf4j
public class B2cBusinessErrorUploadService {

	@Autowired
	@Qualifier("OutwardB2cRepository")
	private OutwardB2cRepository outwardB2cRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("SRFileToOutwardB2CDetailsConvertion")
	private SRFileToOutwardB2CDetailsConvertion sRFileToOutwardB2CDetailsConvertion;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("B2cBusinessValidationChain")
	private B2cBusinessValidationChain b2cBusinessValidationChain;

	@Autowired
	@Qualifier("OutwardB2cExcelRepository")
	private OutwardB2cExcelRepository outwardB2cExcelRepository;

	@Autowired
	@Qualifier("SRFileToOutwardB2CExcelConvertion")
	private SRFileToOutwardB2CExcelConvertion sRFileToOutwardB2CExcelConvertion;

	/*@Autowired
	@Qualifier("OutwardGstnB2cRepository")
	private OutwardGstnB2cRepository outwardGstnB2cRepository;*/

	public void processBusinessData(List<Object[]> listOfB2c,
			List<OutwardB2cExcelEntity> strucProcessedRecords,
			List<OutwardB2cExcelEntity> structuralErrors,
			Gstr1FileStatusEntity updateFileStatus, List<Long> ids) {

		List<OutwardB2cExcelEntity> businessErrors = new ArrayList<>();
		List<OutwardB2cExcelEntity> infoProcessed = new ArrayList<>();
		List<OutwardB2cExcelEntity> businessProcessed = new ArrayList<>();
		//List<OutwardGstnB2cEntity> addFile = new ArrayList<>();

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
		Map<String, List<ProcessingResult>> infoWithProcessed = new HashMap<>();

		List<String> errorKeys = new ArrayList<>();
		List<String> infoKeys = new ArrayList<>();
		List<String> errorInfo =  new ArrayList<>();
		List<OutwardB2cExcelEntity> strucProcessed =
				sRFileToOutwardB2CExcelConvertion.
				convertSRFileToOutwardB2c(strucProcessedRecords, updateFileStatus);
		List<ProcessingResult> current = null;
		for (OutwardB2cExcelEntity b2cs : strucProcessed) {
			List<ProcessingResult> results = b2cBusinessValidationChain
					.validate(b2cs, null);

			if (results != null && results.size() > 0) {
				String key = b2cs.getB2cInvKey();
				Long id = b2cs.getId();
				String keys = key.concat(GSTConstants.SLASH)
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
							/*outwardB2cExcelRepository.b2cUpdateErrorInfo(key,
									id);*/
							errorInfo.add(key);
							errorKeys.add(key);
							businessValErrors.put(keys, results);
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(key);
							businessValErrors.put(keys, results);
						} else {
							infoWithProcessed.put(keys, results);
							infoKeys.add(key);
						}
					}
				} else {
					if (errorType.size() > 0) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							errorInfo.add(key);
							errorKeys.add(key);
							/*outwardB2cExcelRepository.b2cUpdateErrorInfo(key,
									id);*/
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(key);
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						} else {
							infoWithProcessed
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
							infoKeys.add(key);
						}
					}
				}
			}
		}

		List<String> busiProcessedKeys = new ArrayList<>();

		for (OutwardB2cExcelEntity process : strucProcessedRecords) {
			String key = process.getB2cInvKey();
			if (!errorKeys.contains(key)) {
				busiProcessedKeys.add(key);
			}
		}
		if(errorInfo.size() > 0 && !errorInfo.isEmpty()){
			outwardB2cExcelRepository.b2cUpdateErrorInfo(errorInfo,
					ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			outwardB2cExcelRepository.b2cUpdateStructuralError(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			outwardB2cExcelRepository.b2cUpdateInfo(infoKeys, ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			businessErrors = outwardB2cExcelRepository
					.getAllExcelData(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			infoProcessed = outwardB2cExcelRepository
					.getAllProcessedWithInfoData(infoKeys, ids);
		}
		if (busiProcessedKeys.size() > 0 && !busiProcessedKeys.isEmpty()) {
			businessProcessed = outwardB2cExcelRepository
					.getAllExcelData(busiProcessedKeys, ids);
		}

		LOGGER.error("businessErrorRecords ", businessErrors.size());
		LOGGER.error("businessProcessedRecords ", businessProcessed.size());
		if (businessErrors.size() > 0 && !businessErrors.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Ann1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(businessValErrors, BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService.storedErrorRecords(businessErrors,
					errorMap);
		}
		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Ann1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService.storedErrorRecords(infoProcessed,
					errorMap);
		}

		if (businessProcessed.size() > 0 && !businessProcessed.isEmpty()) {

			List<OutwardB2cEntity> processedOfBusi = sRFileToOutwardB2CDetailsConvertion
					.convertSRFileToOutwardB2c(businessProcessed,
							updateFileStatus);
			for (OutwardB2cEntity b2cProcessed : processedOfBusi) {
				String b2cInvKey = b2cProcessed.getB2cInvKey();
				outwardB2cRepository.UpdateSameInvKey(b2cInvKey);
			}
			/*List<OutwardB2cEntity> saveAll =*/ outwardB2cRepository
					.saveAll(processedOfBusi);
		/*	List<OutwardGstnB2cEntity> sameRecords = sameRecords(saveAll,
					updateFileStatus);
			addFile = convertAddWithinSameFile(sameRecords);
			for (OutwardGstnB2cEntity b2cUpdate : addFile) {
				String b2cKey = b2cUpdate.getB2cGstnKey();
				outwardGstnB2cRepository.UpdateSameGstnKey(b2cKey);
			}
			outwardGstnB2cRepository.saveAll(addFile);*/

		}

		totalRecords = (listOfB2c.size() != 0) ? listOfB2c.size() : 0;
		errorRecords = (businessErrors.size() != 0
				|| structuralErrors.size() != 0)
						? businessErrors.size() + structuralErrors.size() : 0;
		processedRecords = totalRecords - errorRecords;
		information = (infoProcessed.size() != 0) ? infoProcessed.size() : 0;

		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		gstr1FileStatusRepository.save(updateFileStatus);

	}

	/*private List<OutwardGstnB2cEntity> convertAddWithinSameFile(
			List<OutwardGstnB2cEntity> sameRecords) {
		Map<String, OutwardGstnB2cEntity> map = new HashMap<>();
		for (OutwardGstnB2cEntity summEntity : sameRecords) {
			String key = summEntity.getB2cGstnKey();
			if (!map.containsKey(key)) {
				map.put(key, summEntity);
				continue;
			}
			OutwardGstnB2cEntity b2cGstnKey = map.get(key);
			b2cGstnKey.add(summEntity);
		}

		return new ArrayList<>(map.values());
	}

	private List<OutwardGstnB2cEntity> sameRecords(
			List<OutwardB2cEntity> saveAll,
			Gstr1FileStatusEntity updateFileStatus) {
		List<OutwardGstnB2cEntity> list = new ArrayList<>();
		for (OutwardB2cEntity arr : saveAll) {
			OutwardGstnB2cEntity obj = new OutwardGstnB2cEntity();
			obj.setRetType(arr.getRetType());
			obj.setRetPeriod(arr.getRetPeriod());
			obj.setSgstin(arr.getSgstin());
			obj.setDiffPercent(arr.getDiffPercent());
			obj.setSec7OfIgstFlag(arr.getSec7OfIgstFlag());
			obj.setAutoPopulateToRefund(arr.getAutoPopulateToRefund());
			obj.setDerivedRetPeriod(arr.getDerivedRetPeriod());
			obj.setPos(arr.getPos());
			if (arr.getRate() != null) {
				obj.setTaxRate(arr.getRate());
			}
			if (arr.getTaxableValue() != null) {
				obj.setTaxableValue(arr.getTaxableValue());
			}
			if (arr.getIgstAmt() != null) {
				obj.setIgstAmt(arr.getIgstAmt());
			}
			if (arr.getCgstAmt() != null) {
				obj.setCgstAmt(arr.getCgstAmt());
			}
			if (arr.getSgstAmt() != null) {
				obj.setSgstAmt(arr.getSgstAmt());
			}
			if (arr.getCessAmt() != null) {
				obj.setCessAmt(arr.getCessAmt());
			}
			if (arr.getTotalValue() != null) {
				obj.setTotalValue(arr.getTotalValue());
			}
			if (arr.getStateCessAmt() != null) {
				obj.setStateCessAmt(arr.getStateCessAmt());
			}
			if (arr.getEcomValueSuppMade() != null) {
				obj.setEcomValueSuppMade(arr.getEcomValueSuppMade());
			}
			if (arr.getEcomValSuppRet() != null) {
				obj.setEcomValSuppRet(arr.getEcomValSuppRet());
			}
			if (arr.getEcomNetValSupp() != null) {
				obj.setEcomNetValSupp(arr.getEcomNetValSupp());
			}
			if (arr.getQuentity() != null) {
				obj.setQuentity(arr.getQuentity());
			}
			if (arr.getTcsAmt() != null) {
				obj.setTcsAmt(arr.getTcsAmt());
			}
			obj.setB2cGstnKey(arr.getB2cGstnKey());
			if (updateFileStatus != null) {
				obj.setFileId(updateFileStatus.getId());
			}
			obj.setInfo(arr.isInfo());
			obj.setCreatedBy("SYSTEM");
			obj.setCreatedOn(LocalDateTime.now());
			obj.setDelete(arr.isDelete());
			list.add(obj);
		}
		return list;
	}
*/}