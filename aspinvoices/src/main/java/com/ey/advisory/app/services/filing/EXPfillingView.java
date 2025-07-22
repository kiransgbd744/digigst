package com.ey.advisory.app.services.filing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.HsnFillingDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;

@Component("EXPfillingView")
public class EXPfillingView {
	


	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	
	public ExpFillingDto loadBasicExpSection(
			String sgstins,String TaxPeriod) {
		int firstDerRetPeriod = GenUtil.convertTaxPeriodToInt(
				TaxPeriod.toString());
		
		try {
			String viewName ="GST_VIEW/EXP_GSTR1SUMMARY_VIEW";
		Query q = entityManager.createNativeQuery(
				"SELECT SUM(RECORD_COUNT),SUM(DOC_AMT),"
				
         + "SUM(IGST),"
         + "SUM(TAXABLE_VALUE)  FROM \"" + viewName 
				+ "\" WHERE SUPPLIER_GSTIN = :sgstin "
				+ "AND DERIVED_RET_PERIOD =:TaxPeriod");
		
			q.setParameter("sgstin", sgstins);		
				
			q.setParameter("TaxPeriod", firstDerRetPeriod);
			
		List<Object[]> list = q.getResultList();

		List<ExpFillingDto> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		
		
		return retList.get(0);
	} catch (Exception e) {
		e.printStackTrace();
		throw new AppException("Unexpected error in query execution.");
	}
	}

	private ExpFillingDto convert(Object[] arr) {
		ExpFillingDto obj = new ExpFillingDto();
		obj.setSecNum("EXP");
		obj.setChksum("");
		if(arr[0]==null || arr[0].toString().isEmpty()){
    		arr[0]=new BigDecimal("0.0");
    	}
		if(arr[1]==null || arr[1].toString().isEmpty()){
    		arr[1]=new BigDecimal("0.0");
    	}
    	if(arr[2]==null || arr[2].toString().isEmpty()){
    		arr[2]=new BigDecimal("0.0");
    	}
    	if(arr[3]==null || arr[3].toString().isEmpty()){
    		arr[3]=new BigDecimal("0.0");
    	}
    	
    	
    	
		//obj.setTtl_rec((BigDecimal)arr[0]);
		obj.setTtl_val((BigDecimal)arr[1]);//invoice value
		obj.setTtl_igst((BigDecimal)arr[2]);
		obj.setTtl_tax((BigDecimal)arr[3]);
		return obj;
	}

	




}
