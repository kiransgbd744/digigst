package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ErpInfoEntity;

/**
 * @author Siva.Nandam
 *
 */
@Repository("ErpInfoEntityRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ErpInfoEntityRepository
		extends JpaRepository<ErpInfoEntity, Long>,
		JpaSpecificationExecutor<ErpInfoEntity> {

	@Query("SELECT g FROM ErpInfoEntity g")
	public List<ErpInfoEntity> findAllVal();

	@Modifying
	@Transactional
	@Query("UPDATE ErpInfoEntity SET isDelete =true WHERE id = :id")
	public void deleterecord(@Param("id") Long id);

	@Query("SELECT erp.id, erp.systemId FROM ErpInfoEntity erp WHERE erp.isDelete=false")
	public List<Object[]> getErpIdName();

	@Query("SELECT e.id FROM ErpInfoEntity e WHERE e.systemId=:sourceId AND e.isDelete=false")
	public Long getErpId(@Param("sourceId") String sourceId);

	@Query("SELECT e.sourceType FROM ErpInfoEntity e WHERE e.id=:id "
			+ "AND e.isDelete=false")
	public String getSourceTypeByErpId(@Param("id") Long id);

	@Modifying
	@Transactional
	@Query("UPDATE ErpInfoEntity SET isDelete=true WHERE systemId=:systemId "
			+ "AND isDelete=false ")
	public void inActiveErpBasedOnSystem(@Param("systemId") String systemId);
	
	@Query("SELECT e FROM ErpInfoEntity e WHERE e.id=:id "
			+ "AND e.isDelete=false")
	public ErpInfoEntity getEntityByErpId(@Param("id") Long id);
	
	@Query("SELECT erp.id, erp.systemId FROM ErpInfoEntity erp WHERE "
			+ "erp.sourceType='SFTP' AND erp.isDelete=false ")
	public List<Object[]> getErpIdNameForSftpSourceType();

}
