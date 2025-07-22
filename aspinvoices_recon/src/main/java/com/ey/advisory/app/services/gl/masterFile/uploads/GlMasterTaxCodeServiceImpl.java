package com.ey.advisory.app.services.gl.masterFile.uploads;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.GlTaxCodeMasterEntity;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlTaxCodeMasterRepo;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Kiran s
 *
 */
@Component("GlMasterTaxCodeServiceImpl")
@Slf4j
public class GlMasterTaxCodeServiceImpl {

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;
	
	@Autowired
	//@Qualifier("GlTaxCodeMasterRepo")
	GlTaxCodeMasterRepo glMasterTaxCodeRepo;
	
	
	public void convertTaxCodeMasterWorkSheetDataToList(
	        Object[][] objList, int columnCount, String fileType, String fileId) {

	    LOGGER.debug("Start converting Tax Code Master worksheet data. fileType: {}, fileId: {}", fileType, fileId);

	    List<GlTaxCodeMasterEntity> entityList = new ArrayList<>();
	    List<Long> existingIdsToSoftDelete = new ArrayList<>();
	    boolean hasInvalidRow = false;

	    for (int i = 0; i < objList.length; i++) {
	        Object[] obj = objList[i];

	        String transactionTypeGl = isNullOrEmpty(obj[0]) ? null : obj[0].toString().trim();
	        String taxCodeDescriptionMs = isNullOrEmpty(obj[1]) ? null : obj[1].toString().trim();
	        String taxTypeMs = isNullOrEmpty(obj[2]) ? null : obj[2].toString().trim();
	        String eligibilityMs = isNullOrEmpty(obj[3]) ? null : obj[3].toString().trim();
	        BigDecimal taxRateMs = isNullOrEmpty(obj[4]) ? null : getBigDecimal(obj[4]);

	        LOGGER.debug("Processing row {}: transactionTypeGl={}, taxRateMs={}", i + 1, transactionTypeGl, taxRateMs);

	        boolean isInvalid = isNullOrEmpty(transactionTypeGl) || taxRateMs == null;
	        if (isInvalid) {
	            hasInvalidRow = true;
	            LOGGER.debug("Invalid row at index {}. Marking entity as inactive.", i + 1);
	        }

	        GlTaxCodeMasterEntity entity = new GlTaxCodeMasterEntity();
	        entity.setTransactionTypeGl(transactionTypeGl);
	        entity.setTaxCodeDescriptionMs(taxCodeDescriptionMs);
	        entity.setTaxTypeMs(taxTypeMs);
	        entity.setEligibilityMs(eligibilityMs);
	        entity.setTaxRateMs(taxRateMs);
	        entity.setFileType(fileType);
	        entity.setFileId(fileId != null ? fileId.toString() : null);
	        entity.setCreatedDate(LocalDateTime.now());
	        entity.setUpdatedDate(LocalDateTime.now());

	        if (isInvalid) {
	            entity.setIsActive(false);
	            entity.setErrorCode("ERGL1004");
	            entity.setErrorDesc("Data/Value in both the columns is mandatory");
	        } else {
	            entity.setIsActive(false);
	            entity.setErrorCode("");
	            entity.setErrorDesc("");
	            List<GlTaxCodeMasterEntity> existingList = glMasterTaxCodeRepo
	                    .findByTransactionTypeGlAndTaxRateMsAndIsActiveTrue(transactionTypeGl, taxRateMs);

	            if (existingList != null && !existingList.isEmpty()) {
	                for (GlTaxCodeMasterEntity existingEntity : existingList) {
	                    existingIdsToSoftDelete.add(existingEntity.getId());
	                }
	            }

	        }

	        entityList.add(entity);
	    }

	    if (hasInvalidRow) {
	        LOGGER.debug("Invalid rows found. Saving all entities (invalid included) to DB.");
	        glMasterTaxCodeRepo.saveAll(entityList);
	        LOGGER.error("Data/Value in both the columns is mandatory. Aborting operation.");
	        throw new RuntimeException("Data/Value in both the columns is mandatory");
	    }

	    // ✅ No invalid rows — make all entities active and clean
	    for (GlTaxCodeMasterEntity entity : entityList) {
	        entity.setIsActive(true);
	        entity.setErrorCode("");
	        entity.setErrorDesc("");
	    }

	    // Soft delete old matching records
	    if (!existingIdsToSoftDelete.isEmpty()) {
	        LOGGER.debug("Soft deleting {} existing entries with IDs: {}", existingIdsToSoftDelete.size(), existingIdsToSoftDelete);
	        glMasterTaxCodeRepo.softDeleteByIds(existingIdsToSoftDelete);
	    }

	    LOGGER.debug("Saving {} valid entities to the database.", entityList.size());
	    glMasterTaxCodeRepo.saveAll(entityList);

	    LOGGER.debug("Tax Code Master worksheet data conversion completed successfully.");
	}

	private boolean isNullOrEmpty(Object obj) {
	    return obj == null || obj.toString().trim().isEmpty();
	}

	private BigDecimal getBigDecimal(Object obj) {
	    try {
	        return new BigDecimal(obj.toString().trim());
	    } catch (NumberFormatException e) {
	        throw new RuntimeException("Invalid decimal value: " + obj);
	    }
	}	

	
/*	public void convertTaxCodeMasterWorkSheetDataToList(
	        Object[][] objList, int columnCount, String fileType, String fileId) {

	    List<GlTaxCodeMasterEntity> entityList = new ArrayList<>();
	    boolean hasInvalidRow = false;

	    // Step 1: Convert raw data to entities and detect invalid rows
	    for (int i = 0; i < objList.length; i++) {
	        Object[] obj = objList[i];

	        String transactionTypeGl = isNullOrEmpty(obj[0]) ? null : obj[0].toString().trim();
	        String taxCodeDescriptionMs = isNullOrEmpty(obj[1]) ? null : obj[1].toString().trim();
	        String taxTypeMs = isNullOrEmpty(obj[2]) ? null : obj[2].toString().trim();
	        String eligibilityMs = isNullOrEmpty(obj[3]) ? null : obj[3].toString().trim();
	        BigDecimal taxRateMs = isNullOrEmpty(obj[4]) ? null : new BigDecimal(obj[4].toString().trim());

	        boolean isInvalid = isNullOrEmpty(transactionTypeGl) || taxRateMs == null;
	        if (isInvalid) hasInvalidRow = true;

	        GlTaxCodeMasterEntity entity = new GlTaxCodeMasterEntity();
	        entity.setTransactionTypeGl(transactionTypeGl);
	        entity.setTaxCodeDescriptionMs(taxCodeDescriptionMs);
	        entity.setTaxTypeMs(taxTypeMs);
	        entity.setEligibilityMs(eligibilityMs);
	        entity.setTaxRateMs(taxRateMs);
	        entity.setFileType(fileType);
	        entity.setFileId(fileId != null ? fileId.toString() : null);
	        entity.setCreatedDate(LocalDateTime.now());
	        entity.setUpdatedDate(LocalDateTime.now());

	        if (isInvalid) {
	            entity.setIsActive(false);
	            entity.setErrorCode("ERGL1004");
	            entity.setErrorDesc("Data/Value in both the columns is mandatory");
	        }

	        entityList.add(entity);
	    }

	    // Step 2: If invalid rows exist, mark all entities inactive and save
	    if (hasInvalidRow) {
	        for (GlTaxCodeMasterEntity entity : entityList) {
	            entity.setIsActive(false);
	            if (entity.getErrorCode() == null) {
	                entity.setErrorCode("ERGL1004");
	                entity.setErrorDesc("Data/Value in both the columns is mandatory");
	            }
	        }
	        glMasterTaxCodeRepo.saveAll(entityList);
	        throw new RuntimeException("Data/Value in both the columns is mandatory");
	    }

	    // Step 3: All rows are valid — collect composite keys
	    Set<String> compositeKeys = entityList.stream()
	            .map(e -> e.getTransactionTypeGl() + "||" + e.getTaxRateMs())
	            .collect(Collectors.toSet());

	    // Step 4: Fetch existing records by composite keys
	    List<GlTaxCodeMasterEntity> existingList = glMasterTaxCodeRepo.findByCompositeKeys(compositeKeys);

	    Map<String, Long> keyToIdMap = existingList.stream()
	            .collect(Collectors.toMap(
	                    e -> e.getTransactionTypeGl() + "||" + e.getTaxRateMs(),
	                    GlTaxCodeMasterEntity::getId
	            ));

	    List<Long> updateIds = new ArrayList<>();

	    // Step 5: Set isActive true and prepare soft delete list
	    for (GlTaxCodeMasterEntity entity : entityList) {
	        entity.setIsActive(true);
	        entity.setErrorCode("");
	        entity.setErrorDesc("");
	        String key = entity.getTransactionTypeGl() + "||" + entity.getTaxRateMs();
	        Long existingId = keyToIdMap.get(key);
	        if (existingId != null) {
	            updateIds.add(existingId);
	        }
	    }

	    // Step 6: Soft delete existing entries and save new ones
	    if (!updateIds.isEmpty()) {
	        glMasterTaxCodeRepo.softDeleteByIds(updateIds);
	    }

	    glMasterTaxCodeRepo.saveAll(entityList);
	}*/

	// Helper method
	
}
