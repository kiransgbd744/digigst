package com.ey.advisory.app.services.gl.masterFile.uploads;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.GlBusinessPlaceMasterEntity;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlBusinessPlaceMasterRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Kiran s
 *
 */
@Component("GlMasterBusinessUnitServiceImpl")
@Slf4j
public class GlMasterBusinessUnitServiceImpl {

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;
	
	@Autowired
	GlBusinessPlaceMasterRepository glBpMasterRepo;
	
	public void convertBusinessUnitMasterWorkSheetDataToList(
	        Object[][] objList, int columnCount, String fileType, String fileId) {

	    List<GlBusinessPlaceMasterEntity> entityList = new ArrayList<>();
	    boolean hasInvalidRow = false;

	    for (int i = 0; i < objList.length; i++) {
	        Object[] obj = objList[i];

	        GlReconDataDto dto = new GlReconDataDto();
	        dto.setBusinessPlace(isNullOrEmpty(obj[0]) ? null : obj[0].toString());
	        dto.setBusinessArea(isNullOrEmpty(obj[1]) ? null : obj[1].toString());
	        dto.setPlantCode(isNullOrEmpty(obj[2]) ? null : obj[2].toString());
	        dto.setProfitCenter(isNullOrEmpty(obj[3]) ? null : obj[3].toString());
	        dto.setCostCenter(isNullOrEmpty(obj[4]) ? null : obj[4].toString());
	        dto.setGstin(isNullOrEmpty(obj[5]) ? null : obj[5].toString());

	        boolean isGstinBlank = isNullOrEmpty(obj[5]);
	        boolean areAllOthersBlank =
	                isNullOrEmpty(obj[0]) &&
	                isNullOrEmpty(obj[1]) &&
	                isNullOrEmpty(obj[2]) &&
	                isNullOrEmpty(obj[3]) &&
	                isNullOrEmpty(obj[4]);

	        GlBusinessPlaceMasterEntity entity = new GlBusinessPlaceMasterEntity();
	        entity.setBusinessPlace(dto.getBusinessPlace());
	        entity.setBusinessArea(dto.getBusinessArea());
	        entity.setPlantCode(dto.getPlantCode());
	        entity.setProfitCentre(dto.getProfitCenter());
	        entity.setCostCentre(dto.getCostCenter());
	        entity.setGstin(dto.getGstin());
	        entity.setFileType(fileType);
	        entity.setFileId(fileId != null ? fileId.toString() : null);
	        entity.setCreatedDate(LocalDateTime.now());
	        entity.setUpdatedDate(LocalDateTime.now());

	        if (isGstinBlank || areAllOthersBlank) {
	            hasInvalidRow = true;
	            entity.setIsActive(false);
	            entity.setErrorCode("ERGL1001");
	            entity.setErrorDesc("Data/Value in GSTIN column & any one of the other five columns is mandatory.");
	        }
	        else {
	        	entity.setIsActive(false);
	            entity.setErrorCode("");
	            entity.setErrorDesc("");
	        }

	        entityList.add(entity);
	    }

	    if (hasInvalidRow) {
	        glBpMasterRepo.saveAll(entityList);
	        throw new RuntimeException("Data/Value in GSTIN column & any one of the other five columns is mandatory");
	    } else {
	        Set<String> gstinSet = entityList.stream()
	                .map(GlBusinessPlaceMasterEntity::getGstin)
	                .filter(Objects::nonNull)
	                .collect(Collectors.toSet());

	        List<GlBusinessPlaceMasterEntity> existingList = glBpMasterRepo.findByGstinInAndIsActiveTrue(gstinSet);

	        Map<String, Long> gstinToIdMap = existingList.stream()
	                .collect(Collectors.toMap(GlBusinessPlaceMasterEntity::getGstin, GlBusinessPlaceMasterEntity::getId));

	        List<Long> updateIds = new ArrayList<>();

	        for (GlBusinessPlaceMasterEntity entity : entityList) {
	            entity.setIsActive(true);
	            entity.setErrorCode(""); 
	            entity.setErrorDesc(""); 
	            Long existingId = gstinToIdMap.get(entity.getGstin());
	            if (existingId != null) {
	                updateIds.add(existingId);
	            }
	        }

	        if (!updateIds.isEmpty()) {
	            glBpMasterRepo.softDeleteByIds(updateIds);
	        }
	        glBpMasterRepo.saveAll(entityList);
	    }
	}
	
/*	public void convertBusinessUnitMasterWorkSheetDataToList(
	        Object[][] objList, int columnCount, String fileType, String fileId) {

	    List<GlBusinessPlaceMasterEntity> entityList = new ArrayList<>();
	    boolean hasInvalidRow = false;

	    for (int i = 0; i < objList.length; i++) {
	        Object[] obj = objList[i];

	        GlReconDataDto dto = new GlReconDataDto();
	        dto.setBusinessPlace(isNullOrEmpty(obj[0]) ? null : obj[0].toString());
	        dto.setBusinessArea(isNullOrEmpty(obj[1]) ? null : obj[1].toString());
	        dto.setPlantCode(isNullOrEmpty(obj[2]) ? null : obj[2].toString());
	        dto.setProfitCenter(isNullOrEmpty(obj[3]) ? null : obj[3].toString());
	        dto.setCostCenter(isNullOrEmpty(obj[4]) ? null : obj[4].toString());
	        dto.setGstin(isNullOrEmpty(obj[5]) ? null : obj[5].toString());

	        boolean isGstinBlank = isNullOrEmpty(obj[5]);
	        boolean areAllOthersBlank =
	                isNullOrEmpty(obj[0]) &&
	                isNullOrEmpty(obj[1]) &&
	                isNullOrEmpty(obj[2]) &&
	                isNullOrEmpty(obj[3]) &&
	                isNullOrEmpty(obj[4]);

	        if (isGstinBlank || areAllOthersBlank) {
	            hasInvalidRow = true;
	        }

	        GlBusinessPlaceMasterEntity entity = new GlBusinessPlaceMasterEntity();
	        entity.setBusinessPlace(dto.getBusinessPlace());
	        entity.setBusinessArea(dto.getBusinessArea());
	        entity.setPlantCode(dto.getPlantCode());
	        entity.setProfitCentre(dto.getProfitCenter());
	        entity.setCostCentre(dto.getCostCenter());
	        entity.setGstin(dto.getGstin());
	        entity.setFileType(fileType);
	        entity.setFileId(fileId != null ? fileId.toString() : null);
	        entity.setCreatedDate(LocalDateTime.now());
	        entity.setUpdatedDate(LocalDateTime.now());

	        entityList.add(entity);
	    }

	    if (hasInvalidRow) {
	        for (GlBusinessPlaceMasterEntity entity : entityList) {
	            entity.setIsActive(false);
	            entity.setErrorCode("ERGL1001");
	            entity.setErrorDesc("Data/Value in GSTIN column & any one of the other five columns is mandatory.");
	        }
	        glBpMasterRepo.saveAll(entityList);
	        throw new RuntimeException("Data/Value in GSTIN column & any one of the other five columns is mandatory");
	    } else {
	        // Step 1: Collect all non-null GSTINs
	        Set<String> gstinSet = entityList.stream()
	                .map(GlBusinessPlaceMasterEntity::getGstin)
	                .filter(Objects::nonNull)
	                .collect(Collectors.toSet());

	        // Step 2: Fetch all existing active records in one query
	        List<GlBusinessPlaceMasterEntity> existingList = glBpMasterRepo.findByGstinInAndIsActiveTrue(gstinSet);

	        // Step 3: Map GSTIN to existing ID
	        Map<String, Long> gstinToIdMap = existingList.stream()
	                .collect(Collectors.toMap(GlBusinessPlaceMasterEntity::getGstin, GlBusinessPlaceMasterEntity::getId));

	        List<Long> updateIds = new ArrayList<>();

	        for (GlBusinessPlaceMasterEntity entity : entityList) {
	        	entity.setIsActive(true);
	            entity.setErrorCode(""); 
	            entity.setErrorDesc(""); 
	            Long existingId = gstinToIdMap.get(entity.getGstin());
	            if (existingId != null) {
	                updateIds.add(existingId);
	            }
	        }

	        if (!updateIds.isEmpty()) {
	            glBpMasterRepo.softDeleteByIds(updateIds);
	        }
	        glBpMasterRepo.saveAll(entityList);
	    }
	}
	
*/

	

    // Helper Methods
    private boolean isNullOrEmpty(Object obj) {
        return obj == null || obj.toString().trim().isEmpty();
    }

	
}
