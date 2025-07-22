/**
 * 
 */
package com.ey.advisory.service.interest.gstr3b;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("GSTR3BInterestReportServiceImpl")
public class GSTR3BInterestReportServiceImpl implements GSTR3BInterestReportService {

	@Override
	public List<GSTR3BInterestDto> getGstr3BInterestSummaryReportData(String sGstin, String respJson) {

		JsonParser jsonParser = new JsonParser();
		JsonObject parseResponse = new JsonObject();
		List<GSTR3BInterestDto> dtoList = new ArrayList<>();

		GSTR3BTaxDto taxPayData = null;
		GSTR3BTaxDto pdCashData = null;
		GSTR3BTaxDto pdItcData = null;
		GSTR3BTaxDto interestData = null;

		if (respJson != null) {

			parseResponse = (JsonObject) jsonParser.parse(respJson).getAsJsonObject();

			String gstin = parseResponse.has("gstin") ? parseResponse.get("gstin").getAsString() : null;
			String retPeriod = parseResponse.has("ret_period") ? parseResponse.get("ret_period").getAsString() : null;
			String filedPeriod = parseResponse.has("filed_period") ? parseResponse.get("filed_period").getAsString()
					: null;
			String filedDate = parseResponse.has("filingdt") ? parseResponse.get("filingdt").getAsString() : null;
			String compDate = parseResponse.has("computedt") ? parseResponse.get("computedt").getAsString() : null;

			JsonObject taxPay = parseResponse.has("txpay") ? parseResponse.getAsJsonObject("txpay") : null;
			if (taxPay != null) {
				taxPayData = readtaxData(taxPay);
			}
			JsonObject pdCash = parseResponse.has("pdcash") ? parseResponse.getAsJsonObject("pdcash") : null;
			if (taxPay != null) {
				pdCashData = readtaxData(pdCash);
			}
			JsonObject pdItc = parseResponse.has("pditc") ? parseResponse.getAsJsonObject("pditc") : null;
			if (taxPay != null) {
				pdItcData = readtaxData(pdItc);
			}
			JsonObject interest = parseResponse.has("interest") ? parseResponse.getAsJsonObject("interest") : null;
			if (taxPay != null) {
				interestData = readtaxData(interest);
			}

			for (int i = 0; i <= 3; i++) {
				GSTR3BInterestDto dto = new GSTR3BInterestDto();
				dto.setSupplierGSTIN(gstin);
				dto.setReturnPeriod(retPeriod);
				dto.setPreviousFiledReturnPeriod(filedPeriod);
				dto.setFilingDate(filedDate);
				dto.setInterestComputationDate(compDate);
				dtoList.add(dto);

			}

			GSTR3BInterestDto dto1 = dtoList.get(0);
			dto1.setInterest(appendDecimalDigit(interestData.getIgst()));
			dto1.setPdCash(appendDecimalDigit(pdCashData.getIgst()));
			dto1.setPdITC(appendDecimalDigit(pdItcData.getIgst()));
			dto1.setTaxPayable(appendDecimalDigit(taxPayData.getIgst()));

			GSTR3BInterestDto dto2 = dtoList.get(1);
			dto2.setInterest(appendDecimalDigit(interestData.getCgst()));
			dto2.setPdCash(appendDecimalDigit(pdCashData.getCgst()));
			dto2.setPdITC(appendDecimalDigit(pdItcData.getCgst()));
			dto2.setTaxPayable(appendDecimalDigit(taxPayData.getCgst()));

			GSTR3BInterestDto dto3 = dtoList.get(2);
			dto3.setInterest(appendDecimalDigit(interestData.getSgst()));
			dto3.setPdCash(appendDecimalDigit(pdCashData.getSgst()));
			dto3.setPdITC(appendDecimalDigit(pdItcData.getSgst()));
			dto3.setTaxPayable(appendDecimalDigit(taxPayData.getSgst()));

			GSTR3BInterestDto dto4 = dtoList.get(3);
			dto4.setInterest(appendDecimalDigit(interestData.getCess()));
			dto4.setPdCash(appendDecimalDigit(pdCashData.getCess()));
			dto4.setPdITC(appendDecimalDigit(pdItcData.getCess()));
			dto4.setTaxPayable(appendDecimalDigit(taxPayData.getCess()));

		}
		return dtoList;
	}

	private GSTR3BTaxDto readtaxData(JsonObject taxData) {
		GSTR3BTaxDto dto = new GSTR3BTaxDto();
		dto.setIgst(taxData.has("iamt") ? taxData.get("iamt").getAsBigDecimal() : null);
		dto.setCgst(taxData.has("camt") ? taxData.get("camt").getAsBigDecimal() : null);
		dto.setSgst(taxData.has("samt") ? taxData.get("samt").getAsBigDecimal() : null);
		dto.setCess(taxData.has("csamt") ? taxData.get("csamt").getAsBigDecimal() : null);
		return dto;
	}

	@Override
	public List<GSTR3BInterestDetailsDto> getGstr3BInterestDetailsReportData(String sGstin, 
				String respJson) {
		
		List<GSTR3BInterestDetailsDto> dtoList = new ArrayList<>();
		List<GSTR3BInterestDetailsDto> respList = new ArrayList<>();
		Gson gson = GsonUtil.newSAPGsonInstance();
		
		if(respJson != null) {
		
		JsonObject requestObject = (new JsonParser()).parse(respJson)
		.getAsJsonObject();

		GSTR3BInterestJsonDto jsonDto = gson.fromJson(requestObject,
				GSTR3BInterestJsonDto.class);
		
		List<GSTR3BInterestBreakupDto> interestBreakups = jsonDto.getInterestBreakups();
		
		for (GSTR3BInterestBreakupDto interestBreakup : interestBreakups) {
			
			String aaroFinFlag = interestBreakup.getAaroFinFlag();
			String aatoFY = interestBreakup.getAatoFY();
			String annualAggregateTurnover = interestBreakup.getAnnualAggregateTurnover();
			String dueDate = interestBreakup.getDueDate();
			String notificationDate = interestBreakup.getNotificationDate();
			String notificationName = interestBreakup.getNotificationName();
			String profile = interestBreakup.getProfile();
			if(profile != null && "M".equalsIgnoreCase(profile)) {
				profile = "Monthly";
			}else if(profile != null && "Q".equalsIgnoreCase(profile)) {
				profile = "Quarterly";
			}
			
			String returnPeriodBrkup = interestBreakup.getReturnPeriodBrkup();
			GSTR3BTaxDto txPay = interestBreakup.getTxPay();
			GSTR3BTaxDto txPayDeclared = interestBreakup.getTxPayDeclared();
			
			
			List<String> txPayData = new ArrayList<>();
			
			txPayData.add(appendDecimalDigit(txPay.getIgst()));
			txPayData.add(appendDecimalDigit(txPay.getCgst()));
			txPayData.add(appendDecimalDigit(txPay.getSgst()));
			txPayData.add(appendDecimalDigit(txPay.getCess()));

			List<String> txPayDeclaredData = new ArrayList<>();
			
			txPayDeclaredData.add(appendDecimalDigit(txPayDeclared.getIgst()));
			txPayDeclaredData.add(appendDecimalDigit(txPayDeclared.getCgst()));
			txPayDeclaredData.add(appendDecimalDigit(txPayDeclared.getSgst()));
			txPayDeclaredData.add(appendDecimalDigit(txPayDeclared.getCess()));

			
			
			
			List<String> txChalanData = new ArrayList<>();
			List<GSTR3BChalanDto> chalans = interestBreakup.getChalans();
			
			int counter = 0;
			if (chalans != null && !chalans.isEmpty()) {
				for (GSTR3BChalanDto chalan : chalans) {
					txChalanData.add(appendDecimalDigit(chalan.getPdCashChalan().getIgst()));
					txChalanData.add(appendDecimalDigit(chalan.getPdCashChalan().getCgst()));
					txChalanData.add(appendDecimalDigit(chalan.getPdCashChalan().getSgst()));
					txChalanData.add(appendDecimalDigit(chalan.getPdCashChalan().getCess()));
					
					for (int i = 0; i <= 3; i++) {

						GSTR3BInterestDetailsDto dto = new GSTR3BInterestDetailsDto();
						dto.setSupplierGSTIN(sGstin);
						dto.setAaroFinFlag(aaroFinFlag);
						dto.setAatoFY(aatoFY);
						dto.setAnnualAggregateTurnover(annualAggregateTurnover);
						dto.setDueDate(dueDate);
						dto.setProfile(profile);
						dto.setNotificationDate(notificationDate);
						dto.setNotificationName(notificationName);
						dto.setReturnPeriod(returnPeriodBrkup);
						dto.setChallanPaidReturnPeriod(chalan.getRetPeriodChalan());
						if(counter >= 1) {
							int temp = counter * 4 + i;
						dto.setCashPaidChallan(txChalanData.get(temp));
						}else {
							dto.setCashPaidChallan(txChalanData.get(i));
						}
						dto.setInterestLiabilityConsidered(txPayData.get(i));
						dto.setInterestLiabilityDeclared(txPayDeclaredData.get(i));

						dtoList.add(dto);
					}
					counter++;
				}

			}
			
			List<String> rateInterstData = new ArrayList<>();
			List<GSTR3BRateDto> rates = interestBreakup.getRates();
			
			if (rates != null && !rates.isEmpty()) {
				for (GSTR3BRateDto rate : rates) {
					rateInterstData.add(appendDecimalDigit(rate.getInteresRate().getIgst()));
					rateInterstData.add(appendDecimalDigit(rate.getInteresRate().getCgst()));
					rateInterstData.add(appendDecimalDigit(rate.getInteresRate().getSgst()));
					rateInterstData.add(appendDecimalDigit(rate.getInteresRate().getCess()));
				}
			}
			
				List<String> rateStDate = new ArrayList<>();
				if (rates != null && !rates.isEmpty()) {
					for (GSTR3BRateDto rate : rates) {
						for (int i = 0; i <= 3; i++) {
							rateStDate.add(rate.getRateStartDate());
						}
					}
				}

				List<String> rateDeyaled = new ArrayList<>();

				if (rates != null && !rates.isEmpty()) {
					for (GSTR3BRateDto rate : rates) {
						for (int i = 0; i <= 3; i++) {
							rateDeyaled.add(rate.getDelay());
						}
					}
				}
				List<String> rateInterstAplcbl = new ArrayList<>();

				if (rates != null && !rates.isEmpty()) {
					for (GSTR3BRateDto rate : rates) {
						for (int i = 0; i <= 3; i++) {
							rateInterstAplcbl.add(rate.getRate());
						}
					}
				}

				List<String> rateEdDate = new ArrayList<>();
				if (rates != null && !rates.isEmpty()) {
					for (GSTR3BRateDto rate : rates) {
						for (int i = 0; i <= 3; i++) {
							rateEdDate.add(rate.getRateEndDate());
							
						}
					}
				}

				int i = 0;
				for (GSTR3BInterestDetailsDto dto : dtoList) {
					if (i <= dtoList.size()) {
						dto.setInterestRateApplicable(rateInterstAplcbl.get(i));
						dto.setNoOfDaysDelayed(rateDeyaled.get(i));
						dto.setRetunPeriodEndDate(rateEdDate.get(i));
						dto.setRetunPeriodStartDate(rateStDate.get(i));
						dto.setSystemCalculatedInterest(rateInterstData.get(i));
						i++;
					}

				}
			
			respList.addAll(dtoList);
			
		}
		
		}
		
		return respList;
	}

	private String appendDecimalDigit(BigDecimal value) {
		try {
			if (isPresent(value)) {
				String val = value.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
				String[] s = val.split("\\.");
				if (s.length == 2) {
					if (s[1].length() == 1) {
						val = (s[0] + "." + s[1] + "0");
						return val;
					} else {
						return val;
					}
				} else {
					val = (val + ".00");
					return val;
				}

			}
		} catch (Exception e) {
			LOGGER.error("GSTR3BInterestReportServiceImpl.AppendDecimalDigit method {}", value);
			return value != null ? value.toString() : null;
		}
		LOGGER.error("GSTR3BInterestReportServiceImpl.AppendDecimalDigit method val : {} before final return ",
				value.toString());
		return value.toString();
	}

}
