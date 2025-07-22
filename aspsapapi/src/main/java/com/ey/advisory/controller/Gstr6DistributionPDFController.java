package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.TemplateMappingEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.TemplateMappingRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.Gstr6DistributionDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class Gstr6DistributionPDFController {

	@Autowired
	@Qualifier("TemplateMappingRepository")
	private TemplateMappingRepository templateMappingRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinDetailRepository;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	AsyncJobsService asyncJobsService;

	@PostMapping(value = "/ui/gstr6DistributionRedistributionPdfReport")
	public ResponseEntity<String> generateEinvSummaryReport(
			@RequestBody String jsonString)
			{

		Gson gson = new Gson();
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		Type listType = new TypeToken<List<Gstr6DistributionDto>>() {
		}.getType();
		JsonObject jsonReq = requestObject.get("req").getAsJsonObject();

		Long reqEntityId =jsonReq.get("entityId").getAsLong();

		JsonArray json = jsonReq.get("gstrPrintDetails").getAsJsonArray();
		//JsonArray json = requestObject.get("req").getAsJsonArray();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request Json {} for GSTR6 distribution", json);
		}

		Set<String> gstnlist = new HashSet<String>();
		Set<String> docTypeList = new HashSet<String>();
		Set<String> templateTypeList = new HashSet<String>();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName() != null
				? user.getUserPrincipalName() : "SYSTEM";

		List<Gstr6DistributionDto> reqDto = gson.fromJson(json, listType);

		JsonObject resp = new JsonObject();

		List<String> templateTypes = deriveTemplateCode(reqEntityId);

		try {

			for (Gstr6DistributionDto x : reqDto) {
				gstnlist.add(x.getSgstin());
				docTypeList.add(x.getDocumentType());
				templateTypeList.add(deriveTemplateType(x.getDocumentType()));
			}
			if (templateTypes.isEmpty() || !templateTypes.containsAll(templateTypeList))
			{
				APIRespDto dto = new APIRespDto("Failed",
					"User needs to onboard first");
			//JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "User needs to onboard first";
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(),
					HttpStatus.OK);
			
			}/*else if(!templateTypes.containsAll(templateTypeList))
			{
				APIRespDto dto = new APIRespDto("Failed",
						"User needs to onboard first");
				//JsonObject resp = new JsonObject();
				JsonElement respBody = gson.toJsonTree(dto);
				String msg = "User needs to onboard first";
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				resp.add("resp", respBody);
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
				LOGGER.error("TEMPLATES ARE NOT SELECTED FROM THE ONBOARDING");
				throw new AppException("User needs to onboard first");
			}*/

			// to create one entrty in the DB with respect to request

			//JsonObject jsonReq = new JsonObject();
			JsonElement respBody = gson.toJsonTree(reqDto);
			jsonReq.add("reqDto", respBody);

			JsonObject jobParams = new JsonObject();

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			entity.setGstins(GenUtil
					.convertStringToClob(convertToQueryFormat(gstnlist)));

			entity.setDocType(convertToQueryFormat(docTypeList));
			entity.setCreatedBy(userName);
			entity.setDataType("GSTR6");
			entity.setReportCateg("ISD Distribution");
			entity.setReportType("ISD Document");
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(jsonReq.toString());
			entity = downloadRepository.save(entity);

			Long entityId = entity.getId();
			// job params inclues we are passing the parameter in to the
			// processosr
			jobParams.addProperty("id", entityId);

			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR6_DISTRIBUTION_ISD, jobParams.toString(),
					userName, 1L, null, null);

			jobParams.addProperty("reportType", "ISD Document");
			JsonElement resps = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", resps);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while generating GSTR6 pdf ", ex);
			String msg = String.format("Exception while generating GSTR6 pdf ",
					ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
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

	
	private List<String> deriveTemplateCode(Long entityId) {

		List<String> templateTypes = new ArrayList<>();

		List<TemplateMappingEntity> templateCodeEntity = templateMappingRepository
				.findByEntityIdAndIsDeleteFalse(entityId);
		if (!templateCodeEntity.isEmpty()) {
			templateTypes = templateCodeEntity.stream()
					.map(o -> o.getTemplateType())
					.collect(Collectors.toCollection(ArrayList::new));
		}
		return templateTypes;

	}

	private String deriveTemplateType(String docType) {

		if (docType.equalsIgnoreCase("CR")) {
			return "ISD Distribution Credit Note";
		} else if (docType.equalsIgnoreCase("RCR")) {
			return "ISD Redistribution Credit Note";

		} else if (docType.equalsIgnoreCase("INV")) {
			return "ISD Distribution Invoice";
		}
		return "ISD Redistribution Invoice";

	}

}
