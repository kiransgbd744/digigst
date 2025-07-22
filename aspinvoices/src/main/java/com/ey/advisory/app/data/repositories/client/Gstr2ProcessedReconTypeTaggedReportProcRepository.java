package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.recon.type.tagging.report.Gstr2ProcessedReconTypeTaggedReportProcEntity;

/**
 * @author Vishal.verma
 *
 */

@Repository("Gstr2ProcessedReconTypeTaggedReportProcRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr2ProcessedReconTypeTaggedReportProcRepository
		extends JpaRepository<Gstr2ProcessedReconTypeTaggedReportProcEntity, Integer>,
		JpaSpecificationExecutor<Gstr2ProcessedReconTypeTaggedReportProcEntity> {

	@Query("Select entity from Gstr2ProcessedReconTypeTaggedReportProcEntity entity WHERE"
			+ " isActive = true")
	public List<Gstr2ProcessedReconTypeTaggedReportProcEntity> findProcedure();

}
