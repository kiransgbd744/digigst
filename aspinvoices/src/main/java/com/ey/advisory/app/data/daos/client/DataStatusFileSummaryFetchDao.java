package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.docs.dto.anx1.DataStatusFilesummaryReqDto;

public interface DataStatusFileSummaryFetchDao {
	public List<Object[]> fecthOutwardRawFileData(
			DataStatusFilesummaryReqDto summaryRequest);

	public List<Object[]> fecthInwardRawFileData(
			DataStatusFilesummaryReqDto summaryRequest);

	public List<Object[]> fetchOutwardB2cData(
			DataStatusFilesummaryReqDto summaryRequest);

	public List<Object[]> fetchOutwardTable4Data(
			DataStatusFilesummaryReqDto summaryRequest);

	public List<Object[]> fecthInwardTable3h3iFileData(
			DataStatusFilesummaryReqDto summaryRequest);

	public List<Object[]> fecthOthersRet1And1aFileData(
			DataStatusFilesummaryReqDto summaryRequest);

	public List<Object[]> fecthOthersInterestFileData(
			DataStatusFilesummaryReqDto summaryRequest);

	public List<Object[]> fecthOthersSetOffAndUtilFileData(
			DataStatusFilesummaryReqDto summaryRequest);

	public List<Object[]> fecthOthersRefundsFileData(
			DataStatusFilesummaryReqDto summaryRequest);

	public List<Object[]> fecthOutwardRaw109FileData(
			DataStatusFilesummaryReqDto summaryRequest);
}
