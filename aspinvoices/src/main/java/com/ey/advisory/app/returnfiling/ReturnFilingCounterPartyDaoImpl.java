/**
 * 
 */
package com.ey.advisory.app.returnfiling;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author Arun KA
 *
 */
@Component("ReturnFilingCounterPartyDaoImpl")
public class ReturnFilingCounterPartyDaoImpl
		implements ReturnFilingCounterPartyDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<ReturnFilingCounterPartyStatusDto> getCounterPartyDetailsDB(
			String userName) {

		String queryStr = createQuery(userName);

		Query q = entityManager.createNativeQuery(queryStr);
	//	q.setParameter("userName", userName);

		@SuppressWarnings("unchecked")
		List<Object[]> result = q.getResultList();

		List<ReturnFilingCounterPartyStatusDto> list = result.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return list;
	}

	private ReturnFilingCounterPartyStatusDto convert(Object[] o) {

		ReturnFilingCounterPartyStatusDto obj = 
				new ReturnFilingCounterPartyStatusDto();
		
		obj.setRequestId(GenUtil.getBigInteger(o[0]));
		Timestamp date = (Timestamp)o[1]; 
		//obj.setDateOfUpload(EYDateUtil.toISTDateTimeFromUTC(
			//	date.toLocalDateTime()));
		obj.setDateOfUpload(EYDateUtil
				.toISTDateTimeFromUTC(date.toLocalDateTime()).toString()
				.substring(0, 19).replace("T", " "));
		
		obj.setNoOfGstins(GenUtil.getBigInteger(o[2]));
		obj.setStatus((String) o[3]);
		obj.setUploadedBy((String)o[4]);

		return obj;
	}

	private String createQuery(String userName) {

		/*String query = "SELECT REQUEST_ID,USER_UPLOAD_DATE,NO_OF_GSTINS,STATUS,CREATED_BY "
				+ " FROM RETURN_FILING_CONFIG WHERE "
				+ " CREATED_BY = :userName ORDER BY 1 DESC";
		*/
		String query = "SELECT REQUEST_ID,USER_UPLOAD_DATE,NO_OF_GSTINS,STATUS,CREATED_BY "
				+ " FROM RETURN_FILING_CONFIG "
				+ " ORDER BY 1 DESC";
		
		return query;
	}

}
