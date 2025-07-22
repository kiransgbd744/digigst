/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.controllers.gl.master;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.GlBusinessPlaceMasterEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlCodeMappingMasterEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlCodeMasterEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlDocTypeMasterEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlMasterSupplyTypeEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlTaxCodeMasterEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GlBusinessPlaceMasterRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlCodeMappingMasterRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlCodeMasterRepo;
import com.ey.advisory.app.data.repositories.client.asprecon.GlDocTypeMasterRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlMasterSupplyTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlTaxCodeMasterRepo;
@Service("GlCodeMasterDateSaveOrDeleteServiceImpl")
public  class GlCodeMasterDateSaveOrDeleteServiceImpl implements GlCodeMasterDateSaveOrDeleteService{
	
	@Autowired
	@Qualifier("GlCodeMasterRepo")
	GlCodeMasterRepo glCodeMasterRepo;
	
	 @Autowired
	 @Qualifier("GlDocTypeMasterRepository")
	    private GlDocTypeMasterRepository glDocTypeMasterRepository;
	 
	 @Autowired
	    private GlMasterSupplyTypeRepository glMasterSupplyTypeRepository;
	 

	    @Autowired
	    private GlBusinessPlaceMasterRepository glBusinessPlaceMasterRepository;
	    
	    @Autowired
	    private GlTaxCodeMasterRepo glTaxCodeMasterRepo;
	    
	    @Autowired
	    GlCodeMappingMasterRepository glCodeMappingMasterRepository;
	
	    @Override
	    public void saveGlCodeMasterList(List<GlCodeMasterDto> dtoList, Long entityId) {
	        List<GlCodeMasterEntity> updatedEntities = new ArrayList<>();

	        for (GlCodeMasterDto dto : dtoList) {
	            if (dto.getId() != null) {
	                Optional<GlCodeMasterEntity> optionalEntity = glCodeMasterRepo.findById(dto.getId());
	                if (optionalEntity.isPresent()) {
	                    GlCodeMasterEntity entity = optionalEntity.get();
	                    // Update all fields
	                    entity.setCgstTaxGlCode(dto.getCgstTaxGlCode());
	                    entity.setSgstTaxGlCode(dto.getSgstTaxGlCode());
	                    entity.setIgstTaxGlCode(dto.getIgstTaxGlCode());
	                    entity.setUgstTaxGlCode(dto.getUgstTaxGlCode());
	                    entity.setCompensationCessGlCode(dto.getCompensationCessGlCode());
	                    entity.setKeralaCessGlCode(dto.getKeralaCessGlCode());
	                    entity.setRevenueGls(dto.getRevenueGls());
	                    entity.setExpenceGls(dto.getExpenceGls());
	                    entity.setExchangeRate(dto.getExchangeRate());
	                   // entity.setDiffGl(dto.getDiffGl());
	                    entity.setExportGl(dto.getExportGl());
	                    entity.setForexGlsPor(dto.getForexGlsPor());
	                    entity.setTaxableAdvanceLiabilityGls(dto.getTaxableAdvanceLiabilityGls());
	                    entity.setNonTaxableAdvanceLiabilityGls(dto.getNonTaxableAdvanceLiabilityGls());
	                    entity.setCcAndStGls(dto.getCcAndStGls());
	                    entity.setUnbilledRevenueGls(dto.getUnbilledRevenueGls());
	                    entity.setBankAccGls(dto.getBankAccGls());
	                    entity.setInputTaxGls(dto.getInputTaxGls());
	                    entity.setFixedAssetGls(dto.getFixedAssetGls());
	                    entity.setUpdatedDate(LocalDateTime.now());
	                    entity.setIsActive(true);
	                    updatedEntities.add(entity);
	                }
	            } else {
	                // Treat as new record if id is null
	                GlCodeMasterEntity newEntity = new GlCodeMasterEntity();
	                newEntity.setEntityId(entityId);
	                newEntity.setCgstTaxGlCode(dto.getCgstTaxGlCode());
	                newEntity.setSgstTaxGlCode(dto.getSgstTaxGlCode());
	                newEntity.setIgstTaxGlCode(dto.getIgstTaxGlCode());
	                newEntity.setUgstTaxGlCode(dto.getUgstTaxGlCode());
	                newEntity.setCompensationCessGlCode(dto.getCompensationCessGlCode());
	                newEntity.setKeralaCessGlCode(dto.getKeralaCessGlCode());
	                newEntity.setRevenueGls(dto.getRevenueGls());
	                newEntity.setExpenceGls(dto.getExpenceGls());
	                newEntity.setExchangeRate(dto.getExchangeRate());
	              //  newEntity.setDiffGl(dto.getDiffGl());
	                newEntity.setExportGl(dto.getExportGl());
	                newEntity.setForexGlsPor(dto.getForexGlsPor());
	                newEntity.setTaxableAdvanceLiabilityGls(dto.getTaxableAdvanceLiabilityGls());
	                newEntity.setNonTaxableAdvanceLiabilityGls(dto.getNonTaxableAdvanceLiabilityGls());
	                newEntity.setCcAndStGls(dto.getCcAndStGls());
	                newEntity.setUnbilledRevenueGls(dto.getUnbilledRevenueGls());
	                newEntity.setBankAccGls(dto.getBankAccGls());
	                newEntity.setInputTaxGls(dto.getInputTaxGls());
	                newEntity.setFixedAssetGls(dto.getFixedAssetGls());
	                newEntity.setIsActive(true);
	                newEntity.setCreatedDate(LocalDateTime.now());
	                newEntity.setUpdatedDate(LocalDateTime.now());
	                newEntity.setFileType("GL_Code_Mapping_Master_GL");
	                updatedEntities.add(newEntity);
	            }
	        }

	        glCodeMasterRepo.saveAll(updatedEntities);
	    }
	    @Override
	    public void saveDocTypeMasterList(List<GlMasterDocTypeDto> dtoList, Long entityId) {
	        List<GlDocTypeMasterEntity> updatedEntities = new ArrayList<>();

	        List<Long> updateIds = new ArrayList<>();
	        for (GlMasterDocTypeDto dto : dtoList) {
	            if (dto.getId() != null) {
	                Optional<GlDocTypeMasterEntity> optionalEntity = glDocTypeMasterRepository.findById(dto.getId());
	                if (optionalEntity.isPresent()) {
	                    GlDocTypeMasterEntity entity = optionalEntity.get();
	                    entity.setDocType(dto.getDocType());
	                    entity.setDocTypeMs(dto.getDocTypeMs());
	                    entity.setUpdatedDate(LocalDateTime.now());
	                    entity.setIsActive(true);
	                    updatedEntities.add(entity);
	                }
	            } else {
	               GlDocTypeMasterEntity entity = new GlDocTypeMasterEntity();
	                entity.setEntityId(entityId);
	                entity.setDocType(dto.getDocType());
	                entity.setDocTypeMs(dto.getDocTypeMs());
	                entity.setCreatedDate(LocalDateTime.now());
	                entity.setUpdatedDate(LocalDateTime.now());
	                entity.setIsActive(true);
	                entity.setFileType("Document_type");
	                updatedEntities.add(entity);
	                List<GlDocTypeMasterEntity> optionalEntity = glDocTypeMasterRepository.findByDocTypeAndDocTypeMsAndIsActiveTrue(dto.getDocType(), dto.getDocTypeMs());
	    	        if(!optionalEntity.isEmpty())
	    	        {
	    	        	updateIds.add(optionalEntity.get(0).getId());
	    	        }       }
	        }

	        glDocTypeMasterRepository.saveAll(updatedEntities);
	        if(!updateIds.isEmpty())
        	glDocTypeMasterRepository.softDeleteByIds(updateIds);

	    }
	   
	    @Override
	    public void saveSupplyTypeMasterList(List<GlMasterSupplyTypeDto> dtoList, Long entityId) {
	        List<GlMasterSupplyTypeEntity> updatedEntities = new ArrayList<>();

	        List<Long> updateIds = new ArrayList<>();

	        for (GlMasterSupplyTypeDto dto : dtoList) {
	            if (dto.getId() != null) {
	                Optional<GlMasterSupplyTypeEntity> optionalEntity = glMasterSupplyTypeRepository.findById(dto.getId());
	                if (optionalEntity.isPresent()) {
	                    GlMasterSupplyTypeEntity entity = optionalEntity.get();
	                    entity.setSupplyTypeReg(dto.getSupplyTypeReg());
	                    entity.setSupplyTypeMs(dto.getSupplyTypeMs());
	                    entity.setUpdatedDate(LocalDateTime.now());
	                    entity.setIsActive(true);
	                    updatedEntities.add(entity);
	                }
	            } else {
	                GlMasterSupplyTypeEntity entity = new GlMasterSupplyTypeEntity();
	                entity.setEntityId(entityId);
	                entity.setSupplyTypeReg(dto.getSupplyTypeReg());
	                entity.setSupplyTypeMs(dto.getSupplyTypeMs());
	                entity.setCreatedDate(LocalDateTime.now());
	                entity.setUpdatedDate(LocalDateTime.now());
	                entity.setIsActive(true);
	                entity.setFileType("Supply_Type");
	                updatedEntities.add(entity);
	                List<GlMasterSupplyTypeEntity> optionalEntity = glMasterSupplyTypeRepository.findBySupplyTypeRegAndSupplyTypeMsAndIsActiveTrue(dto.getSupplyTypeReg(), dto.getSupplyTypeMs());
	    	        if(!optionalEntity.isEmpty())
	    	        {
	    	        	updateIds.add(optionalEntity.get(0).getId());
	    	        }       }

	            
	        }

	        glMasterSupplyTypeRepository.saveAll(updatedEntities);
	        if(!updateIds.isEmpty())
	        glMasterSupplyTypeRepository.softDeleteByIds(updateIds);

	    }
	    @Override
	    public void saveBusinessPlaceMasterList(List<GlBusinessPlaceMasterDto> dtoList, Long entityId) {
	        List<GlBusinessPlaceMasterEntity> updatedEntities = new ArrayList<>();
	        List<Long> updateIds = new ArrayList<>();
	        for (GlBusinessPlaceMasterDto dto : dtoList) {
	            if (dto.getId() != null) {
	                Optional<GlBusinessPlaceMasterEntity> optionalEntity = glBusinessPlaceMasterRepository.findById(dto.getId());
	                if (optionalEntity.isPresent()) {
	                    GlBusinessPlaceMasterEntity entity = optionalEntity.get();
	                    entity.setBusinessPlace(dto.getBusinessPlace());
	                    entity.setBusinessArea(dto.getBusinessArea());
	                    entity.setPlantCode(dto.getPlantCode());
	                    entity.setProfitCentre(dto.getProfitCentre());
	                    entity.setCostCentre(dto.getCostCentre());
	                    entity.setGstin(dto.getGstin());
	                    entity.setUpdatedDate(LocalDateTime.now());
	                    entity.setIsActive(true);
	                    updatedEntities.add(entity);
	                }
	            } else {
	                List<GlBusinessPlaceMasterEntity> optionalEntity = glBusinessPlaceMasterRepository.findByGstinAndIsActiveTrue(dto.getGstin());
	            	if(!optionalEntity.isEmpty())
	            	{
	            		updateIds.add(optionalEntity.get(0).getId());
	            	}
	                GlBusinessPlaceMasterEntity entity = new GlBusinessPlaceMasterEntity();
	                entity.setEntityId(entityId);
	                entity.setBusinessPlace(dto.getBusinessPlace());
	                entity.setBusinessArea(dto.getBusinessArea());
	                entity.setPlantCode(dto.getPlantCode());
	                entity.setProfitCentre(dto.getProfitCentre());
	                entity.setCostCentre(dto.getCostCentre());
	                entity.setGstin(dto.getGstin());
	                entity.setCreatedDate(LocalDateTime.now());
	                entity.setUpdatedDate(LocalDateTime.now());
	                entity.setIsActive(true);
	                entity.setFileType("Business_Unit_code");
	                updatedEntities.add(entity);
	            }
	        }

	        glBusinessPlaceMasterRepository.saveAll(updatedEntities);
	        if(!updateIds.isEmpty())
	        {
	        	glBusinessPlaceMasterRepository.softDeleteByIds(updateIds);
	        }
	    }

	    
	    @Override
	    public void saveTaxCodeMasterList(List<GlTaxCodeMasterDto> dtoList, Long entityId) {
	        List<GlTaxCodeMasterEntity> updatedEntities = new ArrayList<>();
	        List<Long> updateIds = new ArrayList<>();
	        for (GlTaxCodeMasterDto dto : dtoList) {
	            if (dto.getId() != null) {
	                Optional<GlTaxCodeMasterEntity> optionalEntity = glTaxCodeMasterRepo.findById(dto.getId());
	                if (optionalEntity.isPresent()) {
	                    GlTaxCodeMasterEntity entity = optionalEntity.get();
	                    entity.setTransactionTypeGl(dto.getTransactionTypeGl());
	                    entity.setTaxCodeDescriptionMs(dto.getTaxCodeDescriptionMs());
	                    entity.setTaxTypeMs(dto.getTaxTypeMs());
	                    entity.setEligibilityMs(dto.getEligibilityMs());
	                    entity.setTaxRateMs(dto.getTaxRateMs());
	                    entity.setUpdatedDate(LocalDateTime.now());
	                    entity.setIsActive(true);
	                    updatedEntities.add(entity);
	                }
	            } else {
	                GlTaxCodeMasterEntity entity = new GlTaxCodeMasterEntity();
	                entity.setTransactionTypeGl(dto.getTransactionTypeGl());
	                entity.setTaxCodeDescriptionMs(dto.getTaxCodeDescriptionMs());
	                entity.setTaxTypeMs(dto.getTaxTypeMs());
	                entity.setEligibilityMs(dto.getEligibilityMs());
	                entity.setTaxRateMs(dto.getTaxRateMs());
	                entity.setEntityId(entityId);
	                entity.setCreatedDate(LocalDateTime.now());
	                entity.setUpdatedDate(LocalDateTime.now());
	                entity.setIsActive(true);
	                entity.setFileType("Tax_code");
	                updatedEntities.add(entity);
	                List<GlTaxCodeMasterEntity> optionalEntity = glTaxCodeMasterRepo.findByTransactionTypeGlAndTaxRateMsAndIsActiveTrue(dto.getTransactionTypeGl(), dto.getTaxRateMs());
		            
	             	if(!optionalEntity.isEmpty())
	            	{
	            		updateIds.add(optionalEntity.get(0).getId());
	            	}
	       
	            }
	        }

	        glTaxCodeMasterRepo.saveAll(updatedEntities);
	        if(!updateIds.isEmpty())
	        {
	        	glTaxCodeMasterRepo.softDeleteByIds(updateIds);
	        }
	  
	    }

	    @Override
	    public void softDeleteGlCodeMaster(List<Long> ids) {
	    	glCodeMasterRepo.softDeleteByIds(ids);
	    }

	    @Override
	    public void softDeleteDocTypeMaster(List<Long> ids) {
	    	glDocTypeMasterRepository.softDeleteByIds(ids);
	    }

	    @Override
	    public void softDeleteSupplyTypeMaster(List<Long> ids) {
	    	glMasterSupplyTypeRepository.softDeleteByIds(ids);
	    }

	    @Override
	    public void softDeleteBusinessPlaceMaster(List<Long> ids) {
	    	glBusinessPlaceMasterRepository.softDeleteByIds(ids);
	    }

	    @Override
	    public void softDeleteTaxCodeMaster(List<Long> ids) {
	    	glTaxCodeMasterRepo.softDeleteByIds(ids);
	    }
		@Override
		public void saveGlMappingMasterList(
				List<GlCodeMappingMasterDto> dtoList, Long entityId) {   List<GlCodeMappingMasterEntity> updatedEntities = new ArrayList<>();
			    List<Long> updateIds = new ArrayList<>();

			    for (GlCodeMappingMasterDto dto : dtoList) {
			        if (dto.getId() != null) {
			            Optional<GlCodeMappingMasterEntity> optional = glCodeMappingMasterRepository.findById(dto.getId());
			            if (optional.isPresent()) {
			                GlCodeMappingMasterEntity entity = optional.get();
			                entity.setBaseHeaders(dto.getBaseHeader());
			                entity.setInputFileHeaders(dto.getInputFileHeaders());
			                entity.setUpdatedDate(LocalDateTime.now());
			                entity.setIsActive(true);
			                updatedEntities.add(entity);
			            }
			        } /*else {
			            GlCodeMappingMasterEntity entity = new GlCodeMappingMasterEntity();
			            entity.setBaseHeaders(dto.getBaseHeader());
			            entity.setInputFileHeaders(dto.getInputFileHeaders());
			            entity.setEntityId(entityId);
			            entity.setCreatedDate(LocalDateTime.now());
			            entity.setUpdatedDate(LocalDateTime.now());
			            entity.setIsActive(true);
			            entity.setFileType("GL Mapping");
			            updatedEntities.add(entity);

			            List<GlCodeMappingMasterEntity> duplicates =
			                glCodeMappingMasterRepository.findByBaseHeadersAndInputFileHeadersAndIsActiveTrue(
			                    dto.getBaseHeader(), dto.getInputFileHeaders());

			            if (!duplicates.isEmpty()) {
			                updateIds.add(duplicates.get(0).getId());
			            }
			        }*/
			    }

			    glCodeMappingMasterRepository.saveAll(updatedEntities);
			   // glCodeMappingMasterRepository.softDeleteByIds(updateIds);
			    }
	    
	   
	    
}
