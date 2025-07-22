package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.ProfilePermissionsMasterEntity;

@Repository("ProfilePermissionsMasterRepository")
public interface ProfilePermissionsMasterRepository
		extends CrudRepository<ProfilePermissionsMasterEntity, Long> {

	@Query("SELECT e FROM ProfilePermissionsMasterEntity e "
			+ "WHERE e.profileName IN(:profileName)")
	public List<ProfilePermissionsMasterEntity> getAllProfilePerm(
			@Param("profileName") String profileName);
}
