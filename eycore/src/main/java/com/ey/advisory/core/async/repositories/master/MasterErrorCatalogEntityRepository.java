/**
 * 
 */
package com.ey.advisory.core.async.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.MasterErrorCatalogEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("MasterErrorCatalogEntityRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface MasterErrorCatalogEntityRepository
		extends CrudRepository<MasterErrorCatalogEntity, Long> {

	@Query("SELECT entity FROM MasterErrorCatalogEntity entity WHERE "
			+ "entity.errorType=:errorType AND entity.tableType=:tableType")
	public List<MasterErrorCatalogEntity> findErrorCodeByType(
			@Param("errorType") String errorType,
			@Param("tableType") String tableType);

	@Query("SELECT entity FROM MasterErrorCatalogEntity entity WHERE "
			+ "entity.errorType=:errorType AND entity.tableType IN (:tableType, "
			+ "'GSTR1','EINVOICE') ")
	public List<MasterErrorCatalogEntity> findOUErrorCodeByType(
			@Param("errorType") String errorType,
			@Param("tableType") String tableType);

	@Query("SELECT entity FROM MasterErrorCatalogEntity entity WHERE "
			+ "entity.errorCode=:errorCode AND entity.tableType=:tableType")
	public MasterErrorCatalogEntity findByErrorCodeAndTableType(
			@Param("errorCode") String errorCode,
			@Param("tableType") String tableType);

	@Override
	public List<MasterErrorCatalogEntity> findAll();

	@Query("SELECT entity FROM MasterErrorCatalogEntity entity WHERE "
			+ "entity.errorCode IN (:errorCodes) AND entity.tableType=:tableType ")
	public List<MasterErrorCatalogEntity> errorDescsForErrorCodeAndTableType(
			@Param("errorCodes") List<String> errorCodes,
			@Param("tableType") String tableType);
	
	@Query("SELECT entity.errorDesc FROM MasterErrorCatalogEntity entity WHERE "
			+ "entity.errorCode=:errorCode")
	public String findByErrorCode(
			@Param("errorCode") String errorCode);
	
}
