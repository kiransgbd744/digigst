package com.ey.advisory.app.services.filing;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.NilFillingDto;
import com.ey.advisory.app.data.repositories.client.NilProcRepository;
/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("NilFillingService")
public class NilFillingService implements FilingService {

	@Autowired
	@Qualifier("NilProcRepository")
	private NilProcRepository nilProcRepository;

	@Override
	public NilFillingDto getFilingData(String gstin, String returnPeriod) {
		BigDecimal nilsum= BigDecimal.ZERO;
		BigDecimal nonsum= BigDecimal.ZERO;
		BigDecimal Exptsum= BigDecimal.ZERO;
		List<BigDecimal> nil = nilProcRepository.getNil(returnPeriod, gstin);
		List<BigDecimal> non = nilProcRepository.getNon(returnPeriod, gstin);
		List<BigDecimal> expt = nilProcRepository.getEXpt(returnPeriod, gstin);
		if(nil.get(0)!=null){
		 nilsum = nil.get(0);
		}
		if(non.get(0)!=null){
		 nonsum = non.get(0);
		}
		if(expt.get(0)!=null){
		 Exptsum = expt.get(0);
		}
		NilFillingDto nne = new NilFillingDto();
	
		nne.setSecNum("NIL");
		nne.setChksum(null);
        nne.setTtl_nil(nilsum);
        nne.setTtl_non(nonsum);
        nne.setTtl_expt(Exptsum);
		
		return nne;
		

	}
}
