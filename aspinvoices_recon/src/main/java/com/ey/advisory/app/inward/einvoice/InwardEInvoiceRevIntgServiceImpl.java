package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEInvoiceERPRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEInvoiceRevIntgMetaRepository;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("InwardEInvoiceRevIntgServiceImpl")
public class InwardEInvoiceRevIntgServiceImpl
		implements InwardEInvoiceRevIntgService {

	@Autowired
	private AnxErpBatchHandler erpBatchHandler;

	@Autowired
	private AnxErpBatchRepository batchRepo;

	@Autowired
	private DestinationConnectivity connectivity;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	private AnxErpBatchHandler anxErpBatchHandler;

	@Autowired
	private InwardEInvoiceRevIntgMetaRepository meta2aprRepo;

	@Autowired
	private InwardEInvoiceERPRequestRepository erpReqRepo;

	static String date = null;

	public static List<String> distinctPan = null;

	private static final String P_BATCHID = "P_BATCHID";
	private static final String FAILED = "FAILED";
	public static final String CONF_KEY = "irn.rev.intg.chunk.size";// "gstr2a.rev.intg.chunk.size"

	public static final DateTimeFormatter dtf = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	@Override
	public Integer getInwardEInvoiceErpPush(InwardEInvoiceRevIntgReqDto dto) {

		Integer respCode = 0;
		Integer chunkSize = null;

		try {

			InwardEInvoiceERPRequestEntity entity = erpReqRepo
					.findByBatchId(dto.getBatchId());

			Map<String, Config> configMap = configManager
					.getConfigs("INWARDEINVOICEREVINTG", CONF_KEY, "DEFAULT");
			chunkSize = configMap.get(CONF_KEY) == null ? 10000
					: Integer.valueOf(configMap.get(CONF_KEY).getValue());

			try {

				int noOfChunk = getHeaderChunk(entity.getBatchId(), chunkSize);

				if (noOfChunk == 0) {

					entity.setIsErpPush(false);
					entity.setStatus("NO_DATA");
					entity.setUpdatedOn(LocalDateTime.now());
					erpReqRepo.save(entity);
					return 0;
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Total Header-item no of chunks for revIntg {} "
									+ "and batchId {}",
							noOfChunk, entity.getBatchId());
				}

				int nestedChunkNo = getPreceedingChunk(entity.getBatchId(),
						chunkSize);

				respCode = pushData(entity, noOfChunk, dto, nestedChunkNo);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Recieved respCode for reverse push  {}",
							respCode);
				}

				if (respCode == HttpURLConnection.HTTP_OK) {

					entity.setIsErpPush(true);
					entity.setErpPushDate(LocalDateTime.now());
					entity.setStatus("SUCCESS");
					entity.setUpdatedOn(LocalDateTime.now());
					erpReqRepo.save(entity);

				} else {
					entity.setIsErpPush(false);
					entity.setErpPushDate(LocalDateTime.now());
					entity.setStatus(FAILED);
					entity.setUpdatedOn(LocalDateTime.now());
					entity.setErrorDescription(String
							.format("ERP Push Error with code %d", respCode));
					erpReqRepo.save(entity);
				}

			} catch (Exception ex) {

				String msg = String.format(
						" Inward EInvoice ERP PUSH failed for batchId %d",
						entity.getBatchId());
				LOGGER.error(msg, ex);
				if (entity != null) {
					entity.setIsErpPush(false);
					entity.setErpPushDate(LocalDateTime.now());
					entity.setStatus(FAILED);
					entity.setUpdatedOn(LocalDateTime.now());
					entity.setErrorDescription(ex.getMessage());
					erpReqRepo.save(entity);

				}

				throw new AppException(msg, ex);

			}

			return respCode;
		} catch (Exception e) {
			String msg = String.format(
					" Inward EInvoice ERP PUSH failed for batchId %d",
					dto.getBatchId());
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage(), e);

		}
	}

	private int getHeaderChunk(Long batchId, int chunkSize) {
		int noOfChunk = 0;
		String msg = null;
		try {

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_REVERSE_FEED_INSERT_CHUNK_IRN_HDR_ITM_DETAIL");

			storedProc.registerStoredProcedureParameter(P_BATCHID, Long.class,
					ParameterMode.IN);

			storedProc.setParameter(P_BATCHID, batchId);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPLIT_VAL",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_CHUNK_SPLIT_VAL", chunkSize);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Executing HeaderChunk proc "
						+ "USP_REVERSE_FEED_INSERT_CHUNK_IRN_HDR_ITM_DETAIL: "
						+ "'%d'", batchId);
				LOGGER.debug(msg);
			}

			Integer chunksize = (Integer) storedProc.getSingleResult();

			noOfChunk = chunksize;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("HeaderChunk proc Executed"
						+ " USP_REVERSE_FEED_INSERT_CHUNK_IRN_HDR_ITM_DETAIL:"
						+ " batchId '%d', " + "noOfChunk %d ", batchId,
						noOfChunk);
				LOGGER.debug(msg);
			}

		} catch (Exception ex) {

			msg = String.format(
					"Error while executing HeaderChunk proc batchId %d",
					batchId);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return noOfChunk;
	}

	private int getPreceedingChunk(Long batchId, int chunkSize) {
		int noOfChunk = 0;
		String msg = null;
		try {

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_REVERSE_FEED_INSERT_CHUNK_IRN_NESTED_DETAIL");

			storedProc.registerStoredProcedureParameter(P_BATCHID, Long.class,
					ParameterMode.IN);

			storedProc.setParameter(P_BATCHID, batchId);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPLIT_VAL",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_CHUNK_SPLIT_VAL", chunkSize);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Executing Nested chunking proc "
						+ "USP_REVERSE_FEED_INSERT_CHUNK_IRN_NESTED_DETAIL: "
						+ "'%d'", batchId);
				LOGGER.debug(msg);
			}

			Integer chunksize = (Integer) storedProc.getSingleResult();

			noOfChunk = chunksize;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Nested Chunking proc Executed"
						+ " USP_REVERSE_FEED_INSERT_CHUNK_IRN_NESTED_DETAIL:"
						+ " batchId '%d', noOfChunk %d ", batchId, noOfChunk);
				LOGGER.debug(msg);
			}

		} catch (Exception ex) {

			msg = String.format(
					"Error while executing nested chunk proc batchId %d",
					batchId);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return noOfChunk;
	}

	private Integer pushData(InwardEInvoiceERPRequestEntity e, int noOfChunk,
			InwardEInvoiceRevIntgReqDto dto, int nestedChunkNo) {

		Integer respCode = 0;
		InwardEInvoiceRevIntgMetaEntity metaEntity = null;
		AnxErpBatchEntity batchEntity = null;

		String destinationName = dto.getDestinationName();
		Long erpId = dto.getErpId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reverse push started for DestName {} and erpId  {}",
					destinationName, erpId);
		}

		try {
			// nested Map
			Map<String, List<Preceeding>> nestedMap = new HashMap<>();

			int nestedCounter = 0;

			while (nestedCounter < nestedChunkNo) {

				nestedCounter++;

				List<Preceeding> nestedList = getNestedDataFromProc(e,
						nestedChunkNo, nestedCounter);

				for (Preceeding preceeding : nestedList) {
					nestedMap.computeIfAbsent(preceeding.getIRN(),
							b -> new ArrayList<>()).add(preceeding);
				}
			}

			int counter = 0;
			while (counter < noOfChunk) {

				/*
				 * Map<String, Pair<Header, List<ItemDetails>>> headerItemMap =
				 * new HashMap<>();
				 */
				counter++;

				StoredProcedureQuery dispProc = entityManager
						.createStoredProcedureQuery(
								"USP_REVERSE_FEED_DISPLAY_CHUNK_IRN_HDR_ITM_DETAIL");

				dispProc.registerStoredProcedureParameter(P_BATCHID, Long.class,
						ParameterMode.IN);

				dispProc.setParameter(P_BATCHID, e.getBatchId());

				dispProc.registerStoredProcedureParameter("P_CHUNK_NUM",
						Integer.class, ParameterMode.IN);

				dispProc.setParameter("P_CHUNK_NUM", counter);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Data proc about to Execute"
							+ " USP_REVERSE_FEED_DISPLAY_CHUNK_IRN_HDR_ITM_DETAIL: batchId '%d', "
							+ "noOfChunk %d ", e.getBatchId(), counter);
					LOGGER.debug(msg);
				}

				@SuppressWarnings("unchecked")
				List<Object[]> headerRecords = dispProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Data proc Executed"
							+ " USP_REVERSE_FEED_DISPLAY_CHUNK_IRN_HDR_ITM_DETAIL: batchId '%d', "
							+ "noOfChunk %d record size %d", e.getBatchId(),
							counter, headerRecords.size());
					LOGGER.debug(msg);
				}

				if (headerRecords != null && !headerRecords.isEmpty()) {

					List<HeaderItemDto> headerItemDtoList = headerRecords
							.stream().map(o -> convertToHeaderItemDto(o))
							.collect(Collectors.toCollection(ArrayList::new));

					Map<String, List<HeaderItemDto>> headerItemListMap = headerItemDtoList
							.stream()
							.collect(Collectors.groupingBy(obj -> obj.getIRN()
									+ "_" + obj.getEInvoiceStatus()));

					VendorEInvoiceDTO erpPushDto = new VendorEInvoiceDTO();
					// VendorEInvoiceItemDto erpPushItemDto = new
					// VendorEInvoiceItemDto();
					for (String key : headerItemListMap.keySet()) {

						InwardEInvoiceErpPushDto ErpHeaderDto = new InwardEInvoiceErpPushDto();

						InwardEinvoiceRevIntItemDto item = new InwardEinvoiceRevIntItemDto();

						InwardEinvoiceRevIntPrecedingDocDto nested = new InwardEinvoiceRevIntPrecedingDocDto();

						InwardEinvoiceRevIntAttributeDocDto attributeObj = new InwardEinvoiceRevIntAttributeDocDto();

						InwardEinvoiceRevIntContractDocDto contractObj = new InwardEinvoiceRevIntContractDocDto();

						InwardEinvoiceRevIntAdditionalDocDto additionalObj = new InwardEinvoiceRevIntAdditionalDocDto();

						List<HeaderItemDto> list = headerItemListMap.get(key);

						boolean isHeaderCreated = false;

						for (HeaderItemDto hIDto : list) {

							if (!isHeaderCreated) {
								Header headerDto = convertToHeader(hIDto);
								isHeaderCreated = true;
								ErpHeaderDto.setHeader(headerDto);
							}
							ItemDetails itemDto = convertToItem(hIDto);

							item.addItem(itemDto);

						}
						// key is combination need to get IRN only

						String[] irnAndSatatusArry = key.split("_");
						String irn = irnAndSatatusArry[0];

						List<Preceeding> preceedingMapList = nestedMap.get(irn);

						List<Contract> contractList = new ArrayList<>();
						List<Additional> additionalList = new ArrayList<>();
						List<Attribute> attrbiuteList = new ArrayList<>();
						List<PreceedingXmlDto> preceedingList = new ArrayList<>();

						if (preceedingMapList != null) {

							for (Preceeding preceedingObj : preceedingMapList) {

								if (preceedingObj.getOtherReference() != null
										|| preceedingObj
												.getPrecedingInvoiceDt() != null
										|| preceedingObj
												.getPrecedingInvoiceNo() != null) {

									PreceedingXmlDto preceeding = new PreceedingXmlDto();
									preceeding.setSupplierGSTIN(
											preceedingObj.getSupplierGSTIN());
									preceeding.setDocumentDate(
											preceedingObj.getDocumentDate());
									preceeding.setDocumentNumber(
											preceedingObj.getDocumentNumber());
									preceeding.setDocumentType(
											preceedingObj.getDocumentType());
									preceeding.setIRN(preceedingObj.getIRN());
									preceeding.setOtherReference(
											preceedingObj.getOtherReference());
									preceeding
											.setPrecedingInvoiceDt(preceedingObj
													.getPrecedingInvoiceDt());
									preceeding
											.setPrecedingInvoiceNo(preceedingObj
													.getPrecedingInvoiceNo());

									preceedingList.add(preceeding);
								}

							}

							for (Preceeding preceedingObj : preceedingMapList) {

								if (preceedingObj.getAttributeName() != null
										|| preceedingObj
												.getAttributeValue() != null) {
									Attribute attrbiute = new Attribute();

									attrbiute.setSupplierGSTIN(
											preceedingObj.getSupplierGSTIN());
									attrbiute.setDocumentDate(
											preceedingObj.getDocumentDate());
									attrbiute.setDocumentNumber(
											preceedingObj.getDocumentNumber());
									attrbiute.setDocumentType(
											preceedingObj.getDocumentType());
									attrbiute.setIRN(preceedingObj.getIRN());
									attrbiute.setAttributeName(
											preceedingObj.getAttributeName());
									attrbiute.setAttributeValue(
											preceedingObj.getAttributeValue());
									attrbiute.setLineNumber(preceedingObj.getLineNumber());
									attrbiuteList.add(attrbiute);

								}

							}

							for (Preceeding preceedingObj : preceedingMapList) {

								if (preceedingObj
										.getSupportingDocument() != null
										|| preceedingObj
												.getAdditionalInformation() != null
										|| preceedingObj
												.getSupportingDocURL() != null) {

									Additional additional = new Additional();
									additional.setSupplierGSTIN(
											preceedingObj.getSupplierGSTIN());
									additional.setDocumentDate(
											preceedingObj.getDocumentDate());
									additional.setDocumentNumber(
											preceedingObj.getDocumentNumber());
									additional.setDocumentType(
											preceedingObj.getDocumentType());
									additional.setIRN(preceedingObj.getIRN());
									additional.setAdditionalInformation(
											preceedingObj
													.getAdditionalInformation());
									additional
											.setSupportingDocument(preceedingObj
													.getSupportingDocument());
									additional.setSupportingDocURL(preceedingObj
											.getSupportingDocURL());

									additionalList.add(additional);
								}

							}

							for (Preceeding preceedingObj : preceedingMapList) {
								if (preceedingObj.getContractReference() != null
										|| preceedingObj
												.getCustomerPOReferenceDate() != null
										|| preceedingObj
												.getCustomerPOReferenceNumber() != null
										|| preceedingObj
												.getExternalReference() != null
										|| preceedingObj
												.getProjectReference() != null
										|| preceedingObj
												.getReceiptAdviceDate() != null
										|| preceedingObj
												.getReceiptAdviceReference() != null
										|| preceedingObj
												.getTenderReference() != null) {

									Contract contract = new Contract();
									contract.setSupplierGSTIN(
											preceedingObj.getSupplierGSTIN());
									contract.setDocumentDate(
											preceedingObj.getDocumentDate());
									contract.setDocumentNumber(
											preceedingObj.getDocumentNumber());
									contract.setDocumentType(
											preceedingObj.getDocumentType());
									contract.setIRN(preceedingObj.getIRN());

									contract.setContractReference(preceedingObj
											.getContractReference());
									contract.setCustomerPOReferenceDate(
											preceedingObj
													.getCustomerPOReferenceDate());
									contract.setCustomerPOReferenceNumber(
											preceedingObj
													.getCustomerPOReferenceNumber());
									contract.setExternalReference(preceedingObj
											.getExternalReference());
									contract.setProjectReference(preceedingObj
											.getProjectReference());
									contract.setReceiptAdviceDate(preceedingObj
											.getReceiptAdviceDate());
									contract.setReceiptAdviceReference(
											preceedingObj
													.getReceiptAdviceReference());
									contract.setTenderReference(
											preceedingObj.getTenderReference());

									contractList.add(contract);

								}
							}
						}

						if (preceedingMapList != null) {

							nested.addAllItem(preceedingList);
							attributeObj.addAllItem(attrbiuteList);
							contractObj.addAllItem(contractList);
							additionalObj.addAllItem(additionalList);
						}

						ErpHeaderDto.setItem(item);
						ErpHeaderDto.setPreceeding(nested);
						ErpHeaderDto.setAdditional(additionalObj);
						ErpHeaderDto.setAttribute(attributeObj);
						ErpHeaderDto.setContract(contractObj);

						erpPushDto.addItem(ErpHeaderDto);

					}

					batchEntity = erpBatchHandler.createErpBatch(
							dto.getGroupCode(), null, dto.getGstin(),
							dto.getDestinationName(), dto.getScenarioId(),
							Long.valueOf(headerRecords.size()), null,
							ERPConstants.EVENT_BASED_JOB, dto.getErpId(), null,
							APIConstants.SYSTEM.toUpperCase());

					// ToDO
					// batchRepo.save(batchEntity);

					String subGstin = null;
					if (e.getGstin().length() > 14) {
						subGstin = e.getGstin().substring(0, 15);
					} else {
						subGstin = e.getGstin();
					}

					metaEntity = craeteMetaRecord(e.getBatchId(),
							e.getBatchId(), subGstin, e.getTotalRecord(),
							counter, Long.valueOf(headerItemListMap.size()));

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Reverse push started for SAP {} "
										+ "and erpId {} and itemSize {}",
								destinationName, erpId,
								headerItemListMap.size());
					}

					respCode = connectivity.pushToErp(erpPushDto,
							"VendorEInvoiceDTO", batchEntity);

					if (respCode != HttpURLConnection.HTTP_OK) {
						metaEntity.setStatus("Failed");
						metaEntity.setUpdatedDate(LocalDateTime.now());
						meta2aprRepo.save(metaEntity);
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Received respCode {} for requestId {}",
								respCode, e.getRequestId());
					}
					if (respCode != HttpURLConnection.HTTP_OK) {
						String msg = String.format(
								"Inward EInvoice reverse intg has failed with "
										+ "code %s, ",
								respCode);
						throw new AppException(msg);
					}
					if (batchEntity != null) {
						batchEntity.setHttpCode(respCode);
						batchEntity.setHttpStatus("OK");
						batchEntity.setSuccess(true);
						batchRepo.save(batchEntity);
					}
				}
			}

		} catch (Exception ex) {
			anxErpBatchHandler.updateErpBatch(batchEntity, respCode, null,
					false, null, ex.getMessage());

			String msg = String.format(
					"InwardEInvoiceRevIntgServiceImpl :"
							+ "Error while executing Disp proc batchId %d",
					e.getRequestId());
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return respCode;

	}

	/**
	 * @param e
	 * @param nestedChunkNo
	 * @param localTimeString
	 * @param localDateString
	 * @param counter
	 * @return
	 */
	private List<Preceeding> getNestedDataFromProc(
			InwardEInvoiceERPRequestEntity e, int nestedChunkNo, int counter) {
		// Nested proc call
		List<Preceeding> nestedList = new ArrayList<>();

		if (nestedChunkNo != 0) {
			StoredProcedureQuery nestedDispProc = entityManager
					.createStoredProcedureQuery(
							"USP_REVERSE_FEED_DISPLAY_CHUNK_IRN_NESTED_DETAIL");

			nestedDispProc.registerStoredProcedureParameter(P_BATCHID,
					Long.class, ParameterMode.IN);

			nestedDispProc.setParameter(P_BATCHID, e.getBatchId());

			nestedDispProc.registerStoredProcedureParameter("P_CHUNK_NUM",
					Integer.class, ParameterMode.IN);

			nestedDispProc.setParameter("P_CHUNK_NUM", counter);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Data proc about to Execute"
						+ " USP_REVERSE_FEED_DISPLAY_CHUNK_IRN_NESTED_DETAIL"
						+ ": batchId '%d', " + "noOfChunk %d ", e.getBatchId(),
						counter);
				LOGGER.debug(msg);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> nestedRecords = nestedDispProc.getResultList();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Data proc Executed"
						+ " USP_REVERSE_FEED_DISPLAY_CHUNK_IRN_NESTED_DETAIL"
						+ ": batchId '%d', " + "noOfChunk %d nestedRecords %d",
						e.getBatchId(), counter, nestedRecords.size());
				LOGGER.debug(msg);
			}

			// Item conversion
			nestedList = nestedRecords.stream().map(o -> convertToNestedItem(o))
					.collect(Collectors.toCollection(ArrayList::new));
		}
		return nestedList;
	}

	private HeaderItemDto convertToHeaderItemDto(Object[] arr) {

		HeaderItemDto dto = new HeaderItemDto();

		dto.setDocumentType((arr[1] != null) ? arr[1].toString() : null);
		dto.setSupplierGSTIN((arr[2] != null) ? arr[2].toString() : null);
		dto.setDocumentDate((arr[4] != null) ? arr[4].toString() : null);
		dto.setDocumentNumber((arr[3] != null) ? arr[3].toString() : null);
		dto.setIRN((arr[5] != null) ? arr[5].toString() : null);
		dto.setIRNDT((arr[6] != null) ? arr[6].toString() : null);
		dto.setEInvoiceStatus((arr[7] != null) ? arr[7].toString() : null);
		dto.setIRNCANDT((arr[8] != null) ? arr[8].toString() : null);
		dto.setACKNO((arr[9] != null) ? arr[9].toString() : null);
		dto.setACKDT((arr[10] != null) ? arr[10].toString() : null);
		dto.setEWBNO((arr[11] != null) ? arr[11].toString() : null);
		dto.setEWBDT((arr[12] != null) ? arr[12].toString() : null);
		dto.setEWBValidTill((arr[13] != null) ? arr[13].toString() : null);

		dto.setSupplyType((arr[14] != null) ? arr[14].toString() : null);
		dto.setTaxScheme((arr[15] != null) ? arr[15].toString() : null);
		dto.setCancellationReason(
				(arr[16] != null) ? arr[16].toString() : null);
		dto.setCancellationRemarks(
				(arr[17] != null) ? arr[17].toString() : null);
		dto.setRevChargeFlag((arr[18] != null) ? arr[18].toString() : null);
		dto.setSupplierTradeName((arr[19] != null) ? arr[19].toString() : null);
		dto.setSupplierLegalName((arr[20] != null) ? arr[20].toString() : null);
		dto.setSupplierAddress1((arr[21] != null) ? arr[21].toString() : null);
		dto.setSupplierAddress2((arr[22] != null) ? arr[22].toString() : null);
		dto.setSupplierLocation((arr[23] != null) ? arr[23].toString() : null);
		dto.setSupplierPincode((arr[24] != null) ? arr[24].toString() : null);
		dto.setSupplierStateCode((arr[25] != null) ? arr[25].toString() : null);
		dto.setSupplierPhone((arr[26] != null) ? arr[26].toString() : null);
		dto.setSupplierEmail((arr[27] != null) ? arr[27].toString() : null);
		dto.setCustomerGSTIN((arr[28] != null) ? arr[28].toString() : null);
		dto.setCustomerTradeName((arr[29] != null) ? arr[29].toString() : null);
		dto.setCustomerLegalName((arr[30] != null) ? arr[30].toString() : null);
		dto.setCustomerAddress1((arr[31] != null) ? arr[31].toString() : null);
		dto.setCustomerAddress2((arr[32] != null) ? arr[32].toString() : null);
		dto.setCustomerLocation((arr[33] != null) ? arr[33].toString() : null);
		dto.setCustomerPincode((arr[34] != null) ? arr[34].toString() : null);
		dto.setCustomerStateCode((arr[35] != null) ? arr[35].toString() : null);
		dto.setBillingPOS((arr[36] != null) ? arr[36].toString() : null);
		dto.setCustomerPhone((arr[37] != null) ? arr[37].toString() : null);
		dto.setCustomerEmail((arr[38] != null) ? arr[38].toString() : null);

		dto.setDispatcherTradeName(
				(arr[39] != null) ? arr[39].toString() : null);
		dto.setDispatcherAddress1(
				(arr[40] != null) ? arr[40].toString() : null);
		dto.setDispatcherAddress2(
				(arr[41] != null) ? arr[41].toString() : null);
		dto.setDispatcherLocation(
				(arr[42] != null) ? arr[42].toString() : null);
		dto.setDispatcherPincode((arr[43] != null) ? arr[43].toString() : null);
		dto.setDispatcherStateCode(
				(arr[44] != null) ? arr[44].toString() : null);
		dto.setShipToGSTIN((arr[45] != null) ? arr[45].toString() : null);
		dto.setShipToTradeName((arr[46] != null) ? arr[46].toString() : null);
		dto.setShipToLegalName((arr[47] != null) ? arr[47].toString() : null);
		dto.setShipToAddress1((arr[48] != null) ? arr[48].toString() : null);
		dto.setShipToAddress2((arr[49] != null) ? arr[49].toString() : null);
		dto.setShipToLocation((arr[50] != null) ? arr[50].toString() : null);
		dto.setShipToPincode((arr[51] != null) ? arr[51].toString() : null);
		dto.setShipToStateCode((arr[52] != null) ? arr[52].toString() : null);

		dto.setInvoiceOtherCharges(
				(arr[53] != null) ? new BigDecimal(arr[53].toString()) : null);
		dto.setInvoiceAssessableAmount(
				(arr[54] != null) ? new BigDecimal(arr[54].toString()) : null);
		dto.setInvoiceIGSTAmount(
				(arr[55] != null) ? new BigDecimal(arr[55].toString()) : null);
		dto.setInvoiceCGSTAmount(
				(arr[56] != null) ? new BigDecimal(arr[56].toString()) : null);
		dto.setInvoiceSGSTAmount(
				(arr[57] != null) ? new BigDecimal(arr[57].toString()) : null);
		dto.setInvoiceCessAdValoremAmount(
				(arr[58] != null) ? new BigDecimal(arr[58].toString()) : null);
		dto.setInvoiceCessSpecificAmount(
				(arr[59] != null) ? new BigDecimal(arr[59].toString()) : null);

		dto.setInvoiceStateCessAdValoremAmt(
				(arr[60] != null) ? new BigDecimal(arr[60].toString()) : null);
		dto.setInvoiceStateCessSpecificAmount(
				(arr[61] != null) ? new BigDecimal(arr[61].toString()) : null);
		dto.setInvoiceValue(
				(arr[62] != null) ? new BigDecimal(arr[62].toString()) : null);

		dto.setRoundOff(
				(arr[63] != null) ? new BigDecimal(arr[63].toString()) : null);
		dto.setCurrencyCode((arr[64] != null) ? arr[64].toString() : null);
		dto.setCountryCode((arr[65] != null) ? arr[65].toString() : null);
		dto.setInvoiceValueFC(
				(arr[66] != null) ? new BigDecimal(arr[66].toString()) : null);
		dto.setPortCode((arr[67] != null) ? arr[67].toString() : null);
		dto.setShippingBillNumber(
				(arr[68] != null) ? arr[68].toString() : null);
		dto.setShippingBillDate((arr[69] != null) ? arr[69].toString() : null);
		dto.setInvoiceRemarks((arr[70] != null) ? arr[70].toString() : null);
		dto.setInvoicePeriodStartDate(
				(arr[71] != null) ? arr[71].toString() : null);
		dto.setInvoicePeriodEndDate(
				(arr[72] != null) ? arr[72].toString() : null);
		dto.setPayeeName((arr[73] != null) ? arr[73].toString() : null);
		dto.setModeOfPayment((arr[74] != null) ? arr[74].toString() : null);

		dto.setBranchOrIFSCCode((arr[75] != null) ? arr[75].toString() : null);
		dto.setPaymentTerms((arr[76] != null) ? arr[76].toString() : null);
		dto.setPaymentInstruction(
				(arr[77] != null) ? arr[77].toString() : null);
		dto.setCreditTransfer((arr[78] != null) ? arr[78].toString() : null);
		dto.setDirectDebit((arr[79] != null) ? arr[79].toString() : null);
		dto.setCreditDays(
				(arr[80] != null) ? new Integer(arr[80].toString()) : null);
		dto.setAccountDetail((arr[81] != null) ? arr[81].toString() : null);
		dto.setEcomGSTIN((arr[82] != null) ? arr[82].toString() : null);
		dto.setTransporterId((arr[83] != null) ? arr[83].toString() : null);
		dto.setTransporterName((arr[84] != null) ? arr[84].toString() : null);
		dto.setTransportMode((arr[85] != null) ? arr[85].toString() : null);
		dto.setTransportDocNo((arr[86] != null) ? arr[86].toString() : null);
		dto.setTransportDocDate((arr[87] != null) ? arr[87].toString() : null);
		dto.setDistance(
				(arr[88] != null) ? new BigDecimal(arr[88].toString()) : null);

		dto.setVehicleNo((arr[89] != null) ? arr[89].toString() : null);
		dto.setVehicleType((arr[90] != null) ? arr[90].toString() : null);

		dto.setSec7IGSTFlag((arr[91] != null) ? arr[91].toString() : null);
		dto.setClaimRefundFlag((arr[92] != null) ? arr[92].toString() : null);
		dto.setInvoiceDiscount(
				(arr[93] != null) ? new BigDecimal(arr[93].toString()) : null);
		dto.setIrnGenerationPeriod(
				(arr[94] != null) ? arr[94].toString() : null);
		dto.setRivIntDate((arr[95] != null) ? arr[95].toString() : null);
		dto.setLineNumber((arr[96] != null) ? arr[96].toString() : null);
		dto.setProductSerialNumber(
				(arr[97] != null) ? arr[97].toString() : null);
		dto.setProductDescription(
				(arr[98] != null) ? arr[98].toString() : null);
		dto.setIsService((arr[99] != null) ? arr[99].toString() : null);
		dto.setHSN((arr[100] != null) ? arr[100].toString() : null);
		dto.setBarcode((arr[101] != null) ? arr[101].toString() : null);
		dto.setBatchName((arr[102] != null) ? arr[102].toString() : null);
		dto.setBatchExpiryDate((arr[103] != null) ? arr[103].toString() : null);
		dto.setWarrantyDate((arr[104] != null) ? arr[104].toString() : null);

		dto.setOrderLineReference(
				(arr[105] != null) ? arr[105].toString() : null);
		dto.setOriginCountry((arr[106] != null) ? arr[106].toString() : null);
		dto.setUQC((arr[107] != null) ? arr[107].toString() : null);
		dto.setQuantity((arr[108] != null) ? new BigDecimal(arr[108].toString())
				: null);
		dto.setFreeQuantity(
				(arr[109] != null) ? new BigDecimal(arr[109].toString())
						: null);

		dto.setUnitPrice(
				(arr[110] != null) ? new BigDecimal(arr[110].toString())
						: null);

		dto.setItemAmount(
				(arr[111] != null) ? new BigDecimal(arr[111].toString())
						: null);
		dto.setItemDiscount(
				(arr[112] != null) ? new BigDecimal(arr[112].toString())
						: null);
		dto.setPreTaxAmount(
				(arr[113] != null) ? new BigDecimal(arr[113].toString())
						: null);
		dto.setItemAssessableAmount(
				(arr[114] != null) ? new BigDecimal(arr[114].toString())
						: null);

		dto.setIgstRate((arr[115] != null) ? new BigDecimal(arr[115].toString())
				: null);
		dto.setIgstAmount(
				(arr[116] != null) ? new BigDecimal(arr[116].toString())
						: null);
		dto.setCgstRate((arr[117] != null) ? new BigDecimal(arr[117].toString())
				: null);
		dto.setCgstAmount(
				(arr[118] != null) ? new BigDecimal(arr[118].toString())
						: null);
		dto.setSgstRate((arr[119] != null) ? new BigDecimal(arr[119].toString())
				: null);
		dto.setSgstAmount(
				(arr[120] != null) ? new BigDecimal(arr[120].toString())
						: null);
		dto.setCessAdValoremRate(
				(arr[121] != null) ? new BigDecimal(arr[121].toString())
						: null);
		dto.setCessAdValoremAmount(
				(arr[122] != null) ? new BigDecimal(arr[122].toString())
						: null);

		dto.setCessSpecificAmount(
				(arr[123] != null) ? new BigDecimal(arr[123].toString())
						: null);

		dto.setStateCessAdValoremRate(
				(arr[124] != null) ? new BigDecimal(arr[124].toString())
						: null);
		dto.setStateCessAdValoremAmount(
				(arr[125] != null) ? new BigDecimal(arr[125].toString())
						: null);
		dto.setStateCessAmount(
				(arr[126] != null) ? new BigDecimal(arr[126].toString())
						: null);
		dto.setItemOtherCharges(
				(arr[127] != null) ? new BigDecimal(arr[127].toString())
						: null);
		dto.setTotalItemAmount(
				(arr[128] != null) ? new BigDecimal(arr[128].toString())
						: null);

		dto.setPaidAmount(
				(arr[129] != null) ? new BigDecimal(arr[129].toString())
						: null);

		dto.setBalanceAmount(
				(arr[130] != null) ? new BigDecimal(arr[130].toString())
						: null);

		dto.setExportDuty(
				(arr[131] != null) ? new BigDecimal(arr[131].toString())
						: null);

		dto.setPayloadId((arr[0] != null) ? arr[0].toString() : null);

		dto.setSignedQrCode(
				(arr[132] != null) ? String.valueOf(arr[132]) : null);

		LOGGER.debug("SingedQrcode{}", dto.getSignedQrCode());

		return dto;

	}

	private Preceeding convertToNestedItem(Object[] arr) {

		Preceeding nested = new Preceeding();

		nested.setAdditionalInformation(
				(arr[19] != null) ? arr[19].toString() : null);
		nested.setContractReference(
				(arr[12] != null) ? arr[12].toString() : null);
		nested.setCustomerPOReferenceDate(
				(arr[16] != null) ? arr[16].toString() : null);
		nested.setCustomerPOReferenceNumber(
				(arr[15] != null) ? arr[15].toString() : null);

		nested.setDocumentDate((arr[4] != null) ? arr[4].toString() : null);
		nested.setDocumentNumber((arr[3] != null) ? arr[3].toString() : null);
		nested.setDocumentType((arr[1] != null) ? arr[1].toString() : null);
		nested.setExternalReference(
				(arr[13] != null) ? arr[13].toString() : null);
		nested.setIRN((arr[5] != null) ? arr[5].toString() : null);
		nested.setOtherReference((arr[8] != null) ? arr[8].toString() : null);
		nested.setPrecedingInvoiceDt(
				(arr[7] != null) ? arr[7].toString() : null);
		nested.setPrecedingInvoiceNo(
				(arr[6] != null) ? arr[6].toString() : null);
		nested.setProjectReference(
				(arr[14] != null) ? arr[14].toString() : null);
		nested.setReceiptAdviceDate(
				(arr[10] != null) ? arr[10].toString() : null);
		nested.setReceiptAdviceReference(
				(arr[9] != null) ? arr[9].toString() : null);
		nested.setSupplierGSTIN((arr[2] != null) ? arr[2].toString() : null);
		nested.setSupportingDocument(
				(arr[18] != null) ? arr[18].toString() : null);
		nested.setSupportingDocURL(
				(arr[17] != null) ? arr[17].toString() : null);
		nested.setTenderReference(
				(arr[11] != null) ? arr[11].toString() : null);
		nested.setAttributeName((arr[20] != null) ? arr[20].toString() : null);
		nested.setAttributeValue((arr[21] != null) ? arr[21].toString() : null);
		nested.setLineNumber((arr[22] != null) ? arr[22].toString() : null);

		return nested;
	}

	private ItemDetails convertToItem(HeaderItemDto item) {

		ItemDetails obj = new ItemDetails();

		obj.setBalanceAmount(item.getBalanceAmount());
		obj.setBarcode(item.getBarcode());
		obj.setBatchExpiryDate(item.getBatchExpiryDate());
		obj.setBatchName(item.getBatchName());
		obj.setCessAdValoremAmount(item.getCessAdValoremAmount());
		obj.setCessAdValoremRate(item.getCessAdValoremRate());
		obj.setCessSpecificAmount(item.getCessSpecificAmount());
		obj.setCgstAmount(item.getCgstAmount());
		obj.setCgstRate(item.getCgstRate());
		obj.setDocumentDate(item.getDocumentDate());
		obj.setDocumentNumber(item.getDocumentNumber());
		obj.setDocumentType(item.getDocumentType());
		obj.setExportDuty(item.getExportDuty());
		obj.setFreeQuantity(item.getFreeQuantity());
		obj.setHSN(item.getHSN());
		obj.setIgstAmount(item.getIgstAmount());
		obj.setIgstRate(item.getIgstRate());
		obj.setIRN(item.getIRN());
		obj.setItemAmount(item.getItemAmount());
		obj.setItemAssessableAmount(item.getItemAssessableAmount());
		obj.setItemDiscount(item.getItemDiscount());
		obj.setItemOtherCharges(item.getItemOtherCharges());
		obj.setLineNumber(item.getLineNumber());
		obj.setOrderLineReference(item.getOrderLineReference());
		obj.setOriginCountry(item.getOriginCountry());
		obj.setPaidAmount(item.getPaidAmount());
		obj.setPreTaxAmount(item.getPreTaxAmount());
		obj.setProductDescription(item.getProductDescription());
		obj.setProductSerialNumber(item.getProductSerialNumber());
		obj.setQuantity(item.getQuantity());
		obj.setIsService(item.getIsService());
		obj.setSgstAmount(item.getSgstAmount());
		obj.setSgstRate(item.getSgstRate());
		obj.setStateCessAdValoremAmount(item.getStateCessAdValoremAmount());
		obj.setStateCessAdValoremRate(item.getStateCessAdValoremRate());
		obj.setStateCessAmount(item.getStateCessAmount());
		obj.setCessSpecificAmount(item.getCessSpecificAmount());
		obj.setSupplierGSTIN(item.getSupplierGSTIN());
		obj.setTotalItemAmount(item.getTotalItemAmount());
		obj.setUnitPrice(item.getUnitPrice());
		obj.setUQC(item.getUQC());
		obj.setWarrantyDate(item.getWarrantyDate());
		return obj;

	}

	private Header convertToHeader(HeaderItemDto dto) {
		Header headrDto = new Header();
		headrDto.setAccountDetail(dto.getAccountDetail());
		headrDto.setACKDT(dto.getACKDT());
		headrDto.setACKNO(dto.getACKNO());

		headrDto.setBillingPOS(dto.getBillingPOS());
		headrDto.setBranchOrIFSCCode(dto.getBranchOrIFSCCode());

		headrDto.setCancellationReason(dto.getCancellationReason());
		headrDto.setCancellationRemarks(dto.getCancellationRemarks());
		headrDto.setClaimRefundFlag(dto.getClaimRefundFlag());
		headrDto.setCountryCode(dto.getCountryCode());
		headrDto.setCreditDays(dto.getCreditDays());
		headrDto.setCreditTransfer(dto.getCreditTransfer());
		headrDto.setCurrencyCode(dto.getCurrencyCode());
		headrDto.setCustomerAddress1(dto.getCustomerAddress1());
		headrDto.setCustomerAddress2(dto.getCustomerAddress2());
		headrDto.setCustomerEmail(dto.getCustomerEmail());
		headrDto.setCustomerGSTIN(dto.getCustomerGSTIN());
		headrDto.setCustomerLegalName(dto.getCustomerLegalName());
		headrDto.setCustomerLocation(dto.getCustomerLocation());
		headrDto.setCustomerPhone(dto.getCustomerPhone());
		headrDto.setCustomerPincode(dto.getCustomerPincode());
		headrDto.setCustomerStateCode(dto.getCustomerStateCode());
		headrDto.setCustomerTradeName(dto.getCustomerTradeName());

		headrDto.setDirectDebit(dto.getDirectDebit());
		headrDto.setDispatcherAddress1(dto.getDispatcherAddress1());
		headrDto.setDispatcherAddress2(dto.getDispatcherAddress2());
		headrDto.setDispatcherLocation(dto.getDispatcherLocation());
		headrDto.setDispatcherPincode(dto.getDispatcherPincode());
		headrDto.setDispatcherStateCode(dto.getDispatcherStateCode());
		headrDto.setDispatcherTradeName(dto.getDispatcherTradeName());
		headrDto.setDistance(dto.getDistance());
		headrDto.setDocumentDate(dto.getDocumentDate());
		headrDto.setDocumentNumber(dto.getDocumentNumber());
		headrDto.setDocumentType(dto.getDocumentType());

		headrDto.setEcomGSTIN(dto.getEcomGSTIN());
		headrDto.setEInvoiceStatus(dto.getEInvoiceStatus());
		headrDto.setEWBDT(dto.getEWBDT());
		headrDto.setEWBNO(dto.getEWBNO());
		headrDto.setEWBValidTill(dto.getEWBValidTill());

		headrDto.setInvoiceAssessableAmount(dto.getInvoiceAssessableAmount());
		headrDto.setInvoiceCessAdValoremAmount(
				dto.getInvoiceCessAdValoremAmount());
		headrDto.setInvoiceCessSpecificAmount(
				dto.getInvoiceCessSpecificAmount());
		headrDto.setInvoiceCGSTAmount(dto.getInvoiceCGSTAmount());
		headrDto.setInvoiceDiscount(dto.getInvoiceDiscount());
		headrDto.setInvoiceIGSTAmount(dto.getInvoiceIGSTAmount());
		headrDto.setInvoiceOtherCharges(dto.getInvoiceOtherCharges());
		headrDto.setInvoicePeriodEndDate(dto.getInvoicePeriodEndDate());
		headrDto.setInvoicePeriodStartDate(dto.getInvoicePeriodStartDate());
		headrDto.setInvoiceRemarks(dto.getInvoiceRemarks());
		headrDto.setInvoiceSGSTAmount(dto.getInvoiceSGSTAmount());
		headrDto.setInvoiceStateCessAdValoremAmt(
				dto.getInvoiceCessAdValoremAmount());
		headrDto.setInvoiceStateCessSpecificAmount(
				dto.getInvoiceCessSpecificAmount());
		headrDto.setInvoiceValue(dto.getInvoiceValue());
		headrDto.setInvoiceValueFC(dto.getInvoiceValueFC());
		headrDto.setIRN(dto.getIRN());
		headrDto.setIRNCANDT(dto.getIRNCANDT());
		headrDto.setIRNDT(dto.getIRNDT());
		headrDto.setIrnGenerationPeriod(dto.getIrnGenerationPeriod());

		headrDto.setModeOfPayment(dto.getModeOfPayment());

		headrDto.setPayeeName(dto.getPayeeName());
		headrDto.setPayloadId(dto.getPayloadId());
		headrDto.setPaymentInstruction(dto.getPaymentInstruction());
		headrDto.setPaymentTerms(dto.getPaymentTerms());
		headrDto.setPortCode(dto.getPortCode());

		headrDto.setRevChargeFlag(dto.getRevChargeFlag());
		headrDto.setRivIntDate(dto.getRivIntDate());
		headrDto.setRoundOff(dto.getRoundOff());

		headrDto.setSec7IGSTFlag(dto.getSec7IGSTFlag());
		headrDto.setShippingBillDate(dto.getShippingBillDate());
		headrDto.setShippingBillNumber(dto.getShippingBillNumber());
		headrDto.setShipToAddress1(dto.getShipToAddress1());
		headrDto.setShipToAddress2(dto.getShipToAddress2());
		headrDto.setShipToGSTIN(dto.getShipToGSTIN());
		headrDto.setShipToLegalName(dto.getShipToLegalName());
		headrDto.setShipToLocation(dto.getShipToLocation());
		headrDto.setShipToPincode(dto.getShipToPincode());
		headrDto.setShipToStateCode(dto.getShipToStateCode());
		headrDto.setShipToTradeName(dto.getShipToTradeName());
		headrDto.setSupplierAddress1(dto.getShipToAddress1());
		headrDto.setSupplierAddress2(dto.getShipToAddress2());
		headrDto.setSupplierEmail(dto.getSupplierEmail());
		headrDto.setSupplierGSTIN(dto.getSupplierGSTIN());
		headrDto.setSupplierLegalName(dto.getSupplierLegalName());
		headrDto.setSupplierLocation(dto.getSupplierLocation());
		headrDto.setSupplierPhone(dto.getSupplierPhone());
		headrDto.setSupplierPincode(dto.getSupplierPincode());
		headrDto.setSupplierStateCode(dto.getSupplierStateCode());
		headrDto.setSupplierTradeName(dto.getSupplierTradeName());
		headrDto.setSupplyType(dto.getSupplyType());

		headrDto.setTaxScheme(dto.getTaxScheme());
		headrDto.setTransportDocDate(dto.getTransportDocDate());
		headrDto.setTransportDocNo(dto.getTransportDocNo());
		headrDto.setTransporterId(dto.getTransporterId());
		headrDto.setTransporterName(dto.getTransporterName());
		headrDto.setTransportMode(dto.getTransportMode());

		headrDto.setVehicleNo(dto.getVehicleNo());
		headrDto.setVehicleType(dto.getVehicleType());

		headrDto.setSec7IGSTFlag(dto.getSec7IGSTFlag());
		headrDto.setSignedQrCode(dto.getSignedQrCode());

		return headrDto;
	}

	private InwardEInvoiceRevIntgMetaEntity craeteMetaRecord(Long batchId,
			Long reportConfigId, String gstin, Long ttlCntrlRec,
			Integer headerChunkId, Long ttlChunkRec) {
		InwardEInvoiceRevIntgMetaEntity e = new InwardEInvoiceRevIntgMetaEntity();
		e.setBatchId(batchId);
		e.setGstin(gstin);
		e.setTotalRecord(ttlCntrlRec);
		e.setTotalChunkRecordSent(ttlChunkRec);
		e.setHeaderChunkId(headerChunkId);
		e.setStatus("INITIATED");
		e.setCreatedDate(LocalDateTime.now());
		meta2aprRepo.save(e);
		return e;
	}

}
