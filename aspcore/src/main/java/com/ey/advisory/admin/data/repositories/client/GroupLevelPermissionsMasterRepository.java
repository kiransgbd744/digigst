package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.GroupLevelPermissionsMasterEntity;

@Repository("GroupLevelPermissionsMasterRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GroupLevelPermissionsMasterRepository
		extends CrudRepository<GroupLevelPermissionsMasterEntity, Long> {

	@Query("SELECT e FROM GroupLevelPermissionsMasterEntity e where e.isDelete=false ")
	public List<GroupLevelPermissionsMasterEntity> getAllPermissions();

	@Query("SELECT e FROM GroupLevelPermissionsMasterEntity e where e.permCode=:permCode "
			+ "and e.isDelete=false ")
	public GroupLevelPermissionsMasterEntity getPermissionCodeDetails(
			@Param("permCode") String permCode);

}
