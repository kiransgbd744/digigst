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

import com.ey.advisory.admin.data.entities.client.Gstr2bAutoCommEntity;

@Repository("Gstr2bAutoCommRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2bAutoCommRepository
		extends CrudRepository<Gstr2bAutoCommEntity, Long>,
		JpaSpecificationExecutor<Gstr2bAutoCommEntity> {

	@Query("SELECT g FROM Gstr2bAutoCommEntity g WHERE g.gstin in (:gstin)"
			+ " and g.derivedTaxPeriod >= :derivedStartPeriod and "
			+ "g.derivedTaxPeriod <= :derivedEndPeriod and g.returnType = :returnType")
	public List<Gstr2bAutoCommEntity> getStatus(
			@Param("gstin") List<String> gstinDtoList,
			@Param("derivedStartPeriod") Integer derivedStartPeriod,
			@Param("derivedEndPeriod") Integer derivedEndPeriod,
			@Param("returnType") String returnType);

	public Gstr2bAutoCommEntity findByGstinAndTaxPeriodAndReturnTypeAndSection(
			String gstin, String taxPeriod, String returnType, String section);
	
	
	@Modifying
	@Query("UPDATE Gstr2bAutoCommEntity tblGstinStatus SET "
			+ "tblGstinStatus.updatedOn = :updatedDate,"
			+ "tblGstinStatus.errorDescription = :errorDescription,"
			+ "tblGstinStatus.status = :status "
			+ "WHERE tblGstinStatus.gstin =:gstinId and "
			+ "tblGstinStatus.taxPeriod =:taxPeriod and "
			+ "tblGstinStatus.returnType =:returnType and "
			+ "tblGstinStatus.section =:section")
	public int updateGst2bAutoGetCallStatus(
			@Param("status") String status,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("errorDescription") String errorDescription,
			@Param("gstinId") String gstinId,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("section") String section);

	@Modifying
	@Query("Update Gstr2bAutoCommEntity set status =:status, "
			+ "  errorDescription =:errorDescription  "
			+ " where gstin =:gstin AND taxPeriod =:taxPeriod AND "
			+ " returnType =:returnType")
	public void updateGstr2bAutoStatus(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("status") String status,
			@Param("errorDescription") String errorDescription);
	
	List<Gstr2bAutoCommEntity> findByTaxPeriodAndReturnTypeAndGstinIn(
			String taxPeriod, String returnType, List<String> gstins);
}
