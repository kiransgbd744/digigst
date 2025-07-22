package com.ey.advisory.app.dashboard.mergefiles;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FileMergeOutput {
	
	private final String outputDirPath; 
	private final List<FileMergeInfo> fileMergeInfoList;
	
	
	public FileMergeOutput(String outputDirPath,
			List<FileMergeInfo> fileMergeInfoList) {
		super();
		this.outputDirPath = outputDirPath;
		this.fileMergeInfoList = fileMergeInfoList;
	}
	
	
	

}
