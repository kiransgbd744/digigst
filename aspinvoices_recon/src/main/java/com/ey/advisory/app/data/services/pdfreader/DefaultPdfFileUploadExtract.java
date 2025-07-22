package com.ey.advisory.app.data.services.pdfreader;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.pdfreader.PDFResponseLineItemEntity;
import com.ey.advisory.app.data.entities.pdfreader.PDFResponseSummaryEntity;
import com.ey.advisory.app.data.services.pdfreader.dto.PDFCountDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j

@Component("DefaultPdfFileUploadExtract")
public class DefaultPdfFileUploadExtract implements FileUploadExtractor {

	@Autowired
	@Qualifier("PdfFileUploadExtractor")
	PdfFileUploadExtractor pdfExtractor;

	@Autowired
	@Qualifier("ZipFileUploadExtractor")
	ZipFileUploadExtractor zipExtractor;

	@Override
	public void extractAndPopulatePDFData(String fileType,
			List<PDFResponseSummaryEntity> listofSumm,
			List<PDFResponseLineItemEntity> pdflistItems, Long id,
			PDFCountDto countDto, String pdfAccesToken) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"DefaultPdfFileUploadExtract : Pdf Reader Uploaded file type {}",
					fileType);
		}
		if ("pdf".equalsIgnoreCase(fileType)) {
			pdfExtractor.extractAndPopulatePDFData(fileType, listofSumm,
					pdflistItems, id, countDto, pdfAccesToken);
		} else {
			zipExtractor.extractAndPopulatePDFData(fileType, listofSumm,
					pdflistItems, id, countDto, pdfAccesToken);
		}
	}
}
