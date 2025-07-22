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
import com.ey.advisory.app.data.entities.client.Ret1And1AEntity;
import com.ey.advisory.app.data.entities.client.Ret1And1AExcelEntity;
import com.ey.advisory.app.data.repositories.client.Ret1And1AExcelRepository;
import com.ey.advisory.app.data.repositories.client.Ret1And1ARepository;
import com.ey.advisory.app.services.businessvalidation.ret1and1a.Ret1And1ABusinessValidationChain;
import com.ey.advisory.app.services.docs.SRFileToRet1And1AExcelConvertion;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Ret1And1ABusinessErrorUploadService")
public class Ret1And1ABusinessErrorUploadService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1And1ABusinessErrorUploadService.class);
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("SRFileToRet1And1AExcelConvertion")
	private SRFileToRet1And1AExcelConvertion sRFileToRet1And1AExcelConvertion;
	@Autowired
	@Qualifier("Ret1And1ABusinessValidationChain")
	private Ret1And1ABusinessValidationChain ret1And1ABusinessValidationChain;

	@Autowired
	@Qualifier("Ret1And1AExcelRepository")
	private Ret1And1AExcelRepository ret1And1AExcelRepository;

	@Autowired
	@Qualifier("Ret1And1ARepository")
	private Ret1And1ARepository ret1And1ARepository;

	public void processBusinessData(List<Object[]> ret1And1AList,
			List<Ret1And1AExcelEntity> strucProcessedRecords,
			List<Ret1And1AExcelEntity> strucErrorRecords,
			Gstr1FileStatusEntity updateFileStatus, List<Long> ids) {

		List<Ret1And1AExcelEntity> businessErrorRecords = new ArrayList<>();
		List<Ret1And1AExcelEntity> infoProcessed = new ArrayList<>();
		List<Ret1And1AExcelEntity> businessProcessedRecords = new ArrayList<>();
		//List<Ret1And1AGstnEntity> addFile = new ArrayList<>();

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
		List<Ret1And1AExcelEntity> strucProcessed = sRFileToRet1And1AExcelConvertion
				.convertSRFileToRet1And1A(strucProcessedRecords,
						updateFileStatus);

		for (Ret1And1AExcelEntity ret11A : strucProcessed) {
			List<ProcessingResult> results = ret1And1ABusinessValidationChain
					.validate(ret11A, null);

			if (results != null && results.size() > 0) {
				String invKey = ret11A.getRet1And1AInvkey();
				Long id = ret11A.getId();
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
							/*ret1And1AExcelRepository
									.RET1ErrorUpdateInfo(invKey, id);*/
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
							/*ret1And1AExcelRepository
									.RET1ErrorUpdateInfo(invKey, id);*/
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

		for (Ret1And1AExcelEntity process : strucProcessedRecords) {
			String key = process.getRet1And1AInvkey();
			if (!errorKeys.contains(key)) {
				busiProcessedKeys.add(key);
			}
		}
		if (errorInfo.size() > 0 && !errorInfo.isEmpty()) {
			ret1And1AExcelRepository.RET1ErrorUpdateInfo(errorInfo,ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			ret1And1AExcelRepository.RET1UpdateErrors(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			ret1And1AExcelRepository.RET1UpdateInfo(infoKeys, ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			businessErrorRecords = ret1And1AExcelRepository
					.getAllExcelData(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			infoProcessed = ret1And1AExcelRepository
					.getAllProcessedWithInfoData(infoKeys, ids);
		}
		if (busiProcessedKeys.size() > 0 && !busiProcessedKeys.isEmpty()) {
			businessProcessedRecords = ret1And1AExcelRepository
					.getAllExcelData(busiProcessedKeys, ids);
		}

		LOGGER.error("businessErrorRecords " + businessErrorRecords.size());
		LOGGER.error(
				"businessProcessedRecords " + businessProcessedRecords.size());
		if (businessValErrors.size() > 0 && !businessValErrors.isEmpty()) {
			Map<String, List<Ann1VerticalWebError>> errorMap = 
					verticalWebUploadErrorService
					.convertErrors(businessValErrors, BUSINESS_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService.storedTableRet1And1ARecords(
					businessErrorRecords, errorMap);
		}

		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			Map<String, List<Ann1VerticalWebError>> errorMap = 
					verticalWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedTableRet1And1ARecords(infoProcessed, errorMap);
		}

		if (businessProcessedRecords.size() > 0
				&& !businessProcessedRecords.isEmpty()) {
			List<Ret1And1AEntity> processedOfBusi = 
					sRFileToRet1And1AExcelConvertion
					.convertSRFileToRet1And1ABusinessErrorOut(
							businessProcessedRecords, updateFileStatus);

			for (Ret1And1AEntity retUpdate : processedOfBusi) {
				String retInvKey = retUpdate.getRetInvKey();
				ret1And1ARepository.UpdateSameInvKey(retInvKey);
			}
			/*List<Ret1And1AEntity> saveAll =*/ ret1And1ARepository
					.saveAll(processedOfBusi);
		/*	List<Ret1And1AGstnEntity> sameRecords = sameRecordsProcessed(
					saveAll, updateFileStatus);
			addFile = convertAddWithinSameFile(sameRecords);

			for (Ret1And1AGstnEntity retUpdate : addFile) {
				String retGstnKey = retUpdate.getRetGstnKey();
				ret1And1AGstnRepository.UpdateSameGstnKey(retGstnKey);
			}

			ret1And1AGstnRepository.saveAll(addFile);*/

		}

		totalRecords = (ret1And1AList.size() != 0) ? ret1And1AList.size() : 0;
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
	/*private List<Ret1And1AGstnEntity> convertAddWithinSameFile(
			List<Ret1And1AGstnEntity> processedOfBusi) {

		Map<String, Ret1And1AGstnEntity> map = new HashMap<>();
		for (Ret1And1AGstnEntity summEntity : processedOfBusi) {

			String key = summEntity.getRetGstnKey();
			if (!map.containsKey(key)) {
				map.put(key, summEntity);
				continue;
			}

			Ret1And1AGstnEntity table4Key = map.get(key);
			table4Key.add(summEntity);

		}

		return new ArrayList<>(map.values());
	}

	private List<Ret1And1AGstnEntity> sameRecordsProcessed(
			List<Ret1And1AEntity> saveAll,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Ret1And1AGstnEntity> list = new ArrayList<>();
		for (Ret1And1AEntity arr : saveAll) {
			Ret1And1AGstnEntity obj = new Ret1And1AGstnEntity();

			obj.setRetType(arr.getRetType());
			obj.setSgstin(arr.getSgstin());
			obj.setRetPeriod(arr.getRetPeriod());
			obj.setDerivedRetPeriod(arr.getDerivedRetPeriod());
			obj.setReturnTable(arr.getReturnTable());
			if (arr.getTaxableValue() != null) {
				obj.setTaxableValue(arr.getTaxableValue());
			}
			if (arr.getIgstAmt() != null) {
				obj.setIgstAmt(arr.getIgstAmt());
			}
			if (arr.getSgstAmt() != null) {
				obj.setSgstAmt(arr.getSgstAmt());
			}
			if (arr.getCgstAmt() != null) {
				obj.setCgstAmt(arr.getCgstAmt());
			}
			if (arr.getCessAmt() != null) {
				obj.setCessAmt(arr.getCessAmt());
			}
			if (updateFileStatus != null) {
				obj.setFileId(updateFileStatus.getId());
			}
			obj.setCreatedBy(arr.getCreatedBy());
			obj.setModifiedBy(arr.getModifiedBy());
			obj.setModifiedOn(arr.getModifiedOn());
			obj.setRetGstnKey(arr.getRetGstnKey());
			obj.setInfo(arr.isInfo());
			list.add(obj);
		}
		return list;
	}
*/}
