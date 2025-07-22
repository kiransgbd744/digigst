/**
 * 
 */
package com.ey.advisory.app.inward.einvoice;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.GetIrnDetailErrorEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GetIrnDetailPayloadEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GetIrnListEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnDtlErrorPayloadRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnDtlPayloadRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnListingRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("GetInwardIrnDetailSectionHandler")
@Slf4j
public class GetInwardIrnDetailSectionHandler {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	private GetIrnDtlPayloadRepository getIrnDtlPayloadRepo;

	@Autowired
	private GetIrnDtlErrorPayloadRepository getIrnDtlErrorPayloadRepo;

	@Autowired
	@Qualifier("InwardGetIrnDetailsDataParserImpl")
	private InwardGetIrnDetailsDataParser irnDtlParser;

	@Autowired
	private GetIrnListingRepository getIrnListingRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


	public void getIrnDtl(Gstr1GetInvoicesReqDto dto, String groupCode) {

		try {
			Gson gson = new Gson();

			APIResponse apiResponse = getIrnDtlResp(dto.getIrn(), groupCode,
			        dto.getGstin(), dto.getReturnPeriod());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
				        "Get IRN Detail apiResponse {} "
				                + "groupcode {} for params {}",
				        apiResponse.toString(), groupCode, dto);
			}

			if (apiResponse.isSuccess()) {

				String apiResp = apiResponse.getResponse();

				JsonObject reqObj = JsonParser.parseString(apiResp)
				        .getAsJsonObject().get("data").getAsJsonObject();

				GetIrnDtlsRespDto respDto = gson.fromJson(reqObj,
				        GetIrnDtlsRespDto.class);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
					        "Get IRN Detail Success block for irn {} and started parsing ",
					        dto.getIrn());
				}

				String response = irnDtlParser.parseHeaderAndLineIrnDtls(
				        reqObj.get("SignedInvoice").getAsString(), dto, respDto,
				        groupCode, reqObj.get("SignedQRCode").getAsString());

				if ("SUCCESS".equalsIgnoreCase(response)) {
					getIrnListingRepo.updateGetDtlSts(dto.getIrn(), "SUCCESS",
					        dto.getIrnSts());
				}

				GetIrnDetailPayloadEntity succEntity = new GetIrnDetailPayloadEntity();

				Clob responseClob = GenUtil
				        .convertStringToClob(reqObj.toString());
				succEntity.setPayload(responseClob);
				succEntity.setBtchId(dto.getBatchId());
				succEntity.setIrn(dto.getIrn());
				succEntity.setIrnSts(dto.getIrnSts());
				succEntity.setCreatedOn(LocalDateTime.now());
				getIrnDtlPayloadRepo.save(succEntity);

				if ("CNL".equalsIgnoreCase(dto.getIrnSts())) {
					getIrnListingRepo.updateIsDeleteFlag(dto.getIrn());
				}

				return;
			} else {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Get IRN Detail failure  block for irn {} ",
					        dto.getIrn());
				}

				String error = apiResponse.getError().getErrorCode()
				        + apiResponse.getError().getErrorDesc();
				GetIrnDetailErrorEntity errorEntity = new GetIrnDetailErrorEntity();

				Clob responseClob = GenUtil.convertStringToClob(error);
				errorEntity.setErrPayload(responseClob);
				errorEntity.setBtchId(dto.getBatchId());
				errorEntity.setIrn(dto.getIrn());
				errorEntity.setIrnSts(dto.getIrnSts());
				errorEntity.setCreatedOn(LocalDateTime.now());
				getIrnDtlErrorPayloadRepo.save(errorEntity);

				getIrnListingRepo.updateGetDtlSts(dto.getIrn(), "FAILED",
				        dto.getIrnSts());
			}

		} catch (Exception ex) {
			LOGGER.error(ex.getLocalizedMessage());

			GetIrnDetailErrorEntity errorEntity = new GetIrnDetailErrorEntity();

			Clob responseClob = GenUtil.convertStringToClob(
			        "Error occured while handling the request");
			errorEntity.setErrPayload(responseClob);
			errorEntity.setBtchId(dto.getBatchId());
			errorEntity.setIrn(dto.getIrn());
			errorEntity.setIrnSts(dto.getIrnSts());
			errorEntity.setCreatedOn(LocalDateTime.now());
			getIrnDtlErrorPayloadRepo.save(errorEntity);

			getIrnListingRepo.updateGetDtlSts(dto.getIrn(), "FAILED",
			        dto.getIrnSts());
			/*
			 * getIrnListingRepo.updateGetDtlSts(dto.getIrn(), "Failed",
			 * dto.getIrnSts());
			 */
			throw new AppException(ex);

		}

	}

	public APIResponse getIrnDtlResp(String irn, String groupCode, String gstin,
	        String returnPrd) {
		APIResponse resp = new APIResponse();
		APIParam param1 = new APIParam("irn", irn);
		APIParam param2 = new APIParam("gstin", gstin);
		APIParam param3 = new APIParam("ret_period", returnPrd);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
		        APIIdentifiers.GET_IRN_DTL, param1, param2, param3);
		resp = apiExecutor.execute(params, null);
		return resp;
	}

	public void getIrnJsonDtl(List<String> irnList,
	        List<Gstr1GetInvoicesReqDto> dtoList, String groupCode,
	        Long batchId) {
		try {
			callingParseHeaderAndLineIrnDtls(irnList, dtoList, groupCode,
			        batchId);
		} catch (Exception ex) {
			LOGGER.error(ex.getLocalizedMessage());
			throw new AppException(ex);
		}
	}

	public void callingParseHeaderAndLineIrnDtls(List<String> irnList,
	        List<Gstr1GetInvoicesReqDto> dtoList, String groupCode,
	        Long batchId) {

		for (Gstr1GetInvoicesReqDto irnDto : dtoList) {
			try {
				GetIrnListEntity irnEntity = getIrnListingRepo
				        .findAllByIrnListAndBatchId(irnDto.getIrn(), batchId,
				                irnDto.getIrnSts());
				GetIrnDtlsRespDto irnDtldto = new GetIrnDtlsRespDto();
				irnDtldto.setIrnSts(irnEntity.getIrnSts());

				irnDtldto
				        .setCnlDt(
				                irnEntity.getCanDate() != null ? (LocalDateTime
				                        .parse(irnEntity.getCanDate()
				                                .toString(), formatter)
				                        .toString()) : null);
				irnDtldto.setCnlRsn(irnEntity.getCancellationReason());
				irnDtldto.setCnlRem(irnEntity.getCancellationRem());
				irnDtldto.setEwbNo(!Strings.isNullOrEmpty(irnEntity.getEwbNum())
				        ? Long.valueOf(irnEntity.getEwbNum()) : null);
				irnDtldto
				        .setEwbDt(
				                irnEntity.getEwbDate() != null ? (LocalDateTime
				                        .parse(irnEntity.getEwbDate()
				                                .toString(), formatter)
				                        .toString()) : null);
				irnDtldto
				        .setEwbVald(irnEntity.getEwbValidTill() != null
				                ? (LocalDateTime
				                        .parse(irnEntity.getEwbValidTill()
				                                .toString(), formatter)
				                        .toString())
				                : null);

				if(!Strings.isNullOrEmpty(irnEntity.getSignedInvoice()))
						{
				String response = irnDtlParser.parseHeaderAndLineIrnDtls(
				        irnEntity.getSignedInvoice(), irnDto, irnDtldto,
				        groupCode, irnEntity.getSignedQRCode());

				if ("SUCCESS".equalsIgnoreCase(response)) {
					getIrnListingRepo.updateGetDtlSts(irnDto.getIrn(),
					        "SUCCESS", irnDto.getIrnSts());
				}
						}
				
				if ("CNL".equalsIgnoreCase(irnDto.getIrnSts())) {
					getIrnListingRepo.updateIsDeleteFlag(irnDto.getIrn());
				}
			} catch (Exception ex) {
				LOGGER.error(
				        " Exception in parsing the details for irn {}  {} ",
				        irnDto.getIrn(), ex);

				GetIrnDetailErrorEntity errorEntity = new GetIrnDetailErrorEntity();

				Clob responseClob = GenUtil.convertStringToClob(
				        "Error occured while handling the request");
				errorEntity.setErrPayload(responseClob);
				errorEntity.setBtchId(irnDto.getBatchId());
				errorEntity.setIrn(irnDto.getIrn());
				errorEntity.setIrnSts(irnDto.getIrnSts());
				errorEntity.setCreatedOn(LocalDateTime.now());
				getIrnDtlErrorPayloadRepo.save(errorEntity);

				getIrnListingRepo.updateGetDtlSts(irnDto.getIrn(), "FAILED",
				        irnDto.getIrnSts());

			}
		}
	}
}