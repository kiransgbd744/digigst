package com.ey.advisory.app.services.refidpolling.gstr6;

import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;

import com.ey.advisory.app.data.entities.client.Gstr6VerticalWebError;
import com.ey.advisory.app.data.entities.client.InwardTransDocError;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.JsonElement;

/**
 * 
 * @author SriBhavya
 *
 */

public interface Gstr6ResponseHandler {
	public Triplet<List<String>, Map<String, InwardTransDocError>, Integer> response(
			JsonElement root, Gstr1SaveBatchEntity batch, Integer n);
	
	public Triplet<List<String>, Map<String, Gstr6VerticalWebError>, Integer> isdResponse(
			JsonElement root, Gstr1SaveBatchEntity batch, Integer n);

}
