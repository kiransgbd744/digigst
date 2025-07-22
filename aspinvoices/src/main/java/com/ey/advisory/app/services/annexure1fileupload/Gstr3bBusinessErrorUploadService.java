package com.ey.advisory.app.services.annexure1fileupload;

import static com.ey.advisory.common.GSTConstants.BUSINESS_VALIDATIONS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Gstr3bEntity;
import com.ey.advisory.app.data.entities.client.Gstr3bExcelEntity;
import com.ey.advisory.app.data.entities.client.Gstr3bVerticalWebError;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3bExcelRepository;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputEntity;
import com.ey.advisory.app.services.businessvalidation.gstr3b.Gstr3bBusinessValidationChain;
import com.ey.advisory.app.services.docs.SRFileToGstr3BConvertion;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Slf4j
@Component("Gstr3bBusinessErrorUploadService")
public class Gstr3bBusinessErrorUploadService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3bBusinessErrorUploadService.class);
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr3BWebUploadErrorService")
	private Gstr3BWebUploadErrorService gstr3BWebUploadErrorService;

	@Autowired
	@Qualifier("SRFileToGstr3BConvertion")
	private SRFileToGstr3BConvertion sRFileToGstr3BConvertion;

	@Autowired
	@Qualifier("Gstr3bBusinessValidationChain")
	private Gstr3bBusinessValidationChain gstr3bBusinessValidationChain;

	@Autowired
	@Qualifier("Gstr3bExcelRepository")
	private Gstr3bExcelRepository gstr3bExcelRepository;

	@Autowired
	@Qualifier("Gstr3BRepository")
	private Gstr3BRepository gstr3BRepository;

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	Gstr3BGstinAspUserInputRepository gstr3bUserRepo;

	@Autowired
	@Qualifier("BasicGstr3BUploadDaoImpl")
	BasicGstr3BUploadDaoImpl basicUserUpload;

	public void processBusinessData(List<Object[]> gstr3bList,
			List<Gstr3bExcelEntity> strucProcessedRecords,
			List<Gstr3bExcelEntity> strucErrorRecords,
			Gstr1FileStatusEntity updateFileStatus, List<Long> ids) {

		List<Gstr3bExcelEntity> businessErrorRecords = new ArrayList<>();
		List<Gstr3bExcelEntity> infoProcessed = new ArrayList<>();
		List<Gstr3bExcelEntity> businessProcessedRecords = new ArrayList<>();
		List<Gstr3bExcelEntity> duplicateErrorRecords = new ArrayList<>();

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
		Map<String, List<ProcessingResult>> infoWithProcessed = new HashMap<>();
		List<String> errorKeys = new ArrayList<>();
		List<String> infoKeys = new ArrayList<>();
		List<String> errorInfo = new ArrayList<>();

		List<ProcessingResult> current = null;

		for (Gstr3bExcelEntity interest : strucProcessedRecords) {
			List<ProcessingResult> results = gstr3bBusinessValidationChain
					.validate(interest, null);

			if (results != null && results.size() > 0) {
				String invKey = interest.getInvKey();
				Long id = interest.getId();
				String keys = invKey.concat(GSTConstants.SLASH)
						.concat(id.toString());
				List<ProcessingResultType> listTypes = new ArrayList<>();
				for (ProcessingResult types : results) {
					ProcessingResultType type2 = types.getType();
					listTypes.add(type2);
				}

				List<String> errorType = listTypes.stream()
						.map(object -> Objects.toString(object, null))
						.collect(Collectors.toList());

				current = businessValErrors.get(keys);
				if (current == null) {
					current = new ArrayList<>();
					if (errorType.size() > 0) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							errorInfo.add(invKey);
							errorKeys.add(invKey);
							businessValErrors.put(keys, results);
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(invKey);
							businessValErrors.put(keys, results);
						} else {
							infoKeys.add(invKey);
							infoWithProcessed
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						}

					}
				} else {
					if (errorType.size() > 0) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							errorInfo.add(invKey);
							errorKeys.add(invKey);
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(invKey);
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						} else {
							infoKeys.add(invKey);
							infoWithProcessed
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						}
					}
				}
			}
		}

		List<String> busiProcessedKeys = new ArrayList<>();

		for (Gstr3bExcelEntity process : strucProcessedRecords) {
			String key = process.getInvKey();
			if (!errorKeys.contains(key)) {
				busiProcessedKeys.add(key);
			}
		}
		if (errorInfo.size() > 0 && !errorInfo.isEmpty()) {
			gstr3bExcelRepository.Gstr3bErrorUpdateInfo(errorInfo, ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			gstr3bExcelRepository.Gstr3bUpdateErrors(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			gstr3bExcelRepository.Gstr3bUpdateInfo(infoKeys, ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			businessErrorRecords = gstr3bExcelRepository
					.getAllExcelData(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			infoProcessed = gstr3bExcelRepository
					.getAllProcessedWithInfoData(infoKeys, ids);
		}
		if (busiProcessedKeys.size() > 0 && !busiProcessedKeys.isEmpty()) {
			businessProcessedRecords = gstr3bExcelRepository
					.getAllExcelData(busiProcessedKeys, ids);
		}

		LOGGER.error("businessErrorRecords " + businessErrorRecords.size());
		LOGGER.error(
				"businessProcessedRecords " + businessProcessedRecords.size());
		if (businessValErrors.size() > 0 && !businessValErrors.isEmpty()) {
			Map<String, List<Gstr3bVerticalWebError>> errorMap = gstr3BWebUploadErrorService
					.convertErrors(businessValErrors, BUSINESS_VALIDATIONS,
							updateFileStatus);
			gstr3BWebUploadErrorService
					.storedGstr3bErrorRecords(businessErrorRecords, errorMap);
		}

		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			Map<String, List<Gstr3bVerticalWebError>> errorMap = gstr3BWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);
			gstr3BWebUploadErrorService.storedGstr3bErrorRecords(infoProcessed,
					errorMap);
		}
		List<Gstr3bEntity> withoutDuplicateProcess = new ArrayList<>();
		List<Gstr3bEntity> withDuplicateProcess = new ArrayList<>();

		if (businessProcessedRecords.size() > 0
				&& !businessProcessedRecords.isEmpty()) {
			List<Gstr3bEntity> processedOfBusi = sRFileToGstr3BConvertion
					.convertSRFileToGstr3b(businessProcessedRecords,
							updateFileStatus);

			List<String> duplicateKeys = new ArrayList<>();
			List<String> nonDuplicateKeys = new ArrayList<>();
			Map<String, Gstr3bEntity> map = new HashMap<>();
			for (Gstr3bEntity gstr3bSameRecords : processedOfBusi) {
				String key = gstr3bSameRecords.getInvKey();
				if (!map.containsKey(key)) {
					map.put(key, gstr3bSameRecords);
					nonDuplicateKeys.add(key);
					continue;
				} else {
					duplicateKeys.add(key);
				}
			}
			for (Gstr3bEntity gstr3bDupCheck : processedOfBusi) {
				if (duplicateKeys.contains(gstr3bDupCheck.getInvKey())) {
					withDuplicateProcess.add(gstr3bDupCheck);
				} else {
					withoutDuplicateProcess.add(gstr3bDupCheck);
				}
			}
			if (withDuplicateProcess != null
					&& !withDuplicateProcess.isEmpty()) {
				gstr3bExcelRepository.Gstr3bUpdateErrors(duplicateKeys, ids);
				duplicateErrorRecords = gstr3bExcelRepository
						.getAllExcelData(duplicateKeys, ids);
				gstr3BWebUploadErrorService.storedGstr3bDuplicateRecords(
						duplicateErrorRecords, updateFileStatus);
			}
			for (Gstr3bEntity Gstr3bUpdate : withoutDuplicateProcess) {
				String interestInvKey = Gstr3bUpdate.getInvKey();
				gstr3BRepository.UpdateSameInvKey(interestInvKey);
			}

			if (withoutDuplicateProcess != null
					&& !withoutDuplicateProcess.isEmpty()) {
				List<Gstr3bEntity> updatedData = new ArrayList<>();
				for (Gstr3bEntity Gstr3bUpdate : withoutDuplicateProcess) {
					if (Gstr3bUpdate.getSerialNo() != null && Gstr3bUpdate.getRetPeriod() != null) {
						if (Gstr3bUpdate.getSerialNo() == 7) {
							DateTimeFormatter formatter = DateTimeFormatter
									.ofPattern("ddMMyyyy");
							LocalDate newDate = LocalDate.of(2022, 8, 01);
							String tax = "01"
									+ Gstr3bUpdate.getRetPeriod().trim();
							LocalDate returnPeriod = LocalDate.parse(tax,
									formatter);
							if (!(returnPeriod.compareTo(newDate) < 0)) {
								updatedData.add(addEntity(Gstr3bUpdate));
							}
						}
					}
					updatedData.add(Gstr3bUpdate);
				}
				gstr3BRepository.saveAll(updatedData);

				// saving data process table to User Input Table
				/**
				 * @author Balakrishna.S Start Code
				 */

				/*
				 * Set<String> gstinSet = new HashSet<>(); Set<String>
				 * taxPeriodSet = new HashSet<>(); Set<String> tableSectionSet =
				 * new HashSet<>();
				 * 
				 * withoutDuplicateProcess.forEach(doc ->
				 * gstinSet.add(doc.getGstin()));
				 * withoutDuplicateProcess.forEach(doc ->
				 * taxPeriodSet.add(doc.getRetPeriod()));
				 * withoutDuplicateProcess.forEach(doc ->
				 * tableSectionSet.add(doc.getTableSection()));
				 */
				List<Gstr3bEntity> loadBasicSummarySection = basicUserUpload
						.loadBasicSummarySection(/*
													 * gstinSet, taxPeriodSet,
													 * tableSectionSet
													 */);

				List<Gstr3BGstinAspUserInputEntity> gstr3bConverter = gstr3bConverter(
						loadBasicSummarySection);

				gstr3bConverter.forEach(doc ->

				gstr3bUserRepo.updateActiveITCFlag(doc.getTaxPeriod(),
						doc.getGstin(), doc.getSectionName()));

				gstr3bUserRepo.saveAll(gstr3bConverter);

				/**
				 * @author Balakrishna.S End Code
				 */
			}

		}

		totalRecords = (gstr3bList.size() != 0) ? gstr3bList.size() : 0;
		errorRecords = (businessErrorRecords.size() != 0
				|| strucErrorRecords.size() != 0
				|| duplicateErrorRecords.size() != 0)
						? businessErrorRecords.size() + strucErrorRecords.size()
								+ duplicateErrorRecords.size()
						: 0;
		processedRecords = totalRecords - errorRecords;
		information = (infoProcessed.size() != 0) ? infoProcessed.size() : 0;

		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		gstr1FileStatusRepository.save(updateFileStatus);

	}

	/**
	 * this method is used for Converting from Gstr3bEntity to
	 * Gstr3BGstinAspUserInputEntity
	 * 
	 * @author Balakrishna.S
	 * @param list
	 * @return
	 */
	public List<Gstr3BGstinAspUserInputEntity> gstr3bConverter(
			List<Gstr3bEntity> list) {

		List<Gstr3BGstinAspUserInputEntity> gstr3bList = new ArrayList<>();

		// Gstr3BGstinAspUserInputEntity userInput = new
		// Gstr3BGstinAspUserInputEntity();

		for (Gstr3bEntity dto : list) {

			Gstr3BGstinAspUserInputEntity userInput = new Gstr3BGstinAspUserInputEntity();
			userInput.setCess(dto.getCessAmnt() != null ? dto.getCessAmnt()
					: BigDecimal.ZERO);
			userInput.setCgst(dto.getCgstAmnt() != null ? dto.getCgstAmnt()
					: BigDecimal.ZERO);
			// userInput.setCreateDate(dto.getCreatedOn());
			// userInput.setCreatedBy(dto.getCreatedBy());
			userInput.setGstin(dto.getGstin().toUpperCase());
			userInput.setIgst(dto.getIgstAmnt() != null ? dto.getIgstAmnt()
					: BigDecimal.ZERO);
			// userInput.setInputId(dto.getId());
			userInput.setSgst(dto.getSgstAmnt() != null ? dto.getSgstAmnt()
					: BigDecimal.ZERO);
			userInput.setTaxableVal(BigDecimal.ZERO);
			userInput.setSectionName(dto.getTableSection());
			// userInput.setTaxableVal(dto.get);
			userInput.setTaxPeriod(dto.getRetPeriod());
			userInput.setSubSectionName(dto.getTableDescription());
			// userInput.setInterState(dto.geti);
			// userInput.setInterState(interState);
			// userInput.setPos();
			userInput.setIsActive(true);
			userInput.setIsITCActive(true);

			gstr3bList.add(userInput);
			if (dto.getTableSection().equalsIgnoreCase("4(a)(5)(5.2.a)")) {
				Gstr3BGstinAspUserInputEntity itcUserInput = new Gstr3BGstinAspUserInputEntity();
				itcUserInput.setCess(dto.getCessAmnt());
				itcUserInput.setCgst(dto.getCgstAmnt());
				itcUserInput.setGstin(dto.getGstin().toUpperCase());
				itcUserInput.setIgst(dto.getIgstAmnt());
				itcUserInput.setSgst(dto.getSgstAmnt());
				itcUserInput.setTaxableVal(BigDecimal.ZERO);
				itcUserInput.setSectionName("4(a)(5)");
				itcUserInput.setTaxPeriod(dto.getRetPeriod());
				itcUserInput.setSubSectionName("AO_ITC");
				itcUserInput.setIsActive(true);
				itcUserInput.setIsITCActive(true);
				gstr3bList.add(itcUserInput);
			}
			if (dto.getTableSection().equalsIgnoreCase("4(b)(1)(1.1)")) {
				Gstr3BGstinAspUserInputEntity itcUserInput = new Gstr3BGstinAspUserInputEntity();
				itcUserInput.setCess(dto.getCessAmnt());
				itcUserInput.setCgst(dto.getCgstAmnt());
				itcUserInput.setGstin(dto.getGstin().toUpperCase());
				itcUserInput.setIgst(dto.getIgstAmnt());
				itcUserInput.setSgst(dto.getSgstAmnt());
				itcUserInput.setTaxableVal(BigDecimal.ZERO);
				itcUserInput.setSectionName("4(b)(1)");
				itcUserInput.setTaxPeriod(dto.getRetPeriod());
				itcUserInput.setSubSectionName("AP42&43");
				itcUserInput.setIsActive(true);
				itcUserInput.setIsITCActive(true);
				gstr3bList.add(itcUserInput);
			}
			String tableSection = dto.getTableSection();

			LOGGER.debug(
					"Before dto.getTableSection().equalsIgnoreCase 4(b)(2)(2.1) {} ",
					tableSection);
			if (dto.getTableSection().equalsIgnoreCase("4(b)(2)(2.1)")) {
				LOGGER.debug(
						"After dto.getTableSection().equalsIgnoreCase 4(b)(2)(2.1) {} ",
						tableSection);
				Gstr3BGstinAspUserInputEntity itcUserInput = new Gstr3BGstinAspUserInputEntity();
				itcUserInput.setCess(dto.getCessAmnt());
				itcUserInput.setCgst(dto.getCgstAmnt());
				itcUserInput.setGstin(dto.getGstin().toUpperCase());
				itcUserInput.setIgst(dto.getIgstAmnt());
				itcUserInput.setSgst(dto.getSgstAmnt());
				itcUserInput.setTaxableVal(BigDecimal.ZERO);
				itcUserInput.setSectionName("4(b)(2)");
				itcUserInput.setTaxPeriod(dto.getRetPeriod());
				itcUserInput.setSubSectionName("OR_RFU");
				itcUserInput.setIsActive(true);
				itcUserInput.setIsITCActive(true);
				LOGGER.debug(
						"before end dto.getTableSection().equalsIgnoreCase 4(b)(2)(b) {} ",
						itcUserInput.toString());
				gstr3bList.add(itcUserInput);
				LOGGER.debug(
						"At end dto.getTableSection().equalsIgnoreCase 4(b)(2)(b) {} ",
						itcUserInput.toString());
			}
		}
		LOGGER.debug("before return {} ", gstr3bList.toString());
		return gstr3bList;

	}

	public Gstr3bEntity addEntity(Gstr3bEntity Gstr3bUpdate){
		Gstr3bEntity entity = new Gstr3bEntity();
		entity.setCessAmnt(Gstr3bUpdate.getCessAmnt());
		entity.setCgstAmnt(Gstr3bUpdate.getCgstAmnt());
		entity.setSerialNo(Gstr3bUpdate.getSerialNo());
		entity.setGstin(Gstr3bUpdate.getGstin());
		entity.setRetPeriod(Gstr3bUpdate.getRetPeriod());
		entity.setDescription(
				Gstr3bUpdate.getDescription());
		entity.setRetPeriod(Gstr3bUpdate.getRetPeriod());
		entity.setIgstAmnt(Gstr3bUpdate.getIgstAmnt());
		entity.setSgstAmnt(Gstr3bUpdate.getSgstAmnt());
		entity.setCreatedBy(Gstr3bUpdate.getCreatedBy());
		entity.setCreatedOn(Gstr3bUpdate.getCreatedOn());
		entity.setFileId(Gstr3bUpdate.getFileId());
		entity.setInvKey(Gstr3bUpdate.getInvKey());
		entity.setModifiedBy(Gstr3bUpdate.getModifiedBy());
		entity.setModifiedOn(Gstr3bUpdate.getModifiedOn());
		entity.setTableDescription(
				Gstr3bUpdate.getTableDescription());
		entity.setAsEnteredId(Gstr3bUpdate.getAsEnteredId());
		entity.setDerivedRetPeriod(Gstr3bUpdate.getDerivedRetPeriod());
		entity.setTableSection("4(d)(1)");
		return entity;	
	}
}
