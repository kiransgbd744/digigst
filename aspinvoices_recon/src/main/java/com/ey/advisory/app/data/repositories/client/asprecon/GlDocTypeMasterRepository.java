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

import com.ey.advisory.app.data.entities.client.asprecon.GlDocTypeMasterEntity;

@Repository("GlDocTypeMasterRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface GlDocTypeMasterRepository extends JpaRepository<GlDocTypeMasterEntity, Long>,
JpaSpecificationExecutor<GlDocTypeMasterEntity>{

	List<GlDocTypeMasterEntity> findByEntityIdAndIsActiveTrue(Long entityId);
	List<GlDocTypeMasterEntity> findByFileTypeAndIsActiveTrue(String fileType);
	
	List<GlDocTypeMasterEntity> findByDocTypeAndDocTypeMsAndIsActiveTrue(String docType, String docTypeMs);
	
	@Modifying
    @Query("UPDATE GlDocTypeMasterEntity e SET e.isActive = false WHERE e.id IN :ids")
    void softDeleteByIds(@Param("ids") List<Long> ids);
	
	
	@Query("SELECT e.id FROM GlDocTypeMasterEntity e WHERE e.fileType = :fileType AND e.isActive = true")
	List<Long> findActiveIdsByFileType(@Param("fileType") String fileType);

	List<GlDocTypeMasterEntity> findByFileIdAndFileTypeAndIsActiveFalse(String fileId, String fileType);

	List<GlDocTypeMasterEntity> findByIsActiveTrue();
	
	@Query("SELECT e FROM GlDocTypeMasterEntity e WHERE CONCAT(e.docType, '||', e.docTypeMs) IN :compositeKeys AND e.isActive = true")
	List<GlDocTypeMasterEntity> findByCompositeKeys(@Param("compositeKeys") Set<String> compositeKeys);

}