package com.ey.advisory.app.processors.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GetAutoJobDtlsRepo;
import com.ey.advisory.app.common.GstrConsolidatedEmailService;
import com.ey.advisory.app.common.GstrEmailDetailsDto;
import com.ey.advisory.app.data.entities.client.ConsolidateEmailMappingEntity;
import com.ey.advisory.app.data.entities.client.RecipientMasterUploadEntity;
import com.ey.advisory.app.data.repositories.client.ConsolidateEmailMappingRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.RecipientMasterUploadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr2aGetOverallStatusHandler")
public class Gstr2aGetOverallStatusHandler {

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	private GstnUserRequestRepository gstnUserRepo;

	@Autowired
	private ConsolidateEmailMappingRepository consolidateEmailMappingRepo;

	@Autowired
	private RecipientMasterUploadRepository recipientMasterUploadRepo;

	@Autowired
	private Gstr2aGetEmailHandler gstr2aGetEmailHandler;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepo;

	@Autowired
	private GSTNDetailRepository gstnDetailRepo;

	@Autowired
	@Qualifier("GstrConsolidatedEmailServiceImpl")
	private GstrConsolidatedEmailService emailService;

	@Autowired
	private GetAutoJobDtlsRepo jobDtlsRepo;

	// @Transactional(value = "clientTransactionManager")
	public void execute(String groupCode) {

		try {
			TenantContext.setTenantId(groupCode);

			/**
			 * GSTIn wise status
			 */
			// updateGstinLevelStatusinUserRequestTable();

	
			LocalDateTime maxGetCallDate = jobDtlsRepo
					.findMaxGetCall(APIConstants.GSTR2A.toUpperCase());

			// Max get Call Date is nothing but the Max Get Call Date in UTC
			// Since Batch table will also have the dates in UTC.
			prepareEmailBodyContent(maxGetCallDate.toLocalDate());

		} catch (Exception e) {
			LOGGER.error("Unexpected Error", e);
			throw new AppException(e.getMessage(), e);
		}
	}

	private void updateGstinLevelStatusinUserRequestTable() {

		List<Object[]> objs = gstnUserRepo.findNewUserRequestIds(
				APIConstants.GET, APIConstants.GSTR2A.toUpperCase());

		for (Object[] obj : objs) {

			Long userRequestId = obj[0] != null
					? Long.parseLong(String.valueOf(obj[0])) : null;
			String gstin = obj[1] != null ? String.valueOf(obj[1]) : null;
			String retPeriod = obj[2] != null ? String.valueOf(obj[2]) : null;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format(
						"GSTR2a GET sections Overall status execution "
								+ "started for userRequestId %s, gstin %s, "
								+ "retperiod %s",
						userRequestId, gstin, retPeriod));
			}

			if (userRequestId != null) {

				if (isAllSectionsGetCallsSuccess(userRequestId, gstin,
						retPeriod)) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"GSTR2a GET sections Overall status  "
										+ "for userRequestId %s, gstin %s, "
										+ "retperiod %s is Success",
								userRequestId, gstin, retPeriod));
					}
					gstnUserRepo.updateRequestStatus(userRequestId, 1);
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"GSTR2a GET sections Overall status "
										+ "for userRequestId %s, gstin %s, "
										+ "retperiod %s is not Success",
								userRequestId, gstin, retPeriod));
					}
					gstnUserRepo.updateRequestStatus(userRequestId, 0);
				}

			}

		}

	}

	private Boolean isAllSectionsGetCallsSuccess(Long userRequestId,
			String gstin, String retPeriod) {

		List<String> statusList = batchRepo
				.findStatusByUserRequestIdAndIsDeleteFalse(gstin, retPeriod,
						userRequestId);

		if (statusList == null || statusList.isEmpty()) {
			return false;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"%d GSTR2a GET sections are availble for userRequestId %d ",
					statusList.size(), userRequestId));
		}

		List<String> gstnStatus = new ArrayList<>();
		gstnStatus.add(APIConstants.SUCCESS);
		gstnStatus.add(APIConstants.SUCCESS_WITH_NO_DATA);

		statusList.removeAll(gstnStatus);

		if (statusList.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format(
						"All GSTR2a GET sections for userRequestId %d is successful",
						userRequestId));
			}
			return true;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"%d GSTR2a GET sections for userRequestId %d is not successful",
					statusList.size(), userRequestId));
		}
		return false;

	}

	private void prepareEmailBodyContent(LocalDate maxGetCallDate) {

		List<ConsolidateEmailMappingEntity> consolidatedEmailMap = consolidateEmailMappingRepo
				.findByReturnTypeAndIsFirstEmailTriggeredTrueAndIsSecondEmailTriggeredFalseAndIsSecondEmailEligibleTrue(
						APIConstants.GSTR2A.toUpperCase());

		/*
		 * List<String> primaryEmailIds = consolidatedEmailMap.stream()
		 * .map(ConsolidateEmailMappingEntity::getPrimaryEmailId)
		 * .collect(Collectors.toList()); List<RecipientMasterUploadEntity>
		 * emailMasterData = recipientMasterUploadRepo
		 * .findByRecipientPrimEmailIdInAndIsDeleteFalseAndIsGetGstr2AEmailTrueOrderByRecipientPrimEmailId(
		 * primaryEmailIds);
		 */

		List<RecipientMasterUploadEntity> emailMasterData = new ArrayList<>();

		consolidatedEmailMap.forEach(emailMaping -> {

			String recipientPrimaryEmail = emailMaping.getPrimaryEmailId();
			String activeGstins = emailMaping.getActiveGstins();
			if (activeGstins != null && activeGstins.length() > 2) {

				// ArrayList<String> consolidatedGstinsList =
				// Lists.newArrayList(
				// activeGstins.substring(1, activeGstins.length() - 1)
				// .split(","));
				List<String> consolidatedGstinsList = Arrays.stream(activeGstins
						.substring(1, activeGstins.length() - 1).split(","))
						.map(String::trim).collect(Collectors.toList());

				List<RecipientMasterUploadEntity> masterDataList = recipientMasterUploadRepo
						.findByRecipientPrimEmailIdAndRecipientGstinInAndIsDeleteFalseAndIsGetGstr2AEmailTrueOrderByRecipientPrimEmailId(
								recipientPrimaryEmail, consolidatedGstinsList);

				emailMasterData.addAll(masterDataList);
			}

		});

		Map<String, Pair<Set<String>, Set<String>>> primaryEmailWiseMap = gstr2aGetEmailHandler
				.getPrimaryEmailWiseMap(emailMasterData);

		List<GstrEmailDetailsDto> emailDetailsDtoList = new ArrayList<>();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Get2aAutomation second eamil primaryEmailWiseMap has formed as {} ",
					primaryEmailWiseMap);
		}

		primaryEmailWiseMap.entrySet().forEach(emailGstin -> {

			String primaryEmailId = emailGstin.getKey();
			Pair<Set<String>, Set<String>> value = emailGstin.getValue();
			Set<String> gstins = value.getValue1();
			Set<String> secondaryEmailIds = value.getValue0();
			String notfnCode = "101";
			List<String> initAndInProgressStatus = new ArrayList<>();
			initAndInProgressStatus.add(APIConstants.INITIATED.toUpperCase());
			initAndInProgressStatus
					.add(JobStatusConstants.IN_PROGRESS.toUpperCase());
			GSTNDetailEntity gstinInfo = gstnDetailRepo
					.findByGstinAndIsDeleteFalse(gstins.iterator().next());
			EntityInfoEntity entityInfo = entityInfoRepo
					.findByIdAndIsDeleteFalse(gstinInfo.getEntityId());

			LocalDateTime atStartOfDay = LocalDate.now().atStartOfDay();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Get2aAutomation findMinAndMaxTaxPeriods with params atStartOfDay, gstins are {} and {}",
						atStartOfDay, gstins);
			}

			String fromTaxPeriod = batchRepo.findMinTaxPeriods(
					new ArrayList<String>(gstins),
					APIConstants.GSTR2A.toUpperCase(), maxGetCallDate);

			String toTaxPeriod = batchRepo.findMaxTaxPeriods(
					new ArrayList<String>(gstins),
					APIConstants.GSTR2A.toUpperCase(), maxGetCallDate);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Get2aAutomation second eamil Min and Max taxPerods are {}, {}  ",
						fromTaxPeriod, toTaxPeriod);
			}

			if (fromTaxPeriod == null || toTaxPeriod == null) {
				LOGGER.error(
						"findMinAndMaxTaxPeriods has returned Null values");
				return;
			}
			/*
			 * Optional<Get2aAutomationEntity> get2aEntity = get2aAutomationRepo
			 * .findByIdAndIsMonthlyGetAndIsDeleteFalse(gstinInfo.getEntityId())
			 * ; Get2aAutomationEntity entity = new Get2aAutomationEntity(); if
			 * (get2aEntity.isPresent()) { entity = get2aEntity.get(); }
			 * Pair<String, String> fromToTaxPeriods = gstr2aGetEmailHandler
			 * .getFromToTaxPeriods(entity.getNumOfTaxPeriods(),
			 * entity.isFinYearGet());
			 */

			// Pair<String, String> fromToTaxPeriods = null;

			int inprogressCount = batchRepo
					.inprogressAutoGetBatchesCountByByGstinAndTaxPerFromToAndReturnType(
							new ArrayList<String>(gstins), fromTaxPeriod,
							toTaxPeriod, APIConstants.GSTR2A.toUpperCase(),
							initAndInProgressStatus);

			if (inprogressCount > 0) {
				LOGGER.error(
						"Some of the batches are inprogress for the primary EamilId {} or GSTINS {} Hence Email will not be sent now",
						primaryEmailId, gstins);
				return;
			} else {

				List<String> findDistinctGstins = batchRepo.findDistinctGstins(
						new ArrayList<String>(gstins), fromTaxPeriod,
						toTaxPeriod, APIConstants.GSTR2A.toUpperCase());
				// Retain the only the GSTIN's which are part of batch table
				gstins.retainAll(findDistinctGstins);
			}

			if (gstins != null) {
				List<String> inActiveGstins = new ArrayList<>();
				GstrEmailDetailsDto emailDto = gstr2aGetEmailHandler
						.prepareRequestPayload(primaryEmailId,
								secondaryEmailIds, gstins,
								entityInfo.getEntityName(), fromTaxPeriod,
								toTaxPeriod, GenUtil.getCurrentFinancialYear(),
								notfnCode, inActiveGstins);

				emailDetailsDtoList.add(emailDto);
			}

		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Get2aAutomation second eamil calling the service method persistAndSendEmail with params dto {} ",
					emailDetailsDtoList);
		}
		emailService.persistAndSendEmail(emailDetailsDtoList, false);
	}

}
