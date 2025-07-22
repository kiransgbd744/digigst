package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.TemplateMappingEntity;

@Repository(value = "TemplateMappingRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TemplateMappingRepository
		extends CrudRepository<TemplateMappingEntity, Long> {

	@Query("SELECT distinct t.entityId,t.templateCode,"
			+ "t.templateId FROM TemplateMappingEntity t "
			+ "WHERE t.entityId IN (:entityId) AND t.isDelete=false ")
	public List<Object[]> getTemplateMapping(
			@Param("entityId") List<Long> entityId);

	@Query("SELECT t.id FROM TemplateMappingEntity t WHERE t.entityId=:entityId "
			+ "AND t.gstinId=:gstinId AND t.templateId=:templateId "
			+ "AND t.isDelete=false ")
	public Long getTemplateMappringId(@Param("entityId") Long entityId,
			@Param("gstinId") Long gstinId,
			@Param("templateId") Long templateId);

	public List<TemplateMappingEntity> findByEntityIdAndTemplateTypeAndIsDeleteFalse(Long entityId, String templateType);
	
	public List<TemplateMappingEntity> findByEntityIdAndIsDeleteFalse(Long entityId);
}
