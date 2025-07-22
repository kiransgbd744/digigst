package com.ey.advisory.app.services.docs;

import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.ey.advisory.common.GSTConstants;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Component("DefaultOutwardTransDocErrorKeyGenerator")
public class DefaultOutwardTransDocErrorKeyGenerator
		implements DocKeyGenerator<Anx1OutWardErrHeader, String> {

	/**
	 * sgstin, docType, docNo, docDate
	 */

	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String generateKey(Anx1OutWardErrHeader doc) {

		String docNo = (doc.getDocNo() != null
				&& !doc.getDocNo().trim().isEmpty()) ? doc.getDocNo().trim()
						: "";
		if (docNo != null && docNo.startsWith(GSTConstants.SPE_CHAR)) {
			docNo = docNo.substring(1);
		}
		String finYear = (doc.getFinYear() != null
				&& !doc.getFinYear().trim().isEmpty()) ? doc.getFinYear().trim()
						: "";
		String sgstin = (doc.getSgstin() != null
				&& !doc.getSgstin().trim().isEmpty()) ? doc.getSgstin().trim()
						: "";
		String docType = (doc.getDocType() != null
				&& !doc.getDocType().trim().isEmpty()) ? doc.getDocType().trim()
						: "";

		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(sgstin)
				.add(docType).add(docNo).toString();
	}

	@Override
	public String generateOrgKey(Anx1OutWardErrHeader doc) {
		// TODO Auto-generated method stub
		return null;
	}

}
