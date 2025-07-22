package com.ey.advisory.app.inward.einvoice;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportStatusConstants;

import lombok.extern.slf4j.Slf4j;

@Component("AsyncInwardEinvoiceJsonDownloadHandler")
@Slf4j
public class AsyncInwardEinvoiceJsonDownloadHandler
		implements AsyncReportDownloadService {

	@Autowired
	private FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("AsyncJsonDownloadServiceImpl")
	AsyncJsonDownloadServiceImpl jsonServiceImpl;

	@Transactional(value = "clientTransactionManager")
	@Override
	public void generateReports(Long id) {
		
		try {
	
			String resp = jsonServiceImpl.fileDownloads(id);
			
			if("Success".equalsIgnoreCase(resp))
			{
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATED, null,
					LocalDateTime.now());
			}else
			{
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now());
			
			}
		} catch (Exception e) {
			handleReportGenerationFailure(id, e);
		}
	}

	private void handleReportGenerationFailure(Long id, Exception e) {
		fileStatusDownloadReportRepo.updateStatus(id,
				ReportStatusConstants.REPORT_GENERATION_FAILED, null,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		String msg = "Exception occurred while generating Download file";
		LOGGER.error(msg, e);
		throw new AppException(msg, e);
	}
}