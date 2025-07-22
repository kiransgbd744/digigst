package com.ey.advisory.controllers.anexure1;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.search.reports.BasicCommonSecParamReports;
import com.ey.advisory.app.services.reports.Anx1ApiErrorReportHandler;
import com.ey.advisory.app.services.reports.Anx1ApiProcessedInfoReportHandler;
import com.ey.advisory.app.services.reports.Anx1ApiProcessedReportHandler;
import com.ey.advisory.app.services.reports.Anx1ApiTotalRecordsReportHandler;
import com.ey.advisory.app.services.reports.Anx1InwardApiErrorRecordsReportHandler;
import com.ey.advisory.app.services.reports.Anx1InwardApiProcessedInfoReportHandler;
import com.ey.advisory.app.services.reports.Anx1InwardApiProcessedReportHandler;
import com.ey.advisory.app.services.reports.Anx1InwardApiTotalReportHandler;
import com.ey.advisory.app.services.reports.Anx1OutwardApiDataStatusSummaryReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class Anx1ApiDownloadReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ApiDownloadReportsController.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;
	
	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPrmtRepository;

	@Autowired
	@Qualifier("BasicCommonSecParamReports")
	BasicCommonSecParamReports basicCommonSecParamReports;

	@Autowired
	@Qualifier("Anx1ApiProcessedReportHandler")
	private Anx1ApiProcessedReportHandler anx1ApiProcessedReportHandler;

	@Autowired
	@Qualifier("Anx1ApiProcessedInfoReportHandler")
	private Anx1ApiProcessedInfoReportHandler anx1ApiProcessedInfoReportHandler;

	@Autowired
	@Qualifier("Anx1ApiErrorReportHandler")
	private Anx1ApiErrorReportHandler anx1ApiErrorReportHandler;

	@Autowired
	@Qualifier("Anx1ApiTotalRecordsReportHandler")
	private Anx1ApiTotalRecordsReportHandler anx1ApiTotalRecordsReportHandler;

	@Autowired
	@Qualifier("Anx1InwardApiProcessedReportHandler")
	private Anx1InwardApiProcessedReportHandler anx1InwardApiProcessedReportHandler;

	@Autowired
	@Qualifier("Anx1InwardApiTotalReportHandler")
	private Anx1InwardApiTotalReportHandler anx1InwardApiTotalReportHandler;

	@Autowired
	@Qualifier("Anx1InwardApiErrorRecordsReportHandler")
	private Anx1InwardApiErrorRecordsReportHandler anx1InwardApiErrorRecordsReportHandler;

	@Autowired
	@Qualifier("Anx1InwardApiProcessedInfoReportHandler")
	private Anx1InwardApiProcessedInfoReportHandler anx1InwardApiProcessedInfoReportHandler;

	@Autowired
	@Qualifier("Anx1OutwardApiDataStatusSummaryReportHandler")
	private Anx1OutwardApiDataStatusSummaryReportHandler anx1OutwardApiDataStatusSummaryReportHandler;

	@RequestMapping(value = "/ui/downloadApiReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadApiProcessedReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		
		try {
			String fileName = null;
			Workbook workbook = null;
			Anx1ReportSearchReqDto criteria = gson.fromJson(json,
					Anx1ReportSearchReqDto.class);
			
			List<Object> cutoffPeriods = entityConfigPrmtRepository
					.findByEntityQtnCode(criteria.getEntityId());
			if (cutoffPeriods != null && !cutoffPeriods.isEmpty()) {
				Integer cutoffPeriod = Integer
						.parseInt(String.valueOf(cutoffPeriods.get(0)));
				criteria.setAnswer(cutoffPeriod);
			}else{
				criteria.setAnswer(202010);
			}
			/**
			 * Start - Set Data Security Attributes
			 */
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");

			Anx1ReportSearchReqDto setDataSecurity = basicCommonSecParamReports
					.setDataSecuritySearchParams(criteria);

			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");
			/**
			 * End - Set Data Security Attributes
			 */

			if (criteria.getDataType() != null && criteria.getDataType()
					.equalsIgnoreCase(DownloadReportsConstant.OUTWARD)) {
				if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
					workbook = anx1ApiProcessedReportHandler
							.downloadApiProcessedReport(setDataSecurity);
					if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.PROCESSEDACTIVE)) {
						fileName = "OutwardApiProcessedActiveRecords";
					} else if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.PROCESSEDINACTIVE)) {
						fileName = "OutwardApiProcessedInactiveRecords";
					}
				} else if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
					workbook = anx1ApiErrorReportHandler
							.downloadApiErrorReport(setDataSecurity);
					if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.ERRORACTIVE)) {
						fileName = "OutwardApiErrorActiveReport";
					} else if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.ERRORINACTIVE)) {
						fileName = "OutwardApiErrorInactiveReport";
					}
				}

				else if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSEDINFO)) {
					workbook = anx1ApiProcessedInfoReportHandler
							.downloadApiProcessedInfoReport(setDataSecurity);
					if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.PROCESSEDINFOACTIVE)) {
						fileName = "OutwardApiProcessedInfoActiveReport";
					} else if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.PROCESSEDINFOINACTIVE)) {
						fileName = "OutwardApiProcessedInfoInactiveReport";
					}
				}
				if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.TOTALERECORDS)) {
					workbook = anx1ApiTotalRecordsReportHandler
							.downloadApiTotalRecordsReport(setDataSecurity);
					fileName = "OutwardApiTotalRecordsReport";
				}
				if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.DATASTATUSSUMMARY)) {
					workbook = anx1OutwardApiDataStatusSummaryReportHandler
							.downloadDatastatusSummary(setDataSecurity);
					fileName = "OutwardApiDataStatusSummaryReport";
				}
			}
			if (criteria.getDataType() != null && criteria.getDataType()
					.equalsIgnoreCase(DownloadReportsConstant.INWARD)) {

				if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.TOTALERECORDS)) {
					workbook = anx1InwardApiTotalReportHandler
							.downloadInwardApiTotalRecordsReport(
									setDataSecurity);
					fileName = "InwardApiTotalRecordsReport";
				}
				if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
					workbook = anx1InwardApiProcessedReportHandler
							.downloadApiProcessedReport(setDataSecurity);
					if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.PROCESSEDACTIVE)) {
						fileName = "InwardApiProcessedActiveReport";
					} else if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.PROCESSEDINACTIVE)) {
						fileName = "InwardApiProcessedInactiveReport";
					}
				}
				if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSEDINFO)) {
					workbook = anx1InwardApiProcessedInfoReportHandler
							.downloadApiProcessedInfoReport(setDataSecurity);
					if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.PROCESSEDINFOACTIVE)) {
						fileName = "InwardApiProcessedInfoActiveReport";
					} else if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.PROCESSEDINFOINACTIVE)) {
						fileName = "InwardApiProcessedInfoInactiveReport";
					}
				}

				if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
					workbook = anx1InwardApiErrorRecordsReportHandler
							.downloadApiInwardErrorRecordsReport(
									setDataSecurity);

					if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.ERRORACTIVE)) {
						fileName = "InwardApiErrorActiveReport";
					} else if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.ERRORINACTIVE)) {
						fileName = "InwardApiErrorInactiveReport";
					}
				}
			}

			if (workbook == null) {
				workbook = new Workbook();
			}
			if (fileName != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving download report ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}

}
