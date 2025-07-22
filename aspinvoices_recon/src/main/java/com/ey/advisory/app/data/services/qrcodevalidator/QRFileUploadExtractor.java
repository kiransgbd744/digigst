package com.ey.advisory.app.data.services.qrcodevalidator;

import java.util.List;

import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFJSONResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseSummaryEntity;

public interface QRFileUploadExtractor {

	public void extractAndPopulateQRData(String fileName, String fileType,
			List<QRResponseSummaryEntity> listofSumm, String accessToken,
			String apiAccessKey, Long id, QRCountDto countDto, String docId,
			String apiUrl , String uploadType , String entityId);

	public void extractAndPopulateQRPDFData(String fileType,
			List<QRPDFResponseSummaryEntity> listofSumm, String accessToken,
			String apiAccessKey, Long id, QRCountDto countDto, String apiUrl,String pdfAccesToken ,
			String uploadType , String entityId);

	public void extractAndPopulateQRPDFJsonData(String fileType,
			List<QRPDFJSONResponseSummaryEntity> listofSumm, String accessToken,
			String apiAccessKey, Long id, QRCountDto countDto, String apiUrl, String pdfAccesToken ,
			String uploadType , String entityId);

}
