package com.ey.advisory.app.data.services.pdfreader;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.entities.pdfreader.PDFResponseLineItemEntity;
import com.ey.advisory.app.data.entities.pdfreader.PDFResponseSummaryEntity;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRInwardEinvoiceTaggingRepository;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRResponseLogRepo;
import com.ey.advisory.app.data.services.pdfreader.dto.PDFCountDto;
import com.ey.advisory.app.data.services.pdfreader.dto.PdfReaderLineItemRespDto;
import com.ey.advisory.app.data.services.pdfreader.dto.PdfReaderRespDto;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCommonUtility;
import com.ey.advisory.app.data.services.qrvspdf.QRvsPdfCommonUtility;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.async.repositories.master.EinvMasterGstinRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PDFCommonUtility {

	@Autowired
	@Qualifier("GSTNHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClientnew;

	@Autowired
	QRvsPdfCommonUtility qrvsPdfCommonUtility;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPemtRepo;

	@Autowired
	private QRResponseLogRepo respLogRep;

	// autowire the QRInwardEinvoiceTaggingRepository
	@Autowired
	private QRInwardEinvoiceTaggingRepository qrInwardEinvoiceTaggingRepository;

	public final static String EXCEP_ERROR = "Excep_Error";

	private final static String Available = "Available";

	private final static String Valid = "Valid";

	public final static String ERROR = "Error";

	@Autowired
	private EinvMasterGstinRepository einvMasterGstinRepo;

	
	@Autowired
	QRCommonUtility commonUtility;

	public String getPDFResponse(File destFile, String accessTokenPdf) {
		try {

			Map<String, Config> configPDFMap = configManager
					.getConfigs("QRVALIDATOR", "pdf.details", "DEFAULT");

			String pdfApiAccesskey = configPDFMap
					.get("pdf.details.apiAccessKey") == null ? ""
							: configPDFMap.get("pdf.details.apiAccessKey")
									.getValue();

			String pdfReaderUrl = configPDFMap
					.get("pdf.details.validatorUrl") == null ? ""
							: configPDFMap.get("pdf.details.validatorUrl")
									.getValue();

			String response = commonUtility.callPdfTool(destFile,
					accessTokenPdf, pdfApiAccesskey, pdfReaderUrl);

			return response;
		} catch (Exception e) {
			String msg = e.getMessage();
			LOGGER.error(msg, e);
			throw new AppException(msg, e.getMessage());
		} finally {
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_PopulateFileDtls_END",
					"QRCodeValidatorServiceImpl", "saveAndPersistQRReports",
					destFile.getName());

		}
	}

	public void parseAndPopulatePDFResponse(
			List<PDFResponseSummaryEntity> listofSumm,
			List<PDFResponseLineItemEntity> pdflistItems,
			String pdfResponseBody, Long fileId, PDFCountDto countDto,
			String fileName) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("PDF Reader Response {} ", pdfResponseBody);
		}
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			
			PdfReaderRespDto pdfDetails = parsePDFResponse(pdfResponseBody);
			
			PDFResponseSummaryEntity summaryEntity = convertFromPdfReaderRespDto(
					pdfDetails, fileId);

			List<PDFResponseLineItemEntity> listItems = new ArrayList<PDFResponseLineItemEntity>();

			if (pdfDetails.getLineItems().size() > 0) {
				// pdflistItems.addAll(
				// convertFromLineItemsDto(pdfDetails.getLineItems()));
				listItems.addAll(
						convertFromLineItemsDto(pdfDetails.getLineItems()));
				summaryEntity.setPdflistItems(listItems);
			}

			listofSumm.add(summaryEntity);

		} catch (Exception e) {
			String msg = String.format(
					"Exception while doing the Recon for File ID %s", fileId);
			LOGGER.error(msg, e);
			throw new AppException(msg);
		}
	}

	public List<PDFResponseLineItemEntity> convertFromLineItemsDto(
			List<PdfReaderLineItemRespDto> lineItems) {

		List<PDFResponseLineItemEntity> lineItemList = new ArrayList<>();
		PDFResponseLineItemEntity entity;
		int count = 0;
		for (PdfReaderLineItemRespDto dto : lineItems) {
			entity = convertFromLineItemDto(dto, count++);
			lineItemList.add(entity);
		}

		return lineItemList;
	}

	public PDFResponseLineItemEntity convertFromLineItemDto(
			PdfReaderLineItemRespDto lineItem, int itemNumber) {
		PDFResponseLineItemEntity entity = new PDFResponseLineItemEntity();

		entity.setItemNumber(lineItem.getLineItemNo());
		
		String description=lineItem.getDescription();
		String hsnNumber=lineItem.getHsnNumber();

		if (!Strings.isNullOrEmpty(description) && description.length() >100) {
			entity.setDescription(
					description.substring(0, 100));
		}else{
			entity.setDescription(description);
		}
		
		if (!Strings.isNullOrEmpty(hsnNumber) && hsnNumber.length() >20) {
			entity.setHsnNumber(
					hsnNumber.substring(0, 20));
		}else{
			entity.setHsnNumber(hsnNumber);
		}
		
//		String taxRate= lineItem.getTaxRate();
//		String tax= lineItem.getTaxRate();
		
		//entity.setDescription(lineItem.getDescription());
		//entity.setHsnNumber(lineItem.getHsnNumber());
		entity.setQuantity(lineItem.getQuantity());
		entity.setUnitPrice(lineItem.getUnitPrice());
		entity.setTaxableAmount(lineItem.getTaxableAmount());
		entity.setUnit(lineItem.getUnit());
//		entity.setTaxRate(lineItem.getTaxRate());
//		entity.setTaxAmount(lineItem.getTax());
		entity.setTaxRate(decimalCheck(lineItem.getTaxRate()));
		entity.setTaxAmount(decimalCheck(lineItem.getTax()));
		entity.setTtlAmount(lineItem.getAmount());

		return entity;
	}

	public PDFResponseSummaryEntity convertFromPdfReaderRespDto(
			PdfReaderRespDto pdfResp, Long fileId) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("PDF Reader Response Dto {} ", pdfResp);
		}
		
		PDFResponseSummaryEntity entity = new PDFResponseSummaryEntity();
		entity.setFileId(fileId);

		entity.setFileName(pdfResp.getFileName());

		entity.setIrnNumber(pdfResp.getIrnNumber());

		entity.setIrnDate(tryConvertUsingFormat(pdfResp.getIrnDate(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		entity.setDocType(pdfResp.getDocumentType());

		entity.setDocNumber(pdfResp.getInvoiceId());

		entity.setDocDate(tryConvertUsingFormat(pdfResp.getInvoiceDate(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		entity.setRevChrgFlag(pdfResp.getRCm());

		entity.setSuppGstin(pdfResp.getVendorTaxId());

		entity.setSuppName(pdfResp.getVendorName());

		entity.setSuppAddress(pdfResp.getVendorAddress());

		entity.setSuppPincode(pdfResp.getVendorPincode());

		entity.setSuppState(pdfResp.getVendorState());

		entity.setSuppStateCode(pdfResp.getVendorStateCode());

		entity.setCustGstin(pdfResp.getCustomerTaxId());

		entity.setCustName(pdfResp.getCustomerName());

		entity.setCustAddress(pdfResp.getCustomerAddress());

		entity.setCustPincode(pdfResp.getCustomerPincode());

		entity.setCustState(pdfResp.getCustomerAddressRecipient());

		entity.setCustStateCode(pdfResp.getCustomerAddressRecipientCode());

		entity.setBillingAddress(pdfResp.getBillingAddress());

		entity.setShippingAddress(pdfResp.getShippingAddress());

		entity.setBillingPos(pdfResp.getPlaceOfSupply());

		entity.setInvTaxableAmt(pdfResp.getSubTotal());
		
		//entity.setIgstRate(truncateValue(truncateValue(pdfResp.getIgstRate())));

		entity.setIgstRate(decimalCheck(pdfResp.getIgstRate()));

		entity.setIgstAmount(decimalCheck(pdfResp.getIgstAmount()));

		entity.setCgstRate(decimalCheck(pdfResp.getCgstRate()));

		entity.setCgstAmount(decimalCheck(pdfResp.getCgstAmount()));

		entity.setSgstRate(decimalCheck(pdfResp.getSgstRate()));

		entity.setSgstAmount(decimalCheck(pdfResp.getSgstAmount()));

		entity.setUtgstRate(decimalCheck(pdfResp.getUtgstRate()));

		entity.setUtgstAmount(decimalCheck(pdfResp.getUtgstAmount()));

		entity.setCessRate(decimalCheck(pdfResp.getCessRate()));

		entity.setCessAmount(decimalCheck(pdfResp.getCessAmount()));

		entity.setTtlTax(truncateValue(pdfResp.getTotalTax()));

		entity.setInvValue(truncateValue(pdfResp.getInvoiceTotal()));

		entity.setPurchaseOrdNum(pdfResp.getPurchaseOrder());

		entity.setCreatedBy((SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM");
		entity.setCreatedOn(LocalDateTime.now());

		entity.setDocKeyPDF(QRCommonUtility.generateQRCodeKey(
				pdfResp.getVendorTaxId(),
				EYDateUtil.fmtDateOnly(
						tryConvertUsingFormat(
								truncateValue(pdfResp.getInvoiceDate(), 10, 10,
										false),
								DateTimeFormatter.ofPattern("yyyy-MM-dd")),
						DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				mapDocumentType(pdfResp.getDocumentType()),
				truncateValue(pdfResp.getInvoiceId(), 16, 16, false),
				truncateValue(pdfResp.getCustomerTaxId(), 15, 15, true)));

		entity.setValidatedDateTime(LocalDateTime.now());
		return entity;
	}

	private PdfReaderRespDto parsePDFResponse(String pdfResponseBody) {
		if (!pdfResponseBody.equalsIgnoreCase(PDFCommonUtility.EXCEP_ERROR)) {
			
			JsonObject respObject = JsonParser.parseString(pdfResponseBody)
					.getAsJsonObject();
			String status = respObject.get("status").getAsString();
			
			if (status.equalsIgnoreCase("success")
					|| status.equalsIgnoreCase("1")) {
				String dataString = respObject.get("data").toString();
				PdfReaderRespDto pdfDetails = GsonUtil
							.gsonInstanceWithEWBDateFormat()
							.fromJson(dataString, PdfReaderRespDto.class);
				 
				return pdfDetails;
			} else if (status.equalsIgnoreCase("0")) {
				String errMsg = respObject.has("message")
						? respObject.get("message").getAsString()
						: "PDF Reader Failed";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
				
			} else {
				String errMsg = "PDF Reader Failed";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
				
				
			}
		} else {
			String errMsg = "Exception while processing the PDF File.";
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
	}
	
	private String truncateValue(String value, int maxlengthTobeTruncated,
			int lengthToBeTruncated, boolean spTrunNeeded) {

		if (Strings.isNullOrEmpty(value)) {
			return null;
		}
		if (spTrunNeeded) {
			String specialCharacters = "[/\\\\.,\\-_|:;#!@%&*\\(\\)'\"\\s]";
			value = value.replaceAll(specialCharacters, "");
		}

		if (value.length() > maxlengthTobeTruncated) {
			return value.substring(0, lengthToBeTruncated);
		}

		return value;
	}

	private BigDecimal truncateValue(BigDecimal value) {

		if (value == null) {
			return null;
		}
		return value.setScale(2, BigDecimal.ROUND_DOWN);

	}

	private static LocalDate tryConvertUsingFormat(String docDate,
			DateTimeFormatter dateFormat) {
		try {
			return LocalDate.parse(docDate, dateFormat);
		} catch (Exception e) {
			return null;
		}
	}

	private String mapDocumentType(String docType) {
		if (!Strings.isNullOrEmpty(docType)) {
			if (docType.contains("CRN") || docType.contains("Credit")) {
				return "CR";
			} else if (docType.contains("DRN") || docType.contains("Debit")) {
				return "DR";
			} else {
				return "INV";
			}
		}
		return null; // or return a default value based on your requirement
	}

	public static String formattedDateOfUpload(
			LocalDateTime istDateTimeFromUTC) {
		LocalDateTime dateTimeInIst = EYDateUtil
				.toISTDateTimeFromUTC(istDateTimeFromUTC);
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd HH:mm:ss");
		return dateTimeInIst.format(formatter);
	}
	
	public static void main(String[] args) {
		//String [] strs={"0","18.0" , "18", "28.0", "0.0", "0.00%",  "6%" ,null, "9.0"  ,"null",""  ,   "9.00" ,  "NA" , "18.000"};
		//String [] strs={"0","18.0" ,"0.0", "0.00%",  "6%" ,null, "9.0"  ,"null",""  ,   "9.00" ,  "NA" };
//		for (int i = 0; i < strs.length; i++) {
//			
//			System.out.println("Indx : "+i+ "  " +"Original Value : "+strs[i] +"   "+"Calculated Value : "+new PDFCommonUtility().decimalCheck(strs[i]));
//		}
//		String pdfResponseBody ="{\"status\":\"success\",\"data\":{\"File Name\":\"E-invice22082024_20241107T182947441.pdf\",\"Customer Name\":\"Novo Nordisk India Private Ltd.\",\"Customer GSTIN\":\"23AAACB5170B1ZE\",\"Customer Address\":\"KANCHAN PHARMA HOUSE, 8,AHMEDABAD,AHMEDABAD\",\"Customer State\":\"Gujarat\",\"Customer State Code\":\"24\",\"Customer Pincode\":\"382427\",\"Billing Address\":\"ABBOTT INDIA LTD INDORE HUB, SANWER NO 52/2/2 52/2/1 58 & VILLAG,INDORE,INDORE, Madhya Pradesh, 453771\",\"Shipping Address\":\"ABBOTT INDIA LTD INDORE HUB, SANWER NO 52/2/2 52/2/1 58 & VILLAG,INDORE,INDORE, Madhya Pradesh, 453771\",\"Document Number\":\"9880052000\",\"Document Date\":\"2024-02-07\",\"Document Type\":\"INV\",\"IRN Number\":\"2b650cacbf02318827bcc8384a6fb42249eefbd35f1a8bfad5db7fef34d5e148\",\"IRN Date\":\"2024-02-07\",\"Supplier Name\":\"Abbott India Limited\",\"Supplier GSTIN\":\"24AAACN7425M1ZB\",\"Supplier Address\":\"ABBOTT INDIA LTD INDORE HUB, SANWER NO 52/2/2 52/2/1 58 & VILLAG,INDORE,INDORE, Madhya Pradesh, 453771\",\"Supplier State\":\"Madhya Pradesh\",\"Supplier State Code\":\"23\",\"Supplier Pincode\":\"453771\",\"Place of Supply\":\"Madhya Pradesh\",\"Sub Total\":130935850.0,\"Total Tax\":14028841.0,\"Invoice Total\":130935850.0,\"Purchase Order Number\":null,\"CGST Rate\":null,\"CGST Amount\":null,\"SGST Rate\":null,\"SGST Amount\":null,\"IGST Rate\":12.0,\"IGST Amount\":9639965.49,\"UTGST Rate\":null,\"UTGST Amount\":null,\"CESS Rate\":null,\"CESS Amount\":null,\"Reverse Charge / RCM Flag\":\"NO\",\"Line-items\":[{\"Description\":\"RYBELSUS 7MG 10 TABLETS\",\"HSN number\":\"30049099\",\"Quantity\":1,\"Unit Price\":15851.0,\"Taxable Amount\":36573963.29,\"Unit\":\"Nos\",\"Tax Rate\":12.0,\"Tax Amount\":4388875.6,\"Total Amount\":40962838.89,\"Line Item Number\":\"1\"},{\"Description\":\"RYBELSUS 7MG 10 TABLETS\",\"HSN number\":\"30049099\",\"Quantity\":1,\"Unit Price\":34816.0,\"Taxable Amount\":80333045.62,\"Unit\":\"Nos\",\"Tax Rate\":12.0,\"Tax Amount\":9639965.49,\"Total Amount\":89973011.11,\"Line Item Number\":\"2\"}]},\"file_path\":\"E-invice22082024_20241107T182947441.pdf\"}";
		String pdfResponseBody ="{\"File Name\":\"E-invice22082024_20241107T182947441.pdf\",\"Customer Name\":\"Novo Nordisk India Private Ltd.\",\"Customer GSTIN\":\"23AAACB5170B1ZE\",\"Customer Address\":\"KANCHAN PHARMA HOUSE, 8,AHMEDABAD,AHMEDABAD\",\"Customer State\":\"Gujarat\",\"Customer State Code\":\"24\",\"Customer Pincode\":\"382427\",\"Billing Address\":\"ABBOTT INDIA LTD INDORE HUB, SANWER NO 52/2/2 52/2/1 58 & VILLAG,INDORE,INDORE, Madhya Pradesh, 453771\",\"Shipping Address\":\"ABBOTT INDIA LTD INDORE HUB, SANWER NO 52/2/2 52/2/1 58 & VILLAG,INDORE,INDORE, Madhya Pradesh, 453771\",\"Document Number\":\"9880052000\",\"Document Date\":\"2024-02-07\",\"Document Type\":\"INV\",\"IRN Number\":\"2b650cacbf02318827bcc8384a6fb42249eefbd35f1a8bfad5db7fef34d5e148\",\"IRN Date\":\"2024-02-07\",\"Supplier Name\":\"Abbott India Limited\",\"Supplier GSTIN\":\"24AAACN7425M1ZB\",\"Supplier Address\":\"ABBOTT INDIA LTD INDORE HUB, SANWER NO 52/2/2 52/2/1 58 & VILLAG,INDORE,INDORE, Madhya Pradesh, 453771\",\"Supplier State\":\"Madhya Pradesh\",\"Supplier State Code\":\"23\",\"Supplier Pincode\":\"453771\",\"Place of Supply\":\"Madhya Pradesh\",\"Sub Total\":130935850.0,\"Total Tax\":14028841.0,\"Invoice Total\":130935850.0,\"Purchase Order Number\":null,\"CGST Rate\":null,\"CGST Amount\":null,\"SGST Rate\":null,\"SGST Amount\":null,\"IGST Rate\":12.0,\"IGST Amount\":9639965.49,\"UTGST Rate\":null,\"UTGST Amount\":null,\"CESS Rate\":null,\"CESS Amount\":null,\"Reverse Charge / RCM Flag\":\"NO\",\"Line-items\":[{\"Description\":\"RYBELSUS 7MG 10 TABLETS\",\"HSN number\":\"30049099\",\"Quantity\":1,\"Unit Price\":15851.0,\"Taxable Amount\":36573963.29,\"Unit\":\"Nos\",\"Tax Rate\":12.0,\"Tax Amount\":4388875.6,\"Total Amount\":40962838.89,\"Line Item Number\":\"1\"},{\"Description\":\"RYBELSUS 7MG 10 TABLETS\",\"HSN number\":\"30049099\",\"Quantity\":1,\"Unit Price\":34816.0,\"Taxable Amount\":80333045.62,\"Unit\":\"Nos\",\"Tax Rate\":12.0,\"Tax Amount\":9639965.49,\"Total Amount\":89973011.11,\"Line Item Number\":\"2\"}]}";
		PdfReaderRespDto pdfDetails = new PDFCommonUtility().parsePDFResponse(pdfResponseBody);
		System.out.println("OP : "+pdfDetails);
		
//		PdfValidatorRespDto pdfDetails = parsePDFResponse(pdfResponseBody);
//		PdfReaderRespDto pdfDetails = parsePDFResponse(pdfResponseBody);
	}
	
	public BigDecimal decimalCheck(String value) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside decimalCheckr for value {} ", value);
		}
		
		Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
		
		BigDecimal decimalVal = null;

		if (Strings.isNullOrEmpty(value)) {
			return null;
		}
		if (true) {
			String specialCharacters = "[/\\\\,\\-_|:;#!@%&*\\(\\)'\"\\s$₹]";
			value = value.replaceAll(specialCharacters, "");
		}
		
		//boolean parsable = NumberUtils.isParsable(value);
		boolean isNumber = pattern.matcher(value).matches();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Tax Rate OR Tax Amount isNumber {} ", isNumber);
		}
		
		if(isNumber){
			decimalVal = new BigDecimal(value);
			decimalVal = decimalVal.setScale(2, BigDecimal.ROUND_DOWN);
		}

		
		return decimalVal;
	}
	
}
