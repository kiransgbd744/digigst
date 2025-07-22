package com.ey.advisory.app.gstr3b;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGetLiabilityAutoCalcRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Rajesh N K
 *
 */
@Service("Gstr3BGetLiabilityAutoCalcDetailServiceImpl")
@Slf4j
public class Gstr3BGetLiabilityAutoCalcDetailServiceImpl
		implements Gstr3BGetLiabilityAutoCalcDetailService {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	private Gstr3bUtil gstr3bUtil;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("Gstr3BGetLiabilityAutoCalcRepository")
	private Gstr3BGetLiabilityAutoCalcRepository gstr3bAutoCalcRepo;

	@Autowired
	@Qualifier("GstnUserRequestRepository")
	private GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	Gstr3BGetLiabilityAutoCalcHelper gstr3BAutoCalcHelper;

	/**
	 * this method is invoking GSTN API.
	 */
	@SuppressWarnings("unused")
	@Override
	public String getGstnAutoCalc(String gstin, String taxPeriod) {

		APIResponse gstnApiResponse = null;
		String gstnResponse = null;
		String respBody = null;

		try {

			String authStatus = authTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (authStatus.equals("A")) {

				// get gstn call

				gstnApiResponse = getGSTR3BAutoCalc(taxPeriod, gstin);

				if (gstnApiResponse.isSuccess()) {
					gstnResponse = gstnApiResponse.getResponse();

					if (LOGGER.isDebugEnabled()) {
						LOGGER.info(
								"Gstr3BGetLiabilityAutoCalcDetailServiceImpl"
										+ ".getGstnAutoCalc parsing the "
										+ "json response");
					}

					List<Gstr3BGstinsDto> resultList = getGstrList(
							gstnResponse);

					// Saving Response to DB Table.
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Gstr3BGetLiabilityAutoCalcDetailServiceImpl"
										+ ".getGstnAutoCalc persisting GSTR3B auto calc response"
										+ " to GSTR3B_AUTO_CALC table");
					}
					gstr3BAutoCalcHelper.saveOrUpdateAutoCalc(gstin, taxPeriod,
							resultList, "AutoCal");

					// Saving Response to DB Table.
					saveAutoCalcDetails(gstin, taxPeriod, gstnResponse);

					respBody = "GSTR3B Auto Calc Submitted Successfully";
				} else {
					APIError error = gstnApiResponse.getError();
					String msg = error.getErrorDesc();
					LOGGER.error(msg);
					respBody = msg;
					return respBody;
				}
			}

			else {
				respBody = "Auth Token is Inactive, Please Activate";
			}
		} catch (Exception e) {
			String msg = String.format(
					"Internal error occured while get " + "gstr3BAutoCalc {} ",
					e);
			LOGGER.error(msg);
			return msg;
		}

		return respBody;

	}

	private void saveAutoCalcDetails(String gstin, String taxPeriod,
			String gstnResponse) {

		GstnUserRequestEntity gstnUserRequestEntity = gstnUserRequestRepo
				.findByGstinAndTaxPeriodAndReturnType(gstin, taxPeriod,
						Gstr3BAutoCalConstants.GSTR3B);

		Clob reqClob;
		try {
			reqClob = new javax.sql.rowset.serial.SerialClob(
					gstnResponse.toCharArray());

			if (gstnUserRequestEntity == null) {

				gstnUserRequestEntity = new GstnUserRequestEntity();

				String userName = SecurityContext.getUser()
						.getUserPrincipalName() == null ? "SYSTEM"
								: SecurityContext.getUser()
										.getUserPrincipalName();

				gstnUserRequestEntity.setGstin(gstin);
				gstnUserRequestEntity.setTaxPeriod(taxPeriod);
				gstnUserRequestEntity
						.setReturnType(Gstr3BAutoCalConstants.GSTR3B);
				gstnUserRequestEntity.setRequestType("GET");
				gstnUserRequestEntity.setRequestStatus(1);
				gstnUserRequestEntity.setCreatedBy(userName);
				gstnUserRequestEntity.setCreatedOn(LocalDateTime.now());
				gstnUserRequestEntity.setDelete(false);
				gstnUserRequestEntity.setAutoCalcResponse(reqClob);

				gstnUserRequestRepo.save(gstnUserRequestEntity);

			} else {
				gstnUserRequestRepo.updateGstinAutoCalGstnResponse(reqClob, 1,
						gstin, taxPeriod, Gstr3BAutoCalConstants.GSTR3B,
						LocalDateTime.now());

			}
		} catch (Exception e) {
			String msg = "Exception occured while persisting GSTR3B auto calc response";
			LOGGER.error(msg, e);

			throw new AppException(msg, e);
		}

	}

	private APIResponse getGSTR3BAutoCalc(String taxPeriod, String gstin) {

		try {
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", taxPeriod);
			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN, APIIdentifiers.GSTR3B_GET_AUTO_CALC,
					param1, param2);
			return apiExecutor.execute(params, null);
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while invoking '%s',"
							+ " GSTIN - '%s' and Taxperiod - '%s'",
					APIIdentifiers.GSTR3B_GET_AUTO_CALC, gstin, taxPeriod);
			LOGGER.error(msg, ex);
			throw new AppException(ex.getMessage());
		}
	}

	private List<Gstr3BGstinsDto> getGstrList(String gstnResponse) {

		Map<String, JsonObject> getGstnResponseMap = parseJsonResponseForAutoCalc(
				gstnResponse);

		// Map to set sectionName and subSectionName
		Map<String, Gstr3BGstinDashboardDto> subMap = gstr3bUtil
				.getGstr3BGstinDashboardMap();

		List<Gstr3BGstinsDto> responseList = new ArrayList<>();

		for (Map.Entry<String, JsonObject> entry : getGstnResponseMap
				.entrySet()) {

			String key = entry.getKey();

			JsonObject value = entry.getValue();

			if (value != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(" Before invoking "
							+ "Gstr3BGetLiabilityAutoCalcDetailServiceImpl"
							+ ".setGstinvalues() " + "method for value : {} ",
							value);
				}

				if ("3.2(a)".contains(key) || "3.2(b)".contains(key)
						|| "3.2(c)".contains(key)) {

					JsonArray jsonArray = entry.getValue()
							.getAsJsonArray("subtotal");
					for (int i = 0; i < jsonArray.size(); i++) {

						LOGGER.error(key);
						value = jsonArray.get(i).getAsJsonObject();

						if (value.has("pos") && Strings.isNullOrEmpty(
								value.get("pos").getAsString())) {
							continue;
						}
						Gstr3BGstinsDto dto = new Gstr3BGstinsDto(key,
								subMap.get(key).getSupplyType());

						setGstinvalues(dto, value);
						responseList.add(dto);
					}

				} else {
					Gstr3BGstinsDto dto = null;
					if (!key.equalsIgnoreCase("4(a)(5)(a)")) {
						LOGGER.error(key);
						dto = new Gstr3BGstinsDto(key,
								subMap.get(key).getSupplyType());
					} else {
						LOGGER.error(key);
						LOGGER.error(value.toString());
						dto = new Gstr3BGstinsDto("4(a)(5)(a)", "APTPPR");
					}
					setGstinvalues(dto, value);
					responseList.add(dto);
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("get3bAutoCalc response dto : {} ",
							responseList);
				}
			}

		}

		return responseList;

	}

	private void setGstinvalues(Gstr3BGstinsDto gstr3bgstnDto,
			JsonObject jsonObject) {

		if (jsonObject.has("txval"))
			gstr3bgstnDto
					.setTaxableVal(jsonObject.get("txval").getAsBigDecimal());
		if (jsonObject.has("csamt"))
			gstr3bgstnDto.setCess(jsonObject.get("csamt").getAsBigDecimal());

		if (jsonObject.has("camt"))
			gstr3bgstnDto.setCgst(jsonObject.get("camt").getAsBigDecimal());

		if (jsonObject.has("samt"))
			gstr3bgstnDto.setSgst(jsonObject.get("samt").getAsBigDecimal());

		if (jsonObject.has("iamt"))
			gstr3bgstnDto.setIgst(jsonObject.get("iamt").getAsBigDecimal());

		if (jsonObject.has("inter"))
			gstr3bgstnDto
					.setInterState(jsonObject.get("inter").getAsBigDecimal());

		if (jsonObject.has("intra"))
			gstr3bgstnDto
					.setIntraState(jsonObject.get("intra").getAsBigDecimal());

		if (jsonObject.has("pos"))
			gstr3bgstnDto.setPos(jsonObject.get("pos").getAsString());

	}

	public Map<String, JsonObject> parseJsonResponseForAutoCalc(
			String gstnResponse) {
		Map<String, JsonObject> map = new LinkedHashMap<>();
		JsonObject osup3A = null;
		JsonObject osup3B = null;
		JsonObject osup3C = null;
		JsonObject osup3E = null;

		JsonObject isup3D = null;

		JsonObject itc4a1 = null;
		JsonObject itc4a3 = null;
		JsonObject itc4a4 = null;
		JsonObject itc4a5 = null;
		JsonObject itc4b2 = null;
		JsonObject itc4d2 = null;

		JsonObject elgItc4a1 = null;
		JsonObject elgItc4a3 = null;
		JsonObject elgItc4a4 = null;
		JsonObject elgItc4a5 = null;
		JsonObject elgItc4b2 = null;
		JsonObject elgItc4d2 = null;

		try {

			JsonObject parseResponse = (JsonObject) JsonParser
					.parseString(gstnResponse).getAsJsonObject();

			JsonObject r3bAutoPop = parseResponse.getAsJsonObject("r3bautopop");

			JsonObject liabItc = r3bAutoPop.has("liabitc")
					? r3bAutoPop.getAsJsonObject("liabitc") : null;

			JsonObject supDetails = liabItc.has("sup_details")
					? liabItc.getAsJsonObject("sup_details") : null;

			JsonObject suppDetails3A = supDetails.has("osup_3_1a")
					? supDetails.getAsJsonObject("osup_3_1a") : null;

			if (suppDetails3A != null) {
				osup3A = suppDetails3A.has("subtotal")
						? suppDetails3A.getAsJsonObject("subtotal") : null;
			}

			JsonObject suppDetails3B = supDetails.has("osup_3_1b")
					? supDetails.getAsJsonObject("osup_3_1b") : null;

			if (suppDetails3B != null) {
				osup3B = suppDetails3B.has("subtotal")
						? suppDetails3B.getAsJsonObject("subtotal") : null;

			}

			JsonObject suppDetails3C = supDetails.has("osup_3_1c")
					? supDetails.getAsJsonObject("osup_3_1c") : null;

			if (suppDetails3C != null) {
				osup3C = suppDetails3C.has("subtotal")
						? suppDetails3C.getAsJsonObject("subtotal") : null;

			}

			JsonObject suppDetails3D = supDetails.has("isup_3_1d")
					? supDetails.getAsJsonObject("isup_3_1d") : null;

			if (suppDetails3D != null) {
				isup3D = suppDetails3D.has("subtotal")
						? suppDetails3D.getAsJsonObject("subtotal") : null;
			}

			JsonObject suppDetails3E = supDetails.has("osup_3_1e")
					? supDetails.getAsJsonObject("osup_3_1e") : null;

			if (suppDetails3E != null) {
				osup3E = suppDetails3E.has("subtotal")
						? suppDetails3E.getAsJsonObject("subtotal") : null;

			}

			JsonObject interSup = liabItc.has("inter_sup")
					? liabItc.getAsJsonObject("inter_sup") : null;
			JsonObject interSupA = interSup.has("osup_unreg_3_2")
					? interSup.getAsJsonObject("osup_unreg_3_2") : null;

			JsonObject interSupB = interSup.has("osup_comp_3_2")
					? interSup.getAsJsonObject("osup_comp_3_2") : null;

			JsonObject interSupC = interSup.has("osup_uin_3_2")
					? interSup.getAsJsonObject("osup_uin_3_2") : null;

			// elgitc
			JsonObject elgItc = liabItc.has("elgitc")
					? liabItc.getAsJsonObject("elgitc") : null;

			if (elgItc != null) {

				elgItc4a1 = elgItc.has("itc4a1")
						? elgItc.getAsJsonObject("itc4a1") : null;
				elgItc4a3 = elgItc.has("itc4a3")
						? elgItc.getAsJsonObject("itc4a3") : null;
				elgItc4a4 = elgItc.has("itc4a4")
						? elgItc.getAsJsonObject("itc4a4") : null;
				elgItc4a5 = elgItc.has("itc4a5")
						? elgItc.getAsJsonObject("itc4a5") : null;
				elgItc4b2 = elgItc.has("itc4b2")
						? elgItc.getAsJsonObject("itc4b2") : null;
				elgItc4d2 = elgItc.has("itc4d2")
						? elgItc.getAsJsonObject("itc4d2") : null;

			}
			if (elgItc4a1 != null) {
				itc4a1 = elgItc4a1.has("subtotal")
						? elgItc4a1.getAsJsonObject("subtotal") : null;

			}

			if (elgItc4a3 != null) {
				itc4a3 = elgItc4a3.has("subtotal")
						? elgItc4a3.getAsJsonObject("subtotal") : null;

			}

			if (elgItc4a4 != null) {
				itc4a4 = elgItc4a4.has("subtotal")
						? elgItc4a4.getAsJsonObject("subtotal") : null;

			}

			if (elgItc4a5 != null) {
				itc4a5 = elgItc4a5.has("subtotal")
						? elgItc4a5.getAsJsonObject("subtotal") : null;

			}

			if (elgItc4b2 != null) {
				itc4b2 = elgItc4b2.has("subtotal")
						? elgItc4b2.getAsJsonObject("subtotal") : null;

			}
			if (elgItc4d2 != null) {
				itc4d2 = elgItc4d2.has("subtotal")
						? elgItc4d2.getAsJsonObject("subtotal") : null;

			}

			map.put(Gstr3BAutoCalConstants.Table3_1_A, osup3A);
			map.put(Gstr3BAutoCalConstants.Table3_1_B, osup3B);
			map.put(Gstr3BAutoCalConstants.Table3_1_C, osup3C);
			map.put(Gstr3BAutoCalConstants.Table3_1_E, osup3E);
			map.put(Gstr3BAutoCalConstants.Table3_2_A, interSupA);
			map.put(Gstr3BAutoCalConstants.Table3_2_B, interSupB);
			map.put(Gstr3BAutoCalConstants.Table3_2_C, interSupC);

			map.put(Gstr3BAutoCalConstants.Table3_1_D, isup3D);

			// section 4 Itc
			map.put(Gstr3BAutoCalConstants.Table4A1, itc4a1);
			// not available Table4A2
			map.put(Gstr3BAutoCalConstants.Table4A3, itc4a3);
			map.put(Gstr3BAutoCalConstants.Table4A4, itc4a4);
			map.put(Gstr3BAutoCalConstants.Table4A5, itc4a5);
			map.put(Gstr3BAutoCalConstants.Table4A5A, itc4a5);

			// not available Table4B1
			map.put(Gstr3BAutoCalConstants.Table4B2, itc4b2);

			// 4D2 Changes.
			map.put(Gstr3BAutoCalConstants.Table4D2, itc4d2);

			return map;
		} catch (Exception ex) {
			LOGGER.error("Exception occured while parsing get3BAutoGet payload",
					ex);
			throw new AppException(
					"Exception occured while parsing " + "get3BAutoGet payload",
					ex);

		}
	}

}
