package com.ey.advisory.app.gstr1.einv;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr2a2bVs3BbReqIdWiseDataDto;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kiran s
 *
 */

@Slf4j
@Component("Gstr2Avs3BReqIdWiseDaoImpl")
public class Gstr2Avs3BReqIdWiseDaoImpl
		implements Gstr2Avs3BReqIdWiseDao {

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
	public List<Gstr2a2bVs3BbReqIdWiseDataDto> get2a2bvs3aReqIdWiseStatus(
			Gstr2InitiateReconReqDto reqDto, String userName) {
		int returnPeriodFrom = 0;
		int returnPeriodTo = 0;
		Long entityId = reqDto.getEntityId();
		String taxPeriodFrom = reqDto.getTaxPeriodFrom();
		String taxPeriodTo = reqDto.getTaxPeriodTo();
		List<String> initiationByUserId = reqDto.getInitiationByUserId();
		String reconStatus = reqDto.getReconStatus();
		// 2 more extra filter
		List<Long> requestId = reqDto.getRequestId();
		List<String> initiationByUserEmailId = reqDto
				.getInitiationByUserEmailId();

		if (reconStatus != null && !reconStatus.isEmpty()) {
			if (reconStatus.equalsIgnoreCase("REPORT GENERATION FAILED")) {
				reconStatus = "REPORT_GENERATION_FAILED";
			} else if (reconStatus.equalsIgnoreCase("REPORT GENERATED")) {
				reconStatus = "REPORT_GENERATED";
			} else if (reconStatus
					.equalsIgnoreCase("REPORT GENERATION INPROGRESS")) {
				reconStatus = "REPORT_GENERATION_INPROGRESS";
			} else if (reconStatus.equalsIgnoreCase("RECON INITIATED")) {
				reconStatus = "RECON_INITIATED";
			}
		}

		if (!Strings.isNullOrEmpty(taxPeriodFrom)) {
			returnPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
			reqDto.setReturnPeriodFrom(returnPeriodFrom);
		}

		if (!Strings.isNullOrEmpty(taxPeriodTo)) {
			returnPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
			reqDto.setReturnPeriodTo(returnPeriodTo);
		}

		String condtion = createQueryCondition(reqDto);

		String queryString = createQueryString(userName, condtion);

		Query q = entityManager.createNativeQuery(queryString);

		if (reqDto.getEntityId() != null) {
			q.setParameter("entityId", entityId);
		}
	/*	if (reqDto.getTaxPeriodFrom() != null
				&& !reqDto.getTaxPeriodFrom().isEmpty()) {
			String convertdFromTaxPeriod = convertFromTaxPeriod(reqDto.getTaxPeriodFrom());
			
			q.setParameter("taxPeriodFrom", convertdFromTaxPeriod);
		}*/
		if (reqDto.getTaxPeriodFrom() != null && !reqDto.getTaxPeriodFrom().isEmpty()) {
			String convertdFromTaxPeriod = convertFromTaxPeriod(reqDto.getTaxPeriodFrom());
		  //  Date taxPeriodFromDate = convertToDate(convertdFromTaxPeriod);
		    q.setParameter("taxPeriodFrom", convertdFromTaxPeriod);
		}

		
		if (reqDto.getTaxPeriodTo() != null
				&& !reqDto.getTaxPeriodTo().isEmpty()) {
			String convertdToTaxPerd = convertToTaxPeriod(reqDto.getTaxPeriodTo());
			//Date convertdToTaxPeriod = convertToDate(convertdToTaxPerd);
			q.setParameter("taxPeriodTo", convertdToTaxPerd);
		}

		if (!CollectionUtils.isEmpty(reqDto.getRequestId())) {
			q.setParameter("requestId", requestId);
		}
		if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
			q.setParameter("initiationByUserId", initiationByUserId);
		}
		if (reqDto.getReconStatus() != null
				&& !reqDto.getReconStatus().isEmpty()) {
			q.setParameter("reconStatus", reconStatus);
		}
		if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserEmailId())) {
			q.setParameter("initiationByUserEmail", initiationByUserEmailId);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("executing query to get the data {} %s", reqDto);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		List<Gstr2a2bVs3BbReqIdWiseDataDto> retList = list
				.stream().map(o -> converGstr2A2Bvs3bData(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return retList;
	}

	private String createQueryString(String userName, String condtion) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Creating query for Request Status";
			LOGGER.debug(msg);
		}

		String query = "SELECT REQUEST_ID, NO_OF_GSTINS, FROM_TAX_PERIOD, INITIATED_ON,"
				+ " INITIATED_BY, COMPLETED_ON, FILE_PATH, STATUS, ENTITY_ID,"
				+ " GSTINS, DOC_ID,TO_TAX_PERIOD,INITIATED_BY_EMAIL_ID FROM TBL_RECON_GSTR2A2B_VS_3B"
				+ " WHERE ENTITY_ID = :entityId " + condtion;

		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Query created for Request Status : %s",
					query);
			LOGGER.debug(str);
		}

		return query;
	}

	private String createQueryCondition(Gstr2InitiateReconReqDto reqDto) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin Gstr2Avs3BReqIdWiseDaoImpl.createQueryCondition() ";
			LOGGER.debug(msg);
		}

		StringBuilder condition1 = new StringBuilder();

		if (reqDto.getTaxPeriodFrom() != null && !reqDto.getTaxPeriodFrom().isEmpty() &&
		        reqDto.getTaxPeriodTo() != null && !reqDto.getTaxPeriodTo().isEmpty()) {
			condition1.append(" AND TO_CHAR(INITIATED_ON, 'yyyy-MM-dd') >= :taxPeriodFrom ")
            .append(" AND TO_CHAR(INITIATED_ON, 'yyyy-MM-dd') < :taxPeriodTo ");
		    }


		if (!CollectionUtils.isEmpty(reqDto.getRequestId())) {
			condition1
					.append(" AND REQUEST_ID IN (:requestId) ");
		}

		if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserId())) {
			condition1.append(" AND INITIATED_BY IN (:initiationByUserId) ");
		}

		if (!CollectionUtils.isEmpty(reqDto.getInitiationByUserEmailId())) {
			condition1.append(
					" AND INITIATED_BY_EMAIL_ID IN (:initiationByUserEmail) ");
		}

		if (reqDto.getReconStatus() != null
				&& (!reqDto.getReconStatus().isEmpty())) {
			condition1.append(" AND STATUS =:reconStatus ");
		}

		return condition1.toString();
	}

	private Gstr2a2bVs3BbReqIdWiseDataDto converGstr2A2Bvs3bData(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " converGstr6CredData object";
			LOGGER.debug(str);
		}

		Gstr2a2bVs3BbReqIdWiseDataDto dto = new Gstr2a2bVs3BbReqIdWiseDataDto();

		BigInteger b = GenUtil.getBigInteger(arr[0]);
		Long requestId = b.longValue();
		dto.setRequestId(requestId);

		BigInteger b1 = GenUtil.getBigInteger(arr[1]);
		Long noOfGstins = b1.longValue();
		dto.setGstinCount(noOfGstins);

		if (arr[2] != null) {
			String taxPeriodFrmDb = ((String) arr[2]).toString();
			String fromTaxPeriod = convertToReadableDate(taxPeriodFrmDb);

			dto.setFromTaxPeriod(fromTaxPeriod);
		}
		Timestamp date = (Timestamp) arr[3];
		if (date != null) {
			LocalDateTime dt = date.toLocalDateTime();
			dto.setInitiatedOn(
					getFormattedTime(EYDateUtil.toISTDateTimeFromUTC(dt)));
		}
	

		Timestamp compdate = (Timestamp) arr[5];
		if (compdate != null) {
			String cmpldt = compdate != null
					? getFormattedTime(
							EYDateUtil.toISTDateTimeFromUTC(
									compdate.toLocalDateTime()))
					: null;
			dto.setCompletionOn(cmpldt);
		}
		dto.setStatus((String) arr[7]);
		BigInteger bi = GenUtil.getBigInteger(arr[8]);
		Long entityId = bi.longValue();
		dto.setEntityId(entityId);

	/*	String docId = (String) arr[10];
		dto.setDocId(docId);*/

		String gstinString = (String) arr[9];
		List<Map<String, String>> gstins = new ArrayList<>();
		for (String gstin : gstinString.split(",")) {
			Map<String, String> gstinMap = new HashMap<>();
			gstinMap.put("gstin", gstin.trim()); // or just gstin if you are
													// sure there are no
													// leading/trailing spaces
			gstins.add(gstinMap);
		}
		dto.setGstins(gstins);

		if (arr[11] != null) {
			String taxPeriodTo = ((String) arr[11]).toString();
			String toTaxPeriod = convertToReadableDate(taxPeriodTo);

			dto.setToTaxPeriod(toTaxPeriod);
		}
		if (arr[4] != null && arr[12] != null) {
		    dto.setInitiatedBy((String) arr[4] + "_" + (String) arr[12]);
		}
		else
		{
			if (arr[4] != null) {
				dto.setInitiatedBy((String) arr[4]);
				}
		}

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


	 public static String convertToReadableDate(String date) {
	        YearMonth yearMonth = YearMonth.parse(date, DateTimeFormatter.ofPattern("MMyyyy"));
	        
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);
	        return yearMonth.format(formatter);
	    }
	 public String convertFromTaxPeriod(String fromTaxperiod) {
	        String initiationDateFrom = fromTaxperiod; // Example input
	        
	        // Step 1: Extract month and year from the input string
	        String month = initiationDateFrom.substring(0, 2);
	        String year = initiationDateFrom.substring(2, 6);
	        
	        // Step 2: Construct the date string with day = 01
	        String dateStr = year + "-" + month + "-01";
	        
	        // Step 3: Convert to LocalDate and format it as needed
	        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        
	        // Step 4: Format the date to the desired output
	        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        
	        System.out.println(formattedDate); // Output will be "2024-08-01"
			return formattedDate;
	    }
	 
	 public String convertToTaxPeriod(String ToTaxperiod) {
		 // Step 1: Extract month and year from the input string
		    String month = ToTaxperiod.substring(0, 2);  // Extract first 2 characters as month
		    String year = ToTaxperiod.substring(2, 6);   // Extract next 4 characters as year

		    // Step 2: Convert the extracted month and year to a LocalDate object representing the last day of the month
		    LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1)
		                              .withDayOfMonth(LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1).lengthOfMonth());

		    // Step 3: Format the LocalDate object to "yyyy-MM-dd"
		    String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		    
		    System.out.println("Converted ToTaxperiod to date: " + formattedDate); // Debug print statement

		    return formattedDate;
		 /*
	        String initiationDateto = ToTaxperiod; // Example input
	        
	        // Step 1: Extract month and year from the input string
	        String month = initiationDateto.substring(0, 2);
	        String year = initiationDateto.substring(2, 6);
	        
	        // Step 2: Construct the date string with day = 01
	        String dateStr = year + "-" + month + "-31";
	        
	        // Step 3: Convert to LocalDate and format it as needed
	        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        
	        // Step 4: Format the date to the desired output
	        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        
	        System.out.println(formattedDate); // Output will be "2024-08-01"
			return formattedDate;
	    */}
	 
	 public Date convertToDate(String dateStr) {
		    // Update the pattern to match 'yyyy-MM-dd'
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		    LocalDate localDate = LocalDate.parse(dateStr, formatter);
		    return Date.from(localDate.atStartOfDay(ZoneId.of("UTC")).toInstant());
		}
}
