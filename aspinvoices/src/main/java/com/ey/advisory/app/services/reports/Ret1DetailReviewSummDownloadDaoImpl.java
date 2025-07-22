package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.simplified.RefundSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.TaxPaymentSummaryDto;
import com.ey.advisory.app.services.search.simplified.docsummary.Anx1GetGstnDataSearchService;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1ReviewSummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1SumLatefee6ReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1SumPayment7ReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1SumRefund8ReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1SumReqRespHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Component("Ret1DetailReviewSummDownloadDaoImpl")
public class Ret1DetailReviewSummDownloadDaoImpl
		implements Ret1ReviewSummaryScreenDownloadDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ReviewSummOutwardScreenDownloadDaoImpl.class);

	@Autowired
	@Qualifier("Anx1GetGstnDataSearchService")
	Anx1GetGstnDataSearchService gstnService;

	@Autowired
	@Qualifier("Ret1SumLatefee6ReqRespHandler")
	Ret1SumLatefee6ReqRespHandler lateFess;

	@Autowired
	@Qualifier("Ret1ReviewSummaryRespHandler")
	Ret1ReviewSummaryRespHandler ret1ReviewSummaryRespHandler;

	@Autowired
	@Qualifier("Ret1SumReqRespHandler")
	Ret1SumReqRespHandler retSum1ReqRespHandler;

	@Autowired
	@Qualifier("Ret1SumPayment7ReqRespHandler")
	Ret1SumPayment7ReqRespHandler taxPayment;

	@Autowired
	@Qualifier("Ret1SumRefund8ReqRespHandler")
	Ret1SumRefund8ReqRespHandler refund;

	@Override
	public List<Ret1SummaryRespDto> getRet1ReviewSummScreenDownload(
			Annexure1SummaryReqDto ret1SummaryRequest) {

		JsonElement handleRet1ReqAndResp = retSum1ReqRespHandler
				.handleRe1ReqAndResp(ret1SummaryRequest);
		Map<String, JsonElement> combinedMap2 = new HashMap<>();

		combinedMap2.put("aspValues", handleRet1ReqAndResp);
		JsonElement element = combinedMap2.get("aspValues");

		Gson gson = GsonUtil.newSAPGsonInstance();

		java.lang.reflect.Type type = new TypeToken<Map<String, List<Ret1SummaryRespDto>>>() {
			private static final long serialVersionUID = 1L;
		}.getType();

		Map<String, List<Ret1SummaryRespDto>> lateFeeMap = gson
				.fromJson(element, type);

		List<Ret1SummaryRespDto> detailDto = new ArrayList<>();
		List<Ret1SummaryRespDto> detailA3 = lateFeeMap.get("A3");
		List<Ret1SummaryRespDto> detailB3 = lateFeeMap.get("B3");
		List<Ret1SummaryRespDto> detailC3 = lateFeeMap.get("C3");
		List<Ret1SummaryRespDto> detailD3 = lateFeeMap.get("D3");
		List<Ret1SummaryRespDto> detailE3 = lateFeeMap.get("E3");
		List<Ret1SummaryRespDto> detailA4 = lateFeeMap.get("A4");
		List<Ret1SummaryRespDto> detailB4 = lateFeeMap.get("B4");
		List<Ret1SummaryRespDto> detailC4 = lateFeeMap.get("C4");
		List<Ret1SummaryRespDto> detailD4 = lateFeeMap.get("D4");
		List<Ret1SummaryRespDto> detailE4 = lateFeeMap.get("E4");
		List<Ret1SummaryRespDto> detail5 = lateFeeMap.get("5");

		detailDto.addAll(detailA3);
		detailDto.addAll(detailB3);
		detailDto.addAll(detailC3);
		detailDto.addAll(detailD3);
		detailDto.addAll(detailE3);
		detailDto.addAll(detailA4);
		detailDto.addAll(detailB4);
		detailDto.addAll(detailC4);
		detailDto.addAll(detailD4);
		detailDto.addAll(detailE4);
		detailDto.addAll(detail5);

		List<Ret1SummaryRespDto> detailDtoList = new ArrayList<>();

		for (Ret1SummaryRespDto resdto : detailDto) {

			Ret1SummaryRespDto res = new Ret1SummaryRespDto();

			Map<String, String> outwardSectionsMap = buildOutwardSectionsMap();
			String table = String.valueOf(resdto.getTable());

			if ("A3".equalsIgnoreCase(table)) {
				res.setTable("3A");
			} else {
				res.setTable(table);
			}
			if ("B3".equalsIgnoreCase(table)) {
				res.setTable("3B");
			} else {
				res.setTable(table);
			}
			if ("C3".equalsIgnoreCase(table)) {
				res.setTable("3C");
			} else {
				res.setTable(table);
			}
			if ("D3".equalsIgnoreCase(table)) {
				res.setTable("3D");
			} else {
				res.setTable(table);
			}
			if ("E3".equalsIgnoreCase(table)) {
				res.setTable("3E");
			} else {
				res.setTable(table);
			}
			if ("A4".equalsIgnoreCase(table)) {
				res.setTable("4A");
			} else {
				res.setTable(table);
			}
			if ("B4".equalsIgnoreCase(table)) {
				res.setTable("4B");
			} else {
				res.setTable(table);
			}
			if ("C4".equalsIgnoreCase(table)) {
				res.setTable("4C");
			} else {
				res.setTable(table);
			}
			if ("D4".equalsIgnoreCase(table)) {
				res.setTable("4D");

			} else {
				res.setTable(table);
			}
			if ("E4".equalsIgnoreCase(table)) {
				res.setTable("4E");

			} else {
				res.setTable(table);
			}

			res.setSupplyType(outwardSectionsMap.get(resdto.getTable()));
			res.setAspValue(resdto.getAspValue());
			res.setAspIgst(resdto.getAspIgst());
			res.setAspCgst(resdto.getAspCgst());
			res.setAspSgst(resdto.getAspSgst());
			res.setAspCess(resdto.getAspCess());
			res.setUsrValue(resdto.getUsrValue());
			res.setUsrIgst(resdto.getUsrIgst());
			res.setUsrCgst(resdto.getUsrCgst());
			res.setUsrSgst(resdto.getUsrSgst());
			res.setUsrCess(resdto.getUsrCess());
			res.setGstnValue(resdto.getGstnValue());
			res.setGstnIgst(resdto.getGstnIgst());
			res.setGstnCgst(resdto.getGstnCgst());
			res.setGstnSgst(resdto.getGstnSgst());
			res.setGstnCess(resdto.getGstnCess());
			res.setDiffValue(resdto.getDiffValue());
			res.setDiffIgst(resdto.getDiffIgst());
			res.setDiffCgst(resdto.getDiffCgst());
			res.setDiffSgst(resdto.getDiffSgst());
			res.setDiffCess(resdto.getDiffCess());

			detailDtoList.add(res);
		}
		return detailDtoList;
	}

	@Override
	public List<Ret1LateFeeSummaryDto> getRet1ReviewSummaryInterestAndLateFeeDao(
			Annexure1SummaryReqDto ret1SummaryRequest) {

		JsonElement handleRet1ReqLateFee = lateFess
				.handleRe1ReqAndResp(ret1SummaryRequest);

		Gson gson = GsonUtil.newSAPGsonInstance();
		@SuppressWarnings("serial")
		java.lang.reflect.Type type = new TypeToken<Map<String, List<Ret1LateFeeSummaryDto>>>() {
		}.getType();
		Map<String, List<Ret1LateFeeSummaryDto>> lateFeeMap = gson
				.fromJson(handleRet1ReqLateFee, type);

		List<Ret1LateFeeSummaryDto> lateFeeSummDetailDto = lateFeeMap
				.get("tab_6");
		List<Ret1LateFeeSummaryDto> lateFeeSummDetailDtoList = new ArrayList<>();

		for (Ret1LateFeeSummaryDto resdto : lateFeeSummDetailDto) {

			Ret1LateFeeSummaryDto res = new Ret1LateFeeSummaryDto();

			Map<String, String> lateFeeSectionsMap = buildLateFeeSectionsMap();
			res.setTable(resdto.getTable());
			res.setDesc(lateFeeSectionsMap.get(resdto.getTable()));
			res.setUsrIgst(resdto.getUsrIgst());
			res.setUsrCgst(resdto.getUsrCgst());
			res.setUsrSgst(resdto.getUsrSgst());
			res.setUsrCess(resdto.getUsrCess());
			res.setGstnIgst(resdto.getUsrIgst());
			res.setGstnCgst(resdto.getUsrIgst());
			res.setGstnSgst(resdto.getUsrIgst());
			res.setGstnCess(resdto.getUsrIgst());
			res.setDiffIgst(resdto.getUsrIgst());
			res.setDiffCgst(resdto.getUsrIgst());
			res.setDiffSgst(resdto.getUsrIgst());
			res.setDiffCess(resdto.getUsrIgst());

			lateFeeSummDetailDtoList.add(res);
		}
		return lateFeeSummDetailDtoList;
	}

	private Map<String, String> buildLateFeeSectionsMap() {

		Map<String, String> map = new HashMap<>();
		map.put("6", "Interest & Late Fee ");
		map.put("6(1)",
				"Interest & Late Fee due to late filing (Auto Computed)");
		map.put("6(2)", "Interest - Reversal of ITC");
		map.put("6(3)", "Interest on late reporting of RCM supplies");
		map.put("6(4)", "Other Interest");

		return map;
	}

	@Override
	public List<TaxPaymentSummaryDto> getReviewSummTaxPaymentDownload(
			Annexure1SummaryReqDto ret1SummaryRequest) {
		JsonElement handleRet1ReqTaxPayment = taxPayment
				.handleRe1ReqAndResp(ret1SummaryRequest);
		Gson gson = GsonUtil.newSAPGsonInstance();
		@SuppressWarnings("serial")
		java.lang.reflect.Type type = new TypeToken<Map<String, List<TaxPaymentSummaryDto>>>() {
		}.getType();
		Map<String, List<TaxPaymentSummaryDto>> lateFeeMap = gson
				.fromJson(handleRet1ReqTaxPayment, type);

		List<TaxPaymentSummaryDto> taxPaymenSummDetailDto = lateFeeMap
				.get("tab_7");
		List<TaxPaymentSummaryDto> taxPaymenSummDetailDtoList = new ArrayList<>();

		for (TaxPaymentSummaryDto resdto : taxPaymenSummDetailDto) {

			TaxPaymentSummaryDto res = new TaxPaymentSummaryDto();

			Map<String, String> taxPaymentSectionsMap = buildTaxPaymentSectionsMap();
			res.setTable(resdto.getTable());
			res.setDesc(taxPaymentSectionsMap.get(resdto.getTable()));
			res.setUsrPayable(resdto.getUsrPayable());
			res.setUsrOtherPayable(resdto.getDiffOtherPayable());
			res.setUsrPaid(resdto.getUsrPaid());
			res.setUsrOtherPaid(resdto.getUsrOtherPaid());
			res.setUsrLiability(resdto.getUsrLiability());
			res.setUsrOtherLiability(resdto.getUsrOtherLiability());
			res.setUsrItcPaidIgst(resdto.getUsrItcPaidIgst());
			res.setUsrItcPaidCgst(resdto.getUsrItcPaidCgst());
			res.setUsrItcPaidSgst(resdto.getUsrItcPaidSgst());
			res.setUsrItcPaidCess(resdto.getUsrItcPaidCess());
			res.setUsrCashPaidInterest(resdto.getUsrCashPaidInterest());
			res.setUsrCashPaidTax(resdto.getUsrCashPaidTax());
			res.setUsrCashPaidLateFee(resdto.getUsrCashPaidLateFee());
			res.setGstnPayable(resdto.getGstnPayable());
			res.setGstnOtherPayable(resdto.getGstnOtherPayable());
			res.setGstnPaid(resdto.getGstnPaid());
			res.setGstnOtherPaid(resdto.getGstnOtherPaid());
			res.setGstnLiability(resdto.getGstnLiability());
			res.setGstnOtherLiability(resdto.getGstnOtherLiability());
			res.setGstnItcPaidIgst(resdto.getGstnItcPaidIgst());
			res.setGstnItcPaidCgst(resdto.getGstnItcPaidIgst());
			res.setGstnItcPaidSgst(resdto.getGstnItcPaidSgst());
			res.setGstnItcPaidCess(resdto.getGstnItcPaidCess());
			res.setGstnCashPaidInterest(resdto.getGstnCashPaidInterest());
			res.setGstnCashPaidTax(resdto.getGstnCashPaidTax());
			res.setGstnCashPaidLateFee(resdto.getGstnCashPaidLateFee());
			res.setDiffPayable(resdto.getUsrPayable());
			res.setDiffOtherPayable(resdto.getUsrOtherPayable());
			res.setDiffPaid(resdto.getUsrPaid());
			res.setDiffOtherPaid(resdto.getUsrOtherPaid());
			res.setDiffLiability(resdto.getUsrLiability());
			res.setDiffOtherLiability(resdto.getUsrOtherLiability());
			res.setDiffItcPaidIgst(resdto.getUsrItcPaidIgst());
			res.setDiffItcPaidCgst(resdto.getUsrItcPaidCgst());
			res.setDiffItcPaidSgst(resdto.getUsrItcPaidSgst());
			res.setDiffItcPaidCess(resdto.getUsrItcPaidCess());
			res.setDiffCashPaidInterest(resdto.getUsrCashPaidInterest());
			res.setDiffCashPaidTax(resdto.getUsrCashPaidTax());
			res.setDiffCashPaidLateFee(resdto.getUsrCashPaidLateFee());

			taxPaymenSummDetailDtoList.add(res);
		}
		return taxPaymenSummDetailDtoList;
	}

	private Map<String, String> buildTaxPaymentSectionsMap() {

		Map<String, String> map = new HashMap<>();
		map.put("7", "PaymentTax");
		map.put("7(1)", "IGST");
		map.put("7(2)", "CGST");
		map.put("7(3)", "SGST");
		map.put("7(4)", "Cess");

		return map;
	}

	@Override
	public List<RefundSummaryDto> getReviewSummRefundDownload(
			Annexure1SummaryReqDto ret1SummaryRequest) {

		JsonElement handleRe1ReqRefund = refund
				.handleRe1ReqAndResp(ret1SummaryRequest);
		Gson gson = GsonUtil.newSAPGsonInstance();
		@SuppressWarnings("serial")
		java.lang.reflect.Type type = new TypeToken<Map<String, List<RefundSummaryDto>>>() {
		}.getType();
		Map<String, List<RefundSummaryDto>> lateFeeMap = gson
				.fromJson(handleRe1ReqRefund, type);

		List<RefundSummaryDto> refundSummDetailDto = lateFeeMap.get("tab_8");
		List<RefundSummaryDto> refundSummDetailDtoList = new ArrayList<>();

		for (RefundSummaryDto resdto : refundSummDetailDto) {

			RefundSummaryDto res = new RefundSummaryDto();

			Map<String, String> outwardSectionsMap = buildRefundSectionsMap();
			res.setTable(resdto.getTable());
			res.setDesc(outwardSectionsMap.get(resdto.getTable()));
			res.setUsrTax(resdto.getUsrTax());
			res.setUsrTotal(resdto.getUsrTotal());
			res.setUsrPenality(resdto.getUsrPenality());
			res.setUsrInterest(resdto.getUsrInterest());
			res.setUsrFee(resdto.getUsrFee());
			res.setUsrOther(resdto.getUsrOther());
			res.setGstnTax(resdto.getGstnTax());
			res.setGstnTotal(resdto.getGstnTotal());
			res.setGstnPenality(resdto.getGstnPenality());
			res.setGstnInterest(resdto.getGstnInterest());
			res.setGstnFee(resdto.getGstnFee());
			res.setGstnOther(resdto.getGstnOther());
			res.setDiffTax(resdto.getUsrTax());
			res.setDiffTotal(resdto.getUsrTotal());
			res.setDiffPenality(resdto.getUsrPenality());
			res.setDiffOther(resdto.getUsrOther());
			res.setDiffInterest(resdto.getUsrInterest());
			res.setDiffFee(resdto.getUsrFee());

			refundSummDetailDtoList.add(res);
		}
		return refundSummDetailDtoList;
	}

	private Map<String, String> buildRefundSectionsMap() {
		Map<String, String> map = new HashMap<>();
		map.put("8", "CashLedgerRefund");
		map.put("8(1)", "IGST");
		map.put("8(2)", "CGST");
		map.put("8(3)", "SGST");
		map.put("8(4)", "Cess");
		return map;
	}

	/**
	 * @return
	 */
	private Map<String, String> buildOutwardSectionsMap() {
		Map<String, String> map = new HashMap<>();
		map.put("A3", "Details of outward supplies");
		map.put("3A",
				"Taxable supplies made to consumers and unregistered persons"
						+ " (B2C) [table 3A of FORM GST ANX-1]");
		map.put("3B",
				"Taxable supplies made to registered persons (other than those"
						+ " attracting reverse charge) (B2B) [table 3B of FORM GST ANX-1]");
		map.put("3C",
				"Exports with payment of tax [table 3C of FORM GST ANX-1]");
		map.put("3D",
				"Exports without payment of tax [table 3D of FORM GST ANX-1]");
		map.put("3E",
				"Supplies to SEZ units/developers with payment of tax [table"
						+ " 3E of FORM GST ANX-1]");
		map.put("3F",
				"Supplies to SEZ units / developers without payment of tax "
						+ "[table 3F of FORM GST ANX-1]");
		map.put("3G", "Deemed exports [table 3G of FORM GST ANX-1]");
		map.put("3A8",
				"Liabilities relating to the period prior to the "
						+ "introduction of current return filing system"
						+ "and any other liability to be paid");
		// map.put("A3", "Sub-total (A) [sum of 1 to 8]");
		map.put("B3",
				"B. Details of inward supplies attracting reverse charge");
		map.put("3H",
				"Inward supplies attracting reverse charge (net of debit / credit notes and advances paid, if any) [table 3H of FORM GST ANX-1]");
		map.put("3I",
				"Import of services (net of debit / credit notes and advances paid, if any) [table 3I of FORM GST ANX-1]");
		// map.put("B3", "Sub-total (B) [sum of 1 & 2]");
		map.put("C3",
				"C. Details of debit / credit notes issued, advances received / adjusted and other reduction in liabilities");
		map.put("3C1",
				"Debit notes issued (FORM GST ANX-1) (Other than those attracting reverse charge)");
		map.put("3C2",
				"Credit notes issued (FORM GST ANX-1) (Other than those attracting reverse charge)");
		map.put("3C3",
				"Advances received (net of refund vouchers and including adjustments on account of wrong reporting of advances earlier)");
		map.put("3C4", "Advances adjusted");
		map.put("3C5",
				"Reduction in output tax liability on account of transition from composition levy to normal levy, if any or any other reduction in liability");
		// map.put("C3", "Sub-total (C) [1-2+3-4-5]");
		map.put("D3", "D. Details of supplies having no liability");
		map.put("3D1", "Exempt and Nil rated supplies");
		map.put("3D2",
				"Non-GST supplies (including No Supply / Schedule III supplies)");
		map.put("3D3",
				"Outward supplies attracting reverse charge (net of debit/ credit notes)");
		map.put("3D4",
				"Supply of goods by a SEZ unit / developer to DTA on a Bill of Entry");
		// map.put("D3", "Sub-total (D) [sum of 1 to 4]");
		map.put("3E", "E. Total value and tax liability (A+B+C+D)");
		map.put("A4",
				"Details of ITC based on auto-population from FORM GST ANX-1, action taken in FORM GST ANX-2 and other claims");
		map.put("4A1",
				"Credit on all documents which have been rejected in FORM GST ANX-2 (net of debit /credit notes)");
		map.put("4A2",
				"Credit on all documents which have been kept pending in FORM GST ANX-2 (net of debit /credit notes)");
		map.put("4A3",
				"Credit on all documents which have been accepted (including deemed accepted) in FORM GST ANX-2 (net of debit/credit notes)");
		map.put("4A4",
				"Eligible credit (after 1st July, 2017) not availed prior to the introduction of this return but admissible as per Law (transition to new return system)");
		map.put("3H",
				"Inward supplies attracting reverse charge (net of debit/credit notes and advances paid, if any) [table 3H of FORM GST ANX-1]");
		map.put("3I",
				"Import of services (net of debit /credit notes and advances paid, if any and excluding services received from SEZ units) [table 3I of FORM GST ANX-1]");
		map.put("3J", "Import of goods [table 3J of FORM GST ANX-1]");
		map.put("3K",
				"Import of goods from SEZ units / developers [table 3K of FORM GST ANX-1]");
		map.put("ISD",
				"ISD Credit (net of ISD credit notes) [table 5 of FORM GST ANX-2]");
		map.put("4A10",
				"Provisional input tax credit on documents not uploaded by the suppliers [net of ineligible credit]");
		map.put("4A11",
				"Upward adjustment in input tax credit due to receipt of credit notes and all other adjustments and reclaims");
		// map.put("A4", "Sub-total (A) [sum of 3 to 11]");
		map.put("B4", "Details of reversals of credit");
		map.put("4B1",
				"Credit on documents which have been accepted in previous returns but rejected in current tax period (net of debit/ credit notes)");
		map.put("4B2",
				"Supplies not eligible for credit (including ISD credit) [out of net credit available in table 4A above]");
		map.put("4B3", "Reversal of credit in respect of supplies on which "
				+ "provisional credit has already been claimed in the previous "
				+ "tax periods but documents have been uploaded by the supplier"
				+ " in the current tax period (net of ineligible credit)");
		map.put("4B4",
				"Reversal of input tax credit as per law (Rule 37, 39, 42 & 43)");
		map.put("4B5",
				"Other reversals including downward adjustment of ITC on account of transition from composition levy to normal levy, If any");
		// map.put("B4", "Sub-total (B) [sum of 1 to 5]");
		map.put("C4", "C. ITC available (net of reversals) (A- B)");
		map.put("D4",
				"ITC declared during first two months of the quarter (Only for quarterly return filers)");
		map.put("4D1", "First month");
		map.put("4D2", "Second month");
		map.put("E4", "E. Net ITC available (C-D)");
		map.put("4E1", "Input tax credit on capital goods (out of C)");
		map.put("4E2", "Input tax credit on services (out of C)");
		map.put("4E3", "Input tax credit on Input Goods (Balance)");
		map.put("5", "TDS and TCS Credit");
		map.put("TDS", "TDS");
		map.put("TCS", "TCS");

		return map;
	}

}
