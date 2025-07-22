/**
 * 
 */
package com.ey.advisory.app.services.doc.gstr1a;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.data.gstr1A.entities.client.OutwardTransDocErrorGstr1A;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceDocErrorDto;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceDocSaveRespDto;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.common.GSTConstants;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Service("Gstr1AEInvoiceOutwardDocSaveResp")
public class Gstr1AEInvoiceOutwardDocSaveResp {

	public List<EInvoiceDocSaveRespDto> createOutwardDocSaveAPIResponse(
			List<Long> oldDocIds, List<Gstr1AOutwardTransDocument> savedDocs,
			Map<String, List<OutwardTransDocErrorGstr1A>> errorMap) {
		List<EInvoiceDocSaveRespDto> docSaveRespDtos = new ArrayList<>();
		/**
		 * This if block is used to form the error response Payload for the
		 * Documents pushed from compliance push/BCAPI push
		 */
		if (oldDocIds.isEmpty()) {
			savedDocs.forEach(savedDocument -> {
				List<EInvoiceDocErrorDto> errors = new ArrayList<>();
				EInvoiceDocSaveRespDto docSaveRespDto = new EInvoiceDocSaveRespDto();
				docSaveRespDto.setId(savedDocument.getId());
				docSaveRespDto.setDocNo(savedDocument.getDocNo());
				docSaveRespDto.setDocType(savedDocument.getDocType());
				docSaveRespDto.setDocDate(savedDocument.getDocDate());
				docSaveRespDto.setSupplierGstin(savedDocument.getSgstin());
				docSaveRespDto.setAccountVoucherNo(
						savedDocument.getAccountingVoucherNumber());
				List<OutwardTransDocErrorGstr1A> outError = errorMap
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
				if (savedDocument.getErrCodes() != null
						&& !savedDocument.getErrCodes().isEmpty()) {

					List<String> errorCodes = Stream
							.of(savedDocument.getErrCodes().split(","))
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
				Gstr1AOutwardTransDocument savedDocument = savedDocs.get(i);
				List<EInvoiceDocErrorDto> errors = new ArrayList<>();
				docSaveRespDto.setId(savedDocument.getId());
				docSaveRespDto.setOldId(oldDocIds.get(i));
				docSaveRespDto.setDocNo(savedDocument.getDocNo());
				docSaveRespDto.setDocType(savedDocument.getDocType());
				docSaveRespDto.setDocDate(savedDocument.getDocDate());
				docSaveRespDto.setSupplierGstin(savedDocument.getSgstin());
				List<OutwardTransDocErrorGstr1A> outError = errorMap
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
				if (savedDocument.getErrCodes() != null
						&& !savedDocument.getErrCodes().isEmpty()) {

					List<String> errorCodes = Stream
							.of(savedDocument.getErrCodes().split(","))
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
		return docSaveRespDtos;
	}

}
