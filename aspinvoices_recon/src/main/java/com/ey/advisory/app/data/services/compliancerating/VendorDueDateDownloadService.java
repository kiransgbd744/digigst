package com.ey.advisory.app.data.services.compliancerating;

import com.aspose.cells.Workbook;

/**
 * @author Jithendra.B
 *
 */
public interface VendorDueDateDownloadService {

	public Workbook getVendorDueDateData(Long entityId);
}
