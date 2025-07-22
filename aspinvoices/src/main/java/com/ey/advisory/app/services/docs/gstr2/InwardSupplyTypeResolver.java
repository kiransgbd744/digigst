package com.ey.advisory.app.services.docs.gstr2;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;

public interface InwardSupplyTypeResolver {

	public String resolve(InwardTransDocument document);
}
