package com.ey.advisory.app.gstr3b;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.Gstr1vs3bConfigEntity;
import com.ey.advisory.app.data.entities.client.Gstr1vs3bStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1Vs3bConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1Vs3bStatusRepository;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Sasidhar reddy
 *
 */
@Service("Gstr1vs3bProcProcessServiceImpl")
public class Gstr1vs3bProcProcessServiceImpl {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1vs3bProcProcessServiceImpl.class);
	@Autowired
	@Qualifier(value = "Gstr1vs3bProcProcessDaoImpl")
	private Gstr1vs3bProcProcessDaoImpl daoImpl;

	@Autowired
	@Qualifier(value = "Gstr1Vs3bStatusRepository")
	private Gstr1Vs3bStatusRepository gstr1Vs3bStatusRepository;
	
	@Autowired
	@Qualifier(value = "Gstr1Vs3bConfigRepository")
	private Gstr1Vs3bConfigRepository gstr1Vs3bconfigRepository;
	
	@Autowired
	AsyncJobsService asyncJobsService;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public String fetchgstr1vs3bProc(
			final Gstr1VsGstr3bProcessSummaryReqDto reqDto) {
		
		try{
		List<String> gstinList = getDataSecurity(reqDto);
		
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();
		
		Long configId = generateCustomId(entityManager);
		if (gstinList != null && !gstinList.isEmpty()) {
			
			Gstr1vs3bConfigEntity obj = new Gstr1vs3bConfigEntity();
			
			obj.setConfigId(configId);
			obj.setCreatedOn(LocalDateTime.now());
			obj.setEntityId(reqDto.getEntityId().get(0));
			obj.setStatus(ReconStatusConstants.RECON_REQUESTED);
			obj.setCreatedBy(userName != null ? userName : "SYSTEM");
			obj.setReconType("GSTR1VS3B");
			obj.setDeriverdRetPeriodTo(GenUtil
					.convertTaxPeriodToInt(reqDto.getTaxPeriodTo()));
			obj.setDeriverdRetPeriodFrom(GenUtil
					.convertTaxPeriodToInt(reqDto.getTaxPeriodFrom()));
			gstr1Vs3bconfigRepository.save(obj);
			
			
			List<Gstr1vs3bStatusEntity> gstnEntity = new ArrayList<>();
			
			for (String gstinvalue : gstinList) {
				int derivedRetPerFrom = GenUtil
						.convertTaxPeriodToInt(reqDto.getTaxPeriodFrom());
				int derivedRetPerTo = GenUtil
						.convertTaxPeriodToInt(reqDto.getTaxPeriodTo());

				Gstr1vs3bStatusEntity entity = new Gstr1vs3bStatusEntity();
				entity.setConfigId(configId);
				entity.setStatus("INITIATED");
				entity.setGstin(gstinvalue);
				entity.setDeriverdRetPeriodFrom(derivedRetPerFrom);
				entity.setDeriverdRetPeriodTo(derivedRetPerTo);
				entity.setCreatedOn(
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				entity.setCreatedBy(userName != null ? userName : "SYSTEM");
				
				gstnEntity.add(entity);
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr1vs3bProcProcessServiceImpl proceCall start");
				}
				
			}
			gstr1Vs3bStatusRepository.saveAll(gstnEntity);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1vs3bProcProcessServiceImpl proceCall end ");
			}
			Gson gson = new Gson();
			
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);
			jsonParams.add("reqDto", gson.toJsonTree(reqDto));
			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR1_VS_3b_INITIATE_RECON, jsonParams.toString(),
					userName, 50L, null, null);
			
			
		}
		}catch(Exception ex)
		{
			LOGGER.error("Error occured while saving the GSTR1 vs GSTR3b ",ex);
			throw new AppException(ex);
		}
		return "SUCCESS";
	}

	private List<String> getDataSecurity(
			final Gstr1VsGstr3bProcessSummaryReqDto reqDto) {
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)
						&& dataSecAttrs.get(OnboardingConstant.GSTIN) != null
						&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()) {
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}
		return gstinList;
	}
	
	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT GSTR1VS3B_CONFIG_SEQ.nextval " + "FROM DUMMY";

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