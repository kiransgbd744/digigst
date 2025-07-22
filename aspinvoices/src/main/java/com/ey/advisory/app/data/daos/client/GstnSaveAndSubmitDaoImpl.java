package com.ey.advisory.app.data.daos.client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.GstnSaveAndSubmitView;
import com.ey.advisory.app.docs.dto.GstnSaveSubmitReqDto;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 *
 * @author Mohana.Dasari
 *
 */
@Component("GstnSaveAndSubmitDaoImpl")
public class GstnSaveAndSubmitDaoImpl implements GstnSaveAndSubmitDao {
	private static final String GSTN_SAVE_SUBMIT_VIEW = "GST_VIEW/SUBMIT_FILE";
		
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<GstnSaveAndSubmitView> getGstnSaveAdnSubmitDocs(
			GstnSaveSubmitReqDto request) {
		StringBuilder buildQuery = new StringBuilder();
		buildQuery.append("DERIVED_RET_PERIOD BETWEEN ");
		buildQuery.append(":retPeriodFrom AND :retPeriodTo"); 
		buildQuery.append(" ORDER BY RECEIVED_DATE DESC");
		
		if(request.getSgstins() != null && request.getSgstins().size() > 0){
			buildQuery.append(" AND SUPPLIER_GSTIN IN :gstin");
		}
		
		String queryStr = createQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);
		
		if (request.getSgstins() != null && request.getSgstins().size() > 0) {
			q.setParameter("gstin", request.getSgstins());
		}
		
		int derivedRetPeriodFrom = GenUtil.convertTaxPeriodToInt(
											request.getRetPeriodFrom());
		int derivedRetPeriodTo = GenUtil.convertTaxPeriodToInt(
											request.getRetPeriodTo()); 
		q.setParameter("retPeriodFrom", derivedRetPeriodFrom);
		q.setParameter("retPeriodTo", derivedRetPeriodTo);
		
		List<Object[]> list = q.getResultList();
		List<GstnSaveAndSubmitView> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}
	
	private GstnSaveAndSubmitView convert(Object[] arr) {
		GstnSaveAndSubmitView obj = new GstnSaveAndSubmitView();
		obj.setSgstin((String)arr[0]);
		obj.setDerivedRetPeriod((Integer) arr[1]);
		java.sql.Date sqlRecieveDate = (java.sql.Date) arr[2];
		obj.setReceivedDate(sqlRecieveDate.toLocalDate());
		obj.setAspTotal((Integer) arr[3]);
		obj.setAspProcessed((Integer) arr[4]);
		obj.setAspError((Integer) arr[5]);
		obj.setAspInfo((Integer) arr[6]);
		return obj;
	}
	
	private String createQueryString(String buildQuery) {
		String queryStr = "SELECT * FROM \"" + GSTN_SAVE_SUBMIT_VIEW
						+ "\" WHERE " + buildQuery;
		return queryStr;
	}

}
