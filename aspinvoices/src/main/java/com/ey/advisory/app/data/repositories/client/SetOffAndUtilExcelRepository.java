package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.SetOffAndUtilExcelEntity;

import jakarta.transaction.Transactional;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("SetOffAndUtilExcelRepository")
public interface SetOffAndUtilExcelRepository
		extends JpaRepository<SetOffAndUtilExcelEntity, Long>,
		JpaSpecificationExecutor<SetOffAndUtilExcelEntity> {
	@Query("SELECT SetOff FROM SetOffAndUtilExcelEntity SetOff WHERE "
			+ "SetOff.setOffInvKey IN (:structuralValKeys) AND "
			+ " SetOff.id IN (:ids) ")
	List<SetOffAndUtilExcelEntity> getAllExcelData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE SetOffAndUtilExcelEntity SetOff SET SetOff.isInfo = TRUE "
			+ "WHERE SetOff.setOffInvKey IN (:gstnKeys) AND SetOff.id IN (:ids) ")
	public void SetOffUpdateInfo(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE SetOffAndUtilExcelEntity SetOff SET SetOff.isError = TRUE "
			+ "WHERE SetOff.setOffInvKey IN (:gstnKeys) AND SetOff.id IN (:ids) ")

	public void SetOffUpdateErrors(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE SetOffAndUtilExcelEntity SetOff SET SetOff.isInfo = TRUE ,SetOff.isDelete = TRUE  "
			+ "WHERE SetOff.setOffInvKey IN (:gstnKeys) AND SetOff.id IN (:ids) ")
	public void SetOffErrorUpdateInfo(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Query("SELECT SetOff FROM SetOffAndUtilExcelEntity SetOff WHERE "
			+ "SetOff.setOffInvKey IN (:structuralValKeys) AND "
			+ " SetOff.id IN (:ids)  AND SetOff.isError = FALSE ")
	List<SetOffAndUtilExcelEntity> getAllProcessedWithInfoData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);

}
