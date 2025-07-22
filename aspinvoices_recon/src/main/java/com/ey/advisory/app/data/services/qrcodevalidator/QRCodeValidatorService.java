package com.ey.advisory.app.data.services.qrcodevalidator;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.data.domain.Pageable;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRCodeFileDetailsEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFJSONResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRUploadFileStatusDTO;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRUploadFileStatusEntity;
import com.ey.advisory.core.config.Config;
import com.google.gson.JsonObject;

public interface QRCodeValidatorService {

	void saveAndPersistQRReports(Long id, String fileType, String optedAns , String uploadType , String entityId);

	JsonObject reconQrAndPdf(File file, String identifier, Long id , String uploadType , String entityId);

	Workbook generateQrReport(List<QRResponseSummaryEntity> retList,
			List<QRPDFResponseSummaryEntity> qrPdfSummList,
			List<QRPDFJSONResponseSummaryEntity> qrPdfJsonRespSummList);

	Workbook generateErroredQrReport(List<QRCodeFileDetailsEntity> retList);

	Pair<List<QRUploadFileStatusDTO>, Integer> fetchQrUploadData(Long entityIdApi,Pageable pageReq);

	QrUploadStatsDto getStatsDetails(List<QRResponseSummaryEntity> retList,
			List<QRPDFResponseSummaryEntity> qrPDFRetList,List<QRPDFJSONResponseSummaryEntity> qrPdfJsonRespSummList);

	QrUploadStatsDto getConsStatsDetails(List<QRResponseSummaryEntity> retList,
			List<QRPDFResponseSummaryEntity> qrPdfList,List<QRPDFJSONResponseSummaryEntity> qrPdfJsonRespSummList,
			QRSearchParams qrSearchParams);

	Pair<List<QRResponseSummaryEntity>, Integer> getConsFilterTableList(
			QRSearchParams qrSearchParams, int pageSize, int pageNum);

	Pair<List<QRPDFResponseSummaryEntity>, Integer> getConsFilterTableListqrPdf(
			QRSearchParams qrSearchParams, int pageSize, int pageNum);

	Pair<List<QRPDFJSONResponseSummaryEntity>, Integer> getConsFilterTableListqrPdfJson(
			QRSearchParams qrSearchParams, int pageSize, int pageNum);

	Pair<List<QRResponseSummaryEntity>, Integer> getViewFilterLisDtls(
			Long fileId, int pageSize, int pageNum);

	Pair<List<QRPDFResponseSummaryEntity>, Integer> getqrPdfViewFilterLisDtls(
			Long fileId, int pageSize, int pageNum);

	Pair<List<QRPDFJSONResponseSummaryEntity>, Integer> getqrPdfJsonViewFilterLisDtls(
			Long fileId, int pageSize, int pageNum);

	List<QRResponseSummaryEntity> getConsFilterList(
			QRSearchParams qrSearchParams);

	List<QRPDFResponseSummaryEntity> getConsFilterListqrPDF(
			QRSearchParams qrSearchParams);
	
	List<QRPDFJSONResponseSummaryEntity> getConsFilterListqrPDFJson(
			QRSearchParams qrSearchParams);

	List<QRGstinDto> getListOfRecipientGstin(String entityId);

	List<QRGstinDto> getListOfQRVendorPan(String entityId);

	List<QRGstinDto> getListOfVendorGstin(List<String> vendorPans,
			String entityId);

	void setMatchandMisMatchCount(List<QRResponseSummaryEntity> summList);

	Long setEntityId(Object entity, String buyerGstin, Long fileId);
	
	public void revIntegrateQRData(List<Long> activeIds,
			Map<String, Config> configMap, File tempDir);
}
