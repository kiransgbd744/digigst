/**
 * 
 */
package com.ey.advisory.app.get.notices.handlers;

import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.TblGetNoticeDocumentDetailEntity;
import com.ey.advisory.app.data.entities.client.asprecon.TblGetNoticesEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.TblGetNoticesRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.TblNoticeDocumentDetailRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author sakshi.jain
 *
 */

@Service("GetNoticesSuccessHandler")
@Slf4j
public class GetNoticesSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private GetNoticesServiceParseImpl getNoticesService;

	@Autowired
	@Qualifier("RefIdDocumentParserServiceImpl")
	private RefIdDocumentParserServiceImpl refIdParseServiceImpl;

	@Autowired
	@Qualifier("TblGetNoticesRepository")
	private TblGetNoticesRepository noticeRefIdRepo;

	@Autowired
	@Qualifier("TblNoticeDocumentDetailRepository")
	private TblNoticeDocumentDetailRepository docDetailsRepo;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		Long batchId = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			GstNoticesReqDto dto = gson.fromJson(ctxParamsObj,
					GstNoticesReqDto.class);
			batchId = dto.getBatchId();
			List<TblGetNoticesEntity> incrementRefIdList = getNoticesService
					.parseNoticeDetails(batchId, resultIds, dto);
			if (!incrementRefIdList.isEmpty()) {
				Pair<List<TblGetNoticesEntity>, List<TblGetNoticeDocumentDetailEntity>> refIdAndDocDetailsPair = refIdParseServiceImpl
						.processRefIdNotice(batchId, dto, incrementRefIdList);

				noticeRefIdRepo.saveAll(refIdAndDocDetailsPair.getValue0());
				docDetailsRepo.saveAll(refIdAndDocDetailsPair.getValue1());
			}
			batchUtil.updateById(batchId, APIConstants.SUCCESS, null,
					null, false);
			
		} catch (Exception ex) {
			LOGGER.error("Error while parsing get notices api {} ", ex);
			batchUtil.updateById(batchId, APIConstants.FAILED, null,
					"Error while parsing get notices api", false);
			throw new AppException();
		}
	}

}