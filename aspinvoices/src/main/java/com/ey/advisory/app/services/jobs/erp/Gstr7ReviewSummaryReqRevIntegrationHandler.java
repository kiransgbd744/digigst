/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.daos.client.Gstr7PopupRecordsFetchDaoImpl;
import com.ey.advisory.app.data.daos.client.Gstr7ProcessedRecordsFetchDaoImpl;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7ProcessedRepository;
import com.ey.advisory.app.data.services.anx1.Gstr7ReviewSummaryFetchService;
import com.ey.advisory.app.data.views.client.Gstr7ReviewSummaryRespDto;
import com.ey.advisory.app.docs.dto.erp.Gstr7DiffummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr7ProcessAndReviewSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr7ProcessSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr7ProcessSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr7ReverseIntgDiffSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr7ReverseIntgReviewSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr7ReverseIntgTable3SummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr7ReverseIntgTable4SummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr7ReviewHeaderSummaryRequestDto;
import com.ey.advisory.app.docs.dto.erp.Gstr7ReviewSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr7Table3SummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr7Table4SummaryItemDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr7PopupRecordsRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr7ProcessedRecordsRespDto;
import com.ey.advisory.app.gstr3b.Gstr3BLiabilitySetOffService;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva
 *
 */
@Slf4j
@Service("Gstr7ReviewSummaryReqRevIntegrationHandler")
public class Gstr7ReviewSummaryReqRevIntegrationHandler {

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	@Autowired
	@Qualifier("GstnSummaryErpSectionService")
	private GstnSummaryErpSectionService gstnSummaryErpSectionService;

	@Autowired
	@Qualifier("Gstr7ReviewSummaryFetchService")
	Gstr7ReviewSummaryFetchService gstr7ReviewSummaryFetchService;

	@Autowired
	@Qualifier("Gstr7PopupRecordsFetchDaoImpl")
	private Gstr7PopupRecordsFetchDaoImpl gstr1ProcessedRecordsFetchDao;

	@Autowired
	@Qualifier("ErpScenarioPermissionRepository")
	private ErpScenarioPermissionRepository permRepository;

	@Autowired
	private DestinationConnectivity destinationConn;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepo;

	@Autowired
	private AnxErpBatchHandler erpBatchHandler;

	@Autowired
	@Qualifier("Gstr3BLiabilitySetOffServiceImpl")
	private Gstr3BLiabilitySetOffService gstr3BLaibilityService;

	@Autowired
	@Qualifier("ErpEventsScenarioPermissionRepository")
	private ErpEventsScenarioPermissionRepository erpEventsScenPermiRep;

	@Autowired
	@Qualifier("Gstr7ProcessedRepository")
	private Gstr7ProcessedRepository gstr7ProcessedRepository;

	@Autowired
	@Qualifier("Gstr7ProcessedRecordsFetchDaoImpl")
	private Gstr7ProcessedRecordsFetchDaoImpl gstr7ProcessedRecordsFetchService;

	Integer respcode = 0;

	public Integer reviewSummaryRequestToErp(
			RevIntegrationScenarioTriggerDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"reviewSummaryRequestToErp method called with reqDto {}",
					dto);
		}
		try {
			// String tenantId = TenantContext.getTenantId();
			String destinationName = dto.getDestinationName();
			Long scenarioId = dto.getScenarioId();
			String groupcode = dto.getGroupcode();
			String gstin = dto.getGstin();

			Long entityId = dto.getEntityId();
			String stateCode = gstin.substring(0, 2);

			String stateName = statecodeRepository
					.findStateNameByCode(stateCode);
			Optional<EntityInfoEntity> entity = entityRepo.findById(entityId);
			String entityName = null;
			String entityPan = null;
			String companyCode = null;
			if (entity.isPresent()) {
				entityName = entity.get().getEntityName();
				entityPan = entity.get().getPan();
				companyCode = entity.get().getCompanyHq();
			}
			List<String> uniqueGstins = new ArrayList<>();
			uniqueGstins.add(gstin);
			// we are using common dto for gstr1 nad gstr7
			Gstr1ProcessedRecordsReqDto gstr1ProcessedRecordsReqDto = new Gstr1ProcessedRecordsReqDto();
			gstr1ProcessedRecordsReqDto.setEntityId(Arrays.asList(entityId));
			gstr1ProcessedRecordsReqDto.setGSTIN(uniqueGstins);
			Map<String, List<String>> gstinmap = new HashMap<>();
			gstinmap.put("GSTIN", Arrays.asList(gstin));
			gstr1ProcessedRecordsReqDto.setDataSecAttrs(gstinmap);
			List<Gstr7ProcessedRecordsRespDto> entityData = gstr7ProcessedRecordsFetchService
					.loadGstr7ProcessedRecords(gstr1ProcessedRecordsReqDto);
			List<String> returnPeriod = gstr7ProcessedRepository
					.ReturnPeriodBygstin(gstin);
			Set<String> returnPeriodSet = new HashSet<String>(returnPeriod);
			defaultValueforCan(returnPeriodSet, gstin, entityData.get(0),
					entityData);

			// return period is used in query itself.
			Gstr7ReviewHeaderSummaryRequestDto reviewSummeryreqDto = convertProcessDocsAsDtos(
					gstin, entityName, entityPan, companyCode, stateName,
					entityData, entityId);
			long currentBatchSize = 0;
			if (reviewSummeryreqDto.getProcessAndReviewSummary() != null
					&& !reviewSummeryreqDto.getProcessAndReviewSummary()
							.isEmpty()) {
				currentBatchSize = currentBatchSize + reviewSummeryreqDto
						.getProcessAndReviewSummary().size();
			}

			Long gstinId = gstnDetailRepo.findIdByGstin(gstin);

			// Get erp id based on gstin id,scenariodId and entity Id

			ErpScenarioPermissionEntity permEntity = permRepository
					.findByScenarioIdAndGstinIdAndErpIdAndEntityIdAndIsDeleteFalse(
							dto.getScenarioId(), gstinId, dto.getErpId(),
							entityId);

			AnxErpBatchEntity batch = erpBatchHandler.createErpBatch(groupcode,
					entityId, gstin, destinationName, scenarioId,
					currentBatchSize, null, ERPConstants.BACKGROUND_BASED_JOB,
					permEntity.getErpId(), null,
					APIConstants.SYSTEM.toUpperCase());

			respcode = destinationConn.pushToErp(reviewSummeryreqDto,
					"Gstr7ReviewHeaderSummaryRequestDto", batch);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Batch is created as {} ", batch);
			}

			respcode = respcode != null ? respcode : 0;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination {} -> Response code is {} ",
						destinationName, respcode);
			}

			return respcode;
		} catch (Exception ex) {
			LOGGER.error("Unexpected Eror", ex);
			return null;
		}
	}

	public Gstr7ReviewHeaderSummaryRequestDto convertProcessDocsAsDtos(
			String gstin, String entityName, String entityPan,
			String companyCode, String stateName,
			List<Gstr7ProcessedRecordsRespDto> processSumItemDtos,
			Long entiTyid) {
		Gstr7ReviewHeaderSummaryRequestDto requestDto = new Gstr7ReviewHeaderSummaryRequestDto();

		List<Gstr7ProcessAndReviewSummaryDto> processAndRevSumList = new ArrayList<>();

		if (processSumItemDtos != null && !processSumItemDtos.isEmpty()) {

			processSumItemDtos.forEach(processSumItemDto -> {
				if (processSumItemDto.getGstin() != null
						&& processSumItemDto.getReturnPerid() != null) {
					Gstr7ProcessAndReviewSummaryDto reqSumDto = new Gstr7ProcessAndReviewSummaryDto();
					if (entityName != null) {
						reqSumDto.setEntity(StringUtils.upperCase(entityName));
					}
					reqSumDto.setEntityPan(entityPan);
					reqSumDto.setGstinNum(processSumItemDto.getGstin());
					reqSumDto.setReturnPeriod(
							processSumItemDto.getReturnPerid());
					reqSumDto.setProcessSummary(
							processSumConvert(processSumItemDto));
					reqSumDto.setReviewSummary(
							reviewSummeryconvert(processSumItemDto.getGstin(),
									processSumItemDto.getReturnPerid(),
									Arrays.asList(entiTyid)));
					reqSumDto.setTable3(table3(processSumItemDto.getGstin(),
							processSumItemDto.getReturnPerid(),
							Arrays.asList(entiTyid)));
					reqSumDto.setTable4(table4(processSumItemDto.getGstin(),
							processSumItemDto.getReturnPerid(),
							Arrays.asList(entiTyid)));
					reqSumDto.setDiff(diff(reqSumDto.getReviewSummary()));
					processAndRevSumList.add(reqSumDto);
				}
			});
		}
		requestDto.setProcessAndReviewSummary(processAndRevSumList);
		return requestDto;
	}

	public Gstr7ReverseIntgDiffSummaryDto diff(
			Gstr7ReverseIntgReviewSummaryDto apiresdto) {
		List<Gstr7DiffummaryItemDto> diffList = new ArrayList<>();
		Gstr7ReverseIntgDiffSummaryDto resp = new Gstr7ReverseIntgDiffSummaryDto();
		List<Gstr7ReviewSummaryItemDto> items = apiresdto.getItems();
		for (Gstr7ReviewSummaryItemDto reqdo : items) {
			Gstr7DiffummaryItemDto diff = new Gstr7DiffummaryItemDto();
			diff.setSection(reqdo.getTableSection());
			diff.setDiffCount(reqdo.getDiffCount());
			diff.setDiffTotalAmt(reqdo.getDiffTotalAmt());
			diff.setDiffCgst(reqdo.getDiffCgst());
			diff.setDiffSgst(reqdo.getDiffSgst());
			diff.setDiffIgst(reqdo.getDiffIgst());
			diffList.add(diff);
		}
		resp.setItems(diffList);
		return resp;

	}

	public Gstr7ProcessSummaryDto processSumConvert(
			Gstr7ProcessedRecordsRespDto apiresdto) {
		Gstr7ProcessSummaryDto processDto = new Gstr7ProcessSummaryDto();
		List<Gstr7ProcessSummaryItemDto> dtoList = new ArrayList<>();
		Gstr7ProcessSummaryItemDto dto = new Gstr7ProcessSummaryItemDto();
		dto.setCgst(apiresdto.getCgst());
		dto.setCont(apiresdto.getCount());
		dto.setIgst(apiresdto.getIgst());
		dto.setSaveStatus(apiresdto.getSaveStatus());
		dto.setTimeStamp(apiresdto.getSaveDateTime());
		dto.setSgst(apiresdto.getSgst());
		dto.setTotalAmt(apiresdto.getTotalAmount());
		dtoList.add(dto);
		processDto.setItems(dtoList);
		return processDto;

	}

	private Gstr7ReverseIntgReviewSummaryDto reviewSummeryconvert(String gstin,
			String returnPeriod, List<Long> entityId) {
		Map<String, List<String>> gstinmap = new HashMap<>();
		gstinmap.put("GSTIN", Arrays.asList(gstin));
		Gstr2AProcessedRecordsReqDto reqDto = new Gstr2AProcessedRecordsReqDto();
		reqDto.setEntityId(entityId);
		reqDto.setRetunPeriod(returnPeriod);
		reqDto.setDataSecAttrs(gstinmap);
		List<Gstr7ReviewSummaryRespDto> gstr7GstinDashboard = gstr7ReviewSummaryFetchService
				.getReviewSummary(reqDto);

		Gstr7ReverseIntgReviewSummaryDto reviewSummary = new Gstr7ReverseIntgReviewSummaryDto();
		List<Gstr7ReviewSummaryItemDto> dtoList = new ArrayList<>();

		for (Gstr7ReviewSummaryRespDto reqdo : gstr7GstinDashboard) {

			Gstr7ReviewSummaryItemDto dto = new Gstr7ReviewSummaryItemDto();
			dto.setAspCgst(reqdo.getAspCgst());
			dto.setAspCount(reqdo.getAspCount());
			dto.setAspIgst(reqdo.getAspIgst());
			dto.setAspSgst(reqdo.getAspSgst());
			dto.setAspTotalAmt(reqdo.getAspTotalAmount());
			dto.setDiffCgst(reqdo.getDiffCgst());
			dto.setDiffCount(reqdo.getDiffCount());
			dto.setDiffIgst(reqdo.getDiffIgst());
			dto.setDiffSgst(reqdo.getDiffSgst());
			dto.setDiffTotalAmt(reqdo.getDiffTotalAmount());
			dto.setGstnCgst(reqdo.getGstnCgst());
			dto.setGstnSgst(reqdo.getGstnSgst());
			dto.setGstnIgst(reqdo.getGstnIgst());
			dto.setGstnTotalAmt(reqdo.getGstnTotalAmount());
			dto.setGstnCount(reqdo.getGstnCount());
			dto.setTableSection(reqdo.getSection());
			dtoList.add(dto);

		}
		reviewSummary.setItems(dtoList);
		return reviewSummary;

	}

	private Gstr7ReverseIntgTable3SummaryDto table3(String gstin,
			String returnPeriod, List<Long> entityId) {
		Map<String, List<String>> gstinmap = new HashMap<>();
		gstinmap.put("GSTIN", Arrays.asList(gstin));
		Gstr1ProcessedRecordsReqDto reqDto = new Gstr1ProcessedRecordsReqDto();
		reqDto.setType("Table-3");
		reqDto.setEntityId(entityId);
		reqDto.setRetunPeriod(returnPeriod);
		reqDto.setDataSecAttrs(gstinmap);
		List<Gstr7PopupRecordsRespDto> processedRespDtos = gstr1ProcessedRecordsFetchDao
				.loadGstr7PopupSummaryRecords(reqDto);

		Gstr7ReverseIntgTable3SummaryDto reviewSummary = new Gstr7ReverseIntgTable3SummaryDto();
		List<Gstr7Table3SummaryItemDto> dtoList = new ArrayList<>();

		for (Gstr7PopupRecordsRespDto reqdo : processedRespDtos) {

			Gstr7Table3SummaryItemDto dto = new Gstr7Table3SummaryItemDto();
			dto.setAmountPaid(reqdo.getAmount());
			dto.setCgst(reqdo.getCgst());
			dto.setIgst(reqdo.getIgst());
			dto.setSgst(reqdo.getSgst());
			dto.setGstin(reqdo.getGstin());
			dtoList.add(dto);
		}
		reviewSummary.setItems(dtoList);
		return reviewSummary;

	}

	private Gstr7ReverseIntgTable4SummaryDto table4(String gstin,
			String returnPeriod, List<Long> entityId) {
		Map<String, List<String>> gstinmap = new HashMap<>();
		gstinmap.put("GSTIN", Arrays.asList(gstin));
		Gstr1ProcessedRecordsReqDto reqDto = new Gstr1ProcessedRecordsReqDto();
		reqDto.setType("Table-4");
		reqDto.setEntityId(entityId);
		reqDto.setRetunPeriod(returnPeriod);
		reqDto.setDataSecAttrs(gstinmap);
		List<Gstr7PopupRecordsRespDto> processedRespDtos = gstr1ProcessedRecordsFetchDao
				.loadGstr7PopupSummaryRecords(reqDto);
		Gstr7ReverseIntgTable4SummaryDto reviewSummary = new Gstr7ReverseIntgTable4SummaryDto();
		List<Gstr7Table4SummaryItemDto> dtoList = new ArrayList<>();
		for (Gstr7PopupRecordsRespDto reqdo : processedRespDtos) {
			Gstr7Table4SummaryItemDto dto = new Gstr7Table4SummaryItemDto();
			dto.setMonth(reqdo.getOdMonth());
			dto.setOriginalgstin(reqdo.getOdGstin());
			dto.setOrgAmountPaid(reqdo.getOdAmount());
			dto.setRevisedgstin(reqdo.getRdGstin());
			dto.setRevAmountPaid(reqdo.getRdAmount());
			dto.setCgst(reqdo.getRdCgst());
			dto.setIgst(reqdo.getRdIgst());
			dto.setSgst(reqdo.getRdSgst());
			dtoList.add(dto);
		}
		reviewSummary.setItems(dtoList);
		return reviewSummary;

	}

	private static List<Gstr7ProcessedRecordsRespDto> defaultValueforCan(
			Set<String> returnPeriodlist, String gstin,
			Gstr7ProcessedRecordsRespDto dto,
			List<Gstr7ProcessedRecordsRespDto> entityData) {
		List<String> returnperiodData = new ArrayList<>();
		for (Gstr7ProcessedRecordsRespDto data : entityData) {
			returnperiodData.add(data.getReturnPerid());
		}
		String status = "NOT INITIATED";
		for (String returnPeriod : returnPeriodlist) {
			Gstr7ProcessedRecordsRespDto dummy = new Gstr7ProcessedRecordsRespDto();
			if (returnperiodData.contains(returnPeriod))
				continue;
			dummy.setGstin(gstin);
			dummy.setReturnPerid(returnPeriod);
			dummy.setCount(new BigInteger("0"));
			dummy.setIgst(new BigDecimal("0.0"));
			dummy.setCgst(new BigDecimal("0.0"));
			dummy.setSgst(new BigDecimal("0.0"));
			dummy.setTotalAmount(new BigDecimal("0.0"));
			dummy.setAuthToken(dto.getAuthToken());
			dummy.setState(dto.getState());
			dummy.setSaveStatus(status);
			entityData.add(dummy);
		}
		return entityData;

	}
}
