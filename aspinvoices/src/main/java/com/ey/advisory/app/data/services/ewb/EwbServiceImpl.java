/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.ewb.EwbMultiVehicleDao;
import com.ey.advisory.app.data.entities.client.EwbEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EwbMultiVehicleDetailsRepository;
import com.ey.advisory.app.data.repositories.client.EwbMultiVehicleRepository;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.app.data.repositories.client.OutwardTransDocumentRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AspInvoiceStatus;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.EwbProcessingStatus;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.einv.common.BusinessStatisticsLogHelper;
import com.ey.advisory.ewb.api.AddMultiVehicleDetails;
import com.ey.advisory.ewb.api.CancelEWB;
import com.ey.advisory.ewb.api.ChangeMultiVehicleDetails;
import com.ey.advisory.ewb.api.EWBUpdatePartB;
import com.ey.advisory.ewb.api.ExtendEWB;
import com.ey.advisory.ewb.api.GenerateConsolidatedEWB;
import com.ey.advisory.ewb.api.GenerateEwayBill;
import com.ey.advisory.ewb.api.GetEWBDetails;
import com.ey.advisory.ewb.api.GetEWBDetailsByDate;
import com.ey.advisory.ewb.api.GetEwayBillGeneratedByConsigner;
import com.ey.advisory.ewb.api.GetGSTINDetails;
import com.ey.advisory.ewb.api.GetHSNDetails;
import com.ey.advisory.ewb.api.InitiateMultiVehicle;
import com.ey.advisory.ewb.api.RejectEWB;
import com.ey.advisory.ewb.api.UpdateEWBTransporter;
import com.ey.advisory.ewb.app.api.APIError;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.client.domain.EwbLifecycleEntity;
import com.ey.advisory.ewb.client.domain.EwbMultiVehicleDetailsEntity;
import com.ey.advisory.ewb.client.domain.EwbMultiVehicleEntity;
import com.ey.advisory.ewb.client.repositories.EwbLifecycleRepository;
import com.ey.advisory.ewb.common.EwbConstants;
import com.ey.advisory.ewb.common.EyEwbCommonUtil;
import com.ey.advisory.ewb.common.EyEwbUtilMethods;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsDtoReq;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleRequestDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBResponseDto;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleGetFromAndToDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleGetVehicleRespDto;
import com.ey.advisory.ewb.dto.EwbMultiVehicleListGroupResp;
import com.ey.advisory.ewb.dto.EwbMultiVehicleListVehicleResp;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;
import com.ey.advisory.ewb.dto.ExtendEWBResponseDto;
import com.ey.advisory.ewb.dto.GetEwayBillGeneratedByConsignerDto;
import com.ey.advisory.ewb.dto.GetEwbByDateResponseDto;
import com.ey.advisory.ewb.dto.GetEwbResponseDto;
import com.ey.advisory.ewb.dto.GetGSTINResponseDto;
import com.ey.advisory.ewb.dto.GetHsnDetailsResponseDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleRequestDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleRespDto;
import com.ey.advisory.ewb.dto.RejectEwbReqDto;
import com.ey.advisory.ewb.dto.RejectEwbResponseDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterReqDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterRespDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbRequestDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbResponseDto;
import com.ey.advisory.processing.message.GetCounterPartyMsg;
import com.ey.advisory.repositories.client.LoggerAdviceRepository;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("EwbServiceImpl")
@Slf4j
public class EwbServiceImpl implements EwbService {

	@Autowired
	private EwbRequestConverter ewbRequestConverter;

	@Autowired
	@Qualifier("GenerateEwayBillImpl")
	private GenerateEwayBill generateEwayBill;

	@Autowired
	@Qualifier("CancelEWBImpl")
	private CancelEWB cancelEWB;

	@Autowired
	@Qualifier("EWBUpdatePartBImpl")
	private EWBUpdatePartB ewbUpdatePartB;

	@Autowired
	@Qualifier("GenerateConsolidatedEWBImpl")
	private GenerateConsolidatedEWB genConsolidatedEwb;

	@Autowired
	@Qualifier("ExtendEWBImpl")
	private ExtendEWB extendEwb;

	@Autowired
	@Qualifier("RejectEWBImpl")
	private RejectEWB rejectEWB;

	@Autowired
	@Qualifier("GetEWBDetailsImpl")
	private GetEWBDetails getEWBDetails;

	@Autowired
	@Qualifier("UpdateEWBTransporterImpl")
	private UpdateEWBTransporter updateTransporter;

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepository;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docrepo;

	@Autowired
	@Qualifier("EwbDbUtilServiceImpl")
	private EwbDbUtilService ewbDbService;

	@Autowired
	@Qualifier("EwbErpUtilServiceImpl")
	private EwbErpUtilService ewbErpService;

	@Autowired
	@Qualifier("InitiateMultiVehicleImpl")
	private InitiateMultiVehicle intiateMultiVeh;

	@Autowired
	@Qualifier("LoggerAdviceRepository")
	private LoggerAdviceRepository logAdviceRepo;

	@Autowired
	@Qualifier("AddMultiVehicleDetailsImpl")
	private AddMultiVehicleDetails addMultiVeh;

	@Autowired
	@Qualifier("ChangeMultiVehicleDetailsImpl")
	private ChangeMultiVehicleDetails changeMultiVeh;

	@Autowired
	@Qualifier("GetHSNDetailsImpl")
	private GetHSNDetails getHSNDetails;

	@Autowired
	@Qualifier("GetGSTINDetailsImpl")
	private GetGSTINDetails getGSTINDetails;

	@Autowired
	@Qualifier("GetEwayBillGeneratedByConsignerImpl")
	private GetEwayBillGeneratedByConsigner getEwayBillGeneratedByConsigner;

	@Autowired
	@Qualifier("GetEWBDetailsByDateImpl")
	private GetEWBDetailsByDate getEWBDetailsByDate;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Autowired
	@Qualifier("EwbMultiVehicleDaoImpl")
	private EwbMultiVehicleDao ewbMultiVehicleDao;

	@Autowired
	@Qualifier("EwbRepository")
	private EwbRepository ewbRepo;

	@Autowired
	@Qualifier("EwbMultiVehicleRepository")
	private EwbMultiVehicleRepository eWbrepoMultiVehicleRepo;

	@Autowired
	@Qualifier("EwbMultiVehicleDetailsRepository")
	private EwbMultiVehicleDetailsRepository ewbMultiVehicleDetailsRepo;

	@Autowired
	@Qualifier("EwbLifecycleRepository")
	private EwbLifecycleRepository ewbLifeCycleRepo;

	@Autowired
	private OutwardTransDocumentRepository outwardTransDocRepo;
	
	@Autowired
	BusinessStatisticsLogHelper businessStatisticsLogHelper;

	@Override
	public UpdatePartBEwbResponseDto updateEwbPartB(
			UpdatePartBEwbRequestDto req, String gstin, boolean dbUpdate,
			boolean erpPush) {
		APIResponse response;
		UpdatePartBEwbResponseDto resp;
		try {
			req.setTransMode(EyEwbCommonUtil.getTransMode(req.getTransMode()));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Begin  updateEwbPartB with requestInfo {} ", req);
			}
			response = ewbUpdatePartB.updateEwbPartB(req, gstin);
			resp = createUpdateEwbResponse(response);
			if (resp.getErrorCode() == null) {
				resp.setValidUpto(resp.getValidUpto());
				resp.setVehUpdDate(resp.getVehUpdDate());
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("End  updateEwbPartB ,response = {} ", response);
			}
			if (dbUpdate)
				ewbDbService.updatePartBDbUpdate(req, resp);
			// if (erpPush)
			// ewbErpService.updatePartBErpPush(req, resp);
		} catch (Exception e) {
			LOGGER.error("Exception while Updating Part B", e);
			APIError error = new APIError();
			error.setErrorCode(EwbConstants.INTERNAL_ERROR_CODE);
			error.setErrorDesc(e.getMessage());
			response = new APIResponse();
			response.addError(error);
			resp = createUpdateEwbResponse(response);
			docrepo.updateProcessingStatusByDocId(req.getDocHeaderId(),
					EwbProcessingStatus.ERROR_UPDATE_PARTB
							.getEwbProcessingStatusCode());

		}
		return resp;
	}

	private UpdatePartBEwbResponseDto createUpdateEwbResponse(
			APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			return gson.fromJson(jsonResp, UpdatePartBEwbResponseDto.class);
		} else {
			return createErrorResponse(response.getErrors());
		}

	}

	private UpdatePartBEwbResponseDto createErrorResponse(
			List<APIError> errors) {
		String errorCode = "";
		String errorDesc = "";
		for (int i = 0; i < errors.size(); i++) {
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				errorCode = errorCode + (i + 1) + ") "
						+ errors.get(i).getErrorCode() + " ";
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				errorDesc = errorDesc + (i + 1) + ") "
						+ errors.get(i).getErrorDesc() + " ";
		}
		UpdatePartBEwbResponseDto errResp = new UpdatePartBEwbResponseDto();
		errResp.setErrorCode(errorCode);
		errResp.setErrorMessage(errorDesc);
		return errResp;

	}

	@Override
	public CancelEwbResponseDto cancelEwb(CancelEwbReqDto req, boolean updateDb,
			boolean erpPush) {
		CancelEwbResponseDto resp;
		try {
			resp = createCancelEwbResponse(
					cancelEWB.cancelEwb(req, req.getGstin()), req.getEwbNo());
			if (updateDb)
				ewbDbService.cancelEwbDbUpdate(req, resp);
			if (erpPush)
				ewbErpService.cancelEwbErpPush(req, resp);
		} catch (Exception e) {

			LOGGER.error("Unable to cancel ewb", e);
			APIError error = new APIError();
			error.setErrorCode(EwbConstants.INTERNAL_ERROR_CODE);
			error.setErrorDesc(e.getMessage());
			APIResponse response = new APIResponse();
			response.addError(error);
			resp = createCancelEwbResponse(response, req.getEwbNo());
			docrepo.updateCancelErrorEwbResponseById(req.getDocHeaderId(),
					EwbConstants.INTERNAL_ERROR_CODE,
					"Unable to cancel eway bill",
					AspInvoiceStatus.ASP_ERROR.getAspInvoiceStatusCode(),
					EwbProcessingStatus.ERROR_CANCELLATION
							.getEwbProcessingStatusCode());
		}
		return resp;
	}

	private CancelEwbResponseDto createCancelEwbResponse(APIResponse response,
			String ewbNo) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			CancelEwbResponseDto cancelResp = gson.fromJson(jsonResp,
					CancelEwbResponseDto.class);
			reqLogHelper.updateResponsePayload(response.getResponse(), true);

			return cancelResp;
		} else {
			CancelEwbResponseDto resp = createCancelErrorResponse(
					response.getErrors());
			reqLogHelper.updateResponsePayload(response.getErrors().toString(),
					false);
			return resp;
		}

	}

	private CancelEwbResponseDto createCancelErrorResponse(
			List<APIError> errors) {
		String errorCode = "";
		String errorDesc = "";
		for (int i = 0; i < errors.size(); i++) {
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				errorCode = errorCode + (i + 1) + ") "
						+ errors.get(i).getErrorCode() + " ";
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				errorDesc = errorDesc + (i + 1) + ") "
						+ errors.get(i).getErrorDesc() + " ";
		}
		CancelEwbResponseDto errResp = new CancelEwbResponseDto();
		errResp.setErrorCode(errorCode);
		errResp.setErrorMessage(errorDesc);
		return errResp;

	}

	@Override
	public EwbResponseDto generateEwb(OutwardTransDocument hdr,
			boolean updateDb, boolean erpPush) {
		EwbResponseDto resp;
		APIResponse response;
		EwayBillRequestDto ewayRequestDto;
		try {
			String gstin;
			if ("O".equalsIgnoreCase(hdr.getTransactionType()))
				gstin = hdr.getSgstin();
			else
				gstin = hdr.getCgstin();
			ewayRequestDto = ewbRequestConverter.convert(hdr);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"The OutwardTransDocument is converted to "
								+ "the EinvoiceRequestDto : {}",
						ewayRequestDto);
			}
			if (hdr.getLineItems().get(0) != null
					&& hdr.getLineItems().get(0).getLineItemAmt() != null)
				ewayRequestDto.setTotInvValue(
						hdr.getLineItems().get(0).getLineItemAmt());
			response = generateEwayBill.generateEwbill(ewayRequestDto, gstin);
			resp = createGenerateEwbResponse(response);
			if (updateDb)
				ewbDbService.generateEwbDbUpdate(hdr, ewayRequestDto, resp,APIIdentifiers.GENERATE_EWB);
			if (erpPush)
				ewbErpService.generateEwbErpPush(ewayRequestDto, resp);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			APIError error = new APIError();
			error.setErrorCode(EwbConstants.INTERNAL_ERROR_CODE);
			error.setErrorDesc(e.getMessage());
			response = new APIResponse();
			response.addError(error);
			resp = createGenerateEwbResponse(response);

			docrepo.updateCancelErrorEwbResponseById(hdr.getId(),
					EwbConstants.INTERNAL_ERROR_CODE, e.getMessage(),
					AspInvoiceStatus.ASP_ERROR.getAspInvoiceStatusCode(),
					EwbProcessingStatus.ERROR_CANCELLATION
							.getEwbProcessingStatusCode());
			if (updateDb)
				ewbDbService.generateEwbDbUpdate(hdr, new EwayBillRequestDto(),
						resp,null);
			if (erpPush)
				ewbErpService.generateEwbErpPush(new EwayBillRequestDto(),
						resp);
		}
		return resp;
	}

	private EwbResponseDto createGenerateEwbResponse(APIResponse response) {

		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			EwbResponseDto successResp = gson.fromJson(jsonResp,
					EwbResponseDto.class);
			extractAndPopulateNicDistance(successResp);
			reqLogHelper.updateResponsePayload(response.getResponse(), true);
			businessStatisticsLogHelper.logBusinessApiResponse(null, null, null,
					null, null, null, null, null, successResp.getEwayBillNo(),
					successResp.getEwayBillDate(), successResp.getValidUpto(),
					null, successResp.getNicDistance(),
					APIIdentifiers.GENERATE_EWB, false, null, null);
			return successResp;
		} else {
			EwbResponseDto jsonResp = createGenerateEwbErrorResponse(
					response.getErrors());
			reqLogHelper.updateResponsePayload(response.getErrors().toString(),
					false);
			return jsonResp;
		}
	}

	private EwbResponseDto createGenerateEwbErrorResponse(
			List<APIError> errors) {
		String errorCode = "";
		String errorDesc = "";
		for (int i = 0; i < errors.size(); i++) {
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				errorCode = errorCode + (i + 1) + ") "
						+ errors.get(i).getErrorCode() + " ";
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				errorDesc = errorDesc + (i + 1) + ") "
						+ errors.get(i).getErrorDesc() + " ";
		}
		EwbResponseDto errResp = new EwbResponseDto();
		errResp.setErrorCode(errorCode);
		errResp.setErrorDesc(errorDesc);
		return errResp;

	}

	@Override
	public ConsolidateEWBResponseDto consolidateEWB(ConsolidateEWBReqDto req,
			String gstin, boolean updateDb, boolean erpPush) {
		APIResponse response;
		ConsolidateEWBResponseDto resp;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Begin  updateEwbPartB with requestInfo {} ", req);
			}
			response = genConsolidatedEwb.consolidateEWB(req, gstin);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("End  updateEwbPartB ,response = {} ", response);
			}
			resp = createGenConsolidatedEWBResponse(response);
			if (resp.getErrorCode() == null) {
				resp.setCEWBDate(
						EYDateUtil.toUTCDateTimeFromIST(resp.getCEWBDate()));
			}
			if (updateDb)
				ewbDbService.consolidateEwbDbUpdate(req, resp);
			if (erpPush)
				ewbErpService.consolidateEwbErpPush(req, resp);
		} catch (Exception e) {
			APIError error = new APIError();
			error.setErrorCode(EwbConstants.INTERNAL_ERROR_CODE);
			error.setErrorDesc(e.getMessage());
			response = new APIResponse();
			response.addError(error);
			resp = createGenConsolidatedEWBResponse(response);
			if (updateDb)
				ewbDbService.consolidateEwbDbUpdate(req, resp);
			if (erpPush)
				ewbErpService.consolidateEwbErpPush(req, resp);
		}
		return resp;
	}

	private ConsolidateEWBResponseDto createGenConsolidatedEWBResponse(
			APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			return gson.fromJson(jsonResp, ConsolidateEWBResponseDto.class);
		} else {
			Pair<String, String> errCodeDesc = EyEwbUtilMethods
					.createErrorResponse(response.getErrors());
			ConsolidateEWBResponseDto resp = new ConsolidateEWBResponseDto();
			resp.setErrorCode(errCodeDesc.getValue0());
			resp.setErrorDesc(errCodeDesc.getValue1());
			return resp;
		}

	}

	@Override
	public ExtendEWBResponseDto extendEWB(ExtendEWBReqDto req, String gstin,
			boolean updateDb, boolean erpPush) {
		APIResponse response;
		ExtendEWBResponseDto resp;
		try {
			req.setTransMode(EyEwbCommonUtil.getTransMode(req.getTransMode()));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Begin  updateEwbPartB with requestInfo {} ", req);
			}
			response = extendEwb.extendEwb(req, gstin);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("End  updateEwbPartB ,response = {} ", response);
			}
			resp = createExtendResponse(response);
			if (resp.getErrorCode() == null) {
				resp.setUpdatedDate(resp.getUpdatedDate());
				resp.setValidUpto(resp.getValidUpto());
			}
			if (updateDb)
				ewbDbService.extendDbUpdate(req, resp);
			if (erpPush)
				ewbErpService.extendErpPush(req, resp);
		} catch (Exception e) {
			APIError error = new APIError();
			error.setErrorCode(EwbConstants.INTERNAL_ERROR_CODE);
			error.setErrorDesc(e.getMessage());
			response = new APIResponse();
			response.addError(error);
			resp = createExtendResponse(response);
			if (updateDb)
				ewbDbService.extendDbUpdate(req, resp);
			if (erpPush)
				ewbErpService.extendErpPush(req, resp);
		}
		return resp;
	}

	private ExtendEWBResponseDto createExtendResponse(APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			return gson.fromJson(jsonResp, ExtendEWBResponseDto.class);
		} else {
			Pair<String, String> errCodeDesc = EyEwbUtilMethods
					.createErrorResponse(response.getErrors());
			ExtendEWBResponseDto resp = new ExtendEWBResponseDto();
			resp.setErrorCode(errCodeDesc.getValue0());
			resp.setErrorDesc(errCodeDesc.getValue1());
			return resp;
		}

	}

	@Override
	public GetEwbResponseDto getEWB(String ewbNo, String gstin,
			boolean updateDb, boolean erpPush) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin  getEwb with requestInfo {} ", ewbNo);
		}
		APIResponse response = getEWBDetails.getEWBDetails(ewbNo, gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End  getEwb ,response = {} ", response);
		}
		return createGetEWBResponse(response);
	}

	private GetEwbResponseDto createGetEWBResponse(APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			return gson.fromJson(jsonResp, GetEwbResponseDto.class);
		} else {
			Pair<String, String> errCodeDesc = EyEwbUtilMethods
					.createErrorResponse(response.getErrors());
			GetEwbResponseDto resp = new GetEwbResponseDto();
			resp.setErrorCode(errCodeDesc.getValue0());
			resp.setErrorMessage(errCodeDesc.getValue1());
			return resp;
		}

	}

	@Override
	public UpdateEWBTransporterRespDto updateTransporter(
			UpdateEWBTransporterReqDto req, String gstin, boolean updateDb,
			boolean erpPush) {
		APIResponse response;
		UpdateEWBTransporterRespDto resp;
		try {

			EwbEntity ewbEntity = ewbRepository.findByEwbNum(req.getEwbNo());
			if (ewbEntity == null) {
				String errMsg = String.format(
						"EWB No %s is not available in DigiGST",
						req.getEwbNo());
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			response = updateTransporter.updateEWBTransporter(req, gstin);
			resp = createUpdateTransporterResponse(response);
			ewbDbService.updateTransporterDbUpdate(req, resp);
		} catch (Exception e) {
			APIError error = new APIError();
			error.setErrorCode(EwbConstants.INTERNAL_ERROR_CODE);
			error.setErrorDesc(e.getMessage());
			response = new APIResponse();
			response.addError(error);
			resp = createUpdateTransporterResponse(response);
			docrepo.updateProcessingStatusByDocId(req.getDocHeaderId(),
					EwbProcessingStatus.ERROR_UPDATE_TRANSPORTER
							.getEwbProcessingStatusCode());
		}
		return resp;
	}

	private UpdateEWBTransporterRespDto createUpdateTransporterResponse(
			APIResponse response) {

		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			return gson.fromJson(jsonResp, UpdateEWBTransporterRespDto.class);
		} else {
			Pair<String, String> errCodeDesc = EyEwbUtilMethods
					.createErrorResponse(response.getErrors());
			UpdateEWBTransporterRespDto resp = new UpdateEWBTransporterRespDto();
			resp.setErrorCode(errCodeDesc.getValue0());
			resp.setErrorMessage(errCodeDesc.getValue1());
			return resp;
		}
	}

	@Override
	public InitiateMultiVehicleRespDto initiateMultiVehicles(
			InitiateMultiVehicleRequestDto req, String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin   initiate multivehicle with requestInfo {} ",
					req);
		}

		Gson gson = GsonUtil.newSAPGsonInstanceWithEWBDateFmt();

		InitiateMultiVehicleRequestDto request = new InitiateMultiVehicleRequestDto();
		request.setEwbNo(req.getEwbNo());
		request.setGstin(req.getGstin());
		request.setFromPlace(req.getFromPlace());
		request.setFromState(req.getFromState());
		request.setReasonCode(req.getReasonCode());
		request.setReasonRem(req.getReasonRem());
		request.setToPlace(req.getToPlace());
		request.setToState(req.getToState());
		request.setTotalQuantity(req.getTotalQuantity());
		request.setUnitCode(req.getUnitCode());
		request.setTransMode(req.getTransMode());

		String reqBody = gson.toJson(request);

		APIResponse response = ewbMultiVehicleDao.initiateMultiVehicle(reqBody,
				gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End   initiate multivehicle ,response = {} ",
					response);
		}
		return createInitiateMultivehicleResponse(response, req);
	}

	@SuppressWarnings("unused")
	private InitiateMultiVehicleRespDto createInitiateMultivehicleResponse(
			APIResponse response, InitiateMultiVehicleRequestDto req) {

		String respBody = null;

		InitiateMultiVehicleRespDto resp = new InitiateMultiVehicleRespDto();

		if (response.isSuccess()) {
			String jsonResp = "";

			jsonResp = response.getResponse();

			InitiateMultiVehicleRespDto respDto = new InitiateMultiVehicleRespDto();

			JsonParser jsonParser = new JsonParser();

			JsonObject parseResponse = (JsonObject) jsonParser.parse(jsonResp)
					.getAsJsonObject();

			String groupNo = parseResponse.get("groupNo").getAsString();

			respDto.setGroupNo(groupNo);

			ewbMultiVehicleDao.saveInitiateMultivehicleResp(jsonResp, req);

			return respDto;
		} else {
			List<APIError> apiErrorList = response.getErrors();
			resp = createErrorRespInitiate(apiErrorList, req);
			return resp;
		}
	}

	private InitiateMultiVehicleRespDto createErrorRespInitiate(
			List<APIError> apiErrorList, InitiateMultiVehicleRequestDto req) {

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

		EwbMultiVehicleEntity entity = new EwbMultiVehicleEntity();

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		entity.setEwbNo(req.getEwbNo());
		entity.setDocHeaderId(req.getDocHeaderId());
		entity.setTransMode(req.getTransMode());
		entity.setFromPlace(req.getFromPlace());
		entity.setToPlace(req.getToPlace());
		entity.setTotalQty(req.getTotalQuantity());
		entity.setUnit(req.getUnitCode());
		entity.setFromState(req.getFromState());
		entity.setToState(req.getToState());
		entity.setReason(req.getReasonCode());
		entity.setRemarks(req.getReasonRem());
		entity.setCreatedDate(LocalDateTime.now());
		entity.setCreatedBy(userName);
		entity.setErrorCode(errorCode);
		entity.setErrorMessage(errorDesc);
		eWbrepoMultiVehicleRepo.save(entity);

		InitiateMultiVehicleRespDto resp = new InitiateMultiVehicleRespDto();
		resp.setErrorCode(errorCode);
		resp.setErrorMessage(errorDesc);
		return resp;
	}

	@Override
	public AddMultiVehicleDetailsRespDto addMultiVehicles(
			AddMultiVehicleDetailsDtoReq req, String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin   add multivehicle with requestInfo {} ", req);
		}
		APIResponse response = ewbMultiVehicleDao.addMultiVehicle(req, gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End   add multivehicle ,response = {} ", response);
		}
		return createAddMultivehicleResponse(response, req);
	}

	private AddMultiVehicleDetailsRespDto createAddMultivehicleResponse(
			APIResponse response, AddMultiVehicleDetailsDtoReq req) {

		AddMultiVehicleDetailsRespDto addVehicleresp = new AddMultiVehicleDetailsRespDto();

		if (response.isSuccess()) {
			String jsonResp = "";
			jsonResp = response.getResponse();

			String message = ewbMultiVehicleDao.saveAddMultivehicleRes(jsonResp,
					req);
			addVehicleresp.setMessage(message);

			return addVehicleresp;
		} else {
			List<APIError> apiErrorList = response.getErrors();
			addVehicleresp = createErrorResp(apiErrorList, req);
			return addVehicleresp;
		}

	}

	private AddMultiVehicleDetailsRespDto createErrorResp(
			List<APIError> apiErrorList, AddMultiVehicleDetailsDtoReq req) {
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
		AddMultiVehicleDetailsRespDto resp = new AddMultiVehicleDetailsRespDto();
		resp.setErrorCode(errorCode);
		resp.setErrorMessage(errorDesc);

		String groupNo = req.getGroupNo();

		Long groupNumb = Long.parseLong(groupNo);

		EwbMultiVehicleEntity initiateVehicle = eWbrepoMultiVehicleRepo
				.findByEwbNoAndGroupNoAndErrorCodeIsNullAndErrorMessageIsNull(
						req.getEwbNo(), groupNumb);

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		DateTimeFormatter formatt = DateTimeFormatter.ofPattern("d/MM/yyyy");
		LocalDate localDate = LocalDate.parse(req.getTransDocDate(), formatt);

		EwbMultiVehicleDetailsEntity entity = new EwbMultiVehicleDetailsEntity();
		entity.setVehicleNum(req.getVehicleNo());
		entity.setMultiVehicleId(initiateVehicle.getId());
		entity.setTransDocDate(localDate);
		entity.setTransDocNo(req.getTransDocNo());
		entity.setVehicleQty(req.getQuantity());
		entity.setGroupNo(groupNumb);
		entity.setCreatedDate(LocalDateTime.now());
		entity.setCreatedBy(userName);
		entity.setFromState(initiateVehicle.getFromState());
		entity.setFromPlace(initiateVehicle.getFromPlace());
		entity.setReason(initiateVehicle.getReason());
		entity.setRemarks(initiateVehicle.getRemarks());
		entity.setErrorCode(errorCode);
		entity.setErrorMessage(errorDesc);
		entity.setError(true);
		entity.setFunction("A");
		ewbMultiVehicleDetailsRepo.save(entity);
		return resp;
	}

	@Override
	public ChangeMultiVehicleDetailsRespDto changeMultiVehicles(
			ChangeMultiVehicleRequestDto req, String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin   change multivehicle with requestInfo {} ",
					req);
		}
		APIResponse response = ewbMultiVehicleDao.changeMultiVehicle(req,
				gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End   change multivehicle ,response = {} ", response);
		}
		return createChangeMultivehicleResponse(response, req);
	}

	private ChangeMultiVehicleDetailsRespDto createChangeMultivehicleResponse(
			APIResponse response, ChangeMultiVehicleRequestDto req) {

		ChangeMultiVehicleDetailsRespDto createVehicleResp = new ChangeMultiVehicleDetailsRespDto();

		if (response.isSuccess()) {
			String jsonResp = "";
			jsonResp = response.getResponse();

			String message = ewbMultiVehicleDao.changeMultiVehicle(jsonResp,
					req);
			createVehicleResp.setMessage(message);

			return createVehicleResp;
		} else {
			List<APIError> apiErrorList = response.getErrors();
			createVehicleResp = createErrorRespForChangeMultiVeh(apiErrorList,
					req);
			return createVehicleResp;
		}
	}

	private ChangeMultiVehicleDetailsRespDto createErrorRespForChangeMultiVeh(
			List<APIError> apiErrorList, ChangeMultiVehicleRequestDto req) {

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

		Long ewbNo = req.getEwbNo();
		String groupNo = req.getGroupNo();
		String vehicleNo = req.getOldvehicleNo();
		String transDocNo = req.getOldTranNo();

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		EwbMultiVehicleEntity ewbMultiVehicle = eWbrepoMultiVehicleRepo
				.findByEwbNoAndGroupNoAndErrorCodeIsNullAndErrorMessageIsNull(
						ewbNo, Long.valueOf(groupNo));

		Long multiVehicleId = ewbMultiVehicle.getId();

		EwbMultiVehicleDetailsEntity findMultiVehcile = ewbMultiVehicleDetailsRepo
				.findByMultiVehicleIdAndVehicleNumAndTransDocNoAndIsDeleteFalseAndErrorCodeIsNullAndErrorMessageIsNull(
						multiVehicleId, vehicleNo, transDocNo);

		Long vechicleQty = findMultiVehcile.getVehicleQty();

		EwbMultiVehicleDetailsEntity entity = new EwbMultiVehicleDetailsEntity();
		entity.setGroupNo(Long.valueOf(groupNo));
		entity.setMultiVehicleId(multiVehicleId);
		entity.setFromState(String.valueOf(req.getFromState()));
		entity.setFromPlace(req.getFromPlace());
		entity.setReason(req.getReasonCode());
		entity.setRemarks(req.getReasonRem());
		entity.setVehicleNum(req.getNewVehicleNo());
		entity.setOldVehicleNum(req.getOldvehicleNo());
		entity.setTransDocNo(req.getNewTranNo());
		entity.setOldTransDocNum(req.getOldTranNo());
		entity.setVehicleQty(vechicleQty);
		entity.setCreatedDate(LocalDateTime.now());
		entity.setCreatedBy(userName);
		entity.setErrorCode(errorCode);
		entity.setErrorMessage(errorDesc);
		entity.setError(true);
		entity.setFunction("U");
		ewbMultiVehicleDetailsRepo.save(entity);

		ChangeMultiVehicleDetailsRespDto resp = new ChangeMultiVehicleDetailsRespDto();
		resp.setErrorCode(errorCode);
		resp.setErrorMessage(errorDesc);

		return resp;
	}

	public List<EwbMultiVehicleListGroupResp> getGroupNumber(Long eWbno) {

		return ewbMultiVehicleDao.getListOfGroup(eWbno);
	}

	@Override
	public List<EwbMultiVehicleListVehicleResp> listVehicleNumber(Long eWbno,
			String groupNo) {

		return ewbMultiVehicleDao.getListOfVehicleNumber(eWbno, groupNo);
	}

	@Override
	public RejectEwbResponseDto rejectEwb(RejectEwbReqDto rejectreq,
			boolean updateDb, boolean erpPush) {
		APIResponse response;
		RejectEwbResponseDto resp;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Begin  Reject Ewb with requestInfo {} ",
						rejectreq);
			}
			response = rejectEWB.rejectEwb(rejectreq, rejectreq.getGstin());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("End  Reject Ewb ,response = {} ", response);
			}
			resp = createrejectEwbResponse(response);
			if (updateDb)
				ewbDbService.rejectEwbDbUpdate(rejectreq, resp);
		} catch (Exception e) {
			APIError error = new APIError();
			error.setErrorCode(EwbConstants.INTERNAL_ERROR_CODE);
			error.setErrorDesc(e.getMessage());
			response = new APIResponse();
			response.addError(error);
			resp = createrejectEwbResponse(response);
			if (updateDb)
				ewbDbService.rejectEwbDbUpdate(rejectreq, resp);
			// if (erpPush)
			// ewbErpService.updateTransporterErpPush(req, resp);
		}
		return resp;
	}

	private RejectEwbResponseDto createrejectEwbResponse(APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			return gson.fromJson(jsonResp, RejectEwbResponseDto.class);
		} else {
			return rejectcreateErrorResponse(response.getErrors());
		}
	}

	private RejectEwbResponseDto rejectcreateErrorResponse(
			List<APIError> errors) {
		String errorCode = "";
		String errorDesc = "";
		for (int i = 0; i < errors.size(); i++) {
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				errorCode = errorCode + (i + 1) + ") "
						+ errors.get(i).getErrorCode() + " ";
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode()))
				errorDesc = errorDesc + (i + 1) + ") "
						+ errors.get(i).getErrorDesc() + " ";
		}
		RejectEwbResponseDto errResp = new RejectEwbResponseDto();
		errResp.setErrorCode(errorCode);
		errResp.setErrorMessage(errorDesc);
		return errResp;
	}

	@Override
	public GetHsnDetailsResponseDto getHsnDetails(String hsnCode, String gstin,
			boolean updateDb, boolean erpPush) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin  getHsnDetails with requestInfo {} ", hsnCode);
		}
		APIResponse response = getHSNDetails.getHSNDetails(hsnCode, gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End  getHsnDetails ,response = {} ", response);
		}
		return createGetHsnDetailsResponse(response);
	}

	private GetHsnDetailsResponseDto createGetHsnDetailsResponse(
			APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			return gson.fromJson(jsonResp, GetHsnDetailsResponseDto.class);
		} else {
			Pair<String, String> errCodeDesc = EyEwbUtilMethods
					.createErrorResponse(response.getErrors());
			GetHsnDetailsResponseDto resp = new GetHsnDetailsResponseDto();
			resp.setErrorCode(errCodeDesc.getValue0());
			resp.setErrorMessage(errCodeDesc.getValue1());
			return resp;
		}

	}

	@Override
	public GetGSTINResponseDto getGSTINDetails(String getGstin,
			String userGstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin  getGSTINDetails with requestInfo {} ",
					getGstin);
		}
		APIResponse response = getGSTINDetails.getGSTINDetails(getGstin,
				userGstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End  getGSTINDetails ,response = {} ", response);
		}
		return createGetGSTINDetailsResponse(response);
	}

	private GetGSTINResponseDto createGetGSTINDetailsResponse(
			APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			return gson.fromJson(jsonResp, GetGSTINResponseDto.class);
		} else {
			Pair<String, String> errCodeDesc = EyEwbUtilMethods
					.createErrorResponse(response.getErrors());
			GetGSTINResponseDto resp = new GetGSTINResponseDto();
			resp.setErrorCode(errCodeDesc.getValue0());
			resp.setErrorMessage(errCodeDesc.getValue1());
			return resp;
		}

	}

	@Override
	public GetEwayBillGeneratedByConsignerDto getEwayBillGeneratedByConsigner(
			String docType, String docNo, String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(
					"Begin  GetEwayBillGeneratedByConsigner with requestInfo {} ",
					docType, docNo);
		}
		APIResponse response = getEwayBillGeneratedByConsigner
				.getEwayBillGeneratedByConsigner(docType, docNo, gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End  GetEwayBillGeneratedByConsigner ,response = {} ",
					response);
		}
		return createConsignerResponse(response, gstin);
	}

	private GetEwayBillGeneratedByConsignerDto createConsignerResponse(
			APIResponse response, String gstin) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			GetEwayBillGeneratedByConsignerDto dto = gson.fromJson(jsonResp,
					GetEwayBillGeneratedByConsignerDto.class);
			getEwayBillByConsignerAsync(dto, gstin);

			return gson.fromJson(jsonResp,
					GetEwayBillGeneratedByConsignerDto.class);
		} else {
			Pair<String, String> errCodeDesc = EyEwbUtilMethods
					.createErrorResponse(response.getErrors());
			GetEwayBillGeneratedByConsignerDto resp = new GetEwayBillGeneratedByConsignerDto();
			resp.setErrorCode(errCodeDesc.getValue0());
			resp.setErrorMessage(errCodeDesc.getValue1());
			return resp;
		}

	}

	@Override
	public List<GetEwbByDateResponseDto> getEWBByDate(String ewbNo,
			String gstin, boolean updateDb, boolean erpPush) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin  GetEwbDetailsByDate with requestInfo {} ",
					ewbNo);
		}
		APIResponse response = getEWBDetailsByDate.getEWBDetailsByDate(ewbNo,
				gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End  GetEwbDetailsByDate ,response = {} ", response);
		}
		return createGetEwbDetailsByDateResponse(response);
	}

	private List<GetEwbByDateResponseDto> createGetEwbDetailsByDateResponse(
			APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();

			TypeToken<List<GetEwbByDateResponseDto>> token = new TypeToken<List<GetEwbByDateResponseDto>>() {
			};
			List<GetEwbByDateResponseDto> getEwbByDateResponseDto = gson
					.fromJson(jsonResp, token.getType());
			getEWBByDateAsync(getEwbByDateResponseDto);

			return getEwbByDateResponseDto;
		} else {
			Pair<String, String> errCodeDesc = EyEwbUtilMethods
					.createErrorResponse(response.getErrors());
			List<GetEwbByDateResponseDto> resp = new ArrayList<>();

			GetEwbByDateResponseDto getEwbByDateResponseDto = new GetEwbByDateResponseDto();

			getEwbByDateResponseDto.setErrorCode(errCodeDesc.getValue0());
			getEwbByDateResponseDto.setErrorMessage(errCodeDesc.getValue1());

			resp.add(getEwbByDateResponseDto);

			return resp;
		}

	}

	public void getEWBByDateAsync(
			List<GetEwbByDateResponseDto> getEwbByDateResponseDto) {

		String groupCode = TenantContext.getTenantId();

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		List<GetCounterPartyMsg> msgList = getEwbByDateResponseDto.stream()
				.map(o -> convertToMsg(o)).collect(Collectors.toList());
		JsonObject jobParamsObj = new JsonObject();

		JsonElement element = gson.toJsonTree(msgList,
				new TypeToken<List<GetCounterPartyMsg>>() {
				}.getType());
		jobParamsObj.add("ewbdetails", element);

		asyncJobsService.createJob(groupCode, "GETEWB", jobParamsObj.toString(),
				"SYSTEM", 1L, null, null);

	}

	private GetCounterPartyMsg convertToMsg(GetEwbByDateResponseDto o) {

		GetCounterPartyMsg obj = new GetCounterPartyMsg();
		obj.setEwbNo(o.getEwbNo());
		obj.setGstin(o.getGenGstin());

		return obj;
	}

	public void getEwayBillByConsignerAsync(
			GetEwayBillGeneratedByConsignerDto getEwayBillGeneratedByConsignerDto,
			String gstin) {

		String groupCode = TenantContext.getTenantId();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject jobParamsObj = new JsonObject();
		GetCounterPartyMsg obj = new GetCounterPartyMsg();
		obj.setEwbNo(getEwayBillGeneratedByConsignerDto.getEwayBillNo());
		obj.setGstin(gstin);
		List<GetCounterPartyMsg> msgList = Arrays.asList(obj);
		JsonElement element = gson.toJsonTree(msgList,
				new TypeToken<List<GetCounterPartyMsg>>() {
				}.getType());
		jobParamsObj.add("ewbdetails", element);

		asyncJobsService.createJob(groupCode, "GETEWB", jobParamsObj.toString(),
				"SYSTEM", 1L, null, null);
	}

	@Override
	public List<EwbMultiVehicleDetailsRespDto> getMultiVehicleDetails(
			Long eWbno, String docNo, String supplierGstin) {

		return ewbMultiVehicleDao.getMultiVehicleDetails(eWbno, docNo,
				supplierGstin);
	}

	@Override
	public EwbMultiVehicleGetVehicleRespDto getVehiclesforGroup(Long eWbno,
			String groupNo) {

		return ewbMultiVehicleDao.getVehiclesforGroup(eWbno, groupNo);
	}

	@Override
	public EwbMultiVehicleGetFromAndToDto getEwbFromAndToData(Long ewbNo) {

		EwbMultiVehicleGetFromAndToDto dto = new EwbMultiVehicleGetFromAndToDto();

		OutwardTransDocument doc = null;

		try {

			List<EwbMultiVehicleEntity> ewbMultiVehicle = eWbrepoMultiVehicleRepo
					.findByEwbNoOrderByIdDesc(ewbNo);

			LOGGER.debug(
					"List Multi Vehicle Data from EwbMultiVehicleEntity for "
							+ "ewbNo: {}, and list: {} ",
					ewbNo, ewbMultiVehicle);

			String ewbNumber = String.valueOf(ewbNo);

			Long docHeaderId = ewbRepository.findDocIdByEwbNum(ewbNumber);

			LOGGER.debug(
					"Doc Header Id for EwbServiceImpl.getEwbFromAndToData() {} ",
					docHeaderId);

			Optional<OutwardTransDocument> transDocuments = outwardTransDocRepo
					.findById(docHeaderId);

			List<String> functionName = new ArrayList<>();
			functionName.add("VEHEWB");
			functionName.add("GENEWAYBILL");

			List<EwbLifecycleEntity> ewbLifeCycle = ewbLifeCycleRepo
					.findByEwbNumAndFunctionInAndIsActiveTrueAndFunctionStatusFalseOrderByIdDesc(
							ewbNumber, functionName);

			LOGGER.debug(
					"List EWB Life Cycle Data  EwbServiceImpl.getEwbFromAndToData()"
							+ " ewbNo = {}, and findByEwbNumAndFunctionOrderByIdDesc() = {}",
					ewbNo, ewbLifeCycle);

			if (!ewbMultiVehicle.isEmpty() && ewbMultiVehicle != null) {

				EwbMultiVehicleEntity ewb = ewbMultiVehicle.get(0);
				dto.setFromPlace(ewb.getFromPlace());
				dto.setFromState(ewb.getFromState());
				dto.setToPlace(ewb.getToPlace());
				dto.setToState(ewb.getToState());

				LOGGER.debug(
						"EWB Multi Vehicle list From state, To State, From "
								+ "Place, To Place Response dto = {}",
						dto);

			} else if (!ewbLifeCycle.isEmpty() && ewbLifeCycle != null) {

				EwbLifecycleEntity ewbLCycle = ewbLifeCycle.get(0);

				dto.setFromPlace(ewbLCycle.getFromPlace());
				dto.setFromState(ewbLCycle.getFromState());
				if (transDocuments.isPresent()) {
					doc = transDocuments.get();

					dto.setToPlace(EyEwbCommonUtil.getToPlace(
							doc.getShipToLocation(),
							doc.getCustOrSuppAddress4(), doc.getDocCategory()));

					dto.setToState(doc.getBillToState());
				}

				LOGGER.debug(
						"EWB Multi Vehicle list From state, To State, From "
								+ "Place, To Place Response dto = {}",
						dto);
			}

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "EwbServiceImpl.getEwbFromAndToData");
		}

		return dto;
	}
	
	
	private void extractAndPopulateNicDistance(EwbResponseDto resp) {
		String alertMsg = resp.getAlert();
		if (!Strings.isNullOrEmpty(alertMsg)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Received alert in response {} for group {}",
						alertMsg, TenantContext.getTenantId());
			String distanceStr = alertMsg.replaceAll("[^0-9]", "");
			if (!Strings.isNullOrEmpty(distanceStr)) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(
							"Extracted and populating distance {} for group {}",
							distanceStr, TenantContext.getTenantId());
				Integer nicDistance = Integer.valueOf(distanceStr);
				resp.setNicDistance(nicDistance);
			}
		}
	}

}
