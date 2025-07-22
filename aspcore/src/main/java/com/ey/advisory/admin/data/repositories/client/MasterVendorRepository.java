package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.MasterVendorEntity;

import jakarta.transaction.Transactional;

@Repository("masterVendorRepository")
public interface MasterVendorRepository
		extends CrudRepository<MasterVendorEntity, Long> {
	
	@Query("SELECT e FROM MasterVendorEntity e WHERE e.isDelete=false ")
	public List<MasterVendorEntity> getAllMasterVendor();

	@Query("SELECT e FROM MasterVendorEntity e WHERE isDelete = false "
			+ "AND entityId=:entityId ORDER BY id DESC")
	public List<MasterVendorEntity> getMasterVendorDetails(
			@Param("entityId") final Long entityId);

	@Transactional
	@Modifying
	@Query("UPDATE MasterVendorEntity SET isDelete = true WHERE "
			+ "id IN (:ids)")
	public void deleteMasterVendor(@Param("ids") final List<Long> ids);

	@Query("SELECT  g FROM  MasterVendorEntity g WHERE "
			+ "g.supplierGstinPan IN (:supplierGstinPan)")
	List<MasterVendorEntity> findBySGstnOrPan(
			@Param("supplierGstinPan") String supplierGstinPan);

	@Transactional
	@Modifying
	@Query("UPDATE MasterVendorEntity SET isDelete = true WHERE "
			+ " isDelete = false and entityId = :entityId")
	public void updateAllToDelete(@Param("entityId") final Long entityId);
	
	@Query("SELECT e.supplierGstinPan FROM MasterVendorEntity e WHERE e.isDelete=false ")
	public List<String> getCustGstinPanMasterVendor();

}
