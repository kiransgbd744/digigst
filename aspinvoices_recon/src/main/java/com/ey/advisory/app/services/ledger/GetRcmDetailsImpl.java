package com.ey.advisory.app.services.ledger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.ledger.CrReversalLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;
import com.ey.advisory.app.docs.dto.ledger.LedgerItcReclaimBalanceAmts;
import com.ey.advisory.app.docs.dto.ledger.RcmDetailedLedgerDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.RcmDetailedLedgerTransactionDto;
import com.ey.advisory.app.docs.dto.ledger.RcmDetailsRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("GetRcmDetailsImpl")
public class GetRcmDetailsImpl implements GetDetailedRcmDetails {

	@Autowired
	@Qualifier("iTCLedgerDetailsDataAtGstnImpl")
	private ITCLedgerDetailsDataAtGstn itcDataAtGstn;

	@Autowired
	@Qualifier("cashLedgerDetailsDataAtGstnImpl")
	private CashLedgerDetailsDataAtGstn crReversalAndReclaim;

	@Override
	public List<RcmDetailsRespDto> findRcmDetails(String jsonReq) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		List<RcmDetailsRespDto> allDetailsDto = new ArrayList<>();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonReq)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			GetCashLedgerDetailsReqDto dto = gson.fromJson(reqObject,
					GetCashLedgerDetailsReqDto.class);
			String apiResp = crReversalAndReclaim
					.fromGstnDetailedRcmDetailsTest(dto);
			if (apiResp != null) {
				JsonObject reqObj = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				RcmDetailedLedgerDetailsDto respDto = gson.fromJson(reqObj,
						RcmDetailedLedgerDetailsDto.class);
				CrReversalLedgerOpenCloseBalDto closeBal = respDto
						.getClosenBal();
				/*
				 * CrReversalLedgerOpenCloseBalDto openBal = respDto
				 * .getOpenBal();
				 */

				List<RcmDetailedLedgerTransactionDto> respTransDtos = respDto
						.getTransTypeBalDto();

				if (respTransDtos != null) {
					AtomicInteger srNoCounter = new AtomicInteger(1);
					respTransDtos.forEach(obj -> {
						RcmDetailsRespDto transBal = new RcmDetailsRespDto();
						transBal.setDate(
								obj.getTransDate() != null ? obj.getTransDate()
										: null);
						transBal.setDesc(obj.getDescription() != null
								? obj.getDescription()
								: null);
						transBal.setRefNo(obj.getReferenceNo() != null
								? obj.getReferenceNo()
								: null);
						transBal.setTaxPeriod(
								obj.getRtnprd() != null ? obj.getRtnprd()
										: null);

						LedgerItcReclaimBalanceAmts table4a2 = obj.getItc4a2();
						if (table4a2 != null) {
							transBal.setTable4a2Igst(table4a2.getIgst() != null
									? table4a2.getIgst().toString()
									: "0");
							transBal.setTable4a2Cgst(table4a2.getCgst() != null
									? table4a2.getCgst().toString()
									: "0");
							transBal.setTable4a2Sgst(table4a2.getSgst() != null
									? table4a2.getSgst().toString()
									: "0");
							transBal.setTable4a2Cess(table4a2.getCess() != null
									? table4a2.getCess().toString()
									: "0");
						}

						// Retrieve values from itc4a3 and set in transBal
						LedgerItcReclaimBalanceAmts table4a3 = obj.getItc4a3();
						if (table4a3 != null) {
							transBal.setTable4a3Igst(table4a3.getIgst() != null
									? table4a3.getIgst().toString()
									: "0");
							transBal.setTable4a3Cgst(table4a3.getCgst() != null
									? table4a3.getCgst().toString()
									: "0");
							transBal.setTable4a3Sgst(table4a3.getSgst() != null
									? table4a3.getSgst().toString()
									: "0");
							transBal.setTable4a3Cess(table4a3.getCess() != null
									? table4a3.getCess().toString()
									: "0");
						}

						// Retrieve values from itc31d and set in transBal
						LedgerItcReclaimBalanceAmts table31d = obj.getItc31d();
						if (table31d != null) {
							transBal.setTable31dIgst(table31d.getIgst() != null
									? table31d.getIgst().toString()
									: "0");
							transBal.setTable31dCgst(table31d.getCgst() != null
									? table31d.getCgst().toString()
									: "0");
							transBal.setTable31dSgst(table31d.getSgst() != null
									? table31d.getSgst().toString()
									: "0");
							transBal.setTable31dCess(table31d.getCess() != null
									? table31d.getCess().toString()
									: "0");
						}

						// Retrieve values from clsbal and set in transBal
						LedgerItcReclaimBalanceAmts clsbal = obj.getClsbal();
						if (clsbal != null) {
							transBal.setClsBalIgst(clsbal.getIgst() != null
									? clsbal.getIgst().toString()
									: "0");
							transBal.setClsBalCgst(clsbal.getCgst() != null
									? clsbal.getCgst().toString()
									: "0");
							transBal.setClsBalSgst(clsbal.getSgst() != null
									? clsbal.getSgst().toString()
									: "0");
							transBal.setClsBalCess(clsbal.getCess() != null
									? clsbal.getCess().toString()
									: "0");
						}

						allDetailsDto.add(transBal);
					});
				}

				if (closeBal != null) {
					RcmDetailsRespDto respCloseBal = new RcmDetailsRespDto();
					respCloseBal.setDate(closeBal.getTransDate() != null
							? closeBal.getTransDate()
							: null);
					respCloseBal.setDesc(closeBal.getDescription() != null
							? closeBal.getDescription()
							: "Closing Balance");
					respCloseBal.setRefNo(closeBal.getReferenceNo() != null
							? closeBal.getReferenceNo()
							: null);
					respCloseBal.setTaxPeriod(
							closeBal.getRtnprd() != null ? closeBal.getRtnprd()
									: null);
					respCloseBal.setClsBalIgst(closeBal.getIgst() != null
							? closeBal.getIgst().toString()
							: "0");
					respCloseBal.setClsBalCgst(closeBal.getCgst() != null
							? closeBal.getCgst().toString()
							: "0");
					respCloseBal.setClsBalSgst(closeBal.getSgst() != null
							? closeBal.getSgst().toString()
							: "0");
					respCloseBal.setClsBalCess(closeBal.getCess() != null
							? closeBal.getCess().toString()
							: "0");

					allDetailsDto.add(respCloseBal);

				}
			}
		} catch (Exception ex) {
			String msg = "Exception while extracting the RCM Details ";
			LOGGER.error(msg, ex);
			throw new AppException(ex.getMessage());
		}
		return allDetailsDto;
	}
}
