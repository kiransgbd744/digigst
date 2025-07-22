/**
 * 
 */
package com.ey.advisory.app.service.upload.way3recon;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadErrorHeaderEntity;
import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadErrorItemEntity;
import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadProcessedHeaderEntity;
import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadProcessedItemEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbUploadErrorHeaderRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbUploadErrorItemRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbUploadProcessedHeaderRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbUploadProcessedItemRepository;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.FormatValidationUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Ewb3WayReconUploadFileArrivalServiceImpl")
public class Ewb3WayReconUploadFileArrivalServiceImpl
		implements Ewb3WayReconUploadFileArrivalService {

	private static String[] headerArray = { "EWB No", "EWB Date", "Supply Type",
			"Doc.No", "Doc.Date", "Other Party GSTIN", "Transporter Details",
			"From GSTIN Info", "TO GSTIN Info", "status", "No of Items",
			"Main HSN Code", "Main HSN Desc", "Assessable Value", "SGST Value",
			"CGST Value", "IGST Value", "CESS Value", "CESS Non.Advol Value",
			"Other Value", "Total Invoice Value", "Valid Till Date",
			"Mode of Generation", "Cancelled By", "Cancelled Date" };

	private static List<String> HEADER_LIST = Arrays.asList(headerArray);

	private static ThreadLocal<NumberFormat> numberFormatter = ThreadLocal
			.withInitial(() -> new DecimalFormat("0"));

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("EwbUploadErrorItemRepository")
	private EwbUploadErrorItemRepository errItemRepo;

	@Autowired
	@Qualifier("EwbUploadProcessedHeaderRepository")
	private EwbUploadProcessedHeaderRepository psdHeaderRepo;

	@Autowired
	@Qualifier("EwbUploadProcessedItemRepository")
	private EwbUploadProcessedItemRepository psdItemRepo;

	@Autowired
	@Qualifier("EwbUploadErrorHeaderRepository")
	private EwbUploadErrorHeaderRepository errHeaderRepo;

	@Autowired
	@Qualifier("EwbUploadValidation")
	private EwbUploadValidation validation;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("Ewb3WayReconBusinessValidation") private
	 * Ewb3WayReconBusinessValidation businessValidation;
	 */

	private static String duplicateItemCode = "ER-1010-3";

	private static final Map<String, String> HEADER_FIELDS = new ImmutableMap.Builder<String, String>()
			.put("EWB-1001", "EWB number cannot be left blank")
			.put("EWB-1002", "Invalid EWB Number")
			.put("ER-1013", "Invalid Assessable value")
			.put("ER-1014", "Invalid SGST amount")
			.put("ER-1015", "Invalid CGST amount")
			.put("ER-1016", "Invalid IGST Amount")
			.put("ER-1017", "Invalid CESS Amount")
			.put("ER-1018", "Invalid specific CESS amount")
			.put("ER-1019", "Invalid other amount")
			.put("ER-1020", "Invalid Invoice value")
			.put("ER-1024", "Invalid cancelled date")
			.put("ER-1012", "Invalid HSN or SAC Desc")
			.put("ER-1011", "Invalid HSN or SAC Code")
			.put("ER-1010", "Invalid Item serial number")
			.put("ER-1003", "Document Number cannot be left blank")
			.put("ER-1004", "Invalid Document Number")
			.put("ER-1004-2", "Invalid Document Date")
			.put("ER-1004-1", "Document Date cannot be left blank")
			.put("ER-1002-1", "Supply Type cannot be left blank")
			.put("ER-1002-2", "Invalid Supply Type")
			.put("ER-1001-1", "EWB Date cannot be left blank")
			.put("ER-1001-2", "Invalid EWB Date and Time")
			.put("ER-1007", "From GSTIN Info cannot be left blank")
			.put("ER-1007-1", "Invalid Supplier GSTIN")
			.put("ER-1007-2", "Supplier GSTIN is not as per On-Boarding data")
			.put("ER-1008", "To GSTIN Info cannot be left blank")
			// .put("ER-1008-1", "Invalid Customer GSTIN")
			.put("ER-1010-1", "Item serial number cannot be left Blank")
			.put("ER-1010-3", "Duplicate No of Items")
			.put("ER-1010-2", "Invalid No of Items")

			.build();

	private InputStream getFileInpStream(String fileName, String folderName) {
		InputStream inputStream = null;
		try {
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("openCmisSession:{} ", openCmisSession);
			}
			Document document = gstr1FileUploadUtil.getDocument(openCmisSession,
					fileName, folderName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("document name:{}", document.getName());
			}
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			LOGGER.error(
					"Exception occured in reading File Ewb3WayReconUploadFileArrivalServiceImpl",
					e);
			throw new AppException(
					"Error occured while reading the file " + fileName, e);
		}
		return inputStream;
	}

	private void validateHeaders(String fileName, String folderName,
			Long fileId) {
		try {
			InputStream fin = getFileInpStream(fileName, folderName);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, 25);

			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverseHeaderOnly(fin, layout, rowHandler, null);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<String> actualHeaderNames = new ArrayList(
					Arrays.asList(rowHandler.getHeaderRow()));

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"User upload file headers names %s and header count %d",
						actualHeaderNames.toString(), actualHeaderNames.size());
				LOGGER.debug(msg);
			}

			if (actualHeaderNames.size() != 25) {
				String msg = "The number of columns in the file should be 25. Aborting the file processing.";
				markFileAsFailed(fileId, msg);
				LOGGER.debug(msg);
				throw new AppException(msg);
			}

			List<String> expectedHeaderNames = null;
			expectedHeaderNames = HEADER_LIST;

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Expected User response headers names "
								+ "%s and header count %d",
						expectedHeaderNames.toString(),
						expectedHeaderNames.size());
				LOGGER.debug(msg);
			}

			boolean isMatch = headersMatch(expectedHeaderNames,
					actualHeaderNames);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Comparing two files header name " + "and count: %s",
						isMatch);
				LOGGER.debug(msg);
			}

			if (!isMatch) {
				String msg = "The header names/order are not as expected.";
				markFileAsFailed(fileId, msg);
				throw new AppException(msg);
			}

		} catch (Exception ex) {
			String msg = (ex instanceof AppException) ? ex.getMessage()
					: "Error occured while processing the file";
			markFileAsFailed(fileId, msg);
			throw (ex instanceof AppException) ? ((AppException) ex)
					: new AppException(msg, ex);
		}

	}

	private void markFileAsFailed(Long fileId, String reason) {

		String msg = "[SEVERE] Unable to mark the file as failed."
				+ "Reason for file failure is: [" + reason + "]";
		LOGGER.error(msg);
	}

	@Transactional(value = "clientTransactionManager")
	public void validateResponseFile(Long fileId, String fileName,
			String folderName) {

		LOGGER.debug("Validating user uploaded header with template header");

		Optional<Gstr1FileStatusEntity> fileStatusEntity = fileStatusRepository
				.findById(fileId);

		List<EwbUploadProcessedHeaderEntity> processedEntityList = new ArrayList<>();
		List<EwbUploadErrorHeaderEntity> errorEntityList = new ArrayList<>();

		try {
			// reading row data
			validateHeaders(fileName, folderName, fileId);
			InputStream fin = getFileInpStream(fileName, folderName);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(25);

			/*
			 * FileUploadDocRowHandler<?> rowHandler = new
			 * FileUploadDocRowHandler<>(); List<Object[]> fileList =
			 * ((FileUploadDocRowHandler<?>) rowHandler).getFileUploadList();
			 */

			EwbUploadDocRowHandler<?> rowHandler = new EwbUploadDocRowHandler<String>(
					new EwbUploadDocumentKeyBuilder());

			/*
			 * String filePath = new StringJoiner("/").add(folderName)
			 * .add(fileName).toString(); traverser.traverse(filePath, layout,
			 * rowHandler, null);
			 */

			traverser.traverse(fin, layout, rowHandler, null);

			Map<String, List<Object[]>> documentMap = ((EwbUploadDocRowHandler<?>) rowHandler)
					.getDocumentMap();

			if (!documentMap.isEmpty()) {
				LOGGER.debug("Document Map  Length {}", documentMap.size());
			}

			if (documentMap.isEmpty() || documentMap == null) {
				String msg = "Failed Empty file..";
				LOGGER.error(msg);
				throw new AppException(msg);

			}

			Map<String, List<ProcessingResult>> processingResults = validation(
					documentMap, fileId, null, null);

			List<String> listKeys = new ArrayList<>();
			for (String keys : processingResults.keySet()) {
				listKeys.add(keys);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("structural Validations {} keys {}",
						processingResults.values(), processingResults.keySet());
			}
			if (!processingResults.isEmpty()) {

				Map<String, List<Object[]>> documentMapObj = new HashMap<>();
				Map<String, List<Object[]>> errDocMapObj = new HashMap<>();
				for (String keys : documentMap.keySet()) {
					if (!listKeys.contains(keys)) {
						List<Object[]> list = documentMap.get(keys);
						documentMapObj.put(keys, list);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(" documentMap.keySet Begining");
						}
					} else {
						List<Object[]> list = documentMap.get(keys);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.info(" errDocMapObj.keySet Begining");
						}
						errDocMapObj.put(keys, list);
					}

				}
				try {
					if (!errDocMapObj.isEmpty()) {

						errorEntityList = makeErrorEntityList(fileId,
								errDocMapObj, processingResults);

						List<String> docKeyList = errorEntityList.stream()
								.distinct().map(o -> o.getDocKey())
								.collect(Collectors
										.toCollection(ArrayList::new));

						softDelete(docKeyList, false);

						errHeaderRepo.saveAll(errorEntityList);
					}

					if (!documentMapObj.isEmpty()) {
						processedEntityList = makeProcessedEntityList(fileId,
								documentMapObj);
						List<String> docKeyListPsd = processedEntityList
								.stream().distinct().map(o -> o.getDocKey())
								.collect(Collectors
										.toCollection(ArrayList::new));

						softDelete(docKeyListPsd, true);

						psdHeaderRepo.saveAll(processedEntityList);
					}

				} catch (Exception e) {
					LOGGER.error("Error Occured:{} ", e);
					fileStatusEntity.get().setProcessed(0);
					fileStatusEntity.get().setError(0);
					fileStatusEntity.get().setTotal(0);
					fileStatusEntity.get().setFileStatus("FAILED");
					fileStatusRepository.save(fileStatusEntity.get());
					throw new AppException(e);
				}

			} else {

				processedEntityList = makeProcessedEntityList(fileId,
						documentMap);
				List<String> docKeyList = processedEntityList.stream()
						.distinct().map(o -> o.getDocKey())
						.collect(Collectors.toCollection(ArrayList::new));

				softDelete(docKeyList, true);

				psdHeaderRepo.saveAll(processedEntityList);

			}

			fileStatusEntity.get().setProcessed(processedEntityList.size());
			fileStatusEntity.get().setError(errorEntityList.size());
			fileStatusEntity.get().setTotal(documentMap.keySet().size());
			fileStatusEntity.get().setFileStatus("PROCESSED");
			fileStatusRepository.save(fileStatusEntity.get());

		} catch (Exception ex) {

			String msg = "Failed, error while reading file.";
			ex.printStackTrace();
			LOGGER.error(msg, ex);

			throw new AppException(msg, ex);
		}
	}

	private boolean headersMatch(List<String> expected, List<String> actual) {
		return !Streams
				.zip(actual.stream(), expected.stream(),
						(a, e) -> createPair(a, e))
				.filter(p -> !p.getValue0().equals(p.getValue1())).findAny()
				.isPresent();
	}

	private Pair<String, String> createPair(String val1, String val2) {
		String val1Str = (val1 == null) ? "" : val1.trim().toUpperCase();
		String val2Str = (val2 == null) ? "" : val2.trim().toUpperCase();
		return new Pair<>(val1Str, val2Str);
	}

	private String createDocType(String supplyType) {

		Map<String, String> docTypeMap = getdocTypeMap();

		if (supplyType != null && docTypeMap.containsKey(supplyType)) {
			return supplyType != null ? docTypeMap.get(supplyType) : null;
		} else {
			return null;
		}

	}

	private Map<String, String> getdocTypeMap() {

		Map<String, String> map = new HashMap<>();

		map.put("Outward Supply", "INV");
		map.put("Outward Export", "INV");
		map.put("Outward Job Work", "DLC");
		map.put("Outward SKD/CKD", "INV");
		map.put("Outward Recipient not known", "DLC");
		map.put("Outward For own use", "DLC");
		map.put("Outward Exhibition or Fairs", "DLC");
		map.put("Outward Line Sales", "DLC");
		map.put("Outward Others", "DLC");

		return map;
	}

	private Map<String, List<ProcessingResult>> validation(
			Map<String, List<Object[]>> documentMap, Long fileId,
			Integer errorCount, Integer psdCount) {
		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside Ewb3WayReconUploadFileArrivalServiceImpl "
					+ "Validation method ";
			LOGGER.debug(msg);
		}

		// gstin info list
		List<String> gstinList = gSTNDetailRepository.findAllGstin();

		Map<String, List<ProcessingResult>> errorResultMap = new HashMap<>();

		documentMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			List<Object[]> value = entry.getValue();

			// First do normal structural valdiations (cell by cell)
			List<ProcessingResult> validationResult = new ArrayList<>();

			Set<String> serialNoList = new HashSet<>();

			for (Object[] rowData : value) {

				validation.rowDataValidation(validationResult, rowData,
						gstinList, value.indexOf(rowData));

				if (isPresent(rowData[10])
						&& !serialNoList.add(rowData[10].toString())) {
					String errMsg = String
							.format("Duplicate Item serial number");
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.ITEM_SERIAL_NUMBER);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							value.indexOf(rowData), errorLocations.toArray());
					validationResult.add(new ProcessingResult(APP_VALIDATION,
							duplicateItemCode, errMsg, location));

				}

			}

			if (!validationResult.isEmpty()) {
				errorResultMap.put(key, validationResult);
			}

		});
		return errorResultMap;

	}

	private List<EwbUploadProcessedHeaderEntity> makeProcessedEntityList(
			Long fileId, Map<String, List<Object[]>> documentMap) {

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		List<EwbUploadProcessedHeaderEntity> documents = new ArrayList<>();
		documentMap.entrySet().forEach(entry -> {
			String key = entry.getKey();

			List<Object[]> value = entry.getValue();

			EwbUploadProcessedHeaderEntity document = new EwbUploadProcessedHeaderEntity();

			List<EwbUploadProcessedItemEntity> lineItems = new ArrayList<>();

			BigDecimal igstHeader = BigDecimal.ZERO;
			BigDecimal cessNonAdvlHeader = BigDecimal.ZERO;
			BigDecimal cgstHeader = BigDecimal.ZERO;
			BigDecimal sgstHeader = BigDecimal.ZERO;
			BigDecimal cessHeader = BigDecimal.ZERO;
			BigDecimal totalInvoiceValHeader = BigDecimal.ZERO;
			BigDecimal assesValHeader = BigDecimal.ZERO;
			BigDecimal otherValueHeader = BigDecimal.ZERO;

			String custGstin = null;
			String docDate = null;
			String docNo = null;
			String supplyType = null;
			String canDate = null;
			String ewbNo = null;
			String time = null;
			LocalDate date = null;
			String status = null;
			String[] timeString = null;
			String suppGstin = null;
			String docType = null;
			String suppAddress = null;
			String custAddress = null;
			String transPorterDeatils = null;
			String validTillDate = null;
			
			String fromGstinInfo = null;
			String toGstinInfo = null;

			for (Object[] obj : value) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Key - " + key + ", Value - "
							+ Arrays.toString(obj));
				}
				EwbUploadProcessedItemEntity entity = new EwbUploadProcessedItemEntity();

				ewbNo = getWithValue(obj[0]);
				String ewbDate = getWithValue(obj[1]);
				supplyType = getWithValue(obj[2]);
			
				fromGstinInfo = getWithValue(obj[7]);
				toGstinInfo = getWithValue(obj[8]);
				
				docType = createDocType(supplyType);
				docNo = getWithValue(obj[3]);
				docDate = getWithValue(obj[4]);
				String otherPartyGstin = getWithValue(obj[5]);
				transPorterDeatils = getWithValue(obj[6]);
				status = getWithValue(obj[9]);
				String noOfItem = getWithValue(obj[10]);
				String hsnCode = getWithValue(obj[11]);
				String hsnDesc = getWithValue(obj[12]);
				String assessibleValue = getWithValue(obj[13]);
				String sgst = getWithValue(obj[14]);
				String cgst = getWithValue(obj[15]);
				String igst = getWithValue(obj[16]);
				String cess = getWithValue(obj[17]);
				String cessAdvl = getWithValue(obj[18]);
				String othValue = getWithValue(obj[19]);
				String invoiceValue = getWithValue(obj[20]);
				validTillDate = getWithValue(obj[21]);
				String modeOfGen = getWithValue(obj[22]);
				String canBy = getWithValue(obj[23]);
				canDate = getWithValue(obj[24]);

				entity.setCancelBy(canBy);
				if (canDate != null) {
					String canDateArry[] = canDate.split("T");
					entity.setCancelDate(canDateArry[0]);
				}
				if (validTillDate != null) {

					String validTillDateArry[] = validTillDate.split("T");
					entity.setValidTillDate(validTillDateArry[0]);
				}

				BigDecimal cessNonAdvl = cessAdvl != null && !cessAdvl.isEmpty()
						? new BigDecimal(cessAdvl) : BigDecimal.ZERO;

				entity.setCessAmountAdv(cessNonAdvl);
				cessNonAdvlHeader = cessNonAdvlHeader.add(cessNonAdvl);

				BigDecimal cessAmt = cess != null && !cess.isEmpty()
						? new BigDecimal(cess) : BigDecimal.ZERO;
				entity.setCessAmountSpec(cessAmt);
				cessHeader = cessHeader.add(cessAmt);

				BigDecimal cgstAmt = cgst != null && !cgst.isEmpty()
						? new BigDecimal(cgst) : BigDecimal.ZERO;
				entity.setCgstAmount(cgstAmt);
				cgstHeader = cgstHeader.add(cgstAmt);

				entity.setCreatedBy(userName);
				entity.setCreatedDate(LocalDateTime.now());

				if (toGstinInfo != null && toGstinInfo.length() > 14) {
					custGstin = toGstinInfo.trim().substring(0, 15);

					String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
							+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
							+ "[A-Za-z0-9][A-Za-z0-9]$";

					Pattern pattern = Pattern.compile(regex);

					Matcher matcher = pattern.matcher(custGstin.trim());

					if (matcher.matches()) {
						entity.setCustomerGstin(custGstin);
					}

					custAddress = toGstinInfo.substring(15);
					if (custAddress.length() > 500) {
						custAddress = toGstinInfo.substring(15, 500);
					}
				}

				//entity.setCustomerAddr1(custAddress);
				entity.setCustomerTradename(custAddress);
				//entity.setCustomerGstin(custGstin);

				entity.setDocDate(DateUtil.parseObjToDate(docDate));

				entity.setDocNumber(docNo);
				entity.setDocType(docType);

				String ewbDateTime = ewbDate;

				date = DateFormatForStructuralValidatons
						.parseObjToDate(ewbDateTime.trim());

				timeString = ewbDateTime.split("T");

				entity.setEwbDate(date);

				if (timeString.length > 1) {
					for (int i = 1; i < timeString.length; i++) {
						if (!timeString[i].isEmpty())
							time = timeString[i];

					}
					entity.setEwbTime(LocalTime.parse(time));
				}

				entity.setEwbStatus(status);

				if (obj[0] instanceof Number) {
					ewbNo = numberFormatter.get().format(obj[0]);
				} else
					ewbNo = obj[0].toString().trim();

				entity.setEwbStatus(status);

				entity.setEwbNo(Long.valueOf(ewbNo));

				entity.setHsn(hsnCode);

				BigDecimal igstAmount = igst != null && !igst.isEmpty()
						? new BigDecimal(igst) : BigDecimal.ZERO;

				entity.setIgstAmount(igstAmount);
				igstHeader = igstHeader.add(igstAmount);

				BigDecimal totalInvoiceValue = invoiceValue != null
						&& !invoiceValue.isEmpty()
								? new BigDecimal(invoiceValue)
								: BigDecimal.ZERO;
				entity.setInvoiceValue(totalInvoiceValue);
				totalInvoiceValHeader = totalInvoiceValHeader
						.add(totalInvoiceValue);

				BigDecimal itemAsseAmount = assessibleValue != null
						&& !assessibleValue.isEmpty()
								? new BigDecimal(assessibleValue)
								: BigDecimal.ZERO;
				entity.setItemAsseAmount(itemAsseAmount);
				assesValHeader = assesValHeader.add(itemAsseAmount);

				entity.setItemSerialNo(noOfItem);

				entity.setModeofGeneration(modeOfGen);

				BigDecimal otherValue = othValue != null && !othValue.isEmpty()
						? new BigDecimal(othValue) : BigDecimal.ZERO;
				entity.setOtherValue(otherValue);
				otherValueHeader = otherValueHeader.add(otherValue);

				entity.setOthPartyGstin(otherPartyGstin);

				entity.setProductDesc(hsnDesc);

				BigDecimal sgstValue = sgst != null && !sgst.isEmpty()
						? new BigDecimal(sgst) : BigDecimal.ZERO;
				entity.setSgstAmount(sgstValue);
				sgstHeader = sgstHeader.add(sgstValue);

				if (fromGstinInfo != null && fromGstinInfo.length() > 14) {
					suppGstin = fromGstinInfo.substring(0, 15);

					suppAddress = fromGstinInfo.substring(15);

					if (fromGstinInfo.length() > 500) {
						suppAddress = fromGstinInfo.substring(15, 500);
					}
				}

				entity.setFromGstinInfo(fromGstinInfo);
				entity.setSupplierTradeName(suppAddress);
				entity.setSupplierGstin(suppGstin);
				

				// entity.setSubSupplyType(supplyType);
				entity.setSupplyType(supplyType);

				entity.setToGstinInfo(toGstinInfo);
				entity.setTransactionId(transPorterDeatils);

				entity.setDocKey(key);
				entity.setFileId(fileId);
				entity.setDataOrigin("E");
				

				lineItems.add(entity);

			}

			document.setIsProcessed(true);
			document.setIsError(false);
			document.setIsDelete(false);
			document.setEwbNo(ewbNo);
			document.setEwbDate(date);
			document.setEwbStatus(status);
			if (timeString.length > 1) {
				for (int i = 1; i < timeString.length; i++) {
					if (!timeString[i].isEmpty())
						time = timeString[i];

				}
				document.setTime(LocalTime.parse(time));
			}

			document.setFromGstinInfo(fromGstinInfo);
			document.setToGstinInfo(toGstinInfo);
			
			document.setSupplyType(supplyType);
			
			document.setSupplierGstin(suppGstin);
			document.setAssessableVal(assesValHeader);
			document.setOtherVal(otherValueHeader);
			document.setIgstAmount(igstHeader);
			document.setSgstAmount(sgstHeader);
			document.setCessAmountSpec(cessHeader);
			document.setCanDate(canDate);
			document.setCessAmountAdv(cessNonAdvlHeader);
			document.setCgstAmount(cgstHeader);
			document.setInvoiceValue(totalInvoiceValHeader);
			document.setCreatedBy(userName);
			document.setCreatedDate(LocalDateTime.now());
			document.setCustomerGstin(custGstin);
			document.setDerviedTaxPeriod(GenUtil.convertTaxPeriodToInt(
					setRetPeriod(DateUtil.parseObjToDate(docDate))));
			document.setReturnPeriod(
					setRetPeriod(DateUtil.parseObjToDate(docDate)));
			document.setDocKey(key);
			document.setDocNum(docNo);
			document.setDocType(docType);
			if ("INV".equalsIgnoreCase(docType)) {
				document.setEInvAppl(true);
				document.setComplianceAppl(true);
			}
			document.setFileId(fileId);
			document.setDocDate(DateUtil.parseObjToDate(docDate));
			document.setLineItems(lineItems);
			document.setSupplierTradeName(suppAddress);
			document.setCustomerTradename(custAddress);
			document.setTransactionId(transPorterDeatils);
			document.setDataOrigin("E");
			if (validTillDate != null) {

				String validTillDateArry[] = validTillDate.split("T");
				document.setValidTillDate(validTillDateArry[0]);
			}
			documents.add(document);
			document.getLineItems().forEach(item -> {
				item.setEwbHeaderId(document);
			});

		});

		return documents;

	}

	private void softDelete(List<String> docKeys, boolean flag) {

		if (docKeys.isEmpty())
			return;

		int softDeletedCount = 0;

		List<List<String>> chunks = Lists.partition(docKeys, 2000);
		for (List<String> chunk : chunks) {
			LOGGER.debug("Inside Chunk Method");
			int rowsEffected = 0;
			if (flag) {
				rowsEffected = psdHeaderRepo.updateIsDeleteFlag(chunk);
			} else {
				rowsEffected = errHeaderRepo.updateIsDeleteFlag(chunk);
			}
			softDeletedCount = softDeletedCount + rowsEffected;
			String msg = String.format(
					" %d rows affected in TBL_EWB_FU_HEADER table",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	private List<EwbUploadErrorHeaderEntity> makeErrorEntityList(Long fileId,
			Map<String, List<Object[]>> documentMap,
			Map<String, List<ProcessingResult>> errorResultMap) {

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		List<EwbUploadErrorHeaderEntity> documents = new ArrayList<>();
		documentMap.entrySet().forEach(entry -> {
			String key = entry.getKey();

			List<ProcessingResult> processingResults = errorResultMap.get(key);

			List<Object[]> value = entry.getValue();

			EwbUploadErrorHeaderEntity document = new EwbUploadErrorHeaderEntity();

			List<EwbUploadErrorItemEntity> lineItems = new ArrayList<>();
			String docDate = null;
			String docNo = null;

			String custGstin = null;

			for (Object[] obj : value) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Key - " + key + ", Value - "
							+ Arrays.toString(obj));
				}
				EwbUploadErrorItemEntity entity = new EwbUploadErrorItemEntity();

				String ewbNo = getWithValue(obj[0]);
				String ewbDate = getWithValue(obj[1]);
				String supplyType = getWithValue(obj[2]);
				docNo = getWithValue(obj[3]);
				docDate = getWithValue(obj[4]);
				String otherPartyGstin = getWithValue(obj[5]);
				String transPorterDeatils = getWithValue(obj[6]);
				String fromGstinInfo = getWithValue(obj[7]);
				String toGstinInfo = getWithValue(obj[8]);
				String status = getWithValue(obj[9]);
				String noOfItem = getWithValue(obj[10]);
				String hsnCode = getWithValue(obj[11]);
				String hsnDesc = getWithValue(obj[12]);
				String assessibleValue = getWithValue(obj[13]);
				String sgst = getWithValue(obj[14]);
				String cgst = getWithValue(obj[15]);
				String igst = getWithValue(obj[16]);
				String cess = getWithValue(obj[17]);
				String cessAdvl = getWithValue(obj[18]);
				String othValue = getWithValue(obj[19]);
				String invoiceValue = getWithValue(obj[20]);
				String validTillDate = getWithValue(obj[21]);
				String modeOfGen = getWithValue(obj[22]);
				String canBy = getWithValue(obj[23]);
				String canDate = getWithValue(obj[24]);

				entity.setCancelBy(canBy);
				entity.setCancelDate(canDate);
				entity.setCessAmountAdv(cessAdvl);
				entity.setCessAmountSpec(cess);

				entity.setCgstAmount(cgst);

				entity.setCreatedBy(userName);
				entity.setCreatedDate(LocalDateTime.now());

				String custAddress = null;
				if (toGstinInfo != null && toGstinInfo.length() > 14) {
					custGstin = toGstinInfo.substring(0, 15);
					custAddress = toGstinInfo.substring(15);
					entity.setCustomerGstin(custGstin);
					//entity.setCustomerAddr1(custAddress);
				} else {
					entity.setCustomerGstin(toGstinInfo);
					//entity.setCustomerAddr1(toGstinInfo);
				}

				entity.setDocDate(docDate);
				entity.setDocNumber(docNo);
				// entity.setDocType(docType);
				entity.setSupplyType(supplyType);

				entity.setEwbDate(ewbDate);

				entity.setEwbStatus(status);

				if (FormatValidationUtil.isPresent(obj[0])) {
					if (obj[0] instanceof Number) {
						ewbNo = numberFormatter.get().format(obj[0]);
					} else
						ewbNo = obj[0].toString().trim();
				}

				entity.setEwbNumber(ewbNo);

				entity.setHsn(hsnCode);

				entity.setIgstAmount(igst);

				entity.setInvoiceValue(invoiceValue);

				entity.setItemAsseAmount(assessibleValue);

				entity.setItemSerialNo(noOfItem);

				entity.setModeofGeneration(modeOfGen);

				entity.setOtherValue(othValue);
				entity.setOtherPartyGstin(otherPartyGstin);

				entity.setProductDesc(hsnDesc);

				entity.setSgstAmount(sgst);

				String suppGstin = null;
				String suppAddress = null;
				if (fromGstinInfo != null && fromGstinInfo.length() > 14) {
					suppGstin = fromGstinInfo.substring(0, 15);
					suppAddress = fromGstinInfo.substring(15);
					entity.setSupplierAdd1(suppAddress);
					entity.setSupplierGstin(suppGstin);
				} else {
					entity.setSupplierAdd1(fromGstinInfo);
					entity.setSupplierGstin(fromGstinInfo);
				}

				entity.setFromGstinInfo(fromGstinInfo);

				// entity.setSubSupplyType(supplyType);
				entity.setSupplyType(supplyType);

				entity.setToGstinInfo(toGstinInfo);
				entity.setTransactionDocDate(null);
				entity.setTransactionDocNo(null);
				entity.setTransactionId(transPorterDeatils);
				entity.setTransactionName(null);
				entity.setTransactionType(supplyType);
				entity.setValidTillDate(validTillDate);

				entity.setDocKey(key);
				entity.setFileId(fileId);
				lineItems.add(entity);

			}

			// document.setIgstAmount(igst);
			// document.setCanDate(canDate);
			// document.setCessAmountAdv(cessAmountAdv);
			// document.setCgstAmount(cgstAmount);
			document.setCreatedBy(userName);
			document.setCreatedDate(LocalDateTime.now());
			// document.setCustomerGstin(customerGstin);
			// document.setDataType(dataType);
			// document.setDerviedTaxPeriod(derviedTaxPeriod);
			document.setDocKey(key);
			document.setDocNum(docNo);
			// document.setDocType(docType);
			document.setFileId(fileId);

			if (docDate != null) {
				String docDateArry[] = docDate.split("T");
				document.setDocDate(docDateArry[0]);
			}
			document.setLineItems(lineItems);
			documents.add(document);

			document.getLineItems().forEach(item -> {
				item.setEwbHeaderId(document);
			});

			populateErrorCodesAndDescription(processingResults, key, document);

		});

		return documents;
	}

	private String getWithValue(Object obj) {
		String value = obj != null && !obj.toString().trim().isEmpty()
				? String.valueOf(obj.toString().trim()) : null;
		return value;
	}

	private void populateErrorCodesAndDescription(List<ProcessingResult> errors,
			String docKey, EwbUploadErrorHeaderEntity errDoc) {
		// List<ProcessingResult> errors = processingResults.get(docKey);
		List<String> headerList = new ArrayList<>();
		Map<Integer, List<String>> aitemErrorMap = new HashMap<>();
		for (ProcessingResult error : errors) {
			TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) error
					.getLocation();
			Integer lineNo = null;
			if (loc != null) {
				lineNo = loc.getLineNo();
			}

			if (loc == null || lineNo == null)
				headerList.add(error.getCode());
			else
				aitemErrorMap.computeIfAbsent(lineNo, k -> new ArrayList<>())
						.add(error.getCode());
		}
		if (!headerList.isEmpty()) {
			errDoc.setErrorCode(
					headerList.stream().collect(Collectors.joining(",")));
			errDoc.setErrorDesc(setErrorDesc(errDoc.getErrorCode()));

		}
		if (aitemErrorMap.size() > 0) {
			IntStream.range(0, errDoc.getLineItems().size()).forEach(idx -> {
				EwbUploadErrorItemEntity item = errDoc.getLineItems().get(idx);

				if (aitemErrorMap.get(idx) != null) {
					item.setErrorCode(aitemErrorMap.get(idx).stream()
							.collect(Collectors.joining(",")));
					item.setErrorDesc(setErrorDesc(item.getErrorCode()));

				}
			});
		}
	}

	private String setErrorDesc(String errorCodes) {

		List<String> errorList = new ArrayList<>();
		String[] errorCodeArr = errorCodes.split(",");
		for (String errorCode : errorCodeArr) {

			errorList.add(errorCode + " - " + HEADER_FIELDS.get(errorCode));

		}
		return errorList.stream().collect(Collectors.joining(","));
	}

	private String setRetPeriod(LocalDate docDate) {

		int month = docDate.getMonth().getValue();
		int year = docDate.getYear();

		String taxPeriod = month < 10 ? "0" + month + year : "" + month + year;
		return taxPeriod;

	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * String a = "123456789";
	 * 
	 * System.out.println(a.substring(3, 9)); }
	 */

}
