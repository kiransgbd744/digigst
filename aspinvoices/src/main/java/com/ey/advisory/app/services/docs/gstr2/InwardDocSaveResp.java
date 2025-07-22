package com.ey.advisory.app.services.docs.gstr2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.InwardTransDocError;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSaveRespDto;
import com.ey.advisory.app.docs.dto.InwardDocErrorDto;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Service("InwardDocSaveResp")
public class InwardDocSaveResp {

	public List<InwardDocSaveRespDto> createInwardDocSaveAPIResponse(
			List<Long> oldDocIds, List<InwardTransDocument> savedDocs,
			List<InwardTransDocError> inErrors) {
		List<InwardDocSaveRespDto> docSaveRespDtos = new ArrayList<>();

		if (oldDocIds.isEmpty()) {
			savedDocs.forEach(savedDocument -> {
				// errorsResp list is to show errors in API response
				List<InwardDocErrorDto> errorsResp = new ArrayList<>();
				InwardDocSaveRespDto docSaveRespDto = new InwardDocSaveRespDto();
				docSaveRespDto.setId(savedDocument.getId());
				docSaveRespDto.setDocNo(savedDocument.getDocNo());
				docSaveRespDto.setDocType(savedDocument.getDocType());
				docSaveRespDto.setDocDate(savedDocument.getDocDate());
				docSaveRespDto.setSupplierGstin(savedDocument.getSgstin());
				docSaveRespDto.setCustGstin(savedDocument.getCgstin());
				inErrors.forEach(inError -> {
					if (savedDocument.getId()
							.equals(inError.getDocHeaderId())) {
						InwardDocErrorDto docErrorDto = new InwardDocErrorDto();
						docErrorDto.setIndex(inError.getItemIndex());
						docErrorDto.setItemNo(inError.getItemNum());
						docErrorDto.setErrorCode(inError.getErrorCode());
						docErrorDto.setErrorDesc(inError.getErrorDesc());
						docErrorDto.setErrorType(inError.getErrorType());
						docErrorDto.setErrorFields(inError.getErrorField());
						docErrorDto.setValType(inError.getValType());
						errorsResp.add(docErrorDto);
					}
					docSaveRespDto.setErrors(errorsResp);
				});
				docSaveRespDtos.add(docSaveRespDto);
			});
		} else {
			IntStream.range(0, savedDocs.size()).forEach(i -> {
				InwardTransDocument savedDocument = savedDocs.get(i);
				// errorsResp list is to show errors in API response
				List<InwardDocErrorDto> errorsResp = new ArrayList<>();
				InwardDocSaveRespDto docSaveRespDto = new InwardDocSaveRespDto();
				docSaveRespDto.setId(savedDocument.getId());
				docSaveRespDto.setOldId(oldDocIds.get(i));
				docSaveRespDto.setDocNo(savedDocument.getDocNo());
				docSaveRespDto.setDocType(savedDocument.getDocType());
				docSaveRespDto.setDocDate(savedDocument.getDocDate());
				docSaveRespDto.setSupplierGstin(savedDocument.getSgstin());
				docSaveRespDto.setCustGstin(savedDocument.getCgstin());
				inErrors.forEach(inError -> {
					if (savedDocument.getId()
							.equals(inError.getDocHeaderId())) {
						InwardDocErrorDto docErrorDto = new InwardDocErrorDto();
						docErrorDto.setIndex(inError.getItemIndex());
						docErrorDto.setItemNo(inError.getItemNum());
						docErrorDto.setErrorCode(inError.getErrorCode());
						docErrorDto.setErrorDesc(inError.getErrorDesc());
						docErrorDto.setErrorType(inError.getErrorType());
						docErrorDto.setErrorFields(inError.getErrorField());
						docErrorDto.setValType(inError.getValType());
						errorsResp.add(docErrorDto);
					}
					docSaveRespDto.setErrors(errorsResp);
				});
				docSaveRespDtos.add(docSaveRespDto);
			});
		}

		return docSaveRespDtos;
	}

}
