package com.ey.advisory.app.gstr1.einv;

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
import com.ey.advisory.app.data.entities.client.Gstr1PrVsSubmittedReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.EinvReconReportDownloadRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1EInvReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1PRvsSubReconGstinDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1SubmittedReconConfigRepository;
import com.ey.advisory.app.services.reports.AspProcessVsSubmitReportServiceImpl;
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
 * @author kiran s
 *
 */
@Slf4j
@Component("Gstr1PrVsSubmReconFetchReportDetailsImpl")
public class Gstr1PrVsSubmReconFetchReportDetailsImpl
		implements Gstr1PrVsSubmInitiateReconFetchReportDetails {

	@Autowired
	@Qualifier("EinvReconReportDownloadRepository")
	EinvReconReportDownloadRepository einvchunkrepo;

	@Autowired
	@Qualifier("Gstr1EInvReconConfigRepository")
	Gstr1EInvReconConfigRepository configRepo;

	@Autowired
	@Qualifier("Gstr1EInvReconConfigRepository")
	Gstr1EInvReconConfigRepository gstr1InvReconConfigRepo;

	@Autowired
	@Qualifier("Gstr1SubmittedReconConfigRepository")
	Gstr1SubmittedReconConfigRepository gstr1PrSubmReconConfigRepo;

	@Autowired
	@Qualifier("Gstr1PRvsSubReconGstinDetailsRepository")
	Gstr1PRvsSubReconGstinDetailsRepository Gstr1GstinDetailRepo;

	@Autowired
	@Qualifier("AspProcessVsSubmitReportServiceImpl")
	AspProcessVsSubmitReportServiceImpl aspProcessVsSubmitReport;

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

		//String reportType = "Gstr1_Pr_Vs_Submitted_Records_Report";

		Optional<Gstr1PrVsSubmittedReconConfigEntity> configData = gstr1PrSubmReconConfigRepo
				.findById(configId);
		List<String> gstnList = Gstr1GstinDetailRepo
				.findGstinsByReconConfigId(configId);
		String fromTaxPeriod = configData.get().getFromTaxPeriod();
		String toTaxPeriod = configData.get().getToTaxPeriod();
		Long entityId = configData.get().getEntityId();
		//String completedOn = configData.get().getCompletedOn().toString();
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
		String convertNow1 = date +" & " +time;
		downloadRequest.setGenOfTimeAndDate(convertNow1);
		downloadRequest.setTaxPeriodFrom(fromTaxPeriod);
		downloadRequest.setTaxPeriodTo(toTaxPeriod);
		List<Long> entityIdList=new ArrayList<Long>();
		entityIdList.add(entityId);
		downloadRequest.setEntityId(entityIdList);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put("GSTIN", gstnList);
		downloadRequest.setDataSecAttrs(dataSecAttrs);
		Workbook prVsSubmReport = aspProcessVsSubmitReport
				.AspProcessVsSubmitReports(downloadRequest, null);

		String updatedReportName = "Processed DigiGST vs Submitted"
				+ "_" + fromTaxPeriod + "_" + toTaxPeriod + "_"
				+ configId.toString();

		
		String uploadDocWithFileName = DocumentUtility
				.uploadDocumentWithFileName(prVsSubmReport, folderName,
						updatedReportName);

	

			if (uploadDocWithFileName != null) {
				gstr1PrSubmReconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.REPORT_GENERATED, uploadDocWithFileName,
						LocalDateTime.now(),
						configId);
			}
			
		} catch (Exception ex) {
			String message = String.format(
					"Exception while generating report in AspProcessVsSubmitReportServiceImpl ");
			LOGGER.error(message, ex);
			gstr1PrSubmReconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now(),
					configId);
			throw new AppException();
		} 
	}

	
}
