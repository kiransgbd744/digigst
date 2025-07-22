package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.GstnGetStatusEntity;

@Repository("GstinApiCallRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GstinApiCallRepository
		extends CrudRepository<GstnGetStatusEntity, Long>,
		JpaSpecificationExecutor<GstnGetStatusEntity> {

	@Query("SELECT g FROM GstnGetStatusEntity g WHERE g.gstin in (:gstin)"
			+ " and g.derivedTaxPeriod >= :derivedStartPeriod and "
			+ "g.derivedTaxPeriod <= :derivedEndPeriod and g.returnType = :returnType")
	public List<GstnGetStatusEntity> getStatus(
			@Param("gstin") List<String> gstinDtoList,
			@Param("derivedStartPeriod") Integer derivedStartPeriod,
			@Param("derivedEndPeriod") Integer derivedEndPeriod,
			@Param("returnType") String returnType);
	
	@Query("SELECT g FROM GstnGetStatusEntity g WHERE g.gstin in (:gstin)"
			+ " and g.taxPeriod = :taxPeriod and g.returnType = :returnType")
	public List<GstnGetStatusEntity> getDataStatusbyTaxPeriod(
			@Param("gstin") List<String> gstinDtoList,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType);
	
	@Query("SELECT g FROM GstnGetStatusEntity g WHERE g.gstin in (:gstin)"
			+ " and g.taxPeriod in (:taxPeriod) and g.returnType = :returnType")
	public List<GstnGetStatusEntity> getDataStatusbyGstnTaxPeriods(
			@Param("gstin") List<String> gstinDtoList,
			@Param("taxPeriod") List<String> taxPeriod,
			@Param("returnType") String returnType);
	
	@Query("SELECT g FROM GstnGetStatusEntity g WHERE g.gstin in (:gstin)"
			+ " and g.derivedTaxPeriod >= :derivedStartPeriod and "
			+ "g.derivedTaxPeriod <= :derivedEndPeriod and "
			+ "g.returnType = :returnType ")
	public List<GstnGetStatusEntity> getActiveStatus(
			@Param("gstin") List<String> gstinDtoList,
			@Param("derivedStartPeriod") Integer derivedStartPeriod,
			@Param("derivedEndPeriod") Integer derivedEndPeriod,
			@Param("returnType") String returnType);

	/*@Modifying
	@Query("Update GstnGetStatusEntity set isDeleted = true where gstin  "
			+ " =:gstin AND taxPeriod =:taxPeriod AND returnType =:returnType")
	public void updateActive(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType);*/
	
	@Modifying
	@Query("Update GstnGetStatusEntity set status =:status, "
			+ " csvGenPath =:csvGenPath, errorDescription =:errorDescription, "
			+ " updatedOn =:updatedOn "
			+ " where gstin =:gstin AND taxPeriod =:taxPeriod AND "
			+ " returnType =:returnType ")
	public void updateStatus(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("status") String status,
			
			@Param("csvGenPath") String csvGenPath,
			@Param("errorDescription") String errorDescription,
			@Param("updatedOn") LocalDateTime updatedOn);
	
	public GstnGetStatusEntity findByGstinAndTaxPeriodAndReturnTypeAndSection(String gstin, String taxPeriod,
			String returnType, String section);
	
	
	@Modifying
	@Query("Update GstnGetStatusEntity set status =:status, "
			+ " csvGenPath =:csvGenPath, errorDescription =:errorDescription, "
			+ " updatedOn =:updatedOn, docId =:docId  "
			+ " where gstin =:gstin AND taxPeriod =:taxPeriod AND "
			+ " returnType =:returnType ")
	public void updateStatusAndDocId(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("status") String status,
			@Param("docId") String docId,
			@Param("csvGenPath") String csvGenPath,
			@Param("errorDescription") String errorDescription,
			@Param("updatedOn") LocalDateTime updatedOn);
	
	@Query("from GstnGetStatusEntity where csvGenPath =:csvGenPath")
	public GstnGetStatusEntity findByCsvGenPath(
			@Param("csvGenPath") String csvGenPath);
}
