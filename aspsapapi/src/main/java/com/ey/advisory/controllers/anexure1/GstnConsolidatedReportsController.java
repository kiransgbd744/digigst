/**
 * 
 */
package com.ey.advisory.controllers.anexure1;

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
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.docs.dto.GstnConsolidatedErrorReqDto;
import com.ey.advisory.app.services.reports.GstnConsolidatedReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
@Slf4j
public class GstnConsolidatedReportsController {

	@Autowired
	@Qualifier("GstnConsolidatedReportHandler")
	private GstnConsolidatedReportHandler gstnConsolidatedReportHandler;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPrmtRepository;

	@RequestMapping(value = "/ui/consoidateGstnErrorReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadApiCsvReport(@RequestBody String jsonString, HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString).getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String fileName = null;
			Workbook workbook = null;

			GstnConsolidatedErrorReqDto criteria = gson.fromJson(json, GstnConsolidatedErrorReqDto.class);

			List<Object> cutoffPeriods = entityConfigPrmtRepository.findByEntityQtnCode(criteria.getEntityId());
			if (cutoffPeriods != null && !cutoffPeriods.isEmpty()) {
				Integer cutoffPeriod = Integer.parseInt(String.valueOf(cutoffPeriods.get(0)));
				criteria.setAnswer(cutoffPeriod);
			} else {
				criteria.setAnswer(202010);
			}
			
			String returnStr = null;
			if(criteria.getReturnType()!=null && "GSTR1A".equalsIgnoreCase(criteria.getReturnType()))
			{
				returnStr = "GSTR1A_";
				
				
			}else
			{
				returnStr = "GSTR1_";
				
			}
			if (criteria.getGstnRefId() == null || criteria.getGstnRefId().isEmpty()) {
				workbook = gstnConsolidatedReportHandler
						.getConsolidatedReports(criteria);
				fileName = returnStr + criteria.getGstin() + "_" + criteria.getTaxPeriod()
						+ "_Consolidated_PE_ERROR_DigiGST";
			} else {
				workbook = gstnConsolidatedReportHandler
						.getConsolidatedReports(criteria);
				fileName = returnStr + criteria.getGstin() + "_" + criteria.getTaxPeriod() + "_PE_ERROR_DigiGST";
			}
			if (workbook == null) {
				workbook = new Workbook();
			}
			if (fileName != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String.format("attachment; filename=" + fileName + ".xlsx"));
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