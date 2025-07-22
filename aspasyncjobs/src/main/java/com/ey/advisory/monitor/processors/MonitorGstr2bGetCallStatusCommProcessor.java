package com.ey.advisory.monitor.processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr2bAutoCommEntity;
import com.ey.advisory.app.common.GstinWiseEmailDto;
import com.ey.advisory.app.common.GstrConsolidatedEmailService;
import com.ey.advisory.app.common.GstrEmailDetailsDto;
import com.ey.advisory.app.common.NotificationCodes;
import com.ey.advisory.app.data.entities.client.ConsolidateEmailMappingEntity;
import com.ey.advisory.app.data.entities.client.RecipientMasterUploadEntity;
import com.ey.advisory.app.data.repositories.client.ConsolidateEmailMappingRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bAutoCommRepository;
import com.ey.advisory.app.data.repositories.client.RecipientMasterUploadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.domain.master.Group;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@Slf4j
@Component("MonitorGstr2bGetCallStatusCommProcessor")
public class MonitorGstr2bGetCallStatusCommProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("ConsolidateEmailMappingRepository")
	private ConsolidateEmailMappingRepository emailMappingRepo;

	@Autowired
	@Qualifier("RecipientMasterUploadRepository")
	private RecipientMasterUploadRepository masterUploadRepo;

	@Autowired
	@Qualifier("Gstr2bAutoCommRepository")
	private Gstr2bAutoCommRepository gstr2bAutoCommRepo;

	@Autowired
	@Qualifier("GstrConsolidatedEmailServiceImpl")
	private GstrConsolidatedEmailService consolidatedEmailService;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			// Gives all the mapped entries whose first email is triggered
			List<ConsolidateEmailMappingEntity> mappedEntities = emailMappingRepo
					.findByReturnTypeAndIsFirstEmailTriggeredTrueAndIsSecondEmailTriggeredFalseAndIsSecondEmailEligibleTrue(
							"GSTR2B");

			if (mappedEntities.isEmpty()) {
				LOGGER.info(
						"There are no Gstr2b GET call status emails pending "
								+ "to be triggrred for group {}",
						group.getGroupCode());
				return;
			}

			// gives List of Emails and List of Active Gstins
			Pair<List<String>, List<String>> emailsAndGstins = getAllEmailsAndGstins(
					mappedEntities);

			List<RecipientMasterUploadEntity> optedEntities = masterUploadRepo
					.findByRecipientPrimEmailIdInAndRecipientGstinInAndIsDeleteFalseAndIsGetGstr2BEmailTrue(
							emailsAndGstins.getValue0(),
							emailsAndGstins.getValue1());

			LOGGER.debug("Entities opted for gstr2b are {}", optedEntities);

			String taxPeriod = GenUtil.getCurrentTaxPeriod();
			String fy = GenUtil.getFinancialYearByTaxperiod(taxPeriod);

			List<Gstr2bAutoCommEntity> getCallRecords = gstr2bAutoCommRepo
					.findByTaxPeriodAndReturnTypeAndGstinIn(taxPeriod,
							APIConstants.GSTR2B, emailsAndGstins.getValue1());

			if (getCallRecords.isEmpty()) {
				String msg = String.format(
						"There are no GET Calls happened for"
								+ " taxPeriod %s, returnType %s and gstins %s",
						taxPeriod, APIConstants.GSTR2B,
						emailsAndGstins.getValue1());
				LOGGER.error(msg);
				return;
			}

			Map<String, String> getCallMap = getCallRecords.stream()
					.collect(Collectors.toMap(Gstr2bAutoCommEntity::getGstin,
							Gstr2bAutoCommEntity::getStatus));

			List<RecipientMasterUploadEntity> requiredEntities = new ArrayList<>();
			List<String> inProgressEmails = new ArrayList<>();

			for (RecipientMasterUploadEntity entity : optedEntities) {
				if (getCallMap.containsKey(entity.getRecipientGstin())) {
					requiredEntities.add(entity);
					if (getCallMap.get(entity.getRecipientGstin())
							.equals(APIConstants.INITIATED)) {
						inProgressEmails.add(entity.getRecipientPrimEmailId());
					}
				}
			}
			Map<String, List<RecipientMasterUploadEntity>> map = consolidatedEmailService
					.groupByPrimayEmail(requiredEntities);

			if (!inProgressEmails.isEmpty()) {
				LOGGER.error(
						"Pending Emails where GET status is in INITIATED are {}",
						inProgressEmails);
				inProgressEmails.forEach(email -> {
					map.remove(email);
				});
			}

			List<GstrEmailDetailsDto> reqDtos = new ArrayList<>();

			for (Entry<String, List<RecipientMasterUploadEntity>> pair : map
					.entrySet()) {
				GstrEmailDetailsDto dto = new GstrEmailDetailsDto();
				dto.setReturnType(APIConstants.GSTR2B);
				dto.setFromTaxPeriod(taxPeriod);
				dto.setToTaxPeriod(taxPeriod);
				dto.setFy(fy);
				// dto.setEntityName(entityName);
				dto.setPrimaryEmail(pair.getKey());

				Pair<List<String>, List<GstinWiseEmailDto>> gstinsData = consolidatedEmailService
						.getGstinsAndSectionWiseData(pair.getValue(),
								getCallRecords, taxPeriod);

				dto.setSecondaryEmail(gstinsData.getValue0());
				dto.setGstins(gstinsData.getValue1());
				dto.setNotfnCode(NotificationCodes.GET_SUCC_FAIL);
				reqDtos.add(dto);
			}

			consolidatedEmailService.persistAndSendEmail(reqDtos, false);
		} catch (Exception ee) {
			LOGGER.error("Exception occured while monitoring 2B Get"
					+ " Call and Communication", ee);
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(ee,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}

	private Pair<List<String>, List<String>> getAllEmailsAndGstins(
			List<ConsolidateEmailMappingEntity> mappedEntities) {
		List<String> emails = new ArrayList<>();
		List<String> gstins = new ArrayList<>();
		for (ConsolidateEmailMappingEntity entity : mappedEntities) {
			emails.add(entity.getPrimaryEmailId());
			if (entity.getActiveGstins() != null) {
				List<String> activeGstins = Arrays
						.asList(entity.getActiveGstins().split(","));
				gstins.addAll(activeGstins);
			}
		}
		return new Pair<>(emails, gstins);
	}

}
