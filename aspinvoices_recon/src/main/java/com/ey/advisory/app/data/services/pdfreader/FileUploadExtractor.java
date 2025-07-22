package com.ey.advisory.app.data.services.pdfreader;

import java.util.List;

import com.ey.advisory.app.data.entities.pdfreader.PDFResponseLineItemEntity;
import com.ey.advisory.app.data.entities.pdfreader.PDFResponseSummaryEntity;
import com.ey.advisory.app.data.services.pdfreader.dto.PDFCountDto;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCountDto;

public interface FileUploadExtractor {

	public void extractAndPopulatePDFData(String fileType,
			List<PDFResponseSummaryEntity> listofSumm,
			List<PDFResponseLineItemEntity> pdflistItems, Long id,
			PDFCountDto countDto, String pdfAccesToken);

}
