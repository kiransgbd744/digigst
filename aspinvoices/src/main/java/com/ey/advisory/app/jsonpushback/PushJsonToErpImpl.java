package com.ey.advisory.app.jsonpushback;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.erp.Anx1ErrorDocsRevIntegrationReqDto;
import com.ey.advisory.app.services.jobs.erp.AnxErpScenarioControlHelper;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

@Component("PushJsonToErpImpl")
@Slf4j
public class PushJsonToErpImpl implements PushJsonToErp {

	@Autowired
	@Qualifier("PushJsonToErpDaoImpl")
	private PushJsonToErpDao pushJsonToErpDao;

	@Autowired
	private DocRepository outDocRepo;

	@Autowired
	@Qualifier("HttpPushToErpImpl")
	private HttpPushToErp httpPush;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupRepo;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	@Autowired
	@Qualifier("AnxErpScenarioControlHelperImpl")
	private AnxErpScenarioControlHelper erpScenarioControlHelper;

	@Override
	public void pushErrorRecordJson(Long batchId, String gstin) {
		List<BatchErrorResponseDto> list = pushJsonToErpDao
				.getErrorRecords(batchId, gstin);

		Map<String, List<BatchErrorResponseDto>> map = list.stream().collect(
				Collectors.groupingBy(BatchErrorResponseDto::getDocKey));

		map.forEach((k, v) -> processErrorList(v));
	}

	private void processErrorList(List<BatchErrorResponseDto> responseList) {
		String errorDesc = responseList.stream().map(o -> o.getErrorDesc())
				.collect(Collectors.joining(", "));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("error code " + errorDesc);
		}
		BatchErrorResponseDto dto = responseList.get(0);
		if (errorDesc.length() > 1000)
			errorDesc = errorDesc.substring(0, 1000);
		JsonPushBackDto pushBackJson = createJsonPushBackDto(errorDesc, dto);
		httpPush.pushToErp(pushBackJson);

	}

	private JsonPushBackDto createJsonPushBackDto(String errorDesc,
			BatchErrorResponseDto dto) {
		JsonPushBackDto jsonPushBackDto = new JsonPushBackDto();
		InvoiceDetails invoiceDetails = new InvoiceDetails();
		Infres infres = new Infres();
		InvoiceLevelResponse invLevelResp = new InvoiceLevelResponse();

		invLevelResp.setErrorCd("ERRDIGIGST");
		invLevelResp.setInvoiceNumber(dto.getDocNo());
		invLevelResp.setBatchID("");
		invLevelResp.setAdminUnit("");
		invLevelResp.setBranchOffice("");
		invLevelResp.setPrincipalOffice("");
		invLevelResp.setRecordType("Su");
		invLevelResp.setInvoiceStatus("Not OK");
		invLevelResp.setErrorMsg(errorDesc);

		infres.setAcknowledgementNumber("");
		infres.setAt("");
		infres.setBatchId("");
		infres.setDealerID("9999");
		infres.setFilingPeriod(dto.getRetPeriod());
		infres.setGstinId(dto.getSuppGstin());
		infres.setInvoiceLevelResponse(invLevelResp);
		infres.setMessageID("");
		infres.setMessageType("INF");
		infres.setResponseErrorFileGenerationTimestamp(
				Timestamp.valueOf(LocalDateTime.now()));
		infres.setStatus("R");

		invoiceDetails.setInfres(infres);
		jsonPushBackDto.setInvoiceDetails(invoiceDetails);
		return jsonPushBackDto;
	}

	@Override
	public Integer erpErrorDocsToErp(Anx1ErrorDocsRevIntegrationReqDto dto) {
		Long scenarioId = 0l;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ErrorDocsToErp method called with reqDto {} ", dto);
		}
		try {
			String destinationName = dto.getDestinationName();
			String groupcode = dto.getGroupcode();
			String gstin = dto.getGstin();

			TenantContext.setTenantId(groupcode);

			/*if (dto.getScenarioId() != null) {
				scenarioId = dto.getScenarioId();
			}*/

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Rev Integ Destination {} call started.",
						destinationName);
			}

			List<Object[]> objs = null;
			// fetching all the outward/inward error docs
			if (destinationName.equals(APIConstants.ASP_ERP_OUTWARD_ERROR)) {
				Pair<Long, Long> range = erpScenarioControlHelper.getRange(
						gstin, scenarioId, new OutwardTransDocument());
				LOGGER.error("RANGE IS {} ",range);
				if (range != null) {
					List<BatchErrorResponseDto> list = pushJsonToErpDao
							.getAspErrorRecords(gstin, range.getValue0(), range.getValue1());
					LOGGER.error("list size of asp errored invoice is {} ",list.size());
					Map<String, List<BatchErrorResponseDto>> map = list.stream().collect(
							Collectors.groupingBy(BatchErrorResponseDto::getDocKey));

					map.forEach((k, v) -> processErrorList(v));
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Unexpected Eror", ex);
			return null;
		}

		return null;
	}

	@Override
	public void pushErrorRecordJson(Long minId, Long maxId, String gstin) {
		List<BatchErrorResponseDto> list = pushJsonToErpDao
				.getAspErrorRecords(gstin, minId, maxId);
		LOGGER.error("the size of the response dto is {}", list.size());
		Map<String, List<BatchErrorResponseDto>> map = list.stream().collect(
				Collectors.groupingBy(BatchErrorResponseDto::getDocKey));

		map.forEach((k, v) -> processErrorList(v));
		
	}

}
