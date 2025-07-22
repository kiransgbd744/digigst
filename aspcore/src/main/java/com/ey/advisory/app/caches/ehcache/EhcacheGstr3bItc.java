package com.ey.advisory.app.caches.ehcache;

import com.ey.advisory.admin.data.entities.master.Gstr3BItcEntityMaster;

/**
 * 
 * @author Mahesh.Golla
 *
 */
public interface EhcacheGstr3bItc {
	public int findDesCription(String description);

	public Gstr3BItcEntityMaster findGstr3Bitc(Integer serialNumber);

}
