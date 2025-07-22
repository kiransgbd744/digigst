package com.ey.advisory.app.services.jobs.gstr7;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr7TdsDetailsEntity;
import com.ey.advisory.app.data.repositories.client.Gstr7GetTdsDetailsGstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr7GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr7GetTdsDetailsSuccessHandler")
@Slf4j
public class Gstr7GetTdsDetailsSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Gstr7TdsDataParserImpl")
	private Gstr7TdsGetDataParser gstr7TdsGetDataParser;

	@Autowired
	@Qualifier("Gstr7GetTdsDetailsGstnRepository")
	private Gstr7GetTdsDetailsGstnRepository tdsHeaderRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		Long batchId = null;
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			Gstr7GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr7GetInvoicesReqDto.class);
			batchId = dto.getBatchId();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside Gstr7GetTdsDetailsSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}

			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			if (resultIds != null && !resultIds.isEmpty()) {
				tdsHeaderRepo.softlyDeleteTdsHeader(dto.getGstin(),
						dto.getReturnPeriod(), now);

			}

			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				if (dto.getType().equalsIgnoreCase(APIConstants.TDS)) {
					List<Gstr7TdsDetailsEntity> tdsEntities = gstr7TdsGetDataParser
							.parseTdsGetData(dto, apiResp, dto.getType(),
									ctxParamsObj.get("batchId").getAsLong(),
									now);
					if (!tdsEntities.isEmpty()) {
						tdsHeaderRepo.saveAll(tdsEntities);
					}

				} else {
					//  disscuss with siva shall we need to write any logic here
				}

			});

			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
					isTokenResp);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			batchUtil.updateById(batchId, APIConstants.FAILED, "CATCH_BLOCK",
					e.getMessage(), false);
			e.printStackTrace();
			throw new APIException(e.getLocalizedMessage());
		}
	}

}
