/**
 * 
 */
package com.ey.advisory.app.ims.handlers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsERPRequestRepository;
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

@Service("GetImsInvoicesSectionSuccessHandler")
@Slf4j
public class GetImsInvoicesSectionSuccessHandler implements SuccessHandler {
	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("ImsInvoicesDataParserImpl")
	private ImsInvoicesDataParser imsInvoiceParser;

	@Autowired
	@Qualifier("ImsInvoicesProcCallServiceImpl")
	private ImsInvoicesProcCallService imsProcCallInvoiceParser;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	@Autowired
	@Qualifier("imsERPRequestRepository")
	private ImsERPRequestRepository imsRepo;

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
			String typDxp = dto.getType();
			String apiSection = dto.getApiSection();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside "
								+ "GetImsInvoicesSectionSuccessHandler.java",
						batchId);
			}

			if (APIConstants.IMS_INVOICE.equalsIgnoreCase(apiSection)) {
				imsInvoiceParser.parseImsInvoicesData(resultIds, dto, batchId,null);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"calling dup check proc and update for batchid {}  "
								+ " and section {}",
						batchId, dto.getType());
			}

			if (APIConstants.IMS_INVOICE.equalsIgnoreCase(apiSection)) {
				imsProcCallInvoiceParser.procCall(dto, batchId);
			}

			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
					false);
			
			ImsERPRequestEntity erpEntity = new ImsERPRequestEntity();
			
			erpEntity.setBatchId(batchId);
			erpEntity.setSupplyType(dto.getType());
			erpEntity.setGstin(dto.getGstin());
			erpEntity.setStatus("INITIATED");
			erpEntity.setCreatedOn(LocalDateTime.now());
			
			imsRepo.save(erpEntity);

		} catch (Exception ex) {
			LOGGER.error("Error while parsing ims invoices {} ", ex);
			batchUtil.updateById(batchId, APIConstants.FAILED, null,
					"Error while parsing ims invoices", false);
			throw new AppException();
		}
	}

}