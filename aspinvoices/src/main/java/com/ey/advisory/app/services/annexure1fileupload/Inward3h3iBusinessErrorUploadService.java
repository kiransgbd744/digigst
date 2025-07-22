package com.ey.advisory.app.services.annexure1fileupload;

import static com.ey.advisory.common.GSTConstants.BUSINESS_VALIDATIONS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Ann1VerticalWebError;
import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.data.entities.client.InwardTable3IDetailsEntity;
import com.ey.advisory.app.data.repositories.client.InwardExcel3I3HRepository;
import com.ey.advisory.app.data.repositories.client.InwardTable3HRepository;
import com.ey.advisory.app.services.businessvalidation.table3h3i.Table3h3iValidationChain;
import com.ey.advisory.app.services.docs.InwardTable3H3IDetailsConvertion;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

import lombok.extern.slf4j.Slf4j;

@Component("Inward3h3iBusinessErrorUploadService")
@Slf4j
public class Inward3h3iBusinessErrorUploadService {

	@Autowired
	@Qualifier("InwardTable3HRepository")
	private InwardTable3HRepository table3HRepository;

	@Autowired
	@Qualifier("InwardTable3H3IDetailsConvertion")
	private InwardTable3H3IDetailsConvertion inwardTable3H3IDetailsConvertion;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Table3h3iValidationChain")
	private Table3h3iValidationChain table3h3iValidationChain;

	@Autowired
	@Qualifier("InwardExcel3I3HRepository")
	private InwardExcel3I3HRepository inwardExcel3I3HRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	public void processBusinessData(List<Object[]> table3h3i,
			List<InwardTable3I3HExcelEntity> strucProcessedRecords,
			List<InwardTable3I3HExcelEntity> strucErrorRecords,
			Gstr1FileStatusEntity updateFileStatus, List<Long> ids) {

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		List<InwardTable3I3HExcelEntity> businessErrorRecords = new ArrayList<>();
		List<InwardTable3I3HExcelEntity> infoProcessed = new ArrayList<>();
		List<InwardTable3I3HExcelEntity> businessProcessedRecords = new ArrayList<>();
		//List<InwardTable3H3IGstnDetailsEntity> addFile = new ArrayList<>();

		Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
		Map<String, List<ProcessingResult>> infoWithProcessed = new HashMap<>();
		List<String> errorKeys = new ArrayList<>();
		List<String> infoKeys = new ArrayList<>();
		List<String> errorInfo = new ArrayList<>();
		

		List<ProcessingResult> current = null;

		for (InwardTable3I3HExcelEntity table3h : strucProcessedRecords) {
			List<ProcessingResult> results = table3h3iValidationChain
					.validate(table3h, null);

			if (results != null && results.size() > 0) {
				String table3h3iKey = table3h.getTable3h3iInvKey();
				Long id = table3h.getId();
				String keys = table3h3iKey.concat(GSTConstants.SLASH)
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
					current = new ArrayList<ProcessingResult>();
					if (errorType.size() > 0) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							/*inwardExcel3I3HRepository
									.table3h3iUpdateErrorInfo(table3h3iKey, id);*/
							errorInfo.add(table3h3iKey);
							errorKeys.add(table3h3iKey);
							businessValErrors.put(keys, results);
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(table3h3iKey);
							businessValErrors.put(keys, results);
						} else {
							infoKeys.add(table3h3iKey);
							infoWithProcessed.put(keys, results);
						}
					}
				} else {
					if (errorType.size() > 0) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							/*inwardExcel3I3HRepository
									.table3h3iUpdateErrorInfo(table3h3iKey, id);*/
							errorKeys.add(table3h3iKey);
							errorInfo.add(table3h3iKey);
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(table3h3iKey);
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						} else {
							infoKeys.add(table3h3iKey);
							infoWithProcessed
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						}
					}
					current.addAll(results);
				}
			}
		}
		List<String> busiProcessedKeys = new ArrayList<>();

		for (InwardTable3I3HExcelEntity process : strucProcessedRecords) {
			String key = process.getTable3h3iInvKey();
			if (!errorKeys.contains(key)) {
				busiProcessedKeys.add(key);
			}
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			inwardExcel3I3HRepository.table3h3iUpdateErrors(errorKeys, ids);
		}
		if(errorInfo.size() > 0 && !errorInfo.isEmpty()){
			inwardExcel3I3HRepository.table3h3iUpdateErrorInfo(errorInfo,
					ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			inwardExcel3I3HRepository.table3h3iUpdateInfo(infoKeys, ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			businessErrorRecords = inwardExcel3I3HRepository
					.getAllExcelData(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			infoProcessed = inwardExcel3I3HRepository
					.getAllProcessedWithInfoData(infoKeys, ids);
		}
		if (busiProcessedKeys.size() > 0 && !busiProcessedKeys.isEmpty()) {
			businessProcessedRecords = inwardExcel3I3HRepository
					.getAllExcelData(busiProcessedKeys, ids);
		}

		LOGGER.error("businessErrorRecords ", businessErrorRecords.size());
		LOGGER.error("businessProcessedRecords ",
				businessProcessedRecords.size());

		if (businessValErrors.size() > 0 && !businessValErrors.isEmpty()) {
			Map<String, List<Ann1VerticalWebError>> errorBusinessMap = verticalWebUploadErrorService
					.convertErrors(businessValErrors, BUSINESS_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService.storedTable3HErrorRecords(
					businessErrorRecords, errorBusinessMap);

		}
		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Ann1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedTable3HErrorRecords(infoProcessed, errorMap);
		}

		if (businessProcessedRecords.size() > 0
				&& !businessProcessedRecords.isEmpty()) {
			List<InwardTable3IDetailsEntity> processedOfBusi = inwardTable3H3IDetailsConvertion
					.convertSRFileTo3h3iDoc(businessProcessedRecords,
							updateFileStatus);
			for (InwardTable3IDetailsEntity b2cUpdate : processedOfBusi) {
				String table3H3Ikey = b2cUpdate.getTable3h3iInvKey();
				table3HRepository.UpdateSameInvKey(table3H3Ikey);
			}

			/*List<InwardTable3IDetailsEntity> saveAll = */table3HRepository
					.saveAll(processedOfBusi);
			/*List<InwardTable3H3IGstnDetailsEntity> sameRecords = sameRecords(
					saveAll, updateFileStatus);

			addFile = convertAddWithinSameFile(sameRecords);
			for (InwardTable3H3IGstnDetailsEntity table3h3iUpdate : addFile) {
				String table3h3iKey = table3h3iUpdate.getTable3h3iGstnKey();
				inwardTable3H3IGstnRepository.UpdateSameKey(table3h3iKey);
			}
			inwardTable3H3IGstnRepository.saveAll(addFile);*/

		}

		totalRecords = (table3h3i.size() != 0) ? table3h3i.size() : 0;
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
	/*private List<InwardTable3H3IGstnDetailsEntity> convertAddWithinSameFile(
			List<InwardTable3H3IGstnDetailsEntity> processedOfBusi) {
		Map<String, InwardTable3H3IGstnDetailsEntity> map = new HashMap<>();

		for (InwardTable3H3IGstnDetailsEntity summEntity : processedOfBusi) {

			String key = summEntity.getTable3h3iGstnKey();
			if (!map.containsKey(key)) {
				map.put(key, summEntity);
				continue;
			}
			InwardTable3H3IGstnDetailsEntity table3iKey = map.get(key);
			table3iKey.add(summEntity);

		}
		return new ArrayList<>(map.values());
	}
*/
	/*private List<InwardTable3H3IGstnDetailsEntity> sameRecords(
			List<InwardTable3IDetailsEntity> saveAll,
			Gstr1FileStatusEntity updateFileStatus) {
		List<InwardTable3H3IGstnDetailsEntity> list = new ArrayList<>();

		for (InwardTable3IDetailsEntity arr : saveAll) {

			InwardTable3H3IGstnDetailsEntity obj = new InwardTable3H3IGstnDetailsEntity();

			obj.setReturnType(arr.getReturnType());
			obj.setReturnPeriod(arr.getReturnPeriod());
			obj.setSupplierGSTINorpan(arr.getSupplierGSTINorpan());
			obj.setCustGstin(arr.getCustGstin());
			obj.setTransactionFlag(arr.getTransactionFlag());
			obj.setAutoPopulateToRefund(arr.getAutoPopulateToRefund());
			obj.setDerivedRetPeriod(arr.getDerivedRetPeriod());
			obj.setDelete(arr.isDelete());
			if (arr.getIntegratedTaxAmount() != null) {
				obj.setIntegratedTaxAmount(arr.getIntegratedTaxAmount());
			}
			if (arr.getCentralTaxAmount() != null) {
				obj.setCentralTaxAmount(arr.getCentralTaxAmount());
			}
			if (arr.getStateUTTaxAmount() != null) {
				obj.setStateUTTaxAmount(arr.getStateUTTaxAmount());
			}
			if (arr.getCessAmount() != null) {
				obj.setCessAmount(arr.getCessAmount());
			}
			if (arr.getAvailableIGST() != null) {
				obj.setAvailableIGST(arr.getAvailableIGST());
			}
			if (arr.getAvailableCGST() != null) {
				obj.setAvailableCGST(arr.getAvailableCGST());
			}
			if (arr.getAvailableSGST() != null) {
				obj.setAvailableSGST(arr.getAvailableSGST());
			}
			if (arr.getAvailableCess() != null) {
				obj.setAvailableCess(arr.getAvailableCess());
			}
			obj.setSec70fIGSTFLAG(arr.getSec70fIGSTFLAG());
			obj.setHsn(arr.getHsn());
			obj.setPos(arr.getPos());
			obj.setTable3h3iGstnKey(arr.getTable3h3iGstnKey());
			if (arr.getTaxableValue() != null) {
				obj.setTaxableValue(arr.getTaxableValue());
			}
			if (arr.getTotalValue() != null) {
				obj.setTotalValue(arr.getTotalValue());
			}
			if (arr.getTaxRate() != null) {
				obj.setTaxRate(arr.getTaxRate());
			}
			obj.setInfo(arr.isInfo());
			if (updateFileStatus != null) {
				obj.setFileId(updateFileStatus.getId());
			}
			obj.setCreatedBy(arr.getCreatedBy());
			obj.setCreatedOn(arr.getCreatedOn());
			obj.setModifiedBy(arr.getModifiedBy());
			obj.setModifiedOn(arr.getModifiedOn());
			list.add(obj);
		}

		return list;
	}
*/
}
