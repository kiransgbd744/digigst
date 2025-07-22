package com.ey.advisory.app.data.daos.client.simplified;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr1AdvancedSectionQueryBuilder {

	// Vertical view queries
	/*
	 * String createATVerticalQueryString(String buildQuery); String
	 * createATAVerticalQueryString(String buildQuery); String
	 * createTXPDVerticalQueryString(String buildQuery); String
	 * createTXPDAVerticalQueryString(String buildQuery);
	 */

	String createVeriticalQueryString(String buildQuery, String docType);

	String createGstinViewQueryString(String buildQuery, String buildTransQuery,
			String docType);

	String createSummaryQueryString(String buildVertiQuery,
			String buildTransQuery, String docType);
	
	//gstr1a code
	String createGstr1aVeriticalQueryString(String buildQuery, String docType);

	String createGstr1aGstinViewQueryString(String buildQuery, String buildTransQuery,
			String docType);

	String createGstr1aSummaryQueryString(String buildVertiQuery,
			String buildTransQuery, String docType);

}
