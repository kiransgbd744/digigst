package com.ey.advisory.app.inward.einvoice;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.entities.client.asprecon.GetIrnDetailPayloadEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnDtlPayloadRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnHeaderB2BRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnHeaderDexpRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnHeaderExpwpRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnHeaderExwopRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnHeaderSezwopRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnHeaderSezwpRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemB2BRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemDexpRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemExpWopRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemExpWpRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemSezwopRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemSezwpRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.QRcodeGenerator;
import com.ey.advisory.core.dto.GoodsProductDetails;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component("InwardEinvoiceSummaryPDFDaoImpl")
@Slf4j
public class InwardEinvoiceSummaryPDFDaoImpl
		implements InwardEinvoiceServicePdf {

	private static final String AMOUNT_STRING = "0.00";

	@Autowired
	QRcodeGenerator qRcodeGenerator;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("GetIrnDtlPayloadRepository")
	private GetIrnDtlPayloadRepository getIrnDtlPayloadRepository;

	@Autowired
	private GetIrnHeaderB2BRepository getIrnb2bHeaderRepo;

	@Autowired
	private GetIrnLineItemB2BRepository getIrnb2bLineRepo;

	@Autowired
	private GetIrnHeaderSezwopRepository getIrnSezwopHeaderRepo;

	@Autowired
	private GetIrnLineItemSezwopRepository getIrnSezwopLineRepo;

	@Autowired
	private GetIrnHeaderSezwpRepository getIrnSezwpHeaderRepo;

	@Autowired
	private GetIrnLineItemSezwpRepository getIrnSezwpLineRepo;

	@Autowired
	private GetIrnHeaderDexpRepository getIrnDexpHeaderRepo;

	@Autowired
	private GetIrnLineItemDexpRepository getIrnDexpLineRepo;

	@Autowired
	private GetIrnHeaderExpwpRepository getIrnExpwpHeaderRepo;

	@Autowired
	private GetIrnLineItemExpWpRepository getIrnExpWpLineRepo;

	@Autowired
	private GetIrnHeaderExwopRepository getIrnExpWopHeaderRepo;

	@Autowired
	private GetIrnLineItemExpWopRepository getIrnExpWopLineRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");
	static DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	final static Map<String, String> docType = ImmutableMap
			.<String, String>builder().put("INV", " Tax Invoice")
			//.put("CRN", "Credit Note").put("DBN", "Debit Note")
			.put("RNV", "Revised Invoice").put("CR", "Credit Note")
			.put("RCR", "Revised Credit Note").put("DR", "Debit Note")
			.put("RDR", "Revised Debit note").put("DLC", "Delivery Challan")
			.put("BOE", "Bill of Entry").put("OTH", "Others")
			.put("BOS", "Bill of Supply").build();

	final static Map<String, String> supplyType = ImmutableMap
			.<String, String>builder()
			.put("EXPT", "SUPPLY MEANT FOR EXPORT ON PAYMENT OF INTEGRATED TAX")
			.put("EXPWT",
					"SUPPLY MEANT FOR EXPORT UNDER BOND OR LETTER OF UNDERTAKING WITHOUT PAYMENT OF INTEGRATED TAX")
			.put("SEZWP",
					"SUPPLY MEANT FOR SEZ UNIT OR SEZ DEVELOPER FOR AUTHORISED OPERATIONS ON PAYMENT OF INTEGRATED TAX")
			.put("SEZWOP",
					"SUPPLY MEANT FOR SEZ UNIT OR SEZ DEVELOPER FOR AUTHORISED OPERATIONS UNDER BOND OR LETTER OF UNDERTAKING WITHOUT PAYMENT OF INTEGRATED TAX")
			.build();

	final static Map<String, String> REVERSECHARGE = ImmutableMap
			.<String, String>builder().put("Y", "YES").put("N", "NO")
			.put("L", "NO").put(" ", "NO").build();
	final static String slash = "/";

	public JasperPrint generatePdfReport(String irn, String irnStatus,
			String supplyType, String docType) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<>();
		String source = "jasperReports/Inward_Einvoice_Summary.jrxml";
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Inside GoodsServicesPDFDaoImpl for Irn: %s", irn);
			LOGGER.debug(msg);
		}

		if (supplyType.equalsIgnoreCase("B2B")) {
			Optional<GetIrnB2bHeaderEntity> b2bData = getIrnb2bHeaderRepo
					.findByIrnAndIrnStatus(irn, irnStatus);
			GetIrnB2bHeaderEntity b2b = b2bData.get();
			jasperPrint = generateJasperPrint(b2b, parameters, source,
					jasperPrint);
		} else if (supplyType.equalsIgnoreCase("DXP")) {
			Optional<GetIrnDexpHeaderEntity> dexpData = getIrnDexpHeaderRepo
					.findByIrnAndIrnStatus(irn, irnStatus);
			GetIrnDexpHeaderEntity dexp = dexpData.get();
			jasperPrint = generateJasperPrint(dexp, parameters, source,
					jasperPrint);
		} else if (supplyType.equalsIgnoreCase("EXPWOP")) {
			Optional<GetIrnExpWopHeaderEntity> expwopData = getIrnExpWopHeaderRepo
					.findByIrnAndIrnStatus(irn, irnStatus);
			GetIrnExpWopHeaderEntity expwop = expwopData.get();
			jasperPrint = generateJasperPrint(expwop, parameters, source,
					jasperPrint);
		} else if (supplyType.equalsIgnoreCase("EXPWP")) {
			Optional<GetIrnExpWpHeaderEntity> expwpData = getIrnExpwpHeaderRepo
					.findByIrnAndIrnStatus(irn, irnStatus);
			GetIrnExpWpHeaderEntity expwp = expwpData.get();
			jasperPrint = generateJasperPrint(expwp, parameters, source,
					jasperPrint);
		} else if (supplyType.equalsIgnoreCase("SEZWOP")) {
			Optional<GetIrnSezWopHeaderEntity> sezwopData = getIrnSezwopHeaderRepo
					.findByIrnAndIrnStatus(irn, irnStatus);
			GetIrnSezWopHeaderEntity sezwop = sezwopData.get();
			jasperPrint = generateJasperPrint(sezwop, parameters, source,
					jasperPrint);
		} else if (supplyType.equalsIgnoreCase("SEZWP")) {
			Optional<GetIrnSezWpHeaderEntity> sezwpData = getIrnSezwpHeaderRepo
					.findByIrnAndIrnStatus(irn, irnStatus);
			GetIrnSezWpHeaderEntity sezwp = sezwpData.get();
			jasperPrint = generateJasperPrint(sezwp, parameters, source,
					jasperPrint);
		}

		return jasperPrint;

	}

	public JasperPrint generateJasperPrint(GetIrnB2bHeaderEntity b2b,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint) {
		try {

			String irn = b2b.getIrn();

			List<String> gstins = new ArrayList<>();

			if (b2b.getSupplierGSTIN() != null)
				gstins.add(b2b.getSupplierGSTIN());
			if (b2b.getCustomerGSTIN() != null)
				gstins.add(b2b.getCustomerGSTIN());
			if (b2b.getShipToGSTIN() != null)
				gstins.add(b2b.getShipToGSTIN());
			String supplierAddress = (b2b.getSupplierAddress1() != null
					? b2b.getSupplierAddress1() + "," : "")
					+ (b2b.getSupplierAddress2() != null
							? b2b.getSupplierAddress2() + "," : "")
					+ (b2b.getSupplierLocation() != null
							? b2b.getSupplierLocation() : "");

			parameters.put("SupplierAddress", supplierAddress);
			String supplierName = (b2b.getSupplierLegalName() != null
					? b2b.getSupplierLegalName() : "");
			parameters.put("SupplierName", supplierName);
			String stateName = statecodeRepository.findStateNameByCode(
					b2b.getSupplierGSTIN().substring(0, 2));
			String placeOfSupply1 = b2b.getSupplierGSTIN().substring(0, 2) + "-"
					+ stateName;
			if (stateName == null) {
				placeOfSupply1 = b2b.getSupplierGSTIN().substring(0, 2);
			}

			String supplierState = (b2b.getSupplierPincode() != null
					? placeOfSupply1 + "," + b2b.getSupplierPincode().toString()
					: "");
			parameters.put("SupplierStateCodeNamePincode", supplierState);

			String customerAddress = (b2b.getCustomerAddress1() != null
					? b2b.getCustomerAddress1() + "," : "")
					+ (b2b.getCustomerAddress2() != null
							? b2b.getCustomerAddress2() + "," : "")
					+ (b2b.getCustomerLocation() != null
							? b2b.getCustomerLocation() : "");

			parameters.put("CustomerAddress", customerAddress);
			String customerName = (b2b.getCustomerLegalName() != null
					? b2b.getCustomerLegalName() : "");

			parameters.put("CustomerName", customerName);
			String custstateName = "";
			if (b2b.getCustomerStateCode().toString() != null) {
				custstateName = statecodeRepository.findStateNameByCode(
						b2b.getCustomerStateCode().toString());
			}

			String placeOfSupply4 = "";
			String customerState = "";
			if (b2b.getShipToStateCode() != null) {
				placeOfSupply4 = b2b.getShipToStateCode() + "-" + custstateName;

			}
			customerState = (b2b.getCustomerPincode() != null
					? placeOfSupply4 + "," + b2b.getCustomerPincode().toString()
					: "");
			parameters.put("CustomerStateCodeNamePincode", customerState);

			String shiptoAddress = (b2b.getShipToAddress1() != null
					? b2b.getShipToAddress1() + "," : "")
					+ (b2b.getShipToAddress2() != null
							? b2b.getShipToAddress2() + "," : "")
					+ (b2b.getShipToLocation() != null ? b2b.getShipToLocation()
							: "");
			if (Strings.isNullOrEmpty(shiptoAddress)) {
				shiptoAddress = customerAddress;
			}

			String dispatcherAddress = (b2b.getDispatcherAddress1() != null
					? b2b.getDispatcherAddress1() + "," : "")
					+ (b2b.getDispatcherAddress2() != null
							? b2b.getDispatcherAddress2() + "," : "")
					+ (b2b.getDispatcherLocation() != null
							? b2b.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress;
			}

			List<GetIrnB2bItemEntity> lineItems = getIrnb2bLineRepo
					.findByHeaderId(b2b.getId());
			parameters.put("SGSTIN",
					(!Strings.isNullOrEmpty(b2b.getSupplierGSTIN()))
							? b2b.getSupplierGSTIN() : "");

			parameters.put("irn", b2b.getIrn() != null ? b2b.getIrn() : "N/A");

			String custgstin = (b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("customergstin", custgstin);

			String placeOfSupply = "";
			if (b2b.getBillingPOS() != null) {
				String stateName1 = statecodeRepository
						.findStateNameByCode(b2b.getBillingPOS());
				placeOfSupply = stateName1 + "(" + b2b.getBillingPOS() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);

			parameters.put("SupplierAdd",
					(b2b.getSupplierAddress1() != null
							? b2b.getSupplierAddress1() + "\n" : "")
							+ (b2b.getSupplierAddress2() != null
									? b2b.getSupplierAddress2() + "\n" : "")
							+ (b2b.getSupplierLocation() != null
									? b2b.getSupplierLocation() : ""));

			parameters.put("CustomerGSTIN", b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("CustomerAdd",
					(b2b.getCustomerAddress1() != null
							? b2b.getCustomerAddress1() + "\n" : "")
							+ (b2b.getCustomerAddress2() != null
									? b2b.getCustomerAddress2() + "\n" : "")
							+ (b2b.getCustomerLocation() != null
									? b2b.getCustomerLocation() : ""));

			List<GoodsProductDetails> eInvoiceProductDetails = new ArrayList<>();
			int slNo = 1;
			BigDecimal ttl = new BigDecimal("0.00");
			BigDecimal tt2 = new BigDecimal("0.00");
			BigDecimal tt4 = new BigDecimal("0.00");
			for (GetIrnB2bItemEntity line : lineItems) {
				GoodsProductDetails productDetails = new GoodsProductDetails();
				productDetails.setSlNo(String.valueOf(slNo));
				productDetails.setPrdname(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails.setPrddesc(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails
						.setHsn(line.getHsn() != null ? line.getHsn() : "");
				if (line.getQuantity() == null) {
					productDetails.setQuantity(line.getQuantity() != null
							? line.getQuantity().toString() : "0");
				} else {
					productDetails.setQuantity(line.getQuantity().toString());
				}
				productDetails.setUnit(line.getUnit() != null
						? line.getUnit().toString()
						: "");
				productDetails.setUnitprice(line.getUnitPrice() != null
						? GenUtil.formatCurrency(line.getUnitPrice())
						: AMOUNT_STRING);

				productDetails.setItemdiscount(line.getItemDiscount() != null
						? GenUtil.formatCurrency(line.getItemDiscount())
						: AMOUNT_STRING);
				if (line.getItemDiscount() != null) {
					tt4 = tt4.add(line.getItemDiscount());
				}
				productDetails.setItemassessableamount(
						line.getItemAssessableAmt() != null
								? GenUtil.formatCurrency(
										line.getItemAssessableAmt())
								: AMOUNT_STRING);
				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				productDetails.setIgstrate(String.valueOf(igstRate));
				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				productDetails.setCgstrate(String.valueOf(cgstRate));
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				productDetails.setSgstrate(String.valueOf(sgstRate));
				BigDecimal cessRate = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				cessRate = cessRate.setScale(2, BigDecimal.ROUND_HALF_UP);

				productDetails.setCessadvlrate(String.format("%.2f", cessRate));
				BigDecimal stateCessRate = line
						.getStateCessAdvaloremRate() != null
								? line.getStateCessAdvaloremRate()
								: BigDecimal.ZERO;
				stateCessRate = stateCessRate.setScale(2,
						BigDecimal.ROUND_HALF_UP);

				productDetails.setTaxrate(String.format("%.2f", stateCessRate));

				productDetails.setItemotherchargesFormatted(
						line.getItemOtherCharges() != null
								? GenUtil.formatCurrency(
										line.getItemOtherCharges())
								: AMOUNT_STRING);
				if (line.getItemOtherCharges() != null) {
					tt2 = tt2.add(line.getItemOtherCharges());
				}
				productDetails.setTotalitemValFormatted(
						line.getTotalItemAmount() != null
								? GenUtil.formatCurrency(
										line.getTotalItemAmount())
								: AMOUNT_STRING);
				if (line.getTotalItemAmount() != null) {
					ttl = ttl.add(line.getTotalItemAmount());
				}
				eInvoiceProductDetails.add(productDetails);
				slNo++;
			}
			parameters.put("ItemDetails",
					new JRBeanCollectionDataSource(eInvoiceProductDetails));

			parameters.put("RoundOffValue", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");

			String signedqr = "";
			String ackNo = "";
			String ackDate = "";
			GetIrnDetailPayloadEntity getSignedEntity = getIrnDtlPayloadRepository
					.findByIrnAndIrnStatus(irn, b2b.getIrnStatus());

			if (getSignedEntity != null
					&& getSignedEntity.getPayload() != null) {
				Clob payloadClob = getSignedEntity.getPayload();
				try (Reader reader = payloadClob.getCharacterStream()) {
					StringBuilder payloadStringBuilder = new StringBuilder();
					char[] buffer = new char[1024];
					int bytesRead;
					while ((bytesRead = reader.read(buffer)) != -1) {
						payloadStringBuilder.append(buffer, 0, bytesRead);
					}
					String payloadString = payloadStringBuilder.toString();

					// Parse the JSON to get the SignedQRCode
					JsonObject payloadJson = JsonParser
							.parseString(payloadString).getAsJsonObject();
					ackNo = payloadJson.getAsJsonPrimitive("AckNo")
							.getAsString();
					ackDate = payloadJson.getAsJsonPrimitive("AckDt")
							.getAsString();

					if (payloadJson.has("SignedQRCode")) {
						signedqr = payloadJson
								.getAsJsonPrimitive("SignedQRCode")
								.getAsString();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Signed QR {}", signedqr);
						}
					} else {
						// Handle the case where the key is not present
						LOGGER.warn(
								"Key 'SignedQRCode' not found in the JSON object.");
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Signed QR {}, ackNo {}, ackDate {}",
								signedqr, ackNo, ackDate);
					}

				} catch (IOException | SQLException e) {
					LOGGER.error(
							"Exception occured while getting the signed QR from clob.",
							e);
				}
			}

			parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));
			parameters.put("actNo", ackNo != null ? String.valueOf(ackNo) : "");
			parameters.put("actDate",
					ackDate != null ? String.valueOf(ackDate) : "");
			parameters.put("category",
					b2b.getSupplyType() != null ? b2b.getSupplyType() : "");

			parameters.put("docType", docType.containsKey(b2b.getDocType())
					? docType.get(b2b.getDocType()) : "");

			parameters.put("docNo",
					b2b.getDocNum() != null ? b2b.getDocNum() : "");
			parameters.put("docDate", b2b.getDocDate() != null
					? String.valueOf(formatter2.format(b2b.getDocDate())) : "");

			parameters.put("igstOnInfra", b2b.getSection7OfIGSTFlag() != null
					? b2b.getSection7OfIGSTFlag() : "");

			parameters.put("TotalTaxableAmount",
					b2b.getInvAssessableAmt() != null
							? GenUtil.formatCurrency(b2b.getInvAssessableAmt())
							: "0.00");
			parameters.put("TotalCgstAmount", b2b.getInvCgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCgstAmt()) : "0.00");
			parameters.put("TotalSgstAmount", b2b.getInvSgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvSgstAmt()) : "0.00");
			parameters.put("TotalIgstAmount", b2b.getInvIgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvIgstAmt()) : "0.00");
			parameters.put("TotalCessAmount", b2b.getInvCessAdvaloremAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCessAdvaloremAmt()) : "0.00");
			parameters.put("TotalStateCessAmount",
					b2b.getInvCessAdvaloremAmt() != null
							? GenUtil
									.formatCurrency(b2b.getInvCessAdvaloremAmt()
											.add(b2b.getInvCessSpecificAmt()))
							: "0.00");
			parameters.put("OtherCharges",
					b2b.getInvOtherCharges() != null
							? GenUtil.formatCurrency(b2b.getInvOtherCharges())
							: "0.00");
			parameters.put("RoundOffAmount", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");
			parameters.put("TotalInvoiceAmt", b2b.getInvValue() != null
					? GenUtil.formatCurrency(b2b.getInvValue()) : "0.00");

			LocalDate printedDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("printedDate",
					printedDate != null ? formatter2.format(printedDate) : "");
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

	public JasperPrint generateJasperPrint(GetIrnDexpHeaderEntity b2b,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint) {
		try {

			String irn = b2b.getIrn();

			List<String> gstins = new ArrayList<>();

			if (b2b.getSupplierGSTIN() != null)
				gstins.add(b2b.getSupplierGSTIN());
			if (b2b.getCustomerGSTIN() != null)
				gstins.add(b2b.getCustomerGSTIN());
			if (b2b.getShipToGSTIN() != null)
				gstins.add(b2b.getShipToGSTIN());
			String supplierAddress = (b2b.getSupplierAddress1() != null
					? b2b.getSupplierAddress1() + "," : "")
					+ (b2b.getSupplierAddress2() != null
							? b2b.getSupplierAddress2() + "," : "")
					+ (b2b.getSupplierLocation() != null
							? b2b.getSupplierLocation() : "");

			parameters.put("SupplierAddress", supplierAddress);
			String supplierName = (b2b.getSupplierLegalName() != null
					? b2b.getSupplierLegalName() : "");
			parameters.put("SupplierName", supplierName);
			String stateName = statecodeRepository.findStateNameByCode(
					b2b.getSupplierGSTIN().substring(0, 2));
			String placeOfSupply1 = b2b.getSupplierGSTIN().substring(0, 2) + "-"
					+ stateName;
			if (stateName == null) {
				placeOfSupply1 = b2b.getSupplierGSTIN().substring(0, 2);
			}

			String supplierState = (b2b.getSupplierPincode() != null
					? placeOfSupply1 + "," + b2b.getSupplierPincode().toString()
					: "");
			parameters.put("SupplierStateCodeNamePincode", supplierState);

			String customerAddress = (b2b.getCustomerAddress1() != null
					? b2b.getCustomerAddress1() + "," : "")
					+ (b2b.getCustomerAddress2() != null
							? b2b.getCustomerAddress2() + "," : "")
					+ (b2b.getCustomerLocation() != null
							? b2b.getCustomerLocation() : "");

			parameters.put("CustomerAddress", customerAddress);
			String customerName = (b2b.getCustomerLegalName() != null
					? b2b.getCustomerLegalName() : "");

			parameters.put("CustomerName", customerName);
			String custstateName = "";
			if (b2b.getCustomerStateCode().toString() != null) {
				custstateName = statecodeRepository.findStateNameByCode(
						b2b.getCustomerStateCode().toString());
			}

			String placeOfSupply4 = "";
			String customerState = "";
			if (b2b.getShipToStateCode() != null) {
				placeOfSupply4 = b2b.getShipToStateCode() + "-" + custstateName;

			}
			customerState = (b2b.getCustomerPincode() != null
					? placeOfSupply4 + "," + b2b.getCustomerPincode().toString()
					: "");
			parameters.put("CustomerStateCodeNamePincode", customerState);

			String shiptoAddress = (b2b.getShipToAddress1() != null
					? b2b.getShipToAddress1() + "," : "")
					+ (b2b.getShipToAddress2() != null
							? b2b.getShipToAddress2() + "," : "")
					+ (b2b.getShipToLocation() != null ? b2b.getShipToLocation()
							: "");
			if (Strings.isNullOrEmpty(shiptoAddress)) {
				shiptoAddress = customerAddress;
			}

			String dispatcherAddress = (b2b.getDispatcherAddress1() != null
					? b2b.getDispatcherAddress1() + "," : "")
					+ (b2b.getDispatcherAddress2() != null
							? b2b.getDispatcherAddress2() + "," : "")
					+ (b2b.getDispatcherLocation() != null
							? b2b.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress;
			}

			List<GetIrnDexpItemEntity> lineItems = getIrnDexpLineRepo
					.findByHeaderId(b2b.getId());
			parameters.put("SGSTIN",
					(!Strings.isNullOrEmpty(b2b.getSupplierGSTIN()))
							? b2b.getSupplierGSTIN() : "");

			parameters.put("irn", b2b.getIrn() != null ? b2b.getIrn() : "N/A");

			String custgstin = (b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("customergstin", custgstin);

			String placeOfSupply = "";
			if (b2b.getBillingPOS() != null) {
				String stateName1 = statecodeRepository
						.findStateNameByCode(b2b.getBillingPOS());
				placeOfSupply = stateName1 + "(" + b2b.getBillingPOS() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);

			parameters.put("SupplierAdd",
					(b2b.getSupplierAddress1() != null
							? b2b.getSupplierAddress1() + "\n" : "")
							+ (b2b.getSupplierAddress2() != null
									? b2b.getSupplierAddress2() + "\n" : "")
							+ (b2b.getSupplierLocation() != null
									? b2b.getSupplierLocation() : ""));

			parameters.put("CustomerGSTIN", b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("CustomerAdd",
					(b2b.getCustomerAddress1() != null
							? b2b.getCustomerAddress1() + "\n" : "")
							+ (b2b.getCustomerAddress2() != null
									? b2b.getCustomerAddress2() + "\n" : "")
							+ (b2b.getCustomerLocation() != null
									? b2b.getCustomerLocation() : ""));

			List<GoodsProductDetails> eInvoiceProductDetails = new ArrayList<>();
			int slNo = 1;
			BigDecimal ttl = new BigDecimal("0.00");
			BigDecimal tt2 = new BigDecimal("0.00");
			BigDecimal tt4 = new BigDecimal("0.00");
			for (GetIrnDexpItemEntity line : lineItems) {
				GoodsProductDetails productDetails = new GoodsProductDetails();
				productDetails.setSlNo(String.valueOf(slNo));
				productDetails.setPrdname(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails.setPrddesc(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails
						.setHsn(line.getHsn() != null ? line.getHsn() : "");
				if (line.getQuantity() == null) {
					productDetails.setQuantity(line.getQuantity() != null
							? line.getQuantity().toString() : "0");
				} else {
					productDetails.setQuantity(line.getQuantity().toString());
				}
				productDetails.setUnit(line.getUnit() != null
						? line.getUnit().toString()
						: "");
				productDetails.setUnitprice(line.getUnitPrice() != null
						? GenUtil.formatCurrency(line.getUnitPrice())
						: AMOUNT_STRING);

				productDetails.setItemdiscount(line.getItemDiscount() != null
						? GenUtil.formatCurrency(line.getItemDiscount())
						: AMOUNT_STRING);
				if (line.getItemDiscount() != null) {
					tt4 = tt4.add(line.getItemDiscount());
				}
				productDetails.setItemassessableamount(
						line.getItemAssessableAmt() != null
								? GenUtil.formatCurrency(
										line.getItemAssessableAmt())
								: AMOUNT_STRING);
				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				productDetails.setIgstrate(String.valueOf(igstRate));
				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				productDetails.setCgstrate(String.valueOf(cgstRate));
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				productDetails.setSgstrate(String.valueOf(sgstRate));
				BigDecimal cessRate = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				cessRate = cessRate.setScale(2, BigDecimal.ROUND_HALF_UP);

				productDetails.setCessadvlrate(String.format("%.2f", cessRate));
				BigDecimal stateCessRate = line
						.getStateCessAdvaloremRate() != null
								? line.getStateCessAdvaloremRate()
								: BigDecimal.ZERO;
				stateCessRate = stateCessRate.setScale(2,
						BigDecimal.ROUND_HALF_UP);

				productDetails.setTaxrate(String.format("%.2f", stateCessRate));

				productDetails.setItemotherchargesFormatted(
						line.getItemOtherCharges() != null
								? GenUtil.formatCurrency(
										line.getItemOtherCharges())
								: AMOUNT_STRING);
				if (line.getItemOtherCharges() != null) {
					tt2 = tt2.add(line.getItemOtherCharges());
				}
				productDetails.setTotalitemValFormatted(
						line.getTotalItemAmount() != null
								? GenUtil.formatCurrency(
										line.getTotalItemAmount())
								: AMOUNT_STRING);
				if (line.getTotalItemAmount() != null) {
					ttl = ttl.add(line.getTotalItemAmount());
				}
				eInvoiceProductDetails.add(productDetails);
				slNo++;
			}
			parameters.put("ItemDetails",
					new JRBeanCollectionDataSource(eInvoiceProductDetails));

			parameters.put("RoundOffValue", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");

			String signedqr = "";
			String ackNo = "";
			String ackDate = "";
			GetIrnDetailPayloadEntity getSignedEntity = getIrnDtlPayloadRepository
					.findByIrnAndIrnStatus(irn, b2b.getIrnStatus());

			if (getSignedEntity != null
					&& getSignedEntity.getPayload() != null) {
				Clob payloadClob = getSignedEntity.getPayload();
				try (Reader reader = payloadClob.getCharacterStream()) {
					StringBuilder payloadStringBuilder = new StringBuilder();
					char[] buffer = new char[1024];
					int bytesRead;
					while ((bytesRead = reader.read(buffer)) != -1) {
						payloadStringBuilder.append(buffer, 0, bytesRead);
					}
					String payloadString = payloadStringBuilder.toString();

					// Parse the JSON to get the SignedQRCode
					JsonObject payloadJson = JsonParser
							.parseString(payloadString).getAsJsonObject();
					ackNo = payloadJson.getAsJsonPrimitive("AckNo")
							.getAsString();
					ackDate = payloadJson.getAsJsonPrimitive("AckDt")
							.getAsString();
					if (payloadJson.has("SignedQRCode")) {
						signedqr = payloadJson
								.getAsJsonPrimitive("SignedQRCode")
								.getAsString();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Signed QR {}", signedqr);
						}
					} else {
						// Handle the case where the key is not present
						LOGGER.warn(
								"Key 'SignedQRCode' not found in the JSON object.");
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Signed QR {}, ackNo {}, ackDate {}",
								signedqr, ackNo, ackDate);
					}

				} catch (IOException | SQLException e) {
					LOGGER.error(
							"Exception occured while getting the signed QR from clob.",
							e);
				}
			}

			parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));
			parameters.put("actNo", ackNo != null ? String.valueOf(ackNo) : "");
			parameters.put("actDate",
					ackDate != null ? String.valueOf(ackDate) : "");
			parameters.put("category",
					b2b.getSupplyType() != null ? b2b.getSupplyType() : "");
			parameters.put("docType", docType.containsKey(b2b.getDocType())
					? docType.get(b2b.getDocType()) : "");

			parameters.put("docNo",
					b2b.getDocNum() != null ? b2b.getDocNum() : "");
			parameters.put("docDate", b2b.getDocDate() != null
					? String.valueOf(formatter2.format(b2b.getDocDate())) : "");

			parameters.put("igstOnInfra", b2b.getSection7OfIGSTFlag() != null
					? b2b.getSection7OfIGSTFlag() : "");

			parameters.put("TotalTaxableAmount",
					b2b.getInvAssessableAmt() != null
							? GenUtil.formatCurrency(b2b.getInvAssessableAmt())
							: "0.00");
			parameters.put("TotalCgstAmount", b2b.getInvCgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCgstAmt()) : "0.00");
			parameters.put("TotalSgstAmount", b2b.getInvSgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvSgstAmt()) : "0.00");
			parameters.put("TotalIgstAmount", b2b.getInvIgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvIgstAmt()) : "0.00");
			parameters.put("TotalCessAmount", b2b.getInvCessAdvaloremAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCessAdvaloremAmt()) : "0.00");
			parameters.put("TotalStateCessAmount",
					b2b.getInvCessAdvaloremAmt() != null
							? GenUtil
									.formatCurrency(b2b.getInvCessAdvaloremAmt()
											.add(b2b.getInvCessSpecificAmt()))
							: "0.00");
			parameters.put("OtherCharges",
					b2b.getInvOtherCharges() != null
							? GenUtil.formatCurrency(b2b.getInvOtherCharges())
							: "0.00");
			parameters.put("RoundOffAmount", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");
			parameters.put("TotalInvoiceAmt", b2b.getInvValue() != null
					? GenUtil.formatCurrency(b2b.getInvValue()) : "0.00");

			LocalDate printedDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("printedDate",
					printedDate != null ? formatter2.format(printedDate) : "");
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

	public JasperPrint generateJasperPrint(GetIrnExpWopHeaderEntity b2b,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint) {
		try {

			String irn = b2b.getIrn();

			List<String> gstins = new ArrayList<>();

			if (b2b.getSupplierGSTIN() != null)
				gstins.add(b2b.getSupplierGSTIN());
			if (b2b.getCustomerGSTIN() != null)
				gstins.add(b2b.getCustomerGSTIN());
			if (b2b.getShipToGSTIN() != null)
				gstins.add(b2b.getShipToGSTIN());
			String supplierAddress = (b2b.getSupplierAddress1() != null
					? b2b.getSupplierAddress1() + "," : "")
					+ (b2b.getSupplierAddress2() != null
							? b2b.getSupplierAddress2() + "," : "")
					+ (b2b.getSupplierLocation() != null
							? b2b.getSupplierLocation() : "");

			parameters.put("SupplierAddress", supplierAddress);
			String supplierName = (b2b.getSupplierLegalName() != null
					? b2b.getSupplierLegalName() : "");
			parameters.put("SupplierName", supplierName);
			String stateName = statecodeRepository.findStateNameByCode(
					b2b.getSupplierGSTIN().substring(0, 2));
			String placeOfSupply1 = b2b.getSupplierGSTIN().substring(0, 2) + "-"
					+ stateName;
			if (stateName == null) {
				placeOfSupply1 = b2b.getSupplierGSTIN().substring(0, 2);
			}

			String supplierState = (b2b.getSupplierPincode() != null
					? placeOfSupply1 + "," + b2b.getSupplierPincode().toString()
					: "");
			parameters.put("SupplierStateCodeNamePincode", supplierState);

			String customerAddress = (b2b.getCustomerAddress1() != null
					? b2b.getCustomerAddress1() + "," : "")
					+ (b2b.getCustomerAddress2() != null
							? b2b.getCustomerAddress2() + "," : "")
					+ (b2b.getCustomerLocation() != null
							? b2b.getCustomerLocation() : "");

			parameters.put("CustomerAddress", customerAddress);
			String customerName = (b2b.getCustomerLegalName() != null
					? b2b.getCustomerLegalName() : "");

			parameters.put("CustomerName", customerName);
			String custstateName = "";
			if (b2b.getCustomerStateCode().toString() != null) {
				custstateName = statecodeRepository.findStateNameByCode(
						b2b.getCustomerStateCode().toString());
			}

			String placeOfSupply4 = "";
			String customerState = "";
			if (b2b.getShipToStateCode() != null) {
				placeOfSupply4 = b2b.getShipToStateCode() + "-" + custstateName;

			}
			customerState = (b2b.getCustomerPincode() != null
					? placeOfSupply4 + "," + b2b.getCustomerPincode().toString()
					: "");
			parameters.put("CustomerStateCodeNamePincode", customerState);

			String shiptoAddress = (b2b.getShipToAddress1() != null
					? b2b.getShipToAddress1() + "," : "")
					+ (b2b.getShipToAddress2() != null
							? b2b.getShipToAddress2() + "," : "")
					+ (b2b.getShipToLocation() != null ? b2b.getShipToLocation()
							: "");
			if (Strings.isNullOrEmpty(shiptoAddress)) {
				shiptoAddress = customerAddress;
			}

			String dispatcherAddress = (b2b.getDispatcherAddress1() != null
					? b2b.getDispatcherAddress1() + "," : "")
					+ (b2b.getDispatcherAddress2() != null
							? b2b.getDispatcherAddress2() + "," : "")
					+ (b2b.getDispatcherLocation() != null
							? b2b.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress;
			}

			List<GetIrnExpWopItemEntity> lineItems = getIrnExpWopLineRepo
					.findByHeaderId(b2b.getId());
			parameters.put("SGSTIN",
					(!Strings.isNullOrEmpty(b2b.getSupplierGSTIN()))
							? b2b.getSupplierGSTIN() : "");

			parameters.put("irn", b2b.getIrn() != null ? b2b.getIrn() : "N/A");

			String custgstin = (b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("customergstin", custgstin);

			String placeOfSupply = "";
			if (b2b.getBillingPOS() != null) {
				String stateName1 = statecodeRepository
						.findStateNameByCode(b2b.getBillingPOS());
				placeOfSupply = stateName1 + "(" + b2b.getBillingPOS() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);

			parameters.put("SupplierAdd",
					(b2b.getSupplierAddress1() != null
							? b2b.getSupplierAddress1() + "\n" : "")
							+ (b2b.getSupplierAddress2() != null
									? b2b.getSupplierAddress2() + "\n" : "")
							+ (b2b.getSupplierLocation() != null
									? b2b.getSupplierLocation() : ""));

			parameters.put("CustomerGSTIN", b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("CustomerAdd",
					(b2b.getCustomerAddress1() != null
							? b2b.getCustomerAddress1() + "\n" : "")
							+ (b2b.getCustomerAddress2() != null
									? b2b.getCustomerAddress2() + "\n" : "")
							+ (b2b.getCustomerLocation() != null
									? b2b.getCustomerLocation() : ""));

			List<GoodsProductDetails> eInvoiceProductDetails = new ArrayList<>();
			int slNo = 1;
			BigDecimal ttl = new BigDecimal("0.00");
			BigDecimal tt2 = new BigDecimal("0.00");
			BigDecimal tt4 = new BigDecimal("0.00");
			for (GetIrnExpWopItemEntity line : lineItems) {
				GoodsProductDetails productDetails = new GoodsProductDetails();
				productDetails.setSlNo(String.valueOf(slNo));
				productDetails.setPrdname(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails.setPrddesc(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails
						.setHsn(line.getHsn() != null ? line.getHsn() : "");
				if (line.getQuantity() == null) {
					productDetails.setQuantity(line.getQuantity() != null
							? line.getQuantity().toString() : "0");
				} else {
					productDetails.setQuantity(line.getQuantity().toString());
				}
				productDetails.setUnit(line.getUnit() != null
						? line.getUnit().toString()
						: "");
				productDetails.setUnitprice(line.getUnitPrice() != null
						? GenUtil.formatCurrency(line.getUnitPrice())
						: AMOUNT_STRING);

				productDetails.setItemdiscount(line.getItemDiscount() != null
						? GenUtil.formatCurrency(line.getItemDiscount())
						: AMOUNT_STRING);
				if (line.getItemDiscount() != null) {
					tt4 = tt4.add(line.getItemDiscount());
				}
				productDetails.setItemassessableamount(
						line.getItemAssessableAmt() != null
								? GenUtil.formatCurrency(
										line.getItemAssessableAmt())
								: AMOUNT_STRING);
				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				productDetails.setIgstrate(String.valueOf(igstRate));
				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				productDetails.setCgstrate(String.valueOf(cgstRate));
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				productDetails.setSgstrate(String.valueOf(sgstRate));
				BigDecimal cessRate = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				cessRate = cessRate.setScale(2, BigDecimal.ROUND_HALF_UP);

				productDetails.setCessadvlrate(String.format("%.2f", cessRate));
				BigDecimal stateCessRate = line
						.getStateCessAdvaloremRate() != null
								? line.getStateCessAdvaloremRate()
								: BigDecimal.ZERO;
				stateCessRate = stateCessRate.setScale(2,
						BigDecimal.ROUND_HALF_UP);

				productDetails.setTaxrate(String.format("%.2f", stateCessRate));

				productDetails.setItemotherchargesFormatted(
						line.getItemOtherCharges() != null
								? GenUtil.formatCurrency(
										line.getItemOtherCharges())
								: AMOUNT_STRING);
				if (line.getItemOtherCharges() != null) {
					tt2 = tt2.add(line.getItemOtherCharges());
				}
				productDetails.setTotalitemValFormatted(
						line.getTotalItemAmount() != null
								? GenUtil.formatCurrency(
										line.getTotalItemAmount())
								: AMOUNT_STRING);
				if (line.getTotalItemAmount() != null) {
					ttl = ttl.add(line.getTotalItemAmount());
				}
				eInvoiceProductDetails.add(productDetails);
				slNo++;
			}
			parameters.put("ItemDetails",
					new JRBeanCollectionDataSource(eInvoiceProductDetails));

			parameters.put("RoundOffValue", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");

			String signedqr = "";
			String ackNo = "";
			String ackDate = "";
			GetIrnDetailPayloadEntity getSignedEntity = getIrnDtlPayloadRepository
					.findByIrnAndIrnStatus(irn, b2b.getIrnStatus());

			if (getSignedEntity != null
					&& getSignedEntity.getPayload() != null) {
				Clob payloadClob = getSignedEntity.getPayload();
				try (Reader reader = payloadClob.getCharacterStream()) {
					StringBuilder payloadStringBuilder = new StringBuilder();
					char[] buffer = new char[1024];
					int bytesRead;
					while ((bytesRead = reader.read(buffer)) != -1) {
						payloadStringBuilder.append(buffer, 0, bytesRead);
					}
					String payloadString = payloadStringBuilder.toString();

					// Parse the JSON to get the SignedQRCode
					JsonObject payloadJson = JsonParser
							.parseString(payloadString).getAsJsonObject();
					ackNo = payloadJson.getAsJsonPrimitive("AckNo")
							.getAsString();
					ackDate = payloadJson.getAsJsonPrimitive("AckDt")
							.getAsString();
					if (payloadJson.has("SignedQRCode")) {
						signedqr = payloadJson
								.getAsJsonPrimitive("SignedQRCode")
								.getAsString();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Signed QR {}", signedqr);
						}
					} else {
						// Handle the case where the key is not present
						LOGGER.warn(
								"Key 'SignedQRCode' not found in the JSON object.");
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Signed QR {}, ackNo {}, ackDate {}",
								signedqr, ackNo, ackDate);
					}

				} catch (IOException | SQLException e) {
					LOGGER.error(
							"Exception occured while getting the signed QR from clob.",
							e);
				}
			}

			parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));
			parameters.put("actNo", ackNo != null ? String.valueOf(ackNo) : "");
			parameters.put("actDate",
					ackDate != null ? String.valueOf(ackDate) : "");
			parameters.put("category",
					b2b.getSupplyType() != null ? b2b.getSupplyType() : "");
			parameters.put("docType", docType.containsKey(b2b.getDocType())
					? docType.get(b2b.getDocType()) : "");

			parameters.put("docNo",
					b2b.getDocNum() != null ? b2b.getDocNum() : "");
			parameters.put("docDate", b2b.getDocDate() != null
					? String.valueOf(formatter2.format(b2b.getDocDate())) : "");

			parameters.put("igstOnInfra", b2b.getSection7OfIGSTFlag() != null
					? b2b.getSection7OfIGSTFlag() : "");

			parameters.put("TotalTaxableAmount",
					b2b.getInvAssessableAmt() != null
							? GenUtil.formatCurrency(b2b.getInvAssessableAmt())
							: "0.00");
			parameters.put("TotalCgstAmount", b2b.getInvCgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCgstAmt()) : "0.00");
			parameters.put("TotalSgstAmount", b2b.getInvSgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvSgstAmt()) : "0.00");
			parameters.put("TotalIgstAmount", b2b.getInvIgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvIgstAmt()) : "0.00");
			parameters.put("TotalCessAmount", b2b.getInvCessAdvaloremAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCessAdvaloremAmt()) : "0.00");
			parameters.put("TotalStateCessAmount",
					b2b.getInvCessAdvaloremAmt() != null
							? GenUtil
									.formatCurrency(b2b.getInvCessAdvaloremAmt()
											.add(b2b.getInvCessSpecificAmt()))
							: "0.00");
			parameters.put("OtherCharges",
					b2b.getInvOtherCharges() != null
							? GenUtil.formatCurrency(b2b.getInvOtherCharges())
							: "0.00");
			parameters.put("RoundOffAmount", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");
			parameters.put("TotalInvoiceAmt", b2b.getInvValue() != null
					? GenUtil.formatCurrency(b2b.getInvValue()) : "0.00");

			LocalDate printedDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("printedDate",
					printedDate != null ? formatter2.format(printedDate) : "");
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

	public JasperPrint generateJasperPrint(GetIrnExpWpHeaderEntity b2b,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint) {
		try {

			String irn = b2b.getIrn();

			List<String> gstins = new ArrayList<>();

			if (b2b.getSupplierGSTIN() != null)
				gstins.add(b2b.getSupplierGSTIN());
			if (b2b.getCustomerGSTIN() != null)
				gstins.add(b2b.getCustomerGSTIN());
			if (b2b.getShipToGSTIN() != null)
				gstins.add(b2b.getShipToGSTIN());
			String supplierAddress = (b2b.getSupplierAddress1() != null
					? b2b.getSupplierAddress1() + "," : "")
					+ (b2b.getSupplierAddress2() != null
							? b2b.getSupplierAddress2() + "," : "")
					+ (b2b.getSupplierLocation() != null
							? b2b.getSupplierLocation() : "");

			parameters.put("SupplierAddress", supplierAddress);
			String supplierName = (b2b.getSupplierLegalName() != null
					? b2b.getSupplierLegalName() : "");
			parameters.put("SupplierName", supplierName);
			String stateName = statecodeRepository.findStateNameByCode(
					b2b.getSupplierGSTIN().substring(0, 2));
			String placeOfSupply1 = b2b.getSupplierGSTIN().substring(0, 2) + "-"
					+ stateName;
			if (stateName == null) {
				placeOfSupply1 = b2b.getSupplierGSTIN().substring(0, 2);
			}

			String supplierState = (b2b.getSupplierPincode() != null
					? placeOfSupply1 + "," + b2b.getSupplierPincode().toString()
					: "");
			parameters.put("SupplierStateCodeNamePincode", supplierState);

			String customerAddress = (b2b.getCustomerAddress1() != null
					? b2b.getCustomerAddress1() + "," : "")
					+ (b2b.getCustomerAddress2() != null
							? b2b.getCustomerAddress2() + "," : "")
					+ (b2b.getCustomerLocation() != null
							? b2b.getCustomerLocation() : "");

			parameters.put("CustomerAddress", customerAddress);
			String customerName = (b2b.getCustomerLegalName() != null
					? b2b.getCustomerLegalName() : "");

			parameters.put("CustomerName", customerName);
			String custstateName = "";
			if (b2b.getCustomerStateCode().toString() != null) {
				custstateName = statecodeRepository.findStateNameByCode(
						b2b.getCustomerStateCode().toString());
			}

			String placeOfSupply4 = "";
			String customerState = "";
			if (b2b.getShipToStateCode() != null) {
				placeOfSupply4 = b2b.getShipToStateCode() + "-" + custstateName;

			}
			customerState = (b2b.getCustomerPincode() != null
					? placeOfSupply4 + "," + b2b.getCustomerPincode().toString()
					: "");
			parameters.put("CustomerStateCodeNamePincode", customerState);

			String shiptoAddress = (b2b.getShipToAddress1() != null
					? b2b.getShipToAddress1() + "," : "")
					+ (b2b.getShipToAddress2() != null
							? b2b.getShipToAddress2() + "," : "")
					+ (b2b.getShipToLocation() != null ? b2b.getShipToLocation()
							: "");
			if (Strings.isNullOrEmpty(shiptoAddress)) {
				shiptoAddress = customerAddress;
			}

			String dispatcherAddress = (b2b.getDispatcherAddress1() != null
					? b2b.getDispatcherAddress1() + "," : "")
					+ (b2b.getDispatcherAddress2() != null
							? b2b.getDispatcherAddress2() + "," : "")
					+ (b2b.getDispatcherLocation() != null
							? b2b.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress;
			}

			List<GetIrnExpWpItemEntity> lineItems = getIrnExpWpLineRepo
					.findByHeaderId(b2b.getId());
			parameters.put("SGSTIN",
					(!Strings.isNullOrEmpty(b2b.getSupplierGSTIN()))
							? b2b.getSupplierGSTIN() : "");

			parameters.put("irn", b2b.getIrn() != null ? b2b.getIrn() : "N/A");

			String custgstin = (b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("customergstin", custgstin);

			String placeOfSupply = "";
			if (b2b.getBillingPOS() != null) {
				String stateName1 = statecodeRepository
						.findStateNameByCode(b2b.getBillingPOS());
				placeOfSupply = stateName1 + "(" + b2b.getBillingPOS() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);

			parameters.put("SupplierAdd",
					(b2b.getSupplierAddress1() != null
							? b2b.getSupplierAddress1() + "\n" : "")
							+ (b2b.getSupplierAddress2() != null
									? b2b.getSupplierAddress2() + "\n" : "")
							+ (b2b.getSupplierLocation() != null
									? b2b.getSupplierLocation() : ""));

			parameters.put("CustomerGSTIN", b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("CustomerAdd",
					(b2b.getCustomerAddress1() != null
							? b2b.getCustomerAddress1() + "\n" : "")
							+ (b2b.getCustomerAddress2() != null
									? b2b.getCustomerAddress2() + "\n" : "")
							+ (b2b.getCustomerLocation() != null
									? b2b.getCustomerLocation() : ""));

			List<GoodsProductDetails> eInvoiceProductDetails = new ArrayList<>();
			int slNo = 1;
			BigDecimal ttl = new BigDecimal("0.00");
			BigDecimal tt2 = new BigDecimal("0.00");
			BigDecimal tt4 = new BigDecimal("0.00");
			for (GetIrnExpWpItemEntity line : lineItems) {
				GoodsProductDetails productDetails = new GoodsProductDetails();
				productDetails.setSlNo(String.valueOf(slNo));
				productDetails.setPrdname(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails.setPrddesc(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails
						.setHsn(line.getHsn() != null ? line.getHsn() : "");
				if (line.getQuantity() == null) {
					productDetails.setQuantity(line.getQuantity() != null
							? line.getQuantity().toString() : "0");
				} else {
					productDetails.setQuantity(line.getQuantity().toString());
				}
				productDetails.setUnit(line.getUnit() != null
						? line.getUnit().toString()
						: "");
				productDetails.setUnitprice(line.getUnitPrice() != null
						? GenUtil.formatCurrency(line.getUnitPrice())
						: AMOUNT_STRING);

				productDetails.setItemdiscount(line.getItemDiscount() != null
						? GenUtil.formatCurrency(line.getItemDiscount())
						: AMOUNT_STRING);
				if (line.getItemDiscount() != null) {
					tt4 = tt4.add(line.getItemDiscount());
				}
				productDetails.setItemassessableamount(
						line.getItemAssessableAmt() != null
								? GenUtil.formatCurrency(
										line.getItemAssessableAmt())
								: AMOUNT_STRING);
				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				productDetails.setIgstrate(String.valueOf(igstRate));
				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				productDetails.setCgstrate(String.valueOf(cgstRate));
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				productDetails.setSgstrate(String.valueOf(sgstRate));
				BigDecimal cessRate = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				cessRate = cessRate.setScale(2, BigDecimal.ROUND_HALF_UP);

				productDetails.setCessadvlrate(String.format("%.2f", cessRate));
				BigDecimal stateCessRate = line
						.getStateCessAdvaloremRate() != null
								? line.getStateCessAdvaloremRate()
								: BigDecimal.ZERO;
				stateCessRate = stateCessRate.setScale(2,
						BigDecimal.ROUND_HALF_UP);

				productDetails.setTaxrate(String.format("%.2f", stateCessRate));

				productDetails.setItemotherchargesFormatted(
						line.getItemOtherCharges() != null
								? GenUtil.formatCurrency(
										line.getItemOtherCharges())
								: AMOUNT_STRING);
				if (line.getItemOtherCharges() != null) {
					tt2 = tt2.add(line.getItemOtherCharges());
				}
				productDetails.setTotalitemValFormatted(
						line.getTotalItemAmount() != null
								? GenUtil.formatCurrency(
										line.getTotalItemAmount())
								: AMOUNT_STRING);
				if (line.getTotalItemAmount() != null) {
					ttl = ttl.add(line.getTotalItemAmount());
				}
				eInvoiceProductDetails.add(productDetails);
				slNo++;
			}
			parameters.put("ItemDetails",
					new JRBeanCollectionDataSource(eInvoiceProductDetails));

			parameters.put("RoundOffValue", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");

			String signedqr = "";
			String ackNo = "";
			String ackDate = "";
			GetIrnDetailPayloadEntity getSignedEntity = getIrnDtlPayloadRepository
					.findByIrnAndIrnStatus(irn, b2b.getIrnStatus());

			if (getSignedEntity != null
					&& getSignedEntity.getPayload() != null) {
				Clob payloadClob = getSignedEntity.getPayload();
				try (Reader reader = payloadClob.getCharacterStream()) {
					StringBuilder payloadStringBuilder = new StringBuilder();
					char[] buffer = new char[1024];
					int bytesRead;
					while ((bytesRead = reader.read(buffer)) != -1) {
						payloadStringBuilder.append(buffer, 0, bytesRead);
					}
					String payloadString = payloadStringBuilder.toString();

					// Parse the JSON to get the SignedQRCode
					JsonObject payloadJson = JsonParser
							.parseString(payloadString).getAsJsonObject();
					ackNo = payloadJson.getAsJsonPrimitive("AckNo")
							.getAsString();
					ackDate = payloadJson.getAsJsonPrimitive("AckDt")
							.getAsString();
					if (payloadJson.has("SignedQRCode")) {
						signedqr = payloadJson
								.getAsJsonPrimitive("SignedQRCode")
								.getAsString();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Signed QR {}", signedqr);
						}
					} else {
						// Handle the case where the key is not present
						LOGGER.warn(
								"Key 'SignedQRCode' not found in the JSON object.");
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Signed QR {}, ackNo {}, ackDate {}",
								signedqr, ackNo, ackDate);
					}

				} catch (IOException | SQLException e) {
					LOGGER.error(
							"Exception occured while getting the signed QR from clob.",
							e);
				}
			}

			parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));
			parameters.put("actNo", ackNo != null ? String.valueOf(ackNo) : "");
			parameters.put("actDate",
					ackDate != null ? String.valueOf(ackDate) : "");
			parameters.put("category",
					b2b.getSupplyType() != null ? b2b.getSupplyType() : "");
			parameters.put("docType", docType.containsKey(b2b.getDocType())
					? docType.get(b2b.getDocType()) : "");

			parameters.put("docNo",
					b2b.getDocNum() != null ? b2b.getDocNum() : "");
			parameters.put("docDate", b2b.getDocDate() != null
					? String.valueOf(formatter2.format(b2b.getDocDate())) : "");

			parameters.put("igstOnInfra", b2b.getSection7OfIGSTFlag() != null
					? b2b.getSection7OfIGSTFlag() : "");

			parameters.put("TotalTaxableAmount",
					b2b.getInvAssessableAmt() != null
							? GenUtil.formatCurrency(b2b.getInvAssessableAmt())
							: "0.00");
			parameters.put("TotalCgstAmount", b2b.getInvCgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCgstAmt()) : "0.00");
			parameters.put("TotalSgstAmount", b2b.getInvSgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvSgstAmt()) : "0.00");
			parameters.put("TotalIgstAmount", b2b.getInvIgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvIgstAmt()) : "0.00");
			parameters.put("TotalCessAmount", b2b.getInvCessAdvaloremAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCessAdvaloremAmt()) : "0.00");
			parameters.put("TotalStateCessAmount",
					b2b.getInvCessAdvaloremAmt() != null
							? GenUtil
									.formatCurrency(b2b.getInvCessAdvaloremAmt()
											.add(b2b.getInvCessSpecificAmt()))
							: "0.00");
			parameters.put("OtherCharges",
					b2b.getInvOtherCharges() != null
							? GenUtil.formatCurrency(b2b.getInvOtherCharges())
							: "0.00");
			parameters.put("RoundOffAmount", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");
			parameters.put("TotalInvoiceAmt", b2b.getInvValue() != null
					? GenUtil.formatCurrency(b2b.getInvValue()) : "0.00");

			LocalDate printedDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("printedDate",
					printedDate != null ? formatter2.format(printedDate) : "");
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


	public JasperPrint generateJasperPrint(GetIrnSezWopHeaderEntity b2b,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint) {
		try {

			String irn = b2b.getIrn();

			List<String> gstins = new ArrayList<>();

			if (b2b.getSupplierGSTIN() != null)
				gstins.add(b2b.getSupplierGSTIN());
			if (b2b.getCustomerGSTIN() != null)
				gstins.add(b2b.getCustomerGSTIN());
			if (b2b.getShipToGSTIN() != null)
				gstins.add(b2b.getShipToGSTIN());
			String supplierAddress = (b2b.getSupplierAddress1() != null
					? b2b.getSupplierAddress1() + "," : "")
					+ (b2b.getSupplierAddress2() != null
							? b2b.getSupplierAddress2() + "," : "")
					+ (b2b.getSupplierLocation() != null
							? b2b.getSupplierLocation() : "");

			parameters.put("SupplierAddress", supplierAddress);
			String supplierName = (b2b.getSupplierLegalName() != null
					? b2b.getSupplierLegalName() : "");
			parameters.put("SupplierName", supplierName);
			String stateName = statecodeRepository.findStateNameByCode(
					b2b.getSupplierGSTIN().substring(0, 2));
			String placeOfSupply1 = b2b.getSupplierGSTIN().substring(0, 2) + "-"
					+ stateName;
			if (stateName == null) {
				placeOfSupply1 = b2b.getSupplierGSTIN().substring(0, 2);
			}

			String supplierState = (b2b.getSupplierPincode() != null
					? placeOfSupply1 + "," + b2b.getSupplierPincode().toString()
					: "");
			parameters.put("SupplierStateCodeNamePincode", supplierState);

			String customerAddress = (b2b.getCustomerAddress1() != null
					? b2b.getCustomerAddress1() + "," : "")
					+ (b2b.getCustomerAddress2() != null
							? b2b.getCustomerAddress2() + "," : "")
					+ (b2b.getCustomerLocation() != null
							? b2b.getCustomerLocation() : "");

			parameters.put("CustomerAddress", customerAddress);
			String customerName = (b2b.getCustomerLegalName() != null
					? b2b.getCustomerLegalName() : "");

			parameters.put("CustomerName", customerName);
			String custstateName = "";
			if (b2b.getCustomerStateCode().toString() != null) {
				custstateName = statecodeRepository.findStateNameByCode(
						b2b.getCustomerStateCode().toString());
			}

			String placeOfSupply4 = "";
			String customerState = "";
			if (b2b.getShipToStateCode() != null) {
				placeOfSupply4 = b2b.getShipToStateCode() + "-" + custstateName;

			}
			customerState = (b2b.getCustomerPincode() != null
					? placeOfSupply4 + "," + b2b.getCustomerPincode().toString()
					: "");
			parameters.put("CustomerStateCodeNamePincode", customerState);

			String shiptoAddress = (b2b.getShipToAddress1() != null
					? b2b.getShipToAddress1() + "," : "")
					+ (b2b.getShipToAddress2() != null
							? b2b.getShipToAddress2() + "," : "")
					+ (b2b.getShipToLocation() != null ? b2b.getShipToLocation()
							: "");
			if (Strings.isNullOrEmpty(shiptoAddress)) {
				shiptoAddress = customerAddress;
			}

			String dispatcherAddress = (b2b.getDispatcherAddress1() != null
					? b2b.getDispatcherAddress1() + "," : "")
					+ (b2b.getDispatcherAddress2() != null
							? b2b.getDispatcherAddress2() + "," : "")
					+ (b2b.getDispatcherLocation() != null
							? b2b.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress;
			}

			List<GetIrnSezWopItemEntity> lineItems = getIrnSezwopLineRepo
					.findByHeaderId(b2b.getId());
			parameters.put("SGSTIN",
					(!Strings.isNullOrEmpty(b2b.getSupplierGSTIN()))
							? b2b.getSupplierGSTIN() : "");

			parameters.put("irn", b2b.getIrn() != null ? b2b.getIrn() : "N/A");

			String custgstin = (b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("customergstin", custgstin);

			String placeOfSupply = "";
			if (b2b.getBillingPOS() != null) {
				String stateName1 = statecodeRepository
						.findStateNameByCode(b2b.getBillingPOS());
				placeOfSupply = stateName1 + "(" + b2b.getBillingPOS() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);

			parameters.put("SupplierAdd",
					(b2b.getSupplierAddress1() != null
							? b2b.getSupplierAddress1() + "\n" : "")
							+ (b2b.getSupplierAddress2() != null
									? b2b.getSupplierAddress2() + "\n" : "")
							+ (b2b.getSupplierLocation() != null
									? b2b.getSupplierLocation() : ""));

			parameters.put("CustomerGSTIN", b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("CustomerAdd",
					(b2b.getCustomerAddress1() != null
							? b2b.getCustomerAddress1() + "\n" : "")
							+ (b2b.getCustomerAddress2() != null
									? b2b.getCustomerAddress2() + "\n" : "")
							+ (b2b.getCustomerLocation() != null
									? b2b.getCustomerLocation() : ""));

			List<GoodsProductDetails> eInvoiceProductDetails = new ArrayList<>();
			int slNo = 1;
			BigDecimal ttl = new BigDecimal("0.00");
			BigDecimal tt2 = new BigDecimal("0.00");
			BigDecimal tt4 = new BigDecimal("0.00");
			for (GetIrnSezWopItemEntity line : lineItems) {
				GoodsProductDetails productDetails = new GoodsProductDetails();
				productDetails.setSlNo(String.valueOf(slNo));
				productDetails.setPrdname(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails.setPrddesc(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails
						.setHsn(line.getHsn() != null ? line.getHsn() : "");
				if (line.getQuantity() == null) {
					productDetails.setQuantity(line.getQuantity() != null
							? line.getQuantity().toString() : "0");
				} else {
					productDetails.setQuantity(line.getQuantity().toString());
				}
				productDetails.setUnit(line.getUnit() != null
						? line.getUnit().toString()
						: "");
				productDetails.setUnitprice(line.getUnitPrice() != null
						? GenUtil.formatCurrency(line.getUnitPrice())
						: AMOUNT_STRING);

				productDetails.setItemdiscount(line.getItemDiscount() != null
						? GenUtil.formatCurrency(line.getItemDiscount())
						: AMOUNT_STRING);
				if (line.getItemDiscount() != null) {
					tt4 = tt4.add(line.getItemDiscount());
				}
				productDetails.setItemassessableamount(
						line.getItemAssessableAmt() != null
								? GenUtil.formatCurrency(
										line.getItemAssessableAmt())
								: AMOUNT_STRING);
				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				productDetails.setIgstrate(String.valueOf(igstRate));
				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				productDetails.setCgstrate(String.valueOf(cgstRate));
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				productDetails.setSgstrate(String.valueOf(sgstRate));
				BigDecimal cessRate = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				cessRate = cessRate.setScale(2, BigDecimal.ROUND_HALF_UP);

				productDetails.setCessadvlrate(String.format("%.2f", cessRate));
				BigDecimal stateCessRate = line
						.getStateCessAdvaloremRate() != null
								? line.getStateCessAdvaloremRate()
								: BigDecimal.ZERO;
				stateCessRate = stateCessRate.setScale(2,
						BigDecimal.ROUND_HALF_UP);

				productDetails.setTaxrate(String.format("%.2f", stateCessRate));

				productDetails.setItemotherchargesFormatted(
						line.getItemOtherCharges() != null
								? GenUtil.formatCurrency(
										line.getItemOtherCharges())
								: AMOUNT_STRING);
				if (line.getItemOtherCharges() != null) {
					tt2 = tt2.add(line.getItemOtherCharges());
				}
				productDetails.setTotalitemValFormatted(
						line.getTotalItemAmount() != null
								? GenUtil.formatCurrency(
										line.getTotalItemAmount())
								: AMOUNT_STRING);
				if (line.getTotalItemAmount() != null) {
					ttl = ttl.add(line.getTotalItemAmount());
				}
				eInvoiceProductDetails.add(productDetails);
				slNo++;
			}
			parameters.put("ItemDetails",
					new JRBeanCollectionDataSource(eInvoiceProductDetails));

			parameters.put("RoundOffValue", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");

			String signedqr = "";
			String ackNo = "";
			String ackDate = "";
			GetIrnDetailPayloadEntity getSignedEntity = getIrnDtlPayloadRepository
					.findByIrnAndIrnStatus(irn, b2b.getIrnStatus());

			if (getSignedEntity != null
					&& getSignedEntity.getPayload() != null) {
				Clob payloadClob = getSignedEntity.getPayload();
				try (Reader reader = payloadClob.getCharacterStream()) {
					StringBuilder payloadStringBuilder = new StringBuilder();
					char[] buffer = new char[1024];
					int bytesRead;
					while ((bytesRead = reader.read(buffer)) != -1) {
						payloadStringBuilder.append(buffer, 0, bytesRead);
					}
					String payloadString = payloadStringBuilder.toString();

					// Parse the JSON to get the SignedQRCode
					JsonObject payloadJson = JsonParser
							.parseString(payloadString).getAsJsonObject();
					ackNo = payloadJson.getAsJsonPrimitive("AckNo")
							.getAsString();
					ackDate = payloadJson.getAsJsonPrimitive("AckDt")
							.getAsString();
					if (payloadJson.has("SignedQRCode")) {
						signedqr = payloadJson
								.getAsJsonPrimitive("SignedQRCode")
								.getAsString();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Signed QR {}", signedqr);
						}
					} else {
						// Handle the case where the key is not present
						LOGGER.warn(
								"Key 'SignedQRCode' not found in the JSON object.");
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Signed QR {}, ackNo {}, ackDate {}",
								signedqr, ackNo, ackDate);
					}

				} catch (IOException | SQLException e) {
					LOGGER.error(
							"Exception occured while getting the signed QR from clob.",
							e);
				}
			}

			parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));
			parameters.put("actNo", ackNo != null ? String.valueOf(ackNo) : "");
			parameters.put("actDate",
					ackDate != null ? String.valueOf(ackDate) : "");
			parameters.put("category",
					b2b.getSupplyType() != null ? b2b.getSupplyType() : "");
			parameters.put("docType", docType.containsKey(b2b.getDocType())
					? docType.get(b2b.getDocType()) : "");

			parameters.put("docNo",
					b2b.getDocNum() != null ? b2b.getDocNum() : "");
			parameters.put("docDate", b2b.getDocDate() != null
					? String.valueOf(formatter2.format(b2b.getDocDate())) : "");

			parameters.put("igstOnInfra", b2b.getSection7OfIGSTFlag() != null
					? b2b.getSection7OfIGSTFlag() : "");

			parameters.put("TotalTaxableAmount",
					b2b.getInvAssessableAmt() != null
							? GenUtil.formatCurrency(b2b.getInvAssessableAmt())
							: "0.00");
			parameters.put("TotalCgstAmount", b2b.getInvCgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCgstAmt()) : "0.00");
			parameters.put("TotalSgstAmount", b2b.getInvSgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvSgstAmt()) : "0.00");
			parameters.put("TotalIgstAmount", b2b.getInvIgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvIgstAmt()) : "0.00");
			parameters.put("TotalCessAmount", b2b.getInvCessAdvaloremAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCessAdvaloremAmt()) : "0.00");
			parameters.put("TotalStateCessAmount",
					b2b.getInvCessAdvaloremAmt() != null
							? GenUtil
									.formatCurrency(b2b.getInvCessAdvaloremAmt()
											.add(b2b.getInvCessSpecificAmt()))
							: "0.00");
			parameters.put("OtherCharges",
					b2b.getInvOtherCharges() != null
							? GenUtil.formatCurrency(b2b.getInvOtherCharges())
							: "0.00");
			parameters.put("RoundOffAmount", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");
			parameters.put("TotalInvoiceAmt", b2b.getInvValue() != null
					? GenUtil.formatCurrency(b2b.getInvValue()) : "0.00");

			LocalDate printedDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("printedDate",
					printedDate != null ? formatter2.format(printedDate) : "");
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

	public JasperPrint generateJasperPrint(GetIrnSezWpHeaderEntity b2b,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint) {
		try {

			String irn = b2b.getIrn();

			List<String> gstins = new ArrayList<>();

			if (b2b.getSupplierGSTIN() != null)
				gstins.add(b2b.getSupplierGSTIN());
			if (b2b.getCustomerGSTIN() != null)
				gstins.add(b2b.getCustomerGSTIN());
			if (b2b.getShipToGSTIN() != null)
				gstins.add(b2b.getShipToGSTIN());
			String supplierAddress = (b2b.getSupplierAddress1() != null
					? b2b.getSupplierAddress1() + "," : "")
					+ (b2b.getSupplierAddress2() != null
							? b2b.getSupplierAddress2() + "," : "")
					+ (b2b.getSupplierLocation() != null
							? b2b.getSupplierLocation() : "");

			parameters.put("SupplierAddress", supplierAddress);
			String supplierName = (b2b.getSupplierLegalName() != null
					? b2b.getSupplierLegalName() : "");
			parameters.put("SupplierName", supplierName);
			String stateName = statecodeRepository.findStateNameByCode(
					b2b.getSupplierGSTIN().substring(0, 2));
			String placeOfSupply1 = b2b.getSupplierGSTIN().substring(0, 2) + "-"
					+ stateName;
			if (stateName == null) {
				placeOfSupply1 = b2b.getSupplierGSTIN().substring(0, 2);
			}

			String supplierState = (b2b.getSupplierPincode() != null
					? placeOfSupply1 + "," + b2b.getSupplierPincode().toString()
					: "");
			parameters.put("SupplierStateCodeNamePincode", supplierState);

			String customerAddress = (b2b.getCustomerAddress1() != null
					? b2b.getCustomerAddress1() + "," : "")
					+ (b2b.getCustomerAddress2() != null
							? b2b.getCustomerAddress2() + "," : "")
					+ (b2b.getCustomerLocation() != null
							? b2b.getCustomerLocation() : "");

			parameters.put("CustomerAddress", customerAddress);
			String customerName = (b2b.getCustomerLegalName() != null
					? b2b.getCustomerLegalName() : "");

			parameters.put("CustomerName", customerName);
			String custstateName = "";
			if (b2b.getCustomerStateCode().toString() != null) {
				custstateName = statecodeRepository.findStateNameByCode(
						b2b.getCustomerStateCode().toString());
			}

			String placeOfSupply4 = "";
			String customerState = "";
			if (b2b.getShipToStateCode() != null) {
				placeOfSupply4 = b2b.getShipToStateCode() + "-" + custstateName;

			}
			customerState = (b2b.getCustomerPincode() != null
					? placeOfSupply4 + "," + b2b.getCustomerPincode().toString()
					: "");
			parameters.put("CustomerStateCodeNamePincode", customerState);

			String shiptoAddress = (b2b.getShipToAddress1() != null
					? b2b.getShipToAddress1() + "," : "")
					+ (b2b.getShipToAddress2() != null
							? b2b.getShipToAddress2() + "," : "")
					+ (b2b.getShipToLocation() != null ? b2b.getShipToLocation()
							: "");
			if (Strings.isNullOrEmpty(shiptoAddress)) {
				shiptoAddress = customerAddress;
			}

			String dispatcherAddress = (b2b.getDispatcherAddress1() != null
					? b2b.getDispatcherAddress1() + "," : "")
					+ (b2b.getDispatcherAddress2() != null
							? b2b.getDispatcherAddress2() + "," : "")
					+ (b2b.getDispatcherLocation() != null
							? b2b.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress;
			}

			List<GetIrnSezWpItemEntity> lineItems = getIrnSezwpLineRepo
					.findByHeaderId(b2b.getId());
			parameters.put("SGSTIN",
					(!Strings.isNullOrEmpty(b2b.getSupplierGSTIN()))
							? b2b.getSupplierGSTIN() : "");

			parameters.put("irn", b2b.getIrn() != null ? b2b.getIrn() : "N/A");

			String custgstin = (b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("customergstin", custgstin);

			String placeOfSupply = "";
			if (b2b.getBillingPOS() != null) {
				String stateName1 = statecodeRepository
						.findStateNameByCode(b2b.getBillingPOS());
				placeOfSupply = stateName1 + "(" + b2b.getBillingPOS() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);

			parameters.put("SupplierAdd",
					(b2b.getSupplierAddress1() != null
							? b2b.getSupplierAddress1() + "\n" : "")
							+ (b2b.getSupplierAddress2() != null
									? b2b.getSupplierAddress2() + "\n" : "")
							+ (b2b.getSupplierLocation() != null
									? b2b.getSupplierLocation() : ""));

			parameters.put("CustomerGSTIN", b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("CustomerAdd",
					(b2b.getCustomerAddress1() != null
							? b2b.getCustomerAddress1() + "\n" : "")
							+ (b2b.getCustomerAddress2() != null
									? b2b.getCustomerAddress2() + "\n" : "")
							+ (b2b.getCustomerLocation() != null
									? b2b.getCustomerLocation() : ""));

			List<GoodsProductDetails> eInvoiceProductDetails = new ArrayList<>();
			int slNo = 1;
			BigDecimal ttl = new BigDecimal("0.00");
			BigDecimal tt2 = new BigDecimal("0.00");
			BigDecimal tt4 = new BigDecimal("0.00");
			for (GetIrnSezWpItemEntity line : lineItems) {
				GoodsProductDetails productDetails = new GoodsProductDetails();
				productDetails.setSlNo(String.valueOf(slNo));
				productDetails.setPrdname(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails.setPrddesc(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails
						.setHsn(line.getHsn() != null ? line.getHsn() : "");
				if (line.getQuantity() == null) {
					productDetails.setQuantity(line.getQuantity() != null
							? line.getQuantity().toString() : "0");
				} else {
					productDetails.setQuantity(line.getQuantity().toString());
				}
				productDetails.setUnit(line.getUnit() != null
						? line.getUnit().toString()
						: "");
				productDetails.setUnitprice(line.getUnitPrice() != null
						? GenUtil.formatCurrency(line.getUnitPrice())
						: AMOUNT_STRING);

				productDetails.setItemdiscount(line.getItemDiscount() != null
						? GenUtil.formatCurrency(line.getItemDiscount())
						: AMOUNT_STRING);
				if (line.getItemDiscount() != null) {
					tt4 = tt4.add(line.getItemDiscount());
				}
				productDetails.setItemassessableamount(
						line.getItemAssessableAmt() != null
								? GenUtil.formatCurrency(
										line.getItemAssessableAmt())
								: AMOUNT_STRING);
				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				productDetails.setIgstrate(String.valueOf(igstRate));
				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				productDetails.setCgstrate(String.valueOf(cgstRate));
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				productDetails.setSgstrate(String.valueOf(sgstRate));
				BigDecimal cessRate = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				cessRate = cessRate.setScale(2, BigDecimal.ROUND_HALF_UP);

				productDetails.setCessadvlrate(String.format("%.2f", cessRate));
				BigDecimal stateCessRate = line
						.getStateCessAdvaloremRate() != null
								? line.getStateCessAdvaloremRate()
								: BigDecimal.ZERO;
				stateCessRate = stateCessRate.setScale(2,
						BigDecimal.ROUND_HALF_UP);

				productDetails.setTaxrate(String.format("%.2f", stateCessRate));

				productDetails.setItemotherchargesFormatted(
						line.getItemOtherCharges() != null
								? GenUtil.formatCurrency(
										line.getItemOtherCharges())
								: AMOUNT_STRING);
				if (line.getItemOtherCharges() != null) {
					tt2 = tt2.add(line.getItemOtherCharges());
				}
				productDetails.setTotalitemValFormatted(
						line.getTotalItemAmount() != null
								? GenUtil.formatCurrency(
										line.getTotalItemAmount())
								: AMOUNT_STRING);
				if (line.getTotalItemAmount() != null) {
					ttl = ttl.add(line.getTotalItemAmount());
				}
				eInvoiceProductDetails.add(productDetails);
				slNo++;
			}
			parameters.put("ItemDetails",
					new JRBeanCollectionDataSource(eInvoiceProductDetails));

			parameters.put("RoundOffValue", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");

			String signedqr = "";
			String ackNo = "";
			String ackDate = "";
			GetIrnDetailPayloadEntity getSignedEntity = getIrnDtlPayloadRepository
					.findByIrnAndIrnStatus(irn, b2b.getIrnStatus());

			if (getSignedEntity != null
					&& getSignedEntity.getPayload() != null) {
				Clob payloadClob = getSignedEntity.getPayload();
				try (Reader reader = payloadClob.getCharacterStream()) {
					StringBuilder payloadStringBuilder = new StringBuilder();
					char[] buffer = new char[1024];
					int bytesRead;
					while ((bytesRead = reader.read(buffer)) != -1) {
						payloadStringBuilder.append(buffer, 0, bytesRead);
					}
					String payloadString = payloadStringBuilder.toString();

					// Parse the JSON to get the SignedQRCode
					JsonObject payloadJson = JsonParser
							.parseString(payloadString).getAsJsonObject();
					ackNo = payloadJson.getAsJsonPrimitive("AckNo")
							.getAsString();
					ackDate = payloadJson.getAsJsonPrimitive("AckDt")
							.getAsString();
					if (payloadJson.has("SignedQRCode")) {
						signedqr = payloadJson
								.getAsJsonPrimitive("SignedQRCode")
								.getAsString();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Signed QR {}", signedqr);
						}
					} else {
						// Handle the case where the key is not present
						LOGGER.warn(
								"Key 'SignedQRCode' not found in the JSON object.");
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Signed QR {}, ackNo {}, ackDate {}",
								signedqr, ackNo, ackDate);
					}

				} catch (IOException | SQLException e) {
					LOGGER.error(
							"Exception occured while getting the signed QR from clob.",
							e);
				}
			}

			parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));
			parameters.put("actNo", ackNo != null ? String.valueOf(ackNo) : "");
			parameters.put("actDate",
					ackDate != null ? String.valueOf(ackDate) : "");
			parameters.put("category",
					b2b.getSupplyType() != null ? b2b.getSupplyType() : "");
			parameters.put("docType", docType.containsKey(b2b.getDocType())
					? docType.get(b2b.getDocType()) : "");

			parameters.put("docNo",
					b2b.getDocNum() != null ? b2b.getDocNum() : "");
			parameters.put("docDate", b2b.getDocDate() != null
					? String.valueOf(formatter2.format(b2b.getDocDate())) : "");

			parameters.put("igstOnInfra", b2b.getSection7OfIGSTFlag() != null
					? b2b.getSection7OfIGSTFlag() : "");

			parameters.put("TotalTaxableAmount",
					b2b.getInvAssessableAmt() != null
							? GenUtil.formatCurrency(b2b.getInvAssessableAmt())
							: "0.00");
			parameters.put("TotalCgstAmount", b2b.getInvCgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCgstAmt()) : "0.00");
			parameters.put("TotalSgstAmount", b2b.getInvSgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvSgstAmt()) : "0.00");
			parameters.put("TotalIgstAmount", b2b.getInvIgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvIgstAmt()) : "0.00");
			parameters.put("TotalCessAmount", b2b.getInvCessAdvaloremAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCessAdvaloremAmt()) : "0.00");
			parameters.put("TotalStateCessAmount",
					b2b.getInvCessAdvaloremAmt() != null
							? GenUtil
									.formatCurrency(b2b.getInvCessAdvaloremAmt()
											.add(b2b.getInvCessSpecificAmt()))
							: "0.00");
			parameters.put("OtherCharges",
					b2b.getInvOtherCharges() != null
							? GenUtil.formatCurrency(b2b.getInvOtherCharges())
							: "0.00");
			parameters.put("RoundOffAmount", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");
			parameters.put("TotalInvoiceAmt", b2b.getInvValue() != null
					? GenUtil.formatCurrency(b2b.getInvValue()) : "0.00");

			LocalDate printedDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("printedDate",
					printedDate != null ? formatter2.format(printedDate) : "");
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

	
}
