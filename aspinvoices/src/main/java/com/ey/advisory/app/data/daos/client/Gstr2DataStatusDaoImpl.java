package com.ey.advisory.app.data.daos.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr2DataStatusEntity;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;


@Component("Gstr2DataStatusDaoImpl")
public class Gstr2DataStatusDaoImpl implements Gstr2DataStatusDao {
	public static final String WHERE= "\" WHERE ";
	public static final String DATASTATUS= "DATASTATUS";
	
	private static final Map<String, String> SECTION_VIEW__DATASTATUS_MAP = 
			new HashMap<>();
	
	static {
		// Initialize the static array.
		SECTION_VIEW__DATASTATUS_MAP.put("DATASTATUS", 
				"GSTR2_VIEW/DATA_STATUS");
		
	}

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	

	@Override
	public List<Gstr2DataStatusEntity> dataGstr2StatusSection(
			String sectionType, String buildQuery, List<String> sgstins,
			Object dataRecvFrom, Object dataRecvTo) {		
		
		String queryStr = createQueryString(sectionType,buildQuery);
		Query q = entityManager.createNativeQuery(queryStr);
		
		if (sgstins != null && !sgstins.isEmpty()) {
			q.setParameter("gstin", sgstins);
		}	
if("DATA_RECEIVED".equals(sectionType)){
		q.setParameter("recivedFromDate", dataRecvFrom);
		q.setParameter("recivedToDate", dataRecvTo);
}
else if("RETURN_PERIOD".equals(sectionType)){
		
		int firstDerRetPeriod = GenUtil.convertTaxPeriodToInt(
				dataRecvFrom.toString());
		int secondDerRetPeriod = GenUtil.convertTaxPeriodToInt(
				dataRecvTo.toString()); 
		q.setParameter("retunPeriodFrom", firstDerRetPeriod);
		q.setParameter("retunPeriodTo", secondDerRetPeriod);
}
else{
		q.setParameter("docFromdate", dataRecvFrom);
		q.setParameter("docTodate", dataRecvTo);
}
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		List<Gstr2DataStatusEntity> retList =
				list.parallelStream()
				.map(o -> convert(o,sectionType))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
		}

        private Gstr2DataStatusEntity convert(Object[] arr,String sectionType) {
		
		Gstr2DataStatusEntity obj = new Gstr2DataStatusEntity();
		if(sectionType.equalsIgnoreCase("DOC_DATE")){
			java.sql.Date sqlDocDate=(java.sql.Date) arr[0];
			if(sqlDocDate != null){
				obj.setDocumentDate((sqlDocDate.toLocalDate()));
			}
			
			java.sql.Date sqlRecieveDate=(java.sql.Date) arr[1];
			
			if(sqlRecieveDate != null){
			obj.setReceivedDate(sqlRecieveDate.toLocalDate());
			   }
			obj.setAspTotal((Integer) arr[2]);
			obj.setAspProcessed((Integer) arr[3]);
			obj.setAspError((Integer) arr[4]);
			obj.setAspInfo((Integer) arr[5]);
			}
		
		else if(sectionType.equalsIgnoreCase("RETURN_PERIOD")){
			java.sql.Date sqlRecieveDate=(java.sql.Date) arr[0];
			if(sqlRecieveDate != null){
				obj.setReceivedDate(sqlRecieveDate.toLocalDate());
			}
			obj.setDerivedRetPeriod((Integer) arr[1]);
			obj.setAspTotal((Integer) arr[2]);
			obj.setAspProcessed((Integer) arr[3]);
			obj.setAspError((Integer) arr[4]);
			obj.setAspInfo((Integer) arr[5]);
		}
		else{
				java.sql.Date sqlRecieveDate=(java.sql.Date) arr[0];
				if(sqlRecieveDate != null){
					obj.setReceivedDate(sqlRecieveDate.toLocalDate());
				}
				
				obj.setAspTotal((Integer) arr[1]);
				obj.setAspProcessed((Integer) arr[2]);
				obj.setAspError((Integer) arr[3]);
				obj.setAspInfo((Integer) arr[4]);
			}
		
			
		return obj;
	}
	
	private String createQueryString(String sectionType,String buildQuery) {
		String viewName="";
		String queryStr=null;
		
		if("DATA_RECEIVED".equalsIgnoreCase(sectionType)){
		 viewName = SECTION_VIEW__DATASTATUS_MAP.get(DATASTATUS);
		 queryStr = "SELECT RECEIVED_DATE,ASPTOTAL,PROCESSED,ERRORS,"
		 		+ "INFORMATION FROM \""+ viewName + WHERE
				 + buildQuery;
		 
		}else if("RETURN_PERIOD".equals(sectionType)){
			 viewName = SECTION_VIEW__DATASTATUS_MAP.get(DATASTATUS);
			 queryStr = "SELECT RECEIVED_DATE,DERIVED_RET_PERIOD, "
			 		+ "ASPTOTAL,PROCESSED,"
			 		+ "ERRORS,INFORMATION FROM \""+ viewName + WHERE
						+ buildQuery;
			 
		}else{
			 viewName = SECTION_VIEW__DATASTATUS_MAP.get(DATASTATUS);
			 queryStr = "SELECT DOC_DATE,RECEIVED_DATE,ASPTOTAL,PROCESSED,"
			 		+ "ERRORS,INFORMATION FROM \""+ viewName + WHERE
				+ buildQuery;
			 
		}
		return queryStr;
	}

	
}

