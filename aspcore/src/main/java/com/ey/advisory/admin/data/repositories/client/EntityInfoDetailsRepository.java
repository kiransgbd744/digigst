package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;

/**
 * @author Umesha.M
 *
 */
@Repository("entityInfoDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EntityInfoDetailsRepository
		extends CrudRepository<EntityInfoEntity, Long> {

	/**
	 * @param groupCode
	 * @return
	 */
	@Query("SELECT entity FROM EntityInfoEntity entity WHERE "
			+ "entity.groupCode=:groupCode AND entity.isDelete=false")
	public List<EntityInfoEntity> findEntityInfoDetails(
			@Param("groupCode") String groupCode);

	@Query("SELECT entity FROM EntityInfoEntity entity WHERE "
			+ "entity.id=:entityId AND entity.isDelete=false")
	public EntityInfoEntity findEntityByEntityId(
			@Param("entityId") Long entityId);

	@Query("SELECT entity FROM EntityInfoEntity entity WHERE "
			+ "entity.id=:entityId AND entity.isDelete=false AND entity.pan=:pan")
	public EntityInfoEntity findEntityByEntityId(
			@Param("entityId") Long entityId, @Param("pan") String pan);

	@Query("SELECT entity FROM EntityInfoEntity entity WHERE "
			+ "entity.groupCode=:groupCode " + "AND entity.isDelete=false")
	public List<EntityInfoEntity> findAllEntitlementEntitydetails(
			@Param("groupCode") String groupCode);

	@Query("SELECT entity.id FROM EntityInfoEntity entity WHERE "
			+ "entity.groupCode=:groupCode AND entity.isDelete=false")
	public Long findEntityId(@Param("groupCode") String groupCode);

	@Query("SELECT entity.id FROM EntityInfoEntity entity WHERE "
			+ "entity.entityName=:entityName")
	public Long findIdEntityId(@Param("entityName") String entityName);

	@Query("SELECT count(e) FROM EntityInfoEntity e WHERE "
			+ "e.entityName=:entityName")
	public int entityNamecount(@Param("entityName") String entityName);

	@Query("SELECT count(e) FROM EntityInfoEntity e WHERE "
			+ "e.companyHq=:companyHq")
	public int hqNamecount(@Param("companyHq") String companyHq);

	@Query("SELECT count(e) FROM EntityInfoEntity e WHERE " + "e.pan=:pan")
	public int pancount(@Param("pan") String pan);

	@Query("SELECT entity.id FROM EntityInfoEntity entity WHERE "
			+ "entity.groupCode=:groupCode AND entity.isDelete=false "
			+ "AND entity.entityName=:entityName AND entity.pan=:pan")
	public Long findEntityIdByEntityNameAndPanNumber(
			@Param("groupCode") String groupCode,
			@Param("entityName") String entityName, @Param("pan") String pan);

	/**
	 * @param groupCode
	 * @return
	 */
	@Query("SELECT e.id,e.entityName,eum.userId,uce.userName "
			+ "FROM EntityInfoEntity e LEFT JOIN "
			+ "EntityUserMapping eum on e.id = eum.entityId "
			+ "INNER JOIN UserCreationEntity uce on eum.userId=uce.id WHERE "
			+ "e.groupCode=:groupCode AND e.isDelete=false AND eum.isFlag=false ")
	public List<Object[]> findEntityAndEntityUser(
			@Param("groupCode") String groupCode);

	@Query("SELECT entity.entityName FROM EntityInfoEntity entity WHERE "
			+ "entity.id IN (:entityId) AND entity.isDelete=false")
	public String entityNameById(@Param("entityId") List<Long> entityId);
	
	@Query("SELECT entity.entityName FROM EntityInfoEntity entity WHERE "
			+ "entity.id IN (:entityId) AND entity.isDelete=false")
	public List<String> entityNameByIds(@Param("entityId") List<Long> entityId);

	public EntityInfoEntity findByIdAndIsDeleteFalse(@Param("id") Long id);

	public List<EntityInfoEntity> findByPanAndIsDeleteFalse(
			@Param("pan") String pan);

	public List<EntityInfoEntity> findByIdInAndIsDeleteFalse(List<Long> id);
}
