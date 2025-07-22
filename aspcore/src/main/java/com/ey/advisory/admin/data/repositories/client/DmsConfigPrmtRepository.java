package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.DmsConfigPrmtEntity;

/**
 * @author ashutosh.kar
 *
 */
@Repository("DmsConfigPrmtRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface DmsConfigPrmtRepository 
  			extends CrudRepository<DmsConfigPrmtEntity, Long>{
	
	@Query("SELECT max(e.id) FROM  DmsConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode AND "
			+ " e.confgPrmtId=:confgPrmtId AND e.isDelete= false")
	Long findByGroupCodeAndConfgPrmtId(@Param("groupCode") String groupCode,
			@Param("confgPrmtId") Long confgPrmtId);
	
	@Query("SELECT e.paramValue FROM DmsConfigPrmtEntity e "
			+ "WHERE e.id=:id AND e.isDelete= false ")
	String findAnswerById(@Param("id") Long id);
	
	@Query("SELECT e.paramValue FROM  DmsConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode "
			+ "AND e.confgPrmtId =:confgPrmtId AND e.id=:id AND "
			+ "e.paramValue=:paramValue AND e.isDelete=false ")
	String findParamValByGroupCodeAndIdAndAnswer(
			@Param("groupCode") String groupCode,
			@Param("confgPrmtId") Long confgPrmtId, @Param("id") Long id,
			@Param("paramValue") String paramValue);
	
	@Transactional
	@Modifying
	@Query("UPDATE DmsConfigPrmtEntity SET "
			+ "derivedAnswer = :derivedAnswer, paramValue=:paramValue where "
			+ "groupId=:groupId AND paramValKeyId='G9' AND isDelete= false")
	void updateDerivedAnswer(@Param("groupId") Long groupId,
			@Param("derivedAnswer") Integer derivedAnswer,
			@Param("paramValue") String paramValue);
	
	@Transactional
	@Modifying
	@Query("UPDATE DmsConfigPrmtEntity SET isDelete=true WHERE "
			+ "id IN (:id)")
	public void deleteExitingAnswerForQuestion(@Param("id") List<Long> id);

	@Query("SELECT max(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN DmsConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='D1' AND q.isActive=true "
			+ "AND q.questionType='R' AND e.isDelete= false")
	public String findByGroupLevelDmsRoles();
}
