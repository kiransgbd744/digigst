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
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.MasterErrorEntity;
import com.ey.advisory.admin.data.entities.client.MasterItemEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.admin.data.repositories.client.MasterErrorRepository;
import com.ey.advisory.admin.data.repositories.client.MasterItemRepository;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.MasterDataToItemConverter;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.ItemHeaderCheckService;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.annexure1fileupload.OutwardFileUploadUtil;
import com.ey.advisory.app.services.businessvalidation.master.ItemBusinessValidationChain;
import com.ey.advisory.app.services.strcutvalidation.outward.MasterItemStructuralValidatorChain;
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
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;

/**
 * 
 * @author Anand3.M
 *
 */

@Component("ItemFileArrivalHandler")
public class ItemFileArrivalHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ItemFileArrivalHandler.class);

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("ItemHeaderCheckService")
	private ItemHeaderCheckService itemHeaderCheckService;

	@Autowired
	@Qualifier("MasterDataToItemConverter")
	private MasterDataToItemConverter masterDataToItemConverter;

	@Autowired
	@Qualifier("OutwardFileUploadUtil")
	private OutwardFileUploadUtil outwardFileUploadUtil;

	@Autowired
	@Qualifier("ItemBusinessValidationChain")
	private ItemBusinessValidationChain itemBusinessValidationChain;

	@Autowired
	@Qualifier("masterItemRepository")
	private MasterItemRepository masterItemRepository;

	@Autowired
	@Qualifier("MasterErrorRepository")
	private MasterErrorRepository masterErrorRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("MasterItemStructuralValidatorChain")
	private MasterItemStructuralValidatorChain masterItemStructuralValidatorChain;

	@Autowired
	@Qualifier("ItemFileTansDocErrConvertion")
	private ItemFileTansDocErrConvertion itemFileTansDocErrConvertion;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	public void processItemFile(Message message, AppExecContext context) {

		LOGGER.error("processItemFile");
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'", message.getParamsJson());
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
			Document document = outwardFileUploadUtil.getDocument(openCmisSession, fileName, fileFolder);
			LOGGER.error("document name" + document.getName());
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			LOGGER.error("Exception occured in onboarding Item " + "File Arrival Processor", e);
		}
		TabularDataSourceTraverser traverser = traverserFactory.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(19);

		// Add a dummy row handler that will keep counting the rows.
		@SuppressWarnings("rawtypes")
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
		long stTime = System.currentTimeMillis();
		LOGGER.info("Start Time " + stTime);
		traverser.traverse(inputStream, layout, rowHandler, null);

		Object[] getHeaders = rowHandler.getHeaderRow();

		Pair<Boolean, String> checkHeaderFormat = itemHeaderCheckService.validate(getHeaders);

		LOGGER.error(
				"Header Checker Validations " + checkHeaderFormat.getValue0() + " ," + checkHeaderFormat.getValue1());

		List<Object[]> itemList = ((FileUploadDocRowHandler<?>) rowHandler).getFileUploadList();
		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository.getFileName(fileName);

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		LOGGER.error("businessProcessedRecords ");
		List<Object[]> struErrRecords = new ArrayList<>();
		List<Object[]> struProRecords = new ArrayList<>();
		List<Object[]> businessErrRecords = new ArrayList<>();
		List<Object[]> businessProRecords = new ArrayList<>();

		if (checkHeaderFormat.getValue0()) {

			Map<String, List<ProcessingResult>> processingResults = masterItemStructuralValidatorChain
					.validation(itemList);
			List<String> structuralValKeys = new ArrayList<>();
			for (String k : processingResults.keySet()) {
				structuralValKeys.add(k);
			}
			for (Object[] obj : itemList) {
				String itemKey = masterDataToItemConverter.getItemValues(obj);
				if (!structuralValKeys.contains(itemKey)) {
					struProRecords.add(obj);
				} else {
					struErrRecords.add(obj);
				}
			}

			filterTheDuplicatesInFile(struProRecords, struErrRecords);

			if (struProRecords != null && !struProRecords.isEmpty()) {
				Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
				List<MasterItemEntity> ite = masterDataToItemConverter.convertItem(struProRecords, updateFileStatus,
						entityId);
				for (MasterItemEntity item : ite) {
					List<ProcessingResult> results = itemBusinessValidationChain.validate(item, null);
					if (results != null && results.size() > 0) {
						String itemKey = item.getItemKey();
						List<ProcessingResult> current = businessValErrors.get(itemKey);
						if (current == null) {
							current = new ArrayList<>();
							businessValErrors.put(itemKey, results);
						} else {
							businessValErrors.put(itemKey, results);
							current.addAll(results);
						}
					}
				}
				List<String> errorKeys = new ArrayList<>();
				for (String keys : businessValErrors.keySet()) {
					errorKeys.add(keys);
				}
				for (Object[] itemEntity : struProRecords) {
					String custKeys = masterDataToItemConverter.getItemValues(itemEntity);
					if (!errorKeys.contains(custKeys)) {
						businessProRecords.add(itemEntity);
					} else {
						businessErrRecords.add(itemEntity);
					}
				}
				if (!businessErrRecords.isEmpty() && businessErrRecords.size() > 0) {
					List<MasterErrorEntity> itemErr = itemFileTansDocErrConvertion
							.convertItemFileTransDoc(businessErrRecords, updateFileStatus, businessValErrors, entityId);
					masterErrorRepository.saveAll(itemErr);
				}
				if (businessProRecords.size() > 0 && !businessProRecords.isEmpty()) {
					List<MasterItemEntity> itemProcess = masterDataToItemConverter.convertItem(businessProRecords,
							updateFileStatus, entityId);
					List<MasterItemEntity> overrideRecords = overrideRecords(itemProcess);

					masterItemRepository.updateAllToDelete(entityId);

					masterItemRepository.saveAll(overrideRecords);

				}
			}
			if (struErrRecords.size() > 0 && !struErrRecords.isEmpty()) {
				List<MasterErrorEntity> prod = itemFileTansDocErrConvertion.convertItemFileTransDoc(struErrRecords,
						updateFileStatus, processingResults, entityId);
				masterErrorRepository.saveAll(prod);
			}
		} else {
			LOGGER.error("Header Checker Failed ");
			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
			gstr1FileStatusRepository.save(updateFileStatus);

		}

	}

	private void filterTheDuplicatesInFile(List<Object[]> struProRecords, List<Object[]> struErrRecords) {
		Map<String, List<Object[]>> filteredMap = new HashMap<>();

		struProRecords.forEach(obj -> {
			String itemKey = masterDataToItemConverter.getItemValues(obj);

			if (filteredMap.containsKey(itemKey)) {
				List<Object[]> objArray = filteredMap.get(itemKey);
				objArray.add(obj);
				filteredMap.put(itemKey, objArray);
			} else {
				List<Object[]> objArray = new ArrayList<>();
				objArray.add(obj);
				filteredMap.put(itemKey, objArray);
			}
		});

		struProRecords.clear();
		filteredMap.keySet().forEach(itemKey -> {
			List<Object[]> objArray = filteredMap.get(itemKey);
			if (objArray.size() > 1) {
				struErrRecords.addAll(objArray);
			} else {
				struProRecords.addAll(objArray);
			}
		});
	}

	private List<MasterItemEntity> overrideRecords(List<MasterItemEntity> itemProcess) {
		Map<String, MasterItemEntity> map = new HashMap<>();
		for (MasterItemEntity itemMaster : itemProcess) {
			String key = itemMaster.getItemKey();
			if (!map.containsKey(key)) {
				map.put(key, itemMaster);
			}
			MasterItemEntity itemSame = map.get(key);
			itemSame.add(itemMaster);
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
		FileArrivalMsgDto msg = GsonUtil.newSAPGsonInstance().fromJson(message.getParamsJson(),
				FileArrivalMsgDto.class);

		if (msg.getFilePath() == null || msg.getFileName() == null) {
			String errMsg = "Invalid Path/FileName received in msg";
			String logMsg = String.format("Values received are -> " + "Path: '%s', FileName: '%s', GroupCode: '%s'",
					msg.getFilePath(), msg.getFileName(), message.getGroupCode());
			LOGGER.error(String.format("%s %s", msg, logMsg));
			throw new AppException(errMsg);
		}

		if (msg.getEntityId() == null) {
			String errMsg = "Entity Id missing to update in master item table in msg";
			String logMsg = String.format(
					"Values received are -> " + "Path: '%s', FileName: '%s', GroupCode: '%s',  entityId: '%s'",
					msg.getFilePath(), msg.getFileName(), message.getGroupCode(), msg.getEntityId());
			LOGGER.error(String.format("%s %s", msg, logMsg));
			throw new AppException(errMsg);
		}

		// Log the extracted info from the message.
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format("Message Details are -> Path: '%s', " + "File Name: '%s', Group Code: '%s'",
					msg.getFilePath(), msg.getFileName(), message.getGroupCode());
			LOGGER.error(logMsg);
		}
		return msg;
	}
}
