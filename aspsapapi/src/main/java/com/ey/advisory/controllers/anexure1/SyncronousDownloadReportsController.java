package com.ey.advisory.controllers.anexure1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.AuditTrailReportsReqDto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.AuditTrailInwardSummaryReportHandler;
import com.ey.advisory.app.services.reports.AuditTrailOutwardSummaryReportHandler;
import com.ey.advisory.app.services.reports.CewbErrorReportHandler;
import com.ey.advisory.app.services.reports.CewbProcessedInfoReportHandler;
import com.ey.advisory.app.services.reports.CewbProcessedReportHandler;
import com.ey.advisory.app.services.reports.CewbTotalReportHandler;
import com.ey.advisory.app.services.reports.CrossItcErrorReportHandler;
import com.ey.advisory.app.services.reports.CrossItcProcessedInfoReportHandler;
import com.ey.advisory.app.services.reports.CrossItcProcessedReportHandler;
import com.ey.advisory.app.services.reports.CrossItcTotalReportHandler;
import com.ey.advisory.app.services.reports.Gstr9InOutwardErrorReportHandler;
import com.ey.advisory.app.services.reports.Gstr9InOutwardProcessedInfoReportHandler;
import com.ey.advisory.app.services.reports.Gstr9InOutwardProcessedReportHandler;
import com.ey.advisory.app.services.reports.Gstr9InOutwardTotalReportHandler;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@RestController
@Slf4j
public class SyncronousDownloadReportsController {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("CewbErrorReportHandler")
	private CewbErrorReportHandler cewbErrorReportHandler;

	@Autowired
	@Qualifier("CewbProcessedReportHandler")
	private CewbProcessedReportHandler cewbProcessedReportHandler;

	@Autowired
	@Qualifier("CewbProcessedInfoReportHandler")
	private CewbProcessedInfoReportHandler cewbProcessedInfoReportHandler;

	@Autowired
	@Qualifier("CewbTotalReportHandler")
	private CewbTotalReportHandler cewbTotalReportHandler;

	@Autowired
	@Qualifier("CrossItcErrorReportHandler")
	private CrossItcErrorReportHandler crossItcErrorReportHandler;

	@Autowired
	@Qualifier("CrossItcProcessedReportHandler")
	private CrossItcProcessedReportHandler crossItcProcessedReportHandler;

	@Autowired
	@Qualifier("CrossItcProcessedInfoReportHandler")
	private CrossItcProcessedInfoReportHandler crossItcProcessedInfoReportHandler;

	@Autowired
	@Qualifier("CrossItcTotalReportHandler")
	private CrossItcTotalReportHandler crossItcTotalReportHandler;

	@Autowired
	@Qualifier("Gstr9InOutwardErrorReportHandler")
	private Gstr9InOutwardErrorReportHandler gstr9InOutwardErrorReportHandler;

	@Autowired
	@Qualifier("Gstr9InOutwardProcessedReportHandler")
	private Gstr9InOutwardProcessedReportHandler gstr9InOutwardProcessedReportHandler;

	@Autowired
	@Qualifier("Gstr9InOutwardProcessedInfoReportHandler")
	private Gstr9InOutwardProcessedInfoReportHandler gstr9InOutwardProcessedInfoReportHandler;

	@Autowired
	@Qualifier("Gstr9InOutwardTotalReportHandler")
	private Gstr9InOutwardTotalReportHandler gstr9InOutwardTotalReportHandler;

	@Autowired
	@Qualifier("AuditTrailOutwardSummaryReportHandler")
	private AuditTrailOutwardSummaryReportHandler auditTrailOutwardSummaryReportHandler;

	@Autowired
	@Qualifier("AuditTrailInwardSummaryReportHandler")
	private AuditTrailInwardSummaryReportHandler auditTrailInwardSummaryReportHandler;

	private static final String DATE_FORMATTER = "yyyyMMdd HHmmss";

	@RequestMapping(value = "/ui/downloadCewbReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadCewbReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			Gstr1VerticalDownloadReportsReqDto criteria = gson.fromJson(json,
					Gstr1VerticalDownloadReportsReqDto.class);

			if (null != criteria.getFileId()) {
				if ((criteria.getFileType() == null
						|| criteria.getFileType().isEmpty())
						&& (criteria.getDataType() == null
								|| criteria.getDataType().isEmpty())) {
					List<Object[]> objects = gstr1FileStatusRepository
							.getTypes(criteria.getFileId());

					objects.forEach(object -> {

						String filetype = (String) object[0];
						String datatype = (String) object[1];

						criteria.setFileType(filetype);
						criteria.setDataType(datatype);

					});
				}
			}

			String fileName = null;
			Workbook workbook = null;

			if ((criteria.getDataType() != null && (criteria.getDataType()
					.equalsIgnoreCase(GSTConstants.EWB)))
					&& (criteria.getFileType() != null
							&& (criteria.getFileType()
									.equalsIgnoreCase(GSTConstants.CEWB)))) {

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.ERROR)) {
					workbook = cewbErrorReportHandler
							.downloadCewbErrorReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(GSTConstants.CEWB)) {
						fileName = "CEWB_ErrorRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& DownloadReportsConstant.PROCESSED
								.equalsIgnoreCase(criteria.getType().trim())) {
					workbook = cewbProcessedReportHandler
							.downloadCewbProcessedReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(GSTConstants.CEWB)) {
						fileName = "CEWB_ProcessRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSEDINFO)) {
					workbook = cewbProcessedInfoReportHandler
							.downloadCewbVerticalProcessedInfoReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(GSTConstants.CEWB)) {
						fileName = "CEWB_ProcessedInfoRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.TOTALERECORDS)) {
					workbook = cewbTotalReportHandler
							.downloadCewbTotalReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(GSTConstants.CEWB)) {
						fileName = "CEWB_TotalRecords";
					}
				}

			} else {
				LOGGER.error("Invalid fileId");
				throw new Exception("Invalid fileId");
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
			String msg = "Unexpected error while retriving "
					+ "GSTN Save and Submit Documents ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}

	// Cross ITC files download reports....

	@RequestMapping(value = "/ui/downloadCrossItcReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadCrossITCReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			Gstr1VerticalDownloadReportsReqDto criteria = gson.fromJson(json,
					Gstr1VerticalDownloadReportsReqDto.class);

			if (null != criteria.getFileId()) {
				if ((criteria.getFileType() == null
						|| criteria.getFileType().isEmpty())
						&& (criteria.getDataType() == null
								|| criteria.getDataType().isEmpty())) {
					List<Object[]> objects = gstr1FileStatusRepository
							.getTypes(criteria.getFileId());

					objects.forEach(object -> {

						String filetype = (String) object[0];
						String datatype = (String) object[1];

						criteria.setFileType(filetype);
						criteria.setDataType(datatype);

					});
				}
			}

			String fileName = null;
			Workbook workbook = null;

			if ((criteria.getDataType() != null && (criteria.getDataType()
					.equalsIgnoreCase(GSTConstants.INWARD)))
					&& (criteria.getFileType() != null
							&& (criteria.getFileType().equalsIgnoreCase(
									GSTConstants.CROSS_ITC)))) {

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.ERROR)) {
					workbook = crossItcErrorReportHandler
							.downloadCrossItcErrorReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(GSTConstants.CROSS_ITC)) {
						fileName = "Cross_ITC_ErrorRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& DownloadReportsConstant.PROCESSED
								.equalsIgnoreCase(criteria.getType().trim())) {
					workbook = crossItcProcessedReportHandler
							.downloadCrossItcProcessedReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(GSTConstants.CROSS_ITC)) {
						fileName = "Cross_ITC_ProcessRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSEDINFO)) {
					workbook = crossItcProcessedInfoReportHandler
							.downloadCrossItcVerticalProcessedInfoReport(
									criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(GSTConstants.CROSS_ITC)) {
						fileName = "Cross_ITC_ProcessedInfoRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.TOTALERECORDS)) {
					workbook = crossItcTotalReportHandler
							.downloadCrossItcTotalReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(GSTConstants.CROSS_ITC)) {
						fileName = "Cross_ITC_TotalRecords";
					}
				}

			} else {
				LOGGER.error("Invalid fileId");
				throw new Exception("Invalid fileId");
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
			String msg = "Unexpected error while retriving "
					+ "GSTN Save and Submit Documents ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}
	// GSTR-9 In and Outward files download reports....

	@RequestMapping(value = "/ui/downloadGstr9InOutwardReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadReports(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			Gstr1VerticalDownloadReportsReqDto criteria = gson.fromJson(json,
					Gstr1VerticalDownloadReportsReqDto.class);

			if (null != criteria.getFileId()) {
				if ((criteria.getFileType() == null
						|| criteria.getFileType().isEmpty())
						&& (criteria.getDataType() == null
								|| criteria.getDataType().isEmpty())) {
					List<Object[]> objects = gstr1FileStatusRepository
							.getTypes(criteria.getFileId());

					objects.forEach(object -> {

						String filetype = (String) object[0];
						String datatype = (String) object[1];

						criteria.setFileType(filetype);
						criteria.setDataType(datatype);

					});
				}
			}

			String fileName = null;
			Workbook workbook = null;

			if ((criteria.getDataType() != null && (criteria.getDataType()
					.equalsIgnoreCase(GSTConstants.GSTR9)))
					&& (criteria.getFileType() != null
							&& (criteria.getFileType().equalsIgnoreCase(
									GSTConstants.INWARD_OUTWARD)))) {

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.ERROR)) {
					workbook = gstr9InOutwardErrorReportHandler
							.downloadGstr9InOutwardErrorReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(GSTConstants.INWARD_OUTWARD)) {
						fileName = "GSTR-9_Inward_Outward_ErrorRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& DownloadReportsConstant.PROCESSED
								.equalsIgnoreCase(criteria.getType().trim())) {
					workbook = gstr9InOutwardProcessedReportHandler
							.downloadGstr9InOutwardProcessedReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(GSTConstants.INWARD_OUTWARD)) {
						fileName = "GSTR-9_Inward_Outward_ProcessRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSEDINFO)) {
					workbook = gstr9InOutwardProcessedInfoReportHandler
							.downloadGstr9InOutwardVerticalProcessedInfoReport(
									criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(GSTConstants.INWARD_OUTWARD)) {
						fileName = "GSTR-9_Inward_Outward_ProcessedInfoRecords";
					}
				}

				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.TOTALERECORDS)) {
					workbook = gstr9InOutwardTotalReportHandler
							.downloadGstr9InOutwardTotalReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(GSTConstants.INWARD_OUTWARD)) {
						fileName = "GSTR-9_Inward_Outward_TotalRecords";
					}
				}

			} else {
				LOGGER.error("Invalid fileId");
				throw new Exception("Invalid fileId");
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
			String msg = "Unexpected error while retriving "
					+ "GSTN Save and Submit Documents ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}

	// Audit Trail Outward summarys files download reports....

	@RequestMapping(value = "/ui/downloadAuditTrailSummaryReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadAuditTrailSummaryReports(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			AuditTrailReportsReqDto criteria = gson.fromJson(json,
					AuditTrailReportsReqDto.class);

			String fileName = null;
			Workbook workbook = null;
			String postfixName = "_SummaryRecords_";
			LocalDateTime convertUTC = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			LocalDateTime convertIST = EYDateUtil
					.toISTDateTimeFromUTC(convertUTC);
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern(DATE_FORMATTER);
			String now = convertIST.format(formatter);
			now = now.substring(0, 8) + "T"
					+ now.substring(8).replaceAll("\\s", "");
			if (criteria.getType() != null && !criteria.getType().isEmpty()
					&& criteria.getType()
							.equalsIgnoreCase(DownloadReportsConstant.O)) {
				String prifixName = "AuditTrail_Outward_";
				workbook = auditTrailOutwardSummaryReportHandler
						.downloadAudiTrailoutwardSummaryReport(criteria);
				fileName = new StringBuilder().append(prifixName)
						.append(criteria.getDocNum()).append(postfixName)
						.append(now).toString();

			} else if (criteria.getType() != null
					&& !criteria.getType().isEmpty() && criteria.getType()
							.equalsIgnoreCase(DownloadReportsConstant.I)) {
				String prifixName = "AuditTrail_Inward_";
				workbook = auditTrailInwardSummaryReportHandler
						.downloadAudiTrailnwardSummaryReport(criteria);
				fileName = new StringBuilder().append(prifixName)
						.append(criteria.getDocNum()).append(postfixName)
						.append(now).toString();
			}

			else {
				LOGGER.error("Invalid Type");
				throw new Exception("Invalid Type");
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
			String msg = "Unexpected error while retriving "
					+ "GSTN Save and Submit Documents ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}
}