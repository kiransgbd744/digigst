/**
 * 
 */
package com.ey.advisory.core.async.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.FeedbackConfigQuestionEntity;

/**
 * @author Siva.Reddy
 *
 */
@Repository("FeedbackConfigQuestionRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface FeedbackConfigQuestionRepository
		extends CrudRepository<FeedbackConfigQuestionEntity, Long> {

	public List<FeedbackConfigQuestionEntity> findByIsActiveTrue();

	@Query("SELECT questionCode FROM FeedbackConfigQuestionEntity where id=:id")
	String findQuestionCode(@Param("id") Long id);

	List<FeedbackConfigQuestionEntity> findByQuestionTypeAndIsActiveTrue(
			String questionType);

}
