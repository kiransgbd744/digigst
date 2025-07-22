package com.ey.advisory.controller.gstr2a;

import java.lang.reflect.Type;
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
import com.ey.advisory.app.data.daos.client.gstr2.Gstr6SaveStatusDownloadReportHandler;
import com.ey.advisory.app.data.daos.client.gstr2.Gstr6SaveStatusDownloadV2ReportHandler;
import com.ey.advisory.app.docs.dto.gstr1.Gstr6SaveStatusDownloadReqDto;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Sasidhar Reddy
 *
 */
@RestController
public class Gstr6StatusReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6ReportsController.class);

	@Autowired
	@Qualifier("BasicGstr6SecCommonParam")
	BasicGstr6SecCommonParam basicGstr6SecCommonParam;

	@Autowired
	@Qualifier("Gstr6SaveStatusDownloadReportHandler")
	private Gstr6SaveStatusDownloadReportHandler gstr6SaveStatusDownloadReportHandler;

	@Autowired
	@Qualifier("Gstr6SaveStatusDownloadV2ReportHandler")
	private Gstr6SaveStatusDownloadV2ReportHandler gstr6SaveStatusDownloadV2ReportHandler;

	@Autowired
	private GstnApi gstnapi;

	@RequestMapping(value = "/ui/gstr6SectionwiseDownloads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void gstr6SectionwiseDownloads(@RequestBody String jsonString,
			HttpServletResponse response) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupcode = TenantContext.getTenantId();
		TenantContext.setTenantId(groupcode);
		JsonArray jsonReqArray = (new JsonParser()).parse(jsonString)
				.getAsJsonObject().get("req").getAsJsonArray();
		@SuppressWarnings("serial")
		Type listType = new TypeToken<List<Gstr6SaveStatusDownloadReqDto>>() {
		}.getType();

		List<Gstr6SaveStatusDownloadReqDto> reqDtos = gson
				.fromJson(jsonReqArray, listType);

		try {
			String fileName = null;
			Workbook workbook = null;
			/*
			 * if
			 * (gstnapi.isDelinkingEligible(APIConstants.GSTR6.toUpperCase())) {
			 */
			workbook = gstr6SaveStatusDownloadV2ReportHandler
					.downloadGstr6SaveSectionsReport(reqDtos);
			/*
			 * else { // Write new handler to fetch the limited columns that is
			 * // required in report workbook =
			 * gstr6SaveStatusDownloadReportHandler
			 * .downloadGstr6SaveSectionsReport(reqDtos); }
			 */

			fileName = "GSTR-6_Get_Records";
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
					+ "Data from Report ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
	}
}
