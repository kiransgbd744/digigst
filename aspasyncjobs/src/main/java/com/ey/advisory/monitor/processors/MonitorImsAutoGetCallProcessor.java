package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GetAutoJobDtlsEntity;
import com.ey.advisory.admin.data.entities.client.ImsAutomationEntity;
import com.ey.advisory.admin.data.repositories.client.GetAutoJobDtlsRepo;
import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.ImsAutomationRepository;
import com.ey.advisory.app.ims.handlers.GetImsAutomationHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("MonitorImsAutoGetCallProcessor")
public class MonitorImsAutoGetCallProcessor
		extends DefaultMultiTenantTaskProcessor {

	public static final String DAILY_GET = "D";
	public static final String WEEKLY_GET = "W";
	public static final String MONTHLY_GET = "M";
	public static final String FORTNIGHT_GET = "F";
	public static final String CUSTOM_GET = "C";

	@Autowired
	private ImsAutomationRepository imsAutomationRepo;

	@Autowired
	private GetImsAutomationHandler handler;

	@Autowired
	private GetAutoJobDtlsRepo jobDtlsRepo;
	
	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring"
								+ " MonitorImsAutoGetCallProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			createEntitesPollingTasks(message, group.getGroupCode());

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Completed one cycle of periodic Monitoring"
								+ " job for MonitorImsAutoGetCallProcessor group '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			LOGGER.error(
					"Unexpected error while executing Ims Automation Job {} ",
					ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}

	private void createEntitesPollingTasks(Message message, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Fetching ImsAutomationEntity "
							+ "with groupcode {} and message {}",
					groupCode, message);
			LOGGER.debug(logMsg);
		}

		String imsRoles = groupConfigPrmtRepository.findByGroupLevelImsRoles();

		if (Strings.isNullOrEmpty(imsRoles)
				|| (!"A".equalsIgnoreCase(imsRoles))) {

			String logMsg = String.format(
					"Ims Option not enabled for"
							+ "with groupcode {} and message {}",
					groupCode, message);
			LOGGER.debug(logMsg);
		
			return;
		}
		
		String ims = groupConfigPrmtRepository.findByEntityAutoInitiateGetCall(
				"Whether Auto Get Call of IMS data is to be enabled?", "G38");
		if (Strings.isNullOrEmpty(ims)
				|| (!"A".equalsIgnoreCase(ims))) {

			String logMsg = String.format(
					"Auto Ims Option not enabled for"
							+ "with groupcode {} and message {}",
					groupCode, message);
			LOGGER.debug(logMsg);
		
			return;
		}
		LocalDate istDate = EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
				.toLocalDate();

		List<ImsAutomationEntity> onboardedEntities = imsAutomationRepo
				.findByIsDeleteFalseOrderByEntityIdDesc();

		if (onboardedEntities.isEmpty()) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(
						"There are no(zero) eligible ImsAutomationEntity for Polling for group : {}",
						groupCode);
			}
			return;
		}

		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug(
					"Fetched ImsAutomationEntity list having size {} "
							+ " for which Polling job will be created",
					onboardedEntities.size());
		}

		Map<Long, List<Triplet<String, String, Integer>>> uniqueMap = new HashMap<>();

		Map<Long, List<ImsAutomationEntity>> optedEntitiesMap = onboardedEntities
				.stream().collect(Collectors
						.groupingBy(ImsAutomationEntity::getEntityId));

		String today = String.valueOf(istDate.getDayOfMonth());

		optedEntitiesMap.entrySet().forEach(entry -> {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inward Einvoice AUTO GET call initiated for the ImsAutomationEntity is {}",
						entry);
			}

			/**
			 * Streaming to identify if there is any Monthly GET scheduled for
			 * today at entity level
			 */
			Optional<ImsAutomationEntity> obj = checkForMonthly(entry, today);

			boolean isWeeklyGetEligibleToday = false;

			boolean isMothlyGetEligibleToday = (obj.isPresent()) ? Boolean.TRUE
					: Boolean.FALSE;
			if (!isMothlyGetEligibleToday) {
				/**
				 * Streaming to identify if there is any Weekly GET scheduled
				 * for today at entity level
				 */
				obj = checkForWeekly(entry, today);

				isWeeklyGetEligibleToday = (obj.isPresent()) ? Boolean.TRUE
						: Boolean.FALSE;

				LOGGER.debug("Inside Weekly block --> {} ",
						isWeeklyGetEligibleToday);
			}

			boolean isfortNightEligibleToday = false;
			if (!isMothlyGetEligibleToday && !isWeeklyGetEligibleToday) {
				/**
				 * Streaming to identify if there is any ForthNight GET
				 * scheduled for today at entity level
				 */

				obj = checkForFortNight(entry, today);

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
				 * Streaming to identify if there is any Custom GET scheduled
				 * for today at entity level
				 */

				obj = checkForCustom(entry, today);

				isCustomGetEligibleToday = (obj.isPresent()) ? Boolean.TRUE
						: Boolean.FALSE;

				LOGGER.debug("Inside custom block --> {} ",
						isCustomGetEligibleToday);

			}

			if (!isfortNightEligibleToday && !isMothlyGetEligibleToday
					&& !isWeeklyGetEligibleToday && !isCustomGetEligibleToday) {
				/**
				 * Streaming to identify if there is any Daily GET scheduled for
				 * today at entity level
				 */

				obj = checkForDaily(entry, today);

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
						.findByEntityIdAndPostedDateAndReturnType(
								obj.get().getEntityId(), istDate,
								APIConstants.GET_IMS_LIST.toUpperCase());

				if (isJobPosted.isPresent()) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Job is posted for this entity Id {} and date {}",
								obj.get().getEntityId(), istDate);
					}
					return;
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Cron expression is valid for ImsAutomationEntity is {}",
							entry);
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"ImsAutomationEntity lastJobPostDate is {}",
							istDate);
				}
				LocalDateTime nextScheduledTime = getNextScheduledTime(cron,
						LocalDateTime.now());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"ImsAutomationEntity nextScheduledTime is {} for "
									+ "entityId : {}",
							nextScheduledTime, obj.get().getEntityId());
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"ImsAutomationEntity CurrentdateTime in UTC {} for"
									+ "entityId : {}",
							EYDateUtil
									.toISTDateTimeFromUTC(LocalDateTime.now()),
							obj.get().getEntityId());
				}

				LocalDateTime curDateTime = LocalDateTime.now();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"ImsAutomationEntity CurrentdateTime in IST {} for "
									+ "entityId : {}",
							curDateTime, obj.get().getEntityId());
				}

				if (curDateTime.compareTo(nextScheduledTime) >= 0) {

					if (LOGGER.isDebugEnabled()) {
						String logMsg = String.format(
								"ImsGetCallAutomation Job frequency is matched to "
										+ "execute for combination:'%s'",
								entry);
						LOGGER.debug(logMsg);
					}

					handler.jobSubmissionAtEntityLevel(obj.get(), message,
							uniqueMap);
					persistInJobDtls(obj.get().getEntityId());
				}

			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"ImsGetCallAutomation Job frequency is not matched to "
									+ "execute for entityId :{} and groupcode : {} ",
							entry.getKey(), TenantContext.getTenantId());
				}
			}

		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ImsGetCallAutomation Job is executed "
							+ "execute for groupcode : {} ",
					TenantContext.getTenantId());
		}

		/**
		 * Inserting Entries for GSTIN and Entity level
		 */
		// createReconStatusJobs(uniqueMap);

	}

	private static LocalDateTime getNextScheduledTime(String cron,
			LocalDateTime lastJobPostDate) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2aAutomation getNextScheduledTime called with params {}, {} ", cron, lastJobPostDate);
		}

		CronExpression cronExpression = CronExpression.parse(cron);

		LocalDateTime nextScheduledTime = cronExpression.next(lastJobPostDate);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2aAutomation getNextScheduledTime is {} ", nextScheduledTime);
		}

		nextScheduledTime = EYDateUtil.toUTCDateTimeFromIST(nextScheduledTime);// 8.30
																				// am

		return nextScheduledTime;
	}

	private Optional<ImsAutomationEntity> checkForDaily(
			Entry<Long, List<ImsAutomationEntity>> entry, String today) {

		return entry.getValue().stream()
				.filter(eachEntry -> DAILY_GET.equals(eachEntry.getGetEvent()))
				.findFirst();
	}

	private Optional<ImsAutomationEntity> checkForWeekly(
			Entry<Long, List<ImsAutomationEntity>> entry, String today) {

		return entry.getValue().stream()
				.filter(eachEntry -> WEEKLY_GET.equals(eachEntry.getGetEvent())
						&& eachEntry.getCalendarDateAsString().equals(today))
				.findFirst();
	}

	private Optional<ImsAutomationEntity> checkForFortNight(
			Entry<Long, List<ImsAutomationEntity>> entry, String today) {

		return entry.getValue().stream().filter(
				eachEntry -> FORTNIGHT_GET.equals(eachEntry.getGetEvent())
						&& eachEntry.getCalendarDateAsString().equals(today))
				.findFirst();
	}

	private Optional<ImsAutomationEntity> checkForMonthly(
			Entry<Long, List<ImsAutomationEntity>> entry, String today) {

		return entry.getValue().stream()
				.filter(eachEntry -> MONTHLY_GET.equals(eachEntry.getGetEvent())
						&& eachEntry.getCalendarDateAsString().equals(today))
				.findFirst();
	}

	private Optional<ImsAutomationEntity> checkForCustom(
			Entry<Long, List<ImsAutomationEntity>> entry, String today) {

		LocalDate lastDateOfMonth = LocalDate.now().withDayOfMonth(LocalDate
				.now().getMonth().length(LocalDate.now().isLeapYear()));
		int lastday = lastDateOfMonth.getDayOfMonth();

		return entry.getValue().stream().filter(eachEntry -> CUSTOM_GET
				.equals(eachEntry.getGetEvent())
				&& Integer.valueOf(eachEntry.getCalendarDateAsString()) <= 31
				&& (eachEntry.getCalendarDateAsString().equals(today)
						|| (Integer.valueOf(
								eachEntry.getCalendarDateAsString()) > lastday
										? ((Integer.valueOf(today) == lastday)
												? true : false)
										: false)))
				.findFirst();

		// 5 ,10, 25, 31

		// calendardate > lastdate (if current date == lastdate ) -> run
		// 29 > 28 ( 28 == 28 ) -> true
	}

	private void persistInJobDtls(Long entityId) {
		GetAutoJobDtlsEntity jobDtlsEnt = new GetAutoJobDtlsEntity();
		jobDtlsEnt.setEntityId(entityId);
		jobDtlsEnt.setCreatedOn(LocalDateTime.now());
		jobDtlsEnt.setCreatedBy("SYSTEM");
		jobDtlsEnt.setPostedDate(EYDateUtil
				.toISTDateTimeFromUTC(LocalDateTime.now()).toLocalDate());
		jobDtlsEnt.setReturnType(APIConstants.GET_IMS_LIST.toUpperCase());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GetAutoJobDtlsEntity PostedDate is {}", EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now().toLocalDate()));
		}
		jobDtlsRepo.save(jobDtlsEnt);
	}
}
