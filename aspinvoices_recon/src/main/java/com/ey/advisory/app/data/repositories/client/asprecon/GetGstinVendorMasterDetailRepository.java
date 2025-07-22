package com.ey.advisory.app.data.repositories.client.asprecon;

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

import com.ey.advisory.app.itcmatching.vendorupload.GetGstinVendorMasterDetailEntity;

@Repository("GetGstinVendorMasterDetailRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstinVendorMasterDetailRepository
		extends JpaRepository<GetGstinVendorMasterDetailEntity, Long>,
		JpaSpecificationExecutor<GetGstinVendorMasterDetailEntity> {

	// initiate get call
	/*
	 * @Modifying
	 * 
	 * @Query("UPDATE GetGstinVendorMasterDetailEntity SET isDelete=true, " +
	 * "isActive=false where vendorGstin IN (:vendorGstins)") public
	 * List<GetGstinVendorMasterDetailEntity> softDeleteVgstinBeforePersist(
	 * 
	 * @Param("vendorGstins") List<String> vendorGstins);
	 */

	@Modifying
	@Query("UPDATE GetGstinVendorMasterDetailEntity SET isDelete = true "
			+ "WHERE vendorGstin IN :vendorGstins AND isDelete = false and entityId=:entityId ")
	void softDeleteVgstinBeforePersist(
			@Param("vendorGstins") List<String> vendorGstins,@Param("entityId") Long entityId);

	// for searching default values
	@Query("SELECT  doc FROM GetGstinVendorMasterDetailEntity doc WHERE "
			+ "doc.vendorGstin IN (:vendorGstinList) AND doc.isDelete=false and entityId=:entityId ")
	List<GetGstinVendorMasterDetailEntity> findByVendorGstinIn(
			@Param("vendorGstinList") List<String> vendorGstinList,@Param("entityId") Long entityId);



	@Query("SELECT doc FROM GetGstinVendorMasterDetailEntity doc WHERE "
			+ "doc.einvApplicable IN (:einvoiceApplicabilityList) AND doc.gstinStatus IN (:gstinStatusList)"
			+ " AND doc.lastUpdated <=:thresholdDate AND doc.isDelete=false AND doc.entityId=:entityId ")
	List<GetGstinVendorMasterDetailEntity> findByEinvApplicableInAndGstinStatusInAndLastUpdated(
			@Param("einvoiceApplicabilityList") List<String> einvoiceApplicabilityList,
			@Param("gstinStatusList") List<String> gstinStatusList,
			@Param("thresholdDate") LocalDateTime thresholdDate,
			@Param("entityId") Long entityId);

	// search pagination count
	@Query("SELECT count(*) FROM GetGstinVendorMasterDetailEntity doc WHERE "
			+ "doc.einvApplicable IN (:einvoiceApplicabilityList) AND doc.gstinStatus IN (:gstinStatusList) "
			+ "AND doc.lastUpdated <= :thresholdDate AND doc.isDelete = false")
	int findByeinvApplicableInAndgstinStatusInAndlastUpdatedCount(
			@Param("einvoiceApplicabilityList") List<String> einvoiceApplicabilityList,
			@Param("gstinStatusList") List<String> gstinStatusList,
			@Param("thresholdDate") LocalDateTime thresholdDate);

	@Query("SELECT  doc.vendorGstin FROM GetGstinVendorMasterDetailEntity doc WHERE "
			+ "doc.vendorGstin IN (:vendorGstinList) AND doc.isDelete=false")
	List<String> findVendorGstn(
			@Param("vendorGstinList") List<String> vendorGstinList);

	@Query("SELECT  doc FROM GetGstinVendorMasterDetailEntity doc WHERE "
			+ "doc.vendorGstin IN (:vendorGstinList) AND doc.isDelete=false")
	List<GetGstinVendorMasterDetailEntity> findVendorGstnStatusSummery(
			@Param("vendorGstinList") List<String> vendorGstinList);

	// to download the report
	@Query("SELECT  doc FROM GetGstinVendorMasterDetailEntity doc WHERE "
			+ "doc.vendorGstin=:gstinList AND doc.isDelete=false and doc.entityId=:entityId ")
	GetGstinVendorMasterDetailEntity findByVendorGstin(
			@Param("gstinList") String gstinList,@Param("entityId") Long entityId );

	@Modifying
	@Query("UPDATE GetGstinVendorMasterDetailEntity SET lastGetCallStatus=:lastGetCallStatus "
			+ "WHERE vendorGstin IN (:vendorGstins) AND isDelete = false and entityId=:entityId")
	void updatingInitiateGetCallStatus(
			@Param("vendorGstins") List<String> vendorGstins,
			@Param("lastGetCallStatus") String lastGetCallStatus,@Param("entityId") Long entityId);


}
