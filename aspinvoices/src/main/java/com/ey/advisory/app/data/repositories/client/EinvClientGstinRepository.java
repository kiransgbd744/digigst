package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.EinvGstinClientEntity;

@Repository("EinvClientGstinRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EinvClientGstinRepository
		extends JpaRepository<EinvGstinClientEntity, Long>,
		JpaSpecificationExecutor<EinvGstinClientEntity> {

	List<EinvGstinClientEntity> findByisSynced(boolean b);

	@Modifying
	@Query("UPDATE EinvGstinClientEntity SET cancelledDate = :cancelledDate, "
			+ "createdDate = :modifiedDate, isSynced = false where gstin = :gstin")
	void updateCancellationDate(@Param("gstin") String cgstin,
			@Param("cancelledDate") String cancelDate,
			@Param("modifiedDate") LocalDateTime modifiedDate);

}
