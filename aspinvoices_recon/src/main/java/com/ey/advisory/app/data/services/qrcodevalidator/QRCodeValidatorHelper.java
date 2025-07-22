
package com.ey.advisory.app.data.services.qrcodevalidator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFJSONResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseSummaryEntity;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRUploadFileStatusRepo;
import com.ey.advisory.common.QRCodeValidatorConstants;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Component
@Slf4j
public class QRCodeValidatorHelper {

	public static final String UnAvailable = "UnAvailable";
	public static final String Available = "Available";

	@Autowired
	QRUploadFileStatusRepo qrUploadFileStatusRepo;

	public <T> QRCountDto extractCountDetails(List<T> respEntityList,
			QRSearchParams qrSearchParams) {
		int qrCnt = 0;
		int sigMisCnt = 0;
		int fullMatchCnt = 0;
		int errCnt = 0;
		QRCountDto countDto = new QRCountDto();

		for (T respEntity : respEntityList) {
			if (respEntity instanceof QRResponseSummaryEntity) {
				QRResponseSummaryEntity qrSummEntity = (QRResponseSummaryEntity) respEntity;
				if (!Strings.isNullOrEmpty(qrSummEntity.getIrn())) {
					qrCnt++;
				}
				if (Strings.isNullOrEmpty(qrSummEntity.getSignature())
						|| "InValid".equalsIgnoreCase(
								qrSummEntity.getSignature())) {
					sigMisCnt++;
				}
				boolean isMatch = QRCommonUtility.isFullMatch(qrSummEntity);
				if (isMatch) {
					fullMatchCnt++;
				}
			} else if (respEntity instanceof QRPDFResponseSummaryEntity) {
				QRPDFResponseSummaryEntity qrPdfSummEntity = (QRPDFResponseSummaryEntity) respEntity;
				if (qrPdfSummEntity.getValidatedDate() != null) {
					qrCnt++;
				}
				if (Strings.isNullOrEmpty(qrPdfSummEntity.getSignatureQR())
						|| "InValid".equalsIgnoreCase(
								qrPdfSummEntity.getSignatureQR())) {
					sigMisCnt++;
				}
				if (qrPdfSummEntity.getIsFullMatch()) {
					fullMatchCnt++;
				}
			} else if (respEntity instanceof QRPDFJSONResponseSummaryEntity) {
				QRPDFJSONResponseSummaryEntity qrPdfJsonSummEntity = (QRPDFJSONResponseSummaryEntity) respEntity;
				// Validate Date is null is added because we will have
				// considered the Processed with QR Cnt if get Valid Response
				// from PDF.
				if (qrPdfJsonSummEntity.getValidatedDate() != null) {
					qrCnt++;
				}
				if (Strings.isNullOrEmpty(qrPdfJsonSummEntity.getSignatureQR())
						|| "InValid".equalsIgnoreCase(
								qrPdfJsonSummEntity.getSignatureQR())) {
					sigMisCnt++;
				}
				if (qrPdfJsonSummEntity.getIsFullMatch()) {
					fullMatchCnt++;
				}
			}
		}

		if (!respEntityList.isEmpty()) {
			List<Long> fileIds = respEntityList.stream().map(entity -> {
				if (entity instanceof QRResponseSummaryEntity) {
					return ((QRResponseSummaryEntity) entity).getFileId();
				} else if (entity instanceof QRPDFResponseSummaryEntity) {
					return ((QRPDFResponseSummaryEntity) entity).getFileId();
				} else if (entity instanceof QRPDFJSONResponseSummaryEntity) {
					return ((QRPDFJSONResponseSummaryEntity) entity)
							.getFileId();
				}
				return null;
			}).filter(Objects::nonNull).collect(Collectors.toList());

			errCnt = qrUploadFileStatusRepo.getErrDocCount(fileIds);
		}

		if (qrSearchParams == null) {
			countDto.setErrCnt(errCnt);
		} else {
			boolean isErrorCntValid = !qrSearchParams.getRecipientGstins()
					.isEmpty() || !qrSearchParams.getVendorGstin().isEmpty()
					|| !qrSearchParams.getRecordStatus().isEmpty();
			if (isErrorCntValid) {
				errCnt = 0;
				countDto.setErrCnt(0);
			} else {
				countDto.setErrCnt(errCnt);
			}
		}

		countDto.setTotalDocCnt(respEntityList.size() + errCnt);
		countDto.setQrCnt(qrCnt);
		countDto.setSigMisCnt(sigMisCnt);
		countDto.setFullMatCnt(fullMatchCnt);
		LOGGER.debug("extracted count is {} ", countDto);
		return countDto;
	}

	public List<QRGstnPanStats> extractGstnPanWiseStats(
			List<QRResponseSummaryEntity> summList, boolean isGstin) {

		List<QRGstnPanStats> tableStats = new ArrayList<>();
		Map<String, List<QRResponseSummaryEntity>> groupedData = new HashMap<>();
		if (isGstin) {
			LOGGER.debug("extracting data for gstinStats");
			groupedData = summList.stream()
					.filter(p -> p.getSellerGstin() != null)
					.collect(Collectors.groupingBy(k -> k.getSellerGstin()));
		} else {
			LOGGER.debug("extracting data for pan");
			groupedData = summList.stream()
					.filter(p -> p.getSellerPan() != null)
					.collect(Collectors.groupingBy(k -> k.getSellerPan()));
		}
		for (Map.Entry<String, List<QRResponseSummaryEntity>> entry : groupedData
				.entrySet()) {
			QRGstnPanStats tableStatsDto = new QRGstnPanStats();
			String gstin = entry.getKey();
			tableStatsDto.setGstin(gstin);
			List<QRResponseSummaryEntity> listOfQrSumm = entry.getValue();
			tableStatsDto.setInvoices(listOfQrSumm.size());
			tableStatsDto
					.setMatch(String.valueOf(getMisMatchPerc(listOfQrSumm)));
			tableStats.add(tableStatsDto);
		}
		LOGGER.debug("extracted tableStats is {} ", tableStats);

		List<QRGstnPanStats> sortedList = tableStats
				.stream().sorted(Comparator
						.comparingInt(QRGstnPanStats::getInvoices).reversed())
				.collect(Collectors.toList());

		return sortedList;
	}

	public List<QRGstnPanStats> extractGstnPanWiseStatsQrPDF(
			List<QRPDFResponseSummaryEntity> summList, boolean isGstin) {

		List<QRGstnPanStats> tableStats = new ArrayList<>();
		Map<String, List<QRPDFResponseSummaryEntity>> groupedData = new HashMap<>();
		if (isGstin) {
			LOGGER.debug("extracting data for gstinStats");
			groupedData = summList.stream()
					.filter(p -> p.getSellerGstinQR() != null)
					.collect(Collectors.groupingBy(k -> k.getSellerGstinQR()));
		} else {
			LOGGER.debug("extracting data for pan");
			groupedData = summList.stream()
					.filter(p -> p.getSellerPanQR() != null)
					.collect(Collectors.groupingBy(k -> k.getSellerPanQR()));
		}
		for (Map.Entry<String, List<QRPDFResponseSummaryEntity>> entry : groupedData
				.entrySet()) {
			QRGstnPanStats tableStatsDto = new QRGstnPanStats();
			String gstin = entry.getKey();
			tableStatsDto.setGstin(gstin);
			List<QRPDFResponseSummaryEntity> listOfQrSumm = entry.getValue();
			tableStatsDto.setInvoices(listOfQrSumm.size());
			tableStatsDto.setMatch(
					String.valueOf(getMisMatchPercQRPDF(listOfQrSumm)));
			tableStats.add(tableStatsDto);
		}
		LOGGER.debug("extracted tableStats is {} ", tableStats);

		List<QRGstnPanStats> sortedList = tableStats
				.stream().sorted(Comparator
						.comparingInt(QRGstnPanStats::getInvoices).reversed())
				.collect(Collectors.toList());

		return sortedList;
	}

	public List<QRGstnPanStats> extractGstnPanWiseStatsQrPDFJson(
			List<QRPDFJSONResponseSummaryEntity> summList, boolean isGstin) {

		List<QRGstnPanStats> tableStats = new ArrayList<>();
		Map<String, List<QRPDFJSONResponseSummaryEntity>> groupedData = new HashMap<>();
		if (isGstin) {
			LOGGER.debug("extracting data for gstinStats");
			groupedData = summList.stream()
					.filter(p -> p.getSellerGstinJson() != null).collect(
							Collectors.groupingBy(k -> k.getSellerGstinJson()));
		} else {
			LOGGER.debug("extracting data for pan");
			groupedData = summList.stream()
					.filter(p -> p.getSellerPanJson() != null)
					.collect(Collectors.groupingBy(k -> k.getSellerPanJson()));
		}
		for (Map.Entry<String, List<QRPDFJSONResponseSummaryEntity>> entry : groupedData
				.entrySet()) {
			QRGstnPanStats tableStatsDto = new QRGstnPanStats();
			String gstin = entry.getKey();
			tableStatsDto.setGstin(gstin);
			List<QRPDFJSONResponseSummaryEntity> listOfQrSumm = entry
					.getValue();
			tableStatsDto.setInvoices(listOfQrSumm.size());
			tableStatsDto.setMatch(
					String.valueOf(getMisMatchPercQRPDFJson(listOfQrSumm)));
			tableStats.add(tableStatsDto);
		}
		LOGGER.debug("extracted tableStats is {} ", tableStats);

		List<QRGstnPanStats> sortedList = tableStats
				.stream().sorted(Comparator
						.comparingInt(QRGstnPanStats::getInvoices).reversed())
				.collect(Collectors.toList());

		return sortedList;
	}

	private double getMisMatchPerc(List<QRResponseSummaryEntity> listOfQrSumm) {
		double noOfMisMatches = 0;
		double totalInvoices = listOfQrSumm.size();
		for (QRResponseSummaryEntity qrResponseSummaryEntity : listOfQrSumm) {
			boolean isMatch = QRCommonUtility
					.isFullMatch(qrResponseSummaryEntity);
			if (!isMatch) {
				noOfMisMatches++;
			}
		}
		double misMatchPerc = (totalInvoices - noOfMisMatches) * 100
				/ totalInvoices;

		DecimalFormat df = new DecimalFormat("#.##");
		double result = new Double(df.format(misMatchPerc));
		return result;
	}

	private double getMisMatchPercQRPDF(
			List<QRPDFResponseSummaryEntity> listOfQrSumm) {
		double noOfMisMatches = 0;
		double totalInvoices = listOfQrSumm.size();
		for (QRPDFResponseSummaryEntity qrResponseSummaryEntity : listOfQrSumm) {
			boolean isMatch = qrResponseSummaryEntity.getIsFullMatch();
			if (!isMatch) {
				noOfMisMatches++;
			}
		}
		double misMatchPerc = (totalInvoices - noOfMisMatches) * 100
				/ totalInvoices;

		DecimalFormat df = new DecimalFormat("#.##");
		double result = new Double(df.format(misMatchPerc));
		return result;
	}

	private double getMisMatchPercQRPDFJson(
			List<QRPDFJSONResponseSummaryEntity> listOfQrSumm) {
		double noOfMisMatches = 0;
		double totalInvoices = listOfQrSumm.size();
		for (QRPDFJSONResponseSummaryEntity qrResponseSummaryEntity : listOfQrSumm) {
			boolean isMatch = qrResponseSummaryEntity.getIsFullMatch();
			if (!isMatch) {
				noOfMisMatches++;
			}
		}
		double misMatchPerc = (totalInvoices - noOfMisMatches) * 100
				/ totalInvoices;

		DecimalFormat df = new DecimalFormat("#.##");
		double result = new Double(df.format(misMatchPerc));
		return result;
	}

	public void extractMisMatchDetails(List<QRMisMatchCount> misMatchDtoList,
			List<QRResponseSummaryEntity> responseSummaryList) {
		QRMisMatchCount sellerMisMatchDto = new QRMisMatchCount();

		long sellerMisMatch = responseSummaryList.stream()
				.filter(p -> p.getSellerGstinMatch() != null && UnAvailable
						.equalsIgnoreCase(p.getSellerGstinMatch()))
				.count();
		sellerMisMatchDto.setReportType(QRCodeValidatorConstants.SELLER_GSTIN);
		sellerMisMatchDto.setQrCnt(Math.toIntExact(sellerMisMatch));
		misMatchDtoList.add(sellerMisMatchDto);

		QRMisMatchCount buyerMisMatchDto = new QRMisMatchCount();
		long buyerMisMatch = responseSummaryList.stream()
				.filter(p -> p.getBuyerGstinMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getBuyerGstinMatch()))
				.count();
		buyerMisMatchDto.setReportType(QRCodeValidatorConstants.BUYER_GSTIN);
		buyerMisMatchDto.setQrCnt(Math.toIntExact(buyerMisMatch));
		misMatchDtoList.add(buyerMisMatchDto);

		QRMisMatchCount docNoMisMatchDto = new QRMisMatchCount();
		long docNoMisMatch = responseSummaryList.stream()
				.filter(p -> p.getDocNoMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getDocNoMatch()))
				.count();
		docNoMisMatchDto.setReportType(QRCodeValidatorConstants.DOC_NO);
		docNoMisMatchDto.setQrCnt(Math.toIntExact(docNoMisMatch));
		misMatchDtoList.add(docNoMisMatchDto);

		QRMisMatchCount docTypeMisMatchDto = new QRMisMatchCount();
		long docTypeMisMatch = responseSummaryList.stream()
				.filter(p -> p.getDocTypeMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getDocTypeMatch()))
				.count();
		docTypeMisMatchDto.setReportType(QRCodeValidatorConstants.DOC_TYPE);
		docTypeMisMatchDto.setQrCnt(Math.toIntExact(docTypeMisMatch));
		misMatchDtoList.add(docTypeMisMatchDto);

		QRMisMatchCount docDtMisMatchDto = new QRMisMatchCount();
		long docDtMisMatch = responseSummaryList.stream()
				.filter(p -> p.getDocDtMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getDocDtMatch()))
				.count();
		docDtMisMatchDto.setReportType(QRCodeValidatorConstants.DOC_DT);
		docDtMisMatchDto.setQrCnt(Math.toIntExact(docDtMisMatch));
		misMatchDtoList.add(docDtMisMatchDto);

		QRMisMatchCount irnMisMatchDto = new QRMisMatchCount();
		long irnMisMatch = responseSummaryList.stream()
				.filter(p -> p.getIrnMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getIrnMatch()))
				.count();
		irnMisMatchDto.setReportType(QRCodeValidatorConstants.IRN);
		irnMisMatchDto.setQrCnt(Math.toIntExact(irnMisMatch));
		misMatchDtoList.add(irnMisMatchDto);

		QRMisMatchCount totalInvMisMatchDto = new QRMisMatchCount();
		long totalInvMisMatch = responseSummaryList.stream()
				.filter(p -> p.getTotInvValMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getTotInvValMatch()))
				.count();
		totalInvMisMatchDto.setReportType(QRCodeValidatorConstants.INV_VALUE);
		totalInvMisMatchDto.setQrCnt(Math.toIntExact(totalInvMisMatch));
		misMatchDtoList.add(totalInvMisMatchDto);

		QRMisMatchCount hsnMisMatchDto = new QRMisMatchCount();
		long hsnMisMatch = responseSummaryList.stream()
				.filter(p -> p.getMainHsnCodeMatch() != null && UnAvailable
						.equalsIgnoreCase(p.getMainHsnCodeMatch()))
				.count();
		hsnMisMatchDto.setReportType(QRCodeValidatorConstants.HSN);
		hsnMisMatchDto.setQrCnt(Math.toIntExact(hsnMisMatch));
		misMatchDtoList.add(hsnMisMatchDto);

		LOGGER.debug("extracted misMatchDtoList is {} ", misMatchDtoList);
	}

	public void extractMisMatchDetailsQrPdf(
			List<QRMisMatchCount> misMatchDtoList,
			List<QRPDFResponseSummaryEntity> responseSummaryList) {
		QRMisMatchCount sellerMisMatchDto = new QRMisMatchCount();

		long sellerMisMatch = responseSummaryList.stream()
				.filter(p -> p.getSellerGstinMatch() != null && UnAvailable
						.equalsIgnoreCase(p.getSellerGstinMatch()))
				.count();
		sellerMisMatchDto.setReportType(QRCodeValidatorConstants.SELLER_GSTIN);
		sellerMisMatchDto.setQrCnt(Math.toIntExact(sellerMisMatch));
		misMatchDtoList.add(sellerMisMatchDto);

		QRMisMatchCount buyerMisMatchDto = new QRMisMatchCount();
		long buyerMisMatch = responseSummaryList.stream()
				.filter(p -> p.getBuyerGstinMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getBuyerGstinMatch()))
				.count();
		buyerMisMatchDto.setReportType(QRCodeValidatorConstants.BUYER_GSTIN);
		buyerMisMatchDto.setQrCnt(Math.toIntExact(buyerMisMatch));
		misMatchDtoList.add(buyerMisMatchDto);

		QRMisMatchCount docNoMisMatchDto = new QRMisMatchCount();
		long docNoMisMatch = responseSummaryList.stream()
				.filter(p -> p.getDocNoMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getDocNoMatch()))
				.count();
		docNoMisMatchDto.setReportType(QRCodeValidatorConstants.DOC_NO);
		docNoMisMatchDto.setQrCnt(Math.toIntExact(docNoMisMatch));
		misMatchDtoList.add(docNoMisMatchDto);

		QRMisMatchCount docTypeMisMatchDto = new QRMisMatchCount();
		long docTypeMisMatch = responseSummaryList.stream()
				.filter(p -> p.getDocTypeMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getDocTypeMatch()))
				.count();
		docTypeMisMatchDto.setReportType(QRCodeValidatorConstants.DOC_TYPE);
		docTypeMisMatchDto.setQrCnt(Math.toIntExact(docTypeMisMatch));
		misMatchDtoList.add(docTypeMisMatchDto);

		QRMisMatchCount docDtMisMatchDto = new QRMisMatchCount();
		long docDtMisMatch = responseSummaryList.stream()
				.filter(p -> p.getDocDtMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getDocDtMatch()))
				.count();
		docDtMisMatchDto.setReportType(QRCodeValidatorConstants.DOC_DT);
		docDtMisMatchDto.setQrCnt(Math.toIntExact(docDtMisMatch));
		misMatchDtoList.add(docDtMisMatchDto);

		QRMisMatchCount irnMisMatchDto = new QRMisMatchCount();
		long irnMisMatch = responseSummaryList.stream()
				.filter(p -> p.getIrnMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getIrnMatch()))
				.count();
		irnMisMatchDto.setReportType(QRCodeValidatorConstants.IRN);
		irnMisMatchDto.setQrCnt(Math.toIntExact(irnMisMatch));
		misMatchDtoList.add(irnMisMatchDto);

		QRMisMatchCount totalInvMisMatchDto = new QRMisMatchCount();
		long totalInvMisMatch = responseSummaryList.stream()
				.filter(p -> p.getTotInvValMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getTotInvValMatch()))
				.count();
		totalInvMisMatchDto.setReportType(QRCodeValidatorConstants.INV_VALUE);
		totalInvMisMatchDto.setQrCnt(Math.toIntExact(totalInvMisMatch));
		misMatchDtoList.add(totalInvMisMatchDto);

		QRMisMatchCount hsnMisMatchDto = new QRMisMatchCount();
		long hsnMisMatch = responseSummaryList.stream()
				.filter(p -> p.getMainHsnCodeMatch() != null && UnAvailable
						.equalsIgnoreCase(p.getMainHsnCodeMatch()))
				.count();
		hsnMisMatchDto.setReportType(QRCodeValidatorConstants.HSN);
		hsnMisMatchDto.setQrCnt(Math.toIntExact(hsnMisMatch));
		misMatchDtoList.add(hsnMisMatchDto);

		QRMisMatchCount sigurateMisMatchDto = new QRMisMatchCount();
		long sigurateMisMatch = responseSummaryList.stream()
				.filter(p -> p.getSignatureMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getSignatureMatch()))
				.count();
		sigurateMisMatchDto.setReportType(QRCodeValidatorConstants.SIGN);
		sigurateMisMatchDto.setQrCnt(Math.toIntExact(sigurateMisMatch));
		misMatchDtoList.add(sigurateMisMatchDto);

		QRMisMatchCount irnDateMisMatchDto = new QRMisMatchCount();
		long irnrnDateMisMatch = responseSummaryList.stream()
				.filter(p -> p.getIrnDateMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getIrnDateMatch()))
				.count();
		irnDateMisMatchDto.setReportType(QRCodeValidatorConstants.IRN_DATE);
		irnDateMisMatchDto.setQrCnt(Math.toIntExact(irnrnDateMisMatch));
		misMatchDtoList.add(irnDateMisMatchDto);

		LOGGER.debug("extracted misMatchDtoList is {} ", misMatchDtoList);
	}

	public void extractMisMatchDetailsQrPdfJson(
			List<QRMisMatchCount> misMatchDtoList,
			List<QRPDFJSONResponseSummaryEntity> responseSummaryList) {
		QRMisMatchCount sellerMisMatchDto = new QRMisMatchCount();

		long sellerMisMatch = responseSummaryList.stream()
				.filter(p -> p.getSellerGstinMatch() != null && UnAvailable
						.equalsIgnoreCase(p.getSellerGstinMatch()))
				.count();
		sellerMisMatchDto.setReportType(QRCodeValidatorConstants.SELLER_GSTIN);
		sellerMisMatchDto.setQrCnt(Math.toIntExact(sellerMisMatch));
		misMatchDtoList.add(sellerMisMatchDto);

		QRMisMatchCount buyerMisMatchDto = new QRMisMatchCount();
		long buyerMisMatch = responseSummaryList.stream()
				.filter(p -> p.getBuyerGstinMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getBuyerGstinMatch()))
				.count();
		buyerMisMatchDto.setReportType(QRCodeValidatorConstants.BUYER_GSTIN);
		buyerMisMatchDto.setQrCnt(Math.toIntExact(buyerMisMatch));
		misMatchDtoList.add(buyerMisMatchDto);

		QRMisMatchCount docNoMisMatchDto = new QRMisMatchCount();
		long docNoMisMatch = responseSummaryList.stream()
				.filter(p -> p.getDocNoMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getDocNoMatch()))
				.count();
		docNoMisMatchDto.setReportType(QRCodeValidatorConstants.DOC_NO);
		docNoMisMatchDto.setQrCnt(Math.toIntExact(docNoMisMatch));
		misMatchDtoList.add(docNoMisMatchDto);

		QRMisMatchCount docTypeMisMatchDto = new QRMisMatchCount();
		long docTypeMisMatch = responseSummaryList.stream()
				.filter(p -> p.getDocTypeMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getDocTypeMatch()))
				.count();
		docTypeMisMatchDto.setReportType(QRCodeValidatorConstants.DOC_TYPE);
		docTypeMisMatchDto.setQrCnt(Math.toIntExact(docTypeMisMatch));
		misMatchDtoList.add(docTypeMisMatchDto);

		QRMisMatchCount docDtMisMatchDto = new QRMisMatchCount();
		long docDtMisMatch = responseSummaryList.stream()
				.filter(p -> p.getDocDtMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getDocDtMatch()))
				.count();
		docDtMisMatchDto.setReportType(QRCodeValidatorConstants.DOC_DT);
		docDtMisMatchDto.setQrCnt(Math.toIntExact(docDtMisMatch));
		misMatchDtoList.add(docDtMisMatchDto);

		QRMisMatchCount irnMisMatchDto = new QRMisMatchCount();
		long irnMisMatch = responseSummaryList.stream()
				.filter(p -> p.getIrnMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getIrnMatch()))
				.count();
		irnMisMatchDto.setReportType(QRCodeValidatorConstants.IRN);
		irnMisMatchDto.setQrCnt(Math.toIntExact(irnMisMatch));
		misMatchDtoList.add(irnMisMatchDto);

		QRMisMatchCount totalInvMisMatchDto = new QRMisMatchCount();
		long totalInvMisMatch = responseSummaryList.stream()
				.filter(p -> p.getTotInvValMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getTotInvValMatch()))
				.count();
		totalInvMisMatchDto.setReportType(QRCodeValidatorConstants.INV_VALUE);
		totalInvMisMatchDto.setQrCnt(Math.toIntExact(totalInvMisMatch));
		misMatchDtoList.add(totalInvMisMatchDto);

		QRMisMatchCount hsnMisMatchDto = new QRMisMatchCount();
		long hsnMisMatch = responseSummaryList.stream()
				.filter(p -> p.getMainHsnCodeMatch() != null && UnAvailable
						.equalsIgnoreCase(p.getMainHsnCodeMatch()))
				.count();
		hsnMisMatchDto.setReportType(QRCodeValidatorConstants.HSN);
		hsnMisMatchDto.setQrCnt(Math.toIntExact(hsnMisMatch));
		misMatchDtoList.add(hsnMisMatchDto);

		QRMisMatchCount sigurateMisMatchDto = new QRMisMatchCount();
		long sigurateMisMatch = responseSummaryList.stream()
				.filter(p -> p.getSignatureMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getSignatureMatch()))
				.count();
		sigurateMisMatchDto.setReportType(QRCodeValidatorConstants.SIGN);
		sigurateMisMatchDto.setQrCnt(Math.toIntExact(sigurateMisMatch));
		misMatchDtoList.add(sigurateMisMatchDto);

		QRMisMatchCount irnDateMisMatchDto = new QRMisMatchCount();
		long irnrnDateMisMatch = responseSummaryList.stream()
				.filter(p -> p.getIrnDateMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getIrnDateMatch()))
				.count();
		irnDateMisMatchDto.setReportType(QRCodeValidatorConstants.IRN_DATE);
		irnDateMisMatchDto.setQrCnt(Math.toIntExact(irnrnDateMisMatch));
		misMatchDtoList.add(irnDateMisMatchDto);

		QRMisMatchCount taxableMisMatchDto = new QRMisMatchCount();
		long taxableDateMisMatch = responseSummaryList.stream()
				.filter(p -> p.getTaxableValueMatch() != null && UnAvailable
						.equalsIgnoreCase(p.getTaxableValueMatch()))
				.count();
		taxableMisMatchDto
				.setReportType(QRCodeValidatorConstants.TAXABLE_VALUE);
		taxableMisMatchDto.setQrCnt(Math.toIntExact(taxableDateMisMatch));
		misMatchDtoList.add(taxableMisMatchDto);

		QRMisMatchCount igstMisMatchDto = new QRMisMatchCount();
		long igstMisMatch = responseSummaryList.stream()
				.filter(p -> p.getIgstMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getIgstMatch()))
				.count();
		igstMisMatchDto.setReportType(QRCodeValidatorConstants.IGST);
		igstMisMatchDto.setQrCnt(Math.toIntExact(igstMisMatch));
		misMatchDtoList.add(igstMisMatchDto);

		QRMisMatchCount cgstMisMatchDto = new QRMisMatchCount();
		long cgstMisMatch = responseSummaryList.stream()
				.filter(p -> p.getCgstMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getCgstMatch()))
				.count();
		cgstMisMatchDto.setReportType(QRCodeValidatorConstants.CGST);
		cgstMisMatchDto.setQrCnt(Math.toIntExact(cgstMisMatch));
		misMatchDtoList.add(cgstMisMatchDto);

		QRMisMatchCount sgstMisMatchDto = new QRMisMatchCount();
		long sgstMisMatch = responseSummaryList.stream()
				.filter(p -> p.getSgstMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getSgstMatch()))
				.count();
		sgstMisMatchDto.setReportType(QRCodeValidatorConstants.SGST);
		sgstMisMatchDto.setQrCnt(Math.toIntExact(sgstMisMatch));
		misMatchDtoList.add(sgstMisMatchDto);

		QRMisMatchCount cessMisMatchDto = new QRMisMatchCount();
		long cessMisMatch = responseSummaryList.stream()
				.filter(p -> p.getCessMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getCessMatch()))
				.count();
		cessMisMatchDto.setReportType(QRCodeValidatorConstants.CESS);
		cessMisMatchDto.setQrCnt(Math.toIntExact(cessMisMatch));
		misMatchDtoList.add(cessMisMatchDto);

		QRMisMatchCount totalTaxMisMatchDto = new QRMisMatchCount();
		long totalTaxMisMatch = responseSummaryList.stream()
				.filter(p -> p.getTotalTaxMatch() != null
						&& UnAvailable.equalsIgnoreCase(p.getTotalTaxMatch()))
				.count();
		totalTaxMisMatchDto.setReportType(QRCodeValidatorConstants.TOTAL_TAX);
		totalTaxMisMatchDto.setQrCnt(Math.toIntExact(totalTaxMisMatch));
		misMatchDtoList.add(totalTaxMisMatchDto);

		LOGGER.debug("extracted misMatchDtoList is {} ", misMatchDtoList);
	}

}
