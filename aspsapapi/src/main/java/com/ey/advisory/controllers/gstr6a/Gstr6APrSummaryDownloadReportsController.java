package com.ey.advisory.controllers.gstr6a;

import java.util.List;
import java.util.Map;

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
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataRequestDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataResponseDto;
import com.ey.advisory.app.services.daos.gstr6a.Gstr6AProcessedDataServiceImpl;
import com.ey.advisory.app.services.reports.Gstr6AProcessedRecScreenHandler;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class Gstr6APrSummaryDownloadReportsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr6APrSummaryDownloadReportsController.class);

	@Autowired
	@Qualifier("Gstr6AProcessedRecScreenHandler")
	private Gstr6AProcessedRecScreenHandler gstr6aProcessedRecScreenHandler;

	@Autowired
	@Qualifier("Gstr6AProcessedDataServiceImpl")
	Gstr6AProcessedDataServiceImpl gstr6AProcessedDataService;

	@RequestMapping(value = "/ui/gstr6aProcessSumamryDownloads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void gstr6aProcessSumamryDownloads(@RequestBody String jsonString, HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString).getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		// String groupcode = TenantContext.getTenantId();
		// TenantContext.setTenantId(groupcode);
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			Gstr6AProcessedDataRequestDto criteria = gson.fromJson(json, Gstr6AProcessedDataRequestDto.class);

			List<Long> entityIds = criteria.getEntityId();
			Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil.getInwardSecurityAttributeMap();
			Map<String, List<String>> dataSecurityAttrMap = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(entityIds, inwardSecurityAttributeMap);
			if (criteria.getDataSecAttrs() == null || criteria.getDataSecAttrs().isEmpty()) {
				criteria.setDataSecAttrs(dataSecurityAttrMap);
			} else {
				Map<String, List<String>> dataSecReqMap = criteria.getDataSecAttrs();
				List<String> gstinList = dataSecReqMap.get(OnboardingConstant.GSTIN);
				if ((gstinList == null || gstinList.isEmpty())) {
					criteria.setDataSecAttrs(dataSecurityAttrMap);
				} else {
					if ((gstinList != null && !gstinList.isEmpty())) {
						dataSecurityAttrMap.put(OnboardingConstant.GSTIN, gstinList);
					}
					criteria.setDataSecAttrs(dataSecurityAttrMap);
				}
			}
			List<Gstr6AProcessedDataResponseDto> responseData = gstr6AProcessedDataService
					.getGstr6AProcessedData(criteria);
			workbook = gstr6aProcessedRecScreenHandler.getGstr6aSummTablesReports(responseData);
			fileName = "GSTR-6A_Processed_Summary_Screen_Download";

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
			String msg = "Unexpected error while retriving " + "Data from Report ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
	}
}
