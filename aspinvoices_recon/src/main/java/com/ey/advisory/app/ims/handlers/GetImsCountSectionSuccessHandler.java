/**
 * 
 */
package com.ey.advisory.app.ims.handlers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("GetImsCountSectionSuccessHandler")
@Slf4j
public class GetImsCountSectionSuccessHandler implements SuccessHandler {
	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("ImsCountDataParserImpl")
	private ImsCountDataParser imsCountParser;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		Long batchId = null;
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			batchId = dto.getBatchId();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call Count is Success for the batchId {} inside "
								+ "GetImsInvoicesSectionSuccessHandler.java",
						batchId);
			}

			if (APIConstants.IMS_COUNT.equalsIgnoreCase(dto.getApiSection())) {
				imsCountParser.parseImsCountData(resultIds, dto, batchId,null);
			}

			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
					false);

		} catch (Exception ex) {
			LOGGER.error("Error while parsing ims count {} ", ex);
			batchUtil.updateById(batchId, APIConstants.FAILED, null,
					"Error while parsing ims count", false);
			throw new AppException();
		}
	}

}