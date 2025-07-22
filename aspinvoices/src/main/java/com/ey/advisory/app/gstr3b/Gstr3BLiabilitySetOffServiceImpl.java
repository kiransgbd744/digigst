package com.ey.advisory.app.gstr3b;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstnSaveToAspRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BRule86BRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveChangesLiabilitySetOffRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveLiabilitySetOffStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityComputeDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffEntityComputeDetailsEntity;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr3BLiabilitySetOffServiceImpl")
public class Gstr3BLiabilitySetOffServiceImpl
		implements Gstr3BLiabilitySetOffService {

	@Autowired
	@Qualifier("Gstr3BSaveChangesLiabilitySetOffRepository")
	private Gstr3BSaveChangesLiabilitySetOffRepository saveChangesRepo;

	@Autowired
	@Qualifier("Gstr3BSaveLiabilitySetOffStatusRepository")
	private Gstr3BSaveLiabilitySetOffStatusRepository liabilitySetOffStatus;

	@Autowired
	@Qualifier("Gstr3BGetLedgerDetailImpl")
	private Gstr3BGetLedgerDetailImpl ledgerService;

	@Autowired
	@Qualifier("Gstr3bUpdateGstnServiceImpl")
	Gstr3bUpdateGstnService gstr3bUpdateGstnService;

	@Autowired
	GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	@Qualifier("Gstr3BGstnSaveToAspRepository")
	Gstr3BGstnSaveToAspRepository gstr3BGetData;

	@Autowired
	@Qualifier("Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository")
	private Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository ledgerRepo;

	@Autowired
	@Qualifier("Gstr3BRule86BRepository")
	private Gstr3BRule86BRepository rule86BRepo;

	@Autowired
	@Qualifier("Gstr3BSetOffEntityComputeDetailsRepository")
	private Gstr3BSetOffEntityComputeDetailsRepository computeDetailsRepo;

	@Autowired
	@Qualifier("Gstr3BGetNegativeLedgerDetailService")
	private Gstr3BGetNegativeLedgerDetailService negativeLedgerService;

	@Override
	public Gstr3BLiabilitySetOffDto get3BLiabilitySetOff(String gstin,
			String taxPeriod, String authToken, String message) {

		Gstr3BLiabilitySetOffDto dto = null;
		List<PaidThroughItcDto> gstr3bDetails = null;
		LedgerRespDto ledgerdetails = new LedgerRespDto();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			if (authToken.equalsIgnoreCase("Active")) {

				APIResponse apiResponse = gstr3bUpdateGstnService
						.getGstnCall(gstin, taxPeriod);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("After invoking getGSTR3BSummaryFromGSTN"
							+ "Response is : {}", apiResponse);
				}

				if (!apiResponse.isSuccess()) {
					gstnUserRequestRepo.updateGstnResponse(null, 0, gstin,
							taxPeriod, APIConstants.GSTR3B,
							LocalDateTime.now());
					APIError error = apiResponse.getError();
					String msg = error.getErrorDesc();
					message = msg;
					LOGGER.error("GSTR3B GET failed for {}, {}, {}, {} ", gstin,
							taxPeriod, error, error.getErrorDesc());
				}

				// ledger GET call.
				ledgerService.getLedgerdetails(gstin, taxPeriod);

				// Negative Ledger call

				negativeLedgerService.getNegativeLedgerdetails(gstin,
						taxPeriod);
			}

			boolean rule86b = false;

			List<Gstr3BRule86BEntity> rule86EntityList = rule86BRepo
					.findByGstinAndTaxPeriodAndIsActiveTrue(gstin, taxPeriod);

			if (rule86EntityList != null && !rule86EntityList.isEmpty()) {
				rule86b = rule86EntityList.get(0).isRule86B();
			}

			Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity ledgerCashBalData = ledgerRepo
					.findByGstinAndTaxPeriod(taxPeriod, gstin);

			if (ledgerCashBalData != null) {
				ledgerdetails.setCgstIntr(ledgerCashBalData.getInterestCgst());
				ledgerdetails.setCgstLateFee(ledgerCashBalData.getFeeCgst());
				ledgerdetails.setCgstTx(ledgerCashBalData.getTaxCgst());
				ledgerdetails.setCrCess(ledgerCashBalData.getItcBalanceCess());
				ledgerdetails.setCrCgst(ledgerCashBalData.getItcBalanceCgst());
				ledgerdetails.setCrIgst(ledgerCashBalData.getItcBalanceIgst());
				ledgerdetails.setCrSgst(ledgerCashBalData.getItcBalanceSgst());
				ledgerdetails.setCsgstIntr(ledgerCashBalData.getInterestCess());
				ledgerdetails.setCsgstLateFee(ledgerCashBalData.getFeeCess());
				ledgerdetails.setCsgstTx(ledgerCashBalData.getTaxCess());
				ledgerdetails.setIgstIntr(ledgerCashBalData.getInterestIgst());
				ledgerdetails.setIgstLateFee(ledgerCashBalData.getFeeIgst());
				ledgerdetails.setIgstTx(ledgerCashBalData.getTaxIgst());
				ledgerdetails.setSgstIntr(ledgerCashBalData.getInterestSgst());
				ledgerdetails.setSgstLateFee(ledgerCashBalData.getFeeSgst());
				ledgerdetails.setSgstTx(ledgerCashBalData.getTaxSgst());
			}
			// NegativeLedger

			BigDecimal totalIgst = BigDecimal.ZERO;
			BigDecimal totalCgst = BigDecimal.ZERO;
			BigDecimal totalSgst = BigDecimal.ZERO;
			BigDecimal totalCess = BigDecimal.ZERO;

			GstnUserRequestEntity jsonEntity = gstnUserRequestRepo
					.findByGstinAndTaxPeriodAndReturnTypeAndRequestType(gstin,
							taxPeriod, "NegativeLedger", "GET");
			if (jsonEntity != null) {

				Clob negativeLedgerJsonClob = jsonEntity
						.getGetResponsePayload();

				String negativeLedgerJsonString = convertClobToString(
						negativeLedgerJsonClob);

				JsonObject negativeLedgerJson = JsonParser
						.parseString(negativeLedgerJsonString)
						.getAsJsonObject();

				JsonArray negliabdtlsArray = negativeLedgerJson
						.getAsJsonArray("closebal");

						// Sum the values
						for (JsonElement negliabalElement : negliabdtlsArray) {
							JsonObject negliabalObject = negliabalElement
									.getAsJsonObject();
							totalIgst = totalIgst.add(negliabalObject
									.get("igst").getAsBigDecimal());
							totalCgst = totalCgst.add(negliabalObject
									.get("cgst").getAsBigDecimal());
							totalSgst = totalSgst.add(negliabalObject
									.get("sgst").getAsBigDecimal());
							totalCess = totalCess.add(negliabalObject
									.get("cess").getAsBigDecimal());
						}

					}

			// getting column 3-4-5-6 + 2i and 8a data from db
			Gstr3BSaveChangesLiabilitySetOffEntity dbResp = saveChangesRepo
					.findByGstinAndTaxPeriodAndIsActive(taxPeriod, gstin);

			// 2(i) & 8A
			BigDecimal cess8A = dbResp != null ? (dbResp.getCs_adjNegative8A() != null
			        ? dbResp.getCs_adjNegative8A()
			        : BigDecimal.ZERO) : BigDecimal.ZERO;

			BigDecimal igst8A = dbResp != null ? (dbResp.getI_adjNegative8A() != null
			        ? dbResp.getI_adjNegative8A()
			        : BigDecimal.ZERO) : BigDecimal.ZERO;

			BigDecimal cgst8A = dbResp != null ? (dbResp.getC_adjNegative8A() != null
			        ? dbResp.getC_adjNegative8A()
			        : BigDecimal.ZERO) : BigDecimal.ZERO;

			BigDecimal sgst8A = dbResp != null ? (dbResp.getS_adjNegative8A() != null
			        ? dbResp.getS_adjNegative8A()
			        : BigDecimal.ZERO) : BigDecimal.ZERO;

			BigDecimal cess2i = dbResp != null ? (dbResp.getCs_adjNegative2i() != null
			        ? dbResp.getCs_adjNegative2i()
			        : BigDecimal.ZERO) : BigDecimal.ZERO;

			BigDecimal igst2i = dbResp != null ? (dbResp.getI_adjNegative2i() != null
			        ? dbResp.getI_adjNegative2i()
			        : BigDecimal.ZERO) : BigDecimal.ZERO;

			BigDecimal cgst2i = dbResp != null ? (dbResp.getC_adjNegative2i() != null
			        ? dbResp.getC_adjNegative2i()
			        : BigDecimal.ZERO) : BigDecimal.ZERO;

			BigDecimal sgst2i = dbResp != null ? (dbResp.getS_adjNegative2i() != null
			        ? dbResp.getS_adjNegative2i()
			        : BigDecimal.ZERO) : BigDecimal.ZERO;
			
			
			//not getting anything from gstn so making these two fields as user edited 

		/*	GstnUserRequestEntity gstr3BGetjsonEntity = gstnUserRequestRepo
					.findByGstinAndTaxPeriodAndReturnTypeAndRequestType(gstin,
							taxPeriod, "GSTR3B", "GET");
			if (gstr3BGetjsonEntity != null) {

				Clob gstr3BGetjsonpayload = gstr3BGetjsonEntity
						.getGetResponsePayload();

				String gstr3BGetjsonString = convertClobToString(
						gstr3BGetjsonpayload);

				JsonObject suppDetails = JsonParser
						.parseString(gstr3BGetjsonString).getAsJsonObject();

				JsonObject txPmt = suppDetails.getAsJsonObject("tx_pmt");

				if (txPmt != null) {
					JsonArray adjnegliabArray = txPmt
							.getAsJsonArray("adjnegliab");
					if (adjnegliabArray != null) {
						Type adjnegliaListType = new TypeToken<List<Gstr3BPaidNetTaxPayDto>>() {
						}.getType();
						List<Gstr3BPaidNetTaxPayDto> adjnegliaListList = gson
								.fromJson(adjnegliabArray, adjnegliaListType);
						for (Gstr3BPaidNetTaxPayDto adjnegliaListDto : adjnegliaListList) {

							if (adjnegliaListDto.getTransType() == 30003) {

								cess8A = adjnegliaListDto.getCess().getTx();
								igst8A = adjnegliaListDto.getIgst().getTx();
								cgst8A = adjnegliaListDto.getSgst().getTx();
								sgst8A = adjnegliaListDto.getCgst().getTx();
							}
							if (adjnegliaListDto.getTransType() == 30002) {

								cess2i = adjnegliaListDto.getCess().getTx();
								igst2i = adjnegliaListDto.getIgst().getTx();
								cgst2i = adjnegliaListDto.getSgst().getTx();
								sgst2i = adjnegliaListDto.getCgst().getTx();
							}

						}
					}
				}
			}*/
			List<Gstr3bGstnSaveToAspEntity> get3BData = gstr3BGetData
					.findOtherLiabilityAndReverseChargeTaxAndInterestAndLateFee(
							taxPeriod, gstin);

			Gstr3bGstnSaveToAspEntity currentMonthItc = gstr3BGetData
					.findByGstinAndTaxPeriodAndSectionName(gstin, taxPeriod);

			Itc4cDto itc4cData = currentMonthItc != null
					? getItc4cData(currentMonthItc)
					: null;

			ItcPaidInnerDto itcPaidDto = writeToItcPaidInnerDto(get3BData,
					currentMonthItc);

			

			gstr3bDetails = converToPaidThroughItcDto(itcPaidDto, dbResp,
					igst2i, cgst2i, sgst2i, cess2i, igst8A, cgst8A, sgst8A,
					cess8A);

			// 86B rule
			BigDecimal percentage2B = new BigDecimal("0.99");
			BigDecimal percentage2A = new BigDecimal("0.01");
			for (PaidThroughItcDto pdDto : gstr3bDetails) {

				if (rule86b == false) {
					pdDto.setOtrci2A(BigDecimal.ZERO);
					pdDto.setOtrci2B(BigDecimal.ZERO);
				} else {
					//using 2i
					BigDecimal otrci = pdDto.getNetOthRecTaxPayable2i();

					pdDto.setOtrci2A(GenUtil
							.roundOffTheAmount(otrci.multiply(percentage2A)));
					pdDto.setOtrci2B(GenUtil
							.roundOffTheAmount(otrci.multiply(percentage2B)));
				}
			}

			List<Gstr3BSetOffEntityComputeDetailsEntity> data = computeDetailsRepo
					.findByGstinAndTaxPeriodAndIsDelete(gstin, taxPeriod,
							false);

			Gstr3BSetOffEntityComputeDetailsEntity crrMonCredit = new Gstr3BSetOffEntityComputeDetailsEntity();
			Gstr3BSetOffEntityComputeDetailsEntity crrMonCash = new Gstr3BSetOffEntityComputeDetailsEntity();
			Gstr3BSetOffEntityComputeDetailsEntity clsBalCredit = new Gstr3BSetOffEntityComputeDetailsEntity();
			Gstr3BSetOffEntityComputeDetailsEntity clsBalCash = new Gstr3BSetOffEntityComputeDetailsEntity();

			if (data != null && !data.isEmpty()) {
				for (Gstr3BSetOffEntityComputeDetailsEntity ledgerClsData : data) {

					if (ledgerClsData.getSection()
							.equalsIgnoreCase("currMonthUtil_Credit")) {

						crrMonCredit.setCess(ledgerClsData.getCess());
						crrMonCredit.setCgst(ledgerClsData.getCgst());
						crrMonCredit.setIgst(ledgerClsData.getIgst());
						crrMonCredit.setSgst(ledgerClsData.getSgst());

					} else if (ledgerClsData.getSection()
							.equalsIgnoreCase("currMonthUtil_Cash")) {

						crrMonCash.setCess(ledgerClsData.getCess());
						crrMonCash.setCgst(ledgerClsData.getCgst());
						crrMonCash.setIgst(ledgerClsData.getIgst());
						crrMonCash.setSgst(ledgerClsData.getSgst());

					} /*else if (ledgerClsData.getSection()
							.equalsIgnoreCase("currMonthUtil_NegativeLedger")) {

						crrMonCash.setCess(ledgerClsData.getCess());
						crrMonCash.setCgst(ledgerClsData.getCgst());
						crrMonCash.setIgst(ledgerClsData.getIgst());
						crrMonCash.setSgst(ledgerClsData.getSgst());

					} */else if (ledgerClsData.getSection()
							.equalsIgnoreCase("clsBal_Credit")) {

						clsBalCredit.setCess(ledgerClsData.getCess());
						clsBalCredit.setCgst(ledgerClsData.getCgst());
						clsBalCredit.setIgst(ledgerClsData.getIgst());
						clsBalCredit.setSgst(ledgerClsData.getSgst());

					} else if (ledgerClsData.getSection()
							.equalsIgnoreCase("clsBal_Cash")) {

						clsBalCash.setCess(ledgerClsData.getCess());
						clsBalCash.setCgst(ledgerClsData.getCgst());
						clsBalCash.setIgst(ledgerClsData.getIgst());
						clsBalCash.setSgst(ledgerClsData.getSgst());

					} /*else if (ledgerClsData.getSection()
							.equalsIgnoreCase("clsBal_NegativeLedger")) {

						clsBalCredit.setCess(ledgerClsData.getCess());
						clsBalCredit.setCgst(ledgerClsData.getCgst());
						clsBalCredit.setIgst(ledgerClsData.getIgst());
						clsBalCredit.setSgst(ledgerClsData.getSgst());

					}*/

				}
			}
			List<LedgerDetailsDto> ledgerDetails = converToLedgerDetailsDto(
					ledgerdetails, itc4cData, crrMonCredit, crrMonCash,
					clsBalCredit, clsBalCash, totalIgst, totalCgst, totalSgst,
					totalCess, igst2i, cgst2i, sgst2i, cess2i, igst8A, cgst8A, sgst8A,
					cess8A);

			Gstr3BSaveLiabilitySetOffStatusEntity liabilitySetoffStatus = liabilitySetOffStatus
					.findFirstByGstinAndTaxPeriodOrderByIdDesc(gstin,
							taxPeriod);
			String liabilityStatus = null;
			LocalDateTime statusUptatedOn = null;
			String updatedDateTime = null;
			if (liabilitySetoffStatus != null) {
				liabilityStatus = liabilitySetoffStatus.getStatus();
				statusUptatedOn = liabilitySetoffStatus.getUpdatedOn() != null
						? EYDateUtil.toISTDateTimeFromUTC(
								liabilitySetoffStatus.getUpdatedOn())
						: null;
				if (statusUptatedOn != null) {
					String dateTime = statusUptatedOn.toString();
					String date = dateTime.substring(0, 10);
					String time = dateTime.substring(11, 19);
					updatedDateTime = (date + " " + time);
				}
			} else {
				liabilityStatus = "Not Initiated";
			}

			dto = new Gstr3BLiabilitySetOffDto(liabilityStatus, updatedDateTime,
					ledgerDetails, gstr3bDetails, message, rule86b);

		} catch (Exception ex) {
			LOGGER.error("Exception while populating the GSTN reponse to "
					+ "get3BLiabilitySetOff Dto", ex);
			throw new AppException(ex);
		}
		return dto;

	}

	private List<LedgerDetailsDto> converToLedgerDetailsDto(
			LedgerRespDto ledgerdetails, Itc4cDto itc4cData,
			Gstr3BSetOffEntityComputeDetailsEntity crrMonCredit,
			Gstr3BSetOffEntityComputeDetailsEntity crrMonCash,
			Gstr3BSetOffEntityComputeDetailsEntity clsBalCredit,
			Gstr3BSetOffEntityComputeDetailsEntity clsBalCash,
			BigDecimal totalIgst, BigDecimal totalCgst, BigDecimal totalSgst,
			BigDecimal totalCess, BigDecimal igst2i,
			BigDecimal cgst2i, BigDecimal sgst2i, BigDecimal cess2i,
			BigDecimal igst8A, BigDecimal cgst8A, BigDecimal sgst8A,
			BigDecimal cess8A) {

		List<LedgerDetailsDto> ledgerDtoList = new ArrayList<>();
		BigDecimal zeroVal = BigDecimal.ZERO;
		LedgerDetailsDto txDto = new LedgerDetailsDto();
		LedgerDetailsDto intrDto = new LedgerDetailsDto();
		LedgerDetailsDto feeDto = new LedgerDetailsDto();
		LedgerDetailsDto crrtMonthDto = new LedgerDetailsDto();
		LedgerDetailsDto clsBalDto = new LedgerDetailsDto();

		// checking as per new logic if 4C is positive then only adding it

		BigDecimal itc4Cgst = itc4cData != null && itc4cData.getCgst() != null
				&& itc4cData.getCgst().compareTo(BigDecimal.ZERO) > 0
						? itc4cData.getCgst()
						: zeroVal;
		BigDecimal itc4Sgst = itc4cData != null && itc4cData.getSgst() != null
				&& itc4cData.getSgst().compareTo(BigDecimal.ZERO) > 0
						? itc4cData.getSgst()
						: zeroVal;
		BigDecimal itc4Igst = itc4cData != null && itc4cData.getIgst() != null
				&& itc4cData.getIgst().compareTo(BigDecimal.ZERO) > 0
						? itc4cData.getIgst()
						: zeroVal;
		BigDecimal itc4Cess = itc4cData != null && itc4cData.getCess() != null
				&& itc4cData.getCess().compareTo(BigDecimal.ZERO) > 0
						? itc4cData.getCess()
						: zeroVal;

		txDto.setDesc("tx");
		txDto.setC((ledgerdetails != null && ledgerdetails.getCgstTx() != null)
				? ledgerdetails.getCgstTx()
				: zeroVal);
		txDto.setI((ledgerdetails != null && ledgerdetails.getIgstTx() != null)
				? ledgerdetails.getIgstTx()
				: zeroVal);
		txDto.setS((ledgerdetails != null && ledgerdetails.getSgstTx() != null)
				? ledgerdetails.getSgstTx()
				: zeroVal);
		txDto.setCs(
				(ledgerdetails != null && ledgerdetails.getCsgstTx() != null)
						? ledgerdetails.getCsgstTx()
						: zeroVal);

		// checking as per new logic

		txDto.setCrc(GenUtil.roundOffTheAmount(
				(ledgerdetails != null && ledgerdetails.getCrCgst() != null)
						? ledgerdetails.getCrCgst().add(itc4Cgst)
						: itc4Cgst));
		txDto.setCrs(GenUtil.roundOffTheAmount(
				(ledgerdetails != null && ledgerdetails.getCrSgst() != null)
						? ledgerdetails.getCrSgst().add(itc4Sgst)
						: itc4Sgst));
		txDto.setCri(GenUtil.roundOffTheAmount(
				(ledgerdetails != null && ledgerdetails.getCrIgst() != null)
						? ledgerdetails.getCrIgst().add(itc4Igst)
						: itc4Igst));
		txDto.setCrcs(GenUtil.roundOffTheAmount(
				(ledgerdetails != null && ledgerdetails.getCrCess() != null)
						? ledgerdetails.getCrCess().add(itc4Cess)
						: itc4Cess));

		txDto.setNlbCess(totalCess);
		txDto.setNlbCgst(totalCgst);
		txDto.setNlbIgst(totalIgst);
		txDto.setNlbSgst(totalSgst);

		ledgerDtoList.add(txDto);

		intrDto.setDesc("intr");
		intrDto.setC(
				(ledgerdetails != null && ledgerdetails.getCgstIntr() != null)
						? ledgerdetails.getCgstIntr()
						: zeroVal);
		intrDto.setI(
				(ledgerdetails != null && ledgerdetails.getIgstIntr() != null)
						? ledgerdetails.getIgstIntr()
						: zeroVal);
		intrDto.setS(
				(ledgerdetails != null && ledgerdetails.getSgstIntr() != null)
						? ledgerdetails.getSgstIntr()
						: zeroVal);
		intrDto.setCs(
				(ledgerdetails != null && ledgerdetails.getCsgstIntr() != null)
						? ledgerdetails.getCsgstIntr()
						: zeroVal);
		ledgerDtoList.add(intrDto);

		feeDto.setDesc("fee");
		feeDto.setC((ledgerdetails != null
				&& ledgerdetails.getCgstLateFee() != null)
						? ledgerdetails.getCgstLateFee()
						: zeroVal);
		feeDto.setI((ledgerdetails != null
				&& ledgerdetails.getIgstLateFee() != null)
						? ledgerdetails.getIgstLateFee()
						: zeroVal);
		feeDto.setS((ledgerdetails != null
				&& ledgerdetails.getSgstLateFee() != null)
						? ledgerdetails.getSgstLateFee()
						: zeroVal);
		feeDto.setCs((ledgerdetails != null
				&& ledgerdetails.getCsgstLateFee() != null)
						? ledgerdetails.getCsgstLateFee()
						: zeroVal);
		ledgerDtoList.add(feeDto);

		crrtMonthDto.setDesc("currMonthUtil");
		crrtMonthDto.setC(crrMonCash != null ? crrMonCash.getCgst() : zeroVal);
		crrtMonthDto.setI(crrMonCash != null ? crrMonCash.getIgst() : zeroVal);
		crrtMonthDto.setS(crrMonCash != null ? crrMonCash.getSgst() : zeroVal);
		crrtMonthDto.setCs(crrMonCash != null ? crrMonCash.getCess() : zeroVal);
		crrtMonthDto.setCrc(
				crrMonCredit != null ? crrMonCredit.getCgst() : zeroVal);
		crrtMonthDto.setCrs(
				crrMonCredit != null ? crrMonCredit.getSgst() : zeroVal);
		crrtMonthDto.setCri(
				crrMonCredit != null ? crrMonCredit.getIgst() : zeroVal);
		crrtMonthDto.setCrcs(
				crrMonCredit != null ? crrMonCredit.getCess() : zeroVal);
		//negativeLiab
		crrtMonthDto.setNlbCess(cess2i.add(cess8A));
		crrtMonthDto.setNlbCgst(cgst2i.add(cgst8A));
		crrtMonthDto.setNlbIgst(igst2i.add(igst8A));
		crrtMonthDto.setNlbSgst(sgst2i.add(sgst8A));
		ledgerDtoList.add(crrtMonthDto);

		clsBalDto.setDesc("clsBal");
		clsBalDto.setC(clsBalCash != null ? clsBalCash.getCgst() : zeroVal);
		clsBalDto.setI(clsBalCash != null ? clsBalCash.getIgst() : zeroVal);
		clsBalDto.setS(clsBalCash != null ? clsBalCash.getSgst() : zeroVal);
		clsBalDto.setCs(clsBalCash != null ? clsBalCash.getCess() : zeroVal);
		clsBalDto.setCrc(
				clsBalCredit != null ? clsBalCredit.getCgst() : zeroVal);
		clsBalDto.setCrs(
				clsBalCredit != null ? clsBalCredit.getSgst() : zeroVal);
		clsBalDto.setCri(
				clsBalCredit != null ? clsBalCredit.getIgst() : zeroVal);
		clsBalDto.setCrcs(
				clsBalCredit != null ? clsBalCredit.getCess() : zeroVal);
		
		//negativeLiab
		clsBalDto.setNlbCess(totalCess.subtract(cess2i.add(cess8A)));
		clsBalDto.setNlbCgst(totalCgst.subtract(cgst2i.add(cgst8A)));
		clsBalDto.setNlbIgst(totalIgst.subtract(igst2i.add(igst8A)));
		clsBalDto.setNlbSgst(totalSgst.subtract(sgst2i.add(sgst8A)));
				
		ledgerDtoList.add(clsBalDto);

		return ledgerDtoList;
	}

	private Itc4cDto getItc4cData(Gstr3bGstnSaveToAspEntity currMonthItc) {

		Itc4cDto c4Dto = new Itc4cDto();

		c4Dto.setIgst(currMonthItc.getIgst());
		c4Dto.setCgst(currMonthItc.getCgst());
		c4Dto.setSgst(currMonthItc.getSgst());
		c4Dto.setCess(currMonthItc.getCess());

		return c4Dto;
	}

	private ItcPaidInnerDto writeToItcPaidInnerDto(
			List<Gstr3bGstnSaveToAspEntity> get3BData,
			Gstr3bGstnSaveToAspEntity itc4cData) {

		ItcPaidInnerDto innerDto = new ItcPaidInnerDto();

		BigDecimal zeroVal = BigDecimal.ZERO;

		BigDecimal igstB = BigDecimal.ZERO;
		BigDecimal cgstB = BigDecimal.ZERO;
		BigDecimal sgstB = BigDecimal.ZERO;
		BigDecimal cessB = BigDecimal.ZERO;

		BigDecimal rcigst = BigDecimal.ZERO;
		BigDecimal rccgst = BigDecimal.ZERO;
		BigDecimal rcsgst = BigDecimal.ZERO;
		BigDecimal rccess = BigDecimal.ZERO;

		// as per current month ITC -4C

		BigDecimal itc4Cgst = itc4cData != null && itc4cData.getCgst() != null
				&& itc4cData.getCgst().compareTo(BigDecimal.ZERO) < 0
						? itc4cData.getCgst().abs()
						: zeroVal;
		BigDecimal itc4Sgst = itc4cData != null && itc4cData.getSgst() != null
				&& itc4cData.getSgst().compareTo(BigDecimal.ZERO) < 0
						? itc4cData.getSgst().abs()
						: zeroVal;
		BigDecimal itc4Igst = itc4cData != null && itc4cData.getIgst() != null
				&& itc4cData.getIgst().compareTo(BigDecimal.ZERO) < 0
						? itc4cData.getIgst().abs()
						: zeroVal;
		BigDecimal itc4Cess = itc4cData != null && itc4cData.getCess() != null
				&& itc4cData.getCess().compareTo(BigDecimal.ZERO) < 0
						? itc4cData.getCess().abs()
						: zeroVal;

		// 3.1(b)
		for (Gstr3bGstnSaveToAspEntity gstnData : get3BData) {

			if (gstnData != null
					&& gstnData.getSectionName().equalsIgnoreCase("3.1(b)")) {

				igstB = gstnData.getIgst() != null ? gstnData.getIgst()
						: BigDecimal.ZERO;
				cgstB = gstnData.getCgst() != null ? gstnData.getCgst()
						: BigDecimal.ZERO;
				sgstB = gstnData.getSgst() != null ? gstnData.getSgst()
						: BigDecimal.ZERO;
				cessB = gstnData.getCess() != null ? gstnData.getCess()
						: BigDecimal.ZERO;
			}

			if (gstnData != null && gstnData.getSectionName()
					.equalsIgnoreCase(Gstr3BConstants.Table3_1_1_A)) {

				rcigst = gstnData.getIgst() != null ? gstnData.getIgst()
						: BigDecimal.ZERO;
				rccgst = gstnData.getCgst() != null ? gstnData.getCgst()
						: BigDecimal.ZERO;
				rcsgst = gstnData.getSgst() != null ? gstnData.getSgst()
						: BigDecimal.ZERO;
				rccess = gstnData.getCess() != null ? gstnData.getCess()
						: BigDecimal.ZERO;
			}
		}

		try {
			for (Gstr3bGstnSaveToAspEntity gstnData : get3BData) {

				// 3.1(a) + 3.1(b)- column 2

				if (gstnData != null && gstnData.getSectionName()
						.equalsIgnoreCase("3.1(a)")) {

					innerDto.setOthrcIgst(gstnData.getIgst() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getIgst().add(igstB).add(itc4Igst))
							: BigDecimal.ZERO);
					innerDto.setOthrcCgst(gstnData.getCgst() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getCgst().add(cgstB).add(itc4Cgst))
							: BigDecimal.ZERO);
					innerDto.setOthrcSgst(gstnData.getSgst() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getSgst().add(sgstB).add(itc4Sgst))
							: BigDecimal.ZERO);
					innerDto.setOthrcCess(gstnData.getCess() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getCess().add(cessB).add(itc4Cess))
							: BigDecimal.ZERO);
				}

				// 3.1(d) + 3.1.1(a) column 8-9
				if (gstnData != null && gstnData.getSectionName()
						.equalsIgnoreCase("3.1(d)")) {
					innerDto.setRctpIgst(gstnData.getIgst() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getIgst().add(rcigst))
							: BigDecimal.ZERO);
					innerDto.setRctpCgst(gstnData.getCgst() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getCgst().add(rccgst))
							: BigDecimal.ZERO);
					innerDto.setRctpSgst(gstnData.getSgst() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getSgst().add(rcsgst))
							: BigDecimal.ZERO);
					innerDto.setRctpCess(gstnData.getCess() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getCess().add(rccess))
							: BigDecimal.ZERO);
				}

				// column 10-11
				if (gstnData != null && gstnData.getSectionName()
						.equalsIgnoreCase("5.1(a)")) {
					innerDto.setIpIgst(gstnData.getIgst() != null
							? GenUtil.roundOffTheAmount(gstnData.getIgst())
							: BigDecimal.ZERO);
					innerDto.setIpCgst(gstnData.getCgst() != null
							? GenUtil.roundOffTheAmount(gstnData.getCgst())
							: BigDecimal.ZERO);
					innerDto.setIpSgst(gstnData.getSgst() != null
							? GenUtil.roundOffTheAmount(gstnData.getSgst())
							: BigDecimal.ZERO);
					innerDto.setIpCess(gstnData.getCess() != null
							? GenUtil.roundOffTheAmount(gstnData.getCess())
							: BigDecimal.ZERO);
				}

				// column 12-13
				if (gstnData != null && gstnData.getSectionName()
						.equalsIgnoreCase("5.1(b)")) {
					innerDto.setLateFeeIgst(gstnData.getIgst() != null
							? GenUtil.roundOffTheAmount(gstnData.getIgst())
							: BigDecimal.ZERO);
					innerDto.setLateFeeCgst(gstnData.getCgst() != null
							? GenUtil.roundOffTheAmount(gstnData.getCgst())
							: BigDecimal.ZERO);
					innerDto.setLateFeeSgst(gstnData.getSgst() != null
							? GenUtil.roundOffTheAmount(gstnData.getSgst())
							: BigDecimal.ZERO);
					innerDto.setLateFeeCess(gstnData.getCess() != null
							? GenUtil.roundOffTheAmount(gstnData.getCess())
							: BigDecimal.ZERO);
				}

			}

		} catch (Exception ex) {
			LOGGER.error("Exception while populating the GSTN reponse to "
					+ " PaidThroughItcDto Dto", ex);
		}
		return innerDto;

	}

	private List<PaidThroughItcDto> converToPaidThroughItcDto(
			ItcPaidInnerDto innerDto,
			Gstr3BSaveChangesLiabilitySetOffEntity dbResp, BigDecimal igst2i,
			BigDecimal cgst2i, BigDecimal sgst2i, BigDecimal cess2i,
			BigDecimal igst8A, BigDecimal cgst8A, BigDecimal sgst8A,
			BigDecimal cess8A) {

		List<PaidThroughItcDto> paidThroughDtoList = new ArrayList<>();
		BigDecimal zeroVal = BigDecimal.ZERO;
		PaidThroughItcDto iTax = new PaidThroughItcDto();
		PaidThroughItcDto cTax = new PaidThroughItcDto();
		PaidThroughItcDto sTax = new PaidThroughItcDto();
		PaidThroughItcDto csTax = new PaidThroughItcDto();

		iTax.setDesc("Integrated Tax");
		iTax.setOtrci(innerDto != null
				? (innerDto.getOthrcIgst() != null ? innerDto.getOthrcIgst()
						: zeroVal)
				: zeroVal);
		iTax.setPdi(dbResp != null
				? (dbResp.getIPDIgst() != null ? dbResp.getIPDIgst() : zeroVal)
				: zeroVal);
		iTax.setPdc(dbResp != null
				? (dbResp.getIPDCgst() != null ? dbResp.getIPDCgst() : zeroVal)
				: zeroVal);
		iTax.setPds(dbResp != null
				? (dbResp.getIPDSgst() != null ? dbResp.getIPDSgst() : zeroVal)
				: zeroVal);
		iTax.setRci8(innerDto != null
				? (innerDto.getRctpIgst() != null ? innerDto.getRctpIgst()
						: zeroVal)
				: zeroVal);
		iTax.setInti10(innerDto != null
				? (innerDto.getIpIgst() != null ? innerDto.getIpIgst()
						: zeroVal)
				: zeroVal);
		iTax.setLateFee12(innerDto != null
				? (innerDto.getLateFeeIgst() != null ? innerDto.getLateFeeIgst()
						: zeroVal)
				: zeroVal);
		iTax.setAdjNegative2i(igst2i);
		iTax.setAdjNegative8A(igst8A);
		iTax.setNetOthRecTaxPayable2i(iTax.getOtrci().subtract(igst2i));
		iTax.setRci9(iTax.getRci8().subtract(igst8A));
		paidThroughDtoList.add(iTax);

		cTax.setDesc("Central Tax");
		cTax.setOtrci(innerDto != null
				? (innerDto.getOthrcCgst() != null ? innerDto.getOthrcCgst()
						: zeroVal)
				: zeroVal);
		cTax.setPdi(dbResp != null
				? (dbResp.getCPDIgst() != null ? dbResp.getCPDIgst() : zeroVal)
				: zeroVal);
		cTax.setPdc(dbResp != null
				? (dbResp.getCPDCgst() != null ? dbResp.getCPDCgst() : zeroVal)
				: zeroVal);
		cTax.setRci8(innerDto != null
				? (innerDto.getRctpCgst() != null ? innerDto.getRctpCgst()
						: zeroVal)
				: zeroVal);
		cTax.setInti10(innerDto != null
				? (innerDto.getIpCgst() != null ? innerDto.getIpCgst()
						: zeroVal)
				: zeroVal);
		cTax.setLateFee12(innerDto != null
				? (innerDto.getLateFeeCgst() != null ? innerDto.getLateFeeCgst()
						: zeroVal)
				: zeroVal);
		cTax.setAdjNegative2i(cgst2i);
		cTax.setAdjNegative8A(cgst8A);
		cTax.setNetOthRecTaxPayable2i(cTax.getOtrci().subtract(cgst2i));
		cTax.setRci9(cTax.getRci8().subtract(cgst8A));
		paidThroughDtoList.add(cTax);

		sTax.setDesc("State/UT Tax");
		sTax.setOtrci(innerDto != null
				? (innerDto.getOthrcSgst() != null ? innerDto.getOthrcSgst()
						: zeroVal)
				: zeroVal);
		sTax.setPdi(dbResp != null
				? (dbResp.getSPDIgst() != null ? dbResp.getSPDIgst() : zeroVal)
				: zeroVal);
		sTax.setPds(dbResp != null
				? (dbResp.getSPDSgst() != null ? dbResp.getSPDSgst() : zeroVal)
				: zeroVal);
		sTax.setRci8(innerDto != null
				? (innerDto.getRctpSgst() != null ? innerDto.getRctpSgst()
						: zeroVal)
				: zeroVal);
		sTax.setInti10(innerDto != null
				? (innerDto.getIpSgst() != null ? innerDto.getIpSgst()
						: zeroVal)
				: zeroVal);
		sTax.setLateFee12(innerDto != null
				? (innerDto.getLateFeeSgst() != null ? innerDto.getLateFeeSgst()
						: zeroVal)
				: zeroVal);
		sTax.setAdjNegative2i(sgst2i);
		sTax.setAdjNegative8A(sgst8A);
		sTax.setNetOthRecTaxPayable2i(sTax.getOtrci().subtract(sgst2i));
		sTax.setRci9(sTax.getRci8().subtract(sgst8A));
		paidThroughDtoList.add(sTax);

		csTax.setDesc("Cess");
		csTax.setOtrci(innerDto != null
				? (innerDto.getOthrcCess() != null ? innerDto.getOthrcCess()
						: zeroVal)
				: zeroVal);
		csTax.setPdcs(dbResp != null
				? (dbResp.getCsPdCess() != null ? dbResp.getCsPdCess()
						: zeroVal)
				: zeroVal);
		csTax.setRci8(innerDto != null
				? (innerDto.getRctpCess() != null ? innerDto.getRctpCess()
						: zeroVal)
				: zeroVal);
		csTax.setInti10(innerDto != null
				? (innerDto.getIpCess() != null ? innerDto.getIpCess()
						: zeroVal)
				: zeroVal);
		csTax.setLateFee12(innerDto != null
				? (innerDto.getLateFeeCess() != null ? innerDto.getLateFeeCess()
						: zeroVal)
				: zeroVal);
		csTax.setAdjNegative2i(cess2i);
		csTax.setAdjNegative8A(cess8A);
		csTax.setNetOthRecTaxPayable2i(csTax.getOtrci().subtract(cess2i));
		csTax.setRci9(csTax.getRci8().subtract(cess8A));
		paidThroughDtoList.add(csTax);

		return paidThroughDtoList;

	}

	private String convertClobToString(Clob clob)
			throws SQLException, IOException {
		StringBuilder sb = new StringBuilder();
		Reader reader = clob.getCharacterStream();
		BufferedReader br = new BufferedReader(reader);
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		return sb.toString();
	}

	/*public static void main(String[] args) {
		
		String jsonString = "{\"openbal\":[{\"desc\":\"Other than reverse charge\",\"trancd\":30002,\"igst\":36425,\"cgst\":36425,\"sgst\":36425,\"cess\":36425},{\"desc\":\"Reverse charge and supplies made u/s 9(5)\",\"trancd\":30003,\"igst\":36425,\"cgst\":36425,\"sgst\":36425,\"cess\":36425}],\"closebal\":[{\"desc\":\"Other than reverse charge\",\"trancd\":30002,\"igst\":36425,\"cgst\":36425,\"sgst\":36425,\"cess\":36425},{\"desc\":\"Reverse charge and supplies made u/s 9(5)\",\"trancd\":30003,\"igst\":36425,\"cgst\":36425,\"sgst\":36425,\"cess\":36425}],\"negliabdtls\":[{\"rtnprd\":\"092019\",\"refno\":\"ab125632512123c\",\"trantyp\":\"Debit\",\"trandate\":\"12/10/2019\",\"desc\":\"Negative liability for GSTR3B/3BQ\",\"negliab\":[{\"desc\":\"Other than reverse charge\",\"trancd\":30002,\"igst\":0,\"cgst\":2323,\"sgst\":656,\"cess\":656},{\"desc\":\"Reverse charge and supplies made u/s 9(5)\",\"trancd\":30003,\"igst\":0,\"cgst\":2323,\"sgst\":656,\"cess\":656}],\"negliabal\":[{\"desc\":\"Other than reverse charge\",\"trancd\":30002,\"igst\":0,\"cgst\":2323,\"sgst\":656,\"cess\":656},{\"desc\":\"Reverse charge and supplies made u/s 9(5)\",\"trancd\":30003,\"igst\":0,\"cgst\":2323,\"sgst\":656,\"cess\":656}]},{\"rtnprd\":\"092019\",\"refno\":\"ab125632512123c\",\"trantyp\":\"Credit\",\"trandate\":\"12/10/2019\",\"desc\":\"Negative liability for GSTR3B/3BQ\",\"negliab\":[{\"desc\":\"Other than reverse charge\",\"trancd\":30002,\"igst\":1212,\"cgst\":0,\"sgst\":0,\"cess\":0},{\"desc\":\"Reverse charge and supplies made u/s 9(5)\",\"trancd\":30003,\"igst\":1212,\"cgst\":0,\"sgst\":0,\"cess\":0}],\"negliabal\":[{\"desc\":\"Other than reverse charge\",\"trancd\":30002,\"igst\":1212,\"cgst\":0,\"sgst\":0,\"cess\":0},{\"desc\":\"Reverse charge and supplies made u/s 9(5)\",\"trancd\":30003,\"igst\":1212,\"cgst\":0,\"sgst\":0,\"cess\":0}]}]}";

		JsonObject negativeLedgerJson = JsonParser
				.parseString(jsonString)
				.getAsJsonObject();
		
		BigDecimal totalIgst = BigDecimal.ZERO;
		BigDecimal totalCgst = BigDecimal.ZERO;
		BigDecimal totalSgst = BigDecimal.ZERO;
		BigDecimal totalCess = BigDecimal.ZERO;

		JsonArray negliabdtlsArray = negativeLedgerJson
				.getAsJsonArray("closebal");

				// Sum the values
				for (JsonElement negliabalElement : negliabdtlsArray) {
					JsonObject negliabalObject = negliabalElement
							.getAsJsonObject();
					totalIgst = totalIgst.add(negliabalObject
							.get("igst").getAsBigDecimal());
					totalCgst = totalCgst.add(negliabalObject
							.get("cgst").getAsBigDecimal());
					totalSgst = totalSgst.add(negliabalObject
							.get("sgst").getAsBigDecimal());
					totalCess = totalCess.add(negliabalObject
							.get("cess").getAsBigDecimal());
				}
				
				System.out.println(totalIgst);
				System.out.println(totalCgst);
				System.out.println(totalSgst);
				System.out.println(totalCess);

			}
		*/
	}

