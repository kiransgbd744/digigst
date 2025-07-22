package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.TermConditionsEntity;

@Repository("TermConditionsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TermConditionsRepository
		extends CrudRepository<TermConditionsEntity, Long> {

	@Query("SELECT e FROM TermConditionsEntity e WHERE e.entityId IN (:entityIds) "
			+ "AND e.isDelete=false ")
	public List<TermConditionsEntity> getTermConditions(
			@Param("entityIds") List<Long> entityIds);

	@Modifying
	@Query("UPDATE TermConditionsEntity SET isDelete=true WHERE id IN (:ids) "
			+ "AND entityId=:entityId AND isDelete=false ")
	public void deleteTermCondtions(@Param("ids") final List<Long> ids,
			@Param("entityId") Long entityId);
}
