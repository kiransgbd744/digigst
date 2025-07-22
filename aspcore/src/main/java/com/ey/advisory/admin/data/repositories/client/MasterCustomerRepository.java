package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.MasterCustomerEntity;

import jakarta.transaction.Transactional;

@Repository("masterCustomerRepository")
public interface MasterCustomerRepository
		extends CrudRepository<MasterCustomerEntity, Long> {
	
	@Query("SELECT e FROM MasterCustomerEntity e WHERE e.isDelete=false ")
	public List<MasterCustomerEntity> getAllMasterCustomer();

	@Query("SELECT e FROM MasterCustomerEntity e WHERE e.isDelete=false "
			+ "AND entityId=:entityId ORDER BY id DESC")
	public List<MasterCustomerEntity> getAllMasterCustomer(
			@Param("entityId") final Long entityId);

	@Transactional
	@Modifying
	@Query("UPDATE MasterCustomerEntity SET isDelete = true WHERE "
			+ "id IN (:ids)")
	public void deleteMasterCustomer(@Param("ids") final List<Long> ids);

	@Query("SELECT  g FROM  MasterCustomerEntity g WHERE "
			+ "g.recipientGstnOrPan IN (:recipientGstnOrPan) "
			+ "AND g.isDelete = false ")
	List<MasterCustomerEntity> findByrecipientGstnOrPan(
			@Param("recipientGstnOrPan") String recipientGstnOrPan);

	@Transactional
	@Modifying
	@Query("UPDATE MasterCustomerEntity CUST SET CUST.isDelete = true WHERE "
			+ " CUST.isDelete = false AND CUST.entityId = :entityId ")
	public void updateAllToDelete(@Param("entityId") Long entityId);
}
