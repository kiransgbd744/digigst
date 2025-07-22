package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Itc04HeaderErrorsEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.docs.dto.DocErrorDto;
import com.ey.advisory.app.docs.dto.OutwardDocSvErrSaveRespDto;

/**
 * This class is responsible for providing save structural validation error
 * response
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Itc04DocSvErrSaveResp")
public class Itc04DocSvErrSaveResp {
	
	public List<OutwardDocSvErrSaveRespDto> createOutwardDocSvErrSaveResponse(
			List<Long> oldDocIds, List<Itc04HeaderErrorsEntity> anx1OutWardErrHeaders,
			List<OutwardTransDocError> outErrors,
			List<OutwardDocSvErrSaveRespDto> docSaveRespDtos) {
		
		if (oldDocIds.isEmpty()) {
			anx1OutWardErrHeaders.forEach(savedDocument -> {
				// errorsResp list is to show errors in response
				List<DocErrorDto> errorsResp = new ArrayList<>();
				OutwardDocSvErrSaveRespDto res = 
						new OutwardDocSvErrSaveRespDto();
				res.setId(savedDocument.getId());
				//res.setDocNo(savedDocument.getDocNo());
				//res.setDocType(savedDocument.getDocType());
				//res.setDocDate(savedDocument.getDocDate());
				//res.setSupplierGstin(savedDocument.getSgstin());
				outErrors.forEach(outError -> {
					if (savedDocument.getId()
							.equals(outError.getDocHeaderId())) {
						DocErrorDto docErrorDto = new DocErrorDto();
						docErrorDto.setIndex(outError.getItemIndex());
						docErrorDto.setItemNo(outError.getItemNum());
						docErrorDto.setErrorCode(outError.getErrorCode());
						docErrorDto.setErrorDesc(outError.getErrorDesc());
						docErrorDto.setErrorType(outError.getErrorType());
						docErrorDto.setErrorFields(outError.getErrorField());
						docErrorDto.setValType(outError.getType());
						errorsResp.add(docErrorDto);
					}
					res.setErrors(errorsResp);
				});
				docSaveRespDtos.add(res);
			});
		}else{
			IntStream.range(0, anx1OutWardErrHeaders.size()).forEach(i -> {
				Itc04HeaderErrorsEntity savedDocument = anx1OutWardErrHeaders.get(i);
				// errorsResp list is to show errors in response
				List<DocErrorDto> errorsResp = new ArrayList<>();
				OutwardDocSvErrSaveRespDto res = 
							new OutwardDocSvErrSaveRespDto();
				res.setId(savedDocument.getId());
				res.setOldId(oldDocIds.get(i));
				//res.setDocNo(savedDocument.getDocNo());
				//res.setDocType(savedDocument.getDocType());
				//res.setDocDate(savedDocument.getDocDate());
				//res.setSupplierGstin(savedDocument.getSgstin());
				outErrors.forEach(outError -> {
					if (savedDocument.getId()
							.equals(outError.getDocHeaderId())) {
						DocErrorDto docErrorDto = new DocErrorDto();
						docErrorDto.setIndex(outError.getItemIndex());
						docErrorDto.setItemNo(outError.getItemNum());
						docErrorDto.setErrorCode(outError.getErrorCode());
						docErrorDto.setErrorDesc(outError.getErrorDesc());
						docErrorDto.setErrorType(outError.getErrorType());
						docErrorDto.setErrorFields(outError.getErrorField());
						docErrorDto.setValType(outError.getType());
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
