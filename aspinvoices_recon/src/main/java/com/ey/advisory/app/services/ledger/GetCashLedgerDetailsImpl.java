package com.ey.advisory.app.services.ledger;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Font;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.app.docs.dto.ledger.CashDetailsRespDto;
import com.ey.advisory.app.docs.dto.ledger.CashLedgerExcelDownloadDto;
import com.ey.advisory.app.docs.dto.ledger.CashLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.CashLedgerPDFDownloadDto;
import com.ey.advisory.app.docs.dto.ledger.CashLedgersDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.CrReversalLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.CreditDetailsRespDto;
import com.ey.advisory.app.docs.dto.ledger.CreditRevAndReclaimTransactionDto;
import com.ey.advisory.app.docs.dto.ledger.CreditReverseAndReclaimLedgerDto;
import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;
import com.ey.advisory.app.docs.dto.ledger.ItcCrReversalAndReclaimDto;
import com.ey.advisory.app.docs.dto.ledger.ItcLedgerDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.ItcLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.ItcTransactionTypeBalDto;
import com.ey.advisory.app.docs.dto.ledger.LedgerItcReclaimBalanceAmts;
import com.ey.advisory.app.docs.dto.ledger.NegativeDetailedLedgerDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.NegativeDetailedLedgerTransactionDto;
import com.ey.advisory.app.docs.dto.ledger.NegativeDetailsRespDto;
import com.ey.advisory.app.docs.dto.ledger.RcmDetailedLedgerDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.RcmDetailedLedgerTransactionDto;
import com.ey.advisory.app.docs.dto.ledger.RcmDetailsRespDto;
import com.ey.advisory.app.docs.dto.ledger.ReversalAndReclaimDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.TransactionTypeBalDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDao;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author Nikhil Duseja
 *
 */
@Service("getCashLedgerDetailsImpl")
public class GetCashLedgerDetailsImpl implements GetCashLedgerDetails {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetCashLedgerDetailsImpl.class);

	@Autowired
	@Qualifier("cashLedgerDetailsDataAtGstnImpl")
	private CashLedgerDetailsDataAtGstn cashDataAtGstn;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("taxPayerDetailsDaoImpl")
	TaxPayerDetailsDao taxPayerDao;

	@Autowired
	@Qualifier("iTCLedgerDetailsDataAtGstnImpl")
	private ITCLedgerDetailsDataAtGstn itcDataAtGstn;

	@Autowired
	@Qualifier("cashLedgerDetailsDataAtGstnImpl")
	private CashLedgerDetailsDataAtGstn crReversalAndReclaim;

	@Autowired
	@Qualifier("cashLedgerDetailsDataAtGstnImpl")
	private CashLedgerDetailsDataAtGstn rcmDetails;

	@Override
	public String findCash(String jsonReq, String groupCode) {

		String apiResp = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<CashDetailsRespDto> allDetailsDto = new ArrayList<>();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			GetCashLedgerDetailsReqDto dto = gson.fromJson(reqObject,
					GetCashLedgerDetailsReqDto.class);

			apiResp = cashDataAtGstn.fromGstn(dto);
			if (apiResp != null) {
				requestObject = (new JsonParser()).parse(apiResp)
						.getAsJsonObject();
				CashLedgersDetailsDto responseDto = gson.fromJson(requestObject,
						CashLedgersDetailsDto.class);

				CashLedgerOpenCloseBalDto closeBal = responseDto.getClosenBal();
				CashLedgerOpenCloseBalDto openBal = responseDto.getOpenBal();
				List<TransactionTypeBalDto> respTransDtos = responseDto
						.getTransTypeBalDto();
				if (openBal != null) {
					CashDetailsRespDto respOpenBal = new CashDetailsRespDto();
					respOpenBal.setDescription(openBal.getDescription());
					respOpenBal.setCessBal(openBal.getCessbal());
					respOpenBal.setIgstBal(openBal.getIgstbal());
					respOpenBal.setCgstBal(openBal.getCgstbal());
					respOpenBal.setSgstBal(openBal.getSgstbal());
					allDetailsDto.add(respOpenBal);
				}

				respTransDtos.forEach(obj -> {
					CashDetailsRespDto transBal = new CashDetailsRespDto();
					transBal.setDptDate(obj.getDptDate());
					transBal.setDptTime(obj.getDptTime());
					transBal.setRptDate(obj.getRptDate());
					transBal.setReferenceNo(obj.getReferenceNo());
					transBal.setRetPeriod(obj.getRetPeriod());
					transBal.setDescription(obj.getDescription());
					transBal.setTransType(obj.getTransType());
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

				if (closeBal != null) {
					CashDetailsRespDto respCloseBal = new CashDetailsRespDto();
					respCloseBal.setDescription(closeBal.getDescription());
					respCloseBal.setCessBal(closeBal.getCessbal());
					respCloseBal.setIgstBal(closeBal.getIgstbal());
					respCloseBal.setCgstBal(closeBal.getCgstbal());
					respCloseBal.setSgstBal(closeBal.getSgstbal());
					allDetailsDto.add(respCloseBal);
				}
			}
		} catch (Exception ex) {
			String msg = String.format("Unexpected Error Occured %s",
					ex.getMessage());
			JsonObject resp1 = new JsonObject();
			resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return resp1.getAsString();
		}
		JsonObject resp1 = new JsonObject();
		JsonElement respBody = gson.toJsonTree(allDetailsDto);
		resp1.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp1.add("resp", respBody);
		return resp1.getAsString();
	}

	// download workbook
	@Override
	public Workbook findCashReportDownload(String jsonReq, String groupCode) {
		Workbook workbook = null;
		String apiResp = null;
		String legalName = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<CashLedgerExcelDownloadDto> allDetailsDto = new ArrayList<>();
		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		JsonObject reqObject = requestObject.get("req").getAsJsonObject();
		GetCashLedgerDetailsReqDto dto = gson.fromJson(reqObject,
				GetCashLedgerDetailsReqDto.class);
		String gstin = dto.getGstin();
		String fromDate = dto.getFromDate();
		String toDate = dto.getToDate();
		final int[] srNoCounter = { 1 }; // Using an array to make it mutable

		APIResponse apiResponse = taxPayerDao.findTaxPayerDetails(gstin,
				groupCode);
		if (apiResponse.isSuccess()) {
			String response = apiResponse.getResponse();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Following is Tax Payer Detailed "
								+ " Response Dto from Gstin  %s :",
						apiResponse);
				LOGGER.debug(msg);
			}

			JsonObject requestObj = JsonParser.parseString(response)
					.getAsJsonObject();
			legalName = checkForNull(requestObj.get("lgnm"));
		}

		try {

			apiResp = cashDataAtGstn.fromGstn(dto);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Following is cashDataAtGstn "
						+ " Response from Gstin  %s :", apiResp);
				LOGGER.debug(msg);
			}
			if (apiResp != null) {
				JsonObject req = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				CashLedgersDetailsDto responseDto = gson.fromJson(req,
						CashLedgersDetailsDto.class);
				CashLedgerOpenCloseBalDto closeBal = responseDto.getClosenBal();
				CashLedgerOpenCloseBalDto openBal = responseDto.getOpenBal();
				List<TransactionTypeBalDto> respTransDtos = responseDto
						.getTransTypeBalDto();
				if (openBal != null) {
					CashLedgerExcelDownloadDto respOpenBal = new CashLedgerExcelDownloadDto();
					// respOpenBal.setSrNo(srNoCounter++);
					respOpenBal.setSrNo(srNoCounter[0]++);

					respOpenBal.setDescription(openBal.getDescription());
					// CESS BAL
					respOpenBal.setCessBalTaxValue(GenUtil.formatCurrency(
					    openBal.getCessbal() != null && openBal.getCessbal().getTaxValue() != null 
					        ? openBal.getCessbal().getTaxValue() 
					        : "0"));
					respOpenBal.setCessBalInterestValue(
					    GenUtil.formatCurrency(
					        openBal.getCessbal() != null && openBal.getCessbal().getInterestValue() != null 
					            ? openBal.getCessbal().getInterestValue() 
					            : "0"));
					respOpenBal.setCessBalPenalty(
					    GenUtil.formatCurrency(
					        openBal.getCessbal() != null && openBal.getCessbal().getPenalty() != null 
					            ? openBal.getCessbal().getPenalty() 
					            : "0"));
					respOpenBal.setCessBalFees(
					    GenUtil.formatCurrency(
					        openBal.getCessbal() != null && openBal.getCessbal().getFees() != null 
					            ? openBal.getCessbal().getFees() 
					            : "0"));
					respOpenBal.setCessBalOther(
					    GenUtil.formatCurrency(
					        openBal.getCessbal() != null && openBal.getCessbal().getOther() != null 
					            ? openBal.getCessbal().getOther() 
					            : "0"));
					respOpenBal.setCessBalTotal(
					    GenUtil.formatCurrency(
					        openBal.getCessbal() != null && openBal.getCessbal().getTotal() != null 
					            ? openBal.getCessbal().getTotal() 
					            : "0"));

					// IGST BAL
					respOpenBal.setIgstBalTaxValue(GenUtil.formatCurrency(
					    openBal.getIgstbal() != null && openBal.getIgstbal().getTaxValue() != null 
					        ? openBal.getIgstbal().getTaxValue() 
					        : "0"));
					respOpenBal.setIgstBalInterestValue(
					    GenUtil.formatCurrency(
					        openBal.getIgstbal() != null && openBal.getIgstbal().getInterestValue() != null 
					            ? openBal.getIgstbal().getInterestValue() 
					            : "0"));
					respOpenBal.setIgstBalPenalty(
					    GenUtil.formatCurrency(
					        openBal.getIgstbal() != null && openBal.getIgstbal().getPenalty() != null 
					            ? openBal.getIgstbal().getPenalty() 
					            : "0"));
					respOpenBal.setIgstBalFees(
					    GenUtil.formatCurrency(
					        openBal.getIgstbal() != null && openBal.getIgstbal().getFees() != null 
					            ? openBal.getIgstbal().getFees() 
					            : "0"));
					respOpenBal.setIgstBalOther(
					    GenUtil.formatCurrency(
					        openBal.getIgstbal() != null && openBal.getIgstbal().getOther() != null 
					            ? openBal.getIgstbal().getOther() 
					            : "0"));
					respOpenBal.setIgstBalTotal(
					    GenUtil.formatCurrency(
					        openBal.getIgstbal() != null && openBal.getIgstbal().getTotal() != null 
					            ? openBal.getIgstbal().getTotal() 
					            : "0"));

					// CGST BAL
					respOpenBal.setCgstBalTaxValue(GenUtil.formatCurrency(
					    openBal.getCgstbal() != null && openBal.getCgstbal().getTaxValue() != null 
					        ? openBal.getCgstbal().getTaxValue() 
					        : "0"));
					respOpenBal.setCgstBalInterestValue(
					    GenUtil.formatCurrency(
					        openBal.getCgstbal() != null && openBal.getCgstbal().getInterestValue() != null 
					            ? openBal.getCgstbal().getInterestValue() 
					            : "0"));
					respOpenBal.setCgstBalPenalty(
					    GenUtil.formatCurrency(
					        openBal.getCgstbal() != null && openBal.getCgstbal().getPenalty() != null 
					            ? openBal.getCgstbal().getPenalty() 
					            : "0"));
					respOpenBal.setCgstBalFees(
					    GenUtil.formatCurrency(
					        openBal.getCgstbal() != null && openBal.getCgstbal().getFees() != null 
					            ? openBal.getCgstbal().getFees() 
					            : "0"));
					respOpenBal.setCgstBalOther(
					    GenUtil.formatCurrency(
					        openBal.getCgstbal() != null && openBal.getCgstbal().getOther() != null 
					            ? openBal.getCgstbal().getOther() 
					            : "0"));
					respOpenBal.setCgstBalTotal(
					    GenUtil.formatCurrency(
					        openBal.getCgstbal() != null && openBal.getCgstbal().getTotal() != null 
					            ? openBal.getCgstbal().getTotal() 
					            : "0"));

					// SGST BAL
					respOpenBal.setSgstBalTaxValue(GenUtil.formatCurrency(
					    openBal.getSgstbal() != null && openBal.getSgstbal().getTaxValue() != null 
					        ? openBal.getSgstbal().getTaxValue() 
					        : "0"));
					respOpenBal.setSgstBalInterestValue(
					    GenUtil.formatCurrency(
					        openBal.getSgstbal() != null && openBal.getSgstbal().getInterestValue() != null 
					            ? openBal.getSgstbal().getInterestValue() 
					            : "0"));
					respOpenBal.setSgstBalPenalty(
					    GenUtil.formatCurrency(
					        openBal.getSgstbal() != null && openBal.getSgstbal().getPenalty() != null 
					            ? openBal.getSgstbal().getPenalty() 
					            : "0"));
					respOpenBal.setSgstBalFees(
					    GenUtil.formatCurrency(
					        openBal.getSgstbal() != null && openBal.getSgstbal().getFees() != null 
					            ? openBal.getSgstbal().getFees() 
					            : "0"));
					respOpenBal.setSgstBalOther(
					    GenUtil.formatCurrency(
					        openBal.getSgstbal() != null && openBal.getSgstbal().getOther() != null 
					            ? openBal.getSgstbal().getOther() 
					            : "0"));
					respOpenBal.setSgstBalTotal(
					    GenUtil.formatCurrency(
					        openBal.getSgstbal() != null && openBal.getSgstbal().getTotal() != null 
					            ? openBal.getSgstbal().getTotal() 
					            : "0"));


					allDetailsDto.add(respOpenBal);
				}
				if (respTransDtos != null) {
					respTransDtos.forEach(obj -> {
						CashLedgerExcelDownloadDto transBal = new CashLedgerExcelDownloadDto();
						// transBal.setGstn(gstin);
						transBal.setSrNo(srNoCounter[0]++);

						transBal.setDptDate(obj.getDptDate() != null
								? obj.getDptDate() : "-");

						transBal.setDptTime(obj.getDptTime() != null
								? obj.getDptTime() : "-");
						transBal.setRptDate(obj.getRptDate() != null
								? obj.getRptDate() : "-");
						transBal.setReferenceNo(obj.getReferenceNo() != null
								? obj.getReferenceNo() : "-");
						transBal.setRetPeriod(obj.getRetPeriod() != null
								? obj.getRetPeriod() : "-");
						transBal.setDescription(obj.getDescription() != null
								? obj.getDescription() : "-");
						transBal.setTransType(obj.getTransType() != null
								? obj.getTransType() : "-");

						// IGST
						transBal.setIgstTaxValue(
						    GenUtil.formatCurrency(obj.getIgst() != null && obj.getIgst().getTaxValue() != null 
						        ? obj.getIgst().getTaxValue() 
						        : "0"));
						transBal.setIgstInterestValue(
						    GenUtil.formatCurrency(obj.getIgst() != null && obj.getIgst().getInterestValue() != null 
						        ? obj.getIgst().getInterestValue() 
						        : "0"));
						transBal.setIgstPenalty(
						    GenUtil.formatCurrency(obj.getIgst() != null && obj.getIgst().getPenalty() != null 
						        ? obj.getIgst().getPenalty() 
						        : "0"));
						transBal.setIgstFees(
						    GenUtil.formatCurrency(obj.getIgst() != null && obj.getIgst().getFees() != null 
						        ? obj.getIgst().getFees() 
						        : "0"));
						transBal.setIgstOther(
						    GenUtil.formatCurrency(obj.getIgst() != null && obj.getIgst().getOther() != null 
						        ? obj.getIgst().getOther() 
						        : "0"));
						transBal.setIgstTotal(
						    GenUtil.formatCurrency(obj.getIgst() != null && obj.getIgst().getTotal() != null 
						        ? obj.getIgst().getTotal() 
						        : "0"));

						// IGST Bal
						transBal.setIgstBalTaxValue(
						    GenUtil.formatCurrency(obj.getIgstBal() != null && obj.getIgstBal().getTaxValue() != null 
						        ? obj.getIgstBal().getTaxValue() 
						        : "0"));
						transBal.setIgstBalInterestValue(
						    GenUtil.formatCurrency(obj.getIgstBal() != null && obj.getIgstBal().getInterestValue() != null 
						        ? obj.getIgstBal().getInterestValue() 
						        : "0"));
						transBal.setIgstBalPenalty(
						    GenUtil.formatCurrency(obj.getIgstBal() != null && obj.getIgstBal().getPenalty() != null 
						        ? obj.getIgstBal().getPenalty() 
						        : "0"));
						transBal.setIgstBalFees(
						    GenUtil.formatCurrency(obj.getIgstBal() != null && obj.getIgstBal().getFees() != null 
						        ? obj.getIgstBal().getFees() 
						        : "0"));
						transBal.setIgstBalOther(
						    GenUtil.formatCurrency(obj.getIgstBal() != null && obj.getIgstBal().getOther() != null 
						        ? obj.getIgstBal().getOther() 
						        : "0"));
						transBal.setIgstBalTotal(
						    GenUtil.formatCurrency(obj.getIgstBal() != null && obj.getIgstBal().getTotal() != null 
						        ? obj.getIgstBal().getTotal() 
						        : "0"));

						// igstBal Bal end

						// cgst

						// CGST
						transBal.setCgstTaxValue(
						    GenUtil.formatCurrency(obj.getCgst() != null && obj.getCgst().getTaxValue() != null 
						        ? obj.getCgst().getTaxValue() 
						        : "0"));
						transBal.setCgstInterestValue(
						    GenUtil.formatCurrency(obj.getCgst() != null && obj.getCgst().getInterestValue() != null 
						        ? obj.getCgst().getInterestValue() 
						        : "0"));
						transBal.setCgstPenalty(
						    GenUtil.formatCurrency(obj.getCgst() != null && obj.getCgst().getPenalty() != null 
						        ? obj.getCgst().getPenalty() 
						        : "0"));
						transBal.setCgstFees(
						    GenUtil.formatCurrency(obj.getCgst() != null && obj.getCgst().getFees() != null 
						        ? obj.getCgst().getFees() 
						        : "0"));
						transBal.setCgstOther(
						    GenUtil.formatCurrency(obj.getCgst() != null && obj.getCgst().getOther() != null 
						        ? obj.getCgst().getOther() 
						        : "0"));
						transBal.setCgstTotal(
						    GenUtil.formatCurrency(obj.getCgst() != null && obj.getCgst().getTotal() != null 
						        ? obj.getCgst().getTotal() 
						        : "0"));

						// CGST Bal
						transBal.setCgstBalTaxValue(
						    GenUtil.formatCurrency(obj.getCgstBal() != null && obj.getCgstBal().getTaxValue() != null 
						        ? obj.getCgstBal().getTaxValue() 
						        : "0"));
						transBal.setCgstBalInterestValue(
						    GenUtil.formatCurrency(obj.getCgstBal() != null && obj.getCgstBal().getInterestValue() != null 
						        ? obj.getCgstBal().getInterestValue() 
						        : "0"));
						transBal.setCgstBalPenalty(
						    GenUtil.formatCurrency(obj.getCgstBal() != null && obj.getCgstBal().getPenalty() != null 
						        ? obj.getCgstBal().getPenalty() 
						        : "0"));
						transBal.setCgstBalFees(
						    GenUtil.formatCurrency(obj.getCgstBal() != null && obj.getCgstBal().getFees() != null 
						        ? obj.getCgstBal().getFees() 
						        : "0"));
						transBal.setCgstBalOther(
						    GenUtil.formatCurrency(obj.getCgstBal() != null && obj.getCgstBal().getOther() != null 
						        ? obj.getCgstBal().getOther() 
						        : "0"));
						transBal.setCgstBalTotal(
						    GenUtil.formatCurrency(obj.getCgstBal() != null && obj.getCgstBal().getTotal() != null 
						        ? obj.getCgstBal().getTotal() 
						        : "0"));

						// SGST
						transBal.setSgstTaxValue(
						    GenUtil.formatCurrency(obj.getSgst() != null && obj.getSgst().getTaxValue() != null 
						        ? obj.getSgst().getTaxValue() 
						        : "0"));
						transBal.setSgstInterestValue(
						    GenUtil.formatCurrency(obj.getSgst() != null && obj.getSgst().getInterestValue() != null 
						        ? obj.getSgst().getInterestValue() 
						        : "0"));
						transBal.setSgstPenalty(
						    GenUtil.formatCurrency(obj.getSgst() != null && obj.getSgst().getPenalty() != null 
						        ? obj.getSgst().getPenalty() 
						        : "0"));
						transBal.setSgstFees(
						    GenUtil.formatCurrency(obj.getSgst() != null && obj.getSgst().getFees() != null 
						        ? obj.getSgst().getFees() 
						        : "0"));
						transBal.setSgstOther(
						    GenUtil.formatCurrency(obj.getSgst() != null && obj.getSgst().getOther() != null 
						        ? obj.getSgst().getOther() 
						        : "0"));
						transBal.setSgstTotal(
						    GenUtil.formatCurrency(obj.getSgst() != null && obj.getSgst().getTotal() != null 
						        ? obj.getSgst().getTotal() 
						        : "0"));

						// SGST Bal
						transBal.setSgstBalTaxValue(
						    GenUtil.formatCurrency(obj.getSgstBal() != null && obj.getSgstBal().getTaxValue() != null 
						        ? obj.getSgstBal().getTaxValue() 
						        : "0"));
						transBal.setSgstBalInterestValue(
						    GenUtil.formatCurrency(obj.getSgstBal() != null && obj.getSgstBal().getInterestValue() != null 
						        ? obj.getSgstBal().getInterestValue() 
						        : "0"));
						transBal.setSgstBalPenalty(
						    GenUtil.formatCurrency(obj.getSgstBal() != null && obj.getSgstBal().getPenalty() != null 
						        ? obj.getSgstBal().getPenalty() 
						        : "0"));
						transBal.setSgstBalFees(
						    GenUtil.formatCurrency(obj.getSgstBal() != null && obj.getSgstBal().getFees() != null 
						        ? obj.getSgstBal().getFees() 
						        : "0"));
						transBal.setSgstBalOther(
						    GenUtil.formatCurrency(obj.getSgstBal() != null && obj.getSgstBal().getOther() != null 
						        ? obj.getSgstBal().getOther() 
						        : "0"));
						transBal.setSgstBalTotal(
						    GenUtil.formatCurrency(obj.getSgstBal() != null && obj.getSgstBal().getTotal() != null 
						        ? obj.getSgstBal().getTotal() 
						        : "0"));


						// Cess
						transBal.setCessTaxValue(
						    GenUtil.formatCurrency(obj.getCess() != null && obj.getCess().getTaxValue() != null 
						        ? obj.getCess().getTaxValue() 
						        : "0"));
						transBal.setCessInterestValue(
						    GenUtil.formatCurrency(obj.getCess() != null && obj.getCess().getInterestValue() != null 
						        ? obj.getCess().getInterestValue() 
						        : "0"));
						transBal.setCessPenalty(
						    GenUtil.formatCurrency(obj.getCess() != null && obj.getCess().getPenalty() != null 
						        ? obj.getCess().getPenalty() 
						        : "0"));
						transBal.setCessFees(
						    GenUtil.formatCurrency(obj.getCess() != null && obj.getCess().getFees() != null 
						        ? obj.getCess().getFees() 
						        : "0"));
						transBal.setCessOther(
						    GenUtil.formatCurrency(obj.getCess() != null && obj.getCess().getOther() != null 
						        ? obj.getCess().getOther() 
						        : "0"));
						transBal.setCessTotal(
						    GenUtil.formatCurrency(obj.getCess() != null && obj.getCess().getTotal() != null 
						        ? obj.getCess().getTotal() 
						        : "0"));

						// Cess Bal
						transBal.setCessBalTaxValue(
						    GenUtil.formatCurrency(obj.getCessBal() != null && obj.getCessBal().getTaxValue() != null 
						        ? obj.getCessBal().getTaxValue() 
						        : "0"));
						transBal.setCessBalInterestValue(
						    GenUtil.formatCurrency(obj.getCessBal() != null && obj.getCessBal().getInterestValue() != null 
						        ? obj.getCessBal().getInterestValue() 
						        : "0"));
						transBal.setCessBalPenalty(
						    GenUtil.formatCurrency(obj.getCessBal() != null && obj.getCessBal().getPenalty() != null 
						        ? obj.getCessBal().getPenalty() 
						        : "0"));
						transBal.setCessBalFees(
						    GenUtil.formatCurrency(obj.getCessBal() != null && obj.getCessBal().getFees() != null 
						        ? obj.getCessBal().getFees() 
						        : "0"));
						transBal.setCessBalOther(
						    GenUtil.formatCurrency(obj.getCessBal() != null && obj.getCessBal().getOther() != null 
						        ? obj.getCessBal().getOther() 
						        : "0"));
						transBal.setCessBalTotal(
						    GenUtil.formatCurrency(obj.getCessBal() != null && obj.getCessBal().getTotal() != null 
						        ? obj.getCessBal().getTotal() 
						        : "0"));


						allDetailsDto.add(transBal);
					});
				}
				if (closeBal != null) {
					CashLedgerExcelDownloadDto respCloseBal = new CashLedgerExcelDownloadDto();
					respCloseBal.setDescription(closeBal.getDescription());
					respCloseBal.setSrNo(srNoCounter[0]++);
					// cess bal
					respCloseBal.setCessBalTaxValue(
					    GenUtil.formatCurrency(closeBal.getCessbal() != null && closeBal.getCessbal().getTaxValue() != null 
					        ? closeBal.getCessbal().getTaxValue() 
					        : "0"));
					respCloseBal.setCessBalInterestValue(
					    GenUtil.formatCurrency(closeBal.getCessbal() != null && closeBal.getCessbal().getInterestValue() != null 
					        ? closeBal.getCessbal().getInterestValue() 
					        : "0"));
					respCloseBal.setCessBalPenalty(
					    GenUtil.formatCurrency(closeBal.getCessbal() != null && closeBal.getCessbal().getPenalty() != null 
					        ? closeBal.getCessbal().getPenalty() 
					        : "0"));
					respCloseBal.setCessBalFees(
					    GenUtil.formatCurrency(closeBal.getCessbal() != null && closeBal.getCessbal().getFees() != null 
					        ? closeBal.getCessbal().getFees() 
					        : "0"));
					respCloseBal.setCessBalOther(
					    GenUtil.formatCurrency(closeBal.getCessbal() != null && closeBal.getCessbal().getOther() != null 
					        ? closeBal.getCessbal().getOther() 
					        : "0"));
					respCloseBal.setCessBalTotal(
					    GenUtil.formatCurrency(closeBal.getCessbal() != null && closeBal.getCessbal().getTotal() != null 
					        ? closeBal.getCessbal().getTotal() 
					        : "0"));

					// IGST Bal
					respCloseBal.setIgstBalTaxValue(
					    GenUtil.formatCurrency(closeBal.getIgstbal() != null && closeBal.getIgstbal().getTaxValue() != null 
					        ? closeBal.getIgstbal().getTaxValue() 
					        : "0"));
					respCloseBal.setIgstBalInterestValue(
					    GenUtil.formatCurrency(closeBal.getIgstbal() != null && closeBal.getIgstbal().getInterestValue() != null 
					        ? closeBal.getIgstbal().getInterestValue() 
					        : "0"));
					respCloseBal.setIgstBalPenalty(
					    GenUtil.formatCurrency(closeBal.getIgstbal() != null && closeBal.getIgstbal().getPenalty() != null 
					        ? closeBal.getIgstbal().getPenalty() 
					        : "0"));
					respCloseBal.setIgstBalFees(
					    GenUtil.formatCurrency(closeBal.getIgstbal() != null && closeBal.getIgstbal().getFees() != null 
					        ? closeBal.getIgstbal().getFees() 
					        : "0"));
					respCloseBal.setIgstBalOther(
					    GenUtil.formatCurrency(closeBal.getIgstbal() != null && closeBal.getIgstbal().getOther() != null 
					        ? closeBal.getIgstbal().getOther() 
					        : "0"));
					respCloseBal.setIgstBalTotal(
					    GenUtil.formatCurrency(closeBal.getIgstbal() != null && closeBal.getIgstbal().getTotal() != null 
					        ? closeBal.getIgstbal().getTotal() 
					        : "0"));

					// CGST Bal
					respCloseBal.setCgstBalTaxValue(
					    GenUtil.formatCurrency(closeBal.getCgstbal() != null && closeBal.getCgstbal().getTaxValue() != null 
					        ? closeBal.getCgstbal().getTaxValue() 
					        : "0"));
					respCloseBal.setCgstBalInterestValue(
					    GenUtil.formatCurrency(closeBal.getCgstbal() != null && closeBal.getCgstbal().getInterestValue() != null 
					        ? closeBal.getCgstbal().getInterestValue() 
					        : "0"));
					respCloseBal.setCgstBalPenalty(
					    GenUtil.formatCurrency(closeBal.getCgstbal() != null && closeBal.getCgstbal().getPenalty() != null 
					        ? closeBal.getCgstbal().getPenalty() 
					        : "0"));
					respCloseBal.setCgstBalFees(
					    GenUtil.formatCurrency(closeBal.getCgstbal() != null && closeBal.getCgstbal().getFees() != null 
					        ? closeBal.getCgstbal().getFees() 
					        : "0"));
					respCloseBal.setCgstBalOther(
					    GenUtil.formatCurrency(closeBal.getCgstbal() != null && closeBal.getCgstbal().getOther() != null 
					        ? closeBal.getCgstbal().getOther() 
					        : "0"));
					respCloseBal.setCgstBalTotal(
					    GenUtil.formatCurrency(closeBal.getCgstbal() != null && closeBal.getCgstbal().getTotal() != null 
					        ? closeBal.getCgstbal().getTotal() 
					        : "0"));

					// SGST Bal
					respCloseBal.setSgstBalTaxValue(
					    GenUtil.formatCurrency(closeBal.getSgstbal() != null && closeBal.getSgstbal().getTaxValue() != null 
					        ? closeBal.getSgstbal().getTaxValue() 
					        : "0"));
					respCloseBal.setSgstBalInterestValue(
					    GenUtil.formatCurrency(closeBal.getSgstbal() != null && closeBal.getSgstbal().getInterestValue() != null 
					        ? closeBal.getSgstbal().getInterestValue() 
					        : "0"));
					respCloseBal.setSgstBalPenalty(
					    GenUtil.formatCurrency(closeBal.getSgstbal() != null && closeBal.getSgstbal().getPenalty() != null 
					        ? closeBal.getSgstbal().getPenalty() 
					        : "0"));
					respCloseBal.setSgstBalFees(
					    GenUtil.formatCurrency(closeBal.getSgstbal() != null && closeBal.getSgstbal().getFees() != null 
					        ? closeBal.getSgstbal().getFees() 
					        : "0"));
					respCloseBal.setSgstBalOther(
					    GenUtil.formatCurrency(closeBal.getSgstbal() != null && closeBal.getSgstbal().getOther() != null 
					        ? closeBal.getSgstbal().getOther() 
					        : "0"));
					respCloseBal.setSgstBalTotal(
					    GenUtil.formatCurrency(closeBal.getSgstbal() != null && closeBal.getSgstbal().getTotal() != null 
					        ? closeBal.getSgstbal().getTotal() 
					        : "0"));


					allDetailsDto.add(respCloseBal);
				}

			}

			if (!allDetailsDto.isEmpty()) {
				workbook = writeToExcel(allDetailsDto, gstin, fromDate, toDate,
						legalName);
			} else {
				workbook = writeToExcel1(gstin, fromDate, toDate, legalName);
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured in the findCashReportDownload method during "
							+ "generating the excel sheet, %s ",
					ex.getMessage());
			LOGGER.error(msg);
			throw new AppException(ex.getMessage(), ex);
		}
		return workbook;

	}

	@Override
	public Workbook findCreditReportDownload(String jsonReq, String groupCode) {

		Workbook workbook = null;
		String apiResp = null;
		String legalName = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<CreditDetailsRespDto> allCreditDetailsDto = new ArrayList<>();

		JsonElement jsonElement = JsonParser.parseString(jsonReq);
		JsonObject requestObject = jsonElement.getAsJsonObject();

		JsonObject reqObject = requestObject.get("req").getAsJsonObject();
		GetCashLedgerDetailsReqDto dto = gson.fromJson(reqObject,
				GetCashLedgerDetailsReqDto.class);
		String gstin = dto.getGstin();
		String fromDate = dto.getFromDate();
		String toDate = dto.getToDate();
		final int[] srNoCounter = { 1 }; // Using an array to make it mutable

		APIResponse apiResponse = taxPayerDao.findTaxPayerDetails(gstin,
				groupCode);
		if (apiResponse.isSuccess()) {
			String response = apiResponse.getResponse();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Following is Tax Payer Detailed "
								+ " Response Dto from Gstin  %s :",
						apiResponse);
				LOGGER.debug(msg);
			}

			JsonObject requestObj = JsonParser.parseString(response)
					.getAsJsonObject();
			legalName = checkForNull(requestObj.get("lgnm"));
		}

		try {

			apiResp = itcDataAtGstn.fromGstn(dto);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Following is credit ledger Details "
						+ " Response  from Gstin  %s :", apiResp);
				LOGGER.debug(msg);
			}
			if (apiResp != null) {
				JsonObject creditReqObject = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				JsonObject creditReqObj = creditReqObject.get("itcLdgDtls")
						.getAsJsonObject();
				ItcLedgerDetailsDto responseDto = gson.fromJson(creditReqObj,
						ItcLedgerDetailsDto.class);

				ItcLedgerOpenCloseBalDto closeBal = responseDto.getClosenBal();
				ItcLedgerOpenCloseBalDto openBal = responseDto.getOpenBal();
				List<ItcTransactionTypeBalDto> respTransDtos = responseDto
						.getItctransTypeBalDto();
				if (openBal != null) {
					CreditDetailsRespDto respOpenBal = new CreditDetailsRespDto();
					respOpenBal.setSrNo(srNoCounter[0]++);
					respOpenBal.setDesc(openBal.getDescription());
					respOpenBal.setBalIgst(GenUtil.formatCurrency(openBal.getIgstTaxBal() != null ? openBal.getIgstTaxBal() : "0"));
					respOpenBal.setBalCgst(GenUtil.formatCurrency(openBal.getCgstTaxBal() != null ? openBal.getCgstTaxBal() : "0"));
					respOpenBal.setBalSgst(GenUtil.formatCurrency(openBal.getSgstTaxBal() != null ? openBal.getSgstTaxBal() : "0"));
					respOpenBal.setBalCess(GenUtil.formatCurrency(openBal.getCessTaxBal() != null ? openBal.getCessTaxBal() : "0"));
					respOpenBal.setBalTotal(GenUtil.formatCurrency(openBal.getTotRngBal() != null ? openBal.getTotRngBal() : "0"));

					allCreditDetailsDto.add(respOpenBal);
				}
				if (respTransDtos != null) {
					respTransDtos.forEach(obj -> {
						CreditDetailsRespDto transBal = new CreditDetailsRespDto();
						transBal.setSrNo(srNoCounter[0]++);
						transBal.setItcTransDate(obj.getItcTransDate());
						transBal.setRefNo(obj.getReferenceNo());
						transBal.setTaxPeriod(obj.getRetPeriod());
						transBal.setDesc(obj.getDescription());
						transBal.setTransType(obj.getTransType());
						transBal.setBalIgst(GenUtil.formatCurrency(obj.getIgstTaxBal() != null ? obj.getIgstTaxBal() : "0"));
						transBal.setBalSgst(GenUtil.formatCurrency(obj.getSgstTaxBal() != null ? obj.getSgstTaxBal() : "0"));
						transBal.setBalCgst(GenUtil.formatCurrency(obj.getCgstTaxBal() != null ? obj.getCgstTaxBal() : "0"));
						transBal.setBalCess(GenUtil.formatCurrency(obj.getCessTaxBal() != null ? obj.getCessTaxBal() : "0"));
						transBal.setBalTotal(GenUtil.formatCurrency(obj.getTotRngBal() != null ? obj.getTotRngBal() : "0"));
						transBal.setCrDrIgst(GenUtil.formatCurrency(obj.getIgstTaxAmt() != null ? obj.getIgstTaxAmt() : "0"));
						transBal.setCrDrCgst(GenUtil.formatCurrency(obj.getCgstTaxAmt() != null ? obj.getCgstTaxAmt() : "0"));
						transBal.setCrDrSgst(GenUtil.formatCurrency(obj.getSgstTaxAmt() != null ? obj.getSgstTaxAmt() : "0"));
						transBal.setCrDrCess(GenUtil.formatCurrency(obj.getCessTaxAmt() != null ? obj.getCessTaxAmt() : "0"));
						transBal.setCrDrTotal(GenUtil.formatCurrency(obj.getTotTrAmt() != null ? obj.getTotTrAmt() : "0"));

						allCreditDetailsDto.add(transBal);
					});
				}
				if (closeBal != null) {
					CreditDetailsRespDto respCloseBal = new CreditDetailsRespDto();
					respCloseBal.setSrNo(srNoCounter[0]++);
					respCloseBal.setDesc(closeBal.getDescription());
					respCloseBal.setBalIgst(closeBal.getIgstTaxBal() != null ? GenUtil.formatCurrency(closeBal.getIgstTaxBal()) : "0");
					respCloseBal.setBalCgst(closeBal.getCgstTaxBal() != null ? GenUtil.formatCurrency(closeBal.getCgstTaxBal()) : "0");
					respCloseBal.setBalSgst(closeBal.getSgstTaxBal() != null ? GenUtil.formatCurrency(closeBal.getSgstTaxBal()) : "0");
					respCloseBal.setBalCess(closeBal.getCessTaxBal() != null ? GenUtil.formatCurrency(closeBal.getCessTaxBal()) : "0");
					respCloseBal.setBalTotal(closeBal.getTotRngBal() != null ? GenUtil.formatCurrency(closeBal.getTotRngBal()) : "0");

					allCreditDetailsDto.add(respCloseBal);
				}

			}

			if (!allCreditDetailsDto.isEmpty()) {
				workbook = writeCreditDataToExcel(allCreditDetailsDto, gstin,
						fromDate, toDate, legalName);
			} else {
				workbook = writeNoCreditdataToExcel(gstin, fromDate, toDate,
						legalName);
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured in the findCashReportDownload method during "
							+ "generating the excel sheet, %s ",
					ex.getMessage());
			LOGGER.error(msg);
			throw new AppException(ex.getMessage(), ex);
		}
		return workbook;

	}

	private Workbook writeToExcel1(String gstin, String fromDate, String toDate,
			String legalName) {
		Workbook workbook = null;

		try {

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "CashLedgerReport.xlsx");

			Worksheet sheet = workbook.getWorksheets().get(0);

			// Style without bold formatting
			Style normalStyle = workbook.createStyle();
			Font normalFont = normalStyle.getFont();
			normalFont.setBold(false);

			Cell cell2 = sheet.getCells().get("B3");
			Cell cell3 = sheet.getCells().get("B4");
			Cell cell4 = sheet.getCells().get("B5");
			Cell cell5 = sheet.getCells().get("B6");

			// Apply the style without bold formatting to the cells
			cell2.setStyle(normalStyle);
			cell3.setStyle(normalStyle);
			cell4.setStyle(normalStyle);
			cell5.setStyle(normalStyle);

			cell2.setValue(gstin);
			cell3.setValue(legalName);
			cell4.setValue(fromDate);
			cell5.setValue(toDate);

			for (int i = 0; i < 1; i++) {
				Style style = workbook.createStyle();
				Font font = style.getFont();
				font.setBold(false);
				font.setSize(12);

				int lastRowIndex = sheet.getCells().getMaxDataRow();
				sheet.getCells().deleteRow(lastRowIndex + 1);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}
			}

			return workbook;
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg);
			throw new AppException(e.getMessage(), e);
		}

	}

	private Workbook writeToExcel(
			List<CashLedgerExcelDownloadDto> allDetailsDto, String gstin,
			String fromDate, String toDate, String legalName) {
		Workbook workbook = null;
		int startRow = 9;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin .writeToExcel " + "List Size = "
					+ allDetailsDto.size();
			LOGGER.debug(msg);
		}
		try {
			if (allDetailsDto != null && !allDetailsDto.isEmpty()) {

				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "CashLedgerReport.xlsx");

				Worksheet sheet = workbook.getWorksheets().get(0);

				// Style without bold formatting
				Style normalStyle = workbook.createStyle();
				Font normalFont = normalStyle.getFont();
				normalFont.setBold(false);

				Cell cell2 = sheet.getCells().get("B3");
				Cell cell3 = sheet.getCells().get("B4");
				Cell cell4 = sheet.getCells().get("B5");
				Cell cell5 = sheet.getCells().get("B6");

				// Apply the style without bold formatting to the cells
				cell2.setStyle(normalStyle);
				cell3.setStyle(normalStyle);
				cell4.setStyle(normalStyle);
				cell5.setStyle(normalStyle);

				cell2.setValue(gstin);
				cell3.setValue(legalName);
				cell4.setValue(fromDate);
				cell5.setValue(toDate);

				for (int i = 0; i < 1; i++) {

					Cells reportCell = workbook.getWorksheets().get(i)
							.getCells();

					Style style = workbook.createStyle();
					Font font = style.getFont();
					font.setBold(false);
					font.setSize(12);

					String[] invoiceHeaders = null;

					if (!allDetailsDto.isEmpty()) {
						invoiceHeaders = commonUtility
								.getProp(
										"ledger.Cash.report.excel.download.header")
								.split(",");
						reportCell.importCustomObjects(allDetailsDto,
								invoiceHeaders, isHeaderRequired, startRow,
								startcolumn, allDetailsDto.size(), true,
								"yyyy-mm-dd", false);

					}

					if (LOGGER.isDebugEnabled()) {
						String msg = "CreditLedgerReportServiceImpl.writeToExcel "
								+ "saving workbook";
						LOGGER.debug(msg);
					}
					int lastRowIndex = sheet.getCells().getMaxDataRow();
					sheet.getCells().deleteRow(lastRowIndex + 1);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Workbook has been generated successfully with the"
										+ " error response list in the directory : %s",
								workbook.getAbsolutePath());
					}
				}

			}
			return workbook;
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg);
			throw new AppException(e.getMessage(), e);
		}

	}

	private Workbook writeCreditDataToExcel(
			List<CreditDetailsRespDto> allDetailsDto, String gstin,
			String fromDate, String toDate, String legalName) {
		Workbook workbook = null;
		int startRow = 9;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin .writeToExcel " + "List Size = "
					+ allDetailsDto.size();
			LOGGER.debug(msg);
		}
		try {
			if (allDetailsDto != null && !allDetailsDto.isEmpty()) {

				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", "CreditLedgerExcelReport.xlsx");

				Worksheet sheet = workbook.getWorksheets().get(0);

				// Style without bold formatting
				Style normalStyle = workbook.createStyle();
				Font normalFont = normalStyle.getFont();
				normalFont.setBold(false);

				Cell cell2 = sheet.getCells().get("B3");
				Cell cell3 = sheet.getCells().get("B4");
				Cell cell4 = sheet.getCells().get("B5");
				Cell cell5 = sheet.getCells().get("B6");

				// Apply the style without bold formatting to the cells
				cell2.setStyle(normalStyle);
				cell3.setStyle(normalStyle);
				cell4.setStyle(normalStyle);
				cell5.setStyle(normalStyle);

				cell2.setValue(gstin);
				cell3.setValue(legalName);
				cell4.setValue(fromDate);
				cell5.setValue(toDate);

				for (int i = 0; i < 1; i++) {

					Cells reportCell = workbook.getWorksheets().get(i)
							.getCells();

					Style style = workbook.createStyle();
					Font font = style.getFont();
					font.setBold(false);
					font.setSize(12);

					String[] invoiceHeaders = null;

					if (!allDetailsDto.isEmpty()) {
						invoiceHeaders = commonUtility
								.getProp(
										"ledger.Credit.report.excel.download.header")
								.split(",");
						reportCell.importCustomObjects(allDetailsDto,
								invoiceHeaders, isHeaderRequired, startRow,
								startcolumn, allDetailsDto.size(), true,
								"yyyy-mm-dd", false);

					}

					if (LOGGER.isDebugEnabled()) {
						String msg = "inside getcashledgerdetailsimpl class writeCreditDataToExcel method ";
						LOGGER.debug(msg);
					}
					int lastRowIndex = sheet.getCells().getMaxDataRow();
					sheet.getCells().deleteRow(lastRowIndex + 1);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Workbook has been generated successfully  : %s",
								workbook.getAbsolutePath());
					}
				}

			}
			return workbook;
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg);
			throw new AppException(e.getMessage(), e);
		}

	}

	private Workbook writeNoCreditdataToExcel(String gstin, String fromDate,
			String toDate, String legalName) {
		Workbook workbook = null;

		try {

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "CreditLedgerExcelReport.xlsx");

			Worksheet sheet = workbook.getWorksheets().get(0);

			// Style without bold formatting
			Style normalStyle = workbook.createStyle();
			Font normalFont = normalStyle.getFont();
			normalFont.setBold(false);

			Cell cell2 = sheet.getCells().get("B3");
			Cell cell3 = sheet.getCells().get("B4");
			Cell cell4 = sheet.getCells().get("B5");
			Cell cell5 = sheet.getCells().get("B6");

			// Apply the style without bold formatting to the cells
			cell2.setStyle(normalStyle);
			cell3.setStyle(normalStyle);
			cell4.setStyle(normalStyle);
			cell5.setStyle(normalStyle);

			cell2.setValue(gstin);
			cell3.setValue(legalName);
			cell4.setValue(fromDate);
			cell5.setValue(toDate);

			for (int i = 0; i < 1; i++) {

				Style style = workbook.createStyle();
				Font font = style.getFont();
				font.setBold(false);
				font.setSize(12);

				int lastRowIndex = sheet.getCells().getMaxDataRow();
				sheet.getCells().deleteRow(lastRowIndex + 1);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully : %s",
							workbook.getAbsolutePath());
				}
			}

			return workbook;
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg);
			throw new AppException(e.getMessage(), e);
		}

	}

	private String checkForNull(JsonElement jsonElement) {
		return jsonElement == null || jsonElement.isJsonNull() ? ""
				: jsonElement.getAsString();
	}

	public JasperPrint generatePdfReport(GetCashLedgerDetailsReqDto dto,
			String groupCode) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<>();
		String source = "jasperReports/cash.jrxml";
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Inside getCashLedgerDetailsImpl for Irn: %s", dto);
			LOGGER.debug(msg);
		}

		jasperPrint = generateJasperPrint(dto, parameters, source, jasperPrint,
				groupCode);

		return jasperPrint;

	}

	public JasperPrint generateReversalAndReclaimPdfReport(
			GetCashLedgerDetailsReqDto dto,
			String groupCode) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<>();
		String source = "jasperReports/reversalAndReclaim.jrxml";
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Inside getCashLedgerDetailsImpl for Irn: %s", dto);
			LOGGER.debug(msg);
		}

		jasperPrint = generateReversalandReclaimPdf(dto, parameters, source,
				jasperPrint,
				groupCode);

		return jasperPrint;

	}

	private JasperPrint generateJasperPrint(GetCashLedgerDetailsReqDto dto,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint, String groupCode) {

		String apiResp = null;
		String legalName = "";
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<CashLedgerPDFDownloadDto> allDetailsDto = new ArrayList<>();
		String gstin = dto.getGstin();
		String fromDate = dto.getFromDate();
		String toDate = dto.getToDate();
		fromDate = fromDate.replace("-", "/");
		toDate = toDate.replace("-", "/");
		final int[] srNoCounter = { 1 };
		parameters.put("GSTIN", gstin);
		parameters.put("fromDate", fromDate);
		parameters.put("toDate", toDate);
		APIResponse apiResponse = taxPayerDao.findTaxPayerDetails(gstin,
				groupCode);
		if (apiResponse.isSuccess()) {
			String response = apiResponse.getResponse();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Following is Tax Payer Detailed "
								+ " Response Dto from Gstin  %s :",
						apiResponse);
				LOGGER.debug(msg);
			}

			JsonObject requestObj = JsonParser.parseString(response)
					.getAsJsonObject();
			legalName = checkForNull(requestObj.get("lgnm"));
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("LegalName  %s :", legalName);
				LOGGER.debug(msg);
			}
			legalName = legalName == null ? " " : legalName;

		}
		parameters.put("legalName", legalName);
		try {
			legalName="TestData";
			parameters.put("legalName", legalName);
			apiResp = cashDataAtGstn.fromGstn(dto);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Following is cashDataAtGstn "
						+ " Response from Gstin  %s :", apiResp);
				LOGGER.debug(msg);
			}
			if (apiResp != null) {
				JsonObject req = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				CashLedgersDetailsDto responseDto = gson.fromJson(req,
						CashLedgersDetailsDto.class);
				CashLedgerOpenCloseBalDto closeBal = responseDto.getClosenBal();
				CashLedgerOpenCloseBalDto openBal = responseDto.getOpenBal();
				List<TransactionTypeBalDto> respTransDtos = responseDto
						.getTransTypeBalDto();
				if (openBal != null) {
					CashLedgerPDFDownloadDto respOpenBal = new CashLedgerPDFDownloadDto();
					respOpenBal.setSrNo(srNoCounter[0]++);

					respOpenBal.setDescription(openBal.getDescription() != null
							? openBal.getDescription() : "-");
					// cess bal
					respOpenBal.setCessBalTaxValue(
							openBal.getCessbal().getTaxValue() != null
									? GenUtil.formatCurrency(
											openBal.getCessbal().getTaxValue())
									: "-");
					respOpenBal.setCessBalInterestValue(
							openBal.getCessbal().getInterestValue() != null
									? GenUtil.formatCurrency(openBal
											.getCessbal().getInterestValue())
									: "-");

					// For CESS balance
					respOpenBal.setCessBalPenalty(
							openBal.getCessbal().getPenalty() != null
									? GenUtil.formatCurrency(
											openBal.getCessbal().getPenalty())
									: "-");
					respOpenBal.setCessBalFees(
							openBal.getCessbal().getFees() != null
									? GenUtil.formatCurrency(
											openBal.getCessbal().getFees())
									: "-");
					respOpenBal.setCessBalOther(
							openBal.getCessbal().getOther() != null
									? GenUtil.formatCurrency(
											openBal.getCessbal().getOther())
									: "-");
					respOpenBal.setCessBalTotal(
							openBal.getCessbal().getTotal() != null
									? GenUtil.formatCurrency(
											openBal.getCessbal().getTotal())
									: "-");

					// For IGST balance
					respOpenBal.setIgstBalTaxValue(
							openBal.getIgstbal().getTaxValue() != null
									? GenUtil.formatCurrency(
											openBal.getIgstbal().getTaxValue())
									: "-");
					respOpenBal.setIgstBalInterestValue(
							openBal.getIgstbal().getInterestValue() != null
									? GenUtil.formatCurrency(openBal
											.getIgstbal().getInterestValue())
									: "-");
					respOpenBal.setIgstBalPenalty(
							openBal.getIgstbal().getPenalty() != null
									? GenUtil.formatCurrency(
											openBal.getIgstbal().getPenalty())
									: "-");
					respOpenBal.setIgstBalFees(
							openBal.getIgstbal().getFees() != null
									? GenUtil.formatCurrency(
											openBal.getIgstbal().getFees())
									: "-");
					respOpenBal.setIgstBalOther(
							openBal.getIgstbal().getOther() != null
									? GenUtil.formatCurrency(
											openBal.getIgstbal().getOther())
									: "-");
					respOpenBal.setIgstBalTotal(
							openBal.getIgstbal().getTotal() != null
									? GenUtil.formatCurrency(
											openBal.getIgstbal().getTotal())
									: "-");

					// For CGST balance
					respOpenBal.setCgstBalTaxValue(
							openBal.getCgstbal().getTaxValue() != null
									? GenUtil.formatCurrency(
											openBal.getCgstbal().getTaxValue())
									: "-");
					respOpenBal.setCgstBalInterestValue(
							openBal.getCgstbal().getInterestValue() != null
									? GenUtil.formatCurrency(openBal
											.getCgstbal().getInterestValue())
									: "-");
					respOpenBal.setCgstBalPenalty(
							openBal.getCgstbal().getPenalty() != null
									? GenUtil.formatCurrency(
											openBal.getCgstbal().getPenalty())
									: "-");
					respOpenBal.setCgstBalFees(
							openBal.getCgstbal().getFees() != null
									? GenUtil.formatCurrency(
											openBal.getCgstbal().getFees())
									: "-");
					respOpenBal.setCgstBalOther(
							openBal.getCgstbal().getOther() != null
									? GenUtil.formatCurrency(
											openBal.getCgstbal().getOther())
									: "-");
					respOpenBal.setCgstBalTotal(
							openBal.getCgstbal().getTotal() != null
									? GenUtil.formatCurrency(
											openBal.getCgstbal().getTotal())
									: "-");

					// For SGST balance
					respOpenBal.setSgstBalTaxValue(
							openBal.getSgstbal().getTaxValue() != null
									? GenUtil.formatCurrency(
											openBal.getSgstbal().getTaxValue())
									: "-");
					respOpenBal.setSgstBalInterestValue(
							openBal.getSgstbal().getInterestValue() != null
									? GenUtil.formatCurrency(openBal
											.getSgstbal().getInterestValue())
									: "-");
					respOpenBal.setSgstBalPenalty(
							openBal.getSgstbal().getPenalty() != null
									? GenUtil.formatCurrency(
											openBal.getSgstbal().getPenalty())
									: "-");
					respOpenBal.setSgstBalFees(
							openBal.getSgstbal().getFees() != null
									? GenUtil.formatCurrency(
											openBal.getSgstbal().getFees())
									: "-");
					respOpenBal.setSgstBalOther(
							openBal.getSgstbal().getOther() != null
									? GenUtil.formatCurrency(
											openBal.getSgstbal().getOther())
									: "-");
					respOpenBal.setSgstBalTotal(
							openBal.getSgstbal().getTotal() != null
									? GenUtil.formatCurrency(
											openBal.getSgstbal().getTotal())
									: "-");

					allDetailsDto.add(respOpenBal);
				}
				if (respTransDtos != null) {
					respTransDtos.forEach(obj -> {
						CashLedgerPDFDownloadDto transBal = new CashLedgerPDFDownloadDto();
						// transBal.setGstn(gstin);
						transBal.setSrNo(srNoCounter[0]++);

						transBal.setDptDate(obj.getDptDate() != null
								? obj.getDptDate() : "-");

						transBal.setDptTime(obj.getDptTime() != null
								? obj.getDptTime() : "-");
						transBal.setRptDate(obj.getRptDate() != null
								? obj.getRptDate() : "-");
						transBal.setReferenceNo(obj.getReferenceNo() != null
								? obj.getReferenceNo() : "-");
						transBal.setRetPeriod(obj.getRetPeriod() != null
								? convertTaxPeriod(obj.getRetPeriod()) : "-");
						transBal.setDescription(obj.getDescription() != null
								? obj.getDescription() : "-");
						transBal.setTransType(obj.getTransType() != null
								? (obj.getTransType().equals("Dr") ? "Debit"
										: (obj.getTransType().equals("Cr")
												? "Credit"
												: obj.getTransType()))
								: "-");

						// igst
						transBal.setIgstTaxValue(
								obj.getIgst().getTaxValue() != null
										? GenUtil.formatCurrency(
												obj.getIgst().getTaxValue())
										: "");
						transBal.setIgstInterestValue(
								obj.getIgst().getInterestValue() != null
										? GenUtil.formatCurrency(obj.getIgst()
												.getInterestValue())
										: "");
						transBal.setIgstPenalty(
								obj.getIgst().getPenalty() != null
										? GenUtil.formatCurrency(
												obj.getIgst().getPenalty())
										: "");
						transBal.setIgstFees(
								obj.getIgst().getFees() != null ? 
										GenUtil.formatCurrency(obj.getIgst().getFees())
										: "");
						transBal.setIgstOther(obj.getIgst().getOther() != null
								? GenUtil.formatCurrency(
										obj.getIgst().getOther())
								: "");
						transBal.setIgstTotal(obj.getIgst().getTotal() != null
								? GenUtil.formatCurrency(
										obj.getIgst().getTotal())
								: "");

						// IGST Bal
						transBal.setIgstBalTaxValue(
								obj.getIgstBal().getTaxValue() != null
										? GenUtil.formatCurrency(
												obj.getIgstBal().getTaxValue())
										: "");
						transBal.setIgstBalInterestValue(
								obj.getIgstBal().getInterestValue() != null
										? GenUtil.formatCurrency(obj.getIgstBal()
														.getInterestValue())
										: "");
						transBal.setIgstBalPenalty(
								obj.getIgstBal().getPenalty() != null
										? GenUtil.formatCurrency(
												obj.getIgstBal().getPenalty())
										: "");
						transBal.setIgstBalFees(
								obj.getIgstBal().getFees() != null
										? GenUtil.formatCurrency(
												obj.getIgstBal().getFees())
										: "");
						transBal.setIgstBalOther(
								obj.getIgstBal().getOther() != null
										? GenUtil.formatCurrency(
												obj.getIgstBal().getOther())
										: "");
						transBal.setIgstBalTotal(
								obj.getIgstBal().getTotal() != null
										? GenUtil.formatCurrency(
												obj.getIgstBal().getTotal())
										: "");

						// igstBal Bal

						// cgst

						transBal.setCgstTaxValue(
								obj.getCgst().getTaxValue() != null
										? GenUtil.formatCurrency(
												obj.getCgst().getTaxValue())
										: "");
						transBal.setCgstInterestValue(
								obj.getCgst().getInterestValue() != null
										? GenUtil.formatCurrency(obj.getCgst()
												.getInterestValue())
										: "");
						transBal.setCgstPenalty(
								obj.getCgst().getPenalty() != null
										? GenUtil.formatCurrency(
												obj.getCgst().getPenalty())
										: "");
						transBal.setCgstFees(
								obj.getCgst().getFees() != null ? GenUtil.formatCurrency(obj.getCgst().getFees())
										: "");
						transBal.setCgstOther(obj.getCgst().getOther() != null
								? GenUtil.formatCurrency(
										obj.getCgst().getOther())
								: "");
						transBal.setCgstTotal(obj.getCgst().getTotal() != null
								? GenUtil.formatCurrency(
										obj.getCgst().getTotal())
								: "");

						// cgst
						// cgst bal

						transBal.setCgstBalTaxValue(
								obj.getCgstBal().getTaxValue() != null
										? GenUtil.formatCurrency(
												obj.getCgstBal().getTaxValue())
										: "");
						transBal.setCgstBalInterestValue(
								obj.getCgstBal().getInterestValue() != null
										? GenUtil.formatCurrency(obj.getCgstBal()
														.getInterestValue())
										: "");
						transBal.setCgstBalPenalty(
								obj.getCgstBal().getPenalty() != null
										? GenUtil.formatCurrency(
												obj.getCgstBal().getPenalty())
										: "");
						transBal.setCgstBalFees(
								obj.getCgstBal().getFees() != null
										? GenUtil.formatCurrency(
												obj.getCgstBal().getFees())
										: "");
						transBal.setCgstBalOther(
								obj.getCgstBal().getOther() != null
										? GenUtil.formatCurrency(
												obj.getCgstBal().getOther())
										: "");
						transBal.setCgstBalTotal(
								obj.getCgstBal().getTotal() != null
										? GenUtil.formatCurrency(
												obj.getCgstBal().getTotal())
										: "");
						// cgst bal

						// sgst
						transBal.setSgstTaxValue(
								obj.getSgst().getTaxValue() != null
										? GenUtil.formatCurrency(
												obj.getSgst().getTaxValue())
										: "");
						transBal.setSgstInterestValue(
								obj.getSgst().getInterestValue() != null
										? GenUtil.formatCurrency(obj.getSgst()
												.getInterestValue())
										: "");
						transBal.setSgstPenalty(
								obj.getSgst().getPenalty() != null
										? GenUtil.formatCurrency(
												obj.getSgst().getPenalty())
										: "");
						transBal.setSgstFees(
								obj.getSgst().getFees() != null ? GenUtil
										.formatCurrency(obj.getSgst().getFees())
										: "");
						transBal.setSgstOther(obj.getSgst().getOther() != null
								? GenUtil.formatCurrency(
										obj.getSgst().getOther())
								: "");
						transBal.setSgstTotal(obj.getSgst().getTotal() != null
								? GenUtil.formatCurrency(
										obj.getSgst().getTotal())
								: "");

						// sgst

						// sgst bal
						transBal.setSgstBalTaxValue(
								obj.getSgstBal().getTaxValue() != null
										? GenUtil.formatCurrency(
												obj.getSgstBal().getTaxValue())
										: "");
						transBal.setSgstBalInterestValue(
								obj.getSgstBal().getInterestValue() != null
										? GenUtil
												.formatCurrency(obj.getSgstBal()
														.getInterestValue())
										: "");
						transBal.setSgstBalPenalty(
								obj.getSgstBal().getPenalty() != null
										? GenUtil.formatCurrency(
												obj.getSgstBal().getPenalty())
										: "");
						transBal.setSgstBalFees(
								obj.getSgstBal().getFees() != null
										? GenUtil.formatCurrency(
												obj.getSgstBal().getFees())
										: "");
						transBal.setSgstBalOther(
								obj.getSgstBal().getOther() != null
										? GenUtil.formatCurrency(
												obj.getSgstBal().getOther())
										: "");
						transBal.setSgstBalTotal(
								obj.getSgstBal().getTotal() != null
										? GenUtil.formatCurrency(
												obj.getSgstBal().getTotal())
										: "");
						// sgstbal

						// cess
						transBal.setCessTaxValue(
								obj.getCess().getTaxValue() != null
										? GenUtil.formatCurrency(
												obj.getCess().getTaxValue())
										: "");
						transBal.setCessInterestValue(
								obj.getCess().getInterestValue() != null
										? GenUtil.formatCurrency(obj.getCess()
												.getInterestValue())
										: "");
						transBal.setCessPenalty(
								obj.getCess().getPenalty() != null
										? GenUtil.formatCurrency(
												obj.getCess().getPenalty())
										: "");
						transBal.setCessFees(
								obj.getCess().getFees() != null ? GenUtil
										.formatCurrency(obj.getCess().getFees())
										: "");
						transBal.setCessOther(obj.getCess().getOther() != null
								? GenUtil.formatCurrency(
										obj.getCess().getOther())
								: "");
						transBal.setCessTotal(obj.getCess().getTotal() != null
								? GenUtil.formatCurrency(
										obj.getCess().getTotal())
								: "");

						// cess

						// cess bal
						transBal.setCessBalTaxValue(
								obj.getCessBal().getTaxValue() != null
										? GenUtil.formatCurrency(
												obj.getCessBal().getTaxValue())
										: "");
						transBal.setCessBalInterestValue(
								obj.getCessBal().getInterestValue() != null
										? GenUtil
												.formatCurrency(obj.getCessBal()
														.getInterestValue())
										: "");
						transBal.setCessBalPenalty(
								obj.getCessBal().getPenalty() != null
										? GenUtil.formatCurrency(
												obj.getCessBal().getPenalty())
										: "");
						transBal.setCessBalFees(
								obj.getCessBal().getFees() != null
										? GenUtil.formatCurrency(
												obj.getCessBal().getFees())
										: "");
						transBal.setCessBalOther(
								obj.getCessBal().getOther() != null
										? GenUtil.formatCurrency(
												obj.getCessBal().getOther())
										: "");
						transBal.setCessBalTotal(
								obj.getCessBal().getTotal() != null
										? GenUtil.formatCurrency(
												obj.getCessBal().getTotal())
										: "");

						allDetailsDto.add(transBal);
					});
				}
				if (closeBal != null) {
					CashLedgerPDFDownloadDto respCloseBal = new CashLedgerPDFDownloadDto();
					respCloseBal.setDescription(closeBal.getDescription());
					respCloseBal.setSrNo(srNoCounter[0]++);

					// For CESS balance
					respCloseBal.setCessBalTaxValue(
							closeBal.getCessbal().getTaxValue() != null
									? GenUtil.formatCurrency(
											closeBal.getCessbal().getTaxValue())
									: "-");
					respCloseBal.setCessBalInterestValue(
							closeBal.getCessbal().getInterestValue() != null
									? GenUtil.formatCurrency(closeBal
											.getCessbal().getInterestValue())
									: "-");
					respCloseBal.setCessBalPenalty(
							closeBal.getCessbal().getPenalty() != null
									? GenUtil.formatCurrency(
											closeBal.getCessbal().getPenalty())
									: "-");
					respCloseBal.setCessBalFees(
							closeBal.getCessbal().getFees() != null
									? GenUtil.formatCurrency(
											closeBal.getCessbal().getFees())
									: "-");
					respCloseBal.setCessBalOther(
							closeBal.getCessbal().getOther() != null
									? GenUtil.formatCurrency(
											closeBal.getCessbal().getOther())
									: "-");
					respCloseBal.setCessBalTotal(
							closeBal.getCessbal().getTotal() != null
									? GenUtil.formatCurrency(
											closeBal.getCessbal().getTotal())
									: "-");

					// For IGST balance
					respCloseBal.setIgstBalTaxValue(
							closeBal.getIgstbal().getTaxValue() != null
									? GenUtil.formatCurrency(
											closeBal.getIgstbal().getTaxValue())
									: "-");
					respCloseBal.setIgstBalInterestValue(
							closeBal.getIgstbal().getInterestValue() != null
									? GenUtil.formatCurrency(closeBal
											.getIgstbal().getInterestValue())
									: "-");
					respCloseBal.setIgstBalPenalty(
							closeBal.getIgstbal().getPenalty() != null
									? GenUtil.formatCurrency(
											closeBal.getIgstbal().getPenalty())
									: "-");
					respCloseBal.setIgstBalFees(
							closeBal.getIgstbal().getFees() != null
									? GenUtil.formatCurrency(
											closeBal.getIgstbal().getFees())
									: "-");
					respCloseBal.setIgstBalOther(
							closeBal.getIgstbal().getOther() != null
									? GenUtil.formatCurrency(
											closeBal.getIgstbal().getOther())
									: "-");
					respCloseBal.setIgstBalTotal(
							closeBal.getIgstbal().getTotal() != null
									? GenUtil.formatCurrency(
											closeBal.getIgstbal().getTotal())
									: "-");

					// For CGST balance
					respCloseBal.setCgstBalTaxValue(
							closeBal.getCgstbal().getTaxValue() != null
									? GenUtil.formatCurrency(
											closeBal.getCgstbal().getTaxValue())
									: "-");
					respCloseBal.setCgstBalInterestValue(
							closeBal.getCgstbal().getInterestValue() != null
									? GenUtil.formatCurrency(closeBal
											.getCgstbal().getInterestValue())
									: "-");
					respCloseBal.setCgstBalPenalty(
							closeBal.getCgstbal().getPenalty() != null
									? GenUtil.formatCurrency(
											closeBal.getCgstbal().getPenalty())
									: "-");
					respCloseBal.setCgstBalFees(
							closeBal.getCgstbal().getFees() != null
									? GenUtil.formatCurrency(
											closeBal.getCgstbal().getFees())
									: "-");
					respCloseBal.setCgstBalOther(
							closeBal.getCgstbal().getOther() != null
									? GenUtil.formatCurrency(
											closeBal.getCgstbal().getOther())
									: "-");
					respCloseBal.setCgstBalTotal(
							closeBal.getCgstbal().getTotal() != null
									? GenUtil.formatCurrency(
											closeBal.getCgstbal().getTotal())
									: "-");

					// For SGST balance
					respCloseBal.setSgstBalTaxValue(
							closeBal.getSgstbal().getTaxValue() != null
									? GenUtil.formatCurrency(
											closeBal.getSgstbal().getTaxValue())
									: "-");
					respCloseBal.setSgstBalInterestValue(
							closeBal.getSgstbal().getInterestValue() != null
									? GenUtil.formatCurrency(closeBal
											.getSgstbal().getInterestValue())
									: "-");
					respCloseBal.setSgstBalPenalty(
							closeBal.getSgstbal().getPenalty() != null
									? GenUtil.formatCurrency(
											closeBal.getSgstbal().getPenalty())
									: "-");
					respCloseBal.setSgstBalFees(
							closeBal.getSgstbal().getFees() != null
									? GenUtil.formatCurrency(
											closeBal.getSgstbal().getFees())
									: "-");
					respCloseBal.setSgstBalOther(
							closeBal.getSgstbal().getOther() != null
									? GenUtil.formatCurrency(
											closeBal.getSgstbal().getOther())
									: "-");
					respCloseBal.setSgstBalTotal(
							closeBal.getSgstbal().getTotal() != null
									? GenUtil.formatCurrency(
											closeBal.getSgstbal().getTotal())
									: "-");

					allDetailsDto.add(respCloseBal);
				}

				if (!allDetailsDto.isEmpty()) {
					parameters.put("IgstDetails",
							new JRBeanCollectionDataSource(allDetailsDto));
					parameters.put("CgstDetails",
							new JRBeanCollectionDataSource(allDetailsDto));
					parameters.put("SgstDetails",
							new JRBeanCollectionDataSource(allDetailsDto));
					parameters.put("CessDetails",
							new JRBeanCollectionDataSource(allDetailsDto));

				}

				File file = ResourceUtils.getFile("classpath:" + source);

				JasperReport jasperReport = JasperCompileManager
						.compileReport(file.toString());
				jasperPrint = JasperFillManager.fillReport(jasperReport,
						parameters,
						new JREmptyDataSource());

			}

		} catch (Exception ex) {
			String msg = String
					.format("Exception occured in the jasperPrintPDF method during "
							+ "generating the PDF, %s ", ex.getMessage());
			LOGGER.error(msg);
			throw new AppException(ex.getMessage(), ex);
		}
		return jasperPrint;
	}

	public String convertTaxPeriod(String taxPeriod) {
		SimpleDateFormat originalFormat = new SimpleDateFormat("MMyyyy");
		SimpleDateFormat targetFormat = new SimpleDateFormat("MMM-yy");

		try {
			Date date = originalFormat.parse(taxPeriod);
			return targetFormat.format(date);
		} catch (ParseException e) {

			return "";
		}
	}

	@Override
	public Workbook reversalAndReclaimExcelRpt(String jsonReq,
			String groupCode) {

		Workbook workbook = null;
		String apiResp = null;
		String legalName = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		// List<ReversalAndReclaimDetailsDto> allValDetailsDto = new
		// ArrayList<>();
		List<ItcCrReversalAndReclaimDto> allValDetailsDto = new ArrayList<>();
		JsonElement jsonElement = JsonParser.parseString(jsonReq);
		JsonObject requestObject = jsonElement.getAsJsonObject();

		JsonObject reqObject = requestObject.get("req").getAsJsonObject();
		GetCashLedgerDetailsReqDto dto = gson.fromJson(reqObject,
				GetCashLedgerDetailsReqDto.class);
		List<ReversalAndReclaimDetailsDto> allDetailsDto = new ArrayList<>();
		String gstin = dto.getGstin();
		String fromDate = dto.getFromDate();
		String toDate = dto.getToDate();
		final int[] srNoCounter = { 1 }; // Using an array to make it mutable

		/*APIResponse apiResponse = taxPayerDao.findTaxPayerDetails(gstin,
				groupCode);
		if (apiResponse.isSuccess()) {
			String response = apiResponse.getResponse();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Following is Tax Payer Detailed "
								+ " Response Dto from Gstin  %s :",
						apiResponse);
				LOGGER.debug(msg);
			}

			JsonObject requestObj = JsonParser.parseString(response)
					.getAsJsonObject();
			legalName = checkForNull(requestObj.get("lgnm"));
		}*/

		try {

			apiResp = crReversalAndReclaim
					.getCreditReversalAndReclaimfromGstnTest(dto);
			/* apiResp=null; */

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Following is credit reversal and reclaim ledger Details "
								+ " Response  from Gstin  %s :",
						apiResp);
				LOGGER.debug(msg);
			}
			if (apiResp != null) {
				JsonObject reqObj = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				CreditReverseAndReclaimLedgerDto respDto = gson.fromJson(
						reqObj,
						CreditReverseAndReclaimLedgerDto.class);
				CrReversalLedgerOpenCloseBalDto closeBal = respDto
						.getClosenBal();

				List<CreditRevAndReclaimTransactionDto> respTransDtos = respDto
						.getTransTypeBalDto();

				if (respTransDtos != null) {
					// AtomicInteger srNoCounter = new AtomicInteger(1);
					respTransDtos.forEach(obj -> {
						ItcCrReversalAndReclaimDto transBal = new ItcCrReversalAndReclaimDto();
						transBal.setSrNo(srNoCounter[0]++);
						transBal.setGstin(gstin);
						transBal.setItcTransDate(obj.getTransDate() != null
								? obj.getTransDate() : null);
						transBal.setDesc(obj.getDescription() != null
								? obj.getDescription() : "Closing Balence");
						transBal.setRefNo(obj.getReferenceNo() != null
								? obj.getReferenceNo() : null);
						transBal.setTaxPeriod(obj.getRtnprd() != null
								? obj.getRtnprd() : null);
						LedgerItcReclaimBalanceAmts itc4a5 = obj.getItc4a5();
						if (itc4a5 != null) {
						    transBal.setTable4a5Igst(itc4a5.getIgst() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(itc4a5.getIgst())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						    transBal.setTable4a5Cgst(itc4a5.getCgst() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(itc4a5.getCgst())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						    transBal.setTable4a5Sgst(itc4a5.getSgst() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(itc4a5.getSgst())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						    transBal.setTable4a5Cess(itc4a5.getCess() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(itc4a5.getCess())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						}

						// Retrieve values from itc4b2 and set in transBal
						LedgerItcReclaimBalanceAmts itc4b2 = obj.getItc4b2();
						if (itc4b2 != null) {
						    transBal.setTable4b2Igst(itc4b2.getIgst() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(itc4b2.getIgst())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						    transBal.setTable4b2Cgst(itc4b2.getCgst() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(itc4b2.getCgst())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						    transBal.setTable4b2Sgst(itc4b2.getSgst() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(itc4b2.getSgst())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						    transBal.setTable4b2Cess(itc4b2.getCess() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(itc4b2.getCess())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						}

						// Retrieve values from itc4d1 and set in transBal
						LedgerItcReclaimBalanceAmts itc4d1 = obj.getItc4d1();
						if (itc4d1 != null) {
						    transBal.setTable4d1Igst(itc4d1.getIgst() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(itc4d1.getIgst())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						    transBal.setTable4d1Cgst(itc4d1.getCgst() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(itc4d1.getCgst())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						    transBal.setTable4d1Sgst(itc4d1.getSgst() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(itc4d1.getSgst())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						    transBal.setTable4d1Cess(itc4d1.getCess() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(itc4d1.getCess())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						}

						// Retrieve values from clsbal and set in transBal
						LedgerItcReclaimBalanceAmts clsbal = obj.getClsbal();
						if (clsbal != null) {
						    transBal.setClsBalIgst(clsbal.getIgst() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(clsbal.getIgst())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						    transBal.setClsBalCgst(clsbal.getCgst() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(clsbal.getCgst())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						    transBal.setClsBalSgst(clsbal.getSgst() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(clsbal.getSgst())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						    transBal.setClsBalCess(clsbal.getCess() != null 
						        ? DownloadReportsConstant.CSVCHARACTER.concat(GenUtil.formatCurrency(clsbal.getCess())) 
						        : DownloadReportsConstant.CSVCHARACTER.concat("0"));
						}


						allValDetailsDto.add(transBal);
					});
				}

				if (closeBal != null) {
					ItcCrReversalAndReclaimDto respCloseBal = new ItcCrReversalAndReclaimDto();
					respCloseBal.setSrNo(srNoCounter[0]++);
					respCloseBal.setItcTransDate(closeBal.getTransDate() != null
							? closeBal.getTransDate() : null);
					respCloseBal.setDesc(closeBal.getDescription() != null
							? closeBal.getDescription() : "Closing Balance");
					respCloseBal.setRefNo(closeBal.getReferenceNo() != null
							? closeBal.getReferenceNo() : null);
					respCloseBal.setTaxPeriod(closeBal.getRtnprd() != null
							? closeBal.getRtnprd() : null);
					respCloseBal.setClsBalIgst(GenUtil.formatCurrency(closeBal.getIgst()));
					respCloseBal.setClsBalCgst(GenUtil.formatCurrency(closeBal.getCgst()));
					respCloseBal.setClsBalSgst(GenUtil.formatCurrency(closeBal.getSgst()));
					respCloseBal.setClsBalCess(GenUtil.formatCurrency(closeBal.getCess()));

					allValDetailsDto.add(respCloseBal);

				}
			}

			/*
			 * if (!allValDetailsDto.isEmpty()) { workbook =
			 * writeReverseAndReclaimDataToExcel(allValDetailsDto, gstin,
			 * fromDate, toDate, legalName); }
			 */

			workbook = writeReverseAndReclaimDataToExcel(allValDetailsDto,
					gstin,
					fromDate, toDate, legalName);

		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured in the findCashReportDownload method during "
							+ "generating the excel sheet, %s ",
					ex.getMessage());
			LOGGER.error(msg);
			throw new AppException(ex.getMessage(), ex);
		}
		return workbook;

	}

	private Workbook writeReverseAndReclaimDataToExcel(
			List<ItcCrReversalAndReclaimDto> allDetailsDto, String gstin,
			String fromDate, String toDate, String legalName) {
		Workbook workbook = null;
		int startRow = 9;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin .writeToExcel " + "List Size = "
					+ allDetailsDto.size();
			LOGGER.debug(msg);
		}
		try {
			// if (allDetailsDto != null && !allDetailsDto.isEmpty()) {

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates",
					"ReversalReclaimLedgerDetailScreenDownload.xlsx");

			Worksheet sheet = workbook.getWorksheets().get(0);

			// Style without bold formatting
			Style normalStyle = workbook.createStyle();
			Font normalFont = normalStyle.getFont();
			normalFont.setBold(false);

			Cell cell2 = sheet.getCells().get("B3");
			Cell cell3 = sheet.getCells().get("B4");
			Cell cell4 = sheet.getCells().get("B5");
			Cell cell5 = sheet.getCells().get("B6");

			// Apply the style without bold formatting to the cells
			cell2.setStyle(normalStyle);
			cell3.setStyle(normalStyle);
			cell4.setStyle(normalStyle);
			cell5.setStyle(normalStyle);

			cell2.setValue(gstin);
			cell3.setValue(legalName);
			cell4.setValue(fromDate);
			cell5.setValue(toDate);

			for (int i = 0; i < 1; i++) {

				Cells reportCell = workbook.getWorksheets().get(i)
						.getCells();

				Style style = workbook.createStyle();
				Font font = style.getFont();
				font.setBold(false);
				font.setSize(12);

				String[] invoiceHeaders = null;

				if (allDetailsDto != null && !allDetailsDto.isEmpty()) {
					invoiceHeaders = commonUtility
							.getProp(
									"ledger.creditreclaim.Detailed.report.header")
							.split(",");
					reportCell.importCustomObjects(allDetailsDto,
							invoiceHeaders, isHeaderRequired, startRow,
							startcolumn, allDetailsDto.size(), true,
							"yyyy-mm-dd", false);
					if (LOGGER.isDebugEnabled()) {
						String msg = "inside getcashledgerdetailsimpl class writereverse and reclaim DataToExcel method ";
						LOGGER.debug(msg);
					}
					int lastRowIndex = sheet.getCells().getMaxDataRow();
					sheet.getCells().deleteRow(lastRowIndex + 1);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Workbook has been generated successfully  : %s",
								workbook.getAbsolutePath());
					}
				}

			}

			return workbook;
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg);
			throw new AppException(e.getMessage(), e);
		}

	}

	private JasperPrint generateReversalandReclaimPdf(
			GetCashLedgerDetailsReqDto dto,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint, String groupCode) {
		{

			String apiResp = null;
			String legalName = "";
			Gson gson = GsonUtil.newSAPGsonInstance();
			List<ReversalAndReclaimDetailsDto> allValDetailsDto = new ArrayList<>();
			String gstin = dto.getGstin();
			String fromDate = dto.getFromDate();
			String toDate = dto.getToDate();
			fromDate = fromDate.replace("-", "/");
			toDate = toDate.replace("-", "/");
			final int[] srNoCounter = { 1 };
			parameters.put("gstin", gstin);
			parameters.put("fromDate", fromDate);
			parameters.put("toDate", toDate);
	/*		APIResponse apiResponse = taxPayerDao.findTaxPayerDetails(gstin,
					groupCode);
			if (apiResponse.isSuccess()) {
				String response = apiResponse.getResponse();
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Following is Tax Payer Detailed "
									+ " Response Dto from Gstin  %s :",
							apiResponse);
					LOGGER.debug(msg);
				}

				JsonObject requestObj = JsonParser.parseString(response)
						.getAsJsonObject();
				legalName = checkForNull(requestObj.get("lgnm"));
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("LegalName  %s :", legalName);
					LOGGER.debug(msg);
				}
				legalName = legalName == null ? " " : legalName;

			}
			parameters.put("legalName", legalName);*/
			try {
				 legalName="Ledger";
				parameters.put("legalName", legalName);
				apiResp = crReversalAndReclaim
						.getCreditReversalAndReclaimfromGstnTest(dto);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Following is credit reversal and reclaim ledger Details "
									+ " Response  from Gstin  %s :",
							apiResp);
					LOGGER.debug(msg);
				}
				if (apiResp != null) {
					JsonObject reqObj = JsonParser.parseString(apiResp)
							.getAsJsonObject();
					CreditReverseAndReclaimLedgerDto respDto = gson.fromJson(
							reqObj,
							CreditReverseAndReclaimLedgerDto.class);
					CrReversalLedgerOpenCloseBalDto closeBal = respDto
							.getClosenBal();

					List<CreditRevAndReclaimTransactionDto> respTransDtos = respDto
							.getTransTypeBalDto();
					if (respTransDtos != null) {
						// AtomicInteger srNoCounter = new AtomicInteger(1);
						respTransDtos.forEach(obj -> {
							ReversalAndReclaimDetailsDto transBal = new ReversalAndReclaimDetailsDto();
							transBal.setSrNo(srNoCounter[0]++);
							transBal.setGstin(gstin);
							transBal.setItcTransDate(obj.getTransDate() != null
									? obj.getTransDate() : "-");
							transBal.setDesc(obj.getDescription() != null
									? obj.getDescription() : "Closing Balence");
							transBal.setRefNo(obj.getReferenceNo() != null
									? obj.getReferenceNo() : "-");
							transBal.setTaxPeriod(obj.getRtnprd() != null
									? obj.getRtnprd() : "-");
							LedgerItcReclaimBalanceAmts itc4a5 = obj
									.getItc4a5();
							if (itc4a5 != null) {
								transBal.setTable4a5Igst(
									    itc4a5.getIgst() != null
									        ? GenUtil.formatCurrency(itc4a5.getIgst())
									        : "-"
									);
									transBal.setTable4a5Cgst(
									    itc4a5.getCgst() != null
									        ? GenUtil.formatCurrency(itc4a5.getCgst())
									        : "-"
									);
									transBal.setTable4a5Sgst(
									    itc4a5.getSgst() != null
									        ? GenUtil.formatCurrency(itc4a5.getSgst())
									        : "-"
									);
									transBal.setTable4a5Cess(
									    itc4a5.getCess() != null
									        ? GenUtil.formatCurrency(itc4a5.getCess())
									        : "-"
									);


							}

							// Retrieve values from itc4b2 and set in transBal
							LedgerItcReclaimBalanceAmts itc4b2 = obj
									.getItc4b2();
							if (itc4b2 != null) {
								transBal.setTable4b2Igst(
									    itc4b2.getIgst() != null
									        ? GenUtil.formatCurrency(itc4b2.getIgst())
									        : "-"
									);
									transBal.setTable4b2Cgst(
									    itc4b2.getCgst() != null
									        ? GenUtil.formatCurrency(itc4b2.getCgst())
									        : "-"
									);
									transBal.setTable4b2Sgst(
									    itc4b2.getSgst() != null
									        ? GenUtil.formatCurrency(itc4b2.getSgst())
									        : "-"
									);
									transBal.setTable4b2Cess(
									    itc4b2.getCess() != null
									        ? GenUtil.formatCurrency(itc4b2.getCess())
									        : "-"
									);


							}

							// Retrieve values from itc4d1 and set in transBal
							LedgerItcReclaimBalanceAmts itc4d1 = obj
									.getItc4d1();
							if (itc4d1 != null) {
								transBal.setTable4d1Igst(
									    itc4d1.getIgst() != null
									        ? GenUtil.formatCurrency(itc4d1.getIgst())
									        : "-"
									);
									transBal.setTable4d1Cgst(
									    itc4d1.getCgst() != null
									        ? GenUtil.formatCurrency(itc4d1.getCgst())
									        : "-"
									);
									transBal.setTable4d1Sgst(
									    itc4d1.getSgst() != null
									        ? GenUtil.formatCurrency(itc4d1.getSgst())
									        : "-"
									);
									transBal.setTable4d1Cess(
									    itc4d1.getCess() != null
									        ? GenUtil.formatCurrency(itc4d1.getCess())
									        : "-"
									);


							}

							// Retrieve values from clsbal and set in transBal
							LedgerItcReclaimBalanceAmts clsbal = obj
									.getClsbal();
							if (clsbal != null) {

								transBal.setClsBalIgst(
									    clsbal.getIgst() != null
									        ? GenUtil.formatCurrency(clsbal.getIgst())
									        : "-"
									);
									transBal.setClsBalCgst(
									    clsbal.getCgst() != null
									        ? GenUtil.formatCurrency(clsbal.getCgst())
									        : "-"
									);
									transBal.setClsBalSgst(
									    clsbal.getSgst() != null
									        ? GenUtil.formatCurrency(clsbal.getSgst())
									        : "-"
									);
									transBal.setClsBalCess(
									    clsbal.getCess() != null
									        ? GenUtil.formatCurrency(clsbal.getCess())
									        : "-"
									);


							}

							allValDetailsDto.add(transBal);
						});
					}

					if (closeBal != null) {
						ReversalAndReclaimDetailsDto respCloseBal = new ReversalAndReclaimDetailsDto();
						respCloseBal.setSrNo(srNoCounter[0]++);
						respCloseBal
								.setItcTransDate(closeBal.getTransDate() != null
										? closeBal.getTransDate() : "-");
						respCloseBal.setDesc(closeBal.getDescription() != null
								? closeBal.getDescription()
								: "Closing Balance");
						respCloseBal.setRefNo(closeBal.getReferenceNo() != null
								? closeBal.getReferenceNo() : "-");
						respCloseBal.setTaxPeriod(closeBal.getRtnprd() != null
								? closeBal.getRtnprd() : "-");
						respCloseBal.setClsBalIgst(
							    closeBal.getIgst() != null
							        ? GenUtil.formatCurrency(closeBal.getIgst())
							        : "-"
							);
							respCloseBal.setClsBalCgst(
							    closeBal.getCgst() != null
							        ? GenUtil.formatCurrency(closeBal.getCgst())
							        : "-"
							);
							respCloseBal.setClsBalSgst(
							    closeBal.getSgst() != null
							        ? GenUtil.formatCurrency(closeBal.getSgst())
							        : "-"
							);
							respCloseBal.setClsBalCess(
							    closeBal.getCess() != null
							        ? GenUtil.formatCurrency(closeBal.getCess())
							        : "-"
							);


						allValDetailsDto.add(respCloseBal);

					}

					if (!allValDetailsDto.isEmpty()) {
						parameters.put("IgstDetails",
								new JRBeanCollectionDataSource(
										allValDetailsDto));

					}

					File file = ResourceUtils.getFile("classpath:" + source);

					JasperReport jasperReport = JasperCompileManager
							.compileReport(file.toString());
					jasperPrint = JasperFillManager.fillReport(jasperReport,
							parameters,
							new JREmptyDataSource());

				}

			} catch (Exception ex) {
				String msg = String
						.format("Exception occured in the jasperPrintPDF method during "
								+ "generating the reverse and reclaim ledger PDF, %s ",
								ex.getMessage());
				LOGGER.error(msg);
				throw new AppException(ex.getMessage(), ex);
			}
			return jasperPrint;

		}

	}

	@Override
	public Workbook rcmDetailscnExcelRpt(String jsonReq) {

		Workbook workbook = null;
		String apiResp = null;
		String legalName = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<ItcCrReversalAndReclaimDto> allValDetailsDto = new ArrayList<>();
		JsonElement jsonElement = JsonParser.parseString(jsonReq);
		JsonObject requestObject = jsonElement.getAsJsonObject();

		JsonObject reqObject = requestObject.get("req").getAsJsonObject();
		GetCashLedgerDetailsReqDto dto = gson.fromJson(reqObject,
				GetCashLedgerDetailsReqDto.class);
		List<RcmDetailsRespDto> allDetailsDto = new ArrayList<>();
		String gstin = dto.getGstin();
		String fromDate = dto.getFromDate();
		String toDate = dto.getToDate();
		final int[] srNoCounter = { 1 }; // Using an array to make it mutable

		apiResp = rcmDetails
				.fromGstnDetailedRcmDetailsTest(dto);

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Following is credit reversal and reclaim ledger Details "
								+ " Response  from Gstin  %s :",
						apiResp);
				LOGGER.debug(msg);
			}
			if (apiResp != null) {
				JsonObject reqObj = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				RcmDetailedLedgerDetailsDto respDto = gson.fromJson(
						reqObj, RcmDetailedLedgerDetailsDto.class);
				CrReversalLedgerOpenCloseBalDto closeBal = respDto
						.getClosenBal();

				List<RcmDetailedLedgerTransactionDto> respTransDtos = respDto
						.getTransTypeBalDto();

				if (respTransDtos != null) {
					AtomicInteger serNoCounter = new AtomicInteger(1);
					respTransDtos.forEach(obj -> {
						RcmDetailsRespDto transBal = new RcmDetailsRespDto();
						transBal.setSrNo(srNoCounter[0]++);
						transBal.setGstin(gstin);
						transBal.setDate(obj.getTransDate() != null
								? obj.getTransDate() : null);
						transBal.setDesc(obj.getDescription() != null
								? obj.getDescription() : null);
						transBal.setRefNo(obj.getReferenceNo() != null
								? obj.getReferenceNo() : null);
						transBal.setTaxPeriod(obj.getRtnprd() != null
								? obj.getRtnprd() : null);
						LedgerItcReclaimBalanceAmts table4a2 = obj
								.getItc4a2();
						if (table4a2 != null) {
							transBal.setTable4a2Igst(
								    GenUtil.formatCurrency(table4a2.getIgst() != null ? table4a2.getIgst() : "0"));

								transBal.setTable4a2Cgst(
								    GenUtil.formatCurrency(table4a2.getCgst() != null ? table4a2.getCgst() : "0"));

								transBal.setTable4a2Sgst(
								    GenUtil.formatCurrency(table4a2.getSgst() != null ? table4a2.getSgst() : "0"));

								transBal.setTable4a2Cess(
								    GenUtil.formatCurrency(table4a2.getCess() != null ? table4a2.getCess() : "0"));


						}

						// Retrieve values from itc4b2 and set in transBal
						LedgerItcReclaimBalanceAmts table4a3 = obj
								.getItc4a3();
						if (table4a3 != null) {
						    transBal.setTable4a3Igst(
						            GenUtil.formatCurrency(table4a3.getIgst() != null ? table4a3.getIgst() : "0"));
						    transBal.setTable4a3Cgst(
						            GenUtil.formatCurrency(table4a3.getCgst() != null ? table4a3.getCgst() : "0"));
						    transBal.setTable4a3Sgst(
						            GenUtil.formatCurrency(table4a3.getSgst() != null ? table4a3.getSgst() : "0"));
						    transBal.setTable4a3Cess(
						            GenUtil.formatCurrency(table4a3.getCess() != null ? table4a3.getCess() : "0"));
						}

						// Retrieve values from itc4d1 and set in transBal
						LedgerItcReclaimBalanceAmts table31d = obj.getItc31d();
						if (table31d != null) {
						    transBal.setTable31dIgst(
						            GenUtil.formatCurrency(table31d.getIgst() != null ? table31d.getIgst() : "0"));
						    transBal.setTable31dCgst(
						            GenUtil.formatCurrency(table31d.getCgst() != null ? table31d.getCgst() : "0"));
						    transBal.setTable31dSgst(
						            GenUtil.formatCurrency(table31d.getSgst() != null ? table31d.getSgst() : "0"));
						    transBal.setTable31dCess(
						            GenUtil.formatCurrency(table31d.getCess() != null ? table31d.getCess() : "0"));
						}

						// Retrieve values from clsbal and set in transBal
						LedgerItcReclaimBalanceAmts clsbal = obj.getClsbal();
						if (clsbal != null) {
						    transBal.setClsBalIgst(
						            GenUtil.formatCurrency(clsbal.getIgst() != null ? clsbal.getIgst() : "0"));
						    transBal.setClsBalCgst(
						            GenUtil.formatCurrency(clsbal.getCgst() != null ? clsbal.getCgst() : "0"));
						    transBal.setClsBalSgst(
						            GenUtil.formatCurrency(clsbal.getSgst() != null ? clsbal.getSgst() : "0"));
						    transBal.setClsBalCess(
						            GenUtil.formatCurrency(clsbal.getCess() != null ? clsbal.getCess() : "0"));
						}


						allDetailsDto.add(transBal);
					});
				}
				if (closeBal != null) {
					RcmDetailsRespDto respCloseBal = new RcmDetailsRespDto();
					respCloseBal.setSrNo(srNoCounter[0]++);
					respCloseBal.setGstin(gstin);
					respCloseBal.setDesc("Closing Balance");
					respCloseBal.setClsBalIgst(
					        GenUtil.formatCurrency(closeBal.getIgst() != null ? closeBal.getIgst() : "0"));
					respCloseBal.setClsBalCgst(
					        GenUtil.formatCurrency(closeBal.getCgst() != null ? closeBal.getCgst() : "0"));
					respCloseBal.setClsBalSgst(
					        GenUtil.formatCurrency(closeBal.getSgst() != null ? closeBal.getSgst() : "0"));
					respCloseBal.setClsBalCess(
					        GenUtil.formatCurrency(closeBal.getCess() != null ? closeBal.getCess() : "0"));


					allDetailsDto.add(respCloseBal);

				}

			}

			if (!allDetailsDto.isEmpty())

				workbook = rcmDetailScnDataToExcel(allDetailsDto,
						gstin,
						fromDate, toDate, legalName);

		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured in the findCashReportDownload method during "
							+ "generating the excel sheet, %s ",
					ex.getMessage());
			LOGGER.error(msg);
			throw new AppException(ex.getMessage(), ex);
		}
		return workbook;

	}

	private Workbook rcmDetailScnDataToExcel(
			List<RcmDetailsRespDto> allDetailsDto, String gstin,
			String fromDate, String toDate, String legalName) {
		Workbook workbook = null;
		int startRow = 7;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin .writeToExcel " + "List Size = "
					+ allDetailsDto.size();
			LOGGER.debug(msg);
		}
		try {
			// if (allDetailsDto != null && !allDetailsDto.isEmpty()) {

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates",
					"ITC RCM Ledger.xlsx");

			Worksheet sheet = workbook.getWorksheets().get(0);

			// Style without bold formatting
			Style normalStyle = workbook.createStyle();
			Font normalFont = normalStyle.getFont();
			normalFont.setBold(false);

			Cell cell2 = sheet.getCells().get("B3");
			Cell cell3 = sheet.getCells().get("B4");
			Cell cell4 = sheet.getCells().get("B5");
			Cell cell5 = sheet.getCells().get("B6");

			// Apply the style without bold formatting to the cells
			cell2.setStyle(normalStyle);
			cell3.setStyle(normalStyle);
			cell4.setStyle(normalStyle);
			cell5.setStyle(normalStyle);

			cell2.setValue(fromDate);
			cell3.setValue(toDate);

			for (int i = 0; i < 1; i++) {

				Cells reportCell = workbook.getWorksheets().get(i)
						.getCells();

				Style style = workbook.createStyle();
				Font font = style.getFont();
				font.setBold(false);
				font.setSize(12);

				String[] invoiceHeaders = null;

				if (allDetailsDto != null && !allDetailsDto.isEmpty()) {
					invoiceHeaders = commonUtility
							.getProp(
									"ledger.rcm.bulk.report.header")
							.split(",");
					reportCell.importCustomObjects(allDetailsDto,
							invoiceHeaders, isHeaderRequired, startRow,
							startcolumn, allDetailsDto.size(), true,
							"yyyy-mm-dd", false);
					if (LOGGER.isDebugEnabled()) {
						String msg = "inside getcashledgerdetailsimpl class writereverse and reclaim DataToExcel method ";
						LOGGER.debug(msg);
					}
					int lastRowIndex = sheet.getCells().getMaxDataRow();
					sheet.getCells().deleteRow(lastRowIndex + 1);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Workbook has been generated successfully  : %s",
								workbook.getAbsolutePath());
					}
				}

			}

			return workbook;
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg);
			throw new AppException(e.getMessage(), e);
		}

	}

	// negative ledger detailed screen excel
	@Override
	public Workbook negativeLedgerDetailscnExcelRpt(String jsonReq) {

		Workbook workbook = null;
		String apiResp = null;
		String legalName = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonElement jsonElement = JsonParser.parseString(jsonReq);
		JsonObject requestObject = jsonElement.getAsJsonObject();

		JsonObject reqObject = requestObject.get("req").getAsJsonObject();
		GetCashLedgerDetailsReqDto dto = gson.fromJson(reqObject,
				GetCashLedgerDetailsReqDto.class);
		List<NegativeDetailsRespDto> allDetailsDto = new ArrayList<>();
		String gstin = dto.getGstin();
		String fromDate = dto.getFromDate();
		String toDate = dto.getToDate();
		final int[] srNoCounter = { 1 }; // Using an array to make it mutable

		apiResp = rcmDetails
				.fromGstnDetailedNegativeDetailsTest(dto);

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Following is credit reversal and reclaim ledger Details "
								+ " Response  from Gstin  %s :",
						apiResp);
				LOGGER.debug(msg);
			}
			if (apiResp != null) {
				JsonObject reqObj = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				NegativeDetailedLedgerDetailsDto respDto = gson.fromJson(reqObj,
						NegativeDetailedLedgerDetailsDto.class);
				List<CrReversalLedgerOpenCloseBalDto> closeBalList = respDto
						.getClosenBal();
				List<NegativeDetailedLedgerTransactionDto> respTransDtos = respDto
						.getTransTypeBalDto();

				if (respTransDtos != null) {
					// AtomicInteger srNumCounter = new AtomicInteger(1);
					respTransDtos.forEach(obj -> {
						NegativeDetailsRespDto transBal = new NegativeDetailsRespDto();
						transBal.setSrNo(srNoCounter[0]++);
						transBal.setGstin(gstin);
						transBal.setDate(obj.getTransDate() != null
								? obj.getTransDate() : null);
						transBal.setDesc(obj.getDescription() != null
								? obj.getDescription() : null);
						transBal.setRefNo(obj.getReferenceNo() != null
								? obj.getReferenceNo() : null);
						transBal.setTaxPeriod(obj.getRtnprd() != null
								? obj.getRtnprd() : null);
						transBal.setTranTyp(obj.getTrantyp() != null
								? obj.getTrantyp() : null);

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
									            GenUtil.formatCurrency(amtCrDrOtrThnRevChg.getIgst() != null ? amtCrDrOtrThnRevChg.getIgst() : "0"));
									    transBal.setAmtCrDrOtherCgst(
									            GenUtil.formatCurrency(amtCrDrOtrThnRevChg.getCgst() != null ? amtCrDrOtrThnRevChg.getCgst() : "0"));
									    transBal.setAmtCrDrOtherSgst(
									            GenUtil.formatCurrency(amtCrDrOtrThnRevChg.getSgst() != null ? amtCrDrOtrThnRevChg.getSgst() : "0"));
									    transBal.setAmtCrDrOtherCess(
									            GenUtil.formatCurrency(amtCrDrOtrThnRevChg.getCess() != null ? amtCrDrOtrThnRevChg.getCess() : "0"));
									}

									// For second object (if present)
									if (amtCrDrRevChg != null) {
									    transBal.setAmtCrDrRevChargeIgst(
									            GenUtil.formatCurrency(amtCrDrRevChg.getIgst() != null ? amtCrDrRevChg.getIgst() : "0"));
									    transBal.setAmtCrDrRevChargeCgst(
									            GenUtil.formatCurrency(amtCrDrRevChg.getCgst() != null ? amtCrDrRevChg.getCgst() : "0"));
									    transBal.setAmtCrDrRevChargeSgst(
									            GenUtil.formatCurrency(amtCrDrRevChg.getSgst() != null ? amtCrDrRevChg.getSgst() : "0"));
									    transBal.setAmtCrDrRevChargeCess(
									            GenUtil.formatCurrency(amtCrDrRevChg.getCess() != null ? amtCrDrRevChg.getCess() : "0"));
									}

									List<LedgerItcReclaimBalanceAmts> negliaBalenceList = obj.getNegliabal();
									LedgerItcReclaimBalanceAmts clsBalOtrThnRevChg = negliaBalenceList.get(0);
									LedgerItcReclaimBalanceAmts clsBalRevChg = negliaBalenceList.get(1);
									if (clsBalOtrThnRevChg != null) {
									    transBal.setClsBalOtherIgst(
									            GenUtil.formatCurrency(clsBalOtrThnRevChg.getIgst() != null ? clsBalOtrThnRevChg.getIgst() : "0"));
									    transBal.setClsBalOtherCgst(
									            GenUtil.formatCurrency(clsBalOtrThnRevChg.getCgst() != null ? clsBalOtrThnRevChg.getCgst() : "0"));
									    transBal.setClsBalOtherSgst(
									            GenUtil.formatCurrency(clsBalOtrThnRevChg.getSgst() != null ? clsBalOtrThnRevChg.getSgst() : "0"));
									    transBal.setClsBalOtherCess(
									            GenUtil.formatCurrency(clsBalOtrThnRevChg.getCess() != null ? clsBalOtrThnRevChg.getCess() : "0"));
									}

									if (clsBalRevChg != null) {
									    transBal.setClsBalRevChargeIgst(
									            GenUtil.formatCurrency(clsBalRevChg.getIgst() != null ? clsBalRevChg.getIgst() : "0"));
									    transBal.setClsBalRevChargeCgst(
									            GenUtil.formatCurrency(clsBalRevChg.getCgst() != null ? clsBalRevChg.getCgst() : "0"));
									    transBal.setClsBalRevChargeSgst(
									            GenUtil.formatCurrency(clsBalRevChg.getSgst() != null ? clsBalRevChg.getSgst() : "0"));
									    transBal.setClsBalRevChargeCess(
									            GenUtil.formatCurrency(clsBalRevChg.getCess() != null ? clsBalRevChg.getCess() : "0"));
									}
						}
						allDetailsDto.add(transBal);
					});

				}

				if (closeBalList != null && !closeBalList.isEmpty()) {
					CrReversalLedgerOpenCloseBalDto clsBalOther = closeBalList
							.get(0);
					CrReversalLedgerOpenCloseBalDto clsBalRevChg = closeBalList
							.get(1);

					NegativeDetailsRespDto respCloseBal = new NegativeDetailsRespDto();
					respCloseBal.setSrNo(srNoCounter[0]++);
					respCloseBal.setDesc("Closing Balance");
					respCloseBal.setGstin(gstin);
					respCloseBal.setClsBalOtherIgst(
					        GenUtil.formatCurrency(clsBalOther.getIgst() != null ? clsBalOther.getIgst() : "0"));
					respCloseBal.setClsBalOtherCgst(
					        GenUtil.formatCurrency(clsBalOther.getCgst() != null ? clsBalOther.getCgst() : "0"));
					respCloseBal.setClsBalOtherSgst(
					        GenUtil.formatCurrency(clsBalOther.getSgst() != null ? clsBalOther.getSgst() : "0"));
					respCloseBal.setClsBalOtherCess(
					        GenUtil.formatCurrency(clsBalOther.getCess() != null ? clsBalOther.getCess() : "0"));

					respCloseBal.setClsBalRevChargeIgst(
					        GenUtil.formatCurrency(clsBalRevChg.getIgst() != null ? clsBalRevChg.getIgst() : "0"));
					respCloseBal.setClsBalRevChargeCgst(
					        GenUtil.formatCurrency(clsBalRevChg.getCgst() != null ? clsBalRevChg.getCgst() : "0"));
					respCloseBal.setClsBalRevChargeSgst(
					        GenUtil.formatCurrency(clsBalRevChg.getSgst() != null ? clsBalRevChg.getSgst() : "0"));
					respCloseBal.setClsBalRevChargeCess(
					        GenUtil.formatCurrency(clsBalRevChg.getCess() != null ? clsBalRevChg.getCess() : "0"));


					allDetailsDto.add(respCloseBal);

				}
				

			}

			if (!allDetailsDto.isEmpty())

				workbook = negativeDetailScnDataToExcel(allDetailsDto,
						gstin,
						fromDate, toDate, legalName);

		} 
		catch (Exception ex) {
			String msg = String.format(
					"Exception occured in the findCashReportDownload method during "
							+ "generating the excel sheet, %s ",
					ex.getMessage());
			LOGGER.error(msg);
			throw new AppException(ex.getMessage(), ex);
		}
		return workbook;

	}

	private Workbook negativeDetailScnDataToExcel(
			List<NegativeDetailsRespDto> allDetailsDto, String gstin,
			String fromDate, String toDate, String legalName) {
		Workbook workbook = null;
		int startRow = 8;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin .writeToExcel " + "List Size = "
					+ allDetailsDto.size();
			LOGGER.debug(msg);
		}
		try {
			// if (allDetailsDto != null && !allDetailsDto.isEmpty()) {

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates",
					"Negative Liability Ledger.xlsx");

			Worksheet sheet = workbook.getWorksheets().get(0);

			// Style without bold formatting
			Style normalStyle = workbook.createStyle();
			Font normalFont = normalStyle.getFont();
			normalFont.setBold(false);

			Cell cell2 = sheet.getCells().get("B3");
			Cell cell3 = sheet.getCells().get("B4");
			Cell cell4 = sheet.getCells().get("B5");
			Cell cell5 = sheet.getCells().get("B6");

			// Apply the style without bold formatting to the cells
			cell2.setStyle(normalStyle);
			cell3.setStyle(normalStyle);
			cell4.setStyle(normalStyle);
			cell5.setStyle(normalStyle);

			cell2.setValue(fromDate);
			cell3.setValue(toDate);
			/*
			 * cell4.setValue(fromDate); cell5.setValue(toDate);
			 */

			for (int i = 0; i < 1; i++) {

				Cells reportCell = workbook.getWorksheets().get(i)
						.getCells();

				Style style = workbook.createStyle();
				Font font = style.getFont();
				font.setBold(false);
				font.setSize(12);

				String[] invoiceHeaders = null;

				if (allDetailsDto != null && !allDetailsDto.isEmpty()) {
					invoiceHeaders = commonUtility
							.getProp(
									"ledger.negetiveLiab.bulk.report.header")
							.split(",");
					reportCell.importCustomObjects(allDetailsDto,
							invoiceHeaders, isHeaderRequired, startRow,
							startcolumn, allDetailsDto.size(), true,
							"yyyy-mm-dd", false);
					if (LOGGER.isDebugEnabled()) {
						String msg = "inside getcashledgerdetailsimpl class negativeDetailScnDataToExcel ";
						LOGGER.debug(msg);
					}
					int lastRowIndex = sheet.getCells().getMaxDataRow();
					sheet.getCells().deleteRow(lastRowIndex + 1);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Workbook has been generated successfully  : %s",
								workbook.getAbsolutePath());
					}
				}

			}

			return workbook;
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg);
			throw new AppException(e.getMessage(), e);
		}

	}
	

	 
	 public static String formatCurrency(BigDecimal value) {   
		 if (value != null) {
         String valueStr = value.toString();

         try {
             // Parse the input to a number
             double numValue = Double.parseDouble(valueStr);
             if (numValue != 0) {
                 boolean isNegative = numValue < 0;
                 String numberPart = valueStr.startsWith("-") ? valueStr.substring(1) : valueStr;

                 // Split integer and decimal parts
                 String[] parts = numberPart.split("\\.");
                 String integerPart = parts[0];
                 String decimalPart = parts.length > 1 ? parts[1] : null;

                 // Format the integer part using Indian numbering system
                 int length = integerPart.length();
                 String lastThree = integerPart.substring(length - Math.min(length, 3));
                 String remaining = length > 3 ? integerPart.substring(0, length - 3) : "";

                 if (!remaining.isEmpty()) {
                     remaining = remaining.replaceAll("(\\d)(?=(\\d{2})+$)", "$1,");
                 }

                 String formattedValue = (isNegative ? "-" : "") + remaining + (remaining.isEmpty() ? "" : ",") + lastThree;
                 if (decimalPart != null) {
                     formattedValue += "." + decimalPart;
                 }

                 return formattedValue;
             }
         } catch (NumberFormatException e) {
             // If value cannot be parsed, return as is
             return valueStr;
         }
     }
     return String.valueOf(value);
     
	 }
}
