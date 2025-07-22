/**
 * 
 */
package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstinApiCallRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bAutoCommRepository;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.gstnapi.FailureHandler;
import com.ey.advisory.gstnapi.FailureResult;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */

@Service("Gstr2BGetAutoFailureHandler")
@Slf4j
public class Gstr2BGetAutoFailureHandler implements FailureHandler {

	@Autowired
	@Qualifier("Gstr2BbatchUtil")
	private Gstr2BbatchUtil batchUtil;

	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("Gstr2bAutoCommRepository")
	private Gstr2bAutoCommRepository gstr2bAutoCommRepo;
	
	@Autowired
	@Qualifier("GstinApiCallRepository")
	private GstinApiCallRepository gstnStatusRepo;

	private static final List<String> ERROR_CODES = ImmutableList.of("RET13508",
			"RET13509", "RET13510", "RET11416", "RET11417", "RET2B1013",
			"RET2B1016", "RET2B1017", "RET2B1018", "RET2B1023", "RET2B1015",
			"RET2B1016", "RET2B1018", "RET2B1023", "GTR2B-002", "GTR2B-001");

	@Override
	public void handleFailure(FailureResult result, String apiParams) {
		try {
			String errorCode = result.getError().getErrorCode();
			String errorDesc = result.getError().getErrorDesc();
			Long reqId = result.getTransactionId();
			String ctxParams = result.getCtxParams();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GET Call is Failed for the batchId {} inside "
						+ "Gstr2BGetAutoFailureHandler");
			}

			Long batchId = ctxParamsObj.get("batchId").getAsLong();

			batchUtil.updateById(batchId, APIConstants.FAILED, errorCode,
					errorDesc, false, reqId);

			GetAnx1BatchEntity batchDto = batchUtil
					.getGstinAndTaxPeriodByBatchId(batchId);

			if (ERROR_CODES.contains(errorCode)) {
				batchUtil.updateById(batchId, APIConstants.SUCCESS_WITH_NO_DATA,
						errorCode, errorDesc, false, reqId);
				gstr2bAutoCommRepo.updateGstr2bAutoStatus(batchDto.getSgstin(),
						batchDto.getTaxPeriod(), APIConstants.GSTR2B,
						APIConstants.SUCCESS_WITH_NO_DATA, "");
				gstnStatusRepo.updateStatus(batchDto.getSgstin(), batchDto.getTaxPeriod(), APIConstants.GSTR2B,
						APIConstants.SUCCESS_WITH_NO_DATA, null, null, LocalDateTime.now());

			} else {
				batchUtil.updateById(batchId, APIConstants.FAILED, errorCode,
						errorDesc, false, reqId);
				gstr2bAutoCommRepo.updateGstr2bAutoStatus(batchDto.getSgstin(),
						batchDto.getTaxPeriod(), APIConstants.GSTR2B,
						APIConstants.FAILED, errorDesc);
				gstnStatusRepo.updateStatus(batchDto.getSgstin(), batchDto.getTaxPeriod(), APIConstants.GSTR2B,
						APIConstants.FAILED, null, errorDesc, LocalDateTime.now());
			}

		} catch (Exception ex) {
			LOGGER.error("Exception while handling GSTR2BFailureHandler", ex);
			throw new APIException(ex.getLocalizedMessage());
		}
	}

}
