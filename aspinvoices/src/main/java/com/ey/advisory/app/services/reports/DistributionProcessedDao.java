package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.DistributionProcessedDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

public interface DistributionProcessedDao {

	List<DistributionProcessedDto> generateProcessedCsv(Anx1FileStatusReportsReqDto request);

}
