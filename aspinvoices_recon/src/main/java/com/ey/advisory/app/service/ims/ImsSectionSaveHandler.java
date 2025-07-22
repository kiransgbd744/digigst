/**
 * 
 */
package com.ey.advisory.app.service.ims;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsProcessedInvoiceRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsSaveJobQueueRepository;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.PayloadSizeExceededException;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ImsSectionSaveHandler")
public class ImsSectionSaveHandler {

	public SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private ImsProcessedInvoiceRepository psdRepo;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchRepo;

	@Autowired
	private ImsSaveJobQueueRepository queueRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final String CONF_KEY = "ims.section.chunk.size";
	private static final String CONF_CATEG = "IMS_SAVE";

	public void saveActiveInvoices(String gstin, String groupCode,
			String tableTyp, String action) {

		LOGGER.debug("Inside ImsSectionSaveHandler - gstin {} ", gstin);
		try {
			List<String> newTableType = new ArrayList<>();
			if (tableTyp.equalsIgnoreCase("CN")) {
				newTableType.add("CN");
				newTableType.add("CDN");
			} else if (tableTyp.equalsIgnoreCase("DN")) {
				newTableType.add("DN");
				newTableType.add("CDN");
			} else if (tableTyp.equalsIgnoreCase("CNA")) {
				newTableType.add("CNA");
				newTableType.add("CDNA");
			} else if (tableTyp.equalsIgnoreCase("DNA")) {
				newTableType.add("DNA");
				newTableType.add("CDNA");
			} else {
				newTableType.add(tableTyp);
			}

			List<ImsProcessedInvoiceEntity> activeRecords = psdRepo
					.findActiveRecipientGstin(gstin, newTableType);

			if (activeRecords.isEmpty()) {
				LOGGER.error("No active Data found to save to IMS for {} :",
						gstin);
				queueRepo.updateStatusBasedonGstin("No Data", gstin, tableTyp,
						"No active data found to be saved", action);
				return;
			}

			// Filter records where ActionResponse = "N"
			List<ImsProcessedInvoiceEntity> actionResponseToBeReset = activeRecords
					.stream()
					.filter(record -> "N".equals(record.getActionResponse()))
					.collect(Collectors.toList());

			// Filter records where ActionResponse != "N"
			List<ImsProcessedInvoiceEntity> actionResponseToBeSaved = activeRecords
					.stream()
					.filter(record -> !"N".equals(record.getActionResponse()))
					.collect(Collectors.toList());

			if ("RESET".equalsIgnoreCase(action)) {
				invokeSaveOrUpdate(gstin, groupCode, tableTyp, action,
						actionResponseToBeReset, newTableType);
			} else {
				if (actionResponseToBeSaved.isEmpty()) {
					LOGGER.error("No active Data found to save to IMS for {} :",
							gstin);
					queueRepo.updateStatusBasedonGstin("No Data", gstin,
							tableTyp, "No active data found to be saved",
							action);
					return;
				}
				invokeSaveOrUpdate(gstin, groupCode, tableTyp, action,
						actionResponseToBeSaved, newTableType);
			}

		} catch (Exception e) {
			LOGGER.error("Error occured in ImsSectionSaveHandler {} : ", e);
			queueRepo.updateStatusBasedonGstin("Failed", gstin, tableTyp,
					"Technical Error", action);
			throw new AppException();
		}

	}

	/**
	 * @param gstin
	 * @param groupCode
	 * @param tableTyp
	 * @param action
	 * @param actionResponseToBeSaved
	 */
	private void invokeSaveOrUpdate(String gstin, String groupCode,
			String tableTyp, String action,
			List<ImsProcessedInvoiceEntity> actionResponse, List<String> newTableType) {
		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);

		Integer chunkSize = 5000;
		if (config != null)
			chunkSize = Integer.valueOf(config.getValue());

		List<List<ImsProcessedInvoiceEntity>> chunks = Lists
				.partition(actionResponse, chunkSize);
		List<ImsProcessedInvoiceEntity> requiredRecords = chunks.get(0);

		if (chunks.size() > 1) {
			User user = SecurityContext.getUser();
			String userName = user != null ? user.getUserPrincipalName()
					: "SYSTEM";
			ImsSaveJobQueueEntity entity = new ImsSaveJobQueueEntity();
			entity.setSection(tableTyp);
			entity.setIsActive(true);
			entity.setGstin(gstin);
			entity.setCreatedBy(userName);
			entity.setCreatedOn(LocalDateTime.now());
			entity.setAction(action);
			entity.setStatus("In Queue");

			queueRepo.save(entity);
		}

		Map<String, List<ImsProcessedInvoiceEntity>> tableTypeMap = requiredRecords
				.stream().collect(Collectors
						.groupingBy(ImsProcessedInvoiceEntity::getTableType));

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"ImsSectionSaveHandler: TableTypeMap, %s", tableTypeMap);
			LOGGER.debug(msg);
		}

		tableTypeMap.forEach((tableType, dtos) -> {
			List<ImsProcessedInvoiceEntity> updatedDtos = dtos.stream()
					.peek(dto -> {
						String invoiceType = dto.getInvoiceType();
						if ("CDN".equalsIgnoreCase(tableType)) {
							if ("CR".equalsIgnoreCase(invoiceType)
									|| "C".equalsIgnoreCase(invoiceType)) {
								dto.setTableType("CN");
							} else {
								dto.setTableType("DN");
							}
						} else if ("CDNA".equalsIgnoreCase(tableType)) {
							if ("CRC".equalsIgnoreCase(invoiceType)
									|| "C".equalsIgnoreCase(invoiceType)) {
								dto.setTableType("CNA");
							} else {
								dto.setTableType("DNA");
							}
						}
					}).collect(Collectors.toList());
			tableTypeMap.replace(tableType, updatedDtos);
		});

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("ImsSectionSaveHandler: TableTypeMap after CN DN "
							+ "changes , %s", tableTypeMap);
			LOGGER.debug(msg);
		}
		for (Map.Entry<String, List<ImsProcessedInvoiceEntity>> entry : tableTypeMap
				.entrySet()) {

			String tableType = entry.getKey();
			if (!tableTyp.equalsIgnoreCase(tableType)) {
				continue;
			}
			List<ImsProcessedInvoiceEntity> dtos = entry.getValue();
			
			
			List<String> docKeys = dtos.stream()
				    .map(ImsProcessedInvoiceEntity::getDocKey)
				    .filter(Objects::nonNull)
				    .distinct()
				    .collect(Collectors.toList());

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"ImsSectionSaveHandler:TableTypeMap, tableType %s - Data size %d ",
						tableType, dtos.size());
				LOGGER.debug(msg);
			}

			// ImsActionSaveRequestDto dto = new ImsActionSaveRequestDto();

			List<Long> ids = dtos.stream().map(ImsProcessedInvoiceEntity::getId)
					.collect(Collectors.toList());

			ImsActionSaveRequestDto saveDto = new ImsActionSaveRequestDto();
			saveDto.setRtin(gstin);
			saveDto.setReqtyp(action);
			ImsInvData invData = new ImsInvData();

			// 156087 IMS | Error handle in case of save to GSTN twice
			// without any fresh IMS get call
			
			/*
			 * List<ImsProcessedInvoiceEntity> previousSaveList = psdRepo
			 * .findLatestSavedToGstin(gstin);
			 */

			List<Object[]> previousSaveList = psdRepo
					.findLatestActionResponsesByDocKeyList(gstin, docKeys, newTableType);
			

			/*Map<String, String> docKeyActionResponseMap = previousSaveList
					.stream()
					.collect(Collectors.toMap(
							ImsProcessedInvoiceEntity::getDocKey,
							ImsProcessedInvoiceEntity::getActionResponse));*/
			
			// Convert result to Map<String, String> => DOC_KEY -> ACTION_RESPONSE
			Map<String, String> docKeyActionResponseMap = previousSaveList.stream()
			    .collect(Collectors.toMap(
			    	row -> row[0] != null ? row[0].toString() : null,  // DOC_KEY
			        row -> row[1] != null ? row[1].toString() : null // ACTION_RESPONSE
			    ));

			switch (tableType) {

			case "B2B":

				List<ImsB2bInvoice> b2bList = convertToDto(dtos, tableType,
						ImsB2bInvoice.class, docKeyActionResponseMap, action);

				invData.setB2b(b2bList);

				break;
			case "B2BA":

				List<ImsB2baInvoice> b2baList = convertToDto(dtos, tableType,
						ImsB2baInvoice.class, docKeyActionResponseMap, action);

				invData.setB2ba(b2baList);

				break;

			case "CN":

				List<ImsCnInvoice> cnList = convertToDto(dtos, tableType,
						ImsCnInvoice.class, docKeyActionResponseMap, action);

				invData.setCn(cnList);

				break;

			case "DN":

				List<ImsDnInvoice> dnList = convertToDto(dtos, tableType,
						ImsDnInvoice.class, docKeyActionResponseMap, action);

				invData.setDn(dnList);

				break;
			case "CNA":

				List<ImsCnaInvoice> cnaList = convertToDto(dtos, tableType,
						ImsCnaInvoice.class, docKeyActionResponseMap, action);

				invData.setCna(cnaList);

				break;

			case "DNA":

				List<ImsDnaInvoice> dnaList = convertToDto(dtos, tableType,
						ImsDnaInvoice.class, docKeyActionResponseMap, action);

				invData.setDna(dnaList);

				break;

			case "ECOM":

				List<ImsEcomInvoice> ecomList = convertToDto(dtos, tableType,
						ImsEcomInvoice.class, docKeyActionResponseMap, action);

				invData.setEcom(ecomList);

				break;
			case "ECOMA":

				List<ImsEcomaInvoice> ecomaList = convertToDto(dtos, tableType,
						ImsEcomaInvoice.class, docKeyActionResponseMap, action);

				invData.setEcoma(ecomaList);

				break;
			default:

				LOGGER.error("unknown TableType encountered {} :", tableType);
			}

			saveDto.setInvdata(invData);

			Triplet<List<Long>, ImsActionSaveRequestDto, String> batchInfo = new Triplet<>(
					ids, saveDto, groupCode);

			List<SaveToGstnBatchRefIds> saveImsBatch = saveImsBatch(batchInfo,
					tableType, action);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"ImsSectionSaveHandler:Save completed , "
								+ "tableType %s - Data size %d , saveImsBatch %s ",
						tableType, dtos.size(), saveImsBatch.toString());
				LOGGER.debug(msg);
			}

		}
	}

	public Gstr1SaveBatchEntity saveBatch(String sgstin, String tableType,
			String groupCode, int batchSize, Long retryCount,
			Long userRequestId, String jsonReq, String action) {

		Gstr1SaveBatchEntity batch = new Gstr1SaveBatchEntity();

		LocalDateTime now = LocalDateTime.now();

		batch.setReturnType("IMS");
		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		batch.setBatchDate(now);
		batch.setGroupCode(groupCode);
		batch.setSgstin(sgstin);
		batch.setReturnPeriod("000000");
		batch.setSection(tableType);
		batch.setBatchSize(batchSize);
		batch.setSaveRequestPayload(GenUtil.convertStringToClob(jsonReq));
		batch.setOperationType(action);
		batch.setRetryCount(retryCount != null ? retryCount : 0l);
		batch.setUserRequestId(userRequestId != null ? userRequestId : 0l);
		TenantContext.setTenantId(groupCode);
		LOGGER.debug("groupCode {} is set", groupCode);
		batch = batchRepo.save(batch);
		LOGGER.debug("Batch is created successfully.");
		return batch;

	}

	private <T> List<T> convertToDto(List<ImsProcessedInvoiceEntity> objs,
			String tableType, Class<T> entityClass,
			Map<String, String> docKeyActionResponseMap, String action) {

		try {

			List<T> entityList = new ArrayList<>();

			for (ImsProcessedInvoiceEntity obj : objs) {

				T entity = entityClass.newInstance();

				if ("SAVE".equalsIgnoreCase(action)) {
					
					entity.getClass().getMethod("setAction", String.class)
							.invoke(entity, obj.getActionResponse());
					
					if(!obj.getActionResponse().equalsIgnoreCase("A")) {
					entity.getClass().getMethod("setRemarks", String.class)
					.invoke(entity, obj.getResponseRemarks());
					}
					
					if (!tableType.equalsIgnoreCase("B2B")
							&& !tableType.equalsIgnoreCase("ECOM")
							&& !tableType.equalsIgnoreCase("DN")) {
						
						entity.getClass().getMethod("setItcRedReq", String.class)
						.invoke(entity, obj.getItcRedReq());
						
						String itcrequired = obj.getItcRedReq()!= null 
								? obj.getItcRedReq() : null;
								
						if(itcrequired !=null) {		
						
						entity.getClass().getMethod("setDeclIgst", BigDecimal.class)
						.invoke(entity, obj.getDeclIgst() != null ? 
								obj.getDeclIgst() : BigDecimal.ZERO);
						
						entity.getClass().getMethod("setDeclCgst", BigDecimal.class)
						.invoke(entity, obj.getDeclCgst() != null ? 
								obj.getDeclCgst() : BigDecimal.ZERO);
						
						entity.getClass().getMethod("setDeclSgst", BigDecimal.class)
						.invoke(entity, obj.getDeclSgst() != null ? 
								obj.getDeclSgst() : BigDecimal.ZERO);
						
						entity.getClass().getMethod("setDeclCess", BigDecimal.class)
						.invoke(entity, obj.getDeclCess() != null ? 
								obj.getDeclCess() : BigDecimal.ZERO);
						}
					}
				}

				if (!tableType.equalsIgnoreCase("ECOM")
						&& !tableType.equalsIgnoreCase("ECOMA")) {

					entity.getClass().getMethod("setInv_typ", String.class)
							.invoke(entity, obj.getGstnInvType());

				}

				if (tableType.equalsIgnoreCase("B2BA")
						|| tableType.equalsIgnoreCase("CNA")
						|| tableType.equalsIgnoreCase("DNA")) {

					entity.getClass().getMethod("setOinum", String.class)
							.invoke(entity, obj.getOrgDocNum());

					Date orgDocDate = obj.getOrgDocDate();

					if (orgDocDate != null) {

						LocalDate localOrgDate = orgDocDate.toInstant()
								.atZone(ZoneId.systemDefault()).toLocalDate();

						DateTimeFormatter formatter = DateTimeFormatter
								.ofPattern("dd-MM-yyyy");

						String orgDate = localOrgDate.format(formatter);

						entity.getClass().getMethod("setOidt", String.class)
								.invoke(entity, orgDate);
					}

				}

				entity.getClass().getMethod("setStin", String.class)
						.invoke(entity, obj.getSupplierGstin());
				entity.getClass().getMethod("setInum", String.class)
						.invoke(entity, obj.getInvoiceNumber());

				entity.getClass().getMethod("setSrcform", String.class)
						.invoke(entity, obj.getFormType());
				entity.getClass().getMethod("setRtnprd", String.class)
						.invoke(entity, obj.getReturnPeriod());

				Date invoiceDate = obj.getInvoiceDate();

				if (invoiceDate != null) {

					LocalDate localDate = invoiceDate.toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate();

					DateTimeFormatter formatter = DateTimeFormatter
							.ofPattern("dd-MM-yyyy");

					String date = localDate.format(formatter);

					entity.getClass().getMethod("setIdt", String.class)
							.invoke(entity, date);
				}

				entity.getClass().getMethod("setVal", BigDecimal.class)
						.invoke(entity, obj.getInvoiceValue());
				entity.getClass().getMethod("setTxval", BigDecimal.class)
						.invoke(entity, obj.getTaxableValue());
				entity.getClass().getMethod("setIamt", BigDecimal.class)
						.invoke(entity, obj.getIgstAmt());
				entity.getClass().getMethod("setCamt", BigDecimal.class)
						.invoke(entity, obj.getCgstAmt());
				entity.getClass().getMethod("setSamt", BigDecimal.class)
						.invoke(entity, obj.getSgstAmt());
				entity.getClass().getMethod("setCess", BigDecimal.class)
						.invoke(entity, obj.getCessAmt());
				entity.getClass().getMethod("setPos", String.class)
						.invoke(entity, obj.getPos());

				if (docKeyActionResponseMap.get(obj.getDocKey()) != null) {

					entity.getClass().getMethod("setPrev_status", String.class)
							.invoke(entity, docKeyActionResponseMap
									.get(obj.getDocKey()));

				} else {
					entity.getClass().getMethod("setPrev_status", String.class)
							.invoke(entity, obj.getActionGstn());
				}

				entity.getClass().getMethod("setPsdDocId", Long.class)
						.invoke(entity, obj.getId());

				entityList.add(entity);

			}

			return entityList;

		} catch (Exception e) {
			LOGGER.error("Error while creating instance of entity class {}",
					entityClass.getName(), e);
			throw new AppException(e);
		}

	}

	private List<SaveToGstnBatchRefIds> saveImsBatch(
			Triplet<List<Long>, ImsActionSaveRequestDto, String> batchInfo,
			String tableType, String action) {

		// Return collection.
		List<SaveToGstnBatchRefIds> retIds = new ArrayList<>();
		Stack<Triplet<List<Long>, ImsActionSaveRequestDto, String>> batchStack = new Stack<>();
		batchStack.push(batchInfo);

		while (true) {
			try {
				Triplet<List<Long>, ImsActionSaveRequestDto, String> curBatch = batchStack
						.pop();
				SaveToGstnBatchRefIds refIds = process(curBatch, tableType,
						action);
				retIds.add(refIds);

				// TODO check for 5MB.
			} catch (PayloadSizeExceededException ex) {

				List<Triplet<List<Long>, ImsActionSaveRequestDto, String>> splitChunks = batchChunk(
						batchInfo.getValue1(), batchInfo.getValue2(),
						tableType);
				splitChunks.forEach(obj -> batchStack.push(obj));
			}

			if (batchStack.isEmpty())
				break;
		}

		return retIds;
	}

	// check for 5MB.
	private List<Triplet<List<Long>, ImsActionSaveRequestDto, String>> batchChunk(
			ImsActionSaveRequestDto ims, String groupCode, String tableType) {

		int objSize = 0;

		Pair<List<Long>, ImsActionSaveRequestDto> pair1 = null;
		Pair<List<Long>, ImsActionSaveRequestDto> pair2 = null;

		List<ImsB2bInvoice> b2bList1 = null;
		List<ImsB2baInvoice> b2baList1 = null;
		List<ImsCnInvoice> cnList1 = null;
		List<ImsDnInvoice> dnList1 = null;
		List<ImsCnaInvoice> cnaList1 = null;
		List<ImsDnaInvoice> dnaList1 = null;
		List<ImsEcomInvoice> ecomList1 = null;
		List<ImsEcomaInvoice> ecomaList1 = null;

		List<ImsB2bInvoice> b2bList2 = null;
		List<ImsB2baInvoice> b2baList2 = null;
		List<ImsCnInvoice> cnList2 = null;
		List<ImsDnInvoice> dnList2 = null;
		List<ImsCnaInvoice> cnaList2 = null;
		List<ImsDnaInvoice> dnaList2 = null;
		List<ImsEcomInvoice> ecomList2 = null;
		List<ImsEcomaInvoice> ecomaList2 = null;

		ImsInvData invdata = ims.getInvdata();
		if ("B2B".equalsIgnoreCase(tableType)) {
			objSize = invdata.getB2b().size();

			if (objSize > 1) {
				b2bList1 = invdata.getB2b().subList(0, objSize / 2);
				b2bList2 = invdata.getB2b().subList(objSize / 2, objSize);
			}

		} else if ("B2BA".equalsIgnoreCase(tableType)) {
			objSize = invdata.getB2ba().size();
			if (objSize > 1) {
				b2baList1 = invdata.getB2ba().subList(0, objSize / 2);
				b2baList2 = invdata.getB2ba().subList(objSize / 2, objSize);
			}
		} else if ("CN".equalsIgnoreCase(tableType)) {
			objSize = invdata.getCn().size();
			if (objSize > 1) {
				cnList1 = invdata.getCn().subList(0, objSize / 2);
				cnList2 = invdata.getCn().subList(objSize / 2, objSize);
			}
		} else if ("CNA".equalsIgnoreCase(tableType)) {
			objSize = invdata.getCna().size();
			if (objSize > 1) {
				cnaList1 = invdata.getCna().subList(0, objSize / 2);
				cnaList2 = invdata.getCna().subList(objSize / 2, objSize);
			}
		} else if ("DN".equalsIgnoreCase(tableType)) {
			objSize = invdata.getDn().size();
			if (objSize > 1) {
				dnList1 = invdata.getDn().subList(0, objSize / 2);
				dnList2 = invdata.getDn().subList(objSize / 2, objSize);
			}
		} else if ("DNA".equalsIgnoreCase(tableType)) {
			objSize = invdata.getDna().size();
			if (objSize > 1) {
				dnaList1 = invdata.getDna().subList(0, objSize / 2);
				dnaList2 = invdata.getDna().subList(objSize / 2, objSize);
			}
		} else if ("ECOM".equalsIgnoreCase(tableType)) {
			objSize = invdata.getEcom().size();
			if (objSize > 1) {
				ecomList1 = invdata.getEcom().subList(0, objSize / 2);
				ecomList2 = invdata.getEcom().subList(objSize / 2, objSize);
			}
		} else if ("ECOMA".equalsIgnoreCase(tableType)) {
			objSize = invdata.getEcoma().size();
			if (objSize > 1) {
				ecomaList1 = invdata.getEcoma().subList(0, objSize / 2);
				ecomaList2 = invdata.getEcoma().subList(objSize / 2, objSize);
			}
		}

		pair1 = setChunkData(ims, b2bList1, b2baList1, cnList1, dnList1,
				cnaList1, dnaList1, ecomList1, ecomaList1, tableType);

		pair2 = setChunkData(ims, b2bList2, b2baList2, cnList2, dnList2,
				cnaList2, dnaList2, ecomList2, ecomaList2, tableType);

		List<Triplet<List<Long>, ImsActionSaveRequestDto, String>> batchInfoList = new ArrayList<>();
		batchInfoList.add(
				new Triplet<>(pair1.getValue0(), pair1.getValue1(), groupCode));
		batchInfoList.add(
				new Triplet<>(pair2.getValue0(), pair2.getValue1(), groupCode));
		return batchInfoList;

	}

	private Pair<List<Long>, ImsActionSaveRequestDto> setChunkData(
			ImsActionSaveRequestDto imsData, List<ImsB2bInvoice> b2bList,
			List<ImsB2baInvoice> b2baList, List<ImsCnInvoice> cnList,
			List<ImsDnInvoice> dnList, List<ImsCnaInvoice> cnaList,
			List<ImsDnaInvoice> dnaList, List<ImsEcomInvoice> ecomList,
			List<ImsEcomaInvoice> ecomaList, String tableType) {

		ImsActionSaveRequestDto ims = new ImsActionSaveRequestDto();
		List<Long> idsList = new ArrayList<>();
		ImsInvData invData = new ImsInvData();

		ims.setReqtyp(imsData.getReqtyp());
		ims.setRtin(imsData.getRtin());

		switch (tableType) {

		case "B2B":

			invData.setB2b(b2bList);

			idsList = b2bList.stream().map(ImsB2bInvoice::getPsdDocId)
					.collect(Collectors.toList());

			break;

		case "B2BA":

			invData.setB2ba(b2baList);

			idsList = b2baList.stream().map(ImsB2baInvoice::getPsdDocId)
					.collect(Collectors.toList());

			break;

		case "CN":

			invData.setCn(cnList);

			idsList = cnList.stream().map(ImsCnInvoice::getPsdDocId)
					.collect(Collectors.toList());

			break;

		case "DN":

			invData.setDn(dnList);

			idsList = dnList.stream().map(ImsDnInvoice::getPsdDocId)
					.collect(Collectors.toList());

			break;
		case "CNA":

			invData.setCna(cnaList);

			idsList = cnaList.stream().map(ImsCnaInvoice::getPsdDocId)
					.collect(Collectors.toList());

			break;

		case "DNA":

			invData.setDna(dnaList);

			idsList = dnaList.stream().map(ImsDnaInvoice::getPsdDocId)
					.collect(Collectors.toList());

			break;

		case "ECOM":

			invData.setEcom(ecomList);

			idsList = ecomList.stream().map(ImsEcomInvoice::getPsdDocId)
					.collect(Collectors.toList());

			break;
		case "ECOMA":

			invData.setEcoma(ecomaList);

			idsList = ecomaList.stream().map(ImsEcomaInvoice::getPsdDocId)
					.collect(Collectors.toList());

			break;
		default:

			LOGGER.error("unknown TableType encountered while chunking{} :",
					tableType);
		}

		ims.setInvdata(invData);

		return new Pair<>(idsList, ims);
	}

	private SaveToGstnBatchRefIds process(
			Triplet<List<Long>, ImsActionSaveRequestDto, String> batchInfo,
			String tableType, String action) {
		List<Long> idsList = batchInfo.getValue0();
		ImsActionSaveRequestDto imsData = batchInfo.getValue1();
		String groupCode = batchInfo.getValue2();
		SaveToGstnBatchRefIds oneResp = new SaveToGstnBatchRefIds();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		LOGGER.debug("New Batch with SGSTN {}", imsData.getRtin());
		try {

			String batch = gson.toJson(imsData);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Ims SaveToGstn {} section batch json has formed as {}",
						tableType, batch);
			}

			// making entry in GSTR1_GSTN_SAVE_BATCH table for each table
			// type

			Gstr1SaveBatchEntity saveBatch = saveBatch(imsData.getRtin(),
					tableType, groupCode, idsList.size(), null, null, batch, action);
			Long gstnBatchId = saveBatch.getId();
			queueRepo.updateBatchId(imsData.getRtin(), tableType,
					gstnBatchId.toString(), action);
			APIResponse resp = null;
			try {
				
				if ("SAVE".equalsIgnoreCase(action)) {
				resp = gstnServer.imsSaveApiCall(groupCode, batch,
						imsData.getRtin(), gstnBatchId);
				}else {
					resp = gstnServer.imsResetApiCall(groupCode, batch,
							imsData.getRtin(), gstnBatchId);
				}
				

			} catch (java.lang.UnsupportedOperationException
					| AppException ex) {
				deleteBatch(gstnBatchId, groupCode);

				throw new AppException(ex,
						"{} error while saving batch to Gstn");
			}

			String refId = null;
			String txnId = null;
			if (resp.isSuccess()) {

				// update RefId and other details
				refId = updateRefIdAndTxnId(groupCode, gstnBatchId, resp);
				// update is_sent_to_gstn in Proceesed table

				queueRepo.updateInActiveStatus("RefId Generated",
						imsData.getRtin(), tableType, "", action);

				List<List<Long>> chunks = Lists.partition(idsList, 2000);
				for (List<Long> chunk : chunks) {
					LOGGER.debug("Inside Chunk Method");
					psdRepo.updateBatchIdAndSentFlag(chunk, gstnBatchId, refId,
							true);

				}

				psdRepo.updateBatchIdAndSentFlag(idsList, gstnBatchId, refId,
						true);

				txnId = resp.getTxnId();
			} else {
				String errorCode = resp.getError().getErrorCode();
				String errorDesc = resp.getError().getErrorDesc();
				queueRepo.updateStatusBasedonGstin("Failed", imsData.getRtin(),
						tableType, errorDesc, action);
				if (errorDesc.contains("API Under Maintenance in DC2")) {
					queueRepo.updateStatusDowntime("Failed",imsData.getRtin());
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Error thrown by Gstn ErrorCode: {} and "
							+ "ErrorDesc: {}", errorCode, errorDesc);
				}
				LocalDateTime now = LocalDateTime.now();
				batchRepo.updateErrorMesg(gstnBatchId, errorCode, errorDesc,
						now);
				deleteBatch(gstnBatchId, groupCode);
			}

			/**
			 * Setting to return the ref and its batch id
			 */
			oneResp.setGstnBatchId(gstnBatchId);
			oneResp.setRefId(refId);
			oneResp.setTxnId(txnId);

			LOGGER.info("New Batch with BatchId {} and RefId {}", gstnBatchId,
					refId);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			queueRepo.updateStatusBasedonGstin("Failed", imsData.getRtin(),
					tableType, msg, action);
			LOGGER.error(msg, ex);
		}

		return oneResp;
	}

	private String updateRefIdAndTxnId(String groupCode, Long gstnBatchId,
			APIResponse resp) {
		String refId = null;
		// try {

		String saveJsonResp = resp.getResponse();
		String txnId = resp.getTxnId();

		JsonObject jsonObject = JsonParser.parseString(saveJsonResp)
				.getAsJsonObject();
		if (jsonObject.get(APIIdentifiers.REF_ID) != null) {
			refId = jsonObject.get(APIIdentifiers.REF_ID).getAsString();
		} else if (jsonObject.get(APIIdentifiers.REFERENECE_ID) != null) {
			refId = jsonObject.get(APIIdentifiers.REFERENECE_ID).getAsString();
		}

		if (refId != null && refId.length() > 0) {
			LocalDateTime now = LocalDateTime.now();
			batchRepo.updateBatchRefID(refId, gstnBatchId, txnId, now);
		}
		return refId;

	}
	public void deleteBatch(Long gstnBatchId, String groupCode) {

		if (gstnBatchId != null && gstnBatchId != 0) {
			// try {

			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);
			Optional<Gstr1SaveBatchEntity> batch = batchRepo
					.findById(gstnBatchId);
			if (batch.isPresent()) {
				Gstr1SaveBatchEntity batch1 = batch.get();
				batch1.setDelete(true);
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				batch1.setModifiedOn(now);
				batch1.setModifiedBy(APIConstants.SYSTEM.toUpperCase());
				batchRepo.save(batch1);
				LOGGER.debug("Batch is deleted softly.");
			}
			/*
			 * } catch (Exception ex) { throw new
			 * AppException("Error in deleting Batch", ex); }
			 */
		} else {
			LOGGER.debug("Invalid Batch Id to delete batch.");
		}

	}
}
