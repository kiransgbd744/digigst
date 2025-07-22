package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.VendorMasterApiEntity;

@Repository("VendorMasterApiEntityRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface VendorMasterApiEntityRepository
		extends JpaRepository<VendorMasterApiEntity, Long>,
		JpaSpecificationExecutor<VendorMasterApiEntity> {

	@Query("SELECT DISTINCT vendorGstin FROM VendorMasterApiEntity where vendorPAN in :vendorPans "
			+ "and isDelete=false and recipientPAN = :reciepientPan")
	public List<String> findAllNonCompVendorGstinByVendorPans(
			@Param("vendorPans") List<String> vendorPans,
			@Param("reciepientPan") String reciepientPan);

	@Query("SELECT DISTINCT v.vendorPAN FROM VendorMasterApiEntity v "
			+ " WHERE v.recipientPAN in :recipientPANs and v.isDelete = false")
	public List<String> getDistinctActiveNonComplaintVendorPans(
			@Param("recipientPANs") List<String> recipientPANs);

	List<VendorMasterApiEntity> findByIsDeleteFalseAndRecipientPANIn(
			List<String> recipientPANs);

	List<VendorMasterApiEntity> findByIsDeleteFalseAndRecipientPANInAndVendorPANIn(
			List<String> recipientPANs, List<String> vendorPans);

	List<VendorMasterApiEntity> findByIsDeleteFalseAndRecipientPANInAndVendorGstinIn(
			List<String> recipientPANs, List<String> vendorGstins);
	
	@Query("SELECT DISTINCT vendorGstin FROM VendorMasterApiEntity where "
			+ " vendorPAN in :vendorPans and isDelete=false and "
			+ " recipientPAN in :reciepientPan")
	public List<String> getAllActiveVendorGstinByVendorPans(
			@Param("vendorPans") List<String> vendorPans,
			@Param("reciepientPan") List<String> reciepientPan);
	
	@Query("SELECT DISTINCT vendorGstin FROM VendorMasterApiEntity where "
			+ " isDelete =false and recipientPAN in :reciepientPan")
	public List<String> getAllActiveVendorGstinByRecipientPan(
			@Param("reciepientPan") List<String> reciepientPan);
	
	@Modifying
	@Query("UPDATE VendorMasterApiEntity entity SET entity.isDelete=true "
			+ "where entity.vendorGstin IN (:vendorGstin)")
	public int updateisDeleteBeforePersist(
			@Param("vendorGstin") List<String> vendorGstin);
	
	@Query("SELECT DISTINCT v.vendorGstin FROM VendorMasterApiEntity v "
			+ " WHERE v.recipientPAN in :recipientPANs and v.isDelete = false")
	public List<String> getDistinctActiveGstins(
			@Param("recipientPANs") List<String> recipientPANs);
	
	@Query("SELECT DISTINCT v.vendorPAN FROM VendorMasterApiEntity v "
			+ " WHERE v.vendorGstin in :vendorGstins and v.isDelete = false")
	public List<String> getDistinctActiveVendorPan(
			@Param("vendorGstins") List<String> vendorGstins);
	
}
