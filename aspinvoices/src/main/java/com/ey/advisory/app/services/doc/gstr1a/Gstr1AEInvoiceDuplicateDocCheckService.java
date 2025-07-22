/**
 * 
 */
package com.ey.advisory.app.services.doc.gstr1a;

/**
 * @author Shashikant.Shukla
 *
 */
import java.util.List;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.DuplicateInwardDocCheckDto;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.AppException;

public interface Gstr1AEInvoiceDuplicateDocCheckService {

	Gstr1AEInvoiceDuplicateDocCheckDto checkDuplicateDocuments(
			List<Gstr1AOutwardTransDocument> docs) throws AppException;

	DuplicateInwardDocCheckDto checkInwardDuplicateDocuments(
			List<InwardTransDocument> docs,
			DocKeyGenerator<InwardTransDocument, String> docKeyGen,
			InwardTransDocRepository docHeaderRepository) throws AppException;

	void softDeleteDupDocsAndRetainEwbDetails(
			List<Gstr1AOutwardTransDocument> docs);

}
