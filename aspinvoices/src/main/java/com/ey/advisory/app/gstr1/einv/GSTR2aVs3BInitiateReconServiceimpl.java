package com.ey.advisory.app.gstr1.einv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.Gstr2Avs3bReconEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2Avs3BReconRepository;
import com.ey.advisory.app.services.reports.Gstr2Avs3bReviewSummaryReportHandler;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran.s
 *
 */

@Slf4j
@Component("GSTR2aVs3BInitiateReconServiceimpl")
public class GSTR2aVs3BInitiateReconServiceimpl  {

	@Autowired
	CommonUtility commonUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr2Avs3bReviewSummaryReportHandler")
	private Gstr2Avs3bReviewSummaryReportHandler gstr2Avs3bReviewSummaryReportHandler;

	@Autowired
	@Qualifier("Gstr2Avs3BReconRepository")
	private Gstr2Avs3BReconRepository gstr2Avs3BReconRepository;

	public void gstr2aVS3bReportDownload(Gson gson, Gstr2Avs3bReconEntity req,
			String response,Long requestId) {
		File tempDir = null;

		try {
			String fileName = null;
			Workbook workbook = null;
			String date = null;
			String time = null;
			tempDir = createTempDir();
			
			Gstr1VsGstr3bProcessSummaryReqDto criteria = new Gstr1VsGstr3bProcessSummaryReqDto();
			Map<String, List<String>> gstinmap = new HashMap<>();
			
			String clobString = clobToString(req.getGstins());
	        List<String> gstinList = Arrays.asList(clobString.split(","));
	        // Store in Map
	        gstinmap.put("GSTIN", gstinList);
			List entityidList= new ArrayList<>();
			entityidList.add(req.getEntityId());
			criteria.setEntityId(entityidList);
			criteria.setTaxPeriodFrom(req.getFromTaxPeriod());
			criteria.setTaxPeriodTo(req.getToTaxPeriod());
			criteria.setDataSecAttrs(gstinmap);
			//criteria.setType(req.getReconType());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("ddMMyyyy");
			DateTimeFormatter FOMATTER1 = DateTimeFormatter.ofPattern("HHmmss");
			date = FOMATTER.format(istDateTimeFromUTC);
			time = FOMATTER1.format(istDateTimeFromUTC);

			LocalDateTime utcDateTime = LocalDateTime.now();
			ZoneId istZone = ZoneId.of("Asia/Kolkata");
			LocalDateTime istDateTime = utcDateTime.atZone(ZoneId.of("UTC"))
					.withZoneSameInstant(istZone).toLocalDateTime();

			DateTimeFormatter dtf = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			String timeMilli = dtf.format(istDateTime);
			workbook = gstr2Avs3bReviewSummaryReportHandler
					.findReviewSummaryData(criteria);

			fileName = tempDir.getAbsolutePath() + File.separator
					+ "GSTR2AvsGSTR3B"
					+ "_" + requestId + "_" + timeMilli + ".xlsx";
			//fileName = "GSTR2AvsGSTR3B" + "_" + requestId;

			workbook.save(fileName);
			String zipFileName = zipEinvoicePdfFiles(tempDir, timeMilli,
					req.getFromTaxPeriod(), req.getToTaxPeriod(),requestId);
			if (LOGGER.isDebugEnabled()) {
				String str = String.format(
						"zipFile name before file upload is : %s",
						zipFileName);
				LOGGER.debug(str);
			}
			File zipFile = new File(tempDir, zipFileName);

			Pair<String, String> uploadedDocName = DocumentUtility
					.uploadFile(zipFile,
							ConfigConstants.GSTR6_CRED_REPORT_DOWNLOAD);
			String uploadedFileName = uploadedDocName.getValue0();
			String docId = uploadedDocName.getValue1();
			if (docId != null) {
				gstr2Avs3BReconRepository.UpdateGstr2Avs3BRecon(
						ReconStatusConstants.REPORT_GENERATION_COMPLETED, null,
						LocalDateTime.now(), requestId, null);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr2a vs 3b report uploaded with docid and uploadedFileName ",
							docId, uploadedFileName);
				}

				gstr2Avs3BReconRepository.UpdateGstr2Avs3BRecon(
						ReconStatusConstants.REPORT_GENERATED, null,
						LocalDateTime.now(), requestId, docId);
			}
			LOGGER.debug("Uploaded FileName: {}, DocId: {}", uploadedFileName,
					docId);

		}  catch (Exception ex) {
			String msg = "Unexpected error while retriving "
					+ "Data from Report ";
			gstr2Avs3BReconRepository.UpdateGstr2Avs3BRecon(
					ReconStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now(), requestId, null);
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
	}
	private String zipEinvoicePdfFiles(File tempDir, String dateAndTime,
			String fromtaxPeriod, String toTaxPeriod,Long requestId) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " getting files to be zipped from temp directory";
			LOGGER.debug(msg);
		}
		String fileName = null;
		List<String> filesToZip = getAllFilesToBeZipped(tempDir);

		fileName = "GSTR2AvsGSTR3B"
				+ "_" + requestId;

		String compressedFileName = fileName;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("compressedFileName : %s",
					compressedFileName);
			LOGGER.debug(msg);
		}

		CombineAndZipXlsxFiles.compressFiles(tempDir.getAbsolutePath(),
				compressedFileName + ".zip", filesToZip);
		String zipFileName = compressedFileName + ".zip";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("zipFileName : %s", zipFileName);
			LOGGER.debug(msg);
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = " zipping is successful";
			LOGGER.debug(msg);
		}
		return zipFileName;

	}

	private static List<String> getAllFilesToBeZipped(File tmpDir) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Temporary directory containing files to "
							+ "be zipped: %s", tmpDir.getAbsolutePath());
			LOGGER.debug(msg);
		}
		FilenameFilter pdfFilter = new FilenameFilter() {
			public boolean accept(File tmpDir, String name) {
				return name.toLowerCase().endsWith(".xlsx");
			}
		};
		File[] files = tmpDir.listFiles(pdfFilter);
		List<String> retFileNames = Arrays.stream(files)
				.map(f -> f.getAbsolutePath())
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("List of files to be zipped %s",
					retFileNames);
			LOGGER.debug(msg);
		}
		return retFileNames;
	}

	private static File createTempDir() throws IOException {
		return Files
				.createTempDirectory(ConfigConstants.GSTR6_CRED_REPORT_DOWNLOAD)
				.toFile();
	}
	
    public int proceCallForComputeReversal(final String gstin,
            final Integer derivedRetPerFrom, final Integer derivedRetPerTo) {
        int count = 0;
        try {
            StoredProcedureQuery storedProcQuery = entityManager
                    .createStoredProcedureQuery("COMPUTE_GSTR2A_VS_3B");
            storedProcQuery.registerStoredProcedureParameter("GSTIN",
                    String.class, ParameterMode.IN);
            storedProcQuery.registerStoredProcedureParameter(
                    "FROM_DERIVED_RET_PERIOD", Integer.class, ParameterMode.IN);
            storedProcQuery.registerStoredProcedureParameter(
                    "TO_DERIVED_RET_PERIOD", Integer.class, ParameterMode.IN);
            storedProcQuery.setParameter("FROM_DERIVED_RET_PERIOD",
                    derivedRetPerFrom);
            storedProcQuery.setParameter("TO_DERIVED_RET_PERIOD",
                    derivedRetPerTo);
            storedProcQuery.setParameter("GSTIN", gstin);
            storedProcQuery.execute();
            count = 1 + count;

        } catch (Exception e) {

            LOGGER.debug("Exception Occured:", e);
        }
        return count;
    }

	   private String clobToString(Clob clob) throws SQLException, IOException {
	        StringBuilder sb = new StringBuilder();
	        try (Reader reader = clob.getCharacterStream();
	             BufferedReader br = new BufferedReader(reader)) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                sb.append(line);
	            }
	        }
	        return sb.toString();
	    }
}
