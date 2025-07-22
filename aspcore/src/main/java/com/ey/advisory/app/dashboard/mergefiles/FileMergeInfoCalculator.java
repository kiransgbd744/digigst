package com.ey.advisory.app.dashboard.mergefiles;

import java.util.List;

public interface FileMergeInfoCalculator {

	List<FileMergeInfo> calculator(Long requestId,
			List<FileMergeInput> requestData);

	List<FileMergeInfo> calculatorGSTR3B(Long id,
			List<FileMergeInput> fileMergeInputList);

	List<FileMergeInfo> calculatorGSTR9(
			List<FileMergeInput> fileMergeInputList);

	List<FileMergeInfo> calculatorGSTR7(
			List<FileMergeInput> fileMergeInputList);

	public List<FileMergeInfo> calculatorGSTR2X(
			List<FileMergeInput> fileMergeInputList);

	List<FileMergeInfo> calculatorITC04(
			List<FileMergeInput> fileMergeInputList);
	
	public List<FileMergeInfo> calculatorGSTR6(
			List<FileMergeInput> fileMergeInputList);

	List<FileMergeInfo> calculatorGSTR8(List<FileMergeInput> requestData);
}
