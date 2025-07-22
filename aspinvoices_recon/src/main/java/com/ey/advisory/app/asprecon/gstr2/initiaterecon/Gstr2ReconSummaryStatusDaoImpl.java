/**
 * 
 */
package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2ReconSummaryStatusDaoImpl")
public class Gstr2ReconSummaryStatusDaoImpl
		implements Gstr2ReconSummaryStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr2ReconSummaryStatusDto> findReconSummStatus(Long entityId,
			String toTaxPeriod2A, String fromTaxPeriod2A, String toTaxPeriodPR,
			String fromTaxPeriodPR, String fromDocDate, String toDocDate, 
			String reconType) {

		String queryString = createQueryString(toTaxPeriod2A, fromTaxPeriod2A,
				toTaxPeriodPR, fromTaxPeriodPR, fromDocDate, toDocDate,
				reconType);

		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Query created for Gstr2ReconSummaryStatus "
							+ "on FilterConditions Provided By User : %s",
					queryString);
			LOGGER.debug(str);
		}

		Query q = entityManager.createNativeQuery(queryString);
		q.setParameter("entityId", entityId);

		if (reconType != null && !reconType.equals("")
				&& (!reconType.equalsIgnoreCase("AP_M_2APR"))) {
		
		if (toTaxPeriod2A != null
				&& (!toTaxPeriod2A.equals("") && !toTaxPeriod2A.isEmpty()))
			q.setParameter("toTaxPeriod2A", toTaxPeriod2A);

		if (fromTaxPeriod2A != null
				&& (!fromTaxPeriod2A.equals("") && !fromTaxPeriod2A.isEmpty()))
			q.setParameter("fromTaxPeriod2A", fromTaxPeriod2A);

		if (toTaxPeriodPR != null
				&& (!toTaxPeriodPR.equals("") && !toTaxPeriodPR.isEmpty()))
			q.setParameter("toTaxPeriodPR", toTaxPeriodPR);

		if (fromTaxPeriodPR != null
				&& (!fromTaxPeriodPR.equals("") && !fromTaxPeriodPR.isEmpty()))
			q.setParameter("fromTaxPeriodPR", fromTaxPeriodPR);

		if (fromDocDate != null
				&& (!fromDocDate.equals("") && !fromDocDate.isEmpty()))
			q.setParameter("fromDocDate", fromDocDate);

		if (toDocDate != null
				&& (!toDocDate.equals("") && !toDocDate.isEmpty()))
			q.setParameter("toDocDate", toDocDate);
		
		}
		
		if (reconType != null)
			q.setParameter("reconType", reconType);

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
		String ldt = dt != null ? EYDateUtil
				.toISTDateTimeFromUTC(dt.toLocalDateTime()).toString() : null;
		obj.setStatusdate(ldt);
		obj.setGstinIdentifier(arr[1] != null ? (String) arr[1] : null);
		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Gstr2ReconSummaryStatusDto object " + "is %s", obj);
			LOGGER.debug(str);
		}
		return obj;
	}

	public String createQueryString(String toTaxPeriod2A,
			String fromTaxPeriod2A, String toTaxPeriodPR,
			String fromTaxPeriodPR, String fromDocDate, String toDocDate,
			String reconType) {

		StringBuffer condition = new StringBuffer();

		if (reconType != null && !reconType.equals("")
				&& (reconType.equalsIgnoreCase("AP_M_2APR"))) {
			condition.append("AND RC.RECON_TYPE =:reconType");

		} else if (toTaxPeriod2A != null && !toTaxPeriod2A.equals("")
				&& (fromTaxPeriod2A != null && !fromTaxPeriod2A.equals(""))
				&& toTaxPeriodPR != null && !toTaxPeriodPR.equals("")
				&& (fromTaxPeriodPR != null && !fromTaxPeriodPR.equals(""))) {
			condition.append(" AND RC.A2_TO_RET_PERIOD =:toTaxPeriod2A  "
					+ " AND RC.A2_FROM_RET_PERIOD =:fromTaxPeriod2A "
					+ " AND RC.TO_RET_PERIOD =:toTaxPeriodPR  "
					+ " AND RC.FROM_RET_PERIOD =:fromTaxPeriodPR"
					+ " AND RC.RECON_TYPE =:reconType");
			
		} else if (toDocDate != null && !toDocDate.equals("")
				&& (fromDocDate != null && !fromDocDate.equals(""))) {
			condition.append(" AND RC.TO_DOC_DATE =:toDocDate  "
					+ " AND RC.FROM_DOC_DATE =:fromDocDate "
					+ " AND RC.RECON_TYPE =:reconType");
		}


		return " SELECT state_name, reg_type ,gstin,status, "
				+ " status_date AS STATUS_DATE"
				+ " FROM (SELECT state_name, reg_type , gstin, "
				+ " status, status_date, "
				+ " Row_number() OVER( "
				+ " partition BY state_name, reg_type , gstin "
				+ " ORDER BY status_date DESC) RNK1"
				+ " FROM (SELECT MS.state_name, reg_type , "
				+ " GI.gstin,"
				+ "                       CASE "
				+ "                      WHEN RC.status = 'RECON_COMPLETED' "
				+ "           OR RC.status = 'REPORT_GENERATED' THEN 'SUCCESS' "
				+ "      WHEN RC.status = 'RECON_INITIATED' THEN 'IN PROGRESS' "
				+ "                      WHEN RC.status = 'NO_DATA_FOUND' "
				+ "             OR RC.status = 'REPORT_GENERATION_FAILED' THEN "
				+ "                         'FAILED' "
				+ "                         ELSE 'NOT INITIATED' "
				+ "                       END " + "                       AS "
				+ "                               STATUS, "
				+ "                       RC.completed_on "
				+ "                       AS "
				+ "                               STATUS_DATE, "
				+ "                       RC.to_ret_period, "
				+ "                       RC.from_ret_period, "
				+ "                       Row_number() "
				+ "                         OVER( "
				+ "                   partition BY GI.gstin, RC.to_ret_period, "
				+ "                         RC.from_ret_period "
				+ "                   ORDER BY RC.recon_report_config_id DESC, "
				+ "                         RC.completed_on "
				+ "                         DESC) AS "
				+ "                               RNK "
				+ "                FROM  gstin_info GI "
				+ "                      INNER JOIN entity_info EI "
				+ "                               ON EI.id = GI.entity_id "
				+ "                     INNER JOIN master_state MS "
				+ "                           ON MS.state_code = GI.state_code "
				+ "                       LEFT OUTER JOIN "
				+ "              tbl_recon_report_gstin_details GD "
				+ "                                    ON GD.gstin = GI.gstin "
				+ "     LEFT OUTER JOIN tbl_recon_report_config RC "
				+ "                                    ON "
				+ "      GD.recon_report_config_id = RC.recon_report_config_id "
				+ condition.toString()
				+ "                WHERE  EI.id =:entityId "
				+ "                       AND GI.is_delete = false "
				+ "                       AND EI.is_delete = false)A "
				+ "        WHERE  rnk = 1)B " + "WHERE  rnk1 = 1 "
				+ "ORDER  BY gstin;";
	}

}
