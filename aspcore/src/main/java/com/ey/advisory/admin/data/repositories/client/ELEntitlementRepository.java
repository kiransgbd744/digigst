package com.ey.advisory.admin.data.repositories.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ELEntitlementEntity;

/**
 * @author Umesha.M
 *
 */
@Repository("elEntitlementRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ELEntitlementRepository
		extends CrudRepository<ELEntitlementEntity, Long> {

	/**
	 * @param groupCode
	 * @param entityId
	 * @return
	 */
	@Query("SELECT entity FROM ELEntitlementEntity entity WHERE "
			+ "entity.groupCode=:groupCode "
			+ "AND entity.entityId=:entityId AND entity.isDelete=false")
	public List<ELEntitlementEntity> findAllEntitlementdetails(
			@Param("groupCode") String groupCode,
			@Param("entityId") Long entityId);

	@Query("SELECT e FROM ELEntitlementEntity e WHERE e.isDelete=true AND "
			+ "e.entityId=:entityId AND "
			+ "((:elValue)='' OR :elValue IS NULL OR e.elValue=:elValue) AND "
			+ "(:updateFromDate IS NULL OR :updateToDate IS NULL OR "
			+ "e.modifiedOn  BETWEEN :updateFromDate AND :updateToDate)")
	public List<ELEntitlementEntity> getELEntitlementHistory(
			@Param("entityId") Long entityId, @Param("elValue") String elValue,
			@Param("updateFromDate") LocalDate updateFromDate,
			@Param("updateToDate") LocalDate updateToDate);

	@Modifying
	@Transactional
	@Query("UPDATE ELEntitlementEntity SET isDelete=true WHERE "
			+ "elId IN (:elId)")
	public void updateEntitlement(@Param("elId") List<Long> elId);

	/*@Query("SELECT e.functionalityCode ELEntitlementEntity e WHERE "
			+ "e.entityId = :entityId")
	public String getFunctionality(@Param("entityId") Long entityId);*/
}
