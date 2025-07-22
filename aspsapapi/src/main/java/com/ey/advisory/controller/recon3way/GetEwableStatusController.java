package com.ey.advisory.controller.recon3way;

import java.io.IOException;

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
import com.ey.advisory.app.service.upload.way3recon.EwbStatusInputDto;
import com.ey.advisory.app.service.upload.way3recon.GetEwbStatusServiceImpl;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@RestController
@Slf4j
public class GetEwableStatusController {

	@Autowired
	@Qualifier("GetEwbStatusServiceImpl")
	private GetEwbStatusServiceImpl getEwbStatusServiceImpl;

	@RequestMapping(value = "/ui/getEwableStatusReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {
		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"GetEwableStatusController");
		Gson gson = GsonUtil.newSAPGsonInstance();
		Workbook workbook = null;
		String fileName = null;
		JsonObject jsonObj = new JsonObject();
		try {
			String tenantCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", tenantCode);
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			EwbStatusInputDto criteria = gson.fromJson(json,
					EwbStatusInputDto.class);
			workbook = getEwbStatusServiceImpl.generateReport(criteria);
			fileName = DocumentUtility
					.getUniqueFileName(ConfigConstants.EWB_FOR_EWB_STATUS);
			if (workbook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
				return new ResponseEntity<>(null, HttpStatus.OK);
			} else {
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				jsonObj.addProperty("resp", "No Data Available");
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			}
		} catch (Exception ex) {
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.addProperty("resp", ex.getMessage());
			String msg = "Error occured while generating report for Get EWB Status";
			LOGGER.error(msg, ex);
			try {
				response.flushBuffer();
			} catch (IOException e) {
				String errMsg = "Exception occurred while flushing the Get EWB Status";
				LOGGER.error(errMsg, ex);
			}
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}
}
