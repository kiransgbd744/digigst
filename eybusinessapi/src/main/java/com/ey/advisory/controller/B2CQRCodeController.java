package com.ey.advisory.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.business.dto.OutwardTransDocLineItem;
import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.common.APIRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.B2COnBoardingCommonUtility;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.EinvJsonSchemaValidatorHelper;
import com.ey.advisory.common.GenerateDeepLinkRespDto;
import com.ey.advisory.common.GenerateParamsForDeepLink;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SaveOnBoardingParamsUtility;
import com.ey.advisory.common.client.domain.B2COnBoardingConfigEntity;
import com.ey.advisory.common.client.domain.B2CQRAmtConfigEntity;
import com.ey.advisory.common.client.repositories.B2COnBoardingConfigRepo;
import com.ey.advisory.common.client.repositories.B2CQRAmtConfigRepo;
import com.ey.advisory.common.service.B2CDeepLinkReqRespLogHelper;
import com.ey.advisory.einv.client.ErrorDetailsDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class B2CQRCodeController {

	private static final String AND = "&";
	private static final String PIPE = "|";

	@Autowired
	@Qualifier("B2COnBoardingConfigRepo")
	B2COnBoardingConfigRepo b2cOnBoardingRepo;

	@Autowired
	@Qualifier("B2CQRAmtConfigRepo")
	B2CQRAmtConfigRepo b2CQRAmtConfigRepo;

	@Autowired
	SaveOnBoardingParamsUtility saveOnBoardingUtility;

	@Autowired
	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;

	@Autowired
	private B2CDeepLinkReqRespLogHelper b2creqLogHelper;

	@Autowired
	B2COnBoardingCommonUtility b2cOnBoardingCommonUtility;

	@PostMapping(value = "/saveB2CQROnboardingParamsBCAPI")
	public ResponseEntity<String> saveB2CQROnboardingParamsBCAPI(
			@RequestBody String jsonString, HttpServletResponse response)
			throws Exception {
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		JsonObject resp = null;
		try {
			JsonObject reqJson = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", reqJson);
			}
			reqJson.addProperty("createdUser", "SYSTEM");
			JsonArray dataArray = reqJson.get("data").getAsJsonArray();
			String persistStatus = saveOnBoardingUtility
					.saveQROnBoardingData(reqJson, dataArray);
			if (persistStatus.equalsIgnoreCase("Success")) {
				resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.addProperty("resp", "Records Saved Successfully");
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			} else {
				resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("Failed to Save Records"));
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while saveB2COnboardingParams in BCAPI",
					ex);
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree("Failed to Save Records"));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/saveB2CQRAmtOnboardingParamsBCAPI")
	public ResponseEntity<String> saveB2CQRAmtOnboardingParamsBCAPI(
			@RequestBody String jsonString, HttpServletResponse response)
			throws Exception {
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		JsonObject resp = null;
		try {
			JsonObject reqJson = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", reqJson);
			}
			reqJson.addProperty("createdUser", "SYSTEM");
			String persistStatus = saveOnBoardingUtility
					.saveQRAmtConfigData(reqJson);
			if (persistStatus.equalsIgnoreCase("Success")) {
				resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.addProperty("resp", "Records Saved Successfully");
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			} else {
				resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("Failed to Save Records"));
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while saveB2CQRAmtOnboardingParamsBCAPI in BCAPI",
					ex);
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree("Failed to Save Records"));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/generateB2cDeepLink", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateQRCodeURLNew(
			@RequestBody String jsonString, HttpServletRequest req) {
		JsonObject jsonObj = new JsonObject();
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		GenerateDeepLinkRespDto response;
		List<ErrorDetailsDto> errorList = new ArrayList<>();
		String pan = null;
		BigDecimal docamount = BigDecimal.ZERO;
		try {
			errorList = jsonSchemaValidatorHelper.validateInptJson(jsonString,
					"EinvoiceNestedJsonSchema.json");
			if (errorList.isEmpty()) {
				OutwardTransDocument hdr = gson.fromJson(
						requestObject.get("req"), OutwardTransDocument.class);
				pan = hdr.getSgstin().substring(2, 12);
				String plantAtFirstLine = hdr.getLineItems().get(0)
						.getPlantCode();
				if (plantAtFirstLine != null) {
					String msg = String.format(
							"Extracting PlantCode %s from first"
									+ " Lineitem and setting it to Header",
							plantAtFirstLine);
					hdr.setPlantCode(plantAtFirstLine);
					LOGGER.debug(msg);
				} else {
					LOGGER.debug("Plant Code of first Line Item is Present,"
							+ " Hence Plant Code is considered as NULL");
				}
				GenerateParamsForDeepLink getValuesfromEntity = null;
				List<B2COnBoardingConfigEntity> bcOnboardingEntity = b2cOnBoardingRepo
						.findByPanAndIsActiveTrue(pan);
				if (bcOnboardingEntity.isEmpty()) {
					String errMsg = String.format(
							"No Data Available in OnBoarding Entity for the Pan %s",
							pan);
					LOGGER.error(errMsg);
					throw new AppException(errMsg);
				}
				String optionSelected = bcOnboardingEntity.get(0)
						.getOptionSelected();
				getValuesfromEntity = b2cOnBoardingCommonUtility
						.getValuesfromEntityBasedOnOption(optionSelected,
								hdr.getPlantCode(), hdr.getProfitCentre(),
								hdr.getSgstin(), pan);
				if (getValuesfromEntity == null) {
					String errMsg = String.format(
							"No Data Available in OnBoarding Entity for the Selected Option %s",
							optionSelected);
					LOGGER.error(errMsg);
					throw new AppException(errMsg);

				}

				String payeeAddress = getValuesfromEntity.getPayeeAddress();
				String payeeMerCode = getValuesfromEntity.getPayeeMerCode();
				String payeeName = getValuesfromEntity.getPayeeName();
				String transMode = getValuesfromEntity.getTransMode();
				String qrExpiryTime = getValuesfromEntity.getQrExpireTime();
				String transQRMed = getValuesfromEntity.getTransMode();
				String paymentInfo = getValuesfromEntity.getPaymentInfo();

				if ("B".equalsIgnoreCase(paymentInfo)) {

					docamount = hdr.getLineItems().stream()
							.filter(Objects::nonNull)
							.map(OutwardTransDocLineItem::getBalanceAmount)
							.filter(Objects::nonNull) // If price can be null
							.reduce(BigDecimal.ZERO, BigDecimal::add)
							.setScale(2, BigDecimal.ROUND_HALF_EVEN);
				} else {
					docamount = hdr.getDocAmount();
				}

				response = signedQrCodeGeneration(hdr, transMode, transQRMed,
						payeeAddress, payeeName, payeeMerCode, qrExpiryTime,
						pan, docamount);
				B2CQRAmtConfigEntity amtOnboardingEntity = b2CQRAmtConfigRepo
						.findByPanAndIsActiveTrue(pan);
				if (amtOnboardingEntity == null) {
					String errMsg = String.format(
							"No Data Available for Minimum Amount for the Pan %s Hence Generating 14 attribute URL",
							pan);
					LOGGER.error(errMsg);
				} else {
					String qrCodeUrl = response.getQrUrl();
					String amtIdentifier = amtOnboardingEntity.getIdentifier();
					String value = amtOnboardingEntity.getValue();
					if (amtIdentifier.equalsIgnoreCase("percent")) {
						BigDecimal percentageAmount = hdr.getDocAmount();
						percentageAmount = percentageAmount
								.multiply(new BigDecimal(
										(double) Double.valueOf(value) / 100));
						qrCodeUrl = qrCodeUrl + AND + "mam=" + percentageAmount
								.setScale(2, BigDecimal.ROUND_HALF_EVEN);

					} else {
						qrCodeUrl = qrCodeUrl + AND + "mam=" + value;
					}
					response.setQrUrl(qrCodeUrl);
				}
				JsonElement respBody = gson.toJsonTree(response);
				if (response == null)
					jsonObj.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
				else {
					jsonObj.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
				}
				jsonObj.add("resp", respBody);
				b2creqLogHelper.updateResponsePayload(jsonObj.toString(), true,
						pan);
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			}
			GenerateDeepLinkRespDto errorResp = generateErrorList(errorList);
			JsonElement respBody = gson.toJsonTree(errorResp);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.add("resp", respBody);
			b2creqLogHelper.updateResponsePayload(jsonObj.toString(), false,
					pan);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (NumberFormatException ex) {
			LOGGER.error("Exception while Generating QR Code URL ", ex);
			JsonObject resp = new JsonObject();
			response = new GenerateDeepLinkRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage("Request contains a field in string format"
					+ " which should be passed as numeric format");
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			b2creqLogHelper.updateResponsePayload(resp.toString(), false, pan);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (IncorrectResultSizeDataAccessException ex) {
			LOGGER.error("Exception while Generating QR Code URL ", ex);
			String errorMessage = "Found Duplicate Records in Onboarding For the Selected Option,Please check.";
			JsonObject resp = new JsonObject();
			response = new GenerateDeepLinkRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(errorMessage);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			b2creqLogHelper.updateResponsePayload(resp.toString(), false, pan);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Generating QR Code URL ", ex);
			JsonObject resp = new JsonObject();
			response = new GenerateDeepLinkRespDto();
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);

			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			b2creqLogHelper.updateResponsePayload(resp.toString(), false, pan);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			b2creqLogHelper.saveLogEntity();
		}
	}

	private GenerateDeepLinkRespDto signedQrCodeGeneration(
			OutwardTransDocument out, String transMode, String transQRMed,
			String payeeAddress, String payeeName, String merchantCode,
			String qrExpiryTime, String pan, BigDecimal docAmount) {
		GenerateDeepLinkRespDto respDto = null;
		try {
			StringBuilder result = new StringBuilder();
			result.append("upi://pay?ver=01");
			if (!Strings.isNullOrEmpty(transMode)) {
				result.append(AND);
				result.append("mode=");
				result.append(transMode);
			}
			result.append(AND);
			result.append("tr=");
			result.append(out.getDocNo());
			result.append(AND);
			result.append("tn=00");
			if (!Strings.isNullOrEmpty(payeeAddress)) {
				result.append(AND);
				result.append("pa=");
				result.append(payeeAddress);
			}
			if (!Strings.isNullOrEmpty(payeeName)) {
				result.append(AND);
				result.append("pn=");
				result.append(payeeName);
			}
			if (!Strings.isNullOrEmpty(merchantCode)) {
				result.append(AND);
				result.append("mc=");
				result.append(merchantCode);
			}
			result.append(AND);
			result.append("am=");
			result.append(docAmount);
			result.append(AND);
			result.append("gstBrkUp=");
			result.append(amount(out));
			if (!Strings.isNullOrEmpty(transQRMed)) {
				result.append(AND);
				result.append("qrMedium=");
				result.append(transQRMed);
			}
			result.append(AND);
			result.append("invoiceNo=");
			result.append(out.getDocNo());
			result.append(AND);
			result.append("InvoiceDate=");
			result.append(out.getDocDate().atStartOfDay() + ":00+5:30");
			if (!Strings.isNullOrEmpty(qrExpiryTime)) {
				result.append(AND);
				result.append("QRexpire=");
				LocalDateTime nowDate = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now()
								.plusDays(Integer.parseInt(qrExpiryTime)));
				String qRexpire = nowDate + "+5:30";
				result.append(qRexpire);
			}
			result.append(AND);
			result.append("gstIn=");
			result.append(out.getSgstin());

			respDto = new GenerateDeepLinkRespDto();
			respDto.setDocNo(out.getDocNo());
			respDto.setDocDate(out.getDocDate());
			respDto.setDocType(out.getDocType());
			respDto.setSgstin(out.getSgstin());
			respDto.setQrUrl(result.toString().replaceAll(" ", "%20"));
			respDto.setPan(pan);
			b2creqLogHelper.updateUrlCreatedOn();
			return respDto;
		} catch (Exception e) {
			LOGGER.error(
					"Exception while Generating QR Code URL in SignedQR Method",
					e);
			return respDto;
		}
	}

	private boolean isIntraState(OutwardTransDocument out) {

		return out.getInvoiceCgstAmount() != null
				&& out.getInvoiceCgstAmount().compareTo(BigDecimal.ZERO) == 1
				&& out.getInvoiceSgstAmount() != null
				&& out.getInvoiceSgstAmount().compareTo(BigDecimal.ZERO) == 1;
	}

	private boolean isInterState(OutwardTransDocument out) {

		return out.getInvoiceIgstAmount() != null
				&& out.getInvoiceIgstAmount().compareTo(BigDecimal.ZERO) == 1;
	}

	private boolean isPartialIntraState(OutwardTransDocument out) {
		boolean isPartialIntraState = false;

		if ((out.getInvoiceCgstAmount() != null
				&& out.getInvoiceCgstAmount().compareTo(BigDecimal.ZERO) == 1)
				&& (out.getInvoiceSgstAmount() == null
						|| out.getInvoiceSgstAmount()
								.compareTo(BigDecimal.ZERO) == 0)) {

			isPartialIntraState = true;
		}

		if ((out.getInvoiceSgstAmount() != null
				&& out.getInvoiceSgstAmount().compareTo(BigDecimal.ZERO) == 1)
				&& (out.getInvoiceCgstAmount() == null
						|| out.getInvoiceCgstAmount()
								.compareTo(BigDecimal.ZERO) == 0)) {

			isPartialIntraState = true;
		}

		return isPartialIntraState;
	}

	private String amount(OutwardTransDocument out) {
		StringBuilder amount = new StringBuilder();
		boolean isInterState = isInterState(out);
		boolean isIntraState = isIntraState(out);
		boolean isPartialIntraState = isPartialIntraState(out);

		if (isInterState && !isIntraState) {
			amount.append("IGST:" + out.getInvoiceIgstAmount());
		} else if ((!isInterState && isIntraState) || isPartialIntraState) {
			BigDecimal sgstAmount = out.getInvoiceSgstAmount();
			BigDecimal cgstAmount = out.getInvoiceCgstAmount();

			if (cgstAmount == null) {
				cgstAmount = BigDecimal.ZERO;
			}
			if (sgstAmount == null) {
				sgstAmount = BigDecimal.ZERO;
			}
			amount.append("CGST:" + cgstAmount).append(PIPE)
					.append("SGST:" + sgstAmount);
		} else {
			BigDecimal igstAmount = out.getInvoiceIgstAmount();
			if (igstAmount == null) {
				igstAmount = BigDecimal.ZERO;
			}
			amount.append("IGST:" + igstAmount);
		}

		BigDecimal invcessAmountAdvalorem = out.getInvoiceCessAdvaloremAmount();
		BigDecimal invcessAmountSpecific = out.getInvoiceCessSpecificAmount();
		BigDecimal invstateCessSpecificAmt = out.getInvStateCessSpecificAmt();
		BigDecimal invstateAmountAdvalorem = out.getInvoiceStateCessAmount();
		if (invcessAmountAdvalorem != null || invcessAmountSpecific != null
				|| invstateCessSpecificAmt != null
				|| invstateAmountAdvalorem != null) {
			if (invcessAmountAdvalorem == null) {
				invcessAmountAdvalorem = BigDecimal.ZERO;
			}

			if (invcessAmountSpecific == null) {
				invcessAmountSpecific = BigDecimal.ZERO;
			}

			if (invstateCessSpecificAmt == null) {
				invstateCessSpecificAmt = BigDecimal.ZERO;
			}
			if (invstateAmountAdvalorem == null) {
				invstateAmountAdvalorem = BigDecimal.ZERO;
			}

			BigDecimal cessAmount = invcessAmountAdvalorem
					.add(invcessAmountSpecific).add(invstateCessSpecificAmt)
					.add(invstateAmountAdvalorem);
			amount.append(PIPE).append("CESS:" + cessAmount);
		}

		return amount.toString();
	}

	private GenerateDeepLinkRespDto generateErrorList(
			List<ErrorDetailsDto> errorDto) {
		String errorCode = "";
		String errorDesc = "";
		String infoErrorCode = "";
		String infoErrorDesc = "";
		for (int i = 0; i < errorDto.size(); i++) {

			if (!Strings.isNullOrEmpty(errorDto.get(i).getErrorField())) {
				errorCode = errorCode + (i + 1) + ") "
						+ errorDto.get(i).getErrorField() + " ";
			}
			if (!Strings.isNullOrEmpty(errorDto.get(i).getErrorDesc()))
				errorDesc = errorDesc + (i + 1) + ") "
						+ errorDto.get(i).getErrorDesc() + " ";
		}
		GenerateDeepLinkRespDto errResp = new GenerateDeepLinkRespDto();
		errResp.setErrorCode(errorCode);
		errResp.setErrorMessage(errorDesc);
		errResp.setInfoErrorCode(infoErrorCode);
		errResp.setInfoErrorMessage(infoErrorDesc);
		return errResp;
	}
}
