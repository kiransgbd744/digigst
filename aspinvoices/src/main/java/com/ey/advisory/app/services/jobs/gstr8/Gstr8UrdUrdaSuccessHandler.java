package com.ey.advisory.app.services.jobs.gstr8;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr8UrdHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr8UrdaHeaderEntity;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8GetUrdGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8GetUrdaGstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("Gstr8UrdUrdaSuccessHandler")
@Slf4j
public class Gstr8UrdUrdaSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Gstr8UrdUrdaDataParserImpl")
	private Gstr8UrdUrdaDataParser gstr8urdUrdaDataParser;

	@Autowired
	@Qualifier("Gstr8GetUrdGstnRepository")
	private Gstr8GetUrdGstnRepository gstr8GetUrdGstnRepository;

	@Autowired
	@Qualifier("Gstr8GetUrdaGstnRepository")
	private Gstr8GetUrdaGstnRepository gstr8GetUrdaGstnRepository;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	GstinGetStatusRepository gstinGetStatusRepo;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		Long batchId = null;
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			batchId = dto.getBatchId();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside Gstr8UrdUrdaSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}

			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.URD.equalsIgnoreCase(dto.getType())) {
					gstr8GetUrdGstnRepository.softlyDeleteUrdHeader(
							dto.getGstin(), dto.getReturnPeriod(), now);

					gstr8GetUrdaGstnRepository.softlyDeleteUrdaHeader(
							dto.getGstin(), dto.getReturnPeriod(), now);

				}
			}

			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				if (APIConstants.URD.equalsIgnoreCase(dto.getType())) {
					List<GetGstr8UrdHeaderEntity> urdEntities = gstr8urdUrdaDataParser
							.parseUrdData(dto, apiResp);
					if (urdEntities != null && !urdEntities.isEmpty()) {
						gstr8GetUrdGstnRepository.saveAll(urdEntities);
					}

					List<GetGstr8UrdaHeaderEntity> urdaEntities = gstr8urdUrdaDataParser
							.parseUrdaData(dto, apiResp);
					if (urdaEntities != null && !urdaEntities.isEmpty()) {
						gstr8GetUrdaGstnRepository.saveAll(urdaEntities);
					}
				}
			});
			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
					isTokenResp);

			gstinGetStatusRepo.updateDBLoad(dto.getGstin(),
					dto.getType().toUpperCase(), dto.getReturnPeriod(), "GSTR8",
					true);

		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			batchUtil.updateById(batchId, APIConstants.FAILED, "CATCH_BLOCK",
					e.getMessage(), false);
			e.printStackTrace();
			throw new APIException(e.getLocalizedMessage());
		}

	}

}
