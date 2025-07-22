package com.ey.advisory.controller.gstr2;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.anx1a.GetGstr2aReviewSummaryFinalRespDto;
import com.ey.advisory.app.services.reports.Gstr2AProcessedRecScreenHandler;
import com.ey.advisory.app.services.search.anx2.GetGstr2aReviewSummaryService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Sasidhar Reddy
 *
 */
@RestController
public class GetGstr2aReviewSummaryController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetGstr2aReviewSummaryController.class);

	@Autowired
	@Qualifier("GetGstr2aReviewSummaryService")
	private GetGstr2aReviewSummaryService getGstr2aReviewSummaryService;

	@Autowired
	@Qualifier("Gstr2AProcessedRecScreenHandler")
	private Gstr2AProcessedRecScreenHandler gstr2AProcessedRecScreenHandler;

	@PostMapping(value = "/ui/getGstr2aReviewSummaryData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnx2SummaryDetails(
			@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Gstr2AProcessedRecordsReqDto criteria = gson.fromJson(
					reqJson.toString(), Gstr2AProcessedRecordsReqDto.class);
			List<Long> entityIds = criteria.getEntityId();
			Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
					.getInwardSecurityAttributeMap();
			Map<String, List<String>> dataSecurityAttrMap = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(entityIds,
							inwardSecurityAttributeMap);
			if (criteria.getDataSecAttrs() == null
					|| criteria.getDataSecAttrs().isEmpty()) {
				criteria.setDataSecAttrs(dataSecurityAttrMap);
			} else {
				Map<String, List<String>> dataSecReqMap = criteria
						.getDataSecAttrs();
				List<String> gstinList = dataSecReqMap
						.get(OnboardingConstant.GSTIN);
				if ((gstinList == null || gstinList.isEmpty())) {
					criteria.setDataSecAttrs(dataSecurityAttrMap);
				} else {
					if ((gstinList != null && !gstinList.isEmpty())) {
						dataSecurityAttrMap.put(OnboardingConstant.GSTIN,
								gstinList);
					}
					criteria.setDataSecAttrs(dataSecurityAttrMap);
				}
			}
			@SuppressWarnings("unused")
			GetGstr2aReviewSummaryFinalRespDto resp = getGstr2aReviewSummaryService
					.<GetGstr2aReviewSummaryFinalRespDto>loadSummaryDetails(
							criteria);

			JsonObject resps = new JsonObject();
			if (resp != null) {
				JsonElement respBody = gson.toJsonTree(resp);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				JsonElement respBody = gson.toJsonTree(
						"No Summary details data found for the Given Date.");
				resps.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}

		}

		catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json for Summary details";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error for Summary details";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/ui/gstr2aTableWiseDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void gstr2aTableWiseDownload(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		String groupcode = TenantContext.getTenantId();
		TenantContext.setTenantId(groupcode);
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			Gstr2AProcessedRecordsReqDto criteria = gson.fromJson(
					json.toString(), Gstr2AProcessedRecordsReqDto.class);
			List<Long> entityIds = criteria.getEntityId();
			Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
					.getInwardSecurityAttributeMap();
			Map<String, List<String>> dataSecurityAttrMap = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(entityIds,
							inwardSecurityAttributeMap);
			if (criteria.getDataSecAttrs() == null
					|| criteria.getDataSecAttrs().isEmpty()) {
				criteria.setDataSecAttrs(dataSecurityAttrMap);
			} else {
				Map<String, List<String>> dataSecReqMap = criteria
						.getDataSecAttrs();
				List<String> gstinList = dataSecReqMap
						.get(OnboardingConstant.GSTIN);
				if ((gstinList == null || gstinList.isEmpty())) {
					criteria.setDataSecAttrs(dataSecurityAttrMap);
				} else {
					if ((gstinList != null && !gstinList.isEmpty())) {
						dataSecurityAttrMap.put(OnboardingConstant.GSTIN,
								gstinList);
					}
					criteria.setDataSecAttrs(dataSecurityAttrMap);
				}
			}

			workbook = gstr2AProcessedRecScreenHandler
					.getGstr2aRevSummTablesReports(criteria);
			fileName = "Gstr2a_Table_Wise_Summary";

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
			String msg = "Unexpected error while retriving "
					+ "Data from Report ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
	}

}
