package com.ey.advisory.app.services.ledger;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.GetSummarizedLedgerBalanceEntity;
import com.ey.advisory.app.data.entities.client.asprecon.LedgerSaveToGstnRcmEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.LedgerSaveToGstnRcmRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.LedgerSummaryBalanceRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.SaveRcmOpeningBalRepository;
import com.ey.advisory.app.docs.dto.ledger.LedgerBalanceJobTriggerDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("LedgerTotalBalanceSummServiceImpl")
public class LedgerTotalBalanceSummServiceImpl
		implements LedgerTotalBalanceSummService {
	@Autowired
	@Qualifier(value = "FindLedgerSummaryAtGstinImpl")
	FindLedgerSummaryAtGstin findLedgerSummAtGstin;

	@Autowired
	@Qualifier(value = "LedgerSummaryBalanceRepository")
	private LedgerSummaryBalanceRepository ledgerSummaryBalanceRepository;

	@Autowired
	@Qualifier(value = "RcmSaveToGstnServiceImpl")
	RcmSaveToGstnService rcmSaveToGstnServiceImpl;

	@Autowired
	@Qualifier("SaveRcmOpeningBalRepository")
	private SaveRcmOpeningBalRepository saveRcmOpeningBalRepository;

	@Autowired
	@Qualifier("LedgerSaveToGstnRcmRepository")
	LedgerSaveToGstnRcmRepository ledgerSaveToGstnRcmRepository;

	@Override
	public void getLedgerTotalBalance(LedgerBalanceJobTriggerDto reqDto) {
		try {
			List<GetSummarizedLedgerBalanceEntity> entList = findLedgerSummAtGstin
					.findLedgerSummAtGstinLevl(reqDto);
			if (!entList.isEmpty()) {
				LOGGER.debug(
						"About to Inactivate the Existing Ledger Summary"
								+ " for GSTINS {} ",
						reqDto.getGstins());
				int rowsAffected = ledgerSummaryBalanceRepository
						.updateRefreshStatusWithOutTaxPeriod(reqDto.getGstins(),
								"InActive",
								LocalDateTime.now());
				if (rowsAffected > 0) {
					LOGGER.debug(
							"Inactivated the Active Ledger Summary"
									+ " for GSTINS {} ",
							reqDto.getGstins());
				} else {
					LOGGER.debug(
							"There are no Active Ledger Summary"
									+ " for GSTINS {} ",
							reqDto.getGstins());
				}
				ledgerSummaryBalanceRepository.saveAll(entList);
				LOGGER.debug(
						"Ledger Refresh is successfull, Data refreshed is {}",
						entList);
			} else {
				LOGGER.debug(
						"Response is empty, Hence cannot refresh Ledger Data");
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while Ledger Refresh, Exception is ", ex);
			throw new AppException(ex);
		}
	}

	// save to gstin
	@Override
	public void saveToGstnRcmLedger(LedgerSaveToGstnRcmEntity data, Long id) {
		try {

			GstnRcmLedgerDto dto = new GstnRcmLedgerDto();
			dto.setGstin(data.getGstin());

			RcmLedgerOpnBal opnbal = new RcmLedgerOpnBal();
			opnbal.setIgst(data.getIgst());
			opnbal.setCgst(data.getCgst());
			opnbal.setSgst(data.getSgst());
			opnbal.setCess(data.getCess());
			dto.setOpnbal(opnbal);

			dto.setAction(data.getIsAmended());
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			// Convert DTO to JSON
			String jsonPayload = gson.toJson(dto);
			String ledgerType = data.getLedgerType();
			if (ledgerType.equalsIgnoreCase("RCM"))
				rcmSaveToGstnServiceImpl
						.rcmSaveToGstnApiCall(jsonPayload,
								data.getGstin(), id);
			else
				rcmSaveToGstnServiceImpl
						.reclaimSaveToGstnApiCall(jsonPayload,
								data.getGstin(), id);

		} catch (Exception ex) {
			String errorMessage = ex.getMessage();
			if (errorMessage == null || errorMessage.isEmpty()) {
				errorMessage = "Unknown error occurred";
			}
			ledgerSaveToGstnRcmRepository.updateStatusById("FAILED",
					LocalDateTime.now(), null, errorMessage, id);

			throw new AppException(ex);
		}
	}
}
