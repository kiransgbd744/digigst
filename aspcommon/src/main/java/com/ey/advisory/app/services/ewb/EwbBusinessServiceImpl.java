/**
 * 
 */
package com.ey.advisory.app.services.ewb;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.business.dto.OutwardTransDocument;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.einv.common.BusinessStatisticsLogHelper;
import com.ey.advisory.ewb.api.AddMultiVehicleDetails;
import com.ey.advisory.ewb.api.CancelEWB;
import com.ey.advisory.ewb.api.ChangeMultiVehicleDetails;
import com.ey.advisory.ewb.api.EWBUpdatePartB;
import com.ey.advisory.ewb.api.ExtendEWB;
import com.ey.advisory.ewb.api.GenerateConsolidatedEWB;
import com.ey.advisory.ewb.api.GenerateEwayBill;
import com.ey.advisory.ewb.api.GetEWBDetails;
import com.ey.advisory.ewb.api.GetEwbByConsigner;
import com.ey.advisory.ewb.api.InitiateMultiVehicle;
import com.ey.advisory.ewb.api.RejectEWB;
import com.ey.advisory.ewb.api.UpdateEWBTransporter;
import com.ey.advisory.ewb.app.api.APIError;
import com.ey.advisory.ewb.app.api.APIExecutor;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.common.EyEwbCommonUtil;
import com.ey.advisory.ewb.common.EyEwbUtilMethods;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsReqDto;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.ey.advisory.ewb.dto.CancelEwbRequestListDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleDetaiilsReqDto;
import com.ey.advisory.ewb.dto.ChangeMultiVehicleDetailsRespDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;
import com.ey.advisory.ewb.dto.ConsolidateEWBResponseDto;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;
import com.ey.advisory.ewb.dto.ExtendEWBResponseDto;
import com.ey.advisory.ewb.dto.GetEwbResponseDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleReqDto;
import com.ey.advisory.ewb.dto.InitiateMultiVehicleRespDto;
import com.ey.advisory.ewb.dto.RejectEwbRequestparamDto;
import com.ey.advisory.ewb.dto.RejectEwbResponseDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterReqDto;
import com.ey.advisory.ewb.dto.UpdateEWBTransporterRespDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbRequestDto;
import com.ey.advisory.ewb.dto.UpdatePartBEwbResponseDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("EwbBusinessServiceImpl")
@Slf4j
public class EwbBusinessServiceImpl implements EwbBusinessService {

	@Autowired
	@Qualifier("DefaultEWBAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	private EwbBusinessRequestConverter ewbRequestConverter;

	@Autowired
	@Qualifier("GenerateEwayBillImpl")
	private GenerateEwayBill generateEwayBill;

	@Autowired
	@Qualifier("RejectEWBImpl")
	private RejectEWB rejectEWB;

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
	@Qualifier("GetEWBDetailsImpl")
	private GetEWBDetails getEWBDetails;

	@Autowired
	@Qualifier("UpdateEWBTransporterImpl")
	private UpdateEWBTransporter updateTransporter;

	@Autowired
	@Qualifier("AddMultiVehicleDetailsImpl")
	private AddMultiVehicleDetails addMultiVeh;

	@Autowired
	@Qualifier("InitiateMultiVehicleImpl")
	private InitiateMultiVehicle intiateMultiVeh;

	@Autowired
	@Qualifier("ChangeMultiVehicleDetailsImpl")
	private ChangeMultiVehicleDetails changeMultiVeh;

	@Autowired
	@Qualifier("GetEwbByConsignerImpl")
	private GetEwbByConsigner getEwbByConsigner;

	@Autowired
	BusinessStatisticsLogHelper businessStatisticsLogHelper;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Override
	public UpdatePartBEwbResponseDto updateEwbPartB(
			UpdatePartBEwbRequestDto req, String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin  updateEwbPartB with requestInfo {} ", req);
		}
		APIResponse response = ewbUpdatePartB.updateEwbPartB(req, gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End  updateEwbPartB ,response = {} ", response);
		}
		return createUpdateEwbResponse(response);
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
	public CancelEwbResponseDto cancelEwb(CancelEwbRequestListDto req) {
		List<CancelEwbReqDto> reqList = req.getCancelEwbReqDtoList();
		List<CancelEwbResponseDto> resp = new ArrayList<>();
		if (!reqList.isEmpty()) {
			reqList.forEach(o -> resp.add(createCancelEwbResponse(
					cancelEWB.cancelEwb(o, o.getGstin()), o.getEwbNo(),
					o.getGstin())));
		}
		return resp.get(0);
	}

	private CancelEwbResponseDto createCancelEwbResponse(APIResponse response,
			String ewbNo, String gstin) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			CancelEwbResponseDto cancelResp = gson.fromJson(jsonResp,
					CancelEwbResponseDto.class);
			reqLogHelper.logAppMessage(null, null, cancelResp.getEwayBillNo(),
					"Success Response from NIC for Cancel EWB");
			businessStatisticsLogHelper.logBusinessApiResponse(null, null, null,
					null, null, null, gstin, null, cancelResp.getEwayBillNo(),
					null, null, cancelResp.getCancelDate(), null,
					APIIdentifiers.CANCEL_EWB, false, null, null);

			return cancelResp;
		} else {
			CancelEwbResponseDto resp = createCancelErrorResponse(
					response.getErrors());
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
	public EwbResponseDto generateEwb(OutwardTransDocument hdr) {
		String gstin;
		if ("O".equalsIgnoreCase(hdr.getTransactionType()))
			gstin = hdr.getSgstin();
		else
			gstin = hdr.getCgstin();

		EwayBillRequestDto ewayRequestDto = ewbRequestConverter.convert(hdr);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("The OutwardTransDocument is converted to "
					+ "the EinvoiceRequestDto : {}", ewayRequestDto);
		}
		APIResponse response = generateEwayBill.generateEwbill(ewayRequestDto,
				gstin);
		EwbResponseDto resp = createGenerateEwbResponse(response);
		extractAndPopulateNicDistance(ewayRequestDto, resp);
		return resp;
	}

	private EwbResponseDto createGenerateEwbResponse(APIResponse response) {

		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			EwbResponseDto resp = gson.fromJson(jsonResp, EwbResponseDto.class);
			reqLogHelper.logAppMessage(null, null, resp.getEwayBillNo(),
					"Success Response from NIC for Generate EWB");
			businessStatisticsLogHelper.logBusinessApiResponse(null, null, null,
					null, null, null, null, null, resp.getEwayBillNo(),
					resp.getEwayBillDate(), resp.getValidUpto(), null,
					resp.getNicDistance(), APIIdentifiers.GENERATE_EWB, false,
					null, null);
			return gson.fromJson(jsonResp, EwbResponseDto.class);
		} else {
			return createGenerateEwbErrorResponse(response.getErrors());
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
			String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin  updateEwbPartB with requestInfo {} ", req);
		}
		APIResponse response = genConsolidatedEwb.consolidateEWB(req, gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End  updateEwbPartB ,response = {} ", response);
		}
		return createGenConsolidatedEWBResponse(response);
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
	public ExtendEWBResponseDto extendEWB(ExtendEWBReqDto req, String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin  updateEwbPartB with requestInfo {} ", req);
		}
		APIResponse response = extendEwb.extendEwb(req, gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End  updateEwbPartB ,response = {} ", response);
		}
		ExtendEWBResponseDto resp = createExtendResponse(response);
		resp.setNicDistance(req.getRemainingDistance());
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
	public GetEwbResponseDto getEWB(String ewbNo, String gstin) {
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
			UpdateEWBTransporterReqDto req, String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin  updateTransporter with requestInfo {} ", req);
		}
		APIResponse response = updateTransporter.updateEWBTransporter(req,
				gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End  updateTransporter ,response = {} ", response);
		}
		return createUpdateTransporterResponse(response);

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
	public AddMultiVehicleDetailsRespDto addMultiVehicles(
			AddMultiVehicleDetailsReqDto req, String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin   add multivehicle with requestInfo {} ", req);
		}
		APIResponse response = addMultiVeh.addMultiVehicle(req, gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End   add multivehicle ,response = {} ", response);
		}
		return createAddMultivehicleResponse(response);
	}

	private AddMultiVehicleDetailsRespDto createAddMultivehicleResponse(
			APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			return gson.fromJson(jsonResp, AddMultiVehicleDetailsRespDto.class);
		} else {
			Pair<String, String> errCodeDesc = EyEwbUtilMethods
					.createErrorResponse(response.getErrors());
			AddMultiVehicleDetailsRespDto resp = new AddMultiVehicleDetailsRespDto();
			resp.setErrorCode(errCodeDesc.getValue0());
			resp.setErrorMessage(errCodeDesc.getValue1());
			return resp;
		}
	}

	@Override
	public InitiateMultiVehicleRespDto initiateMultiVehicles(
			InitiateMultiVehicleReqDto req, String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin   initiate multivehicle with requestInfo {} ",
					req);
		}
		APIResponse response = intiateMultiVeh.initiateMultiVehicle(req, gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End   initiate multivehicle ,response = {} ",
					response);
		}
		return createInitiateMultivehicleResponse(response);
	}

	private InitiateMultiVehicleRespDto createInitiateMultivehicleResponse(
			APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			return gson.fromJson(jsonResp, InitiateMultiVehicleRespDto.class);
		} else {
			Pair<String, String> errCodeDesc = EyEwbUtilMethods
					.createErrorResponse(response.getErrors());
			InitiateMultiVehicleRespDto resp = new InitiateMultiVehicleRespDto();
			resp.setErrorCode(errCodeDesc.getValue0());
			resp.setErrorMessage(errCodeDesc.getValue1());
			return resp;
		}
	}

	@Override
	public ChangeMultiVehicleDetailsRespDto changeMultiVehicles(
			ChangeMultiVehicleDetaiilsReqDto req, String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin   change multivehicle with requestInfo {} ",
					req);
		}
		APIResponse response = changeMultiVeh.changeMultiVehicle(req, gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End   change multivehicle ,response = {} ", response);
		}
		return createChangeMultivehicleResponse(response);
	}

	private ChangeMultiVehicleDetailsRespDto createChangeMultivehicleResponse(
			APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			return gson.fromJson(jsonResp,
					ChangeMultiVehicleDetailsRespDto.class);
		} else {
			Pair<String, String> errCodeDesc = EyEwbUtilMethods
					.createErrorResponse(response.getErrors());
			ChangeMultiVehicleDetailsRespDto resp = new ChangeMultiVehicleDetailsRespDto();
			resp.setErrorCode(errCodeDesc.getValue0());
			resp.setErrorMessage(errCodeDesc.getValue1());
			return resp;
		}
	}

	@Override
	public RejectEwbResponseDto rejectEwb(RejectEwbRequestparamDto rejectreq) {

		APIResponse response = rejectEWB.rejectEwb(
				rejectreq.getRejectEwbReqDto(),
				rejectreq.getRejectEwbReqDto().getGstin());
		return createrejectEwbResponse(response);
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

	private void extractAndPopulateNicDistance(
			EwayBillRequestDto ewayRequestDto, EwbResponseDto resp) {
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
			} else {
				resp.setNicDistance(ewayRequestDto.getTransDistance());
			}
		} else {
			resp.setNicDistance(ewayRequestDto.getTransDistance());
		}
	}

	@Override
	public GetEwbResponseDto getEwbByConsigner(String gstin, String docType,
			String docNo) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside getEwbByConsigner");
		}
		docType = EyEwbCommonUtil.getDocType(docType);
		APIResponse response = getEwbByConsigner.getEwbByConsigner(gstin,
				docType, docNo);
		GetEwbResponseDto resp = createGenerateEwbByConsignerResponse(gstin,
				response);
		return resp;
	}

	private GetEwbResponseDto createGenerateEwbByConsignerResponse(String gstin,
			APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			EwbResponseDto resp = gson.fromJson(jsonResp, EwbResponseDto.class);
			businessStatisticsLogHelper.logBusinessApiResponse(null, null, null,
					null, null, null, null, null, resp.getEwayBillNo(),
					resp.getEwayBillDate(), resp.getValidUpto(), null, null,
					"GETEWBBYCONSIGNER", false, null, null);
			reqLogHelper.logAppMessage(null, null, resp.getEwayBillNo(),
					"Success Response from NIC for Generate EWB");

			GetEwbResponseDto getEwbResp = getEWB(resp.getEwayBillNo(), gstin);

			return getEwbResp;

		} else {

			Pair<String, String> errCodeDesc = EyEwbUtilMethods
					.createErrorResponse(response.getErrors());
			GetEwbResponseDto resp = new GetEwbResponseDto();
			resp.setErrorCode(errCodeDesc.getValue0());
			resp.setErrorMessage(errCodeDesc.getValue1());
			return resp;
		}
	}

}
