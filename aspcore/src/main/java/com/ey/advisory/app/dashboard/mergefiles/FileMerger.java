package com.ey.advisory.app.dashboard.mergefiles;

import java.util.List;

public interface FileMerger {
	
	public String merge(Long id, List<FileMergeInfo> fileMergeInfoList, String returnType);

}
