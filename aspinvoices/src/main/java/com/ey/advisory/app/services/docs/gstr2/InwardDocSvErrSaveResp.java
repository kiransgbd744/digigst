package com.ey.advisory.app.services.docs.gstr2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocError;
import com.ey.advisory.app.docs.dto.InwardDocErrorDto;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSvErrSaveRespDto;

/**
 * This class is responsible for providing save structural validation error
 * response
 * 
 * @author Mohana.Dasari
 *
 */
@Component("InwardDocSvErrSaveResp")
public class InwardDocSvErrSaveResp {

	public List<InwardDocSvErrSaveRespDto> createInwardDocSvErrSaveResponse(
			List<Long> oldDocIds, List<Anx2InwardErrorHeaderEntity> savedDocs,
			List<InwardTransDocError> inErrors,
			List<InwardDocSvErrSaveRespDto> docSaveRespDtos) {
		
		if (oldDocIds.isEmpty()) {
			savedDocs.forEach(savedDocument -> {
				// errorsResp list is to show errors in response
				List<InwardDocErrorDto> errorsResp = new ArrayList<>();
				InwardDocSvErrSaveRespDto res = new InwardDocSvErrSaveRespDto();
				res.setId(savedDocument.getId());
				res.setDocNo(savedDocument.getDocNo());
				res.setDocType(savedDocument.getDocType());
				res.setDocDate(savedDocument.getDocDate());
				res.setCustGstin(savedDocument.getCgstin());
				res.setSupplierGstin(savedDocument.getSgstin());
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
					res.setErrors(errorsResp);
				});
				docSaveRespDtos.add(res);
			});
		}else {
			IntStream.range(0, savedDocs.size()).forEach(i -> {
				Anx2InwardErrorHeaderEntity savedDocument = savedDocs.get(i);
				// errorsResp list is to show errors in response
				List<InwardDocErrorDto> errorsResp = new ArrayList<>();
				InwardDocSvErrSaveRespDto res = new InwardDocSvErrSaveRespDto();
				res.setId(savedDocument.getId());
				res.setOldId(oldDocIds.get(i));
				res.setDocNo(savedDocument.getDocNo());
				res.setDocType(savedDocument.getDocType());
				res.setDocDate(savedDocument.getDocDate());
				res.setCustGstin(savedDocument.getCgstin());
				res.setSupplierGstin(savedDocument.getSgstin());
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
					res.setErrors(errorsResp);
				});
				docSaveRespDtos.add(res);
			});
		}
		return docSaveRespDtos;
	}
}
