package com.ey.advisory.app.services.jobs.gstr6;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Gstr6GetB2bGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6GetB2baGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6GetCdnGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6GetCdnaGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6GetIsdGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6GetIsdaGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6GetLateFeeGstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.ey.advisory.gstnapi.FailureHandler;
import com.ey.advisory.gstnapi.FailureResult;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@Service("Gstr6GetCommonFailureHandler")
public class Gstr6GetCommonFailureHandler implements FailureHandler {

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("Gstr6GetB2bGstnRepository")
	private Gstr6GetB2bGstnRepository b2bHeaderRepo;

	@Autowired
	@Qualifier("Gstr6GetB2baGstnRepository")
	private Gstr6GetB2baGstnRepository b2baHeaderRepo;

	@Autowired
	@Qualifier("Gstr6GetCdnGstnRepository")
	private Gstr6GetCdnGstnRepository cdnHeaderRepo;

	@Autowired
	@Qualifier("Gstr6GetCdnaGstnRepository")
	private Gstr6GetCdnaGstnRepository cdnaHeaderRepo;

	@Autowired
	@Qualifier("Gstr6GetIsdGstnRepository")
	private Gstr6GetIsdGstnRepository isdHeaderRepo;

	@Autowired
	@Qualifier("Gstr6GetIsdaGstnRepository")
	private Gstr6GetIsdaGstnRepository isdaHeaderRepo;

	@Autowired
	@Qualifier("Gstr6GetLateFeeGstnRepository")
	private Gstr6GetLateFeeGstnRepository lateFeeHeaderRepo;

	private static final List<String> ERROR_CODES = ImmutableList.of("RET13508",
			"RET13509", "RET13510", "RET11416", "RET11417");

	@Override
	public void handleFailure(FailureResult result, String apiParams) {
		try {
			String errorCode = result.getError().getErrorCode();
			String errorDesc = result.getError().getErrorDesc();
			Gson gson = GsonUtil.newSAPGsonInstance();
			String ctxParams = result.getCtxParams();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Failed for the batchId {} inside "
								+ "Gstr6GetCommonFailureHandler.java",
						ctxParamsObj.get("batchId").getAsLong());
			}
			Gstr6GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr6GetInvoicesReqDto.class);
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (ERROR_CODES.contains(errorCode)) {

				if (APIConstants.B2B.equalsIgnoreCase(dto.getType())) {
					b2bHeaderRepo.softlyDeleteB2bHeader(dto.getGstin(),
							dto.getReturnPeriod(), now);
				}

				else if (APIConstants.B2BA.equalsIgnoreCase(dto.getType())) {
					b2baHeaderRepo.softlyDeleteB2baHeader(dto.getGstin(),
							dto.getReturnPeriod(), now);
				} else if (APIConstants.CDN.equalsIgnoreCase(dto.getType())) {
					cdnHeaderRepo.softlyDeleteCdnHeader(dto.getGstin(),
							dto.getReturnPeriod(), now);
				} else if (APIConstants.CDNA.equals(dto.getType())) {
					cdnaHeaderRepo.softlyDeleteCdnaHeader(dto.getGstin(),
							dto.getReturnPeriod(), now);
				} else if (APIConstants.ISD.equals(dto.getType())) {
					isdHeaderRepo.softlyDeleteIsdHeader(dto.getGstin(),
							dto.getReturnPeriod(), now);
				} else if (APIConstants.ISDA.equals(dto.getType())) {
					isdaHeaderRepo.softlyDeleteIsdaHeader(dto.getGstin(),
							dto.getReturnPeriod(), now);
				} else if (APIConstants.LATEFEE
						.equalsIgnoreCase(dto.getType())) {
					lateFeeHeaderRepo.softlyDeleteLateFeeHeader(dto.getGstin(),
							dto.getReturnPeriod(), now);
				}
				batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
						APIConstants.SUCCESS_WITH_NO_DATA, errorCode, errorDesc,
						false);
			} else {
				batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
						APIConstants.FAILED, errorCode, errorDesc, false);
			}
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new APIException(e.getLocalizedMessage());
		}
	}
}
