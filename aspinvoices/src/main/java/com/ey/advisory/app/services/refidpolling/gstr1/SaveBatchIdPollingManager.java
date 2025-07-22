/**
 * 
 */
package com.ey.advisory.app.services.refidpolling.gstr1;

import com.ey.advisory.app.docs.dto.ReturnStatusRefIdDto;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;

/**
 * @author Hemasundar.J
 *
 */
public interface SaveBatchIdPollingManager {
	
	public abstract ReturnStatusRefIdDto processBatch(Gstr1SaveBatchEntity batch);
}
