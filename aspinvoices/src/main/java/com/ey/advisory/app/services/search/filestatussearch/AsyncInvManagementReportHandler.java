/**
 * 
 */
package com.ey.advisory.app.services.search.filestatussearch;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface AsyncInvManagementReportHandler {

	public void setDataToEntity(FileStatusDownloadReportEntity entity,
			EInvoiceDocSearchReqDto reqDto);

}
