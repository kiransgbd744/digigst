package com.ey.advisory.app.services.refidpolling.gstr2x;

import org.springframework.http.ResponseEntity;

import com.ey.advisory.core.api.PollingMessage;
/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr2XRefIdPollingManger {
	
	public abstract ResponseEntity<String> processGstr2XRefIds(PollingMessage reqDto, String groupcode);
	
}
