package com.ey.advisory.app.sftp.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.common.GstnValidatorConstants;
import com.ey.advisory.app.data.entities.client.GstinValidatorEntity;
import com.ey.advisory.app.data.repositories.client.GstinValidatorConfigRepository;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GstinAndEinvoiceApplicabilitySFTPServiceImpl")
public class GstinAndEinvoiceApplicabilitySFTPServiceImpl
                implements GstinAndEinvoiceApplicabilitySFTPService{
	
	@Autowired
	private AsyncJobsService asyncJobsService;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GstinValidatorConfigRepository")
	GstinValidatorConfigRepository gstinValidRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;
	
	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;
	
	//@RequestParam("file-data") String isEinvApplicable
	
	public String gstinValidSftpFileUploads(File file) {
		
		Workbook wkb = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Started Validating GSTIN Validator SFTP file Upload");
			}
			/*if (files.length > 1) {
				String errMsg = "Multiple Files are not allowed";
				return errMsg;
			}
			MultipartFile file = files[0];
			String fileName = file.getOriginalFilename();

			if (!fileName.endsWith(".xlsx")) {
				String errMsg = "Uploaded file is Invalid. Please upload .xlsx File";
				return errMsg;
			}

			LOGGER.debug("About to get mimeType at ::{}", LocalDateTime.now());

			String mimeType = GenUtil.getMimeType(file.getInputStream());
			LOGGER.debug("Got mimeType {} at::{} ", mimeType,
					LocalDateTime.now());

			if (mimeType != null && !CSV_CONTENT_TYPE.contains(mimeType)) {
				String errMsg = "Invalid content in the uploaded file";
				return errMsg;
			}
*/
			String groupCode = TenantContext.getTenantId();
			String folderName = GSTConstants.GSTIN_VALIDATOR_UPLOAD_FOLDER;

			  InputStream in = new FileInputStream(file);
			//InputStream in = file.getInputStream();
			  String fileName = file.getName();

			try {
				wkb = new Workbook(in);
			} catch (Exception ex) {
				String errMsg = "Exception while processing the Gstin Validator "
						+ " Sftp file upload.";
				LOGGER.error(errMsg, ex);
				return errMsg;
			}
			/*
			 * uploading Document in DocRepo with Workbook and FolderName ang
			 * get unique file Name
			 */
			wkb.setFileName(fileName);
			Pair<String,String> uniqueFileName = DocumentUtility.uploadDocumentAndReturnDocID(wkb,
					folderName, "XLSX");

			Document doc = DocumentUtility.downloadDocumentByDocId(uniqueFileName.getValue1());
			
			InputStream fin = doc.getContentStream().getStream();
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(1);
			// Add a dummy row handler that will keep counting the rows.
			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(fin, layout, rowHandler, null);
			// Getting No Of Gstins in the Excel Uploaded
			List<Object[]> listOfGstins = ((FileUploadDocRowHandler<?>) rowHandler)
					.getFileUploadList();

			Long rowCount = (long) listOfGstins.size();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("The Size of The Gstins Present"
						+ " in Excel Sheet is %d ", rowCount);
				LOGGER.debug(msg);

			}
			GstinValidatorEntity entity = new GstinValidatorEntity();
			Long requestID = generateCustomId(entityManager);
			// Checking for No of Gstins if it is greater than 0 then we
			// else return 0
			if (rowCount <= 0) {
				String errMsg = "File Uploaded is Empty";
             return errMsg;
			}

			entity.setFileName(uniqueFileName.getValue0());
			entity.setDateOfUpload(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			entity.setNoOfGstins(rowCount);
			entity.setRequestId(requestID);
			entity.setStatus(GstnValidatorConstants.UPLOADED_STATUS);
			entity.setCreatedBy("SFTP");
			entity.setDocId(uniqueFileName.getValue1());
			boolean einvApplicable = true;
			String isEinvApplicable="true";
			if (!Strings.isNullOrEmpty(isEinvApplicable))
				einvApplicable = Boolean.valueOf(isEinvApplicable);
			entity.setEinvApplicable(einvApplicable);
			gstinValidRepo.save(entity);
          
			// Making entry in Job Schedular
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("requestID", requestID);
			jsonParams.addProperty("fileName", uniqueFileName.getValue0());
			jsonParams.addProperty("folderName", folderName);
			jsonParams.addProperty("isEinvApplicable", isEinvApplicable);
			jsonParams.addProperty("docId", uniqueFileName.getValue1());
			
			

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTINVALIDATORFILEUPLOAD,
					jsonParams.toString(), "SFTP", 1L, null, null);
			String upldMsg = "File Upload Request Submitted Successfully";
            return upldMsg;
		} catch (Exception ex) {
			String errMsg = "Exception while uploading GSTIN File Upload";
			LOGGER.error(errMsg, ex);
			return errMsg;
		}

	}
	private static Long generateCustomId(EntityManager entityManager) {
		String reportId = "";
		String digits = "";
		Long nextSequencevalue = getNextSequencevalue(entityManager);

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		String currentDate = currentYear + (currentMonth < 10
				? ("0" + currentMonth) : String.valueOf(currentMonth));
		if (nextSequencevalue != null && nextSequencevalue > 0) {
			digits = String.format("%06d", nextSequencevalue);
			reportId = currentDate.concat(digits);
		}

		return Long.valueOf(reportId);
	}
	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT GSTINVALID_SEQ.nextval " + "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		return ((Long) query.getSingleResult());

	}

}
