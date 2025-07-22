package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.service.days.revarsal180.Reversal180DaysResponseStageEntity;

/**
 * @author Jithendra.B
 *
 */
@Repository("Reversal180DaysResponseStageRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Reversal180DaysResponseStageRepository
		extends JpaRepository<Reversal180DaysResponseStageEntity, Long>,
		JpaSpecificationExecutor<Reversal180DaysResponseStageEntity> {

	public List<Reversal180DaysResponseStageEntity> findByFileId(Long fileId);
}
