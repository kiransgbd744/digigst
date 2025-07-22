package com.ey.advisory.app.services.jobs.gstr1;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1DocIssuedEntity;
import com.ey.advisory.app.docs.dto.DocIssueDetails;
import com.ey.advisory.app.docs.dto.DocIssueInvoices;
import com.ey.advisory.app.docs.dto.DocIssueList;
import com.ey.advisory.app.services.common.Gstr1GetKeyGenerator;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

@Component("gstr1DocIssuesDataParserImpl")
public class Gstr1DocIssuesDataParserImpl implements Gstr1DocIssuesDataParser {

	@Autowired
	private Gstr1GetKeyGenerator gstr1GetKeyGenerator;

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr1DocIssuesDataParserImpl.class);

	@Override
	public List<GetGstr1DocIssuedEntity> dataParseFromGstr1(Gstr1GetInvoicesReqDto dto, String apiResp) {

		JsonObject jsonObject = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<GetGstr1DocIssuedEntity> entities = new ArrayList<>();
		//try {
			if (APIConstants.DOC_ISSUE.equalsIgnoreCase(dto.getType())) {
				jsonObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
						.get(APIConstants.DOC_ISSUE.toLowerCase()).getAsJsonObject();
			}

			Type listType = new TypeToken<DocIssueInvoices>() {
			}.getType();

			DocIssueInvoices docIssuesInvoices = gson.fromJson(jsonObject, listType);

			List<DocIssueDetails> docIssueDetails = docIssuesInvoices.getDocIssueDetails();
			docIssueDetails.forEach(docIssueD -> {
				List<DocIssueList> docIssueList = docIssueD.getDocIssueList();
				docIssueList.forEach(docIssueL -> {
					// New  Entity
					GetGstr1DocIssuedEntity docIssuesInvoicesEntity = new GetGstr1DocIssuedEntity();

					/**
					 * Input data
					 */
					docIssuesInvoicesEntity.setGstin(dto.getGstin());
					docIssuesInvoicesEntity.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
						docIssuesInvoicesEntity.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					docIssuesInvoicesEntity.setBatchId(dto.getBatchId());
					
					docIssuesInvoicesEntity.setInvChksum(docIssuesInvoices.getCheckSum());
					docIssuesInvoicesEntity.setFlag(docIssuesInvoices.getInvoiceStatus());
					docIssuesInvoicesEntity.setDocNum(docIssueD.getDocNum());
					docIssuesInvoicesEntity.setSerialNum(docIssueL.getSerialNum());
					docIssuesInvoicesEntity.setFromSerialNum(docIssueL.getFromSerialNum());
					docIssuesInvoicesEntity.setToSerialNum(docIssueL.getToSerialNum());
					docIssuesInvoicesEntity.setTotNum(docIssueL.getTotalNum());
					docIssuesInvoicesEntity.setCancel(docIssueL.getCancelled());
					docIssuesInvoicesEntity.setNetIssue(docIssueL.getNetIssued());

					String sgtin = dto.getGstin();
					String retPeriod = dto.getReturnPeriod();
					String serialNo = String.valueOf(docIssueL.getSerialNum());
					String from = docIssueL.getFromSerialNum();
					String to = docIssueL.getToSerialNum();
					docIssuesInvoicesEntity
							.setDocKey(gstr1GetKeyGenerator.generateDocKey(sgtin, retPeriod, serialNo, from, to));
					entities.add(docIssuesInvoicesEntity);
				});
			});
		/*} catch (Exception e) {
			String msg = "Failed to Parse Doc Issue Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}*/
		return entities;
	}

}
