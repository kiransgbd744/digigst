package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.GroupConfigPrmtEntity;

/**
 * @author Umesha.M
 *
 */
@Repository("GroupConfigPrmtRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GroupConfigPrmtRepository
		extends CrudRepository<GroupConfigPrmtEntity, Long> {

	@Query("SELECT max(e.paramValue) FROM  GroupConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode "
			+ "AND paramValKeyId =:paramValKeyId AND isDelete=false")
	String findParamValByGroupCodeAndIsDeleteFalse(
			@Param("groupCode") String groupCode,
			@Param("paramValKeyId") String paramKeyId);

	@Query("SELECT max(e.paramValue) FROM  GroupConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode " + "AND confgPrmtId=:confgPrmtId "
			+ "AND isDelete=false")
	String findAnswerCodeByGroupCodeAndEntityIdAndQuestionId(
			@Param("groupCode") String groupCode,
			@Param("confgPrmtId") Long confgPrmtId);

	@Transactional
	@Modifying
	@Query("UPDATE GroupConfigPrmtEntity SET isDelete=true WHERE "
			+ "id IN (:id)")
	public void deleteExitingAnswerForQuestion(@Param("id") List<Long> id);

	@Query("SELECT e FROM  GroupConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode AND "
			+ " e.paramValKeyId=:paramkryId " + "AND e.isDelete= false")
	GroupConfigPrmtEntity findByGroupCodeAndparamkryIdAndIsDeleteFalse(
			@Param("groupCode") String groupCode,
			@Param("paramkryId") String paramkryId);

	@Query("SELECT e.paramValue FROM GroupConfigPrmtEntity e "
			+ "WHERE e.id=:id AND e.isDelete= false ")
	String findAnswerById(@Param("id") Long id);

	@Query("SELECT max(e.id) FROM  GroupConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode AND "
			+ " e.confgPrmtId=:confgPrmtId AND e.isDelete= false")
	Long findByGroupCodeAndConfgPrmtId(@Param("groupCode") String groupCode,
			@Param("confgPrmtId") Long confgPrmtId);

	@Query("SELECT e FROM  GroupConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode AND e.isDelete= false")
	List<GroupConfigPrmtEntity> findByGroupCode(
			@Param("groupCode") String groupCode);

	@Query("SELECT e.derivedAnswer FROM  GroupConfigPrmtEntity e "
			+ "WHERE e.paramValKeyId='G9' AND e.isDelete= false")
	List<Object> findByQtnCode();

	@Transactional
	@Modifying
	@Query("UPDATE GroupConfigPrmtEntity SET "
			+ "derivedAnswer = :derivedAnswer, paramValue=:paramValue where "
			+ "groupId=:groupId AND paramValKeyId='G9' AND isDelete= false")
	void updateDerivedReturnPeriod(@Param("groupId") Long groupId,
			@Param("derivedAnswer") Integer derivedAnswer,
			@Param("paramValue") String paramValue);

	@Query("SELECT max(e.paramValue) FROM  GroupConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode "
			+ "AND e.confgPrmtId =:confgPrmtId AND e.id=:id AND "
			+ "e.paramValue=:paramValue AND e.isDelete=false ")
	String findParamValByGroupCodeAndIdAndAnswer(
			@Param("groupCode") String groupCode,
			@Param("confgPrmtId") Long confgPrmtId, @Param("id") Long id,
			@Param("paramValue") String paramValue);

	@Transactional
	@Modifying
	@Query("UPDATE GroupConfigPrmtEntity "
			+ " SET isDelete= true WHERE groupCode=:groupCode AND "
			+ " confgPrmtId=:confgPrmtId AND isDelete= false")
	void updateIsDeleteFlagFlase(@Param("groupCode") String groupCode,
			@Param("confgPrmtId") Long confgPrmtId);

	@Query("SELECT max(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G25' " + "AND q.isActive=true "
			+ "AND q.questionType='R' " + "AND e.isDelete= false")
	public String findByGroupLevelOpted();
	
	@Query("SELECT max(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G36' AND q.isActive=true "
			+ "AND q.questionType='R' AND e.isDelete= false")
	public String findByGroupLevelImsRoles();

	@Query("SELECT max(g.paramValue) FROM GroupConfigPrmtEntity g "
			+ "WHERE g.confgPrmtId = (SELECT c.id FROM ConfigQuestionEntity c "
			+ "WHERE c.questionCode = 'G27') " + "AND g.isDelete = false")
	public String findAnswerForMultiSupplyType();

	@Query("SELECT max(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G28' " + "AND q.isActive=true "
			+ "AND q.questionType='R' " + "AND e.isDelete= false")
	public String findQROptedOption();

	@Query("SELECT max(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId=:questionCode " + "AND q.isActive=true "
			+ "AND q.questionType=:questionType " + "AND e.isDelete= false")
	public String findAnswerByQuestionCodeAndQuestionType(
			@Param("questionCode") String questionCode,
			@Param("questionType") String questionType);
	
	@Query("SELECT max(g.paramValue)  FROM GroupConfigPrmtEntity g " +
	           "WHERE g.confgPrmtId = (SELECT c.id FROM ConfigQuestionEntity c " +
	           "WHERE c.questionCode = 'G28') AND g.groupCode=:groupCode " +
	           "AND g.isDelete = false")
	public String findAnswerForQRCodeValidator(@Param("groupCode") String groupCode);
	
	
	@Query("SELECT e FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G38' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Select GetCall Frequency?' "
			+ "AND e.isDelete= false")
	public  GroupConfigPrmtEntity findEntityByEntityAutoImsGetCallFrequency();
	
	@Query("SELECT e FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G38' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription= 'Select Timestamp?' "
			+ "AND e.isDelete= false")
	public  GroupConfigPrmtEntity findEntityByEntityAutoImsGetCallTimestampFrequency();
	
	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId =:paramKeyId "
			+ "AND q.isActive=true " + "AND q.questionDescription =:desc  "
			+ "AND e.isDelete= false")
	public String findByEntityAutoInitiateGetCall(
			@Param("desc") String desc,
			@Param("paramKeyId") String paramKeyId);
	
	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G38' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Select GetCall Frequency?' "
			+ "AND e.isDelete= false")
	public String findAutoImsGetCallFrequency();
	
	@Query("SELECT e.paramValue FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G38' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Select Timestamp?' "
			+ "AND e.isDelete= false")
	public String getTimeStampForEntityAutoIms();
	
	@Query("SELECT max(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G36' AND q.isActive=true "
			+ "AND q.questionType='M' AND e.groupCode =:groupCode AND e.isDelete= false")
	String findIms2BvsPROptionOpted(@Param("groupCode") String groupCode);
	
	@Query("SELECT max(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G36' AND q.isActive=true "
			+ "AND q.questionType='SR' AND e.groupCode =:groupCode AND e.isDelete= false")
	String findByImsAutoReconResponseParam(@Param("groupCode") String groupCode);
	
	@Query("SELECT e FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G40' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Select GetCall Frequency?' "
			+ "AND e.isDelete= false")
	public  GroupConfigPrmtEntity findEntityByEntityAutoImsAutoSaveFrequency();
	
	@Query("SELECT e FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G40' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription= 'Select Timestamp?' "
			+ "AND e.isDelete= false")
	public  GroupConfigPrmtEntity findEntityByEntityAutoImsAutoSaveTimestampFrequency();
	
	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId =:paramKeyId "
			+ "AND q.isActive=true " + "AND q.questionDescription =:desc  "
			+ "AND e.isDelete= false")
	public String findByEntityAutoInitiateAutoSave(
			@Param("desc") String desc,
			@Param("paramKeyId") String paramKeyId);
	
	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G40' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Select Auto Save Frequency?' "
			+ "AND e.isDelete= false")
	public String findAutoImsAutoSaveFrequency();
	
	@Query("SELECT e.paramValue FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G40' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Select Timestamp?' "
			+ "AND e.isDelete= false")
	public String getTimeStampForEntityAutoSaveIms();
	
	@Query("SELECT max(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN GroupConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='G40' AND q.isActive=true "
			+ "AND q.questionType='R' AND e.isDelete= false")
	public String findByGroupLevelImsSaveRoles();
	
}
