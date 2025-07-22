package com.ey.advisory.app.services.ledger;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.GetSummarizedLedgerBalanceEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.LedgerSummaryBalanceRepository;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@Component("GetSummaryLedgerBalanceDaoImpl")
public class GetSummaryLedgerBalanceDaoImpl implements GetSummaryLedgerBalanceDao {

	@Autowired
	@Qualifier(value = "LedgerSummaryBalanceRepository")
	private LedgerSummaryBalanceRepository ledgerSummaryBalanceRepository;

	@Override
	public List<GetSummaryLedgerBalanceDto> getSummaryLedgerDetails(List<String> gstins) {

		List<GetSummarizedLedgerBalanceEntity> entList = null;
		List<GetSummaryLedgerBalanceDto> summaryList = new ArrayList<>();

		entList = ledgerSummaryBalanceRepository.findBySupplierGstinInAndRefreshStatus(gstins, "Active");

		// Collect GSTINs with active data in entList
		Set<String> gstinsWithData = entList.stream().map(GetSummarizedLedgerBalanceEntity::getSupplierGstin)
				.collect(Collectors.toSet());

		// Convert entities to DTOs
		summaryList.addAll(entList.stream().map(this::convert).collect(Collectors.toList()));

		// Add default entries for GSTINs without data
		for (String gstn : gstins) {
			if (!gstinsWithData.contains(gstn)) {
				GetSummaryLedgerBalanceDto obj = createDefaultLedgerBalanceDto(gstn);
				summaryList.add(obj);
			}
		}

		return summaryList;
	}

	private GetSummaryLedgerBalanceDto createDefaultLedgerBalanceDto(String gstn) {
		GetSummaryLedgerBalanceDto obj = new GetSummaryLedgerBalanceDto();
		obj.setGstin(gstn);
		obj.setCashigst_tot_bal(new BigDecimal("0.0"));
		obj.setCashcgst_tot_bal(new BigDecimal("0.0"));
		obj.setCashsgst_tot_bal(new BigDecimal("0.0"));
		obj.setCashcess_tot_bal(new BigDecimal("0.0"));
		obj.setItcigst_totbal(new BigDecimal("0.0"));
		obj.setItccgst_totbal(new BigDecimal("0.0"));
		obj.setItcsgst_totbal(new BigDecimal("0.0"));
		obj.setItccess_totbal(new BigDecimal("0.0"));
		obj.setLibigst_totbal(new BigDecimal("0.0"));
		obj.setLibcgst_totbal(new BigDecimal("0.0"));
		obj.setLibsgst_totbal(new BigDecimal("0.0"));
		obj.setLibcess_totbal(new BigDecimal("0.0"));
		// CR reversal and reclaim
		obj.setCrRevcess_totbal(new BigDecimal("0.0"));
		obj.setCrRevcgst_totbal(new BigDecimal("0.0"));
		obj.setCrRevigst_totbal(new BigDecimal("0.0"));
		obj.setCrRevsgst_totbal(new BigDecimal("0.0"));
		obj.setRcmCess_totbal(new BigDecimal("0.0"));
		obj.setRcmCgst_totbal(new BigDecimal("0.0"));
		obj.setRcmIgst_totbal(new BigDecimal("0.0"));
		obj.setRcmSgst_totbal(new BigDecimal("0.0"));
		obj.setNegativeCess_totbal(new BigDecimal("0.0"));
		obj.setNegativeCgst_totbal(new BigDecimal("0.0"));
		obj.setNegativeIgst_totbal(new BigDecimal("0.0"));
		obj.setNegativeSgst_totbal(new BigDecimal("0.0"));
		obj.setLastupdated_date(null);
		obj.setStatus("InActive");
		return obj;
	}

	private GetSummaryLedgerBalanceDto convert(GetSummarizedLedgerBalanceEntity o) {
		GetSummaryLedgerBalanceDto obj = new GetSummaryLedgerBalanceDto();
		obj.setGstin(o.getSupplierGstin());
		obj.setCashigst_tot_bal(o.getCashIgstTot_bal() != null ? o.getCashIgstTot_bal() : BigDecimal.ZERO);
		obj.setCashcgst_tot_bal(o.getCashCgstTotBal() != null ? o.getCashCgstTotBal() : BigDecimal.ZERO);
		obj.setCashsgst_tot_bal(o.getCashSgstTotBal() != null ? o.getCashSgstTotBal() : BigDecimal.ZERO);
		obj.setCashcess_tot_bal(o.getCashCessTotBal() != null ? o.getCashCessTotBal() : BigDecimal.ZERO);
		obj.setItcigst_totbal(o.getItcIgstTotBal() != null ? o.getItcIgstTotBal() : BigDecimal.ZERO);
		obj.setItccgst_totbal(o.getItcCgstTotbal() != null ? o.getItcCgstTotbal() : BigDecimal.ZERO);
		obj.setItcsgst_totbal(o.getItcSgstTotBal() != null ? o.getItcSgstTotBal() : BigDecimal.ZERO);
		obj.setItccess_totbal(o.getItcCessTotBal() != null ? o.getItcCessTotBal() : BigDecimal.ZERO);
		obj.setLibigst_totbal(o.getLibIgstTotbal() != null ? o.getLibIgstTotbal() : BigDecimal.ZERO);
		obj.setLibcgst_totbal(o.getLibCgstTotBal() != null ? o.getLibCgstTotBal() : BigDecimal.ZERO);
		obj.setLibsgst_totbal(o.getLibSgstTotBal() != null ? o.getLibSgstTotBal() : BigDecimal.ZERO);
		obj.setLibcess_totbal(o.getLibCessTotBal() != null ? o.getLibCessTotBal() : BigDecimal.ZERO);
		// CR reversal and reclaim
		obj.setCrRevcess_totbal(o.getCrRevCessTotBal() != null ? o.getCrRevCessTotBal() : BigDecimal.ZERO);
		obj.setCrRevcgst_totbal(o.getCrRevCgstTotBal() != null ? o.getCrRevCgstTotBal() : BigDecimal.ZERO);
		obj.setCrRevigst_totbal(o.getCrRevIgstTotbal() != null ? o.getCrRevIgstTotbal() : BigDecimal.ZERO);
		obj.setCrRevsgst_totbal(o.getCrRevSgstTotBal() != null ? o.getCrRevSgstTotBal() : BigDecimal.ZERO);
		obj.setRcmCess_totbal(o.getRcmCessTotBal() != null ? o.getRcmCessTotBal() : BigDecimal.ZERO);
		obj.setRcmCgst_totbal(o.getRcmCgstTotBal() != null ? o.getRcmCgstTotBal() : BigDecimal.ZERO);
		obj.setRcmIgst_totbal(o.getRcmIgstTot_bal() != null ? o.getRcmIgstTot_bal() : BigDecimal.ZERO);
		obj.setRcmSgst_totbal(o.getRcmSgstTotBal() != null ? o.getRcmSgstTotBal() : BigDecimal.ZERO);
		obj.setNegativeCess_totbal(o.getNegativeCessTotBal() != null ? o.getNegativeCessTotBal() : BigDecimal.ZERO);
		obj.setNegativeCgst_totbal(o.getNegativeCgstTotBal() != null ? o.getNegativeCgstTotBal() : BigDecimal.ZERO);
		obj.setNegativeIgst_totbal(o.getNegativeIgstTot_bal() != null ? o.getNegativeIgstTot_bal() : BigDecimal.ZERO);
		obj.setNegativeSgst_totbal(o.getNegativeSgstTotBal() != null ? o.getNegativeSgstTotBal() : BigDecimal.ZERO);
		obj.setLastupdated_date(o.getLastRefreshDate());
		obj.setStatus(o.getRefreshStatus());
		//186759
		obj.setGetCallStatus(o.getStatus());
		
		if (o.getGetCallStatusTimeStamp() != null) {
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(EYDateUtil
					.toISTDateTimeFromUTC(o.getGetCallStatusTimeStamp()));
			obj.setGetCallStatusTimeStamp(newdate);
		}
		
		return obj;
	}

}
