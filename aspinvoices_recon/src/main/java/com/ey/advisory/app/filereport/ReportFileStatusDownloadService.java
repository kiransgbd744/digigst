package com.ey.advisory.app.filereport;

import java.util.List;

import org.javatuples.Pair;

public interface ReportFileStatusDownloadService {
	
	public List<ReportDownloadDto> getDownloadData(Long requestId);

}
