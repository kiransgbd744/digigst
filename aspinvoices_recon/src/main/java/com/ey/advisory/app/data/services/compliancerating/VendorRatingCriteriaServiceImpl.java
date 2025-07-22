package com.ey.advisory.app.data.services.compliancerating;

import static java.util.Comparator.comparing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.asprecon.FrequencyDataStorageStatusEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReturnDataStorageStatusEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorRatingCriteriaEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.FrequencyDataStorageStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReturnDataStorageStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorRatingCriteriaRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("VendorRatingCriteriaServiceImpl")
public class VendorRatingCriteriaServiceImpl
		implements VendorRatingCriteriaService {

	@Autowired
	private VendorRatingCriteriaRepository repo;

	@Autowired
	private VendorRatingCriteriaDefaultUtil util;

	@Autowired
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	private ReturnDataStorageStatusRepository returnDataStorageStatusRepo;

	@Autowired
	private FrequencyDataStorageStatusRepository freqDataStorageStatusRepo;
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	@Override
	public void saveVendorRatingCriteria(VendorRatingCriteriaListDTO dto) {

		try {
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			Long entityId = dto.getEntityId();

			if (entityId == null) {
				throw new AppException("EntityID is empty");
			}

			/* convert dto to entityList to persist */
			List<VendorRatingCriteriaEntity> entities = dto
					.getRtngCriteriaList().stream()
					.map(e -> convertDtoToEntity(e, entityId, userName,
							dto.getSource()))
					.collect(Collectors.toList());

			/* soft delete existing active ratings for an entityId */
			int softDeleteCount = repo.softDeleteActiveRatings(entityId,
					userName, VendorRatingCriteriaDefaultUtil.SOURCEMAP
							.get(dto.getSource()));

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Total number of softDeleted records are {}",
						softDeleteCount);
			}

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("Total number rows to be saved are {}",
						entities.size());
			}

			repo.saveAll(entities);

		} catch (Exception e) {
			String errMsg = "Error while saving VendorRatingCriteria";
			LOGGER.error(errMsg, e);
			throw new AppException(e.getMessage());
		}

	}

	@Override
	public VendorRatingCriteriaListDTO getVendorRatingCriteria(Long entityId,
			String source) {
		List<VendorRatingCriteriaEntity> entities = null;
		VendorRatingCriteriaListDTO dto = new VendorRatingCriteriaListDTO();
		List<VendorRatingCriteriaDTO> rtngCriteriaList = new ArrayList<>();
		try {
			entities = repo.findByEntityIdAndIsDeleteFalseAndSource(entityId,
					VendorRatingCriteriaDefaultUtil.SOURCEMAP.get(source));

			/*
			 * if rating criteria doesn't exist for an entityId create default
			 * rating and return the same
			 */
			if (entities.isEmpty()) {
				util.persistDefaultRatingCriteria(entityId, source);
				entities = repo.findByEntityIdAndIsDeleteFalseAndSource(
						entityId,
						VendorRatingCriteriaDefaultUtil.SOURCEMAP.get(source));
			}
			// sorting ReturnType wise
			Collections.sort(entities,
					comparing(VendorRatingCriteriaEntity::getReturnType));

			// convert entity to dto and return
			for (VendorRatingCriteriaEntity e : entities) {
				VendorRatingCriteriaDTO criteriaDto = convertEntityToDto(e);
				rtngCriteriaList.add(criteriaDto);
				dto.setCreatedOn(
						EYDateUtil.toISTDateTimeFromUTC(e.getCreatedOn())
								.format(FORMATTER));
			}
			dto.setRtngCriteriaList(rtngCriteriaList);
			return dto;
		} catch (Exception e) {
			String errMsg = "Error while getting VendorRatingCriteria";
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}

	}

	@Override
	public VendorRatingTimestampDTO getTimeStamp(String financialYear,
			Long entityId) {
		VendorRatingTimestampDTO dto = new VendorRatingTimestampDTO();
		try {
			Optional<Gstr1FileStatusEntity> fileEntity = fileStatusRepository
					.findTop1ByFileTypeOrderByIdDesc("VENDOR_DUE_DATE");
			if (fileEntity.isPresent()) {
				dto.setUploadTime(EYDateUtil
						.toISTDateTimeFromUTC(fileEntity.get().getUpdatedOn())
						.format(FORMATTER));
				dto.setUploadStatus(fileEntity.get().getFileStatus());
			}
			Optional<ReturnDataStorageStatusEntity> returnDataStorageStatusEntity = returnDataStorageStatusRepo
					.findTop1ByFinancialYearOrderByModifiedOnDesc(
							financialYear);
			if (returnDataStorageStatusEntity.isPresent()) {
				dto.setRetFilingStatus(
						returnDataStorageStatusEntity.get().getStatus());
				dto.setRetFilingTime(EYDateUtil.toISTDateTimeFromUTC(
						returnDataStorageStatusEntity.get().getModifiedOn())
						.format(FORMATTER));
			}
			Optional<FrequencyDataStorageStatusEntity> freqDataStorageStatusEntity = freqDataStorageStatusRepo
					.findTop1ByFinancialYearAndEntityIdOrderByModifiedOnDesc(
							financialYear,entityId);
			if (freqDataStorageStatusEntity.isPresent()) {
				dto.setRetFrequencyStatus(
						freqDataStorageStatusEntity.get().getStatus());
				dto.setRetFrequencyTime(EYDateUtil.toISTDateTimeFromUTC(
						freqDataStorageStatusEntity.get().getModifiedOn())
						.format(FORMATTER));
			}
			return dto;
		} catch (Exception e) {
			String errMsg = "Error while getting VendorRatingCriteria";
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

	private VendorRatingCriteriaDTO convertEntityToDto(
			VendorRatingCriteriaEntity e) {
		VendorRatingCriteriaDTO dto = new VendorRatingCriteriaDTO();

		dto.setReturnType(e.getReturnType());
		dto.setDueType(e.getDueType());
		dto.setFromDay(e.getFromDay());
		dto.setToDay(e.getToDay());
		dto.setRating(e.getRating());
		return dto;
	}

	private VendorRatingCriteriaEntity convertDtoToEntity(
			VendorRatingCriteriaDTO dto, Long entityId, String userName,
			String source) {
		VendorRatingCriteriaEntity e = new VendorRatingCriteriaEntity();
		e.setReturnType(dto.getReturnType());
		e.setDueType(dto.getDueType());
		e.setFromDay(dto.getFromDay());
		e.setToDay(dto.getToDay());
		e.setRating(dto.getRating());
		e.setEntityId(entityId);
		e.setSource(VendorRatingCriteriaDefaultUtil.SOURCEMAP.get(source));
		e.setCreatedBy(userName);
		e.setCreatedOn(LocalDateTime.now());
		return e;
	}

}
