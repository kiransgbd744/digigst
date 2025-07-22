package com.ey.advisory.app.anx2.initiaterecon;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateMatchingReconDao;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconGstinEntity;
import com.ey.advisory.app.data.entities.client.asprecon.InwardEinvoiceReconAddlReportsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEinvoiceReconAddlReportsRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr2InitiateMatchingReconDaoImpl")
public class Gstr2InitiateMatchingReconDaoImpl
		implements Gstr2InitiateMatchingReconDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	@Autowired
	@Qualifier("Gstr2ReconGstinRepository")
	Gstr2ReconGstinRepository reconGstinRepo;

	@Autowired
	@Qualifier("Gstr2Recon2BPRAddlReportsRepository")
	Gstr2Recon2BPRAddlReportsRepository addl2BPRReportRepo;

	@Autowired
	@Qualifier("InwardEinvoiceReconAddlReportsRepository")
	InwardEinvoiceReconAddlReportsRepository addlInwardEinvoiceReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;
	
	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigRepo;


	private static final String CONF_KEY = "gstr2.recon.2bpr.report.chunk.size";
	private static final String CONF_CATEG = "RECON_REPORTS";

	private static List<String> status = ImmutableList.of(
			ReconStatusConstants.RECON_INITIATED,
			ReconStatusConstants.RECON_INPROGRESS,
			ReconStatusConstants.RECON_IN_QUEUE);
	
	 public static final Map<String, Integer> RPT_TYP_MAP = new HashMap<String, Integer>() {{
	        put("Exact Match", 1);
	        put("Match With Tolerance", 2);
	        put("Value Mismatch", 3);
	        put("POS Mismatch", 4);
	        put("Doc Date Mismatch", 5);
	        put("Doc Type Mismatch", 6);
	        put("Doc No Mismatch I", 7);
	        put("Multi-Mismatch", 8);
	        put("Potential-I", 9);
	        put("Doc No Mismatch II", 10);
	        put("Potential-II", 11);
	        put("Logical Match", 12);
	        put("Addition in 2A_6A", 13);
	        put("Addition in PR", 14);
	        put("Addition in 2B", 13);
	        put("Force_Match_Records", 17);
	        put("Doc No & Doc Date Mismatch", 25);
	        put("Consolidated PR 2A Report IMS", 16);
	        put("Consolidated PR 2B Report", 16);
	        put("Consolidated PR 2A_6A Report", 16);
	        put("Dropped 2A_6A Records Report", 15);
	        put("ISD Matching Report", 26);
	    }};
	    

	@Override
	public String createReconcileData(List<String> gstins, Long entityId,
			String toTaxPeriod2A, String fromTaxPeriod2A, String toTaxPeriodPR,
			String fromTaxPeriodPR, String toDocDate, String fromDocDate,
			List<String> addlReportsList, String reconType,
			Boolean mandatoryReports) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr2InitiateMatchingReconDaoImpl"
					+ ".createReconcileData() method";
			LOGGER.debug(msg);
		}

		try {
			List<String> type = new ArrayList<>();

			if ("EINVPR".equalsIgnoreCase(reconType)) {
				type = Arrays.asList("EINVPR");

			} else {
				type = Arrays.asList("2BPR");

			}
			List<Gstr2ReconConfigEntity> reconEntity = reconConfigRepo
					.findByTypeIn(type);
			Long count = reconEntity.stream()
					.filter(o -> status.contains(o.getStatus())).count();

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			Long configId = generateCustomId(entityManager);

			Gstr2ReconConfigEntity entity = new Gstr2ReconConfigEntity();
			//159894-US
			Optional<String> findIsItcRejectedOpted = entityConfigRepo
						.findIsItcRejectedOpted(entityId,
								"Whether GSTR 2B rejected tables records should participate in recon 2BvsPR?",
								"R");
			String optedAns = findIsItcRejectedOpted.orElse(null);

			if (optedAns != null && !optedAns.trim().isEmpty()) {
			    if ("A".equalsIgnoreCase(optedAns)) {
			        entity.setIsItcRejOpted("Yes");
			    } else {
			        entity.setIsItcRejOpted("No");
			    }
			} else {
			    entity.setIsItcRejOpted("No"); // Default value
			}


			if ((toDocDate != null && toDocDate != "" && !toDocDate.isEmpty())
					&& (fromDocDate != null && fromDocDate != ""
							&& !fromDocDate.isEmpty())) {
				entity.setToDocDate(LocalDate.parse(toDocDate));
				entity.setFromDocDate(LocalDate.parse(fromDocDate));
				entity.setRequestType("Document Date Wise");

			}
			entity.setIsMandatory(mandatoryReports);
			entity.setReportChunkSize(getChunkSize());
			entity.setEntityId(entityId);
			entity.setConfigId(configId);
			entity.setCreatedDate(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			entity.setType(reconType);
			entity.setPan(gstins.get(0).substring(2, 12));

			if (userName != null)
				entity.setCreatedBy(userName);

			else {
				entity.setCreatedBy("SYSTEM");
			}

			if ((toTaxPeriod2A != null && toTaxPeriod2A != ""
					&& !toTaxPeriod2A.isEmpty())
					&& (fromTaxPeriod2A != null && fromTaxPeriod2A != ""
							&& !fromTaxPeriod2A.isEmpty())
					&& (toTaxPeriodPR != null && toTaxPeriodPR != ""
							&& !toTaxPeriodPR.isEmpty())
					&& (fromTaxPeriodPR != null && fromTaxPeriodPR != ""
							&& !fromTaxPeriodPR.isEmpty())) {
				entity.setToTaxPeriodPR(Integer.parseInt(toTaxPeriodPR));
				entity.setFromTaxPeriodPR(Integer.parseInt(fromTaxPeriodPR));
				entity.setToTaxPeriod2A(Integer.parseInt(toTaxPeriod2A));
				entity.setFromTaxPeriod2A(Integer.parseInt(fromTaxPeriod2A));
				entity.setRequestType("Tax Period Wise");
			}
			entity.setStatus(ReconStatusConstants.RECON_REQUESTED);
			if (addlReportsList.contains("Consolidated IMPG Report")) {
				entity.setImpgRecon(true);
			}
			Gstr2ReconConfigEntity obj = reconConfigRepo.save(entity);

			// saving in child table
			List<Gstr2ReconGstinEntity> reconGstinObjList = gstins.stream()
					.map(o -> new Gstr2ReconGstinEntity(o, obj.getConfigId()))
					.collect(Collectors.toList());

			reconGstinRepo.saveAll(reconGstinObjList);

			// SAVING IN TBL_RECON_RPT_DWNLD table
			if (reconType != null && reconType.equalsIgnoreCase("2APR")) {
				List<Gstr2ReconAddlReportsEntity> addlEntityList = addlReportsList
	                    .stream()
	                    .map(o -> {
	                        Integer rptid = RPT_TYP_MAP.get(o);                   
	                        return new Gstr2ReconAddlReportsEntity(o, obj.getConfigId(), false, rptid != null ? rptid : null);  // -1 or some default value
	                    }).collect(Collectors.toList());
				addlReportRepo.saveAll(addlEntityList);
				
			} // SAVING IN TBL_RECON_2BPR_RPT_DWNLD table
			else if (reconType != null && reconType.equalsIgnoreCase("2BPR")) {
				List<Gstr2Recon2BPRAddlReportsEntity> addl2BPREntityList = addlReportsList
	                    .stream()
	                    .map(o -> {
	                        Integer rptid = RPT_TYP_MAP.get(o);                   
	                        return new Gstr2Recon2BPRAddlReportsEntity(o, obj.getConfigId(), false, rptid != null ? rptid : null);  // -1 or some default value
	                    }).collect(Collectors.toList());
				addl2BPRReportRepo.saveAll(addl2BPREntityList);
				
			} else if (reconType != null
					&& reconType.equalsIgnoreCase("EINVPR")) {
				List<InwardEinvoiceReconAddlReportsEntity> addlEinvVsPrREntityList = addlReportsList
						.stream().map(o -> new InwardEinvoiceReconAddlReportsEntity(o,
								obj.getConfigId(), false))
						.collect(Collectors.toList());

				addlInwardEinvoiceReportRepo.saveAll(addlEinvVsPrREntityList);
			}

			if (count == 0) {
				reconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.RECON_IN_QUEUE, null,
						LocalDateTime.now(), configId);

				JsonObject jsonParams = new JsonObject();
				jsonParams.addProperty("configId", configId);

				String groupCode = TenantContext.getTenantId();
				if ("EINVPR".equalsIgnoreCase(reconType)) {
					
					reconConfigRepo.updateReconConfigStatusAndReportName(
							ReconStatusConstants.RECON_INITIATED, null,
							LocalDateTime.now(), entity.getConfigId());
					
					asyncJobsService.createJob(groupCode,
							JobConstants.EINV_RECON_INITIATE,
							jsonParams.toString(), userName, 1L, null, null);
				} else {
					/*asyncJobsService.createJob(groupCode,
							JobConstants.GSTR2_RECON_INITIATE,
							jsonParams.toString(), userName, 1L, null, null);*/
				}
			} else {

				reconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.RECON_IN_QUEUE, null,
						LocalDateTime.now(), configId);
			}
		} catch (Exception ex) {

			LOGGER.error("Gstr2InitiateMatchingReconDaoImpl error "
					+ "while creating job for 2BPR {} :", ex);

			throw new AppException(ex);
		}

		return "Success";
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT RECON_REPORT_SEQ.nextval " + "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((BigInteger) query.getSingleResult()).longValue();

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

	private Integer getChunkSize() {

		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
		String chunkSize = config.getValue();
		return (Integer.valueOf(chunkSize));
	}

}
