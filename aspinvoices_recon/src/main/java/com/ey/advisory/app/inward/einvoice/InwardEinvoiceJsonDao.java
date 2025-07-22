package com.ey.advisory.app.inward.einvoice;

import java.util.List;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;

public interface InwardEinvoiceJsonDao {
	List<Object> getInwardEinvoiceJsonData(
			FileStatusDownloadReportEntity criteria);
}
