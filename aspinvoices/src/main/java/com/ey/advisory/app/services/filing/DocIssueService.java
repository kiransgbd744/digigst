package com.ey.advisory.app.services.filing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.DocIssueDto;
import com.ey.advisory.app.data.repositories.client.Gstr1InvoiceRepository;
/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("DocIssueService")
/**
 * 
 * @author Siva.Nandam
 *
 */
public class DocIssueService implements FilingService {

	@Autowired
	@Qualifier("Gstr1InvoiceRepository")
	private Gstr1InvoiceRepository gstr1InvoiceRepository;

	@Override
	public DocIssueDto getFilingData(String gstin, String returnPeriod) {

		List<Object[]> doc = gstr1InvoiceRepository.getInvoice(returnPeriod, gstin);
		Object[] arr = doc.get(0);
		DocIssueDto invoice = new DocIssueDto();


		if (arr[0] == null || arr[0].toString().isEmpty()) {
			arr[0] = 0;
			
		}
		if (arr[1] == null || arr[1].toString().isEmpty()) {
			arr[1] = 0;
		}
		if (arr[2] == null || arr[2].toString().isEmpty()) {
			arr[2] = 0;
		}
			invoice.setSecNum("DOCISSUE");
			invoice.setChksum(null);
			invoice.setNetNumber(Integer.valueOf(arr[0].toString()));
			invoice.setCancelled(Integer.valueOf(arr[1].toString()));
            invoice.setNetNumber(Integer.valueOf(arr[2].toString()));
		
		return invoice;
		

	}
}