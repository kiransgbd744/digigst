package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.UserPermissionsEntity;

@Repository("UserPermissionsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface UserPermissionsRepository
		extends CrudRepository<UserPermissionsEntity, Long> {

	@Query("SELECT e FROM UserPermissionsEntity e WHERE e.entityId=:entityId "
			+ "AND e.userId=:userId AND e.permCode=:permCode AND e.isDelete=false ")
	public UserPermissionsEntity getUserPermissions(
			@Param("entityId") Long entityId, @Param("userId") Long userId,
			@Param("permCode") String permCode);

	@Modifying
	@Query("UPDATE UserPermissionsEntity SET isApplicable=:isApplicable "
			+ "WHERE entityId=:entityId AND userId=:userId "
			+ "AND permCode=:permCode")
	public void updateUserPerm(@Param("entityId") Long entityId,
			@Param("userId") Long userId, @Param("permCode") String permCode,
			@Param("isApplicable") boolean isApplicable);

	@Query("SELECT e.permCode FROM UserPermissionsEntity e WHERE "
			+ "entityId=:entityId AND e.userId=:userId AND "
			+ "e.isApplicable=true AND e.isDelete=false")
	public List<String> getPermissionByUser(@Param("userId") Long userId,
			@Param("entityId") Long entityId);

	@Modifying
	@Query("UPDATE UserPermissionsEntity SET isApplicable=false "
			+ "WHERE entityId=:entityId AND userId=:userId ")
	public void updatePermissionUser(@Param("entityId") Long entityId,
			@Param("userId") Long userId);

	public List<UserPermissionsEntity> findByEntityIdInAndUserIdAndIsApplicableTrueAndIsDeleteFalse(List<Long> entityIds,
			Long userId);
	
	@Query("SELECT e FROM UserPermissionsEntity e WHERE e.entityId=:entityId "
			+ "AND e.userId=:userId AND e.permCode=:permCode AND e.isDelete=false ")
	public UserPermissionsEntity getUserPermissionsForUserId(@Param("userId") Long userId,
			@Param("permCode") String permCode);
	

}
