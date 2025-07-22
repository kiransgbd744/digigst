/**
 * 
 */
package com.ey.advisory.controllers.anexure1;

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
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.ITCErrorReportHandler;
import com.ey.advisory.app.services.reports.ITCProcessedReportHandler;
import com.ey.advisory.app.services.reports.ITCTotalReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@RestController
public class ITC04ReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ITC04ReportsController.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("ITCTotalReportHandler")
	private ITCTotalReportHandler iTCTotalReportHandler;

	@Autowired
	@Qualifier("ITCErrorReportHandler")
	private ITCErrorReportHandler iTCErrorReportHandler;

	@Autowired
	@Qualifier("ITCProcessedReportHandler")
	private ITCProcessedReportHandler iTCProcessedReportHandler;

	@RequestMapping(value = "/ui/downloadITC04Reports", method = RequestMethod.POST, produces = {
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
			Gstr1VerticalDownloadReportsReqDto criteria = gson.fromJson(json,
					Gstr1VerticalDownloadReportsReqDto.class);

			Long fileId = criteria.getFileId();
			if (fileId == null && fileId == 0L) {
				LOGGER.error(
						"File Id not found in the request and it is invalid");
				throw new Exception(
						"File Id not found in the request and it is invalid");
			}

			String fileName = null;
			Workbook workbook = null;

			if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
				workbook = iTCProcessedReportHandler
						.downloadITCProcessReport(criteria);
				if (criteria.getStatus() != null
						&& criteria.getStatus().equalsIgnoreCase(
								DownloadReportsConstant.ACTIVE)) {
					fileName = "Itc04ProcessedActiveRecords";
				} else if (criteria.getStatus() != null
						&& criteria.getStatus().equalsIgnoreCase(
								DownloadReportsConstant.INACTIVE)) {
					fileName = "Itc04ProcessedInactiveRecords";
				}
			}
			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
				workbook = iTCErrorReportHandler
						.downloadITCErrorReport(criteria);
				if (criteria.getErrorType() != null
						&& criteria.getErrorType().equalsIgnoreCase(
								DownloadReportsConstant.ACTIVE)) {
					fileName = "Itc04BVErrorActiveRecords";
				} else if (criteria.getErrorType() != null
						&& criteria.getErrorType().equalsIgnoreCase(
								DownloadReportsConstant.INACTIVE)) {
					fileName = "Itc04BVErrorInactiveRecords";
				} else if (criteria.getErrorType() != null
						&& criteria.getErrorType().equalsIgnoreCase(
								DownloadReportsConstant.ERRORSV)) {
					fileName = "Itc04SVErrorRecords";
				} else if (criteria.getErrorType() != null
						&& criteria.getErrorType().equalsIgnoreCase(
								DownloadReportsConstant.ERRORTOTAL)) {
					fileName = "Itc04TotalErrorRecords";
				}
			
			} else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.TOTALERECORDS)) {
				workbook = iTCTotalReportHandler
						.downloadITCTotalReport(criteria);
				fileName = "ITC04TotalRecords";
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
