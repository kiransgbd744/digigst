
package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.util.List;

import com.ey.advisory.app.gstr2.recon.summary.Gstr2Config2bprDto;

public interface Gstr2ReconResponseConfigListService {

	public List<Gstr2Config2bprDto> getAllNonAPConfigId(String entityId,
			Integer fromTaxPeriodInt, Integer toTaxPeriodInt, String reconType);

}
