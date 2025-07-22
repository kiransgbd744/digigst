package com.ey.advisory.app.services.refidpolling.gstr8;

import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;

import com.ey.advisory.app.data.entities.gstr8.Gstr8DocErrorEntity;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.JsonElement;

/**
 * 
 * @author Siva.Reddy
 *
 */
public interface Gstr8ResponseHandler {
	public Triplet<List<String>, Map<String, Gstr8DocErrorEntity>, Integer> response(
			JsonElement root, Gstr1SaveBatchEntity batch, Integer n);
}
