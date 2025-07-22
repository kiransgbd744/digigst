package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

import com.ey.advisory.admin.data.entities.client.AutoReconStatusEntity;
import com.ey.advisory.admin.data.entities.client.Get2aAutomationEntity;
import com.ey.advisory.admin.data.entities.client.GetAutoJobDtlsEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.Get2aAutomationRepository;
import com.ey.advisory.admin.data.repositories.client.GetAutoJobDtlsRepo;
import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.processors.handler.Gstr2aGetAutomationHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.domain.master.Group;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("MonitorGstr2aGetAutomationProcessor")
public class MonitorGstr2aGetAutomationProcessor
		extends DefaultMultiTenantTaskProcessor {

	public static final String DAILY_GET = "D";
	public static final String WEEKLY_GET = "W";
	public static final String MONTHLY_GET = "M";

	@Autowired
	private Get2aAutomationRepository get2aAutomationRepo;

	@Autowired
	private EntityConfigPrmtRepository entityConfigRepo;

	@Autowired
	private AutoReconStatusRepository autoReconStatusRepo;
	
	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;
	
	@Autowired
	private Gstr2aGetAutomationHandler handler;

	@Autowired
	private GetAutoJobDtlsRepo jobDtlsRepo;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring"
								+ " MonitorGstr2aGetAutomationProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			createGstr1PollingTasks(message, group.getGroupCode());

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Completed one cycle of periodic Monitoring"
								+ " job for Gstr2aAutomation group '%s'",
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
					"Unexpected error while executing Get2a Automation Job {} ",
					ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}

	private void createGstr1PollingTasks(Message message, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Fetching Get2aAutomationEntity "
							+ "with groupcode {} and message {}",
					groupCode, message);
			LOGGER.debug(logMsg);
		}

		LocalDate istDate = EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
				.toLocalDate();

		List<Get2aAutomationEntity> onboardedEntities = get2aAutomationRepo
				.findByIsDeleteFalseOrderByEntityIdDesc();

		if (onboardedEntities.isEmpty()) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(
						"There are no(zero) eligible Get2aAutomationEntity for Polling for group : {}",
						groupCode);
			}
			return;
		}

		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug(
					"Fetched Get2aAutomationEntity list having size {} "
							+ " for which Polling job will be created",
					onboardedEntities.size());
		}

		Map<Long, List<Triplet<String, String, Integer>>> uniqueMap = new HashMap<>();

		Map<Long, List<Get2aAutomationEntity>> optedEntitiesMap = onboardedEntities
				.stream().collect(Collectors
						.groupingBy(Get2aAutomationEntity::getEntityId));

		String today = String.valueOf(istDate.getDayOfMonth());

		optedEntitiesMap.entrySet().forEach(entry -> {
			
			Long entityId = entry.getKey();
			
		
			
		
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GSTR2A AUTO GET call initiated for the Get2aAutomationEntity is {}",
						entry);
			}

			/**
			 * Streaming to identify if there is any Monthly GET scheduled for
			 * today at entity level
			 */
			Optional<Get2aAutomationEntity> obj = checkForMonthly(entry, today);

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
			}

			boolean isCustomGetEligibleToday = false;
			if (!isMothlyGetEligibleToday && !isWeeklyGetEligibleToday) {
				/**
				 * Streaming to identify if there is any Custom GET scheduled
				 * for today at entity level
				 */

				obj = checkForCustomDaily(entry, today);

				isCustomGetEligibleToday = (obj.isPresent()) ? Boolean.TRUE
						: Boolean.FALSE;
			}

			boolean isDailyGetEligibleToday = false;
			if (!isCustomGetEligibleToday && !isMothlyGetEligibleToday
					&& !isWeeklyGetEligibleToday) {
				/**
				 * Streaming to identify if there is any Daily GET scheduled for
				 * today at entity level
				 */
				obj = checkForDaily(entry, today);

				isDailyGetEligibleToday = (obj.isPresent()) ? Boolean.TRUE
						: Boolean.FALSE;

			}

			if (isDailyGetEligibleToday || isWeeklyGetEligibleToday
					|| isMothlyGetEligibleToday || isCustomGetEligibleToday) {

				LocalTime hhmmss = obj.get().getCalendarTime();

				String cron = String.valueOf(hhmmss.getSecond()).concat(" ")
						.concat(String.valueOf(hhmmss.getMinute())).concat(" ")
						.concat(String.valueOf(hhmmss.getHour()))
						.concat(" * * ? ");

				Optional<GetAutoJobDtlsEntity> isJobPosted = jobDtlsRepo
						.findByEntityIdAndPostedDateAndReturnType(
								obj.get().getEntityId(), istDate,
								APIConstants.GSTR2A.toUpperCase());

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
							"Cron expression is valid for Get2aAutomationEntity is {}",
							entry);
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Get2aAutomationEntity lastJobPostDate is {}",
							istDate);
				}
				LocalDateTime nextScheduledTime = getNextScheduledTime(cron,
						LocalDateTime.now());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Get2aAutomationEntity nextScheduledTime is {} for "
									+ "entityId : {}",
							nextScheduledTime, obj.get().getEntityId());
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Get2aAutomationEntity CurrentdateTime in UTC {} for"
									+ "entityId : {}",
							EYDateUtil
									.toISTDateTimeFromUTC(LocalDateTime.now()),
							obj.get().getEntityId());
				}

				LocalDateTime curDateTime = LocalDateTime.now();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Get2aAutomationEntity CurrentdateTime in IST {} for "
									+ "entityId : {}",
							curDateTime, obj.get().getEntityId());
				}

				if (curDateTime.compareTo(nextScheduledTime) >= 0) {

					if (LOGGER.isDebugEnabled()) {
						String logMsg = String.format(
								"Gstr2aAutomation Job frequency is matched to "
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
							"Gstr2aAutomation Job frequency is not matched to "
									+ "execute for entityId :{} and groupcode : {} ",
							entry.getKey(), TenantContext.getTenantId());
				}
			}
			
	});
		
		

		/**
		 * Inserting Entries for GSTIN and Entity level
		 */
		createReconStatusJobs(uniqueMap);
		

	}

	private void createReconStatusJobs(
			Map<Long, List<Triplet<String, String, Integer>>> uniqueMap) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2aAutomation AutoReconStatusEntities are about to "
							+ "create with uniqueMap {}",
					uniqueMap);
		}

		if (uniqueMap != null && !uniqueMap.isEmpty()) {

			List<AutoReconStatusEntity> listofEntities = new ArrayList<>();
			uniqueMap.forEach((key, value) -> {

				String paramValue = entityConfigRepo.findByOptedforAP(key);
				paramValue = paramValue != null ? paramValue : "B";
				/**
				 * Answer B informs that No Handshake table insert
				 */
				if ("B".equals(paramValue)) {

					LOGGER.error(
							"I27 answer is B so No Hand shake table insert "
									+ "made for the Entity {} ",
							key);
					return;
				}

				value.forEach(triplet -> {

					AutoReconStatusEntity reconStatus = new AutoReconStatusEntity();
					reconStatus.setGstin(triplet.getValue0());
					reconStatus.setEntityId(key);
					reconStatus.setDate(
							EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
									.toLocalDate());
					reconStatus.setGet2aStatus(
							APIConstants.INITIATED.toUpperCase());
					reconStatus.setGet2aInitaitedOn(LocalDateTime.now());
					reconStatus.setGetEvent(triplet.getValue1());
					reconStatus.setNumOfTaxPeriods(triplet.getValue2());
					reconStatus.setCreatedOn(LocalDateTime.now());
					listofEntities.add(reconStatus);
				});

			});

			autoReconStatusRepo.saveAll(listofEntities);
		}
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

		nextScheduledTime = EYDateUtil.toUTCDateTimeFromIST(nextScheduledTime);
		return nextScheduledTime;
	}

	private Optional<Get2aAutomationEntity> checkForDaily(
			Entry<Long, List<Get2aAutomationEntity>> entry, String today) {

		return entry.getValue().stream()
				.filter(eachEntry -> DAILY_GET.equals(eachEntry.getGetEvent())
						&& eachEntry.getNumOfTaxPeriods() != null
						&& !eachEntry.isFinYearGet())
				.findFirst();
	}

	private Optional<Get2aAutomationEntity> checkForCustomDaily(
			Entry<Long, List<Get2aAutomationEntity>> entry, String today) {

		LocalDate lastDateOfMonth = LocalDate.now().withDayOfMonth(LocalDate
				.now().getMonth().length(LocalDate.now().isLeapYear()));
		int lastday = lastDateOfMonth.getDayOfMonth();

		return entry.getValue().stream().filter(eachEntry -> DAILY_GET
				.equals(eachEntry.getGetEvent())
				&& eachEntry.getNumOfTaxPeriods() == null
				&& eachEntry.isFinYearGet()
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

	private Optional<Get2aAutomationEntity> checkForWeekly(
			Entry<Long, List<Get2aAutomationEntity>> entry, String today) {

		return entry.getValue().stream()
				.filter(eachEntry -> WEEKLY_GET.equals(eachEntry.getGetEvent())
						&& eachEntry.getCalendarDateAsString().equals(today))
				.findFirst();
	}

	private Optional<Get2aAutomationEntity> checkForMonthly(
			Entry<Long, List<Get2aAutomationEntity>> entry, String today) {

		return entry.getValue().stream()
				.filter(eachEntry -> MONTHLY_GET.equals(eachEntry.getGetEvent())
						&& eachEntry.getCalendarDateAsString().equals(today))
				.findFirst();
	}

	private void persistInJobDtls(Long entityId) {
		GetAutoJobDtlsEntity jobDtlsEnt = new GetAutoJobDtlsEntity();
		jobDtlsEnt.setEntityId(entityId);
		jobDtlsEnt.setCreatedOn(LocalDateTime.now());
		jobDtlsEnt.setCreatedBy("SYSTEM");
		jobDtlsEnt.setPostedDate(EYDateUtil
				.toISTDateTimeFromUTC(LocalDateTime.now()).toLocalDate());
		jobDtlsEnt.setReturnType(APIConstants.GSTR2A.toUpperCase());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GetAutoJobDtlsEntity PostedDate is {}", EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now().toLocalDate()));
		}
		jobDtlsRepo.save(jobDtlsEnt);
	}
}
