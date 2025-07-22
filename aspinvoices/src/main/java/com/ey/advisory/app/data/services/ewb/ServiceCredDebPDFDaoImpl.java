package com.ey.advisory.app.data.services.ewb;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.ey.advisory.admin.data.entities.client.BankDetailsEntity;
import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.LogoConfigEntity;
import com.ey.advisory.admin.data.entities.client.TermConditionsEntity;
import com.ey.advisory.admin.data.repositories.client.BankDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.LogoConfigRepository;
import com.ey.advisory.admin.data.repositories.client.TermConditionsRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.EinvoiceRepository;
import com.ey.advisory.app.data.repositories.client.OutwardTransDocumentRepository;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.QRcodeGenerator;
import com.ey.advisory.core.dto.GoodsProductDetails;
import com.ey.advisory.domain.client.B2CQRCodeRequestLogEntity;
import com.ey.advisory.einv.client.EinvoiceEntity;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.ey.advisory.repositories.client.B2CQRCodeLoggerRepository;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component("ServiceCredDebPDFDaoImpl")
@Slf4j
public class ServiceCredDebPDFDaoImpl implements ServiceCredDebPdf {

	private static final String AMOUNT_STRING = "0.00";

	@Autowired
	QRcodeGenerator qRcodeGenerator;
	
	@Autowired
	QRCodeServiceDynamicGenerator qRCodeServiceDynamicGenerator;
	@Autowired
	@Qualifier("B2CQRCodeLoggerRepository")
	B2CQRCodeLoggerRepository qrCodeRepo;
		

	@Autowired
	private OutwardTransDocumentRepository outwardTransDocumentRepo;

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;

	@Autowired
	@Qualifier("TermConditionsRepository")
	TermConditionsRepository termConditionsRepository;

	@Autowired
	@Qualifier("EinvoiceRepository")
	EinvoiceRepository einv;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("LogoConfigRepository")
	LogoConfigRepository logoConfigRepository;

	@Autowired
	@Qualifier("BankDetailsRepository")
	BankDetailsRepository bankDetailsRepository;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;
	
	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

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
			.<String, String>builder()
			.put("EXPT", "SUPPLY MEANT FOR EXPORT ON PAYMENT OF INTEGRATED TAX")
			.put("EXPWT",
					"SUPPLY MEANT FOR EXPORT UNDER BOND OR LETTER OF UNDERTAKING WITHOUT PAYMENT OF INTEGRATED TAX")
			.put("SEZWP",
					"SUPPLY MEANT FOR SEZ UNIT OR SEZ DEVELOPER FOR AUTHORISED OPERATIONS ON PAYMENT OF INTEGRATED TAX")
			.put("SEZWOP",
					"SUPPLY MEANT FOR SEZ UNIT OR SEZ DEVELOPER FOR AUTHORISED OPERATIONS UNDER BOND OR LETTER OF UNDERTAKING WITHOUT PAYMENT OF INTEGRATED TAX")
			.build();

	private static final List<String> TAX_DOC_TYPE = ImmutableList.of("B2CS",
			"B2CL","CDNR","CDNUR-B2CL","NILEXTNON","B2B");

	private static final List<String> TAX_DOC_TYPE1 = ImmutableList.of("B2B",
			"EXPORTS", "CDNR", "CDNUR-EXPORTS");
	private static final List<String> DOC_TYPE = ImmutableList.of("INV", "CR",
			"DR", "BOS");
	private static final List<String> DOC_TYPE1 = ImmutableList.of("INV", "CR",
			"DR");
	private static final List<String> EWB_DOC_TYPE = ImmutableList.of("CR",
			"DR");
	private static final List<String> DOC_TYPE2 = ImmutableList.of("DLC", "OTH",
			"BOS");
	final static Map<String, String> REVERSECHARGE = ImmutableMap
			.<String, String>builder().put("Y", "YES").put("N", "NO")
			.put("L", "NO").put(" ", "NO").build();
	
	final static String SLASH="/";


	public JasperPrint generateServiceCredPdfReport(String id, String docNo,
			String sgstin) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<>();
		String source = "jasperReports/ServiceTaxInvoice (002).jrxml";
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside generateServiceCredPdfReport for docNo: %s", docNo);
			LOGGER.debug(msg);
		}
		Optional<OutwardTransDocument> currOutward = outwardTransDocumentRepo
				.findById(Long.parseLong(id));
		EinvoiceEntity einvoiceEntity = null;
		OutwardTransDocument out = currOutward.get();
		try {

			String irn = out.getIrnResponse();
			if (irn != null && !irn.isEmpty()) {
				einvoiceEntity = einv.findByIrn(irn);
				Long originalDocId = einvoiceEntity.getDocHeaderId();
				if (originalDocId != out.getId()) {
					Optional<OutwardTransDocument> originalOutward = outwardTransDocumentRepo
							.findById(originalDocId);
					out = originalOutward.get();
				}
			}
			List<String> gstins = new ArrayList<>();

			if (out.getSgstin() != null)
				gstins.add(out.getSgstin());
			if (out.getCgstin() != null)
				gstins.add(out.getCgstin());
			if (out.getDispatcherGstin() != null)
				gstins.add(out.getDispatcherGstin());

			Map<String, String> stateNamesMap = entityService
					.getStateNames(gstins);
			String stateName1 = statecodeRepository
					.findStateNameByCode(out.getSgstin().substring(0, 2));
			String placeOfSupply2 = out.getSgstin().substring(0, 2) + "-"
					+ stateName1;
			String supplierState1 = (out.getSupplierPincode() != null
					? placeOfSupply2 + "," + out.getSupplierPincode().toString()
					: "");
			String supplierAddress1 = (out.getSupplierBuildingNumber() != null
					? out.getSupplierBuildingNumber() + "," : "")
					+ (out.getSupplierBuildingName() != null
							? out.getSupplierBuildingName() + "," : "")
					+ (out.getSupplierLocation() != null
							? out.getSupplierLocation() + "\n" : "")
					+ (supplierState1 != null ? supplierState1 : "");

			parameters.put("SupplierAddress", supplierAddress1);
			String supplierName = (out.getSupplierLegalName() != null
					? out.getSupplierLegalName() : "");
			parameters.put("SupplierName", supplierName);
			String stateName = statecodeRepository
					.findStateNameByCode(out.getSgstin().substring(0, 2));
			String placeOfSupply1 = out.getSgstin().substring(0, 2) + "-"
					+ stateName;
			if (stateName == null) {
				placeOfSupply1 = out.getSgstin().substring(0, 2);
			}
			String supplierState = (out.getSupplierPincode() != null
					? placeOfSupply1 + "," + out.getSupplierPincode().toString()
					: "");
			parameters.put("SupplierStateCodeNamePincode", supplierState);

			String customerAddress = (out.getCustOrSuppAddress1() != null
					? out.getCustOrSuppAddress1() + "," : "")
					+ (out.getCustOrSuppAddress2() != null
							? out.getCustOrSuppAddress2() + "," : "")
					+ (out.getCustOrSuppAddress4() != null
							? out.getCustOrSuppAddress4() : "");

			parameters.put("CustomerAddress", customerAddress);
			String customerName = (out.getCustOrSuppName() != null
					? out.getCustOrSuppName() : "");

			parameters.put("CustomerName", customerName);
			String custstateName00 = "";
			if (out.getBillToState() != null) {
				custstateName00 = statecodeRepository
						.findStateNameByCode(out.getBillToState());
			}

			String placeOfSupply4 = "";
			String customerState = "";
			if (out.getBillToState() != null) {
				placeOfSupply4 = out.getBillToState() + "-" + custstateName00;

			}
			customerState = (out.getCustomerPincode() != null
					? placeOfSupply4 + "," + out.getCustomerPincode().toString()
					: "");
			parameters.put("CustomerStateCodeNamePincode", customerState);

			String shiptoAddress = (out.getShipToBuildingNumber() != null
					? out.getShipToBuildingNumber() + "," : "")
					+ (out.getShipToBuildingName() != null
							? out.getShipToBuildingName() + "," : "")
					+ (out.getShipToLocation() != null ? out.getShipToLocation()
							: "");
			if (Strings.isNullOrEmpty(shiptoAddress)) {
				shiptoAddress = customerAddress;
			}
			parameters.put("ShipToAddress", shiptoAddress);

			String shiptoTradeName = (out.getShipToTradeName() != null
					? out.getShipToTradeName() : "");
			if (Strings.isNullOrEmpty(shiptoTradeName)) {
				shiptoTradeName = customerName;
			}
			parameters.put("ShipToTradeName", shiptoTradeName);

			String shipState = stateNamesMap.containsKey(out.getCgstin())
					? stateNamesMap.get(out.getCgstin()) : "";
			String shipPincode = out.getShipToPincode() != null
					? out.getShipToPincode().toString() : "";
			String shipstatename = (shipState != null ? (shipState + ",") : "")
					+ (shipPincode != null ? shipPincode : "");
			if (Strings.isNullOrEmpty(shipstatename)) {
				shipstatename = customerState;
			}
			parameters.put("ShipToStateCodeNamePincode", shipstatename);

			String dispatcherAddress = (out
					.getDispatcherBuildingNumber() != null
							? out.getDispatcherBuildingNumber() + "," : "")
					+ (out.getDispatcherBuildingName() != null
							? out.getDispatcherBuildingName() + "," : "")
					+ (out.getDispatcherLocation() != null
							? out.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress1;
			}
			parameters.put("DispatcherAddress", dispatcherAddress);
			String dispatcherTradeName = (out.getDispatcherTradeName() != null
					? out.getDispatcherTradeName() : "");
			if (Strings.isNullOrEmpty(dispatcherTradeName)) {
				dispatcherTradeName = supplierName;
			}
			parameters.put("DispatcherTradeName", dispatcherTradeName);

			String disState = stateNamesMap.containsKey(out.getSgstin())
					? stateNamesMap.get(out.getSgstin()) : "";
			String disPincode = out.getDispatcherPincode() != null
					? out.getDispatcherPincode().toString() : "";
			String disstatename = (disState != null ? (disState + ",") : "")
					+ (disPincode != null ? disPincode : "");
			if (Strings.isNullOrEmpty(disstatename)) {
				disstatename = supplierState;
			}
			parameters.put("DispatcherStateCodeNamePincode", disstatename);

			
			String docCategory = out.getDocCategory();
			
			if (docCategory != null
					&& Lists.newArrayList("REG", "DIS", "SHP", "CMB")
							.contains(docCategory)) {
				switch (docCategory) {
				case "REG": {
					String stateName2 = statecodeRepository.findStateNameByCode(
							out.getSgstin().substring(0, 2));
					String placeOfSupply3 = out.getSgstin().substring(0, 2)
							+ "-" + stateName2;
					if (stateName == null) {
						placeOfSupply3 = out.getSgstin().substring(0, 2);
					}
					String supplierState2 = (out.getSupplierPincode() != null
							? placeOfSupply3 + ","
									+ out.getSupplierPincode().toString()
							: "");
					String supplierAddress2 = (out
							.getSupplierBuildingNumber() != null
									? out.getSupplierBuildingNumber() + ","
									: "")
							+ (out.getSupplierBuildingName() != null
									? out.getSupplierBuildingName() + "," : "")
							+ (out.getSupplierLocation() != null
									? out.getSupplierLocation() + "\n" : "")
							+ (supplierState2 != null ? supplierState2 : "");

					parameters.put("SupplierAddress", supplierAddress2);
					String supplierName1 = (out.getSupplierLegalName() != null
							? out.getSupplierLegalName() : "");
					parameters.put("SupplierName", supplierName1);

					String customerAddress1 = (out
							.getCustOrSuppAddress1() != null
									? out.getCustOrSuppAddress1() + "," : "")
							+ (out.getCustOrSuppAddress2() != null
									? out.getCustOrSuppAddress2() + "," : "")
							+ (out.getCustOrSuppAddress4() != null
									? out.getCustOrSuppAddress4() : "");

					parameters.put("CustomerAddress", customerAddress1);
					String customerName1 = (out.getCustOrSuppName() != null
							? out.getCustOrSuppName() : "");

					parameters.put("CustomerName", customerName1);

					String custstateName01 = "";
					if (out.getBillToState() != null) {
						custstateName01 = statecodeRepository
								.findStateNameByCode(out.getBillToState());
					}

					String placeOfSupply04 = "";
					String customerState04 = "";
					if (out.getBillToState() != null) {
						placeOfSupply04 = out.getBillToState() + "-"
								+ custstateName01;

					}
					customerState04 = (out.getCustomerPincode() != null
							? placeOfSupply04 + ","
									+ out.getCustomerPincode().toString()
							: "");
					parameters.put("CustomerStateCodeNamePincode",
							customerState04);

					String shiptoAddress1 = (out
							.getShipToBuildingNumber() != null
									? out.getShipToBuildingNumber() + "," : "")
							+ (out.getShipToBuildingName() != null
									? out.getShipToBuildingName() + "," : "")
							+ (out.getShipToLocation() != null
									? out.getShipToLocation() : "");
					if (Strings.isNullOrEmpty(shiptoAddress1)) {
						shiptoAddress1 = customerAddress;
					}
					parameters.put("ShipToAddress", shiptoAddress1);

					String shiptoTradeName1 = (out.getShipToTradeName() != null
							? out.getShipToTradeName() : "");
					if (Strings.isNullOrEmpty(shiptoTradeName1)) {
						shiptoTradeName1 = customerName;
					}
					parameters.put("ShipToTradeName", shiptoTradeName1);

					String shipState1 = stateNamesMap
							.containsKey(out.getCgstin())
									? stateNamesMap.get(out.getCgstin()) : "";
					String shipPincode1 = out.getShipToPincode() != null
							? out.getShipToPincode().toString() : "";
					String shipstatename1 = (shipState1 != null
							? (shipState1 + ",") : "")
							+ (shipPincode1 != null ? shipPincode1 : "");
					if (Strings.isNullOrEmpty(shipstatename1)) {
						shipstatename1 = customerState04;
					}
					parameters.put("ShipToStateCodeNamePincode",
							shipstatename1);

					String dispatcherAddress1 = (out
							.getDispatcherBuildingNumber() != null
									? out.getDispatcherBuildingNumber() + ","
									: "")
							+ (out.getDispatcherBuildingName() != null
									? out.getDispatcherBuildingName() + ","
									: "")
							+ (out.getDispatcherLocation() != null
									? out.getDispatcherLocation() : "");
					if (Strings.isNullOrEmpty(dispatcherAddress1)) {
						dispatcherAddress1 = supplierAddress2;
					}
					parameters.put("DispatcherAddress", dispatcherAddress);
					String dispatcherTradeName1 = (out
							.getDispatcherTradeName() != null
									? out.getDispatcherTradeName() : "");
					if (Strings.isNullOrEmpty(dispatcherTradeName1)) {
						dispatcherTradeName1 = supplierName;
					}
					parameters.put("DispatcherTradeName", dispatcherTradeName1);

					String disState1 = stateNamesMap
							.containsKey(out.getSgstin())
									? stateNamesMap.get(out.getSgstin()) : "";
					String disPincode1 = out.getDispatcherPincode() != null
							? out.getDispatcherPincode().toString() : "";
					String disstatename1 = (disState1 != null
							? (disState1 + ",") : "")
							+ (disPincode1 != null ? disPincode1 : "");
					if (Strings.isNullOrEmpty(disstatename1)) {
						disstatename1 = supplierState1;
					}
					parameters.put("DispatcherStateCodeNamePincode",
							disstatename1);

					break;
				}
				case "DIS": {
					String stateName2 = statecodeRepository.findStateNameByCode(
							out.getSgstin().substring(0, 2));
					String placeOfSupply3 = out.getSgstin().substring(0, 2)
							+ "-" + stateName2;
					if (stateName == null) {
						placeOfSupply3 = out.getSgstin().substring(0, 2);
					}

					String supplierState2 = (out.getSupplierPincode() != null
							? placeOfSupply3 + ","
									+ out.getSupplierPincode().toString()
							: "");
					String supplierAddress2 = (out
							.getSupplierBuildingNumber() != null
									? out.getSupplierBuildingNumber() + ","
									: "")
							+ (out.getSupplierBuildingName() != null
									? out.getSupplierBuildingName() + "," : "")
							+ (out.getSupplierLocation() != null
									? out.getSupplierLocation() + "\n" : "")
							+ (supplierState2 != null ? supplierState2 : "");

					String customerAddress1 = (out
							.getCustOrSuppAddress1() != null
									? out.getCustOrSuppAddress1() + "," : "")
							+ (out.getCustOrSuppAddress2() != null
									? out.getCustOrSuppAddress2() + "," : "")
							+ (out.getCustOrSuppAddress4() != null
									? out.getCustOrSuppAddress4() : "");

					parameters.put("CustomerAddress", customerAddress1);
					String customerName1 = (out.getCustOrSuppName() != null
							? out.getCustOrSuppName() : "");

					parameters.put("CustomerName", customerName1);

					String custstateName01 = "";
					if (out.getBillToState() != null) {
						custstateName01 = statecodeRepository
								.findStateNameByCode(out.getBillToState());
					}

					String placeOfSupply04 = "";
					String customerState04 = "";
					if (out.getBillToState() != null) {
						placeOfSupply04 = out.getBillToState() + "-"
								+ custstateName01;

					}
					customerState04 = (out.getCustomerPincode() != null
							? placeOfSupply04 + ","
									+ out.getCustomerPincode().toString()
							: "");
					parameters.put("CustomerStateCodeNamePincode",
							customerState04);

					String shiptoAddress1 = (out
							.getShipToBuildingNumber() != null
									? out.getShipToBuildingNumber() + "," : "")
							+ (out.getShipToBuildingName() != null
									? out.getShipToBuildingName() + "," : "")
							+ (out.getShipToLocation() != null
									? out.getShipToLocation() : "");
					if (Strings.isNullOrEmpty(shiptoAddress1)) {
						shiptoAddress1 = customerAddress;
					}
					parameters.put("ShipToAddress", shiptoAddress1);

					String shiptoTradeName1 = (out.getShipToTradeName() != null
							? out.getShipToTradeName() : "");
					if (Strings.isNullOrEmpty(shiptoTradeName1)) {
						shiptoTradeName1 = customerName;
					}
					parameters.put("ShipToTradeName", shiptoTradeName1);

					String shipState1 = stateNamesMap
							.containsKey(out.getCgstin())
									? stateNamesMap.get(out.getCgstin()) : "";
					String shipPincode1 = out.getShipToPincode() != null
							? out.getShipToPincode().toString() : "";
					String shipstatename1 = (shipState1 != null
							? (shipState1 + ",") : "")
							+ (shipPincode1 != null ? shipPincode1 : "");
					if (Strings.isNullOrEmpty(shipstatename1)) {
						shipstatename1 = customerState04;
					}
					parameters.put("ShipToStateCodeNamePincode",
							shipstatename1);
					parameters.put("DispatcherAddress",
							(out.getDispatcherBuildingNumber() != null
									? out.getDispatcherBuildingNumber() + ","
									: "")
									+ (out.getDispatcherBuildingName() != null
											? out.getDispatcherBuildingName()
													+ ","
											: "")
									+ (out.getDispatcherLocation() != null
											? out.getDispatcherLocation()
											: ""));
					parameters.put("DispatcherTradeName",
							out.getDispatcherTradeName() != null
									? out.getDispatcherTradeName() : "");
					String disState1 = stateNamesMap
							.containsKey(out.getSgstin())
									? stateNamesMap.get(out.getSgstin()) : "";
					String disPincode1 = out.getDispatcherPincode() != null
							? out.getDispatcherPincode().toString() : "";
					parameters.put("DispatcherStateCodeNamePincode",
							(disState1 != null ? (disState1 + ",") : "")
									+ (disPincode1 != null ? disPincode1 : ""));

					break;
				}
				case "SHP": {
					String stateName2 = statecodeRepository.findStateNameByCode(
							out.getSgstin().substring(0, 2));
					String placeOfSupply3 = out.getSgstin().substring(0, 2)
							+ "-" + stateName2;
					if (stateName == null) {
						placeOfSupply3 = out.getSgstin().substring(0, 2);
					}
					String supplierState2 = (out.getSupplierPincode() != null
							? placeOfSupply3 + ","
									+ out.getSupplierPincode().toString()
							: "");
					String supplierAddress2 = (out
							.getSupplierBuildingNumber() != null
									? out.getSupplierBuildingNumber() + ","
									: "")
							+ (out.getSupplierBuildingName() != null
									? out.getSupplierBuildingName() + "," : "")
							+ (out.getSupplierLocation() != null
									? out.getSupplierLocation() + "\n" : "")
							+ (supplierState2 != null ? supplierState2 : "");

					parameters.put("CustomerAddress",
							(out.getCustOrSuppAddress1() != null
									? out.getCustOrSuppAddress1() + "," : "")
									+ (out.getCustOrSuppAddress2() != null
											? out.getCustOrSuppAddress2() + ","
											: "")
									+ (out.getCustOrSuppAddress4() != null
											? out.getCustOrSuppAddress4()
											: ""));
					parameters.put("CustomerName",
							out.getCustOrSuppName() != null
									? out.getCustOrSuppName() : "");
					String custstateName01 = "";
					if (out.getBillToState() != null) {
						custstateName01 = statecodeRepository
								.findStateNameByCode(out.getBillToState());
					}

					String placeOfSupply04 = "";
					String customerState04 = "";
					if (out.getBillToState() != null) {
						placeOfSupply04 = out.getBillToState() + "-"
								+ custstateName01;

					}
					customerState04 = (out.getCustomerPincode() != null
							? placeOfSupply04 + ","
									+ out.getCustomerPincode().toString()
							: "");
					parameters.put("CustomerStateCodeNamePincode",
							customerState04);

					parameters.put("ShipToAddress",
							(out.getShipToBuildingNumber() != null
									? out.getShipToBuildingNumber() + "," : "")
									+ (out.getShipToBuildingName() != null
											? out.getShipToBuildingName() + ","
											: "")
									+ (out.getShipToLocation() != null
											? out.getShipToLocation() : ""));
					parameters.put("ShipToTradeName",
							out.getShipToTradeName() != null
									? out.getShipToTradeName() : "");
					String shipState1 = stateNamesMap
							.containsKey(out.getCgstin())
									? stateNamesMap.get(out.getCgstin()) : "";
					String shipPincode1 = out.getShipToPincode() != null
							? out.getShipToPincode().toString() : "";
					parameters.put("ShipToStateCodeNamePincode",
							(shipState1 != null ? (shipState1 + ",") : "")
									+ (shipPincode1 != null ? shipPincode1
											: ""));
					String dispatcherAddress1 = (out
							.getDispatcherBuildingNumber() != null
									? out.getDispatcherBuildingNumber() + ","
									: "")
							+ (out.getDispatcherBuildingName() != null
									? out.getDispatcherBuildingName() + ","
									: "")
							+ (out.getDispatcherLocation() != null
									? out.getDispatcherLocation() : "");
					if (Strings.isNullOrEmpty(dispatcherAddress1)) {
						dispatcherAddress1 = supplierAddress2;
					}
					parameters.put("DispatcherAddress", dispatcherAddress);
					String dispatcherTradeName1 = (out
							.getDispatcherTradeName() != null
									? out.getDispatcherTradeName() : "");
					if (Strings.isNullOrEmpty(dispatcherTradeName1)) {
						dispatcherTradeName1 = supplierName;
					}
					parameters.put("DispatcherTradeName", dispatcherTradeName1);

					String disState1 = stateNamesMap
							.containsKey(out.getSgstin())
									? stateNamesMap.get(out.getSgstin()) : "";
					String disPincode1 = out.getDispatcherPincode() != null
							? out.getDispatcherPincode().toString() : "";
					String disstatename1 = (disState1 != null
							? (disState1 + ",") : "")
							+ (disPincode1 != null ? disPincode1 : "");
					if (Strings.isNullOrEmpty(disstatename1)) {
						disstatename1 = supplierState1;
					}
					parameters.put("DispatcherStateCodeNamePincode",
							disstatename1);

					break;
				}
				case "CMB": {
					String stateName2 = statecodeRepository.findStateNameByCode(
							out.getSgstin().substring(0, 2));
					String placeOfSupply3 = out.getSgstin().substring(0, 2)
							+ "-" + stateName2;
					if (stateName == null) {
						placeOfSupply3 = out.getSgstin().substring(0, 2);
					}
					String supplierState2 = (out.getSupplierPincode() != null
							? placeOfSupply3 + ","
									+ out.getSupplierPincode().toString()
							: "");
					String supplierAddress2 = (out
							.getSupplierBuildingNumber() != null
									? out.getSupplierBuildingNumber() + ","
									: "")
							+ (out.getSupplierBuildingName() != null
									? out.getSupplierBuildingName() + "," : "")
							+ (out.getSupplierLocation() != null
									? out.getSupplierLocation() + "\n" : "")
							+ (supplierState2 != null ? supplierState2 : "");

					parameters.put("CustomerAddress",
							(out.getCustOrSuppAddress1() != null
									? out.getCustOrSuppAddress1() + "," : "")
									+ (out.getCustOrSuppAddress2() != null
											? out.getCustOrSuppAddress2() + ","
											: "")
									+ (out.getCustOrSuppAddress4() != null
											? out.getCustOrSuppAddress4()
											: ""));
					parameters.put("CustomerName",
							out.getCustOrSuppName() != null
									? out.getCustOrSuppName() : "");

					String custstateName01 = "";
					if (out.getCgstin() != null) {
						custstateName01 = statecodeRepository
								.findStateNameByCode(out.getBillToState());
					}

					String placeOfSupply04 = "";
					String customerState04 = "";
					if (out.getBillToState() != null) {
						placeOfSupply04 = out.getBillToState() + "-"
								+ custstateName01;

					}
					customerState04 = (out.getCustomerPincode() != null
							? placeOfSupply04 + ","
									+ out.getCustomerPincode().toString()
							: "");
					parameters.put("CustomerStateCodeNamePincode",
							customerState04);

					parameters.put("ShipToAddress",
							(out.getShipToBuildingNumber() != null
									? out.getShipToBuildingNumber() + "," : "")
									+ (out.getShipToBuildingName() != null
											? out.getShipToBuildingName() + ","
											: "")
									+ (out.getShipToLocation() != null
											? out.getShipToLocation() : ""));
					parameters.put("ShipToTradeName",
							out.getShipToTradeName() != null
									? out.getShipToTradeName() : "");
					String shipState1 = stateNamesMap
							.containsKey(out.getCgstin())
									? stateNamesMap.get(out.getCgstin()) : "";
					String shipPincode1 = out.getShipToPincode() != null
							? out.getShipToPincode().toString() : "";
					parameters.put("ShipToStateCodeNamePincode",
							(shipState1 != null ? (shipState1 + ",") : "")
									+ (shipPincode1 != null ? shipPincode1
											: ""));
					parameters.put("DispatcherAddress",
							(out.getDispatcherBuildingNumber() != null
									? out.getDispatcherBuildingNumber() + ","
									: "")
									+ (out.getDispatcherBuildingName() != null
											? out.getDispatcherBuildingName()
													+ ","
											: "")
									+ (out.getDispatcherLocation() != null
											? out.getDispatcherLocation()
											: ""));
					parameters.put("DispatcherTradeName",
							out.getDispatcherTradeName() != null
									? out.getDispatcherTradeName() : "");
					String disState1 = stateNamesMap
							.containsKey(out.getSgstin())
									? stateNamesMap.get(out.getSgstin()) : "";
					String disPincode1 = out.getDispatcherPincode() != null
							? out.getDispatcherPincode().toString() : "";
					parameters.put("DispatcherStateCodeNamePincode",
							(disState1 != null ? (disState1 + ",") : "")
									+ (disPincode1 != null ? disPincode1 : ""));

					break;
				}
				}
			}

			List<OutwardTransDocLineItem> lineItems = out.getLineItems();
			GSTNDetailEntity entity1 = gSTNDetailRepository
					.findRegDates(out.getSgstin());
			String clientName = entity1.getRegisteredName() != null
					? entity1.getRegisteredName() : "";
			if (out.getDocType().equalsIgnoreCase("CR")) {
				parameters.put("PageTitle", "Credit Note");
			}
			if (out.getDocType().equalsIgnoreCase("DR")) {
				parameters.put("PageTitle", "Debit Note");
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
					+ (out.getSupplierLegalName() != null
							? out.getSupplierLegalName() : "");
			parameters.put("SupplierNameFor", suppliernameFor);
			parameters.put("SGSTIN", (!Strings.isNullOrEmpty(out.getSgstin()))
					? out.getSgstin() : "");
			parameters.put("SPAN", out.getSgstin().substring(2, 12));
			parameters.put("SupplierPhone", out.getSupplierPhone() != null
					? out.getSupplierPhone() : "");
			parameters.put("SupplierEmail", out.getSupplierEmail() != null
					? out.getSupplierEmail() : "");
			parameters.put("SupplierFSSAI", out.getSupplierEmail() != null
					? out.getSupplierEmail() : "");
			parameters.put("irn",
					out.getIrnResponse() != null ? out.getIrnResponse() : "");
			parameters.put("irnDate", out.getAckDate() != null
					? (formatter1.format(out.getAckDate())) : "");
			parameters.put("NoteNo",
					out.getDocNo() != null ? out.getDocNo() : "");
			parameters.put("NoteDate", out.getDocDate() != null
					? formatter2.format(
							EYDateUtil.toISTDateTimeFromUTC(out.getDocDate()))
					: "");
			parameters.put("OriginalInvoiceNo",
					out.getPreceedingInvoiceNumber() != null
							? out.getPreceedingInvoiceNumber() : "");
			parameters
					.put("OriginalInvoiceDate",
							out.getPreceedingInvoiceDate() != null
									? formatter2.format(
											EYDateUtil.toISTDateTimeFromUTC(
													out.getPreceedingInvoiceDate()))
									: "");
			parameters.put("SupplierSiteDepotCode", "SupplierSiteDepotCode");
			parameters.put("SalesOrderNumber", out.getSalesOrderNumber() != null
					? out.getSalesOrderNumber() : "");
			parameters.put("isTaxPayableOnReverse",
					REVERSECHARGE.containsKey(out.getReverseCharge())
							? REVERSECHARGE.get(out.getReverseCharge()) : "");
			
			parameters.put("customergstin",
					out.getCgstin() != null ? out.getCgstin() : "");
			parameters.put("CustomerPhone", out.getCustomerPhone() != null
					? out.getCustomerPhone() : "");
			parameters.put("CustomerEmail", out.getCustomerEmail() != null
					? out.getCustomerEmail() : "");

			String placeOfSupply = "";
			if (out.getPos() != null) {
				String stateName0 = statecodeRepository
						.findStateNameByCode(out.getPos());
				placeOfSupply = stateName0 + "(" + out.getPos() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);
			
			parameters.put("ShipToGSTINNo",
					out.getShipToGstin() != null ? out.getShipToGstin() : "");
			
			parameters.put("EWBNumber",
					(out.getEwbNoresp() != null ? out.getEwbNoresp() + " " : "N/A")
							+ (out.getEwbDateResp() != null
									? (formatter1.format(out.getEwbDateResp()))
									: ""));
			parameters.put("TransportMode", out.getTransportMode() != null
					? out.getTransportMode() : "");
			parameters.put("VehicleNo",
					out.getVehicleNo() != null ? out.getVehicleNo() : "");
			parameters.put("TransporterName", out.getTransporterName() != null
					? out.getTransporterName() : "");
			parameters.put("TransportDocNo", (out.getTransportDocNo() != null
					? out.getTransportDocNo() + " " : "")
					+ (out.getTransportDocDate() != null
							? (formatter2.format(out.getTransportDocDate()))
							: ""));
			parameters.put("SupplierAdd",
					(out.getSupplierBuildingNumber() != null
							? out.getSupplierBuildingNumber() + "\n" : "")
							+ (out.getSupplierBuildingName() != null
									? out.getSupplierBuildingName() + "\n" : "")
							+ (out.getSupplierLocation() != null
									? out.getSupplierLocation() : ""));

			
			parameters.put("CustomerGSTIN",
					out.getCgstin() != null ? out.getCgstin() : "");
			parameters.put("CustomerAdd",
					(out.getCustOrSuppAddress1() != null
							? out.getCustOrSuppAddress1() + "\n" : "")
							+ (out.getCustOrSuppAddress2() != null
									? out.getCustOrSuppAddress2() + "\n" : "")
							+ (out.getCustOrSuppAddress4() != null
									? out.getCustOrSuppAddress4() : ""));

			List<GoodsProductDetails> eInvoiceProductDetails = new ArrayList<>();
			int slNo = 1;
			BigDecimal ttl = new BigDecimal("0.00");
			BigDecimal tt2 = new BigDecimal("0.00");
			BigDecimal tt3 = new BigDecimal("0.00");
			BigDecimal tt4 = new BigDecimal("0.00");
			BigDecimal tt5 = new BigDecimal("0.00");
			for (OutwardTransDocLineItem line : lineItems) {
				GoodsProductDetails productDetails = new GoodsProductDetails();
				productDetails.setSlNo(String.valueOf(slNo));
				productDetails.setPrddesc(
						line.getItemCode() != null ? line.getItemCode() : "");
				productDetails.setPrdname(line.getItemDescription() != null
						? line.getItemDescription() : "");
				productDetails.setHsn(
						line.getHsnSac() != null ? line.getHsnSac() : "");
				productDetails
						.setUqc(line.getUom() != null ? line.getUom() : "");
				if(line.getItemQtyUser()==null) {
					productDetails.setQuantity(
							line.getQty() != null ? line.getQty().toString() : "0");
					} else {
					productDetails.setQuantity( line.getItemQtyUser().toString());
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
				productDetails
						.setItemassessableamount(line.getTaxableValue() != null
								? GenUtil.formatCurrency(line.getTaxableValue())
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
				BigDecimal cessRate = line.getCessRateAdvalorem() != null
						? line.getCessRateAdvalorem() : BigDecimal.ZERO;
				productDetails.setCessadvlrate(String.valueOf(cessRate));
				productDetails
						.setCessadvlamount(line.getCessAmountAdvalorem() != null
								? GenUtil.formatCurrency(
										line.getCessAmountAdvalorem())
								: AMOUNT_STRING);
				BigDecimal cessSpec = line.getCessRateSpecific() != null
						? line.getCessRateSpecific() : BigDecimal.ZERO;
				productDetails.setCessspecificrate(String.valueOf(cessSpec));
				productDetails.setCessspecificamount(
						line.getCessAmountSpecific() != null
								? GenUtil.formatCurrency(
										line.getCessAmountSpecific())
								: AMOUNT_STRING);
				BigDecimal invoiceStateCessAdvaloremAmount = line
						.getStateCessAmount() != null
								? line.getStateCessAmount() : BigDecimal.ZERO;
				BigDecimal invoiceStateCessSpecificAmount = line
						.getStateCessSpecificAmt() != null
								? line.getStateCessSpecificAmt()
								: BigDecimal.ZERO;
				BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
						.add(invoiceStateCessSpecificAmount);
				productDetails.setStatecessamount(stateCessAmount != null
						? GenUtil.formatCurrency(stateCessAmount) : AMOUNT_STRING);
				if (stateCessAmount != null) {
					tt5 = tt5.add(stateCessAmount);
				}
				productDetails.setItemotherchargesFormatted(
						line.getOtherValues() != null
								? GenUtil.formatCurrency(line.getOtherValues())
								: AMOUNT_STRING);
				if (line.getOtherValues() != null) {
					tt2 = tt2.add(line.getOtherValues());
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

			parameters.put("AmtInWords",
					out.getTotalInvoiceValueInWords() != null
							? String.valueOf(out.getTotalInvoiceValueInWords())
							: "");
			parameters.put("Remark", out.getInvoiceRemarks() != null
					? String.valueOf(out.getInvoiceRemarks()) : "");
			parameters.put("RoundOffValue", out.getRoundOff() != null
					? GenUtil.formatCurrency(out.getRoundOff()) : "0.00");
			parameters.put("invoiceothercharges",
					out.getInvoiceOtherCharges() != null ? GenUtil
							.formatCurrency(out.getInvoiceOtherCharges())
							: "0.00");
			parameters.put("invoicediscount",
					out.getUserDefinedField28() != null ? GenUtil
							.formatCurrency(out.getUserDefinedField28())
							: "0.00");
			parameters.put("InvoiceValue", out.getDocAmount() != null
					? GenUtil.formatCurrency(out.getDocAmount()) : "0.00");
			parameters.put("note", supplyType.containsKey(out.getSupplyType())
					? supplyType.get(out.getSupplyType()) : "");

			StringBuffer textdata = new StringBuffer();
			textdata.append("");
			if (entity1 != null && entity1.getEntityId() != null) {
				List<TermConditionsEntity> conditionsEntities = termConditionsRepository
						.getTermConditions(
								Arrays.asList(entity1.getEntityId()));
				conditionsEntities.forEach(entity -> textdata
						.append((conditionsEntities.indexOf(entity) + 1 + ". ")
								+ (entity.getConitionsText()))
						.append("\n"));
			}

			parameters.put("TermNConditions", StringUtils.truncate(textdata.toString(),305));
			BankDetailsEntity bankDetailsEntity = null;

			List<BankDetailsEntity> entity = bankDetailsRepository
					.getBankDetailsByGstin(entity1.getId());
			if (CollectionUtils.isNotEmpty(entity)) {
				bankDetailsEntity = entity.get(0);
			}

			parameters.put("AccountDetails", bankDetailsEntity != null
					? String.valueOf(bankDetailsEntity.getBankAcct()) : " ");
			parameters.put("BranchOrIFSCCode", bankDetailsEntity != null
					? String.valueOf(bankDetailsEntity.getIfscCode()) : "");
			parameters.put("PayeeName", bankDetailsEntity != null
					? String.valueOf(bankDetailsEntity.getBeneficiary()) : "");
			parameters.put("BankName", bankDetailsEntity != null
					? String.valueOf(bankDetailsEntity.getBankName()) : " ");

			parameters.put("BankAddress", bankDetailsEntity != null
					? String.valueOf(bankDetailsEntity.getBankAdd()) : " ");

			if (StringUtils.isEmpty(irn)
					&& EWB_DOC_TYPE
							.contains(trimAndConvToUpperCase(out.getDocType()))
					&& TAX_DOC_TYPE.contains(
							trimAndConvToUpperCase(out.getGstnBifurcation()))) {
				parameters.put("EWBNumber", (out.getEwbNoresp() != null
						? out.getEwbNoresp() + " " : "")
						+ (out.getEwbDateResp() != null
								? (formatter1.format(out.getEwbDateResp()))
								: ""));

			}

			String a14 = "";
			String signedqr = "";
			if (einvoiceEntity != null
					&& einvoiceEntity.getSignedQR() != null) {
				if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Signed QR is Available for doc ID {}",
						out.getId());
				}
				signedqr = einvoiceEntity.getSignedQR();
			}
			if (StringUtils.isNotEmpty(irn)
					&& DOC_TYPE1
							.contains(trimAndConvToUpperCase(out.getDocType()))
					&& TAX_DOC_TYPE1.contains(
							trimAndConvToUpperCase(out.getGstnBifurcation()))) {
				if (einvoiceEntity != null
						&& einvoiceEntity.getSignedQR() != null) {
					if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Signed QR is Available for doc ID {}",
							out.getId());
					}
					signedqr = einvoiceEntity.getSignedQR();
				}
			}

			if (StringUtils.isEmpty(irn)
					&& DOC_TYPE
							.contains(trimAndConvToUpperCase(out.getDocType()))
					&& TAX_DOC_TYPE.contains(
							trimAndConvToUpperCase(out.getGstnBifurcation()))) {
				parameters.put("irn", "NA");
				parameters.put("irnDate", "NA");
				Map<String, List<EntityConfigPrmtEntity>> dynamicQuCode = 
						 getDynamicQrCode(out,entity1);
				 
				 a14 = getAnswerByQuestionId(dynamicQuCode, "G14");
				 String a15 = getAnswerByQuestionId(dynamicQuCode, "G15");
				 String payeAddress = getAnswerByQuestionId(dynamicQuCode, "G16");
				 
				 String payeeName = getAnswerByQuestionId(dynamicQuCode, "G17");
				 
				 String marchentCode = getAnswerByQuestionId(dynamicQuCode, "G18");
				 
				 String a19 = getAnswerByQuestionId(dynamicQuCode, "G19");
				 String a20 = getAnswerByQuestionId(dynamicQuCode, "G20");
				 
				if ("A".equalsIgnoreCase(a14)) {
					signedqr = out.getSgstin() + SLASH + out.getDocNo() + SLASH
							+ out.getDocDate() + SLASH + out.getDocAmount();
				}
				if ("B".equalsIgnoreCase(a14)) {
					/*signedqr =	GoodsCredDebPDFDaoImpl.
							signedQrCodeGeneration(out,a15,a19,payeAddress,payeeName,
							marchentCode,signedqr,a20);*/
					signedqr = qrCodeFetching(out);
				}
				
			}

			if (StringUtils.isEmpty(irn)
					&& DOC_TYPE2
							.contains(trimAndConvToUpperCase(out.getDocType()))
					&& !TAX_DOC_TYPE.contains(
							trimAndConvToUpperCase(out.getGstnBifurcation()))
					&& !TAX_DOC_TYPE.contains(
							trimAndConvToUpperCase(out.getGstnBifurcation()))) {
				if (einvoiceEntity == null
						|| einvoiceEntity.getSignedQR() == null) {
					if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Signed QR is Available for doc ID {}",
							out.getId());
					}
					parameters.put("irn", "NA");
					parameters.put("irnDate", "NA");

					if (out.getDocType().equalsIgnoreCase("DLC")) {
						parameters.put("PageTitle", "Delivery Challan ");
					} else if (out.getDocType().equalsIgnoreCase("BOS")) {
						parameters.put("PageTitle", "Bill Of Supply");
					} else {
						parameters.put("PageTitle", "Document – Others");
					}
				}
			}

			if ("B".equalsIgnoreCase(a14)) {
				parameters.put("qrImg", qRCodeServiceDynamicGenerator.getImage(signedqr));
				}
				else {
				parameters.put("qrImg", qRcodeGenerator.getImage(signedqr));
				
				}
			List<LogoConfigEntity> entity2 = logoConfigRepository
					.getLogFileByGstin(entity1.getId());
			if (CollectionUtils.isNotEmpty(entity2)) {
				LogoConfigEntity logoConfigEntity = entity2.get(0);
				byte[] blob = logoConfigEntity.getLogofile();
				ByteArrayInputStream bis = new ByteArrayInputStream(blob);
				BufferedImage bImage2 = ImageIO.read(bis);
				parameters.put("logo", bImage2);
			}

			parameters.put("invoiceassessableamount",
					out.getInvoiceAssessableAmount() != null ? GenUtil
							.formatCurrency(out.getInvoiceAssessableAmount())
							: "0.00");
			parameters.put("invoicecgstamount",
					out.getInvoiceCgstAmount() != null
							? GenUtil.formatCurrency(out.getInvoiceCgstAmount())
							: "0.00");
			parameters.put("invoicesgstamount",
					out.getInvoiceSgstAmount() != null
							? GenUtil.formatCurrency(out.getInvoiceSgstAmount())
							: "0.00");
			parameters.put("invoiceigstamount",
					out.getInvoiceIgstAmount() != null
							? GenUtil.formatCurrency(out.getInvoiceIgstAmount())
							: "0.00");
			parameters.put("invoicecessadvlamount",
					out.getInvoiceCessAdvaloremAmount() != null
							? GenUtil.formatCurrency(
									out.getInvoiceCessAdvaloremAmount())
							: "0.00");
			parameters.put("invoicecessspecificamount",
					out.getInvoiceCessSpecificAmount() != null
							? GenUtil.formatCurrency(
									out.getInvoiceCessSpecificAmount())
							: "0.00");
			parameters.put("totalothercharges",
					tt2 != null ? GenUtil.formatCurrency(tt2) : "0.00");
			parameters.put("totalitemamt",
					tt3 != null ? GenUtil.formatCurrency(tt3) : "0.00");
			parameters.put("totaldiscount",
					tt4 != null ? GenUtil.formatCurrency(tt4) : "0.00");
			parameters.put("invoicestatecesscamount",
					tt5 != null ? GenUtil.formatCurrency(tt5) : "0.00");
			parameters.put("PaymentDueDate",
					out.getPaymentDueDate() != null
							? String.valueOf(
									formatter2.format(out.getPaymentDueDate()))
							: "");
			parameters.put("PaymentTerms", out.getPaymentTerms() != null
					?  StringUtils.truncate(String.valueOf(out.getPaymentTerms()),100) : "");
			parameters.put("totalproductvalue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			parameters.put("SubInvoiceValue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "");
			LocalDate printedDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			parameters.put("PrintedDate",
					printedDate != null ? formatter2.format(printedDate) : "");
			parameters.put("PaymentInstruction",
					out.getPaymentInstruction() != null
							? StringUtils.truncate(String.valueOf(out.getPaymentInstruction()),100) : "");

			BigDecimal invoiceStateCessAdvaloremAmount = out
					.getInvoiceStateCessAmount() != null
							? out.getInvoiceStateCessAmount() : BigDecimal.ZERO;
			BigDecimal invoiceStateCessSpecificAmount = out
					.getInvStateCessSpecificAmt() != null
							? out.getInvStateCessSpecificAmt()
							: BigDecimal.ZERO;
			BigDecimal stateCessAmount = invoiceStateCessAdvaloremAmount
					.add(invoiceStateCessSpecificAmount);

			parameters.put("StateCess", stateCessAmount != null
					? GenUtil.formatCurrency(stateCessAmount) : "0.00");

			parameters.put("CurrencyCode", "");
			parameters.put("InvoiceValueFC", "");
			if (out.getSupplyType().equals("EXPT")
					|| out.getSupplyType().equals("EXPWT")) {
				
				if (out.getInvoiceValueFc() != null) {
					parameters.put("CurrencyCode", "Invoice Value FC"
							+ (out.getForeignCurrency() != null ? " " + "("
									+ out.getForeignCurrency() + ")" + " " + ":"
									: ":"));
					parameters.put("InvoiceValueFC",
							out.getInvoiceValueFc() != null ? GenUtil
									.formatCurrency(out.getInvoiceValueFc())
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
	
	private String getAnswerByQuestionId(Map<String, 
			     List<EntityConfigPrmtEntity>> map,String QuestionId){
		if(map==null || map.isEmpty() || map.get(QuestionId)==null 
				|| map.get(QuestionId).isEmpty()) return "";
		return map.get(QuestionId).get(0).getParamValue().trim();
		
	}
	     private Map<String, List<EntityConfigPrmtEntity>>  
		                  getDynamicQrCode(OutwardTransDocument out,GSTNDetailEntity entity) {
			

			Map<Long, List<EntityConfigPrmtEntity>> map = onboardingConfigParamCheck
					.getEntityAndConfParamMap();
			List<EntityConfigPrmtEntity> list = map.get(entity.getEntityId());
			List<EntityConfigPrmtEntity> docList = list.stream()
					.filter(doc -> !doc.isDelete())
					.collect(Collectors.toList());
			

			Map<String, List<EntityConfigPrmtEntity>> allDocsMap = docList.stream()
					.collect(Collectors
							.groupingBy(doc -> doc.getParamValKeyId()));
			
			return allDocsMap;
		}

	     private String qrCodeFetching(OutwardTransDocument out){
	 		List<B2CQRCodeRequestLogEntity> doc = qrCodeRepo.findByDockey(out.getDocKey());
	 		if(!doc.isEmpty()){
	 			return doc.get(doc.size() - 1).getRespPayload();
	 		}
	 		return "";
	 	}

	 }