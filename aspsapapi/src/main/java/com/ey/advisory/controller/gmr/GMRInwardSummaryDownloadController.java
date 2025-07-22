package com.ey.advisory.controller.gmr;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.http.HttpServletResponse;

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
import com.ey.advisory.app.gmr.GMRInwardSummaryDownloadService;
import com.ey.advisory.app.gmr.GmrInwardEntityAndMonthDto;
import com.ey.advisory.app.gmr.GmrInwardSummaryDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ravindra V S
 *
 */
@Slf4j
@RestController
public class GMRInwardSummaryDownloadController {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository entityRepository;

	@Autowired
	@Qualifier("GMRInwardSummaryDownloadServiceImpl")
	private GMRInwardSummaryDownloadService gmrDownloadService;

	@RequestMapping(value = "/ui/gmrInwardSummaryDownloadReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void gmrInwardSummaryDownloadReport(HttpServletResponse response,
			@RequestBody String jsonString) throws Exception {

		JsonObject errorResp = new JsonObject();

		Workbook workBook = null;

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			GmrInwardSummaryDto criteria = gson.fromJson(json,
					GmrInwardSummaryDto.class);

			if (CollectionUtils.isEmpty(criteria.getSgstins()))
				throw new AppException("User has not selected any gstin");

			workBook = gmrDownloadService.find(criteria);
			GmrInwardEntityAndMonthDto var = gmrDownloadService
					.entityAndTaxPeriod(criteria);
			

			String date = null;
			String time = null;
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("yyyyMMdd");

			DateTimeFormatter FOMATTER1 = DateTimeFormatter
					.ofPattern("HHmmssms");

			date = FOMATTER.format(istDateTimeFromUTC);
			time = FOMATTER1.format(istDateTimeFromUTC);
			String entityName = var.getEntityName();
			
			 entityName = entityName.replaceAll("\\s", "");

			String fileName = "ITC_Summary_Report"+ "_" + entityName 
					+ "_" + date + "T" + time.substring(0, 6);

			if (workBook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName + ".xlsx"));
				workBook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();

			}

		} catch (Exception ex) {
			String msg = "Error occured while generating GMR Inward report ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			response.flushBuffer();

		}
	}
}