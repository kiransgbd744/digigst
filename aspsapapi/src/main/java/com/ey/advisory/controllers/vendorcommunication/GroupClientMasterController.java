package com.ey.advisory.controllers.vendorcommunication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.javatuples.Pair;
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
import com.ey.advisory.app.data.returns.compliance.service.ComplienceSummeryRespDto;
import com.ey.advisory.app.data.returns.compliance.service.GroupComplianceHistoryDataRecordsReqDto;
import com.ey.advisory.app.data.returns.compliance.service.GroupComplianceResponseDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.GenUtil;
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
public class GroupClientMasterController {

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

	public  static final String Not_Initiated = "Not Initiated";

	@PostMapping("/ui/groupComplienceHistory")
	public ResponseEntity<String> getGstr2AProcessedRecords(
			@RequestBody String jsonString) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GroupClientMasterController getDataSecurityForUser begin");
		}
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"gstr1,gstr7,gstr6,gstr3b groupComplienceHistory request{}",
					json);
		}

		try {
			GroupComplianceHistoryDataRecordsReqDto groupComplianceHistoryDataRecordsReqDto = gson
					.fromJson(json,
							GroupComplianceHistoryDataRecordsReqDto.class);

			List<Gstr2aProcessedDataRecordsReqDto> groupComplianceProcessed = new ArrayList<>();
			List<Long> entityIdList = null;
			if (!groupComplianceHistoryDataRecordsReqDto.getDataSecAttrs()
					.get("entityIds").isEmpty()) {
				entityIdList = groupComplianceHistoryDataRecordsReqDto
						.getDataSecAttrs().get("entityIds");
			} else {
				User user = SecurityContext.getUser();
				String userName = user.getUserPrincipalName();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("GroupClientMasterController User Name: {}",
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
				int aprilStatusCount, mayStatusCount, juneStatusCount,
						julyStatusCount, augStatusCount, sepStatusCount,
						octStatusCount, novStatusCount, decStatusCount,
						janStatusCount, febStatusCount, marchStatusCount,
						totalCount;
				totalCount = aprilStatusCount = mayStatusCount = juneStatusCount = julyStatusCount = augStatusCount = sepStatusCount = octStatusCount = novStatusCount = decStatusCount = janStatusCount = febStatusCount = marchStatusCount = 0;
				if (!gstr2AProcessedDataRecordsReqDto.getDataSecAttrs()
						.get("GSTIN").isEmpty()) {
					List<ComplienceSummeryRespDto> respDtos = complienceSummery
							.findcomplienceSummeryRecords(
									gstr2AProcessedDataRecordsReqDto);
					for (int i = 0; i < respDtos.size(); i++) {
						if (respDtos.get(i) != null) {
							if (respDtos.get(i).getAprilStatus() != null)
								if (respDtos.get(i).getAprilStatus()
										.equalsIgnoreCase("Filed"))
									aprilStatusCount++;
							if (respDtos.get(i).getMayStatus() != null)
								if (respDtos.get(i).getMayStatus()
										.equalsIgnoreCase("Filed"))
									mayStatusCount++;
							if (respDtos.get(i).getJuneStatus() != null)
								if (respDtos.get(i).getJuneStatus()
										.equalsIgnoreCase("Filed"))
									juneStatusCount++;
							if (respDtos.get(i).getJulyStatus() != null)
								if (respDtos.get(i).getJulyStatus()
										.equalsIgnoreCase("Filed"))
									julyStatusCount++;
							if (respDtos.get(i).getAugStatus() != null)
								if (respDtos.get(i).getAugStatus()
										.equalsIgnoreCase("Filed"))
									augStatusCount++;
							if (respDtos.get(i).getSepStatus() != null)
								if (respDtos.get(i).getSepStatus()
										.equalsIgnoreCase("Filed"))
									sepStatusCount++;
							if (respDtos.get(i).getOctStatus() != null)
								if (respDtos.get(i).getOctStatus()
										.equalsIgnoreCase("Filed"))
									octStatusCount++;
							if (respDtos.get(i).getNovStatus() != null)
								if (respDtos.get(i).getNovStatus()
										.equalsIgnoreCase("Filed"))
									novStatusCount++;
							if (respDtos.get(i).getDecStatus() != null)
								if (respDtos.get(i).getDecStatus()
										.equalsIgnoreCase("Filed"))
									decStatusCount++;
							if (respDtos.get(i).getJanStatus() != null)
								if (respDtos.get(i).getJanStatus()
										.equalsIgnoreCase("Filed"))
									janStatusCount++;
							if (respDtos.get(i).getFebStatus() != null)
								if (respDtos.get(i).getFebStatus()
										.equalsIgnoreCase("Filed"))
									febStatusCount++;
							if (respDtos.get(i).getMarchStatus() != null)
								if (respDtos.get(i).getMarchStatus()
										.equalsIgnoreCase("Filed"))
									marchStatusCount++;
						}
						totalCount = respDtos.size();
					}
				}
				String fy[] = gstr2AProcessedDataRecordsReqDto
						.getFinancialYear().split("-");

				groupComplianceResponseDto.setEntityId(
						gstr2AProcessedDataRecordsReqDto.getEntity());
				String entityName = entityInfoRepository.findEntityNameByEntityId(
						Long.valueOf(gstr2AProcessedDataRecordsReqDto.getEntity()));
				groupComplianceResponseDto.setEntityName(entityName);
				
				String taxPeriod = "04" + fy[0];
				if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
					groupComplianceResponseDto.setAprilFiledCount(
							String.valueOf(aprilStatusCount));
				} else {
					groupComplianceResponseDto
							.setAprilFiledCount(Not_Initiated);
				}
				taxPeriod = "05" + fy[0];
				if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
					groupComplianceResponseDto
							.setMayFiledCount(String.valueOf(mayStatusCount));
				} else {
					groupComplianceResponseDto.setMayFiledCount(Not_Initiated);
				}
				taxPeriod = "06" + fy[0];
				if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
					groupComplianceResponseDto
							.setJuneFiledCount(String.valueOf(juneStatusCount));
				} else {
					groupComplianceResponseDto.setJuneFiledCount(Not_Initiated);
				}
				taxPeriod = "07" + fy[0];
				if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
					groupComplianceResponseDto
							.setJulyFiledCount(String.valueOf(julyStatusCount));
				} else {
					groupComplianceResponseDto.setJulyFiledCount(Not_Initiated);
				}
				taxPeriod = "08" + fy[0];
				if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
					groupComplianceResponseDto
							.setAugFiledCount(String.valueOf(augStatusCount));
				} else {
					groupComplianceResponseDto.setAugFiledCount(Not_Initiated);
				}
				taxPeriod = "09" + fy[0];
				if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
					groupComplianceResponseDto
							.setSepFiledCount(String.valueOf(sepStatusCount));
				} else {
					groupComplianceResponseDto.setSepFiledCount(Not_Initiated);
				}
				taxPeriod = "10" + fy[0];
				if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
					groupComplianceResponseDto
							.setOctFiledCount(String.valueOf(octStatusCount));
				} else {
					groupComplianceResponseDto.setOctFiledCount(Not_Initiated);
				}
				taxPeriod = "11" + fy[0];
				if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
					groupComplianceResponseDto
							.setNovFiledCount(String.valueOf(novStatusCount));
				} else {
					groupComplianceResponseDto.setNovFiledCount(Not_Initiated);
				}
				taxPeriod = "12" + fy[0];
				if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
					groupComplianceResponseDto
							.setDecFiledCount(String.valueOf(decStatusCount));
				} else {
					groupComplianceResponseDto.setDecFiledCount(Not_Initiated);
				}
				taxPeriod = "01"+(Integer.parseInt(fy[0])+ 1);
				if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
					groupComplianceResponseDto
							.setJanFiledCount(String.valueOf(janStatusCount));
				} else {
					groupComplianceResponseDto.setJanFiledCount(Not_Initiated);
				}
				taxPeriod = "02" + (Integer.parseInt(fy[0])+ 1);
				if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
					groupComplianceResponseDto
							.setFebFiledCount(String.valueOf(febStatusCount));
				} else {
					groupComplianceResponseDto.setFebFiledCount(Not_Initiated);
				}
				taxPeriod = "03" + (Integer.parseInt(fy[0])+ 1);
				if (GenUtil.isValidTaxPeriodForCurrentFy(taxPeriod)) {
					groupComplianceResponseDto.setMarchFiledCount(
							String.valueOf(marchStatusCount));
				} else {
					groupComplianceResponseDto
							.setMarchFiledCount(Not_Initiated);
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