package com.ey.advisory.app.gstr2.reconresults.filter;

import java.util.List;

public interface Gstr2ReconResponseUploadDao {

	public List<Long> getReconLinkIds(
			Gstr2ReconResultsReqDto dto);

}
