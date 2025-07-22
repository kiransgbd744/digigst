package com.ey.advisory.app.gstr3b;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BErrorMasterRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BWebUploadRepository;
import com.ey.advisory.app.services.docs.Gstr3BFileUploadRowHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Vishal.Verma
 *
 */

@Slf4j
@Component("Gstr3BFileUploadServiceImpl")
public class Gstr3BFileUploadServiceImpl implements Gstr3BFileUploadService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("FileStatusRepository")
	private FileStatusRepository fileStatusRepo;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr3BStructuralValidationsImpl")
	private Gstr3BStructuralValidations validator;

	@Autowired
	@Qualifier("Gstr3BWebUploadRepository")
	private Gstr3BWebUploadRepository webUploadRepo;

	@Autowired
	@Qualifier("Gstr3BErrorMasterRepository")
	private Gstr3BErrorMasterRepository errMasterRepo;

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository gstr3bRepo;
	
	@Autowired
	private Gstr3bUtil gstr3bUtil;

	@Autowired
	AsyncJobsService asyncJobsService;

	public void gstr3bFileUpload(Workbook workbook, String fileFolder) {

		fileFolder = "gstr3bFiles";

		InputStream inputStream = null;

		try {
			// uploading file to the doc repo
			String fileName = DocumentUtility.uploadDocument(workbook,
					fileFolder, "XLSX");
			LOGGER.debug("fileFolder name " + fileFolder);

			// downloading file from doc repo

			Document document = DocumentUtility.downloadDocument(fileName,
					fileFolder);
			LOGGER.debug("document name" + document.getName());

			// Saving File Status.

			Gstr1FileStatusEntity fStatusEntity = new Gstr1FileStatusEntity();
			fStatusEntity.setFileName(fileName);
			fStatusEntity.setFileType("RAW");
			fStatusEntity.setStartOfUploadTime(LocalDateTime.now());
			fStatusEntity.setEndOfUploadTime(LocalDateTime.now());
			fStatusEntity.setSource("WEB_UPLOAD");
			fStatusEntity.setDataType("GSTR3B");

			fileStatusRepo.save(fStatusEntity);
			Long fileId = fStatusEntity.getId();

			// creating job for inserting fileName, fileFolder and File ID

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();
			String groupCode = TenantContext.getTenantId();

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("fileId", fileId);
			jsonParams.addProperty("fileFolder", fileFolder);
			jsonParams.addProperty("fileName", fileName);

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR3BWEBFILEUPLOAD, jsonParams.toString(),
					userName, 1L, null, null);

			// creating input Stream to read the file

			inputStream = document.getContentStream().getStream();

			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(8);

			// Implementing Row Handler that will keep counting the rows.
			RowHandler rowHandler = new Gstr3BFileUploadRowHandler<String>(
					new Gstr3bBlockKeyBuilder());

			traverser.traverse(inputStream, layout, rowHandler, null);

			List<Object[]> fileList = ((Gstr3BFileUploadRowHandler<?>) rowHandler)
					.getFileList();

			LOGGER.debug(fileList.toString());

			Map<String, List<Object[]>> docMap = 
					((Gstr3BFileUploadRowHandler<?>) rowHandler)
					.getDocumentMap();

			// validating data by passing the List of Object which we are
			// getting from file
			Map<String, List<ProcessingResult>> errMap = validator
					.Validation(fileList, layout);

			List<Object[]> errorFreeData = new ArrayList<>();

			docMap.forEach((key, value) -> {
				if (!errMap.containsKey(key))
					errorFreeData.addAll(value);
			});
			
			//Map to set sectionname and subSectionName
			Map<String, Gstr3BGstinDashboardDto> subMap = gstr3bUtil
					.getGstr3BGstinDashboardMap();

			/**
			 * saving Data into UserInput table
			 */

			if (!errorFreeData.isEmpty()) {

				String docUserName = (SecurityContext.getUser() != null
						&& SecurityContext.getUser()
								.getUserPrincipalName() != null)
										? SecurityContext.getUser()
												.getUserPrincipalName()

										: "SYSTEM";
												
												
				Gstr3BGstinAspUserInputEntity userInputEntity = null;
				List<Gstr3BGstinAspUserInputEntity> userInputEntities =
						new ArrayList<>();
				
				
				for (Object[] objects : errorFreeData) {
					userInputEntity = new Gstr3BGstinAspUserInputEntity();
					userInputEntity.setTaxPeriod((String) objects[0]);
					userInputEntity.setGstin((String) objects[1]);
					BigDecimal taxableVal = NumberFomatUtil
							.getBigDecimal(objects[3]);
					userInputEntity.setTaxableVal(taxableVal);
					BigDecimal sgst = NumberFomatUtil.getBigDecimal(objects[6]);
					userInputEntity.setSgst(sgst);
					BigDecimal cess = NumberFomatUtil.getBigDecimal(objects[7]);
					userInputEntity.setCess(cess);
					BigDecimal cgst = NumberFomatUtil.getBigDecimal(objects[5]);
					userInputEntity.setCgst(cgst);
					BigDecimal igst = NumberFomatUtil.getBigDecimal(objects[4]);
					userInputEntity.setIgst(igst);
					userInputEntity.setCreateDate(LocalDateTime.now());
					userInputEntity.setCreatedBy(docUserName);
					userInputEntity.setIsActive(true);
					userInputEntity.setSectionName((String) objects[2]);
					userInputEntity.setSubSectionName("user_provided");
					userInputEntity.setUpdateDate(LocalDateTime.now());
					userInputEntity.setUpdatedBy(docUserName);
					if(!userInputEntities.contains(userInputEntity))
					userInputEntities.add(userInputEntity);
					gstr3bRepo.updatePerActiveFlag((String) objects[0],
							(String) objects[1], (String) objects[2]);
				}

				gstr3bRepo.saveAll(userInputEntities);

			}

			/**
			 * saving Error records into ErrorTable
			 */

			if (!errMap.isEmpty()) {
				for (Map.Entry<String, List<ProcessingResult>> entry : errMap
						.entrySet()) {
					List<ProcessingResult> res = entry.getValue();
					String key = entry.getKey();

					List<Gstr3BWebUploadEntity> errEntities = new ArrayList<>();
					Gstr3BWebUploadEntity errEntity = null;

					for (ProcessingResult obj : res) {
						Long errorId = errMasterRepo
								.findByErrorCode(obj.getCode());
						errEntity = new Gstr3BWebUploadEntity();
						errEntity.setErrorId(errorId);
						errEntity.setFileId(fileId);
						errEntity.setKey(key);
						errEntities.add(errEntity);
					}
					webUploadRepo.saveAll(errEntities);
				}
			}

		} catch (Exception e) {
			LOGGER.error("Exception occured in Gstr3B File Arrival Processor",
					e);
			throw new AppException(e);

		}
	}

}
