package com.ey.advisory.app.filereport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.EYDateUtil;

/**
 * @author Arun K.A
 *
 */

@Component("ReportFileStatusDaoImpl")
public class ReportFileStatusDaoImpl implements ReportFileStatusDao{
	

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<ReportFileStatusReportDto> getFileStatusReportDetails(
			String userName) {
		
		String queryString = createQueryString(userName);

		Query q = entityManager.createNativeQuery(queryString);

		q.setParameter("userName", userName);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<ReportFileStatusReportDto> retList = list
				.parallelStream().map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
		
	}

	private ReportFileStatusReportDto convert(Object[] arr) {
		
		ReportFileStatusReportDto obj = new ReportFileStatusReportDto();
		obj.setRequestId((Long)arr[0]);
		obj.setCompletionOn(EYDateUtil.toISTDateTimeFromUTC((Timestamp)arr[1]));
		obj.setTaxPeriod((String)arr[2]);
		obj.setInitiatedOn(EYDateUtil.toISTDateTimeFromUTC((Timestamp)arr[3]));
		
		return obj;
	}

	private String createQueryString(String userName) {
		
		String query = "SELECT REPORT_DOWNLOAD_ID,COMPLETED_ON,TAX_PERIOD,"
				+ "CREATED_DATE,REPORT_TYPE,REPORT_STATUS,"
				+ "UPLOADED_FILE_NAME FROM REPORT_DOWNLOAD_REQUEST "
				+ "WHERE CREATED_BY = :userName";
		return query;
	}

}
