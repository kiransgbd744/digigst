package com.ey.advisory.app.data.services.einvoice;

import java.math.RoundingMode;
import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.javatuples.Quartet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EinvoiceRepository;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.app.data.services.ewb.EwbDbUtilService;
import com.ey.advisory.app.data.services.ewb.EwbRequestConverter;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AspInvoiceStatus;
import com.ey.advisory.common.EInvoiceStatus;
import com.ey.advisory.common.EwbProcessingStatus;
import com.ey.advisory.common.EwbStatusNew;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.IrnStatusMaster;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.einv.api.GenerateIrn;
import com.ey.advisory.einv.api.GetEInvDetails;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.client.EinvoiceEntity;
import com.ey.advisory.einv.common.BusinessStatisticsLogHelper;
import com.ey.advisory.einv.common.EinvConstant;
import com.ey.advisory.einv.common.EyEInvCommonUtil;
import com.ey.advisory.einv.common.JwtParserUtility;
import com.ey.advisory.einv.dto.CancelIrnERPResponseDto;
import com.ey.advisory.einv.dto.CancelIrnReqDto;
import com.ey.advisory.einv.dto.CancelIrnReqList;
import com.ey.advisory.einv.dto.EinvoiceRequestDto;
import com.ey.advisory.einv.dto.GenEWBByIrnDispNICReqDto;
import com.ey.advisory.einv.dto.GenEWBByIrnExpShpNICReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnNICReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;
import com.ey.advisory.einv.dto.GenerateIrnResponseXmlDto;
import com.ey.advisory.einv.dto.QrCodeDataResponse;
import com.ey.advisory.ewb.common.EwbConstants;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("EinvoiceAsyncServiceImpl")
public class EinvoiceAsyncServiceImpl implements EinvoiceAsyncService {

	private static final List<String> INCLUDED_IRPS = ImmutableList.of("NIC101",
			"EY", "NIC", "IRP");

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docrepo;

	@Autowired
	@Qualifier("GenerateIrnImpl")
	private GenerateIrn generateIrn;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("CancelIrnServiceImpl")
	private CancelIrnService cancelIrnService;

	@Autowired
	@Qualifier("EinvoiceRequestConverter")
	private EinvoiceRequestConverter einvReqConverter;

	@Autowired
	@Qualifier("EinvoiceRepository")
	EinvoiceRepository einvoiceRepo;

	@Autowired
	@Qualifier("EwbRequestConverter")
	EwbRequestConverter ewbReqConverter;

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepository;

	@Autowired
	private AsyncJobsService persistenceMngr;

	@Autowired
	private GeneEInvUtil geneEInvUtil;

	@Autowired
	@Qualifier("GenerateEWBByIrnServiceImpl")
	private GenerateEWBByIrnService generateEWBByIrn;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Autowired
	@Qualifier("EwbDbUtilServiceImpl")
	private EwbDbUtilService ewbDbService;

	@Autowired
	@Qualifier("GetEInvDetailsServiceImpl")
	GetEInvDetailsService getEInvDetailsService;

	@Autowired
	@Qualifier("GetEInvDetailsImpl")
	private GetEInvDetails getEInvDetails;

	@Autowired
	BusinessStatisticsLogHelper businessStatisticsLogHelper;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static DateTimeFormatter FORMATTER1 = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");

	private static DateTimeFormatter FORMATTER2 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	@Override
	public String generateIrnAsync(List<Long> docIdList, String isEwbReq) {
		JsonObject jsonParams = new JsonObject();
		List<AsyncExecJob> geneinvAsyncJobs = new ArrayList<>();

		if (isEwbReq.equals("true")) {
			jsonParams.addProperty("isEwbRequired", "true");
		} else
			jsonParams.addProperty("isEwbRequired", "false");
		docIdList.forEach(o -> {
			jsonParams.addProperty("id", o);
			geneinvAsyncJobs.add(asyncJobsService.createJobAndReturn(
					TenantContext.getTenantId(), JobConstants.EINVOICE_ASYNC,
					jsonParams.toString(), "SYSTEM", 1L, null, null));
		});
		if (!geneinvAsyncJobs.isEmpty()) {
			persistenceMngr.createJobs(geneinvAsyncJobs);
		}
		return "Request has been submitted";
	}

	@Override
	public String generateIrn(Long id, String isEwbReq) {
		APIResponse response = null;
		OutwardTransDocument doc = null;
		boolean isDuplicateIrn = false;
		try {
			LOGGER.debug("generateIrn Method Begin ,fetching the"
					+ "doc header details from db for id  {}", id);

			Optional<OutwardTransDocument> transDocuments = docrepo
					.findById(id);
			if (!transDocuments.isPresent()) {
				String errMsg = "No Invoice Found For Requested Params";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			doc = transDocuments.get();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("generateIrn  ,convert the doc header detail "
						+ "into einvoice request dto");
			}
			EinvoiceRequestDto eInvoiceReqDto = einvReqConverter.convert(doc,
					isEwbReq);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"generateIrn  ,converted  the doc header detail "
								+ "into einvoice request dto {} ",
						eInvoiceReqDto);
			}
			String sgstin = doc.getSgstin();
			response = callEnvoiceApi(eInvoiceReqDto, sgstin);

			if (response.isSuccess()) {
				String jsonResp = "";
				jsonResp = response.getResponse();
				GenerateIrnResponseXmlDto resp = getParsedResponse(jsonResp,
						eInvoiceReqDto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Einvoice Generation successfull now about to update "
									+ "the db with new irn {} and ackDate {}",
							resp.getIrn(), resp.getAckDt().toString());
				}
				// save einvoice
				Optional<EinvoiceEntity> einvRecord = einvoiceRepo
						.getEinvDetails(doc.getIrnResponse());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Entered saveDocuments for Irn num {}",
							doc.getIrn());
				}

				if (!einvRecord.isPresent()) {
					EinvoiceEntity einv = createEinvoiceEntity(resp, id);
					einvoiceRepo.save(einv);
				}

				// if both einvoice and ewb are success and are opted
				if (isEwbReq.equalsIgnoreCase("true")) {
					if (!Strings.isNullOrEmpty(resp.getEwbNo())) {
						// save to ewb
						EwayBillRequestDto ewbReqDto = ewbReqConverter
								.convert(doc);

						EwbResponseDto ewbRespDto = new EwbResponseDto();
						ewbRespDto.setEwayBillDate(resp.getEwbDt());
						ewbRespDto.setEwayBillNo(resp.getEwbNo());
						ewbRespDto.setValidUpto(resp.getEwbValidTill());
						if (resp.getNicDistance() != null) {
							ewbReqDto.setTransDistance(
									Integer.valueOf(resp.getNicDistance()));
							ewbRespDto.setNicDistance(
									Integer.valueOf(resp.getNicDistance()));
						}

						ewbDbService.generateEwbDbUpdate(doc, ewbReqDto,
								ewbRespDto, APIIdentifiers.GENERATE_EINV);

						docrepo.updateIrnDetails(id, resp.getAckNo().toString(),
								resp.getAckDt(), resp.getIrn(),
								IrnStatusMaster.GENERATED.getIrnStatusMaster(),
								null, null, null, null,
								AspInvoiceStatus.ASP_PROCESSED
										.getAspInvoiceStatusCode(),
								EInvoiceStatus.IRN_GENERATED
										.geteInvoiceStatusCode());

					} else {

						LOGGER.error("EinvoiceAsync job error code : {}",
								resp.getErrorCode());

						boolean ewbAlreadyGenerated = (resp
								.getErrorCode() != null
								&& resp.getErrorCode().contains("604")) ? true
										: false;

						docrepo.updateDocHeaderForEWBFailure(id,
								resp.getAckNo().toString(), resp.getAckDt(),
								resp.getIrn(),
								IrnStatusMaster.GENERATED.getIrnStatusMaster(),
								null, null,
								EwbStatusNew.PENDING_ERROR
										.getEwbNewStatusCode(),
								ewbAlreadyGenerated
										? EwbProcessingStatus.ERROR_CANCELLATION
												.getEwbProcessingStatusCode()
										: EwbProcessingStatus.GENERATION_ERROR
												.getEwbProcessingStatusCode(),
								resp.getInfoErrorCode(),
								resp.getInfoErrorMessage(), null, null,
								AspInvoiceStatus.ASP_PROCESSED
										.getAspInvoiceStatusCode(),
								EInvoiceStatus.IRN_GENERATED
										.geteInvoiceStatusCode());
					}
				}

				else {
					docrepo.updateEinvoiceDocHeader(id,
							resp.getAckNo().toString(), resp.getAckDt(),
							resp.getIrn(),
							IrnStatusMaster.GENERATED.getIrnStatusMaster(),
							null, null,
							AspInvoiceStatus.ASP_PROCESSED
									.getAspInvoiceStatusCode(),
							EInvoiceStatus.IRN_GENERATED
									.geteInvoiceStatusCode());
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("generateIrn  ,updation successfull");
				}
				Integer nicDistance = null;
				if (!Strings.isNullOrEmpty(resp.getNicDistance())) {
					nicDistance = Integer.valueOf(resp.getNicDistance());

				}
				businessStatisticsLogHelper.logBusinessApiResponse(
						String.valueOf(resp.getAckNo()), resp.getAckDt(),
						resp.getIrn(), resp.getDocType(), resp.getDocNum(),
						resp.getDocDate(), resp.getSellerGstin(),
						resp.getBuyerGstin(), resp.getEwbNo(), resp.getEwbDt(),
						resp.getEwbValidTill(), null, nicDistance,
						APIIdentifiers.GENERATE_EINV, false,
						resp.getSignedInvoice(), resp.getSignedQRCode());
				reqLogHelper.logAdditionalParams(doc.getSgstin(),
						doc.getDocType(), doc.getDocNo(), true, true);
				reqLogHelper.logInvDtlsParams(resp.getIrn(), resp.getAckDt(),
						resp.getEwbNo(), resp.getEwbDt());
				reqLogHelper.logAckNum(String.valueOf(resp.getAckNo()));
				reqLogHelper.updateResponsePayload(response.getResponse(),
						true);
				return EinvConstant.SYNC_SUCCESS_RESPONSE;

			} else {
				String msg = String.format(
						"Error occured while generating Einvoice"
								+ " with errorCode : %s , errorMsg : %s ",
						response.getError().getErrorCode(),
						response.getError().getErrorDesc());
				LOGGER.debug(msg);
				Integer aspInvStatus = AspInvoiceStatus.ASP_ERROR
						.getAspInvoiceStatusCode();

				List<APIError> errors = response.getErrors();

				String errorCode = "";
				String errorDesc = "";
				String infoErrorDesc = "";
				boolean isRetryReq = false;
				boolean isDupIrn = false;
				for (int i = 0; i < errors.size(); i++) {
					if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode())) {
						errorCode = errorCode + (i + 1) + ") "
								+ errors.get(i).getErrorCode() + " ";
						if (errors.get(i).getErrorCode()
								.equalsIgnoreCase("2150")
								|| errors.get(i).getErrorCode()
										.equalsIgnoreCase("2295")) {
							isRetryReq = true;
						} else if (errors.get(i).getErrorCode()
								.equalsIgnoreCase("2278")) {
							isDupIrn = true;
						}
					}
					if (!Strings.isNullOrEmpty(errors.get(i).getErrorDesc())) {
						errorDesc = errorDesc + (i + 1) + ") "
								+ errors.get(i).getErrorDesc() + " ";
					}

					if (errors.get(i).getErrorCode().equalsIgnoreCase("DUPIRN")
							|| errors.get(i).getErrorCode()
									.equalsIgnoreCase("DUPIRNGLP")) {
						if (isRetryReq || isDupIrn) {

							docrepo.updateEInvByDocId(id,
									EInvoiceStatus.DUPLICATE_IRN
											.geteInvoiceStatusCode());
							if (isDupIrn) {
								isDuplicateIrn = true;
							} else {
								infoErrorDesc = errors.get(i).getErrorDesc();
								String irn = infoErrorDesc.substring(
										infoErrorDesc.indexOf("Irn") + 4,
										infoErrorDesc.length());
								String regIrp = errors.get(i).getRegIrp();

								APIResponse getEInvresponse = null;
								if (Strings.isNullOrEmpty(regIrp)) {
									getEInvresponse = getEInvDetails
											.getEInvDetails(irn, transDocuments
													.get().getSgstin(), null);

								} else if (INCLUDED_IRPS.contains(regIrp)) {
									getEInvresponse = getEInvDetails
											.getEInvDetails(irn,
													transDocuments.get()
															.getSgstin(),
													regIrp);
								}

								if (getEInvresponse != null
										&& getEInvresponse.isSuccess()) {
									GenerateIrnResponseXmlDto genEinvresp = getParsedResponse(
											getEInvresponse.getResponse(),
											eInvoiceReqDto);
									EinvoiceEntity einvEntity = einvoiceRepo
											.findByIrn(irn);
									if (einvEntity == null) {
										EinvoiceEntity einv = createEinvoiceEntity(
												genEinvresp, id);
										einvoiceRepo.save(einv);
									}
									geneEInvUtil.dbPersistanceGenEInv(
											genEinvresp, isEwbReq, doc, id);
									reqLogHelper.logInvDtlsParams(
											genEinvresp.getIrn(),
											genEinvresp.getAckDt(),
											genEinvresp.getEwbNo(),
											genEinvresp.getEwbDt());
									reqLogHelper.logAckNum(String
											.valueOf(genEinvresp.getAckNo()));
									reqLogHelper.updateResponsePayload(
											getEInvresponse.getResponse(),
											true);
									return EinvConstant.SYNC_SUCCESS_RESPONSE;
								} else {
									String getEinverrorCode = "";
									String getEinverrorDesc = "";
									List<APIError> getEinvErrors = getEInvresponse
											.getErrors();
									for (int j = 0; j < getEinvErrors
											.size(); j++) {
										if (!Strings.isNullOrEmpty(getEinvErrors
												.get(j).getErrorCode()))
											getEinverrorCode = getEinverrorCode
													+ (j + 1) + ") "
													+ getEinvErrors.get(j)
															.getErrorCode()
													+ " ";
										if (!Strings.isNullOrEmpty(getEinvErrors
												.get(j).getErrorCode()))
											getEinverrorDesc = getEinverrorDesc
													+ (j + 1) + ") "
													+ getEinvErrors.get(j)
															.getErrorDesc()
													+ " ";
									}
									isDuplicateIrn = true;
									errorDesc = getEinverrorDesc;
									errorCode = getEinverrorCode;
								}
							}
						}
					}
				}

				// einoive and ewb both failure also ewb is opted
				if (isEwbReq.equalsIgnoreCase("true")) {

					LOGGER.error(
							"EinvoiceAsync and ewb failure with error code : {}",
							errorCode);
					docrepo.updateEinvoiceEwbError(id, errorDesc, errorCode,
							EwbStatusNew.PENDING_ERROR.getEwbNewStatusCode(),
							isDuplicateIrn
									? EwbProcessingStatus.ERROR_CANCELLATION
											.getEwbProcessingStatusCode()
									: EwbProcessingStatus.GENERATION_ERROR
											.getEwbProcessingStatusCode(),
							IrnStatusMaster.PENDING_ERROR.getIrnStatusMaster(),
							aspInvStatus,
							isDuplicateIrn
									? EInvoiceStatus.DUPLICATE_IRN
											.geteInvoiceStatusCode()
									: EInvoiceStatus.IRN_ERROR
											.geteInvoiceStatusCode());
				}
				// if einvoice is failure and ewb is not opted
				else {
					docrepo.updateEinvoiceError(id, errorDesc, errorCode,
							IrnStatusMaster.PENDING_ERROR.getIrnStatusMaster(),
							aspInvStatus,
							isDuplicateIrn
									? EInvoiceStatus.DUPLICATE_IRN
											.geteInvoiceStatusCode()
									: EInvoiceStatus.IRN_ERROR
											.geteInvoiceStatusCode());
				}
				reqLogHelper.updateResponsePayload(
						response.getErrors().toString(), false);
				reqLogHelper.logAdditionalParams(doc.getSgstin(),
						doc.getDocType(), doc.getDocNo(), true, true);
				return EinvConstant.REQUEST_FAILED;
			}
		} catch (Exception e) {
			LOGGER.error("Exception while Generating E-invoice", e);
			docrepo.updateEinvoiceError(id, e.getMessage(),
					EwbConstants.INTERNAL_ERROR_CODE,
					IrnStatusMaster.PENDING_ERROR.getIrnStatusMaster(),
					AspInvoiceStatus.ASP_ERROR.getAspInvoiceStatusCode(),
					EInvoiceStatus.IRN_ERROR.geteInvoiceStatusCode());
			APIError err = new APIError();
			if (e.getMessage().equals("socketException")) {
				err.setErrorCode("ER001");
			}
			err.setErrorCode("OTHEXP");
			err.setErrorDesc(e.getMessage());
			response = new APIResponse();
			response.addError(err);
			reqLogHelper.updateResponsePayload(e.getMessage(), false);
			throw new AppException(e);
		}
	}

	private EinvoiceEntity createEinvoiceEntity(GenerateIrnResponseXmlDto resp,
			Long id) {
		EinvoiceEntity einv = new EinvoiceEntity();
		Clob signedInv = null;
		try {
			signedInv = new javax.sql.rowset.serial.SerialClob(
					resp.getSignedInvoice().toCharArray());
		} catch (Exception ex) {
			LOGGER.error("exception occured while converting into clob");
		}
		einv.setAckNo(String.valueOf(resp.getAckNo()));
		einv.setAckDt(resp.getAckDt());
		einv.setSignedInv(signedInv);
		einv.setSignedQR(resp.getSignedQRCode());
		einv.setIrn(resp.getIrn());
		einv.setCreatedBy("SYSTEM");
		einv.setCreatedOn(LocalDateTime.now());
		einv.setModifiedBy("SYSTEM");
		einv.setModifiedOn(LocalDateTime.now());
		einv.setDelete(false);
		einv.setDocHeaderId(id);
		einv.setQrCode(resp.getQrData());
		einv.setFormattedQRCode(resp.getFormattedQrCode());
		einv.setStatusDesc(resp.getStatus());
		return einv;
	}

	private APIResponse callEnvoiceApi(EinvoiceRequestDto eInvoiceReqDto,
			String sgstin) {
		try {
			return generateIrn.generateIrn(eInvoiceReqDto, sgstin);
		} catch (Exception e) {
			LOGGER.error("Exception Occured in callEnvoiceApi", e);
			throw new AppException(e.getMessage());
		}

	}

	private GenerateIrnResponseXmlDto getParsedResponse(String jsonResp,
			EinvoiceRequestDto eInvoiceReqDto) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		GenerateIrnResponseDto resp = gson.fromJson(jsonResp,
				GenerateIrnResponseDto.class);

		GenerateIrnResponseXmlDto xmlDto = new GenerateIrnResponseXmlDto();
		Claims claims = JwtParserUtility
				.getJwtBodyWithoutSignature(resp.getSignedQRCode());
		String claimResp = gson.toJson(claims);
		JsonObject requestObject = (new JsonParser()).parse(claimResp)
				.getAsJsonObject();
		String qrCodeString = requestObject.get("data").getAsString();
		QrCodeDataResponse qrCodeData = gson.fromJson(qrCodeString,
				QrCodeDataResponse.class);
		xmlDto.setBuyerGstin(qrCodeData.getBuyerGstin());
		xmlDto.setSellerGstin(qrCodeData.getSellerGstin());
		xmlDto.setDocDate(qrCodeData.getDocDt());
		xmlDto.setDocNum(qrCodeData.getDocNo());
		xmlDto.setDocType(qrCodeData.getDocTyp());
		xmlDto.setFormattedQrCode(qrCodeString);
		xmlDto.setQrData(qrCodeString);
		xmlDto.setAckDt(resp.getAckDt());
		xmlDto.setAckNo(Long.valueOf(resp.getAckNo()));
		xmlDto.setIrn(resp.getIrn());
		xmlDto.setSignedInvoice(resp.getSignedInvoice());
		xmlDto.setSignedQRCode(resp.getSignedQRCode());
		xmlDto.setStatus(resp.getStatus());
		xmlDto.setEwbNo(resp.getEwbNo());
		xmlDto.setEwbDt(resp.getEwbDt());
		xmlDto.setEwbValidTill(resp.getEwbValidTill());

		JsonObject jsonResponse = (new JsonParser()).parse(jsonResp)
				.getAsJsonObject();

		if (jsonResponse.has("InfoDtls")
				&& jsonResponse.get("InfoDtls") != null) {

			String infoErrorCode = "";
			String infoErrorDesc = "";

			String infoJson = jsonResponse.get("InfoDtls").getAsString();

			String infCode = (new JsonParser()).parse(infoJson).getAsJsonArray()
					.get(0).getAsJsonObject().get("InfCd").getAsString();

			if ("EWBERR".equalsIgnoreCase(infCode)) {
				JsonArray infoArray = (new JsonParser()).parse(infoJson)
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
					|| "EWBPPD".equalsIgnoreCase(infCode)) {
				infoErrorDesc = (new JsonParser()).parse(infoJson)
						.getAsJsonArray().get(0).getAsJsonObject().get("Desc")
						.getAsString();
			} else if ("EWBVEH".equalsIgnoreCase(infCode)) {
				infoErrorDesc = (new JsonParser()).parse(infoJson)
						.getAsJsonArray().get(0).getAsJsonObject().get("Desc")
						.getAsString();

			} else if ("ADDNLNFO".equalsIgnoreCase(infCode)) {
				infoErrorDesc = (new JsonParser()).parse(infoJson)
						.getAsJsonArray().get(0).getAsJsonObject().get("Desc")
						.getAsString();
			}

			String nicDistance = extractDistance(infoErrorDesc);
			if (!Strings.isNullOrEmpty(nicDistance)
					&& !Strings.isNullOrEmpty(resp.getEwbNo())) {
				xmlDto.setNicDistance(nicDistance.replaceAll("[^0-9]", ""));
			}
			xmlDto.setInfoErrorCode(infoErrorCode);
			xmlDto.setInfoErrorMessage(infoErrorDesc);
		} else {
			if (eInvoiceReqDto.getEwbDetails() != null)
				xmlDto.setNicDistance(String
						.valueOf(eInvoiceReqDto.getEwbDetails().getDistance()));
		}

		return xmlDto;
	}

	@Override
	public String cancelEInvSync(CancelIrnReqDto cancelIrnReqDto) {

		CancelIrnERPResponseDto response;
		try {
			response = cancelIrnService.CancelEinvEwbRequest(cancelIrnReqDto);
			if (response.getCancelDate() != null) {
				return EinvConstant.SYNC_SUCCESS_RESPONSE;
			} else {
				return response.getErrorMessage();
			}
		} catch (Exception e) {
			LOGGER.error("error in sync cancel E Invoice processing", e);
			docrepo.updateEInvByDocId(cancelIrnReqDto.getDocHeaderId(),
					EInvoiceStatus.ERROR_CANCELLATION.geteInvoiceStatusCode());
			return e.getMessage();
		}
	}

	public String cancelEInvASync(CancelIrnReqList canelEInvReqList) {

		List<CancelIrnReqDto> reqList = canelEInvReqList.getReqList();

		List<AsyncExecJob> canAsyncJobs = new ArrayList<>();
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();

		try {
			reqList.forEach(o -> {
				canAsyncJobs.add(createAsyncJob(JobConstants.EINV_CANCEL,
						gson.toJson(o)));
			});
			if (!canAsyncJobs.isEmpty()) {
				persistenceMngr.createJobs(canAsyncJobs);
			}
			return EinvConstant.ASYNC_SUCCESS_RESPONSE;

		} catch (Exception e) {
			String errMsg = "Exception Occured while processing cancel E Inv Req";
			LOGGER.error(errMsg, e);
			return EinvConstant.REQUEST_FAILED;
		}

	}

	private AsyncExecJob createAsyncJob(String jobCategory, String jobParams) {

		AsyncExecJob job = new AsyncExecJob();
		job.setGroupCode(TenantContext.getTenantId());
		job.setJobCategory(jobCategory);
		job.setStatus(JobStatusConstants.SUBMITTED);
		job.setMessage(jobParams);
		job.setJobPriority(1L);
		job.setUserName("SYSTEM");
		job.setCreatedDate(new Date());
		return job;
	}

	public String generateEWBByIrn(Long id) {
		GenerateEWBByIrnResponseDto response = null;
		OutwardTransDocument doc = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("generateEWB by Irn Method Begin ,fetching the"
						+ "doc header details from db for id " + id);
			}
			Optional<OutwardTransDocument> transDocuments = docrepo
					.findById(id);
			if (!transDocuments.isPresent()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("generate Ewb  ,no invoice available");
				}
				throw new AppException("No Invoice Found For Requested Params");
			}
			doc = transDocuments.get();

			LOGGER.debug("generate Ewb  ,convert the doc header detail "
					+ "into einvoice request dto");
			reqLogHelper.logAdditionalParams(doc.getSgstin(), doc.getDocType(),
					doc.getDocNo(), true, true);
			GenerateEWBByIrnNICReqDto req = convertOutWardtoGenEWBIrn(doc);
			response = generateEWBByIrn.generateEwayIrnRequest(req,
					transDocuments.get());
			if (response.getEwbNo() != null) {
				return EinvConstant.SYNC_SUCCESS_RESPONSE;
			} else {
				return response.getErrorMessage();
			}
		} catch (Exception e) {
			LOGGER.error("error in sync generate Ewb by Irn processing", e);
			return e.getMessage();
		}
	}

	@Override
	public GenerateEWBByIrnNICReqDto convertOutWardtoGenEWBIrn(
			OutwardTransDocument outTransDocument) {
		GenerateEWBByIrnNICReqDto nicReqDto = new GenerateEWBByIrnNICReqDto();
		nicReqDto.setTransId(outTransDocument.getTransporterID());
		nicReqDto.setTransName(outTransDocument.getTransporterName());
		nicReqDto
				.setDistance(outTransDocument.getDistance() != null
						? outTransDocument.getDistance()
								.setScale(0, RoundingMode.HALF_UP).intValue()
						: null);
		String irn = extractIRNFromDoc(outTransDocument);
		nicReqDto.setIrn(irn);
		nicReqDto.setGstin(outTransDocument.getSgstin());
		nicReqDto.setSuppPincd(outTransDocument.getSupplierPincode());
		nicReqDto.setDispatcherPincd(outTransDocument.getDispatcherPincode());
		nicReqDto.setCustPincd(outTransDocument.getCustomerPincode());
		nicReqDto.setShipToPincd(outTransDocument.getShipToPincode());
		boolean isPartBEligible = isEligibleForPartB(outTransDocument);
		if (isPartBEligible) {
			if (nicReqDto.getTrnDocDt() != null) {
				nicReqDto.setTrnDocDt(convertDateFormat(
						outTransDocument.getTransportDocDate().toString()));
			}
			nicReqDto.setTrnDocNo(outTransDocument.getTransportDocNo());
			nicReqDto.setTransMode(EyEInvCommonUtil
					.getTransMode(outTransDocument.getTransportMode()));
			nicReqDto.setVehNo(outTransDocument.getVehicleNo());
			nicReqDto.setVehType(outTransDocument.getVehicleType());
		}
		boolean isEligibleforDocCatFlag = geneEInvUtil
				.isAddressSuppressRequired(outTransDocument);
		Quartet<Boolean, Boolean, Boolean, Boolean> eligibleAddSuppression = EyEInvCommonUtil
				.eligibleAddresstobeNIC(outTransDocument.getDocCategory(),
						isEligibleforDocCatFlag);

		boolean isDispatcherDetailsReq = eligibleAddSuppression.getValue2();
		boolean isShiptoDetailsReq = eligibleAddSuppression.getValue3();
		if (isDispatcherDetailsReq) {
			GenEWBByIrnDispNICReqDto nicReqdispDtls = new GenEWBByIrnDispNICReqDto();
			nicReqdispDtls
					.setAddr1(outTransDocument.getDispatcherBuildingNumber());
			nicReqdispDtls
					.setAddr2(outTransDocument.getDispatcherBuildingName());
			nicReqdispDtls.setLoc(outTransDocument.getDispatcherLocation());
			nicReqdispDtls
					.setPin(outTransDocument.getDispatcherPincode() != null
							? outTransDocument.getDispatcherPincode() : null);
			nicReqdispDtls.setStcd(EyEInvCommonUtil
					.getstateCode(outTransDocument.getDispatcherStateCode()));
			nicReqdispDtls.setNm(outTransDocument.getDispatcherTradeName());
			if (!GenEWBByIrnDispNICReqDto.isEmpty(nicReqdispDtls))
				nicReqDto.setDispDtls(nicReqdispDtls);
		}
		if (isShiptoDetailsReq) {
			GenEWBByIrnExpShpNICReqDto nicReqExpShpDtls = new GenEWBByIrnExpShpNICReqDto();
			nicReqExpShpDtls
					.setAddr1(outTransDocument.getShipToBuildingNumber());
			nicReqExpShpDtls.setAddr2(outTransDocument.getShipToBuildingName());
			nicReqExpShpDtls.setLoc(outTransDocument.getShipToLocation());
			nicReqExpShpDtls.setPin(outTransDocument.getShipToPincode() != null
					? outTransDocument.getShipToPincode() : null);
			nicReqExpShpDtls.setStcd(EyEInvCommonUtil
					.getstateCode(outTransDocument.getShipToState()));
			if (!GenEWBByIrnExpShpNICReqDto.isEmpty(nicReqExpShpDtls))
				nicReqDto.setExpShipDtls(nicReqExpShpDtls);
		}

		return nicReqDto;

	}

	public String convertDateFormat(String dateFormat1) {
		try {
			LocalDate localDateFormat1 = LocalDate.parse(dateFormat1,
					FORMATTER1);
			return FORMATTER2.format(localDateFormat1);
		} catch (Exception ex) {
			String errMsg = String.format(
					"Error while Parsing the TransdocDate for GenEWBBYIRN date - %s",
					dateFormat1);
			LOGGER.error(errMsg, ex);
			throw new AppException(errMsg);
		}
	}

	private boolean isEligibleForPartB(OutwardTransDocument hdrReq) {

		Map<String, Config> configMap = configManager.getConfigs("EINV",
				"ewb.partb", TenantContext.getTenantId());

		boolean partBSuppresReq = configMap.get("ewb.partb.suppresreq") == null
				? false
				: Boolean.valueOf(
						configMap.get("ewb.partb.suppresreq").getValue());

		if (!partBSuppresReq) {
			return true;
		}

		if (Strings.isNullOrEmpty(hdrReq.getTransportMode())) {
			return false;
		}

		if ("Road".equalsIgnoreCase(hdrReq.getTransportMode())) {
			return !Strings.isNullOrEmpty(hdrReq.getVehicleNo())
					&& !Strings.isNullOrEmpty(hdrReq.getVehicleType());
		} else if ("InTransit".equalsIgnoreCase(hdrReq.getTransportMode())) {
			return true;
		} else {
			return !Strings.isNullOrEmpty(hdrReq.getTransportDocNo())
					&& hdrReq.getTransportDocDate() != null;
		}
	}

	private String extractIRNFromDoc(OutwardTransDocument doc) {
		String irnResponse = doc.getIrnResponse();
		if (!Strings.isNullOrEmpty(irnResponse)) {
			return irnResponse;
		} else {
			return doc.getIrn();
		}
	}

	private String extractDistance(String infoMessage) {
		String nicDistanceStr = "";
		int distanceIndex = infoMessage.lastIndexOf("distance:");
		if (distanceIndex != -1) {
			nicDistanceStr = infoMessage.substring(distanceIndex);
		}
		return nicDistanceStr;
	}
}
