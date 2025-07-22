/**
 * 
 */
package com.ey.advisory.app.services.docs.gstr7;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceDocErrorDto;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceDocSaveRespDto;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.common.GSTConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Service("GSTR7TransDocSaveResp")
public class GSTR7TransDocSaveResp {

	public List<EInvoiceDocSaveRespDto> createOutwardDocSaveAPIResponse(
			List<Long> oldDocIds, List<Gstr7TransDocHeaderEntity> savedDocs,
			Map<String, List<OutwardTransDocError>> errorMap) {
		List<EInvoiceDocSaveRespDto> docSaveRespDtos = new ArrayList<>();
		/**
		 * This if block is used to form the error response Payload for the
		 * Documents pushed from compliance push/BCAPI push
		 */
		if (oldDocIds.isEmpty()) {
			LOGGER.error("Inside Old Doc Id");
			savedDocs.forEach(savedDocument -> {
				List<EInvoiceDocErrorDto> errors = new ArrayList<>();
				EInvoiceDocSaveRespDto docSaveRespDto = new EInvoiceDocSaveRespDto();
				docSaveRespDto.setId(savedDocument.getId());
				docSaveRespDto.setDocNo(savedDocument.getDocNum());
				docSaveRespDto.setDocType(savedDocument.getDocType());
				docSaveRespDto.setDocDate(savedDocument.getDocDate());
				docSaveRespDto
						.setSupplierGstin(savedDocument.getDeducteeGstin());
				List<OutwardTransDocError> outError = errorMap
						.get(savedDocument.getDocKey());
				outError.forEach(error -> {
					EInvoiceDocErrorDto docErrorDto = new EInvoiceDocErrorDto();
					docErrorDto.setIndex(error.getItemIndex());
					docErrorDto.setItemNo(error.getItemNum());
					docErrorDto.setErrorCode(error.getErrorCode());
					docErrorDto.setErrorDesc(error.getErrorDesc());
					docErrorDto.setErrorType(error.getErrorType());
					docErrorDto.setErrorFields(error.getErrorField());
					errors.add(docErrorDto);

				});
				if (savedDocument.getErrorCodes() != null
						&& !savedDocument.getErrorCodes().isEmpty()) {

					List<String> errorCodes = Stream
							.of(savedDocument.getErrorCodes().split(","))
							.collect(Collectors.toList());
					if (errorCodes.contains(GSTConstants.ER15171)) {
						EInvoiceDocErrorDto docErrorDto2 = new EInvoiceDocErrorDto();
						docErrorDto2.setErrorCode(GSTConstants.ER15171);
						String errDesc = ErrorMasterUtil.findErrDescByErrCode(
								GSTConstants.ER15171, "OUTWARD");
						docErrorDto2.setErrorDesc(errDesc);
						docErrorDto2.setErrorType("ERR");
						docErrorDto2.setErrorFields("docNo");
						errors.add(docErrorDto2);
					}
					if (errorCodes.contains(GSTConstants.ER15167)) {
						EInvoiceDocErrorDto docErrorDto2 = new EInvoiceDocErrorDto();
						docErrorDto2.setErrorCode(GSTConstants.ER15167);
						String errDesc = ErrorMasterUtil.findErrDescByErrCode(
								GSTConstants.ER15167, "OUTWARD");
						docErrorDto2.setErrorDesc(errDesc);
						docErrorDto2.setErrorType("ERR");
						docErrorDto2.setErrorFields("docNo");
						errors.add(docErrorDto2);
					}

				}
				docSaveRespDto.setErrors(errors);
				docSaveRespDtos.add(docSaveRespDto);
			});
		} else {// UI
			IntStream.range(0, savedDocs.size()).forEach(i -> {
				EInvoiceDocSaveRespDto docSaveRespDto = new EInvoiceDocSaveRespDto();
				Gstr7TransDocHeaderEntity savedDocument = savedDocs.get(i);
				List<EInvoiceDocErrorDto> errors = new ArrayList<>();
				docSaveRespDto.setId(savedDocument.getId());
				docSaveRespDto.setOldId(oldDocIds.get(i));
				docSaveRespDto.setDocNo(savedDocument.getDocNum());
				docSaveRespDto.setDocType(savedDocument.getDocType());
				docSaveRespDto.setDocDate(savedDocument.getDocDate());
				docSaveRespDto
						.setSupplierGstin(savedDocument.getDeducteeGstin());
				List<OutwardTransDocError> outError = errorMap
						.get(savedDocument.getDocKey());
				outError.forEach(error -> {
					if (savedDocument.getId().equals(error.getDocHeaderId())) {
						EInvoiceDocErrorDto docErrorDto = new EInvoiceDocErrorDto();
						docErrorDto.setIndex(error.getItemIndex());
						docErrorDto.setItemNo(error.getItemNum());
						docErrorDto.setErrorCode(error.getErrorCode());
						docErrorDto.setErrorDesc(error.getErrorDesc());
						docErrorDto.setErrorType(error.getErrorType());
						docErrorDto.setErrorFields(error.getErrorField());
						errors.add(docErrorDto);
					}
				});
				if (savedDocument.getErrorCodes() != null
						&& !savedDocument.getErrorCodes().isEmpty()) {

					List<String> errorCodes = Stream
							.of(savedDocument.getErrorCodes().split(","))
							.collect(Collectors.toList());
					if (errorCodes.contains(GSTConstants.ER15171)) {
						EInvoiceDocErrorDto docErrorDto2 = new EInvoiceDocErrorDto();
						docErrorDto2.setErrorCode(GSTConstants.ER15171);
						String errDesc = ErrorMasterUtil.findErrDescByErrCode(
								GSTConstants.ER15171, "OUTWARD");
						docErrorDto2.setErrorDesc(errDesc);
						docErrorDto2.setErrorType("ERR");
						docErrorDto2.setErrorFields("docNo");
						errors.add(docErrorDto2);
					}
					if (errorCodes.contains(GSTConstants.ER15167)) {
						EInvoiceDocErrorDto docErrorDto2 = new EInvoiceDocErrorDto();
						docErrorDto2.setErrorCode(GSTConstants.ER15167);
						String errDesc = ErrorMasterUtil.findErrDescByErrCode(
								GSTConstants.ER15167, "OUTWARD");
						docErrorDto2.setErrorDesc(errDesc);
						docErrorDto2.setErrorType("ERR");
						docErrorDto2.setErrorFields("docNo");
						errors.add(docErrorDto2);
					}
				}
				docSaveRespDto.setErrors(errors);
				docSaveRespDtos.add(docSaveRespDto);
			});
		}
		LOGGER.error("docSaveRespDtos {} ", docSaveRespDtos);
		return docSaveRespDtos;
	}

}
