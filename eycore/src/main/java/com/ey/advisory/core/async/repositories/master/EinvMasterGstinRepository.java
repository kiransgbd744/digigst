package com.ey.advisory.core.async.repositories.master;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.EinvGstinMasterEntity;

/**
 * 
 * @author Ravindra
 *
 */
@Repository("EinvMasterGstinRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EinvMasterGstinRepository
		extends JpaRepository<EinvGstinMasterEntity, Long>,
		JpaSpecificationExecutor<EinvGstinMasterEntity> {

	void deleteBySource(String string);

	EinvGstinMasterEntity findByGstin(String gstin);

	Optional<EinvGstinMasterEntity> findByPan(String pan);

	@Modifying
	@Query("UPDATE EinvGstinMasterEntity SET cancelledDate = :cancelledDate, "
			+ "modifiedDate = :modifiedDate where gstin = :gstin")
	void updateCancellationDate(@Param("gstin") String gstin,
			@Param("cancelledDate") String cancelledDate,
			@Param("modifiedDate") LocalDateTime modifiedDate);

	@Query("SELECT distinct pan FROM EinvGstinMasterEntity")
	List<String> getAllDistinctPan();

}
