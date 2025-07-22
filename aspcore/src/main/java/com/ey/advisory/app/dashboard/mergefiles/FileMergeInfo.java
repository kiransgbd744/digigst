package com.ey.advisory.app.dashboard.mergefiles;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FileMergeInfo {
	
	private final List<FileInfo> fileInfoList;
	private final String outputFileName;
	
	
	public FileMergeInfo(List<FileInfo> fileInfoList, String outputFileName) {
		super();
		this.fileInfoList = fileInfoList;
		this.outputFileName = outputFileName;
	}
	
	

}
