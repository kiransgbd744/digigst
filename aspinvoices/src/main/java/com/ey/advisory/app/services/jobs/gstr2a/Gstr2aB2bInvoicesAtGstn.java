package com.ey.advisory.app.services.jobs.gstr2a;

import org.springframework.http.ResponseEntity;
/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Gstr2aB2bInvoicesAtGstn {

	public ResponseEntity<String> findB2bFromGstn(String jsonReq,
			String groupCode, String type);

}
