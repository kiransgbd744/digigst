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

import com.ey.advisory.app.data.entities.client.OutwardB2cExcelEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("OutwardB2cExcelRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface OutwardB2cExcelRepository
		extends JpaRepository<OutwardB2cExcelEntity, Long>,
		JpaSpecificationExecutor<OutwardB2cExcelEntity> {

	@Query("SELECT B2C FROM OutwardB2cExcelEntity B2C WHERE B2C.b2cInvKey "
			+ "IN (:structuralValKeys) AND B2C.id IN (:id) ")
	List<OutwardB2cExcelEntity> getAllExcelData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("id") List<Long> id);

	@Modifying
	@Query("UPDATE OutwardB2cExcelEntity B2C SET B2C.isInfo = TRUE "
			+ "WHERE B2C.b2cInvKey IN (:b2cKey) AND B2C.id IN (:id) ")
	public void b2cUpdateInfo(@Param("b2cKey") List<String> b2cKey,
			@Param("id") List<Long> id);

	@Modifying
	@Query("UPDATE OutwardB2cExcelEntity B2C SET B2C.isError = TRUE "
			+ "WHERE B2C.b2cInvKey IN (:b2cKey) AND B2C.id IN (:ids) ")
	public void b2cUpdateStructuralError(@Param("b2cKey") List<String> b2cKey,
			@Param("ids") List<Long> ids);

	@Modifying
	@Query("UPDATE OutwardB2cExcelEntity B2C SET B2C.isInfo = TRUE ,"
			+ "B2C.isDelete = TRUE WHERE B2C.b2cInvKey IN (:b2cKey) "
			+ "AND B2C.id IN (:id) ")
	public void b2cUpdateErrorInfo(@Param("b2cKey") List<String> b2cKey,
			@Param("id") List<Long> ids);

	@Query("SELECT B2C FROM OutwardB2cExcelEntity B2C WHERE B2C.b2cInvKey "
			+ "IN (:structuralValKeys) AND B2C.id IN (:id)  AND "
			+ "B2C.isError = FALSE ")
	List<OutwardB2cExcelEntity> getAllProcessedWithInfoData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("id") List<Long> id);
}
