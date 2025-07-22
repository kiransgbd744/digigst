package com.ey.advisory.app.data.services.pdfreader;

import java.util.List;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.pdfreader.PDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.pdfreader.PDFUploadFileStatusEntity;
import com.ey.advisory.app.data.services.pdfreader.dto.PDFUploadFileStatusDto;

public interface PDFReaderService {
	void saveAndPersistPdfSummary(Long id, String fileType);
	
	Workbook generatePdfReaderReport(List<PDFResponseSummaryEntity> pdfReaderList);
	
	List<PDFUploadFileStatusDto> fetchPdfUploadData(String entityIdApi);
}
