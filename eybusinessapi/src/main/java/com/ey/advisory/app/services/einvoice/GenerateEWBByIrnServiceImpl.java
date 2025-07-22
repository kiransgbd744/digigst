package com.ey.advisory.app.services.einvoice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.javatuples.Quartet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.einv.api.GenerateEWBByIrn;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.common.BusinessStatisticsLogHelper;
import com.ey.advisory.einv.common.EyEInvCommonUtil;
import com.ey.advisory.einv.dto.GenEWBByIrnDispNICReqDto;
import com.ey.advisory.einv.dto.GenEWBByIrnDummyResponseDto;
import com.ey.advisory.einv.dto.GenEWBByIrnExpShpNICReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnERPReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnNICReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnRequest;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GenerateEWBByIrnServiceImpl")
public class GenerateEWBByIrnServiceImpl implements GenerateEWBByIrnService {

	private static DateTimeFormatter FORMATTER1 = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");

	private static DateTimeFormatter FORMATTER2 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	@Autowired
	@Qualifier("GenerateEWBByIrnImpl")
	private GenerateEWBByIrn generateEWBByIrn;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	BusinessStatisticsLogHelper businessStatisticsLogHelper;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Override
	public GenerateEWBByIrnResponseDto generateEwayIrnRequest(
			GenerateEWBByIrnRequest req) {
		GenerateEWBByIrnERPReqDto erpReqDto = req.getGenerateEWBReq();
		PerfUtil.logEventToFile("GENERATE_EWBBY_IRN", "GENERATE_EWB_BY_IRN_SERVICE",
				"GenerateEWBByIrnServiceImpl", "generateEwayIrnRequest",
				"CONVERT_METHOD_START", true);
		GenerateEWBByIrnNICReqDto nicReqDto = convertERPtoNIC(erpReqDto);
		reqLogHelper.logAppMessage(null, nicReqDto.getIrn(), null,
				"Processing Generate Ewb by Irn Request");
		PerfUtil.logEventToFile("GENERATE_EWBBY_IRN", "GENERATE_EWB_BY_IRN_SERVICE",
				"GenerateEWBByIrnServiceImpl", "generateEwayIrnRequest",
				"CONVERT_METHOD_END", true);
		return processResponse(generateEWBByIrn.generateEWBByIrn(nicReqDto),
				nicReqDto);
	}

	private GenerateEWBByIrnNICReqDto convertERPtoNIC(
			GenerateEWBByIrnERPReqDto erpReq) {
		GenerateEWBByIrnNICReqDto nicReqDto = new GenerateEWBByIrnNICReqDto();
		boolean isPartBEligible = isEligibleForPartB(erpReq);

		if (isPartBEligible) {
			if (erpReq.getTrnDocDt() != null) {
				nicReqDto.setTrnDocDt(convertDateFormat(erpReq.getTrnDocDt()));
			}
			nicReqDto.setTrnDocNo(erpReq.getTrnDocNo());
			nicReqDto.setTransMode(
					EyEInvCommonUtil.getTransMode(erpReq.getTransMode()));
			nicReqDto.setVehNo(erpReq.getVehNo());
			nicReqDto.setVehType(erpReq.getVehType());
		}
		nicReqDto.setGstin(erpReq.getGstin());
		nicReqDto.setDistance(erpReq.getDistance());
		nicReqDto.setIrn(erpReq.getIrn());
		nicReqDto.setTransName(erpReq.getTransName());
		nicReqDto.setTransId(erpReq.getTransId());
		nicReqDto.setSuppPincd(erpReq.getSuppPincd());
		nicReqDto.setDispatcherPincd(erpReq.getDispatcherPincd());
		nicReqDto.setShipToPincd(erpReq.getShipToPincd());
		nicReqDto.setCustPincd(erpReq.getCustPincd());
		boolean isEligibleforDocCatFlag = isAddressSuppressRequired(
				erpReq.getDocCategory(), erpReq.getSupplyType());
		Quartet<Boolean, Boolean, Boolean, Boolean> eligibleAddSuppression = EyEInvCommonUtil
				.eligibleAddresstobeNIC(erpReq.getDocCategory(),
						isEligibleforDocCatFlag);

		boolean isDispatcherDetailsReq = eligibleAddSuppression.getValue2();
		boolean isShiptoDetailsReq = eligibleAddSuppression.getValue3();

		if (erpReq.getDispDtls() != null && isDispatcherDetailsReq) {
			GenEWBByIrnDispNICReqDto nicReqdispDtls = new GenEWBByIrnDispNICReqDto();
			nicReqdispDtls.setAddr1(erpReq.getDispDtls().getAddr1());
			if (!Strings.isNullOrEmpty(erpReq.getDispDtls().getAddr2())) {
				nicReqdispDtls.setAddr2(erpReq.getDispDtls().getAddr2());
			}
			nicReqdispDtls.setLoc(erpReq.getDispDtls().getLoc());
			nicReqdispDtls.setPin(erpReq.getDispDtls().getPin());
			nicReqdispDtls.setStcd(EyEInvCommonUtil
					.getstateCode(erpReq.getDispDtls().getStcd()));
			nicReqdispDtls.setNm(erpReq.getDispDtls().getNm());
			nicReqDto.setDispDtls(nicReqdispDtls);
		}
		if (erpReq.getExpShipDtls() != null && isShiptoDetailsReq) {
			GenEWBByIrnExpShpNICReqDto nicReqExpShpDtls = new GenEWBByIrnExpShpNICReqDto();
			nicReqExpShpDtls.setAddr1(erpReq.getExpShipDtls().getAddr1());
			if (!Strings.isNullOrEmpty(erpReq.getExpShipDtls().getAddr2())) {
				nicReqExpShpDtls.setAddr2(erpReq.getExpShipDtls().getAddr2());
			}
			nicReqExpShpDtls.setLoc(erpReq.getExpShipDtls().getLoc());
			nicReqExpShpDtls.setPin(erpReq.getExpShipDtls().getPin());
			nicReqExpShpDtls.setStcd(EyEInvCommonUtil
					.getstateCode(erpReq.getExpShipDtls().getStcd()));
			nicReqDto.setExpShipDtls(nicReqExpShpDtls);
		}
		return nicReqDto;

	}

	private GenerateEWBByIrnResponseDto processResponse(APIResponse response,
			GenerateEWBByIrnNICReqDto nicReqDto) {
		GenerateEWBByIrnResponseDto genEwbIrnResp = null;
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			GenEWBByIrnDummyResponseDto genEwbresp = gson.fromJson(jsonResp,
					GenEWBByIrnDummyResponseDto.class);
			genEwbIrnResp = new GenerateEWBByIrnResponseDto();
			genEwbIrnResp.setEwbNo(String.valueOf(genEwbresp.getEwbNo()));
			genEwbIrnResp.setEwbDt(genEwbresp.getEwbDt());
			genEwbIrnResp.setEwbValidTill(genEwbresp.getEwbValidTill());

			if (genEwbresp.getInfoDtls() != null) {

				extractInfoDtlsandSetNICDis(genEwbresp, genEwbIrnResp);
			} else {
				genEwbIrnResp.setNicDistance(
						String.valueOf(nicReqDto.getDistance()));
			}

			reqLogHelper.logAppMessage(null, null, genEwbIrnResp.getEwbNo(),
					"Success Response from NIC for Generate Ewb By Irn for the Irn");

			businessStatisticsLogHelper.logBusinessApiResponse(null, null, null,
					null, null, null, null, null, genEwbIrnResp.getEwbNo(),
					genEwbIrnResp.getEwbDt(), genEwbIrnResp.getEwbValidTill(),
					null, Integer.valueOf(genEwbIrnResp.getNicDistance()),
					APIIdentifiers.GENERATE_EWBByIRN, false, null, null);
		} else {
			List<APIError> apiErrorList = response.getErrors();
			genEwbIrnResp = createErrorResponse(apiErrorList);
		}
		return genEwbIrnResp;
	}

	private GenerateEWBByIrnResponseDto createErrorResponse(
			List<APIError> apiErrorList) {
		String errorCode = "";
		String errorDesc = "";
		for (int i = 0; i < apiErrorList.size(); i++) {
			if (!Strings.isNullOrEmpty(apiErrorList.get(i).getErrorCode()))
				errorCode = errorCode + (i + 1) + ") "
						+ apiErrorList.get(i).getErrorCode() + " ";
			if (!Strings.isNullOrEmpty(apiErrorList.get(i).getErrorCode()))
				errorDesc = errorDesc + (i + 1) + ") "
						+ apiErrorList.get(i).getErrorDesc() + " ";
		}
		GenerateEWBByIrnResponseDto resp = new GenerateEWBByIrnResponseDto();
		resp.setErrorCode(errorCode);
		resp.setErrorMessage(errorDesc);
		return resp;
	}

	private boolean isEligibleForPartB(
			GenerateEWBByIrnERPReqDto genEWBByIRNERPReq) {

		PerfUtil.logEventToFile("GENERATE_EWBBY_IRN", "GENERATE_EWB_BY_IRN_SERVICE",
				"GenerateEWBByIrnServiceImpl", "isEligibleForPartB",
				"isEligibleForPartB_START", true);
		
		Map<String, Config> configMap = configManager.getConfigs("EINV",
				"ewb.partb", TenantContext.getTenantId());

		boolean partBSuppresReq = configMap.get("ewb.partb.suppresreq") == null
				? Boolean.FALSE
				: Boolean.valueOf(
						configMap.get("ewb.partb.suppresreq").getValue());

		PerfUtil.logEventToFile("GENERATE_EWBBY_IRN", "GENERATE_EWB_BY_IRN_SERVICE",
				"GenerateEWBByIrnServiceImpl", "isEligibleForPartB",
				"isEligibleForPartB_END", true);

		if (!partBSuppresReq) {
			return true;
		}

		if (genEWBByIRNERPReq.getTransMode() == null
				|| Strings.isNullOrEmpty(genEWBByIRNERPReq.getTransMode())) {
			return false;
		}

		if ("Road".equalsIgnoreCase(genEWBByIRNERPReq.getTransMode())) {
			return !Strings.isNullOrEmpty(genEWBByIRNERPReq.getVehNo())
					&& !Strings.isNullOrEmpty(genEWBByIRNERPReq.getVehType());
		} else if ("InTransit"
				.equalsIgnoreCase(genEWBByIRNERPReq.getTransMode())) {
			return true;
		} else {
			return !Strings.isNullOrEmpty(genEWBByIRNERPReq.getTrnDocNo())
					&& genEWBByIRNERPReq.getTrnDocDt() != null;
		}
	}

	public String convertDateFormat(String dateFormat1) {
		try {
			LocalDate localDateFormat1 = LocalDate.parse(dateFormat1,
					FORMATTER1);
			return FORMATTER2.format(localDateFormat1);
		} catch (Exception ex) {
			String errMsg = String
					.format("Invalid TransdocDate format - %s while GenEWBByIRN,"
							+ " Expected format is yyyy-MM-dd", dateFormat1);
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
	}

	private void extractInfoDtlsandSetNICDis(
			GenEWBByIrnDummyResponseDto genEwbresp,
			GenerateEWBByIrnResponseDto genEwbIrnResp) {

		String infoErrorCode = "";
		String infoErrorDesc = "";

		String infoJson = genEwbresp.getInfoDtls();

		String infCode = (new JsonParser()).parse(infoJson).getAsJsonArray()
				.get(0).getAsJsonObject().get("InfCd").getAsString();

		if ("EWBALERT".equalsIgnoreCase(infCode)
				|| "EWBPPD".equalsIgnoreCase(infCode)
				|| "EWBVEH".equalsIgnoreCase(infCode)
				|| "ADDNLNFO".equalsIgnoreCase(infCode)) {
			infoErrorDesc = (new JsonParser()).parse(infoJson).getAsJsonArray()
					.get(0).getAsJsonObject().get("Desc").getAsString();
		}

		genEwbIrnResp.setInfoErrorCode(infoErrorCode);
		genEwbIrnResp.setInfoErrorMessage(infoErrorDesc);
		extractDistanceandSetNICDis(infoErrorDesc, genEwbIrnResp);

	}

	private void extractDistanceandSetNICDis(String infoMessage,
			GenerateEWBByIrnResponseDto genEwbIrnResp) {
		String nicDistance = "";
		int distanceIndex = infoMessage.lastIndexOf("distance:");
		if (distanceIndex != -1) {
			nicDistance = infoMessage.substring(distanceIndex);
		}
		if (!Strings.isNullOrEmpty(nicDistance)) {
			genEwbIrnResp.setNicDistance(nicDistance.replaceAll("[^0-9]", ""));
		}
	}

	private boolean isAddressSuppressRequired(String docCategory,
			String supplyType) {

		if ((!Strings.isNullOrEmpty(supplyType)
				&& ("EXP".equalsIgnoreCase(supplyType)
						|| Stream.of("EXPT", "EXPWT")
								.anyMatch(supplyType::equalsIgnoreCase)))
				|| Strings.isNullOrEmpty(docCategory)) {
			return false;
		}

		PerfUtil.logEventToFile("GENERATE_EWBBY_IRN", "GENERATE_EWB_BY_IRN_SERVICE",
				"GenerateEWBByIrnServiceImpl", "isEligibleForPartB",
				"isAddressSuppressRequired_START", true);
		
		Map<String, Config> configMap = configManager.getConfigs("EINV",
				"einv.address", TenantContext.getTenantId());

		PerfUtil.logEventToFile("GENERATE_EWBBY_IRN", "GENERATE_EWB_BY_IRN_SERVICE",
				"GenerateEWBByIrnServiceImpl", "isEligibleForPartB",
				"isAddressSuppressRequired_END", true);

		return configMap.get("einv.address.suppresreq") == null ? Boolean.FALSE
				: Boolean.valueOf(
						configMap.get("einv.address.suppresreq").getValue());

	}
}
