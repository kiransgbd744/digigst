/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.data.repositories.client.asprecon;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.GlMasterSupplyTypeEntity;

@Repository("GlMasterSupplyTypeRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface GlMasterSupplyTypeRepository extends JpaRepository<GlMasterSupplyTypeEntity, Long> ,
JpaSpecificationExecutor<GlMasterSupplyTypeEntity> {

	List<GlMasterSupplyTypeEntity> findByEntityIdAndIsActiveTrue(Long entityId);
	
	List<GlMasterSupplyTypeEntity> findByFileTypeAndIsActiveTrue(String fileType);
	
	List<GlMasterSupplyTypeEntity> findBySupplyTypeRegAndSupplyTypeMsAndIsActiveTrue(String supplyTupeReg, String supplyTypeReg);
	
	@Modifying
    @Query("UPDATE GlMasterSupplyTypeEntity e SET e.isActive = false WHERE e.id IN :ids")
    void softDeleteByIds(@Param("ids") List<Long> ids);
	
	@Query("SELECT e.id FROM GlMasterSupplyTypeEntity e WHERE e.fileType = :fileType AND e.isActive = true")
	List<Long> findActiveIdsByFileType(@Param("fileType") String fileType);
	
	List<GlMasterSupplyTypeEntity> findByFileIdAndFileTypeAndIsActiveFalse(String fileId, String fileType);

	List<GlMasterSupplyTypeEntity> findByIsActiveTrue();
	
	@Query("SELECT e FROM GlMasterSupplyTypeEntity e WHERE e.isActive = true AND CONCAT(e.supplyTypeReg, '||', e.supplyTypeMs) IN :keys")
	List<GlMasterSupplyTypeEntity> findByCompositeKeys(@Param("keys") Set<String> keys);

	
}
