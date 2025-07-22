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
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.NilNonVerticalErrorReportHandler;
import com.ey.advisory.app.services.reports.NilNonVerticalProcessedInfoReportHandler;
import com.ey.advisory.app.services.reports.NilNonVerticalProcessedReportHandler;
import com.ey.advisory.app.services.reports.NilNonVerticalTotalReportHandler;
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
public class NilNonVerticalDownloadReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr7VerticalDownloadReportsController.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("NilNonVerticalProcessedReportHandler")
	private NilNonVerticalProcessedReportHandler nilNonVerticalProcessedReportHandler;
	
	@Autowired
	@Qualifier("NilNonVerticalProcessedInfoReportHandler")
	private NilNonVerticalProcessedInfoReportHandler nilNonVerticalProcessedInfoReportHandler;

	@Autowired
	@Qualifier("NilNonVerticalErrorReportHandler")
	private NilNonVerticalErrorReportHandler nilNonVerticalErrorReportHandler;

	@Autowired
	@Qualifier("NilNonVerticalTotalReportHandler")
	private NilNonVerticalTotalReportHandler nilNonVerticalTotalReportHandler;

	@RequestMapping(value = "/ui/downloadNilNonReports", method = RequestMethod.POST, produces = {
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
			//gstr1a logic
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
					.equalsIgnoreCase(DownloadReportsConstant.TOTALERECORDS)) {
				workbook = nilNonVerticalTotalReportHandler
						.downloadNilNonVerticalTotalReport(criteria);
				fileName = "NilNonExtTotalRecords";
			}

			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
				workbook = nilNonVerticalProcessedReportHandler
						.downloadNilNonVerticalProcessedReport(criteria);
				fileName = "NilNonExtProcessedRecords";
			}
			
			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
				workbook = nilNonVerticalErrorReportHandler
						.downloadNilNonVerticalerrorReport(criteria);
				fileName = "NilNonExtErrorRecords";
			}
			else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.PROCESSEDINFO)) {
				workbook = nilNonVerticalProcessedInfoReportHandler
						.downloadNilNonVerticalProcessedInfoReport(criteria);
				fileName = "NilNonExtInfoRecords";
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
