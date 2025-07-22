package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.RefundsExcelEntity;

import jakarta.transaction.Transactional;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("RefundExcelRepository")
public interface RefundExcelRepository
		extends JpaRepository<RefundsExcelEntity, Long>,
		JpaSpecificationExecutor<RefundsExcelEntity> {
	@Query("SELECT REFUND FROM RefundsExcelEntity REFUND WHERE "
			+ "REFUND.refundInvkey IN (:structuralValKeys) AND "
			+ " REFUND.id IN (:ids) ")
	List<RefundsExcelEntity> getAllExcelData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE RefundsExcelEntity REFUND SET REFUND.isInfo = TRUE "
			+ "WHERE REFUND.refundInvkey IN (:gstnKeys) AND REFUND.id IN (:ids) ")
	public void REFUNDUpdateInfo(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE RefundsExcelEntity REFUND SET REFUND.isError = TRUE "
			+ "WHERE REFUND.refundInvkey IN (:gstnKeys) AND REFUND.id IN (:ids) ")

	public void REFUNDUpdateErrors(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE RefundsExcelEntity REFUND SET REFUND.isInfo = TRUE ,REFUND.isDelete = TRUE  "
			+ "WHERE REFUND.refundInvkey IN (:gstnKeys) AND REFUND.id IN (:ids) ")
	public void REFUNDErrorUpdateInfo(@Param("gstnKeys") List<String> gstnKeys,
			@Param("ids") List<Long> ids);

	@Query("SELECT REFUND FROM RefundsExcelEntity REFUND WHERE "
			+ "REFUND.refundInvkey IN (:structuralValKeys) AND "
			+ " REFUND.id IN (:ids)  AND REFUND.isError = FALSE ")
	List<RefundsExcelEntity> getAllProcessedWithInfoData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);

}
