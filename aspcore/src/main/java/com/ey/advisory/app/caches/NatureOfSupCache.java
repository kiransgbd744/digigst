package com.ey.advisory.app.caches;

import com.ey.advisory.admin.data.entities.master.NatureOfSupEntity;

public interface NatureOfSupCache {

	public NatureOfSupEntity findNatureOfSupp(String serialNumber);

}
