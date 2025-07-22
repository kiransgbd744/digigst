package com.ey.advisory.app.services.ledger;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.ledger.CashLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.GetCashITCBalanceReqDto;
import com.ey.advisory.app.docs.dto.ledger.LiabDetailsRespDto;
import com.ey.advisory.app.docs.dto.ledger.LiabilityLedgersDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.TransactionTypeLibBalDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("getLiabilityLedgerDetailsForReturnLiabilityImpl")
public class GetLiabilityLedgerDetailsForReturnLiabilityImpl
		implements GetLiabilityLedgerDetailsForReturnLiability {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetCashITCBalanceImpl.class);
	
	

	@Autowired
	@Qualifier("liabilityLedgerDetailsForReturnLiabilityDataAtGstnImpl")
	private LiabilityLedgerDetailsForReturnLiabilityDataAtGstn taxDataAtGstn;
	
	@Override
	public List<LiabDetailsRespDto> findTax(String jsonReq) {

		List<LiabDetailsRespDto> allDetailsDto = new ArrayList<>();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			GetCashITCBalanceReqDto dto = gson.fromJson(reqObject,
					GetCashITCBalanceReqDto.class);
			String apiResp = taxDataAtGstn.fromGstnTestLiab(dto);
			requestObject = (new JsonParser()).parse(apiResp).getAsJsonObject();
			LiabilityLedgersDetailsDto liabLedgerDetDto = gson
					.fromJson(requestObject, LiabilityLedgersDetailsDto.class);
			CashLedgerOpenCloseBalDto closeBal = liabLedgerDetDto.getCloseBal();
			List<TransactionTypeLibBalDto> transTypeLibBalDto = liabLedgerDetDto
					.getTransTypeBalDto();

			if(transTypeLibBalDto != null){
			transTypeLibBalDto.forEach(obj -> {
				LiabDetailsRespDto transBal = new LiabDetailsRespDto();
				transBal.setDptDate(obj.getDptDate());
				transBal.setReferenceNo(obj.getReferenceNo());
				transBal.setRetPeriod(liabLedgerDetDto.getRetPeriod());
				transBal.setDescription(obj.getDescription());
				transBal.setTransType(obj.getTransType());
				transBal.setDschrgType(obj.getDischargingType());
				transBal.setIgst(obj.getIgst());
				transBal.setIgstBal(obj.getIgstBal());
				transBal.setCgst(obj.getCgst());
				transBal.setCgstBal(obj.getCgstBal());
				transBal.setSgst(obj.getSgst());
				transBal.setSgstBal(obj.getSgstBal());
				transBal.setCess(obj.getCess());
				transBal.setCessBal(obj.getCessBal());
				allDetailsDto.add(transBal);
			});
			
			}

			if (closeBal != null) {
				LiabDetailsRespDto respCloseBal = new LiabDetailsRespDto();
				respCloseBal.setDescription(closeBal.getDescription());
				respCloseBal.setCessBal(closeBal.getCessbal());
				respCloseBal.setIgstBal(closeBal.getIgstbal());
				respCloseBal.setCgstBal(closeBal.getCgstbal());
				respCloseBal.setSgstBal(closeBal.getSgstbal());
				allDetailsDto.add(respCloseBal);
			}
			return allDetailsDto;
		} catch (Exception ex) {
			String msg = "Exception while extracting the Liability Ledger details api";
			LOGGER.error(msg, ex);
			throw new AppException(ex.getMessage());

		}
	}

}
