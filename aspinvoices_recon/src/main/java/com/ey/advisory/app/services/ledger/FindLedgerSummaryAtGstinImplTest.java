package com.ey.advisory.app.services.ledger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.GetSummarizedLedgerBalanceEntity;
import com.ey.advisory.app.docs.dto.ledger.LedgerBalanceJobTriggerDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("FindLedgerSummaryAtGstinImplTest")
public class FindLedgerSummaryAtGstinImplTest implements FindLedgerSummaryAtGstin {

	@Autowired
	@Qualifier(value = "LedgerBalanceParserImpl")
	private LedgerBalanceParser ledgerBalanceParser;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService gstnAuthService;

	@Override
	public List<GetSummarizedLedgerBalanceEntity> findLedgerSummAtGstinLevl(
			LedgerBalanceJobTriggerDto reqDto) {

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



		
		//from date and to date doubt what to pass
		for (String gstin : activeGstins) {
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", returnPeriod);
			APIParam param3 = new APIParam("fr_dt", returnPeriod);
			APIParam param4 = new APIParam("to_dt", returnPeriod);
			APIParam param5 = new APIParam("from_date", fmtFromDate);
			APIParam param6 = new APIParam("to_date", fmtToDate);
			/*	APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
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
			APIResponse negativeDetailsResp = apiExecutor.execute(params, null);*/
			
			/*if (!cashCreditResp.isSuccess()) {
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
			}*/
			
			/*
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
			String apiNegativeDetailsResp=negativeDetailsResp.getResponse();*/
			String apiRcmDetailsResp=  "{\n" +
	                "  \"gstin\": \"24MAYAS0100E9Z9\",\n" +
	                "  \"clsbal\": {\n" +
	                "    \"igst\": 100,\n" +
	                "    \"cgst\": 200,\n" +
	                "    \"sgst\": 200,\n" +
	                "    \"cess\": 100\n" +
	                "  }\n" +
	                "}";
			

			String apiNegativeDetailsResp= "{\n" +
	                "  \"openbal\": [\n" +
	                "    {\n" +
	                "      \"desc\": \"Other than reverse charge\",\n" +
	                "      \"trancd\": 30002,\n" +
	                "      \"igst\": 36425,\n" +
	                "      \"cgst\": 36425,\n" +
	                "      \"sgst\": 36425,\n" +
	                "      \"cess\": 36425\n" +
	                "    },\n" +
	                "    {\n" +
	                "      \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
	                "      \"trancd\": 30003,\n" +
	                "      \"igst\": 36425,\n" +
	                "      \"cgst\": 36425,\n" +
	                "      \"sgst\": 36425,\n" +
	                "      \"cess\": 36425\n" +
	                "    }\n" +
	                "  ],\n" +
	                "  \"closebal\": [\n" +
	                "    {\n" +
	                "      \"desc\": \"Other than reverse charge\",\n" +
	                "      \"trancd\": 30002,\n" +
	                "      \"igst\": 36425,\n" +
	                "      \"cgst\": 36425,\n" +
	                "      \"sgst\": 36425,\n" +
	                "      \"cess\": 36425\n" +
	                "    },\n" +
	                "    {\n" +
	                "      \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
	                "      \"trancd\": 30003,\n" +
	                "      \"igst\": 36425,\n" +
	                "      \"cgst\": 36425,\n" +
	                "      \"sgst\": 36425,\n" +
	                "      \"cess\": 36425\n" +
	                "    }\n" +
	                "  ],\n" +
	                "  \"negliabdtls\": [\n" +
	                "    {\n" +
	                "      \"rtnprd\": \"092019\",\n" +
	                "      \"refno\": \"ab125632512123c\",\n" +
	                "      \"trantyp\": \"Debit\",\n" +
	                "      \"trandate\": \"12/10/2019\",\n" +
	                "      \"desc\": \"Negative liability for GSTR3B/3BQ\",\n" +
	                "      \"negliab\": [\n" +
	                "        {\n" +
	                "          \"desc\": \"Other than reverse charge\",\n" +
	                "          \"trancd\": 30002,\n" +
	                "          \"igst\": 0,\n" +
	                "          \"cgst\": 2323,\n" +
	                "          \"sgst\": 656,\n" +
	                "          \"cess\": 656\n" +
	                "        },\n" +
	                "        {\n" +
	                "          \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
	                "          \"trancd\": 30003,\n" +
	                "          \"igst\": 0,\n" +
	                "          \"cgst\": 2323,\n" +
	                "          \"sgst\": 656,\n" +
	                "          \"cess\": 656\n" +
	                "        }\n" +
	                "      ],\n" +
	                "      \"negliabal\": [\n" +
	                "        {\n" +
	                "          \"desc\": \"Other than reverse charge\",\n" +
	                "          \"trancd\": 30002,\n" +
	                "          \"igst\": 0,\n" +
	                "          \"cgst\": 2323,\n" +
	                "          \"sgst\": 656,\n" +
	                "          \"cess\": 656\n" +
	                "        },\n" +
	                "        {\n" +
	                "          \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
	                "          \"trancd\": 30003,\n" +
	                "          \"igst\": 0,\n" +
	                "          \"cgst\": 2323,\n" +
	                "          \"sgst\": 656,\n" +
	                "          \"cess\": 656\n" +
	                "        }\n" +
	                "      ]\n" +
	                "    },\n" +
	                "    {\n" +
	                "      \"rtnprd\": \"092019\",\n" +
	                "      \"refno\": \"ab125632512123c\",\n" +
	                "      \"trantyp\": \"Credit\",\n" +
	                "      \"trandate\": \"12/10/2019\",\n" +
	                "      \"desc\": \"Negative liability for GSTR3B/3BQ\",\n" +
	                "      \"negliab\": [\n" +
	                "        {\n" +
	                "          \"desc\": \"Other than reverse charge\",\n" +
	                "          \"trancd\": 30002,\n" +
	                "          \"igst\": 1212,\n" +
	                "          \"cgst\": 0,\n" +
	                "          \"sgst\": 0,\n" +
	                "          \"cess\": 0\n" +
	                "        },\n" +
	                "        {\n" +
	                "          \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
	                "          \"trancd\": 30003,\n" +
	                "          \"igst\": 1212,\n" +
	                "          \"cgst\": 0,\n" +
	                "          \"sgst\": 0,\n" +
	                "          \"cess\": 0\n" +
	                "        }\n" +
	                "      ],\n" +
	                "      \"negliabal\": [\n" +
	                "        {\n" +
	                "          \"desc\": \"Other than reverse charge\",\n" +
	                "          \"trancd\": 30002,\n" +
	                "          \"igst\": 1212,\n" +
	                "          \"cgst\": 0,\n" +
	                "          \"sgst\": 0,\n" +
	                "          \"cess\": 0\n" +
	                "        },\n" +
	                "        {\n" +
	                "          \"desc\": \"Reverse charge and supplies made u/s 9(5)\",\n" +
	                "          \"trancd\": 30003,\n" +
	                "          \"igst\": 1212,\n" +
	                "          \"cgst\": 0,\n" +
	                "          \"sgst\": 0,\n" +
	                "          \"cess\": 0\n" +
	                "        }\n" +
	                "      ]\n" +
	                "    }\n" +
	                "  ]\n" +
	                "}";
			
			if (apiRcmDetailsResp==null && apiNegativeDetailsResp==null)
				continue;
			GetSummarizedLedgerBalanceEntity ledgerBalanceEntity = ledgerBalanceParser
					.parseLedgerBalanceData(gstin, returnPeriod, null,
							null, reqDto.getJobId(),null
							,apiRcmDetailsResp,apiNegativeDetailsResp);
			ledgerBalanceEntities.add(ledgerBalanceEntity);
		}
		return ledgerBalanceEntities;
	
	}
public static void main(String[] args) {
	
	LocalDate toDate = LocalDate.parse("2024-10-15");
	//LocalDate startOfMonth = toDate.withDayOfMonth(1);
	//String fmtFromDate = EYDateUtil.fmtLocalDate(startOfMonth);

	String fmtToDate = EYDateUtil.fmtLocalDate(toDate);
	
	System.out.println(fmtToDate);
}
}
