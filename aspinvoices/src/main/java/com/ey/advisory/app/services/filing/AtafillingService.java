package com.ey.advisory.app.services.filing;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.AtAtaProcRepository;
import com.ey.advisory.app.docs.dto.HsnFillingDto;
@Component("AtafillingService")
public class AtafillingService implements FilingService {

	@Autowired
	@Qualifier("AtAtaProcRepository")
	private AtAtaProcRepository atAtaProcRepository;

	@Override
	public HsnFillingDto getFilingData(String gstin, String returnPeriod) {

		List<Object[]> doc = atAtaProcRepository.getAta(returnPeriod, gstin);
		Object[] arr = doc.get(0);
		HsnFillingDto ata = new HsnFillingDto();


		if (arr[0] == null || arr[0].toString().isEmpty()) {
			arr[0] = 0;
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
			ata.setSecNum("ATA");
			ata.setChksum(null);
            //b2cs.setTtl_val(ttl_val);invoiceValue
			ata.setTtl_rec((Long) arr[0]);
			ata.setTtl_tax((BigDecimal) arr[1]);
			ata.setTtl_igst((BigDecimal) arr[2]);
			ata.setTtl_cgst((BigDecimal) arr[3]);
			ata.setTtl_sgst((BigDecimal) arr[4]);
			ata.setTtl_cess((BigDecimal) arr[5]);
			

		
		return ata;
		

	}


}
