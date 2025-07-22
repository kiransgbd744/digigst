package com.ey.advisory.app.services.ledger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.ledger.LedgerSummarizeBalanceDto;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service("getLedgerBalances")
public class GetLedgerBalance {
	
	public List<LedgerSummarizeBalanceDto> getLedgerBalances(
			final String jsonRequest,final String groupCode) {
				
		List<LedgerSummarizeBalanceDto> listObjs =
				new ArrayList();
		LedgerSummarizeBalanceDto ledgerSummObj = new 
				LedgerSummarizeBalanceDto();
		
		
		LedgerSummarizeBalanceDto ledSummObj = new 
				LedgerSummarizeBalanceDto();
		
		ledgerSummObj.setGstin("05AVHPB7348G1ZP");
		ledgerSummObj.setState("Maharashtra");
		ledgerSummObj.setStatus("Active");
		ledgerSummObj.setCashSgstTotBal(new BigDecimal(12000));
		ledgerSummObj.setCashCgstTotBal(new BigDecimal(13000));
		ledgerSummObj.setCashIgstTotBal(new BigDecimal(19000));
		ledgerSummObj.setCashCessTotBal(new BigDecimal(3000));
		ledgerSummObj.setItcSgstTotBal(new BigDecimal(12000));
		ledgerSummObj.setItcCgstTotBal(new BigDecimal(13000));
		ledgerSummObj.setItcIgstTotBal(new BigDecimal(19000));
		ledgerSummObj.setItcCessTotBal(new BigDecimal(34000));
		ledgerSummObj.setLibSgstTotBal(new BigDecimal(12000));
		ledgerSummObj.setLibCgstTotBal(new BigDecimal(13000));
		ledgerSummObj.setLibIgstTotBal(new BigDecimal(19000));
		ledgerSummObj.setLibCessTotBal(new BigDecimal(49000));
		LocalDateTime date = LocalDateTime.now();
	    DateTimeFormatter formatter =  DateTimeFormatter.
	    		ofPattern("dd-MM-yyyy HH:MM");
		ledgerSummObj.setLastUpdatedDate(date.format(formatter));
		
		ledSummObj.setGstin("07AVHPB7348G1ZP");
		ledSummObj.setState("Telangana");
		ledSummObj.setStatus("Active");
		ledSummObj.setCashSgstTotBal(new BigDecimal(13000));
		ledSummObj.setCashCgstTotBal(new BigDecimal(19000));
		ledSummObj.setCashIgstTotBal(new BigDecimal(39000));
		ledSummObj.setCashCessTotBal(new BigDecimal(6000));
		ledSummObj.setItcSgstTotBal(new BigDecimal(12050));
		ledSummObj.setItcCgstTotBal(new BigDecimal(13500));
		ledSummObj.setItcIgstTotBal(new BigDecimal(17500));
		ledSummObj.setItcCessTotBal(new BigDecimal(54000));
		ledSummObj.setLibSgstTotBal(new BigDecimal(52000));
		ledSummObj.setLibCgstTotBal(new BigDecimal(63000));
		ledSummObj.setLibIgstTotBal(new BigDecimal(79000));
		ledSummObj.setLibCessTotBal(new BigDecimal(89000));
		LocalDateTime dt = LocalDateTime.now();
		ledSummObj.setLastUpdatedDate(dt.format(formatter));
		
		listObjs.add(ledgerSummObj);
		listObjs.add(ledSummObj);
		return listObjs;
	}
}
