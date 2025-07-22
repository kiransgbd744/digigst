/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.Config180DaysComputeEntity;
import com.ey.advisory.app.data.repositories.client.Config180DaysComputeRepository;
import com.ey.advisory.app.data.repositories.client.ITCRevesal180ComputeGstinDetailsRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("ITCReversal180DaysComputeServiceImpl")
public class ITCReversal180DaysComputeServiceImpl
		implements ITCReversal180DaysComputeService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Config180DaysComputeRepository")
	Config180DaysComputeRepository configRepo;

	@Autowired
	@Qualifier("ITCRevesal180ComputeGstinDetailsRepository")
	ITCRevesal180ComputeGstinDetailsRepository gstinRepo;
	
	@Autowired
	@Qualifier("ITCRevesalTest")
	ITCRevesalTest test;
	
	@Autowired
	AsyncJobsService asyncJobsService;

	@Override
	public String itcReversalCompute(ITCReversal180DaysComputeReqDto reqDto) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside ITCReversal180DaysComputeServiceImpl"
					+ ".itcReversalCompute() method";
			LOGGER.debug(msg);
		}

		try {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			Long configId = generateCustomId(entityManager);

			Config180DaysComputeEntity entity = new Config180DaysComputeEntity();
			
			
			Integer fromTaxperiod = GenUtil.convertTaxPeriodToInt(reqDto.getFromTaxPeriod());
			Integer toTaxperiod = GenUtil.convertTaxPeriodToInt(reqDto.getToTaxPeriod());
			entity.setActive(true);
			entity.setComputeId(configId);
			entity.setCreatedBy(userName);
			entity.setCreatedOn(LocalDateTime.now());
			entity.setEntityId(reqDto.getEntityId());
			entity.setDelete(false);

			if (reqDto.getCriteria().equalsIgnoreCase("docDate")) {
				entity.setToDocDate(reqDto.getToDate());
				entity.setFromDocDate(reqDto.getFromDate());
			} else if(reqDto.getCriteria().equalsIgnoreCase("accVoucherDate")){
				entity.setToAccDate(reqDto.getToDate());
				entity.setFromAccDate(reqDto.getFromDate());
			}
			else if(reqDto.getCriteria().equalsIgnoreCase("taxPeriod")){
				entity.setFromTaxPeriod(fromTaxperiod);
				entity.setToTaxPeriod(toTaxperiod);
			}
			
			
			entity.setModifiedBy(userName);
			entity.setModifiedOn(LocalDateTime.now());
			entity.setStatus("Initiated");

			//making entry in config table
			configRepo.save(entity);

			List<String> gstins = reqDto.getGstins();

			// saving in child table
			List<ITCRevesal180ComputeGstinDetailsEntity> gstnDetailEntity =
					gstins
					.stream()
					.map(o -> new ITCRevesal180ComputeGstinDetailsEntity(o,
							entity.getComputeId()))
					.collect(Collectors.toList());

			gstinRepo.saveAll(gstnDetailEntity);

			
			//test
		  //test.callProcs(configId);
			
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);
			String groupCode = TenantContext.getTenantId();

			asyncJobsService.createJob(groupCode,
					JobConstants.ITC_REVERSAL_180_COMPUTE,
					jsonParams.toString(), userName, 1L, null, null);

		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.error("Error Occured ITCReversal180DaysComputeServiceImpl");
			return "failure";
		}

		return "Success";
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT RECON_REPORT_SEQ.nextval FROM DUMMY";

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