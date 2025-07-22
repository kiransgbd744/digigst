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

import com.ey.advisory.app.data.entities.client.asprecon.GlDocTypeMasterEntity;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlDocTypeMasterRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Kiran s
 *
 */
@Component("GlMasterDocumentTypeServiceImpl")
@Slf4j
public class GlMasterDocumentTypeServiceImpl {

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	// @Qualifier("GlDocTypeMasterRepository")
	GlDocTypeMasterRepository glDocTypeMasterRepo;
	
	
	public void convertDocumentTypeMasterWorkSheetDataToList(
	        Object[][] objList, int columnCount, String fileType, String fileId) {

	    List<GlDocTypeMasterEntity> entityList = new ArrayList<>();
	    boolean hasInvalidRow = false;

	    // Step 1: Convert raw data to entities and detect invalid rows
	    for (int i = 0; i < objList.length; i++) {
	        Object[] obj = objList[i];
	        String docType = isNullOrEmpty(obj[0]) ? null : obj[0].toString();
	        String docTypeMs = isNullOrEmpty(obj[1]) ? null : obj[1].toString();

	        boolean isInvalid = isNullOrEmpty(docType) || isNullOrEmpty(docTypeMs);
	        if (isInvalid) hasInvalidRow = true;

	        GlDocTypeMasterEntity entity = new GlDocTypeMasterEntity();
	        entity.setDocType(docType);
	        entity.setDocTypeMs(docTypeMs);
	        entity.setFileType(fileType);
	        entity.setFileId(fileId != null ? fileId.toString() : null);
	        entity.setCreatedDate(LocalDateTime.now());
	        entity.setUpdatedDate(LocalDateTime.now());

	        if (isInvalid) {
	            entity.setIsActive(false);
	            entity.setErrorCode("ERGL1002");
	            entity.setErrorDesc("Data/Value in both the columns is mandatory");
	        } else {
	        	entity.setIsActive(false);
	            entity.setErrorCode("");
	            entity.setErrorDesc("");
	        }

	        entityList.add(entity);
	    }

	    // Step 2: If invalid rows exist, save only with their corresponding error info
	    if (hasInvalidRow) {
	        glDocTypeMasterRepo.saveAll(entityList);
	        throw new RuntimeException("Data/Value in both the columns is mandatory");
	    }

	    // Step 3: All rows are valid — collect composite keys to avoid multiple DB hits
	    Set<String> compositeKeys = entityList.stream()
	            .map(e -> e.getDocType() + "||" + e.getDocTypeMs())
	            .collect(Collectors.toSet());

	    // Step 4: Fetch existing records in a single DB call
	    List<GlDocTypeMasterEntity> existingList = glDocTypeMasterRepo.findByCompositeKeys(compositeKeys);

	    Map<String, Long> keyToIdMap = existingList.stream()
	            .collect(Collectors.toMap(
	                    e -> e.getDocType() + "||" + e.getDocTypeMs(),
	                    GlDocTypeMasterEntity::getId
	            ));

	    List<Long> updateIds = new ArrayList<>();

	    // Step 5: Update active status and prepare soft delete list
	    for (GlDocTypeMasterEntity entity : entityList) {
	        entity.setIsActive(true);
	        entity.setErrorCode("");
	        entity.setErrorDesc("");
	        String key = entity.getDocType() + "||" + entity.getDocTypeMs();
	        Long existingId = keyToIdMap.get(key);
	        if (existingId != null) {
	            updateIds.add(existingId);
	        }
	    }

	    // Step 6: Soft delete & save
	    if (!updateIds.isEmpty()) {
	        glDocTypeMasterRepo.softDeleteByIds(updateIds);
	    }

	    glDocTypeMasterRepo.saveAll(entityList);
	}


/*	public void convertDocumentTypeMasterWorkSheetDataToList(
	        Object[][] objList, int columnCount, String fileType, String fileId) {

	    List<GlDocTypeMasterEntity> entityList = new ArrayList<>();
	    boolean hasInvalidRow = false;

	    // Step 1: Convert raw data to entities and detect invalid rows
	    for (int i = 0; i < objList.length; i++) {
	        Object[] obj = objList[i];
	        String docType = isNullOrEmpty(obj[0]) ? null : obj[0].toString();
	        String docTypeMs = isNullOrEmpty(obj[1]) ? null : obj[1].toString();

	        boolean isInvalid = isNullOrEmpty(docType) || isNullOrEmpty(docTypeMs);
	        if (isInvalid) hasInvalidRow = true;

	        GlDocTypeMasterEntity entity = new GlDocTypeMasterEntity();
	        entity.setDocType(docType);
	        entity.setDocTypeMs(docTypeMs);
	        entity.setFileType(fileType);
	        entity.setFileId(fileId != null ? fileId.toString() : null);
	        entity.setCreatedDate(LocalDateTime.now());
	        entity.setUpdatedDate(LocalDateTime.now());

	        if (isInvalid) {
	            entity.setIsActive(false);
	            entity.setErrorCode("ERGL1002");
	            entity.setErrorDesc("Data/Value in both the columns is mandatory");
	        }

	        entityList.add(entity);
	    }

	    // Step 2: If invalid rows exist, mark all entities inactive and save
	    if (hasInvalidRow) {
	        for (GlDocTypeMasterEntity entity : entityList) {
	            entity.setIsActive(false);
	            if (entity.getErrorCode() == null) {
	                entity.setErrorCode("ERGL1002");
	                entity.setErrorDesc("Data/Value in both the columns is mandatory");
	            }
	        }
	        glDocTypeMasterRepo.saveAll(entityList);
	        throw new RuntimeException("Data/Value in both the columns is mandatory");
	    }

	    // Step 3: All rows are valid — collect composite keys to avoid multiple DB hits
	    Set<String> compositeKeys = entityList.stream()
	            .map(e -> e.getDocType() + "||" + e.getDocTypeMs())
	            .collect(Collectors.toSet());

	    // Step 4: Fetch existing records in a single DB call
	    List<GlDocTypeMasterEntity> existingList = glDocTypeMasterRepo.findByCompositeKeys(compositeKeys);

	    Map<String, Long> keyToIdMap = existingList.stream()
	            .collect(Collectors.toMap(
	                    e -> e.getDocType() + "||" + e.getDocTypeMs(),
	                    GlDocTypeMasterEntity::getId
	            ));

	    List<Long> updateIds = new ArrayList<>();

	    // Step 5: Update active status and prepare soft delete list
	    for (GlDocTypeMasterEntity entity : entityList) {
	        entity.setIsActive(true);
	        entity.setErrorCode("");
	        entity.setErrorDesc("");
	        String key = entity.getDocType() + "||" + entity.getDocTypeMs();
	        Long existingId = keyToIdMap.get(key);
	        if (existingId != null) {
	            updateIds.add(existingId);
	        }
	    }

	    // Step 6: Soft delete & save
	    if (!updateIds.isEmpty()) {
	        glDocTypeMasterRepo.softDeleteByIds(updateIds);
	    }

	    glDocTypeMasterRepo.saveAll(entityList);
	}*/

	private boolean isNullOrEmpty(Object object) {
	    return object == null || object.toString().trim().isEmpty();
	}

	
	

	
}
