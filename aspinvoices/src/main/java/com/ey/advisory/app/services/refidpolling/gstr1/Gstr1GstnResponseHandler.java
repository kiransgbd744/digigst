/**
 * 
 */
package com.ey.advisory.app.services.refidpolling.gstr1;

import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.JsonElement;

/**
 * @author Hemasundar.J
 *
 */
public interface Gstr1GstnResponseHandler {

	public abstract void handleResponse(JsonElement root,
			Gstr1SaveBatchEntity batch);
}
