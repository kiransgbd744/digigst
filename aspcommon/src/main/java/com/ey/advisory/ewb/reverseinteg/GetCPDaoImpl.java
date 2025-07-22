/**
 * 
 */
package com.ey.advisory.ewb.reverseinteg;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * @author Khalid.Khan
 *
 */
@Component("GetCPDaoImpl")
public class GetCPDaoImpl implements GetCPDao{
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Override
	public List<ReverseIntegParamsDto> getDistinctGstinDate() {
		String queryStr = createQueryString();
		Query q = entityManager.createNativeQuery(queryStr);
		List<Object[]> resultList = q.getResultList();
		return resultList.stream().map(o -> convert(o)).collect(Collectors.toList());
	}

	private ReverseIntegParamsDto  convert(Object[] o) {
		ReverseIntegParamsDto dto = new ReverseIntegParamsDto();
		dto.setGstin(String.valueOf(o[0]));
		dto.setLocalDate(((Date) o[1]).toLocalDate());
		return dto;
	}

	private String createQueryString() {
		String query = "select * from (select distinct CLIENT_GSTIN as gstin ,EWB_GEN_DATE as date from COUNTER_PARTY_EWAY_BILLS "
				+ " where FETCH_STATUS = 'SUCCESS' AND REV_INT_STATUS IN ('NOT_INITIATED','FAILED') "
				+ " MINUS select  distinct GSTIN as gstin ,EWB_GEN_DATE as date from GET_COUNTER_PARTY_INVOC_CONTROL where REV_INT_STATUS in"
				+ " ('POSTED','INITIATED')) order by gstin,date";
		return query;
	}

}
