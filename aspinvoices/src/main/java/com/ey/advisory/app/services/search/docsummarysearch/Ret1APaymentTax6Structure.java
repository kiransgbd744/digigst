package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.TaxPaymentSummaryDto;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Ret1APaymentTax6Structure")
public class Ret1APaymentTax6Structure {

	
	public List<TaxPaymentSummaryDto> ret1A6Resp(List<TaxPaymentSummaryDto> listadd,
			String section, String desc, String table) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<TaxPaymentSummaryDto> defaultintEYList = getDefaultint6EYStructure(
				section, desc, table);

		List<TaxPaymentSummaryDto> b2cEYList = getB2CEyList(defaultintEYList,
				listadd,table);

		List<TaxPaymentSummaryDto> retList = new ArrayList<>();
		retList.addAll(b2cEYList);
		// String json = gson.toJson(b2bTotal);

	//	JsonElement b2cRespbody = gson.toJsonTree(retList);

		return retList;

	}

	// Replacing Default Value To Normal Value
	private List<TaxPaymentSummaryDto> 
	getDefaultint6EYStructure(String section,String desc,String table) {

		List<TaxPaymentSummaryDto> defaultB2CEY = new ArrayList<>();

		TaxPaymentSummaryDto b2cEy3AInv = new TaxPaymentSummaryDto();
		if(section != null){
		b2cEy3AInv.setSection(section);
		}
		b2cEy3AInv.setDesc(desc);
		b2cEy3AInv.setTable(table);
		b2cEy3AInv.setUsrCashPaidInterest(BigDecimal.ZERO);
		b2cEy3AInv.setUsrCashPaidLateFee(BigDecimal.ZERO);
		b2cEy3AInv.setUsrCashPaidTax(BigDecimal.ZERO);
		b2cEy3AInv.setUsrItcPaidCess(BigDecimal.ZERO);
		b2cEy3AInv.setUsrItcPaidCgst(BigDecimal.ZERO);
		b2cEy3AInv.setUsrItcPaidIgst(BigDecimal.ZERO);
		b2cEy3AInv.setUsrItcPaidSgst(BigDecimal.ZERO);
		b2cEy3AInv.setUsrLiability(BigDecimal.ZERO);
		b2cEy3AInv.setUsrOtherLiability(BigDecimal.ZERO);
		b2cEy3AInv.setUsrOtherPaid(BigDecimal.ZERO);
		b2cEy3AInv.setUsrOtherPayable(BigDecimal.ZERO);
		b2cEy3AInv.setUsrPaid(BigDecimal.ZERO);
		b2cEy3AInv.setUsrPayable(BigDecimal.ZERO);
		b2cEy3AInv.setGstnCashPaidInterest(BigDecimal.ZERO);
		b2cEy3AInv.setGstnCashPaidLateFee(BigDecimal.ZERO);
		b2cEy3AInv.setGstnCashPaidTax(BigDecimal.ZERO);
		b2cEy3AInv.setGstnItcPaidCess(BigDecimal.ZERO);
		b2cEy3AInv.setGstnItcPaidCgst(BigDecimal.ZERO);
		b2cEy3AInv.setGstnItcPaidIgst(BigDecimal.ZERO);
		b2cEy3AInv.setGstnItcPaidSgst(BigDecimal.ZERO);
		b2cEy3AInv.setGstnLiability(BigDecimal.ZERO);
		b2cEy3AInv.setGstnOtherLiability(BigDecimal.ZERO);
		b2cEy3AInv.setGstnOtherPaid(BigDecimal.ZERO);
		b2cEy3AInv.setGstnOtherPayable(BigDecimal.ZERO);
		b2cEy3AInv.setGstnPaid(BigDecimal.ZERO);
		b2cEy3AInv.setGstnPayable(BigDecimal.ZERO);
		b2cEy3AInv.setGstnItcPaidIgst(BigDecimal.ZERO);
		b2cEy3AInv.setGstnItcPaidSgst(BigDecimal.ZERO);
		b2cEy3AInv.setGstnLiability(BigDecimal.ZERO);
		b2cEy3AInv.setGstnOtherLiability(BigDecimal.ZERO);
		b2cEy3AInv.setGstnOtherPaid(BigDecimal.ZERO);
		b2cEy3AInv.setGstnOtherPayable(BigDecimal.ZERO);
		b2cEy3AInv.setGstnPaid(BigDecimal.ZERO);
		b2cEy3AInv.setGstnPayable(BigDecimal.ZERO);
		
		b2cEy3AInv.setDiffCashPaidInterest(BigDecimal.ZERO);
		b2cEy3AInv.setDiffCashPaidLateFee(BigDecimal.ZERO);
		b2cEy3AInv.setDiffCashPaidTax(BigDecimal.ZERO);
		b2cEy3AInv.setDiffItcPaidCess(BigDecimal.ZERO);
		b2cEy3AInv.setDiffItcPaidCgst(BigDecimal.ZERO);
		b2cEy3AInv.setDiffItcPaidIgst(BigDecimal.ZERO);
		b2cEy3AInv.setDiffItcPaidSgst(BigDecimal.ZERO);
		b2cEy3AInv.setDiffLiability(BigDecimal.ZERO);
		b2cEy3AInv.setDiffOtherLiability(BigDecimal.ZERO);
		b2cEy3AInv.setDiffOtherPaid(BigDecimal.ZERO);
		b2cEy3AInv.setDiffOtherPayable(BigDecimal.ZERO);
		b2cEy3AInv.setDiffPaid(BigDecimal.ZERO);
		b2cEy3AInv.setDiffPayable(BigDecimal.ZERO);
		defaultB2CEY.add(b2cEy3AInv);
		return defaultB2CEY;
	}

	public List<TaxPaymentSummaryDto> getB2CEyList(
			List<TaxPaymentSummaryDto> b2cEYList,
			List<TaxPaymentSummaryDto> eySummaryListFromView,String table) {

		List<TaxPaymentSummaryDto> view3AFiltered = eySummaryListFromView.stream()
				.filter(p -> table.equalsIgnoreCase(p.getTable()))
				.collect(Collectors.toList());

		// If INV filtered list is not null
		if (view3AFiltered != null & view3AFiltered.size() > 0) {
			// then filter default List for 4A
			List<TaxPaymentSummaryDto> default3AInvFiltered = b2cEYList.stream()
					.filter(p -> table.equalsIgnoreCase(p.getTable()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3AInvFiltered.forEach(defaultInv -> {
				// then remove it from List
				b2cEYList.remove(defaultInv);
			});

			view3AFiltered.forEach(Summary -> {
				TaxPaymentSummaryDto summaryResp = new TaxPaymentSummaryDto();
				summaryResp.setTable(Summary.getTable());
				summaryResp.setSection(Summary.getSection());
				summaryResp.setDesc(Summary.getDesc());
				summaryResp.setUsrPayable(Summary.getUsrPayable());
				summaryResp
						.setUsrOtherPayable(Summary.getUsrOtherPayable());
				summaryResp.setUsrPaid(Summary.getUsrPaid());
				summaryResp.setUsrOtherPaid(Summary.getUsrOtherPaid());
				summaryResp.setUsrLiability(Summary.getUsrLiability());
				summaryResp.setUsrOtherLiability(
						Summary.getUsrOtherLiability());
				summaryResp.setUsrItcPaidIgst(Summary.getUsrItcPaidIgst());
				summaryResp.setUsrItcPaidCgst(Summary.getUsrItcPaidCgst());
				summaryResp.setUsrItcPaidSgst(Summary.getUsrItcPaidSgst());
				summaryResp.setUsrItcPaidCess(Summary.getUsrItcPaidCess());
				summaryResp.setUsrCashPaidInterest(
						Summary.getUsrCashPaidInterest());
				summaryResp.setUsrCashPaidTax(Summary.getUsrCashPaidTax());
				summaryResp.setUsrCashPaidLateFee(
						Summary.getUsrCashPaidLateFee());
				
				summaryResp.setGstnPayable(Summary.getGstnPayable());
				summaryResp
						.setGstnOtherPayable(Summary.getGstnOtherPayable());
				summaryResp.setGstnPaid(Summary.getGstnPaid());
				summaryResp.setGstnOtherPaid(Summary.getGstnOtherPaid());
				summaryResp.setGstnLiability(Summary.getGstnLiability());
				summaryResp.setGstnOtherLiability(
						Summary.getGstnOtherLiability());
				summaryResp
						.setGstnItcPaidIgst(Summary.getGstnItcPaidIgst());
				summaryResp
						.setGstnItcPaidCgst(Summary.getGstnItcPaidCgst());
				summaryResp
						.setGstnItcPaidSgst(Summary.getGstnItcPaidSgst());
				summaryResp
						.setGstnItcPaidCess(Summary.getGstnItcPaidCess());
				summaryResp.setGstnCashPaidInterest(
						Summary.getGstnCashPaidInterest());
				summaryResp.setGstnCashPaidTax(Summary.getGstnCashPaidTax());
				summaryResp.setGstnCashPaidLateFee(
						Summary.getGstnCashPaidLateFee());

				summaryResp.setDiffPayable(Summary.getDiffPayable());
				summaryResp.setDiffOtherPayable(Summary.getDiffOtherPayable());
				summaryResp.setDiffPaid(Summary.getDiffPaid());
				summaryResp.setDiffOtherPaid(Summary.getDiffOtherPaid());
				summaryResp.setDiffLiability(Summary.getDiffLiability());
				summaryResp.setDiffOtherLiability(Summary.getDiffOtherLiability());
				summaryResp.setDiffItcPaidIgst(Summary.getDiffItcPaidIgst());
				summaryResp.setDiffItcPaidCgst(Summary.getDiffItcPaidCgst());
				summaryResp.setDiffItcPaidSgst(Summary.getDiffItcPaidSgst());
				summaryResp.setDiffItcPaidCess(Summary.getDiffItcPaidCess());
				summaryResp.setDiffCashPaidInterest(Summary.getDiffCashPaidInterest());
				summaryResp.setDiffCashPaidTax(Summary.getDiffCashPaidTax());
				summaryResp.setDiffCashPaidLateFee(Summary.getDiffCashPaidLateFee());
				b2cEYList.add(summaryResp);
			});
		}

		Collections.sort(b2cEYList, new Comparator<TaxPaymentSummaryDto>() {
			@Override	
			public int compare(TaxPaymentSummaryDto respDto1,
					TaxPaymentSummaryDto respDto2) {
				return respDto1.getTable().compareTo(respDto2.getTable());
			}
		});

		return b2cEYList;

	}

	
	
}
