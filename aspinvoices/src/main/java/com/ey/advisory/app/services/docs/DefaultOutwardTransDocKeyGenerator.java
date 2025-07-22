package com.ey.advisory.app.services.docs;

import java.time.LocalDate;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;

/**
 * This class is responsible for creating a document key for an Outward Supply
 * Document. This is required, as the identifier that we use as docId is
 * internal to our system. This is valid within our system, but not outside our
 * system. So, when we send documents to GSTN, we lose this ID, as GSTN doesn't
 * store our internal id. (It would be nice if GSTN introduced this
 * functionality). So, when we fetch the documents back from GSTN, we need some
 * other mechanism to match it with the documents existing in our DB (as ID is
 * not in the data that we obtained from GSTN). So, we need to have another
 * natural key for each document to do the matching. Following is the
 * combination that we use:
 * 
 * Document Key = SGS
 * 
 * @author Mohana.Dasari
 *
 */
@Component("DefaultOutwardTransDocKeyGenerator")
public class DefaultOutwardTransDocKeyGenerator
		implements DocKeyGenerator<OutwardTransDocument, String> {

	/**
	 * sgstin, docType, docNo, docDate
	 */
	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String generateKey(OutwardTransDocument doc) {

		String gstin="";
		
		String docNo = (doc.getDocNo() != null) ? doc.getDocNo().trim() : "";
		if (docNo != null && docNo.startsWith(GSTConstants.SPE_CHAR)) {
			docNo = docNo.substring(1);
		}
		String finYear = (doc.getFinYear() != null) ? doc.getFinYear().trim()
				: "";
		if(GSTConstants.I.equalsIgnoreCase(doc.getTransactionType())){
		gstin = (doc.getCgstin() != null) ? doc.getCgstin().trim() : "";
		}else{
		gstin = (doc.getSgstin() != null) ? doc.getSgstin().trim() : "";
		}
		String docType = (doc.getDocType() != null) ? doc.getDocType().trim()
				: "";
		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(gstin)
				.add(docType).add(docNo).toString();
	}

	@Override
	public String generateOrgKey(OutwardTransDocument doc) {

		String docNo = (doc.getPreceedingInvoiceNumber() != null)
				? doc.getPreceedingInvoiceNumber().trim() : null;
		if (docNo != null && docNo.startsWith(GSTConstants.SPE_CHAR)) {
			docNo = docNo.substring(1);
		}

		LocalDate orgDocDate = (doc.getPreceedingInvoiceDate() != null)
				? doc.getPreceedingInvoiceDate() : null;
		String finYear = GenUtil.getFinYear(orgDocDate);

		String sgstin = (doc.getSgstin() != null) ? doc.getSgstin().trim() : "";
		String docType =GSTConstants.INV;

		if(GSTConstants.RCR.equalsIgnoreCase(doc.getDocType())){
			docType=GSTConstants.CR;
		}
		if(GSTConstants.RDR.equalsIgnoreCase(doc.getDocType())){
			docType=GSTConstants.DR;
		}
		
		
		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(sgstin)
				.add(docType).add(docNo).toString();
	}

}
