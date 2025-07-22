package com.ey.advisory.admin.data.repositories.client;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;

/**
 * @author Umesha.M
 *
 */
@Repository("EntityConfigPrmtRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EntityConfigPrmtRepository
		extends CrudRepository<EntityConfigPrmtEntity, Long> {

	@Query("SELECT max(e.paramValue) FROM  EntityConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode AND e.entityId=:entityId "
			+ "AND paramValKeyId =:paramValKeyId AND isDelete=false")
	String findParamValByGroupCodeAndEntityIdAndIsDeleteFalse(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId,
			@Param("paramValKeyId") String paramKeyId);

	@Query("SELECT max(e.paramValue) FROM  EntityConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode AND e.entityId=:entityId "
			+ "AND confgPrmtId=:confgPrmtId " + "AND isDelete=false")
	String findAnswerCodeByGroupCodeAndEntityIdAndQuestionId(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId,
			@Param("confgPrmtId") Long confgPrmtId);

	@Transactional
	@Modifying
	@Query("UPDATE EntityConfigPrmtEntity SET isDelete=true WHERE "
			+ "id IN (:id) AND entityId =:entityId")
	public int deleteExitingAnswerForQuestion(@Param("id") List<Long> id,
			@Param("entityId") Long entityId);

	@Query("SELECT e FROM  EntityConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode AND "
			+ "e.entityId=:entityId AND e.paramValKeyId=:paramkryId "
			+ "AND e.isDelete= false")
	EntityConfigPrmtEntity findByGroupCodeAndEntityIdAndparamkryIdAndIsDeleteFalse(
			@Param("groupCode") String groupCode,
			@Param("entityId") long entityId,
			@Param("paramkryId") String paramkryId);

	@Query("SELECT e.paramValue FROM EntityConfigPrmtEntity e "
			+ "WHERE e.id=:id AND e.isDelete= false ")
	String findAnswerById(@Param("id") Long id);

	@Query("SELECT max(e.id) FROM  EntityConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode AND "
			+ "e.entityId=:entityId AND e.confgPrmtId=:confgPrmtId AND e.isDelete= false")
	Long findByGroupCodeAndEntityIdAndConfgPrmtId(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId,
			@Param("confgPrmtId") Long confgPrmtId);

	@Query("SELECT e FROM  EntityConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode AND e.isDelete= false")
	List<EntityConfigPrmtEntity> findByGroupCode(
			@Param("groupCode") String groupCode);

	@Query("SELECT e.derivedAnswer FROM  EntityConfigPrmtEntity e "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='G9' AND e.isDelete= false")
	List<Object> findByEntityQtnCode(@Param("entityId") List<Long> entityId);

	@Query("SELECT e.derivedAnswer FROM  EntityConfigPrmtEntity e "
			+ "WHERE e.paramValKeyId='G9' AND e.isDelete= false")
	List<Object> findByQtnCode();

	@Transactional
	@Modifying
	@Query("UPDATE EntityConfigPrmtEntity SET "
			+ "derivedAnswer = :derivedAnswer, paramValue=:paramValue where "
			+ "groupId=:groupId AND paramValKeyId='G9' AND isDelete= false")
	void updateDerivedReturnPeriod(@Param("groupId") Long groupId,
			@Param("derivedAnswer") Integer derivedAnswer,
			@Param("paramValue") String paramValue);

	@Query("SELECT MAX(e.paramValue) FROM EntityConfigPrmtEntity "
			+ "e WHERE e.paramValKeyId='G9' AND e.groupId=:groupId "
			+ " AND e.entityId=:entityId AND e.isDelete= false")
	String findMaxAnswer(@Param("groupId") Long groupId,
			@Param("entityId") Long entityId);

	@Query("SELECT MAX(e.paramValue) FROM EntityConfigPrmtEntity "
			+ "e WHERE e.paramValKeyId='G10' AND "
			+ "e.entityId=:entityId AND e.isDelete= false")
	String findMaxAnswerForQuestionCode(@Param("entityId") Long entityId);

	@Query("SELECT MAX(e.derivedAnswer) FROM EntityConfigPrmtEntity "
			+ "e WHERE e.paramValKeyId='G9' AND (e.groupId=:groupId "
			+ "OR e.entityId=:entityId) AND e.isDelete= false")
	Integer findMaxDerivedAnswer(@Param("groupId") Long groupId,
			@Param("entityId") Long entityId);

	@Query("SELECT e.paramValue FROM  EntityConfigPrmtEntity e "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='G10' AND e.isDelete= false")
	List<Object> findByEntityServiceCode(
			@Param("entityId") List<Long> entityId);

	@Query("SELECT e.paramValue FROM  EntityConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode AND e.entityId=:entityId "
			+ "AND e.confgPrmtId =:confgPrmtId AND e.id=:id AND "
			+ "e.paramValue=:paramValue AND e.isDelete=false ")
	String findParamValByGroupCodeAndEntityIdAndIdAndAnswer(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId,
			@Param("confgPrmtId") Long confgPrmtId, @Param("id") Long id,
			@Param("paramValue") String paramValue);

	@Transactional
	@Modifying
	@Query("UPDATE EntityConfigPrmtEntity "
			+ " SET isDelete= true WHERE groupCode=:groupCode AND "
			+ "entityId=:entityId AND confgPrmtId=:confgPrmtId AND isDelete= false")
	void updateIsDeleteFlagFlase(@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId,
			@Param("confgPrmtId") Long confgPrmtId);

	@Query("SELECT MAX(e.paramValue) FROM  EntityConfigPrmtEntity e "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='G12' AND e.isDelete= false")
	public String findByEntityServiceOptionsEIvoice(
			@Param("entityId") Long entityId);

	@Query("SELECT MAX(e.paramValue) FROM  EntityConfigPrmtEntity e "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='G13' AND e.isDelete= false")
	public String findByEntityServiceOptionsForEwb(
			@Param("entityId") Long entityId);

	@Query("SELECT MAX(e.paramValue) FROM  EntityConfigPrmtEntity e "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='G14' AND e.isDelete= false")
	public String findByEntityQRCode(@Param("entityId") Long entityId);

	@Query("SELECT e.entityId from EntityConfigPrmtEntity e "
			+ " WHERE e.paramValue = 'A' AND e.paramValKeyId =:paramValKeyId "
			+ " AND e.entityId IN :entityIds AND e.isDelete=false")
	public List<Long> getAllEntitiesOpted2B(
			@Param("entityIds") List<Long> entityIds,
			@Param("paramValKeyId") String paramValKeyId);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId =:paramKeyId "
			+ "AND q.isActive=true " + "AND q.questionDescription =:desc  "
			+ "AND e.isDelete= false")
	public String findByEntityAutoInitiateGetCall(
			@Param("entityId") Long entityId, @Param("desc") String desc,
			@Param("paramKeyId") String paramKeyId);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I26' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Initiate Get GSTR-2A Automation is required?' "
			+ "AND e.isDelete= false")
	public String findByEntityAutoGet2ACall(@Param("entityId") Long entityId);

	@Query("SELECT e.entityId FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.paramValKeyId='I26' " + "AND q.isActive=true "
			+ "AND q.questionDescription='Initiate Get GSTR-2A Automation is required?' "
			+ "AND e.isDelete= false AND e.paramValue = 'A'")
	public List<Long> findAllEntityOptedForAutoGet2A();

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I26' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Select GetCall Frequency?' "
			+ "AND e.isDelete= false")
	public String findByEntityAutoGet2AGetCall(
			@Param("entityId") Long entityId);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I50' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Select GetCall Frequency?' "
			+ "AND e.isDelete= false")
	public String findByEntityAutoInwardEinvoiceGetCallFrequency(
			@Param("entityId") Long entityId);

	@Query("SELECT e.paramValue FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I27' "
			+ "AND q.isActive=true " + "AND q.questionType='R' "
			+ "AND e.isDelete= false")
	public String findByOptedforAP(@Param("entityId") Long entityId);

	@Query("SELECT e.entityId from EntityConfigPrmtEntity e "
			+ " WHERE e.paramValue = 'A' AND e.paramValKeyId =:paramValKeyId "
			+ " AND e.entityId IN :entityIds AND e.isDelete=false AND confgPrmtId = :confgPrmtId")
	public List<Long> getAllEntitiesOpted2A(
			@Param("entityIds") List<Long> entityIds,
			@Param("paramValKeyId") String paramValKeyId,
			@Param("confgPrmtId") Long confgPrmtId);

	@Query("SELECT e.entityId from EntityConfigPrmtEntity e "
			+ " WHERE e.paramValue = 'B' AND e.paramValKeyId =:paramValKeyId "
			+ " AND e.entityId IN :entityIds AND e.isDelete=false")
	public List<Long> getAllEntitiesOptedUserRestriction(
			@Param("entityIds") List<Long> entityIds,
			@Param("paramValKeyId") String paramValKeyId);

	@Query("SELECT e.paramValue FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I26' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Select Timestamp?' "
			+ "AND e.isDelete= false")
	public String getTimeStampForEntityAutoGet2AGetCall(
			@Param("entityId") Long entityId);

	@Query("SELECT e.paramValue FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I50' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Select Timestamp?' "
			+ "AND e.isDelete= false")
	public String getTimeStampForEntityAutoInwardEinvoice(
			@Param("entityId") Long entityId);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ " INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ " WHERE e.entityId=:entityId AND e.paramValKeyId='I35' "
			+ " AND q.isActive=true "
			+ " AND q.questionDescription='Do you want to enable Auto recon (6AvsPR) under AIM? (This has to be kept ''No'' if Auto recon is not required and any other function is required under AIM like QR Code, Compliance History, E-invoice Validator)' "
			+ " AND e.isDelete= false")
	public String findByOpted6aRecon(@Param("entityId") Long entityId);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ " INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ " WHERE e.entityId=:entityId and e.paramValue=:paramValue "
			+ " AND q.isActive=true "
			+ " AND q.questionDescription= :questionDesc"
			+ " AND e.isDelete= false")
	public String findAnsbyQuestion(@Param("entityId") Long entityId,
			@Param("paramValue") String paramValue,
			@Param("questionDesc") String questionDesc);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ " INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ " WHERE e.entityId=:entityId " + " AND q.isActive=true "
			+ " AND q.questionDescription= :questionDesc"
			+ " AND e.isDelete= false")
	public String findAnsbyQuestion(@Param("entityId") Long entityId,
			@Param("questionDesc") String questionDesc);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ " INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ " WHERE e.entityId=:entityId " + " AND q.isActive=true "
			+ " AND q.questionDescription= :questionDesc AND q.questionCategory= :questionCategory"
			+ " AND e.isDelete= false")
	public String findAnsbyQuestion1(@Param("entityId") Long entityId,
			@Param("questionDesc") String questionDesc,
			@Param("questionCategory") String questionCategory);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I34' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Do you want to opt for Auto GSTR 6A get call?' "
			+ "AND e.isDelete= false")
	public String findByEntityAutoGet6ACall(@Param("entityId") Long entityId);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I36' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='6A get call frequency?' "
			+ "AND e.isDelete= false")
	public String findByEntityAutoGet6AGetCall(
			@Param("entityId") Long entityId);

	@Query("SELECT e.entityId  from EntityConfigPrmtEntity e "
			+ " WHERE e.paramValue = 'B' AND e.paramValKeyId =:paramValKeyId "
			+ " AND e.entityId =:entityId AND e.isDelete=false")
	public Long getIfFifoSelected(@Param("entityId") Long entityId,
			@Param("paramValKeyId") String paramValKeyId);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ " INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ " WHERE e.entityId=:entityId AND e.paramValKeyId='O28' "
			+ " AND q.isActive=true " + " AND q.questionDescription="
			+ " 'Do you want Outward - Rate level Report (Limited column)?' "
			+ " AND e.isDelete= false")
	public String findByOptedRateLevelReport(@Param("entityId") Long entityId);

	@Query("SELECT MAX(e.paramValue) FROM EntityConfigPrmtEntity e "
			+ " WHERE e.entityId=:entityId AND e.paramValKeyId='O35' "
			+ " AND e.isDelete= false")
	public String findByOptedHsnSummaryReport(@Param("entityId") Long entityId);

	@Query("SELECT e.paramValue FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I44' "
			+ "AND q.isActive=true " + "AND q.questionType='R' "
			+ "AND e.isDelete= false")
	public String findByEntityAndQuestionType(@Param("entityId") Long entityId);

	@Query("SELECT e.paramValue FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I44' "
			+ "AND q.isActive=true " + "AND q.questionType='SR' "
			+ "AND e.isDelete= false")
	public String findByEntityAndQuestionTypeSR(
			@Param("entityId") Long entityId);

	@Query("SELECT max(e.paramValue) FROM  EntityConfigPrmtEntity e "
			+ "WHERE e.groupCode=:groupCode AND e.entityId=(select g.entityId "
			+ "from GSTNDetailEntity g where g.gstin=:gstin)"
			+ "AND e.paramValKeyId =:paramValKeyId AND e.isDelete=false")
	public String findOnboardingAnswer(@Param("groupCode") String groupCode,
			@Param("gstin") String gstin,
			@Param("paramValKeyId") String paramKeyId);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I51' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Do you want to enable Inward E-invoice functionality?' "
			+ "AND e.isDelete= false")
	public String findByInwardEinvoiceOpted(@Param("entityId") Long entityId);

	@Query("SELECT e FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I50' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Select GetCall Frequency?' "
			+ "AND e.isDelete= false")
	public EntityConfigPrmtEntity findEntityByEntityAutoInwardEinvoiceGetCallFrequency(
			@Param("entityId") Long entityId);

	@Query("SELECT e FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I50' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription= 'Select Timestamp?' "
			+ "AND e.isDelete= false")
	public EntityConfigPrmtEntity findEntityByEntityAutoInwardEinvoiceGetCallTimestampFrequency(
			@Param("entityId") Long entityId);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='O42' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Do you want to enable Auto-Email alert functionality to client users from DigiGST?' "
			+ "AND e.isDelete= false")
	public String findByDRC01AutoEmailOpted(@Param("entityId") Long entityId);

	@Query("SELECT e.entityId FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId IN (:entityId) AND e.paramValKeyId='I51' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription='Do you want to enable Inward E-invoice functionality?' "
			+ "AND e.isDelete= false")
	public List<Long> findEntitiesOptedInwardEinvoice(
			@Param("entityId") List<Long> entityId);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId=:entityId AND e.paramValKeyId='I19' "
			+ "AND q.isActive=true "
			+ "AND q.questionDescription=:questionDesc "
			+ "AND e.isDelete= false")
	public String findByEntityGstr3b(@Param("entityId") Long entityId,
			@Param("questionDesc") String questionDesc);

	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ " INNER JOIN EntityConfigPrmtEntity e ON q.id = e.confgPrmtId "
			+ " WHERE e.entityId = :entityId " + " AND q.isActive = true "
			+ " AND q.questionDescription = :questionDesc "
			+ " AND q.questionType = :questionType "
			+ " AND e.isDelete = false")
	public String findAnsbyQuestionGLRecon(@Param("entityId") Long entityId,
			@Param("questionDesc") String questionDesc,
			@Param("questionType") String questionType);

	@Query("SELECT e.entityId FROM ConfigQuestionEntity q "
			+ "INNER JOIN EntityConfigPrmtEntity e ON q.id=e.confgPrmtId "
			+ "WHERE e.entityId IN (:entityId) AND e.paramValKeyId='I27' "
			+ "AND q.isActive=true AND e.paramValue = 'A' "
			+ "AND q.questionDescription LIKE 'Do you want to enable Auto recon (2AvsPR) under AIM?%' "
			+ "AND e.isDelete= false")
	public List<Long> getAllEntitiesOptedAIM(
			@Param("entityId") List<Long> entityId);
	

	
	@Query("SELECT MAX(e.paramValue) FROM ConfigQuestionEntity q "
			+ " INNER JOIN EntityConfigPrmtEntity e ON q.id = e.confgPrmtId "
			+ " WHERE e.entityId = :entityId " + " AND q.isActive = true "
			+ " AND q.questionDescription = :questionDesc "
			+ " AND q.questionType = :questionType "
			+ " AND e.isDelete = false")
	Optional<String> findIsItcRejectedOpted(@Param("entityId") Long entityId,
	                                        @Param("questionDesc") String questionDesc,
	                                        @Param("questionType") String questionType);



}
