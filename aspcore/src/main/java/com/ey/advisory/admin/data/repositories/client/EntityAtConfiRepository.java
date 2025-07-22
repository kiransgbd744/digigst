package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.EntityAtConfigEntity;

/**
 * @author Umesha.M
 *
 */
@Repository("entityAtConfiRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EntityAtConfiRepository
		extends CrudRepository<EntityAtConfigEntity, Long> {

	/**
	 * @param groupcode
	 * @param entityId
	 * @return
	 */
	@Query("SELECT entity FROM EntityAtConfigEntity entity WHERE "
			+ "entity.groupcode=:groupcode AND entity.entityId=:entityId "
			+ "AND entity.isDelete=false")
	public List<EntityAtConfigEntity> findAllEntityAtConfigEntity(
			@Param("groupcode") String groupcode,
			@Param("entityId") Long entityId);

	@Query("SELECT entity FROM EntityAtConfigEntity entity WHERE "
			+ "entity.groupcode=:groupcode AND entity.entityId IN (:entityId) "
			+ "AND entity.isDelete=false")
	public List<EntityAtConfigEntity> findAtConfigAllEntity(
			@Param("groupcode") String groupcode,
			@Param("entityId") List<Long> entityId);

	/**
	 * @param entityIds
	 */
	@Modifying
	@Query("UPDATE EntityAtConfigEntity set isDelete = true WHERE id in "
			+ "(:entityIds)")
	public void updateAll(@Param("entityIds") List<Long> entityIds);

	@Query("SELECT entity FROM EntityAtConfigEntity entity WHERE "
			+ "entity.groupcode=:groupcode AND "
			+ "(entity.atInward != 'N'  AND entity.atOutward != 'N') "
			+ "AND entity.isDelete=false")
	public List<EntityAtConfigEntity> findAllEntityAtConfigApplicableForGroup(
			@Param("groupcode") String groupcode);

	@Query("SELECT entity FROM EntityAtConfigEntity entity WHERE "
			+ "entity.id=:id AND entity.atCode=:atCode AND "
			+ "entity.isDelete=false")
	public EntityAtConfigEntity selectApplicableAttributes(@Param("id") Long id,
			@Param("atCode") String atCode);

	@Modifying
	@Query("UPDATE EntityAtConfigEntity set isDelete = true WHERE "
			+ "atCode = 'GSTIN' AND entityId= :entityId AND "
			+ "groupcode = :groupcode")
	public void updateGstinValue(@Param("entityId") Long entityId,
			@Param("groupcode") String groupcode);

	@Query("SELECT entity FROM EntityAtConfigEntity entity WHERE "
			+ "entity.entityId=:entityId AND entity.atCode=:atCode "
			+ "AND entity.isDelete=false")
	public EntityAtConfigEntity entityAtConfig(@Param("entityId") Long entityId,
			@Param("atCode") String atCode);

	@Query("SELECT entity FROM EntityAtConfigEntity entity WHERE "
			+ "groupcode = :groupcode AND entity.isDelete=false")
	public List<EntityAtConfigEntity> findAllAtConfigEntity(
			@Param("groupcode") String groupcode);

	@Query("SELECT id FROM EntityAtConfigEntity WHERE atCode=:atCode "
			+ "AND isDelete=false")
	public Long getEntityConfigId(@Param("atCode") String atCode);
}
