package com.ey.advisory.app.services.refidpolling.gstr6;

import org.springframework.http.ResponseEntity;

import com.ey.advisory.core.api.PollingMessage;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr6RefIdPollingManger {

	public abstract ResponseEntity<String> processGstr6RefIds(
			PollingMessage reqDto, String groupcode);
}
