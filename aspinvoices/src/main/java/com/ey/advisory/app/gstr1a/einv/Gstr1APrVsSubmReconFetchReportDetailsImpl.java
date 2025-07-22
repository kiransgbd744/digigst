package com.ey.advisory.app.gstr1a.einv;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1APrVsSubmittedReconConfigEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1APRvsSubReconGstinDetailsRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ASubmittedReconConfigRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Component("Gstr1APrVsSubmReconFetchReportDetailsImpl")
public class Gstr1APrVsSubmReconFetchReportDetailsImpl
		implements Gstr1APrVsSubmInitiateReconFetchReportDetails {

	@Autowired
	@Qualifier("Gstr1ASubmittedReconConfigRepository")
	Gstr1ASubmittedReconConfigRepository gstr1PrSubmReconConfigRepo;

	@Autowired
	@Qualifier("Gstr1APRvsSubReconGstinDetailsRepository")
	Gstr1APRvsSubReconGstinDetailsRepository Gstr1GstinDetailRepo;

	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitReportServiceImpl")
	Gstr1AAspProcessVsSubmitReportServiceImpl aspProcessVsSubmitReport;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Override
	public void getPrVSSubmiReconReportData(Long configId) throws IOException {

		try {
			String msg = null;
			String folderName = "PrVsSubmittedReport";
			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Get Gstr1 PR vs Submitted Initiate Report Details with configId:'%s'",
						configId.toString());
				LOGGER.debug(msg);
			}

			// String reportType = "Gstr1_Pr_Vs_Submitted_Records_Report";

			Optional<Gstr1APrVsSubmittedReconConfigEntity> configData = gstr1PrSubmReconConfigRepo
					.findById(configId);
			List<String> gstnList = Gstr1GstinDetailRepo
					.findGstinsByReconConfigId(configId);
			String fromTaxPeriod = configData.get().getFromTaxPeriod();
			String toTaxPeriod = configData.get().getToTaxPeriod();
			Long entityId = configData.get().getEntityId();
			// String completedOn =
			// configData.get().getCompletedOn().toString();
			Gstr1ReviwSummReportsReqDto downloadRequest = new Gstr1ReviwSummReportsReqDto();
			String date = null;
			String time = null;
			String dateAndTime = null;
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy");
			DateTimeFormatter FOMATTER1 = DateTimeFormatter
					.ofPattern("HH:mm:ss");
			DateTimeFormatter FOMATTER2 = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			date = FOMATTER.format(istDateTimeFromUTC);
			time = FOMATTER1.format(istDateTimeFromUTC);
			dateAndTime = FOMATTER2.format(istDateTimeFromUTC);
			String convertNow = date + time;
			String convertNow1 = date + " & " + time;
			downloadRequest.setGenOfTimeAndDate(convertNow1);
			downloadRequest.setTaxPeriodFrom(fromTaxPeriod);
			downloadRequest.setTaxPeriodTo(toTaxPeriod);
			List<Long> entityIdList = new ArrayList<Long>();
			entityIdList.add(entityId);
			downloadRequest.setEntityId(entityIdList);
			Map<String, List<String>> dataSecAttrs = new HashMap<>();
			dataSecAttrs.put("GSTIN", gstnList);
			downloadRequest.setDataSecAttrs(dataSecAttrs);
			Workbook prVsSubmReport = aspProcessVsSubmitReport
					.AspProcessVsSubmitReports(downloadRequest, null);

			String updatedReportName = "GSTR1A_Processed DigiGST vs Submitted" + "_"
					+ fromTaxPeriod + "_" + toTaxPeriod + "_"
					+ configId.toString();

			String uploadDocWithFileName = DocumentUtility
					.uploadDocumentWithFileName(prVsSubmReport, folderName,
							updatedReportName);

			if (uploadDocWithFileName != null) {
				gstr1PrSubmReconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.REPORT_GENERATED,
						uploadDocWithFileName, LocalDateTime.now(), configId);
			}

		} catch (Exception ex) {
			String message = String.format(
					"Exception while generating report in AspProcessVsSubmitReportServiceImpl ");
			LOGGER.error(message, ex);
			gstr1PrSubmReconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now(), configId);
			throw new AppException();
		}
	}

}
