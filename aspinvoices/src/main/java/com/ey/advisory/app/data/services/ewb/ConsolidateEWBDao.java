/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.util.List;

import com.ey.advisory.app.docs.dto.einvoice.ConsolidatedEWBResponse;
import com.ey.advisory.core.dto.ConsolidatedEWBRequest;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface ConsolidateEWBDao {

	List<ConsolidatedEWBResponse> getCEWBDetails(
			ConsolidatedEWBRequest request);

}
