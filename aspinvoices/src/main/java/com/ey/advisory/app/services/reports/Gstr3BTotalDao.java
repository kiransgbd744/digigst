package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Gstr3bdownloadProcessedDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

public interface Gstr3BTotalDao {

	List<Gstr3bdownloadProcessedDto> generateTotalCsv(Anx1FileStatusReportsReqDto request);

}
