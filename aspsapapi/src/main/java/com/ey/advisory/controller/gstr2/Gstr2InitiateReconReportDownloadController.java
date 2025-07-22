package com.ey.advisory.controller.gstr2;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconDto;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconReportDownloadService;
import com.ey.advisory.common.AppException;
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
 * 
 * @author Rajesh N K
 *
 */
@Slf4j
@RestController
public class Gstr2InitiateReconReportDownloadController {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository entityRepository;

	@Autowired
	@Qualifier("Gstr2InitiateReconReportDownloadServiceImpl")
	private Gstr2InitiateReconReportDownloadService reconDownloadService;

	@RequestMapping(value = "/ui/gstr2InitiateReconDownloadReport", method = 
			RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE })
	public void getInitiateReconDownloadReport(HttpServletResponse response,
			@RequestBody String jsonString) throws Exception {

		JsonObject errorResp = new JsonObject();

		Workbook workBook = null;

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			Gstr2InitiateReconDto criteria = gson.fromJson(json,
					Gstr2InitiateReconDto.class);

			if (CollectionUtils.isEmpty(criteria.getSgstins()))
				throw new AppException("User has not selected any gstin");

			workBook = reconDownloadService.find(criteria);
			
			String fileName = DocumentUtility.getUniqueFileName(
					ConfigConstants.GSTR2A_INITIATE_RECON_REPORT);

			if (workBook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String.format(
						"attachment; filename=" +fileName ));
				workBook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();

			}

		} catch (Exception ex) {
			String msg = "Error occured while generating reconSummary report ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			response.flushBuffer();

		}
	}
}