package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.EntityAtValueEntity;

import jakarta.transaction.Transactional;

/**
 * @author Umesha.M
 *
 */
@Repository("entityAtValueRepository")
public interface EntityAtValueRepository
		extends CrudRepository<EntityAtValueEntity, Long> {

	@Query("SELECT e FROM EntityAtValueEntity e WHERE "
			+ "e.groupCode=:groupCode AND e.isDelete=false")
	List<EntityAtValueEntity> getAllEntityAtValues(
			@Param("groupCode") String groupCode);

	/**
	 * @param groupCode
	 * @param entityId
	 * @return
	 */
	@Query("SELECT e FROM EntityAtValueEntity e WHERE "
			+ "e.groupCode=:groupCode AND e.entityId=:entityId AND e.isDelete=false")
	List<EntityAtValueEntity> getAllEntityAtValueEntity(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId);

	@Query("SELECT e FROM EntityAtValueEntity e WHERE "
			+ "e.entityId=:entityId AND e.isDelete=false")
	List<EntityAtValueEntity> getConfigAtValueEntityIdByEntityId(
			@Param("entityId") Long entityId);

	@Query("SELECT e.atValue FROM EntityAtValueEntity e WHERE "
			+ "e.groupCode=:groupCode AND e.entityId=:entityId AND"
			+ " e.isDelete=false AND e.atCode =:atCode")
	List<String> getAllAtValueForEntityAndAtCode(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId, @Param("atCode") String atCode);

	@Query("SELECT e FROM EntityAtValueEntity e WHERE e.id=:id "
			+ "AND isDelete=false")
	public EntityAtValueEntity getEntityAlValue(@Param("id") Long id);

	@Modifying
	@Transactional
	@Query("UPDATE EntityAtValueEntity SET isDelete=true WHERE"
			+ " entityId=:entityId AND groupCode = :groupCode AND atValue IN (:gstins) ")
	public void updateAllValues(@Param("entityId") final Long entityId,
			@Param("groupCode") final String groupCode,
			@Param("gstins") final List<String> gstins);

	@Modifying
	@Transactional
	@Query("UPDATE EntityAtValueEntity e set e.isDelete = true WHERE e.id in (:ids)")
	public void deleteOldRecordForEntity(@Param("ids") List<Long> ids);

	@Query("SELECT e.id FROM EntityAtValueEntity e WHERE e.atValue=:atValue "
			+ "AND e.entityId=:entityId AND e.isDelete=false")
	public Long getIdsBasedOnAtValue(@Param("atValue") String atValue,
			@Param("entityId") Long entityId);

	@Query("SELECT e FROM EntityAtValueEntity e WHERE "
			+ "e.groupCode=:groupCode AND e.entityId=:entityId AND"
			+ " e.isDelete=false AND e.atCode =:atCode")
	List<EntityAtValueEntity> getAllAttributes(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId, @Param("atCode") String atCode);

	@Query("SELECT e FROM EntityAtValueEntity e WHERE e.groupCode=:groupCode AND e.entityId=:entityId "
			+ " AND e.atValue=:atValue  AND e.atCode =:atCode AND e.isDelete=false ")
	public EntityAtValueEntity exitingEntityAtValue(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId, @Param("atCode") String atCode,
			@Param("atValue") String atValue);

	@Query("SELECT e FROM EntityAtValueEntity e WHERE "
			+ "e.atValue=:atValue AND entityId=:entityId AND e.isDelete=false")
	public EntityAtValueEntity getEntityAtValue(
			@Param("atValue") String atValue, @Param("entityId") Long entityId);

	@Modifying
	@Transactional
	@Query("UPDATE EntityAtValueEntity SET isDelete = true WHERE id in (:id)")
	public void deleteAttributes(@Param("id") List<Long> id);

	@Query("SELECT e FROM EntityAtValueEntity e WHERE e.entityId=:entityId AND"
			+ " e.isDelete=false AND e.atCode =:atCode")
	public List<EntityAtValueEntity> extingAttributeValue(
			@Param("entityId") Long entityId, @Param("atCode") String atCode);
}
