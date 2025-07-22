/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

/**
 * @author Laxmi.Salukuti
 *
 */
import java.util.List;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.DuplicateInwardDocCheckDto;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceDuplicateDocCheckDto;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.AppException;

public interface EInvoiceDuplicateDocCheckService {

	EInvoiceDuplicateDocCheckDto checkDuplicateDocuments(
			List<OutwardTransDocument> docs) throws AppException;

	DuplicateInwardDocCheckDto checkInwardDuplicateDocuments(
			List<InwardTransDocument> docs,
			DocKeyGenerator<InwardTransDocument, String> docKeyGen,
			InwardTransDocRepository docHeaderRepository) throws AppException;
	
	void softDeleteDupDocsAndRetainEwbDetails(List<OutwardTransDocument> docs);

}
