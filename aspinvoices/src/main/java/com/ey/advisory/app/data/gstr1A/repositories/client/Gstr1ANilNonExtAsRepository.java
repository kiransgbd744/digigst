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

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilNonExemptedAsEnteredEntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Repository("Gstr1ANilNonExtAsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1ANilNonExtAsRepository
		extends JpaRepository<Gstr1ANilNonExemptedAsEnteredEntity, Long>,
		JpaSpecificationExecutor<Gstr1ANilNonExemptedAsEnteredEntity> {

	@Query("SELECT INV FROM Gstr1ANilNonExemptedAsEnteredEntity INV WHERE INV.nKey "
			+ "IN (:structuralValKeys) AND INV.id IN (:id) ")

	List<Gstr1ANilNonExemptedAsEnteredEntity> getAllExcelData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("id") List<Long> id);

	@Modifying
	@Query("UPDATE Gstr1ANilNonExemptedAsEnteredEntity INV SET INV.isError = TRUE "
			+ "WHERE INV.nKey IN (:invKey) AND INV.id IN (:ids) ")
	public void invUpdateStructuralError(@Param("invKey") List<String> invKey,
			@Param("ids") List<Long> ids);

	@Modifying
	@Query("UPDATE Gstr1ANilNonExemptedAsEnteredEntity INV SET INV.isInfo = TRUE,"
			+ " INV.isDelete = false "
			+ "WHERE INV.nKey IN (:invKey) AND INV.id IN (:id) ")
	public void invUpdateErrorInfo(@Param("invKey") List<String> invKey,
			@Param("id") List<Long> ids);

	@Modifying
	@Query("UPDATE Gstr1ANilNonExemptedAsEnteredEntity INV SET INV.isInfo = TRUE,"
			+ " INV.isError = TRUE "
			+ "WHERE INV.nKey IN (:invKey) AND INV.id IN (:id) ")
	public void invUpdateErrorInfoTrue(@Param("invKey") List<String> invKey,
			@Param("id") List<Long> ids);

	@Modifying
	@Query("UPDATE Gstr1ANilNonExemptedAsEnteredEntity EQ SET EQ.isError = TRUE"
			+ " WHERE EQ.nKey IN (:invKey) AND EQ.id IN (:id) ")
	public void invUpdateStrError(@Param("invKey") List<String> invKey,
			@Param("id") List<Long> ids);

	@Modifying
	@Query("UPDATE Gstr1ANilNonExemptedAsEnteredEntity INV SET INV.isInfo = TRUE "
			+ "WHERE INV.nKey IN (:invKey) AND INV.id IN (:id) ")
	public void invUpdateInfo(@Param("invKey") List<String> invKey,
			@Param("id") List<Long> id);

	@Query("SELECT INV FROM Gstr1ANilNonExemptedAsEnteredEntity INV WHERE INV.nKey "
			+ "IN (:structuralValKeys) AND INV.id IN (:id)  AND INV.isError = FALSE")

	List<Gstr1ANilNonExemptedAsEnteredEntity> getAllProcessedWithInfoData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("id") List<Long> id);
}
