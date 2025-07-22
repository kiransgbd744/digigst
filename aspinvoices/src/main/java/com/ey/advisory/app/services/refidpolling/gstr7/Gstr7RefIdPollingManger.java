package com.ey.advisory.app.services.refidpolling.gstr7;

import org.springframework.http.ResponseEntity;

import com.ey.advisory.core.api.PollingMessage;
/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr7RefIdPollingManger {
	public abstract ResponseEntity<String> processGstr7RefIds(
			PollingMessage reqDto, String groupcode);

}
