/**
 * 
 */
package com.ey.advisory.app.services.einvoice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.einv.api.GenerateIrn;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.app.api.APIReqParamConstants;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.common.BusinessStatisticsLogHelper;
import com.ey.advisory.einv.common.JwtParserUtility;
import com.ey.advisory.einv.dto.EinvoiceRequestDto;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;
import com.ey.advisory.einv.dto.QrCodeDataResponse;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun K.A
 *
 */
@Slf4j
@Component("GenerateEinvoiceServiceImpl")
public class GenerateEinvoiceServiceImpl implements GenerateEinvoiceService {

	private static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final List<String> INCLUDED_IRPS = ImmutableList.of("NIC101",
			APIReqParamConstants.EY, "NIC", APIReqParamConstants.IRP,
			APIReqParamConstants.NIC_1, APIReqParamConstants.NIC_2);

	@Autowired
	@Qualifier("GenerateIrnImpl")
	private GenerateIrn generateIrn;

	@Autowired
	@Qualifier("EinvoiceRequestConverter")
	private EinvoiceRequestConverter einvReqConverter;

	@Autowired
	@Qualifier("EinvoiceRequestVersionConverter")
	private EinvoiceRequestVersionConverter einvVersionReqConverter;

	@Autowired
	@Qualifier("GetEInvDetailsServiceImpl")
	GetEInvDetailsService getEInvDetailsService;

	@Autowired
	BusinessStatisticsLogHelper businessStatisticsLogHelper;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Override
	public GenerateIrnResponseDto generateEinvRequest(OutwardTransDocument hdr,
			boolean isVersionEnabled) {
		String gstin = hdr.getSgstin();
		EinvoiceRequestDto eInvRequestDto = null;
		PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_SERVICE",
				"GenerateEinvoiceServiceImpl", "generateEinvRequest",
				"CONVERTER_START", true);

		if (isVersionEnabled) {
			eInvRequestDto = einvVersionReqConverter.convert(hdr);
		} else {
			eInvRequestDto = einvReqConverter.convert(hdr);
		}
		reqLogHelper.logAppMessage(hdr.getDocNo(), null, null,
				"Generate E Invoice Request Coverted");

		PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_SERVICE",
				"GenerateEinvoiceServiceImpl", "generateEinvRequest",
				"CONVERTER_END", true);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("The OutwardTransDocument is converted to "
					+ "the EinvoiceRequestDto : {}", eInvRequestDto);
		}
		APIResponse response = generateIrn.generateIrn(eInvRequestDto, gstin);
		return createGenerateIrnResponse(response, eInvRequestDto, gstin);
	}

	private GenerateIrnResponseDto createGenerateIrnResponse(
			APIResponse response, EinvoiceRequestDto eInvRequestDto,
			String gstin) {
		if (response.isSuccess()) {
			String jsonResp = "";
			jsonResp = response.getResponse();
			return getParsedResponse(jsonResp, eInvRequestDto);
		} else {
			return createGenerateEwbErrorResponse(response.getErrors(), gstin);
		}
	}

	private GenerateIrnResponseDto createGenerateEwbErrorResponse(
			List<APIError> errors, String gstin) {
		String errorCode = "";
		String errorDesc = "";
		String infoErrorCode = "";
		String infoErrorDesc = "";
		boolean isRetryReq = false;
		for (int i = 0; i < errors.size(); i++) {
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				if (errors.get(i).getErrorCode().equalsIgnoreCase("2150")
						|| errors.get(i).getErrorCode()
								.equalsIgnoreCase("2295")) {
					// 2150-Attempting to register a document again which is
					// already registered and IRN is generated
					isRetryReq = true;
				} else if (errors.get(i).getErrorCode()
						.equalsIgnoreCase("2278")) {
					// 2278-IRN is already generated and is cancelled for this
					// Document number. The duplicate IRN cannot be generated
					// for the same number.
					reqLogHelper.updateisDuplicate(true);
				}
			if (errors.get(i).getErrorCode().equalsIgnoreCase("DUPIRN")
					|| errors.get(i).getErrorCode()
							.equalsIgnoreCase("DUPIRNGLP")) {
				if (isRetryReq) {
					infoErrorCode = errors.get(i).getErrorCode();
					infoErrorDesc = errors.get(i).getErrorDesc();
					String ackDtStr = infoErrorDesc.split(",")[1].split("=")[1];
					String irn = infoErrorDesc.split(",")[2].split("=")[1];
					String regIrp = errors.get(i).getRegIrp();
					LocalDateTime ackDt = null;
					if (!Strings.isNullOrEmpty(ackDtStr)
							&& !ackDtStr.equalsIgnoreCase("-")) {
						try {
							ackDt = LocalDateTime.parse(ackDtStr, formatter);
						} catch (Exception e) {
							LOGGER.error("Exception while Deserializing AckDt",
									e);
						}
					}
					if (Strings.isNullOrEmpty(regIrp)) {
						GenerateIrnResponseDto respDto = getEInvDetailsService
								.getEInvDetails(irn, gstin, null);
						reqLogHelper.logInvDtlsParams(irn, ackDt,
								respDto.getEwbNo(), respDto.getEwbDt());
						return respDto;
					} else if (INCLUDED_IRPS.contains(regIrp)) {
						GenerateIrnResponseDto respDto = getEInvDetailsService
								.getEInvDetails(irn, gstin, regIrp);
						reqLogHelper.logInvDtlsParams(irn, ackDt,
								respDto.getEwbNo(), respDto.getEwbDt());
						return respDto;
					}
				}
			}
			errorCode = errorCode + (i + 1) + ") "
					+ errors.get(i).getErrorCode() + " ";
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				errorDesc = errorDesc + (i + 1) + ") "
						+ errors.get(i).getErrorDesc() + " ";
		}

		GenerateIrnResponseDto errResp = new GenerateIrnResponseDto();
		errResp.setErrorCode(errorCode);
		errResp.setErrorMessage(errorDesc);
		errResp.setInfoErrorCode(infoErrorCode);
		errResp.setInfoErrorMessage(infoErrorDesc);
		return errResp;
	}

	private GenerateIrnResponseDto getParsedResponse(String jsonResp,
			EinvoiceRequestDto eInvRequestDto) {

		PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_SERVICE",
				"GenerateEinvoiceServiceImpl", "getParsedResponse",
				"Response_Parse_START", true);
		
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		GenerateIrnResponseDto resp = gson.fromJson(jsonResp,
				GenerateIrnResponseDto.class);
		Claims claims = JwtParserUtility
				.getJwtBodyWithoutSignature(resp.getSignedQRCode());
		String claimResp = gson.toJson(claims);
		JsonObject requestObject = JsonParser.parseString(claimResp)
				.getAsJsonObject();
		String qrCodeString = requestObject.get("data").getAsString();
		QrCodeDataResponse qrCodeData = gson.fromJson(qrCodeString,
				QrCodeDataResponse.class);
		resp.setBuyerGstin(qrCodeData.getBuyerGstin());
		resp.setSellerGstin(qrCodeData.getSellerGstin());
		resp.setDocDate(qrCodeData.getDocDt());
		resp.setDocNum(qrCodeData.getDocNo());
		resp.setDocType(qrCodeData.getDocTyp());
		resp.setFormattedQrCode(qrCodeString);
		resp.setQrData(resp.getSignedQRCode());
		if (eInvRequestDto.getEwbDetails() != null
				&& eInvRequestDto.getEwbDetails().getDistance() != null) {
			resp.setNicDistance(
					eInvRequestDto.getEwbDetails().getDistance().toString());
		}

		JsonObject jsonResponse = JsonParser.parseString(jsonResp)
				.getAsJsonObject();

		if (jsonResponse.get("InfoDtls") != null) {

			String infoErrorCode = "";
			String infoErrorDesc = "";

			String infoJson = jsonResponse.get("InfoDtls").getAsString();

			String infCode = JsonParser.parseString(infoJson).getAsJsonArray()
					.get(0).getAsJsonObject().get("InfCd").getAsString();

			if ("EWBERR".equalsIgnoreCase(infCode)) {

				JsonArray infoArray = JsonParser.parseString(infoJson)
						.getAsJsonArray().get(0).getAsJsonObject()
						.getAsJsonArray("Desc");

				for (int i = 0; i < infoArray.size(); i++) {
					JsonObject obj = infoArray.get(i).getAsJsonObject();
					if (!Strings.isNullOrEmpty(obj.toString()))
						infoErrorCode = infoErrorCode + (i + 1) + ") "
								+ obj.get("ErrorCode").getAsString() + " ";
					if (!Strings.isNullOrEmpty(obj.toString()))
						infoErrorDesc = infoErrorDesc + (i + 1) + ") "
								+ obj.get("ErrorMessage").getAsString() + " ";
				}
			} else if ("EWBALERT".equalsIgnoreCase(infCode)
					|| "EWBPPD".equalsIgnoreCase(infCode)
					|| "EWBVEH".equalsIgnoreCase(infCode)
					|| "ADDNLNFO".equalsIgnoreCase(infCode)) {
				infoErrorDesc = JsonParser.parseString(infoJson)
						.getAsJsonArray().get(0).getAsJsonObject().get("Desc")
						.getAsString();
			}

			resp.setInfoErrorCode(infoErrorCode);
			resp.setInfoErrorMessage(infoErrorDesc);

			// if distance is not sending in request, then extracting distance
			// from NIC response
			String nicDistance = extractDistance(infoErrorDesc);
			if (!Strings.isNullOrEmpty(nicDistance)
					&& !Strings.isNullOrEmpty(resp.getEwbNo())) {
				resp.setNicDistance(nicDistance.replaceAll("[^0-9]", ""));
			}
		}

		Integer nicDistance = null;
		if (!Strings.isNullOrEmpty(resp.getNicDistance())) {
			nicDistance = Integer.valueOf(resp.getNicDistance());

		}
		
		businessStatisticsLogHelper.logBusinessApiResponse(resp.getAckNo(),
				resp.getAckDt(), resp.getIrn(), resp.getDocType(),
				resp.getDocNum(), resp.getDocDate(), resp.getSellerGstin(),
				resp.getBuyerGstin(), resp.getEwbNo(), resp.getEwbDt(),
				resp.getEwbValidTill(), null, nicDistance,
				APIIdentifiers.GENERATE_EINV, false, resp.getSignedInvoice(),
				resp.getSignedQRCode());

		reqLogHelper.logAppMessage(resp.getDocNum(), null, null,
				"Success Response from NIC for Generate E Invoice");
		
		PerfUtil.logEventToFile("GENERATE_EINVOICE", "GENERATE_EINVOICE_SERVICE",
				"GenerateEinvoiceServiceImpl", "getParsedResponse",
				"Response_Parse_END", true);

		return resp;
	}

	private String extractDistance(String infoMessage) {
		String nicDistanceStr = "";
		int distanceIndex = infoMessage.lastIndexOf("distance:");
		if (distanceIndex != -1) {
			nicDistanceStr = infoMessage.substring(distanceIndex);
		}
		return nicDistanceStr;
	}

	// private GenerateIrnResponseDto getIrnDtlsReq(String infoErrorCode,
	// String infoErrorDesc, String gstin, String regIrp,
	// boolean switchReq) {
	//
	// String ackDtStr = infoErrorDesc.split(",")[1].split("=")[1];
	// String irn = infoErrorDesc.split(",")[2].split("=")[1];
	// LocalDateTime ackDt = null;
	// if (!Strings.isNullOrEmpty(ackDtStr)
	// && !ackDtStr.equalsIgnoreCase("-")) {
	// try {
	// ackDt = LocalDateTime.parse(ackDtStr, formatter);
	// } catch (Exception e) {
	// LOGGER.error("Exception while Deserializing AckDt", e);
	// }
	// }
	// if (!switchReq) {
	// GenerateIrnResponseDto respDto = getEInvDetailsService
	// .getEInvDetails(irn, gstin);
	// reqLogHelper.logInvDtlsParams(irn, ackDt, respDto.getEwbNo(),
	// respDto.getEwbDt());
	// return respDto;
	// } else {
	// GenerateIrnResponseDto respDto = getEInvDetailsService
	// .getEInvDetails(irn, gstin);
	// reqLogHelper.logInvDtlsParams(irn, ackDt, respDto.getEwbNo(),
	// respDto.getEwbDt());
	// return respDto;
	// }
	// }

}
