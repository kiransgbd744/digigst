package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.VendorCommRequestEntity;

@Repository("VendorCommRequestRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface VendorCommRequestRepository
		extends JpaRepository<VendorCommRequestEntity, Long>,
		JpaSpecificationExecutor<VendorCommRequestEntity> {

	@Modifying
	@Query("UPDATE VendorCommRequestEntity SET status = :status,"
			+ " filePath = :filePath, updatedOn =:updatedOn"
			+ " where id  = :id")
	void updateStatus(@Param("id") Long id, @Param("status") String status,
			@Param("filePath") String filePath,
			@Param("updatedOn") LocalDateTime updatedOn);
	
	@Modifying
	@Query("UPDATE VendorCommRequestEntity SET status = :status,"
	        + " filePath = :filePath, updatedOn = :updatedOn, docId = :docId"
	        + " where id = :id")
	void updateStatus(@Param("id") Long id, 
	                  @Param("status") String status,
	                  @Param("filePath") String filePath,
	                  @Param("updatedOn") LocalDateTime updatedOn,
	                  @Param("docId") String docId);


	public List<VendorCommRequestEntity> findByCreatedByAndReconType(
			String createdBy, String reconType);

	public VendorCommRequestEntity findByRequestId(Long reqID);

	@Query("select vendorCommRequestEntity.filePath from VendorCommRequestEntity vendorCommRequestEntity "
			+ "  where vendorCommRequestEntity.requestId = :requestId")
	public String getFilePath(@Param("requestId") Long requestId);
	
	@Query("SELECT vendorCommRequestEntity FROM VendorCommRequestEntity vendorCommRequestEntity "
	        + "WHERE vendorCommRequestEntity.requestId = :requestId ")
	public Optional<VendorCommRequestEntity> getFilePathDocId(@Param("requestId") Long requestId);

	@Query(value = "select Max(REQUEST_ID) from VENDOR_COMM_REQUEST where Status = 'REPORT_GENERATED'",nativeQuery = true)
	public Long getLastReqId();

	@Query("select chd.updatedOn, chd.vendorGstin, chd.totalRec, prt.fromTaxPeriod, "
			+ " prt.toTaxPeriod,chd.respRecordsCnt,chd.respFilePath, "
			+ " chd.totFilePath, chd.isRead from VendorCommRequestEntity prt inner join "
			+ " VendorReqVendorGstinEntity chd on prt.requestId = chd.requestId where "
			+ " prt.status = 'REPORT_GENERATED' and chd.vendorGstin in (:vendrGstin) and prt.requestId =:reqId "
			+ " and chd.isResp = true ")
	public List<Object[]> getResponseData(
			@Param("vendrGstin") List<String> vendrGstin,
			@Param("reqId") Long reqId);
	
	@Modifying
	@Query("UPDATE VendorCommRequestEntity SET dataPrepStatus =:status,"
			+ " updatedOn = CURRENT_TIMESTAMP where requestId =:reqId")
	void updatePrepStatus(@Param("reqId") Long reqId, @Param("status") String status);
}
