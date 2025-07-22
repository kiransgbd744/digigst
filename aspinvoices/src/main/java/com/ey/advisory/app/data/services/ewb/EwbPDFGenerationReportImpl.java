/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.entities.client.EwbEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.EwbMultiVehicleDetailsRepository;
import com.ey.advisory.app.data.repositories.client.EwbMultiVehicleRepository;
import com.ey.advisory.app.data.repositories.client.EwbRepository;
import com.ey.advisory.app.data.repositories.client.OutwardTransDocumentRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.QRcodeGenerator;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.client.domain.EwbLifecycleEntity;
import com.ey.advisory.ewb.client.domain.EwbMultiVehicleDetailsEntity;
import com.ey.advisory.ewb.client.domain.EwbMultiVehicleEntity;
import com.ey.advisory.ewb.client.repositories.EwbLifecycleRepository;
import com.ey.advisory.ewb.common.EyEwbCommonUtil;
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
 * @author Arun KA
 *
 */
@Slf4j
@Component("EwbPDFGenerationReportImpl")
public class EwbPDFGenerationReportImpl implements EwbPDFGenerationReport {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ey.advisory.app.data.services.ewb.
	 * EwbPDFGenerationReport#generatePdfReport(java.lang.String)
	 */

	@Autowired
	private EwbRepository ewbRepo;

	@Autowired
	@Qualifier("EwbMultiVehicleRepository")
	private EwbMultiVehicleRepository eWbrepoMultiVehicleRepo;

	@Autowired
	@Qualifier("EwbMultiVehicleDetailsRepository")
	private EwbMultiVehicleDetailsRepository ewbMultiVehicleDetailsRepo;

	@Autowired
	private EwbLifecycleRepository ewbLifecycleRepo;

	@Autowired
	QRcodeGenerator qRcodeGenerator;

	@Autowired
	private OutwardTransDocumentRepository outwardTransDocumentRepo;

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	StatecodeRepository StatecodeRepo;

	DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy HH:mm:ss");
	DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
			.<String, String>builder().put("TAX", "Taxable supplies")
			.put("SEZWP", "Supplies to SEZ With Payment")
			.put("SEZWOP", "Supplies to SEZ Without Payment")
			.put("NON", "Non taxable").put("EXT", "Exempt")
			.put("DXP", "Deemed Export").put("NIL", "Taxable at Nil rate")
			.put("EXPT", "Export with Payment of Tax")
			.put("EXPWT", "Export without Payment of Tax")
			.put("NSY", "Non-supply transactions")
			.put("CAN", "Cancelled invoice")
			.put("SCH3", "Schedule III No Supply")
			.put("DTA", "Supplies by SEZ to DTA").build();

	@Override
	public JasperPrint generatePdfReport(String ewbNo) {

		JasperPrint jasperPrint = null;
		String source = "jasperReports/detailedewbprint.jrxml";

		EwbEntity ewb = ewbRepo.findByEwbNum(ewbNo);
		Optional<OutwardTransDocument> outward = outwardTransDocumentRepo
				.findById(ewb.getDocHeaderId());

		OutwardTransDocument out = outward.get();
		List<OutwardTransDocLineItem> lineItem = out.getLineItems();

		List<EwbLifecycleEntity> ewbLifeCycle = ewbLifecycleRepo
				.findByEwbNum(ewbNo);
		try {
			Map<String, Object> parameters = new HashMap<>();

			parameters.put("irn", Strings.isNullOrEmpty(out.getIrnResponse())
					? "N/A" : out.getIrnResponse());
			parameters.put("irnDate", out.getAckDate() != null
					? formatter1.format(out.getAckDate()).toString() : "N/A");
			parameters.put("ackNo", Strings.isNullOrEmpty(out.getAckNum())
					? "N/A" : out.getAckNum());

			String transType = ewb.getTransitType() != null
					? ewb.getTransitType() : out.getTransactionType();
			parameters.put("generatedDate", ewb.getEwbDate() != null
					? formatter1.format(ewb.getEwbDate()).toString() : "");
			parameters.put("generatedBy", ("O".equalsIgnoreCase(transType))
					? out.getSgstin() : out.getCgstin());
			parameters.put("documentDetails",
					supplyType.get(out.getSupplyType()) + "-"
							+ docType.get(out.getDocType()) + "-"
							+ out.getDocNo() + "-" + formatter2
									.format(out.getDocDate().atStartOfDay()));
			parameters.put("currentDate", new Date());

			List<String> gstins = new ArrayList<>();

			if (out.getSgstin() != null)
				gstins.add(out.getSgstin());
			if (out.getCgstin() != null)
				gstins.add(out.getCgstin());
			if (out.getDispatcherGstin() != null)
				gstins.add(out.getDispatcherGstin());

			Map<String, String> stateNamesMap = entityService
					.getStateNames(gstins);

			parameters.put("supplierName", out.getSupplierTradeName());
			parameters.put("supplierGstin", "GSTIN: " + out.getSgstin());
			parameters.put("supplierAddress",
					out.getSupplierBuildingNumber() + ","
							+ out.getSupplierBuildingName() + ","
							+ out.getSupplierLocation());
			parameters.put("supplierPin", out.getSupplierPincode() != null
					? out.getSupplierPincode().toString() : "");
			parameters.put("supplierPlace", stateNamesMap.get(out.getSgstin()));

			parameters.put("recipientName", out.getCustomerTradeName());
			parameters.put("recipientGstin", "GSTIN: " + out.getCgstin());

			String custAddress1 = out.getCustOrSuppAddress1();
			String custAddress2 = out.getCustOrSuppAddress2();
			String custAddress3 = out.getCustOrSuppAddress4();

			parameters.put("recipientAddress",
					"\n" + (custAddress1 != null ? custAddress1 + "," : "")
							+ (custAddress2 != null ? custAddress2 + "," : "")
							+ (custAddress3 != null ? custAddress3 : ""));

			parameters.put("recipientPin", out.getCustomerPincode() != null
					? out.getCustomerPincode().toString() : "");
			parameters.put("recipientPlace",
					stateNamesMap.get(out.getCgstin()));

			String DispAddress1 = EyEwbCommonUtil.getFromAdd1(
					out.getDispatcherBuildingNumber(),
					out.getSupplierBuildingNumber(), out.getDocCategory());
			String DispAddress2 = EyEwbCommonUtil.getFromAdd2(
					out.getDispatcherBuildingName(),
					out.getSupplierBuildingName(), out.getDocCategory());
			String DispAddress3 = EyEwbCommonUtil.getFromPlace(
					out.getDispatcherLocation(), out.getSupplierLocation(),
					out.getDocCategory());
			Integer dispPincode = EyEwbCommonUtil.getFromPinocode(
					out.getShipToPincode(), out.getSupplierPincode(),
					out.getDocCategory());

			parameters.put("dispatcherAddress",
					"\n" + (DispAddress1 != null ? DispAddress1 + "," : "")
							+ (DispAddress2 != null ? DispAddress2 + "," : "")
							+ (DispAddress3 != null ? DispAddress3 : ""));

			parameters.put("dispatcherPin",
					dispPincode != null ? dispPincode.toString() : "");
			parameters.put("dispatcherPlace", DispAddress3 != null
							? DispAddress3 : "");

			String shipAddress1 = EyEwbCommonUtil.getToAdd1(
					out.getShipToBuildingNumber(), out.getCustOrSuppAddress1(),
					out.getDocCategory());
			String shipAddress2 = EyEwbCommonUtil.getToAdd2(
					out.getShipToBuildingName(), out.getCustOrSuppAddress2(),
					out.getDocCategory());
			String shipAddress3 = EyEwbCommonUtil.getToPlace(
					out.getShipToLocation(), out.getCustOrSuppAddress4(),
					out.getDocCategory());
			Integer shipPincode = EyEwbCommonUtil.getToPinocode(
					out.getShipToPincode(), out.getCustomerPincode(),
					out.getDocCategory());
			
			String stateCode = EyEwbCommonUtil.getActToStateCode(
					out.getShipToState(), out.getBillToState(), 
					out.getDocCategory());
			String shipPlace = StatecodeRepo
					.findStateNameByCode(stateCode);

			parameters.put("shipAddress",
					"\n" + (shipAddress1 != null ? shipAddress1 + "," : "")
							+ (shipAddress2 != null ? shipAddress2 + "," : "")
							+ (shipAddress3 != null ? shipAddress3 : ""));

			parameters.put("shipPin",
					shipPincode != null ? shipPincode.toString() : "");
			parameters.put("shipPlace",
					shipPlace != null ? shipPlace : "");

			parameters.put("ewaybillno", ewbNo);
			parameters.put("mode",
					EyEwbCommonUtil.getTransModeDesc(ewb.getTransMode()));
			parameters.put("status",
					ewb.getStatus() != null ? ewb.getStatus().toString() : "");
			List<ProductDetails> list = new ArrayList<>();
			for (OutwardTransDocLineItem line : lineItem) {
				ProductDetails pd1 = new ProductDetails();
				pd1.setHsn(line.getHsnSac());
				pd1.setDescripition(line.getItemDescription());
				pd1.setQuantity(line.getQty() != null
						? line.getQty().setScale(2, RoundingMode.HALF_UP) + " "
								+ line.getUom()
						: "");
				pd1.setTaxableValue(line.getTaxableValue());
				BigDecimal igstRate = line.getIgstRate() != null
						? line.getIgstRate() : BigDecimal.ZERO;
				BigDecimal cgstRate = line.getCgstRate() != null
						? line.getCgstRate() : BigDecimal.ZERO;
				BigDecimal sgstRate = line.getSgstRate() != null
						? line.getSgstRate() : BigDecimal.ZERO;
				BigDecimal cessRate = line.getCessRateAdvalorem() != null
						? line.getCessRateAdvalorem() : BigDecimal.ZERO;
				pd1.setTaxRate(igstRate.toBigInteger().toString() + "+"
						+ cgstRate.toBigInteger().toString() + "+"
						+ sgstRate.toBigInteger().toString() + "+"
						+ cessRate.toBigInteger().toString());
				list.add(pd1);
			}
			parameters.put("itemDataSource",
					new JRBeanCollectionDataSource(list));
			List<VehicleDetails> vlist = new ArrayList<>();

			for (EwbLifecycleEntity ewblc : ewbLifeCycle) {
				if (!ewblc.isFunctionStatus() && APIIdentifiers.GENERATE_EWB
						.equalsIgnoreCase(ewblc.getFunction())) {
					VehicleDetails v1 = new VehicleDetails();
					v1.setCewbNo("-");
					String enteredBy = ("O".equalsIgnoreCase(transType))
							? out.getSgstin() : out.getCgstin();
					v1.setEnteredBy(enteredBy);
					v1.setEnteredDate(ewblc.getModifiedOn() != null ? formatter1
							.format(ewblc.getModifiedOn()).toString() : "");
					v1.setFrom(ewblc.getFromPlace());
					v1.setMode(EyEwbCommonUtil
							.getTransModeDesc(ewblc.getTransMode()));
					v1.setMulVehNo("-");
					String vehNum = ewblc.getVehicleNum() != null
							? ewblc.getVehicleNum() + "/" : "";
					String transDocNo = ewblc.getTransDocNo() != null
							? ewblc.getTransDocNo() + " & " : "";
					String transDocDate = ewblc.getTransDocDate() != null
							? formatter2.format(
									ewblc.getTransDocDate().atStartOfDay())
							: "-";
					v1.setTransporterDocNo(vehNum + transDocNo + transDocDate);
					vlist.add(v1);
				}
				if (!ewblc.isFunctionStatus()
						&& APIIdentifiers.EXTEND_VEHICLE_DETAILS
								.equalsIgnoreCase(ewblc.getFunction())) {
					VehicleDetails v1 = new VehicleDetails();
					v1.setCewbNo("-");
					String enteredBy = ("O".equalsIgnoreCase(transType))
							? out.getSgstin() : out.getCgstin();
					v1.setEnteredBy(enteredBy);
					v1.setEnteredDate(ewblc.getModifiedOn() != null ? formatter1
							.format(ewblc.getModifiedOn()).toString() : "");
					v1.setFrom(ewblc.getFromPlace());
					v1.setMode(EyEwbCommonUtil
							.getTransModeDesc(ewblc.getTransMode()));
					v1.setMulVehNo("-");
					String vehNum = ewblc.getVehicleNum() != null
							? ewblc.getVehicleNum() + "/" : "";
					String transDocNo = ewblc.getTransDocNo() != null
							? ewblc.getTransDocNo() + " & " : "";
					String transDocDate = ewblc.getTransDocDate() != null
							? formatter2.format(
									ewblc.getTransDocDate().atStartOfDay())
							: "-";
					v1.setTransporterDocNo(vehNum + transDocNo + transDocDate);
					vlist.add(v1);
				}
			}

			List<EwbMultiVehicleEntity> parentfindMultiVehcile = eWbrepoMultiVehicleRepo
					.getActiveEwbNo(Long.valueOf(ewbNo));
			for (EwbMultiVehicleEntity parentEwbMulVeh : parentfindMultiVehcile) {
				List<EwbMultiVehicleDetailsEntity> findMultiVehcile = ewbMultiVehicleDetailsRepo
						.findByMultiVehicleIdAndIsDeleteFalseAndIsErrorFalseOrderByCreatedDateDesc(
								parentEwbMulVeh.getId());
				for (EwbMultiVehicleDetailsEntity ewbMulVehDe : findMultiVehcile) {
					VehicleDetails multiVeh = new VehicleDetails();

					String multiVehEnteredBy = ("O".equalsIgnoreCase(transType))
							? out.getSgstin() : out.getCgstin();
					multiVeh.setEnteredBy(multiVehEnteredBy);
					LocalDateTime istCreatedDate = EYDateUtil
							.toISTDateTimeFromUTC(ewbMulVehDe.getCreatedDate());
					multiVeh.setEnteredDate(
							formatter1.format(istCreatedDate).toString());
					multiVeh.setMode(EyEwbCommonUtil
							.getTransModeDesc(parentEwbMulVeh.getTransMode()));
					multiVeh.setFrom(ewbMulVehDe.getFromPlace());
					multiVeh.setMulVehNo(ewbMulVehDe.getFromPlace() + ","
							+ parentEwbMulVeh.getToPlace() + ","
							+ ewbMulVehDe.getVehicleQty() + ","
							+ parentEwbMulVeh.getUnit());
					String multiVehNum = ewbMulVehDe.getVehicleNum() != null
							? ewbMulVehDe.getVehicleNum() + "/" : "";
					String multiTransDocNo = ewbMulVehDe.getTransDocNo() != null
							? ewbMulVehDe.getTransDocNo() + " & " : "";
					String multiTransDocDate = ewbMulVehDe
							.getTransDocDate() != null
									? formatter2.format(
											ewbMulVehDe.getTransDocDate())
									: "-";
					multiVeh.setTransporterDocNo(
							multiVehNum + multiTransDocNo + multiTransDocDate);
					vlist.add(multiVeh);
				}
			}
			List<VehicleDetails> sortedDataList = vlist.stream()
					.sorted(Comparator.comparing(VehicleDetails::getEnteredDate)
							.reversed())
					.collect(Collectors.toList());

			parameters.put("vehicleDataSource",
					new JRBeanCollectionDataSource(sortedDataList));
			parameters.put("link", "-");
			parameters.put("billdate",
					EYDateUtil.toISTDateTimeFromUTC(out.geteWayBillDate()));
			parameters.put("fromdate", EYDateUtil
					.toISTDateTimeFromUTC(out.getInvoicePeriodStartDate()));
			parameters.put("todate", EYDateUtil
					.toISTDateTimeFromUTC(out.getInvoicePeriodEndDate()));
			parameters.put("transactionType", out.getTransactionType());
			parameters.put("subType", out.getSubSupplyType());
			parameters.put("documentType", out.getDocType());
			parameters.put("documentNumber", out.getDocNo());
			parameters.put("documentDate",
					EYDateUtil.toISTDateTimeFromUTC(out.getDocDate()));

			String type = out.getTransactionType() != null
					&& "O".equalsIgnoreCase(out.getTransactionType())
							? "Outward - Supply" : "Inward- Supply";
			parameters.put("type", type);
			parameters.put("approxDistance", ewb.getRemDistance() == null
					? (out.getDistance() != null
							? out.getDistance().toString() + "KM" : "")
					: (ewb.getRemDistance() != null
							? ewb.getRemDistance().toString() + "KM" : ""));
			
			parameters.put("validUpto", ewb.getValidUpto() != null
					? (formatter1.format(ewb.getValidUpto()).toString() + 
							(ewb.getVehicleType().equalsIgnoreCase("O") ? " [ODC]" : "")) : "");

			parameters.put("totalTaxableValue", out.getTaxableValue() != null
					? out.getTaxableValue() : BigDecimal.ZERO);
			parameters.put("cgstAmount", out.getCgstAmount() != null
					? out.getCgstAmount() : BigDecimal.ZERO);
			parameters.put("sgstAmount", out.getSgstAmount() != null
					? out.getSgstAmount() : BigDecimal.ZERO);
			parameters.put("igstAmount", out.getIgstAmount() != null
					? out.getIgstAmount() : BigDecimal.ZERO);

			BigDecimal cessAdAmount = out.getInvoiceCessAdvaloremAmount() != null
					? out.getInvoiceCessAdvaloremAmount() : BigDecimal.ZERO;
			BigDecimal cessSpecAmount = out.getInvoiceCessSpecificAmount() != null
					? out.getInvoiceCessSpecificAmount() : BigDecimal.ZERO;
			BigDecimal cessAmount = cessAdAmount.add(cessSpecAmount);
			parameters.put("cessAmount", cessAmount);

			String transpoterIdName = (ewb.getTransporterId() != null
					? ewb.getTransporterId() : out.getTransporterID()) + " & "
					+ (out.getTransporterName() != null
							? out.getTransporterName() : "-");
			parameters.put("transporterIDName", transpoterIdName);

			String transDate = out.getTransportDocDate() != null
					? formatter2.format(out.getTransportDocDate()) + "" : "";
			parameters
					.put("transporterDocNoDate",
							(out.getTransportDocNo() != null
									? out.getTransportDocNo() + "-" : "")
									+ transDate);
			parameters.put("invoiceValue", out.getDocAmount() != null
					? out.getDocAmount() : BigDecimal.ZERO);

			parameters.put("otherAmt", out.getInvoiceOtherCharges() != null
					? out.getInvoiceOtherCharges() : BigDecimal.ZERO);

			String date = ewb.getEwbDate() != null ? formatter1
					.format(EYDateUtil.toISTDateTimeFromUTC(ewb.getEwbDate()))
					.toString() : "";

			String generatedBy = ("O".equalsIgnoreCase(transType))
					? out.getSgstin() : out.getCgstin();
			parameters.put("Image", qRcodeGenerator
					.getImage(ewbNo + "/" + generatedBy + "/" + date));

			parameters.put("ewaybill", "#" + ewbNo);
			/*
			 * parameters.put("ewbstatus", ewb.getStatus() == null ?
			 * out.getEwbStatus() + "" : ewb.getStatus() + "");
			 */
			parameters.put("docCategory", docCateg.get(out.getDocCategory()));

			/*
			 * InputStream jrxmlTemplateStream =
			 * JasperUtil.class.getClassLoader() .getResourceAsStream(source);
			 * jasperPrint = JasperFillManager.fillReport(jrxmlTemplateStream,
			 * parameters, new JREmptyDataSource());
			 * jasperPrintList.add(jasperPrint);
			 * 
			 * JasperUtil.getPDFReportBytesArray(jasperPrintList, outputStream);
			 */

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

	private boolean isAddressSuppressRequired(OutwardTransDocument hdr) {

		if (Strings.isNullOrEmpty(hdr.getDocCategory())) {
			return false;
		}
		Map<String, Config> configMap = configManager.getConfigs("EINV",
				"einv.address", TenantContext.getTenantId());

		boolean docCategorySuppresReq = configMap
				.get("einv.address.suppresreq") == null ? Boolean.FALSE
						: Boolean.valueOf(configMap
								.get("einv.address.suppresreq").getValue());

		return docCategorySuppresReq;
	}
}
