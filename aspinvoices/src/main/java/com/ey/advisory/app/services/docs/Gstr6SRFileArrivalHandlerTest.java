package com.ey.advisory.app.services.docs;

import static com.ey.advisory.common.GSTConstants.STRUCTURAL_VALIDATIONS;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.Gstr6DistrbtnHeaderCheckService;
import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.app.data.entities.client.Gstr6VerticalWebError;
import com.ey.advisory.app.data.repositories.client.Ann1VerticalWebErrorRepo;
import com.ey.advisory.app.data.repositories.client.Anx1OutWardErrHeaderRepository;
import com.ey.advisory.app.data.repositories.client.DocErrorRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6DistributionExcelRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.annexure1fileupload.DistriButionCanService;
import com.ey.advisory.app.services.annexure1fileupload.Gstr6BusinessErrorUploadService;
import com.ey.advisory.app.services.annexure1fileupload.Gstr6VerticalWebUploadErrorService;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1HeaderCheckService;
import com.ey.advisory.app.services.strcutvalidation.outward.Anx1OutwardDocStructuralValidatorChain;
import com.ey.advisory.app.services.structuralvalidation.gstr6.Gstr6StructuralValidatorChain;
import com.ey.advisory.app.services.validation.sales.SalesDocRulesValidatorService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Lists;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Gstr6SRFileArrivalHandlerTest")
public class Gstr6SRFileArrivalHandlerTest {


	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6SRFileArrivalHandlerTest.class);
	
	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	
	@Autowired
	@Qualifier("SalesDocRulesValidatorService")
	private SalesDocRulesValidatorService salesDocRulesValidatorService;
	@Autowired
	@Qualifier("SRFileToOutwardTransDocConvertion")
	private SRFileToOutwardTransDocConvertion srFileToOutwardTransDocConvertion;

	@Autowired
	@Qualifier("SRFileToOutwardTransDocErrConvertion")
	private SRFileToOutwardTransDocErrConvertion srFileToOutwardTransDocErrConvertion;

	
	@Autowired
	@Qualifier("DocErrorRepository")
	private DocErrorRepository docErrorRepository;

	@Autowired
	@Qualifier("DefaultDocSaveService")
	private DefaultDocSaveService srFileSaveService;
	
	@Autowired
	@Qualifier("Gstr6DistrbtnHeaderCheckService")
	private Gstr6DistrbtnHeaderCheckService distrbtnHeaderCheckService;
	
	@Autowired
	@Qualifier("Gstr6DistrbtnExcelConvertion")
	Gstr6DistrbtnExcelConvertion convertion;

	
	@Autowired
	@Qualifier("DistriButionCanServiceImpl")
	private DistriButionCanService canService;
	
	/*
	 * @Autowired
	 * 
	 * @Qualifier("OutwardDocStructuralValidator") private
	 * OutwardDocStructuralValidator outwardDocStructuralValidator;
	 */
	@Autowired
	@Qualifier("Anx1OutwardDocStructuralValidatorChain")
	private Anx1OutwardDocStructuralValidatorChain outwardDocStructuralValidator;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("SRFileHeaderCheckService")
	private Gstr1HeaderCheckService gstr1HeaderCheckService;

	@Autowired
	@Qualifier("anx1OutWardErrHeaderRepository")
	private Anx1OutWardErrHeaderRepository anx1OutWardErrHeaderRepository;

	@Autowired
	@Qualifier("DefaultDocErrorSaveService")
	private DefaultDocErrorSaveService defaultDocErrorSaveService;

	@Autowired
	@Qualifier("Gstr6StructuralValidatorChain")
	private Gstr6StructuralValidatorChain validatorChain;
	
	/*@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;
*/
	@Autowired
	@Qualifier("Gstr6VerticalWebUploadErrorService")
	private Gstr6VerticalWebUploadErrorService gstr6VerticalWebUploadErrorService;

	
	/*@Autowired
	@Qualifier("B2cStructValidationChain")
	private B2cStructValidationChain b2cStructValidationChain;
	*/
	
	@Autowired
	@Qualifier("Gstr6StructuralValidatorChain")
	private Gstr6StructuralValidatorChain gstr6StructValidationChain;
/*	@Autowired
	@Qualifier("Gstr6DstrbtnBusinessValidationChain")
	private Gstr6DstrbtnBusinessValidationChain productBusinessValidationChain;
*/
	@Autowired
	@Qualifier("Ann1VerticalWebErrorRepo")
	private Ann1VerticalWebErrorRepo ann1VerticalWebErrorRepo;
	
/*	@Autowired
	@Qualifier("Gstr6VerticalWebErrorRepo")
	Gstr6VerticalWebErrorRepo gstr6VerticalWebErrorRepo;
*/
	@Autowired
	@Qualifier("Gstr6DistributionExcelRepository")
	private Gstr6DistributionExcelRepository gstr6DistrbtnExcelRepository;
	
	@Autowired
	@Qualifier("Gstr6DistrbtnExcelConvertion")
	private Gstr6DistrbtnExcelConvertion masterDataToProductConverter;

	/*@Autowired
	@Qualifier("B2cBusinessErrorUploadService")
	private B2cBusinessErrorUploadService b2cBusinessErrorUploadService;
	*/
	@Autowired
	@Qualifier("Gstr6BusinessErrorUploadService")
	Gstr6BusinessErrorUploadService gstr6BusinessErrorUploadService;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	
	
	/**
	 * 
	 * @param message
	 * @param context
	 */
	@SuppressWarnings("rawtypes")
	public void processProductFile(Message message, AppExecContext context) {
		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'",
				message.getParamsJson());
		LOGGER.debug(fileArrivalMsg);

		DocumentKeyBuilder documentKeyBuilder = new DocumentKeyBuilder();
		Gstr1FileStatusEntity updateFileStatus = new Gstr1FileStatusEntity();
		try {
			// Extract the File Arrival message from the serialized Job params
			// object.
			 FileArrivalMsgDto msg = extractAndValidateMessage(message);

			// Join the file path and file name to get the file path.

			 ProcessingContext processingContext = new ProcessingContext();
			  String fileName = msg.getFileName(); String fileFolder =
			  msg.getFilePath();
			 

			String file = message.getUserName();
			
			String filePath = new StringJoiner("/").add(fileFolder).add(fileName)
					.toString();	
		
			//String file1 = "C:/Users/Balakrishna.S/Desktop/DevCode/" + file;
			String userName = message.getUserName();
			InputStream inputStream = null;

			/*
			 * try { Session openCmisSession =
			 * gstr1FileUploadUtil.getCmisSession();
			 * LOGGER.debug("openCmisSession " + openCmisSession); Document
			 * document = gstr1FileUploadUtil .getDocument(openCmisSession,
			 * fileName, fileFolder); LOGGER.debug("document name" +
			 * document.getName()); inputStream =
			 * document.getContentStream().getStream(); } catch (Exception e) {
			 * LOGGER.error("Exception occured in SR File Arrival Processor",
			 * e); }
			 */

			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(filePath);

			// Dummy List
			
			List<Gstr6DistributionExcelEntity> structuralErrorsRecords = new ArrayList<>();
			List<Gstr6DistributionExcelEntity> strucProcessedRecords = new ArrayList<>();

			int totalRecords = 0;
			Integer processedRecords = 0;
			Integer errorRecords = 0;
			Integer information = 0;
			
			
		
			/*
			 * TabularDataSourceTraverser traverser = traverserFactory
			 * .getTraverser(fileName);
			 */

			TabularDataLayout layout = new DummyTabularDataLayout(23);

			// Add a dummy row handler that will keep counting the rows.
		/*	OutwardTransDocRowHandler rowHandler = new OutwardTransDocRowHandler<String>(
					new DocumentKeyBuilder());
*/			
			
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			//Gstr6SRFileArrivalHandlerTest 
			long stTime = System.currentTimeMillis();
			// traverser.traverse(inputStream, layout, rowHandler, null);
			traverser.traverse(filePath, layout, rowHandler, null);
			//Object[] getHeaders = rowHandler.getHeaderData();
			Object[] getHeaders = rowHandler.getHeaderRow();
			List<Object[]> listobject = new ArrayList<>();
			listobject.add(getHeaders);
			Pair<Boolean, String> checkHeaderFormat = distrbtnHeaderCheckService
					.validate(getHeaders);

			List<Object[]> productList = ((FileUploadDocRowHandler<?>) rowHandler)
					.getFileUploadList();
			
			List<Gstr6DistributionExcelEntity> excelData = masterDataToProductConverter
					.convertProduct(productList,
							updateFileStatus);
			
			// deleting Existing record Based On Key 
		
			softDelete(excelData);
			
			List<Gstr6DistributionExcelEntity> saveExcelAll = gstr6DistrbtnExcelRepository
					.saveAll(excelData);
			
		
			
			LOGGER.error("Header Checker Validations "
					+ checkHeaderFormat.getValue0() + " ,"
					+ checkHeaderFormat.getValue1());

			
			
			
		/*	Map<String, List<Object[]>> documentMap = ((OutwardTransDocRowHandler<?>) rowHandler)
					.getDocumentMap();*/
		/*	documentMap.entrySet().forEach(entry -> {
				String key = entry.getKey();
				LOGGER.debug("key " + key);
			});
			if (!documentMap.isEmpty()) {
				LOGGER.debug("Document Map  Length " + documentMap.size());
			}
*/
			// Gstr1FileStatusEntity updateFileStatus =
			// gstr1FileStatusRepository.getFileName(fileName);

			updateFileStatus = gstr1FileStatusRepository.getFileName(
					"Redistribution_ISD_GSTR6.xlsx");
			// Structural Validations
			Map<String, List<ProcessingResult>> processingResults = validatorChain
					.validation(productList,saveExcelAll);

			
		/*	Map<String, List<ProcessingResult>> processingResults = new HashMap<>(
					processingStructResults);

					distriButionCanLookUp.forEach(
					(key, value) -> processingResults.merge(key, value,
							(v1, v2) -> Stream.of(v1, v2)
									.flatMap(x -> x.stream()).collect(
											Collectors.toList())));
							
			*/
			
			List<String> structuralValKeys = new ArrayList<>();

			for (String k : processingResults.keySet()) {
				String errkey = k.substring(0, k.lastIndexOf('-'));
				structuralValKeys.add(errkey);
			}

			List<Long> ids = new ArrayList<>();
			List<String> processedKeys = new ArrayList<>();

		//	List<String> processedKeys = new ArrayList<>();

			for (Gstr6DistributionExcelEntity id : saveExcelAll) {
				String processedKey = id.getProcessKey();
				if (!structuralValKeys.contains(processedKey)) {
					processedKeys.add(processedKey);
					strucProcessedRecords.add(id);
				} else {
					id.setError(true);
					structuralErrorsRecords.add(id);
				}
			}
			
			if (structuralValKeys.size() > 0 && !structuralValKeys.isEmpty()) {
				
				structuralErrorsRecords = gstr6DistrbtnExcelRepository
						.getAllExcelData(structuralValKeys, ids);
				
				
				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug("To check cancel Invoices The number of dockeys "
							+ "recieved from the iteration is : " + structuralValKeys.size());
				}

				Config config = configManager.getConfig("EYInternal",
						"outward.save.chunksize");
				String chnkSizeStr = config != null ? config.getValue() : "2000";
				int chunkSize = Integer.parseInt(chnkSizeStr);

			
				List<List<String>> docKeyChunks = Lists.partition(structuralValKeys,
						chunkSize);
				if (!docKeyChunks.isEmpty()) {
					docKeyChunks.forEach(list -> 
					gstr6DistrbtnExcelRepository.updateSameInvKey(list));
				}
				
				
			//	gstr6DistrbtnExcelRepository.updateSameInvKey(structuralValKeys);
				gstr6DistrbtnExcelRepository.saveAll(structuralErrorsRecords);
				
				
				
				
				
				gstr6DistrbtnExcelRepository
						.b2cUpdateStructuralError(structuralValKeys, ids);

			}
		/*	if (processedKeys.size() > 0 && !processedKeys.isEmpty()) {
				strucProcessedRecords = gstr6DistrbtnExcelRepository
						.getAllExcelData(processedKeys, ids);
			}
*/
			LOGGER.error("strucErrorRecords ", structuralErrorsRecords.size());
			LOGGER.error("strucProcessRecords ", strucProcessedRecords.size());
			
			if (structuralErrorsRecords.size() > 0
					&& !structuralErrorsRecords.isEmpty()) {
				// Keep the list of errors ready.
				Map<String, List<Gstr6VerticalWebError>> errorMap = 
						gstr6VerticalWebUploadErrorService
						.convertErrors(processingResults, STRUCTURAL_VALIDATIONS,
								updateFileStatus);

				gstr6VerticalWebUploadErrorService
						.storedErrorRecords(structuralErrorsRecords, errorMap);

			}
			if (strucProcessedRecords.size() > 0
					&& !strucProcessedRecords.isEmpty()) {
				gstr6BusinessErrorUploadService.processBusinessData(listobject,
						strucProcessedRecords, structuralErrorsRecords,
						updateFileStatus, processingContext);
			}else {
				updateFileStatus.setTotal(totalRecords);
				updateFileStatus.setProcessed(processedRecords);
				updateFileStatus.setError(errorRecords);
				updateFileStatus.setInformation(information);
				updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
				gstr1FileStatusRepository.save(updateFileStatus);

			}

		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
		}
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

		// Log the extracted info from the message. if (LOGGER.isDebugEnabled())
		{
			String logMsg = String.format(
					"Message Details are -> Path: '%s', "
							+ "File Name: '%s', Group Code: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.debug(logMsg);
		}
		return msg;
	}

	
	// Deleting Existing Record based on Processed Key
private void softDelete(List<Gstr6DistributionExcelEntity> excelData){
		
	Set<String> docKeySet = new HashSet<>();
	excelData.forEach(doc -> docKeySet.add(doc.getProcessKey()));
	
	List<String> docKeys = new ArrayList<>();
	docKeys = new ArrayList<>(docKeySet);

	if (LOGGER.isDebugEnabled()) {

		LOGGER.debug("To check cancel Invoices The number of dockeys "
				+ "recieved from the iteration is : " + docKeys.size());
	}

	Config config = configManager.getConfig("EYInternal",
			"outward.save.chunksize");
	String chnkSizeStr = config != null ? config.getValue() : "2000";
	int chunkSize = Integer.parseInt(chnkSizeStr);


	List<List<String>> docKeyChunks = Lists.partition(docKeys,
			chunkSize);

	
	if (!docKeyChunks.isEmpty()) {
		docKeyChunks.forEach(list -> gstr6DistrbtnExcelRepository.updateSameInvKey(list));
	}
	
	}
	
	
}
