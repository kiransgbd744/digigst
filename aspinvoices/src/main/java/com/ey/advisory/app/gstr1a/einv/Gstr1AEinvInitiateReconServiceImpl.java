package com.ey.advisory.app.gstr1a.einv;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.data.entities.client.Gstr1EInvReconConfigEntity;
import com.ey.advisory.app.data.entities.client.Gstr1EInvReconGstinDetailEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1APRvsSubmReconGstinDetailEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1APrVsSubmittedReconConfigEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1APRvsSubReconGstinDetailsRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ASubmittedReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.EinvReconReportDownloadRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1EInvReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1EInvReconGstinDetailsRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Service("Gstr1AEinvInitiateReconServiceImpl")
@Slf4j
public class Gstr1AEinvInitiateReconServiceImpl
		implements Gstr1AEinvInitiateReconService {

	@Autowired
	EntityService entityService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr1EInvReconConfigRepository")
	Gstr1EInvReconConfigRepository gstr1InvReconrepo;

	@Autowired
	@Qualifier("Gstr1ASubmittedReconConfigRepository")
	Gstr1ASubmittedReconConfigRepository gstr1SubmReconrepo;

	@Autowired
	@Qualifier("Gstr1EInvReconGstinDetailsRepository")
	Gstr1EInvReconGstinDetailsRepository gstr1ReconGstin;

	@Autowired
	@Qualifier("EinvReconReportDownloadRepository")
	EinvReconReportDownloadRepository einvchunkrepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr1APRvsSubReconGstinDetailsRepository")
	Gstr1APRvsSubReconGstinDetailsRepository gstr1PrSubmReconGstin;

	/*
	 * @Autowired Gstr6ComputeCredDistDataRepository
	 * gstr6ComputeCredDistDataRepository;
	 */

	private static final Map<String, String> STATUS_MAP = ImmutableMap
			.<String, String>builder().put("NOT INITIATED", "NOT INITIATED")
			.put("REPORT_GENERATION_FAILED", "FAILED")
			.put("REPORT_GENERATED", "SUCCESS")
			.put("RECON_REQUESTED", "INITIATED")
			.put("RECON_INITIATED", "INITIATED")
			.put("RECON_COMPLETED", "SUCCESS").put("NO_DATA_FOUND", "SUCCESS")
			.build();

	@Override
	public List<Gstr1AEinvInitiateReconGstinDetailsDto> getGstins(
			List<GSTNDetailEntity> gstins, String taxPeriod) {

		LOGGER.debug("Gstr1EinvInitiateReconServiceImpl getGstins Starts");

		List<String> listGstins = new ArrayList<>();

		gstins.forEach(e -> {
			listGstins.add(e.getGstin());
		});

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("In GSTR1 EINV Initiate Recon,"
					+ "Fetching state names for gstins %s", gstins);
			LOGGER.debug(msg);
		}

		Map<String, String> stateNamesMap = entityService
				.getStateNames(listGstins);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"In GSTR1 EINV Initiate Recon,"
							+ "state names for gstins %s are %s",
					gstins, stateNamesMap);
			LOGGER.debug(msg);
		}

		Map<String, String> authMap = authTokenService
				.getAuthTokenStatusForGstins(listGstins);
		Map<String, Gstr1AEinvoiceAndReconStatusDto> reconStatusMap = getReconstatus(
				listGstins, taxPeriod);
		List<Gstr1AEinvInitiateReconGstinDetailsDto> gstnList = new ArrayList<>();

		if (!gstins.isEmpty()) {
			gstins.forEach(e -> {

				Gstr1AEinvInitiateReconGstinDetailsDto dto = new Gstr1AEinvInitiateReconGstinDetailsDto();
				if (!reconStatusMap.isEmpty()) {
					if (reconStatusMap.containsKey(e.getGstin())) {
						String status = reconStatusMap.get(e.getGstin())
								.getReconStatus();
						dto.setReconStatus(STATUS_MAP.get(status));
						dto.setReconCreatedOn(reconStatusMap.get(e.getGstin())
								.getReconCreatedOn() != null
										? reconStatusMap.get(e.getGstin())
												.getReconCreatedOn()
										: null);
					} else {
						dto.setReconStatus("NOT INITIATED");
					}
				} else {
					dto.setReconStatus("NOT INITIATED");
				}
				dto.setGstin(e.getGstin());
				dto.setRegType(e.getRegistrationType());
				dto.setAuth(authMap.get(e.getGstin()));
				dto.setStateName(stateNamesMap.get(e.getGstin()));
				gstnList.add(dto);

			});
		}
		LOGGER.debug("Gstr1EinvInitiateReconServiceImpl getGstins END");

		return gstnList;
	}

	@Override
	public String initiatRecon(ArrayList<String> gstinlist, String taxPeriod,
			Long entityId) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside InitiateReconcileDaoImpl.createReconcileData()"
					+ " method";
			LOGGER.debug(msg);
		}
		try {

			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			Long configId = generateCustomId(entityManager);

			Integer returnPeriod;

			Gstr1EInvReconConfigEntity entity = new Gstr1EInvReconConfigEntity();

			returnPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);
			entity.setEntityId(entityId);
			entity.setReconConfigId(configId);
			entity.setCreatedDate(now);
			entity.setCreatedBy(userName);
			entity.setTaxPeriod(taxPeriod);
			entity.setDerivedTaxPeriod(returnPeriod);
			entity.setStatus(ReconStatusConstants.RECON_REQUESTED);

			Gstr1EInvReconConfigEntity obj = gstr1InvReconrepo.save(entity);

			// saving in child table
			List<Gstr1EInvReconGstinDetailEntity> reconGstinObjList = gstinlist
					.stream()
					.map(o -> new Gstr1EInvReconGstinDetailEntity(o,
							obj.getReconConfigId(), obj.getDerivedTaxPeriod()))
					.collect(Collectors.toList());

			gstr1ReconGstin.saveAll(reconGstinObjList);

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);

			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR1_EINV_INITIATE_RECON,
					jsonParams.toString(), userName, 1L, null, null);
		} catch (Exception ex) {
			String msg = "Exception occured in Gstr1-EINV InitiatRecon";
			LOGGER.error(msg, ex);
			return "failure";
		}

		return "Success";
	}

	@Override
	public String initiatPRvsSubmRecon(List<String> gstinlist,
			String fromTaxPeriod, String toTaxPeriod, Long entityId) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside InitiateReconserviceimpl" + " method";
			LOGGER.debug(msg);
		}
		try {

			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			LocalDateTime now = LocalDateTime.now();
			Long configId = generateCustomId(entityManager);

			Integer fromReturnPeriod;
			Integer toReturnPeriod;
			Boolean isActive = false;
			Gstr1APrVsSubmittedReconConfigEntity entity = new Gstr1APrVsSubmittedReconConfigEntity();

			fromReturnPeriod = GenUtil.convertTaxPeriodToInt(fromTaxPeriod);
			toReturnPeriod = GenUtil.convertTaxPeriodToInt(toTaxPeriod);
			entity.setEntityId(entityId);
			entity.setReconConfigId(configId);
			entity.setCreatedDate(now);
			entity.setCreatedBy(userName);
			entity.setFromTaxPeriod(fromTaxPeriod);
			entity.setToTaxPeriod(toTaxPeriod);
			entity.setFromDerivedTaxPeriod(fromReturnPeriod);
			entity.setToDerivedTaxPeriod(toReturnPeriod);
			entity.setStatus(ReconStatusConstants.RECON_REQUESTED);

			Gstr1APrVsSubmittedReconConfigEntity obj = gstr1SubmReconrepo
					.save(entity);

			// saving in child table

			List<Gstr1APRvsSubmReconGstinDetailEntity> reconGstinObjList = gstinlist
					.stream()
					.map(o -> new Gstr1APRvsSubmReconGstinDetailEntity(o,
							obj.getReconConfigId(), fromReturnPeriod,
							toReturnPeriod, isActive))
					.collect(Collectors.toList());

			gstr1PrSubmReconGstin.saveAll(reconGstinObjList);

			gstr1PrSubmReconGstin.updateOldConfigIdsActiveStatus(configId);
			gstr1PrSubmReconGstin.updatenewConfigIdActiveStatus(configId);

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);

			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR1A_PR_SUBMITTED_INITIATE_RECON,
					jsonParams.toString(), userName, 1L, null, null);
		} catch (Exception ex) {
			String msg = "Exception occured in Gstr1-Pr VS Sub InitiatRecon";
			LOGGER.error(msg, ex);
			return "failure";
		}

		return "Success";

	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT EINV_RECON_SEQ.nextval " + "FROM DUMMY";

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

	@Override
	public List<Gstr1AEinvRequesIdWiseDownloadTabDto> getDownloadData(
			Long configId) {

		List<Object[]> listObj = einvchunkrepo.getDataList(configId);

		List<Gstr1AEinvRequesIdWiseDownloadTabDto> respList = listObj.stream()
				.map(o -> convertToDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return respList;
	}

	@Override
	public List<Gstr1AEinvRequesIdWiseDownloadTabDto> getPrVsSubmDownloadData(
			Long configId) {
		List<Gstr1AEinvRequesIdWiseDownloadTabDto> list = new ArrayList<>();

		String filePath = gstr1SubmReconrepo.getPrVsSubmDataList(configId);
		Gstr1AEinvRequesIdWiseDownloadTabDto gstr1ReqIdWiseDownloDto = new Gstr1AEinvRequesIdWiseDownloadTabDto();
		gstr1ReqIdWiseDownloDto.setFlag(false);
		gstr1ReqIdWiseDownloDto.setPath(filePath);
		gstr1ReqIdWiseDownloDto
				.setReportName("Processed Records Vs Submitted Data");
		if (filePath != null)
			gstr1ReqIdWiseDownloDto.setFlag(true);

		list.add(gstr1ReqIdWiseDownloDto);
		return list;
	}

	private Gstr1AEinvRequesIdWiseDownloadTabDto convertToDto(Object[] arr) {

		Gstr1AEinvRequesIdWiseDownloadTabDto dto = new Gstr1AEinvRequesIdWiseDownloadTabDto();

		if (arr[0] != null && arr[0].toString()
				.equalsIgnoreCase("Gstr1_Einv_Records_Report")) {
			dto.setReportName("Consolidated DigiGST vs EInvoice Data");
		} else {
			dto.setReportName(arr[0] != null ? arr[0].toString() : null);
		}
		dto.setFlag(arr[1].equals(true) ? true : false);
		dto.setPath(arr[2] != null ? arr[2].toString() : null);
		dto.setDocId(arr[3] != null ? arr[3].toString() : null);
		return dto;
	}

	public Map<String, Gstr1AEinvoiceAndReconStatusDto> getReconstatus(
			List<String> gstnsList, String taxPeriod) {

		Integer returnPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);

		String queryString = createQueryString();

		Query q = entityManager.createNativeQuery(queryString);

		q.setParameter("gstnsList", gstnsList);
		q.setParameter("returnPeriod", returnPeriod);

		@SuppressWarnings("unchecked")
		List<Object[]> reconStatusList = q.getResultList();
		Map<String, Gstr1AEinvoiceAndReconStatusDto> recStatusMap = convertToMap(
				reconStatusList);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Gstr1vsEInv: Recon status List%s",
					recStatusMap);
			LOGGER.debug(msg);
		}

		return recStatusMap;

	}

	private Map<String, Gstr1AEinvoiceAndReconStatusDto> convertToMap(
			List<Object[]> reconStatusList) {

		Map<String, Gstr1AEinvoiceAndReconStatusDto> statusMap = new HashMap<>();

		Gstr1AEinvoiceAndReconStatusDto dto = new Gstr1AEinvoiceAndReconStatusDto();

		for (Object[] arr : reconStatusList) {
			dto.setGstin(arr[0] != null ? arr[0].toString() : null);
			if (arr[1] != null) {
				DateTimeFormatter formatter = null;
				String dateTime = arr[1].toString();
				char ch = 'T';
				dateTime = dateTime.substring(0, 10) + ch
						+ dateTime.substring(10 + 1);
				String s1 = dateTime.substring(0, 19);
				formatter = DateTimeFormatter
						.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

				LocalDateTime dateTimes = LocalDateTime.parse(s1, formatter);

				LocalDateTime dateTimeFormatter = EYDateUtil
						.toISTDateTimeFromUTC(dateTimes);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("dd-MM-yyyy : HH:mm:ss");
				String newdate = FOMATTER.format(dateTimeFormatter);
				dto.setReconCreatedOn(newdate);
			} else {
				dto.setReconCreatedOn(null);
			}
			dto.setReconStatus(
					arr[2] != null ? arr[2].toString() : "NOT INITIATED");

			statusMap.put(dto.getGstin(), dto);
		}
		return statusMap;

	}

	private String createQueryString() {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Creating query for Request Status";
			LOGGER.debug(msg);
		}

		String queryStr = "SELECT GD.GSTIN, RC.COMPLETED_ON, CASE WHEN RC.STATUS "
				+ "IS NULL THEN 'NOT INITIATED' " + "ELSE RC.STATUS "
				+ "END AS STATUS "
				+ "FROM GSTR1_EINV_RECON_GSTIN_DETAILS GD LEFT OUTER JOIN "
				+ "GSTR1_EINV_RECON_CONFIG RC "
				+ "ON RC.RECON_CONFIG_ID=GD.RECON_CONFIG_ID "
				+ "WHERE  GD.IS_ACTIVE=TRUE AND GD.GSTIN IN :gstnsList "
				+ "AND GD.RETURN_PERIOD =:returnPeriod";

		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Query created for Request Status : %s",
					queryStr);
			LOGGER.debug(str);
		}

		return queryStr;
	}

}
