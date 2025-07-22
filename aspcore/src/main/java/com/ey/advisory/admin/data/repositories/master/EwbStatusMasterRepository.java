package com.ey.advisory.admin.data.repositories.master;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.EwbStatusMasterEntity;

@Repository(value = "EwbStatusMasterRepository")
public interface EwbStatusMasterRepository
		extends CrudRepository<EwbStatusMasterEntity, Long> {

	@Query("SELECT e.ewbStatus FROM EwbStatusMasterEntity e WHERE e.id=:id")
	public String getEwbStatus(@Param("id") Long id);

}
