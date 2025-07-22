package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GetAutoJobDtlsEntity;
import com.ey.advisory.admin.data.entities.client.ImsSaveAutomationEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GetAutoJobDtlsRepo;
import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.ImsAutoSaveRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsProcessedInvoiceRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsSaveJobQueueRepository;
import com.ey.advisory.app.service.ims.ImsSaveJobQueueEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */
@Slf4j
@Service("MonitorImsAutoSaveProcessor")
public class MonitorImsAutoSaveProcessor
		extends DefaultMultiTenantTaskProcessor {

	public static final String DAILY_GET = "D";
	public static final String WEEKLY_GET = "W";
	public static final String MONTHLY_GET = "M";
	public static final String FORTNIGHT_GET = "F";
	public static final String CUSTOM_GET = "C";

	@Autowired
	private ImsAutoSaveRepository imsAutomationRepo;

	@Autowired
	private GetAutoJobDtlsRepo jobDtlsRepo;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	@Autowired
	private ImsProcessedInvoiceRepository psdRepo;

	@Autowired
	private ImsSaveJobQueueRepository queueRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private GSTNDetailRepository gstnDetailRepo;

	private static final List<String> statusList = ImmutableList.of("In Queue",
			"InProgress","RefId Generated");

	private static final List<String> GETIMS_SUPPLY_TYPES = ImmutableList.of(
			APIConstants.IMS_TYPE_B2B, APIConstants.IMS_TYPE_B2BA,
			APIConstants.IMS_TYPE_CN, APIConstants.IMS_TYPE_CNA,
			APIConstants.IMS_TYPE_DN, APIConstants.IMS_TYPE_DNA,
			APIConstants.IMS_TYPE_ECOM, APIConstants.IMS_TYPE_ECOMA);

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring" + " MonitorImsAutoSaveProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			createEntitesPollingTasks(message, group.getGroupCode());

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Completed one cycle of periodic Monitoring"
								+ " job for MonitorImsAutoSaveProcessor group '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

		} catch (Exception ex) {
			LOGGER.error(
					"Unexpected error while executing Ims Auto Save Job {} ",
					ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}

	private void createEntitesPollingTasks(Message message, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Fetching ImsSaveAutomationEntity "
							+ "with groupcode {} and message {}",
					groupCode, message);
			LOGGER.debug(logMsg);
		}

		String imsRoles = groupConfigPrmtRepository
				.findByGroupLevelImsSaveRoles();

		if (Strings.isNullOrEmpty(imsRoles)
				|| (!"A".equalsIgnoreCase(imsRoles))) {

			String logMsg = String.format(
					"Ims Option not enabled for"
							+ "with groupcode {} and message {}",
					groupCode, message);
			LOGGER.debug(logMsg);

			return;
		}

		String ims = groupConfigPrmtRepository.findByEntityAutoInitiateAutoSave(
				"Whether auto save of IMS Action Response is to be enabled?",
				"G40");
		if (Strings.isNullOrEmpty(ims) || (!"A".equalsIgnoreCase(ims))) {

			String logMsg = String.format(
					"Auto Ims Option not enabled for"
							+ "with groupcode {} and message {}",
					groupCode, message);
			LOGGER.debug(logMsg);

			return;
		}
		LocalDate istDate = EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
				.toLocalDate();

		List<ImsSaveAutomationEntity> onboardedEntities = imsAutomationRepo
				.findByIsDeleteFalseOrderByEntityIdDesc();

		if (onboardedEntities.isEmpty()) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(
						"There are no(zero) eligible ImsSaveAutomationEntity for Polling for group : {}",
						groupCode);
			}
			return;
		}

		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug(
					"Fetched ImsSaveAutomationEntity list having size {} "
							+ " for which Polling job will be created",
					onboardedEntities.size());
		}

		String today = String.valueOf(istDate.getDayOfMonth());

		for (ImsSaveAutomationEntity entity : onboardedEntities) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inward Einvoice AUTO Save initiated for the ImsSaveAutomationEntity is {}",
						entity);
			}

			/**
			 * Streaming to identify if there is any Monthly SAVE scheduled for
			 * today at entity level
			 */
			Optional<ImsSaveAutomationEntity> obj = checkForMonthly(entity,
					today);

			boolean isWeeklyGetEligibleToday = false;

			boolean isMothlyGetEligibleToday = (obj.isPresent()) ? Boolean.TRUE
					: Boolean.FALSE;
			if (!isMothlyGetEligibleToday) {
				/**
				 * Streaming to identify if there is any Weekly SAVE scheduled
				 * for today at entity level
				 */
				obj = checkForWeekly(entity, today);

				isWeeklyGetEligibleToday = (obj.isPresent()) ? Boolean.TRUE
						: Boolean.FALSE;

				LOGGER.debug("Inside Weekly block --> {} ",
						isWeeklyGetEligibleToday);
			}
			
			boolean isTwiceAWeekEligibleToday = false;
			if (!isMothlyGetEligibleToday
					&& !isWeeklyGetEligibleToday) {
				obj = checkForTwiceWeekly(entity, today);
				isTwiceAWeekEligibleToday = obj.isPresent();
				LOGGER.debug("Inside twice-a-week block --> {} ",
						isTwiceAWeekEligibleToday);
			}

			boolean isfortNightEligibleToday = false;
			if (!isMothlyGetEligibleToday && !isWeeklyGetEligibleToday) {
				/**
				 * Streaming to identify if there is any ForthNight SAVE
				 * scheduled for today at entity level
				 */

				obj = checkForFortNight(entity, today);

				isfortNightEligibleToday = (obj.isPresent()) ? Boolean.TRUE
						: Boolean.FALSE;

				LOGGER.debug("Inside fortnight block --> {} ",
						isfortNightEligibleToday);
			}

			

			boolean isDailyGetEligibleToday = false;
			boolean isCustomGetEligibleToday = false;

			if (!isfortNightEligibleToday && !isMothlyGetEligibleToday
					&& !isWeeklyGetEligibleToday) {
				/**
				 * Streaming to identify if there is any Custom SAVE scheduled
				 * for today at entity level
				 */

				obj = checkForCustom(entity, today);

				isCustomGetEligibleToday = (obj.isPresent()) ? Boolean.TRUE
						: Boolean.FALSE;

				LOGGER.debug("Inside custom block --> {} ",
						isCustomGetEligibleToday);

			}

			if (!isfortNightEligibleToday && !isMothlyGetEligibleToday
					&& !isWeeklyGetEligibleToday && !isCustomGetEligibleToday) {
				/**
				 * Streaming to identify if there is any Daily SAVE scheduled
				 * for today at entity level
				 */

				obj = checkForDaily(entity, today);

				isDailyGetEligibleToday = (obj.isPresent()) ? Boolean.TRUE
						: Boolean.FALSE;

				LOGGER.debug("Inside daily block --> {} ",
						isDailyGetEligibleToday);

			}

			if (isDailyGetEligibleToday || isWeeklyGetEligibleToday
					|| isMothlyGetEligibleToday || isfortNightEligibleToday
					|| isCustomGetEligibleToday) {

				LocalTime hhmmss = obj.get().getCalendarTime();

				String cron = String.valueOf(hhmmss.getSecond()).concat(" ")
						.concat(String.valueOf(hhmmss.getMinute())).concat(" ")
						.concat(String.valueOf(hhmmss.getHour()))
						.concat(" * * ? ");

				Optional<GetAutoJobDtlsEntity> isJobPosted = jobDtlsRepo
						.findByEntityIdAndPostedDateAndReturnType(0L, istDate,
								APIConstants.GET_IMS_SAVE.toUpperCase());

				if (isJobPosted.isPresent()) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Job is posted for this entity Id {} and date {}",
								0L, istDate);
					}
					return;
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Cron expression is valid for ImsSaveAutomationEntity is {}",
							entity);
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"ImsSaveAutomationEntity lastJobPostDate is {}",
							istDate);
				}
				LocalDateTime nextScheduledTime = getNextScheduledTime(cron,
						LocalDateTime.now());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"ImsSaveAutomationEntity nextScheduledTime is {} for "
									+ "entityId : {}",
							nextScheduledTime, 0L);
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"ImsSaveAutomationEntity CurrentdateTime in UTC {} for"
									+ "entityId : {}",
							EYDateUtil.toISTDateTimeFromUTC(
									LocalDateTime.now()),
							0L);
				}

				LocalDateTime curDateTime = LocalDateTime.now();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"ImsSaveAutomationEntity CurrentdateTime in IST {} for "
									+ "entityId : {}",
							curDateTime, 0L);
				}

				if (curDateTime.compareTo(nextScheduledTime) >= 0) {

					if (LOGGER.isDebugEnabled()) {
						String logMsg = String.format(
								"ImsAutoSaveAutomation Job frequency is matched to "
										+ "execute for combination:'%s'",
								entity);
						LOGGER.debug(logMsg);
					}

					saveEntityLevelData();
					persistInJobDtls(0L);
				}

			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"ImsAutoSaveAutomation Job frequency is not matched to "
									+ "execute for entityId :{} and groupcode : {} ",
							entity, TenantContext.getTenantId());
				}
			}

		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ImsAutoSaveAutomation Job is executed "
							+ "execute for groupcode : {} ",
					TenantContext.getTenantId());
		}

	}

	private List<String> getAllInActiveGstnList(
			List<String> uniqueActiveGstins) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Getting all the active GSTNs for GET INWARDEINVOICE save");
		}

		List<String> activeGstins = new ArrayList<>();
		List<String> inActiveGstins = new ArrayList<>();
		if (!uniqueActiveGstins.isEmpty()) {
			for (String gstin : uniqueActiveGstins) {
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (authStatus.equals("A")) {
					activeGstins.add(gstin);
				} else {
					inActiveGstins.add(gstin);
				}
			}
			uniqueActiveGstins.clear();
			uniqueActiveGstins.addAll(activeGstins);
		}
		return inActiveGstins;
	}

	public void saveEntityLevelData() {

		List<String> activeGstins = gstnDetailRepo.getGstr1Gstr3bActiveGstns();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Total GSTIN'S {} ", activeGstins);
		}

		List<String> inActiveGstins = getAllInActiveGstnList(activeGstins);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inActiveGstins {}", inActiveGstins);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ActiveGstins {}", activeGstins);
		}

		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String msg = null;

		Map<String, String> authMap = authTokenService
				.getAuthTokenStatusForGstins(activeGstins);
		for (String gstin : activeGstins) {

			String authStatus = authMap.get(gstin);
			if (authStatus.equals("A")) {
				String section = "";
				String sectionReset = "";
				Long count = 0L;
				Long countReset = 0L;

				List<Object[]> objectList = psdRepo
						.findActiveAPRCountRecipientGstin(gstin,
								GETIMS_SUPPLY_TYPES);
				Map<String, Long> map = new HashMap<>();
				for (Object[] object : objectList) {
					section = object[0] != null
							&& !object[0].toString().trim().isEmpty()
									? String.valueOf(
											object[0].toString().trim())
									: null;

					count = (Long) object[1];
					map.put(section, count);

				}

				List<Object[]> objectResetList = psdRepo
						.findActiveNCountRecipientGstin(gstin,
								GETIMS_SUPPLY_TYPES);
				Map<String, Long> mapReset = new HashMap<>();
				for (Object[] object : objectResetList) {
					sectionReset = object[0] != null
							&& !object[0].toString().trim().isEmpty()
									? String.valueOf(
											object[0].toString().trim())
									: null;

					countReset = (Long) object[1];
					mapReset.put(sectionReset, countReset);

				}

				List<ImsSaveJobQueueEntity> queueEntities = new ArrayList<>();
				List<String> sectionList = queueRepo.findInprogressData(gstin, statusList,
						"SAVE");
				for (String tableType : GETIMS_SUPPLY_TYPES) {
					JsonObject json = new JsonObject();
					json.addProperty("tableType", tableType);
					if (map.containsKey(tableType)) {
						if (sectionList.contains(tableType)) {
							msg = " Save to GSTN Already in Queue";
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Save {}", msg);
							}
						} else {
							ImsSaveJobQueueEntity entity = new ImsSaveJobQueueEntity();
							entity.setSection(tableType);
							entity.setIsActive(true);
							entity.setGstin(gstin);
							entity.setCreatedBy(userName);
							entity.setCreatedOn(LocalDateTime.now());
							entity.setStatus("In Queue");
							entity.setAction("SAVE");
							queueEntities.add(entity);
							msg = " Save to GSTN in Queue";
						}
					} else {
						msg = "No Data available to Save";
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Save {}", msg);
						}
					}
				}

				// Reset
				List<String> sectionListReset = queueRepo.findInprogressData(gstin,
						statusList, "RESET");

				for (String tableType : GETIMS_SUPPLY_TYPES) {
					JsonObject json = new JsonObject();
					json.addProperty("tableType", tableType);
					if (mapReset.containsKey(tableType)) {
						if (sectionListReset.contains(tableType)) {
							msg = " RESET GSTN Already in Queue";
						} else {
							ImsSaveJobQueueEntity entity = new ImsSaveJobQueueEntity();
							entity.setSection(tableType);
							entity.setIsActive(true);
							entity.setGstin(gstin);
							entity.setCreatedBy(userName);
							entity.setCreatedOn(LocalDateTime.now());
							entity.setStatus("In Queue");
							entity.setAction("RESET");
							queueEntities.add(entity);
							msg = " RESET GSTN in Queue";
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Reset {}", msg);
							}
						}
					}
				}

				queueRepo.saveAll(queueEntities);
			} else {
				msg = "Please select only the Active GSTINs";
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Reset {}", msg);
				}
			}
		}

	}

	private Optional<ImsSaveAutomationEntity> checkForDaily(
			ImsSaveAutomationEntity entity, String today) {
		return DAILY_GET.equals(entity.getGetEvent()) ? Optional.of(entity)
				: Optional.empty();
	}

	private Optional<ImsSaveAutomationEntity> checkForWeekly(
			ImsSaveAutomationEntity entity, String today) {
		return (WEEKLY_GET.equals(entity.getGetEvent())
				&& entity.getCalendarDateAsString().equals(today))
						? Optional.of(entity) : Optional.empty();
	}

	private Optional<ImsSaveAutomationEntity> checkForTwiceWeekly(
			ImsSaveAutomationEntity entity, String today) {
		return (WEEKLY_GET.equals(entity.getGetEvent())
				&& entity.getCalendarDateAsString().equals(today))
						? Optional.of(entity) : Optional.empty();
	}

	private Optional<ImsSaveAutomationEntity> checkForFortNight(
			ImsSaveAutomationEntity entity, String today) {
		return (FORTNIGHT_GET.equals(entity.getGetEvent())
				&& entity.getCalendarDateAsString().equals(today))
						? Optional.of(entity) : Optional.empty();
	}

	private Optional<ImsSaveAutomationEntity> checkForCustom(
			ImsSaveAutomationEntity entity, String today) {
		LocalDate lastDateOfMonth = LocalDate.now()
				.withDayOfMonth(LocalDate.now().lengthOfMonth());
		int lastDay = lastDateOfMonth.getDayOfMonth();

		return (CUSTOM_GET.equals(entity.getGetEvent())
				&& Integer.parseInt(entity.getCalendarDateAsString()) <= 31
				&& (entity.getCalendarDateAsString().equals(today) || (Integer
						.parseInt(entity.getCalendarDateAsString()) > lastDay
						&& Integer.parseInt(today) == lastDay)))
								? Optional.of(entity) : Optional.empty();
	}

	private static LocalDateTime getNextScheduledTime(String cron,
			LocalDateTime lastJobPostDate) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Ims get Call getNextScheduledTime called with params {}, {} ",
					cron, lastJobPostDate);
		}
		CronExpression cronExpression = CronExpression.parse(cron);
		LocalDateTime nextScheduledTime = cronExpression.next(lastJobPostDate);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Ims Save getNextScheduledTime is {} ",
					nextScheduledTime);
		}

		nextScheduledTime = EYDateUtil.toUTCDateTimeFromIST(nextScheduledTime);
		return nextScheduledTime;
	}

	private Optional<ImsSaveAutomationEntity> checkForMonthly(
			ImsSaveAutomationEntity entity, String today) {
		return (MONTHLY_GET.equals(entity.getGetEvent())
				&& entity.getCalendarDateAsString().equals(today))
						? Optional.of(entity) : Optional.empty();
	}

	private void persistInJobDtls(Long entityId) {
		GetAutoJobDtlsEntity jobDtlsEnt = new GetAutoJobDtlsEntity();
		jobDtlsEnt.setEntityId(entityId);
		jobDtlsEnt.setCreatedOn(LocalDateTime.now());
		jobDtlsEnt.setCreatedBy("SYSTEM");
		jobDtlsEnt.setPostedDate(EYDateUtil
				.toISTDateTimeFromUTC(LocalDateTime.now()).toLocalDate());
		jobDtlsEnt.setReturnType(APIConstants.GET_IMS_SAVE.toUpperCase());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GetAutoJobDtlsEntity PostedDate is {}", EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now().toLocalDate()));
		}
		jobDtlsRepo.save(jobDtlsEnt);
	}
}
