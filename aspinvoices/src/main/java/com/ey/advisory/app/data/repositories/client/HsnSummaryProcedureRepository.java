package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.service.hsn.summary.HsnSummaryProcedureEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("HsnSummaryProcedureRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface HsnSummaryProcedureRepository
		extends JpaRepository<HsnSummaryProcedureEntity, Long>,
		JpaSpecificationExecutor<HsnSummaryProcedureEntity> {

	@Query("Select entity from HsnSummaryProcedureEntity entity WHERE"
			+ " isActive=TRUE")
	public List<HsnSummaryProcedureEntity> findProcedure();

}
