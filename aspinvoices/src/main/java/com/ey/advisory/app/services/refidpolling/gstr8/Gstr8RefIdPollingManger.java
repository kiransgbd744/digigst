package com.ey.advisory.app.services.refidpolling.gstr8;

import org.springframework.http.ResponseEntity;

import com.ey.advisory.core.api.PollingMessage;
/**
 * 
 * @author Siva.Reddy
 *
 */
public interface Gstr8RefIdPollingManger {
	public abstract ResponseEntity<String> processGstr8RefIds(
			PollingMessage reqDto, String groupcode);

}
