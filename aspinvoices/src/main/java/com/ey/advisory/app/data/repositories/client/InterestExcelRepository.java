package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.InterestExcelEntity;

import jakarta.transaction.Transactional;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("InterestExcelRepository")
public interface InterestExcelRepository
		extends JpaRepository<InterestExcelEntity, Long>,
		JpaSpecificationExecutor<InterestExcelEntity> {
	@Query("SELECT INTEREST FROM InterestExcelEntity INTEREST WHERE "
			+ "INTEREST.interestInvKey IN (:structuralValKeys) AND "
			+ " INTEREST.id IN (:ids) ")
	List<InterestExcelEntity> getAllExcelData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE InterestExcelEntity INTEREST SET INTEREST.isInfo = TRUE "
			+ "WHERE INTEREST.interestInvKey IN (:gstnKeys) AND INTEREST.id IN (:ids) ")
	public void INTERESTUpdateInfo(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE InterestExcelEntity INTEREST SET INTEREST.isError = TRUE "
			+ "WHERE INTEREST.interestInvKey IN (:gstnKeys) AND INTEREST.id IN (:ids) ")

	public void INTERESTUpdateErrors(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE InterestExcelEntity INTEREST SET INTEREST.isInfo = TRUE ,INTEREST.isDelete = TRUE "
			+ "WHERE INTEREST.interestInvKey IN (:gstnKeys) AND INTEREST.id IN (:ids) ")
	public void INTERESTErrorUpdateInfo(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Query("SELECT INTEREST FROM InterestExcelEntity INTEREST WHERE "
			+ "INTEREST.interestInvKey IN (:structuralValKeys) AND "
			+ " INTEREST.id IN (:ids)  AND INTEREST.isError = FALSE ")
	List<InterestExcelEntity> getAllProcessedWithInfoData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);

}
