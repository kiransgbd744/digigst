
package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.common.base.Strings;
import com.ibm.icu.math.BigDecimal;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("ReportDaoImpl")
public class ReportDaoImpl implements ReportDao {

	private static int CSV_BUFFER_SIZE = 8192;

	private static Integer chunkSize = 10000;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;
	private static final String UPLOADED_FILENAME_MSG = "Uploaded file Name :'%s'";

	@Override
	public Pair<String,String> getErrorData(Long batchId) {

		String fullPath = null;
		File tempDir = null;
		String uploadedDocName = null;
		Writer writer = null;
		Pair<String,String> docUploadIdAndPath = null;
		

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Error Report Details with batchId:'%s'", batchId);
			LOGGER.debug(msg);
		}

		try {
			tempDir = createTempDir(batchId);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"batchId  is '%s',"
								+ " Created temporary directory to generate "
								+ "zip file: %s",
						batchId.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}
			Optional<FileStatusDownloadReportEntity> entity = fileStatusDownloadReportRepo
					.findById(batchId);
			
			String errFileName = "";
			
			if (entity.get().getReportCateg()
					.equalsIgnoreCase("Recon Response 2APR(202DFs)")) {
				errFileName = "ReconResponse_2APR(202DFs)_Error_" + batchId;
			} else if (entity.get().getReportCateg()
					.equalsIgnoreCase("Recon Response Auto(108DFs)")) {
				errFileName = "ReconResponse_Auto(108DFs)_Error_" + batchId;
			} else if (entity.get().getReportCateg()
					.equalsIgnoreCase("Recon Response SFTP(108DFs)")) {
				errFileName = "ReconResponse_SFTP(108DFs)_Error_" + batchId;
			}
			else if(entity.get().getReportCateg()
					.equalsIgnoreCase("Recon Result"))
			{
				errFileName = "Recon_Response_UI_" +batchId+ "_Error_Report";
			}
			fullPath = tempDir.getAbsolutePath() + File.separator + errFileName
					+ ".csv";

			Integer noOfChunk = getChunkNo("USP_RECON_INS_CHUNK_RPT_FM3B_ERR",
					batchId, chunkSize);

			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk == 0) {

				String msg = String.format("for Error Report no.of "
						+ "chunk count is zero batchId:'%s'", batchId);
				LOGGER.error(msg);

				fileStatusDownloadReportRepo.updateStatus(batchId,
						"NO_DATA_FOUND", null, LocalDateTime.now());

				return null;
			}
			int j = 0;
			writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE);

			String invoiceHeadersTemplate = commonUtility
					.getProp("gstr2.recon.response.new.error.headers");

			String[] columnMappings = commonUtility
					.getProp("gstr2.recon.response.new.error.column")
					.split(",");

			StatefulBeanToCsv<GSTR2AAutoReconErrInfoDTO> beanWriter = getBeanWriter(
					columnMappings, writer);
			writer.append(invoiceHeadersTemplate);
			while (j < noOfChunk) {
				j++;

				List<Object[]> records = getData(
						"USP_RECON_DISP_CHUNK_RPT_FM3B_ERR", batchId, j);

				if (records != null && !records.isEmpty()) {

					List<GSTR2AAutoReconErrInfoDTO> reconDataList = records
							.stream().map(o -> convertRowsToDto(o, true, false))
							.collect(Collectors.toCollection(ArrayList::new));

					beanWriter = getBeanWriter(columnMappings, writer);
					beanWriter.write(reconDataList);
					flushWriter(writer);
				}
			}
			File fPathFile = new File(fullPath);
			
			
			/*uploadedDocName = DocumentUtility.uploadZipFile(fPathFile,
					ConfigConstants.GSTR2USERRESPONSEUPLOADS);
*/
			docUploadIdAndPath = DocumentUtility.uploadFile(fPathFile,
					ConfigConstants.GSTR2USERRESPONSEUPLOADS);

			uploadedDocName = docUploadIdAndPath.getValue0();
			//String docId = docUploadIdAndPath.getValue1();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(UPLOADED_FILENAME_MSG,
						uploadedDocName);
				LOGGER.debug(msg);
			}
			deleteTemporaryDirectory(tempDir);

		} catch (Exception ex) {
			String errMsg = String
					.format("Error while creating Error report %s", batchId);

			LOGGER.error(errMsg, ex);
			deleteTemporaryDirectory(tempDir);
			throw new AppException(ex);

		}
		
		return docUploadIdAndPath;
	}

	private static File createTempDir(Long batchId) throws IOException {

		String tempFolderPrefix = "ReconUserResponse" + "_" + batchId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private void deleteTemporaryDirectory(File tempFile) {

		if (tempFile != null && tempFile.exists()) {
			try {
				FileUtils.deleteDirectory(tempFile);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							tempFile.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						tempFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}

	}

	@Override
	public Pair<String,String> getPsdData(Long batchId) {
		String fullPath = null;
		File tempDir = null;
		String uploadedDocName = null;
		Writer writer = null;
		Pair<String,String> docUploadIdAndPath = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get PSD Report Details with batchId:'%s'", batchId);
			LOGGER.debug(msg);
		}

		try {
			tempDir = createTempDir(batchId);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"batchId  is '%s',"
								+ " Created temporary directory to generate "
								+ "zip file: %s",
						batchId.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while creating temp Directory for config id {}",
					batchId, ex);
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get PSD Report Details with batchId:'%s'", batchId);
			LOGGER.debug(msg);
		}
		try {
			String psdFile = "";
			Optional<FileStatusDownloadReportEntity> entity = fileStatusDownloadReportRepo
					.findById(batchId);
			if (entity.get().getReportCateg()
					.equalsIgnoreCase("Recon Response 2APR(202DFs)")) {
				psdFile = "ReconResponse_2APR(202DFs)_Processed_" + batchId;
			} else if (entity.get().getReportCateg()
					.equalsIgnoreCase("Recon Response Auto(108DFs)")) {
				psdFile = "ReconResponse_Auto(108DFs)_Processed_" + batchId;
			} else if (entity.get().getReportCateg()
					.equalsIgnoreCase("Recon Response SFTP(108DFs)")) {
				psdFile = "ReconResponse_SFTP(108DFs)_Processed_" + batchId;
			}
			
			fullPath = tempDir.getAbsolutePath() + File.separator + psdFile
					+ ".csv";

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"batchId  is '%s',"
								+ " Created temporary directory to generate "
								+ "zip file: %s",
						batchId.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}

			Integer noOfChunk = getChunkNo("USP_RECON_INS_CHUNK_RPT_FM3B_PSD",
					batchId, chunkSize);

			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk == 0) {

				String msg = String.format("for Error Report no.of "
						+ "chunk count is zero batchId:'%s'", batchId);
				LOGGER.error(msg);

				fileStatusDownloadReportRepo.updateStatus(batchId,
						"NO_DATA_FOUND", null, LocalDateTime.now());

				return null;
			}
			int j = 0;
			writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE);

			String invoiceHeadersTemplate = commonUtility
					.getProp("gstr2.recon.response.new.psd.headers");
			String[] columnMappings = commonUtility
					.getProp("gstr2.recon.response.new.psd.column").split(",");

			StatefulBeanToCsv<GSTR2AAutoReconErrInfoDTO> beanWriter = getBeanWriter(
					columnMappings, writer);
			writer.append(invoiceHeadersTemplate);
			while (j < noOfChunk) {
				j++;

				List<Object[]> records = getData(
						"USP_RECON_DISP_CHUNK_RPT_FM3B_PSD", batchId, j);

				if (records != null && !records.isEmpty()) {

					List<GSTR2AAutoReconErrInfoDTO> reconDataList = records
							.stream().map(o -> convertRowsToDto(o, false, false))
							.collect(Collectors.toCollection(ArrayList::new));

					beanWriter = getBeanWriter(columnMappings, writer);
					beanWriter.write(reconDataList);
					flushWriter(writer);
				}
			}
			File fPathFile = new File(fullPath);

			docUploadIdAndPath = DocumentUtility.uploadFile(fPathFile,
					ConfigConstants.GSTR2USERRESPONSEUPLOADS);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(UPLOADED_FILENAME_MSG,
						uploadedDocName);
				LOGGER.debug(msg);
			}
			deleteTemporaryDirectory(tempDir);
		} catch (Exception ex) {
			String errMsg = String.format("Error while creating PSD report %s",
					batchId);

			LOGGER.error(errMsg, ex);
			deleteTemporaryDirectory(tempDir);
			throw new AppException(ex);
		}

		return docUploadIdAndPath;
	}


	@Override
	public Pair<String,String> getInfoData(Long batchId) {

		String fullPath = null;
		File tempDir = null;
		String uploadedDocName = null;
		Pair<String,String> docUploadIdAndPath = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Information Report Details with batchId:'%s'",
					batchId);
			LOGGER.debug(msg);
		}
		Writer writer = null;

		try {
			tempDir = createTempDir(batchId);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"batchId  is '%s',"
								+ " Created temporary directory to generate "
								+ "zip file: %s",
						batchId.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while creating temp Directory for config id {}",
					batchId, ex);
		}
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get INFO Report Details with batchId:'%s'", batchId);
			LOGGER.debug(msg);
		}

		try {

			String infoFileName = "Information_Report";
			
			Optional<FileStatusDownloadReportEntity> entity = fileStatusDownloadReportRepo
					.findById(batchId);
			if (entity.get().getReportCateg()
					.equalsIgnoreCase("Recon Response 2APR(202DFs)")) {
				infoFileName = "ReconResponse_2APR(202DFs)_Information_" + batchId;
			} else if (entity.get().getReportCateg()
					.equalsIgnoreCase("Recon Response Auto(108DFs)")) {
				infoFileName = "ReconResponse_Auto(108DFs)_Information_" + batchId;
			} else if (entity.get().getReportCateg()
					.equalsIgnoreCase("Recon Response SFTP(108DFs)")) {
				infoFileName = "ReconResponse_SFTP(108DFs)_Information_" + batchId;
			}
			
			fullPath = tempDir.getAbsolutePath() + File.separator + infoFileName
					+ ".csv";

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"batchId  is '%s',"
								+ " Created temporary directory to generate "
								+ "zip file: %s",
						batchId.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}
			Integer noOfChunk = getChunkNo("USP_RECON_INS_CHUNK_RPT_FM3B_INF",
					batchId, chunkSize);

			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk == 0) {

				String msg = String.format("for Error Report no.of "
						+ "chunk count is zero batchId:'%s'", batchId);
				LOGGER.error(msg);

				fileStatusDownloadReportRepo.updateStatus(batchId,
						"NO_DATA_FOUND", null, LocalDateTime.now());

				return null;
			}
			int j = 0;
			writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE);

			String invoiceHeadersTemplate = commonUtility
					.getProp("gstr2.recon.response.new.information.headers");
			String[] columnMappings = commonUtility
					.getProp("gstr2.recon.response.new.information.column")
					.split(",");

			StatefulBeanToCsv<GSTR2AAutoReconErrInfoDTO> beanWriter = getBeanWriter(
					columnMappings, writer);
			writer.append(invoiceHeadersTemplate);

			while (j < noOfChunk) {
				j++;

				List<Object[]> records = getData(
						"USP_RECON_DISP_CHUNK_RPT_FM3B_INF", batchId, j);

				if (records != null && !records.isEmpty()) {

					List<GSTR2AAutoReconErrInfoDTO> reconDataList = records
							.stream().map(o -> convertRowsToDto(o, false, true))
							.collect(Collectors.toCollection(ArrayList::new));

					beanWriter = getBeanWriter(columnMappings, writer);
					beanWriter.write(reconDataList);
					flushWriter(writer);
				}
			}
			File fPathFile = new File(fullPath);

			docUploadIdAndPath = DocumentUtility.uploadFile(fPathFile,
					ConfigConstants.GSTR2USERRESPONSEUPLOADS);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(UPLOADED_FILENAME_MSG,
						uploadedDocName);
				LOGGER.debug(msg);
			}
			deleteTemporaryDirectory(tempDir);

		} catch (Exception ex) {
			LOGGER.error(
					"Error while creating Information report %s",
					batchId, ex);
			deleteTemporaryDirectory(tempDir);
			throw new AppException(ex);

		}

		return docUploadIdAndPath;

	}

	private GSTR2AAutoReconErrInfoDTO convertRowsToDto(Object[] arr,
			boolean isError, boolean isInfo) {

		GSTR2AAutoReconErrInfoDTO obj = new GSTR2AAutoReconErrInfoDTO();

		String suggestedResp = (arr[0] != null) ? arr[0].toString() : null;
		if (StringUtils.isNumeric(suggestedResp)) {
			obj.setSuggestedResponse("'".concat(suggestedResp));
		} else {
			obj.setSuggestedResponse(suggestedResp);

		}

		String userResp = (arr[1] != null) ? arr[1].toString() : null;
		if (StringUtils.isNumeric(userResp)) {
			obj.setUserResponse("'".concat(userResp));
		} else {
			obj.setUserResponse(userResp);

		}
		obj.setTaxPeriodforGSTR3B(
				(arr[2] != null) ? "'".concat(arr[2].toString()) : null);
		obj.setResponseRemarks((arr[3] != null) ? arr[3].toString() : null);
		obj.setMatchingScoreOutof12(
				(arr[4] != null) ? arr[4].toString() : null);
		obj.setMatchReason((arr[5] != null) ? arr[5].toString() : null);
		obj.setMismatchReason((arr[6] != null) ? arr[6].toString() : null);
		obj.setReportCategory((arr[7] != null) ? arr[7].toString() : null);
		obj.setReportType((arr[8] != null) ? arr[8].toString() : null);
		obj.setErpReportType((arr[9] != null) ? arr[9].toString() : null);
		obj.setPreviousReportType2A(
				(arr[10] != null) ? arr[10].toString() : null);
		obj.setPreviousReportTypePR(
				(arr[11] != null) ? arr[11].toString() : null);
		obj.setTaxPeriod2A(
				(arr[12] != null) ? "'".concat(arr[12].toString()) : null);
		obj.setTaxPeriod2B(
				(arr[13] != null) ? "'".concat(arr[13].toString()) : null);
		obj.setTaxPeriodPR(
				(arr[14] != null) ? "'".concat(arr[14].toString()) : null);
		obj.setCalendarMonth(
				(arr[15] != null) ? "'".concat(arr[15].toString()) : null);
		obj.setRecipientGstin2A((arr[16] != null) ? arr[16].toString() : null);
		obj.setRecipientGstinPR((arr[17] != null) ? arr[17].toString() : null);
		obj.setSupplierPan2A((arr[18] != null) ? arr[18].toString() : null);
		obj.setSupplierPanPR((arr[19] != null) ? arr[19].toString() : null);

		obj.setSupplierGstin2A((arr[20] != null) ? arr[20].toString() : null);
		obj.setSupplierGstinPR((arr[21] != null) ? arr[21].toString() : null);
		obj.setSupplierLegalName2A(
				(arr[22] != null) ? arr[22].toString() : null);
		obj.setSupplierTradeName2A(
				(arr[23] != null) ? arr[23].toString() : null);
		obj.setSupplierNamePR((arr[24] != null) ? arr[24].toString() : null);

		String docType2A = (arr[25] != null) ? arr[25].toString() : null;
		obj.setDocType2A(docType2A);

		String docTypePR = (arr[26] != null) ? arr[26].toString() : null;
		obj.setDocTypePR(docTypePR);

		obj.setDocumentNumber2A(
				(arr[27] != null) ? "'".concat(arr[27].toString()) : null);
		obj.setDocumentNumberPR(
				(arr[28] != null) ? "'".concat(arr[28].toString()) : null);

		obj.setDocumentDate2A((arr[29] != null) ? arr[29].toString() : null);
		obj.setDocumentDatePR((arr[30] != null) ? arr[30].toString() : null);
		obj.setPos2A((arr[31] != null) ? "'".concat(arr[31].toString()) : null);
		obj.setPosPR((arr[32] != null) ? "'".concat(arr[32].toString()) : null);

		if (docType2A != null && ("CR".equalsIgnoreCase(docType2A)
				|| "C".equalsIgnoreCase(docType2A)
				|| "RCR".equalsIgnoreCase(docType2A))) {
			obj.setTaxableValue2A(addNegativeSign(arr[33]));
			obj.setIgst2A(addNegativeSign(arr[35]));
			obj.setCgst2A(addNegativeSign(arr[37]));
			obj.setSgst2A(addNegativeSign(arr[39]));
			obj.setCess2A(addNegativeSign(arr[41]));
			obj.setTotalTax2A(addNegativeSign(arr[43]));
			obj.setInvoiceValue2A(addNegativeSign(arr[45]));

		} else {

			obj.setTaxableValue2A(
					(arr[33] != null) ? arr[33].toString() : null);
			obj.setIgst2A((arr[35] != null) ? arr[35].toString() : null);
			obj.setCgst2A((arr[37] != null) ? arr[37].toString() : null);
			obj.setSgst2A((arr[39] != null) ? arr[39].toString() : null);
			obj.setCess2A((arr[41] != null) ? arr[41].toString() : null);
			obj.setTotalTax2A((arr[43] != null) ? arr[43].toString() : null);
			obj.setInvoiceValue2A(
					(arr[45] != null) ? arr[45].toString() : null);

		}

		if (docTypePR != null && ("CR".equalsIgnoreCase(docTypePR)
				|| "C".equalsIgnoreCase(docTypePR)
				|| "RCR".equalsIgnoreCase(docTypePR))) {

			obj.setTaxableValuePR(addNegativeSign(arr[34]));
			obj.setIgstPR(addNegativeSign(arr[36]));
			obj.setCgstPR(addNegativeSign(arr[38]));
			obj.setSgstPR(addNegativeSign(arr[40]));
			obj.setCessPR(addNegativeSign(arr[42]));
			obj.setTotalTaxPR(addNegativeSign(arr[44]));
			obj.setInvoiceValuePR(addNegativeSign(arr[46]));
		} else {
			obj.setTaxableValuePR(
					(arr[34] != null) ? arr[34].toString() : null);
			obj.setIgstPR((arr[36] != null) ? arr[36].toString() : null);
			obj.setCgstPR((arr[38] != null) ? arr[38].toString() : null);
			obj.setSgstPR((arr[40] != null) ? arr[40].toString() : null);
			obj.setCessPR((arr[42] != null) ? arr[42].toString() : null);
			obj.setTotalTaxPR((arr[44] != null) ? arr[44].toString() : null);
			obj.setInvoiceValuePR(
					(arr[46] != null) ? arr[46].toString() : null);
		}

		if (docTypePR != null && ("CR".equalsIgnoreCase(docTypePR)
				|| "C".equalsIgnoreCase(docTypePR)
				|| "RCR".equalsIgnoreCase(docTypePR))) {

			obj.setAvailableIGST(addNegativeSign(arr[47]));
			obj.setAvailableCGST(addNegativeSign(arr[48]));
			obj.setAvailableSGST(addNegativeSign(arr[49]));
			obj.setAvailableCESS(addNegativeSign(arr[50]));
		} else {

			obj.setAvailableIGST((arr[47] != null) ? arr[47].toString() : null);
			obj.setAvailableCGST((arr[48] != null) ? arr[48].toString() : null);
			obj.setAvailableSGST((arr[49] != null) ? arr[49].toString() : null);
			obj.setAvailableCESS((arr[50] != null) ? arr[50].toString() : null);
		}

		obj.setItcAvailability2B((arr[51] != null) ? arr[51].toString() : null);
		obj.setReasonForItcUnavailability2B(
				(arr[52] != null) ? arr[52].toString() : null);
		obj.setSourceType((arr[53] != null) ? arr[53].toString() : null);
		obj.setGenerationDate2B((arr[54] != null)
				? "'".concat(timeSubstring(arr[54].toString())) : null);
		obj.setGenerationDate2A((arr[55] != null)
				? "'".concat(timeSubstring(arr[55].toString())) : null);

		obj.setReconGeneratedDate(
				(arr[56] != null) ? "'".concat(arr[56].toString()) : null);
		obj.setEInvoiceApplicability(
				(arr[57] != null) ? arr[57].toString() : null);
		obj.setSupplierReturnFilingPeriodicity(
				(arr[58] != null) ? arr[58].toString() : null);

		obj.setGSTR1FilingStatus((arr[59] != null) ? arr[59].toString() : null);
		obj.setGSTR1FilingDate(
				(arr[60] != null) ? timeSubstring(arr[60].toString()) : null);
		obj.setGSTR1FilingPeriod((arr[61] != null)
				? "'".concat(timeSubstring(arr[61].toString())) : null);
		obj.setGSTR3BFilingStatus(
				(arr[62] != null) ? arr[62].toString() : null);
		obj.setCancellationDate(
				(arr[63] != null) ? timeSubstring(arr[63].toString()) : null);
		obj.setOrgInvAmendmentPeriod(
				(arr[64] != null) ? "'".concat(arr[64].toString()) : null);
		obj.setOrgAmendmentType((arr[65] != null) ? arr[65].toString() : null);
		obj.setCDNDelinkingFlag((arr[66] != null) ? arr[66].toString() : null);
		obj.setReverseChargeFlag2A(
				(arr[67] != null) ? arr[67].toString() : null);
		obj.setReverseChargeFlagPR(
				(arr[68] != null) ? arr[68].toString() : null);
		obj.setDifferentialPercentage2A(
				(arr[69] != null) ? arr[69].toString() : null);
		obj.setDifferentialPercentagePR(
				(arr[70] != null) ? arr[70].toString() : null);
		obj.setOrgDocNumber2A(addApostrophe(arr[71]));
		obj.setOrgDocNumberPR(addApostrophe(arr[72]));
		obj.setOrgDocDate2A(
				(arr[73] != null) ? timeSubstring(arr[73].toString()) : null);
		obj.setOrgDocDatePR(
				(arr[74] != null) ? timeSubstring(arr[74].toString()) : null);
		obj.setOrgSupplierGstinPR(
				(arr[75] != null) ? arr[75].toString() : null);
		obj.setOrgSupplierNamePR((arr[76] != null) ? arr[76].toString() : null);
		obj.setCRDRPreGST2A((arr[77] != null) ? arr[77].toString() : null);
		obj.setCRDRPreGSTPR((arr[78] != null) ? arr[78].toString() : null);
		obj.setBoeReferenceDate(
				(arr[79] != null) ? timeSubstring(arr[79].toString()) : null);
		obj.setPortCode2A((arr[80] != null) ? arr[80].toString() : null);
		obj.setBillOfEntry2A((arr[81] != null) ? arr[81].toString() : null);
		obj.setBillOfEntryDate2A(
				(arr[82] != null) ? timeSubstring(arr[82].toString()) : null);
		obj.setBoeAmended((arr[83] != null) ? arr[83].toString() : null);
		obj.setUserID((arr[84] != null) ? arr[84].toString() : null);
		obj.setSourceFileName((arr[85] != null) ? arr[85].toString() : null);
		obj.setProfitCentre1((arr[86] != null) ? arr[86].toString() : null);
		obj.setPlant((arr[87] != null) ? arr[87].toString() : null);
		obj.setDivision((arr[88] != null) ? arr[88].toString() : null);
		obj.setLocation((arr[89] != null) ? arr[89].toString() : null);
		obj.setPurchaseOrganisation(
				(arr[90] != null) ? arr[90].toString() : null);
		obj.setProfitCentre2((arr[91] != null) ? arr[91].toString() : null);
		obj.setProfitCentre3((arr[92] != null) ? arr[92].toString() : null);
		obj.setProfitCentre4((arr[93] != null) ? arr[93].toString() : null);
		obj.setProfitCentre5((arr[94] != null) ? arr[94].toString() : null);
		obj.setProfitCentre6((arr[95] != null) ? arr[95].toString() : null);
		obj.setProfitCentre7((arr[96] != null) ? arr[96].toString() : null);
		obj.setGLCodeAssessableValue(
				(arr[97] != null) ? arr[97].toString() : null);
		obj.setGLCodeIGST((arr[98] != null) ? arr[98].toString() : null);
		obj.setGLCodeCGST((arr[99] != null) ? arr[99].toString() : null);
		obj.setGLCodeSGST((arr[100] != null) ? arr[100].toString() : null);
		obj.setGLCodeAdvaloremCess(
				(arr[101] != null) ? arr[101].toString() : null);
		obj.setGLCodeSpecificCess(
				(arr[102] != null) ? arr[102].toString() : null);
		obj.setGLCodeStateCessAdvalorem(
				(arr[103] != null) ? arr[103].toString() : null);
		obj.setTableType2A((arr[104] != null) ? arr[104].toString() : null);
		obj.setSupplyType2A((arr[105] != null) ? arr[105].toString() : null);
		obj.setSupplyTypePR((arr[106] != null) ? arr[106].toString() : null);
		obj.setSupplierType((arr[107] != null) ? arr[107].toString() : null);
		obj.setSupplierCode((arr[108] != null) ? arr[108].toString() : null);
		obj.setSupplierAddress1(
				(arr[109] != null) ? arr[109].toString() : null);
		obj.setSupplierAddress2(
				(arr[110] != null) ? arr[110].toString() : null);
		obj.setSupplierLocation(
				(arr[111] != null) ? arr[111].toString() : null);
		obj.setSupplierPincode((arr[112] != null) ? arr[112].toString() : null);
		obj.setStateApplyingCess(
				(arr[113] != null) ? arr[113].toString() : null);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Port Code PR", arr[114].toString());
			LOGGER.debug(msg);
		}
		obj.setPortCodePR((arr[114] != null) ? arr[114].toString() : null);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"BillOfEntryPR PR", arr[115].toString());
			LOGGER.debug(msg);
		}
		obj.setBillOfEntryPR((arr[115] != null) ? arr[115].toString() : null);
		obj.setBillOfEntryDatePR(
				(arr[116] != null) ? timeSubstring(arr[116].toString()) : null);
		if (docTypePR != null && ("CR".equalsIgnoreCase(docTypePR)
				|| "C".equalsIgnoreCase(docTypePR)
				|| "RCR".equalsIgnoreCase(docTypePR))) {

			obj.setCIFValue(addNegativeSign(arr[117]));
			obj.setCustomDuty(addNegativeSign(arr[118]));
		} else {

			obj.setCIFValue((arr[117] != null) ? arr[117].toString() : null);
			obj.setCustomDuty((arr[118] != null) ? arr[118].toString() : null);
		}
		obj.setHSNorSAC((arr[119] != null) ? arr[119].toString() : null);
		obj.setProductCode((arr[120] != null) ? arr[120].toString() : null);
		obj.setProductDescription(
				(arr[121] != null) ? arr[121].toString() : null);
		obj.setCategoryOfProduct(
				(arr[122] != null) ? arr[122].toString() : null);
		obj.setUqc((arr[123] != null) ? arr[123].toString() : null);
		obj.setQuantity((arr[124] != null) ? arr[124].toString() : null);
		if (docTypePR != null && ("CR".equalsIgnoreCase(docTypePR)
				|| "C".equalsIgnoreCase(docTypePR)
				|| "RCR".equalsIgnoreCase(docTypePR))) {

			obj.setAdvaloremCessAmount(addNegativeSign(arr[125]));
			obj.setSpecificCessAmount(addNegativeSign(arr[126]));
			obj.setStateCessAdvaloremAmount(addNegativeSign(arr[127]));
			obj.setItemOtherCharges(addNegativeSign(arr[128]));
		} else {
			obj.setAdvaloremCessAmount(
					(arr[125] != null) ? arr[125].toString() : null);
			obj.setSpecificCessAmount(
					(arr[126] != null) ? arr[126].toString() : null);
			obj.setStateCessAdvaloremAmount(
					(arr[127] != null) ? arr[127].toString() : null);
			obj.setItemOtherCharges(
					(arr[128] != null) ? arr[128].toString() : null);
		}
		obj.setClaimRefundFlag((arr[129] != null) ? arr[129].toString() : null);
		obj.setAutoPopulateToRefund(
				(arr[130] != null) ? arr[130].toString() : null);
		obj.setAdjustementReferenceNo(
				(arr[131] != null) ? arr[131].toString() : null);
		obj.setAdjustementReferenceDate(
				(arr[132] != null) ? timeSubstring(arr[132].toString()) : null);
		obj.setEligibilityIndicator(
				(arr[133] != null) ? arr[133].toString() : null);
		obj.setCommonSupplyIndicator(
				(arr[134] != null) ? arr[134].toString() : null);
		obj.setITCEntitlement((arr[135] != null) ? arr[135].toString() : null);
		obj.setITCReversalIdentifier(
				(arr[136] != null) ? arr[136].toString() : null);
		obj.setReasonForCreditDebitNote(
				(arr[137] != null) ? arr[137].toString() : null);
		obj.setAccountingVoucherNumber(
				(arr[138] != null) ? arr[138].toString() : null);
		obj.setAccountingVoucherDate(
				(arr[139] != null) ? timeSubstring(arr[139].toString()) : null);
		obj.setGlPostingDate(
				(arr[140] != null) ? timeSubstring(arr[140].toString()) : null);
		obj.setCustomerPORefNumber(
				(arr[141] != null) ? arr[141].toString() : null);
		obj.setCustomerPORefDate(
				(arr[142] != null) ? timeSubstring(arr[142].toString()) : null);
		if (docTypePR != null && ("CR".equalsIgnoreCase(docTypePR)
				|| "C".equalsIgnoreCase(docTypePR)
				|| "RCR".equalsIgnoreCase(docTypePR))) {

			obj.setPurchaseOrderValue(addNegativeSign(arr[143]));
		} else {
			obj.setPurchaseOrderValue(
					(arr[143] != null) ? arr[143].toString() : null);
		}
		obj.setUserDefinedField1(
				(arr[144] != null) ? arr[144].toString() : null);
		obj.setUserDefinedField2(
				(arr[145] != null) ? arr[145].toString() : null);
		obj.setUserDefinedField3(
				(arr[146] != null) ? arr[146].toString() : null);
		obj.setUserDefinedField4(
				(arr[147] != null) ? arr[147].toString() : null);
		obj.setUserDefinedField5(
				(arr[148] != null) ? arr[148].toString() : null);
		obj.setUserDefinedField6(
				(arr[149] != null) ? arr[149].toString() : null);
		obj.setUserDefinedField7(
				(arr[150] != null) ? arr[150].toString() : null);
		obj.setUserDefinedField8(
				(arr[151] != null) ? arr[151].toString() : null);
		obj.setUserDefinedField9(
				(arr[152] != null) ? arr[152].toString() : null);
		obj.setUserDefinedField10(
				(arr[153] != null) ? arr[153].toString() : null);
		obj.setUserDefinedField11(
				(arr[154] != null) ? arr[154].toString() : null);
		obj.setUserDefinedField12(
				(arr[155] != null) ? arr[155].toString() : null);
		obj.setUserDefinedField13(
				(arr[156] != null) ? arr[156].toString() : null);
		obj.setUserDefinedField14(
				(arr[157] != null) ? arr[157].toString() : null);
		obj.setUserDefinedField15(
				(arr[158] != null) ? arr[158].toString() : null);
		obj.setUserDefinedField28(
				(arr[159] != null) ? arr[159].toString() : null);
		obj.setEWayBillNumber((arr[160] != null) ? arr[160].toString() : null);
		obj.setEWayBillDate(
				(arr[161] != null) ? timeSubstring(arr[161].toString()) : null);
		obj.setMatchingID((arr[162] != null) ? arr[162].toString() : null);
		obj.setRequestID(
				(arr[163] != null) ? "'".concat(arr[163].toString()) : null);
		obj.setIDPR((arr[164] != null) ? arr[164].toString() : null);
		obj.setID2A((arr[165] != null) ? arr[165].toString() : null);
		obj.setInvoiceKeyPR((arr[166] != null) ? arr[166].toString() : null);
		obj.setInvoiceKeyA2((arr[167] != null) ? arr[167].toString() : null);

		obj.setReferenceIDPR((arr[168] != null) ? arr[168].toString() : null);
		obj.setReferenceID2A((arr[169] != null) ? arr[169].toString() : null);
		obj.setIrn2A((arr[170] != null) ? arr[170].toString() : null);
		obj.setIrnPR((arr[171] != null) ? arr[171].toString() : null);
		obj.setIrnDate2A(
				(arr[172] != null) ? timeSubstring(arr[172].toString()) : null);
		obj.setIrnDatePR(
				(arr[173] != null) ? timeSubstring(arr[173].toString()) : null);
		obj.setApprovalStatus((arr[174] != null) ? arr[174].toString() : null);
		obj.setRecordStatus((arr[175] != null) ? arr[175].toString() : null);
		obj.setKeyDescription((arr[176] != null) ? arr[176].toString() : null);

		// new added
		obj.setVendorComplianceTrend(
				(arr[177] != null) ? arr[177].toString() : null);
		obj.setSourceIdentifier(
				(arr[178] != null) ? arr[178].toString() : null);

		obj.setCompanyCode((arr[179] != null) ? arr[179].toString() : null);
		obj.setVendorType((arr[180] != null) ? arr[180].toString() : null);
		obj.setVendorRiskCategory(
				(arr[181] != null) ? arr[181].toString() : null);
		obj.setVendorPaymentTerms_Days(
				(arr[182] != null) ? arr[182].toString() : null);
		obj.setVendorRemarks((arr[183] != null) ? arr[183].toString() : null);
		obj.setReverseIntegratedDate(
				(arr[184] != null) ? "'".concat(arr[184].toString()) : null);
		obj.setQrCodeCheck((arr[185] != null) ? arr[185].toString() : null);
		obj.setQrCodeValidationResult(
				(arr[186] != null) ? arr[186].toString() : null);
		obj.setQrCodeMatchCount(
				(arr[187] != null) ? arr[187].toString() : null);
		obj.setQrCodeMismatchCount(
				(arr[188] != null) ? arr[188].toString() : null);
		obj.setQrMismatchAttributes(
				(arr[189] != null) ? arr[189].toString() : null);

		obj.setGSTR3BFilingDate(
				(arr[190] != null) ? timeSubstring(arr[190].toString()) : null);
		obj.setSupplierGSTINStatus(
				(arr[191] != null) ? arr[191].toString() : null);
		obj.setSysDefinedField1(
				(arr[192] != null) ? arr[192].toString() : null);
		obj.setSysDefinedField2(
				(arr[193] != null) ? arr[193].toString() : null);
		obj.setSysDefinedField3(
				(arr[194] != null) ? arr[194].toString() : null);
		obj.setSysDefinedField4(
				(arr[195] != null) ? arr[195].toString() : null);
		obj.setSysDefinedField5(
				(arr[196] != null) ? arr[196].toString() : null);
		obj.setSysDefinedField6(
				(arr[197] != null) ? arr[197].toString() : null);
		obj.setSysDefinedField7(
				(arr[198] != null) ? arr[198].toString() : null);
		obj.setSysDefinedField8(
				(arr[199] != null) ? arr[199].toString() : null);
		obj.setSysDefinedField9(
				(arr[200] != null) ? arr[200].toString() : null);
		obj.setSysDefinedField10(
				(arr[201] != null) ? arr[201].toString() : null);

		if (isError) {
			obj.setErrorId((arr[202] != null) ? arr[202].toString() : null);
			obj.setErrorDescription(
					(arr[203] != null) ? arr[203].toString() : null);
		}

		return obj;
	}

	private String addNegativeSign(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return (value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else if (value instanceof Integer) {
				return (value != null ? (((Integer) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof Long) {
				return (value != null ? (((Long) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof BigInteger) {
				return (value != null
						? ((((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else {
				if(!value.toString().isEmpty()){
					return "-" + value.toString().replaceFirst("-", "");
				} else{
					return null;
				}
			}
		}
		return null;
	}

	private String timeSubstring(String value) {

		if (Strings.isNullOrEmpty(value)) {
			return null;
		}

		if (value.contains("'"))
			value.replace("'", "");

		if (value.length() > 9)
			return (value.substring(0, 10));
		return value;
	}

	private Integer getChunkNo(String procName, Long downloadId,
			Integer chunkSize) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_REPORT_DOWNLOAD_ID", downloadId);

		storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Executing chunking proc '%s' , downloadId '%d' ", procName,
					downloadId);
			LOGGER.debug(msg);
		}

		Integer chunkNum = (Integer) storedProc.getSingleResult();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Executed chunking proc '%s', downloadId '%d', "
							+ "chunkCount '%d' ",
					procName, downloadId, chunkNum);
			LOGGER.debug(msg);
		}

		return chunkNum;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getData(String procName, Long downloadId,
			Integer chunkValue) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		storedProc.registerStoredProcedureParameter("P_REQUEST_DOWNLOAD_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_REQUEST_DOWNLOAD_ID", downloadId);

		storedProc.registerStoredProcedureParameter("P_CHUNK_DATA",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_DATA", chunkValue);

		List<Object[]> resultList = storedProc.getResultList();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Executing dispaly proc '%s' , downloadId '%d', "
							+ "Records count '%d' ",
					procName, downloadId, resultList.size());
			LOGGER.debug(msg);
		}

		return resultList;
	}

	private StatefulBeanToCsv<GSTR2AAutoReconErrInfoDTO> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<GSTR2AAutoReconErrInfoDTO> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(GSTR2AAutoReconErrInfoDTO.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<GSTR2AAutoReconErrInfoDTO> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<GSTR2AAutoReconErrInfoDTO> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
	}

	private void flushWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.flush();
				if (LOGGER.isDebugEnabled()) {
					String msg = "Flushed writer " + "successfully";
					LOGGER.debug(msg);
				}
			} catch (IOException e) {
				String msg = "Exception while " + "closing the file writer";
				LOGGER.error(msg);
				throw new AppException(msg, e);
			}
		}

	}
	private String addApostrophe(Object o){
		if(o != null){
			if(!(o.toString().isEmpty())){
				return DownloadReportsConstant.CSVCHARACTER.concat(o.toString());
			}
			else
				return null;
		}
		return null;
	}
}
