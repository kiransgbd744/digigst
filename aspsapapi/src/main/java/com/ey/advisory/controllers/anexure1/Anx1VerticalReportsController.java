/**
 * 
 */
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
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.Anx1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.Anx1VerticalErrorReportHandler;
import com.ey.advisory.app.services.reports.Anx1VerticalProcessedInfoReportHandler;
import com.ey.advisory.app.services.reports.Anx1VerticalProcessedReportHandler;
import com.ey.advisory.app.services.reports.Anx1VerticalTotalReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
public class Anx1VerticalReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1VerticalReportsController.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Anx1VerticalProcessedReportHandler")
	private Anx1VerticalProcessedReportHandler anx1VerticalProcessedReportHandler;

	@Autowired
	@Qualifier("Anx1VerticalErrorReportHandler")
	private Anx1VerticalErrorReportHandler anx1VerticalErrorReportHandler;

	@Autowired
	@Qualifier("Anx1VerticalTotalReportHandler")
	private Anx1VerticalTotalReportHandler anx1VerticalTotalReportHandler;

	@Autowired
	@Qualifier("Anx1VerticalProcessedInfoReportHandler")
	private Anx1VerticalProcessedInfoReportHandler anx1VerticalProcessedInfoReportHandler;

	@RequestMapping(value = "/ui/downloadVerticalReports", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadVerticalReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			Anx1VerticalDownloadReportsReqDto criteria = gson.fromJson(json,
					Anx1VerticalDownloadReportsReqDto.class);

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

			if (criteria.getDataType() != null
					&& (criteria.getFileType() != null
							&& (criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.B2C)
									|| criteria.getFileType().equalsIgnoreCase(
											DownloadReportsConstant.TABLE3H3I)
									|| criteria.getFileType().equalsIgnoreCase(
											DownloadReportsConstant.TABLE4)))) {
				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSED)) {
					workbook = anx1VerticalProcessedReportHandler
							.downloadVerticalProcessedReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(DownloadReportsConstant.B2C)) {
						fileName = "Anx1_VerticalB2CProcessedRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.TABLE3H3I)) {
						fileName = "Anx1_Vertical3H3IProcessedRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.TABLE4)) {
						fileName = "Anx1_VerticalTable4ProcessedRecords";
					}
				}
				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.ERROR)) {
					workbook = anx1VerticalErrorReportHandler
							.downloadVerticalErrorReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(DownloadReportsConstant.B2C)) {
						fileName = "Anx1_VerticalB2CErrorRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.TABLE3H3I)) {
						fileName = "Anx1_Vertical3H3IErrorRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.TABLE4)) {
						fileName = "Anx1_VerticalTable4ErrorRecords";
					}
				}
				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.TOTALERECORDS)) {
					workbook = anx1VerticalTotalReportHandler
							.downloadVerticalTotalReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(DownloadReportsConstant.B2C)) {
						fileName = "Anx1_VerticalB2CTotalRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.TABLE3H3I)) {
						fileName = "Anx1_Vertical3H3ITotalRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.TABLE4)) {
						fileName = "Anx1_VerticalTable4TotalRecords";
					}
				}
				if (criteria.getType() != null && !criteria.getType().isEmpty()
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSEDINFO)) {
					workbook = anx1VerticalProcessedInfoReportHandler
							.downloadVerticalProcessedInfoReport(criteria);
					if (criteria.getFileType() != null && criteria.getFileType()
							.equalsIgnoreCase(DownloadReportsConstant.B2C)) {
						fileName = "Anx1_VerticalB2CProcessedInfoRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.TABLE3H3I)) {
						fileName = "Anx1_Vertical3H3IProcessedInfoRecords";
					} else if (criteria.getFileType() != null
							&& criteria.getFileType().equalsIgnoreCase(
									DownloadReportsConstant.TABLE4)) {
						fileName = "Anx1_VerticalTable4ProcessedInfoRecords";
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

}