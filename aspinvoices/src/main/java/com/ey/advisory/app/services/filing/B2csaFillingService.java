package com.ey.advisory.app.services.filing;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.B2csProcRepository;
import com.ey.advisory.app.docs.dto.B2csFillingDto;

/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("B2csaFillingService")
public class B2csaFillingService implements FilingService {

	@Autowired
	@Qualifier("B2csProcRepository")
	private B2csProcRepository b2csProcRepository;

	@Override
	public B2csFillingDto getFilingData(String gstin, String returnPeriod) {

		List<Object[]> doc = b2csProcRepository.getB2csa(returnPeriod, gstin);
		Object[] arr = doc.get(0);
		B2csFillingDto b2csa = new B2csFillingDto();

		if (arr[0] == null || arr[0].toString().isEmpty()) {
			arr[0] = 0L;
		}
		if (arr[1] == null || arr[1].toString().isEmpty()) {
			arr[1] = new BigDecimal("0.0");
		}
		if (arr[2] == null || arr[2].toString().isEmpty()) {
			arr[2] = new BigDecimal("0.0");
		}
		if (arr[4] == null || arr[4].toString().isEmpty()) {
			arr[4] = new BigDecimal("0.0");
		}
		if (arr[5] == null || arr[5].toString().isEmpty()) {
			arr[5] = new BigDecimal("0.0");
		}
		if (arr[3] == null || arr[3].toString().isEmpty()) {
			arr[3] = new BigDecimal("0.0");
		}
		b2csa.setSecNum("B2CSA");
		b2csa.setChksum(null);

		b2csa.setTtl_rec((Long) arr[0]);
		b2csa.setTtl_tax((BigDecimal) arr[1]);
		b2csa.setTtl_igst((BigDecimal) arr[2]);
		b2csa.setTtl_cgst((BigDecimal) arr[3]);
		b2csa.setTtl_sgst((BigDecimal) arr[4]);
		b2csa.setTtl_cess((BigDecimal) arr[5]);

		return b2csa;

	}
}
