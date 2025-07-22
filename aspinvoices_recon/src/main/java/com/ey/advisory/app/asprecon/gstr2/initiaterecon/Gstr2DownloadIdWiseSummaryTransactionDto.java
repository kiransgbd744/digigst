package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.core.dto.Gstr2RequesIdWiseDownloadTabDto;
import com.google.gson.annotations.Expose;

import lombok.Data;
@Data
public class Gstr2DownloadIdWiseSummaryTransactionDto {

	@Expose
	private List<Gstr2RequesIdWiseDownloadTabDto> transactionalRecords = new ArrayList<>();

	@Expose
	private List<Gstr2RequesIdWiseDownloadTabDto> summaryRecords = new ArrayList<>();
}
