package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GstrEmailLogEntity;

@Repository("GstrEmailLogRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface GstrEmailLogRepository
		extends JpaRepository<GstrEmailLogEntity, Long>,
		JpaSpecificationExecutor<GstrEmailLogEntity> {

}
