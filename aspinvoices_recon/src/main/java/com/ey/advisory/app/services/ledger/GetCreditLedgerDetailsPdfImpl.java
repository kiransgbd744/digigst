package com.ey.advisory.app.services.ledger;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.ey.advisory.app.docs.dto.ledger.CashLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.CreditLedgerPdfDetailsRespDto;
import com.ey.advisory.app.docs.dto.ledger.GetCashITCBalanceReqDto;
import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;
import com.ey.advisory.app.docs.dto.ledger.ItcLedgerDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.ItcLedgerOpenCloseBalDto;
import com.ey.advisory.app.docs.dto.ledger.ItcTransactionTypeBalDto;
import com.ey.advisory.app.docs.dto.ledger.LiabPdfDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.LiabilityLedgersDetailsDto;
import com.ey.advisory.app.docs.dto.ledger.TransactionTypeLibBalDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDao;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GsonUtil;
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
 * @author Kiran s
 *
 */
@Service("GetCreditLedgerDetailsPdfImpl")
public class GetCreditLedgerDetailsPdfImpl
		implements GetCreditLedgerDetailsPdf {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetCreditLedgerDetailsPdfImpl.class);

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
	@Qualifier("liabilityLedgerDetailsForReturnLiabilityDataAtGstnImpl")
	private LiabilityLedgerDetailsForReturnLiabilityDataAtGstn taxDataAtGstn;

	private String checkForNull(JsonElement jsonElement) {
		return jsonElement == null || jsonElement.isJsonNull() ? ""
				: jsonElement.getAsString();
	}

	public JasperPrint generateCreditPdfReport(GetCashLedgerDetailsReqDto dto,
			String groupCode) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<>();
		String source = "jasperReports/CreditLedgerPdf.jrxml";
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Inside GetCreditLedgerDetailsPdfImpl generatePdfReport method : %s",
							dto);
			LOGGER.debug(msg);
		}

		jasperPrint = generateJasperPrint(dto, parameters, source, jasperPrint,
				groupCode);

		return jasperPrint;

	}

	public JasperPrint generateLiabilityPdfReport(GetCashITCBalanceReqDto dto,
			String groupCode) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<>();
		String source = "jasperReports/liabilityLedgerPdf.jrxml";
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Inside GetliabilityLedgerDetailsPdfImpl generatePdfReport method : %s",
							dto);
			LOGGER.debug(msg);
		}

		jasperPrint = generateLiabilityJasperPdf(dto, parameters, source,
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
		String gstin = dto.getGstin();
		String fromDate = dto.getFromDate();
		String toDate = dto.getToDate();
		fromDate = fromDate.replace("-", "/");
		toDate = toDate.replace("-", "/");
		final int[] srNoCounter = { 1 };
		parameters.put("GSTIN", gstin);
		parameters.put("fromDate", fromDate);
		parameters.put("toDate", toDate);
		// need to remove
		List<CreditLedgerPdfDetailsRespDto> allDetailsDto = new ArrayList<>();
		/*
		 * Commented for testing purpose
		APIResponse apiResponse = taxPayerDao.findTaxPayerDetails(gstin,
				groupCode);
		if (apiResponse.isSuccess()) {
			String response = apiResponse.getResponse();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Following is Tax Payer Detailed " +
						" Response Dto from Gstin  %s :", apiResponse);
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
*/
		 legalName = "kiran";
		parameters.put("legalName", legalName);
		try {
			//need to uncomment
			apiResp = itcDataAtGstn.fromGstnTest(dto);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Following is creditDataAtGstn "
						+ " Response from Gstin  %s :", apiResp);
				LOGGER.debug(msg);
			}
			if (apiResp != null) {
				JsonObject requestObject = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				JsonObject reqObject = requestObject.get("itcLdgDtls")
						.getAsJsonObject();
				ItcLedgerDetailsDto responseDto = gson.fromJson(reqObject,
						ItcLedgerDetailsDto.class);
				ItcLedgerOpenCloseBalDto closeBal = responseDto.getClosenBal();
				ItcLedgerOpenCloseBalDto openBal = responseDto.getOpenBal();
				List<ItcTransactionTypeBalDto> respTransDtos = responseDto
						.getItctransTypeBalDto();

				if (openBal != null) {
					CreditLedgerPdfDetailsRespDto respOpenBal = new CreditLedgerPdfDetailsRespDto();
					respOpenBal.setSrNo(srNoCounter[0]++);
					respOpenBal.setDesc(openBal.getDescription());
					//formatted code
					respOpenBal.setBalIgst(formatIndianCurrency(openBal.getIgstTaxBal().toString()));
					respOpenBal.setBalCgst(formatIndianCurrency(openBal.getCgstTaxBal().toString()));
					respOpenBal.setBalSgst(formatIndianCurrency(openBal.getSgstTaxBal().toString()));
					respOpenBal.setBalCess(formatIndianCurrency(openBal.getCessTaxBal().toString()));
					respOpenBal.setBalTotal(formatIndianCurrency(openBal.getTotRngBal().toString()));

					// Default values
					respOpenBal.setItcTransDate("-");
					respOpenBal.setRefNo("-");
					respOpenBal.setTaxPeriod("-");
					respOpenBal.setTransType("-");
					allDetailsDto.add(respOpenBal);
				}
				if (respTransDtos != null) {
					respTransDtos.forEach(obj -> {
						CreditLedgerPdfDetailsRespDto transBal = new CreditLedgerPdfDetailsRespDto();
						transBal.setSrNo(srNoCounter[0]++);
						transBal.setItcTransDate(obj.getItcTransDate() != null
								? obj.getItcTransDate() : "-");
						transBal.setRefNo(obj.getReferenceNo() != null
								? obj.getReferenceNo() : "-");
						transBal.setTaxPeriod(obj.getRetPeriod() != null
								? obj.getRetPeriod() : "-");
						transBal.setDesc(obj.getDescription());
						transBal.setTransType(obj.getTransType() != null
								? obj.getTransType() : "-");
						//formatted code
						transBal.setBalIgst(formatIndianCurrency(obj.getIgstTaxBal().toString()));
						transBal.setBalSgst(formatIndianCurrency(obj.getSgstTaxBal().toString()));
						transBal.setBalCgst(formatIndianCurrency(obj.getCgstTaxBal().toString()));
						transBal.setBalCess(formatIndianCurrency(obj.getCessTaxBal().toString()));
						transBal.setBalTotal(formatIndianCurrency(obj.getTotRngBal().toString()));
						transBal.setCrDrIgst(formatIndianCurrency(obj.getIgstTaxAmt().toString()));
						transBal.setCrDrCgst(formatIndianCurrency(obj.getCgstTaxAmt().toString()));
						transBal.setCrDrSgst(formatIndianCurrency(obj.getSgstTaxAmt().toString()));
						transBal.setCrDrCess(formatIndianCurrency(obj.getCessTaxAmt().toString()));
						transBal.setCrDrTotal(formatIndianCurrency(obj.getTotTrAmt().toString()));

						allDetailsDto.add(transBal);
					});
				}
				if (closeBal != null) {
					CreditLedgerPdfDetailsRespDto respCloseBal = new CreditLedgerPdfDetailsRespDto();
					respCloseBal.setSrNo(srNoCounter[0]++);
					respCloseBal.setDesc(closeBal.getDescription());
					//formatted code
					respCloseBal.setBalIgst(formatIndianCurrency(closeBal.getIgstTaxBal().toString()));
					respCloseBal.setBalCgst(formatIndianCurrency(closeBal.getCgstTaxBal().toString()));
					respCloseBal.setBalSgst(formatIndianCurrency(closeBal.getSgstTaxBal().toString()));
					respCloseBal.setBalCess(formatIndianCurrency(closeBal.getCessTaxBal().toString()));
					respCloseBal.setBalTotal(formatIndianCurrency(closeBal.getTotRngBal().toString()));

					respCloseBal.setItcTransDate("-");
					respCloseBal.setRefNo("-");
					respCloseBal.setTaxPeriod("-");
					respCloseBal.setTransType("-");
					allDetailsDto.add(respCloseBal);
				}

				if (!allDetailsDto.isEmpty()) {
					parameters.put("IgstDetails",
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

	private JasperPrint generateLiabilityJasperPdf(GetCashITCBalanceReqDto dto,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint, String groupCode) {

		String apiResp = null;
		String legalName = "";
		Gson gson = GsonUtil.newSAPGsonInstance();
		String gstin = dto.getGstin();
		String fromDate = dto.getRetPeriod();
		String toDate = dto.getRetPeriod();
		fromDate = fromDate.replace("-", "/");
		toDate = toDate.replace("-", "/");
		final int[] srNoCounter = { 1 };
		parameters.put("GSTIN", gstin);
		String formatedFromDate = formatDate(fromDate);
		String formatedToDate = formatDate(toDate);
		parameters.put("fromDate", formatedFromDate);
		parameters.put("toDate", formatedToDate);
		List<LiabPdfDetailsDto> allDetailsDto = new ArrayList<>();

	/*	APIResponse apiResponse = taxPayerDao.findTaxPayerDetails(gstin,
				groupCode);
		//List<ItcDetailsRespDto> allDetailsDto = new ArrayList<>();
		if (apiResponse.isSuccess()) {
			String response = apiResponse.getResponse();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Following is Tax Payer Detailed " +
						" Response Dto from Gstin  %s :", apiResponse);
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

		}*/

		//legalName = "kiran";
		parameters.put("legalName", legalName);
		try {
			apiResp = taxDataAtGstn.fromGstnTestLiab(dto);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Following is LiabilityDataAtGstn "
						+ " Response from Gstin  %s :", apiResp);
				LOGGER.debug(msg);
			}
			if (apiResp != null) {
				JsonObject requestObject = (new JsonParser()).parse(apiResp)
						.getAsJsonObject();
				LiabilityLedgersDetailsDto liabLedgerDetDto = gson
						.fromJson(requestObject,
								LiabilityLedgersDetailsDto.class);
				CashLedgerOpenCloseBalDto closeBal = liabLedgerDetDto
						.getCloseBal();
				List<TransactionTypeLibBalDto> transTypeLibBalDto = liabLedgerDetDto
						.getTransTypeBalDto();

				transTypeLibBalDto.forEach(obj -> {
					LiabPdfDetailsDto transBal = new LiabPdfDetailsDto();
					transBal.setSrNo(srNoCounter[0]++);
					transBal.setDptDate(obj.getDptDate());
					transBal.setReferenceNo(obj.getReferenceNo());
					// transBal.setRetPeriod(liabLedgerDetDto.getRetPeriod());
					transBal.setDescription(obj.getDescription());
					transBal.setTransType(obj.getTransType());
					transBal.setDschrgType(obj.getDischargingType());
					// IGST details
					transBal.setIgstTaxValue(formatIndianCurrency(obj.getIgst().getTaxValue()));
					transBal.setIgstInterestValue(formatIndianCurrency(obj.getIgst().getInterestValue()));
					transBal.setIgstPenalty(formatIndianCurrency(obj.getIgst().getPenalty()));
					transBal.setIgstFees(formatIndianCurrency(obj.getIgst().getFees()));
					transBal.setIgstOther(formatIndianCurrency(obj.getIgst().getOther()));
					transBal.setIgstTotal(formatIndianCurrency(obj.getIgst().getTotal()));

					// IGST Balance details
					transBal.setIgstBalTaxValue(formatIndianCurrency(obj.getIgstBal().getTaxValue()));
					transBal.setIgstBalInterestValue(formatIndianCurrency(obj.getIgstBal().getInterestValue()));
					transBal.setIgstBalPenalty(formatIndianCurrency(obj.getIgstBal().getPenalty()));
					transBal.setIgstBalFees(formatIndianCurrency(obj.getIgstBal().getFees()));
					transBal.setIgstBalOther(formatIndianCurrency(obj.getIgstBal().getOther()));
					transBal.setIgstBalTotal(formatIndianCurrency(obj.getIgstBal().getTotal()));

					// CGST details
					transBal.setCgstTaxValue(formatIndianCurrency(obj.getCgst().getTaxValue()));
					transBal.setCgstInterestValue(formatIndianCurrency(obj.getCgst().getInterestValue()));
					transBal.setCgstPenalty(formatIndianCurrency(obj.getCgst().getPenalty()));
					transBal.setCgstFees(formatIndianCurrency(obj.getCgst().getFees()));
					transBal.setCgstOther(formatIndianCurrency(obj.getCgst().getOther()));
					transBal.setCgstTotal(formatIndianCurrency(obj.getCgst().getTotal()));

					// CGST Balance details
					transBal.setCgstBalTaxValue(formatIndianCurrency(obj.getCgstBal().getTaxValue()));
					transBal.setCgstBalInterestValue(formatIndianCurrency(obj.getCgstBal().getInterestValue()));
					transBal.setCgstBalPenalty(formatIndianCurrency(obj.getCgstBal().getPenalty()));
					transBal.setCgstBalFees(formatIndianCurrency(obj.getCgstBal().getFees()));
					transBal.setCgstBalOther(formatIndianCurrency(obj.getCgstBal().getOther()));
					transBal.setCgstBalTotal(formatIndianCurrency(obj.getCgstBal().getTotal()));

					// SGST details
					transBal.setSgstTaxValue(formatIndianCurrency(obj.getSgst().getTaxValue()));
					transBal.setSgstInterestValue(formatIndianCurrency(obj.getSgst().getInterestValue()));
					transBal.setSgstPenalty(formatIndianCurrency(obj.getSgst().getPenalty()));
					transBal.setSgstFees(formatIndianCurrency(obj.getSgst().getFees()));
					transBal.setSgstOther(formatIndianCurrency(obj.getSgst().getOther()));
					transBal.setSgstTotal(formatIndianCurrency(obj.getSgst().getTotal()));

					// SGST Balance details
					transBal.setSgstBalTaxValue(formatIndianCurrency(obj.getSgstBal().getTaxValue()));
					transBal.setSgstBalInterestValue(formatIndianCurrency(obj.getSgstBal().getInterestValue()));
					transBal.setSgstBalPenalty(formatIndianCurrency(obj.getSgstBal().getPenalty()));
					transBal.setSgstBalFees(formatIndianCurrency(obj.getSgstBal().getFees()));
					transBal.setSgstBalOther(formatIndianCurrency(obj.getSgstBal().getOther()));
					transBal.setSgstBalTotal(formatIndianCurrency(obj.getSgstBal().getTotal()));

					// Cess details
					transBal.setCessTaxValue(formatIndianCurrency(obj.getCess().getTaxValue()));
					transBal.setCessInterestValue(formatIndianCurrency(obj.getCess().getInterestValue()));
					transBal.setCessPenalty(formatIndianCurrency(obj.getCess().getPenalty()));
					transBal.setCessFees(formatIndianCurrency(obj.getCess().getFees()));
					transBal.setCessOther(formatIndianCurrency(obj.getCess().getOther()));
					transBal.setCessTotal(formatIndianCurrency(obj.getCess().getTotal()));

					// Cess Balance details
					transBal.setCessBalTaxValue(formatIndianCurrency(obj.getCessBal().getTaxValue()));
					transBal.setCessBalInterestValue(formatIndianCurrency(obj.getCessBal().getInterestValue()));
					transBal.setCessBalPenalty(formatIndianCurrency(obj.getCessBal().getPenalty()));
					transBal.setCessBalFees(formatIndianCurrency(obj.getCessBal().getFees()));
					transBal.setCessBalOther(formatIndianCurrency(obj.getCessBal().getOther()));
					transBal.setCessBalTotal(formatIndianCurrency(obj.getCessBal().getTotal()));


				
					allDetailsDto.add(transBal);
				});
				if (closeBal != null) {
				
					LiabPdfDetailsDto respCloseBal = new LiabPdfDetailsDto();
					// igstBalence details
					respCloseBal.setSrNo(srNoCounter[0]++);
					
					respCloseBal.setIgstBalTaxValue(formatIndianCurrency(closeBal.getIgstbal().getTaxValue()));
					respCloseBal.setIgstBalInterestValue(formatIndianCurrency(closeBal.getIgstbal().getInterestValue()));
					respCloseBal.setIgstBalPenalty(formatIndianCurrency(closeBal.getIgstbal().getPenalty()));
					respCloseBal.setIgstBalFees(formatIndianCurrency(closeBal.getIgstbal().getFees()));
					respCloseBal.setIgstBalOther(formatIndianCurrency(closeBal.getIgstbal().getOther()));
					respCloseBal.setIgstBalTotal(formatIndianCurrency(closeBal.getIgstbal().getTotal()));

					// Cgst balance details
					respCloseBal.setCgstBalTaxValue(formatIndianCurrency(closeBal.getCgstbal().getTaxValue()));
					respCloseBal.setCgstBalInterestValue(formatIndianCurrency(closeBal.getCgstbal().getInterestValue()));
					respCloseBal.setCgstBalPenalty(formatIndianCurrency(closeBal.getCgstbal().getPenalty()));
					respCloseBal.setCgstBalFees(formatIndianCurrency(closeBal.getCgstbal().getFees()));
					respCloseBal.setCgstBalOther(formatIndianCurrency(closeBal.getCgstbal().getOther()));
					respCloseBal.setCgstBalTotal(formatIndianCurrency(closeBal.getCgstbal().getTotal()));

					// Sgst balance details
					respCloseBal.setSgstBalTaxValue(formatIndianCurrency(closeBal.getSgstbal().getTaxValue()));
					respCloseBal.setSgstBalInterestValue(formatIndianCurrency(closeBal.getSgstbal().getInterestValue()));
					respCloseBal.setSgstBalPenalty(formatIndianCurrency(closeBal.getSgstbal().getPenalty()));
					respCloseBal.setSgstBalFees(formatIndianCurrency(closeBal.getSgstbal().getFees()));
					respCloseBal.setSgstBalOther(formatIndianCurrency(closeBal.getSgstbal().getOther()));
					respCloseBal.setSgstBalTotal(formatIndianCurrency(closeBal.getSgstbal().getTotal()));

					// Cess balance details
					respCloseBal.setCessBalTaxValue(formatIndianCurrency(closeBal.getCessbal().getTaxValue()));
					respCloseBal.setCessBalInterestValue(formatIndianCurrency(closeBal.getCessbal().getInterestValue()));
					respCloseBal.setCessBalPenalty(formatIndianCurrency(closeBal.getCessbal().getPenalty()));
					respCloseBal.setCessBalFees(formatIndianCurrency(closeBal.getCessbal().getFees()));
					respCloseBal.setCessBalOther(formatIndianCurrency(closeBal.getCessbal().getOther()));
					respCloseBal.setCessBalTotal(formatIndianCurrency(closeBal.getCessbal().getTotal()));


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

	public String formatDate(String inputDate) {
		try {
			// Define the input and output date formats
			DateTimeFormatter inputFormatter = DateTimeFormatter
					.ofPattern("MMyyyy");
			DateTimeFormatter outputFormatter = DateTimeFormatter
					.ofPattern("MMM-yy");

			// Parse the input date string to a LocalDate object
			LocalDate date = LocalDate.parse("01" + inputDate,
					DateTimeFormatter.ofPattern("ddMMyyyy"));

			// Format the LocalDate object to the desired output format
			return date.format(outputFormatter);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
					"Invalid input date format. Expected format: MMyyyy");
		}
	}
	
	 public static String formatIndianCurrency(String value) {   
		 if (value != null) {
         String valueStr = value;

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
	

	 
	 public static void main(String[] args) {
		 
		 BigDecimal value = new BigDecimal("12345678");
		 String valueStr = value.toString();
		   double numValue = value.doubleValue();
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

               // Return the value as BigDecimal (without formatting)
               System.out.println("++++" +formattedValue);
	}

}
}
