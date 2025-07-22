/**
 * 
 */
package com.ey.advisory.core.async.repositories.master;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.FeedbackConfigAnswerEntity;

/**
 * @author Siva.Reddy
 *
 */
@Repository("FeedbackConfigAnswerRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface FeedbackConfigAnswerRepository
		extends CrudRepository<FeedbackConfigAnswerEntity, Long> {

	public List<FeedbackConfigAnswerEntity> findByQuestionType(
			String questionType);
}
