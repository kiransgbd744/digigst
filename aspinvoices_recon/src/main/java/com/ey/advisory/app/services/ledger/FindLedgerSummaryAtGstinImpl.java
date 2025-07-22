package com.ey.advisory.app.services.ledger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.GetSummarizedLedgerBalanceEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.LedgerSummaryBalanceRepository;
import com.ey.advisory.app.docs.dto.ledger.LedgerBalanceJobTriggerDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("FindLedgerSummaryAtGstinImpl")
public class FindLedgerSummaryAtGstinImpl implements FindLedgerSummaryAtGstin {

	@Autowired
	@Qualifier(value = "LedgerBalanceParserImpl")
	private LedgerBalanceParser ledgerBalanceParser;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService gstnAuthService;
	
	@Autowired
	@Qualifier(value = "LedgerSummaryBalanceRepository")
	private LedgerSummaryBalanceRepository ledgerSummaryBalance;

	@Override
	public List<GetSummarizedLedgerBalanceEntity> findLedgerSummAtGstinLevl(
			LedgerBalanceJobTriggerDto reqDto) {
		LocalDateTime dateAndTime = LocalDateTime.now();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("I am from findLedgerSummAtGstinLevl Service  method: "
					+ "with following Request Params {}", reqDto);
		}
		List<GetSummarizedLedgerBalanceEntity> ledgerBalanceEntities = new ArrayList<>();

		List<String> gstins = reqDto.getGstins();
		List<String> activeGstins = gstins.stream()
				.filter(gstin -> "A".equals(
						gstnAuthService.getAuthTokenStatusForGstin(gstin)))
				.collect(Collectors.toList());
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("After Filtering the Active gstins are {}",
					activeGstins);
		String groupCode = reqDto.getGroupcode();
		String returnPeriod = reqDto.getTaxPeriod();
		//LocalDate fromDate = 
		LocalDate toDate = LocalDate.now();
		LocalDate startOfMonth = toDate.withDayOfMonth(1);
		String fmtFromDate = EYDateUtil.fmtLocalDate(startOfMonth);

		String fmtToDate = EYDateUtil.fmtLocalDate(toDate);



		List<String> actgstin=null;
		for (String gstin : activeGstins) {
			try {
			actgstin= new ArrayList<>();
			actgstin.add(gstin);
				APIParam param1 = new APIParam("gstin", gstin);
				APIParam param2 = new APIParam("ret_period", returnPeriod);
				APIParam param3 = new APIParam("fr_dt", returnPeriod);
				APIParam param4 = new APIParam("to_dt", returnPeriod);
				APIParam param5 = new APIParam("from_date", fmtFromDate);
				APIParam param6 = new APIParam("to_date", fmtToDate);
				APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
						APIIdentifiers.LEDGER_GET_BAL, param1, param2);
				APIResponse cashCreditResp = apiExecutor.execute(params, null);
				params = new APIParams(groupCode, APIProviderEnum.GSTN,
						APIIdentifiers.LEDGER_GET_TAX, param1, param2, param3,
						param4);
				APIResponse respLibApi = apiExecutor.execute(params, null);
				
				//added new Api credit reversal and reclaim
				params = new APIParams(groupCode, APIProviderEnum.GSTN,
						APIIdentifiers.LEDGER_GET_CRREV_RECLAIM, param1);
				APIResponse crRevAndCrReclaimResp = apiExecutor.execute(params, null);
				
				params = new APIParams(groupCode, APIProviderEnum.GSTN,
						APIIdentifiers.LEDGER_GET_LIABILITY_RCM, param1);
				APIResponse rcmDetailsResp = apiExecutor.execute(params, null);
				
				params = new APIParams(groupCode, APIProviderEnum.GSTN,
						APIIdentifiers.NEGATIVE_LIABILITY, param1,param5,
						param6);
				APIResponse negativeDetailsResp = apiExecutor.execute(params, null);
				
				if (!cashCreditResp.isSuccess()) {
					String msg = String.format(
							"Failed to get Ledger Cash/Credit Balance"
									+ " for Gstn '%s' and Taxperiod '%s'. The Response"
									+ " for Cash/Credit is '%s'",
							gstin, returnPeriod, cashCreditResp.getError());
					LOGGER.error(msg);
				}
				if (!respLibApi.isSuccess()) {
					String msg = String.format(
							"Failed to get Liability Balance"
									+ " for Gstn '%s' and Taxperiod '%s'. The Response"
									+ " for Liability is '%s'",
							gstin, returnPeriod, respLibApi.getError());
					LOGGER.error(msg);
				}
				if (!crRevAndCrReclaimResp.isSuccess()) {
					String msg = String.format(
							"Failed to get credit Reversal and credit reclaim Balance"
									+ " for Gstn '%s' and Taxperiod '%s'. The Response"
									+ " for credit Reversal and credit reclaim is '%s'",
							gstin, returnPeriod, crRevAndCrReclaimResp.getError());
					LOGGER.error(msg);
				}
				if (!rcmDetailsResp.isSuccess()) {
					String msg = String.format(
							"Failed to get Rcm Details "
									+ " for Gstn '%s' . The Response"
									+ " for Rcm Details is '%s'",
							gstin, rcmDetailsResp.getError());
					LOGGER.error(msg);
				}
				if (!negativeDetailsResp.isSuccess()) {
					String msg = String.format(
							"Failed to get negative Details "
									+ " for Gstn '%s'. The Response"
									+ " for negative Details is '%s'",
							gstin, negativeDetailsResp.getError());
					LOGGER.error(msg);
				}
				String apiResp = cashCreditResp.getResponse();
				String apiLibResp = respLibApi.getResponse();
				String apiCrRevAndCrReclaimResp=crRevAndCrReclaimResp.getResponse();
				String apiRcmDetailsResp=rcmDetailsResp.getResponse();
				String apiNegativeDetailsResp=negativeDetailsResp.getResponse();

				if (apiResp == null && apiLibResp == null && apiCrRevAndCrReclaimResp == null
						&& apiRcmDetailsResp==null && apiNegativeDetailsResp==null)
					continue;
				GetSummarizedLedgerBalanceEntity ledgerBalanceEntity = ledgerBalanceParser
						.parseLedgerBalanceData(gstin, returnPeriod, apiResp,
								apiLibResp, reqDto.getJobId(),apiCrRevAndCrReclaimResp
								,apiRcmDetailsResp,apiNegativeDetailsResp);
				ledgerBalanceEntities.add(ledgerBalanceEntity);
			
				
			} catch (Exception e) {
				LOGGER.debug("get call is failed " + " for GSTIN {} ", gstin);
				ledgerSummaryBalance.updateStatusAndLastUpdatedDate("Failed",
						dateAndTime, actgstin);

			}
			}
		return ledgerBalanceEntities;
	
	}

}
