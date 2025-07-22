package com.ey.advisory.app.services.refidpolling.itc04;

import org.springframework.http.ResponseEntity;

import com.ey.advisory.core.api.PollingMessage;

/**
 * 
 * @author Sri Bhavya
 *
 */
public interface Itc04RefIdPollingManger {
	public abstract ResponseEntity<String> processItc04RefIds(
			PollingMessage reqDto, String groupcode);
}
