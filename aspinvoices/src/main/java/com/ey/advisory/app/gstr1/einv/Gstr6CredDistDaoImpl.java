package com.ey.advisory.app.gstr1.einv;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr6ComputeCredDistDataDto;
import com.ey.advisory.app.data.entities.client.Gstr6ComputeCredDistDataEntity;
import com.ey.advisory.app.data.repositories.client.EinvReconRespConfigRepository;
import com.ey.advisory.app.data.repositories.client.EinvReconRespGSTINRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1vsEinvReconRespProcessedRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6ComputeCredDistDataRepository;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kiran s
 *
 */

@Slf4j
@Component("Gstr6CredDistDaoImpl")
public class Gstr6CredDistDaoImpl
		implements Gstr6CredDistDao {

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
	
	@Autowired
	Gstr6ComputeCredDistDataRepository gstr6ComputeCredDistDataRepository;

	
	@Override
	public List<Gstr6ComputeCredDistDataDto> getGstr6CredReqIdWiseStatus(
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
			} else if (reconStatus.equalsIgnoreCase("REPORT GENERATION INPROGRESS")) {
				reconStatus = "REPORT_GENERATION_INPROGRESS";
			} else if (reconStatus.equalsIgnoreCase("COMPUTE INITIATED")) {
				reconStatus = "COMPUTE_INITIATED";
			}
		}
		
		boolean isusernamereq = commonUtility
				 .getAnsForQueMultipleUserAccessToAsyncReports(entityId);
		
		String initiatedby="";
		if(isusernamereq){
			initiatedby=" AND INITIATED_BY = :userName";
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
		
		
		String condtion = createQueryCondition(reqDto, isusernamereq, initiatedby);
		String queryString = createQueryString(userName, condtion);

		Query q = entityManager.createNativeQuery(queryString);
		
		q.setParameter("entityId", entityId);
		
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
		
		
		

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		List<Gstr6ComputeCredDistDataDto> retList = list
				.stream().map(o -> converGstr6CredData(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return retList;
	}
	
	private String createQueryString(String userName, String condtion) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Creating query for Request Status";
			LOGGER.debug(msg);
		}
		
		String query = "SELECT REQUEST_ID, NO_OF_GSTINS, TAX_PERIOD, INITIATED_ON,"
		        + " INITIATED_BY, COMPLETED_ON, FILE_PATH, STATUS, ENTITY_ID,"
		        + " GSTINS, DOC_ID FROM GSTR6_COMPUTE_CREDIT_DIST_DATA"
		        + " WHERE ENTITY_ID = :entityId "+condtion;

		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Query created for Request Status : %s",
					query);
			LOGGER.debug(str);
		}

		return query;
	}
	
	private String createQueryCondition(Gstr2InitiateReconReqDto reqDto, boolean isusernamereq, 
			String initiatedby) {
		if (LOGGER.isDebugEnabled()) {
		String msg = " Begin Gstr6CredDistDaoImpl.createQueryCondition() ";
		LOGGER.debug(msg);
		}
		
		StringBuilder queryBuilder = new StringBuilder();
		
		// FROM_TAX_PERIOD, TO_TAX_PERIOD  -> VARCHAR
		
		if (reqDto.getReturnPeriodFrom() != 0 && reqDto.getReturnPeriodTo() != 0) {
			queryBuilder.append("AND ( DERIVED_TAX_PERIOD >= :returnPeriodFrom"
					+ " AND DERIVED_TAX_PERIOD <= :returnPeriodTo )");
		}else if (reqDto.getReturnPeriodFrom() != 0 && reqDto.getReturnPeriodTo() == 0) {
			queryBuilder.append("AND DERIVED_TAX_PERIOD >= :returnPeriodFrom");
		}
		
		if (!isusernamereq) {
			if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
				queryBuilder.append(" AND INITIATED_BY IN (:initiationByUserId) ");
			} 
		}
		
		if(isusernamereq){
			queryBuilder.append(initiatedby);
		}
		
		if (reqDto.getReconStatus() != null
			&& (!reqDto.getReconStatus().isEmpty())) {
			queryBuilder.append(" AND STATUS =:reconStatus ");
		}
		
		return queryBuilder.toString();
	}
	
	
	private Gstr6ComputeCredDistDataDto converGstr6CredData(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " converGstr6CredData object";
			LOGGER.debug(str);
		}

		Gstr6ComputeCredDistDataDto dto = new Gstr6ComputeCredDistDataDto();

		BigInteger b = GenUtil.getBigInteger(arr[0]);
		Long requestId = b.longValue();
		dto.setRequestId(requestId);
		
		BigInteger b1 = GenUtil.getBigInteger(arr[1]);
		Long noOfGstins = b1.longValue();
		dto.setNoOfGstin(noOfGstins);
		
		
		

		if (arr[2] != null) {
			String taxPeriodFrmDb = ((String) arr[2]).toString();
			//String ConvertedTxpd = getTaxperiod(taxPeriodFrmDb);
			
			dto.setTaxPeriod(taxPeriodFrmDb);
		} 
		Timestamp date = (Timestamp) arr[3];
			if(date!=null){
		LocalDateTime dt = date.toLocalDateTime();
		dto.setInitiatedOn(
				getFormattedTime(EYDateUtil.toISTDateTimeFromUTC(dt)));
		}
		dto.setInitiatedBy((String) arr[4]);
		
		Timestamp compdate = (Timestamp) arr[5];
		if(compdate!=null)
		{
			String cmpldt = compdate != null
					? getFormattedTime(
							EYDateUtil.toISTDateTimeFromUTC(compdate.toLocalDateTime()))
					: null;
		dto.setCompletedOn(cmpldt);
		}
		dto.setStatus((String) arr[7]);
		BigInteger bi = GenUtil.getBigInteger(arr[8]);
		Long entityId = bi.longValue();
		dto.setEntityId(entityId);
		
		String docId = (String) arr[10];
		dto.setDocId(docId);
	
		String gstinString = (String) arr[9];
		List<Map<String, String>> gstins = new ArrayList<>();
		for (String gstin : gstinString.split(",")) {
		    Map<String, String> gstinMap = new HashMap<>();
		    gstinMap.put("gstin", gstin.trim()); // or just gstin if you are sure there are no leading/trailing spaces
		    gstins.add(gstinMap);
		}
		dto.setGstinsList(gstins);
		return dto;
	}
	public String getTaxperiod(String yyyymm) {
	    String month = yyyymm.substring(4);
	    String year = yyyymm.substring(0, 4);
	    return month + year;
	}
	
	private String getFormattedTime(LocalDateTime dateStr) {
		if (dateStr == null) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		return formatter.format(dateStr);
	}
	
	@Override
	public List<Gstr1EinvRequesIdWiseDownloadTabDto> getGstr6CredDistData(
			Long requestId) {
//List<Gstr1EinvInitiateReconService> list= new ArrayList<>();
		
		Optional<Gstr6ComputeCredDistDataEntity> findById = gstr6ComputeCredDistDataRepository.findById(requestId);
		Gstr1EinvRequesIdWiseDownloadTabDto gstr1ReqIdWiseDownloDto= new Gstr1EinvRequesIdWiseDownloadTabDto();
		gstr1ReqIdWiseDownloDto.setFlag(false);
		gstr1ReqIdWiseDownloDto.setDocId(findById.get().getDocId());
		gstr1ReqIdWiseDownloDto.setReportName("Compute Credit Distribution");
		if(gstr1ReqIdWiseDownloDto.getDocId()!=null)
		gstr1ReqIdWiseDownloDto.setFlag(true);
		
		 List<Gstr1EinvRequesIdWiseDownloadTabDto> responseList = new ArrayList<>();
		    responseList.add(gstr1ReqIdWiseDownloDto);
		    return responseList;
	}
	
}
