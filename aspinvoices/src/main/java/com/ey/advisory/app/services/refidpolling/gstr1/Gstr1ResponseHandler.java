/**
 * 
 */
package com.ey.advisory.app.services.refidpolling.gstr1;

import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;

import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.JsonElement;

/**
 * @author Hemasundar.J
 *
 */
public interface Gstr1ResponseHandler {

	public Triplet<List<String>, Map<String, OutwardTransDocError>, Integer> response(
			JsonElement root, Gstr1SaveBatchEntity batch, Integer n);
	
	public Integer SaveResponse(
			JsonElement root, Gstr1SaveBatchEntity batch, Integer n);
}
