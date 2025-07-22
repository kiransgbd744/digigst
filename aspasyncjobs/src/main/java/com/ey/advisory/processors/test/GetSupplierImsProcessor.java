package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstinApiCallRepository;
import com.ey.advisory.app.gstr2b.Gstr2BbatchUtil;
import com.ey.advisory.app.service.ims.supplier.SupplierImsGetReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("GetSupplierImsProcessor")
public class GetSupplierImsProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	@Autowired
	@Qualifier("Gstr2BbatchUtil")
	private Gstr2BbatchUtil batchUtil;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("GstinApiCallRepository")
	private GstinApiCallRepository gstnStatusRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	/*private static DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");*/

	@Override
	public void execute(Message message, AppExecContext context) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"supplier IMS Get Call Job"
							+ " executing for groupcode {} and params {}",
					groupCode, jsonString);
		}

		try {

/*			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");*/
			
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();


			SupplierImsGetReqDto reqDto = gson.fromJson(requestObject,
					SupplierImsGetReqDto.class);

			String gstin = reqDto.getGstin();
			String taxPeriod = reqDto.getTaxPeriod();
			String imsReturnType = reqDto.getReturnType();
			String section = reqDto.getSection();
			Long batchId = reqDto.getBatchId();

			String authStatus = authTokenService
					.getAuthTokenStatusForGstin(gstin);
			if ("I".equalsIgnoreCase(authStatus)) {
				String msg = String.format("Gstin %s is inactive, Supplier "
						+ "IMS get call is terminated", gstin);
				LOGGER.error(msg);
				// TODO - update table
				throw new AppException(msg);
			}

			String successHandler = "SupplierImsSuccessHandler";
			String failureHandler = "SupplierImsFailureHandler";

			initiateGetCall(gstin, taxPeriod, section, batchId, imsReturnType,  
					successHandler, failureHandler);
		} catch (Exception ex) {
			LOGGER.error("Exception while initiating supplier IMS Get call",
					ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}

	private void initiateGetCall(String gstin, String taxPeriod, 
			String section, Long batchId, String imsReturnType,
			String successHandler, String failureHandler) {
		
			Optional<GetAnx1BatchEntity> findById = batchRepo.findById(batchId);
			GetAnx1BatchEntity batch = findById.get();
		
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
		dto.setGstin(gstin);
		dto.setReturnPeriod(taxPeriod);
		dto.setGroupcode(TenantContext.getTenantId());
		dto.setReturnType(imsReturnType);
		dto.setSection(section);
		dto.setBatchId(batchId);
	
		try {

			String ctxParam = gson.toJson(dto);
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", taxPeriod);
			APIParam param3 = new APIParam("section", section);
			APIParam param4 = new APIParam("rtn_typ", imsReturnType);

			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN, APIIdentifiers.IMS_SUPPLIER_GET, param1,
					param2, param3, param4);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Before invoking apiInvoker.invoke() "
								+ "method {} params {}, ctxParam {}",
						params.toString(), ctxParam);

			APIInvocationResult result = apiInvoker.invoke(params, null,
					successHandler, failureHandler, ctxParam);

			gstnStatusRepo.updateStatus(gstin, taxPeriod, 
					APIConstants.SUPPLIER_IMS,
					APIConstants.INPROGRESS, null, null, LocalDateTime.now());

			batch.setStatus(APIConstants.INPROGRESS);
			batch.setRequestId(result.getTransactionId());
			batchRepo.save(batch);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Supplier IMS GET successfully initiated for Gstin {}, "
								+ "TaxPeriod {} : transctionId %s {} ",
						gstin, taxPeriod, result.getTransactionId());

		} catch (Exception ex) {
			LOGGER.error(
					"Error while invocking GetSupplierImsProcessor, invoke() method",
					ex);
			if (batch != null) {
				batchUtil.updateById(batch.getId(), APIConstants.FAILED,
						batch.getErrorCode(), batch.getErrorDesc(),
						batch.isTokenResponse(), batch.getUserRequestId());

				gstnStatusRepo.updateStatus(batch.getSgstin(),
						batch.getTaxPeriod(), APIConstants.SUPPLIER_IMS,
						APIConstants.FAILED, null, null, LocalDateTime.now());

			}
		}

	}

}
