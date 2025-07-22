package com.ey.advisory.app.services.ledger;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;
import com.ey.advisory.app.docs.dto.ledger.ItcLedgerDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.ItcLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.ItcTransactionTypeBalDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Nikhil.Duseja
 *
 */
@Service("getITCLedgerDetailsImpl")
public class GetITCLedgerDetailsImpl implements GetITCLedgerDetails {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetITCLedgerDetailsImpl.class);

	@Autowired
	@Qualifier("iTCLedgerDetailsDataAtGstnImpl")
	private ITCLedgerDetailsDataAtGstn itcDataAtGstn;

	@Override
	public List<ItcDetailsRespDto> findITC(String jsonReq, String groupCode) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		List<ItcDetailsRespDto> allDetailsDto = new ArrayList<>();
		try {
			ItcLedgerDetailsDto responseDto = new ItcLedgerDetailsDto();
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			GetCashLedgerDetailsReqDto dto = gson.fromJson(reqObject,
					GetCashLedgerDetailsReqDto.class);
			 String apiResp = itcDataAtGstn.fromGstn(dto);
			//String apiResp = null;
			if (apiResp != null) {
				requestObject = (new JsonParser()).parse(apiResp)
						.getAsJsonObject();
				reqObject = requestObject.get("itcLdgDtls").getAsJsonObject();
				responseDto = gson.fromJson(reqObject,
						ItcLedgerDetailsDto.class);

				ItcLedgerOpenCloseBalDto closeBal = responseDto.getClosenBal();
				ItcLedgerOpenCloseBalDto openBal = responseDto.getOpenBal();
				List<ItcTransactionTypeBalDto> respTransDtos = responseDto
						.getItctransTypeBalDto();
				if (openBal != null) {
					ItcDetailsRespDto respOpenBal = new ItcDetailsRespDto();
					respOpenBal.setDesc(openBal.getDescription());
					respOpenBal.setBalIgst(openBal.getIgstTaxBal() != null ? openBal.getIgstTaxBal().toString() : "0");
					respOpenBal.setBalCgst(openBal.getCgstTaxBal() != null ? openBal.getCgstTaxBal().toString() : "0");
					respOpenBal.setBalSgst(openBal.getSgstTaxBal() != null ? openBal.getSgstTaxBal().toString() : "0");
					respOpenBal.setBalCess(openBal.getCessTaxBal() != null ? openBal.getCessTaxBal().toString() : "0");
					respOpenBal.setBalTotal(openBal.getTotRngBal() != null ? openBal.getTotRngBal().toString() : "0");

					allDetailsDto.add(respOpenBal);
				}

				if (respTransDtos != null) {
					respTransDtos.forEach(obj -> {
						ItcDetailsRespDto transBal = new ItcDetailsRespDto();
						transBal.setItcTransDate(obj.getItcTransDate());
						transBal.setRefNo(obj.getReferenceNo());
						transBal.setTaxPeriod(obj.getRetPeriod());
						transBal.setDesc(obj.getDescription());
						transBal.setTransType(obj.getTransType());
						transBal.setBalIgst(obj.getIgstTaxBal() != null ? obj.getIgstTaxBal().toString() : "0");
						transBal.setBalSgst(obj.getSgstTaxBal() != null ? obj.getSgstTaxBal().toString() : "0");
						transBal.setBalCgst(obj.getCgstTaxBal() != null ? obj.getCgstTaxBal().toString() : "0");
						transBal.setBalCess(obj.getCessTaxBal() != null ? obj.getCessTaxBal().toString() : "0");
						transBal.setBalTotal(obj.getTotRngBal() != null ? obj.getTotRngBal().toString() : "0");
						transBal.setCrDrIgst(obj.getIgstTaxAmt() != null ? obj.getIgstTaxAmt().toString() : "0");
						transBal.setCrDrCgst(obj.getCgstTaxAmt() != null ? obj.getCgstTaxAmt().toString() : "0");
						transBal.setCrDrSgst(obj.getSgstTaxAmt() != null ? obj.getSgstTaxAmt().toString() : "0");
						transBal.setCrDrCess(obj.getCessTaxAmt() != null ? obj.getCessTaxAmt().toString() : "0");
						transBal.setCrDrTotal(obj.getTotTrAmt() != null ? obj.getTotTrAmt().toString() : "0");

						allDetailsDto.add(transBal);
					});
				}

				if (closeBal != null) {
					ItcDetailsRespDto respCloseBal = new ItcDetailsRespDto();
					respCloseBal.setDesc(closeBal.getDescription());
					respCloseBal.setBalIgst(closeBal.getIgstTaxBal().toString());
					respCloseBal.setBalCgst(closeBal.getCgstTaxBal().toString());
					respCloseBal.setBalSgst(closeBal.getSgstTaxBal().toString());
					respCloseBal.setBalCess(closeBal.getCessTaxBal().toString());
					respCloseBal.setBalTotal(closeBal.getTotRngBal().toString());
					allDetailsDto.add(respCloseBal);
				}
			}
			return allDetailsDto;
		} catch (Exception ex) {
			String msg = "Exception while extracting the ITC Ledger Response ";
			LOGGER.error(msg, ex);
			throw new AppException(ex.getMessage());
		}
	}

}
