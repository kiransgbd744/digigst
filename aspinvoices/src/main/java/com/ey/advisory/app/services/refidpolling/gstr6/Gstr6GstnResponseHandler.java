package com.ey.advisory.app.services.refidpolling.gstr6;

import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.JsonElement;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr6GstnResponseHandler {
	
	public abstract void phandleResponse(JsonElement root,
			Gstr1SaveBatchEntity batch);

	public abstract void pEhandleResponse(JsonElement root,
			Gstr1SaveBatchEntity batch);

	public abstract void ehandleResponse(JsonElement root,
			Gstr1SaveBatchEntity batch);

}
