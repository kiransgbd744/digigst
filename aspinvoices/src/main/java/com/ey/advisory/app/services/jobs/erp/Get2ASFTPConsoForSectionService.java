package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import com.ey.advisory.app.data.views.client.ConsolidatedGstr2ADto;

public interface Get2ASFTPConsoForSectionService {

	public List<ConsolidatedGstr2ADto> getConsolidatedItemList(
			String sectionName,
			List<Object[]> finalOjectList);

	public String createSftpFileName(String gstin, String index, String total, String taxPeriod, String section);
	
	public Integer uploadToSftpServer(String filename,
			List<ConsolidatedGstr2ADto> reconGetDataList);
}
