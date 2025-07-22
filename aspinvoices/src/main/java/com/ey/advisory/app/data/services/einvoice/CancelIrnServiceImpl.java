package com.ey.advisory.app.data.services.einvoice;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EinvoiceRepository;
import com.ey.advisory.app.data.services.ewb.EwbService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AspInvoiceStatus;
import com.ey.advisory.common.EInvoiceStatus;
import com.ey.advisory.common.EwbStatus;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.IrnStatusMaster;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.einv.api.CancelIrn;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIExecutor;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.dto.CancelIrnERPResponseDto;
import com.ey.advisory.einv.dto.CancelIrnNICResponseDto;
import com.ey.advisory.einv.dto.CancelIrnReqDto;
import com.ey.advisory.ewb.common.EwbConstants;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun K.A
 *
 */
@Slf4j
@Component("CancelIrnServiceImpl")
public class CancelIrnServiceImpl implements CancelIrnService {

	@Autowired
	@Qualifier("DefaultEINVNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("CancelIrnImpl")
	private CancelIrn cancelIrn;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docrepo;

	@Autowired
	@Qualifier("EinvoiceRepository")
	EinvoiceRepository einvoiceRepo;

	@Autowired
	@Qualifier("EwbServiceImpl")
	EwbService ewbService;
	
	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Override
	public CancelIrnERPResponseDto cancelEinvRequest(CancelIrnReqDto req) {
		CancelIrnERPResponseDto resp = processResponse(cancelIrn.cancelIrn(req),
				req.getDocHeaderId(),req);
		return resp;
	}

	private CancelIrnERPResponseDto processResponse(APIResponse response,
			Long id,CancelIrnReqDto req) {

		CancelIrnERPResponseDto cancelResp = null;
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();
			CancelIrnNICResponseDto cancelNicResp = gson.fromJson(jsonResp,
					CancelIrnNICResponseDto.class);
			cancelResp = convertNICtoERP(cancelNicResp);
			reqLogHelper.updateResponsePayload(response.getResponse(), true);
			
			Optional<OutwardTransDocument> doc = docrepo
					.findById(id);
			String supplyType = doc.get().getSupplyType();

			if (!"CAN".equalsIgnoreCase(supplyType)) {
				docrepo.updateEInvStatusByIrnAndDelete(id,
					IrnStatusMaster.CANCELLED.getIrnStatusMaster(), 
					EInvoiceStatus.IRN_CANCELLED.geteInvoiceStatusCode());
			}else{
				docrepo.updateEInvStatusByIrn(id,
						IrnStatusMaster.CANCELLED.getIrnStatusMaster(),
						EInvoiceStatus.IRN_CANCELLED.geteInvoiceStatusCode());
			}
			einvoiceRepo.updateEinvCanStatusByIrn(cancelResp.getIrn(), true,
					cancelResp.getCancelDate(),
					req.getCnlRsn(),
					req.getCnlRem());
		} else {
			List<APIError> apiErrorList = response.getErrors();
			cancelResp = createErrorResponse(apiErrorList);
			reqLogHelper.updateResponsePayload(response.getErrors().toString(), false);
			docrepo.updateCnlEinvoiceError(id,
					response.getError().getErrorDesc(),
					response.getError().getErrorCode(),
					AspInvoiceStatus.ASP_ERROR
					.getAspInvoiceStatusCode(),
					EInvoiceStatus.ERROR_CANCELLATION.geteInvoiceStatusCode());
		}
		return cancelResp;
	}

	private CancelIrnERPResponseDto createErrorResponse(
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
		CancelIrnERPResponseDto resp = new CancelIrnERPResponseDto();
		resp.setErrorCode(errorCode);
		resp.setErrorMessage(errorDesc);
		return resp;
	}

	private CancelIrnERPResponseDto convertNICtoERP(
			CancelIrnNICResponseDto response) {
		CancelIrnERPResponseDto erpReqDto = new CancelIrnERPResponseDto();
		erpReqDto.setCancelDate(response.getCancelDate());
		erpReqDto.setIrn(response.getIrn());
		erpReqDto.setErrorCode(response.getErrorCode());
		erpReqDto.setErrorMessage(response.getErrorMessage());
		return erpReqDto;
	}

	@Override
	public CancelIrnERPResponseDto CancelEinvEwbRequest(CancelIrnReqDto req) {

		OutwardTransDocument doc = null;
		CancelIrnERPResponseDto resp = new CancelIrnERPResponseDto();
		try {

			Long id = req.getDocHeaderId();

			LOGGER.debug("generateIrn Method Begin ,fetching the"
						+ "doc header details from db for id " + id);

			Optional<OutwardTransDocument> transDocuments = docrepo
					.findById(id);
			if (!transDocuments.isPresent()) {
					LOGGER.error("generateIrn  ,no invoice available");
				throw new AppException("No Invoice Found in the System");
			}
			doc = transDocuments.get();
				
			LOGGER.debug("generateIrn  ,convert the doc header detail "
						+ "into einvoice request dto");
			// check for ewb and call ewb calcel

			CancelEwbResponseDto ewbResponse = new CancelEwbResponseDto();
			if (EwbStatus.EWB_ACTIVE.getEwbStatusCode() == doc.getEwbStatus()
					|| EwbStatus.PARTA_GENERATED.getEwbStatusCode() == doc
							.getEwbStatus()) {
				CancelEwbReqDto ewbCanReq = new CancelEwbReqDto();
				ewbCanReq.setGstin(doc.getSgstin());
				ewbCanReq.setDocHeaderId(id);
				ewbCanReq.setEwbNo(String.valueOf(doc.getEwbNoresp()));
				ewbCanReq.setCancelRsnCode(req.getCnlRsn());
				ewbCanReq.setCancelRmrk(req.getCnlRem());
				ewbResponse = ewbService.cancelEwb(ewbCanReq, true, false);
			}

			if (Strings.isNullOrEmpty(ewbResponse.getErrorCode())) {
				req.setIrn(doc.getIrnResponse());
				req.setGstin(doc.getSgstin());
				req.setCnlRem(req.getCnlRem());
				req.setCnlRsn(req.getCnlRsn());
				resp = cancelEinvRequest(req);
			} else {
				resp.setErrorMessage(ewbResponse.getErrorMessage());
				resp.setErrorCode(ewbResponse.getErrorCode());
			}

		} catch (Exception ex) {
			
			docrepo.updateCnlEinvoiceError(req.getDocHeaderId(),
					ex.getMessage(),
					EwbConstants.INTERNAL_ERROR_CODE,
					AspInvoiceStatus.ASP_ERROR
					.getAspInvoiceStatusCode(),
					EInvoiceStatus.ERROR_CANCELLATION.geteInvoiceStatusCode());

			LOGGER.error("Exception occured while cancelling einvoice or ewb",
					ex);
			throw new AppException(ex.getMessage());
		}

		return resp;
	}
}
