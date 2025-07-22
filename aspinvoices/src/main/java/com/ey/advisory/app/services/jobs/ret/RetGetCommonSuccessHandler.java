package com.ey.advisory.app.services.jobs.ret;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.ret.GetRefundClaimedEntity;
import com.ey.advisory.app.data.entities.ret.GetRetInterestLateFeeEntity;
import com.ey.advisory.app.data.entities.ret.GetRetInwardEntity;
import com.ey.advisory.app.data.entities.ret.GetRetOutwardEntity;
import com.ey.advisory.app.data.entities.ret.GetRetPaymentOfTaxEntity;
import com.ey.advisory.app.data.entities.ret.GetRetTable5Entity;
import com.ey.advisory.app.data.repositories.client.RetGetCashLedgerGstnRepository;
import com.ey.advisory.app.data.repositories.client.RetGetInterestLateFeeGstnRepository;
import com.ey.advisory.app.data.repositories.client.RetGetInwardGstnRepository;
import com.ey.advisory.app.data.repositories.client.RetGetOutwardGstnRepository;
import com.ey.advisory.app.data.repositories.client.RetGetPaymentTaxGstnRepository;
import com.ey.advisory.app.data.repositories.client.RetGetTable5GstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;
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
@Slf4j
@Service("RetGetCommonSuccessHandler")
public class RetGetCommonSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("RetOutwardDataParserImpl")
	private RetOutwardGetDataParser retOutGetDataParser;

	@Autowired
	@Qualifier("RetInwardDataParserImpl")
	private RetInwardGetDataParser retInGetDataParser;

	@Autowired
	@Qualifier("RetTable5DataParserImpl")
	private RetTable5GetDataParser retTable5GetDataParser;

	@Autowired
	@Qualifier("RetTable6DataParserImpl")
	private RetTable6GetDataParser retTable6GetDataParser;

	@Autowired
	@Qualifier("RetPaymentOfTaxDataParserImpl")
	private RetPaymentOfTaxGetDataParser retPaymentOfTaxGetDataParser;

	@Autowired
	@Qualifier("RetCashLedgerGetDataParserImpl")
	private RetCashLedgerGetDataParser retCashLedgerGetDataParser;

	@Autowired
	@Qualifier("RetGetOutwardGstnRepository")
	private RetGetOutwardGstnRepository outwardHeaderRepo;

	@Autowired
	@Qualifier("RetGetInwardGstnRepository")
	private RetGetInwardGstnRepository inwardHeaderRepo;

	@Autowired
	@Qualifier("RetGetTable5GstnRepository")
	private RetGetTable5GstnRepository table5HeaderRepo;

	@Autowired
	@Qualifier("RetGetInterestLateFeeGstnRepository")
	private RetGetInterestLateFeeGstnRepository table6HeaderRepo;

	@Autowired
	@Qualifier("RetGetPaymentTaxGstnRepository")
	private RetGetPaymentTaxGstnRepository paymentTaxHeaderRepo;

	@Autowired
	@Qualifier("RetGetCashLedgerGstnRepository")
	private RetGetCashLedgerGstnRepository cashLedgerHeaderRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	private GetAnx1BatchEntity batch;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {

		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside RetGetCommonSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}

			RetGetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					RetGetInvoicesReqDto.class);
			TenantContext.setTenantId(dto.getGroupcode());
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				Set<GetRetOutwardEntity> outwardEntities = retOutGetDataParser
						.parseOutwardGetData(dto, apiResp);
				if (!outwardEntities.isEmpty()) {
					outwardHeaderRepo.softlyDeleteOutwardHeader(dto.getGstin(),
							dto.getReturnPeriod());
					outwardHeaderRepo.saveAll(outwardEntities);
				}

				Set<GetRetInwardEntity> inwardEntities = retInGetDataParser
						.parseInwardGetData(dto, apiResp);

				if (!inwardEntities.isEmpty()) {
					inwardHeaderRepo.softlyDeleteInwardHeader(dto.getGstin(),
							dto.getReturnPeriod());
					inwardHeaderRepo.saveAll(inwardEntities);
				}

				Set<GetRetTable5Entity> table5Entities = retTable5GetDataParser
						.parseTable5GetData(dto, apiResp);

				if (!table5Entities.isEmpty()) {
					table5HeaderRepo.softlyDeleteTable5Header(dto.getGstin(),
							dto.getReturnPeriod());
					table5HeaderRepo.saveAll(table5Entities);
				}

				Set<GetRetInterestLateFeeEntity> table6Entities = retTable6GetDataParser
						.parseTable6GetData(dto, apiResp);

				if (!table6Entities.isEmpty()) {
					table6HeaderRepo.softlyDeleteInterestLateFeeHeader(
							dto.getGstin(), dto.getReturnPeriod());
					table6HeaderRepo.saveAll(table6Entities);
				}

				Set<GetRetPaymentOfTaxEntity> payTaxEntities = retPaymentOfTaxGetDataParser
						.parsePaymentTaxGetData(dto, apiResp);

				if (!payTaxEntities.isEmpty()) {
					paymentTaxHeaderRepo.softlyDeletePaymentTaxHeader(
							dto.getGstin(), dto.getReturnPeriod());
					paymentTaxHeaderRepo.saveAll(payTaxEntities);
				}

				Set<GetRefundClaimedEntity> refundEntities = retCashLedgerGetDataParser
						.parseCashLedgerGetData(dto, apiResp);

				if (!refundEntities.isEmpty()) {
					cashLedgerHeaderRepo.softlyDeleteCashLedgerHeader(
							dto.getGstin(), dto.getReturnPeriod());
					cashLedgerHeaderRepo.saveAll(refundEntities);
				}

			});

			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
					APIConstants.SUCCESS, null, null, isTokenResp);

		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw new APIException(e.getLocalizedMessage());
		}
	}
}
