package com.ey.advisory.app.services.docs.gstr2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.google.common.base.Strings;

@Component("DefaultInwardTransDocKeyGenerator")
public class DefaultInwardTransDocKeyGenerator
		implements DocKeyGenerator<InwardTransDocument, String> {

	/**
	 * sgstin, docType, docNo, docDate,cgstin
	 */
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter
			.ofPattern("yyyyMMdd");
	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String generateKey(InwardTransDocument doc) {

		String docNo = (doc.getDocNo() != null) ? doc.getDocNo().trim() : "";
		if (docNo != null && docNo.startsWith(GSTConstants.SPE_CHAR)) {
			docNo = docNo.substring(1);
		}
		String finYear = (doc.getFinYear() != null) ? doc.getFinYear().trim()
				: "";
		String sgstin = (doc.getSgstin() != null) ? doc.getSgstin().trim() : "";
		String docType = (doc.getDocType() != null) ? doc.getDocType().trim()
				: "";
		String cgstin = (doc.getCgstin() != null) ? doc.getCgstin().trim() : "";
		if (!Strings.isNullOrEmpty(sgstin)) {

			return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
					.add(sgstin).add(docType).add(docNo).toString();
		} else {
			String suppName = (doc.getCustOrSuppName() != null
					&& !doc.getCustOrSuppName().toString().isEmpty())
							? StringUtils.truncate(String
									.valueOf(doc.getCustOrSuppName()).trim(),
									25)
							: "";

			return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
					.add(suppName).add(docType).add(docNo).toString();
		}
	}

	@Override
	public String generateOrgKey(InwardTransDocument doc) {

		String docNo = (doc.getOrigDocNo() != null) ? doc.getOrigDocNo().trim()
				: null;
		if (docNo != null && docNo.startsWith(GSTConstants.SPE_CHAR)) {
			docNo = docNo.substring(1);
		}

		LocalDate orgDocDate = (doc.getOrigDocDate() != null)
				? doc.getOrigDocDate() : null;
		String finYear = GenUtil.getFinYear(orgDocDate);

		String sgstin = (doc.getSgstin() != null) ? doc.getSgstin().trim() : "";
		String docType = GSTConstants.INV;

		if (GSTConstants.RCR.equalsIgnoreCase(doc.getDocType())) {
			docType = GSTConstants.CR;
		}
		if (GSTConstants.RDR.equalsIgnoreCase(doc.getDocType())) {
			docType = GSTConstants.DR;
		}
		String cgstin = (doc.getCgstin() != null) ? doc.getCgstin().trim() : "";

		if (!Strings.isNullOrEmpty(sgstin)) {
			return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
					.add(sgstin).add(docType).add(docNo).toString();
		} else {
			String suppName = (doc.getCustOrSuppName() != null
					&& !doc.getCustOrSuppName().toString().isEmpty())
							? StringUtils.truncate(String
									.valueOf(doc.getCustOrSuppName()).trim(),
									25)
							: "";

			return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
					.add(suppName).add(docType).add(docNo).toString();
		}
	}

}
