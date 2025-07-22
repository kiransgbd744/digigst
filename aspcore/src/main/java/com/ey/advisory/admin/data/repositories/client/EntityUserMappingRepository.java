package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.EntityUserMapping;

@Repository("EntityUserMappingRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EntityUserMappingRepository
		extends JpaRepository<EntityUserMapping, Long>,
		JpaSpecificationExecutor<EntityUserMapping> {

	@Query("SELECT e FROM EntityUserMapping e WHERE e.entityId=:entityId "
			+ "AND e.isFlag=false")
	public List<EntityUserMapping> getUserIdsBasedOnEntityId(
			@Param("entityId") Long entityId);

	@Query("SELECT e FROM EntityUserMapping e WHERE e.entityId=:entityId")
	public List<EntityUserMapping> getUserIdsBasedOnEntityIdWithoutFlag(
			@Param("entityId") Long entityId);

	@Query("SELECT e FROM EntityUserMapping e WHERE e.entityId=:entityId")
	public List<EntityUserMapping> getUserIdsBasedOnEntity(
			@Param("entityId") Long entityId);

	@Query("SELECT e FROM EntityUserMapping e WHERE e.id=:id")
	public EntityUserMapping getEntityUserMapping(@Param("id") Long id);

	@Query("SELECT e FROM EntityUserMapping e WHERE e.groupCode =:groupCode AND"
			+ " e.entityId=:entityId AND e.userId in (:userIds) AND e.isFlag=false")
	public List<EntityUserMapping> getByUserAndEntityId(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId,
			@Param("userIds") List<Long> userIds);

	@Query("SELECT e FROM EntityUserMapping e WHERE e.entityId=:entityId AND "
			+ "e.userId=:userId AND e.isFlag=false")
	public EntityUserMapping getUserBy(@Param("entityId") Long entityId,
			@Param("userId") Long userId);

	@Query("SELECT e FROM EntityUserMapping e WHERE e.entityId=:entityId AND "
			+ "e.userId=:userId AND e.isFlag=true")
	public EntityUserMapping getUserByTrue(@Param("entityId") Long entityId,
			@Param("userId") Long userId);

	public List<EntityUserMapping> findByUserIdAndEntityIdInAndIsFlagFalse(
			Long userId, List<Long> entityId);

	@Modifying
	@Transactional
	@Query("UPDATE EntityUserMapping SET isFlag = false WHERE  entityId=:entityId"
			+ " AND userId= :userId")
	public void updateEntityUserAsActive(@Param("entityId") Long entityId,
			@Param("userId") Long userId);

	@Modifying
	@Transactional
	@Query("UPDATE EntityUserMapping SET isFlag = :isFlag WHERE  entityId=:entityId"
			+ " AND userId= :userId")
	public void updateFlag(@Param("entityId") Long entityId,
			@Param("userId") Long userId, @Param("isFlag") boolean isFlag);

	@Query("SELECT e FROM EntityUserMapping e WHERE userId=:userId "
			+ "AND isFlag=false")
	public List<EntityUserMapping> getEntityIds(@Param("userId") Long userId);

	@Query("SELECT e.id FROM EntityUserMapping e WHERE"
			+ " userId=:userId AND entityId=:entityId")
	public Long getidBasedOnUserIdAndEntityId(@Param("userId") Long userId,
			@Param("entityId") Long entityId);
}
