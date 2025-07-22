package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BGstnSaveToAspRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveChangesLiabilitySetOffRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("RevIngGstr3BLiabilitySetOffServiceImpl")
public class RevIngGstr3BLiabilitySetOffServiceImpl implements 
Gstr3BLiabilitySetOffService {

	@Autowired
	@Qualifier("Gstr3BSaveChangesLiabilitySetOffRepository")
	private Gstr3BSaveChangesLiabilitySetOffRepository saveChangesRepo;
	
	@Autowired
	@Qualifier("Gstr3BGstnSaveToAspRepository")
	Gstr3BGstnSaveToAspRepository gstr3BGetData;
	
	@Autowired
	@Qualifier("Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository")
	private Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository ledgerRepo;

	@Override
	public Gstr3BLiabilitySetOffDto get3BLiabilitySetOff(String gstin, 
			String taxPeriod, String authToken, String message) {

		Gstr3BLiabilitySetOffDto dto = null;
		List<PaidThroughItcDto> gstr3bDetails = null;
		LedgerRespDto ledgerdetails = new LedgerRespDto();
		try {
			
				
				Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity ledgerCashBalData
				= ledgerRepo.findByGstinAndTaxPeriod(taxPeriod, gstin);
				
				if(ledgerCashBalData != null){
				ledgerdetails.setCgstIntr(ledgerCashBalData.getInterestCgst());
				ledgerdetails.setCgstLateFee(ledgerCashBalData.getFeeCgst());
				ledgerdetails.setCgstTx(ledgerCashBalData.getTaxCgst());
				ledgerdetails.setCrCess(ledgerCashBalData.getItcBalanceCess());
				ledgerdetails.setCrCgst(ledgerCashBalData.getItcBalanceCgst());
				ledgerdetails.setCrIgst(ledgerCashBalData.getItcBalanceIgst());
				ledgerdetails.setCrSgst(ledgerCashBalData.getItcBalanceSgst());
				ledgerdetails.setCsgstIntr(ledgerCashBalData.getInterestCgst());
				ledgerdetails.setCsgstLateFee(ledgerCashBalData.getFeeCgst());
				ledgerdetails.setCsgstTx(ledgerCashBalData.getTaxCess());
				ledgerdetails.setIgstIntr(ledgerCashBalData.getInterestIgst());
				ledgerdetails.setIgstLateFee(ledgerCashBalData.getFeeIgst());
				ledgerdetails.setIgstTx(ledgerCashBalData.getTaxIgst());
				ledgerdetails.setSgstIntr(ledgerCashBalData.getInterestSgst());
				ledgerdetails.setSgstLateFee(ledgerCashBalData.getFeeSgst());
				ledgerdetails.setSgstTx(ledgerCashBalData.getTaxSgst());
				}
				
				List<Gstr3bGstnSaveToAspEntity> get3BData = gstr3BGetData
					.findOtherLiabilityAndReverseChargeTaxAndInterestAndLateFee(
							taxPeriod, gstin);
				
			ItcPaidInnerDto itcPaidDto = writeToItcPaidInnerDto(get3BData);

			// getting column 3-4-5-6 data from db
			Gstr3BSaveChangesLiabilitySetOffEntity dbResp = saveChangesRepo
					.findByGstinAndTaxPeriodAndIsActive(taxPeriod, gstin);

			gstr3bDetails = converToPaidThroughItcDto(itcPaidDto, dbResp);
			
			Gstr3bGstnSaveToAspEntity currentMonthItc = gstr3BGetData
					.findByGstinAndTaxPeriodAndSectionName(gstin, taxPeriod);

			Itc4cDto itc4cData = currentMonthItc != null ? 
					getItc4cData(currentMonthItc) : null;
					
			List<LedgerDetailsDto> ledgerDetails = 
					converToLedgerDetailsDto(ledgerdetails, itc4cData);

			dto = new Gstr3BLiabilitySetOffDto( null, null, ledgerDetails, 
					gstr3bDetails, message, null);


		} catch (Exception ex) {
			LOGGER.error("Exception while populating the GSTN reponse to "
					+ "get3BLiabilitySetOff Dto", ex);
			throw new AppException(ex);
		}
		return dto;

	}

	private List<LedgerDetailsDto> converToLedgerDetailsDto(
			LedgerRespDto ledgerdetails, Itc4cDto itc4cData) {

		List<LedgerDetailsDto> ledgerDtoList = new ArrayList<>();
		BigDecimal zeroVal = BigDecimal.ZERO;
		LedgerDetailsDto txDto = new LedgerDetailsDto();
		LedgerDetailsDto intrDto = new LedgerDetailsDto();
		LedgerDetailsDto feeDto = new LedgerDetailsDto();
		
		BigDecimal itc4Cgst = itc4cData != null && itc4cData.getCgst() 
				!= null ? itc4cData.getCgst() : zeroVal;
		BigDecimal itc4Sgst = itc4cData != null && itc4cData.getSgst() 
				!= null ? itc4cData.getSgst() : zeroVal;
		BigDecimal itc4Igst = itc4cData != null && itc4cData.getIgst() 
				!= null ? itc4cData.getIgst() : zeroVal;
		BigDecimal itc4Cess = itc4cData != null && itc4cData.getCess() 
				!= null ? itc4cData.getCess() : zeroVal;

		txDto.setDesc("tx");
		txDto.setC((ledgerdetails != null && ledgerdetails.getCgstTx() != null)
				? ledgerdetails.getCgstTx() : zeroVal);
		txDto.setI((ledgerdetails != null && ledgerdetails.getIgstTx() != null)
				? ledgerdetails.getIgstTx() : zeroVal);
		txDto.setS((ledgerdetails != null && ledgerdetails.getSgstTx() != null)
				? ledgerdetails.getSgstTx() : zeroVal);
		txDto.setCs(
				(ledgerdetails != null && ledgerdetails.getCsgstTx() != null)
						? ledgerdetails.getCsgstTx() : zeroVal);
		txDto.setCrc(
				(ledgerdetails != null && ledgerdetails.getCrCgst() != null)
						? ledgerdetails.getCrCgst().add(itc4Cgst)
						: itc4Cgst);
		txDto.setCrs(
				(ledgerdetails != null && ledgerdetails.getCrSgst() != null)
						? ledgerdetails.getCrSgst().add(itc4Sgst)
						: itc4Sgst);
		txDto.setCri(
				(ledgerdetails != null && ledgerdetails.getCrIgst() != null)
						? ledgerdetails.getCrIgst().add(itc4Igst)
						: itc4Igst);
		txDto.setCrcs(
				(ledgerdetails != null && ledgerdetails.getCrCess() != null)
						? ledgerdetails.getCrCess().add(itc4Cess)
						: itc4Cess);
		ledgerDtoList.add(txDto);

		intrDto.setDesc("intr");
		intrDto.setC(
				(ledgerdetails != null && ledgerdetails.getCgstIntr() != null)
						? ledgerdetails.getCgstIntr() : zeroVal);
		intrDto.setI(
				(ledgerdetails != null && ledgerdetails.getIgstIntr() != null)
						? ledgerdetails.getIgstIntr() : zeroVal);
		intrDto.setS(
				(ledgerdetails != null && ledgerdetails.getSgstIntr() != null)
						? ledgerdetails.getSgstIntr() : zeroVal);
		intrDto.setCs(
				(ledgerdetails != null && ledgerdetails.getCsgstIntr() != null)
						? ledgerdetails.getCsgstIntr() : zeroVal);
		ledgerDtoList.add(intrDto);

		feeDto.setDesc("fee");
		feeDto.setC((ledgerdetails != null
				&& ledgerdetails.getCgstLateFee() != null)
						? ledgerdetails.getCgstLateFee() : zeroVal);
		feeDto.setI((ledgerdetails != null
				&& ledgerdetails.getIgstLateFee() != null)
						? ledgerdetails.getIgstLateFee() : zeroVal);
		feeDto.setS((ledgerdetails != null
				&& ledgerdetails.getSgstLateFee() != null)
						? ledgerdetails.getSgstLateFee() : zeroVal);
		feeDto.setCs((ledgerdetails != null
				&& ledgerdetails.getCsgstLateFee() != null)
						? ledgerdetails.getCsgstLateFee() : zeroVal);
		ledgerDtoList.add(feeDto);

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

	private ItcPaidInnerDto writeToItcPaidInnerDto(List
			<Gstr3bGstnSaveToAspEntity> get3BData) {

		ItcPaidInnerDto innerDto = new ItcPaidInnerDto();

		try {
			for (Gstr3bGstnSaveToAspEntity gstnData : get3BData) {
				
				// 3.1(a)- column 2
				if (gstnData != null && gstnData.getSectionName()
						.equalsIgnoreCase("3.1(a)")) {

					innerDto.setOthrcIgst(gstnData.getIgst() != null
							? gstnData.getIgst() : BigDecimal.ZERO);
					innerDto.setOthrcCgst(gstnData.getCgst() != null
							? gstnData.getCgst() : BigDecimal.ZERO);
					innerDto.setOthrcSgst(gstnData.getSgst() != null
							? gstnData.getSgst() : BigDecimal.ZERO);
					innerDto.setOthrcCess(gstnData.getCess() != null
							? gstnData.getCess() : BigDecimal.ZERO);
				}

				// 3.1(d) column 8-9
				if (gstnData != null && gstnData.getSectionName()
						.equalsIgnoreCase("3.1(d)")) {
					innerDto.setRctpIgst(gstnData.getIgst() != null
							? gstnData.getIgst() : BigDecimal.ZERO);
					innerDto.setRctpCgst(gstnData.getCgst() != null
							? gstnData.getCgst() : BigDecimal.ZERO);
					innerDto.setRctpSgst(gstnData.getSgst() != null
							? gstnData.getSgst() : BigDecimal.ZERO);
					innerDto.setRctpCess(gstnData.getCess() != null
							? gstnData.getCess() : BigDecimal.ZERO);
				}

				// column 10-11
				if (gstnData != null && gstnData.getSectionName()
						.equalsIgnoreCase("5.1(a)")) {
					innerDto.setIpIgst(gstnData.getIgst() != null
							? gstnData.getIgst() : BigDecimal.ZERO);
					innerDto.setIpCgst(gstnData.getCgst() != null
							? gstnData.getCgst() : BigDecimal.ZERO);
					innerDto.setIpSgst(gstnData.getSgst() != null
							? gstnData.getSgst() : BigDecimal.ZERO);
					innerDto.setIpCess(gstnData.getCess() != null
							? gstnData.getCess() : BigDecimal.ZERO);
				}

				// column 12-13
				if (gstnData != null && gstnData.getSectionName()
						.equalsIgnoreCase("5.1(b)")) {
					innerDto.setLateFeeIgst(gstnData.getIgst() != null
							? gstnData.getIgst() : BigDecimal.ZERO);
					innerDto.setLateFeeCgst(gstnData.getCgst() != null
							? gstnData.getCgst() : BigDecimal.ZERO);
					innerDto.setLateFeeSgst(gstnData.getSgst() != null
							? gstnData.getSgst() : BigDecimal.ZERO);
					innerDto.setLateFeeCess(gstnData.getCess() != null
							? gstnData.getCess() : BigDecimal.ZERO);
				}

			}
			

		} catch (Exception ex) {
			LOGGER.error("Exception while populating the GSTN reponse to "
					+ " PaidThroughItcDto Dto", ex);
		}
		return innerDto;

	}

	private List<PaidThroughItcDto> converToPaidThroughItcDto(ItcPaidInnerDto
			innerDto, Gstr3BSaveChangesLiabilitySetOffEntity dbResp) {

		List<PaidThroughItcDto> paidThroughDtoList = new ArrayList<>();
		BigDecimal zeroVal = BigDecimal.ZERO;
		PaidThroughItcDto iTax = new PaidThroughItcDto();
		PaidThroughItcDto cTax = new PaidThroughItcDto();
		PaidThroughItcDto sTax = new PaidThroughItcDto();
		PaidThroughItcDto csTax = new PaidThroughItcDto();

		iTax.setDesc("Integrated Tax");
		iTax.setOtrci(innerDto != null ? (innerDto.getOthrcIgst() != null
				? innerDto.getOthrcIgst() : zeroVal) : zeroVal);
		iTax.setPdi(dbResp != null
				? (dbResp.getIPDIgst() != null ? dbResp.getIPDIgst() : zeroVal)
				: zeroVal);
		iTax.setPdc(dbResp != null
				? (dbResp.getIPDCgst() != null ? dbResp.getIPDCgst() : zeroVal)
				: zeroVal);
		iTax.setPds(dbResp != null
				? (dbResp.getIPDSgst() != null ? dbResp.getIPDSgst() : zeroVal)
				: zeroVal);
		iTax.setRci8(innerDto != null ? (innerDto.getRctpIgst() != null
				? innerDto.getRctpIgst() : zeroVal) : zeroVal);
		iTax.setInti10(innerDto != null ? (innerDto.getIpIgst() != null
				? innerDto.getIpIgst() : zeroVal) : zeroVal);
		iTax.setLateFee12(
				innerDto != null
						? (innerDto.getLateFeeIgst() != null
								? innerDto.getLateFeeIgst() : zeroVal)
						: zeroVal);
		paidThroughDtoList.add(iTax);

		cTax.setDesc("Central Tax");
		cTax.setOtrci(innerDto != null ? (innerDto.getOthrcCgst() != null
				? innerDto.getOthrcCgst() : zeroVal) : zeroVal);
		cTax.setPdi(dbResp != null
				? (dbResp.getCPDIgst() != null ? dbResp.getCPDIgst() : zeroVal)
				: zeroVal);
		cTax.setPdc(dbResp != null
				? (dbResp.getCPDCgst() != null ? dbResp.getCPDCgst() : zeroVal)
				: zeroVal);
		cTax.setRci8(innerDto != null ? (innerDto.getRctpCgst() != null
				? innerDto.getRctpCgst() : zeroVal) : zeroVal);
		cTax.setInti10(innerDto != null ? (innerDto.getIpCgst() != null
				? innerDto.getIpCgst() : zeroVal) : zeroVal);
		cTax.setLateFee12(
				innerDto != null
						? (innerDto.getLateFeeCgst() != null
								? innerDto.getLateFeeCgst() : zeroVal)
						: zeroVal);
		paidThroughDtoList.add(cTax);

		sTax.setDesc("State/UT Tax");
		sTax.setOtrci(innerDto != null ? (innerDto.getOthrcSgst() != null
				? innerDto.getOthrcSgst() : zeroVal) : zeroVal);
		sTax.setPdi(dbResp != null
				? (dbResp.getSPDIgst() != null ? dbResp.getSPDIgst() : zeroVal)
				: zeroVal);
		sTax.setPds(dbResp != null
				? (dbResp.getSPDSgst() != null ? dbResp.getSPDSgst() : zeroVal)
				: zeroVal);
		sTax.setRci8(innerDto != null ? (innerDto.getRctpSgst() != null
				? innerDto.getRctpSgst() : zeroVal) : zeroVal);
		sTax.setInti10(innerDto != null ? (innerDto.getIpSgst() != null
				? innerDto.getIpSgst() : zeroVal) : zeroVal);
		sTax.setLateFee12(
				innerDto != null
						? (innerDto.getLateFeeSgst() != null
								? innerDto.getLateFeeSgst() : zeroVal)
						: zeroVal);
		paidThroughDtoList.add(sTax);

		csTax.setDesc("Cess");
		csTax.setOtrci(innerDto != null ? (innerDto.getOthrcCess() != null
				? innerDto.getOthrcCess() : zeroVal) : zeroVal);
		csTax.setPdcs(dbResp != null ? (dbResp.getCsPdCess() != null
				? dbResp.getCsPdCess() : zeroVal) : zeroVal);
		csTax.setRci8(innerDto != null ? (innerDto.getRctpCess() != null
				? innerDto.getRctpCess() : zeroVal) : zeroVal);
		csTax.setInti10(innerDto != null ? (innerDto.getIpCess() != null
				? innerDto.getIpCess() : zeroVal) : zeroVal);
		csTax.setLateFee12(
				innerDto != null
						? (innerDto.getLateFeeCess() != null
								? innerDto.getLateFeeCess() : zeroVal)
						: zeroVal);
		paidThroughDtoList.add(csTax);

		return paidThroughDtoList;

	}

	
}
