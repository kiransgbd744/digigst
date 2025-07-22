package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.docs.dto.DocErrorDto;
import com.ey.advisory.app.docs.dto.DocSaveRespDto;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Service("OutwardDocSaveResp")
public class OutwardDocSaveResp {
	
	public List<DocSaveRespDto> createOutwardDocSaveAPIResponse(
			List<Long> oldDocIds, List<OutwardTransDocument> savedDocs,
			List<OutwardTransDocError> outError) {
		List<DocSaveRespDto> docSaveRespDtos = new ArrayList<>();
		
		if (oldDocIds.isEmpty()) {//SCI
			savedDocs.forEach(savedDocument -> {
				List<DocErrorDto> errors = new ArrayList<>();
				DocSaveRespDto docSaveRespDto = new DocSaveRespDto();
				docSaveRespDto.setId(savedDocument.getId());
				docSaveRespDto.setDocNo(savedDocument.getDocNo());
				docSaveRespDto.setDocType(savedDocument.getDocType());
				docSaveRespDto.setDocDate(savedDocument.getDocDate());
				docSaveRespDto.setSupplierGstin(savedDocument.getSgstin());
				docSaveRespDto.setAccountVoucherNo(
						savedDocument.getAccountingVoucherNumber());
				outError.forEach(error -> {
					if (savedDocument.getId().equals(error.getDocHeaderId())) {
						DocErrorDto docErrorDto = new DocErrorDto();
						docErrorDto.setIndex(error.getItemIndex());
						docErrorDto.setItemNo(error.getItemNum());
						docErrorDto.setErrorCode(error.getErrorCode());
						docErrorDto.setErrorDesc(error.getErrorDesc());
						docErrorDto.setErrorType(error.getErrorType());
						docErrorDto.setErrorFields(error.getErrorField());
						errors.add(docErrorDto);
					}
				});
				docSaveRespDto.setErrors(errors);
				docSaveRespDtos.add(docSaveRespDto);
			});
		}else{//UI
			IntStream.range(0, savedDocs.size()).forEach(i -> {
				DocSaveRespDto docSaveRespDto = new DocSaveRespDto();
				OutwardTransDocument savedDocument = savedDocs.get(i);
				List<DocErrorDto> errors = new ArrayList<>();
				docSaveRespDto.setId(savedDocument.getId());
				docSaveRespDto.setOldId(oldDocIds.get(i));
				docSaveRespDto.setDocNo(savedDocument.getDocNo());
				docSaveRespDto.setDocType(savedDocument.getDocType());
				docSaveRespDto.setDocDate(savedDocument.getDocDate());
				docSaveRespDto.setSupplierGstin(savedDocument.getSgstin());				
				outError.forEach(error -> {
					if (savedDocument.getId().equals(error.getDocHeaderId())) {
						DocErrorDto docErrorDto = new DocErrorDto();
						docErrorDto.setIndex(error.getItemIndex());
						docErrorDto.setItemNo(error.getItemNum());
						docErrorDto.setErrorCode(error.getErrorCode());
						docErrorDto.setErrorDesc(error.getErrorDesc());
						docErrorDto.setErrorType(error.getErrorType());
						docErrorDto.setErrorFields(error.getErrorField());
						errors.add(docErrorDto);
					}
				});
				docSaveRespDto.setErrors(errors);
				docSaveRespDtos.add(docSaveRespDto);
			});
			
		}
		return docSaveRespDtos;
	}

}
