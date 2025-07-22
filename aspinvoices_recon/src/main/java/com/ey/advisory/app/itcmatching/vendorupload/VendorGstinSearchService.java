package com.ey.advisory.app.itcmatching.vendorupload;

import java.util.List;

import org.javatuples.Pair;

import com.aspose.cells.Workbook;

public interface VendorGstinSearchService {

	public GstinSearchResponse getVendorGstinSearchResult(
			List<String> einvoiceApplicabilityList,
			List<String> gstinStatusList, Long entityId,
			String statusNotUpdatedInLastDays,
			int pageSize, int pageNum);

}
