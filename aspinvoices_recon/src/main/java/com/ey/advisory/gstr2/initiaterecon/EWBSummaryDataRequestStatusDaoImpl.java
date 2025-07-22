/**
 * 
 */
package com.ey.advisory.gstr2.initiaterecon;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("EWBSummaryDataRequestStatusDaoImpl")
public class EWBSummaryDataRequestStatusDaoImpl
		implements EWBSummaryDataRequestStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	EntityService entityService;
	
	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;
	
	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;
	
	static final private String Document_Date = "DocumentDate";
	
	@Override
	public List<EWBSummaryDataRequestStatusDto> getRequestDataSummaryStatus(
			List<String> gstinlist, String criteria, String fromDate,
			String toDate) {
		
		Date toDocDate = null;
		Date fromDocDate = null;
		
		Date toEwayDate = null;
		Date fromEwayDate = null;
		
		
		Map<String, String> stateNamesMap = entityService
				.getStateNames(gstinlist);
		
		Map<String, String> authMap = authTokenService
				.getAuthTokenStatusForGstins(gstinlist);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String defaultDate = "9999-12-31";
		
		
		if (criteria !=null && !criteria.isEmpty())
		{
			try{
				
			if(Document_Date.equalsIgnoreCase(criteria))
			{

				toDocDate = toDate != null && toDate != ""
						&& !toDate.isEmpty() ? formatter.parse(toDate)
								: formatter.parse(defaultDate);

				fromDocDate = fromDate != null && fromDate != ""
						&& !fromDate.isEmpty() ? formatter.parse(fromDate)
								: formatter.parse(defaultDate);
				
				toEwayDate = formatter.parse(defaultDate);
				fromEwayDate = formatter.parse(defaultDate);
			}
			
			else{
				
				toEwayDate = toDate != null && toDate != ""
						&& !toDate.isEmpty() ? formatter.parse(toDate)
								: formatter.parse(defaultDate);

				fromEwayDate = fromDate != null && fromDate != ""
						&& !fromDate.isEmpty() ? formatter.parse(fromDate)
								: formatter.parse(defaultDate);
				
				toDocDate = formatter.parse(defaultDate);
				fromDocDate = formatter.parse(defaultDate);
				
				}
			}catch (ParseException e1) {
		e1.printStackTrace();
	
		}	
		}
		
			try {

			StoredProcedureQuery storedProc = null;
			
			LOGGER.info("Invoking USP_EWB_GET_DATA Stored Proc");
				
			storedProc = entityManager
					.createStoredProcedureQuery("USP_EWB_GET_DATA");
			 
			
			storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
					String.class, ParameterMode.IN);

			storedProc.setParameter("P_GSTIN_LIST",
					String.join(",", gstinlist));
			
			storedProc.registerStoredProcedureParameter("P_DOC_FROM_DATE",
					Date.class, ParameterMode.IN);
			
			storedProc.setParameter("P_DOC_FROM_DATE", fromDocDate);

			storedProc.registerStoredProcedureParameter("P_DOC_TO_DATE",
					Date.class, ParameterMode.IN);

			storedProc.setParameter("P_DOC_TO_DATE", toDocDate);

			storedProc.registerStoredProcedureParameter("P_EWB_FROM_DATE",
					Date.class, ParameterMode.IN);

			storedProc.setParameter("P_EWB_FROM_DATE", fromEwayDate);

			storedProc.registerStoredProcedureParameter("P_EWB_TO_DATE", Date.class,
					ParameterMode.IN);

			storedProc.setParameter("P_EWB_TO_DATE", toEwayDate );
			
			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();

			LOGGER.debug("Coverting object to the DTOList, BEGIN");
			
			
			List<EWBSummaryDataRequestStatusDto> retList = list.stream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			
			List<EWBSummaryDataRequestStatusDto> finalRetList = new ArrayList<>();
			retList.forEach(e->{
				
				EWBSummaryDataRequestStatusDto dto = new EWBSummaryDataRequestStatusDto();
				dto.setGstin(e.getGstin());
				dto.setEwbStatus(e.getEwbStatus());
				dto.setCountOfRecords(e.getCountOfRecords());
				dto.setTaxableValue(e.getTaxableValue());
				dto.setTotalTax(e.getTotalTax());
				dto.setInvoiceValue(e.getInvoiceValue());
				dto.setCreatedOn(e.getCreatedOn());
				dto.setStateName(stateNamesMap.get(e.getGstin()));
				dto.setAuth(authMap.get(e.getGstin()));
				finalRetList.add(dto);
				
			});
			
			
			LOGGER.debug("Coverting object to the DTOList, END");
			
			return finalRetList;

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Converting ResultSet into List in EWBSummaryDataRequestStatusDto ");
			throw new AppException("Unexpected error in stored procedure "
					+ "execution.", e);
		}
		}
	private EWBSummaryDataRequestStatusDto convert(Object[] arr) {
		EWBSummaryDataRequestStatusDto ewbEntity = new EWBSummaryDataRequestStatusDto();
		
		ewbEntity.setGstin((arr[0] != null) ? (String) arr[0] : null);
		ewbEntity.setEwbStatus((arr[1] != null) ? (String)arr[1]: null);
		ewbEntity.setCountOfRecords((arr[2] != null) ? GenUtil.getBigInteger(arr[2]) :BigInteger.ZERO);
		ewbEntity.setTaxableValue((arr[3] != null) ? (BigDecimal)arr[3] :BigDecimal.ZERO);
		ewbEntity.setTotalTax((arr[4] != null) ? (BigDecimal) arr[4] : BigDecimal.ZERO);
		ewbEntity.setInvoiceValue((arr[5] != null) ? (BigDecimal)arr[5] : BigDecimal.ZERO);
		ewbEntity.setCreatedOn(null);
		return ewbEntity;
	}


	}

