package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.ProfilesMasterEntity;

@Repository("ProfilesMasterRepository")
public interface ProfilesMasterRepository
		extends CrudRepository<ProfilesMasterEntity, Long> {

	@Query("SELECT e FROM ProfilesMasterEntity e")
	public List<ProfilesMasterEntity> getProfiles();
}
