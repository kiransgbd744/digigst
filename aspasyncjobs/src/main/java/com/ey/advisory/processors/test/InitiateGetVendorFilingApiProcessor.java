package com.ey.advisory.processors.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.asprecon.VendorComplianceRatingAsyncApiResponseEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.VendoComplianceAsyncApiRepository;
import com.ey.advisory.app.data.services.compliancerating.VendorAsyncApiGstinDto;
import com.ey.advisory.app.data.services.compliancerating.VendorAsyncApiRequestDto;
import com.ey.advisory.app.data.services.compliancerating.VendorComplianceRatingAsyncReportDto;
import com.ey.advisory.app.data.services.noncomplaintvendor.VendorCommunicationApiService;
import com.ey.advisory.app.sftp.service.SFTPFileTransferService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("InitiateGetVendorFilingApiProcessor")
public class InitiateGetVendorFilingApiProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("VendoComplianceAsyncApiRepository")
	private VendoComplianceAsyncApiRepository apiRepo;

	@Autowired
	@Qualifier("VendorCommunicationApiServiceImpl")
	private VendorCommunicationApiService vendorCommunicationApiService;

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;

	@Autowired
	CommonUtility commonUtility;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long id = json.get("id").getAsLong();
		Optional<VendorComplianceRatingAsyncApiResponseEntity> optEntity = apiRepo
				.findById(id);
		if (!optEntity.isPresent()) {
			throw new EntityNotFoundException("VendorComplianceRatingAsyncApiResponseEntity not found for ID: " + id);
		}
		VendorComplianceRatingAsyncApiResponseEntity entity = optEntity.get();
		Long entityId = entity.getEntityId();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Inside Vendor Compliance Rating Async "
					+ " processor with Report id : %d", id);
			LOGGER.debug(msg);
		}
		String reuestPayLoad = GenUtil
				.convertClobtoString(entity.getRequestPayload());
		Gson gson = GsonUtil.newSAPGsonInstance();
		VendorAsyncApiRequestDto dto = gson.fromJson(reuestPayLoad,
				VendorAsyncApiRequestDto.class);

		Set<String> vendorGstinsSet = new HashSet<>();
		for (VendorAsyncApiGstinDto apiDto : dto.getGstins()) {
			vendorGstinsSet.add(apiDto.getVendorGstin());
		}
        List<String> vendorGstins = new ArrayList<>(vendorGstinsSet);
		String fy = dto.getFy();
		try {
			vendorCommunicationApiService
					.persistGstnApiForSelectedFinancialYear(dto, vendorGstins,
							id, entityId);

			if (entity.getUploadMode().equalsIgnoreCase("SFTP")) {
				String fileName = entity.getFileName();
				String[] fileNameParts = fileName.split("\\.");
				Optional<VendorComplianceRatingAsyncApiResponseEntity> updatedEntity = apiRepo
						.findById(id);
				if (!updatedEntity.isPresent()) {
				    LOGGER.error("Config data not found for configId: {}", id);
				    throw new AppException("Config data not found for configId: " + id);
				}
				String clobValue = GenUtil.convertClobtoString(
						updatedEntity.get().getResponsePayload());

				ObjectMapper objectMapper = new ObjectMapper();
				List<VendorComplianceRatingAsyncReportDto> dtoList = new ArrayList<>();
				try {
					// Parse JSON array into array of
					// VendorComplianceRatingAsyncReportDto
					VendorComplianceRatingAsyncReportDto[] dtos = objectMapper
							.readValue(clobValue,
									VendorComplianceRatingAsyncReportDto[].class);

					// Convert array to List
					for (VendorComplianceRatingAsyncReportDto reportdto : dtos) {
						dtoList.add(reportdto);
					}
				} catch (Exception e) {
					LOGGER.error("An error occurred while processing the request "
							+ "in InitiateGetVendorFilingApiProcessor", e);
				}

				File tempDir = null;
				tempDir = createTempDir();
				String fullPath = tempDir.getAbsolutePath() + File.separator;
				if (dtoList != null) {
					Pair<String, String> gstinValidatorOnFile = writeToExcel(
							dtoList, fullPath, fy);

					String filePath = gstinValidatorOnFile.getValue0();
					String docId = gstinValidatorOnFile.getValue1();
					if (filePath != null) {
						Document document = null;
						document = DocumentUtility
								.downloadDocumentByDocId(docId);
						File outputFile = new File(filePath);
						
						InputStream contentStream = document.getContentStream()
								.getStream();
						try (OutputStream outputStream = FileUtils
								.openOutputStream(outputFile)) {
							byte[] buffer = new byte[1024];
							int bytesRead;
							while ((bytesRead = contentStream
									.read(buffer)) != -1) {
								outputStream.write(buffer, 0, bytesRead);
							}
						}
						if (!outputFile.exists()) {
							outputFile.mkdirs();
						}
						String fileNameToBeUploaded = 
								fileNameParts[0] + "_" + outputFile.getName();
						File newFile = new File(fileNameToBeUploaded);
						Files.move(outputFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("outputFile {}",
									newFile.getAbsolutePath());
						}
						sftpService.uploadFile(newFile.getAbsolutePath(),
								"SFTP_ROOT/INTG_OUTBOUND/VENDOR_COMPLIANCE_RATING");
					}
				}
				apiRepo.updateStatusSummary(id,updatedEntity.get().getResponsePayload(),
						"Completed");
			}
		} catch (Exception ex) {
			String msg = "Exception occured while downloading csv report";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	private Pair<String, String> writeToExcel(
			List<VendorComplianceRatingAsyncReportDto> reportDtos,
			String fullPath, String fy) {
		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		String folderName = "VendorRatingReport";
		Pair<String, String> downloadFileDetails = null;
		try {
			if (reportDtos != null && !reportDtos.isEmpty()) {
				String template = null;
				String[] invoiceHeaders = null;
				invoiceHeaders = commonUtility
						.getProp("vendor.compliance.sftp.rating.data")
						.split(",");
				template = "VendorComplianceHistory.xlsx";
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", template);
				if (LOGGER.isDebugEnabled()) {
					String msg = "InitiateGetVendorFilingSFTPProcessor."
							+ "writeToExcel workbook created writing data "
							+ "to the workbook";
					LOGGER.debug(msg);
				}

				Cells reportCells = workbook.getWorksheets().get(0).getCells();

				reportCells.importCustomObjects(reportDtos, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn,
						reportDtos.size(), true, "yyyy-mm-dd", false);

				if (LOGGER.isDebugEnabled()) {
					String msg = "InitiateGetVendorFilingApiProcessor.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}

				downloadFileDetails = DocumentUtility
						.uploadDocumentAndReturnDocID(workbook, folderName,
								"XLSX");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " response list in the directory : {}",
							workbook.getAbsolutePath());
				}
			} else {
				throw new AppException(
						"No records found, cannot generate report");
			}
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage(), e);
		}

		return downloadFileDetails;
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}
}
