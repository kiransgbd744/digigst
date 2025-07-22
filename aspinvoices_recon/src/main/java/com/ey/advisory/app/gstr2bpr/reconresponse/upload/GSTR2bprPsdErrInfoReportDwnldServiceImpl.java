package com.ey.advisory.app.gstr2bpr.reconresponse.upload;

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
import com.ibm.icu.math.BigDecimal;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GSTR2bprPsdErrInfoReportDwnldServiceImpl")
public class GSTR2bprPsdErrInfoReportDwnldServiceImpl
		implements GSTR2bprPsdErrInfoReportDwnldService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	private static final int CSV_BUFFER_SIZE = 8192;

	private static Integer chunkSize = 10000;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;
	private static final String UPLOADED_FILENAME_MSG = "Uploaded file Name :'%s'";

	@Override
	public Pair<String,String>  getErrorData(Long batchId) {
		String fullPath = null;
		Pair<String,String>  uploadedDocName = null;
		Writer writer = null;

		File tempDir = null;
		try {
			tempDir = createTempDir(batchId);
		} catch (IOException e) {
			LOGGER.error("error while creating Temp Dir");
			throw new AppException();
		}

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Get Error Report Details with batchId:'%s'", batchId);
				LOGGER.debug(msg);
			}
			

			Optional<FileStatusDownloadReportEntity> entity = fileStatusDownloadReportRepo
					.findById(batchId);
			String errFileName = "";
			if(entity.get().getReportCateg()
					.equalsIgnoreCase("Recon Result"))
			{
				errFileName = "Recon_Response_UI_"+batchId+"_Error_Report";
			}
			else
			{
			errFileName = "ReconResponse_2BPR_Error_" + batchId;
			}
			fullPath = tempDir.getAbsolutePath() + File.separator + errFileName
					+ ".csv";

			Integer noOfChunk = getChunkNo(
					"USP_RECON_2BPR_INS_CHUNK_RPT_FM3B_ERR", batchId,
					chunkSize);

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
					.getProp("gstr2bpr.recon.response.new.error.headers");

			String[] columnMappings = commonUtility
					.getProp("gstr2bpr.recon.response.new.error.column")
					.split(",");
			StatefulBeanToCsv<GSTR2bprAutoReconErrInfoDTO> beanWriter = getBeanWriter(
					columnMappings, writer);

			writer.append(invoiceHeadersTemplate);

			while (j < noOfChunk) {
				j++;

				List<Object[]> records = getData(
						"USP_RECON_2BPR_DISP_CHUNK_RPT_FM3B_ERR", batchId, j);

				if (records != null && !records.isEmpty()) {

					List<GSTR2bprAutoReconErrInfoDTO> reconDataList = records
							.stream().map(o -> convertRowsToDto(o, true, false))
							.collect(Collectors.toCollection(ArrayList::new));

					beanWriter = getBeanWriter(columnMappings, writer);
					beanWriter.write(reconDataList);
					flushWriter(writer);
				}
			}
			File fPathFile = new File(fullPath);

			uploadedDocName = DocumentUtility.uploadFile(fPathFile,
					ConfigConstants.GSTR2BPR_USERRESPONSE_UPLOADS);

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

		return uploadedDocName;
	}

	@Override
	public Pair<String,String>  getPsdData(Long batchId) {
		String fullPath = null;
		Pair<String,String>  uploadedDocName = null;

		Writer writer = null;

		File tempDir = null;
		try {
			tempDir = createTempDir(batchId);
		} catch (IOException e) {
			LOGGER.error("error while creating Temp Dir");
			throw new AppException();
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get PSD Report Details with batchId:'%s'", batchId);
			LOGGER.debug(msg);
		}
		try {

			String psdFile = "ReconResponse_2BPR_Processed_" + batchId;

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

			Integer noOfChunk = getChunkNo(
					"USP_RECON_2BPR_INS_CHUNK_RPT_FM3B_PSD", batchId,
					chunkSize);

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
					.getProp("gstr2bpr.recon.response.new.psd.headers");

			String[] columnMappings = commonUtility
					.getProp("gstr2bpr.recon.response.new.psd.column")
					.split(",");
			StatefulBeanToCsv<GSTR2bprAutoReconErrInfoDTO> beanWriter = getBeanWriter(
					columnMappings, writer);
			writer.append(invoiceHeadersTemplate);

			while (j < noOfChunk) {
				j++;

				List<Object[]> records = getData(
						"USP_RECON_2BPR_DISP_CHUNK_RPT_FM3B_PSD", batchId, j);

				if (records != null && !records.isEmpty()) {

					List<GSTR2bprAutoReconErrInfoDTO> reconDataList = records
							.stream()
							.map(o -> convertRowsToDto(o, false, false))
							.collect(Collectors.toCollection(ArrayList::new));

					beanWriter = getBeanWriter(columnMappings, writer);
					beanWriter.write(reconDataList);
					flushWriter(writer);
				}
			}
			File fPathFile = new File(fullPath);

			uploadedDocName = DocumentUtility.uploadFile(fPathFile,
					ConfigConstants.GSTR2BPR_USERRESPONSE_UPLOADS);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(UPLOADED_FILENAME_MSG,
						uploadedDocName);
				LOGGER.debug(msg);
			}
			deleteTemporaryDirectory(tempDir);

		}

		catch (Exception ex) {
			String errMsg = String.format("Error while creating PSD report %s",
					batchId);

			LOGGER.error(errMsg, ex);
			deleteTemporaryDirectory(tempDir);
			throw new AppException(ex);
		}

		return uploadedDocName;
	}

	@Override
	public Pair<String,String>  getInfoData(Long batchId) {

		String fullPath = null;
		Pair<String,String>  uploadedDocName = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Information Report Details with batchId:'%s'",
					batchId);
			LOGGER.debug(msg);
		}
		Writer writer = null;

		File tempDir = null;
		try {
			tempDir = createTempDir(batchId);
		} catch (IOException e) {
			LOGGER.error("error while creating Temp Dir");
			throw new AppException();
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get INFO Report Details with batchId:'%s'", batchId);
			LOGGER.debug(msg);
		}
		try {

			String infoFileName = "ReconResponse_2BPR_Information_" + batchId;

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
			Integer noOfChunk = getChunkNo(
					"USP_RECON_2BPR_INS_CHUNK_RPT_FM3B_INF", batchId,
					chunkSize);

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
					.getProp("gstr2bpr.recon.response.new.information.headers");

			String[] columnMappings = commonUtility
					.getProp("gstr2bpr.recon.response.new.information.column")
					.split(",");
			StatefulBeanToCsv<GSTR2bprAutoReconErrInfoDTO> beanWriter = getBeanWriter(
					columnMappings, writer);
			writer.append(invoiceHeadersTemplate);
			while (j < noOfChunk) {
				j++;

				List<Object[]> records = getData(
						"USP_RECON_2BPR_DISP_CHUNK_RPT_FM3B_INF", batchId, j);

				if (records != null && !records.isEmpty()) {

					List<GSTR2bprAutoReconErrInfoDTO> reconDataList = records
							.stream().map(o -> convertRowsToDto(o, false, true))
							.collect(Collectors.toCollection(ArrayList::new));

					beanWriter = getBeanWriter(columnMappings, writer);
					beanWriter.write(reconDataList);
					flushWriter(writer);
				}
			}
			File fPathFile = new File(fullPath);

			uploadedDocName = DocumentUtility.uploadFile(fPathFile,
					ConfigConstants.GSTR2BPR_USERRESPONSE_UPLOADS);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(UPLOADED_FILENAME_MSG,
						uploadedDocName);
				LOGGER.debug(msg);
			}
			deleteTemporaryDirectory(tempDir);

		} catch (Exception ex) {
			LOGGER.error(
					"Exception while creating temp Directory for config id {}",
					batchId, ex);
			deleteTemporaryDirectory(tempDir);
			throw new AppException(ex);

		}

		return uploadedDocName;

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

	private static File createTempDir(Long batchId) throws IOException {

		String tempFolderPrefix = "ReconUserResponse2BPR" + "_" + batchId;
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

	private GSTR2bprAutoReconErrInfoDTO convertRowsToDto(Object[] arr,
			boolean isError, boolean isInfo) {

		GSTR2bprAutoReconErrInfoDTO obj = new GSTR2bprAutoReconErrInfoDTO();

		if (isError) {
			obj.setErrorId((arr[188] != null) ? arr[188].toString() : null);
			obj.setErrorDescription(
					(arr[189] != null) ? arr[189].toString() : null);
		}

		if (isInfo) {
			obj.setInfoId((arr[188] != null) ? arr[188].toString() : null);
			obj.setInfoDescription(
					(arr[189] != null) ? arr[189].toString() : null);
		}

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
		obj.setPreviousReportType2B(
				(arr[9] != null) ? arr[9].toString() : null);
		obj.setPreviousReportTypePR(
				(arr[10] != null) ? arr[10].toString() : null);
		obj.setTaxPeriod2B(
				(arr[11] != null) ? "'".concat(arr[11].toString()) : null);
		obj.setTaxPeriodPR(
				(arr[12] != null) ? "'".concat(arr[12].toString()) : null);
		obj.setCalendarMonth(
				(arr[13] != null) ? "'".concat(arr[13].toString()) : null);
		obj.setRecipientGstin2B((arr[14] != null) ? arr[14].toString() : null);
		obj.setRecipientGstinPR((arr[15] != null) ? arr[15].toString() : null);

		obj.setSupplierGstin2B((arr[16] != null) ? arr[16].toString() : null);
		obj.setSupplierGstinPR((arr[17] != null) ? arr[17].toString() : null);
		obj.setSupplierLegalName2B(
				(arr[18] != null) ? arr[18].toString() : null);
		obj.setSupplierTradeName2B(
				(arr[19] != null) ? arr[19].toString() : null);
		obj.setSupplierNamePR((arr[20] != null) ? arr[20].toString() : null);

		String docType2B = (arr[21] != null) ? arr[21].toString() : null;
		obj.setDocType2B(docType2B);

		String docTypePR = (arr[22] != null) ? arr[22].toString() : null;

		obj.setDocTypePR(docTypePR);
		obj.setDocumentNumber2B(
				(arr[23] != null) ? "'".concat(arr[23].toString()) : null);
		obj.setDocumentNumberPR(
				(arr[24] != null) ? "'".concat(arr[24].toString()) : null);
		obj.setDocumentDate2B((arr[25] != null) ? arr[25].toString() : null);
		obj.setDocumentDatePR((arr[26] != null) ? arr[26].toString() : null);
		obj.setGSTPercent2B((arr[27] != null) ? arr[27].toString() : null);
		obj.setGSTPercentPR((arr[28] != null) ? arr[28].toString() : null);
		obj.setPos2B((arr[29] != null) ? "'".concat(arr[29].toString()) : null);
		obj.setPosPR((arr[30] != null) ? "'".concat(arr[30].toString()) : null);

		if (docType2B != null && ("CR".equalsIgnoreCase(docType2B)
				|| "C".equalsIgnoreCase(docType2B)
				|| "RCR".equalsIgnoreCase(docType2B))) {

			obj.setTaxableValue2B(addNegativeSign(arr[31]));
			obj.setIgst2B(addNegativeSign(arr[33]));
			obj.setCgst2B(addNegativeSign(arr[35]));
			obj.setSgst2B(addNegativeSign(arr[37]));
			obj.setTotalTax2B(addNegativeSign(arr[41]));
			obj.setInvoiceValue2B(addNegativeSign(arr[43]));
		} else {
			obj.setTaxableValue2B(
					(arr[31] != null) ? arr[31].toString() : null);
			obj.setIgst2B((arr[33] != null) ? arr[33].toString() : null);
			obj.setCgst2B((arr[35] != null) ? arr[35].toString() : null);
			obj.setSgst2B((arr[37] != null) ? arr[37].toString() : null);
			obj.setTotalTax2B((arr[41] != null) ? arr[41].toString() : null);
			obj.setInvoiceValue2B(
					(arr[43] != null) ? arr[43].toString() : null);

		}

		if (docTypePR != null && ("CR".equalsIgnoreCase(docTypePR)
				|| "C".equalsIgnoreCase(docTypePR)
				|| "RCR".equalsIgnoreCase(docTypePR))) {
			obj.setTaxableValuePR(addNegativeSign(arr[32]));
			obj.setIgstPR(addNegativeSign(arr[34]));
			obj.setCgstPR(addNegativeSign(arr[36]));
			obj.setSgstPR(addNegativeSign(arr[38]));
			obj.setCessPR(addNegativeSign(arr[40]));
			obj.setTotalTaxPR(addNegativeSign(arr[42]));

		} else {
			obj.setTaxableValuePR(
					(arr[32] != null) ? arr[32].toString() : null);
			obj.setIgstPR((arr[34] != null) ? arr[34].toString() : null);
			obj.setCgstPR((arr[36] != null) ? arr[36].toString() : null);
			obj.setSgstPR((arr[38] != null) ? arr[38].toString() : null);
			obj.setCessPR((arr[40] != null) ? arr[40].toString() : null);
			obj.setTotalTaxPR((arr[42] != null) ? arr[42].toString() : null);

		}

		if (docTypePR != null && ("CR".equalsIgnoreCase(docTypePR)
				|| "C".equalsIgnoreCase(docTypePR)
				|| "RCR".equalsIgnoreCase(docTypePR))) {

			obj.setAvailableIGST(addNegativeSign(arr[45]));
			obj.setAvailableCGST(addNegativeSign(arr[46]));
			obj.setAvailableSGST(addNegativeSign(arr[47]));
			obj.setAvailableCESS(addNegativeSign(arr[48]));

		} else {
			obj.setAvailableIGST((arr[45] != null) ? arr[45].toString() : null);
			obj.setAvailableCGST((arr[46] != null) ? arr[46].toString() : null);
			obj.setAvailableSGST((arr[47] != null) ? arr[47].toString() : null);
			obj.setAvailableCESS((arr[48] != null) ? arr[48].toString() : null);

		}
		obj.setCess2B((arr[39] != null) ? arr[39].toString() : null);
		obj.setInvoiceValuePR((arr[44] != null) ? arr[44].toString() : null);
		obj.setTableType2B((arr[49] != null) ? arr[49].toString() : null);
		obj.setGSTR1FilingStatus((arr[50] != null) ? arr[50].toString() : null);
		obj.setGSTR1FilingDate((arr[51] != null) ? arr[51].toString() : null);
		obj.setGSTR1FilingPeriod(
				(arr[52] != null) ? "'".concat(arr[52].toString()) : null);
		obj.setGSTR3BFilingStatus(
				(arr[53] != null) ? arr[53].toString() : null);
		obj.setCancellationDate((arr[54] != null) ? arr[54].toString() : null);
		obj.setOrgInvAmendmentPeriod(
				(arr[55] != null) ? "'".concat(arr[55].toString()) : null);
		obj.setOrgAmendmentType((arr[56] != null) ? arr[56].toString() : null);
		obj.setCDNDelinkingFlag((arr[57] != null) ? arr[57].toString() : null);
		obj.setReverseChargeFlag2B(
				(arr[58] != null) ? arr[58].toString() : null);
		obj.setReverseChargeFlagPR(
				(arr[59] != null) ? arr[59].toString() : null);
		obj.setDifferentialPercentage2B(
				(arr[60] != null) ? arr[60].toString() : null);
		obj.setDifferentialPercentagePR(
				(arr[61] != null) ? arr[61].toString() : null);
		obj.setOrgDocNumber2B(addApostrophe(arr[62]));
		obj.setOrgDocNumberPR(addApostrophe(arr[63]));
		obj.setOrgDocDate2B((arr[64] != null) ? arr[64].toString() : null);
		obj.setOrgDocDatePR((arr[65] != null) ? arr[65].toString() : null);
		obj.setOrgSupplierGstinPR(
				(arr[66] != null) ? arr[66].toString() : null);
		obj.setOrgSupplierNamePR((arr[67] != null) ? arr[67].toString() : null);
		obj.setCRDRPreGST2B((arr[68] != null) ? arr[68].toString() : null);
		obj.setCRDRPreGSTPR((arr[69] != null) ? arr[69].toString() : null);
		obj.setBoeReferenceDate((arr[70] != null) ? arr[70].toString() : null);
		obj.setPortCode2B((arr[71] != null) ? arr[71].toString() : null);
		obj.setBillOfEntry2B((arr[72] != null) ? arr[72].toString() : null);
		obj.setBillOfEntryDate2B((arr[73] != null) ? arr[73].toString() : null);
		obj.setBoeAmended((arr[74] != null) ? arr[74].toString() : null);

		obj.setUserID((arr[75] != null) ? arr[75].toString() : null);
		obj.setSourceFileName((arr[76] != null) ? arr[76].toString() : null);
		obj.setProfitCentre1((arr[77] != null) ? arr[77].toString() : null);
		obj.setPlant((arr[78] != null) ? arr[78].toString() : null);
		obj.setDivision((arr[79] != null) ? arr[79].toString() : null);
		obj.setLocation((arr[80] != null) ? arr[80].toString() : null);
		obj.setPurchaseOrganisation(
				(arr[81] != null) ? arr[81].toString() : null);
		obj.setProfitCentre2((arr[82] != null) ? arr[82].toString() : null);
		obj.setProfitCentre3((arr[83] != null) ? arr[83].toString() : null);
		obj.setProfitCentre4((arr[84] != null) ? arr[84].toString() : null);
		obj.setProfitCentre5((arr[85] != null) ? arr[85].toString() : null);
		obj.setProfitCentre6((arr[86] != null) ? arr[86].toString() : null);
		obj.setProfitCentre7((arr[87] != null) ? arr[87].toString() : null);
		obj.setGLCodeAssessableValue(
				(arr[88] != null) ? arr[88].toString() : null);
		obj.setGLCodeIGST((arr[89] != null) ? arr[89].toString() : null);
		obj.setGLCodeCGST((arr[90] != null) ? arr[90].toString() : null);
		obj.setGLCodeSGST((arr[91] != null) ? arr[91].toString() : null);
		obj.setGLCodeAdvaloremCess(
				(arr[92] != null) ? arr[92].toString() : null);
		obj.setGLCodeSpecificCess(
				(arr[93] != null) ? arr[93].toString() : null);
		obj.setGLCodeStateCessAdvalorem(
				(arr[94] != null) ? arr[94].toString() : null);

		obj.setSupplyType2B((arr[95] != null) ? arr[95].toString() : null);
		obj.setSupplyTypePR((arr[96] != null) ? arr[96].toString() : null);
		obj.setSupplierType((arr[97] != null) ? arr[97].toString() : null);
		obj.setSupplierCode((arr[98] != null) ? arr[98].toString() : null);
		obj.setSupplierAddress1((arr[99] != null) ? arr[99].toString() : null);
		obj.setSupplierAddress2(
				(arr[100] != null) ? arr[100].toString() : null);
		obj.setSupplierLocation(
				(arr[101] != null) ? arr[101].toString() : null);
		obj.setSupplierPincode((arr[102] != null) ? arr[102].toString() : null);
		obj.setStateApplyingCess(
				(arr[103] != null) ? arr[103].toString() : null);
		obj.setPortCodePR((arr[104] != null) ? arr[104].toString() : null);
		obj.setBillOfEntryPR((arr[105] != null) ? arr[105].toString() : null);
		obj.setBillOfEntryDatePR(
				(arr[106] != null) ? arr[106].toString() : null);
		if (docTypePR != null && ("CR".equalsIgnoreCase(docTypePR)
				|| "C".equalsIgnoreCase(docTypePR)
				|| "RCR".equalsIgnoreCase(docTypePR))) {

			obj.setCIFValue(addNegativeSign(arr[107]));
			obj.setCustomDuty(addNegativeSign(arr[108]));
		} else {

			obj.setCIFValue((arr[107] != null) ? arr[107].toString() : null);
			obj.setCustomDuty((arr[108] != null) ? arr[108].toString() : null);
		}
		obj.setHSNorSAC((arr[109] != null) ? arr[109].toString() : null);
		obj.setProductCode((arr[110] != null) ? arr[110].toString() : null);
		obj.setProductDescription(
				(arr[111] != null) ? arr[111].toString() : null);
		obj.setCategoryOfProduct(
				(arr[112] != null) ? arr[112].toString() : null);
		obj.setUqc((arr[113] != null) ? arr[113].toString() : null);
		obj.setQuantity((arr[114] != null) ? arr[114].toString() : null);
		obj.setAdvaloremCessRate(
				(arr[115] != null) ? arr[115].toString() : null);

		if (docTypePR != null && ("CR".equalsIgnoreCase(docTypePR)
				|| "C".equalsIgnoreCase(docTypePR)
				|| "RCR".equalsIgnoreCase(docTypePR))) {

			obj.setAdvaloremCessAmount(addNegativeSign(arr[116]));
			obj.setSpecificCessAmount(addNegativeSign(arr[118]));
			obj.setStateCessAdvaloremAmount(addNegativeSign(arr[120]));
			obj.setItemOtherCharges(addNegativeSign(arr[121]));
		} else {
			obj.setAdvaloremCessAmount(
					(arr[116] != null) ? arr[116].toString() : null);
			obj.setSpecificCessAmount(
					(arr[118] != null) ? arr[118].toString() : null);
			obj.setStateCessAdvaloremAmount(
					(arr[120] != null) ? arr[120].toString() : null);
			obj.setItemOtherCharges(
					(arr[121] != null) ? arr[121].toString() : null);
		}

		obj.setSpecificCessRate(
				(arr[117] != null) ? arr[117].toString() : null);
		obj.setStateCessAdvaloremRate(
				(arr[119] != null) ? arr[119].toString() : null);
		obj.setClaimRefundFlag((arr[122] != null) ? arr[122].toString() : null);
		obj.setAutoPopulateToRefund(
				(arr[123] != null) ? arr[123].toString() : null);
		obj.setAdjustementReferenceNo(
				(arr[124] != null) ? arr[124].toString() : null);
		obj.setAdjustementReferenceDate(
				(arr[125] != null) ? arr[125].toString() : null);
		obj.setEligibilityIndicator(
				(arr[126] != null) ? arr[126].toString() : null);
		obj.setCommonSupplyIndicator(
				(arr[127] != null) ? arr[127].toString() : null);
		obj.setITCEntitlement((arr[128] != null) ? arr[128].toString() : null);
		obj.setITCReversalIdentifier(
				(arr[129] != null) ? arr[129].toString() : null);
		obj.setReasonForCreditDebitNote(
				(arr[130] != null) ? arr[130].toString() : null);
		obj.setAccountingVoucherNumber(
				(arr[131] != null) ? arr[131].toString() : null);
		obj.setAccountingVoucherDate(
				(arr[132] != null) ? arr[132].toString() : null);
		obj.setGlPostingDate((arr[133] != null) ? arr[133].toString() : null);
		obj.setCustomerPORefNumber(
				(arr[134] != null) ? arr[134].toString() : null);
		obj.setCustomerPORefDate(
				(arr[135] != null) ? arr[135].toString() : null);
		if (docTypePR != null && ("CR".equalsIgnoreCase(docTypePR)
				|| "C".equalsIgnoreCase(docTypePR)
				|| "RCR".equalsIgnoreCase(docTypePR))) {

			obj.setPurchaseOrderValue(addNegativeSign(arr[136]));
		} else {
			obj.setPurchaseOrderValue(
					(arr[136] != null) ? arr[136].toString() : null);
		}
		obj.setUserDefinedField1(
				(arr[137] != null) ? arr[137].toString() : null);
		obj.setUserDefinedField2(
				(arr[138] != null) ? arr[138].toString() : null);
		obj.setUserDefinedField3(
				(arr[139] != null) ? arr[139].toString() : null);
		obj.setUserDefinedField4(
				(arr[140] != null) ? arr[140].toString() : null);
		obj.setUserDefinedField5(
				(arr[141] != null) ? arr[141].toString() : null);
		obj.setUserDefinedField6(
				(arr[142] != null) ? arr[142].toString() : null);
		obj.setUserDefinedField7(
				(arr[143] != null) ? arr[143].toString() : null);
		obj.setUserDefinedField8(
				(arr[144] != null) ? arr[144].toString() : null);
		obj.setUserDefinedField9(
				(arr[145] != null) ? arr[145].toString() : null);
		obj.setUserDefinedField10(
				(arr[146] != null) ? arr[146].toString() : null);
		obj.setUserDefinedField11(
				(arr[147] != null) ? arr[147].toString() : null);
		obj.setUserDefinedField12(
				(arr[148] != null) ? arr[148].toString() : null);
		obj.setUserDefinedField13(
				(arr[149] != null) ? arr[149].toString() : null);
		obj.setUserDefinedField14(
				(arr[150] != null) ? arr[150].toString() : null);
		obj.setUserDefinedField15(
				(arr[151] != null) ? arr[151].toString() : null);
		obj.setUserDefinedField28(
				(arr[152] != null) ? arr[152].toString() : null);
		obj.setEWayBillNumber((arr[153] != null) ? arr[153].toString() : null);
		obj.setEWayBillDate((arr[154] != null) ? arr[154].toString() : null);
		obj.setMatchingID((arr[155] != null) ? arr[155].toString() : null);
		obj.setRequestID(
				(arr[156] != null) ? "'".concat(arr[156].toString()) : null);
		obj.setIDPR((arr[157] != null) ? arr[157].toString() : null);
		obj.setID2B((arr[158] != null) ? arr[158].toString() : null);
		obj.setInvoiceKeyUploadPR(
				(arr[159] != null) ? arr[159].toString() : null);
		obj.setInvoiceKeyUpload2B(
				(arr[160] != null) ? arr[160].toString() : null);

		obj.setReferenceIDPR((arr[161] != null) ? arr[161].toString() : null);
		obj.setReferenceID2B((arr[162] != null) ? arr[162].toString() : null);
		obj.setGenerationDate2B(
				(arr[163] != null) ? arr[163].toString() : null);
		obj.setItcAvailability2B(
				(arr[164] != null) ? arr[164].toString() : null);
		obj.setReasonForItcUnavailability2B(
				(arr[165] != null) ? arr[165].toString() : null);
		obj.setSourceTypeofIrn((arr[166] != null) ? arr[166].toString() : null);
		obj.setIrn2B((arr[167] != null) ? arr[167].toString() : null);
		obj.setIrnPR((arr[168] != null) ? arr[168].toString() : null);
		obj.setIrnDate2B((arr[169] != null) ? arr[169].toString() : null);
		obj.setIrnDatePR((arr[170] != null) ? arr[170].toString() : null);
		obj.setSourceIdentifier(
				(arr[171] != null) ? arr[171].toString() : null);

		obj.setItcReversedTaxPeriod(
				(arr[172] != null) ? arr[172].toString() : null);
		obj.setItcReclaimedTaxPeriod(
				(arr[173] != null) ? arr[173].toString() : null);
		obj.setGstr3bFilingDate(
				(arr[174] != null) ? arr[174].toString() : null);
		obj.setSupplierGSTINStatus(
				(arr[175] != null) ? arr[175].toString() : null);
		obj.setBillOfEntryCreatedDate2B(
				(arr[176] != null) ? arr[176].toString() : null);
		obj.setVendorTaxPaidVariance(
				(arr[177] != null) ? arr[177].toString() : null);
		obj.setVendorType((arr[178] != null) ? arr[178].toString() : null);
		obj.setHsnVendor((arr[179] != null) ? arr[179].toString() : null);
		obj.setVendorRiskCategory(
				(arr[180] != null) ? arr[180].toString() : null);
		obj.setVendorPaymentTermsDays(
				(arr[181] != null) ? arr[181].toString() : null);
		obj.setVendorRemarks((arr[182] != null) ? arr[182].toString() : null);
		obj.setSystemDefinedField1(
				(arr[183] != null) ? arr[183].toString() : null);
		obj.setSystemDefinedField2(
				(arr[184] != null) ? arr[184].toString() : null);
		obj.setSystemDefinedField3(
				(arr[185] != null) ? arr[185].toString() : null);
		obj.setSystemDefinedField4(
				(arr[186] != null) ? arr[186].toString() : null);
		obj.setSystemDefinedField5(
				(arr[187] != null) ? arr[187].toString() : null);

		return obj;

	}

	private String addNegativeSign(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return (value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString()
								: value.toString())
						: null);
			} else if (value instanceof Integer) {
				return (value != null
						? (((Integer) value > 0) ? "-" + value.toString()
								: value.toString())
						: null);
			} else if (value instanceof Long) {
				return (value != null
						? (((Long) value > 0) ? "-" + value.toString()
								: value.toString())
						: null);
			} else if (value instanceof BigInteger) {
				return (value != null
						? ((((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
								? "-" + value.toString()
								: value.toString())
						: null);
			} else {
				if (!value.toString().isEmpty()) {
					return "-" + value.toString().replaceFirst("-", "");
				} else {
					return null;
				}
			}
		}
		return null;
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

		storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_REPORT_DOWNLOAD_ID", downloadId);

		storedProc.registerStoredProcedureParameter("P_CHUNK_VALUE",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_VALUE", chunkValue);

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

	private StatefulBeanToCsv<GSTR2bprAutoReconErrInfoDTO> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<GSTR2bprAutoReconErrInfoDTO> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(GSTR2bprAutoReconErrInfoDTO.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<GSTR2bprAutoReconErrInfoDTO> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<GSTR2bprAutoReconErrInfoDTO> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
	}

	private String addApostrophe(Object o) {
		if (o != null) {
			if (!(o.toString().isEmpty())) {
				return DownloadReportsConstant.CSVCHARACTER
						.concat(o.toString());
			} else
				return null;
		}
		return null;
	}
}
