package com.ey.advisory.app.services.proceedtofile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.javatuples.Quartet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.ProceedFileEntity;
import com.ey.advisory.app.data.repositories.client.ProceedFileRepository;
import com.ey.advisory.app.docs.dto.ProceedFileDto;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Service("Gstr6ProceedToFileServiceImpl")
@Slf4j
public class Gstr6ProceedToFileServiceImpl
		implements Gstr6ProceedToFileService {

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	@Autowired
	private GSTNDetailRepository gstnRepo;

	@Autowired
	private ProceedFileRepository proceedFileRepo;

	@Override
	public String findProceedToFileStatus(String reqObject,
			ProceedFileDto dto) {
		String groupCode = TenantContext.getTenantId();
		TenantContext.setTenantId(groupCode);
		LOGGER.info("groupCode {} is set", groupCode);
		APIResponse resp = null;
		try {
			resp = gstnServer.gstnProceedToFileApiCall(groupCode, reqObject,
					dto.getGstin(), dto.getRet_period(), "GSTR6", false);
		} catch (java.lang.UnsupportedOperationException | AppException ex) {
			LOGGER.error("GSTR6 proceedig to file gstn api call exception",
					ex.getMessage());
			throw new AppException(ex,
					"{} error while gstr6 proceedig to file to Gstn");
		}
		String refId = null;
		ProceedFileEntity entity = new ProceedFileEntity();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Response from Gstn: {}", resp);
		}

		if (resp.isSuccess()) {
			String proceedFileJsonResp = resp.getResponse();
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = (JsonObject) jsonParser
					.parse(proceedFileJsonResp);
			if (jsonObject.get(APIIdentifiers.REF_ID) != null) {
				refId = jsonObject.get(APIIdentifiers.REF_ID).getAsString();
			} else if (jsonObject.get(APIIdentifiers.REFERENECE_ID) != null) {
				refId = jsonObject.get(APIIdentifiers.REFERENECE_ID)
						.getAsString();
			}

			entity.setReferenceId(refId);
			entity.setStatus("SUCCESS");
		} else {
			String errorCode = resp.getError().getErrorCode();
			String errorDesc = resp.getError().getErrorDesc();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Error thrown by Gstn ErrorCode: {} and "
						+ "ErrorDesc: {}", errorCode, errorDesc);
			}
			entity.setStatus("FAILED");
			entity.setErrorCode(errorCode);
			entity.setErrorDescription(errorDesc);
		}

		Long entityId = gstnRepo.findEntityIdByGstin(dto.getGstin());

		entity.setEntityId(entityId);
		entity.setGstin(dto.getGstin());
		entity.setReturnPeriod(dto.getRet_period());
		entity.setReturnType("GSTR6");
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		entity.setCreatedOn(now);
		proceedFileRepo.save(entity);

		return refId;
	}

	@Override
	public Quartet<String, String, String, String> getLatestStatusAndTime(
			String gstin, String returnPeriod, String returnType) {

		Optional<ProceedFileEntity> Opentity = proceedFileRepo
				.findTop1ByGstinAndReturnPeriodAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
						gstin, returnPeriod, returnType);

		String lastUpdatedDate = null;
		String status = null;
		String errorCode = null;
		String errorDesc = null;
		if (Opentity.isPresent()) {

			ProceedFileEntity proceedFileEntity = Opentity.get();

			status = proceedFileEntity.getStatus();
			errorCode = proceedFileEntity.getErrorCode();
			errorDesc = proceedFileEntity.getErrorDescription();
			LocalDateTime createdOn = proceedFileEntity.getCreatedOn();
			if (createdOn != null) {

				LocalDateTime istDateTimeFromUTC = EYDateUtil
						.toISTDateTimeFromUTC(createdOn);

				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("yyyy-MM-dd HH:mm:ss");

				lastUpdatedDate = FOMATTER.format(istDateTimeFromUTC);

			}

		}
		return new Quartet<>(status, lastUpdatedDate, errorCode, errorDesc);
	}

}
