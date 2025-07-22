package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.service.gstr3bSummary.Gstr3bSummaryUploadDto;

public interface Gstr3bSummaryDao {

	public List<Gstr3bSummaryUploadDto> getTotalRecords(Integer fileId);

}
