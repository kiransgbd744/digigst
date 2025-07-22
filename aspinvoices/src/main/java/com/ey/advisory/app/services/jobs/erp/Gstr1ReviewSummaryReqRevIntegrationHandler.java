/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.daos.client.simplified.BasicEcomSupScreenSectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.UpdatedMofifiedDateFetchDaoImpl;
import com.ey.advisory.app.data.repositories.client.ErpCompanyCodeMappingRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.docs.dto.erp.Advp111aPopupDto;
import com.ey.advisory.app.docs.dto.erp.Advp111bPopupDto;
import com.ey.advisory.app.docs.dto.erp.Advp211aPopupDto;
import com.ey.advisory.app.docs.dto.erp.Advp211bPopupDto;
import com.ey.advisory.app.docs.dto.erp.B2csPopupDto;
import com.ey.advisory.app.docs.dto.erp.B2csaPopupDto;
import com.ey.advisory.app.docs.dto.erp.DiffPopupProcSumItemRevDto;
import com.ey.advisory.app.docs.dto.erp.DiffProcSummaryRevDto;
import com.ey.advisory.app.docs.dto.erp.DocPopupDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ProcessAndReviewSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ProcessSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ProcessSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ReverseIntgReviewSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ReviewHeaderSummaryRequestDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ReviewSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ReviewSummaryRequestItemDto;
import com.ey.advisory.app.docs.dto.erp.HsnPopupDto;
import com.ey.advisory.app.docs.dto.erp.NilPopupDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

/**
 * @author Umesha M
 *
 */
@Service("Gstr1ReviewSummaryReqRevIntegrationHandler")
public class Gstr1ReviewSummaryReqRevIntegrationHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ReviewSummaryReqRevIntegrationHandler.class);

	@Autowired
	@Qualifier("Gstr1ReviewSummaryRequestDocsImpl")
	private Gstr1ReviewSummaryRequestDocs gstr1ReviewSummaryRequestDocs;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	@Autowired
	@Qualifier("GstnSummaryErpSectionService")
	private GstnSummaryErpSectionService gstnSummaryErpSectionService;

	Integer respcode = 0;

	@Autowired
	private DestinationConnectivity destinationConn;

	@Autowired
	private AnxErpBatchHandler erpBatchHandler;

	@Autowired
	@Qualifier("ErpScenarioPermissionRepository")
	private ErpScenarioPermissionRepository permRepository;

	@Autowired
	@Qualifier("UpdatedMofifiedDateFetchDaoImpl")
	private UpdatedMofifiedDateFetchDaoImpl dateFetchDao;

	@Autowired
	@Qualifier("ErpCompanyCodeMappingRepository")
	private ErpCompanyCodeMappingRepository companyCodeRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepo;

	@Autowired
	@Qualifier("BasicEcomSupScreenSectionDaoImpl")
	BasicEcomSupScreenSectionDaoImpl basicDocSummaryDao;

	public Integer reviewSummaryRequestToErp(
			RevIntegrationScenarioTriggerDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"reviewSummaryRequestToErp method called with reqDto {}",
					dto);
		}
		try {
			TenantContext.getTenantId();
			String destinationName = dto.getDestinationName();
			Long scenarioId = dto.getScenarioId();
			String groupCode = dto.getGroupcode();
			String gstin = dto.getGstin();
			Long entityId = dto.getEntityId();
			String stateCode = gstin.substring(0, 2);
			Long erpId = dto.getErpId();

			// Getting State Name based on state code
			String stateName = statecodeRepository
					.findStateNameByCode(stateCode);
			String companyCode = companyCodeRepo.getCompanyCode(erpId,
					entityId);
			Optional<EntityInfoEntity> entity = entityRepo.findById(entityId);
			String entityName = null;
			String entityPan = null;

			if (entity.isPresent()) {
				entityName = entity.get().getEntityName();
				// entityName = entityName.length() <= 25 ? entityName :
				// entityName.substring(0, 25);
				entityPan = entity.get().getPan();
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr1ReviewSummaryReqRevIntegrationHandler "
								+ "gstin : {}, entityName : {}, entityPan : {}, "
								+ "entityId : {}, companycode :{},statename :{} ",
						gstin, entityName, entityPan, entityId, companyCode,
						stateName);
			}

			// commeted because gstin and return period is used in query itself.
			Gstr1ReviewHeaderSummaryRequestDto reqDto = convertProcessDocsAsDtos(
					gstin, entityName, entityPan, entityId, companyCode,
					stateName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 reverse integration data -> {} ", reqDto);
			}
			long currentBatchSize = 0;
			if (reqDto.getProcessAndReviewSummary() != null
					&& !reqDto.getProcessAndReviewSummary().isEmpty()) {
				currentBatchSize = currentBatchSize
						+ reqDto.getProcessAndReviewSummary().size();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 reverse integration data count -> {} ",
							currentBatchSize);
				}
				// Get gstin based on gst name
				Long gstinId = gstnDetailRepo.findIdByGstin(gstin);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1ReviewSummaryReqRevIntegrationHandler "
							+ "gstinID : {}", gstinId);
				}

				// Get erp id based on gstin id,scenariodId and entity Id
				ErpScenarioPermissionEntity permEntity = permRepository
						.findByScenarioIdAndGstinIdAndErpIdAndEntityIdAndIsDeleteFalse(
								dto.getScenarioId(), gstinId, erpId, entityId);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1ReviewSummaryReqRevIntegrationHandler "
							+ "permEntity : {}", permEntity);
				}
				if (permEntity != null) {
					AnxErpBatchEntity batchEntity = erpBatchHandler
							.createErpBatch(groupCode, entityId, gstin,
									destinationName, scenarioId,
									currentBatchSize, null,
									ERPConstants.BACKGROUND_BASED_JOB,
									permEntity.getErpId(), null,
									APIConstants.SYSTEM.toUpperCase());

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Gstr1ReviewSummaryReqRevIntegrationHandler "
										+ "Batch : {} ",
								batchEntity);
					}
					respcode = destinationConn.pushToErp(reqDto,
							"Gstr1ReviewHeaderSummaryRequestDto", batchEntity);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Gstr1ReviewSummaryReqRevIntegrationHandler "
										+ "respcode: {} ",
								batchEntity);
					}
				}

			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 reverse integration no data found");
				}
			}
			respcode = respcode != null ? respcode : 0;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination {} -> Response code is {} ",
						destinationName, respcode);
			}
			return respcode;
		} catch (Exception ex) {
			String msg = "Unexpected Error occured in Gstr1ReviewSummaryReqRevIntegrationHandler ";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public Gstr1ReviewHeaderSummaryRequestDto convertProcessDocsAsDtos(
			String gstin, String entityName, String entityPan, Long entityId,
			String companyCode, String stateName) {
		Gstr1ReviewHeaderSummaryRequestDto requestDto = new Gstr1ReviewHeaderSummaryRequestDto();

		List<Gstr1ProcessAndReviewSummaryDto> processAndRevSumList = new ArrayList<>();

		// Find Current Tax period
		LocalDate currentDate = LocalDate.now();
		int currentMonth = currentDate.getMonthValue();
		int currentYear = currentDate.getYear();
		StringBuilder currentTaxPeriod = new StringBuilder();
		if (currentMonth < 10) {
			currentTaxPeriod.append("0" + currentMonth);
		} else {
			currentTaxPeriod.append(currentMonth);
		}
		currentTaxPeriod.append(currentYear);
		String taxPeriod = currentTaxPeriod.toString();
		int derivedTaxPeriod = 0;
		if (taxPeriod != null) {
			derivedTaxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1ReviewSummaryReqRevIntegrationHandler "
					+ "derivedTaxPeriod : {}", derivedTaxPeriod);
		}

		List<Gstr1ReviewSummaryRequestItemDto> processSumItemDtos = gstr1ReviewSummaryRequestDocs
				.processSummary(gstin, derivedTaxPeriod, entityPan, stateName,
						entityName, companyCode);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr1ReviewSummaryReqRevIntegrationHandler "
							+ "processSumItemDtos : {}",
					processSumItemDtos.size());
		}

		if (processSumItemDtos != null && !processSumItemDtos.isEmpty()) {

			processSumItemDtos.forEach(processSumItemDto -> {
				if (processSumItemDto.getGstinNum() != null
						&& processSumItemDto.getRetPer() != null) {
					Gstr1ProcessAndReviewSummaryDto reqSumDto = new Gstr1ProcessAndReviewSummaryDto();
					if (entityName != null) {
						reqSumDto.setEntity(StringUtils.upperCase(entityName));
					}
					reqSumDto.setEntityPan(entityPan);
					reqSumDto.setCompanyCode(companyCode);
					reqSumDto.setGstinNum(processSumItemDto.getGstinNum());
					reqSumDto.setRetPer(processSumItemDto.getRetPer());

					// Process Summary
					Gstr1ProcessSummaryDto processSummary = new Gstr1ProcessSummaryDto();
					processSummary = processSummaryConversion(
							processSumItemDto);
					reqSumDto.setProcessSummary(processSummary);

					// Review Summary
					Gstr1ReverseIntgReviewSummaryDto revSumReqDto = getConvertReviewSummary(
							processSumItemDto.getGstinNum(),
							processSumItemDto.getRetPer(), entityName,
							entityPan, entityId, companyCode);
					reqSumDto.setReviewSummary(revSumReqDto);

					// Difference Popup in process summary screen
					DiffProcSummaryRevDto difProcSumRevDto = new DiffProcSummaryRevDto();
					List<DiffPopupProcSumItemRevDto> diffPopupSumItemRev = diffPopupSumItemRev(
							processSumItemDto.getGstinNum(),
							processSumItemDto.getRetPer(), entityName,
							entityPan, entityId, companyCode);

					Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev = mapDiffPopupSumItemRev(
							diffPopupSumItemRev);
					
					// ------------14,15 diff pop up reverse int --starts logic1----------
					Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
					request.setTaxPeriod(processSumItemDto.getRetPer());
					List<String> gstins = new ArrayList<>();
					gstins.add(gstin);
					Map<String, List<String>> dataSecAttrs = new HashMap<>();
					dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
					request.setDataSecAttrs(dataSecAttrs);

					List<Long> entityIds = new ArrayList<>();
					entityIds.add(entityId);
					request.setEntityId(entityIds);
					// --------------------14,15 diff pop up reverse int ends logic1---

					List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos = new ArrayList<>();
					b2bDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					b2baDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					b2clDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					b2claDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					exportsDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					exportsAmadDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					cdnrDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					cdnraDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					cdnurDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					cdnuraDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					b2csDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					b2csaDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					nilDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					advRecievedDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					advAdjustedDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					advRecievedADiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					advAdjustedADiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					hsnDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					docDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

				/*	difProcSumRevDto.setItem(difPopupProSumItemDtos);
					reqSumDto.setDiffPopup(difProcSumRevDto);*/
					
					//---table 14,15 diff pop up logic start2---------------
					
					diffPopUpFor14And15(reqSumDto, difProcSumRevDto, request,
							difPopupProSumItemDtos);
					//----------------14,15 diff pop up logic end 2----------------------------
					// B2cs Popup in Review Summary screen
					B2csPopupDto b2csPopupDto = gstr1ReviewSummaryRequestDocs
							.getB2csPopupDto(processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setB2csPopupDto(b2csPopupDto);

					// B2csa Popup in Review Summary screen
					B2csaPopupDto b2csaPopupDto = gstr1ReviewSummaryRequestDocs
							.getB2csaPopupDto(processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setB2csaPopupDto(b2csaPopupDto);

					// Hsn Popup in Review Summary Screen
					HsnPopupDto hsnPopupDto = gstr1ReviewSummaryRequestDocs
							.getHsnPopup(processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setHsnPopupDto(hsnPopupDto);

					// Doc Issue Popup in Review Summary Screen
					DocPopupDto docPopupDto = gstr1ReviewSummaryRequestDocs
							.getDocPopup(processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setDocPopupDto(docPopupDto);

					// Nil Non Pop up Screen
					NilPopupDto nilPopupDto = gstr1ReviewSummaryRequestDocs
							.getNilNonPopup(processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setNilPopupDto(nilPopupDto);

					// AT Advance Pop up screen
					Advp111aPopupDto advp111aPopupDto = gstr1ReviewSummaryRequestDocs
							.getAdvp111aPopupDto(
									processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setAdvp111aPopupDto(advp111aPopupDto);

					// TXPD Advance Pop up screen
					Advp111bPopupDto advp111bPopupDto = gstr1ReviewSummaryRequestDocs
							.getAdvp111bPopupDto(
									processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setAdvp111bPopupDto(advp111bPopupDto);

					// ATA Advance Pop up screen
					Advp211aPopupDto advp211aPopupDto = gstr1ReviewSummaryRequestDocs
							.getAdvp211aPopupDto(
									processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setAdvp211aPopupDto(advp211aPopupDto);

					// TXPDA Advance Pop up screen
					Advp211bPopupDto advp211bPopupDto = gstr1ReviewSummaryRequestDocs
							.getAdvp211bPopupDto(
									processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setAdvp211bPopupDto(advp211bPopupDto);

					processAndRevSumList.add(reqSumDto);
				}
			});
		}

		LocalDate erliarMonthDate = currentDate.minusMonths(1);
		int erliarMonth = erliarMonthDate.getMonthValue();
		int erliarYear = erliarMonthDate.getYear();
		StringBuilder erliarTaxPeriod = new StringBuilder();
		if (erliarMonth < 10) {
			erliarTaxPeriod.append("0" + erliarMonth);
		} else {
			erliarTaxPeriod.append(erliarMonth);
		}
		erliarTaxPeriod.append(erliarYear);
		String erliarTaxPeriodStr = erliarTaxPeriod.toString();
		int erliarDerivedTaxPeriod = 0;
		if (erliarTaxPeriodStr != null) {
			erliarDerivedTaxPeriod = GenUtil
					.convertTaxPeriodToInt(erliarTaxPeriodStr);
		}
	
		
		List<Gstr1ReviewSummaryRequestItemDto> erliarProcessSumItemDtos = gstr1ReviewSummaryRequestDocs
				.processSummary(gstin, erliarDerivedTaxPeriod, entityPan,
						stateName, entityName, companyCode);
		if (erliarProcessSumItemDtos != null
				&& !erliarProcessSumItemDtos.isEmpty()) {

			erliarProcessSumItemDtos.forEach(processSumItemDto -> {
				if (processSumItemDto.getGstinNum() != null
						&& processSumItemDto.getRetPer() != null) {
					Gstr1ProcessAndReviewSummaryDto reqSumDto = new Gstr1ProcessAndReviewSummaryDto();
					if (entityName != null) {
						reqSumDto.setEntity(StringUtils.upperCase(entityName));
					}
					reqSumDto.setEntityPan(entityPan);
					reqSumDto.setCompanyCode(companyCode);
					reqSumDto.setGstinNum(processSumItemDto.getGstinNum());
					reqSumDto.setRetPer(processSumItemDto.getRetPer());

					// Process Summary
					Gstr1ProcessSummaryDto processSummary = new Gstr1ProcessSummaryDto();
					processSummary = processSummaryConversion(
							processSumItemDto);
					reqSumDto.setProcessSummary(processSummary);

					// Review Summary
					Gstr1ReverseIntgReviewSummaryDto revSumReqDto = getConvertReviewSummary(
							processSumItemDto.getGstinNum(),
							processSumItemDto.getRetPer(), entityName,
							entityPan, entityId, companyCode);
					reqSumDto.setReviewSummary(revSumReqDto);

					// Difference Popup in process summary screen
					DiffProcSummaryRevDto difProcSumRevDto = new DiffProcSummaryRevDto();
					List<DiffPopupProcSumItemRevDto> diffPopupSumItemRev = diffPopupSumItemRev(
							processSumItemDto.getGstinNum(),
							processSumItemDto.getRetPer(), entityName,
							entityPan, entityId, companyCode);

					
					Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev = mapDiffPopupSumItemRev(
							diffPopupSumItemRev);
					
					// ------------14,15 diff pop up reverse int --starts logic3----------
					Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
					request.setTaxPeriod(processSumItemDto.getRetPer());
					List<String> gstins = new ArrayList<>();
					gstins.add(gstin);
					Map<String, List<String>> dataSecAttrs = new HashMap<>();
					dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
					request.setDataSecAttrs(dataSecAttrs);

					List<Long> entityIds = new ArrayList<>();
					entityIds.add(entityId);
					request.setEntityId(entityIds);
					
					
					// --------------------14,15 diff pop up reverse int ends logic3---
					List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos = new ArrayList<>();
					b2bDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					b2baDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					b2clDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					b2claDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					exportsDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					exportsAmadDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					cdnrDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					cdnraDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					cdnurDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					cdnuraDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					b2csDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					b2csaDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					nilDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					advRecievedDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					advAdjustedDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					advRecievedADiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					advAdjustedADiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					hsnDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);

					docDiffPopupProSums(mapDiffPopupSumItemRev,
							difPopupProSumItemDtos);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"controller is going outside erliarProcessSumItemDtos.forEach");
					}
					//14,15 diff pop up reverse int start logic4
					//getting previous month data-------------
					diffPopUpFor14And15(reqSumDto, difProcSumRevDto, request,
							difPopupProSumItemDtos);
					
					/*difProcSumRevDto.setItem(difPopupProSumItemDtos);
					reqSumDto.setDiffPopup(difProcSumRevDto);
*/
					// B2cs Popup in Review Summary screen
					B2csPopupDto b2csPopupDto = gstr1ReviewSummaryRequestDocs
							.getB2csPopupDto(processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setB2csPopupDto(b2csPopupDto);

					// B2csa Popup in Review Summary screen
					B2csaPopupDto b2csaPopupDto = gstr1ReviewSummaryRequestDocs
							.getB2csaPopupDto(processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setB2csaPopupDto(b2csaPopupDto);

					// Hsn Popup in Review Summary Screen
					HsnPopupDto hsnPopupDto = gstr1ReviewSummaryRequestDocs
							.getHsnPopup(processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setHsnPopupDto(hsnPopupDto);

					// Doc Issue Popup in Review Summary Screen
					DocPopupDto docPopupDto = gstr1ReviewSummaryRequestDocs
							.getDocPopup(processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setDocPopupDto(docPopupDto);

					// Nil Non Pop up Screen
					NilPopupDto nilPopupDto = gstr1ReviewSummaryRequestDocs
							.getNilNonPopup(processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setNilPopupDto(nilPopupDto);

					// AT Advance Pop up screen
					Advp111aPopupDto advp111aPopupDto = gstr1ReviewSummaryRequestDocs
							.getAdvp111aPopupDto(
									processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setAdvp111aPopupDto(advp111aPopupDto);

					// TXPD Advance Pop up screen
					Advp111bPopupDto advp111bPopupDto = gstr1ReviewSummaryRequestDocs
							.getAdvp111bPopupDto(
									processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setAdvp111bPopupDto(advp111bPopupDto);

					// ATA Advance Pop up screen
					Advp211aPopupDto advp211aPopupDto = gstr1ReviewSummaryRequestDocs
							.getAdvp211aPopupDto(
									processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setAdvp211aPopupDto(advp211aPopupDto);

					// TXPDA Advance Pop up screen
					Advp211bPopupDto advp211bPopupDto = gstr1ReviewSummaryRequestDocs
							.getAdvp211bPopupDto(
									processSumItemDto.getGstinNum(),
									processSumItemDto.getRetPer(), entityId);
					reqSumDto.setAdvp211bPopupDto(advp211bPopupDto);

					processAndRevSumList.add(reqSumDto);
				}
			});
		}
		requestDto.setProcessAndReviewSummary(processAndRevSumList);
		return requestDto;
	}

	private void diffPopUpFor14And15(Gstr1ProcessAndReviewSummaryDto reqSumDto,
			DiffProcSummaryRevDto difProcSumRevDto,
			Annexure1SummaryReqDto request,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> finalItemsDto = new ArrayList<>();
		List<Gstr1SummarySectionDto> result = basicDocSummaryDao
				.loadBasicSummarySection(request);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Size of result list: {}", result.size());
		}

		Map<String, List<Gstr1SummarySectionDto>> eySummaries = result
				.stream()
				.collect(Collectors
						.groupingBy(e -> e.getTaxDocType()));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Size of eySummaries map: {}",
					eySummaries.size());
		}
		//table 14 we are not reverse integrating bcz it is not present in the Diff pop up in summary screen


		List<Gstr1SummarySectionDto> tbl14ofOneList = eySummaries
				.get(GSTConstants.GSTR1_14I);
		if (tbl14ofOneList != null) {
			if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					" inside diff popup values of tbl14ofOneList");
			}
			table14And15DiffPopMappingToDto(finalItemsDto,
					tbl14ofOneList);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"inside diff popup values of tbl14ofOneList is null ");
				
			}
			table14And15DiffPopDefaultValuesMapping(finalItemsDto,GSTConstants.GSTR1_14I);
		}

		List<Gstr1SummarySectionDto> tbl14ofTwoList = eySummaries
				.get(GSTConstants.GSTR1_14II);
		if (tbl14ofTwoList != null) {
			if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					" inside diff popup values of tbl14ofTwoList");}
			table14And15DiffPopMappingToDto(finalItemsDto,
					tbl14ofTwoList);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"inside diff popup values of tbl14ofTwoList is null ");
				
			}
			table14And15DiffPopDefaultValuesMapping(finalItemsDto,GSTConstants.GSTR1_14II);
		}

	
			List<Gstr1SummarySectionDto> tbl14AmdofOneList = eySummaries
					.get(GSTConstants.GSTR1_14AI);
			if (tbl14AmdofOneList != null) {
				if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" inside diff popup values of tbl14AmdofOneList");}
				table14And15DiffPopMappingToDto(finalItemsDto,
						tbl14AmdofOneList);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"inside diff popup values of tbl14AmdofOneList is null ");
					
				}
				table14And15DiffPopDefaultValuesMapping(finalItemsDto,GSTConstants.GSTR1_14AI);
			}
		

		
			List<Gstr1SummarySectionDto> tbl14AmdofTwoList = eySummaries
					.get(GSTConstants.GSTR1_14AII);
			if (tbl14AmdofTwoList != null) {
				if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" inside diff popup values of tbl14AmdofTwoList");}
				table14And15DiffPopMappingToDto(finalItemsDto,
						tbl14AmdofTwoList);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"inside diff popup values of tbl14AmdofTwoList is null ");
					
				}
				table14And15DiffPopDefaultValuesMapping(finalItemsDto,GSTConstants.GSTR1_14AII);
			}
		

		
			List<Gstr1SummarySectionDto> tbl15SectionList = eySummaries
					.get(GSTConstants.GSTR1_15);
			if (tbl15SectionList != null) {
				if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" inside diff popup values of tbl15SectionList");}
				table14And15DiffPopMappingToDto(finalItemsDto,
						tbl15SectionList);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"inside diff popup values of tbl15SectionList is null ");
					
				}
				table14And15DiffPopDefaultValuesMapping(finalItemsDto,GSTConstants.GSTR1_15);
			}
		

	
			List<Gstr1SummarySectionDto> tbl15AmdofOneSecList = eySummaries
					.get(GSTConstants.GSTR1_15AI);
			if (tbl15AmdofOneSecList != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							" inside diff popup values of tbl15AmdofOneSecList");}
				table14And15DiffPopMappingToDto(finalItemsDto,
						tbl15AmdofOneSecList);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"inside diff popup values of tbl15AmdofOneSecList is null ");
					
				}
				table14And15DiffPopDefaultValuesMapping(finalItemsDto,GSTConstants.GSTR1_15AI);
			}
		

		
			List<Gstr1SummarySectionDto> tbl15AmdoftwoSecList = eySummaries
					.get(GSTConstants.GSTR1_15AII);
			if (tbl15AmdoftwoSecList != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							" inside diff popup values of tbl15AmdoftwoSecList");}
				table14And15DiffPopMappingToDto(finalItemsDto,
						tbl15AmdoftwoSecList);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"inside diff popup values of tbl15AmdoftwoSecList is null ");
					
				}
				table14And15DiffPopDefaultValuesMapping(finalItemsDto,GSTConstants.GSTR1_15AII);
			}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"controller is inside erliarProcessSumItemDtos.forEach mehod loop");
		}
				
		List<DiffPopupProcSumItemRevDto> combinedItems = new ArrayList<>();

		combinedItems.addAll(difPopupProSumItemDtos);
		combinedItems.addAll(finalItemsDto);
		difProcSumRevDto.setItem(combinedItems);
		reqSumDto.setDiffPopup(difProcSumRevDto);
	}

	private void b2bDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> b2bDiffPopupProSums = mapDiffPopupSumItemRev
				.get("B2B");

		if (b2bDiffPopupProSums != null && !b2bDiffPopupProSums.isEmpty()) {
			b2bDiffPopupProSums.forEach(b2bDiffPopupProSum -> {
				b2bDiffPopupProSum.setSection("B2B (Section 4, 6B, 6C)");
				difPopupProSumItemDtos.add(b2bDiffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("B2B (Section 4, 6B, 6C)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void b2baDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> b2baDiffPopupProSums = mapDiffPopupSumItemRev
				.get("B2BA");

		if (b2baDiffPopupProSums != null && !b2baDiffPopupProSums.isEmpty()) {
			b2baDiffPopupProSums.forEach(b2baDiffPopupProSum -> {
				b2baDiffPopupProSum.setSection("B2BA (Section 9A)");
				difPopupProSumItemDtos.add(b2baDiffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("B2BA (Section 9A)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void b2clDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> b2baDiffPopupProSums = mapDiffPopupSumItemRev
				.get("B2CL");

		if (b2baDiffPopupProSums != null && !b2baDiffPopupProSums.isEmpty()) {
			b2baDiffPopupProSums.forEach(b2baDiffPopupProSum -> {
				b2baDiffPopupProSum.setSection("B2CL (Section 5)");
				difPopupProSumItemDtos.add(b2baDiffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("B2CL (Section 5)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void b2claDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> b2baDiffPopupProSums = mapDiffPopupSumItemRev
				.get("B2CLA");

		if (b2baDiffPopupProSums != null && !b2baDiffPopupProSums.isEmpty()) {
			b2baDiffPopupProSums.forEach(b2baDiffPopupProSum -> {
				b2baDiffPopupProSum.setSection("B2CLA (Section 9A)");
				difPopupProSumItemDtos.add(b2baDiffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("B2CLA (Section 9A)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void exportsDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> b2baDiffPopupProSums = mapDiffPopupSumItemRev
				.get("EXPORTS");

		if (b2baDiffPopupProSums != null && !b2baDiffPopupProSums.isEmpty()) {
			b2baDiffPopupProSums.forEach(b2baDiffPopupProSum -> {
				b2baDiffPopupProSum.setSection("Exports (Section 6A)");
				difPopupProSumItemDtos.add(b2baDiffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("Exports (Section 6A)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void exportsAmadDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> b2baDiffPopupProSums = mapDiffPopupSumItemRev
				.get("EXPORTS-A");

		if (b2baDiffPopupProSums != null && !b2baDiffPopupProSums.isEmpty()) {
			b2baDiffPopupProSums.forEach(b2baDiffPopupProSum -> {
				b2baDiffPopupProSum.setSection("Exports Amended (Section 9A)");
				difPopupProSumItemDtos.add(b2baDiffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("Exports Amended (Section 9A)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void cdnrDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> b2baDiffPopupProSums = mapDiffPopupSumItemRev
				.get("CDNR");

		if (b2baDiffPopupProSums != null && !b2baDiffPopupProSums.isEmpty()) {
			b2baDiffPopupProSums.forEach(b2baDiffPopupProSum -> {
				b2baDiffPopupProSum.setSection("CDNR (Section 9B)");
				difPopupProSumItemDtos.add(b2baDiffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("CDNR (Section 9B)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void cdnraDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> b2baDiffPopupProSums = mapDiffPopupSumItemRev
				.get("CDNRA");

		if (b2baDiffPopupProSums != null && !b2baDiffPopupProSums.isEmpty()) {
			b2baDiffPopupProSums.forEach(b2baDiffPopupProSum -> {
				b2baDiffPopupProSum.setSection("CDNRA (Section 9C)");
				difPopupProSumItemDtos.add(b2baDiffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("CDNRA (Section 9C)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void cdnurDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> b2baDiffPopupProSums = mapDiffPopupSumItemRev
				.get("CDNUR");

		if (b2baDiffPopupProSums != null && !b2baDiffPopupProSums.isEmpty()) {
			b2baDiffPopupProSums.forEach(b2baDiffPopupProSum -> {
				b2baDiffPopupProSum.setSection("CDNUR (Section 9B)");
				difPopupProSumItemDtos.add(b2baDiffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("CDNUR (Section 9B)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void cdnuraDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> b2baDiffPopupProSums = mapDiffPopupSumItemRev
				.get("CDNURA");

		if (b2baDiffPopupProSums != null && !b2baDiffPopupProSums.isEmpty()) {
			b2baDiffPopupProSums.forEach(b2baDiffPopupProSum -> {
				b2baDiffPopupProSum.setSection("CDNURA (Section 9C)");
				difPopupProSumItemDtos.add(b2baDiffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("CDNURA (Section 9C)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void b2csDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> b2baDiffPopupProSums = mapDiffPopupSumItemRev
				.get("B2CS");

		if (b2baDiffPopupProSums != null && !b2baDiffPopupProSums.isEmpty()) {
			b2baDiffPopupProSums.forEach(b2baDiffPopupProSum -> {
				b2baDiffPopupProSum.setSection("B2CS (Section 7)");
				difPopupProSumItemDtos.add(b2baDiffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("B2CS (Section 7)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void b2csaDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> b2baDiffPopupProSums = mapDiffPopupSumItemRev
				.get("B2CSA");

		if (b2baDiffPopupProSums != null && !b2baDiffPopupProSums.isEmpty()) {
			b2baDiffPopupProSums.forEach(b2baDiffPopupProSum -> {
				b2baDiffPopupProSum.setSection("B2CSA (Section 10)");
				difPopupProSumItemDtos.add(b2baDiffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("B2CSA (Section 10)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void hsnDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> diffPopupProSums = mapDiffPopupSumItemRev
				.get("HSN");

		if (diffPopupProSums != null && !diffPopupProSums.isEmpty()) {
			diffPopupProSums.forEach(diffPopupProSum -> {
				diffPopupProSum.setSection("HSN Summary (Section 12)");
				difPopupProSumItemDtos.add(diffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("HSN Summary (Section 12)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void advRecievedDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> diffPopupProSums = mapDiffPopupSumItemRev
				.get("ADV REC");

		if (diffPopupProSums != null && !diffPopupProSums.isEmpty()) {
			diffPopupProSums.forEach(diffPopupProSum -> {
				diffPopupProSum
						.setSection("Adv. Received (Section 11 Part I-11A)");
				difPopupProSumItemDtos.add(diffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto
					.setSection("Adv. Received (Section 11 Part I-11A)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void advRecievedADiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> diffPopupProSums = mapDiffPopupSumItemRev
				.get("ADV REC-A");

		if (diffPopupProSums != null && !diffPopupProSums.isEmpty()) {
			diffPopupProSums.forEach(diffPopupProSum -> {
				diffPopupProSum.setSection(
						"Adv. Received Amended (Section 11 Part II-11A)");
				difPopupProSumItemDtos.add(diffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection(
					"Adv. Received Amended (Section 11 Part II-11A)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void advAdjustedDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> diffPopupProSums = mapDiffPopupSumItemRev
				.get("ADV ADJ");

		if (diffPopupProSums != null && !diffPopupProSums.isEmpty()) {
			diffPopupProSums.forEach(diffPopupProSum -> {
				diffPopupProSum
						.setSection("Adv. Adjusted (Section 11 Part I-11B)");
				difPopupProSumItemDtos.add(diffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto
					.setSection("Adv. Adjusted (Section 11 Part I-11B)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void advAdjustedADiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> diffPopupProSums = mapDiffPopupSumItemRev
				.get("ADV ADJ-A");

		if (diffPopupProSums != null && !diffPopupProSums.isEmpty()) {
			diffPopupProSums.forEach(diffPopupProSum -> {
				diffPopupProSum.setSection(
						"Adv. Adjusted Amended (Section 11 Part II-11B)");
				difPopupProSumItemDtos.add(diffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection(
					"Adv. Adjusted Amended (Section 11 Part II-11B)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void nilDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> diffPopupProSums = mapDiffPopupSumItemRev
				.get("NIL");

		if (diffPopupProSums != null && !diffPopupProSums.isEmpty()) {
			diffPopupProSums.forEach(diffPopupProSum -> {
				diffPopupProSum.setSection("NIL, Exempt and NON GST Supplies");
				difPopupProSumItemDtos.add(diffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("NIL, Exempt and NON GST Supplies");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private void docDiffPopupProSums(
			Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev,
			List<DiffPopupProcSumItemRevDto> difPopupProSumItemDtos) {
		List<DiffPopupProcSumItemRevDto> diffPopupProSums = mapDiffPopupSumItemRev
				.get("DOC");

		if (diffPopupProSums != null && !diffPopupProSums.isEmpty()) {
			diffPopupProSums.forEach(diffPopupProSum -> {
				diffPopupProSum.setSection("Documents Issued (Section 13)");
				difPopupProSumItemDtos.add(diffPopupProSum);
			});
		} else {
			DiffPopupProcSumItemRevDto diffPopupSumItemDto = new DiffPopupProcSumItemRevDto();
			diffPopupSumItemDto.setSection("Documents Issued (Section 13)");
			difPopupProSumItemDtos.add(diffPopupSumItemDto);
		}
	}

	private Gstr1ReverseIntgReviewSummaryDto getConvertReviewSummary(
			String gstinNum, String retPer, String entityName, String entityPan,
			Long entityId, String companyCode) {
		Gstr1ReverseIntgReviewSummaryDto revSumReqDto = new Gstr1ReverseIntgReviewSummaryDto();

		List<Gstr1ReviewSummaryItemDto> itemDtos = getReviewSummaryList(
				gstinNum, retPer, entityName, entityPan, entityId, companyCode);
		revSumReqDto.setItems(itemDtos);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1ReviewSummaryReqRevIntegrationHandler "
					+ "itemDtos : {}", itemDtos.size());
		}
		return revSumReqDto;
	}

	private List<Gstr1ReviewSummaryItemDto> getReviewSummaryList(
			String gstinNum, String retPer, String entityName, String entityPan,
			Long entityId, String companyCode) {
		List<Gstr1ReviewSummaryRequestItemDto> revSumItemDtos = gstr1ReviewSummaryRequestDocs
				.reviewSummary(gstinNum, retPer, entityName, entityPan,
						entityId, companyCode);
		List<Gstr1ReviewSummaryItemDto> itemDtos = new ArrayList<>();
		revSumItemDtos.forEach(revSumItemDto -> {
			Gstr1ReviewSummaryItemDto itemDto = new Gstr1ReviewSummaryItemDto();

			// Outward Review Summary
			itemDto.setDataType(revSumItemDto.getDataType());
			itemDto.setTaxCate(revSumItemDto.getTaxCate());
			itemDto.setAspCount(revSumItemDto.getAspCount());
			itemDto.setAspInval(revSumItemDto.getAspInval());
			itemDto.setAspTbval(revSumItemDto.getAspTbval());
			itemDto.setAspTxval(revSumItemDto.getAspTxval());

			itemDto.setAspTaxableVal(revSumItemDto.getAspTaxableValue());
			//itemDto.setGstnTaxableVal(revSumItemDto.getGstnTaxableValue());
			//itemDto.setDiffTaxableVal(revSumItemDto.getDiffTaxableValue());
			
			itemDto.setAspIgstval(revSumItemDto.getAspIgstval());
			itemDto.setAspSgstval(revSumItemDto.getAspSgstval());
			itemDto.setAspCgstval(revSumItemDto.getAspCgstval());
			itemDto.setAspCessval(revSumItemDto.getAspCessval());

			itemDto.setGstnCount(revSumItemDto.getGstnCount());
			itemDto.setGstnInval(revSumItemDto.getGstnInval());
			itemDto.setGstnTbval(revSumItemDto.getGstnTbval());
			itemDto.setGstnTxval(revSumItemDto.getGstnTxval());
			itemDto.setGstnIgstval(revSumItemDto.getGstnIgstval());
			itemDto.setGstnSgstval(revSumItemDto.getGstnSgstval());
			itemDto.setGstnCgstval(revSumItemDto.getGstnCgstval());
			itemDto.setGstnCessval(revSumItemDto.getGstnCessval());

			itemDto.setDiffCount(revSumItemDto.getDiffCount());
			itemDto.setDiffInval(revSumItemDto.getDiffInval());
			itemDto.setDiffTbval(revSumItemDto.getDiffTbval());
			itemDto.setDiffTxval(revSumItemDto.getDiffTxval());
			itemDto.setDiffIgstval(revSumItemDto.getDiffIgstval());
			itemDto.setDiffSgstval(revSumItemDto.getDiffSgstval());
			itemDto.setDiffCgstval(revSumItemDto.getDiffCgstval());
			itemDto.setDiffCessval(revSumItemDto.getDiffCessval());

			// Nil Non Exmptention
			itemDto.setAspNilRsup(revSumItemDto.getAspNilRsup());
			itemDto.setAspExpSup(revSumItemDto.getAspExpSup());
			itemDto.setAspNonGsup(revSumItemDto.getAspNonGsup());
			itemDto.setAspCancel(revSumItemDto.getAspCancel());
			itemDto.setAspNetissue(revSumItemDto.getAspNetissue());

			itemDto.setGstNilRsup(revSumItemDto.getGstNilRsup());
			itemDto.setGstExpSup(revSumItemDto.getGstExpSup());
			itemDto.setGstNonGsup(revSumItemDto.getGstNonGsup());
			itemDto.setGstCancel(revSumItemDto.getGstCancel());
			itemDto.setGstNetissue(revSumItemDto.getGstNetissue());

			itemDto.setDiffNilRsup(revSumItemDto.getDiffNilRsup());
			itemDto.setDiffExpSup(revSumItemDto.getDiffExpSup());
			itemDto.setDiffNonGsup(revSumItemDto.getDiffNonGsup());
			itemDto.setDiffCancel(revSumItemDto.getDiffCancel());
			itemDto.setDiffNetissue(revSumItemDto.getDiffNetissue());
			itemDtos.add(itemDto);
		});
		return itemDtos;
	}

	private List<DiffPopupProcSumItemRevDto> diffPopupSumItemRev(
			String gstinNum, String retPer, String entityName, String entityPan,
			Long entityId, String companyCode) {

		List<DiffPopupProcSumItemRevDto> difProcSumItemRevDtos = new ArrayList<>();
		List<Gstr1ReviewSummaryItemDto> revSumItemDtos = getReviewSummaryList(
				gstinNum, retPer, entityName, entityPan, entityId, companyCode);
		Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
		request.setTaxPeriod(retPer);
		List<String> gstins = new ArrayList<>();
		gstins.add(gstinNum);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		List<Long> entities = new ArrayList<>();
		entities.add(entityId);
		request.setEntityId(entities);
		request.setDataSecAttrs(dataSecAttrs);
		String lastUpdatedDate = dateFetchDao.loadBasicSummarySection(request);
		revSumItemDtos.forEach(revSumItemDto -> {
			if ("NIL".equalsIgnoreCase(revSumItemDto.getTaxCate())) {
				DiffPopupProcSumItemRevDto difProcSumItemRevDto = new DiffPopupProcSumItemRevDto();
				difProcSumItemRevDto.setSection(revSumItemDto.getTaxCate());
				if (lastUpdatedDate != null && !lastUpdatedDate.isEmpty()) {
					String[] lastUpdate = lastUpdatedDate.split(" ");
					difProcSumItemRevDto.setEyDate(lastUpdate[0]);
					difProcSumItemRevDto.setEyTime(lastUpdate[1]);
				}
				BigDecimal totalValue = revSumItemDto.getDiffNilRsup()
						.add(revSumItemDto.getDiffExpSup())
						.add(revSumItemDto.getDiffNonGsup());
				difProcSumItemRevDto.setTotalValue(totalValue);
				difProcSumItemRevDtos.add(difProcSumItemRevDto);
			} else if ("DOC".equalsIgnoreCase(revSumItemDto.getTaxCate())) {
				DiffPopupProcSumItemRevDto difProcSumItemRevDto = new DiffPopupProcSumItemRevDto();
				difProcSumItemRevDto.setSection(revSumItemDto.getTaxCate());
				if (lastUpdatedDate != null && !lastUpdatedDate.isEmpty()) {
					String[] lastUpdate = lastUpdatedDate.split(" ");
					difProcSumItemRevDto.setEyDate(lastUpdate[0]);
					difProcSumItemRevDto.setEyTime(lastUpdate[1]);
				}
				difProcSumItemRevDto.setDCount(revSumItemDto.getDiffCount());
				difProcSumItemRevDtos.add(difProcSumItemRevDto);
			} else {
				DiffPopupProcSumItemRevDto difProcSumItemRevDto = new DiffPopupProcSumItemRevDto();
				if (lastUpdatedDate != null && !lastUpdatedDate.isEmpty()) {
					String[] lastUpdate = lastUpdatedDate.split(" ");
					difProcSumItemRevDto.setEyDate(lastUpdate[0]);
					difProcSumItemRevDto.setEyTime(lastUpdate[1]);
				}
				difProcSumItemRevDto.setSection(revSumItemDto.getTaxCate());
				difProcSumItemRevDto.setAssAmount(revSumItemDto.getDiffTbval());
				difProcSumItemRevDto
						.setTotalValue(revSumItemDto.getDiffInval());
				revSumItemDto.getDiffNetissue();
				difProcSumItemRevDto.setTotalTax(revSumItemDto.getDiffTxval());
				difProcSumItemRevDto.setDCount(revSumItemDto.getDiffCount());
				difProcSumItemRevDto.setIgst(revSumItemDto.getDiffIgstval());
				difProcSumItemRevDto.setCgst(revSumItemDto.getDiffCgstval());
				difProcSumItemRevDto.setSgst(revSumItemDto.getDiffSgstval());
				difProcSumItemRevDto.setCess(revSumItemDto.getDiffCessval());
				difProcSumItemRevDtos.add(difProcSumItemRevDto);
			}
		});

		return difProcSumItemRevDtos;
	}

	private Map<String, List<DiffPopupProcSumItemRevDto>> mapDiffPopupSumItemRev(
			List<DiffPopupProcSumItemRevDto> diffPopupSumItems) {
		Map<String, List<DiffPopupProcSumItemRevDto>> mapDifPopupSum = new HashMap<>();
		diffPopupSumItems.forEach(diffPopupSumItem -> {
			StringBuilder key = new StringBuilder();
			key.append(diffPopupSumItem.getSection());
			String sectionKey = key.toString();
			if (mapDifPopupSum.containsKey(sectionKey)) {
				List<DiffPopupProcSumItemRevDto> diffPopupProSumItemDtos = mapDifPopupSum
						.get(sectionKey);
				diffPopupProSumItemDtos.add(diffPopupSumItem);
				mapDifPopupSum.put(sectionKey, diffPopupProSumItemDtos);
			} else {
				List<DiffPopupProcSumItemRevDto> diffPopupProSumItemDtos = new ArrayList<>();
				diffPopupProSumItemDtos.add(diffPopupSumItem);
				mapDifPopupSum.put(sectionKey, diffPopupProSumItemDtos);
			}
		});
		return mapDifPopupSum;
	}

	private Gstr1ProcessSummaryDto processSummaryConversion(
			Gstr1ReviewSummaryRequestItemDto finalDto) {
		Gstr1ProcessSummaryDto processSummary = new Gstr1ProcessSummaryDto();
		List<Gstr1ProcessSummaryItemDto> items = new ArrayList<>();
		Gstr1ProcessSummaryItemDto item = new Gstr1ProcessSummaryItemDto();
		item.setEyTotDoc(finalDto.getEyTotDoc());
		item.setEyOutsupp(finalDto.getEyOutsupp());
		item.setEyIgstval(finalDto.getEyIgstval());
		item.setEyCgstval(finalDto.getEyCgstval());
		item.setEySgstval(finalDto.getEySgstval());
		item.setEyCessval(finalDto.getEyCessval());
		item.setEyDate(finalDto.getEyDate());
		item.setEyTime(finalDto.getEyTime());
		item.setEyStatus(finalDto.getEyStatus());
		item.setProfitCenter(finalDto.getProfitCenter());
		item.setPlantCode(finalDto.getPlantCode());
		item.setLocation(finalDto.getLocation());
		item.setSalesOrganization(finalDto.getSalesOrganization());
		item.setDistChannel(finalDto.getDistChannel());
		item.setDivision(finalDto.getDivision());
		item.setUseraccess1(finalDto.getUseraccess1());
		item.setUseraccess2(finalDto.getUseraccess2());
		item.setUseraccess3(finalDto.getUseraccess3());
		item.setUseraccess4(finalDto.getUseraccess4());
		item.setUseraccess5(finalDto.getUseraccess5());
		item.setUseraccess6(finalDto.getUseraccess6());
		items.add(item);
		processSummary.setItems(items);
		return processSummary;
	}

	public void table14And15DiffPopMappingToDto(
			List<DiffPopupProcSumItemRevDto> finalItemsDto,
			List<Gstr1SummarySectionDto> tbl14And15SectionList) {


		for (Gstr1SummarySectionDto dto : tbl14And15SectionList) {
			
			LOGGER.debug(" for table14And15DiffPopMappingToDto dtos {} ", dto);
			DiffPopupProcSumItemRevDto itemDto = new DiffPopupProcSumItemRevDto();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside loop for dto with taxDocType: "
						+ dto.getTaxDocType());
			}
			if (dto.getTaxDocType() != null) {
			    switch (dto.getTaxDocType()) {
			    case "14(i)":
		            itemDto.setSection("14 (i) – Supplies attracting u/s 52");
		            break;
		        case "14(ii)":
		            itemDto.setSection("14 (ii) – Supplies attracting u/s 9(5)");
		            break;
		        case "14A(i)":
		            itemDto.setSection("14A (i) – Supplies attracting u/s 52");
		            break;
		        case "14A(ii)":
		            itemDto.setSection("14A (ii) – Supplies attracting u/s 9(5)");
		            break;
			        case "15":
			            itemDto.setSection("E-com - Supplies made through e-Commerce 15");
			            break;
			        case "15(i)":
			            itemDto.setSection("Registered supplier to Registered Recipient 15(i)");
			            break;
			        case "15(ii)":
			            itemDto.setSection("Registered supplier to Unregistered Recipient 15(ii)");
			            break;
			        case "15(iii)":
			            itemDto.setSection("Unregistered supplier to Registered Recipient 15(iii)");
			            break;
			        case "15(iv)":
			            itemDto.setSection("Unregistered supplier to Unregistered Recipient 15(iv)");
			            break;
			        default:
			            itemDto.setSection(dto.getTaxDocType());
			            break;
			    }String section=itemDto.getSection();
			    if (itemDto.getSection().contains("–")) { 
			         section = itemDto.getSection().replace("–", "-"); 
			    }
			    itemDto.setSection(section);

			} else {
			    itemDto.setSection(null);
			}

			itemDto.setDCount(dto.getRecords() != null
					? BigInteger.valueOf(dto.getRecords())
							.subtract(BigInteger.valueOf(dto.getGstnCount()))
					: BigInteger.ZERO);
			
			/*itemDto.setTotalTax(dto.getTaxPayable() != null
					? dto.getTaxPayable()
							.subtract(dto.getGstnTaxPayble())
					: BigDecimal.ZERO);
			*/
			//--getTaxableValue from dto is mapped to Assesible amount in pop up screen
			itemDto.setAssAmount(dto.getTaxableValue()!= null
					? dto.getTaxableValue().subtract(dto.getGstnTaxableValue())
					: BigDecimal.ZERO);
			/*//total value is the sum of  getTaxPayable+getTaxableValue
			BigDecimal totalValue = itemDto.getTotalTax().add(itemDto.getAssAmount());
			itemDto.setTotalValue(totalValue != null ? totalValue : BigDecimal.ZERO);
*/
			
			
			itemDto.setIgst(
					dto.getIgst() != null
							? dto.getIgst().subtract(dto.getGstnIgst())
							: BigDecimal.ZERO);
			itemDto.setCgst(
					dto.getCgst() != null
							? dto.getCgst().subtract(dto.getGstnCgst())
							: BigDecimal.ZERO);
			itemDto.setSgst(
					dto.getSgst() != null
							? dto.getSgst().subtract(dto.getGstnSgst())
							: BigDecimal.ZERO);
			itemDto.setCess(
					dto.getCess() != null
							? dto.getCess().subtract(dto.getGstnCess())
							: BigDecimal.ZERO);

			itemDto.setTaxableVal(dto.getTaxableValue() != null
					? dto.getTaxableValue().subtract(dto.getGstnTaxableValue())
					: BigDecimal.ZERO);
		//// since 14 & 15 we will not have Total Taxable-- we need to calculate manually
			//--getTaxPayable from dto is mapped to total tax in pop up screen
			itemDto.setTotalTax(
				    calculateNonNull(itemDto.getIgst())
				        .add(calculateNonNull(itemDto.getCgst()))
				        .add(calculateNonNull(itemDto.getSgst()))
				        .add(calculateNonNull(itemDto.getCess()))
				        .setScale(2, BigDecimal.ROUND_HALF_UP)
				);

			//total value is the sum of  getTaxPayable+getTaxableValue
			itemDto.setTotalValue(
				    calculateNonNull(itemDto.getTotalTax())
				            .add(calculateNonNull(itemDto.getAssAmount()))
				            .setScale(2, BigDecimal.ROUND_HALF_UP)
				);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Final itemDto values: " + itemDto.toString());
			}
			
			finalItemsDto.add(itemDto);
		}
	}
	
	
	public void table14And15DiffPopDefaultValuesMapping(
	        List<DiffPopupProcSumItemRevDto> finalItemsDto,
	       String section) {
	        
	        DiffPopupProcSumItemRevDto itemDto = new DiffPopupProcSumItemRevDto();
	        
	        if (LOGGER.isDebugEnabled()) {
	            LOGGER.debug("Inside table14And15DiffPopDefaultValuesMapping : {}", section);
	        }
	        
	    	if (section != null) {
			    switch (section) {
			      
			        case "14(i)":
			            itemDto.setSection("14 (i) – Supplies attracting u/s 52");
			            break;
			        case "14(ii)":
			            itemDto.setSection("14 (ii) – Supplies attracting u/s 9(5)");
			            break;
			        case "14A(i)":
			            itemDto.setSection("14A (i) – Supplies attracting u/s 52");
			            break;
			        case "14A(ii)":
			            itemDto.setSection("14A (ii) – Supplies attracting u/s 9(5)");
			            break;
			        case "15":
			            itemDto.setSection("E-com - Supplies made through e-Commerce 15");
			            break;
			        case "15A(i)":
			            itemDto.setSection("E-com - Amendment of Supplies made through e-Commerce 15A(I)");
			            break;
			        case "15A(ii)":
			            itemDto.setSection("E-com - Amendment of Supplies made through e-Commerce 15A(II)");
			            break;
			       
			        default:
			            itemDto.setSection(section);
			            break;
			    }
			    // section=itemDto.getSection();
			    if (itemDto.getSection().contains("–")) { 
			        String sectionVal = itemDto.getSection().replace("–", "-"); 
			         itemDto.setSection(sectionVal);
			    }

			} else {
			    itemDto.setSection(null);
			}

	        itemDto.setDCount(BigInteger.ZERO);
	        itemDto.setAssAmount(BigDecimal.ZERO);
	        itemDto.setTotalTax(BigDecimal.ZERO);
	        itemDto.setTotalValue(BigDecimal.ZERO);
	        itemDto.setIgst(BigDecimal.ZERO);
	        itemDto.setCgst(BigDecimal.ZERO);
	        itemDto.setSgst(BigDecimal.ZERO);
	        itemDto.setCess(BigDecimal.ZERO);
	        itemDto.setTaxableVal(BigDecimal.ZERO);
	        if (LOGGER.isDebugEnabled()) {
	            LOGGER.debug("Final diff pop up  itemDto values: {}", itemDto);
	        }
	        finalItemsDto.add(itemDto);
	    
	}


	private BigDecimal calculateNonNull(BigDecimal value) {
	    return value != null ? value : BigDecimal.ZERO;
	}
}
