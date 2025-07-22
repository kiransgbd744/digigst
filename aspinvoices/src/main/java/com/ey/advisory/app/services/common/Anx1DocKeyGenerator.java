package com.ey.advisory.app.services.common;

import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

/**
 * @author Siva.Nandam
 *
 */
@Component("Anx1DocKeyGenerator")
public class Anx1DocKeyGenerator {

	/**
	 * sgstin, docType, docNo, fi
	 */
	
	private static final String DOC_KEY_JOINER = "|";

	public String generateKey(String sgtin, String docNo, String fy,
			String docType) {
		
		return new StringJoiner(DOC_KEY_JOINER).add(fy).add(sgtin)
				.add(docType).add(docNo).toString();
		
		
	}

}
