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
import com.ey.advisory.app.data.entities.client.OutwardTable4Entity;
import com.ey.advisory.app.data.entities.client.OutwardTable4ExcelEntity;
import com.ey.advisory.app.data.repositories.client.OutwardTable4ExcelRepository;
import com.ey.advisory.app.data.repositories.client.OutwardTable4Repository;
import com.ey.advisory.app.services.businessvalidation.table4.Table4BusinessValidationChain;
import com.ey.advisory.app.services.docs.SRFileToOutwardTableDetailsConvertion;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

import lombok.extern.slf4j.Slf4j;

@Component("Table4BusinessErrorUploadService")
@Slf4j
public class Table4BusinessErrorUploadService {
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	/*@Autowired
	@Qualifier("OutwardGstnTable4Repository")
	private OutwardGstnTable4Repository outwardGstnTable4Repository;*/

	@Autowired
	@Qualifier("SRFileToOutwardTableDetailsConvertion")
	private SRFileToOutwardTableDetailsConvertion 
	                                sRFileToOutwardTableDetailsConvertion;
	@Autowired
	@Qualifier("Table4BusinessValidationChain")
	private Table4BusinessValidationChain table4BusinessValidationChain;

	@Autowired
	@Qualifier("OutwardTable4ExcelRepository")
	private OutwardTable4ExcelRepository outwardTable4ExcelRepository;

	@Autowired
	@Qualifier("OutwardTable4Repository")
	private OutwardTable4Repository outwardTable4Repository;

	public void processBusinessData(List<Object[]> table4List,
			List<OutwardTable4ExcelEntity> strucProcessedRecords,
			List<OutwardTable4ExcelEntity> strucErrorRecords,
			Gstr1FileStatusEntity updateFileStatus, List<Long> ids) {

		List<OutwardTable4ExcelEntity> businessErrorRecords = new ArrayList<>();
		List<OutwardTable4ExcelEntity> infoProcessed = new ArrayList<>();
		List<OutwardTable4ExcelEntity> businessProcessedRecords = new ArrayList<>();
	//	List<OutwardTable4GstnEntity> addFile = new ArrayList<>();

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
		Map<String, List<ProcessingResult>> infoWithProcessed = new HashMap<>();
		List<String> errorKeys = new ArrayList<>();
		List<String> infoKeys = new ArrayList<>();
		List<String> errorInfo = new ArrayList<>();
		
		List<ProcessingResult> current =  null;
		List<OutwardTable4ExcelEntity> strucProcessed =
				sRFileToOutwardTableDetailsConvertion.
				convertSRFileToOutwardTable4Processed(strucProcessedRecords, 
						updateFileStatus);
		for (OutwardTable4ExcelEntity table4 : strucProcessed) {
			List<ProcessingResult> results = table4BusinessValidationChain
					.validate(table4, null); 

			if (results != null && results.size() > 0) {
				String invKey = table4.getTable4Invkey();
				Long id = table4.getId();
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
							/*outwardTable4ExcelRepository
							              .table4ErrorUpdateInfo(invKey, id);*/
							errorInfo.add(invKey);
							errorKeys.add(invKey);
							businessValErrors.put(keys, results);
						}
						else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(invKey);
							businessValErrors.put(keys, results);
						} else {
							infoKeys.add(invKey);
							infoWithProcessed.computeIfAbsent(keys,
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
							/*outwardTable4ExcelRepository
							              .table4ErrorUpdateInfo(invKey, id);*/
							businessValErrors.computeIfAbsent(keys,
									k -> new ArrayList<ProcessingResult>())
							.addAll(results);
						}
						else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(invKey);
							businessValErrors.computeIfAbsent(keys,
									k -> new ArrayList<ProcessingResult>())
							.addAll(results);
						} else {
							infoKeys.add(invKey);
							infoWithProcessed.computeIfAbsent(keys,
									k -> new ArrayList<ProcessingResult>())
							.addAll(results);
						}
					}
				}
			}
		}

		
		List<String> busiProcessedKeys = new ArrayList<>();

		for (OutwardTable4ExcelEntity process : strucProcessedRecords) {
			String key = process.getTable4Invkey();
			if (!errorKeys.contains(key)) {
				busiProcessedKeys.add(key);
			}
		}
		if(errorKeys.size() > 0 && !errorKeys.isEmpty()){
			outwardTable4ExcelRepository
                                     .table4UpdateErrors(errorKeys, ids);
		}
		if(errorInfo.size() > 0 && !errorInfo.isEmpty()){
			outwardTable4ExcelRepository.table4ErrorUpdateInfo(errorInfo,
					ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			 outwardTable4ExcelRepository.table4UpdateInfo(infoKeys , ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			businessErrorRecords = outwardTable4ExcelRepository
					.getAllExcelData(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			infoProcessed = outwardTable4ExcelRepository
					.getAllProcessedWithInfoData(infoKeys, ids);
		}
		if (busiProcessedKeys.size() > 0 && !busiProcessedKeys.isEmpty()) {
			businessProcessedRecords = outwardTable4ExcelRepository
					.getAllExcelData(busiProcessedKeys, ids);
		}

		LOGGER.error("businessErrorRecords " + businessErrorRecords.size());
		LOGGER.error(
				"businessProcessedRecords " + businessProcessedRecords.size());
		if (businessValErrors.size() > 0
				&& !businessValErrors.isEmpty()) {
			Map<String, List<Ann1VerticalWebError>> errorMap = 
					verticalWebUploadErrorService
					.convertErrors(businessValErrors, BUSINESS_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedTable4ErrorRecords(businessErrorRecords, errorMap);
		}

		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			Map<String, List<Ann1VerticalWebError>> errorMap = 
					verticalWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedTable4ErrorRecords(infoProcessed, errorMap);
		}

		if (businessProcessedRecords.size() > 0
				&& !businessProcessedRecords.isEmpty()) {
			List<OutwardTable4Entity> processedOfBusi = 
					sRFileToOutwardTableDetailsConvertion
					.convertSRFileToOutwardTable4(businessProcessedRecords,
							updateFileStatus);

			for (OutwardTable4Entity b2cUpdate : processedOfBusi) {
				String table4Key = b2cUpdate.getTable4Invkey();
				outwardTable4Repository.UpdateSameInvKey(table4Key);
			}
			/*List<OutwardTable4Entity> saveAll =*/ outwardTable4Repository
					.saveAll(processedOfBusi);
			/*List<OutwardTable4GstnEntity> sameRecords = sameRecordsProcessed(
					saveAll, updateFileStatus);
			addFile = convertAddWithinSameFile(sameRecords);

			for (OutwardTable4GstnEntity table4Update : addFile) {
				String table4GstnKey = table4Update.getTable4Gstnkey();
				outwardGstnTable4Repository.UpdateSameGstnKey(table4GstnKey);
			}

			outwardGstnTable4Repository.saveAll(addFile);
*/
		}

		totalRecords = (table4List.size() != 0) ? table4List.size() : 0;
		errorRecords = (businessErrorRecords.size() != 0
				|| strucErrorRecords.size() != 0)
						? businessErrorRecords.size() + strucErrorRecords.size()
						: 0;
		processedRecords = totalRecords - errorRecords;
		information = (infoProcessed.size() != 0) ? infoProcessed.size() :0;

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
	/*private List<OutwardTable4GstnEntity> convertAddWithinSameFile(
			List<OutwardTable4GstnEntity> processedOfBusi) {

		Map<String, OutwardTable4GstnEntity> map = new HashMap<>();
		for (OutwardTable4GstnEntity summEntity : processedOfBusi) {

			String key = summEntity.getTable4Gstnkey();
			if (!map.containsKey(key)) {
				map.put(key, summEntity);
				continue;
			}

			OutwardTable4GstnEntity table4Key = map.get(key);
			table4Key.add(summEntity);

		}

		return new ArrayList<>(map.values());
	}*/

	/*private List<OutwardTable4GstnEntity> sameRecordsProcessed(
			List<OutwardTable4Entity> saveAll,
			Gstr1FileStatusEntity updateFileStatus) {
		List<OutwardTable4GstnEntity> list = new ArrayList<>();
		for (OutwardTable4Entity arr : saveAll) {
			OutwardTable4GstnEntity obj = new OutwardTable4GstnEntity();

			obj.setRetType(arr.getRetType());
			obj.setSgstin(arr.getSgstin());
			obj.setRetPeriod(arr.getRetPeriod());
			obj.setDerivedRetPeriod(arr.getDerivedRetPeriod());
			obj.setEcomGstin(arr.getEcomGstin());
			if(arr.getValueOfSupMade() != null){
				obj.setValueOfSupMade(arr.getValueOfSupMade());
			}
			if(arr.getValueOfSupRet() != null){
			obj.setValueOfSupRet(arr.getValueOfSupRet());
			}
			if(arr.getNetValueOfSup() != null){
			obj.setNetValueOfSup(arr.getNetValueOfSup());
			}
			if(arr.getIgstAmt() != null){
			obj.setIgstAmt(arr.getIgstAmt());
			}
			if(arr.getSgstAmt() != null){
			obj.setSgstAmt(arr.getSgstAmt());
			}
			if(arr.getCgstAmt() != null){
			obj.setCgstAmt(arr.getCgstAmt());
			}
			if(arr.getCessAmt() != null){
			obj.setCessAmt(arr.getCessAmt());
			}
			// obj.setFileId(arr.getFileId());
			if (updateFileStatus != null) {
				obj.setFileId(updateFileStatus.getId());
			}
			obj.setCreatedBy(arr.getCreatedBy());
			obj.setModifiedBy(arr.getModifiedBy());
			obj.setModifiedOn(arr.getModifiedOn());
			// obj.setTable4Invkey(arr.getTable4Invkey());
			obj.setTable4Gstnkey(arr.getTable4Gstnkey());
			obj.setInfo(arr.isInfo());
			list.add(obj);
		}
		return list;
	}
*/}
