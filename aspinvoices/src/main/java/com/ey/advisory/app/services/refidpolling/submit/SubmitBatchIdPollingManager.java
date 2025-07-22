/**
 * 
 */
package com.ey.advisory.app.services.refidpolling.submit;

import com.ey.advisory.app.data.entities.client.GstnSubmitEntity;
import com.ey.advisory.app.docs.dto.ReturnStatusRefIdDto;

/**
 * @author Hemasundar.J
 *
 */
public interface SubmitBatchIdPollingManager {

	public abstract ReturnStatusRefIdDto processBatch(String groupCode,
			GstnSubmitEntity batch);

}
