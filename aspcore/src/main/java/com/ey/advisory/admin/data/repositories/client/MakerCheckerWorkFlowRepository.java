package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.MakerCheckerWorkFlowEntity;

@Repository(value = "MakerCheckerWorkFlowRepository")
@Transactional
public interface MakerCheckerWorkFlowRepository
		extends CrudRepository<MakerCheckerWorkFlowEntity, Long> {

	public List<MakerCheckerWorkFlowEntity> findByEntityIdAndIsDeleteFalse(
			@Param("entityId") Long entityId);

	public List<MakerCheckerWorkFlowEntity> findByGstinAndIsDeleteFalse(
			@Param("gstin") String gstin);

	@Query("SELECT g.makerCheckerId FROM MakerCheckerWorkFlowEntity g WHERE g.entityId = "
			+ ":entityId and g.gstin =:gstin and g.workFlowType =:workFlowType and "
			+ "g.makerCheckerId <> :makerCheckerId and g.makerCheckerType = 'C' and g.isDelete=false")
	public List<String> findAllCheckersByGstinAndWorkFlowType(
			@Param("entityId") Long entityId, @Param("gstin") String gstin,
			@Param("workFlowType") String workFlowType,
			@Param("makerCheckerId") String makerCheckerId);

	@Query("UPDATE MakerCheckerWorkFlowEntity SET isDelete=true WHERE entityId = "
			+ ":entityId and gstin =:gstin and workFlowType =:workFlowType and "
			+ "makerCheckerId =:makerCheckerId and makerCheckerType = :makerCheckerType")
	@Modifying
	public void updateCheckersByGstinAndWorkFlowType(
			@Param("entityId") Long entityId, @Param("gstin") String gstin,
			@Param("workFlowType") String workFlowType,
			@Param("makerCheckerId") String makerCheckerId,
			@Param("makerCheckerType") String makerCheckerType);

}
