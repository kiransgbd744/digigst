package com.ey.advisory.app.services.refidpolling.gstr7;

import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;

import com.ey.advisory.app.data.entities.client.Gstr7DocErrorEntity;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.JsonElement;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr7ResponseHandler {
	public Triplet<List<String>, Map<String, Gstr7DocErrorEntity>, Integer> response(
			JsonElement root, Gstr1SaveBatchEntity batch, Integer n);
}
