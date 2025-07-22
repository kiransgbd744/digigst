package com.ey.advisory.app.caches;

import com.ey.advisory.admin.data.entities.master.NatureOfDocEntity;

public interface NatureOfDocCache {
	public int findDocType(String docType);

	public NatureOfDocEntity findNatureOfDoc(Integer serialNumber);

}
