package com.ey.advisory.app.services.gstr1fileupload;

import static com.ey.advisory.common.GSTConstants.WEB_UPLOAD_KEY;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.Gstr2XExcelTcsTdsEntity;
import com.ey.advisory.admin.data.entities.client.Gstr2XProcessedTcsTdsEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.GetGstr2xTcsAndTcsaInvoicesEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2xTdsAndTdsaInvoicesEntity;
import com.ey.advisory.app.data.entities.client.VerticalWebErrorTable;
import com.ey.advisory.app.data.repositories.client.Gstr2XExcelTcsTdsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2XProcessedTcsTdsRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTCSAndTCSADetailsAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTDSAndTDSADetailsAtGstnRepository;
import com.ey.advisory.app.services.annexure1fileupload.VerticalWebUploadErrorService2;
import com.ey.advisory.app.services.docs.SRFileToTcsTdsConvertion;
import com.ey.advisory.app.services.validation.tdstcs.TcsTdsValidationChain;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("TdsTcsBusinessProcess")
@Slf4j
public class TdsTcsBusinessProcess {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr2XProcessedTcsTdsRepository")
	private Gstr2XProcessedTcsTdsRepository gstr2XProcessedTcsTdsRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService2")
	private VerticalWebUploadErrorService2 verticalWebUploadErrorService;

	@Autowired
	@Qualifier("SRFileToTcsTdsConvertion")
	private SRFileToTcsTdsConvertion sRFileToTcsTdsConvertion;

	@Autowired
	@Qualifier("TcsTdsValidationChain")
	private TcsTdsValidationChain tcsTdsValidationChain;

	@Autowired
	@Qualifier("Gstr2XExcelTcsTdsRepository")
	private Gstr2XExcelTcsTdsRepository gstr2XExcelTcsTdsRepository;

	private static final List<String> TCS_TCSA = ImmutableList.of("TCS",
			"TCSA");

	private static final List<String> TDS_TDSA = ImmutableList.of("TDSA",
			"TDS");

	@Autowired
	@Qualifier("Gstr2xGetTDSAndTDSADetailsAtGstnRepository")
	private Gstr2xGetTDSAndTDSADetailsAtGstnRepository gstr2xTdsTdsa;

	@Autowired
	@Qualifier("Gstr2xGetTCSAndTCSADetailsAtGstnRepository")
	private Gstr2xGetTCSAndTCSADetailsAtGstnRepository gstr2xTcsTcsa;

	public void processTdsTcsBusinessPData(
			List<Gstr2XExcelTcsTdsEntity> strProcessRecords,
			List<Gstr2XExcelTcsTdsEntity> strErrRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Gstr2XExcelTcsTdsEntity> busErrorRecords = new ArrayList<>();
		List<Gstr2XExcelTcsTdsEntity> infoProcessed = new ArrayList<>();
		List<Gstr2XExcelTcsTdsEntity> busProcessRecords = new ArrayList<>();
		List<Gstr2XProcessedTcsTdsEntity> saveAll = new ArrayList<>();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("strProcessRecords is {} ", strProcessRecords);
			
		}

		Map<Long, List<Gstr2XExcelTcsTdsEntity>> allDocsMap = strProcessRecords
				.stream()
				.collect(Collectors.groupingBy(doc -> getIdFromAsEntered(doc)));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("allDocsMap is {} ", allDocsMap);
			
		}

		List<GetGstr2xTcsAndTcsaInvoicesEntity> activeTcsAndTcsaRecords = gstr2xTcsTcsa
				.findActiveRecords();
		List<GetGstr2xTdsAndTdsaInvoicesEntity> activeTdsAndTdsaRecords = gstr2xTdsTdsa
				.findActiveRecords();

		Map<String, List<GetGstr2xTcsAndTcsaInvoicesEntity>> tcsAndTcsarecords = activeTcsAndTcsaRecords
				.stream()
				.collect(Collectors.groupingBy(doc -> generateKey(doc)));

		Map<String, List<GetGstr2xTdsAndTdsaInvoicesEntity>> tdsAndTdsarecords = activeTdsAndTdsaRecords
				.stream()
				.collect(Collectors.groupingBy(doc -> generateTdsKey(doc)));

		Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
		Map<String, List<ProcessingResult>> infoWithProcessed = new HashMap<>();

		List<String> errorKeys = new ArrayList<>();
		List<String> infoKeys = new ArrayList<>();
		List<String> errorInfo = new ArrayList<>();

		List<ProcessingResult> current = null;
		for (Gstr2XExcelTcsTdsEntity tcsTdsData : strProcessRecords) {
			List<ProcessingResult> results = tcsTdsValidationChain
					.validate(tcsTdsData, null);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("tcsTdsData is {} ", tcsTdsData);
				
			}

			if ( !results.isEmpty()) {
				String key = tcsTdsData.getDocKey();
				String keys = key.concat(GSTConstants.SLASH)
						.concat(tcsTdsData.getId().toString());
				List<ProcessingResultType> listTypes = new ArrayList<>();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("inside result not empty results value {} ", results);
					
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("inside result not empty keys value {} ", keys);
					
				}
				
				for (ProcessingResult types : results) {
					ProcessingResultType type = types.getType();
					listTypes.add(type);
				}
				
				List<String> errorType = listTypes.stream()
						.map(object -> Objects.toString(object, null))
						.collect(Collectors.toList());

				current = businessValErrors.get(keys);
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("inside result not empty {} ", errorType);
					
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("inside result not empty current value {} ", current);
					
				}
				if (current == null) {
					current = new ArrayList<>();
					if (!errorType.isEmpty()) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							errorInfo.add(key);
							errorKeys.add(key);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("inside result not empty error and info"
										+ " errorKeys value {} ", errorKeys);
								
							}

							
							businessValErrors.put(keys, results);
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(key);
							businessValErrors.put(keys, results);
							
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("inside result not empty only error "
										+ " errorKeys value {} ", errorKeys);
								
							}
						} else {
							infoWithProcessed.put(keys, results);
							infoKeys.add(key);
							
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("inside result not empty only info "
										+ " errorKeys value {} ", infoKeys);
								
							}
						}
					}
				} else {
					if (!errorType.isEmpty()) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							errorInfo.add(key);
							errorKeys.add(key);
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("inside result not empty error and info"
										+ " errorKeys value {} ", errorKeys);
								
							}
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(key);
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("inside result not empty only error "
										+ " errorKeys value {} ", errorKeys);
								
							}
						} else {
							infoWithProcessed
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
							infoKeys.add(key);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("inside result not empty only info "
										+ " errorKeys value {} ", infoKeys);
								
							}
						}
					}
				}
			}
		}

		for (Gstr2XExcelTcsTdsEntity process : strProcessRecords) {
			String key = process.getDocKey();
			if (!errorKeys.contains(key)) {
				if (infoKeys != null && infoKeys.contains(key)) {
					process.setInformation(true);
					infoProcessed.add(process);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("process is {} ", process);
					
				}
				busProcessRecords.add(process);
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("busProcessRecords inside process is {} ", busProcessRecords);
					
				}
			} else {
				if (errorInfo != null && errorInfo.contains(key)) {
					process.setInformation(true);
				}
				process.setError(true);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("process in else block is {} ", process);
					
				}
				busErrorRecords.add(process);
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("busProcessRecords inside process in else block is {} ", busErrorRecords);
					
				}
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("businessErrorRecords {} ", busErrorRecords);
			LOGGER.debug("businessProcessedRecords {} ", busProcessRecords);
		}

		if ( !busErrorRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<VerticalWebErrorTable>> errorMap = verticalWebUploadErrorService
					.convertErrors(businessValErrors,
							GSTConstants.BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorGstr2xRecords(busErrorRecords, errorMap);
		}
		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<VerticalWebErrorTable>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed,
							GSTConstants.BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorGstr2xRecords(strErrRecords, errorMap);
		}

		if (!busProcessRecords.isEmpty()) {

			List<Gstr2XProcessedTcsTdsEntity> tdsTcsDoc1 = sRFileToTcsTdsConvertion
					.convertSRFileToTcsTds(busProcessRecords, updateFileStatus);

			List<String> existProcessData = new ArrayList<>();
			List<String> gstnTcsAndTcsa = new ArrayList<>();
			List<String> gstnTdsAndTdsa = new ArrayList<>();

			for (Gstr2XProcessedTcsTdsEntity tcsTdsProcessed : tdsTcsDoc1) {
				String txpdInvKey = tcsTdsProcessed.getDocKey();
				existProcessData.add(txpdInvKey);
				String recordType = tcsTdsProcessed.getRecordType()
						.toUpperCase();
				if (TCS_TCSA.contains(recordType)) {
					gstnTcsAndTcsa.add(txpdInvKey);
				} else {
					gstnTdsAndTdsa.add(txpdInvKey);
				}

			}
			if ( !existProcessData.isEmpty()) {

				List<Long> docIds = new ArrayList<>();
				List<List<String>> docKeyChunks = Lists
						.partition(existProcessData, 2000);
				if (!docKeyChunks.isEmpty()) {
					docKeyChunks.forEach(chunk -> docIds
							.addAll(gstr2XProcessedTcsTdsRepository
									.findActiveDocsByDocKeys(chunk)));
				}
				if (!docIds.isEmpty()) {
					List<List<Long>> docIdChunks = Lists.partition(docIds,
							2000);
					docIdChunks.forEach(docIdChunk -> {
						if (LOGGER.isDebugEnabled()) {
							String msg = String
									.format("List of DocIds which are about to get "
											+ "soft delete: %s", docIdChunk);
							LOGGER.debug(msg);
						}
						gstr2XProcessedTcsTdsRepository
								.updateDuplicateDocDeletionByDocKeys(docIdChunk,
										LocalDateTime.now(),
										updateFileStatus.getUpdatedBy());
						if (LOGGER.isDebugEnabled()) {
							String log = "Documents soft deleted successfully";
							LOGGER.debug(log);
						}
					});
				}
			}

			if (!gstnTdsAndTdsa.isEmpty()) {

				List<Long> docIds = new ArrayList<>();
				List<List<String>> docKeyChunks = Lists
						.partition(gstnTdsAndTdsa, 2000);
				if (!docKeyChunks.isEmpty()) {
					docKeyChunks.forEach(chunk -> docIds.addAll(
							gstr2xTdsTdsa.findActiveDocsByDocKeys(chunk)));
				}
				if (!docIds.isEmpty()) {
					List<List<Long>> docIdChunks = Lists.partition(docIds,
							2000);
					docIdChunks.forEach(docIdChunk -> {
						if (LOGGER.isDebugEnabled()) {
							String msg = String
									.format("List of DocIds which are about to get "
											+ "soft delete: %s", docIdChunk);
							LOGGER.debug(msg);
						}
						gstr2xTdsTdsa.updateUserActionByDocKeys(docIdChunk,
								LocalDateTime.now(),
								updateFileStatus.getUpdatedBy());
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Documents soft deleted successfully");
						}
					});
				}
			}
			if (!gstnTcsAndTcsa.isEmpty()) {

				List<Long> docIds = new ArrayList<>();
				List<List<String>> docKeyChunks = Lists
						.partition(gstnTcsAndTcsa, 2000);
				if (!docKeyChunks.isEmpty()) {
					docKeyChunks.forEach(chunk -> docIds.addAll(
							gstr2xTcsTcsa.findActiveDocsByDocKeys(chunk)));
				}
				if (!docIds.isEmpty()) {
					List<List<Long>> docIdChunks = Lists.partition(docIds,
							2000);
					docIdChunks.forEach(docIdChunk -> {
						if (LOGGER.isDebugEnabled()) {
							String msg = String
									.format("List of DocIds which are about to get "
											+ "soft delete: %s", docIdChunk);
							LOGGER.debug(msg);
						}
						gstr2xTcsTcsa.updateUserActionByDocKeys(docIdChunk,
								LocalDateTime.now(),
								updateFileStatus.getUpdatedBy());
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Documents soft deleted successfully");
						}
					});
				}
			}
			List<Gstr2XProcessedTcsTdsEntity> tdsTcsDoc = new ArrayList<>();

			List<String> sameKeys = new ArrayList<>();

			for (Gstr2XProcessedTcsTdsEntity ent : tdsTcsDoc1) {

				GetGstr2xTcsAndTcsaInvoicesEntity listTcsAndTcsa = tcsAndTcsarecords
						.get(ent.getDocKey()) != null
								? tcsAndTcsarecords.get(ent.getDocKey()).get(0)
								: null;

				GetGstr2xTdsAndTdsaInvoicesEntity listTdsAndTdsa = tdsAndTdsarecords
						.get(ent.getDocKey()) != null
								? tdsAndTdsarecords.get(ent.getDocKey()).get(0)
								: null;

				String category = ent.getRecordType() != null
						? ent.getRecordType().toUpperCase() : null;

				if (!sameKeys.contains(ent.getDocKey())) {
					sameKeys.add(ent.getDocKey());
					if (TCS_TCSA.contains(category)) {
						ent = sRFileToTcsTdsConvertion
								.convertBasedOnGetTcsAndTcsaReports(
										listTcsAndTcsa, ent);
						tdsTcsDoc.add(ent);
					} else if (TDS_TDSA.contains(category)) {
						ent = sRFileToTcsTdsConvertion
								.convertBasedOnGetTdsAndTdsaReports(
										listTdsAndTdsa, ent);
						tdsTcsDoc.add(ent);
					}
				}
			}
			List<Gstr2XExcelTcsTdsEntity> list1 = new ArrayList<>();

			for (Gstr2XProcessedTcsTdsEntity asEnterId : tdsTcsDoc) {

				List<Gstr2XExcelTcsTdsEntity> list = allDocsMap
						.get(asEnterId.getAsEnteredId());
				for (Gstr2XExcelTcsTdsEntity exEntity : list) {
					exEntity.setId(asEnterId.getAsEnteredId());
					exEntity.setGstin(asEnterId.getGstin());
					exEntity.setCtin(asEnterId.getCtin());
					exEntity.setFileId(asEnterId.getFileId());
					exEntity.setRetPeriod(asEnterId.getRetPeriod());
					exEntity.setDerivedRetPeriod(
							asEnterId.getDerivedRetPeriod());
					exEntity.setRecordType(asEnterId.getRecordType());
					exEntity.setDeductorUplMonth(
							asEnterId.getDeductorUplMonth());
					exEntity.setOrgDeductorUplMonth(
							asEnterId.getOrgDeductorUplMonth());

					exEntity.setIgstAmt(asEnterId.getIgstAmt() != null
							? asEnterId.getIgstAmt().toString() : null);
					exEntity.setCgstAmt(asEnterId.getCgstAmt() != null
							? asEnterId.getCgstAmt().toString() : null);
					exEntity.setSgstAmt(asEnterId.getSgstAmt() != null
							? asEnterId.getSgstAmt().toString() : null);
					exEntity.setSuppliesCollected(asEnterId.getSuppliesCollected() != null
							? asEnterId.getSuppliesCollected().toString() : null);
					exEntity.setSuppliesReturned(asEnterId.getSuppliesReturned() != null
							? asEnterId.getSuppliesReturned().toString() : null);
					exEntity.setNetSupplies(asEnterId.getNetSupplies() != null
							? asEnterId.getNetSupplies().toString() : null);
					exEntity.setInvoiceValue(asEnterId.getInvoiceValue() != null
							? asEnterId.getInvoiceValue().toString() : null);
					exEntity.setOrgTaxableValue(asEnterId.getOrgTaxableValue() != null
							? asEnterId.getOrgTaxableValue().toString() : null);
					exEntity.setOrgInvoiceValue(asEnterId.getOrgInvoiceValue() != null
							? asEnterId.getOrgInvoiceValue().toString() : null);
					exEntity.setSaveAction(asEnterId.getSaveAction());
					exEntity.setUserAction(asEnterId.getUserAction());
					exEntity.setDocKey(asEnterId.getDocKey());
					exEntity.setDataOriginTypeCode(
							asEnterId.getDataOriginTypeCode());
					exEntity.setCreatedOn(asEnterId.getCreatedOn());
					exEntity.setPsKey(asEnterId.getPsKey());
					exEntity.setBatchId(asEnterId.getBatchId());
					exEntity.setDigiGstRemarks(asEnterId.getDigiGstRemarks());
					exEntity.setDigiGstComment(asEnterId.getDigiGstComment());
					exEntity.setDeductorName(asEnterId.getDeductorName());
					exEntity.setDocNo(asEnterId.getDocNo());
					exEntity.setDocDate(asEnterId.getDocDate());
					exEntity.setOrgDocDate(asEnterId.getOrgDocDate());
					exEntity.setOrgDocNo(asEnterId.getOrgDocNo());
					exEntity.setPos(asEnterId.getPos());
					exEntity.setChkSum(asEnterId.getChkSum());
					exEntity.setGstnRemarks(asEnterId.getGstnRemarks());
					exEntity.setGstnComment(asEnterId.getGstnComment());

					
					list1.add(exEntity);
				
				}

			}
			gstr2XExcelTcsTdsRepository.saveAll(list1);
			saveAll = gstr2XProcessedTcsTdsRepository.saveAll(tdsTcsDoc);
		}

		processedRecords = (!saveAll.isEmpty()) ? saveAll.size() : 0;
		errorRecords = (!busErrorRecords.isEmpty() || !strErrRecords.isEmpty())
				? busErrorRecords.size() + strErrRecords.size() : 0;
		totalRecords = processedRecords + errorRecords;
		information = (!infoProcessed.isEmpty()) ? infoProcessed.size() : 0;

		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		gstr1FileStatusRepository.save(updateFileStatus);

	}

	private static Long getIdFromAsEntered(Gstr2XExcelTcsTdsEntity entity) {
		return entity.getId();

	}

	private String generateTdsKey(GetGstr2xTdsAndTdsaInvoicesEntity doc) {
		String category = doc.getRecordType();
		String taxGstin = doc.getSgstin();
		String retPeriod = doc.getRetPeriod();
		String deGstin = doc.getCgstin();
		String monthOfDedUpl = doc.getDeductorUploadedMonth();
		String orgMonthOfdeductorOrCollectorUpl = doc
				.getOrgDeductorUploadedMonth();

		category = (category != null) ? (String.valueOf(category)).trim() : "";
		taxGstin = (taxGstin != null) ? (String.valueOf(taxGstin)).trim() : "";
		retPeriod = (retPeriod != null) ? (String.valueOf(retPeriod)).trim()
				: "";
		deGstin = (deGstin != null) ? (String.valueOf(deGstin)).trim() : "";
		monthOfDedUpl = (monthOfDedUpl != null)
				? (String.valueOf(monthOfDedUpl)).trim() : "";
		orgMonthOfdeductorOrCollectorUpl = (orgMonthOfdeductorOrCollectorUpl != null)
				? (String.valueOf(orgMonthOfdeductorOrCollectorUpl)).trim()
				: "";
		return new StringJoiner(WEB_UPLOAD_KEY).add(category).add(taxGstin)
				.add(retPeriod).add(deGstin).add(monthOfDedUpl)
				.add(orgMonthOfdeductorOrCollectorUpl).toString();
	}

	private String generateKey(GetGstr2xTcsAndTcsaInvoicesEntity doc) {
		String category = doc.getRecordType();
		String taxGstin = doc.getSgstin();
		String retPeriod = doc.getRetPeriod();
		String deGstin = doc.getCgstin();
		String monthOfDedUpl = doc.getDeductorUploadedMonth();
		String orgMonthOfdeductorOrCollectorUpl = doc
				.getOrgDeductorUploadedMonth();

		category = (category != null) ? (String.valueOf(category)).trim() : "";
		taxGstin = (taxGstin != null) ? (String.valueOf(taxGstin)).trim() : "";
		retPeriod = (retPeriod != null) ? (String.valueOf(retPeriod)).trim()
				: "";
		deGstin = (deGstin != null) ? (String.valueOf(deGstin)).trim() : "";
		monthOfDedUpl = (monthOfDedUpl != null)
				? (String.valueOf(monthOfDedUpl)).trim() : "";
		orgMonthOfdeductorOrCollectorUpl = (orgMonthOfdeductorOrCollectorUpl != null)
				? (String.valueOf(orgMonthOfdeductorOrCollectorUpl)).trim()
				: "";
		return new StringJoiner(WEB_UPLOAD_KEY).add(category).add(taxGstin)
				.add(retPeriod).add(deGstin).add(monthOfDedUpl)
				.add(orgMonthOfdeductorOrCollectorUpl).toString();
	}
}