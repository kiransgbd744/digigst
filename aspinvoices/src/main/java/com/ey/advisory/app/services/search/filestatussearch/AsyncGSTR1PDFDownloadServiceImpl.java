package com.ey.advisory.app.services.search.filestatussearch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.services.pdf.Gstr1SummaryMultiPDFService;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SimpleDocGstnSummarySearchService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Component("AsyncGSTR1PDFDownloadServiceImpl")
@Slf4j
public class AsyncGSTR1PDFDownloadServiceImpl
		implements AsyncReportDownloadService {

	@Autowired
	private FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	@Qualifier("Gstr1SimpleDocGstnSummarySearchService")
	Gstr1SimpleDocGstnSummarySearchService tableSearchService;

	@Autowired
	@Qualifier("Gstr1SummaryMultiPDFService")
	Gstr1SummaryMultiPDFService gstr1SummaryMultiPDFService;

	@Override
	public void generateReports(Long id) {

		File tempDir = null;
		try {
			

			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);

			String reqPayload = optEntity.get().getReqPayload();

			JsonObject obj = JsonParser.parseString(reqPayload)
					.getAsJsonObject();

			boolean isDigigst = obj.get("isDigigst").getAsBoolean();
			
			boolean isGstr1a = obj.get("isGstr1a").getAsBoolean();

			if(Boolean.TRUE.equals(isGstr1a)){
				tempDir = createTempDir1A();
			} else {
				tempDir = createTempDir();
			}
			
			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory";
				LOGGER.debug(msg);
			}
			Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
			Annexure1SummaryReqDto req = gsonEwb.fromJson(obj.toString(),
					Annexure1SummaryReqDto.class);
			Annexure1SummaryReqDto annexure1SummaryRequest = processedRecordsCommonSecParam
					.setGstr1DataSecuritySearchParams(req);
			annexure1SummaryRequest.setIsGstr1a(isGstr1a);
			HashMap<String, List<String>> gstinMap = new HashMap<>();

			int noDataCounter = 0;
			int reportFailedCounter = 0;
			for (String sGstin : annexure1SummaryRequest.getDataSecAttrs()
					.get("GSTIN")) {
				gstinMap.put(OnboardingConstant.GSTIN, Arrays.asList(sGstin));
				annexure1SummaryRequest.setDataSecAttrs(gstinMap);
				SearchResult<Gstr1CompleteSummaryDto> gstnSumryResult = (SearchResult<Gstr1CompleteSummaryDto>) tableSearchService
						.find(annexure1SummaryRequest, null,
								Gstr1CompleteSummaryDto.class);
				LOGGER.debug("OUTWARD GSTN Data Summary Execution END ");

				List<? extends Gstr1CompleteSummaryDto> gstnResult = gstnSumryResult
						.getResult();
				if (gstnResult.isEmpty()) {
					noDataCounter++;
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = "calling generateGstr1SummaryPdfZip method";
					LOGGER.debug(msg);
				}
				String responsePdf = gstr1SummaryMultiPDFService
						.generateGstr1SummaryPdfZip(tempDir,
								annexure1SummaryRequest, gstnResult, sGstin, id,
								isDigigst, isGstr1a);
				
				if (LOGGER.isDebugEnabled()) {
					String msg = " generateGstr1SummaryPdfZip method completed successfully";
					LOGGER.debug(msg);
				}
				if (responsePdf.equalsIgnoreCase("FAILED")) {
					reportFailedCounter++;
				}

			}
			LOGGER.debug("GSTN Data Summary Execution BEGIN ");

			if (noDataCounter == req.getDataSecAttrs()
					.get(OnboardingConstant.GSTIN).size()) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				return;
			}
			if (reportFailedCounter == req.getDataSecAttrs()
					.get(OnboardingConstant.GSTIN).size()) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATION_FAILED, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				return;
			}

			String zipFileName = gstr1SummaryMultiPDFService
					.zipEinvoicePdfFiles(tempDir, id, isGstr1a);
			File zipFile = new File(tempDir, zipFileName);
		
			if (LOGGER.isDebugEnabled()) {
				String msg = " uploadeding  PDF file in to the remote location";
				LOGGER.debug(msg);
			}
			
			/*String uploadedFileName = DocumentUtility.uploadZipFile(zipFile,
					"PdfGstr1Reports");*/
			Pair<String, String> uploadedZipName = null;
			if(Boolean.TRUE.equals(isGstr1a)){
				uploadedZipName = DocumentUtility
						.uploadFile(zipFile, "PdfGstr1AReports");
			} else {
				uploadedZipName = DocumentUtility
						.uploadFile(zipFile, "PdfGstr1Reports");
			}
			String uploadedFileName = uploadedZipName.getValue0();
			String docId = uploadedZipName.getValue1();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Sucessfully uploaded PDF file and updating the "
						+ "status as 'Report Generated'";
				LOGGER.debug(msg);
			}

			fileStatusDownloadReportRepo.updateStatus(id, ReportStatusConstants.REPORT_GENERATED,
					uploadedFileName, LocalDateTime.now(),docId);

		} catch (Exception e) {
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			String msg = "Exception occued while while generating PDF file";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("PdfGstr1Reports").toFile();
	}
	
	private static File createTempDir1A() throws IOException {
		return Files.createTempDirectory("PdfGstr1AReports").toFile();
	}
}
