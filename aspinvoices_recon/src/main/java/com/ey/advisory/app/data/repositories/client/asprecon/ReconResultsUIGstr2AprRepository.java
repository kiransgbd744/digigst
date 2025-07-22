package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.ReconResultsUIGstr2AprEntity;

@Repository("ReconResultsUIGstr2BprRepository")
@Transactional(value = "clientTransactionManager",
propagation = Propagation.REQUIRED)
public interface ReconResultsUIGstr2AprRepository
		extends JpaRepository<ReconResultsUIGstr2AprEntity, Long>,
		JpaSpecificationExecutor<ReconResultsUIGstr2AprEntity> {
}