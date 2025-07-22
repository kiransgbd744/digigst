package com.ey.advisory.app.data.repositories.client;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;

@Repository("entityInfoRepository")
public interface EntityInfoRepository
		extends JpaRepository<EntityInfoEntity, Long>,
		JpaSpecificationExecutor<EntityInfoEntity> {

	@Query("SELECT g FROM EntityInfoEntity g WHERE g.groupCode=:groupCode")
	List<EntityInfoEntity> findEntitiesByGroupCode(
			@Param("groupCode") String groupCode);

	@Query("SELECT id FROM EntityInfoEntity g WHERE g.groupCode=:groupCode")
	List<Long> findEntityIdsByGroupCode(@Param("groupCode") String groupCode);

	@Query("SELECT entity FROM EntityInfoEntity entity WHERE entity.id in (:ids)")
	List<EntityInfoEntity> findByEntityIds(@Param("ids") Set<Long> ids);

	@Query("SELECT entity FROM EntityInfoEntity entity WHERE entity.isDelete = false")
	List<EntityInfoEntity> findActiveEntities();

	@Query("SELECT entityName FROM EntityInfoEntity entity WHERE entity.id =:id")
	String findEntityNameByEntityId(@Param("id") Long id);

	@Query("SELECT companyHq FROM EntityInfoEntity entity WHERE entity.id =:id")
	String findCompanyCodeByEntityId(@Param("id") Long id);

	@Query("SELECT pan FROM EntityInfoEntity entity WHERE entity.id =:id")
	List<String> findPanByEntityId(@Param("id") Long id);
	
	@Query("SELECT pan FROM EntityInfoEntity entity WHERE entity.id IN :ids")
	List<String> findPanByEntityIds(@Param("ids") List<Long> ids);

	@Query("SELECT g.id FROM EntityInfoEntity g where g.isDelete = false")
	List<Long> findActiveEntityIds();

	@Query("SELECT g.id FROM EntityInfoEntity g where g.isDelete = false and g.entityName =:entityName")
	List<Long> findActiveIdByEntityName(@Param("entityName") String entityName);

	@Query("SELECT g.id FROM EntityInfoEntity g where g.isDelete = false and g.pan =:pan")
	Long findActiveIdByPan(@Param("pan") String pan);
	
	@Query("SELECT g FROM EntityInfoEntity g WHERE g.groupCode=:groupCode and g.isDelete = false")
	List<EntityInfoEntity> findEntitiesByGroupCodes(
			@Param("groupCode") String groupCode);
}
