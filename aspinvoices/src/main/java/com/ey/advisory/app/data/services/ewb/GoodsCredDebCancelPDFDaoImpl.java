/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import com.ey.advisory.core.async.repositories.master.EYConfigRepository;
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

/**
 * @author Sujith.Nanga
 *
 */

@Component("GoodsCredDebCancelPDFDaoImpl")
@Slf4j
public class GoodsCredDebCancelPDFDaoImpl implements GoodsCredDebCancelPDF {

	private static final String AMOUNT_STRING = "0.00";

	@Autowired
	QRcodeGenerator qRcodeGenerator;

	@Autowired
	QRCodeDynamicGenerator qRCodeDynamicGenerator;

	@Autowired
	private OutwardTransDocumentRepository outwardTransDocumentRepo;

	@Autowired
	@Qualifier("EYConfigRepository")
	private EYConfigRepository eYConfigRepository;

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
	@Qualifier("BankDetailsRepository")
	BankDetailsRepository bankDetailsRepository;

	@Autowired
	@Qualifier("LogoConfigRepository")
	LogoConfigRepository logoConfigRepository;

	@Autowired
	@Qualifier("TermConditionsRepository")
	TermConditionsRepository termConditionsRepository;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("B2CQRCodeLoggerRepository")
	B2CQRCodeLoggerRepository qrCodeRepo;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");
	static DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	final static Map<String, String> docCateg = ImmutableMap.of("REG",
			"Supplier Addresses and Customer Addresses", "DIS",
			"Bill from Dispatch from", "SHP", "Bill to ship to", "CMB",
			"Comb of 2 & 3");

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
			"B2CL", "CDNUR-B2CL");

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

	private static final List<String> INV_SUPPLY_TYPE = ImmutableList.of("EXPT",
			"EXPWT");

	final static Map<String, String> REVERSECHARGE = ImmutableMap
			.<String, String>builder().put("Y", "YES").put("N", "NO")
			.put("L", "NO").put(" ", "NO").build();

	final static String SLASH = "/";

	public JasperPrint generateCredDebCancelPdfReport(String id, String docNo,
			String sgstin) {

		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<>();
		String source = "jasperReports/GoodscredInvoice (004).jrxml";
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside generateCredDebCancelPdfReport for docNo: %s", docNo);
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
			String supplierAddress = (out.getSupplierBuildingNumber() != null
					? out.getSupplierBuildingNumber() + "," : "")
					+ (out.getSupplierBuildingName() != null
							? out.getSupplierBuildingName() + "," : "")
					+ (out.getSupplierLocation() != null
							? out.getSupplierLocation() : "");

			parameters.put("SupplierAddress", supplierAddress);
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
			String disptostatename = "";
			if (out.getDispatcherStateCode() != null) {
				disptostatename = statecodeRepository
						.findStateNameByCode(out.getDispatcherStateCode());
			}
			String Shiptostatename = "";
			if (out.getShipToState() != null) {
				Shiptostatename = statecodeRepository
						.findStateNameByCode(out.getShipToState());
			}
			String custstateName = "";
			if (out.getBillToState() != null) {
				custstateName = statecodeRepository
						.findStateNameByCode(out.getBillToState());
			}

			String placeOfSupply4 = "";
			String customerState = "";
			if (out.getBillToState() != null) {
				placeOfSupply4 = out.getBillToState() + "-" + custstateName;

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

			String shiptoTradeName = (out.getShipToLegalName() != null
					? out.getShipToLegalName() : "");
			if (Strings.isNullOrEmpty(shiptoTradeName)) {
				shiptoTradeName = customerName;
			}
			parameters.put("ShipToTradeName", shiptoTradeName);
			String placeOfSupply5 = "";
			String shiptoState = "";
			if (out.getShipToState() != null) {
				placeOfSupply5 = out.getShipToState() + "-" + Shiptostatename;

			}
			shiptoState = (out.getShipToPincode() != null
					? placeOfSupply5 + "," + out.getShipToPincode().toString()
					: "");
			if (Strings.isNullOrEmpty(shiptoState)) {
				shiptoState = customerState;
			}
			parameters.put("ShipToStateCodeNamePincode", shiptoState);

			String dispatcherAddress = (out
					.getDispatcherBuildingNumber() != null
							? out.getDispatcherBuildingNumber() + "," : "")
					+ (out.getDispatcherBuildingName() != null
							? out.getDispatcherBuildingName() + "," : "")
					+ (out.getDispatcherLocation() != null
							? out.getDispatcherLocation() : "");
			if (Strings.isNullOrEmpty(dispatcherAddress)) {
				dispatcherAddress = supplierAddress;
			}
			parameters.put("DispatcherAddress", dispatcherAddress);
			String dispatcherTradeName = (out.getDispatcherTradeName() != null
					? out.getDispatcherTradeName() : "");
			if (Strings.isNullOrEmpty(dispatcherTradeName)) {
				dispatcherTradeName = supplierName;
			}
			parameters.put("DispatcherTradeName", dispatcherTradeName);

			String placeOfSupply0 = "";
			String disState0 = "";
			if (out.getDispatcherStateCode() != null) {
				placeOfSupply0 = out.getDispatcherStateCode() + "-"
						+ disptostatename;
			}
			disState0 = (out.getDispatcherPincode() != null ? placeOfSupply0
					+ "," + out.getDispatcherPincode().toString() : "");
			if (Strings.isNullOrEmpty(disState0)) {
				disState0 = supplierState;
			}
			parameters.put("DispatcherStateCodeNamePincode", disState0);

			String docCategory = out.getDocCategory();

			if (docCategory != null
					&& Lists.newArrayList("REG", "DIS", "SHP", "CMB")
							.contains(docCategory)) {
				switch (docCategory) {
				case "REG": {

					String supplierAddress1 = (out
							.getSupplierBuildingNumber() != null
									? out.getSupplierBuildingNumber() + ","
									: "")
							+ (out.getSupplierBuildingName() != null
									? out.getSupplierBuildingName() + "," : "")
							+ (out.getSupplierLocation() != null
									? out.getSupplierLocation() : "");

					parameters.put("SupplierAddress", supplierAddress1);
					String supplierName1 = (out.getSupplierLegalName() != null
							? out.getSupplierLegalName() : "");
					parameters.put("SupplierName", supplierName1);
					String stateName1 = statecodeRepository.findStateNameByCode(
							out.getSgstin().substring(0, 2));
					String placeOfSupply2 = out.getSgstin().substring(0, 2)
							+ "-" + stateName1;
					if (stateName1 == null) {
						placeOfSupply2 = out.getSgstin().substring(0, 2);
					}
					String supplierState1 = (out.getSupplierPincode() != null
							? placeOfSupply2 + ","
									+ out.getSupplierPincode().toString()
							: "");
					parameters.put("SupplierStateCodeNamePincode",
							supplierState1);

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

					String custstateName1 = "";
					if (out.getBillToState() != null) {
						custstateName1 = statecodeRepository
								.findStateNameByCode(out.getBillToState());
					}

					String placeOfSupply11 = "";
					String customerState0 = "";
					if (out.getBillToState() != null) {
						placeOfSupply11 = out.getBillToState() + "-"
								+ custstateName1;
					}
					customerState0 = (out.getCustomerPincode() != null
							? placeOfSupply11 + ","
									+ out.getCustomerPincode().toString()
							: "");
					parameters.put("CustomerStateCodeNamePincode",
							customerState0);

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

					String placeOfSupply6 = "";
					String shiptoState6 = "";
					if (out.getShipToState() != null) {
						placeOfSupply6 = out.getShipToState() + "-"
								+ Shiptostatename;

					}
					shiptoState6 = (out.getShipToPincode() != null
							? placeOfSupply6 + ","
									+ out.getShipToPincode().toString()
							: "");
					if (Strings.isNullOrEmpty(shiptoState6)) {
						shiptoState6 = customerState;
					}
					parameters.put("ShipToStateCodeNamePincode", shiptoState);

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
						dispatcherAddress1 = supplierAddress;
					}
					parameters.put("DispatcherAddress", dispatcherAddress);
					String dispatcherTradeName1 = (out
							.getDispatcherTradeName() != null
									? out.getDispatcherTradeName() : "");
					if (Strings.isNullOrEmpty(dispatcherTradeName1)) {
						dispatcherTradeName1 = supplierName;
					}
					parameters.put("DispatcherTradeName", dispatcherTradeName1);

					String placeOfSupply10 = "";
					String disState10 = "";
					if (out.getDispatcherStateCode() != null) {
						placeOfSupply10 = out.getDispatcherStateCode() + "-"
								+ disptostatename;

					}
					disState10 = (out.getDispatcherPincode() != null
							? placeOfSupply10 + ","
									+ out.getDispatcherPincode().toString()
							: "");
					if (Strings.isNullOrEmpty(disState10)) {
						disState10 = supplierState;
					}
					parameters.put("DispatcherStateCodeNamePincode", disState0);
					break;
				}
				case "DIS": {
					parameters.put("SupplierAddress",
							(out.getSupplierBuildingNumber() != null
									? out.getSupplierBuildingNumber() + ","
									: "")
									+ (out.getSupplierBuildingName() != null
											? out.getSupplierBuildingName()
													+ ","
											: "")
									+ (out.getSupplierLocation() != null
											? out.getSupplierLocation() : ""));
					parameters.put("SupplierName",
							out.getSupplierLegalName() != null
									? out.getSupplierLegalName() : "");
					String stateName1 = statecodeRepository.findStateNameByCode(
							out.getSgstin().substring(0, 2));
					String placeOfSupply2 = out.getSgstin().substring(0, 2)
							+ "-" + stateName1;
					if (stateName == null) {
						placeOfSupply2 = out.getSgstin().substring(0, 2);
					}
					parameters.put("SupplierStateCodeNamePincode",
							out.getSupplierPincode() != null
									? placeOfSupply2 + "," + out
											.getSupplierPincode().toString()
									: "");
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

					String custstateName1 = "";
					if (out.getBillToState() != null) {
						custstateName1 = statecodeRepository
								.findStateNameByCode(out.getBillToState());
					}

					String placeOfSupply10 = "";
					String customerState0 = "";
					if (out.getBillToState() != null) {
						placeOfSupply10 = out.getBillToState() + "-"
								+ custstateName1;

					}
					customerState0 = (out.getCustomerPincode() != null
							? placeOfSupply10 + ","
									+ out.getCustomerPincode().toString()
							: "");
					parameters.put("CustomerStateCodeNamePincode",
							customerState0);
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

					String placeOfSupply7 = "";
					String shiptoState7 = "";
					if (out.getShipToState() != null) {
						placeOfSupply7 = out.getShipToState().substring(0, 2)
								+ "-" + Shiptostatename;

					}
					shiptoState7 = (out.getShipToPincode() != null
							? placeOfSupply7 + ","
									+ out.getShipToPincode().toString()
							: "");
					if (Strings.isNullOrEmpty(shiptoState7)) {
						shiptoState7 = customerState;
					}
					parameters.put("ShipToStateCodeNamePincode", shiptoState7);

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

					String placeOfSupply01 = "";
					String disState01 = "";
					if (out.getDispatcherStateCode() != null) {
						placeOfSupply01 = out.getDispatcherStateCode() + "-"
								+ disptostatename;

					}
					disState01 = (out.getDispatcherPincode() != null
							? placeOfSupply01 + ","
									+ out.getDispatcherPincode().toString()
							: "");
					if (Strings.isNullOrEmpty(disState01)) {
						disState01 = supplierState;
					}
					parameters.put("DispatcherStateCodeNamePincode",
							disState01);
					break;
				}
				case "SHP": {
					String supplierAddress1 = (out
							.getSupplierBuildingNumber() != null
									? out.getSupplierBuildingNumber() + ","
									: "")
							+ (out.getSupplierBuildingName() != null
									? out.getSupplierBuildingName() + "," : "")
							+ (out.getSupplierLocation() != null
									? out.getSupplierLocation() : "");

					parameters.put("SupplierAddress", supplierAddress1);
					String supplierName1 = (out.getSupplierLegalName() != null
							? out.getSupplierLegalName() : "");
					parameters.put("SupplierName", supplierName1);
					String stateName1 = statecodeRepository.findStateNameByCode(
							out.getSgstin().substring(0, 2));
					String placeOfSupply2 = out.getSgstin().substring(0, 2)
							+ "-" + stateName1;
					if (stateName == null) {
						placeOfSupply2 = out.getSgstin().substring(0, 2);
					}
					String supplierState1 = (out.getSupplierPincode() != null
							? placeOfSupply2 + ","
									+ out.getSupplierPincode().toString()
							: "");
					parameters.put("SupplierStateCodeNamePincode",
							supplierState1);

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
					String custstateName1 = "";
					if (out.getBillToState() != null) {
						custstateName1 = statecodeRepository
								.findStateNameByCode(out.getBillToState());
					}

					String placeOfSupply01 = "";
					String customerState01 = "";
					if (out.getBillToState() != null) {
						placeOfSupply01 = out.getBillToState() + "-"
								+ custstateName1;
					}
					customerState01 = (out.getCustomerPincode() != null
							? placeOfSupply01 + ","
									+ out.getCustomerPincode().toString()
							: "");
					parameters.put("CustomerStateCodeNamePincode",
							customerState01);
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
					String placeOfSupply02 = "";
					String shiptoState02 = "";
					if (out.getShipToState() != null) {
						placeOfSupply02 = out.getShipToState() + "-"
								+ Shiptostatename;
					}
					shiptoState02 = (out.getShipToPincode() != null
							? placeOfSupply02 + ","
									+ out.getShipToPincode().toString()
							: "");
					if (Strings.isNullOrEmpty(shiptoState02)) {
						shiptoState02 = customerState;
					}
					parameters.put("ShipToStateCodeNamePincode", shiptoState02);

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
						dispatcherAddress1 = supplierAddress;
					}
					parameters.put("DispatcherAddress", dispatcherAddress);
					String dispatcherTradeName1 = (out
							.getDispatcherTradeName() != null
									? out.getDispatcherTradeName() : "");
					if (Strings.isNullOrEmpty(dispatcherTradeName1)) {
						dispatcherTradeName1 = supplierName;
					}
					parameters.put("DispatcherTradeName", dispatcherTradeName1);

					String placeOfSupply03 = "";
					String disState03 = "";
					if (out.getDispatcherStateCode() != null) {
						placeOfSupply03 = out.getDispatcherStateCode() + "-"
								+ disptostatename;

					}
					disState03 = (out.getDispatcherPincode() != null
							? placeOfSupply03 + ","
									+ out.getDispatcherPincode().toString()
							: "");
					if (Strings.isNullOrEmpty(disState03)) {
						disState03 = supplierState;
					}
					parameters.put("DispatcherStateCodeNamePincode",
							disState03);
					break;
				}
				case "CMB": {
					parameters.put("SupplierAddress",
							(out.getSupplierBuildingNumber() != null
									? out.getSupplierBuildingNumber() + ","
									: "")
									+ (out.getSupplierBuildingName() != null
											? out.getSupplierBuildingName()
													+ ","
											: "")
									+ (out.getSupplierLocation() != null
											? out.getSupplierLocation() : ""));
					parameters.put("SupplierName",
							out.getSupplierLegalName() != null
									? out.getSupplierLegalName() : "");
					String stateName1 = statecodeRepository.findStateNameByCode(
							out.getSgstin().substring(0, 2));
					String placeOfSupply2 = out.getSgstin().substring(0, 2)
							+ "-" + stateName1;
					if (stateName == null) {
						placeOfSupply2 = out.getSgstin().substring(0, 2);
					}
					parameters.put("SupplierStateCodeNamePincode",
							out.getSupplierPincode() != null
									? placeOfSupply2 + "," + out
											.getSupplierPincode().toString()
									: "");
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

					String custstateName1 = "";
					if (out.getBillToState() != null) {
						custstateName1 = statecodeRepository
								.findStateNameByCode(out.getBillToState());
					}

					String placeOfSupply02 = "";
					String customerState0 = "";
					if (out.getBillToState() != null) {
						placeOfSupply02 = out.getBillToState() + "-"
								+ custstateName1;

					}
					customerState0 = (out.getCustomerPincode() != null
							? placeOfSupply02 + ","
									+ out.getCustomerPincode().toString()
							: "");
					parameters.put("CustomerStateCodeNamePincode",
							customerState0);
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
					String placeOfSupply04 = "";
					String shiptoState04 = "";
					if (out.getShipToState() != null) {
						placeOfSupply04 = out.getShipToState() + "-"
								+ Shiptostatename;

					}
					shiptoState = (out.getShipToPincode() != null
							? placeOfSupply04 + ","
									+ out.getShipToPincode().toString()
							: "");
					if (Strings.isNullOrEmpty(shiptoState04)) {
						shiptoState04 = customerState;
					}
					parameters.put("ShipToStateCodeNamePincode", shiptoState04);
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
					String placeOfSupply05 = "";
					String disState05 = "";
					if (out.getDispatcherStateCode() != null) {
						placeOfSupply05 = out.getDispatcherStateCode() + "-"
								+ disptostatename;

					}
					disState05 = (out.getDispatcherPincode() != null
							? placeOfSupply05 + ","
									+ out.getDispatcherPincode().toString()
							: "");
					if (Strings.isNullOrEmpty(disState05)) {
						disState05 = supplierState;
					}
					parameters.put("DispatcherStateCodeNamePincode",
							disState05);

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
					out.getIrnResponse() != null ? out.getIrnResponse() : "NA");
			parameters.put("irnDate", out.getAckDate() != null
					? (formatter1.format(out.getAckDate())) : "NA");
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

			String custgstin = (out.getCgstin() != null ? out.getCgstin() : "");
			parameters.put("customergstin", custgstin);
			parameters.put("customergstin",
					out.getCgstin() != null ? out.getCgstin() : "");
			parameters.put("CustomerPhone", out.getCustomerPhone() != null
					? out.getCustomerPhone() : "");
			parameters.put("CustomerEmail", out.getCustomerEmail() != null
					? out.getCustomerEmail() : "");

			String placeOfSupply = "";
			if (out.getPos() != null) {
				String stateName1 = statecodeRepository
						.findStateNameByCode(out.getPos());
				placeOfSupply = stateName1 + "(" + out.getPos() + ")";
			}

			parameters.put("BillingPos", placeOfSupply);

			String shiptState = (out.getShipToGstin() != null
					? out.getShipToGstin() : "");
			if (Strings.isNullOrEmpty(shiptState)) {
				shiptState = custgstin;
			}
			parameters.put("ShipToGSTINNo",
					out.getShipToGstin() != null ? out.getShipToGstin() : "");

			parameters.put("EWBNumber", (out.getEwbNoresp() != null
					? out.getEwbNoresp() + " " : "NA")
					+ (out.getEwbDateResp() != null
							? (formatter1.format(out.getEwbDateResp())) : ""));
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
				if (line.getItemQtyUser() == null) {
					productDetails.setQuantity(line.getQty() != null
							? line.getQty().toString() : "0");
				} else {
					productDetails
							.setQuantity(line.getItemQtyUser().toString());
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
						? GenUtil.formatCurrency(stateCessAmount)
						: AMOUNT_STRING);
				if (stateCessAmount != null) {
					tt5 = tt5.add(stateCessAmount);
				}
				productDetails.setItemotherchargesFormatted(
						line.getOtherValues() != null
								? GenUtil.formatCurrency(line.getOtherValues())
								: AMOUNT_STRING);
				productDetails.setTotalitemValFormatted(
						line.getTotalItemAmount() != null
								? GenUtil.formatCurrency(
										line.getTotalItemAmount())
								: AMOUNT_STRING);
				if (line.getTotalItemAmount() != null) {
					ttl = ttl.add(line.getTotalItemAmount());
				}
				if (line.getOtherValues() != null) {
					tt2 = tt2.add(line.getOtherValues());
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
						.append((conditionsEntities.indexOf(entity) + 1 + " . ")
								+ (entity.getConitionsText()))
						.append("\n"));
			}
			parameters.put("TermNConditions", StringUtils.truncate(textdata.toString(),302));
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

			if (StringUtils.isEmpty(irn) && EWB_DOC_TYPE
					.contains(trimAndConvToUpperCase(out.getDocType()))) {
				parameters.put("EWBNumber", (out.getEwbNoresp() != null
						? out.getEwbNoresp() + " " : "NA")
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
			if (StringUtils.isNotEmpty(irn) && DOC_TYPE1
					.contains(trimAndConvToUpperCase(out.getDocType()))) {
				if (einvoiceEntity != null
						&& einvoiceEntity.getSignedQR() != null) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Signed QR is Available for doc ID {}",
								out.getId());
					}
					signedqr = einvoiceEntity.getSignedQR();
				}
			}

			if (StringUtils.isEmpty(irn) && DOC_TYPE
					.contains(trimAndConvToUpperCase(out.getDocType()))) {
				parameters.put("irn", "NA");
				parameters.put("irnDate", "NA");
				Map<String, List<EntityConfigPrmtEntity>> dynamicQuCode = getDynamicQrCode(
						out, entity1);

				a14 = getAnswerByQuestionId(dynamicQuCode, "G14");
				// String a15 = getAnswerByQuestionId(dynamicQuCode, "G15");
				/*
				 * String payeAddress = getAnswerByQuestionId(dynamicQuCode,
				 * "G16");
				 * 
				 * String payeeName = getAnswerByQuestionId(dynamicQuCode,
				 * "G17");
				 * 
				 * String marchentCode = getAnswerByQuestionId(dynamicQuCode,
				 * "G18");
				 * 
				 * String a19 = getAnswerByQuestionId(dynamicQuCode, "G19");
				 * String a20 = getAnswerByQuestionId(dynamicQuCode, "G20");
				 */

				if ("A".equalsIgnoreCase(a14)) {
					signedqr = out.getSgstin() + SLASH + out.getDocNo() + SLASH
							+ out.getDocDate() + SLASH + out.getDocAmount();
				}
				if ("B".equalsIgnoreCase(a14)) {
					/*
					 * signedqr = GoodsCredDebPDFDaoImpl.
					 * signedQrCodeGeneration(out,a15,a19,payeAddress,payeeName,
					 * marchentCode,signedqr,a20);
					 */
					signedqr = qrCodeFetching(out);

				}

			}

			if (StringUtils.isEmpty(irn) && DOC_TYPE2
					.contains(trimAndConvToUpperCase(out.getDocType()))) {
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
				parameters.put("qrImg",
						qRCodeDynamicGenerator.getImage(signedqr));
			} else {
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

			File imgFile = ResourceUtils.getFile(
					"classpath:jasperReports/CANCELLED WATERMARK-1.jpg");
			byte[] blob = Files.readAllBytes(Paths.get(imgFile.getPath()));
			ByteArrayInputStream bis = new ByteArrayInputStream(blob);
			BufferedImage bImage2 = ImageIO.read(bis);
			parameters.put("bgCanImage", bImage2);

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

			parameters.put("PaymentDueDate",
					out.getPaymentDueDate() != null
							? String.valueOf(
									formatter2.format(out.getPaymentDueDate()))
							: "");
			parameters.put("PaymentTerms", out.getPaymentTerms() != null
					? StringUtils.truncate(String.valueOf(out.getPaymentTerms()),100) : "");
			parameters.put("totalothercharges",
					tt2 != null ? GenUtil.formatCurrency(tt2) : "0.00");
			parameters.put("totalitemamt",
					tt3 != null ? GenUtil.formatCurrency(tt3) : "0.00");
			parameters.put("totaldiscount",
					tt4 != null ? GenUtil.formatCurrency(tt4) : "0.00");
			parameters.put("totalproductvalue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "0.00");
			parameters.put("SubInvoiceValue",
					ttl != null ? GenUtil.formatCurrency(ttl) : "0.00");
			parameters.put("invoicestatecesscamount",
					tt5 != null ? GenUtil.formatCurrency(tt5) : "0.00");

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

	private String getAnswerByQuestionId(
			Map<String, List<EntityConfigPrmtEntity>> map, String QuestionId) {
		if (map == null || map.isEmpty() || map.get(QuestionId) == null
				|| map.get(QuestionId).isEmpty())
			return "";
		return map.get(QuestionId).get(0).getParamValue().trim();

	}

	private Map<String, List<EntityConfigPrmtEntity>> getDynamicQrCode(
			OutwardTransDocument out, GSTNDetailEntity entity) {

		Map<Long, List<EntityConfigPrmtEntity>> map = onboardingConfigParamCheck
				.getEntityAndConfParamMap();
		List<EntityConfigPrmtEntity> list = map.get(entity.getEntityId());
		List<EntityConfigPrmtEntity> docList = list.stream()
				.filter(doc -> !doc.isDelete()).collect(Collectors.toList());

		Map<String, List<EntityConfigPrmtEntity>> allDocsMap = docList.stream()
				.collect(Collectors.groupingBy(doc -> doc.getParamValKeyId()));

		return allDocsMap;
	}

	private String qrCodeFetching(OutwardTransDocument out) {
		List<B2CQRCodeRequestLogEntity> doc = qrCodeRepo
				.findByDockey(out.getDocKey());
		if (!doc.isEmpty()) {
			return doc.get(doc.size() - 1).getRespPayload();
		}
		return "";
	}

}