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

import com.ey.advisory.app.data.entities.client.asprecon.GlBusinessPlaceMasterEntity;

@Repository("GlBusinessPlaceMasterRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface GlBusinessPlaceMasterRepository
		extends
			JpaRepository<GlBusinessPlaceMasterEntity, Long> ,
			JpaSpecificationExecutor<GlBusinessPlaceMasterEntity> {	
	
	 List<GlBusinessPlaceMasterEntity> findByEntityIdAndIsActiveTrue(Long entityId);
	 
	 List<GlBusinessPlaceMasterEntity> findByFileTypeAndIsActiveTrue(String fileType);
	 
	 List<GlBusinessPlaceMasterEntity> findByGstinAndIsActiveTrue(String gstin);
		
	 
	 @Modifying
	    @Query("UPDATE GlBusinessPlaceMasterEntity e SET e.isActive = false WHERE e.id IN :ids")
	    void softDeleteByIds(@Param("ids") List<Long> ids);
	 
	 @Query("SELECT e.id FROM GlBusinessPlaceMasterEntity e WHERE e.fileType = :fileType AND e.isActive = true")
	 List<Long> findActiveIdsByFileType(@Param("fileType") String fileType);
	 
	 List<GlBusinessPlaceMasterEntity> findByFileIdAndFileTypeAndIsActiveFalse(String fileId, String fileType);

	 List<GlBusinessPlaceMasterEntity> findByIsActiveTrue();
	 
	 List<GlBusinessPlaceMasterEntity> findByGstinInAndIsActiveTrue(Set<String> gstins);
}
