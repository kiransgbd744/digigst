package com.ey.advisory.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
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
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.services.onboarding.DataSecurityService;
import com.ey.advisory.app.data.repositories.client.ClientFilingStatusRepositoty;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.returns.compliance.service.ComplienceSummeryRespDto;
import com.ey.advisory.app.data.returns.compliance.service.ComplienceSummeryService;
import com.ey.advisory.app.data.returns.compliance.service.GroupComplianceHistoryDataRecordsReqDto;
import com.ey.advisory.app.data.returns.compliance.service.GroupComplianceResponseDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
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
public class GroupGstr9ComplianceScreenController {

	@Autowired
	@Qualifier("Gstr9ComplianceServiceImpl")
	private ComplienceSummeryService complienceSummeryService;

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
	@Qualifier("dataSecurityService")
	private DataSecurityService dataSecurityService;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@PostMapping(value = "/ui/getGroupGstr9Compliance", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> fetchProcessedRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("gstr9 complienceHistory request{}");
		}

		try {
			GroupComplianceHistoryDataRecordsReqDto groupComplianceHistoryDataRecordsReqDto = gson
					.fromJson(json,
							GroupComplianceHistoryDataRecordsReqDto.class);

			List<Gstr2aProcessedDataRecordsReqDto> groupComplianceProcessed = new ArrayList<>();
			List<Long> entityIdList = new ArrayList<>();
			if (!groupComplianceHistoryDataRecordsReqDto.getDataSecAttrs()
					.get("entityIds").isEmpty()) {
				entityIdList = groupComplianceHistoryDataRecordsReqDto
						.getDataSecAttrs().get("entityIds");
			} else {
				User user = SecurityContext.getUser();
				String userName = user.getUserPrincipalName();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Anx2GetDataSecurityController User Name: {}",
							userName);
				}
				Map<Long, Map<String, List<Pair<Long, String>>>> attributeMap = user
						.getAttributeMap();
				entityIdList = new ArrayList<>(attributeMap.keySet());

			}
			for (Long entityId : entityIdList) {
				Gstr2aProcessedDataRecordsReqDto req = new Gstr2aProcessedDataRecordsReqDto();
				req.setEntity(entityId.toString());
				req.setFinancialYear(groupComplianceHistoryDataRecordsReqDto
						.getFinancialYear());
				req.setReturnType(groupComplianceHistoryDataRecordsReqDto
						.getReturnType());
				Map<String, List<String>> dataSecAttrs = new HashedMap<>();
				List<String> gstinList = getGstins(new ArrayList<>(), entityId,
						groupComplianceHistoryDataRecordsReqDto.getReturnType(),
						dataSecAttrs);
				dataSecAttrs.put("GSTIN", gstinList);
				req.setDataSecAttrs(dataSecAttrs);
				groupComplianceProcessed.add(req);
			}

			List<GroupComplianceResponseDto> groupComplianceResponseList = new ArrayList<>();
			for (Gstr2aProcessedDataRecordsReqDto gstr2AProcessedDataRecordsReqDto : groupComplianceProcessed) {
				GroupComplianceResponseDto groupComplianceResponseDto = new GroupComplianceResponseDto();
				int filingStatusCount = 0;
				int totalCount = 0;
				if (!gstr2AProcessedDataRecordsReqDto.getDataSecAttrs()
						.get("GSTIN").isEmpty()) {
					List<ComplienceSummeryRespDto> respDtos = complienceSummeryService
							.findcomplienceSummeryRecords(
									gstr2AProcessedDataRecordsReqDto);
					for (int i = 0; i < respDtos.size(); i++) {
						if (respDtos.get(i) != null) {
							if (respDtos.get(i).getFilingStatus() != null)
								if (respDtos.get(i).getFilingStatus()
										.equalsIgnoreCase("Filed"))
									filingStatusCount++;
						}
					}
					totalCount = respDtos.size();
				}
				groupComplianceResponseDto.setEntityId(
						gstr2AProcessedDataRecordsReqDto.getEntity());
				String entityName = entityInfoRepository.findEntityNameByEntityId(
						Long.valueOf(gstr2AProcessedDataRecordsReqDto.getEntity()));
				groupComplianceResponseDto.setEntityName(entityName);
				String fy[] = gstr2AProcessedDataRecordsReqDto
						.getFinancialYear().split("-");
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("ddMMyyyy");
				LocalDate now = LocalDate.now();
				String tax = "0104" + fy[0];
				LocalDate returnPeriod = LocalDate.parse(tax, formatter);
				if (returnPeriod.compareTo(now) < 0) {
					groupComplianceResponseDto.setFilingStatusCount(
							String.valueOf(filingStatusCount));
				} else {
					groupComplianceResponseDto
							.setFilingStatusCount("Not Initiated");
				}
				groupComplianceResponseDto
						.setTotalComplianceCount(String.valueOf(totalCount));

				groupComplianceResponseList.add(groupComplianceResponseDto);
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(groupComplianceResponseList));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Response data for given criteria for processed data records is{}");
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
			String msg = "Unexpected error while retrieving Gstr9 compliance records";
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
