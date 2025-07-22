/**
 * 
 */
package com.ey.advisory.controller.drc;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.inward.einvoice.EinvoiceNestedDetailedReportRequestDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */

@Slf4j
@RestController
public class EinvoicesNestedReportDownloadController {

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	AsyncJobsService asyncJobsService;

	@PostMapping(value = "/ui/getEnvoicesNestedReportDownload", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEnvoicesNestedReportDownload(
			@RequestBody String jsonString) throws IOException {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		

		String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String msg1 = String
				.format("Inside EinvoicesNestedReportDownloadController.getEnvoicesNestedReportDownload() "
						+ "method : %s {} ", jsonString);
		LOGGER.debug(msg1);

		try {
			EinvoiceNestedDetailedReportRequestDto reqDto = gson.fromJson(json,
					EinvoiceNestedDetailedReportRequestDto.class);
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			if(reqDto.getType().equalsIgnoreCase("SUMMARY")){
				entity.setGstins(GenUtil.convertStringToClob(
						convertToQueryFormat(reqDto.getGstins())));
				entity.setTaxPeriod(reqDto.getReturnPeriod());
				if(reqDto.getSupplyType() != null && !reqDto.getSupplyType().isEmpty()){
					String supplyTypeDetails = convertToQueryFormat(reqDto.getSupplyType());
					entity.setSupplyType(supplyTypeDetails);
				}
				
				String irnStatusDetails = convertToQueryFormat(reqDto.getIrnSts());
				entity.setIrnSts(irnStatusDetails);
			
				entity.setCreatedBy(userName);
				entity.setCreatedDate(LocalDateTime.now());
				entity.setReportStatus(ReportStatusConstants.INITIATED);
				entity.setDataType("Inward");
				entity.setReportCateg("E-invoice");
				entity.setEntityId(reqDto.getEntityId());
				entity.setType("SUMMARY");
				entity.setReportType("Preceding Document & Other details");
			} else {
				entity.setGstins(GenUtil.convertStringToClob(
						convertToQueryFormat(reqDto.getGstins())));
				entity.setCriteria(reqDto.getCriteria());
				if(reqDto.getCriteria().equalsIgnoreCase("Month")){
					entity.setTaxPeriodFrom(reqDto.getFromTaxPeriod().substring(2,6)+reqDto.getFromTaxPeriod().substring(0, 2));
					entity.setTaxPeriodTo(reqDto.getToTaxPeriod().substring(2, 6) + reqDto.getToTaxPeriod().substring(0, 2));
				} else {
					LocalDate fromDate = LocalDate.parse(reqDto.getFromDate());
					LocalDate toDate = LocalDate.parse(reqDto.getToDate());
					entity.setRequestFromDate(fromDate.atStartOfDay());
					entity.setRequestToDate(toDate.atStartOfDay());
				}
				if(reqDto.getSupplyType() != null){
					String supplyTypeDetails = convertToQueryFormat(reqDto.getSupplyType());
					entity.setSupplyType(supplyTypeDetails);
				}
				String irnStatusDetails = convertToQueryFormat(reqDto.getIrnSts());
				entity.setIrnSts(irnStatusDetails);
				entity.setIrnNum(reqDto.getIrnNo());
				entity.setDocNum(reqDto.getDocNum());
				entity.setVendorGstin(reqDto.getVendorGstin());
				if(reqDto.getIds() != null){
					entity.setListingIds(GenUtil.convertStringToClob(reqDto.getIds().stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","))));
				}
				entity.setCreatedBy(userName);
				entity.setCreatedDate(LocalDateTime.now());
				entity.setReportStatus(ReportStatusConstants.INITIATED);
				entity.setDataType("Inward");
				entity.setReportCateg("E-invoice");
				entity.setEntityId(reqDto.getEntityId());
				entity.setType("INVMNGT");
				entity.setReportType("Preceding Document & Other details");
			}
			entity = downloadRepository.save(entity);

			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);

			asyncJobsService.createJob(groupCode,
					JobConstants.EINVOICE_NESTED_REPORT, jobParams.toString(),
					userName, 1L, null, null);

			String reportType = entity.getReportType();

			jobParams.addProperty("reportType", reportType);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in Async Report CSV Download Controller"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Async Report Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	private String convertToQueryFormat(List<String> list) {

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