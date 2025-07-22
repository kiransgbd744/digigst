package com.ey.advisory.app.services.docs;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public interface OutwardSupplyTypeResolver {
	
	public String resolve(OutwardTransDocument document);

}
