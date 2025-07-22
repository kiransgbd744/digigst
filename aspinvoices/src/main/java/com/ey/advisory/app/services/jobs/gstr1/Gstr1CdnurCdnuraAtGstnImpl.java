package com.ey.advisory.app.services.jobs.gstr1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("gstr1CdnurCdnuraAtGstnImpl")
public class Gstr1CdnurCdnuraAtGstnImpl implements Gstr1InvoicesAtGstn {

	@Autowired
	@Qualifier("gstr1CdnurCdnuraDataAtGstnImpl")
	Gstr1CdnurCdnuraDataAtGstnImpl gstr1CdnurCdnuraDataAtGstnImpl;

	@Override
	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type,Long batchId) {
		Long reqId = null;

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			/*batch = batchUtil.makeBatchGstr1(dto, type);
			TenantContext.setTenantId(groupCode);
			// InActiveting Previous Batch Records
			batchRepo.softlyDelete(type.toUpperCase(),
					APIConstants.GSTR1.toUpperCase(), dto.getGstin(),
					dto.getReturnPeriod());
			// Save new Batch
			batch = batchRepo.save(batch);*/

			if (dto.getGroupcode() == null
					|| dto.getGroupcode().trim().length() == 0) {
				dto.setGroupcode(groupCode);
			}
			dto.setType(type);
			dto.setBatchId(batchId);
			String json = gson.toJson(dto);

			reqId = gstr1CdnurCdnuraDataAtGstnImpl
					.findCdnurCdnuraDataAtGstn(dto, groupCode, type, json);

		} catch (Exception ex) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(ex.getMessage());
			}
			throw new APIException(
					"Unexpected error while executing Gstr1 Get Cdnur/Cdnura");

		}
		return reqId;
	}
}
