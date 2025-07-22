/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.io.File;
import java.math.BigDecimal;
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

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.EinvoiceRepository;
import com.ey.advisory.app.data.repositories.client.OutwardTransDocumentRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.QRcodeGenerator;
import com.ey.advisory.einv.client.EinvoiceEntity;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Component("EInvoicePDFGenerationReportImpl")
@Slf4j
public class EInvoicePDFGenerationReportImpl implements EinvoicePdfReport {

	private static final String AMOUNT_STRING = "0.00";

	@Autowired
	QRcodeGenerator qRcodeGenerator;

	@Autowired
	private OutwardTransDocumentRepository outwardTransDocumentRepo;

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;

	@Autowired
	@Qualifier("EinvoiceRepository")
	EinvoiceRepository einv;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");
	static DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	final static Map<String, String> docCateg = ImmutableMap.of("REG",
			"Regular", "DIS", "Bill from Dispatch from", "SHP",
			"Bill to ship to", "CMB", "Comb of 2 & 3");

	final static Map<String, String> docType = ImmutableMap
			.<String, String>builder().put("INV", "Invoice")
			.put("RNV", "Revised Invoice").put("CR", "Credit Note")
			.put("RCR", "Revised Credit Note").put("DR", "Debit Note")
			.put("RDR", "Revised Debit note").put("DLC", "Delivery Challan")
			.put("BOE", "Bill of Entry").put("OTH", "Others")
			.put("BOS", "Bill of Supply").build();

	final static Map<String, String> supplyType = ImmutableMap
			.<String, String>builder().put("TAX", "B2B")
			.put("DXP", "Deemed Exports").put("DTA", "B2B")
			.put("SEZWP", "SEZ with Payment")
			.put("SEZWOP", "SEZ without Payment")
			.put("EXPT", "Exports with payment")
			.put("EXPWT", "Exports without Payment").build();

	@Override
	public JasperPrint generateEinvoiceSummaryPdfReport(String id, String docNo,
			String sgstin) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<>();
		String source = "jasperReports/EInvoice_Summary.jrxml";
		try {
			Optional<OutwardTransDocument> currOutward = outwardTransDocumentRepo
					.findById(Long.parseLong(id));
			if (!currOutward.isPresent()) {
				String errMsg = String.format(
						"Invoice %s is not available in the system", id);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			OutwardTransDocument out = currOutward.get();
			String irn = out.getIrnResponse();
			EinvoiceEntity einvoiceEntity = einv.findByIrn(irn);
			Long originalDocId = einvoiceEntity.getDocHeaderId();
			if (originalDocId != Long.parseLong(id)) {
				Optional<OutwardTransDocument> originalOutward = outwardTransDocumentRepo
						.findById(originalDocId);
				out = originalOutward.get();
			}

			List<OutwardTransDocLineItem> lineItems = out.getLineItems();
			GSTNDetailEntity entity1 = gSTNDetailRepository
					.findRegDates(out.getSgstin());
			String clientName = entity1.getRegisteredName() != null
					? entity1.getRegisteredName() : "";
			String regAddress1 = entity1.getAddress1() != null
					? entity1.getAddress1() : "";
			String regAddress2 = entity1.getAddress2() != null
					? entity1.getAddress2() : "";
			String regAddress3 = entity1.getAddress3() != null
					? entity1.getAddress3() : "";
			parameters.put("ClientName", clientName);
			parameters.put("RegisteredAddress1", regAddress1);
			parameters.put("RegisteredAddress2", regAddress2);
			parameters.put("RegisteredAddress3", regAddress3);
			parameters.put("IRN",
					out.getIrnResponse() != null ? out.getIrnResponse() : "");
			parameters.put("ACKNo", out.getAckNum() != null ? out.getAckNum()
					: "INVALID ACKNO");
			parameters.put("ACKDate", out.getAckDate() != null
                    ? (formatter1.format(out.getAckDate())) : "");
			/*parameters.put("ACKDate", out.getAckDate() != null
					? (formatter1.format(
							EYDateUtil.toISTDateTimeFromUTC(out.getAckDate())))
					: "");*/
			parameters.put("Category",
					supplyType.containsKey(out.getSupplyType())
							? supplyType.get(out.getSupplyType()) : "");
			parameters.put("DocumentNo",
					out.getDocNo() != null ? out.getDocNo() : "");
			parameters.put("DocumentDate", out.getDocDate() != null
					? formatter2.format(
							EYDateUtil.toISTDateTimeFromUTC(out.getDocDate()))
					: "");
			parameters.put("DocumentType", docType.containsKey(out.getDocType())
					? docType.get(out.getDocType()) : "");
			parameters.put("IGSTOnIntra", out.getSection7OfIgstFlag() != null
					? out.getSection7OfIgstFlag() : "No");
			String transType = out.getTransactionType();
			parameters.put("generatedBy", ("O".equalsIgnoreCase(transType))
					? out.getSgstin() : out.getCgstin());
			LocalDate currentDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("CurrentDate",
					currentDate != null ? formatter2.format(currentDate) : "");

			List<String> gstins = new ArrayList<>();

			if (out.getSgstin() != null)
				gstins.add(out.getSgstin());
			if (out.getCgstin() != null)
				gstins.add(out.getCgstin());
			if (out.getDispatcherGstin() != null)
				gstins.add(out.getDispatcherGstin());

			Map<String, String> stateNamesMap = entityService
					.getStateNames(gstins);

			parameters.put("SupplierName", out.getSupplierLegalName() != null
					? out.getSupplierLegalName() : "");
			parameters.put("SupplierGSTIN",
					(!Strings.isNullOrEmpty(out.getSgstin())) ? out.getSgstin()
							: "");
			parameters.put("SupplierAdd",
					(out.getSupplierBuildingNumber() != null
							? out.getSupplierBuildingNumber() + "\n" : "")
							+ (out.getSupplierBuildingName() != null
									? out.getSupplierBuildingName() + "\n" : "")
							+ (out.getSupplierLocation() != null
									? out.getSupplierLocation() : ""));
			parameters.put("SupplierAdd1",
					out.getSupplierBuildingNumber() != null
							? out.getSupplierBuildingNumber() : "");
			parameters.put("SupplierAdd2", out.getSupplierBuildingName() != null
					? out.getSupplierBuildingName() : "");
			parameters.put("SupplierLocation", out.getSupplierLocation() != null
					? out.getSupplierLocation() : "");
			String sgstinState = stateNamesMap.containsKey(out.getSgstin())
					? stateNamesMap.get(out.getSgstin()) : "";
			parameters.put("SupplierStateCodeNamePinCode",
					out.getSupplierPincode() != null ? sgstinState + ","
							+ out.getSupplierPincode().toString() : "");
			parameters.put("SupplierMobEmail",
					(out.getSupplierPhone() != null
							? out.getSupplierPhone() + "," : "")
							+ (out.getSupplierEmail() != null
									? out.getSupplierEmail() : ""));

			parameters.put("CustomerTradeName", out.getCustOrSuppName() != null
					? out.getCustOrSuppName() : "");
			parameters.put("CustomerGSTIN",
					out.getCgstin() != null ? out.getCgstin() : "");
			parameters.put("CustomerAdd",
					(out.getCustOrSuppAddress1() != null
							? out.getCustOrSuppAddress1() + "\n" : "")
							+ (out.getCustOrSuppAddress2() != null
									? out.getCustOrSuppAddress2() + "\n" : "")
							+ (out.getCustOrSuppAddress4() != null
									? out.getCustOrSuppAddress4() : ""));
			parameters.put("CustomerAdd1", out.getCustOrSuppAddress1() != null
					? out.getCustOrSuppAddress1() : "");
			parameters.put("CustomerAdd2", out.getCustOrSuppAddress2() != null
					? out.getCustOrSuppAddress2() : "");
			parameters.put("CustomerLocation",
					out.getCustOrSuppAddress4() != null
							? out.getCustOrSuppAddress4() : "");
			String cgstinState = stateNamesMap.containsKey(out.getCgstin())
					? stateNamesMap.get(out.getCgstin()) : "";
			parameters.put("CustomerStateCodeNamePinCode",
					out.getCustomerPincode() != null ? cgstinState + ","
							+ out.getCustomerPincode().toString() : "");
			parameters.put("CustomerMobEmail",
					(out.getCustomerPhone() != null
							? out.getCustomerPhone() + "," : "")
							+ (out.getCustomerEmail() != null
									? out.getCustomerEmail() + "," : ""));

			String placeOfSupply = "";
			if (out.getPos() != null) {
				String stateName = statecodeRepository
						.findStateNameByCode(out.getPos());
				placeOfSupply = stateName + "(" + out.getPos() + ")";
			}

			parameters.put("PlaceOfSupply", placeOfSupply);

			List<EInvoiceProductDetails> eInvoiceProductDetails = new ArrayList<>();
			int slno = 1;
			for (OutwardTransDocLineItem line : lineItems) {
				EInvoiceProductDetails productDetails = new EInvoiceProductDetails();
				productDetails.setSlno(String.valueOf(slno));
				productDetails.setPrddesc(line.getItemDescription() != null
						? line.getItemDescription() : "");
				productDetails.setHsn(line.getHsnSac() != null ? line.getHsnSac() : "");
				productDetails.setQuantity(
						line.getQty() != null ? line.getQty().toString() : "0");
				productDetails.setUnit(line.getUom() != null ? line.getUom() : "");
				productDetails.setUnitprice(line.getUnitPrice() != null
						? String.valueOf(line.getUnitPrice()) : AMOUNT_STRING);
				productDetails.setItemdiscount(line.getItemDiscount() != null
						? String.valueOf(line.getItemDiscount())
						: AMOUNT_STRING);
				productDetails.setTaxableamount(line.getTaxableValue() != null
						? String.valueOf(line.getTaxableValue())
						: AMOUNT_STRING);
				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				BigDecimal cessRate = line.getCessRateAdvalorem() != null
						? line.getCessRateAdvalorem() : BigDecimal.ZERO;
				BigDecimal gst = igstRate.add(cgstRate).add(sgstRate);
				String taxRate = String
						.valueOf(gst != null ? gst.intValue() : "0") + " + "
						+ String.valueOf(cessRate.intValue()) + " | "
						+ (line.getStateCessRate() != null
								? String.valueOf(
										line.getStateCessRate().intValue())
								: "0")
						+ " + "
						+ (line.getCessAmountSpecific() != null
								? String.valueOf(line.getCessAmountSpecific())
								: "0");
				productDetails.setTaxrate(taxRate);
				productDetails.setOthercharges(line.getOtherValues() != null
						? String.valueOf(line.getOtherValues())
						: AMOUNT_STRING);
				productDetails.setTotalamount(line.getTotalItemAmount() != null
						? String.valueOf(line.getTotalItemAmount())
						: AMOUNT_STRING);
				eInvoiceProductDetails.add(productDetails);
				slno++;
			}
			parameters.put("ItemDetails",
					new JRBeanCollectionDataSource(eInvoiceProductDetails));
			List<EInvoiceAmountSummary> amountSummaries = new ArrayList<>();

			EInvoiceAmountSummary amountSummary = new EInvoiceAmountSummary();
			amountSummary
					.setTotaltaxamt(out.getInvoiceAssessableAmount() != null
							? String.valueOf(out.getInvoiceAssessableAmount())
							: AMOUNT_STRING);
			amountSummary.setInvcgstamt(out.getInvoiceCgstAmount() != null
					? String.valueOf(out.getInvoiceCgstAmount())
					: AMOUNT_STRING);
			amountSummary.setInvsgstamt(out.getInvoiceSgstAmount() != null
					? String.valueOf(out.getInvoiceSgstAmount())
					: AMOUNT_STRING);
			amountSummary.setInvigstamt(out.getInvoiceIgstAmount() != null
					? String.valueOf(out.getInvoiceIgstAmount())
					: AMOUNT_STRING);

			BigDecimal invoiceCessAdvaloremAmount = out
					.getInvoiceCessAdvaloremAmount() != null
							? out.getInvoiceCessAdvaloremAmount()
							: BigDecimal.ZERO;
			BigDecimal invoiceCessSpecificAmount = out
					.getInvoiceCessSpecificAmount() != null
							? out.getInvoiceCessSpecificAmount()
							: BigDecimal.ZERO;
			BigDecimal cessAmount = invoiceCessAdvaloremAmount
					.add(invoiceCessSpecificAmount);
			amountSummary.setInvcessamt(cessAmount != null
					? String.valueOf(cessAmount) : AMOUNT_STRING);

			BigDecimal invoiceStateCessAdvaloremAmount = out
					.getInvoiceStateCessAmount() != null
							? out.getInvoiceStateCessAmount() : BigDecimal.ZERO;
			BigDecimal invoiceStateCessSpecificAmount = out
					.getInvStateCessSpecificAmt() != null
							? out.getInvStateCessSpecificAmt()
							: BigDecimal.ZERO;
			BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
					.add(invoiceStateCessSpecificAmount);
			amountSummary.setInvscessamt(stateCessAmount != null
					? String.valueOf(stateCessAmount) : AMOUNT_STRING);
			amountSummary.setInvroundoffamt(out.getRoundOff() != null
					? String.valueOf(out.getRoundOff()) : AMOUNT_STRING);
			amountSummary.setInvotheramt(out.getInvoiceOtherCharges() != null
					? String.valueOf(out.getInvoiceOtherCharges())
					: AMOUNT_STRING);
			amountSummary.setInvtotalamt(out.getDocAmount() != null
					? String.valueOf(out.getDocAmount()) : AMOUNT_STRING);

			amountSummaries.add(amountSummary);

			parameters.put("AmountDetails",
					new JRBeanCollectionDataSource(amountSummaries));

			parameters.put("subType", out.getSubSupplyType());
			parameters.put("documentNumber", out.getDocNo());

			parameters.put("totalTaxableValue", out.getTaxableValue() != null
					? out.getTaxableValue() : BigDecimal.ZERO);
			parameters.put("cgstAmount", out.getCgstAmount() != null
					? out.getCgstAmount() : BigDecimal.ZERO);
			parameters.put("sgstAmount", out.getSgstAmount() != null
					? out.getSgstAmount() : BigDecimal.ZERO);
			parameters.put("igstAmount", out.getIgstAmount() != null
					? out.getIgstAmount() : BigDecimal.ZERO);
			parameters.put("cessAmount", out.getCessAmountAdvalorem() != null
					? out.getCessAmountAdvalorem() : BigDecimal.ZERO);

			String signedqr = "";
			if (einvoiceEntity != null
					&& einvoiceEntity.getSignedQR() != null) {
				LOGGER.debug("Signed QR is Available for doc ID {}",
						out.getId());
				signedqr = einvoiceEntity.getSignedQR();
			}
			parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));
			/*parameters.put("barImg", qRcodeGenerator
					.getImage(out.getAckNum() != null ? out.getAckNum() : ""));*/

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
