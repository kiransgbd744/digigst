/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.ErpCompanyCodeMappingEntity;

/**
 * @author Laxmi.Salukuti
 *
 */

@Repository("ErpCompanyCodeMappingRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ErpCompanyCodeMappingRepository
		extends JpaRepository<ErpCompanyCodeMappingEntity, Long>,
		JpaSpecificationExecutor<ErpCompanyCodeMappingEntity> {

	@Query("SELECT erp.erpSystemId FROM ErpCompanyCodeMappingEntity erp "
			+ "WHERE erp.companyCode = :companyCode AND erp.isDeleted = false ")
	public List<Object> findSourceIdByCompanyCode(
			@Param("companyCode") String companyCode);

	@Query("SELECT erp FROM ErpCompanyCodeMappingEntity erp WHERE "
			+ "erp.entityId IN (:entityId) AND erp.isDeleted=false ")
	public List<ErpCompanyCodeMappingEntity> getCompanyCodeMapping(
			@Param("entityId") List<Long> entityId);

	@Query("UPDATE ErpCompanyCodeMappingEntity SET isDeleted=true WHERE "
			+ "companyCode=:companyCode AND isDeleted=false ")
	@Modifying
	public void updIsStaByCompCodeAndEntIdComp(
			@Param("companyCode") String companyCode);
	
	@Query("SELECT companyCode FROM ErpCompanyCodeMappingEntity WHERE "
			+ "erpId = :erpId AND isDeleted=false ")
	public String getCompanyCode(@Param("erpId") Long erpId);
	
	@Query("SELECT companyCode FROM ErpCompanyCodeMappingEntity WHERE "
			+ "erpId = :erpId AND isDeleted=false AND entityId = :entityId ")
	public String getCompanyCode(@Param("erpId") Long erpId,
			@Param("entityId") Long entityId);
}
