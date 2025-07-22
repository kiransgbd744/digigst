package com.ey.advisory.app.services.jobs.erp.processedrecords;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ProcessedRecordsDaoImpl")
public class ProcessedRecordsDaoImpl implements ProcessedRecordsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	
	@Override
	public List<ProcessedRecDetForGstinTaxPeriodDto> findProcessedRec(
			RevIntegrationScenarioTriggerDto req, List<String> taxPeriods) {
		String gstin = req.getGstin();
		
		if ( gstin == null || gstin.isEmpty()) {
	        String msg = "GSTIN and atleast one TaxPeriod is required";
	     	LOGGER.debug(msg);
			throw new AppException(msg);
		}
		String queryString = "SELECT * FROM CV_PROCESSED_RECS_001 "
				+ "WHERE GSTIN = :gstin";
		
		Query q = entityManager.createNativeQuery(queryString);
		q.setParameter("gstin", gstin);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		
		if (LOGGER.isDebugEnabled()) {
			String str = String.format("Executed Query for Process Records and"
					+ " and Fetched %d rows",list.size());
			LOGGER.debug(str);
		}
		List<ProcessedRecDetForGstinTaxPeriodDto> retList = 
				list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		
		
		return retList;
	}
	
	
	private ProcessedRecDetForGstinTaxPeriodDto convert(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " ProcessedRecDetForGstinTaxPeriodDto object";
			LOGGER.debug(str);
		}
		
		
		ProcessedRecDetForGstinTaxPeriodDto obj = new 
				ProcessedRecDetForGstinTaxPeriodDto();
		
		    obj.setGstinNum((String) arr[0]);
		    obj.setEntity((String) arr[1]);
		    obj.setEntityName((String) arr[3]);
		    obj.setState((String) arr[5]);
		    obj.setReturnPeriod((String) arr[6]);
		    obj.setRecordType((String) arr[7]);
		    obj.setDivision((String) arr[8]);
		    obj.setProfitCenter((String) arr[9]);
		    obj.setLocation((String) arr[10]);
		    obj.setPlantCode((String) arr[11]);
		    obj.setSalesOrg((String) arr[12]);
		    obj.setPurchaseOrg((String) arr[13]);
		    obj.setDistrChannel((String) arr[14]);
		    obj.setUserAccess1((String) arr[15]);
		    obj.setUserAccess2((String) arr[16]);
		    obj.setUserAccess3((String) arr[17]);
		    obj.setUserAccess4((String) arr[18]);
		    obj.setUserAccess5((String) arr[19]);
		    obj.setUserAccess6((String) arr[20]);
		    LocalDateTime date = LocalDateTime.now();
		    DateTimeFormatter formatter =  DateTimeFormatter.
		    		ofPattern("dd-MM-yyyy hh:mm:ss");
		    obj.setCreatedOn( date.format(formatter));
		    obj.setDocCount((Integer) arr[21]);
		    BigDecimal taxableValue = ((BigDecimal) arr[22])
		    		.subtract((BigDecimal) arr[23]);
		    obj.setTaxableValue(taxableValue);
		    BigDecimal igst = ((BigDecimal) arr[24])
		    		.subtract((BigDecimal) arr[25]);
		    obj.setIgstAmt(igst);
		    BigDecimal cgst = ((BigDecimal) arr[26])
		    		.subtract((BigDecimal) arr[27]);
		    obj.setCgstAmt(cgst);
		    BigDecimal sgst = ((BigDecimal) arr[28])
		    		.subtract((BigDecimal) arr[29]);
		    obj.setSgstAmt(sgst);
		    BigDecimal cess = ((BigDecimal) arr[30])
    		.subtract((BigDecimal) arr[31]);
		    obj.setCessAmt(cess);
		    obj.setSaveStatus((String) arr[32]);
		    obj.setStatusDate(null);
		    obj.setCompCode((String) arr[33]);
			return obj;
	}

}
