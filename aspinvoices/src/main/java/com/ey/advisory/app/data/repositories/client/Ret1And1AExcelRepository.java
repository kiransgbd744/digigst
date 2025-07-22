package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Ret1And1AExcelEntity;

import jakarta.transaction.Transactional;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Ret1And1AExcelRepository")
public interface Ret1And1AExcelRepository
		extends JpaRepository<Ret1And1AExcelEntity, Long>,
		JpaSpecificationExecutor<Ret1And1AExcelEntity> {
	@Query("SELECT RET1 FROM Ret1And1AExcelEntity RET1 WHERE "
			+ "RET1.ret1And1AInvkey IN (:structuralValKeys) AND "
			+ " RET1.id IN (:ids) ")
	List<Ret1And1AExcelEntity> getAllExcelData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE Ret1And1AExcelEntity RET1 SET RET1.isInfo = TRUE "
			+ "WHERE RET1.ret1And1AInvkey IN (:gstnKeys) AND RET1.id IN (:ids) ")
	public void RET1UpdateInfo(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE Ret1And1AExcelEntity RET1 SET RET1.isError = TRUE "
			+ "WHERE RET1.ret1And1AInvkey IN (:gstnKeys) AND RET1.id IN (:ids) ")

	public void RET1UpdateErrors(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE Ret1And1AExcelEntity RET1 SET RET1.isInfo = TRUE , RET1.isDelete = TRUE "
			+ "WHERE RET1.ret1And1AInvkey IN (:gstnKeys) AND RET1.id IN (:ids) ")
	public void RET1ErrorUpdateInfo(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Query("SELECT RET1 FROM Ret1And1AExcelEntity RET1 WHERE "
			+ "RET1.ret1And1AInvkey IN (:structuralValKeys) AND "
			+ " RET1.id IN (:ids)  AND RET1.isError = FALSE ")
	List<Ret1And1AExcelEntity> getAllProcessedWithInfoData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);

}
