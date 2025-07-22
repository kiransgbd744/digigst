/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx2;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Itc04SectionSummaryISDEntity;
import com.ey.advisory.app.data.repositories.client.Itc04SectionSummaryRepository;
import com.ey.advisory.app.docs.dto.anx1.Itc04GetSummaryDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("Itc04SummaryDataParserImpl")
@Slf4j
public class Itc04SummaryDataParserImpl {

	@Autowired
	@Qualifier("Itc04SectionSummaryRepository")
	private Itc04SectionSummaryRepository itc04SectionSummaryRepository;

	public void itc04SummaryData(Anx2GetInvoicesReqDto dto, String apiResp,
			Long batchId) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			Itc04GetSummaryDto jsonSummaryData = gson.fromJson(apiResp,
					Itc04GetSummaryDto.class);
			itc04SectionSummaryRepository.softlyDeleteBasedOnGstinAndTaxPeriod(dto.getGstin(), dto.getReturnPeriod());
			
			Itc04SectionSummaryISDEntity entity=new Itc04SectionSummaryISDEntity();
			entity.setBatchId(batchId);
			entity.setGstin(dto.getGstin());
			entity.setReturnPeriod(dto.getReturnPeriod());
			entity.setTotalTaxableValue(new BigDecimal(jsonSummaryData.getM2j_ttl_tax()));
			entity.setCheckSumTable4(jsonSummaryData.getM2jChksum());
			entity.setTotalRecTable4(jsonSummaryData.getM2jNoOfChallanRec());
			entity.setCheckSumTable5a(jsonSummaryData.getTable5AChksum());
			entity.setTotalRecTable5A(jsonSummaryData.getTable5aNo());
			entity.setCheckSumTable5b(jsonSummaryData.getTable5BChksum());
			entity.setTotalRecTable5B(jsonSummaryData.getTable5bNo());
			entity.setCreatedOn(LocalDateTime.now());
			entity.setCheckSumTable5c(jsonSummaryData.getTable5cChksum());
			entity.setTotalRecTable5C(jsonSummaryData.getTable5cNo());
			itc04SectionSummaryRepository.save(entity);
		} catch (Exception e) {
			String msg = "failed to parse itc04 summary response";
			LOGGER.error(msg, e);

		}

	}

}