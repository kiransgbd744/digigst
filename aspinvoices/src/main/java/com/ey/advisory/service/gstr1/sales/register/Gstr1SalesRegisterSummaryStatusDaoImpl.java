/**
 * 
 */
package com.ey.advisory.service.gstr1.sales.register;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("Gstr1SalesRegisterSummaryStatusDaoImpl")
public class Gstr1SalesRegisterSummaryStatusDaoImpl
		implements Gstr1SalesRegisterSummaryStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	static final String Return_Period_Wise = "ReturnPeriodWise";

	@Override
	public List<Gstr2ReconSummaryStatusDto> findsalesRegisterGstinStatus(
			Long entityId, String fromReturnPeriod, String toReturnPeriod,
			String criteria) {

		String queryString = createQueryString(fromReturnPeriod, toReturnPeriod,
				criteria,entityId);

		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Query created for Gstr1SalesRegisterSummaryStatus "
							+ "on FilterConditions Provided By User : %s",
					queryString);
			LOGGER.debug(str);
		}

		Query q = entityManager.createNativeQuery(queryString);
		q.setParameter("entityId", entityId);

		if (criteria != null && !criteria.equals("")) {
			if (Return_Period_Wise.equalsIgnoreCase(criteria)) {
				if (fromReturnPeriod != null && (!fromReturnPeriod.equals("")
						&& !fromReturnPeriod.isEmpty()))
					q.setParameter("fromReturnPeriod", fromReturnPeriod);

				if (toReturnPeriod != null && (!toReturnPeriod.equals("")
						&& !toReturnPeriod.isEmpty()))
					q.setParameter("toReturnPeriod", toReturnPeriod);
			} else {
				if (fromReturnPeriod != null && (!fromReturnPeriod.equals("")
						&& !fromReturnPeriod.isEmpty()))
					q.setParameter("fromDocPeriod", fromReturnPeriod);

				if (toReturnPeriod != null && (!toReturnPeriod.equals("")
						&& !toReturnPeriod.isEmpty()))
					q.setParameter("toDocPeriod", toReturnPeriod);
			}
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		if (LOGGER.isDebugEnabled()) {
			String msg = "Number of Records got Gstins and its Status is :"
					+ list.size();
			LOGGER.debug(msg);
		}
		List<Gstr2ReconSummaryStatusDto> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Gstr2ReconSummaryStatusDto convert(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Gstr2ReconSummaryStatusDto object";
			LOGGER.debug(str);
		}
		Gstr2ReconSummaryStatusDto obj = new Gstr2ReconSummaryStatusDto();
		obj.setState(arr[0] != null ? (String) arr[0] : null);
		obj.setGstin(arr[2] != null ? (String) arr[2] : null);
		obj.setStatus(arr[3] != null ? (String) arr[3] : null);
		Timestamp dt = (Timestamp) arr[4];
		String ldt = ((dt !=null)?dateChange(dt):null);
		obj.setStatusdate(ldt);
		
		obj.setGstinIdentifier(arr[1] != null ? (String) arr[1] : null);
		if (LOGGER.isDebugEnabled()) {
			String str = String
					.format("Gstr1SalesRegisterSummaryStatusDto object " + "is %s", obj);
			LOGGER.debug(str);
		}
		return obj;
	}
	
	public String dateChange(Timestamp oldDate)
	{
		  DateTimeFormatter formatter = null;
			String dateTime = oldDate.toString();
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
			return newdate;
		
	}
	
	public String createQueryString(String fromReturnPeriod,
			String toReturnPeriod, String criteria,Long entityId) {

		StringBuffer condition1 = new StringBuffer();
		StringBuffer condition = new StringBuffer();
		if(entityId != null){
			condition1.append(" ON E.ID =:entityId ");
		}
		if (fromReturnPeriod != null && !fromReturnPeriod.equals("")
				&& (toReturnPeriod != null && !toReturnPeriod.equals(""))) {
			if (criteria != null && !criteria.equals("")
					&& (Return_Period_Wise.equalsIgnoreCase(criteria))) {
				condition.append(" AND GC.FROM_TAX_PERIOD >=:fromReturnPeriod  "
						+ " AND GC.TO_TAX_PERIOD <=:toReturnPeriod ");
			}
		}

		return "SELECT STATE_NAME,REG_TYPE,GSTIN,STATUS,STATUS_DATE"
				+ " FROM (SELECT MS.STATE_NAME,RECON_REPORT_CONFIG_ID,"
				+ " G.REG_TYPE,G.GSTIN, CASE WHEN GC.STATUS='RECON_COMPLETED'"
				+ " OR GC.status = 'REPORT_GENERATED' THEN 'SUCCESS' WHEN GC.status ="
				+ " 'RECON_INITIATED' THEN 'IN_PROGRESS' WHEN GC.status = 'NO_DATA_FOUND'"
				+ " OR (GC.status = 'REPORT_GENERATION_FAILED' OR GC.status = 'RECON_FAILED')"
				+ " THEN 'FAILED' ELSE 'NOT_INITIATED' END AS STATUS,"
				+ " COMPLETED_ON AS STATUS_DATE, FROM_TAX_PERIOD,TO_TAX_PERIOD,"
				+ " ROW_NUMBER()OVER(PARTITION BY G.GSTIN ORDER BY FROM_TAX_PERIOD ASC,"
				+ " COMPLETED_ON DESC) RN FROM GSTIN_INFO G INNER JOIN ENTITY_INFO E "
				+ condition1
				+ " AND E.ID=G.ENTITY_ID iNNER JOIN master_state MS ON MS.state_code = G.state_code"
				+ " LEFT OUTER JOIN TBL_SRVSDIGIGST_RECON_CONFIG GC ON GC.GSTIN=G.GSTIN"
				 + condition.toString()
				 + " AND E.ID=GC.ENTITY_ID) WHERE RN=1;";
	}

}
