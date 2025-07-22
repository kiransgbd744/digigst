package com.ey.advisory.controllers.compliancerating;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.compliancerating.GstinWiseComplianceRatingStatus;
import com.ey.advisory.app.data.services.compliancerating.VendorComplianceCountDto;
import com.ey.advisory.app.data.services.compliancerating.VendorComplianceRatingHelperService;
import com.ey.advisory.app.data.services.compliancerating.VendorComplianceRatingRequestDto;
import com.ey.advisory.app.data.services.compliancerating.VendorComplianceRatingService;
import com.ey.advisory.app.data.services.compliancerating.VendorRatingCriteriaDefaultUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@RestController
public class VendorComplianceRatingController {

	private static final String ATTACHMENT_FILENAME = "attachment; filename=";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String APPLICATION_OCTET_STREAM = "APPLICATION/OCTET-STREAM";

	@Autowired
	@Qualifier("VendorComplianceRatingServiceImpl")
	private VendorComplianceRatingService ratingService;

	@Autowired
	@Qualifier("VendorComplianceRatingHelperServiceImpl")
	private VendorComplianceRatingHelperService ratingHelperService;

	@PostMapping(value = "/ui/getVendorComplianceRating", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorComplianceRating(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			JsonObject request = requestObject.getAsJsonObject("req");
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Inside getVendorComplianceRating"
						+ " method in VendorComplianceRatingController with"
						+ " request: %s", request);
				LOGGER.debug(msg);
			}

			LocalDateTime startTime = LocalDateTime.now();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Start time for getting rating is {}", startTime);
			}

			VendorComplianceRatingRequestDto requestDto = gson.fromJson(
					request.toString(), VendorComplianceRatingRequestDto.class);

			List<GstinWiseComplianceRatingStatus> overallRatingStatusDtos = ratingService
					.getVendorComplianceRatingData(requestDto);

			LocalDateTime endTime = LocalDateTime.now();
			if (LOGGER.isDebugEnabled()) {
				int seconds = (int) ChronoUnit.MILLIS.between(startTime,
						endTime);
				LOGGER.debug(
						"Endtime for time for getting rating is {} "
								+ " and total time taken is {}ms",
						endTime, seconds);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Started for getting count details at {}",LocalDateTime.now());
			}
			VendorComplianceCountDto countDTo = ratingHelperService
					.getVendorCountDetails(overallRatingStatusDtos,
							requestDto.getReturnType(),
							requestDto.getEntityId(), requestDto.getSource());
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("End for getting count details at {}",LocalDateTime.now());
			}

			boolean isChannelOneClient = ratingHelperService
					.getChannelOneClientInfo(requestDto.getEntityId());
			List<List<GstinWiseComplianceRatingStatus>> paginatedList = null;
			int totalCount = 0;
			List<GstinWiseComplianceRatingStatus> requiredList = null;
			if (overallRatingStatusDtos != null
					&& !overallRatingStatusDtos.isEmpty()) {

				paginatedList = Lists.partition(overallRatingStatusDtos,
						pageSize);
				totalCount = overallRatingStatusDtos.size();
				int pages = 0;
				if (totalCount % pageSize == 0) {
					pages = totalCount / pageSize;
				} else {
					pages = totalCount / pageSize + 1;
				}

				if (pageNum <= pages) {
					requiredList = paginatedList.get(pageNum);
				} else {
					throw new AppException("Invalid Page Number");
				}
			}

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(requiredList);
			JsonElement gstr1NonCompliantVgstins = gson
					.toJsonTree(countDTo.getGstr1NonCompliantVgstins());
			JsonElement gstr3BNonCompliantVgstins = gson
					.toJsonTree(countDTo.getGstr3BNonCompliantVgstins());

			resps.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNum, pageSize, "S",
							"Successfully fetched vendor rating records")));
			resps.addProperty("isChannelOne", isChannelOneClient);
			resps.addProperty("ttlVendors", countDTo.getTtlVendors());
			resps.addProperty("compliantVendors",
					countDTo.getCompliantVendors());
			resps.addProperty("nCompliantVendors",
					countDTo.getNCompliantVendors());
			resps.addProperty("gstr1NcomVendors",
					countDTo.getGstr1NcomVendors());
			resps.addProperty("gstr3BNcomVendors",
					countDTo.getGstr3BNcomVendors());
			resps.add("gstr1NonCompliantVgstins", gstr1NonCompliantVgstins);
			resps.add("gstr3BNonCompliantVgstins", gstr3BNonCompliantVgstins);

			resps.add("resp", respBody);

			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			APIRespDto dto = new APIRespDto("Failed", ex.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the ratings";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}

	}

//	@PostMapping(value = "/ui/getVendorRatingTableReport", produces = {
//			MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> getVendorRatingTableData(
//			@RequestBody String jsonString, HttpServletResponse response)
//			throws IOException {
//		Gson gson = GsonUtil.newSAPGsonInstance();
//		Workbook workbook = null;
//		try {
//			JsonObject request = (new JsonParser()).parse(jsonString)
//					.getAsJsonObject().getAsJsonObject("req");
//			if (LOGGER.isDebugEnabled()) {
//				String msg = String.format("Inside getVendorComplianceRating"
//						+ " method in VendorComplianceRatingController with"
//						+ " request: %s", request);
//				LOGGER.debug(msg);
//			}
//			VendorComplianceRatingRequestDto requestDto = gson.fromJson(
//					request.toString(), VendorComplianceRatingRequestDto.class);
//
//			workbook = ratingService.getComplianceRatingTableReport(requestDto);
//
//			LocalDateTime now = LocalDateTime.now();
//			String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();
//			timeMilli = timeMilli.replace(".", "");
//			timeMilli = timeMilli.replace("-", "");
//			timeMilli = timeMilli.replace(":", "");
//
//			String fileName = null;
//
//			if ("vendor".equalsIgnoreCase(requestDto.getSource())) {
//				fileName = "Vendors_Compliance_Table_data";
//			} else if ("customer".equalsIgnoreCase(requestDto.getSource())) {
//				fileName = "Customers_Compliance_Table_data";
//
//			} else if ("my".equalsIgnoreCase(requestDto.getSource())) {
//				fileName = "My_Compliance_Table_data";
//
//			}
//
//			if (workbook != null) {
//				response.setContentType(APPLICATION_OCTET_STREAM);
//				response.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME
//						+ fileName + "_" + timeMilli + ".xlsx");
//				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
//				response.getOutputStream().flush();
//			}
//
//			return new ResponseEntity<>(null, HttpStatus.OK);
//		} catch (Exception ex) {
//			APIRespDto dto = new APIRespDto("Failed", ex.getMessage());
//			JsonObject resp = new JsonObject();
//			JsonElement respBody = gson.toJsonTree(dto);
//			String msg = "Unexpected error while generating file";
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
//			resp.add("resp", respBody);
//			LOGGER.error(msg, ex);
//			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//
//		}
//	}

	@PostMapping(value = "/ui/downloadVendorRatingAsyncReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> downloadVendorRatingAsyncReport(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		String reportType = null;
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject request = requestObject.getAsJsonObject("req");
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside downloadVendorRatingAsyncReport"
								+ " method in VendorComplianceRatingController "
								+ "with request: %s", request);
				LOGGER.debug(msg);
			}
			VendorComplianceRatingRequestDto requestDto = gson.fromJson(
					request.toString(), VendorComplianceRatingRequestDto.class);

			if (VendorRatingCriteriaDefaultUtil.VENDOR
					.equalsIgnoreCase(requestDto.getSource())) {
				if ("Vendor Compliance Rating"
						.equalsIgnoreCase(requestDto.getReportType())) {
					reportType = "Vendor Compliance History";
				} else {
					reportType = "Vendor Compliance Summary";
				}

			} else if (VendorRatingCriteriaDefaultUtil.CUSTOMER
					.equalsIgnoreCase(requestDto.getSource())) {
				if ("Vendor Compliance Rating"
						.equalsIgnoreCase(requestDto.getReportType())) {
					reportType = "Customer Compliance History";
				} else {
					reportType = "Customer Compliance Summary";
				}
			} else {
				if ("Vendor Compliance Rating"
						.equalsIgnoreCase(requestDto.getReportType())) {
					reportType = "My Compliance History";
				} else {
					reportType = "My Compliance Summary";
				}

			}

			Long id = ratingService.createVendorComplianceRatingAsyncReport(
					requestDto, requestObject.toString());
			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", reportType + " Reports");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			APIRespDto dto = new APIRespDto("Failed", ex.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while generating async report";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

}
