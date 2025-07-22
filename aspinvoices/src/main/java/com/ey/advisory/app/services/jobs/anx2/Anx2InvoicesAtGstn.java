package com.ey.advisory.app.services.jobs.anx2;

import org.springframework.http.ResponseEntity;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Anx2InvoicesAtGstn {

	public ResponseEntity<String> findInvFromGstn(String jsonReq,
			String groupCode, String type);
}
