package com.ey.advisory.controller.gstr2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResultReqDto;
import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResultValidations;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class Gstr2ReconResultReportDownloadController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	private Gstr2ReconResultValidations validService;

	@RequestMapping(value = "/ui/gstr2ReconResultReportDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReconResultData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		String groupCode = TenantContext.getTenantId();
		String msg = null;
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		JsonObject json = requestObject.get("req").getAsJsonObject();

		String msg1 = String
				.format("Inside Gstr2ReconResultReportDownloadController.gstr2ReconResultReportDownload() "
						+ "method : %s {} ", jsonString);
		LOGGER.debug(msg1);
		

		try {

			Gstr2ReconResultReqDto reqDto = gson.fromJson(json,
					Gstr2ReconResultReqDto.class);
			
			List<String> docNo = reqDto.getDocNumberList();
			int totalLength =  docNo.stream()
                    .filter(Objects::nonNull) // Exclude null elements
                    .mapToInt(String::length)
                    .sum();
			if (totalLength > 4000) {
				APIRespDto dto = new APIRespDto("Failed",
						"Document Numbers have exceeded the limit of 3000 characters");
				JsonObject resp = new JsonObject();
				JsonElement respBody = gson.toJsonTree(dto);
				 msg = "Document Numbers have exceeded the limit of 3000 characters";
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				resp.add("resp", respBody);
				// LOGGER.error(msg, e);
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}
			
			//accounting voucher number
			List<String> accVoucherNums = reqDto.getAccVoucherNums();
			int accVoucherNumLength = 0;
			if (accVoucherNums != null) {
			    // Filter out null elements and calculate the total length
			    accVoucherNumLength = accVoucherNums.stream()
			                                        .filter(Objects::nonNull) // Exclude null elements
			                                        .mapToInt(String::length)
			                                        .sum();
			}

			if (accVoucherNumLength > 2000) {
			    APIRespDto dto = new APIRespDto("Failed",
			            "Accounting voucher Numbers have exceeded the limit of 2000 characters");
			    JsonObject resp = new JsonObject();
			    JsonElement respBody = gson.toJsonTree(dto);
			     msg = "Accounting voucher Numbers have exceeded the limit of 2000 characters";
			    resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			    resp.add("resp", respBody);
			    // Log the error message
			    LOGGER.error(msg);
			    return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			
			//Vendor Gstin Number
			List<String> vendorGstins = reqDto.getVndrGstins();
			int vendorGstinsLength = 0;
			if (vendorGstins != null) {
			    // Filter out null elements and calculate the total length
				vendorGstinsLength = vendorGstins.stream()
			                                        .filter(Objects::nonNull) // Exclude null elements
			                                        .mapToInt(String::length)
			                                        .sum();
			}

			if (vendorGstinsLength > 4000) {
			    APIRespDto dto = new APIRespDto("Failed",
			            "Vendor GSTIN has exceeded the limit of 3000 characters");
			    JsonObject resp = new JsonObject();
			    JsonElement respBody = gson.toJsonTree(dto);
			     msg = "Vendor GSTIN has exceeded the limit of 3000 characters";
			    resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			    resp.add("resp", respBody);
			    // Log the error message
			    LOGGER.error(msg);
			    return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			validService.validations(reqDto);

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setTaxPeriodFrom(reqDto.getFromTaxPeriod());
			entity.setTaxPeriodTo(reqDto.getToTaxPeriod());
			entity.setReconCriteria(reqDto.getReconCriteria());
			if (reqDto.getGstins().isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Invoking service to get gstins for entity %s ",
							reqDto);
					LOGGER.debug(msg);
				}

				try {
					List<Long> entityIds = new ArrayList<>();
					entityIds.add(Long.valueOf(reqDto.getEntityId()));
					Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
							.getOutwardSecurityAttributeMap();
					Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(entityIds,
									outwardSecurityAttributeMap);
					List<String> gstnsList = dataSecAttrs
							.get(OnboardingConstant.GSTIN);

					reqDto.setGstins(gstnsList);
				} catch (Exception ex) {
					msg = String.format(
							"Error while fetching Gstins from entityId %s", ex);
					LOGGER.error(msg);
					throw new AppException(msg);
				}

			}

			entity.setGstins(GenUtil.convertStringToClob(
					convertToQueryFormat(reqDto.getGstins())));

			if ("2A_PR".equalsIgnoreCase(reqDto.getReconType())) {
				entity.setReportType(
						"Consolidated 2A-6AvsPR Report (Recon Result)");
			} else {
				entity.setReportType(
						"Consolidated 2BvsPR Report (Recon Result)");
			}
			entity.setDocType(!reqDto.getDocType().isEmpty()
					? convertToQueryFormat(reqDto.getDocType()) : null);
			entity.setTableType(!reqDto.getReconType().isEmpty()
					? convertToQueryFormat(reqDto.getReportType()) : null);

			if (!Strings.isNullOrEmpty(reqDto.getFromDocDate())
					&& !Strings.isNullOrEmpty(reqDto.getToDocDate())) {
				entity.setDocDateFrom(LocalDate.parse(reqDto.getFromDocDate()));
				entity.setDocDateTo(LocalDate.parse(reqDto.getToDocDate()));
			}
			entity.setDocNum(!reqDto.getDocNumberList().isEmpty()
					? convertToQueryFormat(reqDto.getDocNumberList()) : null);
			entity.setAccVoucherNo(!reqDto.getAccVoucherNums().isEmpty()
					? convertToQueryFormat(reqDto.getAccVoucherNums()) : null);
			entity.setVendorPan(!reqDto.getVndrPans().isEmpty()
					? convertToQueryFormat(reqDto.getVndrPans()) : null);
			entity.setVendorGstin(!reqDto.getVndrGstins().isEmpty()
					? convertToQueryFormat(reqDto.getVndrGstins()) : null);
			entity.setToTaxPrd3B(!Strings.isNullOrEmpty(reqDto.getToTaxPrd3b())
					? Integer.parseInt(reqDto.getToTaxPrd3b()):null);
			entity.setFromTaxPrd3B(!Strings.isNullOrEmpty(reqDto.getFrmTaxPrd3b())
					? Integer.parseInt(reqDto.getFrmTaxPrd3b()):null);
			entity.setCreatedBy(userName);

			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReportCateg("Recon Result");
			entity.setDataType("Inward");
			entity.setEntityId(Long.valueOf(reqDto.getEntityId()));
			entity.setUsrAcs6(reqDto.getTaxPeriodBase());
			entity = downloadRepository.save(entity);

			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			jobParams.addProperty("reconType", reqDto.getReconType());

			asyncJobsService.createJob(groupCode,
					JobConstants.CONSOLIDATED_RECON_RESULT_REPORT,
					jobParams.toString(), userName, 1L, null, null);

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
			msg = "Error Occured in Async Report Controller";
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(e.getMessage()));
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
			if (list.get(i) == null) {
				continue;
			}
			queryData += "," + list.get(i);
		}

		return queryData;
	}
}
