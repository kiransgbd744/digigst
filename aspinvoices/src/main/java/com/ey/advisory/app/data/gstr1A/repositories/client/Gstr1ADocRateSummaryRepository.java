package com.ey.advisory.app.data.gstr1A.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ADocRateSummary;

/**
 * This class is responsible for repository operations of Doc Rate Summary
 * Entity
 * 
 * @author Shashikant.Shukla
 *
 */
@Repository("Gstr1ADocRateSummaryRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1ADocRateSummaryRepository
		extends JpaRepository<Gstr1ADocRateSummary, Long>,
		JpaSpecificationExecutor<Gstr1ADocRateSummary> {

}
