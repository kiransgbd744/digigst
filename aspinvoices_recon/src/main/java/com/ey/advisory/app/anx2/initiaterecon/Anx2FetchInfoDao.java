package com.ey.advisory.app.anx2.initiaterecon;

import java.util.List;

/**
 * @author Arun.KA
 *
 */
public interface Anx2FetchInfoDao {
	
	public List<Anx2FetchInfoDto> findAnx2FetchInfoByGstins(List<String> gstins,
			String taxPeriod);

}
