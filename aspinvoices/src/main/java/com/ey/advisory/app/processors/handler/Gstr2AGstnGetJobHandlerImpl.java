/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr2aDataAtGstn;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Service("Gstr2AGstnGetJobHandlerImpl")
@Slf4j
public class Gstr2AGstnGetJobHandlerImpl implements Gstr2AGstnGetJobHandler {

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("Gstr2aDataAtGstnImpl")
	private Gstr2aDataAtGstn gstr2aDataAtGstn;

	private static final List<String> SECTIONS = ImmutableList.of(
			APIConstants.B2B, APIConstants.B2BA, APIConstants.CDN,
			APIConstants.CDNA, APIConstants.ISD, APIConstants.ISDA,
			APIConstants.TDS, APIConstants.TDSA, APIConstants.TCS,
			APIConstants.AMDHIST, APIConstants.IMPG, APIConstants.IMPGSEZ,
			APIConstants.ECOM, APIConstants.ECOMA);

	@Override
	public void gstr2AGstnGetCall(String jsonReq, String groupCode) {

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(requestObject,
					Gstr1GetInvoicesReqDto.class);
			TenantContext.setTenantId(groupCode);

			GetAnx1BatchEntity batch = batchRepo
					.findByIdAndIsDeleteFalse(dto.getBatchId());
			Long requestId = new Long(0);

			if (batch != null && batch.getType() != null) {
				if (SECTIONS.contains(batch.getType().toLowerCase())) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Gstr2A {} Get Call is Started.",
								batch.getType());
					}
					requestId = findInvFromGstn(dto, groupCode, batch.getType(),
							batch.getId(), batch.getApiSection());

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Gstr2A {} Get Call is Ended.",
								batch.getType());
					}
				} else {
					LOGGER.error(
							"This section is not handled in GSTR2A GET  {} :",
							batch.getType());
					return;
				}
			} else {
				LOGGER.error(
						"batch.getType() has NULL value for the params {} in GSTR2A GET client {} :",
						jsonReq, groupCode);
				return;
			}
			batch.setStatus(JobStatusConstants.IN_PROGRESS.toUpperCase());
			batch.setRequestId(requestId);
			batchRepo.save(batch);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new AppException(e.getMessage(), e);
		}

	}

	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto, String groupCode,
			String type, Long batchId, String apiSection) {

		Long reqId = null;
		Gson gson = GsonUtil.newSAPGsonInstance();

		if (dto.getGroupcode() == null
				|| dto.getGroupcode().trim().length() == 0) {
			dto.setGroupcode(groupCode);
		}
		dto.setBatchId(batchId);
		dto.setType(type);
		dto.setApiSection(apiSection);
		String json = gson.toJson(dto);
		reqId = gstr2aDataAtGstn.findGstr2aDataAtGstn(dto, groupCode, type,
				json);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2A {} Get Call BatchId {} and framework requestId {} .",
					type, batchId, reqId);
		}

		return reqId;
	}
}
