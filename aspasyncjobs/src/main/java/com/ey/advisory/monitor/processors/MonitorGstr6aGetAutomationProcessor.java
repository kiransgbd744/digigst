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
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AutoReconStatusEntity;
import com.ey.advisory.admin.data.entities.client.Get6aAutomationEntity;
import com.ey.advisory.admin.data.entities.client.GetAutoJobDtlsEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.Get6aAutomationRepository;
import com.ey.advisory.admin.data.repositories.client.GetAutoJobDtlsRepo;
import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.processors.handler.Gstr6aGetAutomationHandler;
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
 * @author Arun.KA
 *
 */
@Slf4j
@Service("MonitorGstr6aGetAutomationProcessor")
public class MonitorGstr6aGetAutomationProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private Get6aAutomationRepository get6aAutomationRepo;

	@Autowired
	private EntityConfigPrmtRepository entityConfigRepo;

	@Autowired
	private AutoReconStatusRepository autoReconStatusRepo;

	@Autowired
	private Gstr6aGetAutomationHandler handler;

	@Autowired
	private GetAutoJobDtlsRepo jobDtlsRepo;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring"
								+ " MonitorGstr6aGetAutomationProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			createGstr1PollingTasks(message, group.getGroupCode());

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Completed one cycle of periodic Monitoring"
								+ " job for Gstr6aAutomation group '%s'",
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
					"Unexpected error while executing Get6a Automation Job {} ",
					ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}

	private void createGstr1PollingTasks(Message message, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Fetching Get6aAutomationEntity "
							+ "with groupcode {} and message {}",
					groupCode, message);
			LOGGER.debug(logMsg);
		}

		List<Get6aAutomationEntity> onboardedEntities = get6aAutomationRepo
				.findByIsDeleteFalseOrderByEntityIdDesc();

		if (onboardedEntities.isEmpty()) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(
						"There are no(zero) eligible Get6aAutomationEntity for Polling for group : {}",
						groupCode);
			}
			return;
		}

		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug(
					"Fetched Get6aAutomationEntity list having size {} "
							+ " for which Polling job will be created",
					onboardedEntities.size());
		}

		Map<Long, List<Triplet<String, String, Integer>>> uniqueMap = new HashMap<>();

		Map<Long, List<Get6aAutomationEntity>> optedEntitiesMap = onboardedEntities
				.stream().collect(Collectors
						.groupingBy(Get6aAutomationEntity::getEntityId));

		String today = String.valueOf(LocalDate.now().getDayOfMonth());

		optedEntitiesMap.entrySet().forEach(entry -> {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GSTR6A AUTO GET call initiated for the Get6aAutomationEntity is {}",
						entry);
			}

			/**
			 * Streaming to identify if there is any Monthly GET scheduled for
			 * today at entity level
			 */
			Optional<Get6aAutomationEntity> obj = checkForCustomDaily(entry, today);
			boolean isCustomGetEligibleToday = (obj.isPresent()) ? Boolean.TRUE
					: Boolean.FALSE;


			if (isCustomGetEligibleToday) {

				LocalTime hhmmss = entry.getValue().get(0).getCalendarTime();

				String cron = String.valueOf(hhmmss.getSecond()).concat(" ")
						.concat(String.valueOf(hhmmss.getMinute())).concat(" ")
						.concat(String.valueOf(hhmmss.getHour()))
						.concat(" * * ? ");				

				Optional<GetAutoJobDtlsEntity> isJobPosted = jobDtlsRepo
						.findByEntityIdAndPostedDateAndReturnType(
								obj.get().getEntityId(), LocalDate.now(),
								APIConstants.GSTR6A.toUpperCase());

				if (isJobPosted.isPresent()) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Job is posted for this entity Id {} and date {}",
								obj.get().getEntityId(), LocalDate.now());
					}
					return;
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Cron expression is valid for Get6aAutomationEntity is {}",
							entry);
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Get6aAutomationEntity lastJobPostDate is {}",
							LocalDateTime.now());
				}
				LocalDateTime nextScheduledTime = getNextScheduledTime(cron,
						LocalDateTime.now());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Get6aAutomationEntity nextScheduledTime is {} for "
									+ "entityId : {}",
							nextScheduledTime, obj.get().getEntityId());
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Get6aAutomationEntity CurrentdateTime in UTC {} for"
									+ "entityId : {}",
							LocalDateTime.now(), obj.get().getEntityId());
				}

				LocalDateTime curDateTime = LocalDateTime.now();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Get6aAutomationEntity CurrentdateTime in IST {} for "
									+ "entityId : {}",
							curDateTime, obj.get().getEntityId());
				}

				if (curDateTime.compareTo(nextScheduledTime) >= 0) {

					if (LOGGER.isDebugEnabled()) {
						String logMsg = String.format(
								"Gstr6aAutomation Job frequency is matched to "
										+ "execute for combination:'%s'",
								entry);
						LOGGER.debug(logMsg);
					}

					persistInJobDtls(obj.get().getEntityId());
					handler.jobSubmissionAtEntityLevel(obj.get(), message,
							uniqueMap);
				}

			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr6aAutomation Job frequency is not matched to "
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
					"Gstr6aAutomation AutoReconStatusEntities are about to "
							+ "create with uniqueMap {}",
					uniqueMap);
		}

		if (uniqueMap != null && !uniqueMap.isEmpty()) {

			List<AutoReconStatusEntity> listofEntities = new ArrayList<>();
			uniqueMap.forEach((key, value) -> {

				String paramValue = entityConfigRepo.findByOpted6aRecon(key);
				paramValue = paramValue != null ? paramValue : "B";
				/**
				 * Answer B informs that No Handshake table insert
				 */
				if ("B".equals(paramValue)) {

					LOGGER.error(
							"I35 answer is B so No Hand shake table insert "
									+ "made for the Entity {} ",
							key);
					return;
				}

				value.forEach(triplet -> {

					AutoReconStatusEntity reconStatus = new AutoReconStatusEntity();
					reconStatus.setGstin(triplet.getValue0());
					reconStatus.setEntityId(key);
					reconStatus.setDate(
							EYDateUtil.toUTCDateTimeFromLocal(LocalDate.now()));
					reconStatus.setGet6aStatus(
							APIConstants.INITIATED.toUpperCase());
					reconStatus.setGet6aInitiatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
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

		nextScheduledTime = EYDateUtil.toUTCDateTimeFromIST(nextScheduledTime);// 8.30
																				// am

		return nextScheduledTime;
	}

	private Optional<Get6aAutomationEntity> checkForCustomDaily(
			Entry<Long, List<Get6aAutomationEntity>> entry, String today) {

		LocalDate lastDateOfMonth = LocalDate.now().withDayOfMonth(LocalDate
				.now().getMonth().length(LocalDate.now().isLeapYear()));
		int lastday = lastDateOfMonth.getDayOfMonth();

		return entry.getValue().stream().filter(eachEntry -> "M"
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
		jobDtlsEnt.setPostedDate(
				EYDateUtil.toISTDateTimeFromUTC(LocalDate.now()));
		jobDtlsEnt.setReturnType(APIConstants.GSTR6A.toUpperCase());
		jobDtlsRepo.save(jobDtlsEnt);
	}
	
}
