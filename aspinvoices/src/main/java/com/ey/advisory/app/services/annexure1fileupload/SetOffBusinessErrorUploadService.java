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
import com.ey.advisory.app.data.entities.client.SetOffAndUtilEntity;
import com.ey.advisory.app.data.entities.client.SetOffAndUtilExcelEntity;
import com.ey.advisory.app.data.repositories.client.SetOffAndUtilExcelRepository;
import com.ey.advisory.app.data.repositories.client.SetOffUtilRepository;
import com.ey.advisory.app.services.businessvalidation.setoffandutil.SetOffAndUtilBusinessValidationChain;
import com.ey.advisory.app.services.docs.SRFileToSetOffAndUtilExcelConvertion;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

@Component("SetOffBusinessErrorUploadService")
public class SetOffBusinessErrorUploadService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SetOffBusinessErrorUploadService.class);
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("SetOffAndUtilBusinessValidationChain")
	private SetOffAndUtilBusinessValidationChain setOffAndUtilBusinessValidationChain;

	@Autowired
	@Qualifier("SetOffAndUtilExcelRepository")
	private SetOffAndUtilExcelRepository setOffAndUtilExcelRepository;

	@Autowired
	@Qualifier("SRFileToSetOffAndUtilExcelConvertion")
	private SRFileToSetOffAndUtilExcelConvertion sRFileToSetOffAndUtilExcelConvertion;

	@Autowired
	@Qualifier("SetOffUtilRepository")
	private SetOffUtilRepository setOffUtilRepository;

	public void processBusinessData(List<Object[]> table4List,
			List<SetOffAndUtilExcelEntity> strucProcessedRecords,
			List<SetOffAndUtilExcelEntity> strucErrorRecords,
			Gstr1FileStatusEntity updateFileStatus, List<Long> ids) {

		List<SetOffAndUtilExcelEntity> businessErrorRecords = new ArrayList<>();
		List<SetOffAndUtilExcelEntity> infoProcessed = new ArrayList<>();
		List<SetOffAndUtilExcelEntity> businessProcessedRecords = new ArrayList<>();
		//List<SetOffAndUtilGstnEntity> addFile = new ArrayList<>();

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

		for (SetOffAndUtilExcelEntity setOff : strucProcessedRecords) {
			List<ProcessingResult> results = setOffAndUtilBusinessValidationChain
					.validate(setOff, null);

			if (results != null && results.size() > 0) {
				String invKey = setOff.getSetOffInvKey();
				Long id = setOff.getId();
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
							/*setOffAndUtilExcelRepository
									.SetOffErrorUpdateInfo(invKey, id);*/
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
							/*setOffAndUtilExcelRepository
									.SetOffErrorUpdateInfo(invKey, id);*/
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

		for (SetOffAndUtilExcelEntity process : strucProcessedRecords) {
			String key = process.getSetOffInvKey();
			if (!errorKeys.contains(key)) {
				busiProcessedKeys.add(key);
			}
		}
		if (errorInfo.size() > 0 && !errorInfo.isEmpty()) {
			setOffAndUtilExcelRepository.SetOffErrorUpdateInfo(errorInfo,ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			setOffAndUtilExcelRepository.SetOffUpdateErrors(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			setOffAndUtilExcelRepository.SetOffUpdateInfo(infoKeys, ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			businessErrorRecords = setOffAndUtilExcelRepository
					.getAllExcelData(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			infoProcessed = setOffAndUtilExcelRepository
					.getAllProcessedWithInfoData(infoKeys, ids);
		}
		if (busiProcessedKeys.size() > 0 && !busiProcessedKeys.isEmpty()) {
			businessProcessedRecords = setOffAndUtilExcelRepository
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
					.storedSetOffErrorRecords(businessErrorRecords, errorMap);
		}

		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			Map<String, List<Ann1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedSetOffErrorRecords(infoProcessed, errorMap);
		}

		if (businessProcessedRecords.size() > 0
				&& !businessProcessedRecords.isEmpty()) {
			List<SetOffAndUtilEntity> processedOfBusi = 
					sRFileToSetOffAndUtilExcelConvertion
					.convertSRFileToSetOffAndUtilConvertion(
							businessProcessedRecords, updateFileStatus);

			for (SetOffAndUtilEntity setOffUpdate : processedOfBusi) {
				String invKey = setOffUpdate.getSetOffInvKey();
				setOffUtilRepository.UpdateSameInvKey(invKey);
			}
			/*List<SetOffAndUtilEntity> saveAll =*/ setOffUtilRepository
					.saveAll(processedOfBusi);
			/*List<SetOffAndUtilGstnEntity> sameRecords = sameRecordsProcessed(
					saveAll, updateFileStatus);
			addFile = convertAddWithinSameFile(sameRecords);

			for (SetOffAndUtilGstnEntity setOffUpdate : addFile) {
				String gstnKey = setOffUpdate.getSetOffGstnKey();
				setOffAndutilGstnRepository.UpdateSameGstnKey(gstnKey);
			}
			setOffAndutilGstnRepository.saveAll(addFile);*/
		}

		totalRecords = (table4List.size() != 0) ? table4List.size() : 0;
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
	/*private List<SetOffAndUtilGstnEntity> convertAddWithinSameFile(
			List<SetOffAndUtilGstnEntity> processedOfBusi) {

		Map<String, SetOffAndUtilGstnEntity> map = new HashMap<>();
		for (SetOffAndUtilGstnEntity summEntity : processedOfBusi) {

			String key = summEntity.getSetOffGstnKey();
			if (!map.containsKey(key)) {
				map.put(key, summEntity);
				continue;
			}

			SetOffAndUtilGstnEntity setOffkey = map.get(key);
			setOffkey.add(summEntity);

		}

		return new ArrayList<>(map.values());
	}

	private List<SetOffAndUtilGstnEntity> sameRecordsProcessed(
			List<SetOffAndUtilEntity> saveAll,
			Gstr1FileStatusEntity updateFileStatus) {
		List<SetOffAndUtilGstnEntity> list = new ArrayList<>();
		for (SetOffAndUtilEntity arr : saveAll) {
			SetOffAndUtilGstnEntity obj = new SetOffAndUtilGstnEntity();
			obj.setSNo(arr.getSNo());
			obj.setRetType(arr.getRetType());
			obj.setSgstin(arr.getSgstin());
			obj.setRetPeriod(arr.getRetPeriod());
			obj.setDesc(arr.getDesc());

			if (arr.getTaxPayableRevCharge() != null) {
				obj.setTaxPayableRevCharge(arr.getTaxPayableRevCharge());
			}
			if (arr.getTaxPayableOthRevCharge() != null) {
				obj.setTaxPayableOthRevCharge(arr.getTaxPayableOthRevCharge());
			}
			if (arr.getTaxAlreadyPaidRevCharge() != null) {
				obj.setTaxAlreadyPaidRevCharge(
						arr.getTaxAlreadyPaidRevCharge());
			}
			if (arr.getTaxAlreadyPaidOthRevCharge() != null) {
				obj.setTaxAlreadyPaidOthRevCharge(
						arr.getTaxAlreadyPaidOthRevCharge());
			}
			if (arr.getAdjRevCharge() != null) {
				obj.setAdjRevCharge(arr.getAdjRevCharge());
			}
			if (arr.getAdjOthRevCharge() != null) {
				obj.setAdjOthRevCharge(arr.getAdjOthRevCharge());
			}
			if (arr.getPaidThrouhItcIgst() != null) {
				obj.setPaidThrouhItcIgst(arr.getPaidThrouhItcIgst());
			}
			if (arr.getPaidThrouhItcCgst() != null) {
				obj.setPaidThrouhItcCgst(arr.getPaidThrouhItcCgst());
			}
			if (arr.getPaidThrouhItcSgst() != null) {
				obj.setAdjOthRevCharge(arr.getPaidThrouhItcSgst());
			}
			if (arr.getPaidThrouhItcCess() != null) {
				obj.setPaidThrouhItcCess(arr.getPaidThrouhItcCess());
			}

			if (updateFileStatus != null) {
				obj.setFileId(updateFileStatus.getId());
			}
			if (arr.getPaidInCashTaxCess() != null) {
				obj.setPaidInCashTaxCess(arr.getPaidInCashTaxCess());
			}
			if (arr.getPaidInCashTaxInterest() != null) {
				obj.setPaidInCashTaxInterest(arr.getPaidInCashTaxInterest());
			}
			if (arr.getPaidInCashLateFee() != null) {
				obj.setPaidInCashLateFee(arr.getPaidInCashLateFee());
			}

			if (updateFileStatus != null) {
				obj.setFileId(updateFileStatus.getId());
			}
			obj.setCreatedBy(arr.getCreatedBy());
			obj.setModifiedBy(arr.getModifiedBy());
			obj.setModifiedOn(arr.getModifiedOn());
			obj.setSetOffGstnKey(arr.getSetOffGstnKey());
			obj.setInfo(arr.isInfo());
			list.add(obj);
		}
		return list;
	}
*/}
