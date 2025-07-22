package com.ey.advisory.core.async.repositories.master;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.CustomisedReportMasterEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("CustomisedReportMasterRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface CustomisedReportMasterRepo
		extends JpaRepository<CustomisedReportMasterEntity, Long>,
		JpaSpecificationExecutor<CustomisedReportMasterEntity> {

	public Optional<CustomisedReportMasterEntity> findByReportNameAndIsActiveTrue(
			String reportName);

}
