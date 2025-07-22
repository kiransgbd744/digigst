package com.ey.advisory.app.data.services.compliancerating;

/**
 * @author Jithendra.B
 *
 */
public interface VendorDueDateUploadService {

	public void validateVendorDueDateFile(Long fileId, String fileName,
			String folderName, Long entityId);

}
