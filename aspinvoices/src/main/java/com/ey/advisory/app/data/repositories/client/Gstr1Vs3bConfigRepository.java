package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1vs3bConfigEntity;

@Repository("Gstr1Vs3bConfigRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1Vs3bConfigRepository
		extends JpaRepository<Gstr1vs3bConfigEntity, Long>,
		JpaSpecificationExecutor<Gstr1vs3bConfigEntity> {

	@Modifying
	@Query(" update Gstr1vs3bConfigEntity set status =:status, completedOn =:completedOn, filePath =:filePath "
			+ " where configId =:configId")
	public void updateGstr1Vs3BReconStatus(@Param("status") String status,
			@Param("completedOn") LocalDateTime completedOn,
			@Param("configId") Long configId,
			@Param("filePath") String filePath);

}
