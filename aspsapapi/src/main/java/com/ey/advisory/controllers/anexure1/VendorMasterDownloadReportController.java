package com.ey.advisory.controllers.anexure1;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.controllers.anexure1.GetBulkCreditLedgerDetailsController.GstnData;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class VendorMasterDownloadReportController {

	
	@Autowired
	FileStatusDownloadReportRepository downloadRepository;
	
	@Autowired
	AsyncJobsService asyncJobsService;
	
	/*@Autowired
	VendorMasterReportDownloadProcessorTesting vendorMasterReportDownloadProcessorTesting;*/

	@PostMapping(value = "/ui/downloadGetVendorGstinDetailsReports")
	public ResponseEntity<String> downloadGetVendorGstinDetailsReports(
			@RequestBody String jsonParams) {


		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside downloadGetVendorGstinDetailsReports Download CSV Report Controller";
			LOGGER.debug(msg);
		}
		JsonObject requestObject = JsonParser.parseString(jsonParams)
				.getAsJsonObject();
		JsonObject jsonreq = requestObject.get("req").getAsJsonObject();
		JsonArray json = jsonreq.get("gstinDetails").getAsJsonArray();
		//boolean gstinDetails = jsonreq.get("gstinDetails").getAsBoolean();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();
		Set<String> gstnlist = new HashSet<String>();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName() != null
				? user.getUserPrincipalName() : "SYSTEM";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download vendor gstin details report: %s",
					json.toString());
			LOGGER.debug(msg);
		}
		Type listType = new TypeToken<List<GstnData>>() {
		}.getType();
		List<GstnData> reqDto = gson.fromJson(json, listType);
		try {
			for (GstnData x : reqDto) {
				gstnlist.add(x.getGstin());
			}
			JsonElement respBody = gson.toJsonTree(reqDto);
			jsonreq.add("reqDto", respBody);
			
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setGstins(GenUtil
					.convertStringToClob(convertToQueryFormat(gstnlist)));
			entity.setCreatedBy(userName);
			entity.setReportCateg("GSTIN & E-invoice Validator");
			entity.setReportType("Vendor E-Invoice Applicability Status");
			entity.setDataType("Get Call");
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(jsonreq.toString());
			downloadRepository.save(entity);
			Long id = entity.getId();
			/*// for testing
			vendorMasterReportDownloadProcessorTesting.execute(id);
			//
*/			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			asyncJobsService.createJob(groupCode,
					JobConstants.VENDOR_MASTER_DOWNLOAD_REPORT, jobParams.toString(),
					userName, 1L, null, null);
			JsonObject resp = new JsonObject();
			
			jobParams.addProperty("reportType", "Vendor E-Invoice Applicability Status");
			jobParams.addProperty("report category", "GSTIN & E-invoice Validator");
			jobParams.addProperty("DataType", "Get Call");
			JsonElement resps = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", resps);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in creating Async Report VendorMasterDownloadReportController"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in VendorMasterDownloadReportController";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	
		}

	private String convertToQueryFormat(Set<String> gstinset) {

		List<String> list = new ArrayList<String>();
		list.addAll(gstinset);

		String queryData = null;
		if (list == null || list.isEmpty())
			return null;

		queryData = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			queryData += "," + list.get(i);
		}

		return queryData;
	}


}
