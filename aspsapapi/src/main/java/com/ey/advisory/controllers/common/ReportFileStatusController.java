package com.ey.advisory.controllers.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.entities.master.ReportCategoryMasterEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.master.ReportCategoryRepository;
import com.ey.advisory.app.async.report.service.AsyncReportCategoryDto;
import com.ey.advisory.app.async.report.service.AsyncReportsDownloadDto;
import com.ey.advisory.app.filereport.ReportFileStatusReportDto;
import com.ey.advisory.app.filereport.ReportFileStatusService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun K.A
 *
 */
@Slf4j
@RestController
public class ReportFileStatusController {

	@Autowired
	@Qualifier("ReportFileStatusServiceImpl")
	ReportFileStatusService reportFileStatusService;

	@Autowired
	@Qualifier("ReportCategoryRepository")
	ReportCategoryRepository repCategRepo;
	
	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;
	

	@PostMapping(value = "/ui/getReportFileStatusDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReportRequestStatus(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Anx2ReconRequestStatusController"
						+ ".getReportRequestStatus ,Parsing Input request";
				LOGGER.debug(msg);
			}
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();
			AsyncReportsDownloadDto req = gson.fromJson(reqJson.toString(),
					AsyncReportsDownloadDto.class);
			List<String> dataType = req.getDataType();
			List<String> reportCateg = req.getReportCateg();

			String fromDate = req.getRequestFromDate();
			String toDate = req.getRequestToDate();
			String userName = req.getUserName();
			Long entityId = req.getEntityId();

			if (dataType.contains("Outward")) {
				dataType.add("OUTWARD");
				dataType.add("outward");
			}
			

			if (dataType.contains("Outward_1A") || dataType.contains("OUTWARD_1A")) {
				dataType.add("OUTWARD_1A");
				dataType.add("outward_1a");
			}
			if (dataType.contains("Inward")) {
				dataType.add("INWARD");
				dataType.add("inward");
			} 
			
			if (dataType.contains("GSTR3B")) {
				dataType.add("vendor_payment");
			}
			
			if (dataType.contains("All Compliants")) {
				dataType.add("All");
			}

			if (reportCateg.contains("File Status")) {
				reportCateg.add("FileStatus");

			} else if (reportCateg.contains("GSTR1")) {
				reportCateg.add("Gstr1");

			}else if (reportCateg.contains("GSTR3B")) {
				reportCateg.add("DataStatus");

			}
			User user = SecurityContext.getUser();
			userName = user.getUserPrincipalName();

			if (userName == null)
				throw new AppException("Invalid User");
			

			Pair<List<ReportFileStatusReportDto>, Integer> status = reportFileStatusService
					.getFileStatusDetails(userName, reportCateg, fromDate,
							toDate, pageSize, pageNum, dataType, entityId);
			

			if (LOGGER.isDebugEnabled()) {
				String msg = "InitiateReconReportRequestStatusServiceImpl"
						+ ".getReportRequestStatus Preparing Response Object";
				LOGGER.debug(msg);
			}

			String responseData = gson.toJson(status.getValue0());
			JsonElement jsonElement = new JsonParser().parse(responseData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("AsyncReportsData", jsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
					status.getValue1(), pageNum, pageSize, "S",
					"Successfully fetched Vendor Master Processed records")));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", new Gson().toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", new Gson().toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getReportCategory", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReportCategory(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Anx2ReconRequestCategoryController"
						+ ".getReportRequestCategory ,Parsing Input request";
				LOGGER.debug(msg);
			}
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			String json = requestObject.get("req").getAsJsonObject().toString();

			AsyncReportsDownloadDto req = gson.fromJson(json.toString(),
					AsyncReportsDownloadDto.class);
			List<String> dataType = req.getDataType();

			List<ReportCategoryMasterEntity> entity = new ArrayList<>();
			List<AsyncReportCategoryDto> repCategFinalList = new ArrayList<>();

			if (!dataType.isEmpty()) {
				if (dataType.contains("ALL")) {
					entity = repCategRepo.findAll();

				} else {
					entity = repCategRepo.getBydT(dataType);
				}
				Set<String> repCateSet = new HashSet<String>();

				for (ReportCategoryMasterEntity entity1 : entity) {
					String repCateg = entity1.getRepCateg();
					List<String> repCateStr = new ArrayList<>();
					repCateStr = Arrays.asList(repCateg.split(","));
					
					repCateSet.addAll(repCateStr);
				}

				for (String repSet : repCateSet) {

					if (repSet.equalsIgnoreCase("GSTR1-GSTR3B")) {
						repCategFinalList.add(
								new AsyncReportCategoryDto("GSTR1,GSTR3B"));
					} else {
						repCategFinalList
								.add(new AsyncReportCategoryDto(repSet));

					}
				}

			}
			if (LOGGER.isDebugEnabled()) {
				String msg = "InitiateReconReportRequestStatusServiceImpl"
						+ ".getReportRequestStatus Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			JsonObject repObject = new JsonObject();
			JsonElement respBody = gson.toJsonTree(repCategFinalList);
			repObject.add("reportCategory", respBody);
			resp.add("resp", repObject);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", new Gson().toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
	
}
