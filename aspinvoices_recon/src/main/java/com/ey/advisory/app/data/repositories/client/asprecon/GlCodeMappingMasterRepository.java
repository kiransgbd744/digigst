/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.data.repositories.client.asprecon;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.GlCodeMappingMasterEntity;

@Repository("GlCodeMappingMasterRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface GlCodeMappingMasterRepository
		extends
			JpaRepository<GlCodeMappingMasterEntity, Long>,
			JpaSpecificationExecutor<GlCodeMappingMasterEntity> {
	@Modifying
	@Query("UPDATE GlCodeMappingMasterEntity e SET e.isActive = false WHERE e.id IN (:ids)")
	void softDeleteByIds(@Param("ids") List<Long> ids);

	List<GlCodeMappingMasterEntity> findByBaseHeadersAndInputFileHeadersAndIsActiveTrue(
			String baseHeader, String inputFileHeader);

	Collection<GlCodeMappingMasterEntity> findByFileTypeAndIsActiveTrue(String fileType);
	
	List<GlCodeMappingMasterEntity> findByIsActiveTrue();
}
