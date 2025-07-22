package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.recon.type.tagging.report.Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity;

/**
 * @author Vishal.verma
 *
 */

@Repository("Gstr2ProcessedReconTypeTaggedReportGstinDetailsRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr2ProcessedReconTypeTaggedReportGstinDetailsRepository
		extends
		JpaRepository<Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity, Integer>,
		JpaSpecificationExecutor<Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity> {

	public List<Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity> findByReportDwndId(
			Long reportDwndId);

}
