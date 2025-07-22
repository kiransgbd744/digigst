package com.ey.advisory.app.gstr1.einv;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.EinvReconRespConfigEntity;
import com.ey.advisory.app.data.repositories.client.EinvReconRespConfigRepository;
import com.ey.advisory.app.data.repositories.client.EinvReconRespGSTINRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1vsEinvReconRespProcessedRepository;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajesh N K
 *
 */

@Slf4j
@Component("Gstr1EinvInitiateReconReptReqStatusDaoImpl")
public class Gstr1EinvInitiateReconReptReqStatusDaoImpl
		implements Gstr1EinvInitiateReconReptReqStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GstnUserRequestRepository")
	GstnUserRequestRepository gstnUserRequestRepository;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	Gstr1vsEinvReconRespProcessedRepository gstr1vsEinvReconRespProcessedRepository;

	@Autowired
	@Qualifier("EinvReconRespGSTINRepository")
	private EinvReconRespGSTINRepository einvReconRespGSTINRepo;

	@Autowired
	@Qualifier("EinvReconRespConfigRepository")
	private EinvReconRespConfigRepository einvReconRespConfigRepo;
	
	@Autowired
	private CommonUtility commonUtility;

	@Override
	public List<Gstr1EinvInitiateReconReportRequestStatusDto> getReportRequestStatus(
			Gstr2InitiateReconReqDto reqDto, String userName) {
		
		Long entityId=reqDto.getEntityId();
		
		String taxPeriodFrom = reqDto.getTaxPeriodFrom();
		String taxPeriodTo = reqDto.getTaxPeriodTo();
		List<String> initiationByUserId = reqDto.getInitiationByUserId();
		
		String reconStatus=reqDto.getReconStatus();
		
		if (reconStatus != null && !reconStatus.isEmpty()) {
			if (reconStatus.equalsIgnoreCase("REPORT GENERATION FAILED")) {
				reconStatus = "REPORT_GENERATION_FAILED";
			} else if (reconStatus.equalsIgnoreCase("REPORT GENERATED")) {
				reconStatus = "REPORT_GENERATED";
			} else if (reconStatus.equalsIgnoreCase("RECON REQUESTED")) {
				reconStatus = "RECON_REQUESTED";
			} else if (reconStatus.equalsIgnoreCase("RECON INITIATED")) {
				reconStatus = "RECON_INITIATED";
			} else if (reconStatus.equalsIgnoreCase("RECON COMPLETED")) {
				reconStatus = "RECON_COMPLETED";
			} else if (reconStatus.equalsIgnoreCase("RECON FAILED")) {
				reconStatus = "RECON_FAILED";
			}
		}
		
		int returnPeriodFrom = 0;
		if (!Strings.isNullOrEmpty(taxPeriodFrom)) {
			returnPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
			reqDto.setReturnPeriodFrom(returnPeriodFrom);
		}
		int returnPeriodTo = 0;
		if (!Strings.isNullOrEmpty(taxPeriodTo)) {
			returnPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
			reqDto.setReturnPeriodTo(returnPeriodTo);
		}
		
		boolean isusernamereq = commonUtility
				 .getAnsForQueMultipleUserAccessToAsyncReports(entityId);
		String initiatedby="";
		if(isusernamereq){
			initiatedby=" AND RC.CREATED_BY = :userName ";
		}

		String condtion = createQueryCondition(reqDto, isusernamereq, initiatedby);
		String queryString = createQueryString(userName,condtion);

		Query q = entityManager.createNativeQuery(queryString);
		
		if (returnPeriodFrom != 0 && returnPeriodTo != 0) {
			q.setParameter("returnPeriodFrom", returnPeriodFrom);
			q.setParameter("returnPeriodTo", returnPeriodTo);
		}else if (returnPeriodFrom != 0 && returnPeriodTo == 0) {
			q.setParameter("returnPeriodFrom", returnPeriodFrom);
		}
		
		if (!isusernamereq) {
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				q.setParameter("initiationByUserId", initiationByUserId);
			} 
		}
		// userName = "SYSTEM";
		if(isusernamereq){
			q.setParameter("userName", userName);
		}
		
		if (reqDto.getReconStatus() != null
				&& (!reqDto.getReconStatus().isEmpty())) {
			q.setParameter("reconStatus",reconStatus);
		}
		
		q.setParameter("entityId", entityId);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		List<Gstr1EinvInitiateReconReportRequestStatusDto> retList = list
				.stream().map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return retList;
	}
	
	/*
	public static void main(String[] args) {
		Gstr2InitiateReconReqDto reqDto= new Gstr2InitiateReconReqDto();
		
		reqDto.setEntityId(32L);
		reqDto.setReturnPeriodFrom(202406);
		reqDto.setReturnPeriodTo(202406);
		reqDto.setInitiationByUserId(Arrays.asList("Poooo1234"));
		reqDto.setReconStatus("REPORT_GENERATED");
		
		Gstr1EinvInitiateReconReptReqStatusDaoImpl obj = new Gstr1EinvInitiateReconReptReqStatusDaoImpl();
		String condtion = obj.createPrVsSubQueryCondition(reqDto, false, " AND RC.CREATED_BY = :userName ");
		String queryString = obj.createPrVsSubQueryString("",condtion);
		System.out.println(queryString);
	}
	*/
	
	@Override
	public List<Gstr1PrVsSubmReconReportRequestStatusDto> getPrVsSubReportRequestStatus(
			Gstr2InitiateReconReqDto reqDto, String userName) {
		
		Long entityId = reqDto.getEntityId();
		
		String taxPeriodFrom = reqDto.getTaxPeriodFrom();
		String taxPeriodTo = reqDto.getTaxPeriodTo();
		List<String> initiationByUserId = reqDto.getInitiationByUserId();
		
		String reconStatus=reqDto.getReconStatus();
		
		if (reconStatus != null && !reconStatus.isEmpty()) {
			if (reconStatus.equalsIgnoreCase("REPORT GENERATION FAILED")) {
				reconStatus = "REPORT_GENERATION_FAILED";
			} else if (reconStatus.equalsIgnoreCase("REPORT GENERATED")) {
				reconStatus = "REPORT_GENERATED";
			} else if (reconStatus.equalsIgnoreCase("RECON REQUESTED")) {
				reconStatus = "RECON_REQUESTED";
			} else if (reconStatus.equalsIgnoreCase("RECON FAILED")) {
				reconStatus = "RECON_FAILED";
			} else if (reconStatus.equalsIgnoreCase("RECON COMPLETED")) {
				reconStatus = "RECON_COMPLETED";
			} else if (reconStatus.equalsIgnoreCase("RECON INITIATED")) {
				reconStatus = "RECON_INITIATED";
			}
		}
		
		
		boolean isusernamereq = commonUtility
				 .getAnsForQueMultipleUserAccessToAsyncReports(entityId);
		String initiatedby="";
		if(isusernamereq){
			initiatedby=" AND RC.CREATED_BY = :userName ";
		}
		
		int returnPeriodFrom = 0;
		if (!Strings.isNullOrEmpty(taxPeriodFrom)) {
			returnPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
			reqDto.setReturnPeriodFrom(returnPeriodFrom);
		}
		int returnPeriodTo = 0;
		if (!Strings.isNullOrEmpty(taxPeriodTo)) {
			returnPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
			reqDto.setReturnPeriodTo(returnPeriodTo);
		}

		String condtion = createPrVsSubQueryCondition(reqDto, isusernamereq, initiatedby);
		String queryString = createPrVsSubQueryString(userName,condtion);

		Query q = entityManager.createNativeQuery(queryString);

		if (returnPeriodFrom != 0 && returnPeriodTo != 0) {
			q.setParameter("returnPeriodFrom", returnPeriodFrom);
			q.setParameter("returnPeriodTo", returnPeriodTo);
		}else if (returnPeriodFrom != 0 && returnPeriodTo == 0) {
			q.setParameter("returnPeriodFrom", returnPeriodFrom);
		}
		
		if (!isusernamereq) {
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				q.setParameter("initiationByUserId", initiationByUserId);
			} 
		}

		if(isusernamereq){
			q.setParameter("userName", userName);
		}
		
		if (reqDto.getReconStatus() != null
				&& (!reqDto.getReconStatus().isEmpty())) {
			q.setParameter("reconStatus",reconStatus);
		}
		
		q.setParameter("entityId", entityId);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		List<Gstr1PrVsSubmReconReportRequestStatusDto> retList = list
				.stream().map(o -> convertPrSubmitted(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return retList;
	}

	private Gstr1EinvInitiateReconReportRequestStatusDto convert(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Gstr1EinvInitiateReconReportRequestStatusDto object";
			LOGGER.debug(str);
		}

		Gstr1EinvInitiateReconReportRequestStatusDto dto = new Gstr1EinvInitiateReconReportRequestStatusDto();

		BigInteger b = GenUtil.getBigInteger(arr[0]);
		Long requestId = b.longValue();
		dto.setRequestId(requestId);
		Timestamp date = (Timestamp) arr[1];
		LocalDateTime dt = date.toLocalDateTime();
		dto.setInitiatedOn(
				getFormattedTime(EYDateUtil.toISTDateTimeFromUTC(dt)));
		dto.setInitiatedBy((String) arr[2]);

		if (arr[3] != null) {
			String taxPeriod = ((String) arr[3]).toString();
			dto.setTaxPeriod(taxPeriod);
		} else {
			dto.setTaxPeriod(null);
		}

		date = (Timestamp) arr[4];
		String ldt = date != null
				? getFormattedTime(
						EYDateUtil.toISTDateTimeFromUTC(date.toLocalDateTime()))
				: null;
		dto.setCompletionOn(ldt);
		dto.setStatus((String) arr[5]);
		dto.setPath((String) arr[6]);
		BigInteger bi = GenUtil.getBigInteger(arr[7]);
		Integer gstnCount = bi.intValue();
		dto.setGstinCount(gstnCount);

		GstinDto gstinDto = new GstinDto((String) arr[8]);
		dto.setGstins(new ArrayList<GstinDto>(Arrays.asList(gstinDto)));

		return dto;
	}
	
	private Gstr1PrVsSubmReconReportRequestStatusDto convertPrSubmitted(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Gstr1EinvInitiateReconReportRequestStatusDto object";
			LOGGER.debug(str);
		}

		Gstr1PrVsSubmReconReportRequestStatusDto dto = new Gstr1PrVsSubmReconReportRequestStatusDto();

		BigInteger b = GenUtil.getBigInteger(arr[0]);
		Long requestId = b.longValue();
		dto.setRequestId(requestId);
		Timestamp date = (Timestamp) arr[1];
		LocalDateTime dt = date.toLocalDateTime();
		dto.setInitiatedOn(
				getFormattedTime(EYDateUtil.toISTDateTimeFromUTC(dt)));
		dto.setInitiatedBy((String) arr[2]);

		if (arr[3] != null) {
			String fromTaxPeriod = ((String) arr[3]).toString();
			dto.setFromTaxPeriod(fromTaxPeriod);
		} else {
			dto.setFromTaxPeriod(null);
		}
		if (arr[4] != null) {
			String toTaxPeriod = ((String) arr[4]).toString();
			dto.setToTaxPeriod(toTaxPeriod);
		} else {
			dto.setToTaxPeriod(null);
		}

		date = (Timestamp) arr[5];
		String ldt = date != null
				? getFormattedTime(
						EYDateUtil.toISTDateTimeFromUTC(date.toLocalDateTime()))
				: null;
		dto.setCompletionOn(ldt);
		dto.setStatus((String) arr[6]);
		dto.setPath((String) arr[7]);
		BigInteger bi = GenUtil.getBigInteger(arr[8]);
		Integer gstnCount = bi.intValue();
		dto.setGstinCount(gstnCount);

		GstinDto gstinDto = new GstinDto((String) arr[9]);
		dto.setGstins(new ArrayList<GstinDto>(Arrays.asList(gstinDto)));

		return dto;
	}

	private String getFormattedTime(LocalDateTime dateStr) {
		if (dateStr == null) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		return formatter.format(dateStr);
	}

	private String createQueryString(String userName, String condtion) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Creating query for Request Status";
			LOGGER.debug(msg);
		}
		String queryStr = "SELECT RC.RECON_CONFIG_ID AS REQUEST_ID, "
				+ "RC.CREATED_DATE, RC.CREATED_BY, RC.TAX_PERIOD, "
				+ "RC.COMPLETED_ON, "
				+ "RC.STATUS, RC.FILE_PATH AS PATH, COUNT(*) "
				+ "AS GSTIN_COUNT, RGD.GSTIN FROM "
				+ "GSTR1_EINV_RECON_CONFIG RC "
				+ "INNER JOIN GSTR1_EINV_RECON_GSTIN_DETAILS RGD "
				+ "ON RGD.RECON_CONFIG_ID = RC.RECON_CONFIG_ID "
				+ "WHERE RC.ENTITY_ID =:entityId"
				+ condtion
				+ " GROUP BY RC.RECON_CONFIG_ID, RC.CREATED_DATE, RC.CREATED_BY, "
				+ "RC.COMPLETED_ON, RC.STATUS, RC.TAX_PERIOD, RGD.GSTIN, "
				+ "RC.FILE_PATH";

		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Query created for Request Status : %s",
					queryStr);
			LOGGER.debug(str);
		}

		return queryStr;
	}
	
	private String createPrVsSubQueryString(String userName, String condtion) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Creating query for Request Status";
			LOGGER.debug(msg);
		}
		String queryStr = "SELECT RC.RECON_CONFIG_ID AS REQUEST_ID, "
				+ "RC.CREATED_DATE, RC.CREATED_BY, RC.FROM_TAX_PERIOD, RC.To_TAX_PERIOD, "
				+ "RC.COMPLETED_ON, "
				+ "RC.STATUS, RC.FILE_PATH AS PATH, COUNT(*) "
				+ "AS GSTIN_COUNT, RGD.GSTIN FROM "
				+ "GSTR1_PR_SUBMITED_RECON_CONFIG RC "
				+ "INNER JOIN GSTR1_PR_VS_SUBM_RECON_GSTIN_DETAILS RGD "
				+ "ON RGD.RECON_CONFIG_ID = RC.RECON_CONFIG_ID WHERE"
				+ " RC.ENTITY_ID =:entityId "
				+  condtion
				+ " GROUP BY RC.RECON_CONFIG_ID, RC.CREATED_DATE, RC.CREATED_BY, "
				+ "RC.COMPLETED_ON, RC.STATUS, RC.FROM_TAX_PERIOD,RC.To_TAX_PERIOD, RGD.GSTIN, "
				+ "RC.FILE_PATH";

		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Query created for Request Status : %s",
					queryStr);
			LOGGER.debug(str);
		}

		return queryStr;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Gstr1EinvoiceAndReconStatusDto> getGstr1status(
			List<String> gstnsList, String taxPeriod) {

		String apiSection = "GSTR1_EINV";
		List<String> getType = new ArrayList<>();
		getType.add("B2B");
		getType.add("CDNR");
		getType.add("CDNUR");
		getType.add("EXP");

		Integer returnPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);

		// Integer taxP = Integer.valueOf(taxPeriod);

		List<Object[]> eivoiceStatusList = batchRepo.findActiveGstr1GetStatus(
				gstnsList, apiSection, getType, taxPeriod);

		List<Gstr1EinvoiceAndReconStatusDto> listEinvStatus = eivoiceStatusList
				.stream().map(o -> convertToDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstr1vsEInv: EINV Initiate Recon status %s",
					listEinvStatus);
			LOGGER.debug(msg);
		}

		String queryString = createQueryString(gstnsList, returnPeriod);

		Query q = entityManager.createNativeQuery(queryString);

		q.setParameter("gstnsList", gstnsList);
		q.setParameter("returnPeriod", returnPeriod);

		@SuppressWarnings("unchecked")
		List<Object[]> reconStatusList = q.getResultList();

		List<Gstr1EinvoiceAndReconStatusDto> recStatusList = reconStatusList
				.stream().map(o -> convertDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Gstr1vsEInv: Recon status List%s",
					recStatusList);
			LOGGER.debug(msg);
		}

		/*
		 * DateFormat formatter = new SimpleDateFormat("MMyyyy"); DateFormat
		 * dateFormat = new SimpleDateFormat("yyyyMM"); Date date = null; try {
		 * date = (Date) formatter.parse(taxPeriod); } catch (ParseException e1)
		 * { LOGGER.error("Parse Exception of date " + e1.getMessage()); }
		 * String returnPer = dateFormat.format(date);
		 */

		List<Object[]> recoResponse = einvReconRespGSTINRepo
				.getReconStatus(gstnsList, taxPeriod);

		List<Gstr1EinvoiceAndReconStatusDto> recStatusResponseList = recoResponse
				.stream().map(o -> convertResconResponseDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		List<Long> reconConfigId = new ArrayList<>();

		recStatusResponseList.forEach(configId -> {

			reconConfigId.add(configId.getReconConfigId());

		});

		Map<String, Pair<String, LocalDateTime>> recMap = new HashMap<>();

		if (!reconConfigId.isEmpty() && reconConfigId != null) {
			List<EinvReconRespConfigEntity> recoCreatedOn = einvReconRespConfigRepo
					.getReconCreatedOn(reconConfigId);

			recStatusResponseList.forEach(e -> {

				recoCreatedOn.forEach(e1 -> {

					if (e.getReconConfigId() == e1.getReconRespConfigId()) {
						recMap.put(e.getGstin(),
								new Pair(e.getReconResponseStatus(),
										e1.getCompletedOn()));
					}
				});

			});

		}

		List<Object[]> reportCountSR = gstr1vsEinvReconRespProcessedRepository
				.getGstr1vsEinvReportCountSR(gstnsList, taxPeriod);

		List<Object[]> reportCountEinv = gstr1vsEinvReconRespProcessedRepository
				.getGstr1vsEinvReportCountEinv(gstnsList, taxPeriod);

		List<Object[]> deleteCount = gstr1vsEinvReconRespProcessedRepository
				.getGstr1vsEinvDeleteCount(gstnsList, taxPeriod);

		List<Gstr1EinvoiceAndReconStatusDto> listReconCountSR = reportCountSR
				.stream().map(o -> convertToDtoSR(o))
				.collect(Collectors.toCollection(ArrayList::new));

		List<Gstr1EinvoiceAndReconStatusDto> listReconCountEinv = reportCountEinv
				.stream().map(o -> convertToDtoSR(o))
				.collect(Collectors.toCollection(ArrayList::new));

		List<Gstr1EinvoiceAndReconStatusDto> listDeleteCount = deleteCount
				.stream().map(o -> convertToDtoDelete(o))
				.collect(Collectors.toCollection(ArrayList::new));

		Map<String, Pair<String, String>> einv = new HashMap<>();

		listEinvStatus.forEach(e -> {
			einv.put(e.getGstin(),
					new Pair(e.getEInvoiceStatus(), e.getEinvCreatedOn()));
		});

		Map<String, Pair<String, String>> recon = new HashMap<>();

		recStatusList.forEach(e -> {
			recon.put(e.getGstin(),
					new Pair(e.getReconStatus(), e.getReconCreatedOn()));
		});

		// Report count for SR
		Map<String, Pair<String, Integer>> countSRMap = new HashMap<>();

		listReconCountSR.forEach(e -> {
			String key = e.getGstin() + e.getReportCategory();
			countSRMap.put(key,
					new Pair(e.getReportCategory(), e.getReportCount()));
		});

		// Report count for EINV
		Map<String, Pair<String, Integer>> countEinvMap = new HashMap<>();

		listReconCountEinv.forEach(e -> {
			String key = e.getGstin() + e.getReportCategory();
			countEinvMap.put(key,
					new Pair(e.getReportCategory(), e.getReportCount()));

		});

		// Report count for Delete
		Map<String, Pair<String, Integer>> deleteMap = new HashMap<>();

		listDeleteCount.forEach(e -> {
			String key = e.getGstin() + e.getReportCategory();
			deleteMap.put(key,
					new Pair(e.getReportCategory(), e.getDeleteCount()));
		});

		List<Gstr1EinvoiceAndReconStatusDto> respList = new ArrayList<>();

		gstnsList.forEach(gstin -> {
			Gstr1EinvoiceAndReconStatusDto resp = new Gstr1EinvoiceAndReconStatusDto();
			resp.setGstin(gstin);
			if (einv.containsKey(gstin)) {
				resp.setEInvoiceStatus(einv.get(gstin).getValue0());
				resp.setEinvCreatedOn(einv.get(gstin).getValue1());
			} else {
				resp.setEInvoiceStatus("NOT INITIATED");
			}
			if (recon.containsKey(gstin)) {
				resp.setReconStatus(recon.get(gstin).getValue0());
				resp.setReconCreatedOn(recon.get(gstin).getValue1());
			} else {
				resp.setReconStatus("NOT INITIATED");
			}

			if (countEinvMap.containsKey(gstin + "1")) {
				resp.setAdditionalAOrPGSTR1(
						countEinvMap.get(gstin + "1").getValue1());
			} else {
				resp.setAdditionalAOrPGSTR1(0);
			}

			if (countEinvMap.containsKey(gstin + '2')) {

				resp.setErrorsInAOrPGSTR1NotAvl(0);
			} else {
				resp.setErrorsInAOrPGSTR1NotAvl(0);
			}

			if (countSRMap.containsKey(gstin + "3")) {
				resp.setErrorsInAOrPGSTR1(
						countSRMap.get(gstin + "3").getValue1());
			} else {
				resp.setErrorsInAOrPGSTR1(0);
			}

			if (countSRMap.containsKey(gstin + "4")) {
				resp.setAdditionalInDigiGSTNotAvl(
						countSRMap.get(gstin + "4").getValue1());
			} else {
				resp.setAdditionalInDigiGSTNotAvl(0);
			}

			if (countSRMap.containsKey(gstin + "5")) {
				resp.setAvlInDigiGstAndAvlInAorPGSTR(
						countSRMap.get(gstin + "5").getValue1());
			} else {
				resp.setAvlInDigiGstAndAvlInAorPGSTR(0);
			}
			if (deleteMap.containsKey(gstin + "6")) {
				resp.setDeleteCount(deleteMap.get(gstin + "6").getValue1());
			} else {
				resp.setDeleteCount(0);
			}

			if (recMap.containsKey(gstin)) {
				resp.setReconResponseStatus(recMap.get(gstin).getValue0());
				if (recMap.get(gstin).getValue1() != null) {

					LocalDateTime dateTimeFormatter = EYDateUtil
							.toISTDateTimeFromUTC(
									recMap.get(gstin).getValue1());
					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("dd-MM-yyyy : HH:mm:ss");
					String newdate = FOMATTER.format(dateTimeFormatter);
					/*
					 * String date = EYDateUtil
					 * .toISTDateTimeFromUTC(recMap.get(gstin).getValue1())
					 * .toString();
					 */
					resp.setReconResponseCreatedOn(newdate);
				}

			} else {
				resp.setReconResponseStatus("NOT INITIATED");
			}
			respList.add(resp);

		});

		return respList;
	}

	private Gstr1EinvoiceAndReconStatusDto convertToDtoDelete(Object[] arr) {

		Gstr1EinvoiceAndReconStatusDto dto = new Gstr1EinvoiceAndReconStatusDto();

		dto.setGstin(arr[0] != null ? arr[0].toString() : null);
		dto.setReportCategory(
				arr[1] != null ? Integer.valueOf(arr[1].toString()) : null);
		dto.setDeleteCount(
				arr[2] != null ? Integer.valueOf(arr[2].toString()) : 0);

		return dto;
	}

	private Gstr1EinvoiceAndReconStatusDto convertResconResponseDto(
			Object[] arr) {

		Gstr1EinvoiceAndReconStatusDto dto = new Gstr1EinvoiceAndReconStatusDto();

		dto.setGstin(arr[0] != null ? arr[0].toString() : null);
		dto.setReconConfigId(
				arr[1] != null ? Long.valueOf(arr[1].toString()) : null);
		dto.setReconResponseStatus(
				arr[2] != null ? arr[2].toString() : "NOT INITIATED");

		return dto;
	}

	private Gstr1EinvoiceAndReconStatusDto convertToDtoSR(Object[] arr) {

		Gstr1EinvoiceAndReconStatusDto dto = new Gstr1EinvoiceAndReconStatusDto();

		dto.setGstin(arr[0] != null ? arr[0].toString() : null);
		dto.setReportCategory(
				arr[1] != null ? Integer.valueOf(arr[1].toString()) : null);
		dto.setReportCount(
				arr[2] != null ? Integer.valueOf(arr[2].toString()) : 0);

		return dto;
	}

	private Gstr1EinvoiceAndReconStatusDto convertDto(Object[] arr) {

		Gstr1EinvoiceAndReconStatusDto dto = new Gstr1EinvoiceAndReconStatusDto();
		dto.setGstin(arr[0] != null ? arr[0].toString() : null);
		if (arr[1] != null) {
			DateTimeFormatter formatter = null;
			String dateTime = arr[1].toString();
			char ch = 'T';
			dateTime = dateTime.substring(0, 10) + ch
					+ dateTime.substring(10 + 1);
			String s1 = dateTime.substring(0, 19);
			formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

			LocalDateTime dateTimes = LocalDateTime.parse(s1, formatter);

			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dateTimes);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);
			dto.setReconCreatedOn(newdate);
		}
		dto.setReconStatus(
				arr[2] != null ? arr[2].toString() : "NOT INITIATED");

		return dto;

	}

	private Gstr1EinvoiceAndReconStatusDto convertToDto(Object[] arr) {

		Gstr1EinvoiceAndReconStatusDto dto = new Gstr1EinvoiceAndReconStatusDto();
		dto.setGstin(arr[0] != null ? arr[0].toString() : null);
		if (arr[1] != null) {
			dto.setEInvoiceStatus(arr[1].toString());
		} else {
			dto.setEInvoiceStatus("NOT INITIATED");
		}
		if (arr[2] != null) {
			LocalDateTime dt = (LocalDateTime) arr[2];
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);
			dto.setEinvCreatedOn(newdate);
		}
		return dto;
	}

	private String createQueryString(List<String> gstnsList,
			Integer returnPeriod) {

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
	
	private String createPrVsSubQueryCondition(Gstr2InitiateReconReqDto reqDto, boolean isusernamereq, 
			String initiatedby) {
		if (LOGGER.isDebugEnabled()) {
		String msg = " Begin Gstr1EinvInitiateReconReptReqStatusDaoImpl.createPrVsSubQueryCondition() ";
		LOGGER.debug(msg);
		}
		
		StringBuilder queryBuilder = new StringBuilder();
		
		// FROM_TAX_PERIOD, TO_TAX_PERIOD  -> VARCHAR
		// FROM_RETURN_PERIOD,TO_RETURN_PERIOD  -> INTEGER
		// from_period >= 202305 and to_period <=202307
		
		
		/*
		if (reqDto.getReturnPeriodFrom() != 0 && reqDto.getReturnPeriodTo() != 0) {
			queryBuilder.append(" AND RC.FROM_RETURN_PERIOD BETWEEN "
					+ ":returnPeriodFrom AND :returnPeriodTo");
			
			queryBuilder.append(" AND RC.TO_RETURN_PERIOD BETWEEN "
					+ ":returnPeriodFrom AND :returnPeriodTo");
		}*/
		
		if (reqDto.getReturnPeriodFrom() != 0 && reqDto.getReturnPeriodTo() != 0) {
			queryBuilder.append("AND ( RC.FROM_RETURN_PERIOD >= :returnPeriodFrom"
					+ " AND RC.TO_RETURN_PERIOD <= :returnPeriodTo )");
		}else if (reqDto.getReturnPeriodFrom() != 0 && reqDto.getReturnPeriodTo() == 0) {
			queryBuilder.append("AND RC.FROM_RETURN_PERIOD >= :returnPeriodFrom");
		}
		
		if (!isusernamereq) {
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				queryBuilder.append(" AND RC.CREATED_BY IN (:initiationByUserId) ");
			} 
		}
		
		if(isusernamereq){
			queryBuilder.append(initiatedby);
		}
		
		if (reqDto.getReconStatus() != null
			&& (!reqDto.getReconStatus().isEmpty())) {
			queryBuilder.append(" AND RC.STATUS =:reconStatus ");
		}
		
		return queryBuilder.toString();
	}
	
	private String createQueryCondition(Gstr2InitiateReconReqDto reqDto, boolean isusernamereq, 
			String initiatedby) {
		if (LOGGER.isDebugEnabled()) {
		String msg = " Begin Gstr1EinvInitiateReconReptReqStatusDaoImpl.createPrVsSubQueryCondition() ";
		LOGGER.debug(msg);
		}
		
		StringBuilder queryBuilder = new StringBuilder();
		
		// TAX_PERIOD  -> VARCHAR
		// RETURN_PERIOD -> INTEGER
		// from_period >= 202305 and to_period <=202307
		
		if (reqDto.getReturnPeriodFrom() != 0 && reqDto.getReturnPeriodTo() != 0) {
			queryBuilder.append(" AND ( RC.RETURN_PERIOD >= :returnPeriodFrom"
					+ " AND RC.RETURN_PERIOD <= :returnPeriodTo )");
		}else if (reqDto.getReturnPeriodFrom() != 0 && reqDto.getReturnPeriodTo() == 0) {
			queryBuilder.append(" AND RC.RETURN_PERIOD >= :returnPeriodFrom");
		}
		
		if (!isusernamereq) {
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				queryBuilder.append(" AND RC.CREATED_BY IN (:initiationByUserId) ");
			} 
		}
		
		if(isusernamereq){
			queryBuilder.append(initiatedby);
		}
		
		if (reqDto.getReconStatus() != null
			&& (!reqDto.getReconStatus().isEmpty())) {
			queryBuilder.append(" AND RC.STATUS =:reconStatus ");
		}
		
		return queryBuilder.toString();
	}
	
	
	


}
