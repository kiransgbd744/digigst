package com.ey.advisory.app.services.docs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.MasterCustomerEntity;
import com.ey.advisory.admin.data.entities.client.MasterErrorEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.admin.data.repositories.client.MasterCustomerRepository;
import com.ey.advisory.admin.data.repositories.client.MasterErrorRepository;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.MasterDataToCustomerConverter;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.MasterDataToCustomerErrorConverter;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.MasterHeaderCheckService;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.annexure1fileupload.OutwardFileUploadUtil;
import com.ey.advisory.app.services.businessvalidation.master.CustomerBusinessValidationChain;
import com.ey.advisory.app.services.structuralvalidation.master.CustomerStructValidationChain;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.JobStatusConstants;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("MasterCustomerFileArrivalHandler")
public class MasterCustomerFileArrivalHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MasterCustomerFileArrivalHandler.class);

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("CustomerHeaderCheckService")
	private MasterHeaderCheckService masterHeaderCheckService;

	@Autowired
	@Qualifier("MasterDataToCustomerConverter")
	private MasterDataToCustomerConverter masterDataToCustomerConverter;

	@Autowired
	@Qualifier("OutwardFileUploadUtil")
	private OutwardFileUploadUtil outwardFileUploadUtil;

	@Autowired
	@Qualifier("CustomerBusinessValidationChain")
	private CustomerBusinessValidationChain customerBusinessValidationChain;

	@Autowired
	@Qualifier("masterCustomerRepository")
	private MasterCustomerRepository masterCustomerRepository;

	@Autowired
	@Qualifier("MasterErrorRepository")
	private MasterErrorRepository masterErrorRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("CustomerStructValidationChain")
	private CustomerStructValidationChain customerStructValidationChain;

	@Autowired
	@Qualifier("MasterDataToCustomerErrorConverter")
	private MasterDataToCustomerErrorConverter masterDataToCustomerErrorConverter;

	@Transactional(value = "clientTransactionManager")
	public void processCusterData(Message message, AppExecContext context) {
		
		LOGGER.error("Enter customer ");
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'",
				message.getParamsJson());
		LOGGER.error(fileArrivalMsg);
		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();
		Long entityId = msg.getEntityId();

		InputStream inputStream = null;

		try {
			Session openCmisSession = outwardFileUploadUtil.openCmisSession();
			LOGGER.error("openCmisSession " + openCmisSession);
			Document document = outwardFileUploadUtil
					.getDocument(openCmisSession, fileName, fileFolder);
			LOGGER.error("document name" + document.getName());
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			LOGGER.error("Exception occured in onboarding Customer "
					+ "File Arrival Processor", e);
		}
		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(8);
		// Add a dummy row handler that will keep counting the rows.
		@SuppressWarnings("rawtypes")
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
		long stTime = System.currentTimeMillis();
		LOGGER.info("Start Time " + stTime);
		traverser.traverse(inputStream, layout, rowHandler, null);
		List<Object[]> customerList = ((FileUploadDocRowHandler<?>) rowHandler)
				.getFileUploadList();
		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
				                                      .getFileName(fileName);
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		LOGGER.error("businessProcessedRecords ");
		List<Object[]> struErrRecords = new ArrayList<>();
		List<Object[]> struProRecords = new ArrayList<>();
		List<Object[]> businessErrRecords = new ArrayList<>();
		List<Object[]> businessProRecords = new ArrayList<>();

		Map<String, List<ProcessingResult>> processingResults = 
				customerStructValidationChain
				.validation(customerList);
		List<String> structuralValKeys = new ArrayList<>();
		for (String k : processingResults.keySet()) {
			structuralValKeys.add(k);
		}
		for (Object[] obj : customerList) {
			String custKey = masterDataToCustomerConverter
					.getCustomerValues(obj);
			if (!structuralValKeys.contains(custKey)) {
				struProRecords.add(obj);
			} else {
				struErrRecords.add(obj);
			}
		}

		if (struErrRecords.size() > 0 && !struErrRecords.isEmpty()) {
			List<MasterErrorEntity> cust = masterDataToCustomerErrorConverter
					.convertCustomer(struErrRecords, updateFileStatus,
							processingResults, entityId);
			masterErrorRepository.saveAll(cust);
		}

		if (struProRecords != null && !struProRecords.isEmpty()) {
			Map<String, List<ProcessingResult>> businessValErrors = 
					new HashMap<>();
			List<MasterCustomerEntity> cust = masterDataToCustomerConverter
					.convertCustomer(struProRecords, updateFileStatus,
							entityId);
			for (MasterCustomerEntity customer : cust) {
				List<ProcessingResult> results = customerBusinessValidationChain
						.validate(customer, null);
				if (results != null && results.size() > 0) {
					String custKey = customer.getCustKey();
					List<ProcessingResult> current = businessValErrors
							.get(custKey);
					if (current == null) {
						current = new ArrayList<>();
						businessValErrors.put(custKey, results);
					} else {
						businessValErrors.
						computeIfAbsent(custKey, k -> 
						new ArrayList<ProcessingResult>()).addAll(results);
					}
				}
			}
			List<String> errorKeys = new ArrayList<>();
			for (String keys : businessValErrors.keySet()) {
				errorKeys.add(keys);
			}
			for (Object[] customerEntity : struProRecords) {
				String custKeys = masterDataToCustomerConverter
						.getCustomerValues(customerEntity);
				if (!errorKeys.contains(custKeys)) {
					businessProRecords.add(customerEntity);
				} else {
					businessErrRecords.add(customerEntity);
				}
			}
			if (!businessErrRecords.isEmpty()
					&& businessErrRecords.size() > 0) {
				List<MasterErrorEntity> custErr = 
						masterDataToCustomerErrorConverter
						.convertCustomer(businessErrRecords, updateFileStatus,
								businessValErrors, entityId);
				masterErrorRepository.saveAll(custErr);
			}
			List<MasterCustomerEntity> withoutDuplicateProcess = 
					new ArrayList<>();
			List<MasterCustomerEntity> withDuplicateProcess = 
					new ArrayList<>();
			if (businessProRecords.size() > 0
					&& !businessProRecords.isEmpty()) {
				List<MasterCustomerEntity> custProcess = 
						masterDataToCustomerConverter
						.convertCustomer(businessProRecords, updateFileStatus,
								entityId);
				List<String> duplicateKeys = new ArrayList<>();
				List<String> nonDuplicateKeys = new ArrayList<>();
				
				Map<String, MasterCustomerEntity> map = new HashMap<>();
				for (MasterCustomerEntity custMaster : custProcess) {
					String key = custMaster.getCustKey();
					if (!map.containsKey(key)) {
						map.put(key, custMaster);
						nonDuplicateKeys.add(key);
						continue;
					}
					duplicateKeys.add(key);
				}
				for (MasterCustomerEntity custMaster : custProcess) {
					if(duplicateKeys.contains(custMaster.getCustKey())){
						withDuplicateProcess.add(custMaster);
					}
					else{
						withoutDuplicateProcess.add(custMaster);
					}
				}
				List<MasterErrorEntity> custErr = 
						masterDataToCustomerErrorConverter
				.convertDuplicateCheckCustomer(withDuplicateProcess, 
						updateFileStatus,businessValErrors, entityId);
				masterErrorRepository.saveAll(custErr);

				masterCustomerRepository.updateAllToDelete(entityId);

				masterCustomerRepository.saveAll(withoutDuplicateProcess);

			}
			totalRecords = (customerList.size() != 0) ? customerList.size() : 0;
			errorRecords = (businessErrRecords.size() != 0
					|| struErrRecords.size() != 0 || 
					withDuplicateProcess.size() !=0)
							? businessErrRecords.size() + struErrRecords.size() 
							+ withDuplicateProcess.size()
							: 0;
			processedRecords = totalRecords - errorRecords;
			information = 0;

			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			updateFileStatus.setFileStatus(JobStatusConstants.PROCESSED);
			gstr1FileStatusRepository.save(updateFileStatus);

		}
	}

	private List<MasterCustomerEntity> overrideRecords(
			List<MasterCustomerEntity> custProcess) {
		Map<String, MasterCustomerEntity> map = new HashMap<>();
		for (MasterCustomerEntity custMaster : custProcess) {
			String key = custMaster.getCustKey();
			if (!map.containsKey(key)) {
				map.put(key, custMaster);
			}
			MasterCustomerEntity custSame = map.get(key);
			custSame.add(custMaster);
		}

		return new ArrayList<>(map.values());
	}

	/**
	 * Validate the Input JSON to check if 'path', 'fileName' and Group Code are
	 * actually present within the JSON. If not, throw an EWB Exception so that
	 * the file processing status is marked as 'Failed'. Otherwise, extract
	 * these values and return them.
	 * 
	 * @param jsonMessage
	 *            The 'Message' instance passed by the AsyncExecution framework
	 * 
	 */
	private FileArrivalMsgDto extractAndValidateMessage(Message message) {
		FileArrivalMsgDto msg = GsonUtil.newSAPGsonInstance()
				.fromJson(message.getParamsJson(), FileArrivalMsgDto.class);

		if (msg.getFilePath() == null || msg.getFileName() == null) {
			String errMsg = "Invalid Path/FileName received in msg";
			String logMsg = String.format(
					"Values received are -> "
							+ "Path: '%s', FileName: '%s', GroupCode: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.error(String.format("%s %s", msg, logMsg));
			throw new AppException(errMsg);
		}

		// Log the extracted info from the message.
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Message Details are -> Path: '%s', "
							+ "File Name: '%s', Group Code: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.error(logMsg);
		}
		return msg;
	}
}
