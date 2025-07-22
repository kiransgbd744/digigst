package com.ey.advisory.controller.gstr2;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2RequesIdWiseDownloadTabDto;

public interface Gstr2RequestIdWiseSummaryTabService {

	public List<Gstr2RequesIdWiseDownloadTabDto> getSummaryDownloadData(Long configId, 
			String reconType);
}
