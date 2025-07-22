package com.ey.advisory.app.services.ledger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.ledger.CrReversalLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.CreditRevAndReclaimTransactionDto;
import com.ey.advisory.app.docs.dto.ledger.CreditReverseAndReclaimLedgerDto;
import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;
import com.ey.advisory.app.docs.dto.ledger.ItcCrReversalAndReclaimDto;
import com.ey.advisory.app.docs.dto.ledger.LedgerItcReclaimBalanceAmts;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author kiran s
 *
 */
@Slf4j
@Service("GetCreditClaimAndReverseBalDetailsImpl")
public class GetCreditClaimAndReverseBalDetailsImpl
		implements GetCreditClaimAndReverseBalDetails {

	@Autowired
	@Qualifier("iTCLedgerDetailsDataAtGstnImpl")
	private ITCLedgerDetailsDataAtGstn itcDataAtGstn;

	@Autowired
	@Qualifier("cashLedgerDetailsDataAtGstnImpl")
	private CashLedgerDetailsDataAtGstn crReversalAndReclaim;

	@Override
	public List<ItcCrReversalAndReclaimDto> findCrReversalAndReclaim(
			String jsonReq) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		List<ItcCrReversalAndReclaimDto> allDetailsDto = new ArrayList<>();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonReq)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			GetCashLedgerDetailsReqDto dto = gson.fromJson(reqObject,
					GetCashLedgerDetailsReqDto.class);
			String apiResp = crReversalAndReclaim
					.getCreditReversalAndReclaimfromGstnTest(dto);
			if (apiResp != null) {
				JsonObject reqObj = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				CreditReverseAndReclaimLedgerDto respDto = gson.fromJson(
						reqObj,
						CreditReverseAndReclaimLedgerDto.class);
				CrReversalLedgerOpenCloseBalDto closeBal = respDto
						.getClosenBal();
				/*
				 * CrReversalLedgerOpenCloseBalDto openBal = respDto
				 * .getOpenBal();
				 */
				List<CreditRevAndReclaimTransactionDto> respTransDtos = respDto
						.getTransTypeBalDto();

				if (respTransDtos != null) {
					  AtomicInteger srNoCounter = new AtomicInteger(1);
					respTransDtos.forEach(obj -> {
						ItcCrReversalAndReclaimDto transBal = new ItcCrReversalAndReclaimDto();
						transBal.setItcTransDate(obj.getTransDate()!=null ? obj.getTransDate() :null);
						transBal.setDesc(obj.getDescription()!=null ? obj.getDescription():null);
						transBal.setRefNo(obj.getReferenceNo()!=null ? obj.getReferenceNo(): null);
						transBal.setTaxPeriod(obj.getRtnprd()!=null ? obj.getRtnprd(): null);
						 // transBal.setSrNo(String.valueOf(srNoCounter.getAndIncrement()));
						LedgerItcReclaimBalanceAmts itc4a5 = obj.getItc4a5();
						if (itc4a5 != null) {
						    transBal.setTable4a5Igst(itc4a5.getIgst() != null ? itc4a5.getIgst().toString() : "0");
						    transBal.setTable4a5Cgst(itc4a5.getCgst() != null ? itc4a5.getCgst().toString() : "0");
						    transBal.setTable4a5Sgst(itc4a5.getSgst() != null ? itc4a5.getSgst().toString() : "0");
						    transBal.setTable4a5Cess(itc4a5.getCess() != null ? itc4a5.getCess().toString() : "0");
						}

						// Retrieve values from itc4b2 and set in transBal
						LedgerItcReclaimBalanceAmts itc4b2 = obj.getItc4b2();
						if (itc4b2 != null) {
						    transBal.setTable4b2Igst(itc4b2.getIgst() != null ? itc4b2.getIgst().toString() : "0");
						    transBal.setTable4b2Cgst(itc4b2.getCgst() != null ? itc4b2.getCgst().toString() : "0");
						    transBal.setTable4b2Sgst(itc4b2.getSgst() != null ? itc4b2.getSgst().toString() : "0");
						    transBal.setTable4b2Cess(itc4b2.getCess() != null ? itc4b2.getCess().toString() : "0");
						}

						// Retrieve values from itc4d1 and set in transBal
						LedgerItcReclaimBalanceAmts itc4d1 = obj.getItc4d1();
						if (itc4d1 != null) {
						    transBal.setTable4d1Igst(itc4d1.getIgst() != null ? itc4d1.getIgst().toString() : "0");
						    transBal.setTable4d1Cgst(itc4d1.getCgst() != null ? itc4d1.getCgst().toString() : "0");
						    transBal.setTable4d1Sgst(itc4d1.getSgst() != null ? itc4d1.getSgst().toString() : "0");
						    transBal.setTable4d1Cess(itc4d1.getCess() != null ? itc4d1.getCess().toString() : "0");
						}

						// Retrieve values from clsbal and set in transBal
						LedgerItcReclaimBalanceAmts clsbal = obj.getClsbal();
						if (clsbal != null) {
						    transBal.setClsBalIgst(clsbal.getIgst() != null ? clsbal.getIgst().toString() : "0");
						    transBal.setClsBalCgst(clsbal.getCgst() != null ? clsbal.getCgst().toString() : "0");
						    transBal.setClsBalSgst(clsbal.getSgst() != null ? clsbal.getSgst().toString() : "0");
						    transBal.setClsBalCess(clsbal.getCess() != null ? clsbal.getCess().toString() : "0");
						}


						allDetailsDto.add(transBal);
					});
				}

				if (closeBal != null) {
					ItcCrReversalAndReclaimDto respCloseBal = new ItcCrReversalAndReclaimDto();
					respCloseBal.setItcTransDate(closeBal.getTransDate() != null ? closeBal.getTransDate() : null);
					respCloseBal.setDesc(closeBal.getDescription() != null ? closeBal.getDescription() : "Closing Balence");
					respCloseBal.setRefNo(closeBal.getReferenceNo() != null ? closeBal.getReferenceNo() : null);
					respCloseBal.setTaxPeriod(closeBal.getRtnprd() != null ? closeBal.getRtnprd() : null);
				//	respCloseBal.setSrNo(String.valueOf(srNoCounter++));
					respCloseBal.setClsBalIgst(closeBal.getIgst() != null ? closeBal.getIgst().toString() : "0");
					respCloseBal.setClsBalCgst(closeBal.getCgst() != null ? closeBal.getCgst().toString() : "0");
					respCloseBal.setClsBalSgst(closeBal.getSgst() != null ? closeBal.getSgst().toString() : "0");
					respCloseBal.setClsBalCess(closeBal.getCess() != null ? closeBal.getCess().toString() : "0");

					allDetailsDto.add(respCloseBal);

				}
			}
		} catch (Exception ex) {
			String msg = "Exception while extracting the credit reversal and reclaim ";
			LOGGER.error(msg, ex);
			throw new AppException(ex.getMessage());
		}
		return allDetailsDto;
	}
}
