package com.ey.advisory.app.data.services.anx1;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.app.data.daos.client.Gstr1NilExmpNonGstFetchDaoImpl;
import com.ey.advisory.app.data.entities.client.Gstr1NilDetailsEntity;
import com.ey.advisory.app.data.entities.client.Gstr1NilNonExmptSummaryEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilDetailsEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilNonExmptSummaryEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AUserInputNilExtnOnEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilNonExtSummaryRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1NilNonExtSummaryRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1NilRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1UserInputNilExtnOnEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1UserInputNilExtnOnRepository;
import com.ey.advisory.app.data.services.Gstr1A.Gstr1ANilExmpNonGstFetchDaoImpl;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstSaveReqDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstStatusRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstSummaryStatusRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstVerticalStatusRespDto;
import com.ey.advisory.app.services.validation.b2cs.ErrorDescriptionDto;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.google.common.collect.Lists;

@Component("Gstr1NilExmpNonGstStautsService")
public class Gstr1NilExmpNonGstStautsService {

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	@Qualifier("gstr1UserInputNilExtnOnRepository")
	private Gstr1UserInputNilExtnOnRepository repository;

	@Autowired
	@Qualifier("Gstr1NilExmpNonGstFetchDaoImpl")
	private Gstr1NilExmpNonGstFetchDaoImpl gstr1NilExmpNonGstFetchDaoImpl;

	@Autowired
	@Qualifier("Gstr1ANilExmpNonGstFetchDaoImpl")
	private Gstr1ANilExmpNonGstFetchDaoImpl gstr1ANilExmpNonGstFetchDaoImpl;

	@Autowired
	@Qualifier("Gstr1NilNonExtSummaryRepository")
	Gstr1NilNonExtSummaryRepository gstr1NilNonExtSummaryRepository;

	@Autowired
	@Qualifier("Gstr1ANilRepository")
	Gstr1ANilRepository gstr1ANilRepository;

	@Autowired
	@Qualifier("Gstr1ANilNonExtSummaryRepository")
	Gstr1ANilNonExtSummaryRepository gstr1ANilNonExtSummaryRepository;

	@Autowired
	@Qualifier("Gstr1NilRepository")
	Gstr1NilRepository gstr1NilRepository;

	@Autowired
	@Qualifier("GstnApi")
	GstnApi gstnApi;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1NilExmpNonGstStautsService.class);

	public List<Gstr1NilExmpNonGstStatusRespDto> findNilExmpNonGstStauts(
			Gstr1ProcessedRecordsReqDto criteria) {
		Gstr1ProcessedRecordsReqDto req = (Gstr1ProcessedRecordsReqDto) criteria;
		Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr1DataSecuritySearchParams(req);

		List<Gstr1NilExmpNonGstStatusRespDto> respDtos = new ArrayList<>();

		if (reqDto.getReturnType() != null && APIConstants.GSTR1A
				.equalsIgnoreCase(reqDto.getReturnType())) {
			respDtos = gstr1ANilExmpNonGstFetchDaoImpl
					.loadNillExmpNonGstRecords(reqDto);

		} else {

			respDtos = gstr1NilExmpNonGstFetchDaoImpl
					.loadNillExmpNonGstRecords(reqDto);

		}
		if (CollectionUtils.isEmpty(respDtos)) {
			return buildDefaultData(respDtos);
		}
		return respDtos;
	}

	private List<Gstr1NilExmpNonGstStatusRespDto> buildDefaultData(
			List<Gstr1NilExmpNonGstStatusRespDto> respDtos) {
		List<Gstr1NilExmpNonGstStatusRespDto> dtos = Lists.newArrayList();
		if (org.apache.commons.collections.CollectionUtils
				.isNotEmpty(respDtos)) {
			respDtos.forEach(dto -> {
				Gstr1NilExmpNonGstStatusRespDto respDto = new Gstr1NilExmpNonGstStatusRespDto();
				respDto.setId(dto.getId());
				respDto.setDesc(dto.getDesc());
				respDto.setAspNilRated(BigDecimal.ZERO);
				respDto.setAspExempted(BigDecimal.ZERO);
				respDto.setAspNonGst(BigDecimal.ZERO);
				respDto.setUsrNilRated(BigDecimal.ZERO);
				respDto.setUsrExempted(BigDecimal.ZERO);
				respDto.setUsrNonGst(BigDecimal.ZERO);
			});
		}
		return dtos;
	}

	public void saveNilExmpNonGstStauts(
			List<Gstr1NilExmpNonGstSaveReqDto> reqDtos) {
		List<Gstr1UserInputNilExtnOnEntity> entites = Lists.newArrayList();
		if (org.apache.commons.collections.CollectionUtils
				.isNotEmpty(reqDtos)) {
			reqDtos.forEach(dto -> {
				Gstr1UserInputNilExtnOnEntity entity = new Gstr1UserInputNilExtnOnEntity();
				String date = dto.getTaxPeriod();
				entity.setReturnPeriod(dto.getTaxPeriod());
				int taxPeriod = GenUtil.convertTaxPeriodToInt(date);
				entity.setDerivedRetPeriod(taxPeriod);
				String gstin = dto.getGstin();
				entity.setSupplierGstin(gstin);
				entity.setDescription(dto.getDesc());
				entity.setDescriptioKey(dto.getId().toString());
				entity.setDocKey(dto.getDocKey());
				entity.setNilRatedSupplies(dto.getUsrNilRated());
				entity.setExmptedSupplies(dto.getUsrExempted());
				entity.setNonGstSupplies(dto.getUsrNonGst());
				entity.setCreatedBy("SYSTEM");
				entity.setCreatedOn(LocalDateTime.now());
				entity.setModifiedBy("SYSTEM");
				entity.setModifiedOn(LocalDateTime.now());
				entity.setDelete(false);
				entites.add(entity);
			});
		}
		gstr1NilExmpNonGstFetchDaoImpl.saveNilExmpNonGstStauts(entites);

	}

	public List<Gstr1NilExmpNonGstSummaryStatusRespDto> findNilExmpNonGstSummaryStauts(
			Gstr1ProcessedRecordsReqDto reqDto) {
		Gstr1ProcessedRecordsReqDto req = (Gstr1ProcessedRecordsReqDto) reqDto;
		Gstr1ProcessedRecordsReqDto summaryDto = processedRecordsCommonSecParam
				.setGstr1DataSecuritySearchParams(req);

		List<Gstr1NilExmpNonGstSummaryStatusRespDto> respDtos = new ArrayList<>();

		if (reqDto.getReturnType() != null && APIConstants.GSTR1A
				.equalsIgnoreCase(reqDto.getReturnType())) {
			respDtos = gstr1ANilExmpNonGstFetchDaoImpl
					.loadNillExmpNonGstSummaryRecords(summaryDto);

		} else {
			respDtos = gstr1NilExmpNonGstFetchDaoImpl
					.loadNillExmpNonGstSummaryRecords(summaryDto);
		}
		return respDtos;
	}

	public List<Gstr1NilExmpNonGstVerticalStatusRespDto> findNilExmpNonGstVerticalStauts(
			Gstr1ProcessedRecordsReqDto reqDto) {
		Gstr1ProcessedRecordsReqDto req = (Gstr1ProcessedRecordsReqDto) reqDto;
		Gstr1ProcessedRecordsReqDto summaryDto = processedRecordsCommonSecParam
				.setGstr1DataSecuritySearchParams(req);
		List<Gstr1NilExmpNonGstVerticalStatusRespDto> respDtos = new ArrayList<>();

		if (reqDto.getReturnType() != null && APIConstants.GSTR1A
				.equalsIgnoreCase(reqDto.getReturnType())) {
			respDtos = gstr1ANilExmpNonGstFetchDaoImpl
					.loadNillExmpNonGstVerticalRecords(summaryDto);

		} else

		{
			respDtos = gstr1NilExmpNonGstFetchDaoImpl
					.loadNillExmpNonGstVerticalRecords(summaryDto);

		}
		if (CollectionUtils.isEmpty(respDtos)) {
			return buildVerticalDefaultData(respDtos);
		}
		return respDtos;
	}

	private List<Gstr1NilExmpNonGstVerticalStatusRespDto> buildVerticalDefaultData(
			List<Gstr1NilExmpNonGstVerticalStatusRespDto> respDtos) {
		List<Gstr1NilExmpNonGstVerticalStatusRespDto> dtos = Lists
				.newArrayList();
		Gstr1NilExmpNonGstVerticalStatusRespDto respDto = new Gstr1NilExmpNonGstVerticalStatusRespDto();

		respDto.setHsn("");
		respDto.setDesc("");
		respDto.setUqc("");
		respDto.setQunty("");
		respDto.setNilInterReg(BigDecimal.ZERO);
		respDto.setNilIntraReg(BigDecimal.ZERO);
		respDto.setNilInterUnreg(BigDecimal.ZERO);
		respDto.setNilIntraUnreg(BigDecimal.ZERO);
		respDto.setExtInterReg(BigDecimal.ZERO);
		respDto.setExtIntraReg(BigDecimal.ZERO);
		respDto.setExtInterUnreg(BigDecimal.ZERO);
		respDto.setExtIntraUnreg(BigDecimal.ZERO);
		respDto.setNonInterReg(BigDecimal.ZERO);
		respDto.setNonIntraReg(BigDecimal.ZERO);
		respDto.setNonInterUnreg(BigDecimal.ZERO);
		respDto.setNonIntraUnreg(BigDecimal.ZERO);
		dtos.add(respDto);
		return dtos;
	}

	public void saveNilExmpNonVerticalStauts(
			List<Gstr1NilExmpNonGstVerticalStatusRespDto> reqDtos) {

		for (Gstr1NilExmpNonGstVerticalStatusRespDto reqDto : reqDtos) {
			List<ErrorDescriptionDto> errorList = new ArrayList<>();

			// Validation logic
			if (reqDto.getHsn() != null && !reqDto.getHsn().isEmpty()) {
				if (GSTConstants.NA.equalsIgnoreCase(reqDto.getUqc())
						&& !reqDto.getHsn().startsWith("99")) {

					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.UQC);

					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());

					ProcessingResult pr = new ProcessingResult(APP_VALIDATION,
							"ER5706", "Invalid UQC.", location);

					ErrorDescriptionDto errorDto = new ErrorDescriptionDto();
					errorDto.setErrorCode(pr.getCode());
					errorDto.setErrorDesc(pr.getDescription());
					errorDto.setErrorType(pr.getType().toString());

					TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) pr
							.getLocation();
					if (loc != null) {
						Object[] arr = loc.getFieldIdentifiers();
						String[] fields = Arrays.copyOf(arr, arr.length,
								String[].class);
						String errField = String.join(",", fields);
						errorDto.setErrorField(errField);
					}

					errorList.add(errorDto);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("errorList {}", errorList);
					}
				}
			}

			if (!errorList.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Inside !errorList.isEmpty() {}", errorList);
				}
				reqDto.setErrorList(errorList);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Final reqDto {}", reqDto);
				}

			} else {
				// collect DTOs without errors for persistence

				List<Gstr1NilDetailsEntity> entites = Lists.newArrayList();
				if (org.apache.commons.collections.CollectionUtils
						.isNotEmpty(reqDtos)) {
					reqDtos.forEach(dto -> {

						Gstr1NilDetailsEntity entity = new Gstr1NilDetailsEntity();
						entity.setReturnPeriod(dto.getTaxPeriod());
						entity.setDerivedRetPeriod(GenUtil
								.convertTaxPeriodToInt(dto.getTaxPeriod()));
						entity.setSgstin(dto.getGstin());
						entity.setId((dto.getId() != null
								&& Long.parseLong(dto.getId()) != 0L)
										? Long.parseLong(dto.getId())
										: 0);

						String quntyy = dto.getQunty();
						BigDecimal bigDecimalQunty = new BigDecimal(quntyy);

						Boolean naConsideredAsUqcValueInHsn = gstnApi
								.isNAConsideredAsUqcValueInHsn(
										dto.getTaxPeriod());
						if (naConsideredAsUqcValueInHsn) {

							/*
							 * if("99".equalsIgnoreCase(dto.getHsn().substring(
							 * 0, 2)) || "OTH".equalsIgnoreCase(dto.getUqc()) ||
							 * com.google.common.base.Strings.isNullOrEmpty(dto.
							 * getUqc())){
							 * 
							 * entity.setUqc("NA");
							 * entity.setQnt(BigDecimal.ZERO);
							 * 
							 * }else{
							 * 
							 */ entity.setUqc(dto.getUqc());
							entity.setQnt(bigDecimalQunty);
							// }
						}

						entity.setHsn(dto.getHsn());
						entity.setDescription(dto.getDesc());

						entity.setNilInterReg(dto.getNilInterReg());
						entity.setNilIntraReg(dto.getNilIntraReg());
						entity.setNilInterUnReg(dto.getNilInterUnreg());
						entity.setNilIntraUnReg(dto.getNilIntraUnreg());
						entity.setExtInterReg(dto.getExtInterReg());
						entity.setExtIntraReg(dto.getExtIntraReg());
						entity.setExtInterUnReg(dto.getExtInterUnreg());
						entity.setExtIntraUnReg(dto.getExtIntraUnreg());
						entity.setNonInterReg(dto.getNonInterReg());
						entity.setNonIntraReg(dto.getNonIntraReg());
						entity.setNonInterUnReg(dto.getNonInterUnreg());
						entity.setNonIntraUnReg(dto.getNonIntraUnreg());
						entity.setCreatedBy("SYSTEM");
						entity.setCreatedOn(LocalDateTime.now());
						entity.setModifiedBy("SYSTEM");
						entity.setModifiedOn(LocalDateTime.now());
						entity.setDelete(false);
						entity.setNKey(dto.getDocKey());
						entites.add(entity);
					});
				}
				if (org.apache.commons.collections.CollectionUtils
						.isNotEmpty(entites)) {
					gstr1NilExmpNonGstFetchDaoImpl
							.saveNilExmpNonVerticalStauts(entites);
					saveNilExmpNonVerticaltoSummaryStauts(entites);
				}
			}
		}
	}

	private void saveNilExmpNonVerticaltoSummaryStauts(
			List<Gstr1NilDetailsEntity> entitesLists) {
		LOGGER.error("Entities Size {} ", entitesLists.size());
		entitesLists.forEach(entity -> {
			Long entityId = entity.getId();
			LOGGER.error("Entities Id {} ", entityId);
			if (entityId != null && entityId != 0) {
				List<Gstr1NilNonExmptSummaryEntity> entities = gstr1NilNonExtSummaryRepository
						.findByProcessId(entity.getId());
				LOGGER.error("Entities {}", entities);
				LOGGER.error(
						"org.apache.commons.collections.CollectionUtils\r\n"
								+ "						.isNotEmpty(entities) {} ",
						org.apache.commons.collections.CollectionUtils
								.isNotEmpty(entities));
				if (org.apache.commons.collections.CollectionUtils
						.isNotEmpty(entities)) {
					LOGGER.error("Entities are not empty");
					Map<String, List<Gstr1NilNonExmptSummaryEntity>> suppliTypes = entities
							.stream().collect(Collectors.groupingBy(
									Gstr1NilNonExmptSummaryEntity::getSupplType));

					suppliTypes.keySet().forEach(supType -> {
						List<Gstr1NilNonExmptSummaryEntity> typeList = suppliTypes
								.get(supType);
						typeList.forEach(dbEntity -> {
							String tabSection = dbEntity.getTableSection();
							if (supType.equals("NIL")) {
								switch (tabSection) {
								case "8A": {
									dbEntity.setTaxableValue(
											entity.getNilInterReg());
									break;
								}
								case "8B": {
									dbEntity.setTaxableValue(
											entity.getNilIntraReg());
									break;
								}
								case "8C": {
									dbEntity.setTaxableValue(
											entity.getNilInterUnReg());
									break;
								}
								case "8D": {
									dbEntity.setTaxableValue(
											entity.getNilIntraUnReg());
									break;
								}
								}
							} else if (supType.equals("NON")) {
								switch (tabSection) {
								case "8A": {
									dbEntity.setTaxableValue(
											entity.getNonInterReg());
									break;
								}
								case "8B": {
									dbEntity.setTaxableValue(
											entity.getNonIntraReg());
									break;
								}
								case "8C": {
									dbEntity.setTaxableValue(
											entity.getNonInterUnReg());
									break;
								}
								case "8D": {
									dbEntity.setTaxableValue(
											entity.getNonIntraUnReg());
									break;
								}
								}

							} else if (supType.equals("EXT")) {
								switch (tabSection) {
								case "8A": {
									dbEntity.setTaxableValue(
											entity.getExtInterReg());
									break;
								}
								case "8B": {
									dbEntity.setTaxableValue(
											entity.getExtIntraReg());
									break;
								}
								case "8C": {
									dbEntity.setTaxableValue(
											entity.getExtInterUnReg());
									break;
								}
								case "8D": {
									dbEntity.setTaxableValue(
											entity.getExtIntraUnReg());
									break;
								}
								}
							}
							dbEntity.setHsn(entity.getHsn());
							dbEntity.setSgstin(entity.getSgstin());
							dbEntity.setDescription(entity.getDescription());
							dbEntity.setUqc(entity.getUqc());
							dbEntity.setQnt(entity.getQnt());
							dbEntity.setCreatedBy("SYSTEM");
							dbEntity.setCreatedOn(LocalDateTime.now());
							dbEntity.setModifiedBy("SYSTEM");
							dbEntity.setModifiedOn(LocalDateTime.now());
							dbEntity.setDelete(false);
							dbEntity.setNKey(entity.getNKey());

							gstr1NilNonExtSummaryRepository.save(dbEntity);
						});

					});

				} else {
					LOGGER.error("Entities are empty");
					long dbEntityId = gstr1NilRepository.findByNKey(
							entity.getNKey(), entity.getDerivedRetPeriod(),
							entity.getSgstin());
					LOGGER.error("Long {} ", dbEntityId);
					gstr1NilNonExtSummaryRepository
							.saveAll(buildTheVerticalData(entity, dbEntityId));
				}
			}
		});
	}

	private List<Gstr1NilNonExmptSummaryEntity> buildTheVerticalData(
			Gstr1NilDetailsEntity dto, long id) {
		List<Gstr1NilNonExmptSummaryEntity> entites = Lists.newArrayList();
		List<String> sectionsList = Lists.newArrayList("8A", "8B", "8C", "8D");

		sectionsList.forEach(section -> {
			Gstr1NilNonExmptSummaryEntity entity = buildSectionObjects(dto, id);
			entity.setTableSection(section);
			entity.setTaxableValue(section.equals("8A") ? dto.getNilInterReg()
					: section.equals("8B") ? dto.getNilIntraReg()
							: section.equals("8C") ? dto.getNilInterUnReg()
									: section.equals("8D")
											? dto.getNilIntraUnReg()
											: BigDecimal.ZERO);
			entity.setSupplType("NIL");
			entites.add(entity);
		});

		sectionsList.forEach(section -> {
			Gstr1NilNonExmptSummaryEntity entity = buildSectionObjects(dto, id);
			entity.setTableSection(section);
			entity.setTaxableValue(section.equals("8A") ? dto.getNonInterReg()
					: section.equals("8B") ? dto.getNonIntraReg()
							: section.equals("8C") ? dto.getNonInterUnReg()
									: section.equals("8D")
											? dto.getNonIntraUnReg()
											: BigDecimal.ZERO);
			entity.setSupplType("NON");
			entites.add(entity);
		});

		sectionsList.forEach(section -> {
			Gstr1NilNonExmptSummaryEntity entity = buildSectionObjects(dto, id);
			entity.setTableSection(section);
			entity.setTaxableValue(section.equals("8A") ? dto.getExtInterReg()
					: section.equals("8B") ? dto.getExtIntraReg()
							: section.equals("8C") ? dto.getExtInterUnReg()
									: section.equals("8D")
											? dto.getExtIntraUnReg()
											: BigDecimal.ZERO);
			entity.setSupplType("EXT");
			entites.add(entity);
		});
		LOGGER.error("entities {} ", entites);
		return entites;
	}

	private Gstr1NilNonExmptSummaryEntity buildSectionObjects(
			Gstr1NilDetailsEntity dto, long id) {
		Gstr1NilNonExmptSummaryEntity entity = new Gstr1NilNonExmptSummaryEntity();
		entity.setReturnPeriod(dto.getReturnPeriod());
		entity.setDerivedRetPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		entity.setSgstin(dto.getSgstin());
		entity.setHsn(dto.getHsn());
		entity.setDescription(dto.getDescription());
		entity.setUqc(dto.getUqc());
		BigDecimal bigDecimalQunty = dto.getQnt();
		entity.setQnt(bigDecimalQunty);
		entity.setCreatedBy("SYSTEM");
		entity.setCreatedOn(LocalDateTime.now());
		entity.setModifiedBy("SYSTEM");
		entity.setModifiedOn(LocalDateTime.now());
		entity.setDelete(false);
		entity.setAsProcessedId(id);
		entity.setNKey(dto.getNKey());
		return entity;

	}

	public void saveGstr1ANilExmpNonGstStauts(
			List<Gstr1NilExmpNonGstSaveReqDto> reqDtos) {
		List<Gstr1AUserInputNilExtnOnEntity> entites = Lists.newArrayList();
		if (org.apache.commons.collections.CollectionUtils
				.isNotEmpty(reqDtos)) {
			reqDtos.forEach(dto -> {
				Gstr1AUserInputNilExtnOnEntity entity = new Gstr1AUserInputNilExtnOnEntity();
				String date = dto.getTaxPeriod();
				entity.setReturnPeriod(dto.getTaxPeriod());
				int taxPeriod = GenUtil.convertTaxPeriodToInt(date);
				entity.setDerivedRetPeriod(taxPeriod);
				String gstin = dto.getGstin();
				entity.setSupplierGstin(gstin);
				entity.setDescription(dto.getDesc());
				entity.setDescriptioKey(dto.getId().toString());
				entity.setDocKey(dto.getDocKey());
				entity.setNilRatedSupplies(dto.getUsrNilRated());
				entity.setExmptedSupplies(dto.getUsrExempted());
				entity.setNonGstSupplies(dto.getUsrNonGst());
				entity.setCreatedBy("SYSTEM");
				entity.setCreatedOn(LocalDateTime.now());
				entity.setModifiedBy("SYSTEM");
				entity.setModifiedOn(LocalDateTime.now());
				entity.setDelete(false);
				entites.add(entity);
			});
		}
		gstr1ANilExmpNonGstFetchDaoImpl.saveNilExmpNonGstStauts(entites);

	}

	public void saveGstr1ANilExmpNonVerticalStauts(
			List<Gstr1NilExmpNonGstVerticalStatusRespDto> reqDtos) {

		for (Gstr1NilExmpNonGstVerticalStatusRespDto reqDto : reqDtos) {
			List<ErrorDescriptionDto> errorList = new ArrayList<>();

			// Validation logic
			if (reqDto.getHsn() != null && !reqDto.getHsn().isEmpty()) {
				if (GSTConstants.NA.equalsIgnoreCase(reqDto.getUqc())
						&& !reqDto.getHsn().startsWith("99")) {

					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.UQC);

					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());

					ProcessingResult pr = new ProcessingResult(APP_VALIDATION,
							"ER5706", "Invalid UQC.", location);

					ErrorDescriptionDto errorDto = new ErrorDescriptionDto();
					errorDto.setErrorCode(pr.getCode());
					errorDto.setErrorDesc(pr.getDescription());
					errorDto.setErrorType(pr.getType().toString());

					TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) pr
							.getLocation();
					if (loc != null) {
						Object[] arr = loc.getFieldIdentifiers();
						String[] fields = Arrays.copyOf(arr, arr.length,
								String[].class);
						String errField = String.join(",", fields);
						errorDto.setErrorField(errField);
					}

					errorList.add(errorDto);
				}
			}

			if (!errorList.isEmpty()) {
				reqDto.setErrorList(errorList);

			} else {
				// collect DTOs without errors for persistence

				List<Gstr1ANilDetailsEntity> entites = Lists.newArrayList();
				if (org.apache.commons.collections.CollectionUtils
						.isNotEmpty(reqDtos)) {
					reqDtos.forEach(dto -> {
						Gstr1ANilDetailsEntity entity = new Gstr1ANilDetailsEntity();
						entity.setReturnPeriod(dto.getTaxPeriod());
						entity.setDerivedRetPeriod(GenUtil
								.convertTaxPeriodToInt(dto.getTaxPeriod()));
						entity.setSgstin(dto.getGstin());
						entity.setId((dto.getId() != null
								&& Long.parseLong(dto.getId()) != 0L)
										? Long.parseLong(dto.getId())
										: 0);

						String quntyy = dto.getQunty();
						BigDecimal bigDecimalQunty = new BigDecimal(quntyy);

						Boolean naConsideredAsUqcValueInHsn = gstnApi
								.isNAConsideredAsUqcValueInHsn(
										dto.getTaxPeriod());
						if (naConsideredAsUqcValueInHsn) {

							/*
							 * if("99".equalsIgnoreCase(dto.getHsn().substring(
							 * 0, 2)) || "OTH".equalsIgnoreCase(dto.getUqc()) ||
							 * com.google.common.base.Strings.isNullOrEmpty(dto.
							 * getUqc())){
							 * 
							 * entity.setUqc("NA");
							 * entity.setQnt(BigDecimal.ZERO);
							 * 
							 * }else{
							 * 
							 */ entity.setUqc(dto.getUqc());
							entity.setQnt(bigDecimalQunty);
							// }
						}

						entity.setHsn(dto.getHsn());
						entity.setDescription(dto.getDesc());

						entity.setNilInterReg(dto.getNilInterReg());
						entity.setNilIntraReg(dto.getNilIntraReg());
						entity.setNilInterUnReg(dto.getNilInterUnreg());
						entity.setNilIntraUnReg(dto.getNilIntraUnreg());
						entity.setExtInterReg(dto.getExtInterReg());
						entity.setExtIntraReg(dto.getExtIntraReg());
						entity.setExtInterUnReg(dto.getExtInterUnreg());
						entity.setExtIntraUnReg(dto.getExtIntraUnreg());
						entity.setNonInterReg(dto.getNonInterReg());
						entity.setNonIntraReg(dto.getNonIntraReg());
						entity.setNonInterUnReg(dto.getNonInterUnreg());
						entity.setNonIntraUnReg(dto.getNonIntraUnreg());
						entity.setCreatedBy("SYSTEM");
						entity.setCreatedOn(LocalDateTime.now());
						entity.setModifiedBy("SYSTEM");
						entity.setModifiedOn(LocalDateTime.now());
						entity.setDelete(false);
						entity.setNKey(dto.getDocKey());
						entites.add(entity);
					});
				}
				if (org.apache.commons.collections.CollectionUtils
						.isNotEmpty(entites)) {
					gstr1ANilExmpNonGstFetchDaoImpl
							.saveNilExmpNonVerticalStauts(entites);
					saveGstr1ANilExmpNonVerticaltoSummaryStauts(entites);
				}
			}
		}
	}

	private void saveGstr1ANilExmpNonVerticaltoSummaryStauts(
			List<Gstr1ANilDetailsEntity> entitesLists) {
		entitesLists.forEach(entity -> {
			Long entityId = entity.getId();
			if (entityId != null && entityId != 0) {
				List<Gstr1ANilNonExmptSummaryEntity> entities = gstr1ANilNonExtSummaryRepository
						.findByProcessId(entity.getId());
				if (org.apache.commons.collections.CollectionUtils
						.isNotEmpty(entities)) {
					Map<String, List<Gstr1ANilNonExmptSummaryEntity>> suppliTypes = entities
							.stream().collect(Collectors.groupingBy(
									Gstr1ANilNonExmptSummaryEntity::getSupplType));

					suppliTypes.keySet().forEach(supType -> {
						List<Gstr1ANilNonExmptSummaryEntity> typeList = suppliTypes
								.get(supType);
						typeList.forEach(dbEntity -> {
							String tabSection = dbEntity.getTableSection();
							if (supType.equals("NIL")) {
								switch (tabSection) {
								case "8A": {
									dbEntity.setTaxableValue(
											entity.getNilInterReg());
									break;
								}
								case "8B": {
									dbEntity.setTaxableValue(
											entity.getNilIntraReg());
									break;
								}
								case "8C": {
									dbEntity.setTaxableValue(
											entity.getNilInterUnReg());
									break;
								}
								case "8D": {
									dbEntity.setTaxableValue(
											entity.getNilIntraUnReg());
									break;
								}
								}
							} else if (supType.equals("NON")) {
								switch (tabSection) {
								case "8A": {
									dbEntity.setTaxableValue(
											entity.getNonInterReg());
									break;
								}
								case "8B": {
									dbEntity.setTaxableValue(
											entity.getNonIntraReg());
									break;
								}
								case "8C": {
									dbEntity.setTaxableValue(
											entity.getNonInterUnReg());
									break;
								}
								case "8D": {
									dbEntity.setTaxableValue(
											entity.getNonIntraUnReg());
									break;
								}
								}

							} else if (supType.equals("EXT")) {
								switch (tabSection) {
								case "8A": {
									dbEntity.setTaxableValue(
											entity.getExtInterReg());
									break;
								}
								case "8B": {
									dbEntity.setTaxableValue(
											entity.getExtIntraReg());
									break;
								}
								case "8C": {
									dbEntity.setTaxableValue(
											entity.getExtInterUnReg());
									break;
								}
								case "8D": {
									dbEntity.setTaxableValue(
											entity.getExtIntraUnReg());
									break;
								}
								}
							}
							dbEntity.setHsn(entity.getHsn());
							dbEntity.setSgstin(entity.getSgstin());
							dbEntity.setDescription(entity.getDescription());
							dbEntity.setUqc(entity.getUqc());
							dbEntity.setQnt(entity.getQnt());
							dbEntity.setCreatedBy("SYSTEM");
							dbEntity.setCreatedOn(LocalDateTime.now());
							dbEntity.setModifiedBy("SYSTEM");
							dbEntity.setModifiedOn(LocalDateTime.now());
							dbEntity.setDelete(false);
							dbEntity.setNKey(entity.getNKey());

							gstr1ANilNonExtSummaryRepository.save(dbEntity);
						});

					});
				}
			} else {

				long dbEntityId = gstr1ANilRepository.findByNKey(
						entity.getNKey(), entity.getDerivedRetPeriod(),
						entity.getSgstin());
				gstr1ANilNonExtSummaryRepository.saveAll(
						buildGstr1ATheVerticalData(entity, dbEntityId));
			}

		});
	}

	private List<Gstr1ANilNonExmptSummaryEntity> buildGstr1ATheVerticalData(
			Gstr1ANilDetailsEntity dto, long id) {
		List<Gstr1ANilNonExmptSummaryEntity> entites = Lists.newArrayList();
		List<String> sectionsList = Lists.newArrayList("8A", "8B", "8C", "8D");

		sectionsList.forEach(section -> {
			Gstr1ANilNonExmptSummaryEntity entity = buildGstr1ASectionObjects(
					dto, id);
			entity.setTableSection(section);
			entity.setTaxableValue(section.equals("8A") ? dto.getNilInterReg()
					: section.equals("8B") ? dto.getNilIntraReg()
							: section.equals("8C") ? dto.getNilInterUnReg()
									: section.equals("8D")
											? dto.getNilIntraUnReg()
											: BigDecimal.ZERO);
			entity.setSupplType("NIL");
			entites.add(entity);
		});

		sectionsList.forEach(section -> {
			Gstr1ANilNonExmptSummaryEntity entity = buildGstr1ASectionObjects(
					dto, id);
			entity.setTableSection(section);
			entity.setTaxableValue(section.equals("8A") ? dto.getNonInterReg()
					: section.equals("8B") ? dto.getNonIntraReg()
							: section.equals("8C") ? dto.getNonInterUnReg()
									: section.equals("8D")
											? dto.getNonIntraUnReg()
											: BigDecimal.ZERO);
			entity.setSupplType("NON");
			entites.add(entity);
		});

		sectionsList.forEach(section -> {
			Gstr1ANilNonExmptSummaryEntity entity = buildGstr1ASectionObjects(
					dto, id);
			entity.setTableSection(section);
			entity.setTaxableValue(section.equals("8A") ? dto.getExtInterReg()
					: section.equals("8B") ? dto.getExtIntraReg()
							: section.equals("8C") ? dto.getExtInterUnReg()
									: section.equals("8D")
											? dto.getExtIntraUnReg()
											: BigDecimal.ZERO);
			entity.setSupplType("EXT");
			entites.add(entity);
		});

		return entites;
	}

	private Gstr1ANilNonExmptSummaryEntity buildGstr1ASectionObjects(
			Gstr1ANilDetailsEntity dto, long id) {
		Gstr1ANilNonExmptSummaryEntity entity = new Gstr1ANilNonExmptSummaryEntity();
		entity.setReturnPeriod(dto.getReturnPeriod());
		entity.setDerivedRetPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		entity.setSgstin(dto.getSgstin());
		entity.setHsn(dto.getHsn());
		entity.setDescription(dto.getDescription());
		entity.setUqc(dto.getUqc());
		BigDecimal bigDecimalQunty = dto.getQnt();
		entity.setQnt(bigDecimalQunty);
		entity.setCreatedBy("SYSTEM");
		entity.setCreatedOn(LocalDateTime.now());
		entity.setModifiedBy("SYSTEM");
		entity.setModifiedOn(LocalDateTime.now());
		entity.setDelete(false);
		entity.setAsProcessedId(id);
		entity.setNKey(dto.getNKey());
		return entity;

	}
}
