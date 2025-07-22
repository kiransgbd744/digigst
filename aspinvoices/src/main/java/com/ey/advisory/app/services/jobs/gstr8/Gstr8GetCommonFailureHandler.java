/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr8;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Gstr8GetTcsGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8GetTcsaGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8GetUrdGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8GetUrdaGstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.FailureHandler;
import com.ey.advisory.gstnapi.FailureResult;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Service("Gstr8GetCommonFailureHandler")
@Slf4j
public class Gstr8GetCommonFailureHandler implements FailureHandler {

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("Gstr8GetTcsGstnRepository")
	private Gstr8GetTcsGstnRepository gstr8GetTcsGstnRepository;

	@Autowired
	@Qualifier("Gstr8GetTcsaGstnRepository")
	private Gstr8GetTcsaGstnRepository gstr8GetTcsaGstnRepository;

	@Autowired
	@Qualifier("Gstr8GetUrdGstnRepository")
	private Gstr8GetUrdGstnRepository gstr8GetUrdGstnRepository;

	@Autowired
	@Qualifier("Gstr8GetUrdaGstnRepository")
	private Gstr8GetUrdaGstnRepository gstr8GetUrdaGstnRepository;

	private static final List<String> ERROR_CODES = ImmutableList.of("RET13508",
			"RET13509", "RET13510", "RET11416", "RET11417", "RETWEB_04");

	@Override
	public void handleFailure(FailureResult result, String apiParams) {
		try {
			String errorCode = result.getError().getErrorCode();
			String errorDesc = result.getError().getErrorDesc();
			Gson gson = GsonUtil.newSAPGsonInstance();
			String ctxParams = result.getCtxParams();
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Failed for the batchId {} inside "
								+ "Gstr1GetCommonFailureHandler.java",
						ctxParamsObj.get("batchId").getAsLong());
			}
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("dto object: {} ", dto.toString());
				LOGGER.debug("dto type: {} ", dto.getType());
			}
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (ERROR_CODES.contains(errorCode)) {
				if (APIConstants.GSTR8.equalsIgnoreCase(dto.getApiSection())) {
					if (APIConstants.TCS.equalsIgnoreCase(dto.getType())) {
						int rowAffectedTcs = gstr8GetTcsGstnRepository
								.softlyDeleteTcsHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "TCS is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffectedTcs);
						}
						int rowAffectedTcsa = gstr8GetTcsaGstnRepository
								.softlyDeleteTcsaHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "TCSA is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffectedTcsa);
						}

					} else if (APIConstants.URD
							.equalsIgnoreCase(dto.getType())) {
						int rowAffectedUrd = gstr8GetUrdGstnRepository
								.softlyDeleteUrdHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "URD is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffectedUrd);
						}

						int rowAffectedUrda = gstr8GetUrdaGstnRepository
								.softlyDeleteUrdaHeader(dto.getGstin(),
										dto.getReturnPeriod(), now);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Total Number of soft deleted "
											+ "for gstin {}, tax period {}, section "
											+ "URDA is {} ",
									dto.getGstin(), dto.getReturnPeriod(),
									rowAffectedUrda);
						}

					}
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
			throw new AppException(e);
		}
	}
}
