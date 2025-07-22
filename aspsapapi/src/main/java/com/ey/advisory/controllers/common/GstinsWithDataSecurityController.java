package com.ey.advisory.controllers.common;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.services.onboarding.DataSecurityService;
import com.ey.advisory.app.data.repositories.client.ClientFilingStatusRepositoty;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.returns.compliance.service.CompienceHistoryHelperService;
import com.ey.advisory.app.docs.dto.GSTINDataSecurityInputDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@RestController
@Slf4j
public class GstinsWithDataSecurityController {

	@Autowired
	@Qualifier("ClientFilingStatusRepositoty")
	private ClientFilingStatusRepositoty returnDataStorageStatusRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("CompienceHistoryHelperService")
	private CompienceHistoryHelperService complienceSummery;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	@Qualifier("BasicGstr6SecCommonParam")
	BasicGstr6SecCommonParam basicGstr6SecCommonParam;

	@Autowired
	@Qualifier("dataSecurityService")
	private DataSecurityService dataSecurityService;

	private static final String Not_Initiated = "Not Initiated";

	@PostMapping("/ui/getGstinsWithDataSec")
	public ResponseEntity<String> getGstr2AProcessedRecords(
			@RequestBody String jsonString) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GstinsWithDataSecurityController getDataSecurityForUser begin");
		}
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<GstinDto> gstinDto = new ArrayList<>();
		try {
			GSTINDataSecurityInputDto inputDto = gson.fromJson(json,
					GSTINDataSecurityInputDto.class);

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GstinsWithDataSecurityController User Name: {}",
						userName);
			}
				List<String> gstinList = getGstins(new ArrayList<>(),
						inputDto.getEntityId(), inputDto.getReturnType(), null);
				for(String gstin : gstinList){
					GstinDto dto = new GstinDto();
					dto.setGstin(gstin);
					gstinDto.add(dto);
				}
				
				
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(gstinDto));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Response data for given criteria for processed data records is{}",
						resp);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retrieving Compliance processed data records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private List<String> getGstins(List<String> gstinList, Long entityId,
			String returnType, Map<String, List<String>> dataSecAttrs) {
		if (!gstinList.isEmpty())
			return gstinList;
		if ("GSTR6".equalsIgnoreCase(returnType)) {
			Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
			dto.setDataSecAttrs(dataSecAttrs);
			dto.setEntityId(Arrays.asList(entityId));
			Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
					.setGstr6DataSecuritySearchParams(dto);
			return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);
		}
		if ("GSTR7".equalsIgnoreCase(returnType)) {
			Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
			dto.setDataSecAttrs(dataSecAttrs);
			dto.setEntityId(Arrays.asList(entityId));
			Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
					.setGstr7DataSecuritySearchParams(dto);
			return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);
		}
		// gstr1,gstr3b,gstr9,itc04
		Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
		dto.setDataSecAttrs(dataSecAttrs);
		dto.setEntityId(Arrays.asList(entityId));
		Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr1DataSecuritySearchParams(dto);

		return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);

	}
}