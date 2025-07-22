package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9PeriodWiseRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun K.A
 *
 */

@Service("MonitorGstr9PeriodWiseProcessor")
@Slf4j
public class MonitorGstr9PeriodWiseProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	GstinGetStatusRepository gstinGetStatusRepo;

	@Autowired
	Gstr9PeriodWiseRepository gstr9PeriodWiseRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"About to monitor GSTR9 PeriodWise level for group '%s' ",
					group.getGroupCode());
			LOGGER.debug(logMsg);
		}

		try {
			JsonObject json = (new JsonParser().parse(message.getParamsJson())
					.getAsJsonObject());

			Integer fy = json.get("fy").getAsInt();
			String stPeriod = json.get("stPeriod").getAsString();
			String endPeriod = json.get("endPeriod").getAsString();
			String amdStPeriod = json.get("amdStPeriod").getAsString();
			String amdEndPeriod = json.get("amdEndPeriod").getAsString();

			Integer derivedStPeriod = GenUtil.getDerivedTaxPeriod(stPeriod);
			Integer derivedEndPeriod = GenUtil.getDerivedTaxPeriod(endPeriod);
			Integer derivedAmdStPeriod = GenUtil
					.getDerivedTaxPeriod(amdStPeriod);
			Integer derviedAmdLtPeriod = GenUtil
					.getDerivedTaxPeriod(amdEndPeriod);

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Get eligible records for fy - '%s', derivedStPeriod - "
								+ "'%s', derivedEndPeriod - '%s' ",
						fy.toString(), derivedStPeriod.toString(),
						derivedEndPeriod.toString());
				LOGGER.debug(logMsg);
			}

			List<Object[]> eligibleCombsGSTR1 = gstr9PeriodWiseRepo
					.findEligibleRecordsGSTR1(derivedStPeriod,
							derviedAmdLtPeriod);

			List<Object[]> eligibleCombsGSTR3B = gstr9PeriodWiseRepo
					.findEligibleRecordsGSTR3B(derivedStPeriod,
							derivedEndPeriod);

			if (eligibleCombsGSTR1.isEmpty() && eligibleCombsGSTR3B.isEmpty()) {
				LOGGER.error(
						"There are no eligible records update GSTR1 or GSTR3B flags "
								+ "in Period Wise table for group: {}",
						group.getGroupCode());
				return;
			}

			populateAndUpdateGSTR1(eligibleCombsGSTR1, derivedStPeriod,
					derviedAmdLtPeriod);

			populateAndUpdateGSTR3B(eligibleCombsGSTR3B, derivedStPeriod,
					derivedEndPeriod);

			createJob(fy, group, derivedStPeriod, derivedEndPeriod,
					derivedAmdStPeriod, derviedAmdLtPeriod);

			LOGGER.debug("Updated Gstr1,Gstr3b status successfully");

		} catch (Exception ex) {
			String msg = "Exception occured in GSTR9 Period wise level monitor job";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	private boolean populateAndUpdateGSTR1(List<Object[]> eligibleCombsGSTR1,
			Integer derivedStPeriod, Integer derivedAmdLtPeriod) {
		try {
			if (eligibleCombsGSTR1.isEmpty()) {
				return false;
			}
			List<String> gstinTaxPeriods = eligibleCombsGSTR1.stream()
					.map(o -> (String) o[0] + "|" + (String) o[1])
					.collect(Collectors.toList());

			List<Object[]> objArr = gstinGetStatusRepo
					.findCsvDBLoadCompletedRecordsGstr1(derivedStPeriod,
							derivedAmdLtPeriod);

			updateJob(objArr, derivedStPeriod, derivedAmdLtPeriod,
					gstinTaxPeriods);
			return true;
		} catch (Exception e) {
			LOGGER.error("Exception while Updating GSTR1", e);
			return false;
		}

	}

	private boolean populateAndUpdateGSTR3B(List<Object[]> eligibleCombsGSTR3B,
			Integer derivedStPeriod, Integer derivedEndPeriod) {
		try {
			if (eligibleCombsGSTR3B.isEmpty()) {
				return false;
			}
			List<String> gstinTaxPeriods = eligibleCombsGSTR3B.stream()
					.map(o -> (String) o[0] + "|" + (String) o[1])
					.collect(Collectors.toList());

			List<Object[]> objArr = gstinGetStatusRepo
					.findCsvDBLoadCompletedRecordsGstr3B(derivedStPeriod,
							derivedEndPeriod);

			updateJob(objArr, derivedStPeriod, derivedEndPeriod,
					gstinTaxPeriods);
			return true;
		} catch (Exception e) {
			LOGGER.error("Exception while Updating GSTR3b", e);
			return false;
		}
	}

	private void updateJob(List<Object[]> objArr, Integer derivedStPeriod,
			Integer derivedEndPeriod, List<String> gstinTaxPeriods) {

		List<Gstr9PeriodWiseResult> result = objArr.stream()
				.map(o -> convertDto(o)).collect(Collectors.toList());
		List<Gstr9PeriodWiseResult> eligibleRecords = result.stream()
				.filter(o -> iseligibleForUpdate(o, gstinTaxPeriods))
				.collect(Collectors.toList());
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String
					.format("Update Gstr1, GSTR3B records eligible for period wise "
							+ "table - '%s' ", eligibleRecords);
			LOGGER.debug(logMsg);
		}
		populateAndUpdatePeriodStatus(eligibleRecords);
	}

	private void createJob(Integer fy, Group group, Integer derivedStPeriod,
			Integer derivedEndPeriod, Integer derivedAmdStPeriod,
			Integer derviedAmdLtPeriod) {

		JsonObject jobParams = new JsonObject();
		jobParams.addProperty("fy", fy);
		jobParams.addProperty("derivedStPeriod", derivedStPeriod);
		jobParams.addProperty("derivedEndPeriod", derivedEndPeriod);
		jobParams.addProperty("derivedAmdStPeriod", derivedAmdStPeriod);
		jobParams.addProperty("derviedAmdLtPeriod", derviedAmdLtPeriod);

		asyncJobsService.createJob(group.getGroupCode(), "Gstr9FyUpdate",
				jobParams.toString(), "SYSTEM", 1L, null, null);

	}

	private Gstr9PeriodWiseResult convertDto(Object[] obj) {

		Gstr9PeriodWiseResult result = new Gstr9PeriodWiseResult();
		result.setGstin((String) obj[0]);
		result.setTaxPeriod((String) obj[1]);
		result.setReturnType((String) obj[2]);
		result.setRowCount((Long) obj[3]);

		return result;
	}

	private boolean iseligibleForUpdate(Gstr9PeriodWiseResult dto,
			List<String> eligibleRecords) {

		return ((dto.getReturnType().equalsIgnoreCase("GSTR1")
				&& dto.getRowCount() == 17)
				|| (dto.getReturnType().equalsIgnoreCase("GSTR3B")
						&& dto.getRowCount() == 1))
				&& eligibleRecords
						.contains((dto.getGstin()) + "|" + dto.getTaxPeriod());

	}

	private void populateAndUpdatePeriodStatus(
			List<Gstr9PeriodWiseResult> eligibleRecords) {
		eligibleRecords.stream().forEach(o -> {
			if ("GSTR1".equalsIgnoreCase(o.getReturnType())) {
				gstr9PeriodWiseRepo.updateGstr1GetStatus(o.getGstin(),
						o.getTaxPeriod(), true, LocalDateTime.now(),
						LocalDate.now());
			} else {
				gstr9PeriodWiseRepo.updateGstr3BGetStatus(o.getGstin(),
						o.getTaxPeriod(), true, LocalDateTime.now(),
						LocalDate.now());
			}
		});
	}

}

@Getter
@Setter
class Gstr9PeriodWiseResult {

	private String gstin;
	private String taxPeriod;
	private String returnType;
	private Long rowCount;

}
