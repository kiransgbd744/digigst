
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
import com.ey.advisory.app.docs.dto.ledger.NegativeDetailedLedgerDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.NegativeDetailedLedgerTransactionDto;
import com.ey.advisory.app.docs.dto.ledger.NegativeDetailsRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("GetNegativeDetailsImpl")
public class GetNegativeDetailsImpl implements GetDetailedNegativeDetails {

	@Autowired
	@Qualifier("iTCLedgerDetailsDataAtGstnImpl")
	private ITCLedgerDetailsDataAtGstn itcDataAtGstn;

	@Autowired
	@Qualifier("cashLedgerDetailsDataAtGstnImpl")
	private CashLedgerDetailsDataAtGstn crReversalAndReclaim;

	@Override
	public List<NegativeDetailsRespDto> findNegativeDetails(String jsonReq) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		List<NegativeDetailsRespDto> allDetailsDto = new ArrayList<>();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonReq)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			GetCashLedgerDetailsReqDto dto = gson.fromJson(reqObject,
					GetCashLedgerDetailsReqDto.class);
			String apiResp = crReversalAndReclaim
					.fromGstnDetailedNegativeDetailsTest(dto);
			if (apiResp != null) {
				JsonObject reqObj = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				NegativeDetailedLedgerDetailsDto respDto = gson.fromJson(reqObj,
						NegativeDetailedLedgerDetailsDto.class);

				/*
				 * CrReversalLedgerOpenCloseBalDto openBal = respDto
				 * .getOpenBal();
				 */

				List<NegativeDetailedLedgerTransactionDto> respTransDtos = respDto
						.getTransTypeBalDto();

				if (respTransDtos != null) {
					AtomicInteger srNoCounter = new AtomicInteger(1);
					respTransDtos.forEach(obj -> {
						NegativeDetailsRespDto transBal = new NegativeDetailsRespDto();
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
						transBal.setTranTyp(
								obj.getTrantyp() != null ? obj.getTrantyp()
										: null);

						List<LedgerItcReclaimBalanceAmts> negliabList = obj
								.getNegliab();
						if (negliabList != null && !negliabList.isEmpty()) {
							LedgerItcReclaimBalanceAmts amtCrDrOtrThnRevChg = negliabList
									.get(0);
							LedgerItcReclaimBalanceAmts amtCrDrRevChg = (negliabList
									.size() > 1) ? negliabList.get(1) : null;

							// For first object
							if (amtCrDrOtrThnRevChg != null) {
								transBal.setAmtCrDrOtherIgst(
										amtCrDrOtrThnRevChg.getIgst() != null
												? amtCrDrOtrThnRevChg.getIgst()
														.toString()
												: "0");
								transBal.setAmtCrDrOtherCgst(
										amtCrDrOtrThnRevChg.getCgst() != null
												? amtCrDrOtrThnRevChg.getCgst()
														.toString()
												: "0");
								transBal.setAmtCrDrOtherSgst(
										amtCrDrOtrThnRevChg.getSgst() != null
												? amtCrDrOtrThnRevChg.getSgst()
														.toString()
												: "0");
								transBal.setAmtCrDrOtherCess(
										amtCrDrOtrThnRevChg.getCess() != null
												? amtCrDrOtrThnRevChg.getCess()
														.toString()
												: "0");
							}

							// For second object (if present)
							if (amtCrDrRevChg != null) {
								transBal.setAmtCrDrRevChargeIgst(
										amtCrDrRevChg.getIgst() != null
												? amtCrDrRevChg.getIgst()
														.toString()
												: "0");
								transBal.setAmtCrDrRevChargeCgst(
										amtCrDrRevChg.getCgst() != null
												? amtCrDrRevChg.getCgst()
														.toString()
												: "0");
								transBal.setAmtCrDrRevChargeSgst(
										amtCrDrRevChg.getSgst() != null
												? amtCrDrRevChg.getSgst()
														.toString()
												: "0");
								transBal.setAmtCrDrRevChargeCess(
										amtCrDrRevChg.getCess() != null
												? amtCrDrRevChg.getCess()
														.toString()
												: "0");
							}

							List<LedgerItcReclaimBalanceAmts> negliaBalenceList = obj
									.getNegliabal();
							if (negliaBalenceList != null
									&& !negliaBalenceList.isEmpty()) {

								LedgerItcReclaimBalanceAmts clsBalOtrThnRevChg = negliaBalenceList
										.get(0);
								LedgerItcReclaimBalanceAmts clsBalRevChg = negliaBalenceList
										.get(1);

								if (clsBalOtrThnRevChg != null) {
									transBal.setClsBalOtherIgst(
											clsBalOtrThnRevChg.getIgst() != null
													? clsBalOtrThnRevChg
															.getIgst()
															.toString()
													: "0");
									transBal.setClsBalOtherCgst(
											clsBalOtrThnRevChg.getCgst() != null
													? clsBalOtrThnRevChg
															.getCgst()
															.toString()
													: "0");
									transBal.setClsBalOtherSgst(
											clsBalOtrThnRevChg.getSgst() != null
													? clsBalOtrThnRevChg
															.getSgst()
															.toString()
													: "0");
									transBal.setClsBalOtherCess(
											clsBalOtrThnRevChg.getCess() != null
													? clsBalOtrThnRevChg
															.getCess()
															.toString()
													: "0");
								}

								// For second object (if present)
								if (clsBalRevChg != null) {
									transBal.setClsBalRevChargeIgst(
											clsBalRevChg.getIgst() != null
													? clsBalRevChg.getIgst()
															.toString()
													: "0");
									transBal.setClsBalRevChargeCgst(
											clsBalRevChg.getCgst() != null
													? clsBalRevChg.getCgst()
															.toString()
													: "0");
									transBal.setClsBalRevChargeSgst(
											clsBalRevChg.getSgst() != null
													? clsBalRevChg.getSgst()
															.toString()
													: "0");
									transBal.setClsBalRevChargeCess(
											clsBalRevChg.getCess() != null
													? clsBalRevChg.getCess()
															.toString()
													: "0");
								}
							}

						}

						allDetailsDto.add(transBal);
					});
				}

				List<CrReversalLedgerOpenCloseBalDto> closeBalList = respDto
						.getClosenBal();
				if (closeBalList != null && !closeBalList.isEmpty()) {
					CrReversalLedgerOpenCloseBalDto clsBalOther = closeBalList
							.get(0);
					CrReversalLedgerOpenCloseBalDto clsBalRevChg = closeBalList
							.get(1);

					NegativeDetailsRespDto respCloseBal = new NegativeDetailsRespDto();

					respCloseBal.setDesc("Closing Balance");
					respCloseBal
							.setClsBalOtherIgst(clsBalOther.getIgst() != null
									? clsBalOther.getIgst().toString()
									: "0");
					respCloseBal
							.setClsBalOtherCgst(clsBalOther.getCgst() != null
									? clsBalOther.getCgst().toString()
									: "0");
					respCloseBal
							.setClsBalOtherSgst(clsBalOther.getSgst() != null
									? clsBalOther.getSgst().toString()
									: "0");
					respCloseBal
							.setClsBalOtherCess(clsBalOther.getCess() != null
									? clsBalOther.getCess().toString()
									: "0");

					respCloseBal.setClsBalRevChargeIgst(
							clsBalRevChg.getIgst() != null
									? clsBalRevChg.getIgst().toString()
									: "0");
					respCloseBal.setClsBalRevChargeCgst(
							clsBalRevChg.getCgst() != null
									? clsBalRevChg.getCgst().toString()
									: "0");
					respCloseBal.setClsBalRevChargeSgst(
							clsBalRevChg.getSgst() != null
									? clsBalRevChg.getSgst().toString()
									: "0");
					respCloseBal.setClsBalRevChargeCess(
							clsBalRevChg.getCess() != null
									? clsBalRevChg.getCess().toString()
									: "0");

					allDetailsDto.add(respCloseBal);

				}

			}
		} catch (Exception ex) {
			String msg = "Exception while extracting the negative Details ";
			LOGGER.error(msg, ex);
			throw new AppException(ex.getMessage());
		}
		return allDetailsDto;
	}
}
