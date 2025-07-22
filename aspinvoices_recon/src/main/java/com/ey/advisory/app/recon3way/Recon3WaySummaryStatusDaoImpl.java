/**
 * 
 */
package com.ey.advisory.app.recon3way;

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
 * @author Sakshi.jain
 *
 */

@Slf4j
@Component("Recon3WaySummaryStatusDaoImpl")
public class Recon3WaySummaryStatusDaoImpl
		implements Recon3WaySummaryStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	static final String Return_Period_Wise = "ReturnPeriodWise";

	@Override
	public List<Gstr2ReconSummaryStatusDto> find3WayReconSummStatus(
			Long entityId, String fromReturnPeriod, String toReturnPeriod,
			String criteria) {

		String queryString = createQueryString(fromReturnPeriod, toReturnPeriod,
				criteria);

		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Query created for Recon3WaySummaryStatus "
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
/*
		if (criteria != null)
			q.setParameter("reconType", criteria);
*/
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
					.format("Recon3WaySummaryStatusDto object " + "is %s", obj);
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
			String toReturnPeriod, String criteria) {

		StringBuffer condition = new StringBuffer();
		/*
		 * if (reconType != null && !reconType.equals("") &&
		 * (reconType.equalsIgnoreCase("AP_M_2APR"))) {
		 * condition.append("AND RC.RECON_TYPE =:reconType");
		 * 
		 * } else
		 */
		if (fromReturnPeriod != null && !fromReturnPeriod.equals("")
				&& (toReturnPeriod != null && !toReturnPeriod.equals(""))) {
			if (criteria != null && !criteria.equals("")
					&& (Return_Period_Wise.equalsIgnoreCase(criteria))) {
				condition.append(" AND RC.FROM_RET_PERIOD =:fromReturnPeriod  "
						+ " AND RC.TO_RET_PERIOD =:toReturnPeriod ");
			} else if (criteria != null && !criteria.equals("")) {
				condition.append(" AND RC.FROM_DOC_DATE =:fromDocPeriod  "
						+ " AND RC.TO_DOC_DATE =:toDocPeriod ");
			}
		}

		/*
		 * if (toTaxPeriod2A != null && !toTaxPeriod2A.equals("") &&
		 * (fromTaxPeriod2A != null && !fromTaxPeriod2A.equals("")) &&
		 * toTaxPeriodPR != null && !toTaxPeriodPR.equals("") &&
		 * (fromTaxPeriodPR != null && !fromTaxPeriodPR.equals(""))) {
		 * condition.append(" AND RC.A2_TO_RET_PERIOD =:toTaxPeriod2A  " +
		 * " AND RC.A2_FROM_RET_PERIOD =:fromTaxPeriod2A " +
		 * " AND RC.TO_RET_PERIOD =:toTaxPeriodPR  " +
		 * " AND RC.FROM_RET_PERIOD =:fromTaxPeriodPR" +
		 * " AND RC.RECON_TYPE =:reconType");
		 * 
		 * } else if (toDocDate != null && !toDocDate.equals("") && (fromDocDate
		 * != null && !fromDocDate.equals(""))) {
		 * condition.append(" AND RC.TO_DOC_DATE =:toDocDate  " +
		 * " AND RC.FROM_DOC_DATE =:fromDocDate " +
		 * " AND RC.RECON_TYPE =:reconType"); }
		 */

		/*
		 * " SELECT state_name, reg_type ,gstin,status, " +
		 * " status_date AS STATUS_DATE" +
		 * " FROM (SELECT state_name, reg_type , gstin, " +
		 * " status, status_date, " + " Row_number() OVER( " +
		 * " partition BY state_name, reg_type , gstin " +
		 * " ORDER BY status_date DESC) RNK1" +
		 * " FROM (SELECT MS.state_name, reg_type , " + " GI.gstin," +
		 * "                       CASE " +
		 * "                      WHEN RC.status = 'RECON_COMPLETED' " +
		 * "           OR RC.status = 'REPORT_GENERATED' THEN 'SUCCESS' " +
		 * "      WHEN RC.status = 'RECON_INITIATED' THEN 'IN PROGRESS' " +
		 * "                      WHEN RC.status = 'NO_DATA_FOUND' " +
		 * "             OR RC.status = 'REPORT_GENERATION_FAILED' THEN " +
		 * "                         'FAILED' " +
		 * "                         ELSE 'NOT INITIATED' " +
		 * "                       END " + "                       AS " +
		 * "                               STATUS, " +
		 * "                       RC.completed_on " +
		 * "                       AS " +
		 * "                               STATUS_DATE, " +
		 * "                       RC.to_ret_period, " +
		 * "                       RC.from_ret_period, " +
		 * "                       Row_number() " +
		 * "                         OVER( " +
		 * "                   partition BY GI.gstin, RC.to_ret_period, " +
		 * "                         RC.from_ret_period " +
		 * "                   ORDER BY RC.recon_report_config_id DESC, " +
		 * "                         RC.completed_on " +
		 * "                         DESC) AS " +
		 * "                               RNK " +
		 * "                FROM  gstin_info GI " +
		 * "                      INNER JOIN entity_info EI " +
		 * "                               ON EI.id = GI.entity_id " +
		 * "                     INNER JOIN master_state MS " +
		 * "                           ON MS.state_code = GI.state_code " +
		 * "                       LEFT OUTER JOIN " +
		 * "              tbl_recon_report_gstin_details GD " +
		 * "                                    ON GD.gstin = GI.gstin " +
		 * "     LEFT OUTER JOIN tbl_recon_report_config RC " +
		 * "                                    ON " +
		 * "      GD.recon_report_config_id = RC.recon_report_config_id " +
		 * condition.toString() + "                WHERE  EI.id =:entityId " +
		 * "                       AND GI.is_delete = false " +
		 * "                       AND EI.is_delete = false)A " +
		 * "        WHERE  rnk = 1)B " + "WHERE  rnk1 = 1 " +
		 * "ORDER  BY gstin;";
		 */

		return " SELECT state_name, reg_type, gstin," + " status, "
				+ " status_date AS STATUS_DATE " + " FROM "
				+ " (SELECT state_name, " + " reg_type, " + " gstin, status, "
				+ " status_date, "
				+ " Row_number() OVER(PARTITION BY state_name, reg_type, gstin "
				+ " ORDER BY status_date DESC) RNK1 " + " FROM "
				+ " (SELECT MS.state_name, " + " reg_type, " + " GI.gstin, "
				+ " CASE " + " WHEN RC.status = 'RECON_COMPLETED' "
				+ " OR RC.status = 'REPORT_GENERATED' THEN 'SUCCESS' "
				+ " WHEN RC.status = 'RECON_INITIATED' THEN 'IN_PROGRESS' "
				+ " WHEN RC.status = 'NO_DATA_FOUND' "
				+ " OR (RC.status = 'REPORT_GENERATION_FAILED' OR RC.status = 'RECON_FAILED') THEN 'FAILED' "
				+ " ELSE 'NOT_INITIATED' " + " END AS STATUS, "
				+ " RC.completed_on AS STATUS_DATE, " + " RC.to_ret_period, "
				+ " RC.from_ret_period, "
				+ " Row_number() OVER(PARTITION BY GI.gstin, RC.to_ret_period, RC.from_ret_period "
				+ " ORDER BY RC.recon_config_id DESC, RC.completed_on DESC) AS RNK "
				+ " FROM gstin_info GI "
				+ " INNER JOIN entity_info EI ON EI.id = GI.entity_id "
				+ " INNER JOIN master_state MS ON MS.state_code = GI.state_code "
				+ " LEFT OUTER JOIN TBL_3WAY_RECON_GSTIN GD ON GD.gstin = GI.gstin "
				+ " LEFT OUTER JOIN TBL_3WAY_RECON_CONFIG RC ON GD.recon_config_id = RC.recon_config_id "
				 + condition.toString()
				 + " WHERE EI.id =:entityId "
				+ " AND GI.is_delete = FALSE " + " AND EI.is_delete = FALSE)A "
				+ " WHERE rnk = 1)B " + " WHERE rnk1 = 1 "
				+ " ORDER BY gstin; ";
	}

}
