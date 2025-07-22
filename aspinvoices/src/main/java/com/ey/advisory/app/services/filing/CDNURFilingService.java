package com.ey.advisory.app.services.filing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.B2CLFilingDto;
import com.ey.advisory.app.docs.dto.B2CLItemFilingDto;
/**
 * 
 * @author Siva.Nandam
 *
 */
@Service("CDNURFilingService")

public class CDNURFilingService implements FilingService {

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;
	@Override
	public B2CLFilingDto getFilingData(String gstin, String returnPeriod) {

		List<Object[]> doc = docRepository
				.getCdnurData(returnPeriod, gstin);
		
		B2CLItemFilingDto b2clitmfile = new B2CLItemFilingDto();
        List<B2CLItemFilingDto> b2clitmList = new ArrayList<>();            
        B2CLFilingDto b2clfile = new B2CLFilingDto();
        Long totalRec_Count=0L;
        BigDecimal ttlIgst=new BigDecimal("0.0");
        BigDecimal ttl_cess=new BigDecimal("0.0");
        BigDecimal Ttl_tax=new BigDecimal("0.0");
    BigDecimal invoiceValue =new BigDecimal("0.0");
        for(Object[] arr:doc){
        //doc.stream().forEach(arr -> {
        	if(arr[4]==null || arr[4].toString().isEmpty()){
        		arr[4]=0;
        	}
        	if(arr[5]==null || arr[5].toString().isEmpty()){
        		arr[5]=0;
        	}
        	if(arr[3]==null || arr[3].toString().isEmpty()){
        		arr[3]=0;
        	}
        	   b2clitmfile.setPos((String)arr[0]);
        	   b2clitmfile.setChksum(null);
               totalRec_Count=totalRec_Count+(Long)arr[1];
               b2clitmfile.setTtl_rec((Long)arr[1]);
               b2clitmfile.setTtl_val((BigDecimal)arr[2]);
               invoiceValue=invoiceValue.add((BigDecimal)arr[2]);
               b2clitmfile.setTtl_igst((BigDecimal)arr[4]);
               ttlIgst=ttlIgst.add((BigDecimal)arr[4]);
               b2clitmfile.setTtl_cess((BigDecimal)arr[5]);
               ttl_cess=ttl_cess.add((BigDecimal)arr[5]);
               b2clitmfile.setTtl_tax((BigDecimal)arr[3]);
               Ttl_tax=Ttl_tax.add((BigDecimal)arr[3]);
               b2clitmList.add(b2clitmfile);
        //});
        }
        b2clfile.setCpty_sum(b2clitmList);
        b2clfile.setSec_nm("CDNUR");
        b2clfile.setChksum(null);
        b2clfile.setTtl_rec(totalRec_Count);
        b2clfile.setTtl_val(invoiceValue);
        b2clfile.setTtl_igst(ttlIgst);
        b2clfile.setTtl_cess(ttl_cess);
        b2clfile.setTtl_tax(Ttl_tax);
       
        return b2clfile;
	}

}

