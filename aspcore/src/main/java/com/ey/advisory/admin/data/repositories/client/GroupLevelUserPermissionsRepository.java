package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.GroupLevelUserPermissionsEntity;

@Repository("GroupLevelUserPermissionsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GroupLevelUserPermissionsRepository
		extends CrudRepository<GroupLevelUserPermissionsEntity, Long> {

	@Query("SELECT e FROM GroupLevelUserPermissionsEntity e WHERE "
			+ " e.userId=:userId AND e.permCode=:permCode AND e.isDelete=false ")
	public GroupLevelUserPermissionsEntity getUserPermissions(@Param("userId") Long userId,
			@Param("permCode") String permCode);

	@Modifying
	@Query("UPDATE GroupLevelUserPermissionsEntity SET isApplicable=:isApplicable "
			+ "WHERE userId=:userId "
			+ "AND permCode=:permCode")
	public void updateUserPerm(@Param("userId") Long userId, @Param("permCode") String permCode,
			@Param("isApplicable") boolean isApplicable);
	
	@Modifying
	@Query("UPDATE GroupLevelUserPermissionsEntity SET isDelete=false "
			+ "WHERE userId=:userId ")
	public void updateIsFlagUserPerm(@Param("userId") Long userId);
	
	@Query("SELECT e.permCode FROM GroupLevelUserPermissionsEntity e WHERE "
			+ " e.userId=:userId AND "
			+ "e.isApplicable=true AND e.isDelete=false")
	public List<String> getPermissionByUser(@Param("userId") Long userId);

	@Modifying
	@Query("UPDATE GroupLevelUserPermissionsEntity SET isApplicable=false "
			+ "WHERE userId=:userId ")
	public void updatePermissionUser(
			@Param("userId") Long userId);

	public List<GroupLevelUserPermissionsEntity> findByUserIdAndIsApplicableTrueAndIsDeleteFalse(Long userId);

}
