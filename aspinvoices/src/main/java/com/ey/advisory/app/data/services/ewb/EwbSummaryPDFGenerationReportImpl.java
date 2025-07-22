package com.ey.advisory.app.data.services.ewb;

import java.io.File;
import java.math.BigDecimal;
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
 * @author Arun K.A
 *
 */

@Component("EwbSummaryPDFGenerationReportImpl")
@Slf4j
public class EwbSummaryPDFGenerationReportImpl
		implements EwbSummaryPDFGenerationReport {

	@Autowired
	private EwbRepository ewbRepo;

	@Autowired
	private EwbLifecycleRepository ewbLifecycleRepo;

	@Autowired
	QRcodeGenerator qRcodeGenerator;

	@Autowired
	private OutwardTransDocumentRepository outwardTransDocumentRepo;

	@Autowired
	@Qualifier("EwbMultiVehicleRepository")
	private EwbMultiVehicleRepository eWbrepoMultiVehicleRepo;

	@Autowired
	@Qualifier("EwbMultiVehicleDetailsRepository")
	private EwbMultiVehicleDetailsRepository ewbMultiVehicleDetailsRepo;

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;

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
	public JasperPrint generateSummaryPdfReport(String ewbNo) {

		JasperPrint jasperPrint = null;
		List<JasperPrint> jasperPrintList = new ArrayList<>();
		String source = "jasperReports/summaryewbprint.jrxml";

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

			parameters.put("ewayBillNO", ewbNo);
			parameters.put("ewayBillDate", ewb.getEwbDate() != null
					? formatter1.format(ewb.getEwbDate()).toString() : "");

			String transType = ewb.getTransitType() != null
					? ewb.getTransitType() : out.getTransactionType();
			parameters.put("generatedDate", ewb.getEwbDate() != null
					? formatter1.format(ewb.getEwbDate()).toString() : "");
			parameters.put("generatedBy", (("O".equalsIgnoreCase(transType))
					? (out.getSgstin() + "," + out.getSupplierTradeName())
					: (out.getCgstin() + "," + out.getCustomerTradeName())));
			parameters.put("documentDetails",
					supplyType.get(out.getSupplyType()) + "-"
							+ docType.get(out.getDocType()) + "-"
							+ out.getDocNo() + "-" + formatter2
									.format(out.getDocDate().atStartOfDay()));
			parameters.put("fromDateDistance",
					ewb.getEwbDate() != null
							? (formatter1.format(ewb.getEwbDate()).toString()
									+ " [ " + (ewb.getRemDistance() == null
											? (out.getDistance() != null
													? out.getDistance()
															.toString() + " KM"
													: "")
											: (ewb.getRemDistance() != null
													? ewb.getRemDistance()
															.toString() + " KM"
													: ""))
									+ " ]")
							: "");
			parameters.put("toDate", ewb.getValidUpto() != null
					? (formatter1.format(ewb.getValidUpto()).toString() + 
							(ewb.getVehicleType().equalsIgnoreCase("O") ? " [ODC]" : "")) : "");

			parameters.put("supplierGSTIN", out.getSgstin() + "\n"
					+ checkForNull(out.getSupplierTradeName()));

			List<String> gstins = new ArrayList<>();

			if (out.getSgstin() != null)
				gstins.add(out.getSgstin());
			if (out.getShipToGstin() != null)
				gstins.add(out.getShipToGstin());
			if (out.getDispatcherGstin() != null)
				gstins.add(out.getDispatcherGstin());
			if (out.getCgstin() != null)
				gstins.add(out.getCgstin());

			Map<String, String> stateNamesMap = entityService
					.getStateNames(gstins);

			String docCategory = out.getDocCategory();
			String dispatchAdress = null;
			if ("REG".equalsIgnoreCase(docCategory)
					|| "SHP".equalsIgnoreCase(docCategory)) {
				dispatchAdress = out.getSupplierBuildingNumber() + ","
						+ out.getSupplierBuildingName() + ","
						+ out.getSupplierLocation() + ","
						+ stateNamesMap.get(out.getSgstin()) + ","
						+ out.getSupplierPincode();
			} else if ("DIS".equalsIgnoreCase(docCategory)
					|| "CMB".equalsIgnoreCase(docCategory)) {
				dispatchAdress = out.getDispatcherBuildingNumber() + ","
						+ out.getDispatcherBuildingName() + ","
						+ out.getDispatcherLocation() + ","
						+ stateNamesMap.get(out.getDispatcherGstin()) + ","
						+ out.getDispatcherPincode();
			}

			parameters.put("placeOfDispatch", dispatchAdress);
			String cgstin = out.getCgstin();
			if (out.getCustomerTradeName() != null) {
				cgstin = cgstin + "\n" + out.getCustomerTradeName();
			}
			parameters.put("recipientGSTIN", cgstin);

			String address1 = EyEwbCommonUtil.getToAdd1(
					out.getShipToBuildingNumber(), out.getCustOrSuppAddress1(),
					out.getDocCategory());
			String address2 = EyEwbCommonUtil.getToAdd2(
					out.getShipToBuildingName(), out.getCustOrSuppAddress2(),
					out.getDocCategory());
			String address3 = EyEwbCommonUtil.getToPlace(
					out.getShipToLocation(), out.getCustOrSuppAddress4(),
					out.getDocCategory());

			String address4 = (out.getCgstin().equalsIgnoreCase("URP")
					|| Strings.isNullOrEmpty(out.getCgstin()))
							? StatecodeRepo
									.findStateNameByCode(out.getBillToState())
							: stateNamesMap.get(out.getCgstin());
			/*
			 * String address4 = (out.getCgstin() != null ||
			 * !out.getCgstin().equalsIgnoreCase("URP")) ?
			 * (stateNamesMap.get(out.getCgstin()) + ",") : "";
			 */
			Integer pincode = EyEwbCommonUtil.getToPinocode(
					out.getShipToPincode(), out.getCustomerPincode(),
					out.getDocCategory());

			String placeOfDelivery = (address1 != null ? address1 + "," : "")
					+ (address2 != null ? address2 + "," : "")
					+ (address3 != null ? address3 + "," : "") 
					+ (address4 != null ? address4 + "," : "")
					+ pincode;

			parameters.put("placeOfDelivery", placeOfDelivery);
			parameters.put("documentNumber", out.getDocNo());
			parameters.put("documentDate", out.getDocDate() != null ? formatter2
					.format(EYDateUtil.toISTDateTimeFromUTC(out.getDocDate()))
					.toString() : "");
			parameters.put("transactionType", out.getDocCategory());
			parameters.put("valueOfGoods",
					out.getDocAmount() != null ? out.getDocAmount().toString()
							: BigDecimal.ZERO.toString());
			parameters.put("hsnCode",
					lineItem.get(0).getHsnSac() != null
							? lineItem.get(0).getHsnSac().toString() + " - "
									+ lineItem.get(0).getItemDescription()
							: "");
			parameters.put("reasonForTransport",
					("O".equalsIgnoreCase(out.getTransactionType()) ? "Outward"
							: "Inward") + "-"
							+ supplyType.get(out.getSupplyType()));

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
				if (!ewblc.isFunctionStatus() && APIIdentifiers.EXTEND_VEHICLE_DETAILS
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

			String transpoterIdName = (ewb.getTransporterId() != null
					? ewb.getTransporterId() : out.getTransporterID()) + " & "
					+ (out.getTransporterName() != null
							? out.getTransporterName() : "-");
			parameters.put("transporterIDName", transpoterIdName);
			parameters.put("currentDate", new Date());
			parameters.put("ewaybill", "#" + ewbNo);
			/*
			 * parameters.put("ewbstatus", ewb.getStatus() == null ?
			 * out.getEwbStatus() + "" : ewb.getStatus() + "");
			 */

			parameters.put("docCategory", docCateg.get(out.getDocCategory()));

			String date = ewb.getEwbDate() != null ? formatter1
					.format(EYDateUtil.toISTDateTimeFromUTC(ewb.getEwbDate()))
					.toString() : "";

			String generatedBy = ("O".equalsIgnoreCase(transType))
					? out.getSgstin() : out.getCgstin();
			parameters.put("Image", qRcodeGenerator
					.getImage(ewbNo + "/" + generatedBy + "/" + date));

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

	private static String checkForNull(String str) {
		return str != null ? str : "";
	}

}
