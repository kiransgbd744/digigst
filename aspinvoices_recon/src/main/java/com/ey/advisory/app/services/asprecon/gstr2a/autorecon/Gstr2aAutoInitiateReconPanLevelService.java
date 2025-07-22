package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import org.springframework.stereotype.Component;

/**
 * @author Saif.S
 *
 */
@Component("Gstr2aAutoInitiateReconPanLevelService")
public interface Gstr2aAutoInitiateReconPanLevelService {

	void initiateAutoReconPanLevel(Long entityId,
			Long configId, String retType);
}
