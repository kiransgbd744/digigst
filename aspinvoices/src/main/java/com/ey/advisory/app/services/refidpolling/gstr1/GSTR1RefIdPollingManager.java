package com.ey.advisory.app.services.refidpolling.gstr1;

import org.springframework.http.ResponseEntity;

import com.ey.advisory.core.api.PollingMessage;

public interface GSTR1RefIdPollingManager {
	
	public abstract ResponseEntity<String> processGstr1RefIds(PollingMessage reqDto, String groupcode);
	public abstract ResponseEntity<String> processAnx1RefIds(PollingMessage reqDto, String groupcode);

}
