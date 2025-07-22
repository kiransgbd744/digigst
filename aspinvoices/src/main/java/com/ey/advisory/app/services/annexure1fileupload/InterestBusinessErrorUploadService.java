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
import com.ey.advisory.app.data.entities.client.InterestAndLateFeeEntity;
import com.ey.advisory.app.data.entities.client.InterestExcelEntity;
import com.ey.advisory.app.data.repositories.client.InterestAndLateFeeRepository;
import com.ey.advisory.app.data.repositories.client.InterestExcelRepository;
import com.ey.advisory.app.services.businessvalidation.interestandlatefee.InterestBusinessValidationChain;
import com.ey.advisory.app.services.docs.SRFileToIntersetExcelConvertion;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

@Component("InterestBusinessErrorUploadService")
public class InterestBusinessErrorUploadService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(InterestBusinessErrorUploadService.class);
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("SRFileToIntersetExcelConvertion")
	private SRFileToIntersetExcelConvertion sRFileToIntersetExcelConvertion;

	@Autowired
	@Qualifier("InterestBusinessValidationChain")
	private InterestBusinessValidationChain interestBusinessValidationChain;

	@Autowired
	@Qualifier("InterestExcelRepository")
	private InterestExcelRepository interestExcelRepository;

	@Autowired
	@Qualifier("InterestAndLateFeeRepository")
	private InterestAndLateFeeRepository interestAndLateFeeRepository;

	public void processBusinessData(List<Object[]> interestList,
			List<InterestExcelEntity> strucProcessedRecords,
			List<InterestExcelEntity> strucErrorRecords,
			Gstr1FileStatusEntity updateFileStatus, List<Long> ids) {

		List<InterestExcelEntity> businessErrorRecords = new ArrayList<>();
		List<InterestExcelEntity> infoProcessed = new ArrayList<>();
		List<InterestExcelEntity> businessProcessedRecords = new ArrayList<>();
		//List<InterestAndLateFeeGstnEntity> addFile = new ArrayList<>();

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

		for (InterestExcelEntity interest : strucProcessedRecords) {
			List<ProcessingResult> results = interestBusinessValidationChain
					.validate(interest, null); 

			if (results != null && results.size() > 0) {
				String invKey = interest.getInterestInvKey();
				Long id = interest.getId();
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
							/*interestExcelRepository
							              .INTERESTErrorUpdateInfo(invKey, id);*/
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
							/*interestExcelRepository
							              .INTERESTErrorUpdateInfo(invKey, id);*/
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

		for (InterestExcelEntity process : strucProcessedRecords) {
			String key = process.getInterestInvKey();
			if (!errorKeys.contains(key)) {
				busiProcessedKeys.add(key);
			}
		}
		if (errorInfo.size() > 0 && !errorInfo.isEmpty()) {
			interestExcelRepository.INTERESTErrorUpdateInfo(errorInfo,ids);
		}
		if(errorKeys.size() > 0 && !errorKeys.isEmpty()){
			interestExcelRepository
                                     .INTERESTUpdateErrors(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			 interestExcelRepository.INTERESTUpdateInfo(infoKeys , ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			businessErrorRecords = interestExcelRepository
					.getAllExcelData(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			infoProcessed = interestExcelRepository
					.getAllProcessedWithInfoData(infoKeys, ids);
		}
		if (busiProcessedKeys.size() > 0 && !busiProcessedKeys.isEmpty()) {
			businessProcessedRecords = interestExcelRepository
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
					.storedINTERESTErrorRecords(businessErrorRecords, errorMap);
		}

		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			Map<String, List<Ann1VerticalWebError>> errorMap = 
					verticalWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedINTERESTErrorRecords(infoProcessed, errorMap);
		}

		if (businessProcessedRecords.size() > 0
				&& !businessProcessedRecords.isEmpty()) {
			List<InterestAndLateFeeEntity> processedOfBusi = 
					sRFileToIntersetExcelConvertion
					.convertSRFileToIntersetAndLateFee(businessProcessedRecords,
							updateFileStatus);

			for (InterestAndLateFeeEntity interestUpdate : processedOfBusi) {
				String interestInvKey = interestUpdate.getInterestInvKey();
				interestAndLateFeeRepository.UpdateSameInvKey(interestInvKey);
			}
			/*List<InterestAndLateFeeEntity> saveAll = */interestAndLateFeeRepository
					.saveAll(processedOfBusi);
		/*	List<InterestAndLateFeeGstnEntity> sameRecords = sameRecordsProcessed( 
					saveAll, updateFileStatus);
			addFile = convertAddWithinSameFile(sameRecords);

			for (InterestAndLateFeeGstnEntity interestUpdate : addFile) {
				String interestGstnKey = interestUpdate.getInterestGstnKey();
				interestAndLateFeeGstnRepository.UpdateSameGstnKey(interestGstnKey);
			}

			interestAndLateFeeGstnRepository.saveAll(addFile); */

		}

		totalRecords = (interestList.size() != 0) ? interestList.size() : 0;
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
	/*private List<InterestAndLateFeeGstnEntity> convertAddWithinSameFile(
			List<InterestAndLateFeeGstnEntity> processedOfBusi) {

		Map<String, InterestAndLateFeeGstnEntity> map = new HashMap<>();
		for (InterestAndLateFeeGstnEntity summEntity : processedOfBusi) {

			String key = summEntity.getInterestGstnKey();
			if (!map.containsKey(key)) {
				map.put(key, summEntity);
				continue;
			}

			InterestAndLateFeeGstnEntity interestAndLate = map.get(key);
			interestAndLate.add(summEntity);

		}

		return new ArrayList<>(map.values());
	}

	private List<InterestAndLateFeeGstnEntity> sameRecordsProcessed(
			List<InterestAndLateFeeEntity> saveAll,
			Gstr1FileStatusEntity updateFileStatus) {
		List<InterestAndLateFeeGstnEntity> list = new ArrayList<>();
		for (InterestAndLateFeeEntity arr : saveAll) {
			InterestAndLateFeeGstnEntity obj = 
					new InterestAndLateFeeGstnEntity();

			obj.setSNo(Integer.valueOf(arr.getSNo()));
			obj.setReturnType(arr.getReturnType());
			obj.setSgstin(arr.getSgstin());
			obj.setRetPeriod(arr.getRetPeriod());
			obj.setDerivedRetPeriod(arr.getDerivedRetPeriod());
			obj.setReturnTable(arr.getReturnTable());
			if(arr.getInterestIgstAmt() != null){
				obj.setInterestIgstAmt(arr.getInterestIgstAmt());
			}
			if(arr.getInterestCgstAmt() != null){
			obj.setInterestCgstAmt(arr.getInterestCgstAmt());
			}
			if(arr.getInterestSgstAmt() != null){
			obj.setInterestSgstAmt(arr.getInterestSgstAmt());
			}
			if(arr.getInterestCessAmt() != null){
			obj.setInterestCessAmt(arr.getInterestCessAmt());
			}
			if(arr.getLateSgstAmt() != null){
			obj.setLateSgstAmt(arr.getLateSgstAmt());
			}
			if(arr.getLateCgstAmt() != null){
			obj.setLateSgstAmt(arr.getLateCgstAmt());
			}
			if (updateFileStatus != null) {
				obj.setFileId(updateFileStatus.getId());
			}
			obj.setCreatedBy(arr.getCreatedBy());
			obj.setModifiedBy(arr.getModifiedBy());
			obj.setModifiedOn(arr.getModifiedOn());
			obj.setInterestGstnKey(arr.getInterestGstnKey());
			obj.setInfo(arr.isInfo());
			list.add(obj);
		}
		return list;
	}
*/}
