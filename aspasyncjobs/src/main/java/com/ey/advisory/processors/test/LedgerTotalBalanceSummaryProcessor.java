package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.asprecon.LedgerSummaryBalanceRepository;
import com.ey.advisory.app.docs.dto.ledger.LedgerBalanceJobTriggerDto;
import com.ey.advisory.app.services.jobs.erp.processedrecords.ProcessRecTaxPeriodsFinder;
import com.ey.advisory.app.services.ledger.LedgerTotalBalanceSummService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service("LedgerTotalBalanceSummaryProcessor")
@Slf4j
public class LedgerTotalBalanceSummaryProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;

	@Autowired
	@Qualifier("LedgerTotalBalanceSummServiceImpl")
	LedgerTotalBalanceSummService ledgerTotBalServ;

	@Autowired
	@Qualifier("ProcessRecTaxPeriodsFinderImpl")
	ProcessRecTaxPeriodsFinder taxPeriodFinder; 
	
	@Autowired
	LedgerSummaryBalanceRepository ledgerSummaryBalance;
	
	@Override
	public void execute(Message message, AppExecContext context) {
		String groupcode = message.getGroupCode();
		String json = message.getParamsJson();
		LOGGER.debug(
				"LedgerTotalBalanceSummary  Data Execute method is ON with "
						+ "groupcode {} and params {}",
				groupcode, json);
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			LedgerBalanceJobTriggerDto dto = gson.fromJson(json,
					LedgerBalanceJobTriggerDto.class);
		//	Long entityId = dto.getEntityId();
			dto.setJobId(message.getId());
			dto.setGroupcode(groupcode);
			List<String> gstins = dto.getGstins();
			//updating status
			LocalDateTime dateAndTime = LocalDateTime.now();
			ledgerSummaryBalance.updateStatusAndLastUpdatedDate("InProgress", dateAndTime, gstins);
			String taxPeriod = dto.getTaxPeriod();
			if (!CollectionUtils.isEmpty(gstins) && taxPeriod != null) {
				dto.setGstins(gstins);
				LOGGER.debug("About to Refresh Ledger with input params {}", dto);
				ledgerTotBalServ.getLedgerTotalBalance(dto);
			} else {
				LOGGER.debug(
						"Partial Request Params groupcode, "
								+ "taxperiod, entityId and gstin are mandatory",
						dto);
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while Ledger Refresh, jsonParams are '%s'",
					json);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

}