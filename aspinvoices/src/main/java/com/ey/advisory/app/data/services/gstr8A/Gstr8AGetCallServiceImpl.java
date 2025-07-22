/**
 * 
 */
package com.ey.advisory.app.data.services.gstr8A;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8ASummaryDetailsRepository;
import com.ey.advisory.app.data.services.common.GstnCommonServiceUtil;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */

@Component("Gstr8AGetCallServiceImpl")
@Slf4j
public class Gstr8AGetCallServiceImpl {

	@Autowired
	GstnCommonServiceUtil gstnCommonUtil;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	Gstr8ASummaryDetailsRepository gstr8ASummaryDetailsRepo;

	@Autowired
	GstinGetStatusRepository gstinGetStatusRepo;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	public void getCall(String gstin, String fy, String userName) {

		String taxPeriod = GenUtil.getFinancialPeriodFromFY(fy);
		try {
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("Inside Gstr8AGetCallServiceImpl before calling getGstr8ADetails method");
			}
		
			getGstr8ADetails(gstin, fy, userName);
			
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("Inside Gstr8AGetCallServiceImpl after calling getGstr8ADetails method");
			}
		} catch (Exception e) {
			gstinGetStatusRepo.updateStatus(gstin, taxPeriod,
					APIConstants.GSTR8A, APIConstants.FAILED,
					LocalDateTime.now(), e.getMessage());
			String msg = "Exception occued while making GSTR8A get and save call";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	private void getGstr8ADetails(String gstin, String fy, String userName) {
		try {
			Long batchId = createBatchAndSave(gstin, GenUtil
					.getFinancialPeriodFromFY(GenUtil.getFormattedFy(fy)),
					userName);
			JsonObject obj = new JsonObject();
			obj.addProperty("gstin", gstin);
			obj.addProperty("fy", fy);
			obj.addProperty("ret_period", GenUtil
					.getFinancialPeriodFromFY(GenUtil.getFormattedFy(fy)));
			obj.addProperty("batchId", batchId);
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("fy", fy);
			APIParam param3 = new APIParam("ret_period", GenUtil
					.getFinancialPeriodFromFY(GenUtil.getFormattedFy(fy)));
			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN, APIIdentifiers.GSTR8A_GETDETAILS,
					param1, param2, param3);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Before invoking apiInvoker.invoke() "
								+ "method {} params {}, ctxParam {}",
						params.toString(), obj);

			APIInvocationResult result = apiInvoker.invoke(params, null,
					"Gstr8AGetSuccessHandler", "Gstr8AFailureHandler",
					obj.toString());

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Invocation Params has executed succesfully for Params  {} and Invocation Id {} ",
						params.toString(), result.getTransactionId());
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while invoking getGstr8A Details '%s',"
							+ " GSTIN - '%s' and fy - '%s'",
					APIIdentifiers.GSTR8A_GETDETAILS, gstin, fy);
			LOGGER.error(msg, ex);
			throw ex;
		}
	}

	private Long createBatchAndSave(String gstin, String retPeriod,
			String userName) {

		batchRepo.softlyDelete("GET8A", APIConstants.GSTR8A.toUpperCase(),
				gstin, retPeriod);
		GetAnx1BatchEntity batch = batchUtil.makeBatch(gstin, retPeriod,
				"GET8A", APIConstants.GSTR8A.toUpperCase(), userName);
		batch = batchRepo.save(batch);
		return batch.getId();
	}

}
