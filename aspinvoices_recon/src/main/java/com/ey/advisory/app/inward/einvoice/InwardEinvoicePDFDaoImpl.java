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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
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

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component("InwardEinvoicePDFDaoImpl")
@Slf4j
public class InwardEinvoicePDFDaoImpl implements InwardEinvoiceServicePdf {

	private static final String AMOUNT_STRING = "0.00";

	@Autowired
	QRcodeGenerator qRcodeGenerator;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

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

	DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");
	static DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	final static Map<String, String> docCateg = ImmutableMap.of("REG",
			"Regular", "DIS", "Bill from Dispatch from", "SHP",
			"Bill to ship to", "CMB", "Comb of 2 & 3");

	final static Map<String, String> docTypes = ImmutableMap
			.<String, String>builder().put("INV", "Invoice")
			.put("RNV", "Revised Invoice").put("CR", "Credit Note")
			// .put("CRN", "Credit Note").put("DBN", "Debit Note")
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
		String source = "jasperReports/Detailed_Summary.jrxml";
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
					jasperPrint, docType);
		} else if (supplyType.equalsIgnoreCase("DXP")) {
			Optional<GetIrnDexpHeaderEntity> dexpData = getIrnDexpHeaderRepo
					.findByIrnAndIrnStatus(irn, irnStatus);
			GetIrnDexpHeaderEntity dexp = dexpData.get();
			jasperPrint = generateJasperPrint(dexp, parameters, source,
					jasperPrint, docType);
		} else if (supplyType.equalsIgnoreCase("EXPWOP")) {
			Optional<GetIrnExpWopHeaderEntity> expwopData = getIrnExpWopHeaderRepo
					.findByIrnAndIrnStatus(irn, irnStatus);
			GetIrnExpWopHeaderEntity expwop = expwopData.get();
			jasperPrint = generateJasperPrint(expwop, parameters, source,
					jasperPrint, docType);
		} else if (supplyType.equalsIgnoreCase("EXPWP")) {
			Optional<GetIrnExpWpHeaderEntity> expwpData = getIrnExpwpHeaderRepo
					.findByIrnAndIrnStatus(irn, irnStatus);
			GetIrnExpWpHeaderEntity expwp = expwpData.get();
			jasperPrint = generateJasperPrint(expwp, parameters, source,
					jasperPrint, docType);
		} else if (supplyType.equalsIgnoreCase("SEZWOP")) {
			Optional<GetIrnSezWopHeaderEntity> sezwopData = getIrnSezwopHeaderRepo
					.findByIrnAndIrnStatus(irn, irnStatus);
			GetIrnSezWopHeaderEntity sezwop = sezwopData.get();
			jasperPrint = generateJasperPrint(sezwop, parameters, source,
					jasperPrint, docType);
		} else if (supplyType.equalsIgnoreCase("SEZWP")) {
			Optional<GetIrnSezWpHeaderEntity> sezwpData = getIrnSezwpHeaderRepo
					.findByIrnAndIrnStatus(irn, irnStatus);
			GetIrnSezWpHeaderEntity sezwp = sezwpData.get();
			jasperPrint = generateJasperPrint(sezwp, parameters, source,
					jasperPrint, docType);
		}

		return jasperPrint;

	}

	public JasperPrint generateJasperPrint(GetIrnB2bHeaderEntity b2b,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint, String docType) {
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
			String disptostatename = "";
			if (b2b.getDispatcherStateCode() != null) {
				disptostatename = statecodeRepository.findStateNameByCode(
						b2b.getDispatcherStateCode().toString());
			}
			String Shiptostatename = "";
			if (b2b.getShipToStateCode() != null) {
				Shiptostatename = statecodeRepository.findStateNameByCode(
						b2b.getShipToStateCode().toString());
			}
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
			parameters.put("ShipToAddress", shiptoAddress);

			String shiptoTradeName = (b2b.getShipToLegalName() != null
					? b2b.getShipToLegalName() : "");
			if (Strings.isNullOrEmpty(shiptoTradeName)) {
				shiptoTradeName = customerName;
			}
			parameters.put("ShipToTradeName", shiptoTradeName);
			String placeOfSupply5 = "";
			String shiptoState = "";
			if (b2b.getShipToStateCode() != null) {
				placeOfSupply5 = b2b.getShipToStateCode() + "-"
						+ Shiptostatename;

			}
			shiptoState = (b2b.getShipToPincode() != null
					? placeOfSupply5 + "," + b2b.getShipToPincode().toString()
					: "");
			if (Strings.isNullOrEmpty(shiptoState)) {
				shiptoState = customerState;
			}
			parameters.put("ShipToStateCodeNamePincode", shiptoState);

			String dispatcherAddress = (b2b.getDispatcherAddress1() != null
					? b2b.getDispatcherAddress1() + "," : "")
					+ (b2b.getDispatcherAddress2() != null
							? b2b.getDispatcherAddress2() + "," : "")
					+ (b2b.getDispatcherLocation() != null
							? b2b.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress;
			}
			parameters.put("DispatcherAddress", dispatcherAddress);
			String dispatcherTradeName = (b2b.getDispatcherTradeName() != null
					? b2b.getDispatcherTradeName() : "");
			if (Strings.isNullOrEmpty(dispatcherTradeName)) {
				dispatcherTradeName = supplierName;
			}
			parameters.put("DispatcherTradeName", dispatcherTradeName);

			String placeOfSupply0 = "";
			String disState0 = "";
			if (b2b.getDispatcherStateCode() != null) {
				placeOfSupply0 = b2b.getDispatcherStateCode() + "-"
						+ disptostatename;
			}
			disState0 = (b2b.getDispatcherPincode() != null ? placeOfSupply0
					+ "," + b2b.getDispatcherPincode().toString() : "");
			if (Strings.isNullOrEmpty(disState0)) {
				disState0 = supplierState;
			}
			parameters.put("DispatcherStateCodeNamePincode", disState0);

			List<GetIrnB2bItemEntity> lineItems = getIrnb2bLineRepo
					.findByHeaderId(b2b.getId());
			GSTNDetailEntity entity1 = gSTNDetailRepository
					.findRegDates(b2b.getCustomerGSTIN());
			String clientName = entity1.getRegisteredName() != null
					? entity1.getRegisteredName() : "";

			parameters.put("docType", docTypes.containsKey(b2b.getDocType())
					? docTypes.get(b2b.getDocType()) : "");

			// if (b2b.getIrnStatus().equalsIgnoreCase("CNL")) {
			// parameters.put("PageTitle", "Tax Invoice (CANCELLED)");
			// } else {
			// parameters.put("PageTitle", "Tax Invoice");
			// }

			if ("INV".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Tax Invoice (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Tax Invoice");
				}
			} else if ("CR".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Credit Note (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Credit Note");
				}
			} else if ("DR".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Debit Note (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Debit Note");
				}
			}

			parameters.put("ClientName", clientName);
			parameters.put("RegisteredOffice",
					(entity1.getAddress1() != null
							? entity1.getAddress1() + "\n" : "")
							+ (entity1.getAddress2() != null
									? entity1.getAddress2() + "\n" : "")
							+ (entity1.getAddress3() != null
									? entity1.getAddress3() : ""));

			String suppliernameFor = "For" + " "
					+ (b2b.getSupplierLegalName() != null
							? b2b.getSupplierLegalName() : "");
			parameters.put("SupplierNameFor", suppliernameFor);

			parameters.put("SGSTIN",
					(!Strings.isNullOrEmpty(b2b.getSupplierGSTIN()))
							? b2b.getSupplierGSTIN() : "");
			parameters.put("SPAN", b2b.getSupplierGSTIN().substring(2, 12));
			parameters.put("SupplierPhone", b2b.getSupplierPhone() != null
					? b2b.getSupplierPhone() : "");
			parameters.put("SupplierEmail", b2b.getSupplierEmail() != null
					? b2b.getSupplierEmail() : "");
			parameters.put("SupplierFSSAI", b2b.getSupplierEmail() != null
					? b2b.getSupplierEmail() : "");
			parameters.put("irn", b2b.getIrn() != null ? b2b.getIrn() : "N/A");
			parameters.put("irnDate", b2b.getIrnDateTime() != null
					? (formatter1.format(b2b.getIrnDateTime())) : "N/A");
			parameters.put("InvoiceNo",
					b2b.getDocNum() != null ? b2b.getDocNum() : "");
			parameters.put("InvoiceDate", b2b.getDocDate() != null
					? formatter2.format(
							EYDateUtil.toISTDateTimeFromUTC(b2b.getDocDate()))
					: "");
			parameters
					.put("InvoicePeriodStartDate",
							b2b.getInvoicePeriodStartDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													b2b.getInvoicePeriodStartDate()))
									: "");
			parameters
					.put("InvoicePeriodEndDate",
							b2b.getInvoicePeriodEndDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													b2b.getInvoicePeriodEndDate()))
									: "");
			parameters.put("SupplierSiteDepotCode", "SupplierSiteDepotCode");
			parameters.put("SalesOrderNumber", b2b.getEWayBillNumber() != null
					? b2b.getEWayBillNumber().toString() : "");
			parameters.put("isTaxPayableOnReverse",
					REVERSECHARGE.containsKey(b2b.getReverseChargeFlag())
							? REVERSECHARGE.get(b2b.getReverseChargeFlag())
							: "");

			String custgstin = (b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("customergstin", custgstin);
			parameters.put("CustomerPhone", b2b.getCustomerPhone() != null
					? b2b.getCustomerPhone() : "");
			parameters.put("CustomerEmail", b2b.getCustomerEmail() != null
					? b2b.getCustomerEmail() : "");

			String placeOfSupply = "";
			if (b2b.getBillingPOS() != null) {
				String stateName1 = statecodeRepository
						.findStateNameByCode(b2b.getBillingPOS());
				placeOfSupply = stateName1 + "(" + b2b.getBillingPOS() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);

			String shiptState = (b2b.getShipToGSTIN() != null
					? b2b.getShipToGSTIN() : "");
			if (Strings.isNullOrEmpty(shiptState)) {
				shiptState = custgstin;
			}
			parameters.put("ShipToGSTINNo",
					b2b.getShipToGSTIN() != null ? b2b.getShipToGSTIN() : "");

			parameters.put("EWBNumber", (b2b.getEWayBillNumber() != null
					? b2b.getEWayBillNumber() + " " : "NA")
					+ (b2b.getEWayBillDate() != null
							? (formatter1.format(b2b.getEWayBillDate())) : ""));
			parameters.put("TransportMode", b2b.getTransportMode() != null
					? b2b.getTransportMode() : "");
			parameters.put("VehicleNo",
					b2b.getVehicleNo() != null ? b2b.getVehicleNo() : "");
			parameters.put("TransporterName", b2b.getTransporterName() != null
					? b2b.getTransporterName() : "");
			parameters.put("TransportDocNo",
					(b2b.getTransportDocNo() != null
							? b2b.getTransportDocNo() + " " : "")
							+ (b2b.getTransportDocDate() != null
									? b2b.getTransportDocDate() : ""));
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
			BigDecimal tt3 = new BigDecimal("0.00");
			BigDecimal tt4 = new BigDecimal("0.00");
			BigDecimal tt5 = new BigDecimal("0.00");
			for (GetIrnB2bItemEntity line : lineItems) {
				GoodsProductDetails productDetails = new GoodsProductDetails();
				productDetails.setSlNo(String.valueOf(slNo));
				productDetails.setPrddesc(line.getProductSerialNumber() != null
						? line.getProductSerialNumber() : "");
				productDetails.setPrdname(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails
						.setHsn(line.getHsn() != null ? line.getHsn() : "");
				productDetails
						.setUqc(line.getUqc() != null ? line.getUqc() : "");
				if (line.getQuantity() == null) {
					productDetails.setQuantity(line.getQuantity() != null
							? line.getQuantity().toString() : "0");
				} else {
					productDetails.setQuantity(line.getQuantity().toString());
				}
				productDetails.setUnitprice(line.getUnitPrice() != null
						? GenUtil.formatCurrency(line.getUnitPrice())
						: AMOUNT_STRING);

				productDetails.setItemamount(line.getItemAmount() != null
						? GenUtil.formatCurrency(line.getItemAmount())
						: AMOUNT_STRING);
				if (line.getItemAmount() != null) {
					tt3 = tt3.add(line.getItemAmount());
				}
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

				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				productDetails.setCgstrate(String.valueOf(cgstRate));
				productDetails.setCgstamount(line.getCgstAmount() != null
						? GenUtil.formatCurrency(line.getCgstAmount())
						: AMOUNT_STRING);
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				productDetails.setSgstrate(String.valueOf(sgstRate));
				productDetails.setSgstamount(line.getSgstAmount() != null
						? GenUtil.formatCurrency(line.getSgstAmount())
						: AMOUNT_STRING);

				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				productDetails.setIgstrate(String.valueOf(igstRate));
				productDetails.setIgstamount(line.getIgstAmount() != null
						? GenUtil.formatCurrency(line.getIgstAmount())
						: AMOUNT_STRING);
				BigDecimal cessRate = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				productDetails.setCessadvlrate(String.valueOf(cessRate));
				productDetails
						.setCessadvlamount(line.getCessAdvaloremAmount() != null
								? GenUtil.formatCurrency(
										line.getCessAdvaloremAmount())
								: AMOUNT_STRING);
				BigDecimal cessSpec = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				productDetails.setCessspecificrate(String.valueOf(cessSpec));
				productDetails.setCessspecificamount(
						line.getCessSpecificAmount() != null
								? GenUtil.formatCurrency(
										line.getCessSpecificAmount())
								: AMOUNT_STRING);
				BigDecimal invoiceStateCessAdvaloremAmount = line
						.getStateCessAdvaloremAmount() != null
								? line.getStateCessAdvaloremAmount()
								: BigDecimal.ZERO;
				BigDecimal invoiceStateCessSpecificAmount = line
						.getStateCessSpecificAmount() != null
								? line.getStateCessSpecificAmount()
								: BigDecimal.ZERO;
				BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
						.add(invoiceStateCessSpecificAmount);
				productDetails.setStatecessamount(stateCessAmount != null
						? GenUtil.formatCurrency(stateCessAmount)
						: AMOUNT_STRING);
				if (stateCessAmount != null) {
					tt5 = tt5.add(stateCessAmount);
				}
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

			parameters.put("AmtInWords", b2b.getInvoiceValueFC() != null
					? convertBigDecimalToWords(b2b.getInvValue().toString())
					: "");
			parameters.put("Remark", b2b.getInvoiceRemarks() != null
					? String.valueOf(b2b.getInvoiceRemarks()) : "");
			parameters.put("RoundOffValue", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");
			parameters.put("invoiceothercharges",
					b2b.getInvOtherCharges() != null
							? GenUtil.formatCurrency(b2b.getInvOtherCharges())
							: "0.00");
			parameters.put("invoicediscount",
					b2b.getUserDefinedField28() != null ? GenUtil
							.formatCurrency(b2b.getUserDefinedField28())
							: "0.00");
			parameters.put("InvoiceValue", b2b.getInvValue() != null
					? GenUtil.formatCurrency(b2b.getInvValue()) : "0.00");
			parameters.put("note", supplyType.containsKey(b2b.getSupplyType())
					? supplyType.get(b2b.getSupplyType()) : "");

			StringBuffer textdata = new StringBuffer();
			textdata.append("");

			parameters.put("TermNConditions", "");

			parameters.put("AccountDetails", b2b.getAccountDetail() != null
					? String.valueOf(b2b.getAccountDetail()) : " ");

			parameters.put("BankName", " ");

			parameters.put("BankAddress", " ");

			parameters.put("BranchOrIFSCCode", b2b.getBranchOrIFSCCode() != null
					? String.valueOf(b2b.getBranchOrIFSCCode()) : "");
			parameters.put("PayeeName", b2b.getPayeeName() != null
					? String.valueOf(b2b.getPayeeName()) : "");
			String signedqr = "";

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
						LOGGER.debug("Signed QR {}", signedqr);
					}

				} catch (IOException | SQLException e) {
					LOGGER.error(
							"Exception occured while getting the signed QR from clob.",
							e);
				}
			}

			parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));

			parameters.put("invoiceassessableamount",
					b2b.getInvAssessableAmt() != null
							? GenUtil.formatCurrency(b2b.getInvAssessableAmt())
							: "0.00");
			parameters.put("invoicecgstamount", b2b.getInvCgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCgstAmt()) : "0.00");
			parameters.put("invoicesgstamount", b2b.getInvSgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvSgstAmt()) : "0.00");
			parameters.put("invoiceigstamount", b2b.getInvIgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvIgstAmt()) : "0.00");
			parameters.put("invoicecessadvlamount",
					b2b.getInvCessAdvaloremAmt() != null ? GenUtil
							.formatCurrency(b2b.getInvCessAdvaloremAmt())
							: "0.00");
			parameters.put("invoicecessspecificamount",
					b2b.getInvCessSpecificAmt() != null ? GenUtil
							.formatCurrency(b2b.getInvCessSpecificAmt())
							: "0.00");
			parameters.put("totalothercharges",
					tt2 != null ? GenUtil.formatCurrency(tt2) : "0.00");
			parameters.put("totalitemamt",
					tt3 != null ? GenUtil.formatCurrency(tt3) : "0.00");
			parameters.put("totaldiscount",
					tt4 != null ? GenUtil.formatCurrency(tt4) : "0.00");
			parameters.put("invoicestatecesscamount",
					tt5 != null ? GenUtil.formatCurrency(tt5) : "0.00");

			parameters.put("PaymentDueDate", "");

			parameters.put("PaymentTerms",
					b2b.getPaymentTerms() != null
							? StringUtils.truncate(
									String.valueOf(b2b.getPaymentTerms()), 100)
							: "");
			parameters.put("totalproductvalue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			parameters.put("SubInvoiceValue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			LocalDate printedDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("PrintedDate",
					printedDate != null ? formatter2.format(printedDate) : "");
			parameters.put("PaymentInstruction",
					b2b.getPaymentInstruction() != null ? StringUtils.truncate(
							String.valueOf(b2b.getPaymentInstruction()), 100)
							: "");

			BigDecimal invoiceStateCessAdvaloremAmount = b2b
					.getInvStateCessAdvaloremAmt() != null
							? b2b.getInvStateCessAdvaloremAmt()
							: BigDecimal.ZERO;
			BigDecimal invoiceStateCessSpecificAmount = b2b
					.getInvStateCessSpecificAmt() != null
							? b2b.getInvStateCessSpecificAmt()
							: BigDecimal.ZERO;
			BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
					.add(invoiceStateCessSpecificAmount);

			parameters.put("StateCess", stateCessAmount != null
					? GenUtil.formatCurrency(stateCessAmount) : "0.00");

			parameters.put("CurrencyCode", "");
			parameters.put("InvoiceValueFC", "");

			if (b2b.getSupplyType().equals("EXPT")
					|| b2b.getSupplyType().equals("EXPWT")) {

				if (b2b.getInvoiceValueFC() != null) {
					parameters.put("CurrencyCode",
							"Invoice Value FC" + (b2b.getCurrencyCode() != null
									? " " + "(" + b2b.getCurrencyCode() + ")"
											+ " " + ":"
									: ":"));
					parameters.put("InvoiceValueFC",
							b2b.getInvoiceValueFC() != null ? GenUtil
									.formatCurrency(b2b.getInvoiceValueFC())
									: "");

				}
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

	public static String convertBigDecimalToWords(String num) {
		BigDecimal bd = new BigDecimal(num);
		long number = bd.longValue();
		long no = bd.longValue();
		int decimal = (int) (bd.remainder(BigDecimal.ONE).doubleValue() * 100);
		int digits_length = String.valueOf(no).length();
		int i = 0;
		ArrayList<String> str = new ArrayList<>();
		HashMap<Integer, String> words = new HashMap<>();
		words.put(0, "");
		words.put(1, "One");
		words.put(2, "Two");
		words.put(3, "Three");
		words.put(4, "Four");
		words.put(5, "Five");
		words.put(6, "Six");
		words.put(7, "Seven");
		words.put(8, "Eight");
		words.put(9, "Nine");
		words.put(10, "Ten");
		words.put(11, "Eleven");
		words.put(12, "Twelve");
		words.put(13, "Thirteen");
		words.put(14, "Fourteen");
		words.put(15, "Fifteen");
		words.put(16, "Sixteen");
		words.put(17, "Seventeen");
		words.put(18, "Eighteen");
		words.put(19, "Nineteen");
		words.put(20, "Twenty");
		words.put(30, "Thirty");
		words.put(40, "Forty");
		words.put(50, "Fifty");
		words.put(60, "Sixty");
		words.put(70, "Seventy");
		words.put(80, "Eighty");
		words.put(90, "Ninety");
		String digits[] = { "", "Hundred", "Thousand", "Lakh", "Crore" };
		while (i < digits_length) {
			int divider = (i == 2) ? 10 : 100;
			number = no % divider;
			no = no / divider;
			i += divider == 10 ? 1 : 2;
			if (number > 0) {
				int counter = str.size();
				String plural = (counter > 0 && number > 9) ? "s" : "";
				String tmp = (number < 21)
						? words.get(Integer.valueOf((int) number)) + " "
								+ digits[counter] + plural
						: words.get(Integer
								.valueOf((int) Math.floor(number / 10) * 10))
								+ " "
								+ words.get(
										Integer.valueOf((int) (number % 10)))
								+ " " + digits[counter] + plural;
				str.add(tmp);
			} else {
				str.add("");
			}
		}

		Collections.reverse(str);
		String Rupees = String.join(" ", str).trim();

		String paise = (decimal) > 0 ? " And "
				+ words.get(Integer.valueOf((int) (decimal - decimal % 10)))
				+ " " + words.get(Integer.valueOf((int) (decimal % 10)))
				+ " Paise " : "";
		return Rupees + " Rupees" + paise + " Only";
	}

	public JasperPrint generateJasperPrint(GetIrnDexpHeaderEntity b2b,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint, String docType) {
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
			String disptostatename = "";
			if (b2b.getDispatcherStateCode() != null) {
				disptostatename = statecodeRepository.findStateNameByCode(
						b2b.getDispatcherStateCode().toString());
			}
			String Shiptostatename = "";
			if (b2b.getShipToStateCode() != null) {
				Shiptostatename = statecodeRepository.findStateNameByCode(
						b2b.getShipToStateCode().toString());
			}
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
			parameters.put("ShipToAddress", shiptoAddress);

			String shiptoTradeName = (b2b.getShipToLegalName() != null
					? b2b.getShipToLegalName() : "");
			if (Strings.isNullOrEmpty(shiptoTradeName)) {
				shiptoTradeName = customerName;
			}
			parameters.put("ShipToTradeName", shiptoTradeName);
			String placeOfSupply5 = "";
			String shiptoState = "";
			if (b2b.getShipToStateCode() != null) {
				placeOfSupply5 = b2b.getShipToStateCode() + "-"
						+ Shiptostatename;

			}
			shiptoState = (b2b.getShipToPincode() != null
					? placeOfSupply5 + "," + b2b.getShipToPincode().toString()
					: "");
			if (Strings.isNullOrEmpty(shiptoState)) {
				shiptoState = customerState;
			}
			parameters.put("ShipToStateCodeNamePincode", shiptoState);

			String dispatcherAddress = (b2b.getDispatcherAddress1() != null
					? b2b.getDispatcherAddress1() + "," : "")
					+ (b2b.getDispatcherAddress2() != null
							? b2b.getDispatcherAddress2() + "," : "")
					+ (b2b.getDispatcherLocation() != null
							? b2b.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress;
			}
			parameters.put("DispatcherAddress", dispatcherAddress);
			String dispatcherTradeName = (b2b.getDispatcherTradeName() != null
					? b2b.getDispatcherTradeName() : "");
			if (Strings.isNullOrEmpty(dispatcherTradeName)) {
				dispatcherTradeName = supplierName;
			}
			parameters.put("DispatcherTradeName", dispatcherTradeName);

			String placeOfSupply0 = "";
			String disState0 = "";
			if (b2b.getDispatcherStateCode() != null) {
				placeOfSupply0 = b2b.getDispatcherStateCode() + "-"
						+ disptostatename;
			}
			disState0 = (b2b.getDispatcherPincode() != null ? placeOfSupply0
					+ "," + b2b.getDispatcherPincode().toString() : "");
			if (Strings.isNullOrEmpty(disState0)) {
				disState0 = supplierState;
			}
			parameters.put("DispatcherStateCodeNamePincode", disState0);

			List<GetIrnDexpItemEntity> lineItems = getIrnDexpLineRepo
					.findByHeaderId(b2b.getId());
			GSTNDetailEntity entity1 = gSTNDetailRepository
					.findRegDates(b2b.getCustomerGSTIN());
			String clientName = entity1.getRegisteredName() != null
					? entity1.getRegisteredName() : "";

			if ("INV".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Tax Invoice (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Tax Invoice");
				}
			} else if ("CR".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Credit Note (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Credit Note");
				}
			} else if ("DR".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Debit Note (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Debit Note");
				}
			}
			parameters.put("ClientName", clientName);
			parameters.put("RegisteredOffice",
					(entity1.getAddress1() != null
							? entity1.getAddress1() + "\n" : "")
							+ (entity1.getAddress2() != null
									? entity1.getAddress2() + "\n" : "")
							+ (entity1.getAddress3() != null
									? entity1.getAddress3() : ""));

			String suppliernameFor = "For" + " "
					+ (b2b.getSupplierLegalName() != null
							? b2b.getSupplierLegalName() : "");
			parameters.put("SupplierNameFor", suppliernameFor);

			parameters.put("SGSTIN",
					(!Strings.isNullOrEmpty(b2b.getSupplierGSTIN()))
							? b2b.getSupplierGSTIN() : "");
			parameters.put("SPAN", b2b.getSupplierGSTIN().substring(2, 12));
			parameters.put("SupplierPhone", b2b.getSupplierPhone() != null
					? b2b.getSupplierPhone() : "");
			parameters.put("SupplierEmail", b2b.getSupplierEmail() != null
					? b2b.getSupplierEmail() : "");
			parameters.put("SupplierFSSAI", b2b.getSupplierEmail() != null
					? b2b.getSupplierEmail() : "");
			parameters.put("irn", b2b.getIrn() != null ? b2b.getIrn() : "N/A");
			parameters.put("irnDate", b2b.getIrnDateTime() != null
					? (formatter1.format(b2b.getIrnDateTime())) : "N/A");
			parameters.put("InvoiceNo",
					b2b.getDocNum() != null ? b2b.getDocNum() : "");
			parameters.put("InvoiceDate", b2b.getDocDate() != null
					? formatter2.format(
							EYDateUtil.toISTDateTimeFromUTC(b2b.getDocDate()))
					: "");
			parameters
					.put("InvoicePeriodStartDate",
							b2b.getInvoicePeriodStartDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													b2b.getInvoicePeriodStartDate()))
									: "");
			parameters
					.put("InvoicePeriodEndDate",
							b2b.getInvoicePeriodEndDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													b2b.getInvoicePeriodEndDate()))
									: "");
			parameters.put("SupplierSiteDepotCode", "SupplierSiteDepotCode");
			parameters.put("SalesOrderNumber", b2b.getEWayBillNumber() != null
					? b2b.getEWayBillNumber().toString() : "");
			parameters.put("isTaxPayableOnReverse",
					REVERSECHARGE.containsKey(b2b.getReverseChargeFlag())
							? REVERSECHARGE.get(b2b.getReverseChargeFlag())
							: "");

			String custgstin = (b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("customergstin", custgstin);
			parameters.put("CustomerPhone", b2b.getCustomerPhone() != null
					? b2b.getCustomerPhone() : "");
			parameters.put("CustomerEmail", b2b.getCustomerEmail() != null
					? b2b.getCustomerEmail() : "");

			String placeOfSupply = "";
			if (b2b.getBillingPOS() != null) {
				String stateName1 = statecodeRepository
						.findStateNameByCode(b2b.getBillingPOS());
				placeOfSupply = stateName1 + "(" + b2b.getBillingPOS() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);

			String shiptState = (b2b.getShipToGSTIN() != null
					? b2b.getShipToGSTIN() : "");
			if (Strings.isNullOrEmpty(shiptState)) {
				shiptState = custgstin;
			}
			parameters.put("ShipToGSTINNo",
					b2b.getShipToGSTIN() != null ? b2b.getShipToGSTIN() : "");

			parameters.put("EWBNumber", (b2b.getEWayBillNumber() != null
					? b2b.getEWayBillNumber() + " " : "NA")
					+ (b2b.getEWayBillDate() != null
							? (formatter1.format(b2b.getEWayBillDate())) : ""));
			parameters.put("TransportMode", b2b.getTransportMode() != null
					? b2b.getTransportMode() : "");
			parameters.put("VehicleNo",
					b2b.getVehicleNo() != null ? b2b.getVehicleNo() : "");
			parameters.put("TransporterName", b2b.getTransporterName() != null
					? b2b.getTransporterName() : "");
			parameters.put("TransportDocNo",
					(b2b.getTransportDocNo() != null
							? b2b.getTransportDocNo() + " " : "")
							+ (b2b.getTransportDocDate() != null
									? b2b.getTransportDocDate() : ""));
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
			BigDecimal tt3 = new BigDecimal("0.00");
			BigDecimal tt4 = new BigDecimal("0.00");
			BigDecimal tt5 = new BigDecimal("0.00");
			for (GetIrnDexpItemEntity line : lineItems) {
				GoodsProductDetails productDetails = new GoodsProductDetails();
				productDetails.setSlNo(String.valueOf(slNo));
				productDetails.setPrddesc(line.getProductSerialNumber() != null
						? line.getProductSerialNumber() : "");
				productDetails.setPrdname(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails
						.setHsn(line.getHsn() != null ? line.getHsn() : "");
				productDetails
						.setUqc(line.getUqc() != null ? line.getUqc() : "");
				if (line.getQuantity() == null) {
					productDetails.setQuantity(line.getQuantity() != null
							? line.getQuantity().toString() : "0");
				} else {
					productDetails.setQuantity(line.getQuantity().toString());
				}
				productDetails.setUnitprice(line.getUnitPrice() != null
						? GenUtil.formatCurrency(line.getUnitPrice())
						: AMOUNT_STRING);

				productDetails.setItemamount(line.getItemAmount() != null
						? GenUtil.formatCurrency(line.getItemAmount())
						: AMOUNT_STRING);
				if (line.getItemAmount() != null) {
					tt3 = tt3.add(line.getItemAmount());
				}
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

				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				productDetails.setCgstrate(String.valueOf(cgstRate));
				productDetails.setCgstamount(line.getCgstAmount() != null
						? GenUtil.formatCurrency(line.getCgstAmount())
						: AMOUNT_STRING);
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				productDetails.setSgstrate(String.valueOf(sgstRate));
				productDetails.setSgstamount(line.getSgstAmount() != null
						? GenUtil.formatCurrency(line.getSgstAmount())
						: AMOUNT_STRING);

				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				productDetails.setIgstrate(String.valueOf(igstRate));
				productDetails.setIgstamount(line.getIgstAmount() != null
						? GenUtil.formatCurrency(line.getIgstAmount())
						: AMOUNT_STRING);
				BigDecimal cessRate = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				productDetails.setCessadvlrate(String.valueOf(cessRate));
				productDetails
						.setCessadvlamount(line.getCessAdvaloremAmount() != null
								? GenUtil.formatCurrency(
										line.getCessAdvaloremAmount())
								: AMOUNT_STRING);
				BigDecimal cessSpec = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				productDetails.setCessspecificrate(String.valueOf(cessSpec));
				productDetails.setCessspecificamount(
						line.getCessSpecificAmount() != null
								? GenUtil.formatCurrency(
										line.getCessSpecificAmount())
								: AMOUNT_STRING);
				BigDecimal invoiceStateCessAdvaloremAmount = line
						.getStateCessAdvaloremAmount() != null
								? line.getStateCessAdvaloremAmount()
								: BigDecimal.ZERO;
				BigDecimal invoiceStateCessSpecificAmount = line
						.getStateCessSpecificAmount() != null
								? line.getStateCessSpecificAmount()
								: BigDecimal.ZERO;
				BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
						.add(invoiceStateCessSpecificAmount);
				productDetails.setStatecessamount(stateCessAmount != null
						? GenUtil.formatCurrency(stateCessAmount)
						: AMOUNT_STRING);
				if (stateCessAmount != null) {
					tt5 = tt5.add(stateCessAmount);
				}
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

			parameters.put("AmtInWords", b2b.getInvoiceValueFC() != null
					? convertBigDecimalToWords(b2b.getInvValue().toString())
					: "");
			parameters.put("Remark", b2b.getInvoiceRemarks() != null
					? String.valueOf(b2b.getInvoiceRemarks()) : "");
			parameters.put("RoundOffValue", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");
			parameters.put("invoiceothercharges",
					b2b.getInvOtherCharges() != null
							? GenUtil.formatCurrency(b2b.getInvOtherCharges())
							: "0.00");
			parameters.put("invoicediscount",
					b2b.getUserDefinedField28() != null ? GenUtil
							.formatCurrency(b2b.getUserDefinedField28())
							: "0.00");
			parameters.put("InvoiceValue", b2b.getInvValue() != null
					? GenUtil.formatCurrency(b2b.getInvValue()) : "0.00");
			parameters.put("note", supplyType.containsKey(b2b.getSupplyType())
					? supplyType.get(b2b.getSupplyType()) : "");

			StringBuffer textdata = new StringBuffer();
			textdata.append("");

			parameters.put("TermNConditions", "");

			parameters.put("AccountDetails", b2b.getAccountDetail() != null
					? String.valueOf(b2b.getAccountDetail()) : " ");

			parameters.put("BankName", " ");

			parameters.put("BankAddress", " ");

			parameters.put("BranchOrIFSCCode", b2b.getBranchOrIFSCCode() != null
					? String.valueOf(b2b.getBranchOrIFSCCode()) : "");
			parameters.put("PayeeName", b2b.getPayeeName() != null
					? String.valueOf(b2b.getPayeeName()) : "");
			String signedqr = "";

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
						LOGGER.debug("Signed QR {}", signedqr);
					}

				} catch (IOException | SQLException e) {
					LOGGER.error(
							"Exception occured while getting the signed QR from clob.",
							e);
				}
			}

			parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));

			parameters.put("invoiceassessableamount",
					b2b.getInvAssessableAmt() != null
							? GenUtil.formatCurrency(b2b.getInvAssessableAmt())
							: "0.00");
			parameters.put("invoicecgstamount", b2b.getInvCgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCgstAmt()) : "0.00");
			parameters.put("invoicesgstamount", b2b.getInvSgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvSgstAmt()) : "0.00");
			parameters.put("invoiceigstamount", b2b.getInvIgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvIgstAmt()) : "0.00");
			parameters.put("invoicecessadvlamount",
					b2b.getInvCessAdvaloremAmt() != null ? GenUtil
							.formatCurrency(b2b.getInvCessAdvaloremAmt())
							: "0.00");
			parameters.put("invoicecessspecificamount",
					b2b.getInvCessSpecificAmt() != null ? GenUtil
							.formatCurrency(b2b.getInvCessSpecificAmt())
							: "0.00");
			parameters.put("totalothercharges",
					tt2 != null ? GenUtil.formatCurrency(tt2) : "0.00");
			parameters.put("totalitemamt",
					tt3 != null ? GenUtil.formatCurrency(tt3) : "0.00");
			parameters.put("totaldiscount",
					tt4 != null ? GenUtil.formatCurrency(tt4) : "0.00");
			parameters.put("invoicestatecesscamount",
					tt5 != null ? GenUtil.formatCurrency(tt5) : "0.00");

			parameters.put("PaymentDueDate", "");

			parameters.put("PaymentTerms",
					b2b.getPaymentTerms() != null
							? StringUtils.truncate(
									String.valueOf(b2b.getPaymentTerms()), 100)
							: "");
			parameters.put("totalproductvalue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			parameters.put("SubInvoiceValue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			LocalDate printedDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("PrintedDate",
					printedDate != null ? formatter2.format(printedDate) : "");
			parameters.put("PaymentInstruction",
					b2b.getPaymentInstruction() != null ? StringUtils.truncate(
							String.valueOf(b2b.getPaymentInstruction()), 100)
							: "");

			BigDecimal invoiceStateCessAdvaloremAmount = b2b
					.getInvStateCessAdvaloremAmt() != null
							? b2b.getInvStateCessAdvaloremAmt()
							: BigDecimal.ZERO;
			BigDecimal invoiceStateCessSpecificAmount = b2b
					.getInvStateCessSpecificAmt() != null
							? b2b.getInvStateCessSpecificAmt()
							: BigDecimal.ZERO;
			BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
					.add(invoiceStateCessSpecificAmount);

			parameters.put("StateCess", stateCessAmount != null
					? GenUtil.formatCurrency(stateCessAmount) : "0.00");

			parameters.put("CurrencyCode", "");
			parameters.put("InvoiceValueFC", "");

			if (b2b.getSupplyType().equals("EXPT")
					|| b2b.getSupplyType().equals("EXPWT")) {

				if (b2b.getInvoiceValueFC() != null) {
					parameters.put("CurrencyCode",
							"Invoice Value FC" + (b2b.getCurrencyCode() != null
									? " " + "(" + b2b.getCurrencyCode() + ")"
											+ " " + ":"
									: ":"));
					parameters.put("InvoiceValueFC",
							b2b.getInvoiceValueFC() != null ? GenUtil
									.formatCurrency(b2b.getInvoiceValueFC())
									: "");

				}
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

	public JasperPrint generateJasperPrint(GetIrnSezWpHeaderEntity b2b,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint, String docType) {
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
			String disptostatename = "";
			if (b2b.getDispatcherStateCode() != null) {
				disptostatename = statecodeRepository.findStateNameByCode(
						b2b.getDispatcherStateCode().toString());
			}
			String Shiptostatename = "";
			if (b2b.getShipToStateCode() != null) {
				Shiptostatename = statecodeRepository.findStateNameByCode(
						b2b.getShipToStateCode().toString());
			}
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
			parameters.put("ShipToAddress", shiptoAddress);

			String shiptoTradeName = (b2b.getShipToLegalName() != null
					? b2b.getShipToLegalName() : "");
			if (Strings.isNullOrEmpty(shiptoTradeName)) {
				shiptoTradeName = customerName;
			}
			parameters.put("ShipToTradeName", shiptoTradeName);
			String placeOfSupply5 = "";
			String shiptoState = "";
			if (b2b.getShipToStateCode() != null) {
				placeOfSupply5 = b2b.getShipToStateCode() + "-"
						+ Shiptostatename;

			}
			shiptoState = (b2b.getShipToPincode() != null
					? placeOfSupply5 + "," + b2b.getShipToPincode().toString()
					: "");
			if (Strings.isNullOrEmpty(shiptoState)) {
				shiptoState = customerState;
			}
			parameters.put("ShipToStateCodeNamePincode", shiptoState);

			String dispatcherAddress = (b2b.getDispatcherAddress1() != null
					? b2b.getDispatcherAddress1() + "," : "")
					+ (b2b.getDispatcherAddress2() != null
							? b2b.getDispatcherAddress2() + "," : "")
					+ (b2b.getDispatcherLocation() != null
							? b2b.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress;
			}
			parameters.put("DispatcherAddress", dispatcherAddress);
			String dispatcherTradeName = (b2b.getDispatcherTradeName() != null
					? b2b.getDispatcherTradeName() : "");
			if (Strings.isNullOrEmpty(dispatcherTradeName)) {
				dispatcherTradeName = supplierName;
			}
			parameters.put("DispatcherTradeName", dispatcherTradeName);

			String placeOfSupply0 = "";
			String disState0 = "";
			if (b2b.getDispatcherStateCode() != null) {
				placeOfSupply0 = b2b.getDispatcherStateCode() + "-"
						+ disptostatename;
			}
			disState0 = (b2b.getDispatcherPincode() != null ? placeOfSupply0
					+ "," + b2b.getDispatcherPincode().toString() : "");
			if (Strings.isNullOrEmpty(disState0)) {
				disState0 = supplierState;
			}
			parameters.put("DispatcherStateCodeNamePincode", disState0);

			List<GetIrnSezWpItemEntity> lineItems = getIrnSezwpLineRepo
					.findByHeaderId(b2b.getId());
			GSTNDetailEntity entity1 = gSTNDetailRepository
					.findRegDates(b2b.getCustomerGSTIN());
			String clientName = entity1.getRegisteredName() != null
					? entity1.getRegisteredName() : "";

			if ("INV".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Tax Invoice (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Tax Invoice");
				}
			} else if ("CR".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Credit Note (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Credit Note");
				}
			} else if ("DR".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Debit Note (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Debit Note");
				}
			}
			parameters.put("ClientName", clientName);
			parameters.put("RegisteredOffice",
					(entity1.getAddress1() != null
							? entity1.getAddress1() + "\n" : "")
							+ (entity1.getAddress2() != null
									? entity1.getAddress2() + "\n" : "")
							+ (entity1.getAddress3() != null
									? entity1.getAddress3() : ""));

			String suppliernameFor = "For" + " "
					+ (b2b.getSupplierLegalName() != null
							? b2b.getSupplierLegalName() : "");
			parameters.put("SupplierNameFor", suppliernameFor);

			parameters.put("SGSTIN",
					(!Strings.isNullOrEmpty(b2b.getSupplierGSTIN()))
							? b2b.getSupplierGSTIN() : "");
			parameters.put("SPAN", b2b.getSupplierGSTIN().substring(2, 12));
			parameters.put("SupplierPhone", b2b.getSupplierPhone() != null
					? b2b.getSupplierPhone() : "");
			parameters.put("SupplierEmail", b2b.getSupplierEmail() != null
					? b2b.getSupplierEmail() : "");
			parameters.put("SupplierFSSAI", b2b.getSupplierEmail() != null
					? b2b.getSupplierEmail() : "");
			parameters.put("irn", b2b.getIrn() != null ? b2b.getIrn() : "N/A");
			parameters.put("irnDate", b2b.getIrnDateTime() != null
					? (formatter1.format(b2b.getIrnDateTime())) : "N/A");
			parameters.put("InvoiceNo",
					b2b.getDocNum() != null ? b2b.getDocNum() : "");
			parameters.put("InvoiceDate", b2b.getDocDate() != null
					? formatter2.format(
							EYDateUtil.toISTDateTimeFromUTC(b2b.getDocDate()))
					: "");
			parameters
					.put("InvoicePeriodStartDate",
							b2b.getInvoicePeriodStartDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													b2b.getInvoicePeriodStartDate()))
									: "");
			parameters
					.put("InvoicePeriodEndDate",
							b2b.getInvoicePeriodEndDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													b2b.getInvoicePeriodEndDate()))
									: "");
			parameters.put("SupplierSiteDepotCode", "SupplierSiteDepotCode");
			parameters.put("SalesOrderNumber", b2b.getEWayBillNumber() != null
					? b2b.getEWayBillNumber().toString() : "");
			parameters.put("isTaxPayableOnReverse",
					REVERSECHARGE.containsKey(b2b.getReverseChargeFlag())
							? REVERSECHARGE.get(b2b.getReverseChargeFlag())
							: "");

			String custgstin = (b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("customergstin", custgstin);
			parameters.put("CustomerPhone", b2b.getCustomerPhone() != null
					? b2b.getCustomerPhone() : "");
			parameters.put("CustomerEmail", b2b.getCustomerEmail() != null
					? b2b.getCustomerEmail() : "");

			String placeOfSupply = "";
			if (b2b.getBillingPOS() != null) {
				String stateName1 = statecodeRepository
						.findStateNameByCode(b2b.getBillingPOS());
				placeOfSupply = stateName1 + "(" + b2b.getBillingPOS() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);

			String shiptState = (b2b.getShipToGSTIN() != null
					? b2b.getShipToGSTIN() : "");
			if (Strings.isNullOrEmpty(shiptState)) {
				shiptState = custgstin;
			}
			parameters.put("ShipToGSTINNo",
					b2b.getShipToGSTIN() != null ? b2b.getShipToGSTIN() : "");

			parameters.put("EWBNumber", (b2b.getEWayBillNumber() != null
					? b2b.getEWayBillNumber() + " " : "NA")
					+ (b2b.getEWayBillDate() != null
							? (formatter1.format(b2b.getEWayBillDate())) : ""));
			parameters.put("TransportMode", b2b.getTransportMode() != null
					? b2b.getTransportMode() : "");
			parameters.put("VehicleNo",
					b2b.getVehicleNo() != null ? b2b.getVehicleNo() : "");
			parameters.put("TransporterName", b2b.getTransporterName() != null
					? b2b.getTransporterName() : "");
			parameters.put("TransportDocNo",
					(b2b.getTransportDocNo() != null
							? b2b.getTransportDocNo() + " " : "")
							+ (b2b.getTransportDocDate() != null
									? b2b.getTransportDocDate() : ""));
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
			BigDecimal tt3 = new BigDecimal("0.00");
			BigDecimal tt4 = new BigDecimal("0.00");
			BigDecimal tt5 = new BigDecimal("0.00");
			for (GetIrnSezWpItemEntity line : lineItems) {
				GoodsProductDetails productDetails = new GoodsProductDetails();
				productDetails.setSlNo(String.valueOf(slNo));
				productDetails.setPrddesc(line.getProductSerialNumber() != null
						? line.getProductSerialNumber() : "");
				productDetails.setPrdname(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails
						.setHsn(line.getHsn() != null ? line.getHsn() : "");
				productDetails
						.setUqc(line.getUqc() != null ? line.getUqc() : "");
				if (line.getQuantity() == null) {
					productDetails.setQuantity(line.getQuantity() != null
							? line.getQuantity().toString() : "0");
				} else {
					productDetails.setQuantity(line.getQuantity().toString());
				}
				productDetails.setUnitprice(line.getUnitPrice() != null
						? GenUtil.formatCurrency(line.getUnitPrice())
						: AMOUNT_STRING);

				productDetails.setItemamount(line.getItemAmount() != null
						? GenUtil.formatCurrency(line.getItemAmount())
						: AMOUNT_STRING);
				if (line.getItemAmount() != null) {
					tt3 = tt3.add(line.getItemAmount());
				}
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

				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				productDetails.setCgstrate(String.valueOf(cgstRate));
				productDetails.setCgstamount(line.getCgstAmount() != null
						? GenUtil.formatCurrency(line.getCgstAmount())
						: AMOUNT_STRING);
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				productDetails.setSgstrate(String.valueOf(sgstRate));
				productDetails.setSgstamount(line.getSgstAmount() != null
						? GenUtil.formatCurrency(line.getSgstAmount())
						: AMOUNT_STRING);

				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				productDetails.setIgstrate(String.valueOf(igstRate));
				productDetails.setIgstamount(line.getIgstAmount() != null
						? GenUtil.formatCurrency(line.getIgstAmount())
						: AMOUNT_STRING);
				BigDecimal cessRate = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				productDetails.setCessadvlrate(String.valueOf(cessRate));
				productDetails
						.setCessadvlamount(line.getCessAdvaloremAmount() != null
								? GenUtil.formatCurrency(
										line.getCessAdvaloremAmount())
								: AMOUNT_STRING);
				BigDecimal cessSpec = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				productDetails.setCessspecificrate(String.valueOf(cessSpec));
				productDetails.setCessspecificamount(
						line.getCessSpecificAmount() != null
								? GenUtil.formatCurrency(
										line.getCessSpecificAmount())
								: AMOUNT_STRING);
				BigDecimal invoiceStateCessAdvaloremAmount = line
						.getStateCessAdvaloremAmount() != null
								? line.getStateCessAdvaloremAmount()
								: BigDecimal.ZERO;
				BigDecimal invoiceStateCessSpecificAmount = line
						.getStateCessSpecificAmount() != null
								? line.getStateCessSpecificAmount()
								: BigDecimal.ZERO;
				BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
						.add(invoiceStateCessSpecificAmount);
				productDetails.setStatecessamount(stateCessAmount != null
						? GenUtil.formatCurrency(stateCessAmount)
						: AMOUNT_STRING);
				if (stateCessAmount != null) {
					tt5 = tt5.add(stateCessAmount);
				}
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

			parameters.put("AmtInWords", b2b.getInvoiceValueFC() != null
					? convertBigDecimalToWords(b2b.getInvValue().toString())
					: "");
			parameters.put("Remark", b2b.getInvoiceRemarks() != null
					? String.valueOf(b2b.getInvoiceRemarks()) : "");
			parameters.put("RoundOffValue", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");
			parameters.put("invoiceothercharges",
					b2b.getInvOtherCharges() != null
							? GenUtil.formatCurrency(b2b.getInvOtherCharges())
							: "0.00");
			parameters.put("invoicediscount",
					b2b.getUserDefinedField28() != null ? GenUtil
							.formatCurrency(b2b.getUserDefinedField28())
							: "0.00");
			parameters.put("InvoiceValue", b2b.getInvValue() != null
					? GenUtil.formatCurrency(b2b.getInvValue()) : "0.00");
			parameters.put("note", supplyType.containsKey(b2b.getSupplyType())
					? supplyType.get(b2b.getSupplyType()) : "");

			StringBuffer textdata = new StringBuffer();
			textdata.append("");

			parameters.put("TermNConditions", "");

			parameters.put("AccountDetails", b2b.getAccountDetail() != null
					? String.valueOf(b2b.getAccountDetail()) : " ");

			parameters.put("BankName", " ");

			parameters.put("BankAddress", " ");

			parameters.put("BranchOrIFSCCode", b2b.getBranchOrIFSCCode() != null
					? String.valueOf(b2b.getBranchOrIFSCCode()) : "");
			parameters.put("PayeeName", b2b.getPayeeName() != null
					? String.valueOf(b2b.getPayeeName()) : "");
			String signedqr = "";

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
						LOGGER.debug("Signed QR {}", signedqr);
					}

				} catch (IOException | SQLException e) {
					LOGGER.error(
							"Exception occured while getting the signed QR from clob.",
							e);
				}
			}

			parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));

			parameters.put("invoiceassessableamount",
					b2b.getInvAssessableAmt() != null
							? GenUtil.formatCurrency(b2b.getInvAssessableAmt())
							: "0.00");
			parameters.put("invoicecgstamount", b2b.getInvCgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCgstAmt()) : "0.00");
			parameters.put("invoicesgstamount", b2b.getInvSgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvSgstAmt()) : "0.00");
			parameters.put("invoiceigstamount", b2b.getInvIgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvIgstAmt()) : "0.00");
			parameters.put("invoicecessadvlamount",
					b2b.getInvCessAdvaloremAmt() != null ? GenUtil
							.formatCurrency(b2b.getInvCessAdvaloremAmt())
							: "0.00");
			parameters.put("invoicecessspecificamount",
					b2b.getInvCessSpecificAmt() != null ? GenUtil
							.formatCurrency(b2b.getInvCessSpecificAmt())
							: "0.00");
			parameters.put("totalothercharges",
					tt2 != null ? GenUtil.formatCurrency(tt2) : "0.00");
			parameters.put("totalitemamt",
					tt3 != null ? GenUtil.formatCurrency(tt3) : "0.00");
			parameters.put("totaldiscount",
					tt4 != null ? GenUtil.formatCurrency(tt4) : "0.00");
			parameters.put("invoicestatecesscamount",
					tt5 != null ? GenUtil.formatCurrency(tt5) : "0.00");

			parameters.put("PaymentDueDate", "");

			parameters.put("PaymentTerms",
					b2b.getPaymentTerms() != null
							? StringUtils.truncate(
									String.valueOf(b2b.getPaymentTerms()), 100)
							: "");
			parameters.put("totalproductvalue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			parameters.put("SubInvoiceValue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			LocalDate printedDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("PrintedDate",
					printedDate != null ? formatter2.format(printedDate) : "");
			parameters.put("PaymentInstruction",
					b2b.getPaymentInstruction() != null ? StringUtils.truncate(
							String.valueOf(b2b.getPaymentInstruction()), 100)
							: "");

			BigDecimal invoiceStateCessAdvaloremAmount = b2b
					.getInvStateCessAdvaloremAmt() != null
							? b2b.getInvStateCessAdvaloremAmt()
							: BigDecimal.ZERO;
			BigDecimal invoiceStateCessSpecificAmount = b2b
					.getInvStateCessSpecificAmt() != null
							? b2b.getInvStateCessSpecificAmt()
							: BigDecimal.ZERO;
			BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
					.add(invoiceStateCessSpecificAmount);

			parameters.put("StateCess", stateCessAmount != null
					? GenUtil.formatCurrency(stateCessAmount) : "0.00");

			parameters.put("CurrencyCode", "");
			parameters.put("InvoiceValueFC", "");

			if (b2b.getSupplyType().equals("EXPT")
					|| b2b.getSupplyType().equals("EXPWT")) {

				if (b2b.getInvoiceValueFC() != null) {
					parameters.put("CurrencyCode",
							"Invoice Value FC" + (b2b.getCurrencyCode() != null
									? " " + "(" + b2b.getCurrencyCode() + ")"
											+ " " + ":"
									: ":"));
					parameters.put("InvoiceValueFC",
							b2b.getInvoiceValueFC() != null ? GenUtil
									.formatCurrency(b2b.getInvoiceValueFC())
									: "");

				}
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

	public JasperPrint generateJasperPrint(GetIrnSezWopHeaderEntity b2b,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint, String docType) {
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
			String disptostatename = "";
			if (b2b.getDispatcherStateCode() != null) {
				disptostatename = statecodeRepository.findStateNameByCode(
						b2b.getDispatcherStateCode().toString());
			}
			String Shiptostatename = "";
			if (b2b.getShipToStateCode() != null) {
				Shiptostatename = statecodeRepository.findStateNameByCode(
						b2b.getShipToStateCode().toString());
			}
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
			parameters.put("ShipToAddress", shiptoAddress);

			String shiptoTradeName = (b2b.getShipToLegalName() != null
					? b2b.getShipToLegalName() : "");
			if (Strings.isNullOrEmpty(shiptoTradeName)) {
				shiptoTradeName = customerName;
			}
			parameters.put("ShipToTradeName", shiptoTradeName);
			String placeOfSupply5 = "";
			String shiptoState = "";
			if (b2b.getShipToStateCode() != null) {
				placeOfSupply5 = b2b.getShipToStateCode() + "-"
						+ Shiptostatename;

			}
			shiptoState = (b2b.getShipToPincode() != null
					? placeOfSupply5 + "," + b2b.getShipToPincode().toString()
					: "");
			if (Strings.isNullOrEmpty(shiptoState)) {
				shiptoState = customerState;
			}
			parameters.put("ShipToStateCodeNamePincode", shiptoState);

			String dispatcherAddress = (b2b.getDispatcherAddress1() != null
					? b2b.getDispatcherAddress1() + "," : "")
					+ (b2b.getDispatcherAddress2() != null
							? b2b.getDispatcherAddress2() + "," : "")
					+ (b2b.getDispatcherLocation() != null
							? b2b.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress;
			}
			parameters.put("DispatcherAddress", dispatcherAddress);
			String dispatcherTradeName = (b2b.getDispatcherTradeName() != null
					? b2b.getDispatcherTradeName() : "");
			if (Strings.isNullOrEmpty(dispatcherTradeName)) {
				dispatcherTradeName = supplierName;
			}
			parameters.put("DispatcherTradeName", dispatcherTradeName);

			String placeOfSupply0 = "";
			String disState0 = "";
			if (b2b.getDispatcherStateCode() != null) {
				placeOfSupply0 = b2b.getDispatcherStateCode() + "-"
						+ disptostatename;
			}
			disState0 = (b2b.getDispatcherPincode() != null ? placeOfSupply0
					+ "," + b2b.getDispatcherPincode().toString() : "");
			if (Strings.isNullOrEmpty(disState0)) {
				disState0 = supplierState;
			}
			parameters.put("DispatcherStateCodeNamePincode", disState0);

			List<GetIrnSezWopItemEntity> lineItems = getIrnSezwopLineRepo
					.findByHeaderId(b2b.getId());
			GSTNDetailEntity entity1 = gSTNDetailRepository
					.findRegDates(b2b.getCustomerGSTIN());
			String clientName = entity1.getRegisteredName() != null
					? entity1.getRegisteredName() : "";

			if ("INV".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Tax Invoice (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Tax Invoice");
				}
			} else if ("CR".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Credit Note (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Credit Note");
				}
			} else if ("DR".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Debit Note (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Debit Note");
				}
			}
			parameters.put("ClientName", clientName);
			parameters.put("RegisteredOffice",
					(entity1.getAddress1() != null
							? entity1.getAddress1() + "\n" : "")
							+ (entity1.getAddress2() != null
									? entity1.getAddress2() + "\n" : "")
							+ (entity1.getAddress3() != null
									? entity1.getAddress3() : ""));

			String suppliernameFor = "For" + " "
					+ (b2b.getSupplierLegalName() != null
							? b2b.getSupplierLegalName() : "");
			parameters.put("SupplierNameFor", suppliernameFor);

			parameters.put("SGSTIN",
					(!Strings.isNullOrEmpty(b2b.getSupplierGSTIN()))
							? b2b.getSupplierGSTIN() : "");
			parameters.put("SPAN", b2b.getSupplierGSTIN().substring(2, 12));
			parameters.put("SupplierPhone", b2b.getSupplierPhone() != null
					? b2b.getSupplierPhone() : "");
			parameters.put("SupplierEmail", b2b.getSupplierEmail() != null
					? b2b.getSupplierEmail() : "");
			parameters.put("SupplierFSSAI", b2b.getSupplierEmail() != null
					? b2b.getSupplierEmail() : "");
			parameters.put("irn", b2b.getIrn() != null ? b2b.getIrn() : "N/A");
			parameters.put("irnDate", b2b.getIrnDateTime() != null
					? (formatter1.format(b2b.getIrnDateTime())) : "N/A");
			parameters.put("InvoiceNo",
					b2b.getDocNum() != null ? b2b.getDocNum() : "");
			parameters.put("InvoiceDate", b2b.getDocDate() != null
					? formatter2.format(
							EYDateUtil.toISTDateTimeFromUTC(b2b.getDocDate()))
					: "");
			parameters
					.put("InvoicePeriodStartDate",
							b2b.getInvoicePeriodStartDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													b2b.getInvoicePeriodStartDate()))
									: "");
			parameters
					.put("InvoicePeriodEndDate",
							b2b.getInvoicePeriodEndDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													b2b.getInvoicePeriodEndDate()))
									: "");
			parameters.put("SupplierSiteDepotCode", "SupplierSiteDepotCode");
			parameters.put("SalesOrderNumber", b2b.getEWayBillNumber() != null
					? b2b.getEWayBillNumber().toString() : "");
			parameters.put("isTaxPayableOnReverse",
					REVERSECHARGE.containsKey(b2b.getReverseChargeFlag())
							? REVERSECHARGE.get(b2b.getReverseChargeFlag())
							: "");

			String custgstin = (b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("customergstin", custgstin);
			parameters.put("CustomerPhone", b2b.getCustomerPhone() != null
					? b2b.getCustomerPhone() : "");
			parameters.put("CustomerEmail", b2b.getCustomerEmail() != null
					? b2b.getCustomerEmail() : "");

			String placeOfSupply = "";
			if (b2b.getBillingPOS() != null) {
				String stateName1 = statecodeRepository
						.findStateNameByCode(b2b.getBillingPOS());
				placeOfSupply = stateName1 + "(" + b2b.getBillingPOS() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);

			String shiptState = (b2b.getShipToGSTIN() != null
					? b2b.getShipToGSTIN() : "");
			if (Strings.isNullOrEmpty(shiptState)) {
				shiptState = custgstin;
			}
			parameters.put("ShipToGSTINNo",
					b2b.getShipToGSTIN() != null ? b2b.getShipToGSTIN() : "");

			parameters.put("EWBNumber", (b2b.getEWayBillNumber() != null
					? b2b.getEWayBillNumber() + " " : "NA")
					+ (b2b.getEWayBillDate() != null
							? (formatter1.format(b2b.getEWayBillDate())) : ""));
			parameters.put("TransportMode", b2b.getTransportMode() != null
					? b2b.getTransportMode() : "");
			parameters.put("VehicleNo",
					b2b.getVehicleNo() != null ? b2b.getVehicleNo() : "");
			parameters.put("TransporterName", b2b.getTransporterName() != null
					? b2b.getTransporterName() : "");
			parameters.put("TransportDocNo",
					(b2b.getTransportDocNo() != null
							? b2b.getTransportDocNo() + " " : "")
							+ (b2b.getTransportDocDate() != null
									? b2b.getTransportDocDate() : ""));
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
			BigDecimal tt3 = new BigDecimal("0.00");
			BigDecimal tt4 = new BigDecimal("0.00");
			BigDecimal tt5 = new BigDecimal("0.00");
			for (GetIrnSezWopItemEntity line : lineItems) {
				GoodsProductDetails productDetails = new GoodsProductDetails();
				productDetails.setSlNo(String.valueOf(slNo));
				productDetails.setPrddesc(line.getProductSerialNumber() != null
						? line.getProductSerialNumber() : "");
				productDetails.setPrdname(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails
						.setHsn(line.getHsn() != null ? line.getHsn() : "");
				productDetails
						.setUqc(line.getUqc() != null ? line.getUqc() : "");
				if (line.getQuantity() == null) {
					productDetails.setQuantity(line.getQuantity() != null
							? line.getQuantity().toString() : "0");
				} else {
					productDetails.setQuantity(line.getQuantity().toString());
				}
				productDetails.setUnitprice(line.getUnitPrice() != null
						? GenUtil.formatCurrency(line.getUnitPrice())
						: AMOUNT_STRING);

				productDetails.setItemamount(line.getItemAmount() != null
						? GenUtil.formatCurrency(line.getItemAmount())
						: AMOUNT_STRING);
				if (line.getItemAmount() != null) {
					tt3 = tt3.add(line.getItemAmount());
				}
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

				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				productDetails.setCgstrate(String.valueOf(cgstRate));
				productDetails.setCgstamount(line.getCgstAmount() != null
						? GenUtil.formatCurrency(line.getCgstAmount())
						: AMOUNT_STRING);
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				productDetails.setSgstrate(String.valueOf(sgstRate));
				productDetails.setSgstamount(line.getSgstAmount() != null
						? GenUtil.formatCurrency(line.getSgstAmount())
						: AMOUNT_STRING);

				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				productDetails.setIgstrate(String.valueOf(igstRate));
				productDetails.setIgstamount(line.getIgstAmount() != null
						? GenUtil.formatCurrency(line.getIgstAmount())
						: AMOUNT_STRING);
				BigDecimal cessRate = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				productDetails.setCessadvlrate(String.valueOf(cessRate));
				productDetails
						.setCessadvlamount(line.getCessAdvaloremAmount() != null
								? GenUtil.formatCurrency(
										line.getCessAdvaloremAmount())
								: AMOUNT_STRING);
				BigDecimal cessSpec = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				productDetails.setCessspecificrate(String.valueOf(cessSpec));
				productDetails.setCessspecificamount(
						line.getCessSpecificAmount() != null
								? GenUtil.formatCurrency(
										line.getCessSpecificAmount())
								: AMOUNT_STRING);
				BigDecimal invoiceStateCessAdvaloremAmount = line
						.getStateCessAdvaloremAmount() != null
								? line.getStateCessAdvaloremAmount()
								: BigDecimal.ZERO;
				BigDecimal invoiceStateCessSpecificAmount = line
						.getStateCessSpecificAmount() != null
								? line.getStateCessSpecificAmount()
								: BigDecimal.ZERO;
				BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
						.add(invoiceStateCessSpecificAmount);
				productDetails.setStatecessamount(stateCessAmount != null
						? GenUtil.formatCurrency(stateCessAmount)
						: AMOUNT_STRING);
				if (stateCessAmount != null) {
					tt5 = tt5.add(stateCessAmount);
				}
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

			parameters.put("AmtInWords", b2b.getInvoiceValueFC() != null
					? convertBigDecimalToWords(b2b.getInvValue().toString())
					: "");
			parameters.put("Remark", b2b.getInvoiceRemarks() != null
					? String.valueOf(b2b.getInvoiceRemarks()) : "");
			parameters.put("RoundOffValue", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");
			parameters.put("invoiceothercharges",
					b2b.getInvOtherCharges() != null
							? GenUtil.formatCurrency(b2b.getInvOtherCharges())
							: "0.00");
			parameters.put("invoicediscount",
					b2b.getUserDefinedField28() != null ? GenUtil
							.formatCurrency(b2b.getUserDefinedField28())
							: "0.00");
			parameters.put("InvoiceValue", b2b.getInvValue() != null
					? GenUtil.formatCurrency(b2b.getInvValue()) : "0.00");
			parameters.put("note", supplyType.containsKey(b2b.getSupplyType())
					? supplyType.get(b2b.getSupplyType()) : "");

			StringBuffer textdata = new StringBuffer();
			textdata.append("");

			parameters.put("TermNConditions", "");

			parameters.put("AccountDetails", b2b.getAccountDetail() != null
					? String.valueOf(b2b.getAccountDetail()) : " ");

			parameters.put("BankName", " ");

			parameters.put("BankAddress", " ");

			parameters.put("BranchOrIFSCCode", b2b.getBranchOrIFSCCode() != null
					? String.valueOf(b2b.getBranchOrIFSCCode()) : "");
			parameters.put("PayeeName", b2b.getPayeeName() != null
					? String.valueOf(b2b.getPayeeName()) : "");
			String signedqr = "";

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
						LOGGER.debug("Signed QR {}", signedqr);
					}

				} catch (IOException | SQLException e) {
					LOGGER.error(
							"Exception occured while getting the signed QR from clob.",
							e);
				}
			}

			parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));
			parameters.put("invoiceassessableamount",
					b2b.getInvAssessableAmt() != null
							? GenUtil.formatCurrency(b2b.getInvAssessableAmt())
							: "0.00");
			parameters.put("invoicecgstamount", b2b.getInvCgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCgstAmt()) : "0.00");
			parameters.put("invoicesgstamount", b2b.getInvSgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvSgstAmt()) : "0.00");
			parameters.put("invoiceigstamount", b2b.getInvIgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvIgstAmt()) : "0.00");
			parameters.put("invoicecessadvlamount",
					b2b.getInvCessAdvaloremAmt() != null ? GenUtil
							.formatCurrency(b2b.getInvCessAdvaloremAmt())
							: "0.00");
			parameters.put("invoicecessspecificamount",
					b2b.getInvCessSpecificAmt() != null ? GenUtil
							.formatCurrency(b2b.getInvCessSpecificAmt())
							: "0.00");
			parameters.put("totalothercharges",
					tt2 != null ? GenUtil.formatCurrency(tt2) : "0.00");
			parameters.put("totalitemamt",
					tt3 != null ? GenUtil.formatCurrency(tt3) : "0.00");
			parameters.put("totaldiscount",
					tt4 != null ? GenUtil.formatCurrency(tt4) : "0.00");
			parameters.put("invoicestatecesscamount",
					tt5 != null ? GenUtil.formatCurrency(tt5) : "0.00");

			parameters.put("PaymentDueDate", "");

			parameters.put("PaymentTerms",
					b2b.getPaymentTerms() != null
							? StringUtils.truncate(
									String.valueOf(b2b.getPaymentTerms()), 100)
							: "");
			parameters.put("totalproductvalue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			parameters.put("SubInvoiceValue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			LocalDate printedDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("PrintedDate",
					printedDate != null ? formatter2.format(printedDate) : "");
			parameters.put("PaymentInstruction",
					b2b.getPaymentInstruction() != null ? StringUtils.truncate(
							String.valueOf(b2b.getPaymentInstruction()), 100)
							: "");

			BigDecimal invoiceStateCessAdvaloremAmount = b2b
					.getInvStateCessAdvaloremAmt() != null
							? b2b.getInvStateCessAdvaloremAmt()
							: BigDecimal.ZERO;
			BigDecimal invoiceStateCessSpecificAmount = b2b
					.getInvStateCessSpecificAmt() != null
							? b2b.getInvStateCessSpecificAmt()
							: BigDecimal.ZERO;
			BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
					.add(invoiceStateCessSpecificAmount);

			parameters.put("StateCess", stateCessAmount != null
					? GenUtil.formatCurrency(stateCessAmount) : "0.00");

			parameters.put("CurrencyCode", "");
			parameters.put("InvoiceValueFC", "");

			if (b2b.getSupplyType().equals("EXPT")
					|| b2b.getSupplyType().equals("EXPWT")) {

				if (b2b.getInvoiceValueFC() != null) {
					parameters.put("CurrencyCode",
							"Invoice Value FC" + (b2b.getCurrencyCode() != null
									? " " + "(" + b2b.getCurrencyCode() + ")"
											+ " " + ":"
									: ":"));
					parameters.put("InvoiceValueFC",
							b2b.getInvoiceValueFC() != null ? GenUtil
									.formatCurrency(b2b.getInvoiceValueFC())
									: "");

				}
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

	public JasperPrint generateJasperPrint(GetIrnExpWpHeaderEntity b2b,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint, String docType) {
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
			String disptostatename = "";
			if (b2b.getDispatcherStateCode() != null) {
				disptostatename = statecodeRepository.findStateNameByCode(
						b2b.getDispatcherStateCode().toString());
			}
			String Shiptostatename = "";
			if (b2b.getShipToStateCode() != null) {
				Shiptostatename = statecodeRepository.findStateNameByCode(
						b2b.getShipToStateCode().toString());
			}
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
			parameters.put("ShipToAddress", shiptoAddress);

			String shiptoTradeName = (b2b.getShipToLegalName() != null
					? b2b.getShipToLegalName() : "");
			if (Strings.isNullOrEmpty(shiptoTradeName)) {
				shiptoTradeName = customerName;
			}
			parameters.put("ShipToTradeName", shiptoTradeName);
			String placeOfSupply5 = "";
			String shiptoState = "";
			if (b2b.getShipToStateCode() != null) {
				placeOfSupply5 = b2b.getShipToStateCode() + "-"
						+ Shiptostatename;

			}
			shiptoState = (b2b.getShipToPincode() != null
					? placeOfSupply5 + "," + b2b.getShipToPincode().toString()
					: "");
			if (Strings.isNullOrEmpty(shiptoState)) {
				shiptoState = customerState;
			}
			parameters.put("ShipToStateCodeNamePincode", shiptoState);

			String dispatcherAddress = (b2b.getDispatcherAddress1() != null
					? b2b.getDispatcherAddress1() + "," : "")
					+ (b2b.getDispatcherAddress2() != null
							? b2b.getDispatcherAddress2() + "," : "")
					+ (b2b.getDispatcherLocation() != null
							? b2b.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress;
			}
			parameters.put("DispatcherAddress", dispatcherAddress);
			String dispatcherTradeName = (b2b.getDispatcherTradeName() != null
					? b2b.getDispatcherTradeName() : "");
			if (Strings.isNullOrEmpty(dispatcherTradeName)) {
				dispatcherTradeName = supplierName;
			}
			parameters.put("DispatcherTradeName", dispatcherTradeName);

			String placeOfSupply0 = "";
			String disState0 = "";
			if (b2b.getDispatcherStateCode() != null) {
				placeOfSupply0 = b2b.getDispatcherStateCode() + "-"
						+ disptostatename;
			}
			disState0 = (b2b.getDispatcherPincode() != null ? placeOfSupply0
					+ "," + b2b.getDispatcherPincode().toString() : "");
			if (Strings.isNullOrEmpty(disState0)) {
				disState0 = supplierState;
			}
			parameters.put("DispatcherStateCodeNamePincode", disState0);

			List<GetIrnExpWpItemEntity> lineItems = getIrnExpWpLineRepo
					.findByHeaderId(b2b.getId());
			GSTNDetailEntity entity1 = gSTNDetailRepository
					.findRegDates(b2b.getCustomerGSTIN());
			String clientName = entity1.getRegisteredName() != null
					? entity1.getRegisteredName() : "";

			if ("INV".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Tax Invoice (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Tax Invoice");
				}
			} else if ("CR".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Credit Note (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Credit Note");
				}
			} else if ("DR".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Debit Note (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Debit Note");
				}
			}
			parameters.put("ClientName", clientName);
			parameters.put("RegisteredOffice",
					(entity1.getAddress1() != null
							? entity1.getAddress1() + "\n" : "")
							+ (entity1.getAddress2() != null
									? entity1.getAddress2() + "\n" : "")
							+ (entity1.getAddress3() != null
									? entity1.getAddress3() : ""));

			String suppliernameFor = "For" + " "
					+ (b2b.getSupplierLegalName() != null
							? b2b.getSupplierLegalName() : "");
			parameters.put("SupplierNameFor", suppliernameFor);

			parameters.put("SGSTIN",
					(!Strings.isNullOrEmpty(b2b.getSupplierGSTIN()))
							? b2b.getSupplierGSTIN() : "");
			parameters.put("SPAN", b2b.getSupplierGSTIN().substring(2, 12));
			parameters.put("SupplierPhone", b2b.getSupplierPhone() != null
					? b2b.getSupplierPhone() : "");
			parameters.put("SupplierEmail", b2b.getSupplierEmail() != null
					? b2b.getSupplierEmail() : "");
			parameters.put("SupplierFSSAI", b2b.getSupplierEmail() != null
					? b2b.getSupplierEmail() : "");
			parameters.put("irn", b2b.getIrn() != null ? b2b.getIrn() : "N/A");
			parameters.put("irnDate", b2b.getIrnDateTime() != null
					? (formatter1.format(b2b.getIrnDateTime())) : "N/A");
			parameters.put("InvoiceNo",
					b2b.getDocNum() != null ? b2b.getDocNum() : "");
			parameters.put("InvoiceDate", b2b.getDocDate() != null
					? formatter2.format(
							EYDateUtil.toISTDateTimeFromUTC(b2b.getDocDate()))
					: "");
			parameters
					.put("InvoicePeriodStartDate",
							b2b.getInvoicePeriodStartDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													b2b.getInvoicePeriodStartDate()))
									: "");
			parameters
					.put("InvoicePeriodEndDate",
							b2b.getInvoicePeriodEndDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													b2b.getInvoicePeriodEndDate()))
									: "");
			parameters.put("SupplierSiteDepotCode", "SupplierSiteDepotCode");
			parameters.put("SalesOrderNumber", b2b.getEWayBillNumber() != null
					? b2b.getEWayBillNumber().toString() : "");
			parameters.put("isTaxPayableOnReverse",
					REVERSECHARGE.containsKey(b2b.getReverseChargeFlag())
							? REVERSECHARGE.get(b2b.getReverseChargeFlag())
							: "");

			String custgstin = (b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("customergstin", custgstin);
			parameters.put("CustomerPhone", b2b.getCustomerPhone() != null
					? b2b.getCustomerPhone() : "");
			parameters.put("CustomerEmail", b2b.getCustomerEmail() != null
					? b2b.getCustomerEmail() : "");

			String placeOfSupply = "";
			if (b2b.getBillingPOS() != null) {
				String stateName1 = statecodeRepository
						.findStateNameByCode(b2b.getBillingPOS());
				placeOfSupply = stateName1 + "(" + b2b.getBillingPOS() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);

			String shiptState = (b2b.getShipToGSTIN() != null
					? b2b.getShipToGSTIN() : "");
			if (Strings.isNullOrEmpty(shiptState)) {
				shiptState = custgstin;
			}
			parameters.put("ShipToGSTINNo",
					b2b.getShipToGSTIN() != null ? b2b.getShipToGSTIN() : "");

			parameters.put("EWBNumber", (b2b.getEWayBillNumber() != null
					? b2b.getEWayBillNumber() + " " : "NA")
					+ (b2b.getEWayBillDate() != null
							? (formatter1.format(b2b.getEWayBillDate())) : ""));
			parameters.put("TransportMode", b2b.getTransportMode() != null
					? b2b.getTransportMode() : "");
			parameters.put("VehicleNo",
					b2b.getVehicleNo() != null ? b2b.getVehicleNo() : "");
			parameters.put("TransporterName", b2b.getTransporterName() != null
					? b2b.getTransporterName() : "");
			parameters.put("TransportDocNo",
					(b2b.getTransportDocNo() != null
							? b2b.getTransportDocNo() + " " : "")
							+ (b2b.getTransportDocDate() != null
									? b2b.getTransportDocDate() : ""));
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
			BigDecimal tt3 = new BigDecimal("0.00");
			BigDecimal tt4 = new BigDecimal("0.00");
			BigDecimal tt5 = new BigDecimal("0.00");
			for (GetIrnExpWpItemEntity line : lineItems) {
				GoodsProductDetails productDetails = new GoodsProductDetails();
				productDetails.setSlNo(String.valueOf(slNo));
				productDetails.setPrddesc(line.getProductSerialNumber() != null
						? line.getProductSerialNumber() : "");
				productDetails.setPrdname(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails
						.setHsn(line.getHsn() != null ? line.getHsn() : "");
				productDetails
						.setUqc(line.getUqc() != null ? line.getUqc() : "");
				if (line.getQuantity() == null) {
					productDetails.setQuantity(line.getQuantity() != null
							? line.getQuantity().toString() : "0");
				} else {
					productDetails.setQuantity(line.getQuantity().toString());
				}
				productDetails.setUnitprice(line.getUnitPrice() != null
						? GenUtil.formatCurrency(line.getUnitPrice())
						: AMOUNT_STRING);

				productDetails.setItemamount(line.getItemAmount() != null
						? GenUtil.formatCurrency(line.getItemAmount())
						: AMOUNT_STRING);
				if (line.getItemAmount() != null) {
					tt3 = tt3.add(line.getItemAmount());
				}
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

				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				productDetails.setCgstrate(String.valueOf(cgstRate));
				productDetails.setCgstamount(line.getCgstAmount() != null
						? GenUtil.formatCurrency(line.getCgstAmount())
						: AMOUNT_STRING);
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				productDetails.setSgstrate(String.valueOf(sgstRate));
				productDetails.setSgstamount(line.getSgstAmount() != null
						? GenUtil.formatCurrency(line.getSgstAmount())
						: AMOUNT_STRING);

				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				productDetails.setIgstrate(String.valueOf(igstRate));
				productDetails.setIgstamount(line.getIgstAmount() != null
						? GenUtil.formatCurrency(line.getIgstAmount())
						: AMOUNT_STRING);
				BigDecimal cessRate = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				productDetails.setCessadvlrate(String.valueOf(cessRate));
				productDetails
						.setCessadvlamount(line.getCessAdvaloremAmount() != null
								? GenUtil.formatCurrency(
										line.getCessAdvaloremAmount())
								: AMOUNT_STRING);
				BigDecimal cessSpec = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				productDetails.setCessspecificrate(String.valueOf(cessSpec));
				productDetails.setCessspecificamount(
						line.getCessSpecificAmount() != null
								? GenUtil.formatCurrency(
										line.getCessSpecificAmount())
								: AMOUNT_STRING);
				BigDecimal invoiceStateCessAdvaloremAmount = line
						.getStateCessAdvaloremAmount() != null
								? line.getStateCessAdvaloremAmount()
								: BigDecimal.ZERO;
				BigDecimal invoiceStateCessSpecificAmount = line
						.getStateCessSpecificAmount() != null
								? line.getStateCessSpecificAmount()
								: BigDecimal.ZERO;
				BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
						.add(invoiceStateCessSpecificAmount);
				productDetails.setStatecessamount(stateCessAmount != null
						? GenUtil.formatCurrency(stateCessAmount)
						: AMOUNT_STRING);
				if (stateCessAmount != null) {
					tt5 = tt5.add(stateCessAmount);
				}
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

			parameters.put("AmtInWords", b2b.getInvoiceValueFC() != null
					? convertBigDecimalToWords(b2b.getInvValue().toString())
					: "");
			parameters.put("Remark", b2b.getInvoiceRemarks() != null
					? String.valueOf(b2b.getInvoiceRemarks()) : "");
			parameters.put("RoundOffValue", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");
			parameters.put("invoiceothercharges",
					b2b.getInvOtherCharges() != null
							? GenUtil.formatCurrency(b2b.getInvOtherCharges())
							: "0.00");
			parameters.put("invoicediscount",
					b2b.getUserDefinedField28() != null ? GenUtil
							.formatCurrency(b2b.getUserDefinedField28())
							: "0.00");
			parameters.put("InvoiceValue", b2b.getInvValue() != null
					? GenUtil.formatCurrency(b2b.getInvValue()) : "0.00");
			parameters.put("note", supplyType.containsKey(b2b.getSupplyType())
					? supplyType.get(b2b.getSupplyType()) : "");

			StringBuffer textdata = new StringBuffer();
			textdata.append("");

			parameters.put("TermNConditions", "");

			parameters.put("AccountDetails", b2b.getAccountDetail() != null
					? String.valueOf(b2b.getAccountDetail()) : " ");

			parameters.put("BankName", " ");

			parameters.put("BankAddress", " ");

			parameters.put("BranchOrIFSCCode", b2b.getBranchOrIFSCCode() != null
					? String.valueOf(b2b.getBranchOrIFSCCode()) : "");
			parameters.put("PayeeName", b2b.getPayeeName() != null
					? String.valueOf(b2b.getPayeeName()) : "");
			String signedqr = "";

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
						LOGGER.debug("Signed QR {}", signedqr);
					}

				} catch (IOException | SQLException e) {
					LOGGER.error(
							"Exception occured while getting the signed QR from clob.",
							e);
				}
			}

			parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));

			parameters.put("invoiceassessableamount",
					b2b.getInvAssessableAmt() != null
							? GenUtil.formatCurrency(b2b.getInvAssessableAmt())
							: "0.00");
			parameters.put("invoicecgstamount", b2b.getInvCgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCgstAmt()) : "0.00");
			parameters.put("invoicesgstamount", b2b.getInvSgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvSgstAmt()) : "0.00");
			parameters.put("invoiceigstamount", b2b.getInvIgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvIgstAmt()) : "0.00");
			parameters.put("invoicecessadvlamount",
					b2b.getInvCessAdvaloremAmt() != null ? GenUtil
							.formatCurrency(b2b.getInvCessAdvaloremAmt())
							: "0.00");
			parameters.put("invoicecessspecificamount",
					b2b.getInvCessSpecificAmt() != null ? GenUtil
							.formatCurrency(b2b.getInvCessSpecificAmt())
							: "0.00");
			parameters.put("totalothercharges",
					tt2 != null ? GenUtil.formatCurrency(tt2) : "0.00");
			parameters.put("totalitemamt",
					tt3 != null ? GenUtil.formatCurrency(tt3) : "0.00");
			parameters.put("totaldiscount",
					tt4 != null ? GenUtil.formatCurrency(tt4) : "0.00");
			parameters.put("invoicestatecesscamount",
					tt5 != null ? GenUtil.formatCurrency(tt5) : "0.00");

			parameters.put("PaymentDueDate", "");

			parameters.put("PaymentTerms",
					b2b.getPaymentTerms() != null
							? StringUtils.truncate(
									String.valueOf(b2b.getPaymentTerms()), 100)
							: "");
			parameters.put("totalproductvalue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			parameters.put("SubInvoiceValue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			LocalDate printedDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("PrintedDate",
					printedDate != null ? formatter2.format(printedDate) : "");
			parameters.put("PaymentInstruction",
					b2b.getPaymentInstruction() != null ? StringUtils.truncate(
							String.valueOf(b2b.getPaymentInstruction()), 100)
							: "");

			BigDecimal invoiceStateCessAdvaloremAmount = b2b
					.getInvStateCessAdvaloremAmt() != null
							? b2b.getInvStateCessAdvaloremAmt()
							: BigDecimal.ZERO;
			BigDecimal invoiceStateCessSpecificAmount = b2b
					.getInvStateCessSpecificAmt() != null
							? b2b.getInvStateCessSpecificAmt()
							: BigDecimal.ZERO;
			BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
					.add(invoiceStateCessSpecificAmount);

			parameters.put("StateCess", stateCessAmount != null
					? GenUtil.formatCurrency(stateCessAmount) : "0.00");

			parameters.put("CurrencyCode", "");
			parameters.put("InvoiceValueFC", "");

			if (b2b.getSupplyType().equals("EXPT")
					|| b2b.getSupplyType().equals("EXPWT")) {

				if (b2b.getInvoiceValueFC() != null) {
					parameters.put("CurrencyCode",
							"Invoice Value FC" + (b2b.getCurrencyCode() != null
									? " " + "(" + b2b.getCurrencyCode() + ")"
											+ " " + ":"
									: ":"));
					parameters.put("InvoiceValueFC",
							b2b.getInvoiceValueFC() != null ? GenUtil
									.formatCurrency(b2b.getInvoiceValueFC())
									: "");

				}
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

	public JasperPrint generateJasperPrint(GetIrnExpWopHeaderEntity b2b,
			Map<String, Object> parameters, String source,
			JasperPrint jasperPrint, String docType) {
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
			String disptostatename = "";
			if (b2b.getDispatcherStateCode() != null) {
				disptostatename = statecodeRepository.findStateNameByCode(
						b2b.getDispatcherStateCode().toString());
			}
			String Shiptostatename = "";
			if (b2b.getShipToStateCode() != null) {
				Shiptostatename = statecodeRepository.findStateNameByCode(
						b2b.getShipToStateCode().toString());
			}
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
			parameters.put("ShipToAddress", shiptoAddress);

			String shiptoTradeName = (b2b.getShipToLegalName() != null
					? b2b.getShipToLegalName() : "");
			if (Strings.isNullOrEmpty(shiptoTradeName)) {
				shiptoTradeName = customerName;
			}
			parameters.put("ShipToTradeName", shiptoTradeName);
			String placeOfSupply5 = "";
			String shiptoState = "";
			if (b2b.getShipToStateCode() != null) {
				placeOfSupply5 = b2b.getShipToStateCode() + "-"
						+ Shiptostatename;

			}
			shiptoState = (b2b.getShipToPincode() != null
					? placeOfSupply5 + "," + b2b.getShipToPincode().toString()
					: "");
			if (Strings.isNullOrEmpty(shiptoState)) {
				shiptoState = customerState;
			}
			parameters.put("ShipToStateCodeNamePincode", shiptoState);

			String dispatcherAddress = (b2b.getDispatcherAddress1() != null
					? b2b.getDispatcherAddress1() + "," : "")
					+ (b2b.getDispatcherAddress2() != null
							? b2b.getDispatcherAddress2() + "," : "")
					+ (b2b.getDispatcherLocation() != null
							? b2b.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress;
			}
			parameters.put("DispatcherAddress", dispatcherAddress);
			String dispatcherTradeName = (b2b.getDispatcherTradeName() != null
					? b2b.getDispatcherTradeName() : "");
			if (Strings.isNullOrEmpty(dispatcherTradeName)) {
				dispatcherTradeName = supplierName;
			}
			parameters.put("DispatcherTradeName", dispatcherTradeName);

			String placeOfSupply0 = "";
			String disState0 = "";
			if (b2b.getDispatcherStateCode() != null) {
				placeOfSupply0 = b2b.getDispatcherStateCode() + "-"
						+ disptostatename;
			}
			disState0 = (b2b.getDispatcherPincode() != null ? placeOfSupply0
					+ "," + b2b.getDispatcherPincode().toString() : "");
			if (Strings.isNullOrEmpty(disState0)) {
				disState0 = supplierState;
			}
			parameters.put("DispatcherStateCodeNamePincode", disState0);

			List<GetIrnExpWopItemEntity> lineItems = getIrnExpWopLineRepo
					.findByHeaderId(b2b.getId());
			GSTNDetailEntity entity1 = gSTNDetailRepository
					.findRegDates(b2b.getCustomerGSTIN());
			String clientName = entity1.getRegisteredName() != null
					? entity1.getRegisteredName() : "";

			if ("INV".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Tax Invoice (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Tax Invoice");
				}
			} else if ("CR".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Credit Note (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Credit Note");
				}
			} else if ("DR".equalsIgnoreCase(docType)) {
				if ("CNL".equalsIgnoreCase(b2b.getIrnStatus())) {
					parameters.put("PageTitle", "Debit Note (CANCELLED)");
				} else {
					parameters.put("PageTitle", "Debit Note");
				}
			}
			parameters.put("ClientName", clientName);
			parameters.put("RegisteredOffice",
					(entity1.getAddress1() != null
							? entity1.getAddress1() + "\n" : "")
							+ (entity1.getAddress2() != null
									? entity1.getAddress2() + "\n" : "")
							+ (entity1.getAddress3() != null
									? entity1.getAddress3() : ""));

			String suppliernameFor = "For" + " "
					+ (b2b.getSupplierLegalName() != null
							? b2b.getSupplierLegalName() : "");
			parameters.put("SupplierNameFor", suppliernameFor);

			parameters.put("SGSTIN",
					(!Strings.isNullOrEmpty(b2b.getSupplierGSTIN()))
							? b2b.getSupplierGSTIN() : "");
			parameters.put("SPAN", b2b.getSupplierGSTIN().substring(2, 12));
			parameters.put("SupplierPhone", b2b.getSupplierPhone() != null
					? b2b.getSupplierPhone() : "");
			parameters.put("SupplierEmail", b2b.getSupplierEmail() != null
					? b2b.getSupplierEmail() : "");
			parameters.put("SupplierFSSAI", b2b.getSupplierEmail() != null
					? b2b.getSupplierEmail() : "");
			parameters.put("irn", b2b.getIrn() != null ? b2b.getIrn() : "N/A");
			parameters.put("irnDate", b2b.getIrnDateTime() != null
					? (formatter1.format(b2b.getIrnDateTime())) : "N/A");
			parameters.put("InvoiceNo",
					b2b.getDocNum() != null ? b2b.getDocNum() : "");
			parameters.put("InvoiceDate", b2b.getDocDate() != null
					? formatter2.format(
							EYDateUtil.toISTDateTimeFromUTC(b2b.getDocDate()))
					: "");
			parameters
					.put("InvoicePeriodStartDate",
							b2b.getInvoicePeriodStartDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													b2b.getInvoicePeriodStartDate()))
									: "");
			parameters
					.put("InvoicePeriodEndDate",
							b2b.getInvoicePeriodEndDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													b2b.getInvoicePeriodEndDate()))
									: "");
			parameters.put("SupplierSiteDepotCode", "SupplierSiteDepotCode");
			parameters.put("SalesOrderNumber", b2b.getEWayBillNumber() != null
					? b2b.getEWayBillNumber().toString() : "");
			parameters.put("isTaxPayableOnReverse",
					REVERSECHARGE.containsKey(b2b.getReverseChargeFlag())
							? REVERSECHARGE.get(b2b.getReverseChargeFlag())
							: "");

			String custgstin = (b2b.getCustomerGSTIN() != null
					? b2b.getCustomerGSTIN() : "");
			parameters.put("customergstin", custgstin);
			parameters.put("CustomerPhone", b2b.getCustomerPhone() != null
					? b2b.getCustomerPhone() : "");
			parameters.put("CustomerEmail", b2b.getCustomerEmail() != null
					? b2b.getCustomerEmail() : "");

			String placeOfSupply = "";
			if (b2b.getBillingPOS() != null) {
				String stateName1 = statecodeRepository
						.findStateNameByCode(b2b.getBillingPOS());
				placeOfSupply = stateName1 + "(" + b2b.getBillingPOS() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);

			String shiptState = (b2b.getShipToGSTIN() != null
					? b2b.getShipToGSTIN() : "");
			if (Strings.isNullOrEmpty(shiptState)) {
				shiptState = custgstin;
			}
			parameters.put("ShipToGSTINNo",
					b2b.getShipToGSTIN() != null ? b2b.getShipToGSTIN() : "");

			parameters.put("EWBNumber", (b2b.getEWayBillNumber() != null
					? b2b.getEWayBillNumber() + " " : "NA")
					+ (b2b.getEWayBillDate() != null
							? (formatter1.format(b2b.getEWayBillDate())) : ""));
			parameters.put("TransportMode", b2b.getTransportMode() != null
					? b2b.getTransportMode() : "");
			parameters.put("VehicleNo",
					b2b.getVehicleNo() != null ? b2b.getVehicleNo() : "");
			parameters.put("TransporterName", b2b.getTransporterName() != null
					? b2b.getTransporterName() : "");
			parameters.put("TransportDocNo",
					(b2b.getTransportDocNo() != null
							? b2b.getTransportDocNo() + " " : "")
							+ (b2b.getTransportDocDate() != null
									? b2b.getTransportDocDate() : ""));
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
			BigDecimal tt3 = new BigDecimal("0.00");
			BigDecimal tt4 = new BigDecimal("0.00");
			BigDecimal tt5 = new BigDecimal("0.00");
			for (GetIrnExpWopItemEntity line : lineItems) {
				GoodsProductDetails productDetails = new GoodsProductDetails();
				productDetails.setSlNo(String.valueOf(slNo));
				productDetails.setPrddesc(line.getProductSerialNumber() != null
						? line.getProductSerialNumber() : "");
				productDetails.setPrdname(line.getProductDescription() != null
						? line.getProductDescription() : "");
				productDetails
						.setHsn(line.getHsn() != null ? line.getHsn() : "");
				productDetails
						.setUqc(line.getUqc() != null ? line.getUqc() : "");
				if (line.getQuantity() == null) {
					productDetails.setQuantity(line.getQuantity() != null
							? line.getQuantity().toString() : "0");
				} else {
					productDetails.setQuantity(line.getQuantity().toString());
				}
				productDetails.setUnitprice(line.getUnitPrice() != null
						? GenUtil.formatCurrency(line.getUnitPrice())
						: AMOUNT_STRING);

				productDetails.setItemamount(line.getItemAmount() != null
						? GenUtil.formatCurrency(line.getItemAmount())
						: AMOUNT_STRING);
				if (line.getItemAmount() != null) {
					tt3 = tt3.add(line.getItemAmount());
				}
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

				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				productDetails.setCgstrate(String.valueOf(cgstRate));
				productDetails.setCgstamount(line.getCgstAmount() != null
						? GenUtil.formatCurrency(line.getCgstAmount())
						: AMOUNT_STRING);
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				productDetails.setSgstrate(String.valueOf(sgstRate));
				productDetails.setSgstamount(line.getSgstAmount() != null
						? GenUtil.formatCurrency(line.getSgstAmount())
						: AMOUNT_STRING);

				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				productDetails.setIgstrate(String.valueOf(igstRate));
				productDetails.setIgstamount(line.getIgstAmount() != null
						? GenUtil.formatCurrency(line.getIgstAmount())
						: AMOUNT_STRING);
				BigDecimal cessRate = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				productDetails.setCessadvlrate(String.valueOf(cessRate));
				productDetails
						.setCessadvlamount(line.getCessAdvaloremAmount() != null
								? GenUtil.formatCurrency(
										line.getCessAdvaloremAmount())
								: AMOUNT_STRING);
				BigDecimal cessSpec = line.getCessAdvaloremRate() != null
						? line.getCessAdvaloremRate() : BigDecimal.ZERO;
				productDetails.setCessspecificrate(String.valueOf(cessSpec));
				productDetails.setCessspecificamount(
						line.getCessSpecificAmount() != null
								? GenUtil.formatCurrency(
										line.getCessSpecificAmount())
								: AMOUNT_STRING);
				BigDecimal invoiceStateCessAdvaloremAmount = line
						.getStateCessAdvaloremAmount() != null
								? line.getStateCessAdvaloremAmount()
								: BigDecimal.ZERO;
				BigDecimal invoiceStateCessSpecificAmount = line
						.getStateCessSpecificAmount() != null
								? line.getStateCessSpecificAmount()
								: BigDecimal.ZERO;
				BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
						.add(invoiceStateCessSpecificAmount);
				productDetails.setStatecessamount(stateCessAmount != null
						? GenUtil.formatCurrency(stateCessAmount)
						: AMOUNT_STRING);
				if (stateCessAmount != null) {
					tt5 = tt5.add(stateCessAmount);
				}
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

			parameters.put("AmtInWords", b2b.getInvoiceValueFC() != null
					? convertBigDecimalToWords(b2b.getInvValue().toString())
					: "");
			parameters.put("Remark", b2b.getInvoiceRemarks() != null
					? String.valueOf(b2b.getInvoiceRemarks()) : "");
			parameters.put("RoundOffValue", b2b.getRoundOff() != null
					? GenUtil.formatCurrency(b2b.getRoundOff()) : "0.00");
			parameters.put("invoiceothercharges",
					b2b.getInvOtherCharges() != null
							? GenUtil.formatCurrency(b2b.getInvOtherCharges())
							: "0.00");
			parameters.put("invoicediscount",
					b2b.getUserDefinedField28() != null ? GenUtil
							.formatCurrency(b2b.getUserDefinedField28())
							: "0.00");
			parameters.put("InvoiceValue", b2b.getInvValue() != null
					? GenUtil.formatCurrency(b2b.getInvValue()) : "0.00");
			parameters.put("note", supplyType.containsKey(b2b.getSupplyType())
					? supplyType.get(b2b.getSupplyType()) : "");

			StringBuffer textdata = new StringBuffer();
			textdata.append("");

			parameters.put("TermNConditions", "");

			parameters.put("AccountDetails", b2b.getAccountDetail() != null
					? String.valueOf(b2b.getAccountDetail()) : " ");

			parameters.put("BankName", " ");

			parameters.put("BankAddress", " ");

			parameters.put("BranchOrIFSCCode", b2b.getBranchOrIFSCCode() != null
					? String.valueOf(b2b.getBranchOrIFSCCode()) : "");
			parameters.put("PayeeName", b2b.getPayeeName() != null
					? String.valueOf(b2b.getPayeeName()) : "");
			String signedqr = "";

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
						LOGGER.debug("Signed QR {}", signedqr);
					}

				} catch (IOException | SQLException e) {
					LOGGER.error(
							"Exception occured while getting the signed QR from clob.",
							e);
				}
			}

			parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));

			parameters.put("invoiceassessableamount",
					b2b.getInvAssessableAmt() != null
							? GenUtil.formatCurrency(b2b.getInvAssessableAmt())
							: "0.00");
			parameters.put("invoicecgstamount", b2b.getInvCgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvCgstAmt()) : "0.00");
			parameters.put("invoicesgstamount", b2b.getInvSgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvSgstAmt()) : "0.00");
			parameters.put("invoiceigstamount", b2b.getInvIgstAmt() != null
					? GenUtil.formatCurrency(b2b.getInvIgstAmt()) : "0.00");
			parameters.put("invoicecessadvlamount",
					b2b.getInvCessAdvaloremAmt() != null ? GenUtil
							.formatCurrency(b2b.getInvCessAdvaloremAmt())
							: "0.00");
			parameters.put("invoicecessspecificamount",
					b2b.getInvCessSpecificAmt() != null ? GenUtil
							.formatCurrency(b2b.getInvCessSpecificAmt())
							: "0.00");
			parameters.put("totalothercharges",
					tt2 != null ? GenUtil.formatCurrency(tt2) : "0.00");
			parameters.put("totalitemamt",
					tt3 != null ? GenUtil.formatCurrency(tt3) : "0.00");
			parameters.put("totaldiscount",
					tt4 != null ? GenUtil.formatCurrency(tt4) : "0.00");
			parameters.put("invoicestatecesscamount",
					tt5 != null ? GenUtil.formatCurrency(tt5) : "0.00");

			parameters.put("PaymentDueDate", "");

			parameters.put("PaymentTerms",
					b2b.getPaymentTerms() != null
							? StringUtils.truncate(
									String.valueOf(b2b.getPaymentTerms()), 100)
							: "");
			parameters.put("totalproductvalue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			parameters.put("SubInvoiceValue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			LocalDate printedDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("PrintedDate",
					printedDate != null ? formatter2.format(printedDate) : "");
			parameters.put("PaymentInstruction",
					b2b.getPaymentInstruction() != null ? StringUtils.truncate(
							String.valueOf(b2b.getPaymentInstruction()), 100)
							: "");

			BigDecimal invoiceStateCessAdvaloremAmount = b2b
					.getInvStateCessAdvaloremAmt() != null
							? b2b.getInvStateCessAdvaloremAmt()
							: BigDecimal.ZERO;
			BigDecimal invoiceStateCessSpecificAmount = b2b
					.getInvStateCessSpecificAmt() != null
							? b2b.getInvStateCessSpecificAmt()
							: BigDecimal.ZERO;
			BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
					.add(invoiceStateCessSpecificAmount);

			parameters.put("StateCess", stateCessAmount != null
					? GenUtil.formatCurrency(stateCessAmount) : "0.00");

			parameters.put("CurrencyCode", "");
			parameters.put("InvoiceValueFC", "");

			if (b2b.getSupplyType().equals("EXPT")
					|| b2b.getSupplyType().equals("EXPWT")) {

				if (b2b.getInvoiceValueFC() != null) {
					parameters.put("CurrencyCode",
							"Invoice Value FC" + (b2b.getCurrencyCode() != null
									? " " + "(" + b2b.getCurrencyCode() + ")"
											+ " " + ":"
									: ":"));
					parameters.put("InvoiceValueFC",
							b2b.getInvoiceValueFC() != null ? GenUtil
									.formatCurrency(b2b.getInvoiceValueFC())
									: "");

				}
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

}
