package com.ey.advisory.app.data.daos.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.DataStatusEntity;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
/**
 * 
 * @author Balakrishna.S
 *
 */

@Component("DataStatusSearchScreenDao")
public class DataStatusScreenDaoImpl  implements DataStatusDao{

	
	
	private static final Map<String, String> SECTION_VIEW__DATASTATUS_MAP = 
			new HashMap<>();
	
	static {
		// Initialize the static array.
		SECTION_VIEW__DATASTATUS_MAP.put("DATARECV", 
				"GST_VIEW/RECEIVED_DATE_DATASTATUS");
		SECTION_VIEW__DATASTATUS_MAP.put("DERRETPERIOD", 
				"GST_VIEW/DERIVED_RETPERIOD_DATASTATUS");
		SECTION_VIEW__DATASTATUS_MAP.put("DOCDATE", 
				"GST_VIEW/DOCDATE_DATASTATUS");
		
	}
	
	

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	

	@Override
	public List<DataStatusEntity> dataStatusSection(
			String sectionType,String buildQuery,List<String> sgstins,
			Object fromDate, Object toData) {
		
		
		//Query q=null;
	
		/*String queryStr = createQueryString(sectionType,sgstins,
				fromDate,toData);*/
		String queryStr = createQueryString(sectionType,buildQuery);
		Query q = entityManager.createNativeQuery(queryStr);
		
		if (sgstins != null && sgstins.size() > 0) {
			q.setParameter("gstin", sgstins);
		}	
if("DATARECV".equals(sectionType)){
	  //  q = entityManager.createNativeQuery(queryStr);
	//	q.setParameter("gstins", sgstins);
		q.setParameter("dataRecvFrom", fromDate);
		q.setParameter("dataRecvTo", toData);
}
else if("DERRETPERIOD".equals(sectionType)){
	// q = entityManager.createNativeQuery(queryStr);
	//	q.setParameter("gstins", sgstins);
		
		int firstDerRetPeriod = GenUtil.convertTaxPeriodToInt(
				fromDate.toString());
		int secondDerRetPeriod = GenUtil.convertTaxPeriodToInt(
				toData.toString()); 
		q.setParameter("retPeriodFrom", firstDerRetPeriod);
		q.setParameter("retPeriodTo", secondDerRetPeriod);
}
else{
	// q = entityManager.createNativeQuery(queryStr);
	//	q.setParameter("gstins", sgstins);
		q.setParameter("docDateFrom", fromDate);
		q.setParameter("docDateTo", toData);
}
		List<Object[]> list = q.getResultList();

		List<DataStatusEntity> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private DataStatusEntity convert(Object[] arr) {
		
		DataStatusEntity obj = new DataStatusEntity();
		if(arr.length==6){
			if(arr[0] != null){
				java.sql.Date sqlDocDate=(java.sql.Date) arr[0];
				obj.setDocumentDate((sqlDocDate.toLocalDate()));
			}else{
				java.sql.Date sqlDocDate=(java.sql.Date) arr[0];
				obj.setDocumentDate((sqlDocDate.toLocalDate()));
			}
			
		//	obj.setDocumentDate((sqlDocDate.toLocalDate()));
			if(arr[1] != null){
				java.sql.Date sqlRecieveDate=(java.sql.Date) arr[1];
				obj.setReceivedDate(sqlRecieveDate.toLocalDate());
			}else{
				//java.sql.Date sqlRecieveDate=(java.sql.Date) arr[1];
				obj.setReceivedDate(null);
			}
			
			obj.setAspTotal((Integer) arr[2]);
			obj.setAspProcessed((Integer) arr[3]);
			obj.setAspError((Integer) arr[4]);
			obj.setAspInfo((Integer) arr[5]);
			obj.setGstnProcessed((Integer) arr[6]);
			obj.setGstnError((Integer) arr[7]);
			obj.setAspStatus((String) arr[8]);

			}else{
				if(arr[0] != null){
					java.sql.Date sqlRecieveDate=(java.sql.Date) arr[0];
					obj.setReceivedDate(sqlRecieveDate.toLocalDate());
				}else{
					obj.setReceivedDate(null);
				}
				//java.sql.Date sqlRecieveDate=(java.sql.Date) arr[0];
				//obj.setReceivedDate(sqlRecieveDate.toLocalDate());
				obj.setAspTotal((Integer) arr[1]);
				obj.setAspProcessed((Integer) arr[2]);
				obj.setAspError((Integer) arr[3]);
				obj.setAspInfo((Integer) arr[4]);
				obj.setGstnProcessed((Integer) arr[5]);
				obj.setGstnError((Integer) arr[6]);
				obj.setAspStatus((String) arr[7]);
				
			}
		
			
		return obj;
	}
	
	private String createQueryString(String sectionType,String buildQuery) {
		String viewName="";
		String queryStr=null;
		
		if("DATARECV".equals(sectionType)){
		 viewName = SECTION_VIEW__DATASTATUS_MAP.get(sectionType);
		 queryStr = "SELECT RECEIVED_DATE,ASPTOTAL,PROCESSED,ERRORS,"
		 		+ "INFORMATION,GSTNPROCESSED,GSTNERROR,STATUS FROM \""+ viewName + "\" WHERE "
				 + buildQuery;
		 
		}else if("DERRETPERIOD".equals(sectionType)){
			 viewName = SECTION_VIEW__DATASTATUS_MAP.get(sectionType);
			 queryStr = "SELECT RECEIVED_DATE,ASPTOTAL,PROCESSED,"
			 		+ "ERRORS,INFORMATION,GSTNPROCESSED,GSTNERROR,STATUS FROM \""+ viewName + "\" WHERE "
						+ buildQuery;
			 
		}else{
			 viewName = SECTION_VIEW__DATASTATUS_MAP.get(sectionType);
			 queryStr = "SELECT DOC_DATE,RECEIVED_DATE,ASPTOTAL,PROCESSED,"
			 		+ "ERRORS,INFORMATION,GSTNPROCESSED,GSTNERROR,STATUS FROM \""+ viewName + "\" WHERE "
				+ buildQuery;
			 
		}
		return queryStr;
	}

	
}
	


