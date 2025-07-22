package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.ConfigQuestionEntity;
import com.ey.advisory.admin.data.entities.client.DRC01BReminderFrequencyEntity;
import com.ey.advisory.admin.data.entities.client.DRC01CReminderFrequencyEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.ConfigQuestionRepository;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.drc.TblDrc01AutoGetCallDetails;
import com.ey.advisory.app.data.repositories.client.RecipientMasterUploadRepository;
import com.ey.advisory.app.data.repositories.client.drc.Drc01AutoGetCallDetailsRepository;
import com.ey.advisory.app.data.repositories.client.drc.Drc01RequestCommDetailsRepository;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01bReminderFrequencyRepo;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01cReminderFrequencyRepo;
import com.ey.advisory.app.data.services.drc.DRC01EmailTrigerringHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("MonitorDrc01EmailTriggeringProcessor")
public class MonitorDrc01EmailTriggeringProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private DRC01EmailTrigerringHandler emailService;

	@Autowired
	@Qualifier("GSTNDetailRepository")

	private GSTNDetailRepository gstNDetailRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Drc01AutoGetCallDetailsRepository")
	private Drc01AutoGetCallDetailsRepository drc01AutoGetCallDetailsRepo;

	@Autowired
	private TblDrc01bReminderFrequencyRepo drc01bReminderFrequencyRepo;

	@Autowired
	private TblDrc01cReminderFrequencyRepo drc01cReminderFrequencyRepo;

	@Autowired
	@Qualifier("RecipientMasterUploadRepository")
	private RecipientMasterUploadRepository masterUploadRepo;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepository;

	@Autowired
	@Qualifier("Drc01RequestCommDetailsRepository")
	private Drc01RequestCommDetailsRepository drc01RequestCommDetailsRepo;

	@Autowired
	@Qualifier("TblDrc01bReminderFrequencyRepo")
	private TblDrc01bReminderFrequencyRepo tblDrc01bReminderFrequencyRepo;

	@Autowired
	@Qualifier("TblDrc01cReminderFrequencyRepo")
	private TblDrc01cReminderFrequencyRepo tblDrc01cReminderFrequencyRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("ConfigQuestionRepository")
	ConfigQuestionRepository configQuestionRepo;

	public static final String CONF_KEY = "drc.comm.calendar.date";

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {
			int currentDayOfMonth = LocalDate.now().getDayOfMonth();

			// can be changes to get only the entities for which email
			// triggering is true
			List<EntityInfoEntity> entityIds = entityInfoRepository
					.findAllEntitlementEntitydetails(group.getGroupCode());

			ConfigQuestionEntity confQuestionAutoEmail = configQuestionRepo
					.findByQuestionCodeAndQuestionType("O42", "R"); // check
			List<Long> optedEntitiesEmail = entityConfigPemtRepo
					.getAllEntitiesOpted2A(
							entityIds.stream().map(o -> o.getId()).collect(
									Collectors.toCollection(ArrayList::new)),
							"O42", confQuestionAutoEmail.getId());

			Map<String, Config> configMap = configManager
					.getConfigs("DRC01COMM", CONF_KEY, "DEFAULT");
			Integer todayDate = configMap.get(CONF_KEY) == null
					? Integer.valueOf(21)
					: Integer.valueOf(
							configMap.get(CONF_KEY).getValue().toString());

			boolean is21stDate = false;
			LOGGER.debug("entityIds {} ", entityIds);
			for (Long entityId : optedEntitiesEmail) {

				LOGGER.debug(" entry {} ", entityId);
				TblDrc01AutoGetCallDetails entity = new TblDrc01AutoGetCallDetails();

				if (currentDayOfMonth == todayDate) {
					entity = drc01AutoGetCallDetailsRepo
							.findOriginalEntry(entityId);
					is21stDate = true;

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"Inside Original Entry for entity {} ",
								entity));
					}
					if (entity != null) {
						emailService.triggerEmails(entity, currentDayOfMonth,
								0);
					}
				} else {
					Pair<Boolean, String> drc01bDate = getDRC01BReinderNumberToTriggerEmail(
							entityId, currentDayOfMonth);
					Pair<Boolean, String> drc01cDate = getDRC01CReinderNumberToTriggerEmail(
							entityId, currentDayOfMonth);

					LOGGER.debug("drc01bDate {} , entity id {} ", drc01bDate,
							entityId);
					LOGGER.debug("drc01cDate {}, entity id {} ", drc01cDate,
							entityId);

					if (drc01bDate.getValue0()) {
						Integer reminderCOunt = Integer
								.valueOf(drc01bDate.getValue1()) - 1;

						entity = drc01AutoGetCallDetailsRepo.findReminderEntry(
								reminderCOunt, "DRC01B", entityId);

						LOGGER.debug("entity {}, reminderCOunt {} ", entity,
								reminderCOunt);

						if (entity != null) {
							emailService.triggerEmails(entity,
									currentDayOfMonth,
									Integer.parseInt(drc01bDate.getValue1()));
						}
					}

					if (drc01cDate.getValue0()) {
						Integer reminderCOunt = Integer
								.valueOf(drc01cDate.getValue1()) - 1;

						entity = drc01AutoGetCallDetailsRepo.findReminderEntry(
								reminderCOunt, "DRC01C", entityId);
						LOGGER.debug("entity {}, reminderCOunt {} ", entity,
								reminderCOunt);

						if (entity != null) {
							emailService.triggerEmails(entity,
									currentDayOfMonth,
									Integer.parseInt(drc01cDate.getValue1()));
						}
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"Inside Reminder Entry for entity {} ",
								entity));
					}
				}
			}
		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

	private void emailTriggerMethod(TblDrc01AutoGetCallDetails entity,
			boolean is21stDate, int currentDateOfMonth) {
		/*
		 * if (is21stDate) { emailService.triggerEmails(entity,
		 * currentDateOfMonth, 0); if (LOGGER.isDebugEnabled()) {
		 * LOGGER.debug(String.format(
		 * "Starting the execution for emailTriggerMethod for Original email entity {} "
		 * , entity)); }
		 * 
		 * } else {
		 * 
		 * 
		 * if (isEligibleFlag.getValue0()) emailService.triggerEmails(entity,
		 * currentDateOfMonth, Integer.parseInt(isEligibleFlag.getValue1())); if
		 * (LOGGER.isDebugEnabled()) { LOGGER.debug(String.format(
		 * "Starting the execution for emailTriggerMethod for Reminder email entity {} "
		 * , entity)); } }
		 */}

	private Pair<Boolean, String> getDRC01BReinderNumberToTriggerEmail(
			Long entity, int currentDateOfMonth) {
		Map<String, String> reminderDates = new HashMap<>();

		List<DRC01BReminderFrequencyEntity> drc01bEntity = drc01bReminderFrequencyRepo
				.getReminderdatesEntity(entity);
		LOGGER.debug("drc01bEntity {} ", drc01bEntity);
		if (drc01bEntity == null)
			return new Pair<Boolean, String>(false, null);

		else {
			reminderDates = drc01bEntity.stream()
					.collect(Collectors.toMap(
							DRC01BReminderFrequencyEntity::getDrc01bReminderDate,
							DRC01BReminderFrequencyEntity::getReminderNumber));
			LOGGER.debug("reminderDates {} ", reminderDates);

			if (reminderDates.containsKey(String.valueOf(currentDateOfMonth))) {
				return new Pair<Boolean, String>(true,
						reminderDates.get(String.valueOf(currentDateOfMonth)));
			} else
				return new Pair<Boolean, String>(false, null);
		}
	}

	private Pair<Boolean, String> getDRC01CReinderNumberToTriggerEmail(
			Long entity, int currentDateOfMonth) {
		Map<String, String> reminderDates = new HashMap<>();

		List<DRC01CReminderFrequencyEntity> drc01cEntity = drc01cReminderFrequencyRepo
				.getReminderdatesEntity(entity);
		LOGGER.debug("drc01cEntity {} ", drc01cEntity);
		if (drc01cEntity == null)
			return new Pair<Boolean, String>(false, null);
		else {
			reminderDates = drc01cEntity.stream()
					.collect(Collectors.toMap(
							DRC01CReminderFrequencyEntity::getDrc01cReminderDate,
							DRC01CReminderFrequencyEntity::getReminderNumber));
			LOGGER.debug("reminderDates {} ", reminderDates);

			if (reminderDates.containsKey(String.valueOf(currentDateOfMonth))) {
				return new Pair<Boolean, String>(true,
						reminderDates.get(String.valueOf(currentDateOfMonth)));
			} else
				return new Pair<Boolean, String>(false, null);
		}
	}
}

/*
 * private Pair<Boolean, String> isEligibleToTriggerEmail(
 * TblDrc01AutoGetCallDetails entity, int currentDateOfMonth) {
 * 
 * Map<String, String> reminderDates = new HashMap<>();
 * 
 * String commType = entity.getCommType(); if
 * (commType.equalsIgnoreCase("DRC01B")) { List<DRC01BReminderFrequencyEntity>
 * drc01bEntity = drc01bReminderFrequencyRepo
 * .getReminderdatesEntity(entity.getEntityId()); if (drc01bEntity == null)
 * return new Pair<Boolean, String>(false, null); else { reminderDates =
 * drc01bEntity.stream().collect(Collectors.toMap(
 * DRC01BReminderFrequencyEntity::getDrc01bReminderDate,
 * DRC01BReminderFrequencyEntity::getReminderNumber)); if (reminderDates
 * .containsKey(String.valueOf(currentDateOfMonth))) { return new Pair<Boolean,
 * String>(true, reminderDates .get(String.valueOf(currentDateOfMonth))); } else
 * return new Pair<Boolean, String>(false, null); } } else {
 * List<DRC01CReminderFrequencyEntity> drc01cEntity =
 * drc01cReminderFrequencyRepo .getReminderdatesEntity(entity.getEntityId()); if
 * (drc01cEntity == null) return new Pair<Boolean, String>(false, null); else {
 * reminderDates = drc01cEntity.stream().collect(Collectors.toMap(
 * DRC01CReminderFrequencyEntity::getDrc01cReminderDate,
 * DRC01CReminderFrequencyEntity::getReminderNumber)); if (reminderDates
 * .containsKey(String.valueOf(currentDateOfMonth))) { return new Pair<Boolean,
 * String>(true, reminderDates .get(String.valueOf(currentDateOfMonth))); } else
 * return new Pair<Boolean, String>(false, null); }
 * 
 * }
 */
