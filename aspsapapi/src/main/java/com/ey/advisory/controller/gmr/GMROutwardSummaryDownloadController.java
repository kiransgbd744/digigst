package com.ey.advisory.controller.gmr;

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
import com.ey.advisory.app.gmr.GMROutwardSummaryDownloadService;
import com.ey.advisory.app.gmr.GmrOutwardEntityAndMonthDto;
import com.ey.advisory.app.gmr.GmrOutwardSummaryDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Slf4j
@RestController
public class GMROutwardSummaryDownloadController {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository entityRepository;

	@Autowired
	@Qualifier("GMROutwardSummaryDownloadServiceImpl")
	private GMROutwardSummaryDownloadService reconDownloadService;

	@RequestMapping(value = "/ui/gmrOutwardSummaryDownloadReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void gmrOutwardSummaryDownloadReport(HttpServletResponse response,
			@RequestBody String jsonString) throws Exception {

		JsonObject errorResp = new JsonObject();

		Workbook workBook = null;

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			GmrOutwardSummaryDto criteria = gson.fromJson(json,
					GmrOutwardSummaryDto.class);

			if (CollectionUtils.isEmpty(criteria.getSgstins()))
				throw new AppException("User has not selected any gstin");

			workBook = reconDownloadService.find(criteria);
			GmrOutwardEntityAndMonthDto var = reconDownloadService
					.entityAndTaxPeriod(criteria);

			String fileName = var.getEntityName() + "_" + "Tax Liability Report"
					+ "_" + var.getFromMonth()
					+ criteria.getFromTaxPeriod().substring(2, 6)
					+ "_" + var.getToMonth()
					+ criteria.getToTaxPeriod().substring(2, 6);

			if (workBook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));
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