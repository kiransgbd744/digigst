package com.ey.advisory.app.services.jobs.gstr1;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesB2bHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnrHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnurHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesExpHeaderEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEInvoiceB2bGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEInvoiceCdnGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEInvoiceCdnurGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEInvoiceExpGstnRepository;
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

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr1EInvSuccessHandler")
@Slf4j
public class Gstr1EInvSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Gstr1EInvoiceDataParserImpl")
	private Gstr1EInvoiceDataParser gstr1B2bEInvoiceDataParser;

	@Autowired
	@Qualifier("Gstr1EInvoiceDataParserImpl")
	private Gstr1EInvoiceDataParser gstr1CdnrEInvoiceDataParser;

	@Autowired
	@Qualifier("Gstr1EInvoiceDataParserImpl")
	private Gstr1EInvoiceDataParser gstr1CdnurEInvoiceDataParser;

	@Autowired
	@Qualifier("Gstr1EInvoiceDataParserImpl")
	private Gstr1EInvoiceDataParser gstr1ExpEInvoiceDataParser;

	@Autowired
	@Qualifier("Gstr1GetEInvoiceB2bGstnRepository")
	private Gstr1GetEInvoiceB2bGstnRepository gstr1GetEInvoiceB2bGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetEInvoiceCdnGstnRepository")
	private Gstr1GetEInvoiceCdnGstnRepository gstr1GetEInvoiceCdnGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetEInvoiceCdnurGstnRepository")
	private Gstr1GetEInvoiceCdnurGstnRepository gstr1GetEInvoiceCdnurGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetEInvoiceExpGstnRepository")
	private Gstr1GetEInvoiceExpGstnRepository gstr1GetEInvoiceExpGstnRepository;

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
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			batchId = dto.getBatchId();
			String apiSection = dto.getApiSection();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside Gstr1EInvSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}

			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			// B2B
			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.EINV_B2B.equalsIgnoreCase(dto.getType())) {
					gstr1GetEInvoiceB2bGstnRepository
							.softlyDeleteEInvoicesHeader(dto.getGstin(),
									dto.getReturnPeriod(), now);
				}
			}

			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				if (APIConstants.EINV_B2B.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1EInvoicesB2bHeaderEntity> eInvEntities = gstr1B2bEInvoiceDataParser
							.parseEInvoiceB2bData(dto, apiResp);
					if (eInvEntities != null && !eInvEntities.isEmpty()) {
						gstr1GetEInvoiceB2bGstnRepository.saveAll(eInvEntities);
					}
				}

			});

			// CDNR
			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.EINV_CDNR.equalsIgnoreCase(dto.getType())) {
					gstr1GetEInvoiceCdnGstnRepository.softlyDeleteCdnrHeader(
							dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}

			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				if (APIConstants.EINV_CDNR.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1EInvoicesCdnrHeaderEntity> eInvEntities = gstr1CdnrEInvoiceDataParser
							.parseEInvoiceCdnrData(dto, apiResp);
					if (eInvEntities != null && !eInvEntities.isEmpty()) {
						gstr1GetEInvoiceCdnGstnRepository.saveAll(eInvEntities);
					}
				}

			});
			// CDNUR
			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.EINV_CDNUR.equalsIgnoreCase(dto.getType())) {
					gstr1GetEInvoiceCdnurGstnRepository.softlyDeleteCdnurHeader(
							dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}

			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				if (APIConstants.EINV_CDNUR.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1EInvoicesCdnurHeaderEntity> eInvEntities = gstr1CdnurEInvoiceDataParser
							.parseEInvoiceCdnurData(dto, apiResp);
					if (eInvEntities != null && !eInvEntities.isEmpty()) {
						gstr1GetEInvoiceCdnurGstnRepository
								.saveAll(eInvEntities);
					}
				}

			});
			// EXP
			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.EINV_EXP.equalsIgnoreCase(dto.getType())) {
					gstr1GetEInvoiceExpGstnRepository.softlyDeleteExpHeader(
							dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}

			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				if (APIConstants.EINV_EXP.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1EInvoicesExpHeaderEntity> eInvEntities = gstr1ExpEInvoiceDataParser
							.parseEInvoiceExpData(dto, apiResp);
					if (eInvEntities != null && !eInvEntities.isEmpty()) {
						gstr1GetEInvoiceExpGstnRepository.saveAll(eInvEntities);
					}
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
