package com.ey.advisory.app.gstr2b;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr2bGet2bGstinDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bGet2bRequestStatusRepository;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr2BGetReportsServiceImpl")
public class Gstr2BGetReportsServiceImpl implements Gstr2BGetReportsService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2bGet2bRequestStatusRepository")
	Gstr2bGet2bRequestStatusRepository configRepo;
	
	@Autowired
	@Qualifier("Gstr2bGet2bGstinDetailsRepository")
	Gstr2bGet2bGstinDetailsRepository gstinDetailsRepo;
	
	@Autowired
	@Qualifier("Gstr2bCompleteReport")
	private Gstr2bCompleteReport reportService;

	@Override
	public String getReports(List<String> taxPeriods, List<String> gstins,
			String reportType, String userName) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr2BGetReportsServiceImpl.getReports()"
					+ " method";
			LOGGER.debug(msg);
		}

		Long configId = 0L;
		try {
			
			configId = generateCustomId(entityManager);

			Gstr2bGet2bRequestStatusEntity entity = new 
					Gstr2bGet2bRequestStatusEntity();

			entity.setCompletedOn(null);
			entity.setCreatedBy(userName);
			entity.setCreatedDate(EYDateUtil.toUTCDateTimeFromLocal(
					LocalDateTime.now()));
			entity.setFilePath(null);
			entity.setGstinCount(Long.valueOf(gstins.size()));
			entity.setReportType(reportType);
			entity.setReqId(configId);
			entity.setStatus("InProgress");
			entity.setTaxPeriodCount(Long.valueOf(taxPeriods.size()));
			configRepo.save(entity);
			
			// saving in child table
			
			for (String taxPeriod : taxPeriods) {

				List<Gstr2bGet2bGstinDetailsEntity> gstinObjList = gstins
						.stream()
						.map(o -> new Gstr2bGet2bGstinDetailsEntity(o,
								taxPeriod, entity.getReqId()))
						.collect(Collectors.toList());

				gstinDetailsRepo.saveAll(gstinObjList);
			
			}
			
			Pair<String,String> gstr2bReportPath = reportService
					.getGstr2bCompleteReport(gstins, taxPeriods, reportType, 
							configId);
			
			if (gstr2bReportPath != null) {
				configRepo.updateStatusAndDcoId("Report Generated",
						LocalDateTime.now(), gstr2bReportPath.getValue0(), 
						gstr2bReportPath.getValue1(), configId);
			} else{
				configRepo.updateStatus("No Records Found",
						LocalDateTime.now(),null, configId);
			}
			

		} catch (Exception ex) {
			configRepo.updateStatus("Failed", LocalDateTime.now(),
					null, configId);
			return "failure";
		}

		return "Success, please check Request status";
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT RECON_REPORT_SEQ.nextval " + "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((Long) query.getSingleResult());

		return seqId;
	}

	private static Long generateCustomId(EntityManager entityManager) {
		String reportId = "";
		String digits = "";
		Long nextSequencevalue = getNextSequencevalue(entityManager);

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		String currentDate = currentYear + (currentMonth < 10
				? ("0" + currentMonth) : String.valueOf(currentMonth));
		if (nextSequencevalue != null && nextSequencevalue > 0) {
			digits = String.format("%06d", nextSequencevalue);
			reportId = currentDate.concat(digits);
		}

		return Long.valueOf(reportId);
	}

}
