/**
 * 
 */
package com.ey.advisory.app.services.refidpolling.gstr1a;

import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;

import com.ey.advisory.app.data.gstr1A.entities.client.OutwardTransDocErrorGstr1A;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.JsonElement;

/**
 * @author Siva.Reddy
 *
 */
public interface Gstr1AResponseHandler {

	public Triplet<List<String>, Map<String, OutwardTransDocErrorGstr1A>, Integer> response(
			JsonElement root, Gstr1SaveBatchEntity batch, Integer n);
	
	public Integer SaveResponse(
			JsonElement root, Gstr1SaveBatchEntity batch, Integer n);
}
