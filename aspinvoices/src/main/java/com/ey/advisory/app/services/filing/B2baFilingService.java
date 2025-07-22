package com.ey.advisory.app.services.filing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.B2BFilingDto;
import com.ey.advisory.app.docs.dto.B2BItemFilingDto;
/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("B2baFilingService")
public class B2baFilingService implements FilingService {

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;
	@Override
	public B2BFilingDto getFilingData(String gstin, String returnPeriod) {

		List<Object[]> doc = docRepository
				.getB2baData(returnPeriod, gstin);
		
        B2BItemFilingDto b2bitmfile = new B2BItemFilingDto();
        List<B2BItemFilingDto> b2bitmList = new ArrayList<>();            
        B2BFilingDto b2bfile = new B2BFilingDto();
        Long totalRec_Count=0L;
        BigDecimal ttlCgst=new BigDecimal("0.0");
        BigDecimal ttlSgst=new BigDecimal("0.0");
        BigDecimal ttlIgst=new BigDecimal("0.0");
        BigDecimal ttl_cess=new BigDecimal("0.0");
        BigDecimal Ttl_tax=new BigDecimal("0.0");
        BigDecimal invoiceValue=new BigDecimal("0.0");
        for(Object[] arr:doc){
        //doc.stream().forEach(arr -> {
        	if(arr[6]==null || arr[6].toString().isEmpty()){
        		arr[6]=0;
        	}
        	if(arr[4]==null || arr[4].toString().isEmpty()){
        		arr[4]=0;
        	}
        	if(arr[5]==null || arr[6].toString().isEmpty()){
        		arr[5]=0;
        	}
        	if(arr[3]==null || arr[3].toString().isEmpty()){
        		arr[3]=0;
        	}
               b2bitmfile.setCtin((String) arr[0]);
               b2bitmfile.setChksum("");
               totalRec_Count=totalRec_Count+(Long)arr[1];
               b2bitmfile.setTtl_rec((Long)arr[1]);
               b2bitmfile.setTtl_val((BigDecimal)arr[2]);
               invoiceValue=invoiceValue.add((BigDecimal)arr[2]);
               b2bitmfile.setTtl_igst((BigDecimal)arr[6]);
               ttlIgst=ttlIgst.add((BigDecimal)arr[6]);
               b2bitmfile.setTtl_cgst((BigDecimal)arr[4]);
               ttlCgst=ttlCgst.add((BigDecimal)arr[4]);
               b2bitmfile.setTtl_sgst((BigDecimal)arr[5]);
               ttlSgst=ttlSgst.add((BigDecimal)arr[5]);
               b2bitmfile.setTtl_cess((BigDecimal)arr[7]);
               ttl_cess=ttl_cess.add((BigDecimal)arr[7]);
               b2bitmfile.setTtl_tax((BigDecimal)arr[3]);
               Ttl_tax=Ttl_tax.add((BigDecimal)arr[3]);
               b2bitmList.add(b2bitmfile);
        //});
        }
        b2bfile.setCpty_sum(b2bitmList);
        b2bfile.setSec_nm("B2BA");
        b2bfile.setChksum(null);
        b2bfile.setTtl_rec(totalRec_Count);
        b2bfile.setTtl_val(invoiceValue);
        b2bfile.setTtl_cgst(ttlCgst);
        b2bfile.setTtl_igst(ttlIgst);
        b2bfile.setTtl_sgst(ttlSgst);
        b2bfile.setTtl_cess(ttl_cess);
        b2bfile.setTtl_tax(Ttl_tax);
        
        
        
		
		return b2bfile;
	}

}

