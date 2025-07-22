package com.ey.advisory.app.services.jobs.erp;

import org.springframework.http.ResponseEntity;

/**
 * 
 * @author Hemasundar.J
 *
 */
@FunctionalInterface
public interface SaveToGstnRI {

	public ResponseEntity<String> getTaxDocsForRevIntegration();
}
