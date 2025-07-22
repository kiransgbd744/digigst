package com.ey.advisory.app.data.services.qrcodevalidator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFJSONResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseSummaryEntity;

@Component("DefaultQRFileUploadExtract")
public class DefaultQRFileUploadExtract implements QRFileUploadExtractor {

	@Autowired
	@Qualifier("QRPdfFileUploadExtractor")
	QRFileUploadExtractor pdfExtractor;

	@Autowired
	@Qualifier("QRZipFileUploadExtractor")
	QRFileUploadExtractor zipExtractor;

	@Override
	public void extractAndPopulateQRData(String fileName, String fileType,
			List<QRResponseSummaryEntity> listofSumm, String accessToken,
			String apiAccessKey, Long id, QRCountDto countDto, String docId,
			String apiUrl , String uploadType , String entityId) {

		if ("pdf".equalsIgnoreCase(fileType)) {
			pdfExtractor.extractAndPopulateQRData(fileName, fileType,
					listofSumm, accessToken, apiAccessKey, id, countDto, docId,
					apiUrl , uploadType , entityId);
		} else {
			zipExtractor.extractAndPopulateQRData(fileName, fileType,
					listofSumm, accessToken, apiAccessKey, id, countDto, docId,
					apiUrl , uploadType , entityId);
		}
	}

	@Override
	public void extractAndPopulateQRPDFData(String fileType,
			List<QRPDFResponseSummaryEntity> listofSumm, String accessToken,
			String apiAccessKey, Long id, QRCountDto countDto, String apiUrl, String pdfAccesToken , 
			String uploadType , String entityId) {
		if ("pdf".equalsIgnoreCase(fileType)) {
			pdfExtractor.extractAndPopulateQRPDFData(fileType, listofSumm,
					accessToken, apiAccessKey, id, countDto, apiUrl,pdfAccesToken , uploadType , entityId);
		} else {
			zipExtractor.extractAndPopulateQRPDFData(fileType, listofSumm,
					accessToken, apiAccessKey, id, countDto, apiUrl,pdfAccesToken, uploadType , entityId);
		}
	}

	@Override
	public void extractAndPopulateQRPDFJsonData(String fileType,
			List<QRPDFJSONResponseSummaryEntity> listofSumm, String accessToken,
			String apiAccessKey, Long id, QRCountDto countDto, String apiUrl, String pdfAccesToken , String uploadType , String entityId) {
		if ("pdf".equalsIgnoreCase(fileType)) {
			pdfExtractor.extractAndPopulateQRPDFJsonData(fileType, listofSumm,
					accessToken, apiAccessKey, id, countDto, apiUrl,pdfAccesToken , uploadType , entityId);
		} else {
			zipExtractor.extractAndPopulateQRPDFJsonData(fileType, listofSumm,
					accessToken, apiAccessKey, id, countDto, apiUrl,pdfAccesToken , uploadType , entityId);
		}
	}

}
