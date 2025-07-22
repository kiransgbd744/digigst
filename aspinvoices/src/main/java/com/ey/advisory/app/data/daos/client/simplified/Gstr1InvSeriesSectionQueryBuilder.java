package com.ey.advisory.app.data.daos.client.simplified;
/**
 * 
 * @author Mahesh.Golla
 *
 */
public interface Gstr1InvSeriesSectionQueryBuilder {
	
	String createGstinViewQueryString(String buildQuery);

	String createGstinVerticalViewQueryString(String buildQuery);
	
	//gstr1a code
	String createGstr1aGstinViewQueryString(String buildQuery);

	String createGstr1aGstinVerticalViewQueryString(String buildQuery);

}
