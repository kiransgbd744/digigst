package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.repositories.client.Gstr1Vs3bConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1Vs3bStatusRepository;
import com.ey.advisory.app.services.reports.Gstr1vs3bReviewSummaryReportHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Slf4j
@Component("Gstr1Vs3bReportProcessor")
public class Gstr1Vs3bReportProcessor implements TaskProcessor {

	@Autowired
	CommonUtility commonUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier(value = "Gstr1Vs3bConfigRepository")
	private Gstr1Vs3bConfigRepository gstr1Vs3bConfigRepo;

	@Autowired
	@Qualifier(value = "Gstr1Vs3bStatusRepository")
	private Gstr1Vs3bStatusRepository gstr1Vs3bStatusRepo;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	@Qualifier("Gstr1vs3bReviewSummaryReportHandler")
	private Gstr1vs3bReviewSummaryReportHandler gstr1vs3bReviewSummaryReportHandler;

	private static final String GSTR1VsGstr3BRepo = "Gstr1Vs3bDownloadRepo";

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Begin Gstr1Vs3bReportProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}
		
		
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();
		
		Gson gson = new Gson();
		
		
		try {
			JsonObject reqJson = json.get("reqDto").getAsJsonObject();

			Gstr1VsGstr3bProcessSummaryReqDto criteria = gson.fromJson(reqJson,
					Gstr1VsGstr3bProcessSummaryReqDto.class);			
			
			criteria.setConfigId(configId);

			/**
			 * Start - Set Data Security Attributes
			 */
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");

			Gstr1VsGstr3bProcessSummaryReqDto reqDto = processedRecordsCommonSecParam
					.setGstr1VsGstr3bDataSecuritySearchParams(criteria);

			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");

			DateTimeFormatter format = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			LocalDateTime timeOfGeneration = LocalDateTime.now();
			LocalDateTime convertISDDate = EYDateUtil
					.toISTDateTimeFromUTC(timeOfGeneration);
			String fileName = null;
			Workbook workbook = null;

			workbook = gstr1vs3bReviewSummaryReportHandler
					.findReviewSummaryData(reqDto);
			
			gstr1Vs3bConfigRepo.updateGstr1Vs3BReconStatus(
					ReportStatusConstants.REPORT_GENERATION_INPROGRESS,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					configId, null);
			fileName = "GSTR1&1AvsGSTR3B_"
					+ criteria.getTaxPeriodFrom() + "_"
					+ criteria.getTaxPeriodTo() + "_"
					+ format.format(convertISDDate)+".xlsx";

			if (workbook == null) {
				workbook = new Workbook();
			}
			String workBookPath = workbook.getFileName();
			String wrkFleName = workBookPath.substring(workBookPath.lastIndexOf('/') + 1);
			
			
			LOGGER.debug("woorkBook fileName ",wrkFleName);
			
			String uploadFileName = DocumentUtility.uploadDocumentWithFileName(
					workbook, GSTR1VsGstr3BRepo,fileName);
			
			gstr1Vs3bConfigRepo.updateGstr1Vs3BReconStatus(
					ReconStatusConstants.REPORT_GENERATED,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					configId, fileName);

		} catch (Exception ex) {
			String msg = String.format(
					"Config Id is '%s', Exception occured during initiate recon for gstr1Vs3B",
					configId.toString());

			LOGGER.debug(msg);
			LOGGER.error(msg, ex);
			gstr1Vs3bConfigRepo.updateGstr1Vs3BReconStatus(
					ReconStatusConstants.REPORT_GENERATION_FAILED,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					configId, null);

			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);


		}

	}
}
