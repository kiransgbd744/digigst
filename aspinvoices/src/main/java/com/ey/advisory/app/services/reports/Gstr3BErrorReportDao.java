package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Gstr3bdownloadErrorDto;
import com.ey.advisory.app.data.views.client.Gstr3bdownloadProcessedDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

public interface Gstr3BErrorReportDao {

	List<Gstr3bdownloadProcessedDto> generateErrorCsv(Anx1FileStatusReportsReqDto request);

}
