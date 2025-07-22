package com.ey.advisory.app.data.daos.client.gstr2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.gstr1.Gstr6SaveStatusDownloadReqDto;

@Component("Gstr6SaveStatusDownloadV2ReportHandler")
public class Gstr6SaveStatusDownloadV2ReportHandler {

	@Autowired
	@Qualifier("Gstr6SaveStatusDownloadV2ServiceImpl")
	private Gstr6SaveStatusDownloadV2ServiceImpl gstr6SaveStatusDownloadV2ServiceImpl;

	@Autowired
	@Qualifier("Gstr6aSaveStatusDownloadV2ServiceImpl")
	private Gstr6aSaveStatusDownloadV2ServiceImpl gstr6aSaveStatusDownloadV2ServiceImpl;

	public Workbook downloadGstr6SaveSectionsReport(
			List<Gstr6SaveStatusDownloadReqDto> reqDtos) {
		return gstr6SaveStatusDownloadV2ServiceImpl.fetchSaveStatus(reqDtos);
	}

	public Workbook downloadGstr6aSaveSectionsReport(
			List<Gstr6SaveStatusDownloadReqDto> reqDtos) {
		return gstr6aSaveStatusDownloadV2ServiceImpl.fetchSaveStatus(reqDtos);
	}
}
