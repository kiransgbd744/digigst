package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.DataSecurityEntity;

import jakarta.transaction.Transactional;

/**
 * @author Umesha.M
 *
 */
@Repository("dataSecurityRepository")
public interface DataSecurityRepository
		extends CrudRepository<DataSecurityEntity, Long> {

	/**
	 * @param groupCode
	 * @return
	 */
	@Query("SELECT e FROM DataSecurityEntity e WHERE "
			+ "e.groupCode=:groupCode AND e.isDelete=false")
	public List<DataSecurityEntity> findDataSecurityDetails(
			@Param("groupCode") String groupCode);

	@Query("SELECT d.entityId,e.atCode,d.entityAtValueId,e.atValue "
			+ "FROM DataSecurityEntity d join "
			+ "EntityAtValueEntity e on d.entityAtValueId = e.id WHERE "
			+ "d.userId=:userId AND d.isDelete=false")
	public List<Object[]> findDataSecurityWithAttributeValues(
			@Param("userId") Long userId);

	@Query("SELECT d.entityId,e.atCode,e.id,e.atValue "
			+ "FROM DataSecurityEntity d join "
			+ "EntityAtValueEntity e on d.entityAtValueId = e.id WHERE "
			+ " d.entityId in (:entityId) AND d.userId=:userId AND d.isDelete=false")
	public List<Object[]> findDataSecurityWithAttributeValuesWithValue(
			@Param("entityId") List<Long> entityId,
			@Param("userId") Long userId);

	/**
	 * @param groupCode
	 * @param entityId
	 * @param userId
	 * @return
	 */
	@Query("SELECT e FROM DataSecurityEntity e WHERE "
			+ "e.groupCode=:groupCode AND e.entityId=:entityId AND "
			+ "e.userId=:userId  AND e.isDelete=false ")
	public List<DataSecurityEntity> findDataSecurityDetailsByUserId(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId, @Param("userId") Long userId);

	/*
	 * @Query("select av.id, av.at_code, av.at_value" +
	 * " from data_security d inner join entity_at_value " +
	 * "av on d.entity_at_value_id = av.id where d.entity_id=32 AND av.is_delete=false order by d.id ASC"
	 * ) public List<Object[]> findDataSecurityDetailsAllEntity(
	 * 
	 * @Param("entityId") Long entityId);
	 */

	@Query("SELECT e.entityAtValueId FROM DataSecurityEntity e WHERE "
			+ "e.groupCode=:groupCode AND e.entityId=:entityId AND "
			+ "e.userId=:userId  AND e.isDelete=false")
	public List<Long> findEntityValueIdsFromDataSecurity(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId, @Param("userId") Long userId);

	@Query("SELECT e FROM DataSecurityEntity e WHERE "
			+ "e.groupCode=:groupCode AND e.entityId=:entityId AND e.isDelete=false")
	public List<DataSecurityEntity> findEntityValueIdsFromDataSecurity(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId);

	@Modifying
	@Transactional
	@Query("UPDATE DataSecurityEntity  SET isDelete = true WHERE "
			+ "userId =:userId AND entityAtValueId IN (:attrs) AND entityId =:entityId")
	public void markAttrsAsDeletedForUser(@Param("userId") Long userId,
			@Param("attrs") List<Long> attrs, @Param("entityId") Long entityId);

	@Query("SELECT e.userId, e.entityAtValueId FROM DataSecurityEntity e WHERE "
			+ "e.userId IN (:userIds) AND e.entityId = :entityId  AND e.isDelete = false")
	public List<Object[]> findAttrValuesForUsers(
			@Param("userIds") List<Long> userIds,
			@Param("entityId") Long entityId);

	@Query("SELECT MAX(id) FROM DataSecurityEntity")
	public Long getMaxDataSecurityId();

	@Query("SELECT count(e) FROM DataSecurityEntity e WHERE e.userId=:userId")
	public Long countByUserId(@Param("userId") Long userId);

	/**
	 * @param groupCode
	 * @param entityId
	 * @param userId
	 * @return
	 */
	@Query("SELECT d.entityId,d.userId,d.entityAtValueId,ev.atCode FROM DataSecurityEntity d "
			+ "INNER JOIN EntityAtValueEntity ev on d.entityAtValueId=ev.id WHERE "
			+ "d.groupCode=:groupCode AND d.entityId in (:entityIds) AND "
			+ "d.userId in (:userIds)  AND ev.isDelete=false AND d.isDelete=false ")
	public List<Object[]> getDataSecurityDetailsByUserIds(
			@Param("groupCode") String groupCode,
			@Param("entityIds") List<Long> entityIds,
			@Param("userIds") List<Long> userIds);

	// select eum.entity_id,eum.user_id,ui.user_name,
	// d.ENTITY_AT_VALUE_ID,ea.AT_VALUE,ea.AT_CODE
	// from client1_gst.entity_user_mapping eum
	// inner join client1_gst.client_userinfo ui on eum.user_id=ui.id left join
	// client1_gst.DATA_SECURITY d on eum.user_id=d.user_id
	// inner join client1_gst.entity_at_value ea on d.ENTITY_AT_VALUE_ID=ea.id
	// where eum.entity_id=20;
	
	
	//select distinct eum.entity_id,eum.userId,ui.user_name,ea.AT_CODE,ea.id from
   //client1_gst.entity_user_mapping eum inner join client1_gst.client_userinfo ui
    //on eum.user_id=ui.id and eum.is_delete=false left outer join
    //client1_gst.DATA_SECURITY d on eum.user_id=d.user_id
    //and eum.entity_id=d.entity_id and d.is_delete=false
   //left join client1_gst.entity_at_value ea on d.ENTITY_AT_VALUE_ID=ea.id
   //and ea.is_delete=false where eum.entity_id IN (24,20,88);
	
	
	
	/**
	 * @param groupCode
	 * @param entityId
	 * @param userId
	 * @return
	 */
	@Query("SELECT distinct eum.entityId,eum.userId,ui.userName,ev.atCode,ev.id,ui.email "
			+ "FROM EntityUserMapping eum "
			+ "INNER JOIN UserCreationEntity ui ON eum.userId=ui.id AND eum.isFlag=false "
			+ "LEFT OUTER JOIN DataSecurityEntity d on eum.userId=d.userId "
			+ "AND eum.entityId = d.entityId AND d.isDelete=false "
			+ "LEFT JOIN EntityAtValueEntity ev on d.entityAtValueId=ev.id "
			+ "AND ev.isDelete=false "
			+ "WHERE eum.entityId in (:entityIds) ")
	public List<Object[]> getDataSecurDetByUserIdsAndAttValues(
			@Param("entityIds") List<Long> entityIds);
	
	@Query("SELECT distinct eum.entityName "
			+ "FROM DataSecurityEntity eum "
			+ "WHERE eum.isDelete=false "
			+ "AND eum.entityId=:entityId ")
	public String findByEntityIdAndIsDeleteFalse(
			@Param("entityId") Long entityId);
}
