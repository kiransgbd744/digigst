/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GstnSubmitEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.docs.dto.Gstr1SubmitGstnDto;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr1SubmitToGstnJobHandler")
public class Gstr1SubmitToGstnJobHandler {

	@Autowired
	private GstnSubmitRepository gstr1SubmitRepo;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer hitGstnServer;

	@Autowired
	private DocRepository docRepo;

	public APIResponse submitGstinAndPeriod(String jsonString,
			String groupCode, String userName) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside submitGstinAndPeriod method");
		}
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		// JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		APIResponse resp = null;
		GstnSubmitEntity entity = null;
		try {

			Gstr1SubmitGstnDto dto = gson.fromJson(requestObject,
					Gstr1SubmitGstnDto.class);
			String gstin = dto.getGstin();
			String ret_period = dto.getRet_period();

			TenantContext.setTenantId(groupCode);
			Optional<GstnSubmitEntity> saveEntity = gstr1SubmitRepo
					.findById(dto.getBatchId());

			if (saveEntity.isPresent()) {

				entity = saveEntity(saveEntity.get(), groupCode, userName);
			} else {
				LOGGER.error(
						"No Valid Request found in the the GSTR1_SUBMIT job.");
				return null;
			}

			/*
			 * GstnSubmitDto submit = new GstnSubmitDto(); submit.setData(dto);
			 */
			// Batch setting to NULL to avoid this value in Submit Json.
			dto.setBatchId(null);
			String data = gson.toJson(dto, Gstr1SubmitGstnDto.class);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GSTR1_SUBMIT Gstn Call initiated");
			}
			resp = hitGstnServer.gstr1SubmitApiCall(groupCode, data, gstin,
					ret_period);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GSTR1_SUBMIT Gstn Call Completed");
			}

			Long id = entity.getId();
			updateRefIdAndTxnId(gstin, ret_period, groupCode, id, resp, userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GSTR1_SUBMIT Gstn Call status updated in table.");
			}
		} catch (Exception e) {
			String msg = "Unexpected Error while doing SubmitToGStn";
			e.printStackTrace();
			TenantContext.setTenantId(groupCode);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			entity.setModifiedOn(now);
			entity.setErrorDesc("Failed in Catch block");
			entity.setModifiedBy(userName);
			entity.setStatus(APIConstants.SUBMIT_FAILED);
			gstr1SubmitRepo.save(entity);
			throw new AppException(msg, e);
		}
		return resp;

	}

	private String updateRefIdAndTxnId(String gstin, String ret_period,
			String groupCode, Long id, APIResponse resp, String userName) {
		String refId = null;
		LocalDate modifiedDate = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDate.now());
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		if (groupCode != null && resp != null) {
			if (resp.isSuccess()) {

				String saveJsonResp = resp.getResponse();
				String txnId = resp.getTxnId();
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonObject = (JsonObject) jsonParser
						.parse(saveJsonResp);
				if (jsonObject.get(APIIdentifiers.REF_ID) != null) {
					refId = jsonObject.get(APIIdentifiers.REF_ID).getAsString();
				} else if (jsonObject
						.get(APIIdentifiers.REFERENECE_ID) != null) {
					refId = jsonObject.get(APIIdentifiers.REFERENECE_ID)
							.getAsString();
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("GSTR1_SUBMIT ref_id: {} and txn_id: {} ",
							refId, txnId);
				}

				if (refId != null && !refId.isEmpty()) {

					TenantContext.setTenantId(groupCode);
					Optional<GstnSubmitEntity> OpEntity = gstr1SubmitRepo
							.findById(id);

					if (OpEntity.isPresent()) {

						GstnSubmitEntity entity = OpEntity.get();
						entity.setRefId(refId);
						entity.setTxnId(txnId);
						entity.setModifiedOn(now);
						entity.setModifiedBy(userName);
						entity.setStatus(APIConstants.SUBMIT_COMPLETED);
						gstr1SubmitRepo.save(entity);
					}
				}

			} else {
				String errorCode = resp.getError().getErrorCode();
				String errorDesc = resp.getError().getErrorDesc();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Error thrown by Gstn ErrorCode: {} and "
							+ "ErrorDesc: {}", errorCode, errorDesc);
				}

				TenantContext.setTenantId(groupCode);
				Optional<GstnSubmitEntity> OpEntity = gstr1SubmitRepo
						.findById(id);
				if (OpEntity.isPresent()) {

					GstnSubmitEntity entity = OpEntity.get();
					entity.setErrorCode(errorCode);
					entity.setErrorDesc(errorDesc);
					entity.setModifiedOn(now);
					entity.setModifiedBy(userName);
					// RET12521//GSTR1 is already submitted for current period
					//RTN_17//Invoices already Submitted
					if ("RET12521".equalsIgnoreCase(errorCode) || 
							"RTN_17".equalsIgnoreCase(errorCode)) {
						entity.setStatus(APIConstants.SUBMIT_COMPLETED);
						entity.setGstnStatus("P");
						entity.setRefId(APIConstants.ALREADY_SUBMITTED);
						docRepo.markSubmittedAsTrue(ret_period, gstin,
								modifiedDate, now);
					} else {
						entity.setStatus(APIConstants.SUBMIT_FAILED);
					}

					gstr1SubmitRepo.save(entity);
				}
			}
		}
		return refId;
	}

	private GstnSubmitEntity saveEntity(GstnSubmitEntity entity,
			String groupCode, String userName) {

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		TenantContext.setTenantId(groupCode);
		entity.setModifiedBy(userName);
		entity.setModifiedOn(now);
		entity.setStatus(APIConstants.SUBMIT_INPROGRESS);
		GstnSubmitEntity save = gstr1SubmitRepo.save(entity);
		return save;

	}
}
