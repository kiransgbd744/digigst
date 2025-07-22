package com.ey.advisory.admin.data.repositories.client;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ConfigQuestionEntity;

@Repository("ConfigQuestionRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ConfigQuestionRepository
		extends CrudRepository<ConfigQuestionEntity, Long> {

	@Query("SELECT questionCode FROM ConfigQuestionEntity where id=:id")
	String findQuestionCode(@Param("id") final Long id);
	
	@Query("SELECT c from ConfigQuestionEntity c")
	List<ConfigQuestionEntity> getAllInwardAndOutwardQuestions();
	
	@Query("SELECT id FROM ConfigQuestionEntity where questionCode =:questionCode")
	Long findQuestionId(@Param("questionCode") String questionCode);
	
	ConfigQuestionEntity findByQuestionCodeAndQuestionType(String questionCode, String questionType);
	
	@Query("SELECT id FROM ConfigQuestionEntity where questionDescription =:questionDescription and questionCode =:questionCode")
	Long findQuestionIdByQuesDes (@Param("questionDescription") String questionDescription,@Param("questionCode") String questionCode);
	
	Optional<ConfigQuestionEntity> findById(Long id);
	
}
