package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.FunctionalityMasterEntity;

@Repository("functionalityMasterRepository")
public interface FunctionalityMasterRepository
		extends CrudRepository<FunctionalityMasterEntity, String> {

	@Query("SELECT e FROM FunctionalityMasterEntity e")
	public List<FunctionalityMasterEntity> getFunctionalityMaster();

}
