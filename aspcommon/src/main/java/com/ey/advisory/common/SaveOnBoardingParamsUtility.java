package com.ey.advisory.common;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.client.domain.B2COnBoardingConfigEntity;
import com.ey.advisory.common.client.domain.B2CQRAmtConfigEntity;
import com.ey.advisory.common.client.repositories.B2COnBoardingConfigRepo;
import com.ey.advisory.common.client.repositories.B2CQRAmtConfigRepo;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SaveOnBoardingParamsUtility {

	@Autowired
	@Qualifier("B2COnBoardingConfigRepo")
	B2COnBoardingConfigRepo b2cOnBoardingRepo;

	@Autowired
	@Qualifier("B2CQRAmtConfigRepo")
	B2CQRAmtConfigRepo b2CQRAmtConfigRepo;

	public String saveQROnBoardingData(JsonObject reqJson,
			JsonArray dataArray) {
		try {
			Gson gson = GsonUtil.gsonInstanceWithExpose();

			Type list = new TypeToken<List<B2COnBoardingConfigEntity>>() {
			}.getType();
			List<B2COnBoardingConfigEntity> reqDtos = gson.fromJson(dataArray,
					list);

			String entityId = reqJson.get("entityId").getAsString();
			String pan = reqJson.get("pan").getAsString();
			String createdUser = reqJson.get("createdUser").getAsString();
			String entityName = reqJson.get("entityName").getAsString();
			String updatedBy = reqJson.get("updatedBy").getAsString();

			for (int i = 0; i < reqDtos.size(); i++) {
				reqDtos.get(i).setPan(pan);
				reqDtos.get(i).setCreatedBy(createdUser);
				reqDtos.get(i).setCreatedOn(LocalDateTime.now());
				reqDtos.get(i).setActive(true);
				reqDtos.get(i).setEntityName(entityName);
			}

			int updatedRecords = b2cOnBoardingRepo.updateActiveExistingRecords(
					Long.valueOf(entityId), updatedBy);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" Updated Existing Active Records {} for"
								+ " Entity Id {}  in B2COnBoardingConfigEntity",
						updatedRecords, entityId);
			}
			b2cOnBoardingRepo.saveAll(reqDtos);
			return "Success";
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception while Save the B2C Code for Group %s",
					TenantContext.getTenantId());
			LOGGER.error(errMsg, e);
			return "Failed";
		}
	}

	public String saveQRAmtConfigData(JsonObject reqJson) {
		try {
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			String entityId = reqJson.get("entityId").getAsString();
			String updatedBy = reqJson.get("updatedBy").getAsString();
			String createdUser = reqJson.get("createdUser").getAsString();
			String isMamOpted = reqJson.get("isMamOpted").getAsString();
			int updatedRecords = b2CQRAmtConfigRepo.updateActiveExistingRecords(
					Long.valueOf(entityId), updatedBy);
			if (isMamOpted.equalsIgnoreCase("N") && updatedRecords > 0) {
				LOGGER.debug(
						"Minimum Amt is Not Opted Entity Id {},"
						+ "Hence SoftDeleting the existing active Entries",
						entityId);
				return "Success";
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" Updated Existing Active Records {} for"
								+ " Entity Id {}  in B2COnBoardingConfigEntity",
						updatedRecords, entityId);
			}
			B2CQRAmtConfigEntity b2cQRAmtEntity = gson.fromJson(reqJson,
					B2CQRAmtConfigEntity.class);
			b2cQRAmtEntity.setActive(true);
			b2cQRAmtEntity.setCreatedOn(LocalDateTime.now());
			b2cQRAmtEntity.setCreatedBy(createdUser);
			b2CQRAmtConfigRepo.save(b2cQRAmtEntity);
			return "Success";
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception while Save the Amt B2C Code for Group %s",
					TenantContext.getTenantId());
			LOGGER.error(errMsg, e);
			return "Failed";
		}
	}
}
