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
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.Anx1DataStatusSummaryReportHandler;
import com.ey.advisory.app.services.reports.Anx1ErrorReportHandler;
import com.ey.advisory.app.services.reports.Anx1InwardErrorReportHandler;
import com.ey.advisory.app.services.reports.Anx1InwardProcessedInfoReportHandler;
import com.ey.advisory.app.services.reports.Anx1InwardProcessedReportHandler;
import com.ey.advisory.app.services.reports.Anx1InwardTotalRecordsReportHandler;
import com.ey.advisory.app.services.reports.Anx1ProcessedInfoReportHandler;
import com.ey.advisory.app.services.reports.Anx1ProcessedReportHandler;
import com.ey.advisory.app.services.reports.Anx1TotalRecordsReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class Anx1ReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ReportsController.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPrmtRepository;

	@Autowired
	@Qualifier("Anx1ProcessedReportHandler")
	private Anx1ProcessedReportHandler anx1ProcessedReportHandler;

	@Autowired
	@Qualifier("Anx1ErrorReportHandler")
	private Anx1ErrorReportHandler anx1ErrorReportHandler;

	@Autowired
	@Qualifier("Anx1ProcessedInfoReportHandler")
	private Anx1ProcessedInfoReportHandler anx1ProcessedInfoReportHandler;

	@Autowired
	@Qualifier("Anx1TotalRecordsReportHandler")
	private Anx1TotalRecordsReportHandler anx1TotalRecordsReportHandler;

	@Autowired
	@Qualifier("Anx1DataStatusSummaryReportHandler")
	private Anx1DataStatusSummaryReportHandler anx1DataStatusSummaryReportHandler;

	@Autowired
	@Qualifier("Anx1InwardProcessedReportHandler")
	private Anx1InwardProcessedReportHandler anx1InwardProcessedReportHandler;
	@Autowired
	@Qualifier("Anx1InwardTotalRecordsReportHandler")
	private Anx1InwardTotalRecordsReportHandler anx1InwardTotalRecordsReportHandler;

	@Autowired
	@Qualifier("Anx1InwardErrorReportHandler")
	private Anx1InwardErrorReportHandler anx1InwardErrorReportHandler;

	@Autowired
	@Qualifier("Anx1InwardProcessedInfoReportHandler")
	private Anx1InwardProcessedInfoReportHandler anx1InwardProcessedInfoReportHandler;

	@RequestMapping(value = "/ui/downloadAnx1Reports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadProcessedReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from downloadProcessedReport request : {}",
					jsonString);
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			Anx1FileStatusReportsReqDto criteria = gson.fromJson(json,
					Anx1FileStatusReportsReqDto.class);
			
			Long fileId = criteria.getFileId();

			if (null != criteria.getFileId()) {
				if ((criteria.getDataType() == null
						|| criteria.getDataType().isEmpty())) {
					String dataType = gstr1FileStatusRepository
							.getDatatype(criteria.getFileId());
					criteria.setDataType(dataType);
					List<Object> answers = entityConfigPrmtRepository
							.findByQtnCode();
					if (answers != null && !answers.isEmpty()) {
						Integer answer = Integer
								.parseInt(String.valueOf(answers.get(0)));
						criteria.setAnswer(answer);
					}
				}
			}
			if (criteria.getDataType() != null
					&& !criteria.getDataType().isEmpty()) {
				if ((criteria.getDataType() != null && criteria.getDataType()
						.equalsIgnoreCase(DownloadReportsConstant.OUTWARD))
						|| (criteria.getDataType() != null
								&& criteria.getDataType().equalsIgnoreCase(
										DownloadReportsConstant.GSTR1))) {
					if (criteria.getType() != null
							&& criteria.getType().equalsIgnoreCase(
									DownloadReportsConstant.PROCESSED)) {
						workbook = anx1ProcessedReportHandler
								.downloadProcessedReport(criteria);
						if (criteria.getStatus() != null
								&& criteria.getStatus().equalsIgnoreCase(
										DownloadReportsConstant.PROCESSEDACTIVE)) {
							fileName = "OutwardFileProcessedActiveRecords";
						} else if (criteria.getStatus() != null
								&& criteria.getStatus().equalsIgnoreCase(
										DownloadReportsConstant.PROCESSEDINACTIVE)) {
							fileName = "OutwardFileProcessedInactiveRecords";
						}
					} else if (criteria.getType() != null && criteria.getType()
							.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
						workbook = anx1ErrorReportHandler
								.downloadErrorReport(criteria);
						if (criteria.getErrorType() != null
								&& criteria.getErrorType().equalsIgnoreCase(
										DownloadReportsConstant.ERRORACTIVE)) {
							fileName = "OutwardBVErrorActiveRecords";
						} else if (criteria.getErrorType() != null
								&& criteria.getErrorType().equalsIgnoreCase(
										DownloadReportsConstant.ERRORINACTIVE)) {
							fileName = "OutwardBVErrorInactiveRecords";
						} else if (criteria.getErrorType() != null
								&& criteria.getErrorType().equalsIgnoreCase(
										DownloadReportsConstant.ERRORSV)) {
							fileName = "OutwardSVErrorRecords";
						} else if (criteria.getErrorType() != null
								&& criteria.getErrorType().equalsIgnoreCase(
										DownloadReportsConstant.ERRORTOTAL)) {
							fileName = "OutwardTotalErrorRecords";
						}

					} else if (criteria.getType() != null
							&& criteria.getType().equalsIgnoreCase(
									DownloadReportsConstant.TOTALERECORDS)) {
						workbook = anx1TotalRecordsReportHandler
								.downloadTotalRecordsReport(criteria);
						fileName = "OutwardFileTotalRecords";

					} else if (criteria.getType() != null
							&& criteria.getType().equalsIgnoreCase(
									DownloadReportsConstant.PROCESSEDINFO)) {
						workbook = anx1ProcessedInfoReportHandler
								.downloadProcessedInfoReport(criteria);
						if (criteria.getStatus() != null
								&& criteria.getStatus().equalsIgnoreCase(
										DownloadReportsConstant.PROCESSEDINFOACTIVE)) {
							fileName = "OutwardFileProcessedInfoActiveRecords";
						} else if (criteria.getStatus() != null
								&& criteria.getStatus().equalsIgnoreCase(
										DownloadReportsConstant.PROCESSEDINFOINACTIVE)) {
							fileName = "OutwardFileProcessedInfoInactiveRecords";

						}
					}
				}
				if (criteria.getDataType() != null && criteria.getDataType()
						.equalsIgnoreCase(DownloadReportsConstant.INWARD)) {

					if (criteria.getType() != null
							&& criteria.getType().equalsIgnoreCase(
									DownloadReportsConstant.PROCESSED)) {
						workbook = anx1InwardProcessedReportHandler
								.downloadProcessedReport(criteria);
						if (criteria.getStatus() != null
								&& criteria.getStatus().equalsIgnoreCase(
										DownloadReportsConstant.PROCESSEDACTIVE)) {
							fileName = fileId + "_InwardFileProcessedActiveReport";
						} else if (criteria.getStatus() != null
								&& criteria.getStatus().equalsIgnoreCase(
										DownloadReportsConstant.PROCESSEDINACTIVE)) {
							fileName = fileId + "_InwardFileProcessedInactiveReport";
						}
					} else if (criteria.getType() != null
							&& criteria.getType().equalsIgnoreCase(
									DownloadReportsConstant.TOTALERECORDS)) {
						workbook = anx1InwardTotalRecordsReportHandler
								.downloadInwardTotalRecordsReport(criteria);
						fileName = fileId + "_InwardFileTotalRecReport";
					}
					if (criteria.getType() != null
							&& criteria.getType().equalsIgnoreCase(
									DownloadReportsConstant.PROCESSEDINFO)) {
						workbook = anx1InwardProcessedInfoReportHandler
								.downloadProcessedInfoReport(criteria);
						if (criteria.getStatus() != null
								&& criteria.getStatus().equalsIgnoreCase(
										DownloadReportsConstant.PROCESSEDINFOACTIVE)) {
							fileName = fileId + "_InwardFileProcessedInfoActiveReport";
						} else if (criteria.getStatus() != null
								&& criteria.getStatus().equalsIgnoreCase(
										DownloadReportsConstant.PROCESSEDINFOINACTIVE)) {
							fileName = fileId + "_InwardFileProcessedInfoInactiveReport";
						}
					}
					if (criteria.getType() != null && criteria.getType()
							.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
						workbook = anx1InwardErrorReportHandler
								.downloadErrorReport(criteria);
						if (criteria.getErrorType() != null
								&& criteria.getErrorType().equalsIgnoreCase(
										DownloadReportsConstant.ERRORACTIVE)) {
							fileName = fileId + "_InwardBVErrorActiveReport";
						} else if (criteria.getErrorType() != null
								&& criteria.getErrorType().equalsIgnoreCase(
										DownloadReportsConstant.ERRORINACTIVE)) {
							fileName = fileId + "_InwardBVErrorInactiveReport";
						} else if (criteria.getErrorType() != null
								&& criteria.getErrorType().equalsIgnoreCase(
										DownloadReportsConstant.ERRORSV)) {
							fileName = fileId + "_InwardSVErrorReport";
						} else if (criteria.getErrorType() != null
								&& criteria.getErrorType().equalsIgnoreCase(
										DownloadReportsConstant.ERRORTOTAL)) {
							fileName = fileId + "_InwardErrorTotalReport";
						}
					}

				}
			} else {
				LOGGER.error("invalid fileId");
				throw new Exception("invalid fileId");
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
			String msg = "Unexpected error while retriving download report";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}
}