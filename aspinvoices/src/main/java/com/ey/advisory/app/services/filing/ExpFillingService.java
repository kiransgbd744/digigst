package com.ey.advisory.app.services.filing;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.DocRepository;

@Component("ExpFillingService")
/**
 * 
 * @author Siva.Nandam
 *
 */
public class ExpFillingService implements FilingService {

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Override
	public ExpFillingDto getFilingData(String gstin, String returnPeriod) {

		List<Object[]> doc = docRepository.getExp(returnPeriod, gstin);
		Object[] arr = doc.get(0);
		ExpFillingDto exp = new ExpFillingDto();

		if (arr[0] == null || arr[0].toString().isEmpty()) {
			arr[0] = 0L;
		}
		if (arr[1] == null || arr[1].toString().isEmpty()) {
			arr[1] = new BigDecimal("0.0");
		}
		if (arr[2] == null || arr[2].toString().isEmpty()) {
			arr[2] = new BigDecimal("0.0");
		}
		if (arr[3] == null || arr[3].toString().isEmpty()) {
			arr[3] = new BigDecimal("0.0");
		}
		exp.setSecNum("EXP");
		exp.setChksum(null);
		exp.setTtl_val((BigDecimal) arr[1]);
		exp.setTtl_rec((Long) arr[0]);
		exp.setTtl_tax((BigDecimal) arr[2]);
		exp.setTtl_igst((BigDecimal) arr[3]);

		return exp;

	}

}
