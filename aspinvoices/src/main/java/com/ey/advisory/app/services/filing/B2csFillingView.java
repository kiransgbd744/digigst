package com.ey.advisory.app.services.filing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.B2csFillingDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
@Component("B2csFillingView")
public class B2csFillingView {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	
	public B2csFillingDto loadBasicB2csSection(
			String sgstins,String TaxPeriod) {
		int firstDerRetPeriod = GenUtil.convertTaxPeriodToInt(
				TaxPeriod.toString());
		
		try {
			String viewName ="GST_VIEW/B2CS_GSTR1SUMMARY_VIEW";
		Query q = entityManager.createNativeQuery(
				"SELECT SUM(RECORD_COUNT),"
				
         + "SUM(IGST), SUM(CGST), SUM(SGST), "
         + "SUM(CESS),SUM(TAXABLE_VALUE)  FROM \"" + viewName 
				+ "\" WHERE SUPPLIER_GSTIN = :sgstin "
				+ "AND DERIVED_RET_PERIOD =:TaxPeriod");
		
			q.setParameter("sgstin", sgstins);		
				
			q.setParameter("TaxPeriod", firstDerRetPeriod);
			
		List<Object[]> list = q.getResultList();

		List<B2csFillingDto> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		
		
		return retList.get(0);
	} catch (Exception e) {
		e.printStackTrace();
		throw new AppException("Unexpected error in query execution.");
	}
	}

	private B2csFillingDto convert(Object[] arr) {
		B2csFillingDto obj = new B2csFillingDto();
		if(arr[0]==null || arr[0].toString().isEmpty()){
    		arr[0]=0;
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
    	if(arr[4]==null || arr[4].toString().isEmpty()){
    		arr[4]=new BigDecimal("0.0");
    	}
    	if(arr[5]==null || arr[5].toString().isEmpty()){
    		arr[5]=new BigDecimal("0.0");
    	}
    	
		obj.setSecNum("B2CS");
		obj.setChksum("");
		//obj.setTtl_rec((int)arr[0]);
		//obj.setTtl_val((BigDecimal)arr[1]);//invoice value
		obj.setTtl_igst((BigDecimal)arr[1]);
		obj.setTtl_cgst((BigDecimal)arr[2]);
		obj.setTtl_sgst((BigDecimal)arr[3]);
		obj.setTtl_cess((BigDecimal)arr[4]);
		obj.setTtl_tax((BigDecimal)arr[5]);
		return obj;
	}

	


}


