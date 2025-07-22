package com.ey.advisory.app.anx2.reconsummary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository("Anx2ReconSummaryStatusDaoImpl")
public class Anx2ReconSummaryStatusDaoImpl
		implements Anx2ReconSummaryStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Anx2ReconSummaryStatusDto> findReconSummStatus(Long entityId,
			int taxPeriod) {
		// TODO Auto-generated method stub
		String queryString = createQueryString();
		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Query created for Anx2ReconSummaryStatus "
							+ "on FilterConditions Provided By User : %s",
					queryString);
			LOGGER.debug(str);
		}

		Query q = entityManager.createNativeQuery(queryString);
		q.setParameter("entityId", entityId);
		q.setParameter("taxPeriod", taxPeriod);
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		if (LOGGER.isDebugEnabled()) {
			String msg = "Number of Records got Gstins and its Status is :"
					+ list.size();
			LOGGER.debug(msg);
		}
		List<Anx2ReconSummaryStatusDto> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Anx2ReconSummaryStatusDto convert(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Anx2ReconSummaryStatusDto object";
			LOGGER.debug(str);
		}
		Anx2ReconSummaryStatusDto obj = new Anx2ReconSummaryStatusDto();
		obj.setState(arr[0] != null ? (String) arr[0] : null);
		obj.setGstin(arr[1] != null ? (String) arr[1] : null);
		obj.setStatus(arr[2] != null ? (String) arr[2] : null);
		String dateStr =  arr[3] != null ? (String) arr[3] : " ";
		obj.setStatusdate(dateStr);
		if (LOGGER.isDebugEnabled()) {
			String str = String
					.format("Anx2ReconSummaryStatusDto object " + "is %s", obj);
			LOGGER.debug(str);
		}
		return obj;
	}

	public String createQueryString() {
		return  "SELECT STATE_NAME,GSTIN,STATUS,TO_VARCHAR(STATUS_DATE) AS STATUS_DATE FROM "
				+ "(SELECT   STATE_NAME,GSTIN,STATUS,STATUS_DATE,ROW_NUMBER() "
				+ "OVER(PARTITION BY STATE_NAME,GSTIN ORDER BY STATUS_DATE DESC) RNK1 "
				+ "FROM ( "
				+ " SELECT    MS.STATE_NAME "
				+ ", GI.GSTIN "
				+ ", CASE WHEN RC.STATUS='RECON_COMPLETED' OR RC.STATUS='REPORT_GENERATED' THEN 'SUCCESS' "
				+ "WHEN RC.STATUS='RECON_INITIATED'  THEN 'IN PROGRESS' "
				+ "WHEN RC.STATUS='NO_DATA_FOUND' OR RC.STATUS='REPORT_GENERATION_FAILED' THEN 'FAILED' "
				+ "ELSE 'NOT INITIATED' "
				+ "END AS \"STATUS\" "
				+ ", RC.COMPLETED_ON AS STATUS_DATE "
				+ ", RC.TAX_PERIOD "
				+ ", ROW_NUMBER() OVER( PARTITION BY GI.GSTIN,RC.TAX_PERIOD ORDER BY "
				+ " RC.RECON_REPORT_CONFIG_ID DESC, RC.COMPLETED_ON DESC) AS RNK "
				+ " FROM \"CLIENT1_GST\".\"GSTIN_INFO\" GI "
				+ " INNER JOIN \"CLIENT1_GST\".\"ENTITY_INFO\" EI "
				+ " ON EI.ID=GI.ENTITY_ID "
				+ " INNER JOIN \"CLIENT1_GST\".\"MASTER_STATE\" MS "
				+ " ON MS.STATE_CODE=GI.STATE_CODE "
				+ " LEFT OUTER JOIN \"CLIENT1_GST\".\"RECON_REPORT_GSTIN_DETAILS\" GD "
				+ " ON GD.GSTIN=GI.GSTIN "
				+ " LEFT OUTER JOIN \"CLIENT1_GST\".\"RECON_REPORT_CONFIG\" RC "
				+ " ON GD.RECON_REPORT_CONFIG_ID=RC.RECON_REPORT_CONFIG_ID AND RC.DERIVED_RET_PERIOD =:taxPeriod "
				+ " WHERE EI.ID=:entityId "
				+ " AND GI.IS_DELETE=FALSE "
				+ " AND EI.IS_DELETE=FALSE )A "
				+ " WHERE RNK=1)B WHERE RNK1=1 "
				+ " ORDER BY GSTIN;";

	}

}
