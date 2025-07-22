package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.DocRateSummary;

/**
 * This class is responsible for repository operations of Doc Rate Summary Entity
 * @author Mohana.Dasari
 *
 */
@Repository("DocRateSummaryRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface DocRateSummaryRepository 
	extends JpaRepository<DocRateSummary, Long>,
	JpaSpecificationExecutor<DocRateSummary>{

}
