package com.ey.advisory.controller.gstr2;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.services.anx1.Gstr2aProcessedRecordsFetchService;
import com.ey.advisory.app.docs.dto.anx1.Gstr2aProcessedRecordsRespDto;
import com.ey.advisory.app.services.reports.Gstr2AProcessedRecScreenHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Sasidhar Reddy
 *
 */
@RestController
public class Gstr2AProcessedController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2AProcessedController.class);

	@Autowired
	@Qualifier("Gstr2aProcessedRecordsFetchService")
	private Gstr2aProcessedRecordsFetchService gstr2aPRFetchService;

	@Autowired
	@Qualifier("Gstr2AProcessedRecScreenHandler")
	private Gstr2AProcessedRecScreenHandler gstr2AProcessedRecScreenHandler;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@RequestMapping(value = "/ui/getGstr2aProcessData", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr2AProcessedRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("The selected criteria for processed records is:->"
				+ requestObject.get("req"));
		}
		// Execute the service method and get the result.
		try {

			Gstr2AProcessedRecordsReqDto gstr2AProcessedRecordsReqDto = gson
					.fromJson(json, Gstr2AProcessedRecordsReqDto.class);

			List<Gstr2aProcessedRecordsRespDto> respDtos = gstr2aPRFetchService
					.findGstr2aProcessedRecords(gstr2AProcessedRecordsReqDto);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respDtos));
			LOGGER.debug(
					"Response data for given criteria for processed records is :->"
							+ resp.toString());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retrieving Gstr2a processed records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/ui/gstr2aEntityLevelDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadreviewSummReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		// String groupcode = TenantContext.getTenantId();
		// TenantContext.setTenantId(groupcode);
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			Gstr2AProcessedRecordsReqDto req = gson.fromJson(json,
					Gstr2AProcessedRecordsReqDto.class);
			Gstr2AProcessedRecordsReqDto setDataSecurity = processedRecordsCommonSecParam
					.setGstr2aDataSecuritySearchParams(req);
			workbook = gstr2AProcessedRecScreenHandler
					.getGstr2aSumTablesReports(setDataSecurity);
			fileName = "Gstr2a_EntityLevel_Summary";

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
		} catch (

		JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		} catch (Exception ex) {
			String msg = "Unexpected error while retrieving "
					+ "Data from Report ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
	}

}
