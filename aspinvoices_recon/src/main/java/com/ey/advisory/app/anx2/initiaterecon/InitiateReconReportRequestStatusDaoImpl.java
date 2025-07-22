package com.ey.advisory.app.anx2.initiaterecon;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.gstr2.userdetails.GstinDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("InitiateReconReportRequestStatusDaoImpl")
public class InitiateReconReportRequestStatusDaoImpl
		implements InitiateReconReportRequestStatusDao {
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<InitiateReconReportRequestStatusDto> getReportRequestStatus(
			String userName) {

		String queryString = createQueryString(userName);

		Query q = entityManager.createNativeQuery(queryString);

		q.setParameter("userName", userName);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<InitiateReconReportRequestStatusDto> retList = list
				.parallelStream().map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	
	@SuppressWarnings("unchecked")
	private InitiateReconReportRequestStatusDto convert(Object[] arr) {
		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " InitiateReconReportRequestStatusDto object";
			LOGGER.debug(str);
		}

		InitiateReconReportRequestStatusDto convert = 
				new InitiateReconReportRequestStatusDto();
		
		BigInteger b = (BigInteger)arr[0];
		Long requestId = b.longValue();
		convert.setRequestId(requestId);
		Timestamp date = (Timestamp)arr[1];
		convert.setInitiatedOn(date.toString());
		convert.setInitiatedBy((String)arr[2]);
		convert.setTaxPeriod((String)arr[3]);
		date =(Timestamp)arr[4];
		String ldt = date != null ? date.toString() : null ;
		convert.setCompletionOn(ldt);
		convert.setStatus((String)arr[5]);
		convert.setPath((String)arr[6]);
		BigInteger bi = (BigInteger)arr[7];
		Integer gstnCount = bi.intValue();
		convert.setGstinCount(gstnCount);
		GstinDto gstinDto = new GstinDto((String)arr[8]);
		convert.setGstins(new ArrayList<GstinDto>(Arrays.asList(gstinDto)));
		return convert;
	}

	private String createQueryString(String userName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Creating query for Request Status";
			LOGGER.debug(msg);
		}
		String queryStr = "SELECT RRC.RECON_REPORT_CONFIG_ID AS REQUEST_ID, "
				+ "RRC.CREATED_DATE, RRC.CREATED_BY, RRC.TAX_PERIOD, "
				+ "RRC.COMPLETED_ON, "
				+ "RRC.STATUS, RRC.FILE_PATH AS PATH, COUNT(*) "
				+ "AS GSTIN_COUNT, RRGD.GSTIN FROM "
				+ "RECON_REPORT_CONFIG RRC "
				+ "INNER JOIN RECON_REPORT_GSTIN_DETAILS RRGD "
				+ "ON RRGD.RECON_REPORT_CONFIG_ID = RRC.RECON_REPORT_CONFIG_ID "
				+ "WHERE RRC.CREATED_BY = :userName GROUP BY "
				+ "RRC.RECON_REPORT_CONFIG_ID, RRC.CREATED_DATE, "
				+ "RRC.CREATED_BY, RRC.COMPLETED_ON, RRC.STATUS,"
				+ " RRC.TAX_PERIOD, RRGD.GSTIN, RRC.FILE_PATH";
				
		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Query created for Request Status : %s", queryStr);
			LOGGER.debug(str);
		}
		
		return queryStr;
	}


}
