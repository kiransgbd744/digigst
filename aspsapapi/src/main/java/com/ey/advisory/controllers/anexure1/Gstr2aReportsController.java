/**
 * 
 */
package com.ey.advisory.controllers.anexure1;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.app.services.reports.Gstr2aGetReportHandler;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
/**
 * @author Sujith.Nanga
 *
 * 
 */
@RestController
public class Gstr2aReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2aReportsController.class);

	@Autowired
	@Qualifier("BasicGstr6SecCommonParam")
	BasicGstr6SecCommonParam basicGstr6SecCommonParam;

	@Autowired
	@Qualifier("Gstr2aGetReportHandler")
	private Gstr2aGetReportHandler gstr2aGetReportHandler;

	@Autowired
	private GstnApi gstnapi;
	
	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	private static final List<String> TABLETYPE = ImmutableList.of("B2B",
			"B2BA", "CDN", "CDNA", "ISD", "ISDA", "IMPG", "IMPGSEZ");

	private static final List<String> DOCTYPE = ImmutableList.of("INV", "CR",
			"DR", "RNV", "RCR", "RDR", "ISD", "ISDCN", "ISDA", "ISDCNA", "TCS",
			"TDS", "TDSA", "IMPG", "IMPGSEZ");
	

	@RequestMapping(value = "/ui/gstr2aGetDownloads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	/*public void downloadreviewSummReport(@RequestBody String jsonString,
			HttpServletResponse response) {*/
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonString) {
		
		/*Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray jsonReqArray = (new JsonParser()).parse(jsonString)
				.getAsJsonObject().get("req").getAsJsonArray();
		@SuppressWarnings("serial")
		Type listType = new TypeToken<List<GstnConsolidatedReqDto>>() {
		}.getType();

		List<GstnConsolidatedReqDto> reqDtoCriterias = gson
				.fromJson(jsonReqArray, listType);

		try {
			String fileName = null;
			Workbook workbook = null;

			if (gstnapi
					.isDelinkingEligible(APIConstants.GSTR2A.toUpperCase())) {
				workbook = gstr2aGetReportHandler
						.findGstnErrorReports(reqDtoCriterias);

				
				 * * else { // Write new handler to fetch the limited columns
				 * that is // required in report workbook =
				 * gstr1aGetReportHandler
				 * .downloadGstr6SaveSectionsReport(reqDtos); }
				 

				fileName = "GSTR-2A_Get_Records";
			}
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
		} catch (JsonParseException ex) {
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
	*/
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray jsonReqArray = (new JsonParser()).parse(jsonString)
				.getAsJsonObject().get("req").getAsJsonArray();
		@SuppressWarnings("serial")
		Type listType = new TypeToken<List<GstnConsolidatedReqDto>>() {
		}.getType();

		List<GstnConsolidatedReqDto> reqDto = gson
				.fromJson(jsonReqArray, listType);
	
		try {
			String groupCode = TenantContext.getTenantId();

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			/*GstnConsolidatedReqDto reqDto = gson.fromJson(json,
					GstnConsolidatedReqDto.class)*/;

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			setDataToEntity(entity, reqDto.get(0));
			entity = fileStatusDownloadReportRepo.save(entity);
			Long id = entity.getId();
			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			String reportType = "GSTR-2A_Get_Records";
			jobParams.addProperty("reportType", reportType);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("async job calling for gstr2a");
				LOGGER.debug(msg);
			}

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2A_CONSOLIDATED, jobParams.toString(),
					userName, 1L, null, null);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("async job end for gstr2a");
				LOGGER.debug(msg);
			}

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

	public void setDataToEntity(FileStatusDownloadReportEntity entity,
			GstnConsolidatedReqDto reqDto) {

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (reqDto.getGstr2aSections() == null
				|| reqDto.getGstr2aSections().size() <= 0) {
			reqDto.setGstr2aSections(TABLETYPE);
		}

		Integer derivedTaxPeriod = Integer.valueOf(
				reqDto.getTaxPeriod().substring(2).concat(reqDto
						.getTaxPeriod().substring(0, 2)));

		entity.setTaxPeriodFrom(reqDto.getTaxPeriod());
		entity.setTaxPeriodTo(reqDto.getTaxPeriod());
		entity.setDerivedRetPeriodFrom(Long.valueOf(derivedTaxPeriod));
		entity.setDerivedRetPeriodFromTo(Long.valueOf(derivedTaxPeriod));

		entity.setCreatedBy(userName);
		entity.setCreatedDate(LocalDateTime.now());
		entity.setReportStatus(ReportStatusConstants.INITIATED);
		entity.setGstins(GenUtil.convertStringToClob(
				reqDto.getGstin()));
		
		entity.setEntityId(Long.parseLong(reqDto.getEntityId()));
		List<String> tableTypeList = new ArrayList<String>();
		if (!CollectionUtils.isEmpty(reqDto.getGstr2aSections())) {
			for (String table : reqDto.getGstr2aSections()) {
				tableTypeList.add("'" + table.toUpperCase() + "'");
			}

			entity.setTableType(
					tableTypeList.stream().collect(Collectors.joining(",")));
		}
		List<String> docTypeList = new ArrayList<String>();
		
			for (String table : DOCTYPE) {
				docTypeList.add("'" + table.toUpperCase() + "'");
			
			entity.setDocType(
					docTypeList.stream().collect(Collectors.joining(",")));
			entity.setReportType("GSTR-2A_Get_Records");
			entity.setDataType("Inward");
			entity.setReportCateg("GSTR-2A");
		}

	}

	

}
