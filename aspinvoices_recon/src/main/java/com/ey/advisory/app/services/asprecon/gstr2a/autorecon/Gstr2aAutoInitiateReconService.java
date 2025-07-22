package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import org.springframework.stereotype.Component;

/**
 * @author Saif.S
 *
 */
@Component("Gstr2aAutoInitiateReconService")
public interface Gstr2aAutoInitiateReconService {

	void initiateAutoRecon(String gstin, Long entityId, Long autoReconId,
			Long configId, String retType);
}
