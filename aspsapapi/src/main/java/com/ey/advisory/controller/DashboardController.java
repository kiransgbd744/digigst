package com.ey.advisory.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.daos.client.DashboardDao;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.services.ledger.GetSummaryLedgerBalanceDao;
import com.ey.advisory.app.services.ledger.GetSummaryLedgerBalanceDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.DashboardAllSecDto;
import com.ey.advisory.core.dto.DashboardGstrReturnStatusDto;
import com.ey.advisory.core.dto.DashboardGstrSupplyDetailsDto;
import com.ey.advisory.core.dto.DashboardReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Divya1.B
 *
 */
@RestController
public class DashboardController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DashboardController.class);

	@Autowired
	@Qualifier("dashboardDaoImpl")
	private DashboardDao dashboardDao;

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("GetSummaryLedgerBalanceDaoImpl")
	GetSummaryLedgerBalanceDao getSummaryLedgerBalanceDao;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("StatecodeRepository")
	StatecodeRepository stateCodeRepo;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	public enum SavedStatusReturnTypesEnum {
		ANX1, ANX2, ANX1A, ANX2A
	}

	public enum FiledStatusReturnTypesEnum {
		RET1, GSTR6, GSTR7, GSTR8
	}

	@PostMapping(value = "/ui/getDasboardDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDasboardDetails(
			@RequestBody String jsonString) {
		LOGGER.debug(
				"executing the getDasboardDetails method in DashboardController");
		String groupCode = TenantContext.getTenantId();
		LOGGER.info("groupCode {} is set", groupCode);
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		
		DashboardReqDto reqDto = gson.fromJson(requestObject,
				DashboardReqDto.class);

		reqDto = processedRecordsCommonSecParam
				.setDashboardDataSecurityParams(reqDto);

		LOGGER.debug("data security params" + reqDto);

		List<String> gstins = new ArrayList<>();

		if (reqDto.getInwardDataSecAttrs() != null
				&& reqDto.getInwardDataSecAttrs()
						.get(OnboardingConstant.GSTIN) != null) {
			gstins = reqDto.getInwardDataSecAttrs()
					.get(OnboardingConstant.GSTIN);

		}
		if (reqDto.getOutwardDataSecAttrs() != null
				&& reqDto.getOutwardDataSecAttrs()
						.get(OnboardingConstant.GSTIN) != null) {
			gstins.addAll(reqDto.getOutwardDataSecAttrs()
					.get(OnboardingConstant.GSTIN));
		}

		if (CollectionUtils.isEmpty(gstins)) {
		   throw new AppException("No Gstins Available for User Logged In");
		}
		
		gstins = gstins.stream().distinct().collect(Collectors.toList()); 

		LOGGER.debug(
				"GetDasboardDetails for " + " -> params: %s, gstins: %s",
				jsonString, gstins);
		Map<String, String> authTokenStatus = authTokenService
				.getAuthTokenStatusForGstins(gstins);

		DashboardAllSecDto dto = new DashboardAllSecDto();
		dto.setEntityId(reqDto.getEntityId().toString());
		dto.setTaxPeriod(reqDto.getTaxPeriod());
		getAuthDetails(authTokenStatus, gstins, dto);

		//dto = sonar issue
		getReturnStatus(gstins, reqDto.getTaxPeriod(), dto);

		List<DashboardGstrSupplyDetailsDto> gstrSupplyDetailsDto = getSupplyDetails(
				gstins, reqDto, authTokenStatus);

		dto.setSupplyDetailsDto(gstrSupplyDetailsDto);
		LOGGER.debug("dashboard details: " + dto);
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		LOGGER.debug(
				"exit the getDasboardDetails method in DashboardController with response"
						+ resp);
		return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
	}

	private void getAuthDetails(Map<String, String> authTokenStatus,
			List<String> gstins, DashboardAllSecDto dto) {
		int authTot = gstins.size();
		int authIn = 0;
		for (String gstin : gstins) {
			if (authTokenStatus.get(gstin).equals("I")) {
				authIn++;
			}
		}
		dto.setAuthTokenTot(authTot);
		dto.setAuthTokenIna(authIn);
	}

	private DashboardAllSecDto setRetStatus(String taxPeriod,
			DashboardAllSecDto dto, List<Object[]> resultset) {
		List<DashboardGstrSupplyDetailsDto> dtoList = dto.getSupplyDetailsDto();
		for (Object[] obj : resultset) {
			for (DashboardGstrSupplyDetailsDto ledger : dtoList) {
				if (obj[1].equals(ledger.getGstin())) {
					ledger.setFilStat((String) obj[4]);
				}
			}

		}
		dto.setSupplyDetailsDto(dtoList);
		return dto;
	}

	private List<GetSummaryLedgerBalanceDto> getLedgersStatus(
			List<String> gstins) {
		LOGGER.debug(" inside getLedgersStatus");
		// List<DashboardGstrLedgerStatusDto> dtoList = new ArrayList<>();
		List<GetSummaryLedgerBalanceDto> ledgers = getSummaryLedgerBalanceDao
				.getSummaryLedgerDetails(gstins);
		LOGGER.debug(" DB ledger data " + ledgers.size());
		/*
		 * DashboardGstrLedgerStatusDto dto = new
		 * DashboardGstrLedgerStatusDto(); for (String gstin : gstins) { dto =
		 * new DashboardGstrLedgerStatusDto(); dto.setGstin(gstin); for
		 * (GetSummaryLedgerBalanceDto ledger : ledgers) { if
		 * (gstin.equals(ledger.getGstin())) {
		 * dto.setCashLed(ledger.getCashcgst_tot_bal()
		 * .add(ledger.getCashigst_tot_bal()) .add(ledger.getCashsgst_tot_bal())
		 * .add(ledger.getCashcess_tot_bal()));
		 * dto.setCrdLed(ledger.getItccgst_totbal()
		 * .add(ledger.getItcigst_totbal()) .add(ledger.getItcsgst_totbal())
		 * .add(ledger.getItccess_totbal()));
		 * dto.setLibLed(ledger.getLibcgst_totbal()
		 * .add(ledger.getLibigst_totbal()) .add(ledger.getLibsgst_totbal())
		 * .add(ledger.getLibcess_totbal())); } } dtoList.add(dto); }
		 */
		// LOGGER.debug(" exit getLedgersStatus with ledgers " +
		// dtoList.size());
		return ledgers;
	}

	private List<DashboardGstrSupplyDetailsDto> getSupplyDetails(
			List<String> gstins, DashboardReqDto reqDto,
			Map<String, String> authTokenStatus) {
		LOGGER.debug(" inside getSupplyDetails");
		List<DashboardGstrSupplyDetailsDto> dtoList = new ArrayList<>();
		List<Object[]> resultset = dashboardDao
				.getSupplyDetails(reqDto.getEntityId(), reqDto);
		LOGGER.debug(" DB data for getSupplyDetails" + resultset.size());

		DashboardGstrSupplyDetailsDto dto = null;
		Map<String, String> stateNames = entityService.getStateNames(gstins);
		List<GetSummaryLedgerBalanceDto> ledgers = getLedgersStatus(gstins);
		for (String gstin : gstins) {
			dto = new DashboardGstrSupplyDetailsDto();

			GSTNDetailEntity gstinInfo = gstinInfoRepository
					.findRegDates(gstin);
			String registrationType = gstinInfo != null
					? gstinInfo.getRegistrationType() : "";
			/*
			 * String stateName = stateCodeRepo
			 * .findStateNameByCode(gstin.substring(0, 2));
			 */
			dto.setGstin(gstin);
			dto.setType(registrationType);
			dto.setStateName(stateNames.get(gstin));
			dto.setAuthStatus(authTokenStatus.get(gstin));
			if (resultset != null && !resultset.isEmpty()) {
				for (Object[] obj : resultset) {
					if (obj[0] != null && obj[0].equals(gstin)) {
						dto.setOutSup(obj[2] != null ? (BigDecimal) obj[2]
								: BigDecimal.ZERO);
						dto.setOutTax(obj[3] != null ? (BigDecimal) obj[3]
								: BigDecimal.ZERO);
						dto.setInwSup(obj[4] != null ? (BigDecimal) obj[4]
								: BigDecimal.ZERO);
						dto.setTaxAmt(obj[5] != null ? (BigDecimal) obj[5]
								: BigDecimal.ZERO);
						dto.setAvbCred(obj[6] != null ? (BigDecimal) obj[6]
								: BigDecimal.ZERO);
					}
				}
			}
			if (ledgers != null && !ledgers.isEmpty()) {
				for (GetSummaryLedgerBalanceDto ledger : ledgers) {
					if (gstin.equals(ledger.getGstin())) {
						dto.setCashLed(ledger.getCashcgst_tot_bal()
								.add(ledger.getCashigst_tot_bal())
								.add(ledger.getCashsgst_tot_bal())
								.add(ledger.getCashcess_tot_bal()));
						dto.setCrdLed(ledger.getItccgst_totbal()
								.add(ledger.getItcigst_totbal())
								.add(ledger.getItcsgst_totbal())
								.add(ledger.getItccess_totbal()));
						dto.setLibLed(ledger.getLibcgst_totbal()
								.add(ledger.getLibigst_totbal())
								.add(ledger.getLibsgst_totbal())
								.add(ledger.getLibcess_totbal()));
					}
				}
			}
			dtoList.add(dto);
		}
		LOGGER.debug(" exit getSupplyDetails with  " + dtoList.size());
		return dtoList;
	}

	private DashboardAllSecDto getReturnStatus(List<String> gstins,
			String taxPeriod, DashboardAllSecDto dto) {
		LOGGER.debug(" inside getReturnStatus");
		List<Object[]> resultset = dashboardDao
				.getGstrReturnStatus(gstins, taxPeriod,
						Stream.of(FiledStatusReturnTypesEnum.values())
								.map(Enum::name).collect(Collectors.toList()),
						"FILED");

		resultset.addAll(dashboardDao.getGstrReturnStatus(gstins,
				taxPeriod, Stream.of(SavedStatusReturnTypesEnum.values())
						.map(Enum::name).collect(Collectors.toList()),
				"SAVED"));

		LOGGER.debug(" DB data for getReturnStatus" + resultset.size());
		DashboardGstrReturnStatusDto gstrReturnStatusDto = null;
		if (resultset != null && !resultset.isEmpty()) {
			gstrReturnStatusDto = setReturnStatusDetails(resultset);
		}

		dto.setGstrReturnStatusDto(gstrReturnStatusDto);

		//dto = sonar issue
		setRetStatus(taxPeriod, dto, resultset);
		LOGGER.debug(" exit getReturnStatus with  " + dto);
		return dto;
	}

	private DashboardGstrReturnStatusDto setReturnStatusDetails(
			List<Object[]> resultset) {
		LOGGER.debug(" inside getReturnStatus");
		DashboardGstrReturnStatusDto dto = new DashboardGstrReturnStatusDto();
		for (Object[] obj : resultset) {
			if (obj[3].equals("ANX1")) {
				dto.setAnx1T((dto.getAnx1T() != 0 ? dto.getAnx1T() + 1 : 1));
				if (obj[4].equals("SAVED")) {
					dto.setAnx1S(
							(dto.getAnx1S() != 0 ? dto.getAnx1S() + 1 : 1));
				}
			} else if (obj[3].equals("ANX2")) {
				dto.setAnx2T((dto.getAnx2T() != 0 ? dto.getAnx2T() + 1 : 1));
				if (obj[4].equals("SAVED")) {
					dto.setAnx2S(
							(dto.getAnx2S() != 0 ? dto.getAnx2S() + 1 : 1));
				}
			} else if (obj[3].equals("RET1")) {
				dto.setRet1T((dto.getRet1T() != 0 ? dto.getRet1T() + 1 : 1));
				if (obj[4].equals("FILED")) {
					dto.setRet1F(
							(dto.getRet1F() != 0 ? dto.getRet1F() + 1 : 1));
				}
			} else if (obj[3].equals("GSTR6")) {
				dto.setGstr6T((dto.getGstr6T() != 0 ? dto.getGstr6T() + 1 : 1));
				if (obj[4].equals("FILED")) {
					dto.setGstr6F(
							(dto.getGstr6F() != 0 ? dto.getGstr6F() + 1 : 1));
				}
			} else if (obj[3].equals("GSTR7")) {
				dto.setGstr7T((dto.getGstr7T() != 0 ? dto.getGstr7T() + 1 : 1));
				if (obj[4].equals("FILED")) {
					dto.setGstr7F(
							(dto.getGstr7F() != 0 ? dto.getGstr7F() + 1 : 1));
				}

			} else if (obj[3].equals("GSTR8")) {
				dto.setGstr8T((dto.getGstr8T() != 0 ? dto.getGstr8T() + 1 : 1));
				if (obj[4].equals("FILED")) {
					dto.setGstr8F(
							(dto.getGstr8F() != 0 ? dto.getGstr8F() + 1 : 1));
				}
			} else if (obj[3].equals("ANX1A")) {
				dto.setAnx1AT((dto.getAnx1AT() != 0 ? dto.getAnx1AT() + 1 : 1));
				if (obj[4].equals("FILED")) {
					dto.setAnx1AA(
							(dto.getAnx1AA() != 0 ? dto.getAnx1AA() + 1 : 1));
				}
			} else if (obj[3].equals("RET1A")) {
				dto.setRet1AT((dto.getRet1AT() != 0 ? dto.getRet1AT() + 1 : 1));
				if (obj[4].equals("FILED")) {
					dto.setRet1AA(
							(dto.getRet1AA() != 0 ? dto.getRet1AA() + 1 : 1));
				}
			}

		}
		LOGGER.debug(" exit setReturnStatusDetails with  " + dto);
		return dto;

	}

}
