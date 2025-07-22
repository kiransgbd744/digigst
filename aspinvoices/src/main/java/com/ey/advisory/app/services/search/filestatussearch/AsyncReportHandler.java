package com.ey.advisory.app.services.search.filestatussearch;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;

public interface AsyncReportHandler {
	
	public void setDataToEntity(FileStatusDownloadReportEntity entity, 
			FileStatusReportDto reqDto);

}
