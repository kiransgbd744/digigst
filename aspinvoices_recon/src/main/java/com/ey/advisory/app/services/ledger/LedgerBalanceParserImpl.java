package com.ey.advisory.app.services.ledger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.GetSummarizedLedgerBalanceEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.LedgerSummaryBalanceRepository;
import com.ey.advisory.app.docs.dto.ledger.CashLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.CrReversalLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.CreditReversalAndReClaimDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.LiabilityLedgersDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.NegativeDetailsRespDto;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("LedgerBalanceParserImpl")
public class LedgerBalanceParserImpl implements LedgerBalanceParser {
	
	@Autowired
	@Qualifier(value = "LedgerSummaryBalanceRepository")
	private LedgerSummaryBalanceRepository ledgerSummaryBalance;


	@Override
	public GetSummarizedLedgerBalanceEntity parseLedgerBalanceData(String gstin,
			String taxPeriod, String cashcrResp, String liabilityResp,
			Long jobId, String apiCrRevAndCrReclaimResp,
			String apiRcmDetailsResp, String apiNegativeDetailsResp) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject responseObject = null;
		JsonObject responseObjectLib = null;
		JsonObject responseObjectCrRev = null;
		JsonObject responseObjectRcm = null;
		JsonObject responseObjectNegative = null;
		List<String> gstinList= new ArrayList<>();
		gstinList.add(gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Parsing Data For LedgerBalanceSummary Api Response ");
		}
		LocalDateTime curTime = LocalDateTime.now();
		GetSummarizedLedgerBalanceEntity entity = new GetSummarizedLedgerBalanceEntity();
		entity.setSupplierGstin(gstin);
		entity.setTaxPeriod(taxPeriod);
		entity.setLastRefreshDate(curTime);
		entity.setLastUpdatedDate(curTime);
		entity.setRefreshJobId(jobId);
		entity.setRefreshStatus("Active");
		entity.setStatus("Success");
		entity.setGetCallStatusTimeStamp(curTime);

		try {
			if (cashcrResp != null) {
				responseObject = JsonParser.parseString(cashcrResp)
						.getAsJsonObject();
				JsonObject cashBal = responseObject.get("cash_bal")
						.getAsJsonObject();
				entity.setCashIgstTot_bal(cashBal.get("igst_tot_bal") != null
						? cashBal.get("igst_tot_bal").getAsBigDecimal()
						: BigDecimal.ZERO);
				entity.setCashCgstTotBal(cashBal.get("cgst_tot_bal") != null
						? cashBal.get("cgst_tot_bal").getAsBigDecimal()
						: BigDecimal.ZERO);
				entity.setCashSgstTotBal(cashBal.get("sgst_tot_bal") != null
						? cashBal.get("sgst_tot_bal").getAsBigDecimal()
						: BigDecimal.ZERO);
				entity.setCashCessTotBal(cashBal.get("cess_tot_bal") != null
						? cashBal.get("cess_tot_bal").getAsBigDecimal()
						: BigDecimal.ZERO);

				JsonObject itcBal = responseObject.get("itc_bal")
						.getAsJsonObject();

				entity.setItcCgstTotbal(itcBal.get("cgst_bal") != null
						? itcBal.get("cgst_bal").getAsBigDecimal()
						: BigDecimal.ZERO);
				entity.setItcIgstTotBal(itcBal.get("igst_bal") != null
						? itcBal.get("igst_bal").getAsBigDecimal()
						: BigDecimal.ZERO);
				entity.setItcSgstTotBal(itcBal.get("sgst_bal") != null
						? itcBal.get("sgst_bal").getAsBigDecimal()
						: BigDecimal.ZERO);
				entity.setItcCessTotBal(itcBal.get("cess_bal") != null
						? itcBal.get("cess_bal").getAsBigDecimal()
						: BigDecimal.ZERO);
			}
			if (liabilityResp != null) {
				responseObjectLib = JsonParser.parseString(liabilityResp)
						.getAsJsonObject();
				LiabilityLedgersDetailsDto liabLedgerDetDto = gson.fromJson(
						responseObjectLib, LiabilityLedgersDetailsDto.class);

				CashLedgerOpenCloseBalDto liabLedgerCloseBal = liabLedgerDetDto
						.getCloseBal();

				entity.setLibCessTotBal(new BigDecimal(
						liabLedgerCloseBal.getCessbal().getTotal().toString()));
				entity.setLibCgstTotBal(new BigDecimal(
						liabLedgerCloseBal.getCgstbal().getTotal().toString()));
				entity.setLibIgstTotbal(new BigDecimal(
						liabLedgerCloseBal.getIgstbal().getTotal().toString()));
				entity.setLibSgstTotBal(new BigDecimal(
						liabLedgerCloseBal.getSgstbal().getTotal().toString()));

			}

			if (apiCrRevAndCrReclaimResp != null) {
				responseObjectCrRev = JsonParser
						.parseString(apiCrRevAndCrReclaimResp)
						.getAsJsonObject();

				CreditReversalAndReClaimDetailsDto CrRevAndCrReclaimDto = gson
						.fromJson(responseObjectCrRev,
								CreditReversalAndReClaimDetailsDto.class);

				CrReversalLedgerOpenCloseBalDto CrRevAndCrReclaimDtoBal = CrRevAndCrReclaimDto
						.getCloseBal();

				entity.setCrRevCessTotBal(CrRevAndCrReclaimDtoBal.getCess());
				entity.setCrRevCgstTotBal(CrRevAndCrReclaimDtoBal.getCgst());
				entity.setCrRevIgstTotbal(CrRevAndCrReclaimDtoBal.getIgst());
				entity.setCrRevSgstTotBal(CrRevAndCrReclaimDtoBal.getSgst());
			}
			if (apiRcmDetailsResp != null) {
				responseObjectRcm = JsonParser.parseString(apiRcmDetailsResp)
						.getAsJsonObject();

				CreditReversalAndReClaimDetailsDto rcmDetailsDto = gson
						.fromJson(responseObjectRcm,
								CreditReversalAndReClaimDetailsDto.class);

				CrReversalLedgerOpenCloseBalDto rcmDetailsDtoBal = rcmDetailsDto
						.getCloseBal();

				entity.setRcmCessTotBal(rcmDetailsDtoBal.getCess());
				entity.setRcmCgstTotBal(rcmDetailsDtoBal.getCgst());
				entity.setRcmIgstTot_bal(rcmDetailsDtoBal.getIgst());
				entity.setRcmSgstTotBal(rcmDetailsDtoBal.getSgst());
			}
			if (apiNegativeDetailsResp != null) {
				responseObjectNegative = JsonParser
						.parseString(apiNegativeDetailsResp).getAsJsonObject();

				CreditReversalAndReClaimDetailsDto CrRevAndCrReclaimDto = gson
						.fromJson(responseObjectNegative,
								CreditReversalAndReClaimDetailsDto.class);

				List<CrReversalLedgerOpenCloseBalDto> closeBalList = CrRevAndCrReclaimDto
						.getCloseBals();
			//	NegativeDetailsRespDto respCloseBal = new NegativeDetailsRespDto();
				  if (closeBalList != null && !closeBalList.isEmpty()) {
			        	CrReversalLedgerOpenCloseBalDto clsBalOther = closeBalList.get(0);
			        	CrReversalLedgerOpenCloseBalDto clsBalRevChg = closeBalList.get(1);
			        	
			        	NegativeDetailsRespDto respCloseBal = new NegativeDetailsRespDto();
						respCloseBal
								.setClsBalOtherIgst(clsBalOther.getIgst().toString());
						respCloseBal.setClsBalOtherCgst(clsBalOther.getCgst().toString());
						respCloseBal.setClsBalOtherSgst(clsBalOther.getSgst().toString());
						respCloseBal.setClsBalOtherCess(clsBalOther.getCess().toString());

						respCloseBal.setClsBalRevChargeIgst(clsBalRevChg.getIgst().toString());
						respCloseBal.setClsBalRevChargeCgst(clsBalRevChg.getCgst().toString());
						respCloseBal.setClsBalRevChargeSgst(clsBalRevChg.getSgst().toString());
						respCloseBal.setClsBalRevChargeCess(clsBalRevChg.getCess().toString());
						
						
					entity.setNegativeIgstTot_bal(
							new BigDecimal(respCloseBal.getClsBalOtherIgst())
									.add(new BigDecimal(respCloseBal
											.getClsBalRevChargeIgst())));

					entity.setNegativeCgstTotBal(
							new BigDecimal(respCloseBal.getClsBalOtherCgst())
									.add(new BigDecimal(respCloseBal
											.getClsBalRevChargeCgst())));

					entity.setNegativeSgstTotBal(
							new BigDecimal(respCloseBal.getClsBalOtherSgst())
									.add(new BigDecimal(respCloseBal
											.getClsBalRevChargeSgst())));

					entity.setNegativeCessTotBal(
							new BigDecimal(respCloseBal.getClsBalOtherCess())
									.add(new BigDecimal(respCloseBal
											.getClsBalRevChargeCess())));

					
				  }
			}

		} catch (Exception ex) {
			LOGGER.error(
					"Exception while populating the GSTN reponse to Summary Dto",
					ex);
			
			ledgerSummaryBalance.updateStatusAndLastUpdatedDate("Failed", curTime, gstinList);
		}
		return entity;
	}

}
