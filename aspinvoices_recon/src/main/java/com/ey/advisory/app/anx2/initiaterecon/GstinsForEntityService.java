package com.ey.advisory.app.anx2.initiaterecon;

import java.util.List;

/**
 * @author Arun.KA
 *
 */
public interface GstinsForEntityService {

	public List<InitiateReconFetchGstinsInfoDto> getGstinsInfo(long entityId,
			String taxPeriod);

}
