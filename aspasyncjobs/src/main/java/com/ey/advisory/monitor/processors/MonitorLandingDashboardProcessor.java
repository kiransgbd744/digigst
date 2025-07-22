package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.LandingDashboardBatchRefreshEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.LandingDashboardRefreshRepository;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOProcCallService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Service("MonitorLandingDashboardProcessor")
public class MonitorLandingDashboardProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepository;

	@Autowired
	@Qualifier("DashboardHOProcCallServiceImpl")
	private DashboardHOProcCallService procCallService;

	@Autowired
	LandingDashboardRefreshRepository ldRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		Long batchId = null;
		Long entityId = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing Monitoring"
								+ " MonitorLandingDashboardProcessor"
								+ ".executeForGroup()  method for group: '%s'",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}

			List<EntityInfoEntity> entityIds = entityInfoRepository
					.findAllEntitlementEntitydetails(group.getGroupCode());

			String derivedtaxPeriod = String
					.valueOf(Calendar.getInstance().get(Calendar.YEAR))
					+ (String.valueOf(
							(Calendar.getInstance().get(Calendar.MONTH)) + 1 > 9
									? String.valueOf((Calendar.getInstance()
											.get(Calendar.MONTH)) + 1)
									: "0" + String.valueOf((Calendar
											.getInstance().get(Calendar.MONTH))
											+ 1)));
			Year currentYear = Year.now();
			int yearValue = currentYear.getValue();

			List<String> totlTaxPeriods = getTaxPeriods(yearValue - 6,
					yearValue, derivedtaxPeriod);

			for (EntityInfoEntity entityIdList : entityIds) {
				entityId = entityIdList.getId();

				List<String> taxPeriods = ldRepo
						.getTaxPeriodHavingBatchIds(entityId);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("tax periods having batchids " + taxPeriods);
				}

				totlTaxPeriods.removeAll(taxPeriods);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"total tax periods post removing active batchids "
									+ totlTaxPeriods);
				}

				if (!totlTaxPeriods.contains(derivedtaxPeriod)) {
					totlTaxPeriods.add(derivedtaxPeriod);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("total tax periods to e considered "
							+ totlTaxPeriods);
				}

				for (String derTaxPeriod : totlTaxPeriods) {
					try {
						if (LOGGER.isDebugEnabled()) {
							String logMsg = String.format(
									"Executing Monitoring"
											+ " MonitorLandingDashboardProcessor"
											+ " method for entityId: '%s' and derivedTaxPeriod: '%s' ",
									entityId, derTaxPeriod);
							LOGGER.debug(logMsg);
						}
						LandingDashboardBatchRefreshEntity entity = new LandingDashboardBatchRefreshEntity();
						entity.setDerRetPeriod(derTaxPeriod);
						entity.setEntityId(entityIdList.getId());
						entity.setCreatedOn(LocalDateTime.now());
						entity.setCreatedBy("SYSTEM");
						entity.setStatus("INITIATED");
						entity.setIsdelete(true);
						entity = ldRepo.save(entity);
						batchId = entity.getBatchId();
						procCallService.dashboardProcCall(derTaxPeriod,
								batchId, entityIdList.getId());
					} catch (Exception ex) {
						LOGGER.error(
								"for entityId, for taxperiod the proc call failed ",
								entityId, derTaxPeriod);
					}
				}
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing  MonitorLandingDashboardProcessor to call "
							+ " proc for batchId :%s group code :%s entityId :%s ",
					batchId, group.getGroupId(), entityId);
			LOGGER.error(msg, ex);
			ldRepo.updateBatchStatus(batchId, "FAILED", LocalDateTime.now());

			throw new AppException(msg);

		}
	}

	public static List<String> getTaxPeriods(int startYear, int endYear,
			String derivedtaxPeriod) {
		List<String> taxPeriods = new ArrayList<>();

		for (int year = startYear; year <= endYear; year++) {
			// Assuming tax year starts in April
			Calendar startPeriod = Calendar.getInstance();
			startPeriod.set(year, Calendar.APRIL, 1);
			Calendar endPeriod = Calendar.getInstance();
			endPeriod.set(year + 1, Calendar.MARCH, 31);

			// Iterate over the months in the tax period
			Calendar currentPeriod = (Calendar) startPeriod.clone();
			// System.out.println(currentPeriod);
			while (currentPeriod.compareTo(endPeriod) <= 0) {
				int month = currentPeriod.get(Calendar.MONTH) + 1;
				int yearInPeriod = currentPeriod.get(Calendar.YEAR);
				String taxPeriod = String.valueOf(yearInPeriod)
						+ String.valueOf(month >= 10 ? month : "0" + month);

				if (taxPeriod.equalsIgnoreCase(derivedtaxPeriod))
					break;
				taxPeriods.add(taxPeriod);

				currentPeriod.add(Calendar.MONTH, 1);
			}
		}

		return taxPeriods;
	}
}
