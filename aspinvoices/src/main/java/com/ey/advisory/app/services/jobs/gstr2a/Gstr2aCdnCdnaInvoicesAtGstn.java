package com.ey.advisory.app.services.jobs.gstr2a;

import org.springframework.http.ResponseEntity;

public interface Gstr2aCdnCdnaInvoicesAtGstn {

	public ResponseEntity<String> findCDNFromGstn(final String jsonReq,
			final String groupCode, final String type);
}
