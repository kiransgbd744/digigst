package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredHsnEntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Repository("Gstr1AHsnAsEnteredRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1AHsnAsEnteredRepository
		extends JpaRepository<Gstr1AAsEnteredHsnEntity, Long>,
		JpaSpecificationExecutor<Gstr1AAsEnteredHsnEntity> {

	@Query("SELECT HSN FROM Gstr1AAsEnteredHsnEntity HSN WHERE HSN.invHsnKey "
			+ "IN (:structuralValKeys) AND HSN.id IN (:id)")
	List<Gstr1AAsEnteredHsnEntity> getAllExcelData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("id") List<Long> id);

	@Transactional
	@Modifying
	@Query("UPDATE Gstr1AAsEnteredHsnEntity HSN SET HSN.isError = TRUE "
			+ "WHERE HSN.invHsnKey IN (:b2csKey) AND HSN.id IN (:ids) ")
	public void b2cUpdateStructuralError(@Param("b2csKey") List<String> b2csKey,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE Gstr1AAsEnteredHsnEntity HSN SET HSN.isInfo = TRUE ,"
			+ "HSN.isDelete = TRUE  WHERE HSN.invHsnKey IN (:b2csKey) AND HSN.id IN (:id) ")
	public void b2csUpdateErrorInfo(@Param("b2csKey") List<String> b2csKey,
			@Param("id") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE Gstr1AAsEnteredHsnEntity HSN SET HSN.isInfo = TRUE "
			+ "WHERE HSN.invHsnKey IN (:b2csKey) AND HSN.id IN (:id) ")
	public void b2csUpdateInfo(@Param("b2csKey") List<String> b2csKey,
			@Param("id") List<Long> id);

	@Query("SELECT HSN FROM Gstr1AAsEnteredHsnEntity HSN WHERE HSN.invHsnKey "
			+ "IN (:structuralValKeys) AND HSN.id IN (:id) AND HSN.isError = FALSE ")
	List<Gstr1AAsEnteredHsnEntity> getAllProcessedWithInfoData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("id") List<Long> id);

	@Query("SELECT COUNT(doc) FROM Gstr1AAsEnteredHsnEntity doc "
			+ "WHERE doc.fileId=:fileId AND "
			+ "doc.isError=true AND doc.isDelete = false")
	public Integer businessValidationCount(@Param("fileId") Long fileId);

	@Query("SELECT COUNT(doc) FROM Gstr1AAsEnteredHsnEntity doc "
			+ "WHERE doc.fileId=:fileId AND "
			+ "doc.isError=false AND doc.isDelete = false")
	public Integer processedCount(@Param("fileId") Long fileId);
}
