package com.ey.advisory.app.services.refidpolling.itc04;

import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.JsonElement;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Itc04GstnResponseHandler {
	
	public abstract void phandleResponse(JsonElement root,
			Gstr1SaveBatchEntity batch);

	public abstract void pEhandleResponse(JsonElement root,
			Gstr1SaveBatchEntity batch);

	public abstract void ehandleResponse(JsonElement root,
			Gstr1SaveBatchEntity batch);
}
