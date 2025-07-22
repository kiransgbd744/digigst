
package com.ey.advisory.app.get.notices.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.TblGetNoticeDocumentDetailEntity;
import com.ey.advisory.app.data.entities.client.asprecon.TblGetNoticesEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.TblGetNoticesRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author sakshi.jain
 *
 */

@Slf4j
@Component("DocumentDetailsParserServiceImpl")
public class DocumentDetailsParserServiceImpl {

	@Autowired
	@Qualifier("TblGetNoticesRepository")
	private TblGetNoticesRepository noticeRefIdRepo;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	public List<TblGetNoticeDocumentDetailEntity> processDocumentIdDetails(
			Long batchId, TblGetNoticesEntity refIdEntity,
			GetNoticeRefIdDto refIdDto, String gstin) {

		List<TblGetNoticeDocumentDetailEntity> documentDetailsEntityList = new ArrayList<>();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" inside  processDocumentIdDetails for batch id {} ",
						batchId);
			}

			/*for (DcupdtlsDetailsDto docDetailDto : refIdDto.getMaindocs()
					.getDcupdtls()) {
				TblGetNoticeDocumentDetailEntity documentDetailENtity = persistDocumentIdDetails(
						docDetailDto, refIdEntity, gstin, true);
				documentDetailsEntityList.add(documentDetailENtity);

			}
			for (DcupdtlsDetailsDto docDetailDto : refIdDto.getSuppdocs()
					.getDcupdtls()) {
				TblGetNoticeDocumentDetailEntity documentDetailENtity = persistDocumentIdDetails(
						docDetailDto, refIdEntity, gstin, false);
				documentDetailsEntityList.add(documentDetailENtity);
			}
*/
			
			for (DocumentDetailsDto docDetailDto : refIdDto.getMaindocs()) {

                List<TblGetNoticeDocumentDetailEntity> documentDetailENtity = persistDocumentIdDetails(

                        docDetailDto.getDcupdtls(), refIdEntity, gstin, true);

                documentDetailsEntityList.addAll(documentDetailENtity);
 
            }

            for (DocumentDetailsDto docDetailDto : refIdDto.getSuppdocs()) {

                List<TblGetNoticeDocumentDetailEntity> documentDetailENtity = persistDocumentIdDetails(

                        docDetailDto.getDcupdtls(), refIdEntity, gstin,false);

                documentDetailsEntityList.addAll(documentDetailENtity);

            }

 
			return documentDetailsEntityList;

		} catch (Exception ex) {
			LOGGER.error(
					" Exception while parsing the Doc Id details {} for batch id {}",
					ex, batchId);
			throw new AppException(ex);
		}
	}

	public List<TblGetNoticeDocumentDetailEntity> persistDocumentIdDetails(
			List<DcupdtlsDetailsDto> docDetailDtoList, TblGetNoticesEntity refIdEntity,
			String gstin, boolean isMainDoc) {
		List<TblGetNoticeDocumentDetailEntity> documentDetailENtityList = new ArrayList<>();
		for(DcupdtlsDetailsDto docDetailDto : docDetailDtoList)
		{
		TblGetNoticeDocumentDetailEntity documentDetailENtity = new TblGetNoticeDocumentDetailEntity();
		documentDetailENtity.setContentType(docDetailDto.getContentType());
		documentDetailENtity.setDocName(docDetailDto.getDocName());
		documentDetailENtity.setHash(docDetailDto.getHash());
		documentDetailENtity.setDocType(docDetailDto.getType());
		documentDetailENtity.setDocId(docDetailDto.getId());
		documentDetailENtity.setIsMainDoc(isMainDoc);
		documentDetailENtity.setNoticeDetail(refIdEntity);
		getDocumentIdDetail(docDetailDto.getId(), TenantContext.getTenantId(),
				gstin, documentDetailENtity, refIdEntity);
		documentDetailENtityList.add(documentDetailENtity);
		}
		return documentDetailENtityList;

	}

	public void getDocumentIdDetail(String docId, String groupCode,
			String gstin, TblGetNoticeDocumentDetailEntity documentDetailENtity,
			TblGetNoticesEntity refIdEntity) {
		try {
			APIParam param1 = new APIParam("doc_id", docId);
			APIParam param2 = new APIParam("gstin", gstin);
			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.NOTICE_DOCID_DTL, param1, param2);

			APIResponse docIdDetailResp = apiExecutor.execute(params, null);
			if (docIdDetailResp.isSuccess()) {
				JsonObject obj = JsonParser
						.parseString(docIdDetailResp.getResponse())
						.getAsJsonObject();
				documentDetailENtity.setDocData(obj.get("data").getAsString());
			} else {
				LOGGER.error(
						" Got exception in doc id api with response {} for doc id {} ",
						docIdDetailResp, docId);
				String errorCode = docIdDetailResp.getError().getErrorCode();
				String errDesc = docIdDetailResp.getError().getErrorDesc();
				documentDetailENtity.setErrorCode(errorCode);
				documentDetailENtity.setErrorDesc(errDesc);
				refIdEntity.setIsDelete(true);
			}
			documentDetailENtity.setNoticeDetail(refIdEntity);

		} catch (Exception ex) {
			LOGGER.error(
					" Exception while parsing the Doc Id details {} for doc id {}",
					ex, docId);
			throw new AppException(ex);
		}
	}
}
