package com.ey.advisory.app.services.gl.masterFile.uploads;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.GlMasterSupplyTypeEntity;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlMasterSupplyTypeRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Kiran s
 *
 */
@Component("GlMasterSupplyTypeServiceImpl")
@Slf4j
public class GlMasterSupplyTypeServiceImpl {

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;
	
	@Autowired
	//@Qualifier("GlMasterSupplyTypeRepository")
	GlMasterSupplyTypeRepository glMasterSupplyTypeRepository;
	
	public void convertSupplyTypeMasterWorkSheetDataToList(
	        Object[][] objList, int columnCount, String fileType, String fileId) {

	    List<GlMasterSupplyTypeEntity> entityList = new ArrayList<>();
	    boolean hasInvalidRow = false;

	    // Step 1: Convert raw data to entities and detect invalid rows
	    for (int i = 0; i < objList.length; i++) {
	        Object[] obj = objList[i];
	        String supplyTypeReg = isNullOrEmpty(obj[0]) ? null : obj[0].toString();
	        String supplyTypeMs = isNullOrEmpty(obj[1]) ? null : obj[1].toString();

	        boolean isInvalid = isNullOrEmpty(supplyTypeReg) || isNullOrEmpty(supplyTypeMs);
	        if (isInvalid) hasInvalidRow = true;

	        GlMasterSupplyTypeEntity entity = new GlMasterSupplyTypeEntity();
	        entity.setSupplyTypeReg(supplyTypeReg);
	        entity.setSupplyTypeMs(supplyTypeMs);
	        entity.setFileType(fileType);
	        entity.setFileId(fileId != null ? fileId.toString() : null);
	        entity.setCreatedDate(LocalDateTime.now());
	        entity.setUpdatedDate(LocalDateTime.now());

	        if (isInvalid) {
	            entity.setIsActive(false);
	            entity.setErrorCode("ERGL1003");
	            entity.setErrorDesc("Data/Value in both the columns is mandatory");
	        }else {
	        	entity.setIsActive(false);
	            entity.setErrorCode("");
	            entity.setErrorDesc("");
	        }

	        entityList.add(entity);
	    }

	    // Step 2: If invalid rows exist, save them with error info and throw exception
	    if (hasInvalidRow) {
	        glMasterSupplyTypeRepository.saveAll(entityList);
	        throw new RuntimeException("Data/Value in both the columns is mandatory");
	    }

	    // Step 3: All rows are valid — collect composite keys to avoid multiple DB hits
	    Set<String> compositeKeys = entityList.stream()
	            .map(e -> e.getSupplyTypeReg() + "||" + e.getSupplyTypeMs())
	            .collect(Collectors.toSet());

	    // Step 4: Fetch existing records in a single DB call
	    List<GlMasterSupplyTypeEntity> existingList = glMasterSupplyTypeRepository.findByCompositeKeys(compositeKeys);

	    Map<String, Long> keyToIdMap = existingList.stream()
	            .collect(Collectors.toMap(
	                    e -> e.getSupplyTypeReg() + "||" + e.getSupplyTypeMs(),
	                    GlMasterSupplyTypeEntity::getId
	            ));

	    List<Long> updateIds = new ArrayList<>();

	    // Step 5: Update active status and prepare soft delete list
	    for (GlMasterSupplyTypeEntity entity : entityList) {
	        entity.setIsActive(true);
	        entity.setErrorCode("");
	        entity.setErrorDesc("");
	        String key = entity.getSupplyTypeReg() + "||" + entity.getSupplyTypeMs();
	        Long existingId = keyToIdMap.get(key);
	        if (existingId != null) {
	            updateIds.add(existingId);
	        }
	    }

	    // Step 6: Soft delete & save
	    if (!updateIds.isEmpty()) {
	        glMasterSupplyTypeRepository.softDeleteByIds(updateIds);
	    }

	    glMasterSupplyTypeRepository.saveAll(entityList);
	}

	private boolean isNullOrEmpty(Object obj) {
	    return obj == null || obj.toString().trim().isEmpty();
	}

	
	/*public void convertSupplyTypeMasterWorkSheetDataToList(
	        Object[][] objList, int columnCount, String fileType, String fileId) {

	    List<GlMasterSupplyTypeEntity> entityList = new ArrayList<>();
	    boolean hasInvalidRow = false;

	    // Step 1: Convert raw data to entities and detect invalid rows
	    for (int i = 0; i < objList.length; i++) {
	        Object[] obj = objList[i];
	        String supplyTypeReg = isNullOrEmpty(obj[0]) ? null : obj[0].toString();
	        String supplyTypeMs = isNullOrEmpty(obj[1]) ? null : obj[1].toString();

	        boolean isInvalid = isNullOrEmpty(supplyTypeReg) || isNullOrEmpty(supplyTypeMs);
	        if (isInvalid) hasInvalidRow = true;

	        GlMasterSupplyTypeEntity entity = new GlMasterSupplyTypeEntity();
	        entity.setSupplyTypeReg(supplyTypeReg);
	        entity.setSupplyTypeMs(supplyTypeMs);
	        entity.setFileType(fileType);
	        entity.setFileId(fileId != null ? fileId.toString() : null);
	        entity.setCreatedDate(LocalDateTime.now());
	        entity.setUpdatedDate(LocalDateTime.now());

	        if (isInvalid) {
	            entity.setIsActive(false);
	            entity.setErrorCode("ERGL1003");
	            entity.setErrorDesc("Data/Value in both the columns is mandatory");
	        }

	        entityList.add(entity);
	    }

	    // Step 2: If invalid rows exist, mark all entities inactive and save
	    if (hasInvalidRow) {
	        for (GlMasterSupplyTypeEntity entity : entityList) {
	            entity.setIsActive(false);
	            if (entity.getErrorCode() == null) {
	                entity.setErrorCode("ERGL1003");
	                entity.setErrorDesc("Data/Value in both the columns is mandatory");
	            }
	        }
	        glMasterSupplyTypeRepository.saveAll(entityList);
	        throw new RuntimeException("Data/Value in both the columns is mandatory");
	    }

	    // Step 3: All rows are valid — collect composite keys to avoid multiple DB hits
	    Set<String> compositeKeys = entityList.stream()
	            .map(e -> e.getSupplyTypeReg() + "||" + e.getSupplyTypeMs())
	            .collect(Collectors.toSet());

	    // Step 4: Fetch existing records in a single DB call
	    List<GlMasterSupplyTypeEntity> existingList = glMasterSupplyTypeRepository.findByCompositeKeys(compositeKeys);

	    Map<String, Long> keyToIdMap = existingList.stream()
	            .collect(Collectors.toMap(
	                    e -> e.getSupplyTypeReg() + "||" + e.getSupplyTypeMs(),
	                    GlMasterSupplyTypeEntity::getId
	            ));

	    List<Long> updateIds = new ArrayList<>();

	    // Step 5: Update active status and prepare soft delete list
	    for (GlMasterSupplyTypeEntity entity : entityList) {
	        entity.setIsActive(true);
	        String key = entity.getSupplyTypeReg() + "||" + entity.getSupplyTypeMs();
	        Long existingId = keyToIdMap.get(key);
	        if (existingId != null) {
	            updateIds.add(existingId);
	        }
	    }

	    // Step 6: Soft delete & save
	    if (!updateIds.isEmpty()) {
	        glMasterSupplyTypeRepository.softDeleteByIds(updateIds);
	    }

	    glMasterSupplyTypeRepository.saveAll(entityList);
	}
	
	 private boolean isNullOrEmpty(Object obj) {
	        return obj == null || obj.toString().trim().isEmpty();
	    }*/
	

}
