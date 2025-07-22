package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.TemplateMasterEntity;

@Repository("TemplateMasterRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TemplateMasterRepository
		extends CrudRepository<TemplateMasterEntity, Long> {

	@Query("SELECT t FROM TemplateMasterEntity t")
	public List<TemplateMasterEntity> getTemplateMasters();

	@Query("SELECT t FROM TemplateMasterEntity t WHERE t.id=:id AND isDelete=false ")
	public TemplateMasterEntity getTempTypeAndCode(@Param("id") Long id);
}
