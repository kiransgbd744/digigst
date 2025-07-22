package com.ey.advisory.app.services.jobs.gstr2a;

import org.springframework.http.ResponseEntity;

/**
 * 
 * @author Santosh.Gururaj
 *
 */


public interface Gstr2aIsdIsdaInvoicesAtGstn {
	
		public ResponseEntity<String> findIsdIsdaFromGstn(final String jsonReq,
				final String groupCode, final String type);
	
}

