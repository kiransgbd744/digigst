package com.ey.advisory.app.services.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

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
@Component("DocKeyGenerator")
public class DocKeyGenerator {

	/**
	 * sgstin, docType, docNo, docDate
	 */
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter
			.ofPattern("yyyyMMdd");
	private static final String DOC_KEY_JOINER = "|";

	public String generateKey(String sgtin, String docNo, LocalDate docDate,
			String docType) {
		String formattedDocDate = (docDate != null) ? docDate.format(FORMATTER)
				: "";

		return new StringJoiner(DOC_KEY_JOINER).add(docType).add(sgtin)
				.add(formattedDocDate).add(docNo).toString();
	}

	public String generateKey(String sgtin, String docNo, String fy,
			String docType) {

		return new StringJoiner(DOC_KEY_JOINER).add(fy).add(sgtin).add(docType)
				.add(docNo).toString();

	}

}
