package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.VendorReqVendorGstinEntity;

@Repository("VendorReqVendorGstinRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface VendorReqVendorGstinRepository
		extends JpaRepository<VendorReqVendorGstinEntity, Long>,
		JpaSpecificationExecutor<VendorReqVendorGstinEntity> {

	@Query("select vendorReqVendorGstinEntity.vendorGstin from VendorReqVendorGstinEntity "
			+ " vendorReqVendorGstinEntity where vendorReqVendorGstinEntity.requestId = :requestId")
	public List<String> getVendorGstin(@Param("requestId") Long requestId);

	public List<VendorReqVendorGstinEntity> findByRequestId(Long requestId);

	@Modifying
	@Query("UPDATE VendorReqVendorGstinEntity SET reportStatus = :reportStatus,"
			+ " filePath = :filePath, updatedOn =:updatedOn"
			+ " where requestId  = :requestId and vendorGstin =:vendorGstin")
	void updateReportStatus(@Param("requestId") Long requestId,
			@Param("reportStatus") String reportStatus,
			@Param("filePath") String filePath,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("vendorGstin") String vendorGstin);
	
	@Modifying
	@Query("UPDATE VendorReqVendorGstinEntity SET reportStatus = :reportStatus,"
	        + " filePath = :filePath, updatedOn = :updatedOn, docId = :docId"
	        + " WHERE requestId = :requestId AND vendorGstin = :vendorGstin")
	void updateReportStatusDocId(@Param("requestId") Long requestId,
	        @Param("reportStatus") String reportStatus,
	        @Param("filePath") String filePath,
	        @Param("updatedOn") LocalDateTime updatedOn,
	        @Param("vendorGstin") String vendorGstin,
	        @Param("docId") String docId);

	
	@Modifying
	@Query("UPDATE VendorReqVendorGstinEntity SET reportStatus = :reportStatus,"
	        + " filePath = :filePath, updatedOn = :updatedOn, docId = :docId"
	        + " WHERE requestId = :requestId AND vendorGstin = :vendorGstin")
	void updateReportStatus(@Param("requestId") Long requestId,
	                        @Param("reportStatus") String reportStatus,
	                        @Param("filePath") String filePath,
	                        @Param("updatedOn") LocalDateTime updatedOn,
	                        @Param("vendorGstin") String vendorGstin,
	                        @Param("docId") String docId);

	@Query("select vendorReqVendorGstinEntity.vendorGstin, vendorReqVendorGstinEntity.emailStatus"
			+ " from VendorReqVendorGstinEntity "
			+ " vendorReqVendorGstinEntity where vendorReqVendorGstinEntity.requestId = :requestId")
	public List<Object[]> getVendorGstinAndEmailStatus(
			@Param("requestId") Long requestId);

	@Query("select vendorReqVendorGstinEntity.filePath from VendorReqVendorGstinEntity vendorReqVendorGstinEntity "
			+ "  where vendorReqVendorGstinEntity.requestId = :requestId "
			+ "  and vendorReqVendorGstinEntity.vendorGstin = :vendorGstin")
	public String getFilePath(@Param("requestId") Long requestId,
			@Param("vendorGstin") String vendorGstin);
	
	@Query("SELECT vendorReqVendorGstinEntity FROM VendorReqVendorGstinEntity vendorReqVendorGstinEntity "
	        + "WHERE vendorReqVendorGstinEntity.requestId = :requestId "
	        + "AND vendorReqVendorGstinEntity.vendorGstin = :vendorGstin")
	public Optional<VendorReqVendorGstinEntity> getFilePathDocId(@Param("requestId") Long requestId,
	                                                              @Param("vendorGstin") String vendorGstin);



	@Modifying
	@Query("UPDATE VendorReqVendorGstinEntity SET emailStatus = :emailStatus,"
			+ " updatedOn =:updatedOn where "
			+ " vendorGstin in :vendorGstin and requestId  = :requestId ")
	void updateEmailStatus(@Param("requestId") Long requestId,
			@Param("vendorGstin") List<String> vendorGstin,
			@Param("emailStatus") String emailStatus,
			@Param("updatedOn") LocalDateTime updatedOn);

	@Modifying
	@Query("UPDATE VendorReqVendorGstinEntity SET totalRec =:totalCount,"
			+ " jsonCnt = :jsonCount"
			+ " where requestId  = :requestId and vendorGstin =:vendorGstin")
	void updateCounts(@Param("jsonCount") int jsonCount,
			@Param("totalCount") int totalCount,
			@Param("requestId") Long requestId,
			@Param("vendorGstin") String vendorGstin);

	@Query(value = "select REQUEST_ID, count(*) from VENDOR_REQUEST_VGSTIN "
			+ " where IS_RESPONDED = true group by REQUEST_ID", nativeQuery = true)
	public List<Object[]> getVendorRespCnt();
	
	@Query(value = "select REQUEST_ID, count(*) from VENDOR_REQUEST_VGSTIN "
			+ " where EMAIL_STATUS = 'SENT' group by REQUEST_ID", nativeQuery = true)
	public List<Object[]> getVendorEmailSentCnt();

	@Modifying
	@Query("UPDATE VendorReqVendorGstinEntity SET isRead = true "
			+ " where requestId =:requestId and vendorGstin =:vendorGstin")
	void updateIsRead(@Param("requestId") Long requestId,
			@Param("vendorGstin") String vendorGstin);

	@Modifying
	@Query("UPDATE VendorReqVendorGstinEntity SET respFilePath =:respPath "
			+ " where requestId =:requestId and vendorGstin =:vendorGstin")
	void updateRespPath(@Param("requestId") Long requestId,
			@Param("vendorGstin") String vendorGstin,
			@Param("respPath") String respPath);

	@Modifying
	@Query("UPDATE VendorReqVendorGstinEntity SET respFilePath = :respPath, docIdRespondedFilePath = :docId "
	        + " WHERE requestId = :requestId AND vendorGstin = :vendorGstin")
	void updateRespPath(@Param("requestId") Long requestId,
	                    @Param("vendorGstin") String vendorGstin,
	                    @Param("respPath") String respPath,
	                    @Param("docId") String docId);

	@Modifying
	@Query("UPDATE VendorReqVendorGstinEntity SET totFilePath =:respPath "
			+ " where requestId =:requestId and vendorGstin =:vendorGstin")
	void updateTotalRecrdsPath(@Param("requestId") Long requestId,
			@Param("vendorGstin") String vendorGstin,
			@Param("respPath") String respPath);
	
	@Modifying
	@Query("UPDATE VendorReqVendorGstinEntity SET totFilePath =:respPath, docIdTotalFilePath = :docId "
			+ " where requestId =:requestId and vendorGstin =:vendorGstin")
	void updateTotalRecrdsPath(@Param("requestId") Long requestId, @Param("vendorGstin") String vendorGstin,
			@Param("respPath") String respPath, @Param("docId") String docId);

	@Modifying
	@Query("UPDATE VendorReqVendorGstinEntity SET updatedOn = CURRENT_TIMESTAMP, isResp = true, "
			+ " respRecordsCnt =:respCount where requestId  =:requestId and vendorGstin =:vendorGstin")
	void updateIsRespAndRespCnt(@Param("requestId") Long requestId,
			@Param("vendorGstin") String vendorGstin,
			@Param("respCount") int respCount);

	public List<VendorReqVendorGstinEntity> findByVendorGstinInAndIsRespTrueAndRequestId(
			List<String> vendrGstin, Long requestId, Pageable pageReq);
	
	@Query("SELECT count(*) from VendorReqVendorGstinEntity where requestId =:reqId and  vendorGstin in :vendrgstin and isResp = true")
	int findCountByVendorGstinInAndIsRespTrueAndRequestId(
			@Param("vendrgstin") List<String> vendrGstin,
			@Param("reqId") Long reqId);

	@Query("SELECT v FROM VendorReqVendorGstinEntity v WHERE v.requestId = :reqId AND v.vendorGstin = :vendorGstin")
	Optional<VendorReqVendorGstinEntity> findByRequestIdVendorGstin(@Param("reqId") Long reqId,
			@Param("vendorGstin") String vendorGstin);

}
