package com.ey.advisory.app.services.configuremaster.reports;

import java.util.List;
import com.ey.advisory.app.data.views.client.MasterErrorRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

public interface MasterErrorReportsDao {

	public List<MasterErrorRecordsDto> getMasterError(
	        final Anx1FileStatusReportsReqDto request);
}
