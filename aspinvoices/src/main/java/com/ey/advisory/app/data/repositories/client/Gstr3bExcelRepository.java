package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr3bExcelEntity;

import jakarta.transaction.Transactional;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Gstr3bExcelRepository")
public interface Gstr3bExcelRepository
		extends JpaRepository<Gstr3bExcelEntity, Long>,
		JpaSpecificationExecutor<Gstr3bExcelEntity> {
	@Query("SELECT g FROM Gstr3bExcelEntity g WHERE "
			+ "g.invKey IN (:structuralValKeys) AND " 
			+ " g.id IN (:ids) ")
	List<Gstr3bExcelEntity> getAllExcelData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE Gstr3bExcelEntity g SET g.isInfo = TRUE "
			+ "WHERE g.invKey IN (:gstnKeys) AND g.id IN (:ids) ")
	public void Gstr3bUpdateInfo(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE Gstr3bExcelEntity g SET g.isError = TRUE "
			+ "WHERE g.invKey IN (:gstnKeys) AND g.id IN (:ids) ")

	public void Gstr3bUpdateErrors(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE Gstr3bExcelEntity g SET g.isInfo = TRUE ,g.isDelete = TRUE "
			+ "WHERE g.invKey IN (:gstnKeys) AND g.id IN (:ids) ")
	public void Gstr3bErrorUpdateInfo(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Query("SELECT g FROM Gstr3bExcelEntity g WHERE "
			+ "g.invKey IN (:structuralValKeys) AND "
			+ " g.id IN (:ids)  AND g.isError = FALSE ")
	List<Gstr3bExcelEntity> getAllProcessedWithInfoData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);

}
