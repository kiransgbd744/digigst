package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1SummaryEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Component("gstr1SummaryAtGstnImpl")
public class Gstr1SummaryAtGstnImpl implements Gstr1SummaryAtGstn {

	@Autowired
	@Qualifier("gstr1SummaryDataAtGstnImpl")
	private Gstr1SummaryDataAtGstn gstr1SummaryDataAtGstn;

	@Autowired
	@Qualifier("gstr1SummaryDataParserImpl")
	private Gstr1SummaryDataParser gstr1SummaryDataParser;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private GetBatchPayloadHandler batchPayloadHelper;

	@Override
	public String getGstr1Summary(Gstr1GetInvoicesReqDto dto,
			String groupCode) {

		String apiResp = null;
		GetAnx1BatchEntity batch = null;
		try {
			int retryCount = 0;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getSummary method called with dto {}", dto);
			}
			String type = APIIdentifiers.GSTR1_GETSUM;
			batch = batchUtil.makeBatchGstr1(dto, type,
					APIConstants.GSTR1.toUpperCase());
			// InActiveting Previous Batch Records
			batchRepo.softlyDelete(type.toUpperCase(),
					APIConstants.GSTR1.toUpperCase(), dto.getGstin(),
					dto.getReturnPeriod());
			// Save new Batch
			batch = batchRepo.save(batch);
			APIResponse resp = gstr1SummaryDataAtGstn.findSummaryDataAtGstn(dto,
					groupCode);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Framework response APIResponse is {}", resp);
			}
			// update retry count column here
			batchRepo.updateRetryCount(Long.valueOf(retryCount), batch.getId());

			if (!resp.isSuccess()) {

				String errorCode = resp.getError().getErrorCode();
				String errorDesc = resp.getError().getErrorDesc();

				LOGGER.error(
						"failed to get Get Gstr1 Summary from Gstn."
								+ " ErrorCode {}, Error Description {}",
						errorCode, errorDesc);
				batchUtil.updateById(batch.getId(), APIConstants.FAILED,
						errorCode, errorDesc, false);
				apiResp = errorDesc;
			} else {
				saveJsonAsRecords(resp.getResponse(), groupCode, dto, batch,
						APIConstants.GSTR1.toUpperCase());
				apiResp = "SUCCESS";
			}

		} catch (Exception ex) {
			batchUtil.updateById(batch.getId(), APIConstants.FAILED, null, null,
					false);
			String msg = "failed to parse the response";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return apiResp;

	}

	@Override
	public String getGstr1ASummary(Gstr1GetInvoicesReqDto dto,
			String groupCode) {

		String apiResp = null;
		GetAnx1BatchEntity batch = null;
		try {
			int retryCount = 0;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getSummary method called with dto {}", dto);
			}
			String type = APIIdentifiers.GSTR1_GETSUM;
			batch = batchUtil.makeBatchGstr1(dto, type,
					APIConstants.GSTR1A.toUpperCase());
			batchRepo.softlyDelete(type.toUpperCase(),
					APIConstants.GSTR1A.toUpperCase(), dto.getGstin(),
					dto.getReturnPeriod());
			batch = batchRepo.save(batch);
			APIResponse resp = gstr1SummaryDataAtGstn
					.findGstr1ASummaryDataAtGstn(dto, groupCode);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Framework response APIResponse is {}", resp);
			}
			// update retry count column here
			batchRepo.updateRetryCount(Long.valueOf(retryCount), batch.getId());
			if (!resp.isSuccess()) {
				String errorCode = resp.getError().getErrorCode();
				String errorDesc = resp.getError().getErrorDesc();

				LOGGER.error(
						"failed to get Get Gstr1 Summary from Gstn."
								+ " ErrorCode {}, Error Description {}",
						errorCode, errorDesc);
				batchUtil.updateById(batch.getId(), APIConstants.FAILED,
						errorCode, errorDesc, false);
				apiResp = errorDesc;
			} else {
				saveJsonAsRecords(resp.getResponse(), groupCode, dto, batch,
						APIConstants.GSTR1A.toUpperCase());
				apiResp = "SUCCESS";
			}

		} catch (Exception ex) {
			batchUtil.updateById(batch.getId(), APIConstants.FAILED, null, null,
					false);
			String msg = "failed to parse the response";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		return apiResp;
	}

	@Override
	public String saveJsonAsRecords(String apiResp, String groupCode,
			Gstr1GetInvoicesReqDto dto, GetAnx1BatchEntity batch,
			String returnType) {

		String staus = APIConstants.FAILED;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 Summary GSTN GET call Json is {} ", apiResp);
		}

		if (apiResp != null && !apiResp.trim().isEmpty()) {

			batchPayloadHelper.dumpGetResponsePayload(groupCode, dto.getGstin(),
					dto.getReturnPeriod(), batch.getId(), apiResp,
					APIConstants.SYSTEM);

			if (returnType
					.equalsIgnoreCase(APIConstants.GSTR1A.toUpperCase())) {

				List<GetGstr1SummaryEntity> entities = gstr1SummaryDataParser
						.parseGstr1ASummaryData(apiResp, batch.getId());
				if (!entities.isEmpty()) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Gstr1 Summary GSTN GET call data persisting into DB.");
					}
					gstr1SummaryDataParser.saveGstr1ASummaryData(entities,
							groupCode, dto.getGstin(), dto.getReturnPeriod());
				}
				batchUtil.updateById(batch.getId(), APIConstants.SUCCESS, null,
						null, false);
				staus = APIConstants.SUCCESS;

			} else {
				List<GetGstr1SummaryEntity> entities = gstr1SummaryDataParser
						.parseSummaryData(apiResp, batch.getId());
				if (!entities.isEmpty()) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Gstr1 Summary GSTN GET call data persisting into DB.");
					}
					gstr1SummaryDataParser.saveSummaryData(entities, groupCode,
							dto.getGstin(), dto.getReturnPeriod());
				}
				batchUtil.updateById(batch.getId(), APIConstants.SUCCESS, null,
						null, false);
				staus = APIConstants.SUCCESS;
			}
		} else {
			batchUtil.updateById(batch.getId(),
					APIConstants.SUCCESS_WITH_NO_DATA, null, null, false);
			staus = APIConstants.SUCCESS_WITH_NO_DATA;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("No response from gstn");
			}
		}

		return staus;

	}

	// removing retry count for Gstr6 Determination Gstr1 call (Bhavya)
	public String getGstr1SummaryCall(Gstr1GetInvoicesReqDto dto,
			String groupCode) {
		String status = APIConstants.FAILED;
		String apiResp = null;
		GetAnx1BatchEntity batch = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getSummary method called with dto {}", dto);
			}
			String type = APIIdentifiers.GSTR1_GETSUM;
			batch = batchUtil.makeBatchGstr1(dto, type,
					APIConstants.GSTR1.toUpperCase());
			TenantContext.setTenantId(groupCode);
			// InActiveting Previous Batch Records
			batchRepo.softlyDelete(type.toUpperCase(),
					APIConstants.GSTR1.toUpperCase(), dto.getGstin(),
					dto.getReturnPeriod());
			// Save new Batch
			batch = batchRepo.save(batch);
			APIResponse resp = gstr1SummaryDataAtGstn.findSummaryDataAtGstn(dto,
					groupCode);
			if (resp != null && !resp.isSuccess()) {
				LOGGER.error("failed to get Get Gstr1 Summary from Gstn");
				String errorCode = resp.getError() != null
						? resp.getError().getErrorCode() : "";
				String errorDesc = resp.getError() != null
						? resp.getError().getErrorDesc() : "";
				batchUtil.updateById(batch.getId(), APIConstants.FAILED,
						errorCode, errorDesc, false);
			} else {
				apiResp = resp != null ? resp.getResponse() : null;
			}
			if (apiResp != null) {
				status = saveJsonAsRecords(apiResp, groupCode, dto, batch,
						APIConstants.GSTR1.toUpperCase());
			} else {
				LOGGER.error(
						"No response received for Gstr1 Summary GET from GSTN.");
			}
		} catch (Exception ex) {
			batchUtil.updateById(batch.getId(), APIConstants.FAILED, null, null,
					false);
			String msg = "failed to parse the response";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		return status;
	}

}
