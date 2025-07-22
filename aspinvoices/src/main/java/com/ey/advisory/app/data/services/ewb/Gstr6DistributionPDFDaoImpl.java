package com.ey.advisory.app.data.services.ewb;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.Gstr6DistributionEntity;
import com.ey.advisory.admin.data.entities.client.LogoConfigEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr6DistributionRepository;
import com.ey.advisory.admin.data.repositories.client.LogoConfigRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component("Gstr6DistributionPDFDaoImpl")
@Slf4j
public class Gstr6DistributionPDFDaoImpl {

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("LogoConfigRepository")
	LogoConfigRepository logoConfigRepository;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	@Autowired
	@Qualifier("Gstr6DistributionRepository")
	private Gstr6DistributionRepository gstr6DistributionRepository;

	private static final String AMOUNT_STRING = "0.00";

	DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");
	static DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	private static final String EMPTY = "";

	private static final String[] X = { EMPTY, "One ", "Two ", "Three ",
			"Four ", "Five ", "Six ", "Seven ", "Eight ", "Nine ", "Ten ",
			"Eleven ", "Twelve ", "Thirteen ", "Fourteen ", "Fifteen ",
			"Sixteen ", "Seventeen ", "Eighteen ", "Nineteen " };

	private static final String[] Y = { EMPTY, EMPTY, "Twenty ", "Thirty ",
			"Fourty ", "Fifty ", "Sixty ", "Seventy ", "Eighty ", "Ninety " };

	public JasperPrint isdTaxInvoicePdfReport(String id, String docType,
			String sgstin) {

		JasperPrint jasperPrint = null;
		String source = null;
		Long gstinId = Long.valueOf(id);
		Map<String, Object> parameters = new HashMap<>();
		if (docType.equalsIgnoreCase("INV")
				|| docType.equalsIgnoreCase("RNV")) {
			source = "ReportTemplates/IsdTaxInvoice.jrxml";
		} else
			source = "ReportTemplates/IsdCreditInvoice.jrxml";
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside gstr6 Isd credit note pdf creation for docType: %s",
					docType);
			LOGGER.debug(msg);
		}

		try {

			GSTNDetailEntity gstnInfo1 = gSTNDetailRepository
					.findRegDates(sgstin);
			String clientName = gstnInfo1.getRegisteredName() != null
					? gstnInfo1.getRegisteredName() : "";
			parameters.put("ClientName", clientName);

			String SupplierLegalName = gstnInfo1.getRegisteredName() != null
					? gstnInfo1.getRegisteredName() : "";
			parameters.put("SupplierName", SupplierLegalName);

			String stateName1 = statecodeRepository
					.findStateNameByCode(sgstin.substring(0, 2));
			String statecodeAndStateName = sgstin.substring(0, 2) + "-"
					+ stateName1;
			parameters.put("SupplierAddress",
					(gstnInfo1.getAddress1() != null
							? gstnInfo1.getAddress1() + "\n" : "")
							+ (gstnInfo1.getAddress2() != null
									? gstnInfo1.getAddress2() + "\n" : "")
							+ (statecodeAndStateName != null
									? statecodeAndStateName + "\n" : "")
							+ (gstnInfo1.getAddress3() != null
									? gstnInfo1.getAddress3() : ""));

			parameters.put("RegisteredOffice",
					(gstnInfo1.getAddress1() != null
							? gstnInfo1.getAddress1() + "\n" : "")
							+ (gstnInfo1.getAddress2() != null
									? gstnInfo1.getAddress2() + "\n" : "")
							+ (gstnInfo1.getAddress3() != null
									? gstnInfo1.getAddress3() : ""));
			String suppliernameFor = "For" + " "
					+ (gstnInfo1.getRegisteredName() != null
							? gstnInfo1.getRegisteredName() : "");
			parameters.put("SupplierNameFor", suppliernameFor);

			parameters.put("SGSTIN",
					(!Strings.isNullOrEmpty(gstnInfo1.getGstin()))
							? gstnInfo1.getGstin() : "");
			parameters.put("SPAN", gstnInfo1.getGstin().substring(2, 12));
			parameters.put("SupplierPhone",
					gstnInfo1.getRegisteredMobileNo() != null
							? gstnInfo1.getRegisteredMobileNo() : "");
			parameters.put("SupplierEmail",
					gstnInfo1.getRegisteredEmail() != null
							? gstnInfo1.getRegisteredEmail() : "");

			// getting values from GSTR6_ISD_DISTRIBUTION
			Optional<Gstr6DistributionEntity> gstr6distributionList = gstr6DistributionRepository
					.findById(gstinId);
			Gstr6DistributionEntity gstr6distribution = gstr6distributionList
					.get();

			parameters.put("InvoiceNumber",
					gstr6distribution.getDocNum() != null
							? gstr6distribution.getDocNum() : "");

			parameters.put("InvoiceDate",
					gstr6distribution.getDocDate() != null
							? formatter2.format(EYDateUtil.toISTDateTimeFromUTC(
									gstr6distribution.getDocDate()))
							: "");
			parameters.put("OriginalInvoiceNo",
					gstr6distribution.getOrigDocNumber() != null
							? gstr6distribution.getOrigDocNumber() : "");
			parameters.put("OriginalInvoiceDate",
					gstr6distribution.getOrigDocDate() != null
							? formatter2.format(EYDateUtil.toISTDateTimeFromUTC(
									gstr6distribution.getOrigDocDate()))
							: "");

			if (!docType.equalsIgnoreCase("INV")
					|| docType.equalsIgnoreCase("RNV")) {
				parameters.put("OriginalNoteNumber",
						gstr6distribution.getOrigCrNoteNumber() != null
								? gstr6distribution.getOrigCrNoteNumber() : "");
				parameters
						.put("OriginalNoteDate",
								gstr6distribution.getOrigCrNoteDate() != null
										? formatter2.format(
												EYDateUtil.toISTDateTimeFromUTC(
														gstr6distribution
																.getOrigCrNoteDate()))
										: "");
			}
			String reciepentgst = (gstr6distribution.getRecipientGSTIN() != null
					? gstr6distribution.getRecipientGSTIN().toString() : null);
			GSTNDetailEntity reciepentGstnInfo1 = null;
			if (reciepentgst != null) {
				reciepentGstnInfo1 = gSTNDetailRepository
						.findRegDates(reciepentgst);

				String reciepentname = reciepentGstnInfo1
						.getRegisteredName() != null
								? reciepentGstnInfo1.getRegisteredName() : "";
				parameters.put("RecipentName", reciepentname);
				parameters.put("Recipentgstin",
						gstr6distribution.getRecipientGSTIN() != null
								? gstr6distribution.getRecipientGSTIN() : "");
				parameters.put("RecipentPhone",
						reciepentGstnInfo1.getRegisteredMobileNo() != null
								? reciepentGstnInfo1.getRegisteredMobileNo()
								: "");
				parameters.put("RecipentEmail",
						reciepentGstnInfo1.getRegisteredEmail() != null
								? reciepentGstnInfo1.getRegisteredEmail() : "");

				parameters.put("RecipentStateCode",
						reciepentGstnInfo1.getStateCode() != null
								? reciepentGstnInfo1.getStateCode() : "");

				parameters.put("RecipentAddress",
						(reciepentGstnInfo1.getAddress1() != null
								? reciepentGstnInfo1.getAddress1()
										+ "\n"
								: "")
								+ (reciepentGstnInfo1.getAddress2() != null
										? reciepentGstnInfo1.getAddress2()
												+ "\n"
										: ""));

				String ReciepentstateName1 = (statecodeRepository
						.findStateNameByCode(reciepentgst.substring(0, 2)));

				String RecipentStateCodeStateName = (reciepentgst.substring(0,
						2) + "-" + ReciepentstateName1);

				parameters.put("RecipentStateCodeStateName",
						(RecipentStateCodeStateName != null
								? RecipentStateCodeStateName + "\n" : "")
								+ (reciepentGstnInfo1.getAddress3() != null
										? reciepentGstnInfo1.getAddress3()
										: ""));
			} else {
				parameters.put("Recipentgstin", "Un Registered");
				parameters.put("RecipentName", "");
				parameters.put("RecipentPhone", "");
				parameters.put("RecipentEmail", "");
				parameters.put("RecipentPhone", "");
				parameters.put("RecipentPincode", "");
				parameters.put("RecipentStateCodeStateName", "");
				parameters.put("RecipentStateCode",
						gstr6distribution.getStateCode() != null
								? gstr6distribution.getStateCode() : "");
				parameters.put("RecipentAddress", "");
			}
			if (docType.equalsIgnoreCase("INV")) {
				parameters.put("PageTitle", "ISD Distribution Invoice");
			} else if (docType.equalsIgnoreCase("RNV")) {
				parameters.put("PageTitle", "ISD Redistribution Invoice");
			} else if (docType.equalsIgnoreCase("CR")) {
				parameters.put("PageTitle", "ISD Distribution Credit Note");
			} else {
				parameters.put("PageTitle", "ISD Redistribution Credit Note");
			}
			String eligibleIndicator = gstr6distribution.getEligibleIndicator();
			if (eligibleIndicator.equalsIgnoreCase("E")) {
				parameters.put("EligibleInEligible", "Eligible");
			} else {
				parameters.put("EligibleInEligible", "InEligible");
			}
			Gstr6PrintPdfProductDetails productDetails = new Gstr6PrintPdfProductDetails();
			List<Gstr6PrintPdfProductDetails> printProductDetails = new ArrayList<>();
			productDetails
					.setDiscription("ISD Credit Distributed for the month");

			productDetails
					.setTaxPeriod(gstr6distribution.getReturnPeriod() != null
							? gstr6distribution.getReturnPeriod() : "");

			BigDecimal IgstSum = gstr6distribution.getIgstAsIgst()
					.add(gstr6distribution.getCgstAsIgst())
					.add(gstr6distribution.getSgstAsIgst());

			productDetails.setTotalIgst(IgstSum.toString());

			BigDecimal cgstSum = gstr6distribution.getIgstAsCgst()
					.add(gstr6distribution.getCgstAsCgst());

			productDetails.setTotalCgst(cgstSum.toString());

			BigDecimal sgstSum = gstr6distribution.getIgstAsSgst()
					.add(gstr6distribution.getSgstAsSgst());

			productDetails.setTotalSgst(sgstSum.toString());

			BigDecimal cessAmount = gstr6distribution.getCessAmount();

			productDetails.setTotalCess(
					cessAmount != null ? cessAmount.toString() : "0.00");

			BigDecimal totalCreditDistributed = IgstSum.add(cgstSum)
					.add(sgstSum).add(cessAmount);

			productDetails.setTotalCredit(totalCreditDistributed != null
					? totalCreditDistributed.toString() : "0.00");

			double totalCredit = totalCreditDistributed.doubleValue();
			String totalCreditInWords = null;
			try {
				if (totalCredit > 0) {
					String firstWord = null;
					String secondWord = null;
					double d = totalCredit;
					// String bob = Double.toString(d);
					String bob = new DecimalFormat("#.00#").format(d);
					String[] convert = bob.split("\\.");

					int a = Integer.parseInt(convert[0]);
					int b = Integer.parseInt(convert[1]);

					firstWord = convert(a) + " Rupees";
					secondWord = (b > 0) ? convertFraction(b) + " Paise" : EMPTY;
					if (!secondWord.isEmpty()) {
						totalCreditInWords = firstWord.concat(" and ").concat(secondWord);
					} else {
						totalCreditInWords = firstWord;
					}
				}
			} catch (Exception e) {
				LOGGER.error(
						"Exception occured while converting amount to words..",
						e);
				throw new AppException(e);
			}

			parameters.put("AmtInWords",
					totalCreditInWords != null ? totalCreditInWords : "");

			printProductDetails.add(productDetails);
			parameters.put("ItemDetails",
					new JRBeanCollectionDataSource(printProductDetails));

			List<LogoConfigEntity> entity2 = logoConfigRepository
					.getLogFileByGstin(gstnInfo1.getId());
			if (CollectionUtils.isNotEmpty(entity2)) {
				LogoConfigEntity logoConfigEntity = entity2.get(0);
				byte[] blob = logoConfigEntity.getLogofile();
				ByteArrayInputStream bis = new ByteArrayInputStream(blob);
				BufferedImage bImage2 = ImageIO.read(bis);
				parameters.put("logo", bImage2);
			}

			File file = ResourceUtils.getFile("classpath:" + source);

			JasperReport jasperReport = JasperCompileManager
					.compileReport(file.toString());
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
					new JREmptyDataSource());
		} catch (Exception ex) {
			LOGGER.error("Exception occured while genearting pdf..", ex);
		}

		return jasperPrint;

	}

	public static String convert(long n) {
		StringBuilder res = new StringBuilder();

		res.append(convertToDigit((n / 1000000000) % 100, "Billion, "));

		res.append(convertToDigit((n / 10000000) % 100, "Crore, "));

		res.append(convertToDigit(((n / 100000) % 100), "Lakh, "));

		res.append(convertToDigit(((n / 1000) % 100), "Thousand, "));

		res.append(convertToDigit(((n / 100) % 10), "Hundred "));

		if ((n > 100) && (n % 100 != 0)) {
			res.append("and ");
		}

		res.append(convertToDigit((n % 100), ""));

		return res.toString().trim().replace(", and", " and")
				.replaceAll("^(.*),$", "$1"); // remove trailing comma
	}

	private static String convertToDigit(long n, String suffix) {
		if (n == 0) {
			return EMPTY;
		}

		if (n > 19) {
			return Y[(int) (n / 10)] + X[(int) (n % 10)] + suffix;
		} else {
			return X[(int) n] + suffix;
		}
	}
	
	private static String convertFraction(int n) {
        if (n == 0) {
            return EMPTY;
        }

        if (n > 19) {
            return Y[n / 10] + X[n % 10];
        } else {
            return X[n];
        }
    }

}