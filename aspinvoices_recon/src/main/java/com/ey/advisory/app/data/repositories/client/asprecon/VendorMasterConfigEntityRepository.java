package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.VendorMasterConfigEntity;

@Repository("VendorMasterConfigEntityRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface VendorMasterConfigEntityRepository
		extends JpaRepository<VendorMasterConfigEntity, Long>,
		JpaSpecificationExecutor<VendorMasterConfigEntity> {

	@Query("SELECT DISTINCT vendorGstin from VendorMasterConfigEntity where isFetched is false")
	List<String> getDistinctVendorGSTINByIsFetched();

	@Modifying
	@Query("UPDATE VendorMasterConfigEntity SET gstinStatus = :gstinStatus,"
			+ " legalName = :legalName,"
			+ " tradeName = :tradeName, taxpayerType =:taxpayerType,"
			+ " updatedOn = :updatedOn, isNameUpdated =:isNameUpdated,"
			+ " nameUpdatedOn = :nameUpdatedOn, isFetched =:isFetched,"
			+ " dateOfRegistration =:dateOfRegistration,"
			+ " dateOfCancellation =:dateOfCancellation, "
			+ " errorCode = null, errorDescription = null "
			+ " where vendorGstin =:vendorGstin")
	public void updateLegalTradeName(@Param("vendorGstin") String vendorGstin,
			@Param("legalName") String legalName,
			@Param("gstinStatus") String gstinStatus,
			@Param("tradeName") String tradeName,
			@Param("taxpayerType") String taxpayerType,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("isNameUpdated") Boolean isNameUpdated,
			@Param("nameUpdatedOn") LocalDateTime nameUpdatedOn,
			@Param("isFetched") Boolean isFetched,
			@Param("dateOfRegistration") LocalDate dateOfRegistration,
			@Param("dateOfCancellation") LocalDate dateOfCancellation);

	@Modifying
	@Query("UPDATE VendorMasterConfigEntity SET gstinStatus = :gstinStatus,"
			+ " updatedOn = :updatedOn, errorCode =:errorCode,"
			+ " errorDescription = :errorDescription, isFetched =:isFetched where vendorGstin =:vendGstin")
	public void updateErrCodeErrDes(@Param("vendGstin") String vendGstin,
			@Param("gstinStatus") String gstinStatus,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("errorCode") String errorCode,
			@Param("errorDescription") String errorDescription,
			@Param("isFetched") Boolean isFetched);

	@Query("SELECT DISTINCT vendorGstin from VendorMasterConfigEntity")
	List<String> getDistinctVendorGstins();

	public List<VendorMasterConfigEntity> findByVendorGstinInAndErrorCodeIsNullAndErrorDescriptionIsNullAndIsActiveTrue(
			List<String> vendorGstin);

	public List<VendorMasterConfigEntity> findByVendorGstinIn(
			List<String> vendorGstin);

	@Query(value = " select vendor_gstin,legal_name from TBL_VENDOR_MASTER_CONFIG "
			+ " WHERE LEGAL_NAME is not null and is_active = true and vendor_gstin in (:vendrGstin) "
			+ " ", nativeQuery = true)
	public List<Object[]> getLegalNameByVendorGstin(
			@Param("vendrGstin") List<String> vendorGstin);
	
	@Query(value = " select vendor_gstin from TBL_VENDOR_MASTER_CONFIG "
			+ " WHERE is_active = true and vendor_gstin in (:vendorGstin) "
			+ " ", nativeQuery = true)
	public List<String> findByGstinIn(
			@Param("vendorGstin") List<String> vendorGstin);
	
	@Query(value = " select vendor_gstin,legal_name,TRADE_NAME from TBL_VENDOR_MASTER_CONFIG "
			+ " WHERE is_active = true and "
			+ " GSTIN_STATUS IN ('Active','Suspended','Cancelled') AND IS_FETCHED = TRUE "
			+ " ", nativeQuery = true)
	public List<Object[]> getLegalNameTradeName();

}
