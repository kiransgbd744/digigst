package com.ey.advisory.app.data.services.compliancerating;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.asprecon.VendorDueDateEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorDueDateRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * This upload service handles two request,SAPAPI and ADMINAPI
 * 
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("VendorDueDateUploadServiceImpl")
public class VendorDueDateUploadServiceImpl
		implements VendorDueDateUploadService {

	private static final List<String> EXPECTED_HEADER_LIST = Arrays.asList(
			"TaxPeriod", "ReturnType", "Return Filing Frequency", "Due date",
			"Vendor State Code");

	private static final String DOC_KEY_JOINER = "|";

	private static final int NO_OF_COLUMNS = EXPECTED_HEADER_LIST.size();

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	private VendorDueDateRepository dueDateRepo;

	@Autowired
	private EntityInfoRepository entityRepo;

	@Override
	public void validateVendorDueDateFile(Long fileId, String fileName,
			String folderName, Long entityId) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Validating vendor due date file";
			LOGGER.debug(msg);
		}
		try {
			Gstr1FileStatusEntity updateFileStatus = fileStatusRepository
					.getFileName(fileName);
			String docId = updateFileStatus.getDocId();
			InputStream in = getFileInpStream(fileName, folderName, docId);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(
					NO_OF_COLUMNS);

			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(in, layout, rowHandler, null);
			@SuppressWarnings("unchecked")
			List<String> actualHeaderNames = (List<String>) (List<?>) Arrays
					.asList(rowHandler.getHeaderRow());

			validateHeaders(fileId, actualHeaderNames);

			List<Object[]> fileList = rowHandler.getFileUploadList();

			if (CollectionUtils.isEmpty(fileList)) {
				String msg = "Failed Empty file";
				LOGGER.error(msg);

				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);

				throw new AppException(msg);
			}

			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			Set<String> keySet = new HashSet<>();
			List<Long> entityList = new ArrayList<>();
			List<VendorDueDateEntity> entities = new ArrayList<>();

			/*
			 * Insert due date for all the entities and make common accross
			 * group
			 */

			entityList = entityRepo.findActiveEntityIds();

			entityList.forEach(eId -> {
				convertRowsToEntity(fileList, entities, eId, userName, keySet,
						fileId);

			});

			if (!keySet.isEmpty()) {

				List<String> keyList = new ArrayList<>(keySet);
				int softDeleteCount = 0;
				List<List<String>> chunks = Lists.partition(keyList, 2000);
				for (List<String> chunk : chunks) {

					int softDeleteChnkCount = dueDateRepo
							.softDeleteActiveRecords(userName, chunk);
					softDeleteCount = softDeleteCount + softDeleteChnkCount;
				}

				if (LOGGER.isDebugEnabled()) {

					LOGGER.debug("Total number of softDeleted records are {}",
							softDeleteCount);
				}

			}

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Total number rows to be saved are {}",
						entities.size());
			}

			if (!entities.isEmpty()) {
				dueDateRepo.saveAll(entities);
			}

			LOGGER.debug("Updating fileId to PROCESSED status {}", fileId);
			Optional<Gstr1FileStatusEntity> gstr1FileStatusEntity = fileStatusRepository
					.findById(fileId);
			if (gstr1FileStatusEntity.isPresent()) {
				gstr1FileStatusEntity.get().setProcessed(entities.size());
				gstr1FileStatusEntity.get()
						.setError(fileList.size() - entities.size());
				gstr1FileStatusEntity.get().setTotal(fileList.size());
				gstr1FileStatusEntity.get()
						.setFileStatus(JobStatusConstants.PROCESSED);
				fileStatusRepository.save(gstr1FileStatusEntity.get());
			}
		} catch (Exception ex) {
			String msg = "Failed, error while reading file.";
			LOGGER.error(msg, ex);
			markFileAsFailed(fileId, ex.getMessage());
			throw new AppException(msg, ex);
		}
	}

	private void validateHeaders(Long fileId, List<String> actualHeaderNames) {
		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"User upload file headers names %s "
								+ "and header count %d",
						actualHeaderNames.toString(), actualHeaderNames.size());
				LOGGER.debug(msg);
			}

			if (actualHeaderNames.size() != NO_OF_COLUMNS) {
				String msg = String.format(
						"The number of columns in the file should be %d,"
								+ " Aborting the file processing.",
						NO_OF_COLUMNS);

				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);

				markFileAsFailed(fileId, msg);

				LOGGER.equals(msg);
				throw new AppException(msg);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Expected headers names " + "%s and header count %d",
						EXPECTED_HEADER_LIST.toString(),
						EXPECTED_HEADER_LIST.size());
				LOGGER.debug(msg);
			}

			boolean isMatch = headersMatch(EXPECTED_HEADER_LIST,
					actualHeaderNames);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Comparing two files header name " + "and count: %s",
						isMatch);
				LOGGER.debug(msg);
			}

			if (!isMatch) {
				String msg = "The header names/order are not as expected.";
				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				fileStatusRepository.updateErrorFieNameById(fileId, msg);
				markFileAsFailed(fileId, msg);
				LOGGER.error(msg);
				throw new AppException(msg);
			}

		} catch (Exception ex) {
			String msg = (ex instanceof AppException) ? ex.getMessage()
					: "Error occured while processing the file";
			markFileAsFailed(fileId, msg);
			if (msg.length() > 200)
				msg = msg.substring(0, 200);

			fileStatusRepository.updateErrorFieNameById(fileId, msg);
			throw (ex instanceof AppException) ? ((AppException) ex)
					: new AppException(msg, ex);
		}

	}

	private void markFileAsFailed(Long fileId, String reason) {

		try {
			fileStatusRepository.updateFileStatus(fileId, "Failed");
		} catch (Exception ex) {
			String msg = String
					.format("[SEVERE] Unable to mark the file as failed. "
							+ "Reason for file failure is: [ %s ]", reason);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	private InputStream getFileInpStream(String fileName, String folderName,
			String docId) {

		try {
			Document document = DocumentUtility.downloadDocumentByDocId(docId);
			if (document == null) {
				String errMsg = String.format(
						"Unable to find the file %s in folder %s for docId %s",
						fileName, folderName, docId);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Downloaded Vendor Due date Uploaded file");
			}
			return document.getContentStream().getStream();
		} catch (Exception ex) {
			throw new AppException(
					"Error occured while " + "reading the file " + fileName,
					ex);
		}
	}

	private boolean headersMatch(List<String> expected, List<String> actual) {
		return expected.equals(actual);
	}

	private void convertRowsToEntity(List<Object[]> fileList,
			List<VendorDueDateEntity> entities, Long entityId, String userName,
			Set<String> keys, Long fileId) {

		fileList.forEach(arr -> {

			VendorDueDateEntity e = new VendorDueDateEntity();
			String returnType = null;
			String stateCode = null;
			String returnFilingFrequency = null;
			String taxPeriod = getValidTaxPeriod(arr[0]);
			if (taxPeriod != null) {
				e.setTaxPeriod(taxPeriod);
			} else {
				String msg = String
						.format("Error Occured due to invalid TaxPeriod");
				LOGGER.error(msg);
				fileStatusRepository.updateFileStatus(fileId, "Failed");
				throw new AppException(msg);
			}

			if (isPresent(arr[1])) {
				returnType = arr[1].toString().trim();
				e.setReturnType(returnType);
			} else {
				String msg = String
						.format("Error Occured due to invalid ReturnType");
				LOGGER.error(msg);
				fileStatusRepository.updateFileStatus(fileId, "Failed");
				throw new AppException(msg);
			}

			if (isValidReturnFilingFrequency(arr[2])) {
				returnFilingFrequency = arr[2].toString().trim();
				e.setReturnFilingFrequency(returnFilingFrequency.toUpperCase());
			} else {
				String msg = String.format(
						"Error Occured due to invalid Return Filing Frequency");
				LOGGER.error(msg);
				fileStatusRepository.updateFileStatus(fileId, "Failed");
				throw new AppException(msg);
			}

			LocalDate dueDate = isDueDateValid(arr[3]);
			if (dueDate != null) {
				e.setDueDate(dueDate);

			} else {
				String msg = String
						.format("Error Occured due to invalid Due date");
				LOGGER.error(msg);
				fileStatusRepository.updateFileStatus(fileId, "Failed");
				throw new AppException(msg);
			}

			boolean isStateCodeValid = checkStateCodeValid(arr[4]);
			if (isStateCodeValid) {
				if (isPresent(arr[4])) {
					stateCode = arr[4].toString().trim();
					e.setVendorStateCode(Integer.parseInt(stateCode));
				}
			} else {
				String msg = String.format(
						"Error Occured due to invalid Vendor State Code");
				LOGGER.error(msg);
				fileStatusRepository.updateFileStatus(fileId, "Failed");
				throw new AppException(msg);
			}

			String key = createKey(taxPeriod, returnType, entityId,
					returnFilingFrequency, stateCode);

			boolean isAdded = keys.add(key);
			e.setKey(key);
			e.setEntityId(entityId);
			e.setCreatedOn(LocalDateTime.now());
			e.setCreatedBy(userName);
			// add distinct entities
			if (isAdded) {
				entities.add(e);
			}
		});

	}

	// Blank is allowed, check for length
	private boolean checkStateCodeValid(Object stateCodeObj) {

		if (!isPresent(stateCodeObj)) {
			return true;
		}
		String stateCode = stateCodeObj.toString().trim();
		if (stateCode.length() > 2 || !StringUtils.isNumeric(stateCode)) {
			LOGGER.error("StateCode not valid");
			return false;

		}
		return true;
	}

	private String getValidTaxPeriod(Object taxPeriodObj) {

		if (!isPresent(taxPeriodObj)) {
			LOGGER.error("TaxPeriod is null or empty");
			return null;
		}

		String taxPeriod = taxPeriodObj.toString().trim();

		if (taxPeriod.contains("'")) {
			taxPeriod = taxPeriod.replace("'", "");
		}
		if (taxPeriod.length() != 6) {
			taxPeriod = "0" + taxPeriod;
		}

		if (!taxPeriod.matches("[0-9]+") || taxPeriod.length() != 6) {
			LOGGER.error("TaxPeriod is not valid {}", taxPeriod);
			return null;

		}

		int month = Integer.parseInt(taxPeriod.substring(0, 2));
		if (month > 12 || month == 00) {
			LOGGER.error("TaxPeriod is not valid {}", taxPeriod);

			return null;

		}
		return taxPeriod;
	}

	private LocalDate isDueDateValid(Object date) {

		if (!isPresent(date)) {
			LOGGER.error("Due Date is blank");
			return null;
		}

		LocalDate dateInstance = DateFormatForStructuralValidatons
				.parseObjToDate(date);

		if (dateInstance == null || (dateInstance.getMonthValue() > 12)) {
			LOGGER.error("Due Date is not valid {}", date);

			return null;
		}
		return dateInstance;
	}

	// returnType|taxPeriod|entityId|stateCode
	private String createKey(String taxPeriod, String returnType, Long entityId,
			String returnFilingFrequency, String stateCode) {

		StringJoiner joiner = new StringJoiner(DOC_KEY_JOINER);
		joiner.add(returnType).add(taxPeriod)
				.add(returnFilingFrequency.toUpperCase())
				.add(entityId.toString());

		// add stateCode to key if exists
		if (stateCode != null) {
			if (stateCode.trim().length() == 1) {
				stateCode = "0" + stateCode;
			}
			joiner.add(stateCode);
		}

		return joiner.toString();
	}

	public static boolean isValidReturnFilingFrequency(Object obj) {

		if (obj == null)
			return false;
		if (obj instanceof String) {
			String frequency = (obj.toString()).trim();
			if (frequency.isEmpty())
				return false;
			if (!((frequency.equalsIgnoreCase("Monthly"))
					|| frequency.equalsIgnoreCase("Quarterly")))
				return false;
		}
		return true;
	}
}
