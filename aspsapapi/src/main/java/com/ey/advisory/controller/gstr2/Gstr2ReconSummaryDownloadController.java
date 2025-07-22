package com.ey.advisory.controller.gstr2;

import java.io.IOException;
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
import com.ey.advisory.app.getr2.ap.recon.summary.Gstr2APAndNonAPReconSummaryReqDto;
import com.ey.advisory.app.gstr2.recon.summary.Gstr2ReconSummaryDownloadService;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajesh N K
 *
 */

@RestController
@Slf4j
public class Gstr2ReconSummaryDownloadController {

	@Autowired
	@Qualifier("Gstr2ReconSummaryDownloadServiceImpl")
	Gstr2ReconSummaryDownloadService gstr2DownloadService;

	@RequestMapping(value = "/ui/gstr2ReconSummaryDownloadReport", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void gstr2ReconSummaryDownloadReport(@RequestBody String jsonString,
			HttpServletResponse response) throws IOException {

		JsonObject errorResp = new JsonObject();
		Workbook workbook = null;

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		Long configId;
		List<String> gstinList = null;

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside Gstr2ReconSummaryDownloadController"
								+ ".gstr2ReconSummaryDownloadReport() method "
								+ ":: jsonString : %s", jsonString);
				LOGGER.debug(msg);
			}

			Gstr2APAndNonAPReconSummaryReqDto req = gson.fromJson(json,
					Gstr2APAndNonAPReconSummaryReqDto.class);

			configId = req.getConfigId();
			String toTaxPeriod = req.getToTaxPeriod();
			String fromTaxPeriod = req.getFromTaxPeriod();
			Long entityId = req.getEntityId();
			
			String toTaxPeriod_A2 = req.getToTaxPeriod_2A();
			String fromTaxPeriod_A2 = req.getFromTaxPeriod_2A();
			String criteria = req.getCriteria();

			if (req.getGstins() != null && !req.getGstins().isEmpty()) {
				gstinList = req.getGstins();
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"reconSummary() Parameter " + "configId %s:: ",
						configId);
				LOGGER.debug(msg);
			}

			String reconType = req.getReconType();

			workbook = gstr2DownloadService.getReconSummaryDownload(configId,
					gstinList, toTaxPeriod, fromTaxPeriod, reconType, entityId, toTaxPeriod_A2, fromTaxPeriod_A2, criteria);

			String fileName = DocumentUtility
					.getUniqueFileName(ConfigConstants.DATA_FOR_RECON_SUMMARY);

			if (workbook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));

		} catch (Exception ex) {
			String msg = "Error occured while generating reconSummary report ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			response.flushBuffer();

		}
	}
}
