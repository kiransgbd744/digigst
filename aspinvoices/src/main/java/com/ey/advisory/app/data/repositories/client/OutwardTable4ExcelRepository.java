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

import com.ey.advisory.app.data.entities.client.OutwardTable4ExcelEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("OutwardTable4ExcelRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface OutwardTable4ExcelRepository
		extends JpaRepository<OutwardTable4ExcelEntity, Long>,
		JpaSpecificationExecutor<OutwardTable4ExcelEntity> {
	@Query("SELECT TABLE4 FROM OutwardTable4ExcelEntity TABLE4 WHERE "
			+ "TABLE4.table4Invkey IN (:structuralValKeys) AND "
			+ " TABLE4.id IN (:ids) ")
	List<OutwardTable4ExcelEntity> getAllExcelData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);

	@Modifying
	@Query("UPDATE OutwardTable4ExcelEntity TABLE4 SET TABLE4.isInfo = TRUE "
			+ "WHERE TABLE4.table4Invkey IN (:gstnKeys) AND TABLE4.id IN (:ids) ")
	public void table4UpdateInfo(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Modifying
	@Query("UPDATE OutwardTable4ExcelEntity TABLE4 SET TABLE4.isError = TRUE "
			+ "WHERE TABLE4.table4Invkey IN (:gstnKeys) AND TABLE4.id IN (:ids) ")

	public void table4UpdateErrors(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Modifying
	@Query("UPDATE OutwardTable4ExcelEntity TABLE4 SET TABLE4.isInfo = TRUE,"
			+ "TABLE4.isDelete = TRUE WHERE TABLE4.table4Invkey IN (:gstnKeys) "
			+ " AND TABLE4.id IN (:ids) ")
	public void table4ErrorUpdateInfo(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Query("SELECT TABLE4 FROM OutwardTable4ExcelEntity TABLE4 WHERE "
			+ "TABLE4.table4Invkey IN (:structuralValKeys) AND "
			+ " TABLE4.id IN (:ids)  AND TABLE4.isError = FALSE ")
	List<OutwardTable4ExcelEntity> getAllProcessedWithInfoData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);

}
