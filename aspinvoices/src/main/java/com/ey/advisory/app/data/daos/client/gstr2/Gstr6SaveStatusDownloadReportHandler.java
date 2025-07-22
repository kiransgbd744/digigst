package com.ey.advisory.app.data.daos.client.gstr2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.gstr1.Gstr6SaveStatusDownloadReqDto;

@Component("Gstr6SaveStatusDownloadReportHandler")
public class Gstr6SaveStatusDownloadReportHandler {

	@Autowired
	@Qualifier("Gstr6SaveStatusDownloadServiceImpl")
	private Gstr6SaveStatusDownloadServiceImpl gstr6SaveStatusDownloadServiceImpl;

	public Workbook downloadGstr6SaveSectionsReport(
			List<Gstr6SaveStatusDownloadReqDto> reqDtos) {
		return gstr6SaveStatusDownloadServiceImpl.fetchSaveStatus(reqDtos);
	}

}
