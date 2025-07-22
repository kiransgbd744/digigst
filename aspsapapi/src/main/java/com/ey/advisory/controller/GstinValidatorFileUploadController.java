package com.ey.advisory.controller;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.common.GstnValidatorConstants;
import com.ey.advisory.app.data.entities.client.GstinValidatorEntity;
import com.ey.advisory.app.data.repositories.client.GstinValidatorConfigRepository;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/*
 * This Controller is for Uploading The Excel file for Gstin
 * TaxPayer Details Check
 * 
 */
@Slf4j
@RestController
public class GstinValidatorFileUploadController {

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("GstinValidatorConfigRepository")
	GstinValidatorConfigRepository gstinValidRepo;

	private static final List<String> CSV_CONTENT_TYPE = ImmutableList
			.of("application/x-tika-ooxml", "application/vnd.ms-excel");

	@PostMapping(value = "/ui/gstinValidFileUpload", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstinValidFileUploads(
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") String isEinvApplicable) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Workbook wkb = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Started Validating GSTN Validator file Uploaded");
			}
			if (files.length > 1) {
				String errMsg = "Multiple Files are not allowed";
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(errMsg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			User user = SecurityContext.getUser();
			MultipartFile file = files[0];
			String fileName = file.getOriginalFilename();

			if (!fileName.endsWith(".xlsx")) {
				String errMsg = "Uploaded file is Invalid. Please upload .xlsx File";
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(errMsg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			LOGGER.debug("About to get mimeType at ::{}", LocalDateTime.now());

			String mimeType = GenUtil.getMimeType(file.getInputStream());
			LOGGER.debug("Got mimeType {} at::{} ", mimeType,
					LocalDateTime.now());

			if (mimeType != null && !CSV_CONTENT_TYPE.contains(mimeType)) {
				String errMsg = "Invalid content in the uploaded file";
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(errMsg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			String userName = user != null ? user.getUserPrincipalName()
					: "SYSTEM";
			String groupCode = TenantContext.getTenantId();
			String folderName = GSTConstants.GSTIN_VALIDATOR_UPLOAD_FOLDER;
			InputStream in = file.getInputStream();

			try {
				wkb = new Workbook(in);
			} catch (Exception ex) {
				String errMsg = "Exception while processing the Gstin Validator "
						+ "file upload.";
				LOGGER.error(errMsg, ex);
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("Invalid file format"));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			/*
			 * uploading Document in DocRepo with Workbook and FolderName ang
			 * get unique file Name
			 */
			wkb.setFileName(fileName);
			Pair<String, String> downloadFileDetails = DocumentUtility
					.uploadDocumentAndReturnDocID(wkb, folderName, "XLSX");
			String uniqueFileName = downloadFileDetails.getValue0();

			Document doc = DocumentUtility
					.downloadDocumentByDocId(downloadFileDetails.getValue1());
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
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(errMsg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			entity.setFileName(uniqueFileName);
			entity.setDateOfUpload(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			entity.setNoOfGstins(rowCount);
			entity.setRequestId(requestID);
			entity.setStatus(GstnValidatorConstants.UPLOADED_STATUS);
			entity.setCreatedBy(userName);
			entity.setDocId(downloadFileDetails.getValue1());
			boolean einvApplicable = false;
			if (!Strings.isNullOrEmpty(isEinvApplicable))
				einvApplicable = Boolean.valueOf(isEinvApplicable);
			entity.setEinvApplicable(einvApplicable);
			gstinValidRepo.save(entity);

			// Making entry in Job Schedular
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("requestID", requestID);
			jsonParams.addProperty("fileName", uniqueFileName);
			jsonParams.addProperty("folderName", folderName);
			jsonParams.addProperty("isEinvApplicable", isEinvApplicable);
			jsonParams.addProperty("docId", downloadFileDetails.getValue1());
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTINVALIDATORFILEUPLOAD,
					jsonParams.toString(), userName, 1L, null, null);
			String upldMsg = "File Upload Request Submitted Successfully";
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(upldMsg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String errMsg = "Exception while uploading GSTIN File Upload";
			LOGGER.error(errMsg, ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT GSTINVALID_SEQ.nextval " + "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		return ((Long) query.getSingleResult());

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

}
