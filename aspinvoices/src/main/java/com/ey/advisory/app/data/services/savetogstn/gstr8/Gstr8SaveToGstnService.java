package com.ey.advisory.app.data.services.savetogstn.gstr8;

import org.springframework.http.ResponseEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */

public interface Gstr8SaveToGstnService {

	public ResponseEntity<String> saveGstr8DataToGstn(String requestBody);
	
	public ResponseEntity<String> getGstr8Summary(String requestBody);
}
