package com.ey.advisory.app.services.filing;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.HsnProcRepository;
import com.ey.advisory.app.docs.dto.HsnFillingDto;

@Component("HsnFillingService")
/**
 * 
 * @author Siva.Nandam
 *
 */
public class HsnFillingService implements FilingService {

	@Autowired
	@Qualifier("HsnProcRepository")
	private HsnProcRepository hsnProcRepository;

	@Override
	public HsnFillingDto getFilingData(String gstin, String returnPeriod) {

		List<Object[]> doc = hsnProcRepository.gethsn(returnPeriod, gstin);
		Object[] arr = doc.get(0);
		HsnFillingDto hsn = new HsnFillingDto();
		
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
		if (arr[4] == null || arr[4].toString().isEmpty()) {
			arr[4] = new BigDecimal("0.0");
		}
		if (arr[5] == null || arr[5].toString().isEmpty()) {
			arr[5] = new BigDecimal("0.0");
		}
		

		hsn.setSecNum("HSN");
		hsn.setChksum(null);
		hsn.setTtl_rec((Long) arr[0]);
		hsn.setTtl_tax((BigDecimal) arr[1]);
		hsn.setTtl_val((BigDecimal) arr[2]);
		hsn.setTtl_igst((BigDecimal) arr[3]);
		hsn.setTtl_cgst((BigDecimal) arr[4]);
		hsn.setTtl_sgst((BigDecimal) arr[5]);
		hsn.setTtl_cess((BigDecimal) arr[6]);

		return hsn;

	}

}
