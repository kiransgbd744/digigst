package com.ey.advisory.app.services.docs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.MasterErrorEntity;
import com.ey.advisory.admin.data.entities.client.MasterVendorEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.admin.data.repositories.client.MasterErrorRepository;
import com.ey.advisory.admin.data.repositories.client.MasterVendorRepository;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.VendorErrorConverter;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.VendorFileConversion;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.annexure1fileupload.OutwardFileUploadUtil;
import com.ey.advisory.app.services.businessvalidation.master.VendorBusinessValidationChain;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1HeaderCheckService;
import com.ey.advisory.app.services.strcutvalidation.outward.VendorStructuralValidatorChain;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
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
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;

/**
 * 
 * @author Sasidhar Reddy
 *
 */
@Service("VendorFileArrivalHandler")
public class VendorFileArrivalHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(VendorFileArrivalHandler.class);

	@Autowired
	@Qualifier("OutwardFileUploadUtil")
	private OutwardFileUploadUtil outwardFileUploadUtil;

	@Autowired
	@Qualifier("VendorErrorConverter")
	private VendorErrorConverter vendorErrorConverter;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("VendorFileConversion")
	private VendorFileConversion vendorFileConversion;

	@Autowired
	@Qualifier("masterVendorRepository")
	private MasterVendorRepository vendorRepository;

	@Autowired
	@Qualifier("VendorStructuralValidatorChain")
	private VendorStructuralValidatorChain vendorStructuralValidatorChain;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("VendorFileHeaderCheckService")
	private Gstr1HeaderCheckService gstr1HeaderCheckService;

	@Autowired
	@Qualifier("MasterErrorRepository")
	private MasterErrorRepository masterErrorRepository;

	@Autowired
	@Qualifier("VendorBusinessValidationChain")
	private VendorBusinessValidationChain vendorBsnsValidationChain;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	/**
	 * 
	 * @param message
	 * @param context
	 */
	@SuppressWarnings("rawtypes")
	public void processVendorFile(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("VendorFileArrivalHandler processVendorFile Begining");
		}
		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'",
				message.getParamsJson());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(fileArrivalMsg);
		}

		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
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
			LOGGER.error("Exception occured in vendor file upload "
					+ "File Arrival Processor", e);
		}

		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(7);

		// Add a dummy row handler that will keep counting the rows.
		long stTime = System.currentTimeMillis();
		LOGGER.info("Start Time " + stTime);
		traverser.traverse(inputStream, layout, rowHandler, null);

		Object[] getHeaders = rowHandler.getHeaderRow();

		Pair<Boolean, String> checkHeaderFormat = gstr1HeaderCheckService
				.validate(getHeaders);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Header Checker Validations "
					+ checkHeaderFormat.getValue0() + " ,"
					+ checkHeaderFormat.getValue1());
		}

		List<Object[]> vendorList = ((FileUploadDocRowHandler<?>) rowHandler)
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

		/* Structural Validations */
		if (checkHeaderFormat.getValue0()) {
			Map<String, List<ProcessingResult>> processingResults = vendorStructuralValidatorChain
					.validation(vendorList);
			List<String> structuralValKeys = new ArrayList<>();
			for (String k : processingResults.keySet()) {
				structuralValKeys.add(k);
			}
			for (Object[] obj : vendorList) {
				String vendorKey = vendorFileConversion.getVendorValues(obj);
				if (!structuralValKeys.contains(vendorKey)) {
					struProRecords.add(obj);
				} else {
					struErrRecords.add(obj);
				}
			}
			if (struErrRecords.size() > 0 && !struErrRecords.isEmpty()) {
				List<MasterErrorEntity> vendErr = vendorErrorConverter
						.convertVendor(businessErrRecords, updateFileStatus,
								processingResults, entityId);
				masterErrorRepository.saveAll(vendErr);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("StruProRecords details -> ",
						struProRecords + "StruErrRecords details -> ",
						struErrRecords);
			}
			if (struProRecords != null && !struProRecords.isEmpty()) {
				Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
				List<MasterVendorEntity> List = vendorFileConversion
						.convertVendorFile(struProRecords, updateFileStatus,
								entityId);
				for (MasterVendorEntity vendor : List) {
					List<ProcessingResult> results = vendorBsnsValidationChain
							.validate(vendor, null);
					if (results != null && results.size() > 0) {
						String vendorKey = vendor.getVendorKey();
						List<ProcessingResult> current = businessValErrors
								.get(vendorKey);
						if (current == null) {
							current = new ArrayList<>();
							businessValErrors.put(vendorKey, results);
						} else {
							businessValErrors.put(vendorKey, results);
							current.addAll(results);
						}
					}
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("businessValErrors details -> ",
							businessValErrors);
				}
				List<String> errorKeys = new ArrayList<>();
				for (String keys : businessValErrors.keySet()) {
					errorKeys.add(keys);
				}
				for (Object[] vendorEntity : struProRecords) {
					String vendorKeys = vendorFileConversion
							.getVendorValues(vendorEntity);
					if (!errorKeys.contains(vendorKeys)) {
						businessProRecords.add(vendorEntity);
					} else {
						businessErrRecords.add(vendorEntity);
					}
				}
				if (!businessErrRecords.isEmpty()
						&& businessErrRecords.size() > 0) {
					List<MasterErrorEntity> vendorErr = vendorErrorConverter
							.convertVendor(businessErrRecords, updateFileStatus,
									businessValErrors, entityId);
					masterErrorRepository.saveAll(vendorErr);
				}
				List<MasterVendorEntity> withoutDuplicateProcess = new ArrayList<>();
				List<MasterVendorEntity> withDuplicateProcess = new ArrayList<>();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("businessProRecords details -> ",
							businessProRecords + "businessErrRecords details",
							businessErrRecords);
				}
				if (businessProRecords.size() > 0
						&& !businessProRecords.isEmpty()) {
					List<MasterVendorEntity> vendorProcess = vendorFileConversion
							.convertVendorFile(businessProRecords,
									updateFileStatus, entityId);
					List<String> duplicateKeys = new ArrayList<>();
					List<String> nonDuplicateKeys = new ArrayList<>();
					Map<String, MasterVendorEntity> map = new HashMap<>();
					for (MasterVendorEntity vendMaster : vendorProcess) {
						String key = vendMaster.getVendorKey();
						if (!map.containsKey(key)) {
							map.put(key, vendMaster);
							nonDuplicateKeys.add(key);
							continue;
						}
						duplicateKeys.add(key);
					}
					for (MasterVendorEntity vendMaster : vendorProcess) {
						if (duplicateKeys.contains(vendMaster.getVendorKey())) {
							withDuplicateProcess.add(vendMaster);
						} else {
							withoutDuplicateProcess.add(vendMaster);
						}
					}
					List<MasterErrorEntity> vendErr = vendorErrorConverter
							.convertDuplicateCheckCustomer(withDuplicateProcess,
									updateFileStatus, businessValErrors,
									entityId);
					masterErrorRepository.saveAll(vendErr);

					vendorRepository.updateAllToDelete(entityId);
					vendorRepository.saveAll(withoutDuplicateProcess);
					LOGGER.debug("overrideRecords details -> ", vendorProcess);
				}

				totalRecords = (vendorList.size() != 0) ? vendorList.size() : 0;
				errorRecords = (businessErrRecords.size() != 0
						|| struErrRecords.size() != 0
						|| withDuplicateProcess.size() != 0)
								? businessErrRecords.size()
										+ struErrRecords.size()
										+ withDuplicateProcess.size()
								: 0;
				processedRecords = totalRecords - errorRecords;
				information = 0;

				updateFileStatus.setTotal(totalRecords);
				updateFileStatus.setProcessed(processedRecords);
				updateFileStatus.setError(errorRecords);
				updateFileStatus.setInformation(information);
				updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
				gstr1FileStatusRepository.save(updateFileStatus);

			}
		}
	}

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

		if (msg.getEntityId() == null) {
			String errMsg = "Entity Id missing to update in master vendor table in msg";
			String logMsg = String.format(
					"Values received are -> "
							+ "Path: '%s', FileName: '%s', GroupCode: '%s',  entityId: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode(), msg.getEntityId());
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