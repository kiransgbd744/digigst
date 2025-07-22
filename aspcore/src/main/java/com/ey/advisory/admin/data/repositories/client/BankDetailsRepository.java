package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.BankDetailsEntity;

@Repository("BankDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface BankDetailsRepository
		extends CrudRepository<BankDetailsEntity, Long> {

	@Query("SELECT distinct entityId,bankAcct,ifscCode,"
			+ "beneficiary,paymentDueDate,paymentTerm,paymentInstruction, bankName, bankAdd "
			+ "FROM BankDetailsEntity b WHERE b.entityId IN (:entityIds) "
			+ "AND b.isDelete=false "
			+ "GROUP BY entityId,bankAcct,ifscCode,beneficiary,"
			+ "paymentDueDate,paymentTerm,paymentInstruction,bankName,bankAdd ")
	public List<Object[]> getBankDetails(
			@Param("entityIds") final List<Long> entityIds);

	@Query("UPDATE BankDetailsEntity SET isDelete=true "
			+ "WHERE entityId=:entityId AND gstinId=:gstinId "
			+ "AND isDelete=false ")
	@Modifying
	public void updateIsDeleteByEntityIdAndGstinId(
			@Param("entityId") Long entityId, @Param("gstinId") Long gstinId);

	@Query("SELECT gstinId FROM BankDetailsEntity  WHERE entityId=:entityId AND "
			+ "bankAcct=:bankAcct AND isDelete=false ")
	public List<Long> getGstinIdsByBankAccnt(@Param("entityId") Long entityId,
			@Param("bankAcct") String bankAcct);
	
	@Query("SELECT b FROM BankDetailsEntity b WHERE b.gstinId=:gstinId "
			+ "AND b.isDelete=false ")
	public List<BankDetailsEntity> getBankDetailsByGstin(
			@Param("gstinId") final Long gstinId);
}
