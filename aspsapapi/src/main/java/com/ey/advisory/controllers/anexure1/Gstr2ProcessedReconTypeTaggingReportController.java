package com.ey.advisory.controllers.anexure1;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2ProcessedReconTypeTaggedReportGstinDetailsRepository;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.app.recon.type.tagging.report.Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity;
import com.ey.advisory.app.services.search.filestatussearch.AsyncReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class Gstr2ProcessedReconTypeTaggingReportController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	Gstr2ProcessedReconTypeTaggedReportGstinDetailsRepository gstinDetailsRepository;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("AsyncGstr2ReportHandlerImpl")
	AsyncReportHandler asyncFileStatusReportHandler;

	private static final List<String> TABLETYPE = ImmutableList.of("B2B",
			"B2BA", "CDN", "CDNA", "RCURD", "RCURDA", "IMPGS", "IMPG", "IMPS",
			"ISD", "ISDA", "IMPSA", "IMPGA", "IMPGSA", "RCMADV");
	private static final List<String> DOCTYPE = ImmutableList.of("CR", "CR23",
			"DOC", "DR", "EXT", "HJHH", "ICN", "INC", "INV", "INVA", "INVAA",
			"IVN", "NIL", "NON", "NVI", "RCR", "RDR", "RNV", "RPV", "RSLF",
			"SLF", "SMP", "ADV", "ADJ");

	private static final List<String> ReconReportType = ImmutableList.of(
			"Exact Match", "Match With Tolerance", "Value Mismatch",
			"POS Mismatch", "Doc Date Mismatch", "Doc Type Mismatch",
			"Doc No Mismatch I", "Doc No Mismatch II",
			"Doc No & Doc Date Mismatch", "Multi-Mismatch", "Addition in PR",
			"Potential-I", "Potential-II", "Logical Match", "Import-Match",
			"Import-Mismatch", "Import-Addition in PR", "ISD-Exact Match",
			"ISD-Match With Tolerance", "ISD-Value Mismatch",
			"ISD-Doc Date Mismatch", "ISD-Doc Type Mismatch",
			"ISD-Doc No Mismatch I", "ISD-Doc No Mismatch II",
			"ISD-Doc No & Doc Date Mismatch", "ISD-Multi-Mismatch",
			"ISD-Addition in PR", "ISD-Potential-I", "ISD-Potential-II",
			"ISD-Logical Match", "Locked-Force Match", "Locked-3B Response",
			"Not Applicable", "Excluded from Recon", "Dropped from Recon",
			"Not Participated");

	@PostMapping(value = "/ui/downloadGstr2ReconTypeTaggingReports")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr2ProcessedReconTypeTaggingReportController";
			LOGGER.debug(msg);
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonParams)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download CSV Report Controller: %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try {
			FileStatusReportDto reqDto = gson.fromJson(json,
					FileStatusReportDto.class);
			if (reqDto.getTableType() == null
					|| reqDto.getTableType().size() <= 0) {
				reqDto.setTableType(TABLETYPE);
			}

			if (reqDto.getDocType() == null
					|| reqDto.getDocType().size() <= 0) {
				reqDto.setDocType(DOCTYPE);
			}

			if (reqDto.getReconReportType() == null
					|| reqDto.getReconReportType().size() <= 0) {
				reqDto.setReconReportType(ReconReportType);
			}

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}

			asyncFileStatusReportHandler.setDataToEntity(entity, reqDto);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Request for Download CSV Report Controller: %s",
						json.toString());
				LOGGER.debug(msg);
			}

			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();

			Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
			List<String> gstins = dataSecAttrs.get("GSTIN");

			List<Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity> 
					gstinDetails = new ArrayList<>();
			for (String gstin : gstins) {

				List<Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity> 
				gstinDetail = saveGstinDetails(reqDto, id, gstin);
				
				gstinDetails.addAll(gstinDetail);
			}
			
			gstinDetailsRepository.saveAll(gstinDetails);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);

			if ("INWARD".equalsIgnoreCase(entity.getDataType())) {
				
					asyncJobsService.createJob(groupCode,
							JobConstants.Processed_Records_Recon_Tagging_Job,
							jobParams.toString(), userName, 1L, null, null);
				
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created JOB with job params as : %s",
						jobParams.toString());
				LOGGER.debug(msg);
			}

			String reportType = getReportType(reqDto.getType(),
					reqDto.getStatus());

			jobParams.addProperty("reportType", reportType);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occurred in Async Report CSV Download Controller"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error occurred in Async Report Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	private String getReportType(String type, String status) {

		String reportType = null;
		try {
			switch (type) {

			case ReportTypeConstants.Processed_Records_Recon_Tagging:
				reportType = "Processed Records (Recon Tagging)";
				break;

			default:
				reportType = "Invalid report type";

			}
		} catch (Exception e) {
			LOGGER.error("Invalid report type");
		}

		return reportType;
	}

	private List<Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity> saveGstinDetails(
			FileStatusReportDto reqDto, Long id, String gstin) {

		List<Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity> list = new ArrayList<>();

		List<String> taxPeriods = getTaxPeriods(reqDto.getReturnFrom(),
				reqDto.getReturnTo());

		for (String taxPeriod : taxPeriods) {

			Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity obj =
					new Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity();

			obj.setCreatedOn(LocalDateTime.now());
			obj.setGstin(gstin);
			obj.setReconType(reqDto.getReconType());
			obj.setReportDwndId(id);
			obj.setTaxPeriod(taxPeriod);
			list.add(obj);
		}

		return list;
	}

	private List<String> getTaxPeriods(String fromTaxPeriod,
			String toTaxPeriod) {
		List<String> taxPeriods = new ArrayList<>();

		int fromYear = Integer.parseInt(fromTaxPeriod.substring(2));
		int fromMonth = Integer.parseInt(fromTaxPeriod.substring(0, 2));
		int toYear = Integer.parseInt(toTaxPeriod.substring(2));
		int toMonth = Integer.parseInt(toTaxPeriod.substring(0, 2));
		
		if (fromYear > toYear || (fromYear == toYear && fromMonth > toMonth)) {
					    throw new IllegalArgumentException("Invalid date range: fromMonth must be before toMonth.");
					}
			
					// Ensure valid month range before entering the loop
		if (fromMonth < 1 || fromMonth > 12 || toMonth < 1 || toMonth > 12) {
					    throw new IllegalArgumentException("Invalid month values. Months should be between 1 and 12.");
					}
		while (fromYear < toYear
				|| (fromYear == toYear && fromMonth <= toMonth)) {
			String taxPeriod = String.format("%02d%04d", fromMonth, fromYear);
			taxPeriods.add(taxPeriod);

			// Increment month and possibly year
			fromMonth++;
			if (fromMonth > 12) {
				fromMonth = 1;
				fromYear++;
			}
		}

		return taxPeriods;
	}

}
