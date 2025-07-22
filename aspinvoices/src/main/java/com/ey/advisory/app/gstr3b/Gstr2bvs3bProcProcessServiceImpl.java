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
import com.ey.advisory.app.data.entities.client.Gstr2bvs3bStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2bVs3bStatusRepository;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("Gstr2bvs3bProcProcessServiceImpl")
public class Gstr2bvs3bProcProcessServiceImpl {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2bvs3bProcProcessServiceImpl.class);
	@Autowired
	@Qualifier(value = "Gstr2bvs3bProcProcessDaoImpl")
	private Gstr2bvs3bProcProcessDaoImpl daoImpl;

	@Autowired
	@Qualifier("Gstr2bVs3bStatusRepository")
	private Gstr2bVs3bStatusRepository gstr2bVs3bStatusRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	AsyncJobsService asyncJobsService;

	public String fetchgstr1vs3bProc(
			final Gstr1VsGstr3bProcessSummaryReqDto reqDto) {
		String msg = "Recon Initiated Successfully ";
		;
		List<String> gstinList = getDataSecurity(reqDto);
		Long batchId = generateCustomId(entityManager);
		LOGGER.error("batchId : " + batchId);
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();
		List<Gstr2bvs3bStatusEntity> entityList = new ArrayList<Gstr2bvs3bStatusEntity>();
		if (gstinList != null && !gstinList.isEmpty()) {
			for (String gstinvalue : gstinList) {
				int taxPeriodFrom = GenUtil.getDerivedTaxPeriod(reqDto.getTaxPeriodFrom());
				int taxPeriodTo = GenUtil.getDerivedTaxPeriod(reqDto.getTaxPeriodTo());

				gstr2bVs3bStatusRepository.gstr2bvs3bInActiveUpdate(gstinvalue,
						taxPeriodFrom, taxPeriodTo);
				Gstr2bvs3bStatusEntity entity = new Gstr2bvs3bStatusEntity();
				entity.setBatchId(batchId);
				entity.setStatus("INITIATED");
				entity.setGstin(gstinvalue);
				entity.setTaxPeriodFrom(taxPeriodFrom);
				entity.setTaxPeriodTo(taxPeriodTo);
				entity.setCreatedOn(
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				entity.setCreatedBy(userName);
				entityList.add(entity);
			}
			gstr2bVs3bStatusRepository.saveAll(entityList);
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("batchId", batchId);

			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode, JobConstants.GSTR2Bvs3BRecon,
					jsonParams.toString(), userName, 1L, null, null);
		}
		return msg;
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

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT RECON_REPORT_SEQ.nextval " + "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((Long) query.getSingleResult());

		return seqId;
	}
}
