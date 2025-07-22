/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.data.repositories.client.asprecon;
import java.math.BigDecimal;
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

import com.ey.advisory.app.data.entities.client.asprecon.GlTaxCodeMasterEntity;

@Repository("GlTaxCodeMasterRepo")
@Transactional(propagation = Propagation.REQUIRED)
public interface GlTaxCodeMasterRepo extends JpaRepository<GlTaxCodeMasterEntity, Long>,
JpaSpecificationExecutor<GlTaxCodeMasterEntity> {
	
	List<GlTaxCodeMasterEntity> findByEntityIdAndIsActiveTrue(Long entityId);
	
	List<GlTaxCodeMasterEntity> findByFileTypeAndIsActiveTrue(String fileType);
	
	List<GlTaxCodeMasterEntity> findByTransactionTypeGlAndTaxRateMsAndIsActiveTrue(String transactionType , BigDecimal taxRateMs);
	
	@Modifying
	@Query("UPDATE GlTaxCodeMasterEntity e SET e.isActive = false WHERE e.id IN :ids")
	void softDeleteByIds(@Param("ids") List<Long> ids);
	
	@Query("SELECT e.id FROM GlTaxCodeMasterEntity e WHERE e.fileType = :fileType AND e.isActive = true")
	List<Long> findActiveIdsByFileType(@Param("fileType") String fileType);
	
	List<GlTaxCodeMasterEntity> findByFileIdAndFileTypeAndIsActiveFalse(String fileId, String fileType);

	List<GlTaxCodeMasterEntity> findByIsActiveTrue();
	
	@Query("SELECT e FROM GlTaxCodeMasterEntity e WHERE CONCAT(e.transactionTypeGl, '||', e.taxRateMs) IN :compositeKeys AND e.isActive = true")
	List<GlTaxCodeMasterEntity> findByCompositeKeys(@Param("compositeKeys") Set<String> compositeKeys);

}
