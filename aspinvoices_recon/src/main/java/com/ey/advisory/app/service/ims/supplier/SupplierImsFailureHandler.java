package com.ey.advisory.app.service.ims.supplier;

import java.util.List;

/**
 * @author vishal.verma
 *
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
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

@Service("SupplierImsFailureHandler")
@Slf4j
public class SupplierImsFailureHandler implements FailureHandler {

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	private static final List<String> ERROR_CODES = ImmutableList.of("RET11416");
	
	@Autowired
	@Qualifier("SupplierImsProcCallServiceImpl")
	private SupplierImsProcCallServiceImpl imsProcCallInvoiceParser;

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
						"GET Supplier IMS Invoices is Failed for the batchId {} inside "
								+ "ImsInvoicesProcCallServiceImpl",
						ctxParamsObj.get("batchId").getAsLong());
			}
			Long batchId = ctxParamsObj.get("batchId").getAsLong();
			
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			if(LOGGER.isDebugEnabled())
			{
			LOGGER.debug("dto {} ",dto );
			
			}
			TenantContext.setTenantId(dto.getGroupcode());
			if (ERROR_CODES.contains(errorCode)) {
				batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
						APIConstants.SUCCESS_WITH_NO_DATA, errorCode, errorDesc, false);
				
				
				
				Gstr1GetInvoicesReqDto invoiceDto = new Gstr1GetInvoicesReqDto();
				invoiceDto.setGstin(dto.getGstin());
				invoiceDto.setReturnType(dto.getReturnType());
				invoiceDto.setSection(dto.getSection());
				
				if(LOGGER.isDebugEnabled())
				{
				LOGGER.debug("invoiceDto1 {} ",invoiceDto );
				
				}
					imsProcCallInvoiceParser.procCall(invoiceDto, batchId);
				
			} 
			else {
				batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
						APIConstants.FAILED, errorCode, errorDesc, false);
			}
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw new AppException(e);
		}
	}
}
