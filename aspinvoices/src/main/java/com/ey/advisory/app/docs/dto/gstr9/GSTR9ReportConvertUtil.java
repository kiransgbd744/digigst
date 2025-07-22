/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("GSTR9ReportConvertUtil")
public class GSTR9ReportConvertUtil {

	public List<GSTR9TaxPaidReportStringDto> convertTaxPiad(
			List<GSTR9TaxPaidReportDownloadDto> taxPaidList) {

		try {
			List<GSTR9TaxPaidReportStringDto> dtoList = taxPaidList.stream()
					.map(o -> convertTP(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return dtoList;

		} catch (Exception e) {
			LOGGER.error("Error occured in String List taxPaidList {}", e);
			throw new AppException(e);
		}

	}

	private GSTR9TaxPaidReportStringDto convertTP(
			GSTR9TaxPaidReportDownloadDto o) {

		GSTR9TaxPaidReportStringDto dto = new GSTR9TaxPaidReportStringDto();
		
		dto.setGstin(o.getGstin());
		dto.setSubSection(o.getSubSection());
		dto.setNatureOfSupply(o.getNatureOfSupply());

		dto.setPdCashDiff(appendDecimalDigit(o.getPdCashDiff()));
		dto.setPdCessDiff(appendDecimalDigit(o.getPdCessDiff()));
		dto.setPdIgstDiff(appendDecimalDigit(o.getPdIgstDiff()));
		dto.setPdCgstDiff(appendDecimalDigit(o.getPdCgstDiff()));
		dto.setPdSgstDiff(appendDecimalDigit(o.getPdSgstDiff()));
		dto.setTaxPayableDiff(appendDecimalDigit(o.getTaxPayableDiff()));

		dto.setPdCashGstn(appendDecimalDigit(o.getPdCashGstn()));
		dto.setPdCessGstn(appendDecimalDigit(o.getPdCessGstn()));
		dto.setPdIgstGstn(appendDecimalDigit(o.getPdIgstGstn()));
		dto.setPdCgstGstn(appendDecimalDigit(o.getPdCgstGstn()));
		dto.setPdSgstGstn(appendDecimalDigit(o.getPdSgstGstn()));
		dto.setTaxPayableGstn(appendDecimalDigit(o.getTaxPayableGstn()));
	
		dto.setPdCashUserInput(appendDecimalDigit(o.getPdCashUserInput()));
		dto.setPdCessUserInput(appendDecimalDigit(o.getPdCessUserInput()));
		dto.setPdIgstUserInput(appendDecimalDigit(o.getPdIgstUserInput()));
		dto.setPdCgstUserInput(appendDecimalDigit(o.getPdCgstUserInput()));
		dto.setPdSgstUserInput(appendDecimalDigit(o.getPdSgstUserInput()));
		dto.setTaxPayableUserInput(appendDecimalDigit(o.getTaxPayableUserInput()));

		dto.setPdCashAutoCal(appendDecimalDigit(o.getPdCashAutoCal()));
		dto.setPdCessAutoCal(appendDecimalDigit(o.getPdCessAutoCal()));
		dto.setPdIgstAutoCal(appendDecimalDigit(o.getPdIgstAutoCal()));
		dto.setPdCgstAutoCal(appendDecimalDigit(o.getPdCgstAutoCal()));
		dto.setPdSgstAutoCal(appendDecimalDigit(o.getPdSgstAutoCal()));
		dto.setTaxPayableAutoCal(appendDecimalDigit(o.getTaxPayableAutoCal()));

		dto.setPdCashFiledAutoComp(appendDecimalDigit(o.getPdCashFiledAutoComp()));
		dto.setPdCessFiledAutoComp(appendDecimalDigit(o.getPdCessFiledAutoComp()));
		dto.setPdIgstFiledAutoComp(appendDecimalDigit(o.getPdIgstFiledAutoComp()));
		dto.setPdCgstFiledAutoComp(appendDecimalDigit(o.getPdCgstFiledAutoComp()));
		dto.setPdSgstFiledAutoComp(appendDecimalDigit(o.getPdSgstFiledAutoComp()));
		dto.setTaxPayableFiledAutoComp(appendDecimalDigit(o.getTaxPayableFiledAutoComp()));

	


		return dto;
	}

	public List<GSTR9ReportDownloadStringDto> convertMainReport(
			List<GSTR9ReportDownloadDto> taxPaidList) {
		try {
			List<GSTR9ReportDownloadStringDto> dtoList = taxPaidList.stream()
					.map(o -> convertMainList(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return dtoList;

		} catch (Exception e) {
			LOGGER.error("Error occured in String List taxPaidList {}", e);
			throw new AppException(e);
		}

	}

	private GSTR9ReportDownloadStringDto convertMainList(
			GSTR9ReportDownloadDto o) {
		GSTR9ReportDownloadStringDto dto = new GSTR9ReportDownloadStringDto();

		dto.setGstin(o.getGstin());
		dto.setSubSection(o.getSubSection());
		dto.setNatureOfSupply(o.getNatureOfSupply());
		
		
		dto.setDigiCessProAutoComp(appendDecimalDigit(o.getDigiCessProAutoComp()));
		dto.setDigiCgstProAutoComp(appendDecimalDigit(o.getDigiCgstProAutoComp()));
		dto.setDigiIgstProAutoComp(appendDecimalDigit(o.getDigiIgstProAutoComp()));
		dto.setDigiSgstProAutoComp(appendDecimalDigit(o.getDigiSgstProAutoComp()));
		dto.setDigiTaxableValueProAutoComp(appendDecimalDigit(o.getDigiTaxableValueProAutoComp()));
		
		dto.setDigiOtherProAutoComp(appendDecimalDigit(o.getDigiOtherProAutoComp()));
		dto.setDigiPenaltyProAutoComp(appendDecimalDigit(o.getDigiPenaltyProAutoComp()));
		dto.setDigiLateFeeProAutoComp(appendDecimalDigit(o.getDigiLateFeeProAutoComp()));
		dto.setDigiInterestProAutoComp(appendDecimalDigit(o.getDigiInterestProAutoComp()));
		
		
		
		dto.setCessUserInput(appendDecimalDigit(o.getCessUserInput()));
		dto.setCgstUserInput(appendDecimalDigit(o.getCgstUserInput()));
		dto.setIgstUserInput(appendDecimalDigit(o.getIgstUserInput()));
		dto.setSgstUserInput(appendDecimalDigit(o.getSgstUserInput()));
		dto.setTaxableValueUserInput(appendDecimalDigit(o.getTaxableValueUserInput()));

	
		dto.setCessFiledAutoComp(appendDecimalDigit(o.getCessFiledAutoComp()));
		dto.setCgstFiledAutoComp(appendDecimalDigit(o.getCgstFiledAutoComp()));
		dto.setIgstFiledAutoComp(appendDecimalDigit(o.getIgstFiledAutoComp()));
		dto.setSgstFiledAutoComp(appendDecimalDigit(o.getSgstFiledAutoComp()));
		dto.setTaxableValueFiledAutoComp(appendDecimalDigit(o.getTaxableValueFiledAutoComp()));
		
	
		dto.setCessAutoCal(appendDecimalDigit(o.getCessAutoCal()));
		dto.setCgstAutoCal(appendDecimalDigit(o.getCgstAutoCal()));
		dto.setIgstAutoCal(appendDecimalDigit(o.getIgstAutoCal()));
		dto.setSgstAutoCal(appendDecimalDigit(o.getSgstAutoCal()));
		dto.setTaxableValueAutoCal(appendDecimalDigit(o.getTaxableValueAutoCal()));
	
		dto.setCessGstn(appendDecimalDigit(o.getCessGstn()));
		dto.setCgstGstn(appendDecimalDigit(o.getCgstGstn()));
		dto.setIgstGstn(appendDecimalDigit(o.getIgstGstn()));
		dto.setSgstGstn(appendDecimalDigit(o.getSgstGstn()));
		dto.setTaxableValueGstn(appendDecimalDigit(o.getTaxableValueGstn()));
	
		dto.setCessDiff(appendDecimalDigit(o.getCessDiff()));
		dto.setCgstDiff(appendDecimalDigit(o.getCgstDiff()));
		dto.setIgstDiff(appendDecimalDigit(o.getIgstDiff()));
		dto.setSgstDiff(appendDecimalDigit(o.getSgstDiff()));
		dto.setTaxableValueDiff(appendDecimalDigit(o.getTaxableValueDiff()));
		
		
		dto.setOtherFiledAutoComp(appendDecimalDigit(o.getOtherFiledAutoComp()));
		dto.setPenaltyFiledAutoComp(appendDecimalDigit(o.getPenaltyFiledAutoComp()));
		dto.setLateFeeFiledAutoComp(appendDecimalDigit(o.getLateFeeFiledAutoComp()));
		dto.setInterestFiledAutoComp(appendDecimalDigit(o.getInterestFiledAutoComp()));
		
		dto.setOtherAutoCal(appendDecimalDigit(o.getOtherAutoCal()));
		dto.setPenaltyAutoCal(appendDecimalDigit(o.getPenaltyAutoCal()));
		dto.setInterestAutoCal(appendDecimalDigit(o.getInterestAutoCal()));
		dto.setLateFeeAutoCal(appendDecimalDigit(o.getLateFeeAutoCal()));
		
		
		dto.setOtherGstn(appendDecimalDigit(o.getOtherGstn()));
		dto.setPenaltyGstn(appendDecimalDigit(o.getPenaltyGstn()));
		dto.setInterestGstn(appendDecimalDigit(o.getInterestGstn()));
		dto.setLateFeeGstn(appendDecimalDigit(o.getLateFeeGstn()));
		
		
		dto.setOtherDiff(appendDecimalDigit(o.getOtherDiff()));
		dto.setPenaltyDiff(appendDecimalDigit(o.getPenaltyDiff()));
		dto.setInterestDiff(appendDecimalDigit(o.getInterestDiff()));
		dto.setLateFeeDiff(appendDecimalDigit(o.getLateFeeDiff()));
		
		
		dto.setOtherUserInput(appendDecimalDigit(o.getOtherUserInput()));
		dto.setPenaltyUserInput(appendDecimalDigit(o.getPenaltyUserInput()));
		dto.setLateFeeUserInput(appendDecimalDigit(o.getLateFeeUserInput()));
		dto.setInterestUserInput(appendDecimalDigit(o.getInterestUserInput()));

	
	
		return dto;
	}

	public List<Gstr9HsnReportDownloadStringDto> convertHsn(
			List<Gstr9HsnReportDownloadDto> taxPaidList) {
		try {
			List<Gstr9HsnReportDownloadStringDto> dtoList = taxPaidList.stream()
					.map(o -> convertHsnList(o))
					.collect(Collectors.toCollection(ArrayList::new));

			return dtoList;

		} catch (Exception e) {
			LOGGER.error("Error occured in String List taxPaidList {}", e);
			throw new AppException(e);
		}

	}

	private Gstr9HsnReportDownloadStringDto convertHsnList(
			Gstr9HsnReportDownloadDto o) {
		Gstr9HsnReportDownloadStringDto dto = new Gstr9HsnReportDownloadStringDto();

		dto.setCess(appendDecimalDigit(o.getCess()));
		dto.setCgst(appendDecimalDigit(o.getCgst()));
		dto.setConcessionalRateFlag(o.getConcessionalRateFlag());
		dto.setDescription(o.getDescription());
		dto.setFy(o.getFy());
		dto.setGstin(o.getGstin());
		dto.setIgst(appendDecimalDigit(o.getIgst()));
		dto.setHsn(o.getHsn());
		dto.setRateofTax(appendDecimalDigit(o.getRateofTax()));
		dto.setSgst(appendDecimalDigit(o.getSgst()));
		dto.setTableNumber(o.getTableNumber());
		dto.setTaxableValue(appendDecimalDigit(o.getTaxableValue()));
		dto.setTotalQuantity(appendDecimalDigit(o.getTotalQuantity()));
		dto.setUqc(o.getUqc());
		

		return dto;
	}
	
	private String appendDecimalDigit(BigDecimal b) {
		
		String val = b.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();

		String[] s = val.split("\\.");
		if (s.length == 2) {
			if (s[1].length() == 1)
				return "'" + (s[0] + "." + s[1] + "0");
			else {
				return "'" + val;
			}
		} else
			return "'" + (val + ".00");

	}

}
