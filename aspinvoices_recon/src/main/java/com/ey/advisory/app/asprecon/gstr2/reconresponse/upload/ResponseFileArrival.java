package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2PathRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconResponseUploadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.google.common.collect.Streams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ResponseFileArrival")
public class ResponseFileArrival {

	private static List<String> HEADER_LIST = null;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr2ReconResponseUploadRepository")
	private Gstr2ReconResponseUploadRepository repo;

	@Autowired
	@Qualifier("ReportDaoImpl")
	private ReportDao reportDao;

	@Autowired
	@Qualifier("Gstr2PathRepository")
	private Gstr2PathRepository pathRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	String batchId = null;
	String msg = null;
	private InputStream getFileInpStream(String fileName, String folderName) {

		try {
			Document doc = DocumentUtility.downloadDocument(fileName,
					folderName);
			return doc.getContentStream().getStream();
		} catch (Exception ex) {
			throw new AppException(
					"Error occured while " + "reading the file " + fileName,
					ex);
		}
	}

	private void validateHeaders(String fileName, String folderName,
			Long fileId) {
		try {
			InputStream fin = getFileInpStream(fileName, folderName);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, 173);

			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverseHeaderOnly(fin, layout, rowHandler, null);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<String> actualHeaderNames = new ArrayList(
					Arrays.asList(rowHandler.getHeaderRow()));

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"User upload file headers names %s "
								+ "and header count %d",
						actualHeaderNames.toString(), actualHeaderNames.size());
				LOGGER.debug(msg);
			}

			if (actualHeaderNames.size() != 173) {
				String msg = "The number of columns in the file should be 173. "
						+ "Aborting the file processing.";
				markFileAsFailed(fileId, msg);
				throw new AppException(msg);
			}

			List<String> expectedHeaderNames = null;
			expectedHeaderNames = loadExpectedHeaders();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Expected User response headers names "
								+ "%s and header count %d",
						expectedHeaderNames.toString(),
						expectedHeaderNames.size());
				LOGGER.debug(msg);
			}

			boolean isMatch = headersMatch(expectedHeaderNames,
					actualHeaderNames);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Comparing two files header name " + "and count: %s",
						isMatch);
				LOGGER.debug(msg);
			}

			if (!isMatch) {
				String msg = "The header names/order are not as expected.";
				markFileAsFailed(fileId, msg);
				throw new AppException(msg);
			}

		} catch (Exception ex) {
			String msg = (ex instanceof AppException) ? ex.getMessage()
					: "Error occured while processing the file";
			markFileAsFailed(fileId, msg);
			throw (ex instanceof AppException) ? ((AppException) ex)
					: new AppException(msg, ex);
		}

	}

	private void markFileAsFailed(Long fileId, String reason) {
		try {
			fileStatusRepository.updateFileStatus(fileId, "Failed");
		} catch (Exception ex) {
			String msg = "[SEVERE] Unable to mark the file as failed. "
					+ "Reason for file failure is: [" + reason + "]";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public void validateReconResponse(Long fileId, String fileName,
			String folderName) {

		LOGGER.debug("Validating user uploaded header with template header");
		validateHeaders(fileName, folderName, fileId);

		Long longBatchId = 0L;

		try {
			// reading row data

			InputStream fin = getFileInpStream(fileName, folderName);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(173);

			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(fin, layout, rowHandler, null);

			List<Object[]> fileList = ((FileUploadDocRowHandler<?>) rowHandler)
					.getFileUploadList();

			LOGGER.error(fileList.toString());

			if (fileList.isEmpty() || fileList == null) {

				String msg = "Failed Empty file..";
				LOGGER.error(msg);
				throw new AppException(msg);

			}

			batchId = getBatchid(fileId);
			longBatchId = Long.valueOf(batchId);

			List<ReconResponseDto> respDto = fileList.stream()
					.map(o -> convertReconResponseDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			// convert respDto to entity & save to db.
			List<Gstr2ReconResponseUploadEntity> entityList = respDto.stream()
					.map(o -> convertToEntity(o))
					.collect(Collectors.toCollection(ArrayList::new));

			repo.saveAll(entityList);
			fileStatusRepository.updateCountSummary(fileId, entityList.size(),
					0, 0);

			pathRepo.updatePath(longBatchId, null, null, fileName, fileId,
					null);

			// call to spc

			LOGGER.debug(
					"Invking stor proc:  USP_RECON_RESP_VALIDATE , BatchId",
					longBatchId);
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_RESP_VALIDATE");

			storedProc.registerStoredProcedureParameter("VAR_BATCHID",
					Long.class, ParameterMode.IN);

			storedProc.setParameter("VAR_BATCHID", longBatchId);

			String status = (String) storedProc.getSingleResult();

			if (status.isEmpty() || status == null) {
				fileStatusRepository.updateFileStatus(fileId, "failed");
			}
			fileStatusRepository.updateFileStatus(fileId, "processed");

			if (status != null) {
				Pair<String,String> errorPath = reportDao.getErrorData(longBatchId);
				Pair<String,String>  psdPath = reportDao.getPsdData(longBatchId);
				Pair<String,String>  infoPath = reportDao.getInfoData(longBatchId);
				msg =String.format("updating PathTable with errorPath %s,  psdPath %s ,infoPath %s",
						errorPath, psdPath, infoPath);
				LOGGER.debug(msg);
				pathRepo.updatePath(longBatchId, psdPath.getValue0(), errorPath.getValue0(), fileName,
						fileId, infoPath.getValue0());
				msg =String.format("updated PathTable with errorPath %s,  psdPath %s ",
						errorPath, psdPath);
				LOGGER.debug(msg);
			}

		} catch (Exception ex) {

			String msg = "Failed, error while reading file.";
			LOGGER.error(msg, ex);
			fileStatusRepository.updateFileStatus(fileId, "failed");
			pathRepo.updatePath(longBatchId, null, null, fileName, fileId,
					null);

			throw new AppException(msg, ex);
		}
	}

	private boolean headersMatch(List<String> expected, List<String> actual) {
		return !Streams
				.zip(actual.stream(), expected.stream(),
						(a, e) -> createPair(a, e))
				.filter(p -> !p.getValue0().equals(p.getValue1())).findAny()
				.isPresent();
	}

	private Pair<String, String> createPair(String val1, String val2) {
		String val1Str = (val1 == null) ? "" : val1.trim().toUpperCase();
		String val2Str = (val2 == null) ? "" : val2.trim().toUpperCase();
		return new Pair<>(val1Str, val2Str);
	}

	private synchronized List<String> loadExpectedHeaders() {

		try {
			// Load the excel sheet and return the list of headers
			// using the traverseHeaderOnly method.
			if (HEADER_LIST == null) {
				HEADER_LIST = loadExpectedHeadersFromTemplate();
			}

			return HEADER_LIST;
		} catch (Exception ex) {
			String msg = "Failed to read the headers from the template file.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private List<String> loadExpectedHeadersFromTemplate()
			throws FileNotFoundException {

		ClassLoader classLoader = this.getClass().getClassLoader();
		URL template_Dir = classLoader.getResource("ReportTemplates/");
		String templatePath = template_Dir.getPath()
				+ "Gstr2ReconReportTemplate.csv";
		FileInputStream fin = new FileInputStream(new File(templatePath));

		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser("Gstr2ReconReportTemplate.csv");
		TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, 173);

		@SuppressWarnings("rawtypes")
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();

		traverser.traverseHeaderOnly(fin, layout, rowHandler, null);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<String> templateHeaderNames = new ArrayList(
				Arrays.asList(rowHandler.getHeaderRow()));

		return templateHeaderNames;
	}

	private ReconResponseDto convertReconResponseDto(Object[] arr) {

		ReconResponseDto obj = new ReconResponseDto();

		if ((arr[1] != null && (!arr[1].toString().isEmpty())
				&& (arr[1].toString().matches("[0-9]+")
						|| arr[1].toString().matches("`[0-9]+")))) {

			obj.setTaxPeriodforGSTR3B(arr[1].toString().length() == 5
					? ("0").concat(arr[1].toString().replace("`", "").trim())
					: (arr[1].toString().replace("`", "").trim()));
		} else {

			obj.setForceMatchResponse(
					(arr[1] != null && !arr[1].toString().isEmpty())
							? arr[1].toString().replace("`", "").trim() : null);
		}
		if ((arr[0] != null && (!arr[0].toString().isEmpty())
				&& (arr[0].toString().matches("[0-9]+")
						|| arr[0].toString().matches("~[0-9]+")))) {

			obj.setSuggestedResponse(arr[1].toString().length() == 5
					? ("0").concat(arr[0].toString()) : arr[0].toString());

		} else {
			obj.setSuggestedResponse(
					arr[0] != null && (!arr[0].toString().isEmpty())
							? arr[0].toString().trim() : arr[0].toString());
		}
		obj.setUserResponse((arr[1] != null && !arr[1].toString().isEmpty())
				? arr[1].toString().replace("`", "").trim() : null);

		obj.setAvailableCESS((arr[49] != null && !arr[49].toString().isEmpty())
				? arr[49].toString().trim() : null);
		obj.setAvailableCGST((arr[47] != null && !arr[47].toString().isEmpty())
				? arr[47].toString().trim() : null);
		obj.setAvailableIGST((arr[46] != null && !arr[46].toString().isEmpty())
				? arr[46].toString().trim() : null);
		obj.setAvailableSGST((arr[48] != null && !arr[48].toString().isEmpty())
				? arr[48].toString().trim() : null);
		obj.setCess2A((arr[40] != null && !arr[40].toString().isEmpty())
				? arr[40].toString().trim() : null);
		obj.setCessPR((arr[41] != null && !arr[41].toString().isEmpty())
				? arr[41].toString().trim() : null);
		obj.setGstr1FillingStatus(
				(arr[55] != null && !arr[55].toString().isEmpty())
						? arr[55].toString().trim() : null);
		obj.setCGST2A((arr[36] != null && !arr[36].toString().isEmpty())
				? arr[36].toString().trim() : null);
		obj.setCGSTPR((arr[37] != null && !arr[37].toString().isEmpty())
				? arr[37].toString().trim() : null);
		obj.setDocType2A((arr[22] != null && !arr[22].toString().isEmpty())
				? arr[22].toString().trim() : null);
		obj.setDocTypePR((arr[23] != null && !arr[23].toString().isEmpty())
				? arr[23].toString().trim() : null);
		obj.setDocumentDate2A((arr[26] != null && !arr[26].toString().isEmpty())
				? arr[26].toString().trim() : null);
		obj.setDocumentDatePR((arr[27] != null && !arr[27].toString().isEmpty())
				? arr[27].toString().trim() : null);
		obj.setDocumentNumber2A(
				(arr[24] != null && !arr[24].toString().isEmpty())
						? arr[24].toString().replace("`", "").trim() : null);
		obj.setDocumentNumberPR(
				(arr[25] != null && !arr[25].toString().isEmpty())
						? arr[25].toString().replace("`", "").trim() : null);

		obj.setID2A((arr[164] != null && !arr[164].toString().isEmpty())
				? arr[164].toString().trim() : null);
		obj.setIDPR((arr[163] != null && !arr[163].toString().isEmpty())
				? arr[163].toString().trim() : null);

		obj.setIGST2A((arr[34] != null && !arr[34].toString().isEmpty())
				? arr[34].toString().trim() : null);
		obj.setIGSTPR((arr[35] != null && !arr[35].toString().isEmpty())
				? arr[35].toString().trim() : null);

		obj.setInvoiceKeyA2((arr[166] != null && !arr[166].toString().isEmpty())
				? arr[166].toString().trim() : null);
		obj.setInvoiceKeyPR((arr[165] != null && !arr[165].toString().isEmpty())
				? arr[165].toString().trim() : null);

		obj.setRecipientGstin2A(
				(arr[15] != null && !arr[15].toString().isEmpty())
						? arr[15].toString().trim() : null);
		obj.setRecipientGstinPR(
				(arr[16] != null && !arr[16].toString().isEmpty())
						? arr[16].toString().trim() : null);
		obj.setSGST2A((arr[38] != null && !arr[38].toString().isEmpty())
				? arr[38].toString().trim() : null);
		obj.setSGSTPR((arr[39] != null && !arr[39].toString().isEmpty())
				? arr[39].toString().trim() : null);
		obj.setSupplierGstin2A(
				(arr[17] != null && !arr[17].toString().isEmpty())
						? arr[17].toString().trim() : null);
		obj.setSupplierGstinPR(
				(arr[18] != null && !arr[18].toString().isEmpty())
						? arr[18].toString().trim() : null);
		obj.setTaxableValue2A((arr[32] != null && !arr[32].toString().isEmpty())
				? arr[32].toString().trim() : null);
		obj.setTaxableValuePR((arr[33] != null && !arr[33].toString().isEmpty())
				? arr[33].toString().trim() : null);
		obj.setTaxPeriod2A((arr[11] != null && !arr[11].toString().isEmpty())
				? arr[11].toString().replace("`", "").trim() : null);

		obj.setTaxPeriodPR((arr[13] != null && !arr[13].toString().isEmpty())
				? arr[13].toString().replace("`", "").trim() : null);
		obj.setTableType2A((arr[100] != null && !arr[100].toString().isEmpty())
				? arr[100].toString().trim() : null);
		obj.setConfigId((arr[162] != null && !arr[162].toString().isEmpty())
				? arr[162].toString().replace("`", "").trim() : null);
		obj.setComments((arr[3] != null && !arr[3].toString().isEmpty())
				? arr[3].toString().trim() : null);
		obj.setReversChargeRegisgter(
				(arr[64] != null && !arr[64].toString().isEmpty())
						? arr[64].toString().trim() : null);

		return obj;
	}

	String userName = SecurityContext.getUser() != null
			? (SecurityContext.getUser().getUserPrincipalName() != null
					? SecurityContext.getUser().getUserPrincipalName()
					: "SYSTEM")
			: "SYSTEM";

	private Gstr2ReconResponseUploadEntity convertToEntity(
			ReconResponseDto dto) {
		Gstr2ReconResponseUploadEntity entity = new Gstr2ReconResponseUploadEntity();

		entity.setAvblCessPR(dto.getAvailableCESS());
		entity.setAvblCGSTPR(dto.getAvailableCGST());
		entity.setAvblIGSTPR(dto.getAvailableIGST());
		entity.setAvblSGSTPR(dto.getAvailableSGST());
		entity.setBatchID(batchId);
		entity.setCESS2A(dto.getCess2A());
		entity.setCESSPR(dto.getCessPR());
		entity.setCfsFlag(dto.getGstr1FillingStatus());
		entity.setCGST2A(dto.getCGST2A());
		entity.setCGSTPR(dto.getCGSTPR());
		entity.setCreatedBy(userName);
		entity.setCreateDTM((EYDateUtil.toUTCDateTimeFromLocal(new Date())));
		entity.setDocDate2A(dto.getDocumentDate2A());
		entity.setDocDatePR(dto.getDocumentDatePR());
		entity.setDocType2A(dto.getDocType2A());
		entity.setDocTypePR(dto.getDocTypePR());
		entity.setDocumentNumber2A(dto.getDocumentNumber2A());
		entity.setDocumentNumberPR(dto.getDocumentNumberPR());
		entity.setFMResponse(dto.getForceMatchResponse());
		entity.setID2A(dto.getID2A());
		entity.setIDPR(dto.getIDPR());
		entity.setIGST2A(dto.getIGST2A());
		entity.setIGSTPR(dto.getIGSTPR());
		entity.setInvoicekeyA2(dto.getInvoiceKeyA2());
		entity.setInvoicekeyPR(dto.getInvoiceKeyPR());
		entity.setRGSTIN2A(dto.getRecipientGstin2A());
		entity.setRGSTINPR(dto.getRecipientGstinPR());
		entity.setRspTaxPeriod3B(dto.getTaxPeriodforGSTR3B());
		entity.setSGST2A(dto.getSGST2A());
		entity.setSGSTPR(dto.getSGSTPR());
		entity.setSGSTINPR(dto.getSupplierGstinPR());
		entity.setSGSTIN2A(dto.getSupplierGstin2A());
		entity.setTableType(dto.getTableType2A());
		entity.setTaxable2A(dto.getTaxableValue2A());
		entity.setTaxablePR(dto.getTaxableValuePR());
		entity.setTaxPeriod2A(dto.getTaxPeriod2A());
		entity.setTaxPeriodPR(dto.getTaxPeriodPR());
		entity.setConfigId(dto.getConfigId());
		entity.setFmComment(dto.getComments());
		entity.setUserResponse(dto.getUserResponse());
		entity.setReversChargeReg(dto.getReversChargeRegisgter());

		return entity;
	}

	private String getBatchid(Long fileId) {

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		String currentDate = currentYear
				+ (currentMonth < 10 ? ("0" + currentMonth)
						: String.valueOf(currentMonth))
				+ (currentDay < 10 ? ("0" + currentDay)
						: String.valueOf(currentDay));
		String batchId = currentDate.concat(String.valueOf(fileId));

		return batchId;
	}

}
