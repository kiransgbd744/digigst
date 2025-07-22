package com.ey.advisory.app.services.docs.gstr7;

import java.time.LocalDate;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocErrHeaderEntity;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Component("Gstr7TransErrorDocKeyGenerator")
public class Gstr7TransErrorDocKeyGenerator
		implements DocKeyGenerator<Gstr7TransDocErrHeaderEntity, String> {

	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String generateKey(Gstr7TransDocErrHeaderEntity doc) {

		String docType = (doc.getDocType() != null) ? doc.getDocType().trim()
				: "";
		String docNo = (doc.getDocNum() != null) ? doc.getDocNum().trim() : "";

		if (docNo != null && docNo.startsWith(GSTConstants.SPE_CHAR)) {
			docNo = docNo.substring(1);
		}

		String deductorGstin = (doc.getDeductorGstin() != null)
				? doc.getDeductorGstin().trim() : "";

		LocalDate docDate = LocalDate.parse(EYDateUtil.fmtDateOnly(
				doc.getDocDate(), DateUtil.SUPPORTED_DATE_FORMAT1,
				DateUtil.SUPPORTED_DATE_FORMAT1));

		String finYear = GenUtil.getFinYear(docDate);

		return new StringJoiner(DOC_KEY_JOINER).add(docType).add(deductorGstin)
				.add(finYear).add(docNo).toString();

	}

	@Override
	public String generateOrgKey(Gstr7TransDocErrHeaderEntity doc) {

		String docType = (doc.getDocType() != null) ? doc.getDocType().trim()
				: "";
		String docNo = (doc.getOriginalDocNum() != null)
				? doc.getOriginalDocNum().trim() : "";

		if (docNo != null && docNo.startsWith(GSTConstants.SPE_CHAR)) {
			docNo = docNo.substring(1);
		}

		String deductorGstin = (doc.getDeductorGstin() != null)
				? doc.getDeductorGstin().trim() : "";

		LocalDate orgDocDate = LocalDate.parse(EYDateUtil.fmtDateOnly(
				doc.getOriginalDocDate(), DateUtil.SUPPORTED_DATE_FORMAT1,
				DateUtil.SUPPORTED_DATE_FORMAT1));

		String finYear = GenUtil.getFinYear(orgDocDate);

		return new StringJoiner(DOC_KEY_JOINER).add(docType).add(deductorGstin)
				.add(finYear).add(docNo).toString();
	}

}
