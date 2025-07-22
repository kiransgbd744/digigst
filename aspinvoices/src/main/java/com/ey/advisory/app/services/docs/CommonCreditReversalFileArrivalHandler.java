package com.ey.advisory.app.services.docs;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.CommonCreditReversalHeaderCheckService;
import com.ey.advisory.app.data.entities.client.CommonCreditReversalErr;
import com.ey.advisory.app.data.entities.client.CommonCreditReversalPsdEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.CommonCreditReversalErrorStatusRepository;
import com.ey.advisory.app.data.repositories.client.CommonCreditReversalPsdRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.CommonCreditReversalProcessedDto;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.strcutvalidation.einvoice.CommonCreditReversalStructuralChain;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Service("CommonCreditReversalFileArrivalHandler")
public class CommonCreditReversalFileArrivalHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CommonCreditReversalFileArrivalHandler.class);

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("CommonCreditReversalHeaderCheckService")
	private CommonCreditReversalHeaderCheckService commonCreditReversalHeaderCheckService;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwardTransDocRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("CommonCreditReversalErrorStatusRepository")
	private CommonCreditReversalErrorStatusRepository commonCreditReversalErrorStatusRepository;

	@Autowired
	@Qualifier("CommonCreditReversalPsdRepository")
	private CommonCreditReversalPsdRepository commonCreditReversalPsdRepository;

	@Autowired
	@Qualifier("CommonCreditReversalStructuralChain")
	private CommonCreditReversalStructuralChain commonCreditReversalStructuralChain;

	private static final String DOC_KEY_JOINER = "|";
	private static final String EXCEPTION_APP = "Exception while saving the records";
	private static final String EXCEPTIONS = "Exception Occured: {}";
	private static final Map<String, String> ERROR_CODE_MAP = new ImmutableMap.Builder<String, String>()
			.put("ER1035", "Customer GSTIN cannot be left balnk")
			.put("ER1034", "Invalid Customer GSTIN")
			.put("ER1037", "Document Type cannot be left blank")
			.put("ER1038", "Invalid Document Type")
			.put("ER1041", "Document Number cannot be left blank")
			.put("ER1042", "Invalid Document no")
			.put("ER1043", "Document Date cannot be left blank")
			.put("ER1044", "Invalid Document Date")
			.put("ER0023", "Invalid Supplier GSTIN")
			.put("ER0046", "Line Number cannot be left Blank")
			.put("ER0047", "Invalid Line Number")
			.put("ER1101", "Common Supply Indicator cannot be left balnk")
			.put("ER1102", "Invalid Common Supply Indicator")
			.put("ER1030",
					"Revised Return Period for ITC Reversal Computation cannot be left blank")
			.put("ER1031",
					"Invalid Revised Return Period for ITC Reversal Computation")
			.put("ER0050",
					"Record is not found in DigiGST Processed PR data, please re-check")
			.put("ER0051",
					"Item Serial Number cannot be repeated in a document")
			.build();

	public void processProductFile(Message message, AppExecContext context,
			String docId) {

		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();
		String userName = message.getUserName();
		try {
			Document document = DocumentUtility.downloadDocumentByDocId(docId);
			if (document == null) {
				String errMsg = String.format(
						"Unable to find the file %s in folder %s for docId %s",
						fileName, fileFolder, docId);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			InputStream inputStream = document.getContentStream().getStream();
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(8);

			CommonCreditReversalDocumentKeyBuilder documentKeyBuilder = new CommonCreditReversalDocumentKeyBuilder();
			CommonCreditReversalFileUploadDocRowHandler rowHandler = new CommonCreditReversalFileUploadDocRowHandler<String>(
					documentKeyBuilder);
			traverser.traverse(inputStream, layout, rowHandler, null);

			Object[] getHeaders = rowHandler.getHeaderData();
			Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
					.getFileName(fileName);
			Pair<Boolean, String> checkHeaderFormat = commonCreditReversalHeaderCheckService
					.validate(getHeaders);
			if (checkHeaderFormat == null || !checkHeaderFormat.getValue0()) {
				LOGGER.error("Headers not matched");
				updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
				gstr1FileStatusRepository.save(updateFileStatus);
				return;
			}

			Map<String, List<Object[]>> documentMap = ((CommonCreditReversalFileUploadDocRowHandler<?>) rowHandler)
					.getDocumentMap();
			Map<String, List<ProcessingResult>> strValidation = commonCreditReversalStructuralChain
					.validation(documentMap);

			List<String> listKeys = new ArrayList<>();
			for (String keys : strValidation.keySet()) {
				listKeys.add(keys);
			}
			Integer totalRecords = 0;
			Integer processedRecords = 0;
			Integer errorRecords = 0;
			if (!strValidation.isEmpty()) {

				Map<String, List<Object[]>> processMapObj = new HashMap<>();
				Map<String, List<Object[]>> errDocMapObj = new HashMap<>();
				for (String keys : documentMap.keySet()) {
					if (!listKeys.contains(keys)) {
						List<Object[]> list = documentMap.get(keys);
						processMapObj.put(keys, list);
					} else {
						List<Object[]> list = documentMap.get(keys);

						errDocMapObj.put(keys, list);
					}
				}
				try {
					saveErrDocAndDoc(documentKeyBuilder, documentMap,
							updateFileStatus, strValidation, processMapObj,
							errDocMapObj, userName);

				} catch (Exception e) {
					LOGGER.error("Error Occured:{} ", e);
					updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
					gstr1FileStatusRepository.save(updateFileStatus);
					throw new AppException(EXCEPTION_APP, e);

				}

			} else {
				try {
					convertToCommonCreditReversalDocument(documentMap,
							documentKeyBuilder, updateFileStatus, userName);
					processedRecords = commonCreditReversalPsdRepository
							.businessValidationCount(updateFileStatus.getId());
					if (updateFileStatus != null) {
						errorRecords = commonCreditReversalErrorStatusRepository
								.errorCount(
										updateFileStatus.getId().toString());
					}
					totalRecords = documentMap.size();
					updateFileStatus.setTotal(totalRecords);
					updateFileStatus.setProcessed(processedRecords);
					updateFileStatus.setError(errorRecords);
					gstr1FileStatusRepository.save(updateFileStatus);
				} catch (Exception e) {
					LOGGER.error("Error Occured:{} ", e);
					updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
					gstr1FileStatusRepository.save(updateFileStatus);
					throw new AppException(EXCEPTION_APP, e);

				}
			}
		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
			throw new AppException(EXCEPTION_APP, e);
		}
	}

	private FileArrivalMsgDto extractAndValidateMessage(Message message) {
		FileArrivalMsgDto msg = GsonUtil.newSAPGsonInstance()
				.fromJson(message.getParamsJson(), FileArrivalMsgDto.class);

		if (msg.getFilePath() == null || msg.getFileName() == null) {
			String errMsg = "Invalid Path/FileName received in msg";
			String logMsg = String.format(
					"Values received are -> "
							+ "Path: '%s', FileName: '%s', GroupCode: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.error(String.format("%s %s", msg, logMsg));
			throw new AppException(errMsg);
		}
		// Log the extracted info from the message.
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Message Details are -> Path: '%s', "
							+ "File Name: '%s', Group Code: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.error(logMsg);
		}
		return msg;
	}

	public static Object trimObj(Object obj) {

		return obj != null ? obj.toString().trim() : null;
	}

	@Transactional(value = "clientTransactionManager")
	private void saveErrDocAndDoc(
			CommonCreditReversalDocumentKeyBuilder documentKeyBuilder,
			Map<String, List<Object[]>> documentMap,
			Gstr1FileStatusEntity updateFileStatus,
			Map<String, List<ProcessingResult>> processingResults,
			Map<String, List<Object[]>> documentMapObj,
			Map<String, List<Object[]>> errDocMapObj, String userName) {
		Integer totalRecords = 0;
		Integer processedRecords = 0;
		Integer errorRecords = 0;
		// Integer businessErrorCount;
		try {
			if (!errDocMapObj.isEmpty()) {
				convertToCommonCreditReversalErr(errDocMapObj,
						processingResults, updateFileStatus, userName);
			}
			if (!documentMapObj.isEmpty()) {
				convertToCommonCreditReversalDocument(documentMapObj,
						documentKeyBuilder, updateFileStatus, userName);
			}
			processedRecords = commonCreditReversalPsdRepository
					.businessValidationCount(updateFileStatus.getId());
			if (updateFileStatus != null) {
				errorRecords = commonCreditReversalErrorStatusRepository
						.errorCount(updateFileStatus.getId().toString());
			}
			totalRecords = documentMap.size();
			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			gstr1FileStatusRepository.save(updateFileStatus);
		} catch (Exception e) {
			LOGGER.error(EXCEPTIONS, e);
			throw new AppException(EXCEPTION_APP);
		}
	}

	public void convertToCommonCreditReversalErr(
			Map<String, List<Object[]>> errDocMapObj,
			Map<String, List<ProcessingResult>> processingResults,
			Gstr1FileStatusEntity fileStatus, String userName) {
		List<CommonCreditReversalErr> errorHeaders = new ArrayList<>();
		try {
			LocalDateTime localDate = LocalDateTime.now();
			errDocMapObj.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<ProcessingResult> errorList = processingResults.get(key);
				List<Object[]> objs = entry.getValue();
				for (Object[] obj : objs) {
					CommonCreditReversalErr errorDocument = new CommonCreditReversalErr();
					String custGstin = getWithValue(obj[0]);
					String documentType = getWithValue(obj[1]);
					String documentNumber = getWithValue(obj[2]);
					String documentDate = getWithValue(obj[3]);
					if (documentDate != null) {
						if (documentDate.contains("T00:00:00")) {
							String[] split = documentDate.split("T");
							documentDate = split[0];
						}
					}
					String supplierGstin = getWithValue(obj[4]);
					String itemSerialNumber = getWithValue(obj[5]);
					String commonSupplyIndicator = getWithValue(obj[6]);
					String revisedTaxPeriod = getWithValue(obj[7]);
					errorDocument.setIsDelete(false);
					errorDocument.setCustGstin(custGstin);
					errorDocument.setDocType(documentType);
					errorDocument.setDocNum(documentNumber);
					errorDocument.setDocDate(documentDate);
					errorDocument.setSupplierGstin(supplierGstin);
					errorDocument.setItemSerialNum(itemSerialNumber);
					errorDocument
							.setCommonSupplierIndicator(commonSupplyIndicator);
					errorDocument.setRevisedTaxPeriod(revisedTaxPeriod);
					errorDocument.setCreatedBy(userName);
					errorDocument.setCreatedOn(localDate);
					errorDocument.setDataOriginInTypeCode("E");
					errorDocument.setCommonCreditDocKey(key);
					Pair<String, String> errorCodeDescPair = populateErrorCodesAndDescription(
							errorList);
					errorDocument.setErrorCodes(errorCodeDescPair.getValue0());
					errorDocument.setErrorDesc(errorCodeDescPair.getValue1());
					errorDocument.setFileId(fileStatus.getId().toString());
					errorDocument.setIsError(true);
					errorDocument.setUpdatedBy(userName);
					errorDocument.setUpdatedOn(localDate);

					errorHeaders.add(errorDocument);
				}
			});
			commonCreditReversalErrorStatusRepository.saveAll(errorHeaders);
		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
			throw new AppException(EXCEPTION_APP);
		}
		LOGGER.error("CommonCreditReversalsDocErrConvertion "
				+ "convertSRFileToOutWardTransError Endining");
	}

	public void convertToCommonCreditReversalDocument(
			Map<String, List<Object[]>> documentMapObj,
			CommonCreditReversalDocumentKeyBuilder documentKeyBuilder,
			Gstr1FileStatusEntity fileStatus, String userName) {
		List<CommonCreditReversalProcessedDto> dtoList = new ArrayList<>();
		List<String> docKeyList = new ArrayList<>();
		Map<String, List<CommonCreditReversalProcessedDto>> updatedMap = new HashMap<>();
		List<CommonCreditReversalProcessedDto> processedList = new ArrayList<>();
		List<CommonCreditReversalProcessedDto> errorList = new ArrayList<>();
		try {
			documentMapObj.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<Object[]> objs = entry.getValue();
				Map<String, List<CommonCreditReversalProcessedDto>> map = convertToDto(
						objs, userName, key, fileStatus);
				chekPRKeyValidation(map);
				updatedMap.putAll(map);
			});

			updatedMap.entrySet().forEach(e -> {
				dtoList.addAll(e.getValue());
			});

			for (CommonCreditReversalProcessedDto dto : dtoList) {
				if (dto.getIsError()) {
					errorList.add(dto);
				} else {
					docKeyList.add(dto.getCommonCreditDocKey());
					processedList.add(dto);
				}
			}

			List<CommonCreditReversalPsdEntity> docHeaders = convertToProcessedRecords(
					processedList);
			List<CommonCreditReversalErr> errorHeaders = convertToErrorRecords(
					errorList);

			softDelete(docKeyList);
			commonCreditReversalPsdRepository.saveAll(docHeaders);
			commonCreditReversalErrorStatusRepository.saveAll(errorHeaders);

		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
		}
		LOGGER.error("SRFileToOutwardTransDocErrConvertion "
				+ "convertSRFileToOutWardTransError Endining");
	}

	private String deriveErrorDesc(String errorCodes) {
		String[] errorCode = errorCodes.split(",");
		List<String> errorDescList = new ArrayList<>();
		for (String code : errorCode) {
			errorDescList.add(code + "-" + ERROR_CODE_MAP.get(code));
		}
		return errorDescList.stream().collect(Collectors.joining(","));
	}

	private void chekPRKeyValidation(
			Map<String, List<CommonCreditReversalProcessedDto>> documentMap) {

		List<String> prlist = new ArrayList<>();
		List<Boolean> duplicateCheck = new ArrayList<>();
		List<Boolean> taxPeriodCheck = new ArrayList<>();
		documentMap.entrySet().forEach(entry -> {
			String checkForDuplicateKey = entry.getKey();
			List<CommonCreditReversalProcessedDto> list = documentMap
					.get(checkForDuplicateKey);
			if (list.size() > 1) {
				duplicateCheck.add(true);
				for (CommonCreditReversalProcessedDto dto : list) {
					dto.setIsError(true);
					dto.setErrorCodes("ER0051");
					dto.setErrorDesc(
							"ER0051-Item Serial Number cannot be repeated in a document");
				}
			} else {
				prlist.add(list.get(0).getPrDocKey());
			}
		});
		if (!duplicateCheck.isEmpty()) {
			documentMap.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<CommonCreditReversalProcessedDto> list = documentMap
						.get(key);
				for (CommonCreditReversalProcessedDto dto : list) {
					dto.setIsError(true);
				}
			});
		} else {
			List<List<String>> chunks = Lists.partition(prlist, 2000);
			List<InwardTransDocLineItem> allLineItems = new ArrayList<>();

			Map<String, List<Integer>> keyLineMap = new HashMap<>();
			for (List<String> chunk : chunks) {
				List<InwardTransDocument> activeRecords = inwardTransDocRepository
						.findIsdRecordsByDocKeys(chunk);
				for (InwardTransDocument document : activeRecords) {
					String documentKey = document.getDocKey();
					List<InwardTransDocLineItem> lineItems = document
							.getLineItems();
					allLineItems.addAll(lineItems);
					List<Integer> lineNumberList = new ArrayList<>();
					for (InwardTransDocLineItem docLineItem : lineItems) {
						lineNumberList.add(docLineItem.getLineNo());
					}
					keyLineMap.put(documentKey, lineNumberList);
				}
			}

			List<String> errorKeys = new ArrayList();
			documentMap.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<CommonCreditReversalProcessedDto> list = documentMap
						.get(key);
				for (CommonCreditReversalProcessedDto dto : list) {
					if (keyLineMap.containsKey(dto.getPrDocKey())) {
						List<Integer> serialNumList = keyLineMap
								.get(dto.getPrDocKey());
						if (!serialNumList.contains(dto.getItemSerialNum())) {
							String errorCodes = dto.getErrorCodes();
							errorCodes = errorCodes == null ? "ER0050"
									: errorCodes + ",ER0050";
							dto.setIsError(true);
							dto.setErrorCodes(errorCodes);
							errorKeys.add(key);
							dto.setErrorDesc(
									"ER0050-Record is not found in DigiGST Processed PR data, please re-check");
						} else {
							for (InwardTransDocLineItem line : allLineItems) {
								if (line.getLineNo().toString()
										.equalsIgnoreCase(String.valueOf(
												dto.getItemSerialNum()))) {
									if (line.getTaxperiod() != null) {

										if (GenUtil.getDerivedTaxPeriod(
												line.getTaxperiod()) > GenUtil
														.getDerivedTaxPeriod(dto
																.getRevisedTaxPeriod())) {
											String errorCodes = dto.getErrorCodes();
											errorCodes = errorCodes == null ? "ER0052"
													: errorCodes + ",ER0052";
											dto.setIsError(true);
											dto.setErrorCodes(errorCodes);
											errorKeys.add(key);
											dto.setErrorDesc(
													"Return Period mentioned in Exceptional Tagging file is prior than Return Period as per Processed PR");

											taxPeriodCheck.add(true);
										}
									}
								}
							}
							dto.setIsError(false);
						}
					} else {
						String errorCodes = dto.getErrorCodes();
						errorCodes = errorCodes == null ? "ER0050"
								: errorCodes + ",ER0050";
						dto.setIsError(true);
						errorKeys.add(key);
						dto.setErrorCodes(errorCodes);
						dto.setErrorDesc(
								"ER0050-Record is not found in DigiGST Processed PR data, please re-check");
					}
				}
				
			});
			if (!taxPeriodCheck.isEmpty()) {
				documentMap.entrySet().forEach(e1 -> {
					String k1 = e1.getKey();
					List<CommonCreditReversalProcessedDto> list1 = documentMap
							.get(k1);
					for (CommonCreditReversalProcessedDto dto : list1) {
						dto.setIsError(true);
					}
				});
			}
			if (!errorKeys.isEmpty()) {
				documentMap.entrySet().forEach(entry -> {
					String key = entry.getKey();
					List<CommonCreditReversalProcessedDto> list = documentMap
							.get(key);
					for (CommonCreditReversalProcessedDto dto : list) {
						dto.setIsError(true);
					}
				});
			}
		}
	
	}

	private String getWithValue(Object obj) {
		return obj != null && !obj.toString().trim().isEmpty()
				? String.valueOf(obj.toString().trim()) : null;
	}

	private Pair<String, String> populateErrorCodesAndDescription(
			List<ProcessingResult> processingResults) {
		List<String> descList = new ArrayList<>();
		List<String> errorCodeList = new ArrayList<>();
		for (ProcessingResult error : processingResults) {
			errorCodeList.add(error.getCode());
			String errorCodeDesc = error.getCode() + "-"
					+ ERROR_CODE_MAP.get(error.getCode());
			descList.add(errorCodeDesc);
		}
		String errorCode = errorCodeList.stream()
				.collect(Collectors.joining(","));
		String errorDesc = descList.stream().collect(Collectors.joining(","));
		return new Pair<String, String>(errorCode, errorDesc);
	}

	private void softDelete(List<String> docKeys) {

		if (docKeys.isEmpty())
			return;

		int softDeletedCount = 0;

		List<List<String>> chunks = Lists.partition(docKeys, 2000);
		for (List<String> chunk : chunks) {
			int rowsEffected = 0;

			rowsEffected = commonCreditReversalPsdRepository
					.updateIsDeleteFlag(chunk);

			softDeletedCount = softDeletedCount + rowsEffected;
			String msg = String.format(
					" %d rows affected in TBL_EWB_FU_HEADER table",
					softDeletedCount);
			LOGGER.debug(msg);

		}
	}

	private String derivePRKey(LocalDate date, String cgstin, String sgstin,
			String docType, String documentNumber) {
		String finYear = GenUtil.getFinYear(date);

		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
				.add(sgstin).add(docType).add(documentNumber).toString();
	}

	private String deriveCheckDuplicateKey(LocalDate date, String cgstin,
			String sgstin, String docType, String documentNumber,
			String itemSerialNumber) {
		String finYear = GenUtil.getFinYear(date);

		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
				.add(sgstin).add(docType).add(documentNumber)
				.add(itemSerialNumber).toString();
	}

	public Map<String, List<CommonCreditReversalProcessedDto>> convertToDto(
			List<Object[]> objs, String userName, String key,
			Gstr1FileStatusEntity fileStatus) {
		Map<String, List<CommonCreditReversalProcessedDto>> map = new HashMap<>();
		for (Object[] obj : objs) {
			CommonCreditReversalProcessedDto document = new CommonCreditReversalProcessedDto();
			String custGstin = getWithValue(obj[0]);
			String documentType = getWithValue(obj[1]);
			String documentNumber = getWithValue(obj[2]);
			String documentDate = getWithValue(obj[3]);
			String supplierGstin = getWithValue(obj[4]);
			String itemSerialNumber = getWithValue(obj[5]);
			String commonSupplierIndicator = getWithValue(obj[6]);
			String revisedTaxPeriod = getWithValue(obj[7]);
			document.setIsDelete(false);
			document.setCustGstin(custGstin);
			document.setDocType(documentType);
			document.setDocNum(documentNumber);
			document.setDocDate(DateUtil.parseObjToDate(documentDate));
			document.setSupplierGstin(supplierGstin);
			document.setItemSerialNum(Integer.parseInt(itemSerialNumber));
			document.setCommonSupplierIndicator(commonSupplierIndicator);
			document.setRevisedTaxPeriod(revisedTaxPeriod);
			document.setCreatedBy(userName);
			document.setCreatedOn(LocalDateTime.now());
			document.setDataOriginInTypeCode("E");
			document.setCommonCreditDocKey(key);
			document.setFileId(fileStatus.getId());
			String prDocKey = derivePRKey(document.getDocDate(), custGstin,
					supplierGstin, documentType, documentNumber);
			String checkDuplicateKey = deriveCheckDuplicateKey(
					document.getDocDate(), custGstin, supplierGstin,
					documentType, documentNumber, itemSerialNumber);
			document.setPrDocKey(prDocKey);
			document.setDuplicateKeyCheck(checkDuplicateKey);
			document.setUpdatedBy(userName);
			document.setUpdatedOn(LocalDateTime.now());
			map.computeIfAbsent(checkDuplicateKey, k -> new ArrayList<>())
					.add(document);
		}
		return map;
	}

	public List<CommonCreditReversalPsdEntity> convertToProcessedRecords(
			List<CommonCreditReversalProcessedDto> dtoList) {
		List<CommonCreditReversalPsdEntity> psdEntities = new ArrayList<>();
		for (CommonCreditReversalProcessedDto dto : dtoList) {
			CommonCreditReversalPsdEntity document = new CommonCreditReversalPsdEntity();
			document.setIsDelete(dto.getIsDelete());
			document.setCustGstin(dto.getCustGstin());
			document.setDocType(dto.getDocType());
			document.setDocNum(dto.getDocNum());
			document.setDocDate(dto.getDocDate());
			document.setSupplierGstin(dto.getSupplierGstin());
			document.setItemSerialNum(dto.getItemSerialNum());
			document.setCommonSupplierIndicator(
					dto.getCommonSupplierIndicator());
			document.setRevisedTaxPeriod(dto.getRevisedTaxPeriod());
			document.setCreatedBy(dto.getCreatedBy());
			document.setCreatedOn(LocalDateTime.now());
			document.setDataOriginInTypeCode("E");
			document.setCommonCreditDocKey(dto.getCommonCreditDocKey());
			document.setFileId(dto.getFileId());
			document.setPrDocKey(dto.getPrDocKey());
			document.setUpdatedBy(dto.getUpdatedBy());
			document.setUpdatedOn(LocalDateTime.now());
			document.setIsDelete(false);
			psdEntities.add(document);
		}
		return psdEntities;
	}

	public List<CommonCreditReversalErr> convertToErrorRecords(
			List<CommonCreditReversalProcessedDto> dtoList) {
		List<CommonCreditReversalErr> errorEntities = new ArrayList<>();
		for (CommonCreditReversalProcessedDto dto : dtoList) {
			CommonCreditReversalErr err = new CommonCreditReversalErr();
			err.setIsDelete(dto.getIsDelete());
			err.setCustGstin(dto.getCustGstin());
			err.setDocType(dto.getDocType());
			err.setDocNum(dto.getDocNum());
			err.setDocDate(String.valueOf(dto.getDocDate()));
			err.setSupplierGstin(dto.getSupplierGstin());
			err.setItemSerialNum(String.valueOf(dto.getItemSerialNum()));
			err.setCommonSupplierIndicator(dto.getCommonSupplierIndicator());
			err.setRevisedTaxPeriod(dto.getRevisedTaxPeriod());
			err.setCreatedBy(dto.getCreatedBy());
			err.setCreatedOn(LocalDateTime.now());
			err.setDataOriginInTypeCode("E");
			err.setCommonCreditDocKey(dto.getCommonCreditDocKey());
			err.setFileId(String.valueOf(dto.getFileId()));
			err.setPrDocKey(dto.getPrDocKey());
			err.setUpdatedBy(dto.getUpdatedBy());
			err.setUpdatedOn(LocalDateTime.now());
			err.setIsError(dto.getIsError());
			err.setErrorCodes(dto.getErrorCodes());
			err.setErrorDesc(dto.getErrorDesc());

			errorEntities.add(err);
		}
		return errorEntities;
	}
}
