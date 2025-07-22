package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.service.days.revarsal180.Reversal180DaysResponseErrorEntity;

/**
 * @author Jithendra.B
 *
 */
@Repository("Reversal180DaysResponseErrorRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Reversal180DaysResponseErrorRepository
		extends JpaRepository<Reversal180DaysResponseErrorEntity, Long>,
		JpaSpecificationExecutor<Reversal180DaysResponseErrorEntity> {

	List<Reversal180DaysResponseErrorEntity> findByFileId(Long fileId);
}
